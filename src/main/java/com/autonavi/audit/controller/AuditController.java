
package com.autonavi.audit.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.autonavi.audit.constant.AuditConstant;
import com.autonavi.audit.constant.GISTypeConstant;
import com.autonavi.audit.entity.CollectAudit;
import com.autonavi.audit.entity.CollectAuditAndCoordinate;
import com.autonavi.audit.entity.CollectAuditImage;
import com.autonavi.audit.entity.CollectAuditLogs;
import com.autonavi.audit.entity.CollectAuditSystem;
import com.autonavi.audit.entity.HistoricalRecords;
import com.autonavi.audit.entity.LayerEntity;
import com.autonavi.audit.entity.ResultEntity;
import com.autonavi.audit.exception.TaskAlreadyClaimedException;
import com.autonavi.audit.exception.TaskNotFoundException;
import com.autonavi.audit.service.AuditService;
import com.autonavi.audit.service.AuditSysConfigService;
import com.autonavi.audit.service.EditPlatformService;

@Controller
@RequestMapping("/audit")
public class AuditController {

	@RequestMapping("/audit")
	public void audit() {
	}
	
	@RequestMapping("/auditEnvir")
	public void auditEnvir() {
	}
	
	@RequestMapping("/taskAllocation")
	public void taskAllocation() {
	}
	
	@RequestMapping("/list")
	public void list() {
	}
	
	@RequestMapping("/listForEnvir")
	public void listForEnvir() {
	}

	/**
	 * @Description: 
	 * @author 刘旭升
	 * @date 2015年7月31日 下午6:13:23 
	 * @version V1.0 
	 * @param page
	 * @param bsType
	 * @param type
	 * @param collectAudit
	 * @return
	 */
	@RequestMapping("/auditList")
	public @ResponseBody List<CollectAudit> auditList(
			@RequestParam("page") Integer page,
			@RequestParam("auditStatus") Integer auditStatus,
			@RequestParam("bsType") String bsType, CollectAudit collectAudit) {
		logger.trace("进入auditList方法");
		int pageSize = 10;
		if(auditStatus!=-1){
			//增加对审核状态的查询
			return auditService.findAuditListWithAuditStatus(page,pageSize, bsType,auditStatus,
					collectAudit);
		}else
		return logger.exit(auditService.findAuditList(page,pageSize, bsType,
				collectAudit));
	}
	
	/**
	 * @Description: 查询任务书总数
	 * @author 刘旭升
	 * @date 2015年8月3日 下午6:14:06 
	 * @version V1.0 
	 * @param page
	 * @param bsType
	 * @param collectAudit
	 * @return
	 */
	@RequestMapping("/auditListCount")
	public @ResponseBody String auditListCount(
			@RequestParam("page") Integer page,
			@RequestParam("auditStatus") Integer auditStatus,
			@RequestParam("bsType") String bsType, CollectAudit collectAudit) {
		logger.trace("进入auditListCount方法");
		int pageSize = 10;
		Long count = auditService.findAuditListCount(page, bsType,collectAudit,auditStatus);
		return "{\"count\":"+count+",\"pageSize\":"+pageSize+"}";
	}
	
	
	/**
	 * 获取所有项目
	 * 
	 * @return
	 */
	@RequestMapping("/getObjects")
	public @ResponseBody List<CollectAuditSystem> getObjects() {
		List<CollectAuditSystem> list = auditSysConfigService
				.selectCollectAuditSystem();
		return list;
	}

	/**
	 * 获取该项目的所有待审核记录数
	 * 
	 * @return
	 */
	@RequestMapping("/getObjectCounts")
	public @ResponseBody Map<String, Long> getObjectCounts(
			@RequestParam("objectId") String objectId) {
		Map<String, Long> rMap = auditService.getObjectCounts(objectId);
		return rMap;
	}

	/**
	 * 待办任务
	 * 
	 * @param page
	 *            页号
	 * @param bsType
	 *            项目类型
	 * @param type
	 *            任务类型
	 * @param collectAudit
	 *            查询条件
	 * @return 查询结果
	 */
	@RequestMapping("/findRepresentativeTask")
	public @ResponseBody List<CollectAudit> findRepresentativeTask(
			@RequestParam("page") Integer page,
			@RequestParam("bsType") String bsType,
			@RequestParam("type") Integer type, CollectAudit collectAudit) {
		logger.trace("进入findRepresentativeTask方法");
		return logger.exit(auditService.findRepresentativeTask(page, bsType,
				type, collectAudit));
	}

