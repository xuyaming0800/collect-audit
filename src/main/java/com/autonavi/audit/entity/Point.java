package com.autonavi.audit.entity;


public class Point implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5882382148768500011L;
	private String id;//主键
	private String overlay_id;//任务ID
	private String lng;//纬度
	private String lat;//经度
	private int order_no;//排序号
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getOverlay_id() {
		return overlay_id;
	}
	public void setOverlay_id(String overlay_id) {
		this.overlay_id = overlay_id;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public int getOrder_no() {
		return order_no;
	}
	public void setOrder_no(int order_no) {
		this.order_no = order_no;
	}

}
