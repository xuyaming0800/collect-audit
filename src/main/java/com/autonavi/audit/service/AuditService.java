package com.autonavi.audit.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import net.sf.json.JSONObject;

import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.dataup.commontools.BaiduApi;

import com.autonavi.audit.constant.AuditConstant;
import com.autonavi.audit.constant.BmpStatusConstant;
import com.autonavi.audit.constant.PayStatusConstant;
import com.autonavi.audit.constant.StatusConstant;
import com.autonavi.audit.constant.TASK_STATUS;
import com.autonavi.audit.dao.AuditAttrDaoForMongoDB;
import com.autonavi.audit.dao.AuditDao;
import com.autonavi.audit.dao.AuditPayRecordDao;
import com.autonavi.audit.dao.AuditSysConfigDao;
import com.autonavi.audit.dao.EditPlatformDao;
import com.autonavi.audit.entity.CollectAudit;
import com.autonavi.audit.entity.CollectAuditAndCoordinate;
import com.autonavi.audit.entity.CollectAuditClaim;
import com.autonavi.audit.entity.CollectAuditImage;
import com.autonavi.audit.entity.CollectAuditLogs;
import com.autonavi.audit.entity.CollectAuditPayRecord;
import com.autonavi.audit.entity.CollectAuditSpecimenImage;
import com.autonavi.audit.entity.CollectAuditSystem;
import com.autonavi.audit.entity.ExportTask;
import com.autonavi.audit.entity.HistoricalRecords;
import com.autonavi.audit.entity.Pagination;
import com.autonavi.audit.entity.Point;
import com.autonavi.audit.entity.Property;
import com.autonavi.audit.entity.ResultEntity;
import com.autonavi.audit.entity.groups.MissingTask;
import com.autonavi.audit.entity.groups.OrdinaryTask;
import com.autonavi.audit.exception.TaskAlreadyClaimedException;
import com.autonavi.audit.exception.TaskNotFoundException;
import com.autonavi.audit.mq.RabbitMQMessageHandler;
import com.autonavi.audit.mq.RabbitMQUtils;
import com.autonavi.audit.search.SearchService;
import com.autonavi.audit.util.CommonUtil;
import com.autonavi.audit.util.HttpClientUtil;
import com.autonavi.audit.util.JsonBinder;
import com.autonavi.audit.util.PrimaryByRedis;
import com.autonavi.audit.util.PropConstants;
import com.autonavi.audit.util.PropertiesConfig;
import com.autonavi.audit.util.RedisUtilComponent;
import com.autonavi.audit.util.StringUtil;
import com.autonavi.audit.util.watermark.Download;
import com.autonavi.audit.util.watermark.ParseProperties;
import com.autonavi.audit.util.watermark.WaterMark;
import com.autonavi.audit.workflow.AuditProcess;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

@Service
public class AuditService {

	public AuditService() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	/**
	 * 开始任务（mq入口）
	 * 
	 * @param CollectAudit
	 */
	@PostConstruct
	public void startTask() {
		new Thread(new Runnable() {
			public void run() {
				List<CollectAuditSystem> sysList = auditSysConfigDao
						.selectCollectAuditSystem();
				logger.info("sysList=" + sysList);
				for (final CollectAuditSystem _collectAuditSystem : sysList) {
					logger.info("_collectAuditSystem=" + _collectAuditSystem);
					final String[] uri = _collectAuditSystem.getMqurl().split(
							":");
					// 从mq中得到消息
					try {
						RabbitMQUtils.receive(_collectAuditSystem.getId(),
								uri[0], Integer.valueOf(uri[1]), null, null,
								_collectAuditSystem.getInput_queue(),
								ResultEntity.class, true,
								new RabbitMQMessageHandler() {
									@Override
									public void setMessage(Object message) {
										logger.entry("客户端发送json串(message):"
												+ message);
										ResultEntity resultEntity = (ResultEntity) message;
										CollectAudit collectAudit = (CollectAudit) resultEntity
												.getInfo();
										logger.entry(collectAudit);
										try {
											auditService.execute(
													_collectAuditSystem, uri,
													resultEntity, collectAudit);
										} catch (Exception e) {
											logger.error(e.getMessage(), e);
										}
									}
								});
					} catch (ShutdownSignalException e) {
						// MQ异常
						logger.error(e.getMessage(), e);
					} catch (ConsumerCancelledException e) {
						// MQ异常
						logger.error(e.getMessage(), e);
					} catch (IOException e) {
						// MQ异常
						logger.error(e.getMessage(), e);
					} catch (InterruptedException e) {
						// MQ异常
						logger.error(e.getMessage(), e);
					} catch (NumberFormatException e) {
						// 解析MQ路径异常
						logger.error(e.getMessage(), e);
					} catch (Exception e) {
						// 反序列化异常
						logger.error(e.getMessage(), e);
						ResultEntity resultEntity = new ResultEntity();
						resultEntity.setSuccess(false);// 状态为失败
						resultEntity.setDesc(e.getMessage());// 失败消息
						try {
							// 向mq发送消息
							RabbitMQUtils.send(_collectAuditSystem.getId(),
									uri[0], Integer.valueOf(uri[1]), null,
									null,
									_collectAuditSystem.getOutput_queue(),
									resultEntity, true);
						} catch (Exception e1) {
							// MQ异常
							logger.error(e1.getMessage(), e1);
						}
					}
				}
			}
		}).start();

	}

	/**
	 * 执行插入或是返回异常
	 * 
	 * @param _collectAuditSystem
	 * @param uri
	 * @param resultEntity
	 * @param collectAudit
	 * @throws Exception
	 */
	public void execute(CollectAuditSystem _collectAuditSystem, String[] uri,
			ResultEntity resultEntity, CollectAudit collectAudit)
			throws Exception {
		if (resultEntity.isSuccess()) {// 如果是成功的（新任务）
			logger.info("-->如果是成功的（新任务）");
			// 校验
			if (validate(_collectAuditSystem, uri, collectAudit)) {// 如果校验通过
				logger.info("-->如果校验通过");
				try {
					// 如果是申诉,走申诉流程
					if (collectAudit.getStatus().equals(
							TASK_STATUS.APPEAL.getCode())) {
						CollectAudit _collectAudit = this.auditDao
								.findCollectAuditByTaskId(collectAudit.getId());
						if (!_collectAudit.getStatus().equals(
								StatusConstant.appeal)) {// 如果队列进来不是申诉状态(status不为8),则进入申诉流程
							logger.info("appealid=" + collectAudit.getId());
							String taskId = this.auditProcess
									.getTaskIdByBusinessKey(collectAudit
											.getId());
							logger.info("task_id=" + taskId);
							if (!"".equals(taskId) && taskId != null) {
								logger.info("-->task!=null,进入申诉流程");
								this.appealTask(taskId);
							}
						}
					} else {// 不是申诉走正常流程
						// 启动流程
						Map<String, Object> variableMap = PropertyUtils
								.describe(collectAudit);
						logger.entry(variableMap);
						logger.info("---->collectAudit.getSystem_type()="
								+ collectAudit.getSystem_type());
						variableMap
								.put("bsType", collectAudit.getSystem_type());
						variableMap.put("bmpStatus", 0);
						logger.info("---->collectAudit.getStatus()="
								+ collectAudit.getStatus());
						variableMap.put("type", collectAudit.getStatus());
						logger.info("---->collectAudit.getVerify_maintain_time()="
								+ collectAudit.getVerify_maintain_time());
						variableMap.put("deadline",
								"PT" + collectAudit.getVerify_maintain_time()
										+ "H");// 审核期限（小时计。ISO
												// 8601格式）
						logger.info("---->collectAudit.getTask_freezing_time()="
								+ collectAudit.getTask_freezing_time());
						variableMap.put("task_freezing_time", "PT"
								+ collectAudit.getTask_freezing_time() + "H");// 冻结时间（小时计。ISO
																				// 8601格式）
						variableMap.put("status", StatusConstant.auditing);
						Map<String, String> resultMap = auditProcess
								.startAuditProcess(null,
										String.valueOf(collectAudit.getId()),
										variableMap);// 插入相关数据
						logger.entry(resultMap);
						logger.info("---->resultMap.get(\"processDefinitionId\")="
								+ resultMap.get("processDefinitionId"));
						collectAudit.setProcess_definition_id(resultMap
								.get("processDefinitionId"));// 流程定义ID
						logger.info("---->resultMap.get(\"processInstanceId\")="
								+ resultMap.get("processInstanceId"));
						collectAudit.setProcess_instance_id(resultMap
								.get("processInstanceId"));// 流程定义ID
						logger.info("---->StatusConstant.auditing="
								+ StatusConstant.auditing);
						collectAudit.setStatus(StatusConstant.auditing);// 状态为审批中
						// 计算公交站亭的总价格
						/*
						 * if (AuditConstant.GGPP_TYPE_NAME.equals(collectAudit.
						 * getTask_class_name())) { Double priceNear =
						 * this.auditDao
						 * .findNearImgPriceByIndex(AuditConstant.GGPP_TYPE_NAME
						 * ); logger.info("priceNear" + priceNear); Double
						 * priceFar = this.auditDao
						 * .findFarImgPriceByIndex(AuditConstant
						 * .GGPP_TYPE_NAME); logger.info("priceFar" + priceFar);
						 * if (priceNear != null && priceFar != null) {
						 * logger.info(
						 * "+++++++++++++++++++++++++++++1++++++++++++++++++++="
						 * ); double task_amount = 0.0; for (CollectAuditImage
						 * collectAuditImage : collectAudit .getImages()) {
						 * logger.info(
						 * "+++++++++++++++++++++++++++++2++++++++++++++++++++="
						 * ); Integer index = collectAuditImage .getIndex();
						 * logger.info("index" + index); if (index != null) {
						 * logger.info(
						 * "+++++++++++++++++++++++++++++3++++++++++++++++++++"
						 * ); if (index % 2 == 0) { logger.info(
						 * "+++++++++++++++++++++++++++++4++++++++++++++++++++"
						 * ); task_amount += priceNear; } else if (index % 2 ==
						 * 1) { logger.info(
						 * "+++++++++++++++++++++++++++++5++++++++++++++++++++"
						 * ); task_amount += priceFar; } } }
						 * logger.info("task_amount" + task_amount);
						 * collectAudit.setTask_amount(task_amount); } else {
						 * logger.error("图片的远景近景价格不存在"); } }
						 */
						// +2015.8.10去mongodb查询大类名称,并设置到collectAudit
						logger.info("---->查询mongodb数据");
						Map<?, ?> MogoAttrMap = null;
						try {
							MogoAttrMap = this.auditAttrDaoForMongoDB
									.getAllAttr(collectAudit.getId());
						} catch (Exception e) {
							logger.error("查询mongodb数据异常", e);
							e.printStackTrace();
						}
						if (MogoAttrMap != null
								&& MogoAttrMap.containsKey("collectClassName")) {
							String collectClassName = (String) MogoAttrMap
									.get("collectClassName");
							collectAudit.setTask_class_name(collectClassName);
							logger.info("---->主分类名称collectClassName:"
									+ collectClassName);
						}
						// 20151026增加打水印功能
						logger.info("---->打水印");
						waterMarkImgs(MogoAttrMap);
						// 20150907增加区域信息的获取和所属城市
						logger.info("---->设置任务所在城市");
						setCityAndRegionalInfo(collectAudit);
						// 插入主表
						logger.info("---->插入主表开始");
						auditDao.insertCollectAudit(collectAudit);
						logger.info("---->插入主表完成");
						// 插入坐标表
						logger.info("---->插入坐标开始");
						if (collectAudit.getOriginalCoordinates() != null
								&& collectAudit.getOriginalCoordinates().length >= 2)
							auditDao.insertOriginalCoordinates(
									collectAudit.getId(),
									collectAudit.getOriginalCoordinates());
						logger.info("---->插入坐标完成");
						// 插入图片表
						logger.info("---->插入图片开始");
						if (collectAudit.getImages() != null)
							auditDao.insertCollectAuditImage(
									collectAudit.getId(),
									collectAudit.getImages());
						logger.info("---->插入图片完成");
						// 插入图片样张表表
						logger.info("---->插入图片样张开始");
						if (collectAudit.getSpecimenImages() != null
								&& collectAudit.getSpecimenImages().size() > 0)
							auditDao.insertCollectAuditSpecimenImage(
									collectAudit.getId(),
									collectAudit.getSpecimenImages());
						logger.info("---->插入图片样张完成");
					}
				} catch (Exception e) {// 如果发生数据库异常，返回给队列
					logger.info("++++如果发生数据库异常，返回给队列++++");
					logger.error(e.getMessage(), e);
					resultEntity = new ResultEntity();
					resultEntity.setSuccess(false);// 状态为失败
					resultEntity.setDesc(e.getMessage());// 失败消息
					resultEntity.setInfo(collectAudit);// 实体
					try {
						// 向mq发送消息
						logger.info("-->开始向mq发送消息=" + resultEntity);
						RabbitMQUtils.send(_collectAuditSystem.getId(), uri[0],
								Integer.valueOf(uri[1]), null, null,
								_collectAuditSystem.getOutput_queue(),
								resultEntity, true);
						logger.info("-->mq发送成功");
					} catch (IOException e1) {
						logger.info("++mq发送失败++");
						logger.error(e1.getMessage(), e1);
						throw e1;
					}
					throw e;
				}
			}
		} else {// 如果是失败的，可能是要求重发的任务
			logger.info("-->如果是失败的，可能是要求重发的任务");
			// 查询业务表，得到要重发的业务实体
			CollectAudit _collectAudit = auditDao.selectCollectAuditById(
					collectAudit.getId(), collectAudit.getSystem_type(),
					TASK_STATUS.APPEAL.getCode());
			logger.entry(_collectAudit);
			resultEntity = new ResultEntity();
			resultEntity.setSuccess(true);// 状态为成功
			resultEntity.setInfo(_collectAudit);// 实体
			logger.entry(resultEntity);
			try {
				// 向mq发送消息
				logger.info("---->开始向mq发送消息=" + resultEntity);
				RabbitMQUtils.send(_collectAuditSystem.getId(), uri[0],
						Integer.valueOf(uri[1]), null, null,
						_collectAuditSystem.getOutput_queue(), resultEntity,
						true);
				logger.info("---->mq发送成功");
			} catch (IOException e) {
				logger.info("++mq发送失败++");
				logger.error(e.getMessage(), e);
				throw e;
			}
		}
	}

