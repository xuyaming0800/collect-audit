package com.autonavi.audit.dao;

import java.util.List;

import com.autonavi.audit.base.mybatis.annotation.MyBatisRepository;
import com.autonavi.audit.entity.CollectAuditSystem;

@MyBatisRepository
public interface AuditSysConfigDao {

	/**
	 * 插入系统配置信息
	 * @param collectAuditSystem
	 */
	public void insertCollectAuditSystem(CollectAuditSystem collectAuditSystem);
	
	
	/**
	 * 修改配置信息
	 * @param collectAuditSystem
	 */
	public void updateCollectAuditSystem(CollectAuditSystem collectAuditSystem);
	
	
	/**
	 * 根据ID删除配置信息
	 * @param id
	 */
	public void deleteCollectAuditSystem(String id);
	
	
	/**
	 * 查询系统配置信息
	 * @return
	 */
	public List<CollectAuditSystem> selectCollectAuditSystem();
	
	
	/**
	 * 根据ID查询系统配置信息
	 * @param id
	 * @return
	 */
	public CollectAuditSystem queryCollectAuditSystemById(String id);
}
