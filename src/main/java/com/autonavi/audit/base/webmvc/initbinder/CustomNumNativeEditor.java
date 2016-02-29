package com.autonavi.audit.base.webmvc.initbinder;


import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.util.StringUtils;
@SuppressWarnings("unchecked")
public class CustomNumNativeEditor extends CustomNumberEditor {
	

	public CustomNumNativeEditor(@SuppressWarnings("rawtypes") Class numberClass, boolean allowEmpty)
			throws IllegalArgumentException {
		super(numberClass, allowEmpty);
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (!StringUtils.hasText(text))
			return;
		else
			super.setAsText(text);
	}
	
}
