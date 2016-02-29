package com.autonavi.audit.base.webmvc.initbinder;

import java.text.DateFormat;

import org.springframework.beans.propertyeditors.CustomDateEditor;

public class CustomBooleanEditor extends CustomDateEditor {

	public CustomBooleanEditor(DateFormat dateFormat, boolean allowEmpty) {
		super(dateFormat, allowEmpty);
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (text.equalsIgnoreCase("false"))
			super.setValue(false);
		else if (text.equalsIgnoreCase("true"))
			super.setValue(true);
	}
}