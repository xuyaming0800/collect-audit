package com.autonavi.audit.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.autonavi.audit.entity.CollectAuditSystem;
import com.autonavi.audit.entity.ResultEntity;
import com.autonavi.audit.service.AuditSysConfigService;
import com.autonavi.audit.util.SysErrorUtil;

@Controller
@RequestMapping("/sys")
public class SysController {
	
	@Autowired
	private AuditSysConfigService auditSysConfigService;
	private Logger logger = LogManager.getLogger(getClass());
	
	/**
	 * 业务系统管理主页
	 * 
	 * @return
	 */
	@RequestMapping("/list")
	public String indexPage() {
		return "sys/list";
	}
	
	
	/**
	 * 保存或修改系统信息
	 * @param collectAuditSystem
	 * @return
	 */
	@RequestMapping("/updateauditsystem")
	public @ResponseBody ResultEntity insertCollectAuditSystem(CollectAuditSystem collectAuditSystem) {
		logger.entry(collectAuditSystem);
		ResultEntity resultEntity = new ResultEntity();
		try {
			auditSysConfigService.saveOrUpdateCollectAuditSystem(collectAuditSystem);
			resultEntity.setSuccess(true);
			return resultEntity;
		}catch(Exception e) {
			logger.error("insertCollectAuditSystem error: " + e);
			resultEntity.setSuccess(false);
			resultEntity.setDesc(SysErrorUtil.SYS_ERROR_INFO);
			return resultEntity;
		}
		
	}
	
	
	/**
	 * 查询系统信息
	 * @return
	 */
	@RequestMapping("/queryauditsystem")
	public @ResponseBody ResultEntity queryCollectAuditSystem() {
		logger.debug("查询系统管理信息");
		ResultEntity resultEntity = new ResultEntity();
		try {
			List<CollectAuditSystem> collectAuditSystems = auditSysConfigService.selectCollectAuditSystem();
			if(null == collectAuditSystems) {
				collectAuditSystems = new ArrayList<CollectAuditSystem>();
			}
			resultEntity.setSuccess(true);
			resultEntity.setInfo(collectAuditSystems);
			logger.exit(resultEntity);
			
			return resultEntity;
		}catch(Exception e) {
			logger.error(e);
			resultEntity.setSuccess(false);
			resultEntity.setDesc(SysErrorUtil.SYS_ERROR_INFO);
			return resultEntity;
		}
		

	}
	
	
	/**
	 * 根据ID删除系统信息
	 * @param id
	 * @return
	 */
	@RequestMapping("/deleteauditsystem")
	public @ResponseBody ResultEntity deleteCollectAuditSystem(String id) {
		logger.entry(id);
		ResultEntity resultEntity = new ResultEntity();
		try {
			auditSysConfigService.deleteCollectAuditSystem(id);
			resultEntity.setSuccess(true);
			logger.exit(resultEntity);
			
			return resultEntity;
		}catch(Exception e) {
			logger.error(e);
			resultEntity.setSuccess(false);
			resultEntity.setDesc(SysErrorUtil.SYS_ERROR_INFO);
			return resultEntity;
		}
	}

	
	/**
	 * 根据ID查询系统信息
	 * @param id
	 * @return
	 */
	@RequestMapping("/queryauditsystembyid")
	public @ResponseBody ResultEntity queryCollectAuditSystemById(String id) {
		logger.debug("查询系统管理信息");
		ResultEntity resultEntity = new ResultEntity();
		try {
			CollectAuditSystem collectAuditSystem = auditSysConfigService.queryCollectAuditSystemById(id);
			resultEntity.setSuccess(true);
			resultEntity.setInfo(collectAuditSystem);
			logger.exit(resultEntity);
			
			return resultEntity;
		}catch(Exception e) {
			logger.error(e);
			resultEntity.setSuccess(false);
			resultEntity.setDesc(SysErrorUtil.SYS_ERROR_INFO);
			return resultEntity;
		}
		

	}
}
