package com.autonavi.audit.base.pagging.mongrid.entity;

import java.util.List;

public class Rows {
	private String id = "0";
	private List<Rows> children = null;
	private Object cell = null;
	private int level = 0;

	public String getId() {
		return String.valueOf(id);
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Rows> getChildren() {
		return children;
	}

	public void setChildren(List<Rows> children) {
		this.children = children;
	}

	public Object getCell() {
		return cell;
	}

	public void setCell(Object cell) {
		this.cell = cell;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "Rows [id=" + id + ", children=" + children + ", cell=" + cell
				+ ", level=" + level + "]";
	}

}
