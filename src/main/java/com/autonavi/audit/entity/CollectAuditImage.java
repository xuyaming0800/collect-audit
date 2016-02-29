package com.autonavi.audit.entity;

import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.NotEmpty;

import com.autonavi.audit.entity.groups.MissingTask;
import com.autonavi.audit.entity.groups.OrdinaryTask;

public class CollectAuditImage implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = 786171581865508065L;
	private Long id;// 主键
	private String audit_id;// 主表外键

	/**
	 * 采集到B
	 */
//	@NotNull(groups = OrdinaryTask.class)
//	@NotEmpty(groups = OrdinaryTask.class)
	private String thumbnai_url;// 缩略图地址
	
//	@NotNull(groups = OrdinaryTask.class)
//	@NotEmpty(groups = OrdinaryTask.class)
	private String image_url;// 图片地址
	
//	@NotNull(groups = OrdinaryTask.class)
	//@Past(groups = OrdinaryTask.class)
	private Date photograph_time;// 拍照时间
	/**
	 * 采集到E
	 */

	/**
	 * 未找到B
	 */
	@NotNull(groups = MissingTask.class)
	@NotEmpty(groups = MissingTask.class)
	private String video_url;// 录像地址
	
	@NotNull(groups = MissingTask.class)
	//@Past(groups = MissingTask.class)
	private Date video_time;// 录像时间
	
	@NotNull(groups = MissingTask.class)
	@NotEmpty(groups = MissingTask.class)
	private String no_exist_reason;// 缺失原因
	/**
	 * 未找到E
	 */

	//@NotNull
	//@Past
	private Date gps_time;// GPS时间
	
	@NotNull
	@Min(1)
	private Double lon;// 精度
	
	@NotNull
	@Min(1)
	private Double lat;// 维度
	
	//@NotNull
	//@Min(1)
	private Integer point_level;// 卫星颗数
	
	//@NotNull
	//@Min(1)
	private Double point_accury;// 定位精度(m)
	
	//@NotNull
	//@Min(0)
	private Double position;// 方位(度)
	
	private Integer index;// 下标  远近景（偶近）

	private Integer used;//是否使用（0不可使用，1可以使用）
	
	// Constructors

	/** default constructor */
	public CollectAuditImage() {
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

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public Date getPhotograph_time() {
		return photograph_time;
	}

	public void setPhotograph_time(Date photograph_time) {
		this.photograph_time = photograph_time;
	}

	public Date getGps_time() {
		return gps_time;
	}

	public void setGps_time(Date gps_time) {
		this.gps_time = gps_time;
	}

	public Integer getPoint_level() {
		return point_level;
	}

	public void setPoint_level(Integer point_level) {
		this.point_level = point_level;
	}

	public Double getPoint_accury() {
		return point_accury;
	}

	public void setPoint_accury(Double point_accury) {
		this.point_accury = point_accury;
	}

	public Double getLon() {
		return this.lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public void setX(Double lon) {
		this.lon = lon;
	}

	public Double getLat() {
		return this.lat;
	}

	public void setY(Double lat) {
		this.lat = lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getPosition() {
		return this.position;
	}

	public void setPosition(Double position) {
		this.position = position;
	}

	public String getThumbnai_url() {
		return thumbnai_url;
	}

	public void setThumbnai_url(String thumbnai_url) {
		this.thumbnai_url = thumbnai_url;
	}

	public String getVideo_url() {
		return video_url;
	}

	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}

	public Date getVideo_time() {
		return video_time;
	}

	public void setVideo_time(Date video_time) {
		this.video_time = video_time;
	}

	public String getNo_exist_reason() {
		return no_exist_reason;
	}

	public void setNo_exist_reason(String no_exist_reason) {
		this.no_exist_reason = no_exist_reason;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer getUsed() {
		return used;
	}

	public void setUsed(Integer used) {
		this.used = used;
	}

	@Override
	public String toString() {
		return "CollectAuditImage [id=" + id + ", audit_id=" + audit_id
				+ ", thumbnai_url=" + thumbnai_url + ", image_url=" + image_url
				+ ", photograph_time=" + photograph_time + ", video_url="
				+ video_url + ", video_time=" + video_time
				+ ", no_exist_reason=" + no_exist_reason + ", gps_time="
				+ gps_time + ", lon=" + lon + ", lat=" + lat + ", point_level="
				+ point_level + ", point_accury=" + point_accury
				+ ", position=" + position + ", index=" + index + "]";
	}

}
