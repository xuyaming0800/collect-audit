package com.autonavi.audit.entity;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @Title: CollectAuditClaim.java
 * @Package com.autonavi.audit.entity
 * @Description: 审核任务认领表
 * @author xusheng.liu
 * @date 2015年11月16日 下午3:38:47
 * @version V1.0
 */
public class CollectAuditClaim implements java.io.Serializable {

	private static final long serialVersionUID = 8418925096580249220L;
	// Fields

	@NotNull
	@NotEmpty
	private Integer id;// 主键
	@NotNull
	@NotEmpty
	private String customId;// 客户id
	@NotNull
	@NotEmpty
	private String systemId; // 项目id
	@NotNull
	@NotEmpty
	private String claimUserId; // 认领人id

	private Date createTime; // 创建时间
	private Date updateTime; // 修改时间

	private String createBy; // 创建人
	private String updateBy; // 修改人

	private String claimType; // 认领类型(1初审,2抽检,4申诉)

	private String customName; 		// 客户名称
	private String systemName; 		// 项目名称
	private String updateByName; 	// 修改人名称
	private String createByName; 	// 创建人名称
	private String claimUserName; 	// 认领人名称
	// -----------------------

	public CollectAuditClaim(String customId, String systemId,
			String claimUserId, String claimType) {
		super();
		this.customId = customId;
		this.systemId = systemId;
		this.claimUserId = claimUserId;
		this.claimType = claimType;
	}
	
	public CollectAuditClaim() {
		super();
	}

	public String getCustomId() {
		return customId;
	}

	public String getSystemId() {
		return systemId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public String getClaimUserId() {
		return claimUserId;
	}


	public String getClaimUserName() {
		return claimUserName;
	}


	public void setClaimUserId(String claimUserId) {
		this.claimUserId = claimUserId;
	}


	public void setClaimUserName(String claimUserName) {
		this.claimUserName = claimUserName;
	}


	public String getCreateBy() {
		return createBy;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public String getClaimType() {
		return claimType;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public void setClaimType(String claimType) {
		this.claimType = claimType;
	}

	public String getUpdateByName() {
		return updateByName;
	}

	public String getCreateByName() {
		return createByName;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setUpdateByName(String updateByName) {
		this.updateByName = updateByName;
	}

	public void setCreateByName(String createByName) {
		this.createByName = createByName;
	}

	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	@Override
	public String toString() {
		return "CollectAuditClaim [id=" + id + ", customId=" + customId
				+ ", systemId=" + systemId + ", claimUserId=" + claimUserId
				+ ", createTime=" + createTime + ", updateTime=" + updateTime
				+ ", createBy=" + createBy + ", updateBy=" + updateBy
				+ ", claimType=" + claimType + ", customName=" + customName
				+ ", systemName=" + systemName + ", updateByName="
				+ updateByName + ", createByName=" + createByName
				+ ", claimUserName=" + claimUserName + "]";
	}
}
