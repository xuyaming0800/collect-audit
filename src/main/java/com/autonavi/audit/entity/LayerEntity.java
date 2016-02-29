package com.autonavi.audit.entity;

import java.util.List;

public class LayerEntity  implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 396585215198165805L;
	private String pro_type;//项目类型 如 小区，写字楼
	private String name;//图层名称
	private List<LayerEntity> list;
	
	public String getPro_type() {
		return pro_type;
	}
	public void setPro_type(String pro_type) {
		this.pro_type = pro_type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<LayerEntity> getList() {
		return list;
	}
	public void setList(List<LayerEntity> list) {
		this.list = list;
	}

}

