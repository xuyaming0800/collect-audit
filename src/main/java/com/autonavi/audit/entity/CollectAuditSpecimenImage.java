package com.autonavi.audit.entity;

import javax.validation.constraints.NotNull;

import com.autonavi.audit.entity.groups.OrdinaryTask;

public class CollectAuditSpecimenImage implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8051761628822783057L;

	private Long id;// 主键
	private String audit_id;// 主表外键
	@NotNull(groups=OrdinaryTask.class)
	private String thumbnai_url;// 缩略图地址
	@NotNull(groups=OrdinaryTask.class)
	private String image_url;// 图片地址

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

	public String getThumbnai_url() {
		return thumbnai_url;
	}

	public void setThumbnai_url(String thumbnai_url) {
		this.thumbnai_url = thumbnai_url;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	@Override
	public String toString() {
		return "CollectAuditSpecimenImage [id=" + id + ", audit_id=" + audit_id
				+ ", thumbnai_url=" + thumbnai_url + ", image_url=" + image_url
				+ "]";
	}
}
