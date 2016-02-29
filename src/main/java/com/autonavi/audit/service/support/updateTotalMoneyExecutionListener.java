package com.autonavi.audit.service.support;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;

import com.autonavi.audit.base.spring.ApplicationContextUtils;
import com.autonavi.audit.service.AuditService;

/**
 * @Title: updateTotalMoneyExecutionListener.java
 * @Package com.autonavi.audit.service.support
 * @Description: 任务审核结束，统计各种金额（按照子任务的通过累加 ）
 * @author xusheng.liu
 * @date 2015年10月16日 上午11:51:42
 * @version V1.0
 */
public class updateTotalMoneyExecutionListener implements ExecutionListener {

	private static final long serialVersionUID = 1576542948570852119L;

	// 从Spring上下文中获取实例
	private AuditService auditService = ApplicationContextUtils
			.getApplicationContext().getBean(AuditService.class);

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		auditService.updateTotalMoney(execution.getProcessInstanceId());
	}

}
