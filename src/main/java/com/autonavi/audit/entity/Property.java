package com.autonavi.audit.entity;

public class Property implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6990873971618199203L;
	private String id;//主键
	private String overlay_id;//覆盖物ID
	private String prop_name;//属性名称
	private String prop_value;//属性值
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
	public String getProp_name() {
		return prop_name;
	}
	public void setProp_name(String prop_name) {
		this.prop_name = prop_name;
	}
	public String getProp_value() {
		return prop_value;
	}
	public void setProp_value(String prop_value) {
		this.prop_value = prop_value;
	}
	public int getOrder_no() {
		return order_no;
	}
	public void setOrder_no(int order_no) {
		this.order_no = order_no;
	}
	
}
