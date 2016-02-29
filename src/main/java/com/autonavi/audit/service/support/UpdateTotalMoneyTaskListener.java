package com.autonavi.audit.service.support;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import com.autonavi.audit.base.spring.ApplicationContextUtils;
import com.autonavi.audit.service.AuditService;

/**
 * @Title: UpdateTotalMoneyTaskListener.java
 * @Package com.autonavi.audit.service.support
 * @Description: 统计审核通过项目，计算出金额设置上总金额
 * @author xusheng.liu
 * @date 2015年10月21日 下午1:58:48
 * @version V1.0
 */
public class UpdateTotalMoneyTaskListener implements TaskListener {

	private static final long serialVersionUID = -6772375352140078790L;

	private AuditService auditService = ApplicationContextUtils
			.getApplicationContext().getBean(AuditService.class);

	@Override
	public void notify(DelegateTask delegateTask) {
		auditService.updateTotalMoney(delegateTask.getProcessInstanceId());
	}
}
