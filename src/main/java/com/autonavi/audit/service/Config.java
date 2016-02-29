package com.autonavi.audit.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class Config {
	public static String get_dictionary_type_url;
	public static String get_dictionary_data_url;
	public static String get_dictionary_url;
	public static String audit_comment_pay_url;
	public static String get_custom_url;
	public static String get_users_url;
	
	/**
	 * 项目列表
	 */
	public static String get_projects;

	@Value("${get_dictionary_type_url}")
	public void setGet_dictionary_type_url(String get_dictionary_type_url) {
		Config.get_dictionary_type_url = get_dictionary_type_url;
	}

	@Value("${get_dictionary_data_url}")
	public void setGet_dictionary_data_url(String get_dictionary_data_url) {
		Config.get_dictionary_data_url = get_dictionary_data_url;
	}

	@Value("${get_dictionary_url}")
	public void setGet_dictionary_url(String get_dictionary_url) {
		Config.get_dictionary_url = get_dictionary_url;
	}

	@Value("${get_projects}")
	public void setGet_projects(String get_projects) {
		Config.get_projects = get_projects;
	}

	@Value("${audit_comment_pay_url}")
	public void setAudit_comment_pay_url(String audit_comment_pay_url) {
		Config.audit_comment_pay_url = audit_comment_pay_url;
	}

	@Value("${get_custom_url}")
	public void setGet_custom_url(String get_custom_url) {
		Config.get_custom_url = get_custom_url;
	}
	
	@Value("${get_users_url}")
	public void setGet_users_url(String get_users_url) {
		Config.get_users_url = get_users_url;
	}

}