	/**
	 * 认领任务
	 * 
	 * @param taskId
	 *            工作流任务ID
	 * @return
	 */
	@RequestMapping("/claimTask")
	public @ResponseBody String claimTask(@RequestParam("taskId") String taskId) {
		logger.trace("进入claimTask方法");
		logger.entry(taskId);
		try {
			auditService.claimTask(taskId);
			return null;
		} catch (TaskAlreadyClaimedException e) {
			logger.error(e.getMessage(), e);
			return "任务已被其他用户认领！";
		} catch (TaskNotFoundException e) {
			logger.error(e.getMessage(), e);
			return "任务不存在！";
		}
	}

	/**
	 * @Description: 申诉任务
	 * @author 刘旭升
	 * @date 2015年7月6日 下午1:29:36
	 * @version V1.0
	 * @param taskId
	 *            工作流任务ID
	 * @return
	 */
	@RequestMapping("/appealTask")
	public @ResponseBody String appealTask(@RequestParam("taskId") String taskId) {
		logger.trace("进入appealTask方法");
		logger.entry(taskId);
		try {
			auditService.appealTask(taskId);
			return null;
		} catch (TaskNotFoundException e) {
			logger.error(e.getMessage(), e);
			return "任务不存在！";
		}
	}

	/**
	 * @Description: 获取任务的明细(图片,样张等)
	 * @author 刘旭升
	 * @date 2015年7月22日 下午3:03:37
	 * @version V1.0
	 * @param taskId
	 * @return
	 */
	@RequestMapping("/getDetail")
	public @ResponseBody Map<?, ?> getDetail(
			@RequestParam("baseId") String baseId) {
		logger.trace("进入getDetail获取任务明细方法");
		logger.trace("任务id",baseId);
		LayerEntity layerEntity = new LayerEntity();
		List<LayerEntity> layerEntities= null;
		Map<?, ?> detail = auditService.getDetail(baseId);
		logger.trace("detail"+detail);
		if(detail!=null){
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> attrList = (List<Map<String, Object>>)detail.get("attrList");
			logger.trace("attrList"+attrList);
			String collectClassName = (String)detail.get("collectClassName");
			logger.trace("collectClassName"+collectClassName);
			layerEntity.setName(collectClassName);
			layerEntity.setPro_type(collectClassName);
			if(attrList!=null&&attrList.size()>0){
				layerEntities = new ArrayList<LayerEntity>();
				for (Map<String, Object> map : attrList) {
					if(map.containsKey("collectClazzName")){
						LayerEntity le = new LayerEntity();
						logger.trace("map.get(collectClazzName)"+map.get("collectClazzName"));
						le.setName((String)map.get("collectClazzName"));
						le.setPro_type((String)map.get("collectClazzName"));
						layerEntities.add(le);
					}
				}
			}
			//调用创建图层的方法
			layerEntity.setList(layerEntities);
			editPlatformService.insertLayer(layerEntity);
			logger.trace("layerEntity"+layerEntity);
		}
		return detail;
	}
	
	/**
	 * @Description: 审核完成
	 * @author 刘旭升
	 * @date 2015年7月24日 下午2:14:58 
	 * @version V1.0 
	 * @param baseId
	 * @return
	 */
	@RequestMapping("/auditSuccess")
	public @ResponseBody String auditSuccess(
			@RequestParam("taskId") String taskId,
			@RequestParam("baseId") String baseId,
			@RequestParam("json") String json) {
		logger.info("进入updateStatus修改任务状态");
		logger.info("任务id:" + taskId);
		logger.info("业务id:" + baseId);
		logger.info("修改任务状态的json串:" + json);
		try {//0不通过,1通过,2报错,null参数为空
			Integer flagInteger = this.auditService.auditSuccess(taskId,baseId,json);
			logger.info("审核完成:"+flagInteger);
			return "{\"flag\":"+flagInteger+"}";
		} catch (TaskNotFoundException e) {
			logger.error(e.getMessage(), e);
			return "{\"flag\":"+false+"\"message\":\"任务不存在\"}";
		} catch (Exception e) {
			logger.info("审核失败",e);
			return "{\"flag\":"+false+"}";
		}
	}

