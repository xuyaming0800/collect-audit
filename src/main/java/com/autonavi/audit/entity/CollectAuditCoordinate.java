package com.autonavi.audit.entity;

import javax.validation.constraints.NotNull;

public class CollectAuditCoordinate implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -2079515179483729883L;
	private Long id;
	private String audit_id;
	@NotNull
	private Double coordinate;// 坐标

	// Constructors

	/** default constructor */
	public CollectAuditCoordinate() {
	}

	public Long getId() {
		return this.id;
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

	public Double getCoordinate() {
		return this.coordinate;
	}

	public void setCoordinate(Double coordinate) {
		this.coordinate = coordinate;
	}

}