	/**
	 * @Description: 打水印
	 * @author xusheng.liu
	 * @date 2015年10月26日 下午2:06:37
	 * @version V1.0
	 * @param mogoAttrMap
	 */
	private void waterMarkImgs(Map<?, ?> mogoAttrMap) {
		if (mogoAttrMap != null && mogoAttrMap.containsKey("attrs")) {
			logger.info("-->打水印[star]");
			Map<String, ?> attrs = (Map<String, ?>) mogoAttrMap.get("attrs");
			for (Entry<String, ?> entry : attrs.entrySet()) {
				Map m = (Map) entry.getValue();
				if (m.containsKey("imgs")) {
					logger.info("---->包含imgs项");
					List<Map> imgs = (List<Map>) m.get("imgs");
					if (imgs != null && imgs.size() > 0) {
						logger.info("---->含有" + imgs.size() + "张图片");
						for (Map map : imgs) {
							if (map.containsKey("image_url")) {
								String url = (String) map.get("image_url");
								logger.info("---->获取图片的原始路径：" + url);
								if (url.length() > 0 && url.contains("/")) {
									String urlString = url.substring(url
											.indexOf(ParseProperties
													.getProperties("subStri",
															null)));
									// 下载
									logger.info("------>下载开始");
									Download.downloadImage(url, urlString);
									logger.info("------>下载结束");
									// 水印
									WaterMark.markByAutoNavi(urlString,
											urlString);
									logger.info("---->打水印成功");
								}
							}
						}
					}
				}
			}
			logger.info("-->打水印[end]");
		}
	}

