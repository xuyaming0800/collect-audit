package com.autonavi.audit.entity;

/**
 * @Title: CollectAuditPayRecord.java
 * @Package com.autonavi.audit.entity
 * @Description: 支付记录
 * @author xusheng.liu
 * @date 2015年10月12日 下午4:24:45
 * @version V1.0
 */
public class CollectAuditPayRecord implements java.io.Serializable {

	private static final long serialVersionUID = -4014525826069645027L;
	private String id;// 主键
	private String taskId;// 任务id
	private Integer status;// 状态
	private String content;// 支付内容记录

	private String createDate;// 创建时间
	private String updateDate;// 创建时间
	
	//---------
	private String taskName;// 创建时间

	public String getId() {
		return id;
	}

	public String getTaskId() {
		return taskId;
	}

	public Integer getStatus() {
		return status;
	}

	public String getContent() {
		return content;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	@Override
	public String toString() {
		return "CollectAuditPayRecord [id=" + id + ", taskId=" + taskId
				+ ", status=" + status + ", content=" + content
				+ ", createDate=" + createDate + ", updateDate=" + updateDate
				+ ", taskName=" + taskName + "]";
	}
}
