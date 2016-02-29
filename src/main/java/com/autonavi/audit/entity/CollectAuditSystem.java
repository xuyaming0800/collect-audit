package com.autonavi.audit.entity;

import javax.validation.constraints.NotNull;

public class CollectAuditSystem implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1207792603466438248L;
	private String id;
	@NotNull
	private String system_name;// 系统名
	@NotNull
	private String mqurl;// MQ的地址
	@NotNull
	private String input_queue;// 输入队列的名字
	@NotNull
	private String output_queue;// 输出队列的名字
	private Long create_time; //创建世间
	
	private String system_id;//系统id
	/**
	 * GIS类型。点为1、线为2、面为3
	 * 
	 * @see com.autonavi.audit.constant.GISTypeConstant
	 */
	private Integer gis_type;

	// Constructors

	/** default constructor */
	public CollectAuditSystem() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMqurl() {
		return this.mqurl;
	}

	public void setMqurl(String mqurl) {
		this.mqurl = mqurl;
	}

	public String getSystem_name() {
		return system_name;
	}

	public void setSystem_name(String system_name) {
		this.system_name = system_name;
	}

	public String getInput_queue() {
		return input_queue;
	}

	public void setInput_queue(String input_queue) {
		this.input_queue = input_queue;
	}

	public String getOutput_queue() {
		return output_queue;
	}

	public void setOutput_queue(String output_queue) {
		this.output_queue = output_queue;
	}

	public Integer getGis_type() {
		return gis_type;
	}

	public void setGis_type(Integer gis_type) {
		this.gis_type = gis_type;
	}

	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}

	public String getSystem_id() {
		return system_id;
	}

	public void setSystem_id(String system_id) {
		this.system_id = system_id;
	}

	@Override
	public String toString() {
		return "CollectAuditSystem [id=" + id + ", system_name=" + system_name
				+ ", mqurl=" + mqurl + ", input_queue=" + input_queue
				+ ", output_queue=" + output_queue + ", create_time="
				+ create_time + ", system_id=" + system_id + ", gis_type="
				+ gis_type + "]";
	}
	
}