	/**
	 * 退领任务
	 * 
	 * @param taskId
	 *            工作流任务ID
	 * @return
	 */
	@RequestMapping("/unclaimTask")
	public @ResponseBody String unclaimTask(
			@RequestParam("taskId") String taskId) {
		logger.trace("进入unclaimTask方法");
		logger.entry(taskId);
		try {
			auditService.unclaimTask(taskId);
			return null;
		} catch (TaskNotFoundException e) {
			logger.error(e.getMessage(), e);
			return "任务不存在！";
		}
	}

	/**
	 * 已认领任务
	 * 
	 * @param page
	 *            页号
	 * @param bsType
	 *            项目类型
	 * @param type
	 *            任务类型
	 * @param collectAudit
	 *            查询条件
	 * @return 查询结果
	 */
	@RequestMapping("/findClaimTask")
	public @ResponseBody List<CollectAudit> findClaimTask(
			@RequestParam("page") Integer page,
			@RequestParam("bsType") String bsType,
			@RequestParam("type") Integer type, CollectAudit collectAudit) {
		logger.trace("进入findClaimTask方法");
		return logger.exit(auditService.findClaimTask(page, bsType, type,
				collectAudit));
	}

	/**
	 * 根据业务ID绘制流程图
	 * 
	 * @param bsTaskId
	 * @param respone
	 * @throws IOException
	 */
	@RequestMapping("/processTracking.png")
	public void processTracking(
			@RequestParam(value = "taskId", required = false) String taskId,
			@RequestParam(value = "processInstanceId", required = false) String processInstanceId,
			@RequestParam(value = "processDefinitionId", required = false) String processDefinitionId,
			HttpServletResponse respone) throws Exception {
		logger.trace("进入processTracking方法");
		logger.entry(taskId, processInstanceId, processDefinitionId);
		OutputStream out = respone.getOutputStream();
		try {
			auditService.processTracking(taskId, processInstanceId,
					processDefinitionId, out);
			out.flush();
		} finally {
			out.close();
		}
	}

