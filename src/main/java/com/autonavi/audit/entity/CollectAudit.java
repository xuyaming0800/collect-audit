package com.autonavi.audit.entity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.autonavi.audit.constant.StatusConstant;

public class CollectAudit implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = 3777237955907697602L;
	@NotNull
	@NotEmpty
	private String id;// 主键
	@NotNull
	@NotEmpty
	private String location_name;// 任务包名
	// @NotNull
	// @NotEmpty
	private String location_address;// 任务包地址

	private String task_class_name;// 任务类别

	private Integer task_class_img_count;// 任务需要拍照数量
	private Integer task_class_near_img_count;// 近景照片数
	private Integer task_class_far_img_count;// 远景照片数

	// @NotNull
	// @NotEmpty
	private String original_task_name;// 原始任务名

	@NotNull
	@NotEmpty
	private String collect_task_name;// 采集任务名
	private String audit_task_name;// 审核任务名
	@NotNull
	@NotEmpty
	private String user_name;// 用户名

	@NotNull
	@Min(0)
	private Integer status;// 状态
	private String statusString;// 状态

	@NotNull
	@Min(1)
	private Integer verify_maintain_time;// 审核允许的最长持续时间（小时计）

	private String system_type;// 系统类型
	private String systemTypeString;// 系统类型

	@Valid
	private List<CollectAuditSpecimenImage> specimenImages;// 样张图片信息
	@Valid
	private List<CollectAuditImage> images;// 采集图片信息

	// @NotNull
	// @Size(min = 2)
	private Double[] originalCoordinates;// 原始坐标

	@NotNull
	// @Past
	private Date submit_time;// 提交时间
	private Date create_time;// 创建时间

	private Date submit_time_start;// 提交时间开始
	private Date submit_time_end;// 提交时间结束
	
	private String submit_time_start_string;// 提交时间开始
	private String submit_time_end_string;// 提交时间结束

	@NotNull
	// @Min(1)
	private Double task_amount;// 包金额
	private Double custom_tolal_money;// 扣款总金额

	@NotNull
	@Min(1)
	private Integer task_freezing_time;// 任务冻结时间（小时计）

	/**
	 * 工作流使用
	 */
	private String process_definition_id;// （工作流）流程定义ID
	private String process_instance_id;// （工作流）流程实例ID
	private String taskId;// （工作流）当前任务ID

	private String task_class_name_for_audit;// （审核）对当前任务备注的可能类型
	private String taskDefinitionKey;//(工作流)区分流程节点的id
	private String bpm_task_name;//(工作流)节点名称

	private Integer count;//查询数量
	
	private String regional_information;//区域信息
	private String city;//所属城市
	private String province;//所属省份
	
	
	// 20个扩展备用字段
	private Object expand1;
	private Object expand2;
	private Object expand3;
	private Object expand4;
	private Object expand5;
	private Object expand6;
	private Object expand7;
	private Object expand8;
	private Object expand9;
	private Object expand10;
	private Object expand11;
	private Object expand12;
	private Object expand13;
	private Object expand14;
	private Object expand15;
	private Object expand16;
	private Object expand17;
	private Object expand18;
	private Object expand19;
	private Object expand20;

	// Constructors

	public CollectAudit() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocation_name() {
		return location_name;
	}

	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}

	public String getLocation_address() {
		return location_address;
	}

	public void setLocation_address(String location_address) {
		this.location_address = location_address;
	}

	public String getTask_class_name() {
		return task_class_name;
	}

	public String getTask_class_name_for_audit() {
		return task_class_name_for_audit;
	}

	public void setTask_class_name_for_audit(String task_class_name_for_audit) {
		this.task_class_name_for_audit = task_class_name_for_audit;
	}

	public String getTaskDefinitionKey() {
		return taskDefinitionKey;
	}

	public void setTaskDefinitionKey(String taskDefinitionKey) {
		this.taskDefinitionKey = taskDefinitionKey;
	}

	public String getRegional_information() {
		return regional_information;
	}

	public String getCity() {
		return city;
	}

	public void setRegional_information(String regional_information) {
		this.regional_information = regional_information;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getSubmit_time_start_string() {
		return submit_time_start_string;
	}

	public String getSubmit_time_end_string() {
		return submit_time_end_string;
	}

	public void setSubmit_time_start_string(String submit_time_start_string) {
		this.submit_time_start_string = submit_time_start_string;
	}

	public void setSubmit_time_end_string(String submit_time_end_string) {
		this.submit_time_end_string = submit_time_end_string;
	}

	public void setTask_class_name(String task_class_name) {
		this.task_class_name = task_class_name;
	}

	public Integer getTask_class_img_count() {
		return task_class_img_count;
	}

	public void setTask_class_img_count(Integer task_class_img_count) {
		this.task_class_img_count = task_class_img_count;
	}

	public Integer getTask_class_near_img_count() {
		return task_class_near_img_count;
	}

	public void setTask_class_near_img_count(Integer task_class_near_img_count) {
		this.task_class_near_img_count = task_class_near_img_count;
	}

	public Integer getTask_class_far_img_count() {
		return task_class_far_img_count;
	}

	public void setTask_class_far_img_count(Integer task_class_far_img_count) {
		this.task_class_far_img_count = task_class_far_img_count;
	}

	public String getOriginal_task_name() {
		return original_task_name;
	}

	public void setOriginal_task_name(String original_task_name) {
		this.original_task_name = original_task_name;
	}

	public String getCollect_task_name() {
		return collect_task_name;
	}

	public void setCollect_task_name(String collect_task_name) {
		this.collect_task_name = collect_task_name;
	}

	public String getAudit_task_name() {
		return audit_task_name;
	}

	public void setAudit_task_name(String audit_task_name) {
		this.audit_task_name = audit_task_name;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStatusString() {
		return StatusConstant.getName(status);
	}

	public void setStatusString(String statusString) {
		this.statusString = statusString;
	}

	public Integer getVerify_maintain_time() {
		return verify_maintain_time;
	}

	public void setVerify_maintain_time(Integer verify_maintain_time) {
		this.verify_maintain_time = verify_maintain_time;
	}

	public String getSystemTypeString() {
		return systemTypeString;
	}

	public void setSystemTypeString(String systemTypeString) {
		this.systemTypeString = systemTypeString;
	}

	public List<CollectAuditSpecimenImage> getSpecimenImages() {
		return specimenImages;
	}

	public void setSpecimenImages(List<CollectAuditSpecimenImage> specimenImages) {
		this.specimenImages = specimenImages;
	}

	public List<CollectAuditImage> getImages() {
		return images;
	}

	public void setImages(List<CollectAuditImage> images) {
		this.images = images;
	}

	public Double[] getOriginalCoordinates() {
		return originalCoordinates;
	}

	public Double getCustom_tolal_money() {
		return custom_tolal_money;
	}

	public void setCustom_tolal_money(Double custom_tolal_money) {
		this.custom_tolal_money = custom_tolal_money;
	}

	public void setOriginalCoordinates(Double[] originalCoordinates) {
		this.originalCoordinates = originalCoordinates;
	}

	public Date getSubmit_time() {
		return submit_time;
	}

	public void setSubmit_time(Date submit_time) {
		this.submit_time = submit_time;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Date getSubmit_time_start() {
		return submit_time_start;
	}

	public void setSubmit_time_start(Date submit_time_start) {
		this.submit_time_start = submit_time_start;
	}

	public Date getSubmit_time_end() {
		return submit_time_end;
	}

	public void setSubmit_time_end(Date submit_time_end) {
		this.submit_time_end = submit_time_end;
	}

	public String getProcess_definition_id() {
		return process_definition_id;
	}

	public void setProcess_definition_id(String process_definition_id) {
		this.process_definition_id = process_definition_id;
	}

	public String getProcess_instance_id() {
		return process_instance_id;
	}

	public void setProcess_instance_id(String process_instance_id) {
		this.process_instance_id = process_instance_id;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Double getTask_amount() {
		return task_amount;
	}

	public void setTask_amount(Double task_amount) {
		this.task_amount = task_amount;
	}

	public Integer getTask_freezing_time() {
		return task_freezing_time;
	}

	public void setTask_freezing_time(Integer task_freezing_time) {
		this.task_freezing_time = task_freezing_time;
	}

	public Object getExpand1() {
		return expand1;
	}

	public void setExpand1(Object expand1) {
		this.expand1 = expand1;
	}

	public Object getExpand2() {
		return expand2;
	}

	public void setExpand2(Object expand2) {
		this.expand2 = expand2;
	}

	public Object getExpand3() {
		return expand3;
	}

	public void setExpand3(Object expand3) {
		this.expand3 = expand3;
	}

	public Object getExpand4() {
		return expand4;
	}

	public void setExpand4(Object expand4) {
		this.expand4 = expand4;
	}

	public Object getExpand5() {
		return expand5;
	}

	public void setExpand5(Object expand5) {
		this.expand5 = expand5;
	}

	public Object getExpand6() {
		return expand6;
	}

	public void setExpand6(Object expand6) {
		this.expand6 = expand6;
	}

	public Object getExpand7() {
		return expand7;
	}

	public void setExpand7(Object expand7) {
		this.expand7 = expand7;
	}

	public Object getExpand8() {
		return expand8;
	}

	public void setExpand8(Object expand8) {
		this.expand8 = expand8;
	}

	public Object getExpand9() {
		return expand9;
	}

	public void setExpand9(Object expand9) {
		this.expand9 = expand9;
	}

	public Object getExpand10() {
		return expand10;
	}

	public void setExpand10(Object expand10) {
		this.expand10 = expand10;
	}

	public Object getExpand11() {
		return expand11;
	}

	public void setExpand11(Object expand11) {
		this.expand11 = expand11;
	}

	public Object getExpand12() {
		return expand12;
	}

	public void setExpand12(Object expand12) {
		this.expand12 = expand12;
	}

	public Object getExpand13() {
		return expand13;
	}

	public void setExpand13(Object expand13) {
		this.expand13 = expand13;
	}

	public Object getExpand14() {
		return expand14;
	}

	public void setExpand14(Object expand14) {
		this.expand14 = expand14;
	}

	public Object getExpand15() {
		return expand15;
	}

	public void setExpand15(Object expand15) {
		this.expand15 = expand15;
	}

	public Object getExpand16() {
		return expand16;
	}

	public void setExpand16(Object expand16) {
		this.expand16 = expand16;
	}

	public Object getExpand17() {
		return expand17;
	}

	public void setExpand17(Object expand17) {
		this.expand17 = expand17;
	}

	public Object getExpand18() {
		return expand18;
	}

	public void setExpand18(Object expand18) {
		this.expand18 = expand18;
	}

	public String getBpm_task_name() {
		return bpm_task_name;
	}

	public String getSystem_type() {
		return system_type;
	}

	public void setSystem_type(String system_type) {
		this.system_type = system_type;
	}

	public void setBpm_task_name(String bpm_task_name) {
		this.bpm_task_name = bpm_task_name;
	}

	public Object getExpand19() {
		return expand19;
	}

	public void setExpand19(Object expand19) {
		this.expand19 = expand19;
	}

	public Object getExpand20() {
		return expand20;
	}

	public void setExpand20(Object expand20) {
		this.expand20 = expand20;
	}

	@Override
	public String toString() {
		return "CollectAudit [id=" + id + ", location_name=" + location_name
				+ ", location_address=" + location_address
				+ ", task_class_name=" + task_class_name
				+ ", task_class_img_count=" + task_class_img_count
				+ ", task_class_near_img_count=" + task_class_near_img_count
				+ ", task_class_far_img_count=" + task_class_far_img_count
				+ ", original_task_name=" + original_task_name
				+ ", collect_task_name=" + collect_task_name
				+ ", audit_task_name=" + audit_task_name + ", user_name="
				+ user_name + ", status=" + status + ", statusString="
				+ statusString + ", verify_maintain_time="
				+ verify_maintain_time + ", system_type=" + system_type
				+ ", systemTypeString=" + systemTypeString
				+ ", specimenImages=" + specimenImages + ", images=" + images
				+ ", originalCoordinates="
				+ Arrays.toString(originalCoordinates) + ", submit_time="
				+ submit_time + ", create_time=" + create_time
				+ ", submit_time_start=" + submit_time_start
				+ ", submit_time_end=" + submit_time_end
				+ ", submit_time_start_string=" + submit_time_start_string
				+ ", submit_time_end_string=" + submit_time_end_string
				+ ", task_amount=" + task_amount + ", custom_tolal_money="
				+ custom_tolal_money + ", task_freezing_time="
				+ task_freezing_time + ", process_definition_id="
				+ process_definition_id + ", process_instance_id="
				+ process_instance_id + ", taskId=" + taskId
				+ ", task_class_name_for_audit=" + task_class_name_for_audit
				+ ", taskDefinitionKey=" + taskDefinitionKey
				+ ", bpm_task_name=" + bpm_task_name + ", count=" + count
				+ ", regional_information=" + regional_information + ", city="
				+ city + ", province=" + province + ", expand1=" + expand1
				+ ", expand2=" + expand2 + ", expand3=" + expand3
				+ ", expand4=" + expand4 + ", expand5=" + expand5
				+ ", expand6=" + expand6 + ", expand7=" + expand7
				+ ", expand8=" + expand8 + ", expand9=" + expand9
				+ ", expand10=" + expand10 + ", expand11=" + expand11
				+ ", expand12=" + expand12 + ", expand13=" + expand13
				+ ", expand14=" + expand14 + ", expand15=" + expand15
				+ ", expand16=" + expand16 + ", expand17=" + expand17
				+ ", expand18=" + expand18 + ", expand19=" + expand19
				+ ", expand20=" + expand20 + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((audit_task_name == null) ? 0 : audit_task_name.hashCode());
		result = prime * result
				+ ((bpm_task_name == null) ? 0 : bpm_task_name.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime
				* result
				+ ((collect_task_name == null) ? 0 : collect_task_name
						.hashCode());
		result = prime * result + ((count == null) ? 0 : count.hashCode());
		result = prime * result
				+ ((create_time == null) ? 0 : create_time.hashCode());
		result = prime
				* result
				+ ((custom_tolal_money == null) ? 0 : custom_tolal_money
						.hashCode());
		result = prime * result + ((expand1 == null) ? 0 : expand1.hashCode());
		result = prime * result
				+ ((expand10 == null) ? 0 : expand10.hashCode());
		result = prime * result
				+ ((expand11 == null) ? 0 : expand11.hashCode());
		result = prime * result
				+ ((expand12 == null) ? 0 : expand12.hashCode());
		result = prime * result
				+ ((expand13 == null) ? 0 : expand13.hashCode());
		result = prime * result
				+ ((expand14 == null) ? 0 : expand14.hashCode());
		result = prime * result
				+ ((expand15 == null) ? 0 : expand15.hashCode());
		result = prime * result
				+ ((expand16 == null) ? 0 : expand16.hashCode());
		result = prime * result
				+ ((expand17 == null) ? 0 : expand17.hashCode());
		result = prime * result
				+ ((expand18 == null) ? 0 : expand18.hashCode());
		result = prime * result
				+ ((expand19 == null) ? 0 : expand19.hashCode());
		result = prime * result + ((expand2 == null) ? 0 : expand2.hashCode());
		result = prime * result
				+ ((expand20 == null) ? 0 : expand20.hashCode());
		result = prime * result + ((expand3 == null) ? 0 : expand3.hashCode());
		result = prime * result + ((expand4 == null) ? 0 : expand4.hashCode());
		result = prime * result + ((expand5 == null) ? 0 : expand5.hashCode());
		result = prime * result + ((expand6 == null) ? 0 : expand6.hashCode());
		result = prime * result + ((expand7 == null) ? 0 : expand7.hashCode());
		result = prime * result + ((expand8 == null) ? 0 : expand8.hashCode());
		result = prime * result + ((expand9 == null) ? 0 : expand9.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((images == null) ? 0 : images.hashCode());
		result = prime
				* result
				+ ((location_address == null) ? 0 : location_address.hashCode());
		result = prime * result
				+ ((location_name == null) ? 0 : location_name.hashCode());
		result = prime * result + Arrays.hashCode(originalCoordinates);
		result = prime
				* result
				+ ((original_task_name == null) ? 0 : original_task_name
						.hashCode());
		result = prime
				* result
				+ ((process_definition_id == null) ? 0 : process_definition_id
						.hashCode());
		result = prime
				* result
				+ ((process_instance_id == null) ? 0 : process_instance_id
						.hashCode());
		result = prime * result
				+ ((province == null) ? 0 : province.hashCode());
		result = prime
				* result
				+ ((regional_information == null) ? 0 : regional_information
						.hashCode());
		result = prime * result
				+ ((specimenImages == null) ? 0 : specimenImages.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ ((statusString == null) ? 0 : statusString.hashCode());
		result = prime * result
				+ ((submit_time == null) ? 0 : submit_time.hashCode());
		result = prime * result
				+ ((submit_time_end == null) ? 0 : submit_time_end.hashCode());
		result = prime
				* result
				+ ((submit_time_end_string == null) ? 0
						: submit_time_end_string.hashCode());
		result = prime
				* result
				+ ((submit_time_start == null) ? 0 : submit_time_start
						.hashCode());
		result = prime
				* result
				+ ((submit_time_start_string == null) ? 0
						: submit_time_start_string.hashCode());
		result = prime
				* result
				+ ((systemTypeString == null) ? 0 : systemTypeString.hashCode());
		result = prime * result
				+ ((system_type == null) ? 0 : system_type.hashCode());
		result = prime
				* result
				+ ((taskDefinitionKey == null) ? 0 : taskDefinitionKey
						.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		result = prime * result
				+ ((task_amount == null) ? 0 : task_amount.hashCode());
		result = prime
				* result
				+ ((task_class_far_img_count == null) ? 0
						: task_class_far_img_count.hashCode());
		result = prime
				* result
				+ ((task_class_img_count == null) ? 0 : task_class_img_count
						.hashCode());
		result = prime * result
				+ ((task_class_name == null) ? 0 : task_class_name.hashCode());
		result = prime
				* result
				+ ((task_class_name_for_audit == null) ? 0
						: task_class_name_for_audit.hashCode());
		result = prime
				* result
				+ ((task_class_near_img_count == null) ? 0
						: task_class_near_img_count.hashCode());
		result = prime
				* result
				+ ((task_freezing_time == null) ? 0 : task_freezing_time
						.hashCode());
		result = prime * result
				+ ((user_name == null) ? 0 : user_name.hashCode());
		result = prime
				* result
				+ ((verify_maintain_time == null) ? 0 : verify_maintain_time
						.hashCode());
		return result;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CollectAudit other = (CollectAudit) obj;
		if (audit_task_name == null) {
			if (other.audit_task_name != null)
				return false;
		} else if (!audit_task_name.equals(other.audit_task_name))
			return false;
		if (bpm_task_name == null) {
			if (other.bpm_task_name != null)
				return false;
		} else if (!bpm_task_name.equals(other.bpm_task_name))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (collect_task_name == null) {
			if (other.collect_task_name != null)
				return false;
		} else if (!collect_task_name.equals(other.collect_task_name))
			return false;
		if (count == null) {
			if (other.count != null)
				return false;
		} else if (!count.equals(other.count))
			return false;
		if (create_time == null) {
			if (other.create_time != null)
				return false;
		} else if (!create_time.equals(other.create_time))
			return false;
		if (custom_tolal_money == null) {
			if (other.custom_tolal_money != null)
				return false;
		} else if (!custom_tolal_money.equals(other.custom_tolal_money))
			return false;
		if (expand1 == null) {
			if (other.expand1 != null)
				return false;
		} else if (!expand1.equals(other.expand1))
			return false;
		if (expand10 == null) {
			if (other.expand10 != null)
				return false;
		} else if (!expand10.equals(other.expand10))
			return false;
		if (expand11 == null) {
			if (other.expand11 != null)
				return false;
		} else if (!expand11.equals(other.expand11))
			return false;
		if (expand12 == null) {
			if (other.expand12 != null)
				return false;
		} else if (!expand12.equals(other.expand12))
			return false;
		if (expand13 == null) {
			if (other.expand13 != null)
				return false;
		} else if (!expand13.equals(other.expand13))
			return false;
		if (expand14 == null) {
			if (other.expand14 != null)
				return false;
		} else if (!expand14.equals(other.expand14))
			return false;
		if (expand15 == null) {
			if (other.expand15 != null)
				return false;
		} else if (!expand15.equals(other.expand15))
			return false;
		if (expand16 == null) {
			if (other.expand16 != null)
				return false;
		} else if (!expand16.equals(other.expand16))
			return false;
		if (expand17 == null) {
			if (other.expand17 != null)
				return false;
		} else if (!expand17.equals(other.expand17))
			return false;
		if (expand18 == null) {
			if (other.expand18 != null)
				return false;
		} else if (!expand18.equals(other.expand18))
			return false;
		if (expand19 == null) {
			if (other.expand19 != null)
				return false;
		} else if (!expand19.equals(other.expand19))
			return false;
		if (expand2 == null) {
			if (other.expand2 != null)
				return false;
		} else if (!expand2.equals(other.expand2))
			return false;
		if (expand20 == null) {
			if (other.expand20 != null)
				return false;
		} else if (!expand20.equals(other.expand20))
			return false;
		if (expand3 == null) {
			if (other.expand3 != null)
				return false;
		} else if (!expand3.equals(other.expand3))
			return false;
		if (expand4 == null) {
			if (other.expand4 != null)
				return false;
		} else if (!expand4.equals(other.expand4))
			return false;
		if (expand5 == null) {
			if (other.expand5 != null)
				return false;
		} else if (!expand5.equals(other.expand5))
			return false;
		if (expand6 == null) {
			if (other.expand6 != null)
				return false;
		} else if (!expand6.equals(other.expand6))
			return false;
		if (expand7 == null) {
			if (other.expand7 != null)
				return false;
		} else if (!expand7.equals(other.expand7))
			return false;
		if (expand8 == null) {
			if (other.expand8 != null)
				return false;
		} else if (!expand8.equals(other.expand8))
			return false;
		if (expand9 == null) {
			if (other.expand9 != null)
				return false;
		} else if (!expand9.equals(other.expand9))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (images == null) {
			if (other.images != null)
				return false;
		} else if (!images.equals(other.images))
			return false;
		if (location_address == null) {
			if (other.location_address != null)
				return false;
		} else if (!location_address.equals(other.location_address))
			return false;
		if (location_name == null) {
			if (other.location_name != null)
				return false;
		} else if (!location_name.equals(other.location_name))
			return false;
		if (!Arrays.equals(originalCoordinates, other.originalCoordinates))
			return false;
		if (original_task_name == null) {
			if (other.original_task_name != null)
				return false;
		} else if (!original_task_name.equals(other.original_task_name))
			return false;
		if (process_definition_id == null) {
			if (other.process_definition_id != null)
				return false;
		} else if (!process_definition_id.equals(other.process_definition_id))
			return false;
		if (process_instance_id == null) {
			if (other.process_instance_id != null)
				return false;
		} else if (!process_instance_id.equals(other.process_instance_id))
			return false;
		if (province == null) {
			if (other.province != null)
				return false;
		} else if (!province.equals(other.province))
			return false;
		if (regional_information == null) {
			if (other.regional_information != null)
				return false;
		} else if (!regional_information.equals(other.regional_information))
			return false;
		if (specimenImages == null) {
			if (other.specimenImages != null)
				return false;
		} else if (!specimenImages.equals(other.specimenImages))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (statusString == null) {
			if (other.statusString != null)
				return false;
		} else if (!statusString.equals(other.statusString))
			return false;
		if (submit_time == null) {
			if (other.submit_time != null)
				return false;
		} else if (!submit_time.equals(other.submit_time))
			return false;
		if (submit_time_end == null) {
			if (other.submit_time_end != null)
				return false;
		} else if (!submit_time_end.equals(other.submit_time_end))
			return false;
		if (submit_time_end_string == null) {
			if (other.submit_time_end_string != null)
				return false;
		} else if (!submit_time_end_string.equals(other.submit_time_end_string))
			return false;
		if (submit_time_start == null) {
			if (other.submit_time_start != null)
				return false;
		} else if (!submit_time_start.equals(other.submit_time_start))
			return false;
		if (submit_time_start_string == null) {
			if (other.submit_time_start_string != null)
				return false;
		} else if (!submit_time_start_string
				.equals(other.submit_time_start_string))
			return false;
		if (systemTypeString == null) {
			if (other.systemTypeString != null)
				return false;
		} else if (!systemTypeString.equals(other.systemTypeString))
			return false;
		if (system_type == null) {
			if (other.system_type != null)
				return false;
		} else if (!system_type.equals(other.system_type))
			return false;
		if (taskDefinitionKey == null) {
			if (other.taskDefinitionKey != null)
				return false;
		} else if (!taskDefinitionKey.equals(other.taskDefinitionKey))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		if (task_amount == null) {
			if (other.task_amount != null)
				return false;
		} else if (!task_amount.equals(other.task_amount))
			return false;
		if (task_class_far_img_count == null) {
			if (other.task_class_far_img_count != null)
				return false;
		} else if (!task_class_far_img_count
				.equals(other.task_class_far_img_count))
			return false;
		if (task_class_img_count == null) {
			if (other.task_class_img_count != null)
				return false;
		} else if (!task_class_img_count.equals(other.task_class_img_count))
			return false;
		if (task_class_name == null) {
			if (other.task_class_name != null)
				return false;
		} else if (!task_class_name.equals(other.task_class_name))
			return false;
		if (task_class_name_for_audit == null) {
			if (other.task_class_name_for_audit != null)
				return false;
		} else if (!task_class_name_for_audit
				.equals(other.task_class_name_for_audit))
			return false;
		if (task_class_near_img_count == null) {
			if (other.task_class_near_img_count != null)
				return false;
		} else if (!task_class_near_img_count
				.equals(other.task_class_near_img_count))
			return false;
		if (task_freezing_time == null) {
			if (other.task_freezing_time != null)
				return false;
		} else if (!task_freezing_time.equals(other.task_freezing_time))
			return false;
		if (user_name == null) {
			if (other.user_name != null)
				return false;
		} else if (!user_name.equals(other.user_name))
			return false;
		if (verify_maintain_time == null) {
			if (other.verify_maintain_time != null)
				return false;
		} else if (!verify_maintain_time.equals(other.verify_maintain_time))
			return false;
		return true;
	}
}
