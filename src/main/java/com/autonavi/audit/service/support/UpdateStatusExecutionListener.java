package com.autonavi.audit.service.support;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;

import com.autonavi.audit.base.spring.ApplicationContextUtils;
import com.autonavi.audit.service.AuditService;

/**
 * 修改状态
 * 
 * @author jia.miao
 *
 */
public class UpdateStatusExecutionListener implements ExecutionListener {

	private static final long serialVersionUID = -3276229041272255400L;

	// 从Spring上下文中获取实例
	private AuditService auditService = ApplicationContextUtils
			.getApplicationContext().getBean(AuditService.class);

	private Expression status;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		if (status != null && status.getExpressionText() != null)
			auditService.updateStatusEvent(execution.getProcessInstanceId(),
					execution.getId(),
					Long.valueOf(status.getValue(execution).toString())
							.intValue());
	}

}
