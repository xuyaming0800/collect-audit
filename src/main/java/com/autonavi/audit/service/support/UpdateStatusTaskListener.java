package com.autonavi.audit.service.support;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;

import com.autonavi.audit.base.spring.ApplicationContextUtils;
import com.autonavi.audit.service.AuditService;

/**
 * 修改状态
 * 
 * @author jia.miao
 *
 */
public class UpdateStatusTaskListener implements TaskListener {

	private static final long serialVersionUID = -441573252759495874L;

	// 从Spring上下文中获取实例
	private AuditService auditService = ApplicationContextUtils
			.getApplicationContext().getBean(AuditService.class);

	private Expression status;

	@Override
	public void notify(DelegateTask delegateTask) {

		if (status != null && status.getExpressionText() != null)
			auditService.updateStatusEvent(
					delegateTask.getProcessInstanceId(),
					delegateTask.getExecutionId(),
					Long.valueOf(
							status.getValue(delegateTask.getExecution())
									.toString()).intValue());

	}

}
