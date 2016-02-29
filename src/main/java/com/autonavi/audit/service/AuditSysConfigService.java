package com.autonavi.audit.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autonavi.audit.dao.AuditSysConfigDao;
import com.autonavi.audit.entity.CollectAuditSystem;
import com.autonavi.audit.util.PrimaryByRedis;


/**
 * 审核系统配置管理
 * @author chunsheng.zhang
 *
 */
@Service
public class AuditSysConfigService {

	@Autowired
	private AuditSysConfigDao auditSysConfigDao;
	
	@Autowired
	private PrimaryByRedis primaryByRedis;
	
	private Logger logger = LogManager.getLogger(getClass());
	
	
	/**
	 * 保存或更新配置信息
	 */
	public void saveOrUpdateCollectAuditSystem(CollectAuditSystem collectAuditSystem) {
		logger.entry(collectAuditSystem);
		if(StringUtils.isBlank(collectAuditSystem.getId())) {
			/*Long id = primaryByRedis.generateEcode();
			logger.debug("新增配置信息,id: " + id);*/
			collectAuditSystem.setId(collectAuditSystem.getSystem_id());
			auditSysConfigDao.insertCollectAuditSystem(collectAuditSystem);
		}else {
			logger.debug("修改配置信息");
			auditSysConfigDao.updateCollectAuditSystem(collectAuditSystem);
		}
	}
	
	
	/**
	 * 根据ID删除配置信息
	 * @param id
	 */
	public void deleteCollectAuditSystem(String id) {
		logger.debug("删除配置信息 id: " + id);
		auditSysConfigDao.deleteCollectAuditSystem(id);
	}
	
	
	/**
	 * 查询系统配置信息
	 * @return
	 */
	public List<CollectAuditSystem> selectCollectAuditSystem() {
		return auditSysConfigDao.selectCollectAuditSystem();
	}
	
	
	/**
	 * 根据ID查询系统配置信息
	 * @param id
	 * @return
	 */
	public CollectAuditSystem queryCollectAuditSystemById(String id) {
		return auditSysConfigDao.queryCollectAuditSystemById(id);
	}
}
