package com.autonavi.audit.service.support;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;

import com.autonavi.audit.base.spring.ApplicationContextUtils;
import com.autonavi.audit.service.AuditService;

/**
 * 过期任务创建监听器
 * 
 * @author jia.miao
 *
 */
public class DeadlineCreateTaskListener implements TaskListener {
	private static final long serialVersionUID = 1356121222303564723L;
	// 从Spring上下文中获取实例
	private AuditService auditService = ApplicationContextUtils
			.getApplicationContext().getBean(AuditService.class);

	private Expression deadline;

	@Override
	public void notify(DelegateTask delegateTask) {
		auditService.timeoutEvent(delegateTask.getProcessInstanceId());// 执行数据库状态更新操作
	}
}
