package com.autonavi.audit.base.security.filter;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;

public class UrlPermissionsAuthorizationFilter extends
		PermissionsAuthorizationFilter {
	public Logger logger = LogManager.getLogger(getClass());

	@Override
	public boolean isAccessAllowed(ServletRequest request,
			ServletResponse response, Object mappedValue) throws IOException {

		HttpServletRequest req = (HttpServletRequest) request;
		Subject subject = getSubject(request, response);
		String uri = req.getRequestURI();
		String requestURL = req.getRequestURL().toString();
		String contextPath = req.getContextPath();
		int i = uri.indexOf(contextPath);
		if (i > -1) {
			uri = uri.substring(i + contextPath.length());
		}
		if (StringUtils.isBlank(uri)) {
			uri = "/";
		}
		boolean permitted = false;
		if (subject.isPermitted(uri)) {
			permitted = true;
		}
		return permitted;
	}
	
	

}
