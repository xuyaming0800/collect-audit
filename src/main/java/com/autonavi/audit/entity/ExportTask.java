package com.autonavi.audit.entity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ExportTask implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 444156730674362980L;
	// Fields
	private String id;// 主键
	private String location_name;// 任务包名
	private String location_address;// 任务包地址
	private String task_class_name;// 任务类别
	private String original_task_name;// 原始任务名
	private String audit_task_name;// 审核任务名
	private String user_name;// 用户名
	private String systemTypeString;// 系统类型
	private List<CollectAuditImage> images;// 采集图片信息
	private String bpm_task_name;//(工作流)节点名称
	private List<Point> points;//坐标信息
	private List<Property> props;//属性信息
	/**
	 * 采集任务名
	 */
	private String collect_task_name;
	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * 系统类型
	 */
	private String system_type;
	/**
	 * 提交时间
	 */
	private Date submit_time;
	/**
	 * 创建时间
	 */
	private Date create_time;
	/**
	 * 提交时间开始
	 */
	private Date submit_time_start;
	/**
	 * 提交时间结束
	 */
	private Date submit_time_end;
	/**
	 * 所属城市
	 */
	private String city;
	/**
	 * 子任务类型
	 */
	private String childTaskType;
	/**
	 * 子任务名称
	 */
	private String childTaskName;
	/**
	 * 子任务类型id
	 */
	private String childTaskId;
	/**
	 * 百度坐标[LON,LAT]
	 */
	private Double[] coordinates_baidu;
	/**
	 * GPS坐标[LON,LAT]
	 */
	private Double[] coordinates_gps;
	/**
	 * 图片地址，中甲使用分号隔开
	 */
	private String imageUrlString;
	
	public String getId() {
		return id;
	}

	public String getLocation_name() {
		return location_name;
	}

	public String getLocation_address() {
		return location_address;
	}

	public String getTask_class_name() {
		return task_class_name;
	}

	public String getOriginal_task_name() {
		return original_task_name;
	}

	public String getCollect_task_name() {
		return collect_task_name;
	}

	public String getChildTaskId() {
		return childTaskId;
	}

	public void setChildTaskId(String childTaskId) {
		this.childTaskId = childTaskId;
	}

	public String getAudit_task_name() {
		return audit_task_name;
	}

	public String getUser_name() {
		return user_name;
	}

	public Integer getStatus() {
		return status;
	}

	public String getSystem_type() {
		return system_type;
	}

	public String getSystemTypeString() {
		return systemTypeString;
	}

	public List<CollectAuditImage> getImages() {
		return images;
	}

	public Date getSubmit_time() {
		return submit_time;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public Date getSubmit_time_start() {
		return submit_time_start;
	}

	public Date getSubmit_time_end() {
		return submit_time_end;
	}

	public String getBpm_task_name() {
		return bpm_task_name;
	}

	public String getCity() {
		return city;
	}

	public String getChildTaskType() {
		return childTaskType;
	}

	public String getChildTaskName() {
		return childTaskName;
	}

	public Double[] getCoordinates_baidu() {
		return coordinates_baidu;
	}

	public Double[] getCoordinates_gps() {
		return coordinates_gps;
	}

	public String getImageUrlString() {
		return imageUrlString;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Point> getPoints() {
		return points;
	}

	public List<Property> getProps() {
		return props;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	public void setProps(List<Property> props) {
		this.props = props;
	}

	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}

	public void setLocation_address(String location_address) {
		this.location_address = location_address;
	}

	public void setTask_class_name(String task_class_name) {
		this.task_class_name = task_class_name;
	}

	public void setOriginal_task_name(String original_task_name) {
		this.original_task_name = original_task_name;
	}

	public void setCollect_task_name(String collect_task_name) {
		this.collect_task_name = collect_task_name;
	}

	public void setAudit_task_name(String audit_task_name) {
		this.audit_task_name = audit_task_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setSystem_type(String system_type) {
		this.system_type = system_type;
	}

	public void setSystemTypeString(String systemTypeString) {
		this.systemTypeString = systemTypeString;
	}

	public void setImages(List<CollectAuditImage> images) {
		this.images = images;
	}

	public void setSubmit_time(Date submit_time) {
		this.submit_time = submit_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public void setSubmit_time_start(Date submit_time_start) {
		this.submit_time_start = submit_time_start;
	}

	public void setSubmit_time_end(Date submit_time_end) {
		this.submit_time_end = submit_time_end;
	}

	public void setBpm_task_name(String bpm_task_name) {
		this.bpm_task_name = bpm_task_name;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setChildTaskType(String childTaskType) {
		this.childTaskType = childTaskType;
	}

	public void setChildTaskName(String childTaskName) {
		this.childTaskName = childTaskName;
	}

	public void setCoordinates_baidu(Double[] coordinates_baidu) {
		this.coordinates_baidu = coordinates_baidu;
	}

	public void setCoordinates_gps(Double[] coordinates_gps) {
		this.coordinates_gps = coordinates_gps;
	}

	public void setImageUrlString(String imageUrlString) {
		this.imageUrlString = imageUrlString;
	}

	@Override
	public ExportTask clone() throws CloneNotSupportedException {
        try {   
            return (ExportTask) super.clone();   
        } catch (CloneNotSupportedException e) {   
            return null;   
        }   
	}

	@Override
	public String toString() {
		return "ExportTask [id=" + id + ", location_name=" + location_name
				+ ", location_address=" + location_address
				+ ", task_class_name=" + task_class_name
				+ ", original_task_name=" + original_task_name
				+ ", audit_task_name=" + audit_task_name + ", user_name="
				+ user_name + ", systemTypeString=" + systemTypeString
				+ ", images=" + images + ", bpm_task_name=" + bpm_task_name
				+ ", points=" + points + ", props=" + props
				+ ", collect_task_name=" + collect_task_name + ", status="
				+ status + ", system_type=" + system_type + ", submit_time="
				+ submit_time + ", create_time=" + create_time
				+ ", submit_time_start=" + submit_time_start
				+ ", submit_time_end=" + submit_time_end + ", city=" + city
				+ ", childTaskType=" + childTaskType + ", childTaskName="
				+ childTaskName + ", childTaskId=" + childTaskId
				+ ", coordinates_baidu=" + Arrays.toString(coordinates_baidu)
				+ ", coordinates_gps=" + Arrays.toString(coordinates_gps)
				+ ", imageUrlString=" + imageUrlString + "]";
	}
}
