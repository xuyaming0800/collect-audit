package com.autonavi.audit.controller;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/index")
public class IndexController {

	
	
	
	/**
	 * 业务系统管理主页
	 * 
	 * @return
	 */
	@RequestMapping("/main")
	public String indexPage() {
		return "index/main";
	}
	
	
	/**
	 * 验证失败页面
	 * 
	 * @return
	 */
	@RequestMapping("/caserror")
	public String casErrorPage() {
		return "index/caserror";
	}
	
}