	/**
	 * 查询历史记录
	 * 
	 * @param bsTaskId
	 *            业务ID
	 * @return 历史记录
	 */
	@RequestMapping("/findHistory")
	public @ResponseBody List<HistoricalRecords> findHistory(
			@RequestParam(value = "taskId", required = false) String taskId,
			@RequestParam(value = "processInstanceId", required = false) String processInstanceId) {
		logger.trace("进入findHistory方法");
		logger.entry(taskId, processInstanceId);
		List<HistoricalRecords> hrs = null;
		try {
			hrs = auditService.findHistory(taskId, processInstanceId);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return logger.exit(hrs);
	}

	/**
	 * 查询历史记录的明细信息
	 * 
	 * @param taskId
	 *            任务ID
	 * @return 历史记录
	 */
	@RequestMapping("/findHistoryDtailByBsTaskId")
	public @ResponseBody CollectAuditLogs findHistoryDtailByBsTaskId(
			@RequestParam(value = "bsTaskId", required = false) String bsTaskId) {
		logger.trace("进入findHistoryDtailByBsTaskId方法");
		logger.entry(bsTaskId);
		CollectAuditLogs collectAuditLogs = auditService
				.findHistoryDtailByBsTaskId(bsTaskId);
		return logger.exit(collectAuditLogs);
	}

	/**
	 * 显示详细页面
	 */
	@RequestMapping("/auditHistoryDatil")
	public void auditHistoryDatil() {
	}

	/**
	 * 审核任务
	 * 
	 * @param bsTaskId
	 *            业务任务ID
	 * @param flag
	 *            是否通过的标记（true为通过，false为不通过）
	 * @param collectAuditLogs
	 *            审核日志实体
	 * @throws TaskNotFoundException
	 *             任务不存在
	 */
	@RequestMapping("/doAudit")
	public @ResponseBody String doAudit(@RequestParam("taskId") String taskId,
			@RequestParam("flag") boolean flag, CollectAudit collectAudit,
			CollectAuditLogs collectAuditLogs) {
		logger.trace("进入doAudit方法");
		logger.entry(taskId, flag, collectAuditLogs);
		try {
			auditService.complete(taskId, flag, collectAudit, collectAuditLogs);
			return "审核成功！";
		} catch (TaskNotFoundException e) {
			logger.error(e.getMessage(), e);
			return "任务不存在！";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "任务发送至消息队列失败，请稍后重试或通知管理员！";
		}
	}
	
	/**
	 * 任务待确认
	 * 
	 * @param bsTaskId
	 *            业务任务ID
	 * @param commentMessage
	 *            审核意见
	 * @throws TaskNotFoundException
	 *             任务不存在
	 */
	@RequestMapping("/beConfirmed")
	public @ResponseBody String beConfirmed(
			@RequestParam("taskId") String taskId,
			@RequestParam("commentMessage") String commentMessage) {
		logger.trace("进入beConfirmed方法");
		logger.entry(taskId, commentMessage);
		try {
			auditService.beConfirmed(taskId, commentMessage);
			return "任务已进入待确认状态，等待进一步处理";
		} catch (TaskNotFoundException e) {
			logger.error(e.getMessage(), e);
			return "任务不存在！";
		}
	}

	/**
	 * 查询自己参与过的所有任务
	 * 
	 * @param page
	 *            页号
	 * @param bsType
	 *            项目类型
	 * @param type
	 *            任务类型
	 * @param collectAudit
	 *            查询条件
	 * @return 自己参与过的所有任务
	 */
	@RequestMapping("/findMyselfInvolvedTask")
	public @ResponseBody List<CollectAudit> findMyselfInvolvedTask(
			@RequestParam("page") Integer page,
			@RequestParam("bsType") String bsType,
			@RequestParam("type") Integer type, CollectAudit collectAudit) {
		return logger.exit(auditService.findMyselfInvolvedTask(page, bsType,
				type, collectAudit));
	}

	/**
	 * 查询所有的照片
	 * 
	 * @param bsTaskId
	 *            业务任务ID
	 * @return
	 */
	@RequestMapping("/findPhoto")
	public @ResponseBody Map<String, Object> findPhoto(
			@RequestParam("id") String bsTaskId,
			@RequestParam("system_type") String system_type) {
		Map<String, Object> photoMap = new HashMap<String, Object>();
		photoMap.put("gisType", auditService.findGISType(system_type));// 点线面类型
		photoMap.put("specimenImage",
				auditService.findAuditSpecimenImageByTaskId(bsTaskId));// 样张
		List<CollectAuditImage> userPhotos = auditService
				.findUserPhotos(bsTaskId);
		photoMap.put("userPhoto", userPhotos);// 用户拍摄
		// 原始坐标list
		List<Double> coordinateList = auditService
				.findOriginalCoordinate(bsTaskId);
		photoMap.put("originalCoordinate", coordinateList);// 原始坐标
		/** 功能更新 */
		// 根据已查询出任务的所有远近景的点，算出近景平均值（点），查询任务点。
		CollectAuditImage centerPoint = auditService
				.findTaskCenterPoint(userPhotos);
		Double radius = AuditConstant.radius_point;
		if(system_type.equals(GISTypeConstant.plane+""))//为面则查询5公里范围的小区
			radius = AuditConstant.radius_plane;
		Map<String, Double> area = auditService.getArea(centerPoint,radius);// 区域范围
		photoMap.put("area", area);// 纬度四点
		photoMap.put("centerPoint", centerPoint);// 任务中心点
		Map<String, List<CollectAuditAndCoordinate>> otherCoordinates = auditService
				.getTasksArea(area, bsTaskId,system_type);
		photoMap.put("otherCoordinates", otherCoordinates);// 区域内坐标
		photoMap.put("className",
				auditService.findAuditById(bsTaskId, system_type));// 区域内坐标
		return photoMap;
	}

	/**
	 * @Description: 查询指定任务的明细
	 * @author 刘旭升
	 * @date 2015年6月30日 下午5:02:40
	 * @version V1.0
	 * @param taskId
	 *            任务id
	 * @return
	 */
	@RequestMapping("/taskDetial")
	public @ResponseBody Map<String, Object> findTaskDetial(
			@RequestParam("taskId") String taskId) {
		Map<String, Object> detailMap = new HashMap<String, Object>();
		List<CollectAuditImage> userPhotos = auditService
				.findUserPhotos(taskId);
		CollectAudit auditDetail = auditService
				.findCollectAuditByTaskId(taskId);
		detailMap.put("userPhoto", userPhotos);// 用户拍摄
		detailMap.put("auditDetail", auditDetail);// 任务明细
		return detailMap;
	}

	/**
	 * @Description: 根据isUsed参数来确定对当前任务总价的增减，index和type来确定图片的单价
	 * @author 刘旭升
	 * @date 2015年7月3日 上午8:33:52
	 * @version V1.0
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
	@RequestMapping("/updateMoneyPhotoUsed")
	public @ResponseBody Double updateMoneyPhotoUsed(
			@RequestParam("taskId") String taskId,
			@RequestParam("index") String index,
			@RequestParam("type") String type,
			@RequestParam("imgId") String imgId,
			@RequestParam("isUsed") Boolean isUsed) {
		return this.auditService.updateTaskMoneyByNoUsed(imgId, taskId, index,
				type, isUsed);
	}

	/**
	 * 下载附件
	 * 
	 * @param attachmentId
	 * @param respone
	 * @throws IOException
	 * @throws TaskNotFoundException
	 */
	@RequestMapping("/downAttachment")
	public void getAttachmentContent(
			@RequestParam("attachmentId") String attachmentId,
			HttpServletResponse respone) throws IOException,
			TaskNotFoundException {
		OutputStream out = respone.getOutputStream();
		try {
			auditService.getAttachmentContent(attachmentId, out);
			out.flush();
		} finally {
			out.close();
		}
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
	@RequestMapping("/addComment")
	public @ResponseBody ResultEntity addComment(
			@RequestParam("taskId") String taskId,
			@RequestParam("processInstanceId") String processInstanceId,
			@RequestParam("message") String message) {
		ResultEntity resultEntity = new ResultEntity();
		try {
			auditService.addComment(taskId, processInstanceId, message);
			resultEntity.setSuccess(true);
			return resultEntity;
		} catch (Exception e) {
			resultEntity.setSuccess(false);
			resultEntity.setDesc("审核意见添加失败!");
			return resultEntity;
		}
	}

	/**
	 * 获取意见
	 * 
	 * @param taskId
	 * @return
	 */
	@RequestMapping("/getComments")
	public @ResponseBody ResultEntity getComments(
			@RequestParam("taskId") String taskId) {
		ResultEntity resultEntity = new ResultEntity();
		try {
			List<Comment> comments = auditService.getComments(taskId);
			resultEntity.setSuccess(true);
			resultEntity.setInfo(comments);
			return resultEntity;
		} catch (Exception e) {
			resultEntity.setSuccess(false);
			resultEntity.setDesc("查询审核意见失败!");
			return resultEntity;
		}
	}

	/**
	 * 删除意见
	 * 
	 * @param commentId
	 * @return
	 */
	@RequestMapping("/delComment")
	public @ResponseBody ResultEntity deleteComment(
			@RequestParam("commentId") String commentId) {
		ResultEntity resultEntity = new ResultEntity();
		try {
			auditService.deleteComment(commentId);
			resultEntity.setSuccess(true);
			return resultEntity;
		} catch (Exception e) {
			resultEntity.setSuccess(false);
			resultEntity.setDesc("删除审核意见失败!");
			return resultEntity;
		}
	}

	/**
	 * 添加URL附件
	 * 
	 * @param attachmentType
	 * @param taskId
	 * @param processInstanceId
	 * @param attachmentName
	 * @param attachmentDescription
	 * @param url
	 * @return
	 */
	@RequestMapping("/addAttachment")
	public void addAttachment(
			@RequestParam("taskId") String taskId,
			@RequestParam("processInstanceId") String processInstanceId,
			@RequestParam("attachmentName") String attachmentName,
			@RequestParam("attachmentDescription") String attachmentDescription,
			@RequestParam("url") String url, HttpServletResponse res) {
		String attachmentType = "url";

		try {
			auditService.addAttachment(attachmentType, taskId,
					processInstanceId, attachmentName, attachmentDescription,
					url);
			res.getWriter().write("<script>parent.showResponse('T')</script>");
		} catch (Exception e) {
			try {
				res.getWriter().write(
						"<script>parent.showResponse('F')</script>");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * 添加文件附件
	 * 
	 * @param attachmentType
	 * @param taskId
	 * @param processInstanceId
	 * @param attachmentName
	 * @param attachmentDescription
	 * @param url
	 * @return
	 */
	@RequestMapping("/addUrlAttachment")
	public void addFileAttachment(
			@RequestParam("taskId") String taskId,
			@RequestParam("processInstanceId") String processInstanceId,
			@RequestParam("attachmentName") String attachmentName,
			@RequestParam("attachmentDescription") String attachmentDescription,
			@RequestParam("myfile") MultipartFile myfile,
			HttpServletResponse res) {

		String attachmentType = myfile.getContentType() + ";"
				+ FilenameUtils.getExtension(myfile.getOriginalFilename());
		try {
			auditService.addAttachment(attachmentType, taskId,
					processInstanceId, attachmentName, attachmentDescription,
					myfile.getInputStream());
			res.getWriter().write("<script>parent.showResponse('T')</script>");
		} catch (Exception e) {
			try {
				res.getWriter().write(
						"<script>parent.showResponse('F')</script>");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * 获取附件
	 * 
	 * @param taskId
	 * @return
	 */
	@RequestMapping("/getAttachments")
	public @ResponseBody ResultEntity getAttachments(
			@RequestParam("taskId") String taskId) {

		ResultEntity resultEntity = new ResultEntity();
		try {
			List<Attachment> attachments = auditService.getAttachments(taskId);
			resultEntity.setSuccess(true);
			resultEntity.setInfo(attachments);
			return resultEntity;
		} catch (Exception e) {
			resultEntity.setSuccess(false);
			resultEntity.setDesc("查询附件失败!");
			return resultEntity;
		}
	}

	/**
	 * 删除附件
	 * 
	 * @param attachmentId
	 * @return
	 */
	@RequestMapping("/delAttachment")
	public @ResponseBody ResultEntity delAttachment(
			@RequestParam("attachmentId") String attachmentId) {

		ResultEntity resultEntity = new ResultEntity();
		try {
			auditService.deleteAttachment(attachmentId);
			resultEntity.setSuccess(true);
			return resultEntity;
		} catch (Exception e) {
			resultEntity.setSuccess(false);
			resultEntity.setDesc("删除附件失败!");
			return resultEntity;
		}
	}

	/**
	 * @Description: 查询周边任务(经济环境)
	 * @author 刘旭升
	 * @date 2015年8月4日 下午5:46:02 
	 * @version V1.0 
	 * @param xString
	 * @param yString
	 * @return
	 */
	@RequestMapping("/findAroundForEnvir")
	public @ResponseBody Object findAroundForEnvir(
			@RequestParam("x") String xString,
			@RequestParam("y") String yString){
		logger.trace(xString);
		logger.trace(yString);
		if(!"".equals(xString)&&!"".equals(yString)){
			try {
				Object searchAround = this.auditService.searchAround(Double.parseDouble(xString), Double.parseDouble(yString));
				return searchAround;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return "参数错误";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * @Description: 查询当前客户的项目
	 * @author xusheng.liu
	 * @date 2015年9月21日 下午9:26:08
	 * @version V1.0
	 * @param enterprise
	 * @return
	 */
	@RequestMapping("queryProject")
	public @ResponseBody ResultEntity queryProject() {
		try {
			logger.info("进入查询项目方法queryProject");
			return auditService.queryProject();
		} catch (Exception e) {
			ResultEntity result = new ResultEntity();
			result.setDesc("查询项目失败");
			logger.error("查询项目失败", e);
			return result;
		}
	}
	
	/**
	 * @Description: 获取所有项目
	 * @author xusheng.liu
	 * @date 2015年10月9日 下午1:57:05 
	 * @version V1.0 
	 * @return
	 */
	@RequestMapping("/getObjectList")
	public @ResponseBody ResultEntity getObjectList() {
		logger.info("-->进去查询项目方法");
		try {
			return auditService.queryProject();
		} catch (Exception e) {
			logger.error("查询项目失败",e);
			ResultEntity resultEntity = new ResultEntity();
			resultEntity.setSuccess(false);
			e.printStackTrace();
			return resultEntity;
		}
	}
	
	@Autowired
	private AuditSysConfigService auditSysConfigService;

	@Autowired
	private AuditService auditService = null;
	
	@Autowired
	private EditPlatformService editPlatformService = null;
	
	private Logger logger = LogManager.getLogger(getClass());
	
}
