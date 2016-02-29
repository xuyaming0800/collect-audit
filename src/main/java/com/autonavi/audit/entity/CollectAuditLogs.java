package com.autonavi.audit.entity;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class CollectAuditLogs implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = 8054268592066631281L;
	private Long id;
	@NotNull
	private String audit_id;
	@NotNull
	private String task_id;
	private String executionId;
	@NotNull
	private Integer status;// 状态
	private Date audit_time;// 审核时间
	@NotNull
	private String audit_user;// 审核用户
	private String audit_task_name;// 审核任务名

	/**
	 * 审核成功明细描述B
	 */
	private String appearance;// 外观
	private String damaged;// 破损
	private String lighting;// 亮灯
	private String occlusion;// 遮挡
	/**
	 * 审核成功明细描述E
	 */

	/**
	 * 审核失败明细描述B
	 */
	private String no_approval_reason;// 不通过原因
	private String comment_message;// 给采集者的留言

	/**
	 * 审核失败明细描述E
	 */
	// Constructors

	/**
	 * 审核信息中的金额,小区子任务名称;用于审核记录,不参与数据库字段存储
	 */
	private Double totalMoney;//任务金额
	private String childName;//子任务名称
	
	
	/** default constructor */
	public CollectAuditLogs() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAudit_id() {
		return audit_id;
	}

	public void setAudit_id(String audit_id) {
		this.audit_id = audit_id;
	}

	public String getTask_id() {
		return task_id;
	}

	public String getChildName() {
		return childName;
	}

	public void setChildName(String childName) {
		this.childName = childName;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getAudit_time() {
		return audit_time;
	}

	public void setAudit_time(Date audit_time) {
		this.audit_time = audit_time;
	}

	public String getAudit_user() {
		return audit_user;
	}

	public Double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(Double totalMoney) {
		this.totalMoney = totalMoney;
	}

	public void setAudit_user(String audit_user) {
		this.audit_user = audit_user;
	}

	public String getAudit_task_name() {
		return audit_task_name;
	}

	public void setAudit_task_name(String audit_task_name) {
		this.audit_task_name = audit_task_name;
	}

	public String getAppearance() {
		return appearance;
	}

	public void setAppearance(String appearance) {
		this.appearance = appearance;
	}

	public String getDamaged() {
		return damaged;
	}

	public void setDamaged(String damaged) {
		this.damaged = damaged;
	}

	public String getLighting() {
		return lighting;
	}

	public void setLighting(String lighting) {
		this.lighting = lighting;
	}

	public String getOcclusion() {
		return occlusion;
	}

	public void setOcclusion(String occlusion) {
		this.occlusion = occlusion;
	}

	public String getNo_approval_reason() {
		return no_approval_reason;
	}

	public void setNo_approval_reason(String no_approval_reason) {
		this.no_approval_reason = no_approval_reason;
	}

	public String getComment_message() {
		return comment_message;
	}

	public void setComment_message(String comment_message) {
		this.comment_message = comment_message;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	@Override
	public String toString() {
		return "CollectAuditLogs [id=" + id + ", audit_id=" + audit_id
				+ ", task_id=" + task_id + ", executionId=" + executionId
				+ ", status=" + status + ", audit_time=" + audit_time
				+ ", audit_user=" + audit_user + ", audit_task_name="
				+ audit_task_name + ", appearance=" + appearance + ", damaged="
				+ damaged + ", lighting=" + lighting + ", occlusion="
				+ occlusion + ", no_approval_reason=" + no_approval_reason
				+ ", comment_message=" + comment_message + "]";
	}

}