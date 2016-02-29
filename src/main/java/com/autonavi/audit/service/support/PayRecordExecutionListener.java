package com.autonavi.audit.service.support;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import com.autonavi.audit.base.spring.ApplicationContextUtils;
import com.autonavi.audit.service.AuditService;

/**
 * @Title: PayExecutionListener.java
 * @Package com.autonavi.audit.service.support
 * @Description: 支付记录
 * @author xusheng.liu
 * @date 2015年10月12日 下午3:38:41
 * @version V1.0
 */
public class PayRecordExecutionListener implements ExecutionListener {

	private static final long serialVersionUID = -5980910090545336226L;

	// 从Spring上下文中获取实例
	private AuditService auditService = ApplicationContextUtils
			.getApplicationContext().getBean(AuditService.class);

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		auditService.recordPayDetailEvent(execution.getProcessInstanceId(),
				execution.getId());
	}

}
