package com.autonavi.audit.service.support;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import com.autonavi.audit.base.spring.ApplicationContextUtils;
import com.autonavi.audit.constant.AuditConstant;
import com.autonavi.audit.service.AuditService;
/**
 * @Title: UpdateFlagExecutionListener.java
 * @Package com.autonavi.audit.service.support
 * @Description: 修改变量
 * @author 刘旭升
 * @date 2015年8月31日 下午2:33:02
 * @version V1.0
 */
public class UpdateFlagTaskListener implements TaskListener {

	private static final long serialVersionUID = 7842254660747647949L;
	
	// 从Spring上下文中获取实例
	private AuditService auditService = ApplicationContextUtils
			.getApplicationContext().getBean(AuditService.class);
	@Override
	public void notify(DelegateTask delegateTask) {
		Object obj = delegateTask.getVariable("bsType");
		String sysType = (String) obj;
		if(!AuditConstant.systemId_ad.equals(sysType)){//兼容广告拍拍增加对项目的限制
			auditService.setFlagEvent(delegateTask.getProcessInstanceId(),
					delegateTask.getExecutionId());
		}
	}

}


