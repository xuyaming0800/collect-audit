package com.autonavi.audit.entity;

public class LayerTree  implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -40486058387380771L;
	
	private String id;//主键
	private String pId;//父Id
	private String name;//名称
	private boolean isParent;//是否有孩子
	private boolean open;//是否默认打开
	private String pro_type;//项目类型
	private int order_no;//排序号
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getpId() {
		return pId;
	}
	public void setpId(String pId) {
		this.pId = pId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean getIsParent() {
		return isParent;
	}
	public void setIsParent(boolean isParent) {
		this.isParent = isParent;
	}
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		this.open = open;
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