	/**
	 * @Description: 设置审核主表的所属城市和区域信息
	 * @author 刘旭升
	 * @date 2015年9月7日 下午3:12:15
	 * @version V1.0
	 * @param collectAudit
	 * @throws Exception
	 */
	private void setCityAndRegionalInfo(CollectAudit collectAudit) {
		logger.info("-->进入设置审核主表所属城市和区域信息方法");
		String point = "";
		List<CollectAuditImage> images = collectAudit.getImages();
		if (images != null && images.size() > 0) {
			CollectAuditImage auditImage = images.get(0);
			point = auditImage.getLat().toString() + ","
					+ auditImage.getLon().toString();
			logger.info("---->当前任务含有坐标:" + point);
		}
		if (!"".equals(point)) {
			String regionalInformation;
			try {
				regionalInformation = BaiduApi.getAddressByPoint(point);
				logger.info(regionalInformation);
				collectAudit.setRegional_information(regionalInformation);
				// 解析获取城市
				if (regionalInformation.contains("(")) {
					String substring = regionalInformation.substring(
							regionalInformation.indexOf("(") + 1,
							regionalInformation.lastIndexOf(")"));
					substring = JSONObject.fromObject(
							JSONObject.fromObject(substring)
									.getString("result")).getString(
							"addressComponent");
					JSONObject obj = JSONObject.fromObject(substring);
					logger.info(obj);
					collectAudit.setCity(obj.get("city").toString());
					collectAudit.setProvince(obj.get("province").toString());
					logger.info("---->设置信息和城市成功");
				}
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
		logger.info("无坐标");
	}

	/**
	 * 校验数据
	 * 
	 * @param _collectAuditSystem
	 * @param uri
	 * @param collectAudit
	 * @return
	 */
	private boolean validate(CollectAuditSystem _collectAuditSystem,
			String[] uri, CollectAudit collectAudit) {
		logger.entry(_collectAuditSystem);
		logger.entry(collectAudit);
		Set<ConstraintViolation<CollectAudit>> constraintViolations = null;
		// 先校验公用必填项
		constraintViolations = validator.validate(collectAudit);
		logger.entry(constraintViolations);
		if (this.validateHandle(_collectAuditSystem, uri, collectAudit,
				constraintViolations)) {// 如果处理结果为ture，再处理组
			logger.info("如果处理结果为ture");
			// 根据状态选择校验方式
			if (collectAudit.getStatus() == TASK_STATUS.SUBMIT.getCode()) {
				logger.info("根据状态选择校验方式=已提交");
				// 如果是提交的，选择普通组
				constraintViolations.addAll(validator.validate(collectAudit,
						OrdinaryTask.class));
			} else if (collectAudit.getStatus() == TASK_STATUS.NOT_FOUND
					.getCode()) {
				// 如果是未找到的，选择缺失组
				logger.info("如果是未找到的，选择缺失组");
				constraintViolations.addAll(validator.validate(collectAudit,
						MissingTask.class));
			}
			// 处理组
			return this.validateHandle(_collectAuditSystem, uri, collectAudit,
					constraintViolations);
		} else {
			return false;
		}
	}

	/**
	 * 校验结果处理
	 * 
	 * @param _collectAuditSystem
	 * @param uri
	 * @param collectAudit
	 * @param constraintViolations
	 */
	private boolean validateHandle(CollectAuditSystem _collectAuditSystem,
			String[] uri, CollectAudit collectAudit,
			Set<ConstraintViolation<CollectAudit>> constraintViolations) {
		logger.entry(_collectAuditSystem);
		logger.entry(collectAudit);
		logger.entry(constraintViolations);
		if (constraintViolations != null && !constraintViolations.isEmpty()) {// 说明出现校验错误
			logger.info("说明出现校验错误");
			Iterator<ConstraintViolation<CollectAudit>> it = constraintViolations
					.iterator();
			String msg = "";
			while (it.hasNext()) {
				// 获取错误信息
				ConstraintViolation<CollectAudit> cv = it.next();
				msg += cv.getPropertyPath() + cv.getMessage() + "\n";
			}
			logger.debug("校验失败！原因：" + msg);
			// 返回实体
			ResultEntity resultEntity = new ResultEntity();
			resultEntity.setSuccess(false);// 状态为失败
			resultEntity.setDesc(msg);// 消息体
			resultEntity.setInfo(collectAudit);// 实体
			try {
				// 发送信息到MQ
				RabbitMQUtils.send(_collectAuditSystem.getId(), uri[0],
						Integer.valueOf(uri[1]), null, null,
						_collectAuditSystem.getOutput_queue(), resultEntity,
						true);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			return false;
		}
		return true;
	}

	/**
	 * 查询代办任务（通过当前用的角色）
	 * 
	 * @param page
	 *            分页
	 * @param bsType
	 *            项目类型
	 * @param type
	 *            审核类型
	 * @param collectAudit
	 *            查询条件
	 * @return 当前用户的待办任务
	 */
	@SuppressWarnings("unchecked")
	public List<CollectAudit> findRepresentativeTask(Integer page,
			String bsType, Integer type, CollectAudit collectAudit) {
		logger.entry(collectAudit);
		Map<String, Object> variableValue = new HashMap<String, Object>();
		variableValue.put("bsType", bsType);
		if (type == 9)// 增加对申诉的查询，使用审核的状态
			variableValue.put("status", 8);
		else
			variableValue.put("type", type);
		// 从session中得到角色名
		List<String> roles = (List<String>) SecurityUtils.getSubject()
				.getSession().getAttribute("roles");
		// 查询代办任务
		List<Map<String, Object>> taskList = auditProcess
				.findTaskByUserOrGroup(null, roles, variableValue, page,
						collectAudit);
		return logger
				.exit(this.selectCollectAuditByIds(taskList, bsType, type));
	}

	/**
	 * 通过业务ID查询业务表中状态不为{status}的任务集合
	 * 
	 * @param taskList
	 *            任务LIST
	 * @param bsType
	 *            项目类型
	 * @param type
	 *            任务状态(采集的状态，9为申诉)
	 * @return
	 */
	private List<CollectAudit> selectCollectAuditByIds(
			List<Map<String, Object>> taskList, String bsType, Integer type) {
		logger.info(taskList);
		List<CollectAudit> auditList = new ArrayList<CollectAudit>();
		// 通过业务ID查询业务详情
		if (taskList != null && !taskList.isEmpty()) {
			logger.info("通过业务ID查询业务详情开始");
			for (Map<String, Object> map : taskList) {
				logger.info("遍历任务");
				CollectAudit collectAudit = auditDao.selectCollectAuditById(
						String.valueOf(map.get("bsTaskId")), bsType, type);
				logger.info(collectAudit);
				if (collectAudit != null) {
					if (map.containsKey("taskId")) {
						collectAudit.setTaskId((String) map.get("taskId"));
					}
					collectAudit.setTaskDefinitionKey((String) map
							.get("taskDefinitionKey"));
					collectAudit.setBpm_task_name((String) map.get("taskName"));
					auditList.add(collectAudit);
				}
			}
		}
		return logger.exit(auditList);
	}

	/**
	 * 认领任务
	 * 
	 * @param taskId
	 *            任务ID
	 * @throws TaskAlreadyClaimedException
	 *             任务已被其他用户认领
	 * @throws TaskNotFoundException
	 *             任务不存在
	 */
	public void claimTask(String taskId) throws TaskAlreadyClaimedException,
			TaskNotFoundException {
		// 从session中得到用户名
		String userName = (String) SecurityUtils.getSubject().getSession()
				.getAttribute("userName");// 从session里获取用户名
		logger.info("userName:" + userName);
		final Map<String, Object> userInfo = (Map<String, Object>) SecurityUtils
				.getSubject().getSession().getAttribute("userInfo");// 从session里获取用户名
		logger.info(userInfo);
		auditProcess.claimTask(taskId, userName, new HashMap<String, Object>() {
			private static final long serialVersionUID = 2346792694369796027L;
			{
				put("email", userInfo.get("mail"));
			}
		});
	}

	/**
	 * 查询当前用户的已认领任务
	 * 
	 * @param page
	 *            页号
	 * @param bsType
	 *            项目类型
	 * @param type
	 *            审核类型
	 * @param collectAudit
	 * 
	 * @return 已认领任务
	 */
	public List<CollectAudit> findClaimTask(Integer page, String bsType,
			Integer type, CollectAudit collectAudit) {
		logger.entry(collectAudit);
		// 流程变量
		Map<String, Object> variableValue = new HashMap<String, Object>();
		variableValue.put("bsType", bsType);
		if (type == 9)
			variableValue.put("status", 8);
		else
			variableValue.put("type", type);
		// 从session中得到用户名
		String userName = (String) SecurityUtils.getSubject().getSession()
				.getAttribute("userName");// 从session里获取用户名
		// 查询当前用户的已认领任务
		List<Map<String, Object>> taskList = auditProcess.findClaimTask(
				userName, page, variableValue, collectAudit);
		List<CollectAudit> auditByIds = this.selectCollectAuditByIds(taskList,
				bsType, type);
		return logger.exit(auditByIds);
	}

	/**
	 * 查询当前用户的已认领任务
	 * 
	 * @param bsTaskId
	 *            业务任务ID
	 * @return 已认领任务
	 */
	public List<CollectAudit> findClaimTaskByBsTaskId(long bsTaskId,
			String bsType, Integer type) {
		Map<String, Object> variableValue = new HashMap<String, Object>();
		variableValue.put("bsType", bsType);
		variableValue.put("type", type);
		// 从session中得到用户名
		String userName = (String) SecurityUtils.getSubject().getSession()
				.getAttribute("userName");// 从session里获取用户名
		// 查询当前用户的已认领任务
		List<Map<String, Object>> taskList = auditProcess
				.findClaimTaskByBsTaskId(bsTaskId, userName, variableValue);
		return logger
				.exit(this.selectCollectAuditByIds(taskList, bsType, null));
	}

	/**
	 * 退领任务
	 * 
	 * @param taskId
	 *            任务ID
	 * 
	 * @throws TaskNotFoundException
	 *             任务不存在
	 */
	public void unclaimTask(String taskId) throws TaskNotFoundException {
		auditProcess.unclaimTask(taskId, "email");
	}

	/**
	 * 通过任务
	 * 
	 * @param taskId
	 *            任务ID
	 * @param flag
	 *            是否通过的标记（true为通过，false为不通过）
	 * @param collectAudit
	 *            业务实体
	 * @param collectAuditLogs
	 *            审核日志实体
	 * @throws TaskNotFoundException
	 *             任务不存在
	 * @throws IOException
	 *             连接MQ出现错误
	 */
	public void complete(String taskId, boolean flag,
			CollectAudit collectAudit, CollectAuditLogs collectAuditLogs)
			throws TaskNotFoundException, Exception {
		logger.entry(collectAudit);
		logger.entry(collectAuditLogs);
		// 从session中得到用户名
		String userName = (String) SecurityUtils.getSubject().getSession()
				.getAttribute("userName");// 从session里获取用户名
		logger.entry("userName=" + userName);
		Map<String, Object> map = auditProcess.findTaskByTaskId(taskId);// 通过任务ID得到流程内存变量
		logger.info("内存变量：" + map + ";executionId:" + map.get("executionId"));
		String bsTaskId = String.valueOf(map.get("bsTaskId"));// 得到业务ID

		// 更新审核名称，同时保存审核人员认为的类型备注信息
		collectAudit.setId(bsTaskId);
		auditDao.updateCollectAudit(collectAudit);

		// 记录日志
		collectAuditLogs.setAudit_id(bsTaskId);// 主表外键
		collectAuditLogs.setTask_id(taskId);// 工作流任务ID
		collectAuditLogs.setAudit_user(userName);// 审核用户
		collectAuditLogs.setStatus(flag ? StatusConstant.auditSuccess
				: StatusConstant.auditFailure);// 状态
		collectAuditLogs.setExecutionId(String.valueOf(map.get("executionId")));// 流程节点实例ID
		auditDao.insertHistory(collectAuditLogs);

		doAuditProcess(taskId, flag);

		// 如果已经结束，更新主表，并回传信息
		if (auditProcess.isFinished((String) map.get("processInstanceId"))) {
			auditDao.audit(bsTaskId, flag);// 更新主表状态，审核结束
			// 查询业务系统表，得到MQ的相关信息
			// 查询业务表，得到当前的业务实体
			collectAudit = auditDao.selectCollectAuditById(bsTaskId,
					String.valueOf(map.get("bsType")), null);
			ResultEntity resultEntity = new ResultEntity();
			resultEntity.setSuccess(true);// 状态为成功
			resultEntity.setInfo(collectAudit);// 实体
		}
	}

	/**
	 * 更新主表状态事件-更新主表状态
	 * 
	 * @param processInstanceId
	 *            流程实例ID
	 * @param executionId
	 */
	public void updateStatusEvent(String processInstanceId, String executionId,
			final int status) {
		// 查询业务ID
		String bsTaskId = auditProcess
				.findBusinessKeyByProcessInstanceId(processInstanceId);
		auditProcess.setVariables(executionId, new HashMap() {
			{
				put("status", status);
			}
		});
		// 更新主表
		auditDao.updateStatus(bsTaskId, status);
	}

	/**
	 * @Description: 设置标识区分部分通过还是全部通过
	 * @author 刘旭升
	 * @date 2015年8月31日 下午2:34:48
	 * @version V1.0
	 * @param processInstanceId
	 * @param executionId
	 */
	public void setFlagEvent(String processInstanceId, String executionId) {
		// 查询业务ID
		String bsTaskId = auditProcess
				.findBusinessKeyByProcessInstanceId(processInstanceId);
		boolean allPass = true;// 审核全通过标识
		List<Integer> statusList = new ArrayList<Integer>();
		Map<?, ?> map = this.auditAttrDaoForMongoDB.getStatusCount(bsTaskId);
		if (map != null) {
			List<?> list = (List<?>) map.get("attrList");
			if (list.size() > 0) {
				for (Object object : list) {
					if (((Map<String, String>) object).get("status") != null) {// 包含key
						String inStatus = ((Map<String, String>) object)
								.get("status");
						statusList.add(Integer.parseInt(inStatus));
						logger.info("状态:" + inStatus);
					}
				}
				for (Integer sta : statusList) {// 任务有一个不通过，则标识为false
					if (sta == 0) {
						allPass = false;
					}
					logger.info("审核全通过标识为" + allPass);
				}
			}
		}
		if (!allPass)// 如果是部分通过则，设置为false、可以申诉
			auditProcess.setVariables(executionId, new HashMap() {
				{
					put("flag", false);
				}
			});
	}

	/**
	 * 向MQ发送消息
	 * 
	 * @param processInstanceId
	 * @param bsType
	 * @throws IOException
	 */
	public void sendMessageEvent(String processInstanceId, Object bsType)
			throws Exception {
		// 查询业务ID
		logger.info("发送消息到客户端");
		String bsTaskId = auditProcess
				.findBusinessKeyByProcessInstanceId(processInstanceId);

		// long lbsType = 0;
		if (bsTaskId != null && bsType != null) {
			// lbsType = Integer.valueOf(bsType.toString());
			CollectAuditSystem collectAuditSystem = auditSysConfigDao
					.queryCollectAuditSystemById(bsType.toString());
			logger.info(collectAuditSystem);
			// 查询业务表，得到当前的业务实体
			CollectAudit collectAudit = auditDao.selectCollectAuditById(
					bsTaskId, bsType.toString(), null);
			collectAudit.setRegional_information(null);// 简短发送信息的长度
			logger.info(collectAudit);
			ResultEntity resultEntity = new ResultEntity();
			resultEntity.setSuccess(true);// 状态为成功
			resultEntity.setInfo(collectAudit);// 实体
			try {
				// 向mq发送消息
				String[] uri = collectAuditSystem.getMqurl().split(":");
				RabbitMQUtils.send(collectAuditSystem.getId(), uri[0],
						Integer.valueOf(uri[1]), null, null,
						collectAuditSystem.getOutput_queue(), resultEntity,
						true);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw e;
			}
		}
	}

	/**
	 * 审核超时事件-更新主表状态为超时
	 * 
	 * @param processInstanceId
	 *            流程实例ID
	 */
	public void timeoutEvent(String processInstanceId) {
		// 查询业务ID
		String bsTaskId = auditProcess
				.findBusinessKeyByProcessInstanceId(processInstanceId);
		// 更新主表
		auditDao.updateStatus(bsTaskId, StatusConstant.auditTimeOut);
	}

	/**
	 * 任务待确认
	 * 
	 * @param taskId
	 *            任务ID
	 * @param commentMessage
	 *            审核意见
	 * @throws TaskNotFoundException
	 *             任务不存在
	 */
	public void beConfirmed(String taskId, String commentMessage)
			throws TaskNotFoundException {
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("beConfirmed", true);// 待确认标记为true
		Map<String, Object> map = auditProcess.findTaskByTaskId(taskId);
		auditProcess.complete((String) map.get("taskId"), variableMap);
	}

	/**
	 * 查询自己参与过的所有任务
	 * 
	 * @param page
	 *            页号
	 * @param bsType
	 *            项目类型
	 * @param type
	 *            审核类型
	 * @param collectAudit
	 * 
	 * @return 自己参与过的所有任务
	 */
	public List<CollectAudit> findMyselfInvolvedTask(Integer page,
			String bsType, Integer type, CollectAudit collectAudit) {
		logger.info("-->进入经办任务查询方法findMyselfInvolvedTask");
		logger.info("---->入参：page==" + page + "；bsType" + bsType + "；type"
				+ type + "；CollectAudit" + collectAudit);
		Integer size = Integer.parseInt(PropertiesConfig
				.getProperty(PropConstants.AUDIT_LIST_SIZE));
		logger.info("---->size = " + size);
		// 从session中得到用户名
		String userName = (String) SecurityUtils.getSubject().getSession()
				.getAttribute("userName");// 从session里获取用户名

		Integer firstResult = page * size;
		return logger.exit(auditDao.findMyselfInvolvedTask(userName, bsType,
				type, firstResult, size, collectAudit));
	}

	/**
	 * 查询历史记录
	 * 
	 * @param bsTaskId
	 *            业务ID
	 * @param processInstanceId
	 *            流程实例ID
	 * @return 历史记录
	 * @throws TaskNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public List<HistoricalRecords> findHistory(String taskId,
			String processInstanceId) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			TaskNotFoundException {
		// 查询信息审核表
		// CollectAudit collectAudit =
		// auditDao.selectCollectAuditById(bsTaskId);
		List<HistoricalRecords> hrs = null;
		if (processInstanceId != null && !processInstanceId.isEmpty()) {
			hrs = auditProcess.findHistory(processInstanceId);
			logger.info(hrs);
		} else if (taskId != null && !taskId.isEmpty()) {
			Map<String, Object> map = auditProcess.findTaskByTaskId(taskId);
			logger.info(map);
			hrs = auditProcess.findHistory((String) map
					.get("processInstanceId"));
		}
		return hrs;
	}

	/**
	 * 查询历史记录的明细信息
	 * 
	 * @param taskId
	 *            任务ID
	 * @return
	 */
	public CollectAuditLogs findHistoryDtailByBsTaskId(String bsTaskId) {
		return logger.exit(auditDao.findHistoryDtailByBsTaskId(bsTaskId));
	}

	/**
	 * 根据业务ID绘制流程图
	 * 
	 * @param bsTaskId
	 *            业务ID
	 * @param processInstanceId
	 *            流程实例ID
	 * @param processDefinitionId
	 *            流程定义ID
	 * @param out
	 *            输出流
	 * @throws IOException
	 */
	public void processTracking(String taskId, String processInstanceId,
			String processDefinitionId, OutputStream out) throws Exception {
		if (processInstanceId != null && processDefinitionId != null
				&& !processInstanceId.isEmpty()
				&& !processDefinitionId.isEmpty()) {
			// 根据流程定义ID和流程运行ID绘制流程图
			auditProcess.processTracking(processDefinitionId,
					processInstanceId, out);
		} else if (taskId != null && !taskId.isEmpty()) {
			Map<String, Object> map = auditProcess.findTaskByTaskId(taskId);
			logger.info(map);
			// 根据流程定义ID和流程运行ID绘制流程图
			auditProcess.processTracking(
					(String) map.get("processDefinitionId"),
					(String) map.get("executionId"), out);
		}
	}

	/**
	 * 根据系统类型查询点线面的类型
	 * 
	 * @param system_type
	 * @return 点线面的类型
	 * @see com.autonavi.audit.constant.GISTypeConstant
	 */
	public int findGISType(String system_type) {
		return auditDao.findGISType(system_type);
	}

	/**
	 * 查询用户拍摄的照片
	 * 
	 * @param bsTaskId
	 * @return 用户拍摄的照片
	 */
	public List<CollectAuditImage> findUserPhotos(String bsTaskId) {
		return logger.exit(auditDao.findUserPhotos(bsTaskId));
	}

	/**
	 * 根据任务ID查询样张图片
	 * 
	 * @param bsTaskId
	 * @return
	 */
	public List<CollectAuditSpecimenImage> findAuditSpecimenImageByTaskId(
			String bsTaskId) {
		return logger.exit(auditDao.findAuditSpecimenImageByTaskId(bsTaskId));
	}

	/**
	 * 查询原始坐标
	 * 
	 * @param bsTaskId
	 * @return 坐标LIST
	 */
	public List<Double> findOriginalCoordinate(String bsTaskId) {
		return logger.exit(auditDao.findOriginalCoordinate(bsTaskId));
	}

	/**
	 * 添加任务意见
	 * 
	 * @param taskId
	 *            任务ID
	 * @param variableMap
	 *            参数（该参数需要用户灵活使用）
	 * @throws TaskNotFoundException
	 *             任务不存在
	 */
	public void addComment(String taskId, String processInstanceId,
			String message) throws TaskNotFoundException {
		String userId = (String) SecurityUtils.getSubject().getSession()
				.getAttribute("userName");// 从session里获取用户名
		auditProcess.addComment(taskId, processInstanceId, userId, message);
	}

	/**
	 * 获得意见
	 * 
	 * @param taskId
	 *            任务ID
	 * @return
	 * @throws TaskNotFoundException
	 */
	public List<Comment> getComments(String taskId)
			throws TaskNotFoundException {
		return logger.exit(auditProcess.getComments(taskId));
	}

	/**
	 * 删除意见
	 * 
	 * @param commentId
	 *            意见ID
	 * @throws TaskNotFoundException
	 */
	public void deleteComment(String commentId) throws TaskNotFoundException {
		auditProcess.deleteComment(commentId);
	}

	/**
	 * 添加附件
	 * 
	 * @param attachmentType
	 *            附件类型
	 * @param taskId
	 *            任务ID
	 * @param processInstanceId
	 *            线程实例ID
	 * @param attachmentName
	 *            附件名称
	 * @param attachmentDescription
	 *            附件描述
	 * @param url
	 *            附件地址
	 * @throws TaskNotFoundException
	 *             任务不存在
	 */
	public void addAttachment(String attachmentType, String taskId,
			String processInstanceId, String attachmentName,
			String attachmentDescription, String url)
			throws TaskNotFoundException {
		String userId = (String) SecurityUtils.getSubject().getSession()
				.getAttribute("userName");// 从session里获取用户名
		auditProcess.addAttachment(attachmentType, taskId, processInstanceId,
				userId, attachmentName, attachmentDescription, url);
	}

	/**
	 * 添加附件
	 * 
	 * @param attachmentType
	 *            附件类型
	 * @param taskId
	 *            任务ID
	 * @param processInstanceId
	 *            线程实例ID
	 * @param attachmentName
	 *            附件名称
	 * @param attachmentDescription
	 *            附件描述
	 * @param is
	 *            文件输入流
	 * @throws TaskNotFoundException
	 *             任务不存在
	 */
	public void addAttachment(String attachmentType, String taskId,
			String processInstanceId, String attachmentName,
			String attachmentDescription, InputStream is)
			throws TaskNotFoundException {

		String userId = (String) SecurityUtils.getSubject().getSession()
				.getAttribute("userName");// 从session里获取用户名
		auditProcess.addAttachment(attachmentType, taskId, processInstanceId,
				userId, attachmentName, attachmentDescription, is);
	}

	/**
	 * 获取附件
	 * 
	 * @param taskId
	 *            任务ID
	 * @return
	 * @throws TaskNotFoundException
	 *             任务不存在
	 */

	public List<Attachment> getAttachments(String taskId)
			throws TaskNotFoundException {
		return logger.exit(auditProcess.getAttachments(taskId));
	}

	/**
	 * 删除附件
	 * 
	 * @param attachmentId
	 *            附件ID
	 * @throws TaskNotFoundException
	 *             任务不存在
	 */
	public void deleteAttachment(String attachmentId)
			throws TaskNotFoundException {
		auditProcess.deleteAttachment(attachmentId);
	}

	/**
	 * 下载附件
	 * 
	 * @param attachmentId
	 *            附件ID
	 * @param outputStream
	 *            输出流
	 * @throws TaskNotFoundException
	 *             任务不存在
	 * @throws IOException
	 */
	public void getAttachmentContent(String attachmentId, OutputStream os)
			throws TaskNotFoundException, IOException {
		InputStream is = null;
		try {
			is = auditProcess.getAttachmentContent(attachmentId);
			IOUtils.copy(is, os);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * 获取各项目的待审批记录数
	 * 
	 * @param objectId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Long> getObjectCounts(String objectId) {
		Map<String, Long> rMap = new HashMap<String, Long>();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		// 从session里获取用户名
		String userName = (String) SecurityUtils.getSubject().getSession()
				.getAttribute("userName");
		// 从session中得到角色名
		List<String> roles = (List<String>) SecurityUtils.getSubject()
				.getSession().getAttribute("roles");
		logger.info("角色roles=" + roles);
		/**
		 * 进入后变成int
		 */
		variableMap.put("bsType", objectId);

		// 正常提交的审核数
		variableMap.put("type", TASK_STATUS.SUBMIT.getCode());
		long auditCount = this.count(variableMap, userName, roles);
		rMap.put("auditCount", auditCount);

		// 未找到审核数
		variableMap.put("type", TASK_STATUS.NOT_FOUND.getCode());
		long lessCount = this.count(variableMap, userName, roles);
		rMap.put("lessCount", lessCount);

		// 申诉审核数
		variableMap.put("type", TASK_STATUS.APPEAL.getCode());
		long appealCount = this.count(variableMap, userName, roles);
		rMap.put("appealCount", appealCount);
		return logger.exit(rMap);
	}

	/**
	 * 审核数（需要加上已认领的）
	 * 
	 * @param variableMap
	 *            内存奕量
	 * @param userName
	 *            用户名
	 * @param roles
	 *            角色
	 * @return
	 */
	private long count(Map<String, Object> variableMap, String userName,
			List<String> roles) {
		logger.info(variableMap);
		long count = auditProcess.findCounts(null, roles, variableMap);// 先用角色查，查到所有未认领但可以被当前角色认领的记录
		count += auditProcess.findCounts(userName, null, variableMap);// 再用用户名查，查询所有已认领的记录
		return count;
	}

	/**
	 * 功能：获取区域(一公里)内的任务信息
	 * 
	 * @author xusheng.liu
	 * @项目名称 collect-audit-web
	 * @date 2015年6月29日 上午11:33:41
	 * @param area
	 * @param bsTaskId
	 * @return 返回坐标点集合，key为任务Id
	 */
	public Map<String, List<CollectAuditAndCoordinate>> getTasksArea(
			Map<String, Double> area, String bsTaskId, String system_type) {
		logger.entry(area);
		Double nMax = null;
		Double nMin = null;
		Double eMax = null;
		Double eMin = null;
		if (area != null) {
			nMax = area.get("nMax");
			nMin = area.get("nMin");
			eMax = area.get("eMax");
			eMin = area.get("eMin");
		}
		// 2、获取该区域的任务信息
		List<CollectAuditAndCoordinate> collectAuditList = auditDao
				.findCollectAuditCoordinateByLongitudeAndLatitude(nMax, nMin,
						eMax, eMin, bsTaskId, system_type);
		logger.info(collectAuditList);
		Map<String, List<CollectAuditAndCoordinate>> collectAuditCoordinateMap = null;
		if (collectAuditList != null && collectAuditList.size() > 0) {
			collectAuditCoordinateMap = new HashMap<String, List<CollectAuditAndCoordinate>>();
			logger.info("遍历collectAuditList");
			for (CollectAuditAndCoordinate collectAuditAndCoordinate : collectAuditList) {
				logger.info("遍历collectAuditList");
				String auditId = collectAuditAndCoordinate.getAuditId();
				if (collectAuditCoordinateMap.containsKey(auditId)) {// 包含
					logger.info("map已经包含key");
					List<CollectAuditAndCoordinate> list1 = collectAuditCoordinateMap
							.get(auditId);
					list1.add(collectAuditAndCoordinate);
					collectAuditCoordinateMap.put(auditId, list1);
				} else {
					List<CollectAuditAndCoordinate> list1 = new ArrayList<CollectAuditAndCoordinate>();
					list1.add(collectAuditAndCoordinate);
					collectAuditCoordinateMap.put(auditId, list1);
				}
			}
		}
		return logger.exit(collectAuditCoordinateMap);
	}

	/**
	 * 功能：获取四个临界经纬度值
	 * 
	 * @author xusheng.liu
	 * @项目名称 collect-audit-web
	 * @date 2015年6月29日 上午11:33:14
	 * @param centerPoint
	 * @return
	 */
	public Map<String, Double> getArea(CollectAuditImage centerPoint,
			Double radius) {
		Map<String, Double> area = null;
		if (centerPoint != null && centerPoint.getLat() != null) {
			area = new HashMap<String, Double>();
			Double nMax = centerPoint.getLat();
			Double nMin = centerPoint.getLat();
			Double eMax = centerPoint.getLon();
			Double eMin = centerPoint.getLon();
			// 一公里以内的
			nMax += radius;
			nMin -= radius;
			eMax += radius;
			eMin -= radius;
			area.put("nMax", nMax);
			area.put("nMin", nMin);
			area.put("eMax", eMax);
			area.put("eMin", eMin);
		}
		return logger.exit(area);
	}

	/**
	 * 功能：获取所有近景的平均点
	 * 
	 * @author xusheng.liu
	 * @项目名称 collect-audit-web
	 * @date 2015年6月29日 下午2:39:27
	 * @param userPhotos
	 * @return
	 */
	public CollectAuditImage findTaskCenterPoint(
			List<CollectAuditImage> userPhotos) {
		logger.entry(userPhotos);
		CollectAuditImage centerImg = null;
		if (userPhotos != null && userPhotos.size() > 0) {
			centerImg = new CollectAuditImage();
			Double lonTol = 0D;// 总经度
			Double latTol = 0D;// 总纬度
			int count = 0;// 近景图片的张数
			for (CollectAuditImage cai : userPhotos) {
				if (cai.getIndex() != null && cai.getIndex() % 2 == 0) {// 近景
					lonTol += cai.getLon();
					latTol += cai.getLat();
					count++;
				}
			}
			if (lonTol != 0D && latTol != 0D) {// 不是初始值的时候才加入集合中
				centerImg.setLon(lonTol / count);
				centerImg.setLat(latTol / count);
			}
		}
		return logger.exit(centerImg);
	}

	/**
	 * @Description: 查询任务
	 * @author 刘旭升
	 * @date 2015年7月2日 下午4:18:13
	 * @version V1.0
	 * @param bsTaskId
	 * @param system_type
	 * @return
	 */
	public String findAuditById(String bsTaskId, String system_type) {
		CollectAudit auditById = this.auditDao.selectCollectAuditById(bsTaskId,
				system_type, null);
		logger.info("auditById==" + auditById);
		if (auditById != null)
			return auditById.getTask_class_name();
		return null;
	}

	/**
	 * @Description: 根据isUsed参数来确定对当前任务总价的增减，index和type来确定图片的单价
	 * @author 刘旭升
	 * @date 2015年7月3日 上午8:34:52
	 * @version V1.0
	 * @param imgId
	 *            图片id
	 * @param taskId
	 *            任务id
	 * @param index
	 *            远近景
	 * @param type
	 *            任务类型
	 * @param isUsed
	 *            使用或未使用
	 * @return
	 */
	public Double updateTaskMoneyByNoUsed(String imgId, String taskId,
			String index, String type, Boolean isUsed) {
		CollectAudit audit = auditDao.findCollectAuditByTaskId(taskId);
		logger.info("audit==" + audit);
		CollectAuditImage auditImage = auditDao
				.findCollectAuditImageById(imgId);
		logger.info("auditImage==" + auditImage);
		Double imgPrice = null;
		if (Integer.parseInt(index) % 2 == 0)// 近景价格
			imgPrice = this.auditDao.findNearImgPriceByIndex(type);
		else
			imgPrice = this.auditDao.findFarImgPriceByIndex(type);// 远景价格
		if ((!isUsed && auditImage.getUsed() == 1)
				|| (isUsed && auditImage.getUsed() == 0)) {// 前端设置为不使用，数据库是使用则修改
			auditDao.updateCollectAuditImageByPrimaryId(isUsed, imgId);// 修改图片的状态
			Double finalAmount = isUsed ? (CommonUtil.add(
					audit.getTask_amount(), imgPrice)) : (CommonUtil.sub(
					audit.getTask_amount(), imgPrice));
			audit.setTask_amount(finalAmount);
			auditDao.updateCollectAudit(audit);
			return logger.exit(finalAmount);
		}
		return null;
	}

	/**
	 * @Description: 申诉
	 * @author 刘旭升
	 * @date 2015年7月6日 下午1:35:07
	 * @version V1.0
	 * @param taskId
	 */
	public void appealTask(String taskId) throws TaskNotFoundException {
		// 流程通过
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("type", TASK_STATUS.APPEAL.getCode());
		variableMap.put("bmpStatus", BmpStatusConstant.appeal);// 申诉设置为4
		auditProcess.complete(taskId, variableMap);// 通过
	}

	/**
	 * @Description: 根据主键查询出任务明细
	 * @author 刘旭升
	 * @date 2015年6月30日 下午6:38:43
	 * @version V1.0
	 * @param taskId
	 *            任务id
	 * @return
	 */
	public CollectAudit findCollectAuditByTaskId(String taskId) {
		CollectAudit auditByTask = this.auditDao
				.findCollectAuditByTaskId(taskId);
		logger.info(auditByTask);
		return logger.exit(this.auditDao.findCollectAuditByTaskId(taskId));
	}

	/**
	 * @Description:审核完全结束,推送审核结果消息
	 * @author 刘旭升
	 * @date 2015年7月14日 下午7:07:37
	 * @version V1.0
	 * @param processInstanceId
	 * @param object
	 */
	public void sendMessageTaskEndEventMobile(String processInstanceId,
			Object bsTypeObject) {
		logger.info("审核结束推送消息");
		// 查询业务ID
		String bsTaskId = auditProcess
				.findBusinessKeyByProcessInstanceId(processInstanceId);
		logger.info("业务ID===" + bsTaskId);
		CollectAudit collectAudit = auditDao.selectCollectAuditById(bsTaskId,
				String.valueOf(bsTypeObject), null);
		logger.info("collectAudit===" + collectAudit);
		if (collectAudit != null) {
			logger.info("审核结束-推送消息成功");
			Map<String, String> pushMap = new HashMap<String, String>();
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date currDate = new Date();
			String time = formater.format(currDate);
			String token = "msgpush03:msgpush012:" + time;
			token = StringUtil.md5(token);
			pushMap.put("users", collectAudit.getUser_name()); // 推送用户名
			pushMap.put("token", token);
			pushMap.put("time", time);
			// pushMap.put("content", "任务审核完成,请查看审核结果");
			pushMap.put("alert", "申诉任务已审核，请在【待结算】中查看");
			pushMap.put("active", "1");// 1：当前登录设备，2：上一次登录设备,默认使用1
			HttpClientUtil.postAsyn(sendMessageHost + "/pushservice/push",
					pushMap, "UTF-8");
		} else {
			logger.info("审核结束-推送消息失败");
			// 用户查询失败
		}
	}

	/**
	 * @Description: 根据任务Id查询mongodb数据库,查询用户明细,图片等
	 * @author 刘旭升
	 * @date 2015年7月20日 下午5:26:55
	 * @version V1.0
	 * @param taskId
	 * @return
	 */
	public Map getDetail(String baseId) {
		if (!"".equals(baseId)) {
			Map map = this.auditAttrDaoForMongoDB.getAllAttr(baseId);
			// 获取权限是否显示金额
			List<String> roles = (List<String>) SecurityUtils.getSubject()
					.getSession().getAttribute("roles");
			if (roles != null && roles.size() > 0) {
				for (String string : roles) {
					if (AuditConstant.AUDIT_SHOW_MONEY_ROLE.equals(string)) {
						map.put(AuditConstant.AUDIT_SHOW_MONEY_ROLE, "true");
					}
				}
			}
			return map;
		}
		return null;
	}

	/**
	 * @Description: 修改状态
	 * @author 刘旭升
	 * @date 2015年7月22日 下午3:18:15
	 * @version V1.0
	 * @param baseId
	 *            业务id
	 * @param type
	 *            类型
	 * @param taskClassNameForAudit
	 *            审核认为的值
	 * @param collectAuditLogs
	 *            审核信息
	 */
	public void updateStatusByBaseIdAndTypeAndStatus(String baseId,
			String type, String taskClassNameForAudit,
			CollectAuditLogs collectAuditLogs) {
		logger.info("参数列表===baseId:" + baseId + ";type" + type
				+ ";taskClassNameForAudit" + taskClassNameForAudit);
		String userName = (String) SecurityUtils.getSubject().getSession()
				.getAttribute("userName");
		this.auditAttrDaoForMongoDB.updateStatus(baseId, type,
				taskClassNameForAudit, collectAuditLogs, userName);
	}

	/**
	 * @Description: 根据业务id,查询其所有的审核项：一通过则通过，全不通过才不通过。如有未审核项则不允许审核
	 * @author 刘旭升
	 * @date 2015年7月24日 下午2:18:59
	 * @version V1.0
	 * @param taskId
	 *            任务id
	 * @param baseId
	 *            业务id
	 * @param json
	 *            审核信息
	 * @return 审核通过与否:0不通过,1通过,2报错,null参数为空
	 * @throws TaskNotFoundException
	 * @throws IOException
	 * @throws JsonParseException
	 */
	public Integer auditSuccess(String taskId, String baseId, String json)
			throws TaskNotFoundException, JsonParseException, IOException {
		if (!"".equals(baseId) || !"".equals(taskId) || !"".equals(json)) {
			logger.info("审核参数完整");
			// 修改状态，同时记录不通过原因
			Map<String, List<?>> statusAndCasMap = updateStatusAndReason(
					baseId, json);
			logger.info("封装大json数据完成");
			List<Integer> statusList = (List<Integer>) statusAndCasMap
					.get("stas");// 状态集合
			logger.info("状态集合:" + statusList);
			List<CollectAuditLogs> caList = (List<CollectAuditLogs>) statusAndCasMap
					.get("cas");// 不通过原因集合
			logger.info("原因集合:" + caList);
			Map<?, ?> map = this.auditAttrDaoForMongoDB.getStatusCount(baseId);
			if (map != null) {
				List<?> list = (List<?>) map.get("attrList");
				if (list.size() > 0) {
					boolean auditSuccess = false;// 审核通过标识
					for (Object object : list) {
						if (((Map<String, String>) object).get("status") != null) {// 包含key
							String inStatus = ((Map<String, String>) object)
									.get("status");
							statusList.add(Integer.parseInt(inStatus));
							logger.info("状态:" + inStatus);
						}
					}
					if (list.size() > statusList.size())// 状态不完全
						return 2;
					for (Integer sta : statusList) {// 任务是一个通过则全部通过
						if (sta == 1) {
							auditSuccess = true;
							logger.info("审核通过标识为true");
							break;
						}
					}

					for (CollectAuditLogs ca : caList) {
						updateStatusByBaseIdAndTypeAndStatus(baseId,
								ca.getChildName(), "", ca);
					}
					doAuditProcess(taskId, auditSuccess);
					logger.info("审核通过完成");
					return auditSuccess ? 1 : 0;
				}
			}
		}
		return null;// 参数为空
	}

	/**
	 * @Description: 修改 给采集用户的金额、扣客户的金额
	 * @author xusheng.liu
	 * @date 2015年10月16日 下午12:04:48
	 * @version V1.0
	 * @param baseId
	 * @param totalUserChangeMoney
	 * @param totalCustomMoney
	 */
	private void setMoneyForMySql(String baseId, Double totalUserChangeMoney,
			Double totalCustomMoney) {
		CollectAudit collectAudit = auditDao.findCollectAuditByTaskId(baseId);
		collectAudit.setTask_amount(totalUserChangeMoney);
		collectAudit.setCustom_tolal_money(totalCustomMoney);
		auditDao.updateCollectAudit(collectAudit);
	}

	/**
	 * @Description: 存储状态,记录不通过原因信息到map中
	 * @author 刘旭升
	 * @date 2015年8月28日 上午9:36:40
	 * @version V1.0
	 * @param baseId
	 *            任务id
	 * @param json
	 *            状态不通过原因信息集
	 * @return 返回状态和不通过原因集合
	 */
	private Map<String, List<?>> updateStatusAndReason(String baseId,
			String json) {
		Map<String, List<?>> map = null;// 返回对象,存储状态集合和不通过原因集合
		List<CollectAuditLogs> caList = null;// 存储不通过原因
		List<Integer> staList = null;// 存储状态
		if (json != null && json != "" && baseId != null && baseId != "") {
			map = new HashMap<String, List<?>>();
			caList = new ArrayList<CollectAuditLogs>();
			staList = new ArrayList<Integer>();
			// 解析json,然后修改状态和信息
			logger.info("json:" + json);
			JSONObject objJson = JSONObject.fromObject(json);
			logger.info("大json转化为对象:" + objJson);
			Map mapJson = (Map) objJson;
			logger.info("大json转化为Map:" + mapJson);
			for (Object keyJson : mapJson.keySet()) {// 循环修改，这里可以优化为一次修改。
				logger.info("遍历json转化的Map中的key:" + keyJson);
				if (keyJson.equals("submit"))
					continue;
				Map valJson = null;
				if (mapJson.containsKey(keyJson))// 含有key
					valJson = (Map) mapJson.get(keyJson);
				logger.info(keyJson + "对应的value值:" + valJson);
				if (!valJson.isEmpty()) {
					// 封装集合
					packageCasAndStatusList(caList, staList, keyJson, valJson);
				}
			}
			map.put("stas", staList);
			map.put("cas", caList);
		}
		return map;
	}

	/**
	 * @Description: 封装原因集合和状态集合
	 * @author 刘旭升
	 * @date 2015年8月28日 上午10:42:31
	 * @version V1.0
	 * @param caList
	 *            原因集合
	 * @param staList
	 *            状态集合
	 * @param keyJson
	 *            key值
	 * @param valJson
	 *            json信息窜
	 */
	private void packageCasAndStatusList(List<CollectAuditLogs> caList,
			List<Integer> staList, Object keyJson, Map valJson) {
		CollectAuditLogs collectAuditLogs;
		logger.info("json内部map不为空");
		Integer statusString = null;
		if (valJson.containsKey("status")) {// 含有key
			statusString = Integer.valueOf(valJson.get("status").toString());
			staList.add(statusString);
		}
		logger.info(keyJson + "中的status值为:" + statusString);
		collectAuditLogs = new CollectAuditLogs();
		collectAuditLogs.setStatus(statusString);
		collectAuditLogs.setChildName((String) keyJson);
		if (AuditConstant.status_fall.equals(statusString)) {
			logger.info(keyJson + "不通过");
			if (valJson.containsKey("messsage")) {// 含有key
				collectAuditLogs.setComment_message(valJson.get("messsage")
						.toString());
				logger.info(keyJson + "不通过反馈信息:"
						+ valJson.get("messsage").toString());
			}
			if (valJson.containsKey("reason")) {// 含有key
				collectAuditLogs.setNo_approval_reason(valJson.get("reason")
						.toString());
				logger.info(keyJson + "不通过原因:"
						+ valJson.get("reason").toString());
			}
		}
		Double moneyString = 0.0;
		if (valJson.containsKey("money")) {// 含有key
			moneyString = Double.valueOf(valJson.get("money").toString());
			collectAuditLogs.setTotalMoney(moneyString);
			logger.info(keyJson + "金额为:" + valJson.get("money").toString());
		}
		caList.add(collectAuditLogs);
	}

	/**
	 * @Description: 走动流程,重构方法
	 * @author 刘旭升
	 * @date 2015年8月7日 下午5:08:14
	 * @version V1.0
	 * @param taskId
	 * @param auditFlag
	 * @throws TaskNotFoundException
	 */
	private void doAuditProcess(String taskId, boolean auditFlag)
			throws TaskNotFoundException {
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("flag", auditFlag);// 是否通过的标记
		variableMap.put("beConfirmed", false);// 待确认标记为false
		auditProcess.complete(taskId, variableMap);// 通过
	}

	/**
	 * @Description: 查询所有的任务
	 * @author 刘旭升
	 * @date 2015年7月28日 上午11:13:21
	 * @version V1.0
	 * @return 主任务列表
	 */
	public List<CollectAudit> findALlCollectAudit() {
		return this.auditDao.findALlCollectAudit();
	}

	/**
	 * @Description: 查询所有任务集合,根据条件
	 * @author 刘旭升
	 * @date 2015年7月31日 下午6:24:20
	 * @version V1.0
	 * @param page
	 *            分页信息
	 * @param bsType
	 *            项目
	 * @param type
	 *            类型
	 * @param collectAudit
	 *            条件封装对象
	 * @return
	 */
	public List<CollectAudit> findAuditList(Integer page, Integer pageSize,
			String bsType, CollectAudit collectAudit) {
		logger.entry(collectAudit);
		if (collectAudit != null)
			return logger.exit(auditDao.selectCollectAuditByCondition(bsType,
					pageSize, page * pageSize,
					collectAudit.getSubmit_time_start(),
					collectAudit.getSubmit_time_end(),
					collectAudit.getCollect_task_name(),
					collectAudit.getUser_name(),
					collectAudit.getTask_class_name()));
		else
			return logger.exit(auditDao.selectCollectAuditByCondition(bsType,
					pageSize, page * pageSize, null, null, null, null, null));
	}

	/**
	 * @Description:查询总数
	 * @author 刘旭升
	 * @date 2015年8月3日 下午6:15:05
	 * @version V1.0
	 * @param page
	 * @param bsType
	 * @param collectAudit
	 * @param auditStatus
	 * @return
	 */
	public Long findAuditListCount(Integer page, String bsType,
			CollectAudit collectAudit, Integer auditStatus) {
		logger.entry(collectAudit);
		if (collectAudit == null)
			collectAudit = new CollectAudit();
		if (auditStatus.equals(0)) { // 审核中
			return logger.exit(auditDao
					.selectCollectAuditCountByConditionForInAudit(bsType,
							collectAudit.getSubmit_time_start(),
							collectAudit.getSubmit_time_end(),
							collectAudit.getCollect_task_name(),
							collectAudit.getUser_name(),
							collectAudit.getTask_class_name()));
		} else if (auditStatus.equals(1)) { // 审核通过
			return logger.exit(auditDao
					.selectCollectAuditCountByConditionForAuditComplete(bsType,
							true, collectAudit.getSubmit_time_start(),
							collectAudit.getSubmit_time_end(),
							collectAudit.getCollect_task_name(),
							collectAudit.getUser_name(),
							collectAudit.getTask_class_name()));
		} else if (auditStatus.equals(2)) { // 审核未通过
			return logger.exit(auditDao
					.selectCollectAuditCountByConditionForAuditComplete(bsType,
							false, collectAudit.getSubmit_time_start(),
							collectAudit.getSubmit_time_end(),
							collectAudit.getCollect_task_name(),
							collectAudit.getUser_name(),
							collectAudit.getTask_class_name()));
		} else if (auditStatus.equals(3)) { // 申诉中
			return logger.exit(auditDao
					.selectCollectAuditCountByConditionForInAppeal(bsType,
							collectAudit.getSubmit_time_start(),
							collectAudit.getSubmit_time_end(),
							collectAudit.getCollect_task_name(),
							collectAudit.getUser_name(),
							collectAudit.getTask_class_name()));
		} else {
			return logger.exit(auditDao.selectCollectAuditCountByCondition(
					bsType, collectAudit.getSubmit_time_start(),
					collectAudit.getSubmit_time_end(),
					collectAudit.getCollect_task_name(),
					collectAudit.getUser_name(),
					collectAudit.getTask_class_name()));
		}
	}

	/**
	 * @Description: 查询周边任务（经济环境数据）
	 * @author 刘旭升
	 * @date 2015年8月4日 下午7:48:13
	 * @version V1.0
	 * @param lon
	 *            经度
	 * @param lat
	 *            维度
	 * @return
	 * @throws Exception
	 */
	public Object searchAround(double lon, double lat) throws Exception {
		String[] names = search_user.split(",");
		return this.searchService.doSearch(names, lon, lat, search_radius);
	}

	/**
	 * @Description: 支持条件查询任务列表(含审核状态[审核中0,审核通过1,审核不通过2,申诉中3])
	 * @author 刘旭升
	 * @date 2015年8月18日 下午5:54:37
	 * @version V1.0
	 * @param page
	 * @param pageSize
	 * @param bsType
	 * @param auditStatus
	 *            [审核中0,审核通过1,审核不通过2,申诉中3]
	 * @param collectAudit
	 * @return
	 */
	public List<CollectAudit> findAuditListWithAuditStatus(Integer page,
			int pageSize, String bsType, Integer auditStatus,
			CollectAudit collectAudit) {
		logger.entry(collectAudit);
		if (collectAudit == null)
			collectAudit = new CollectAudit();
		if (auditStatus.equals(0)) { // 审核中
			return logger.exit(auditDao
					.selectCollectAuditByConditionForInAudit(bsType, pageSize,
							page * pageSize,
							collectAudit.getSubmit_time_start(),
							collectAudit.getSubmit_time_end(),
							collectAudit.getCollect_task_name(),
							collectAudit.getUser_name(),
							collectAudit.getTask_class_name()));
		} else if (auditStatus.equals(1)) { // 审核通过
			return logger.exit(auditDao
					.selectCollectAuditByConditionForAuditComplete(bsType,
							pageSize, page * pageSize, true,
							collectAudit.getSubmit_time_start(),
							collectAudit.getSubmit_time_end(),
							collectAudit.getCollect_task_name(),
							collectAudit.getUser_name(),
							collectAudit.getTask_class_name()));
		} else if (auditStatus.equals(2)) { // 审核未通过
			return logger.exit(auditDao
					.selectCollectAuditByConditionForAuditComplete(bsType,
							pageSize, page * pageSize, false,
							collectAudit.getSubmit_time_start(),
							collectAudit.getSubmit_time_end(),
							collectAudit.getCollect_task_name(),
							collectAudit.getUser_name(),
							collectAudit.getTask_class_name()));
		} else if (auditStatus.equals(3)) { // 申诉中
			return logger.exit(auditDao
					.selectCollectAuditByConditionForInAppeal(bsType, pageSize,
							page * pageSize,
							collectAudit.getSubmit_time_start(),
							collectAudit.getSubmit_time_end(),
							collectAudit.getCollect_task_name(),
							collectAudit.getUser_name(),
							collectAudit.getTask_class_name()));
		} else {
			return logger.exit(auditDao.selectCollectAuditByCondition(bsType,
					pageSize, page * pageSize,
					collectAudit.getSubmit_time_start(),
					collectAudit.getSubmit_time_end(),
					collectAudit.getCollect_task_name(),
					collectAudit.getUser_name(),
					collectAudit.getTask_class_name()));
		}
	}

	public ResultEntity queryProject() throws Exception {
		logger.info("进入项目方法queryProject");
		String json = null;
		ResultEntity resultEntity = new ResultEntity();
		JsonBinder jb = JsonBinder.buildNormalBinder(false);
		Pagination pagination = new Pagination();
		List<Object> objectList = redisUtilComponent.getRedisJsonCache(
				AuditConstant.ALL_NORMAL_PROJECT_INFO_CACHE_PREFIX, List.class,
				jb);
		if (objectList == null) {
			json = HttpClientUtil.get(Config.get_projects, null);
			logger.info("查询项目远程接口:json==" + json);
			if (StringUtils.isBlank(json))
				return null;
			ObjectMapper objectMapper = new ObjectMapper();
			resultEntity = objectMapper.readValue(json, ResultEntity.class);
			logger.info("查询项目对象:resultEntity==" + resultEntity);
		} else {
			logger.info("从缓存中获取信息:objectList==" + objectList);
			pagination.setObjectList(objectList);
			resultEntity.setInfo(pagination);
		}
		return resultEntity;
	}

	/**
	 * @Description: 支付记录
	 * @author xusheng.liu
	 * @date 2015年10月12日 下午3:43:53
	 * @version V1.0
	 * @param processInstanceId
	 * @param id
	 */
	public void recordPayDetailEvent(String processInstanceId, String id) {
		// 查询业务ID
		logger.info("-->进入支付记录方法recordPayDetailEvent");
		logger.info("-->入参：processInstanceId==" + processInstanceId + "；id=="
				+ id);
		String bsTaskId = auditProcess
				.findBusinessKeyByProcessInstanceId(processInstanceId);
		logger.info("---->任务id==" + bsTaskId);
		Map<?, ?> map = auditAttrDaoForMongoDB.getAllAttr(bsTaskId);
		logger.info("---->查询mongodb获得map==" + map);
		CollectAuditPayRecord capr = new CollectAuditPayRecord();
		Long gid = primaryByRedis.generateEcode();
		capr.setId(String.valueOf(gid));
		if (map == null)
			capr.setContent("mongodb数据查询任务id为：[" + bsTaskId
					+ "]的任务信息为null，可能是广告拍拍兼容导致的问题");
		else
			capr.setContent(JsonBinder.buildNonDefaultBinder().toJson(map));
		capr.setStatus(PayStatusConstant.payNoResult);
		capr.setTaskId(bsTaskId);
		logger.info("---->封装支付记录对象==" + capr);
		logger.info("---->支付记录结束");
		auditPayRecordDao.insertCollectAuditPayRecord(capr);
	}

	/**
	 * @Description: 支付接口调用
	 * @author xusheng.liu
	 * @date 2015年10月12日 下午4:55:30
	 * @version V1.0
	 * @param processInstanceId
	 * @param id
	 */
	public void payEvent(String processInstanceId, String id) throws Exception {
		logger.info("-->执行支付接口调用");
		String bsTaskId = auditProcess
				.findBusinessKeyByProcessInstanceId(processInstanceId);
		logger.info("-->任务id==" + bsTaskId);
		Map<?, ?> map = auditAttrDaoForMongoDB.getAllAttr(bsTaskId);
		String content = packageContentForPay(bsTaskId, map);
		Map<String, String> mapForContent = new HashMap<String, String>();
		mapForContent.put("content", content);
		String json = HttpClientUtil.post(Config.audit_comment_pay_url,
				mapForContent, "utf-8");
		logger.info("---->支付远程接口返回结果:json==" + json);
		if (!StringUtils.isBlank(json)) {
			ObjectMapper objectMapper = new ObjectMapper();
			ResultEntity resultEntity = objectMapper.readValue(json,
					ResultEntity.class);
			logger.info("---->支付返回结果:resultEntity==" + resultEntity);
			if (resultEntity != null) {// 支付成功
				CollectAuditPayRecord auditPayRecord = new CollectAuditPayRecord();
				auditPayRecord.setContent(JsonBinder.buildNonDefaultBinder()
						.toJson(map));
				auditPayRecord.setTaskId(bsTaskId);
				if (resultEntity.isSuccess())
					auditPayRecord.setStatus(PayStatusConstant.paySuccess);
				else
					auditPayRecord.setStatus(PayStatusConstant.payFall);
				logger.info("---->封装支付记录对象==" + auditPayRecord);
				logger.info("---->支付接口调用结束");
				this.auditPayRecordDao
						.updateCollectAuditPayRecord(auditPayRecord);
			}
		}
	}

	/**
	 * @Description:
	 * @author xusheng.liu
	 * @date 2015年10月12日 下午6:17:28
	 * @version V1.0
	 * @param bsTaskId
	 * @return
	 */
	private String packageContentForPay(String bsTaskId, Map<?, ?> map) {
		String content = "";
		if (map != null && map.containsKey("attrs")) {
			Map<String, ?> attrs = (Map<String, ?>) map.get("attrs");
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			for (Entry<String, ?> entry : attrs.entrySet()) {
				@SuppressWarnings("rawtypes")
				Map m = (Map) entry.getValue();
				String ownerId = (String) m.get("ownerId");
				String collectClassParentId = (String) m
						.get("collectClassParentId");
				String collectClassId = (String) m.get("collectClassId");
				String userMoney = (String) m.get("userMoney");
				sb.append("{\"ownerId\":\"" + ownerId + "\"");
				sb.append(",\"collectClassParentId\":\"" + collectClassParentId
						+ "\"");
				sb.append(",\"collectClassId\":\"" + collectClassId + "\"");
				sb.append(",\"userMoney\":\"" + userMoney + "\"");
				sb.append(",\"taskId\":\"" + bsTaskId + "\"},");
			}
			content = sb.substring(0, sb.length() - 1) + "]";
		}
		return content;
	}

	/**
	 * @Description: 计算各种总金额[通用方法]（给采集用户的钱[原始]、给采集用户的钱[修改]、扣客户的钱）
	 * @author xusheng.liu
	 * @date 2015年10月16日 上午10:37:44
	 * @version V1.0
	 * @param bsTaskId
	 *            任务id
	 * @param map
	 * @return
	 */
	public Map<String, Double> getTotalMoneyFormMongodb(String bsTaskId) {
		Map<?, ?> map = auditAttrDaoForMongoDB.getAllAttr(bsTaskId);
		if (map == null)// 查询为空则返回为null，不修改数据库字段值
			return null;
		Map<String, Double> moneyTotalMap = new HashMap<String, Double>();
		Double totalUserMoney = 0.0d;
		Double totalCustomMoney = 0.0d;
		Double totalUserChangeMoney = 0.0d;
		if (map.containsKey("attrs") && map.containsKey("attrList")) {
			// 记录部分（mongodb上部分）
			List<?> list = (List<?>) map.get("attrList");
			Map<String, ?> attrs = (Map<String, ?>) map.get("attrs");
			if (list.size() > 0) {
				logger.info("任务baseId：" + bsTaskId + "==获取数据信息‘attrList’成功");
				for (Object object : list) {
					if (((Map<String, String>) object).get("status") != null
							&& ((Map<String, String>) object).get("name") != null) {// 包含key
						String inStatus = ((Map<String, String>) object)
								.get("status");
						String name = ((Map<String, String>) object)
								.get("name");
						if (AuditConstant.status_ok.toString().equals(inStatus)) {
							logger.info("子任务:" + name + "：审核通过，准备累计金额");
							// 显示部分（mongodb下部分）
							for (Entry<String, ?> entry : attrs.entrySet()) {
								String key = entry.getKey();
								if (key.equals(name)) {// 上部分和下部分对上
									logger.info("--attrList信息和attrs信息对应上，开始累计金额");
									Map m = (Map) entry.getValue();
									String userChangeMoney = (String) m
											.get("userChangeMoney");
									String customMoney = (String) m
											.get("customMoney");
									String userMoney = (String) m
											.get("userMoney");
									logger.info("-----给采集用户的钱[原始]：" + userMoney);
									logger.info("-----给采集用户的钱[修改]："
											+ userChangeMoney);
									logger.info("-----扣客户的钱：" + customMoney);
									if (userChangeMoney != null
											&& customMoney != null
											&& userMoney != null) {
										totalUserMoney = CommonUtil.add(
												totalUserMoney,
												Double.valueOf(userMoney));
										totalUserChangeMoney = CommonUtil
												.add(totalUserChangeMoney,
														Double.valueOf(userChangeMoney));
										totalCustomMoney = CommonUtil.add(
												totalCustomMoney,
												Double.valueOf(customMoney));
									} else
										logger.error("任务baseId："
												+ bsTaskId
												+ "==任务出现错误，mongodb数据库字段不全：customMoney="
												+ customMoney
												+ "userChangeMoney="
												+ userChangeMoney
												+ "userMoney=" + userMoney);
								}
							}
						}
					}
				}
			}
		}
		moneyTotalMap.put(AuditConstant.totalUserMoney, totalUserMoney);
		moneyTotalMap.put(AuditConstant.totalUserChangeMoney,
				totalUserChangeMoney);
		moneyTotalMap.put(AuditConstant.totalCustomMoney, totalCustomMoney);
		return moneyTotalMap;
	}

	/**
	 * @Description: 统计金额
	 * @author xusheng.liu
	 * @date 2015年10月16日 上午11:54:24
	 * @version V1.0
	 * @param processInstanceId
	 */
	public void updateTotalMoney(String processInstanceId) {
		logger.info("进入updateTotalMoney方法");
		logger.info("入参：processInstanceId--" + processInstanceId);
		String bsTaskId = auditProcess
				.findBusinessKeyByProcessInstanceId(processInstanceId);
		Map<String, Double> moneyFormMongodb = getTotalMoneyFormMongodb(bsTaskId);
		if (moneyFormMongodb != null) {
			setMoneyForMySql(bsTaskId,
					moneyFormMongodb.get(AuditConstant.totalUserChangeMoney),
					moneyFormMongodb.get(AuditConstant.totalCustomMoney));// 同步mysql金额
			auditAttrDaoForMongoDB.setMoney(bsTaskId,
					moneyFormMongodb.get(AuditConstant.totalUserChangeMoney));
		}
	}

	/**
	 * @Description: 查询认领人集合
	 * @author xusheng.liu
	 * @date 2015年11月18日 下午4:22:28
	 * @version V1.0
	 * @param type
	 * @param sysType
	 * @return
	 */
	public List<CollectAuditClaim> getClaimUser(Integer type, String sysType) {
		logger.info("-->进入getClaimUser查询认领人集合方法");
		logger.info("---->入参：type=" + type + ";sysType" + sysType);
		return claimService.queryCollectAuditClaim(new CollectAuditClaim(null,
				sysType, null, type.toString()));
	}

	public List<String> getPropertiseNames(String systemId){
		logger.info("-->进入getPropertiseNames方法");
		logger.info("---->入参：systemId" + systemId);
		return this.editPlatformDao.findProNamesBySystemId(systemId);
	}
	
	/**
	 * @Description: 查询指定项目的审核任务（客户中心下载模块使用）
	 * @author xusheng.liu
	 * @date 2015年11月24日 上午10:51:42
	 * @version V1.0
	 * @param systemId
	 *            项目id
	 * @param submit_time_start
	 *            开始提交时间
	 * @param submit_time_end
	 *            结束提交时间
	 * @return
	 * @throws Exception 
	 */
	public List<ExportTask> findInfoBySysId(String systemId,
			String submit_time_start, String submit_time_end) throws Exception {
		logger.info("-->进入[findInfoBySysId]客户中心导出方法");
		logger.info("---->入参：systemId=" + systemId + ";submit_time_start="
				+ submit_time_start + ";submit_time_end=" + submit_time_end);
		// 构建查询参数,查询结果
		List<CollectAudit> audits = this.auditDao
				.findCollectAudit(packCollectAuditArgs(systemId,
						submit_time_start, submit_time_end));
		// 返回集合对象
		List<ExportTask> returnList = null;
		if (audits != null && audits.size() > 0) {
			returnList = new ArrayList<ExportTask>();
			ExportTask task = null;
			logger.info("---->查询列表成功，size：" + audits.size());
			for (CollectAudit ca : audits) {
				task = new ExportTask();
				task.setCity(ca.getCity());
				task.setCollect_task_name(ca.getCollect_task_name());
				task.setTask_class_name(ca.getTask_class_name());
				task.setSubmit_time(ca.getSubmit_time());
				task.setSystem_type(systemId);

				Map<?, ?> map = auditAttrDaoForMongoDB.getAllAttr(ca.getId());
				if (map != null)// 查询为空则返回为null，不修改数据库字段值
					packTaskNameAndType(returnList, task, ca, map);
			}
		}
		return returnList;
	}

	/**
	 * @Description: 封装返回对象(客户中心:导出)
	 * @author xusheng.liu
	 * @date 2015年11月24日 下午5:07:37 
	 * @version V1.0 
	 * @param returnList
	 * @param task
	 * @param ca
	 * @param map
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private void packTaskNameAndType(List<ExportTask> returnList,
			ExportTask task, CollectAudit ca, Map<?, ?> map) throws Exception {
		if (map.containsKey("attrs") && map.containsKey("attrList")) {
			// 记录部分（MONGODB上部分）
			List<?> attrList = (List<?>) map.get("attrList");
			Map<String, ?> attrs = (Map<String, ?>) map.get("attrs");
			if (attrList.size() > 0) {
				logger.info("---->任务baseId：" + ca.getId()
						+ "==获取数据信息‘attrList’成功");
				for (Object object : attrList) {
					if (((Map<String, String>) object).get("status") != null
							&& AuditConstant.status_ok
									.toString()
									.equals(((Map<String, String>) object)
											.get("status").toString())) {// 包含key
						String name = ((Map<String, String>) object)
								.get("name");
						String collectClazzName = ((Map<String, String>) object)
								.get("collectClazzName");
						String batchId = ((Map<String, String>) object)
								.get("batchId");
						ExportTask taskReturn = task.clone();
						taskReturn.setChildTaskName(name);			//子任务名称
						taskReturn.setChildTaskType(collectClazzName);//子任务类型
						taskReturn.setChildTaskId(batchId);			//子任务id
						logger.info("---->子任务:" + name + ";batchId=" + batchId + "：审核通过，加入集合");
						List<Point> imgsPoint = new ArrayList<Point>();
						//封装图片信息
						getImgsFromMongo(taskReturn, attrs, name, imgsPoint);
						//覆盖物坐标
						logger.info("---->获取覆盖物信息开始");
						List<Point> points = this.editPlatformDao.queryPointByBatchId(batchId);
						if(points!=null && points.size()>0){
							//覆盖物属性信息
							logger.info("---->覆盖物信息不为空");
							List<Property> properties = this.editPlatformDao.queryPropertyByBatchId(batchId);
							taskReturn.setProps(properties);
							getAveragePoint(taskReturn, batchId, points);
						}else if(imgsPoint!=null && imgsPoint.size()>0){
							logger.info("---->无覆盖物");
							getAveragePoint(taskReturn, batchId, imgsPoint);
						}
						logger.info("-->加入集合成功");
						returnList.add(taskReturn);
					}
				}
			}
		}
	}

	/**
	 * @Description: 获取图片信息
	 * @author xusheng.liu
	 * @date 2015年11月24日 下午5:02:21 
	 * @version V1.0 
	 * @param task
	 * @param attrs
	 * @param name
	 * @param imgsPoint
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getImgsFromMongo(ExportTask task, Map<String, ?> attrs,
			String name, List<Point> imgsPoint) {
		// 显示部分（MONGODB下部分）
		for (Entry<String, ?> entry : attrs .entrySet()) {
			String key = entry.getKey();
			if (key.equals(name)) {// 上部分和下部分对上
				logger.info("---->attrs信息获取，开始获取图片信息");
				Map m = (Map) entry.getValue();
				List<Map> imgs = (List<Map>) m
						.get("imgs");
				StringBuffer sb = null;
				Point point = null;
				if (imgs != null && imgs.size()>0) {
					sb = new StringBuffer();
					logger.info("---->图片集合大小为:"+imgs.size());
					point = new Point();
					for (Map img : imgs) {
						if(img.containsKey("image_url")){
							sb.append(img.get("image_url")+";");
						}
						if(img.containsKey("lon") && img.containsKey("lat")){
							point.setLat(img.get("lat").toString());
							point.setLng(img.get("lon").toString());
							imgsPoint.add(point);
						}
					}
					logger.info("---->图片地址集合为："+sb.toString());
					task.setImageUrlString(sb.toString());//图片路径集合
				}
			}
		}
	}

	/**
	 * @Description: 获取平均坐标点
	 * @author xusheng.liu
	 * @date 2015年11月24日 下午4:59:31 
	 * @version V1.0 
	 * @param task
	 * @param batchId
	 * @param points
	 */
	private void getAveragePoint(ExportTask task, String batchId,
			List<Point> points) {
		task.setPoints(points);
		//求百度坐标：
		/*百度坐标，子任务点或面中心点所在百度地图中的坐标。如果此子任务有一个编辑面，则以面中心点作为子任务点，
		 *如果有多个编辑面，则以面中心点平均值作为此子任务点坐标；如果此子任务只有一个编辑点，以此编辑点为子任务点坐标，
		 *如果有多个编辑点，则求多点平均值作为此子任务坐标；如果没有编辑点，则以近景照片点平均值作为此子任务点坐标；
		 *如无近景远景之分，则以所有照片点平均值作为此子任务点坐标*/
		Double latTotal = 0.0d;
		Double lonTotal = 0.0d;
		for (Point p : points) {
			latTotal += new Double(p.getLat());
			lonTotal += new Double(p.getLng());
		}
		task.setCoordinates_baidu(new Double[]{lonTotal/points.size(),latTotal/points.size()});
	}

	/**
	 * @Description: 构建查询参数
	 * @author xusheng.liu
	 * @date 2015年11月24日 下午1:47:48
	 * @version V1.0
	 * @param systemId
	 *            项目Id
	 * @param submit_time_start
	 *            提交开始时间
	 * @param submit_time_end
	 *            提交结束时间
	 * @return 封装的查询对象
	 */
	private CollectAudit packCollectAuditArgs(String systemId,
			String submit_time_start, String submit_time_end) {
		CollectAudit collectAudit = new CollectAudit();
		collectAudit.setSystem_type(systemId);
		collectAudit.setSubmit_time_start_string(submit_time_start);
		collectAudit.setSubmit_time_end_string(submit_time_end);
		return collectAudit;
	}

	@Resource(name = "auditService")
	private AuditService auditService = null;

	@Autowired
	private AuditProcess auditProcess = null;

	@Autowired
	private SearchService searchService = null;

	@Autowired
	private AuditAttrDaoForMongoDB auditAttrDaoForMongoDB = null;

	@Autowired
	private AuditDao auditDao = null;
	@Autowired
	private AuditPayRecordDao auditPayRecordDao = null;
	@Autowired
	private PrimaryByRedis primaryByRedis;
	@Autowired
	private RedisUtilComponent redisUtilComponent;
	@Autowired
	private ClaimService claimService;
	@Autowired
	private EditPlatformDao editPlatformDao;

	@Autowired
	private AuditSysConfigDao auditSysConfigDao = null;
	private Logger logger = LogManager.getLogger(getClass());
	private static Validator validator;

	@Value("${search_user}")
	private String search_user = null;

	@Value("${send_message_host}")
	private String sendMessageHost = null;

	@Value("${search_radius}")
	private Integer search_radius = null;

}
