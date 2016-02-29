package com.autonavi.audit.entity;

import java.util.List;

public class Overlay implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4743893970229939094L;
	private String id;//主键
	private String  layer_id;//图层id(图层类型)
	private String type;//类型（marker 点，polygon 多边面）
	private String task_id;//任务ID
	private String batch_id;//子任务ID
	private List<Point> points;//坐标信息
	private List<Property> props;//属性信息
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTask_id() {
		return task_id;
	}
	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}
	public List<Point> getPoints() {
		return points;
	}
	public void setPoints(List<Point> points) {
		this.points = points;
	}
	public List<Property> getProps() {
		return props;
	}
	public void setProps(List<Property> props) {
		this.props = props;
	}
	public String getBatch_id() {
		return batch_id;
	}
	public void setBatch_id(String batch_id) {
		this.batch_id = batch_id;
	}

}
