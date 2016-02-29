package com.autonavi.audit.service.support;

import java.io.IOException;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.autonavi.audit.base.spring.ApplicationContextUtils;
import com.autonavi.audit.service.AuditService;

public class MqSendMessageExecutionListener implements ExecutionListener {
	private Logger logger = LogManager.getLogger(getClass());

	private static final long serialVersionUID = 4238452968436368276L;
	// 从Spring上下文中获取实例
	private AuditService auditService = ApplicationContextUtils
			.getApplicationContext().getBean(AuditService.class);

	@Override
	public void notify(DelegateExecution execution) {
		try {
			auditService.sendMessageEvent(execution.getProcessInstanceId(),
					execution.getVariable("bsType"));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
