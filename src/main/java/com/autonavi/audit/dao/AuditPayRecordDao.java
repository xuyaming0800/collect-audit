package com.autonavi.audit.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.autonavi.audit.base.mybatis.annotation.MyBatisRepository;
import com.autonavi.audit.entity.CollectAuditPayRecord;

@MyBatisRepository
public interface AuditPayRecordDao {

	/**
	 * @Description: 保存
	 * @author xusheng.liu
	 * @date 2015年10月12日 下午4:50:38
	 * @version V1.0
	 * @param auditPayRecord
	 */
	void insertCollectAuditPayRecord(CollectAuditPayRecord auditPayRecord);

	/**
	 * @Description: 修改
	 * @author xusheng.liu
	 * @date 2015年10月12日 下午4:50:56
	 * @version V1.0
	 * @param auditPayRecord
	 */
	void updateCollectAuditPayRecord(CollectAuditPayRecord auditPayRecord);

	/**
	 * @Description: 查询支付记录
	 * @author xusheng.liu
	 * @date 2015年10月29日 下午6:11:06
	 * @version V1.0
	 * @param capr
	 * @param pageSize
	 * @param page
	 * @return
	 */
	List<CollectAuditPayRecord> query(@Param(value = "pageNo") Integer pageNo,
			@Param(value = "pageSize") Integer pageSize,
			@Param(value = "capr") CollectAuditPayRecord capr);

	/**
	 * @Description: 查询支付记录总数（同步上面的方法）
	 * @author xusheng.liu
	 * @date 2015年10月30日 下午3:16:38 
	 * @version V1.0 
	 * @param capr
	 * @return
	 */
	long queryCount(@Param(value = "capr") CollectAuditPayRecord capr);

	/**
	 * @Description: 主键查询记录
	 * @author xusheng.liu
	 * @date 2015年10月30日 下午5:34:42 
	 * @version V1.0 
	 * @param id
	 * @return
	 */
	CollectAuditPayRecord queryAuditPayRecordByPrimaryKey(String id);

}
