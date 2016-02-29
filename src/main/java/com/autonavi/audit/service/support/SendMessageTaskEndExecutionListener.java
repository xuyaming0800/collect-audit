package com.autonavi.audit.service.support;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.autonavi.audit.base.spring.ApplicationContextUtils;
import com.autonavi.audit.service.AuditService;

/**
 * @Description: 任务结束推送消息
 * @author 刘旭升
 * @date 2015年7月14日 下午5:42:57
 * @version V1.0
 */
public class SendMessageTaskEndExecutionListener implements ExecutionListener {
	private Logger logger = LogManager.getLogger(getClass());

	private static final long serialVersionUID = 4238452968436368276L;
	// 从Spring上下文中获取实例
	private AuditService auditService = ApplicationContextUtils
			.getApplicationContext().getBean(AuditService.class);

	@Override
	public void notify(DelegateExecution execution) {
		try {
			auditService.sendMessageTaskEndEventMobile(execution.getProcessInstanceId(),
					execution.getVariable("bsType"));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
