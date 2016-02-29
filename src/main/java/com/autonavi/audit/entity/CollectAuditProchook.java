package com.autonavi.audit.entity;

import java.util.Date;

public class CollectAuditProchook implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = -3133114551284581534L;
	private Long id; // 主键
	private String proc_def_id;// 流程定义ID（工作流）
	private String expression;// 流程挂接表达式
	private Date create_time;// 创建时间
	private String create_user;// 创建用户

	// Constructors

	/** default constructor */
	public CollectAuditProchook() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProc_def_id() {
		return proc_def_id;
	}

	public void setProc_def_id(String proc_def_id) {
		this.proc_def_id = proc_def_id;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public String getCreate_user() {
		return create_user;
	}

	public void setCreate_user(String create_user) {
		this.create_user = create_user;
	}

}