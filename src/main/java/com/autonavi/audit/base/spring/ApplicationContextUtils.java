package com.autonavi.audit.base.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 获取Spring上下文的专用类
 * 
 * @author jia.miao
 *
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {
	private int i = 0;
	private static ApplicationContext context;

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		if (i == 0) {
			ApplicationContextUtils.context = context;
			i++;
		}
	}

}
