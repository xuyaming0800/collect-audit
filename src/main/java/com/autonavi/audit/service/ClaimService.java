package com.autonavi.audit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.autonavi.audit.entity.CollectAuditClaim;
import com.autonavi.audit.entity.Pagination;
import com.autonavi.audit.entity.ResultEntity;

/**
 * @Title: ClaimService.java
 * @Package com.autonavi.audit.service
 * @Description: 任务认领管理
 * @author xusheng.liu
 * @date 2015年11月16日 下午3:12:06
 * @version V1.0
 */
@Service
public interface ClaimService {
	/**
	 * @Description: 根据customName来查询 和 userType 来查询客户信息，在boss表中客户类型为3
	 * @author xusheng.liu
	 * @date 2015年11月17日 上午9:39:08
	 * @version V1.0
	 * @param customName 客户名称
	 * @param userType	user类型
	 * @param url 链接
	 * @return
	 * @throws Exception
	 */
	public List<Object> getCustomIdList(String customName, String userType,
			String url) throws Exception;

	/**
	 * @Description: 根据客户id来查询项目列表
	 * @author xusheng.liu
	 * @date 2015年11月17日 上午9:40:45 
	 * @version V1.0 
	 * @param customId 客户id
	 * @return
	 * @throws Exception
	 */
	public ResultEntity getProjects(String customId) throws Exception;

	/**
	 * @Description: 保存
	 * @author xusheng.liu
	 * @date 2015年11月17日 上午11:47:49 
	 * @version V1.0 
	 * @throws Exception
	 */
	public ResultEntity addCollectAuditClaim(CollectAuditClaim collectAuditClaim) throws Exception;

	/**
	 * @Description: 查询列表
	 * @author xusheng.liu
	 * @date 2015年11月17日 下午3:27:14 
	 * @version V1.0 
	 * @param pageSize 
	 * @param pageNo 
	 * @param collectAuditClaim
	 * @return
	 */
	public Pagination queryCollectAuditClaim(
			Integer pageNo, Integer pageSize, CollectAuditClaim collectAuditClaim) throws Exception;

	/**
	 * @Description: 删除
	 * @author xusheng.liu
	 * @date 2015年11月18日 下午2:36:24 
	 * @version V1.0 
	 * @param collectAuditClaim
	 * @return
	 */
	public ResultEntity delCollectAuditClaim(CollectAuditClaim collectAuditClaim);

	/**
	 * @Description: 查询
	 * @author xusheng.liu
	 * @date 2015年11月18日 下午4:17:34 
	 * @version V1.0 
	 * @param collectAuditClaim
	 * @return
	 */
	public List<CollectAuditClaim> queryCollectAuditClaim(CollectAuditClaim collectAuditClaim);
}
