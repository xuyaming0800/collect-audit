package com.autonavi.audit.service.support;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;

import com.autonavi.audit.base.spring.ApplicationContextUtils;
import com.autonavi.audit.constant.AuditConstant;
import com.autonavi.audit.service.AuditService;

/**
 * @Title: UpdateFlagExecutionListener.java
 * @Package com.autonavi.audit.service.support
 * @Description: 修改变量
 * @author xusheng.liu
 * @date 2015年10月16日 下午2:46:45
 * @version V1.0
 */
public class UpdateFlagExecutionListener implements ExecutionListener {

	private static final long serialVersionUID = -7458344422909092359L;
	// 从Spring上下文中获取实例
	private AuditService auditService = ApplicationContextUtils
			.getApplicationContext().getBean(AuditService.class);

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		Object obj = execution.getVariable("bsType");
		String sysType = (String) obj;
		if(!AuditConstant.systemId_ad.equals(sysType)){//兼容广告拍拍增加对项目的限制
			auditService.setFlagEvent(execution.getProcessInstanceId(),
					execution.getId());
		}
	}

}
