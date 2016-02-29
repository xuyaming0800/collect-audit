package com.autonavi.audit.entity;

public class CollectAuditAndCoordinate implements java.io.Serializable {

	// Fields
	private static final long serialVersionUID = -8850764971911471889L;
	private String auditId;// 任务Id
	private String taskClassName;// 任务类别
	private Integer status;// 状态
	private Double coordinateX;// 经度
	private Double coordinateY;// 纬度

	public String getAuditId() {
		return auditId;
	}

	public String getTaskClassName() {
		return taskClassName;
	}

	public void setTaskClassName(String taskClassName) {
		this.taskClassName = taskClassName;
	}

	public void setAuditId(String auditId) {
		this.auditId = auditId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Double getCoordinateX() {
		return coordinateX;
	}

	public void setCoordinateX(Double coordinateX) {
		this.coordinateX = coordinateX;
	}

	public Double getCoordinateY() {
		return coordinateY;
	}

	public void setCoordinateY(Double coordinateY) {
		this.coordinateY = coordinateY;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}