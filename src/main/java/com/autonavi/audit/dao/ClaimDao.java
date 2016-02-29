package com.autonavi.audit.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.autonavi.audit.base.mybatis.annotation.MyBatisRepository;
import com.autonavi.audit.entity.CollectAuditClaim;

@MyBatisRepository
public interface ClaimDao {

	/**
	 * @Description: 保存
	 * @author xusheng.liu
	 * @date 2015年10月12日 下午4:50:38
	 * @version V1.0
	 * @param auditPayRecord
	 */
	void insertClaim(CollectAuditClaim collectAuditClaim);

	/**
	 * @Description: 修改
	 * @author xusheng.liu
	 * @date 2015年10月12日 下午4:50:56
	 * @version V1.0
	 * @param auditPayRecord
	 */
	void updateClaim(CollectAuditClaim collectAuditClaim);
	
	/**
	 * @Description: 主键查询
	 * @author xusheng.liu
	 * @date 2015年10月30日 下午5:34:42 
	 * @version V1.0 
	 * @param id
	 * @return
	 */
	CollectAuditClaim queryClaimByPrimaryKey(String id);
	
	/**
	 * @Description: 根据条件查询
	 * @author xusheng.liu
	 * @date 2015年11月17日 下午2:58:01 
	 * @version V1.0 
	 * @param id
	 * @return
	 */
	List<CollectAuditClaim> queryClaimByCondition(CollectAuditClaim collectAuditClaim);

	/**
	 * @Description: 分页查询
	 * @author xusheng.liu
	 * @date 2015年10月29日 下午6:11:06
	 * @version V1.0
	 * @param capr
	 * @param pageSize
	 * @param page
	 * @return
	 */
	List<CollectAuditClaim> query(@Param(value = "pageNo") Integer pageNo,
			@Param(value = "pageSize") Integer pageSize,
			@Param(value = "cac") CollectAuditClaim collectAuditClaim);

	/**
	 * @Description: 查询总数（同步上面的方法）
	 * @author xusheng.liu
	 * @date 2015年10月30日 下午3:16:38 
	 * @version V1.0 
	 * @param capr
	 * @return
	 */
	long queryCount(@Param(value = "cac") CollectAuditClaim collectAuditClaim);

	/**
	 * @Description: 删除
	 * @author xusheng.liu
	 * @date 2015年11月18日 下午2:41:20 
	 * @version V1.0 
	 * @param collectAuditClaim
	 */
	void delClaim(CollectAuditClaim collectAuditClaim);

}
