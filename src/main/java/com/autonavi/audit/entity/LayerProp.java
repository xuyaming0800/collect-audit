package com.autonavi.audit.entity;

public class LayerProp  implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 396585215198165805L;
	private String id;//主键
	private String layer_id;//图层ID
	private String prop_name;//属性名称
	private int order_no;//排序号
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLayer_id() {
		return layer_id;
	}
	public void setLayer_id(String layer_id) {
		this.layer_id = layer_id;
	}
	public String getProp_name() {
		return prop_name;
	}
	public void setProp_name(String prop_name) {
		this.prop_name = prop_name;
	}
	public int getOrder_no() {
		return order_no;
	}
	public void setOrder_no(int order_no) {
		this.order_no = order_no;
	}


}
