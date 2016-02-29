package com.autonavi.audit.entity;

import java.util.List;

public class Layer  implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 396585215198165805L;
	private String id;//主键
	private String pId;//父ID
	private String pro_type;//项目类型 如 小区，写字楼
	private String name;//图层名称
	private int order_no;//排序号
	private List<LayerProp> layerProps;
	public List<LayerProp> getLayerProps() {
		return layerProps;
	}
	public void setLayerProps(List<LayerProp> layerProps) {
		this.layerProps = layerProps;
	}
	public String getId() {
		return id;
	}
	public String getpId() {
		return pId;
	}
	public void setpId(String pId) {
		this.pId = pId;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPro_type() {
		return pro_type;
	}
	public void setPro_type(String pro_type) {
		this.pro_type = pro_type;
	}
	public int getOrder_no() {
		return order_no;
	}
	public void setOrder_no(int order_no) {
		this.order_no = order_no;
	}

}

