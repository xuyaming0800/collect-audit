package com.autonavi.audit.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autonavi.audit.constant.AuditConstant;
import com.autonavi.audit.dao.ClaimDao;
import com.autonavi.audit.entity.CollectAuditClaim;
import com.autonavi.audit.entity.Pagination;
import com.autonavi.audit.entity.ResultEntity;
import com.autonavi.audit.service.ClaimService;
import com.autonavi.audit.service.Config;
import com.autonavi.audit.util.HttpClientUtil;
import com.autonavi.audit.util.HttpRequestUtil;
import com.autonavi.audit.util.JsonBinder;
import com.autonavi.audit.util.RedisUtilComponent;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("claimService")
public class ClaimServiceImpl implements ClaimService {

	private Logger logger = LogManager.getLogger(getClass());

	public List<Object> getCustomIdList(String customName, String userType,
			String url) throws Exception {
		List<Object> customs = new ArrayList<Object>();
		List<?> customList = getCustomList(userType);
		if (customList == null || customList.size() == 0) {
			customs = HttpRequestUtil.getUserList(url, customName);
		} else {
			for (Object custom : customList) {
				@SuppressWarnings("rawtypes")
				Map user = (Map) custom;
				String name = (String) user.get("name");
				if (name.contains(customName)) {
					customs.add(custom);
				}
			}
		}
		return customs;
	}

	public List<?> getCustomList(String userType) throws Exception {
		JsonBinder jb = JsonBinder.buildNormalBinder(false);
		List<?> userList = redisUtilComponent.getRedisJsonCache(
				AuditConstant.USER_LIST_TYPE_CACHE_PREFIX + userType,
				List.class, jb);
		if (userList == null) {
			logger.warn("userType=[" + userType + "] cache is null ");
		}
		return userList;
	}

	@Override
	public ResultEntity getProjects(String customId) throws Exception {
		logger.info("-->进入项目方法getProjects");
		logger.info("---->入参：customId=" + customId);
		String json = null;
		ResultEntity resultEntity = new ResultEntity();
		JsonBinder jb = JsonBinder.buildNormalBinder(false);
		Pagination pagination = new Pagination();
		List<?> objectList = redisUtilComponent.getRedisJsonCache(
				AuditConstant.CUSTOM_PROJECT_INFO_CACHE_PREFIX + customId,
				List.class, jb);
		if (objectList == null) {
			json = HttpClientUtil.get(Config.get_projects + "&customId="
					+ customId, null);
			logger.info("查询项目远程接口:json==" + json);
			if (StringUtils.isBlank(json))
				return null;
			ObjectMapper objectMapper = new ObjectMapper();
			resultEntity = objectMapper.readValue(json, ResultEntity.class);
			logger.info("---->查询项目对象:resultEntity==" + resultEntity);
		} else {
			logger.info("---->从缓存中获取信息:objectList==" + objectList);
			pagination.setObjectList(objectList);
			resultEntity.setInfo(pagination);
		}
		logger.info("-->项目方法getProjects[end]");
		return resultEntity;
	}

	@Override
	public ResultEntity addCollectAuditClaim(CollectAuditClaim collectAuditClaim)
			throws Exception {
		logger.info("-->进入addCollectAuditClaim保存方法");
		logger.info("---->入参：collectAuditClaim="+collectAuditClaim);
		ResultEntity re = new ResultEntity();
		@SuppressWarnings("unchecked")
		Map<String, Object> userInfo = (Map<String, Object>) SecurityUtils
				.getSubject().getSession().getAttribute("userInfo");
		logger.info("---->获取当前操作者信息：userInfo"+userInfo);
		if (collectAuditClaim != null
				&& StringUtils.isBlank(collectAuditClaim.getCustomId())
				|| StringUtils.isBlank(collectAuditClaim.getClaimType())
				|| StringUtils.isBlank(collectAuditClaim.getSystemId())) {
			logger.info("-->必填参数为空，保存失败");
			re.setSuccess(false);
			re.setDesc("必填参数为空，保存失败");
		} else {
			logger.info("---->参数完整");
			if (collectAuditClaim.getId()!=null) {// 修改
				logger.info("---->开始修改操作");
				collectAuditClaim.setUpdateBy(userInfo.get("id").toString());
				collectAuditClaim.setUpdateByName(userInfo.get("name").toString());
				claimDao.updateClaim(collectAuditClaim);
			} else {// 保存
				//是否已经存在
				List<CollectAuditClaim> list = this.claimDao.queryClaimByCondition(collectAuditClaim);
				if(list!=null && list.size()>0){
					re.setSuccess(false);
					re.setDesc("当前保存的项目，所位于的审核节点分配人已经存在");
					return re;
				}
				logger.info("---->开始保存操作");
				collectAuditClaim.setCreateBy(userInfo.get("id").toString());
				collectAuditClaim.setCreateByName(userInfo.get("name").toString());
				claimDao.insertClaim(collectAuditClaim);
			}
			re.setSuccess(true);
			re.setDesc("保存成功");
			logger.info("-->addCollectAuditClaim方法执行结束");
		}
		return re;
	}

	@Override
	public Pagination queryCollectAuditClaim(Integer pageNo, Integer pageSize,
			CollectAuditClaim collectAuditClaim) throws Exception {
		logger.info("-->执行方法queryCollectAuditClaim");
		Pagination pagination = null;
		if(pageNo!=null && pageSize != null){
			logger.info("---->必须参数不为空");
			pagination = new Pagination(pageNo,pageSize);
			List<CollectAuditClaim> list = this.claimDao.query(pagination.getStart(),pageSize,collectAuditClaim);
			logger.info("---->查询获取结果大小："+list.size());
			long total = claimDao.queryCount(collectAuditClaim);
			logger.info("---->该条件下的总数为："+total);
			pagination.setTotalCount(total);
			pagination.setObjectList(list);
		}
		logger.info("-->queryCollectAuditClaim查询结束");
		return pagination;
	}
	
	@Override
	public ResultEntity delCollectAuditClaim(CollectAuditClaim collectAuditClaim) {
		logger.info("-->进入delCollectAuditClaim删除方法");
		logger.info("---->入参：collectAuditClaim="+collectAuditClaim);
		ResultEntity re = new ResultEntity();
		if (collectAuditClaim.getId()!=null) {//可删除
			logger.info("---->开始删除操作");
			claimDao.delClaim(collectAuditClaim);
			re.setSuccess(true);
			re.setDesc("删除成功");
			logger.info("-->delCollectAuditClaim方法执行结束");
			return re;
		} else {
			re.setSuccess(false);
			re.setDesc("删除失败,参数为空");
			logger.info("-->delCollectAuditClaim方法执行结束");
			return re;
		}
	}
	
	@Override
	public List<CollectAuditClaim> queryCollectAuditClaim(
			CollectAuditClaim collectAuditClaim) {
		logger.info("-->进入queryCollectAuditClaim查询方法");
		logger.info("---->入参：collectAuditClaim="+collectAuditClaim);
		return this.claimDao.queryClaimByCondition(collectAuditClaim);
	}
	
	@Autowired
	private ClaimDao claimDao;
	@Autowired
	private RedisUtilComponent redisUtilComponent;

}
