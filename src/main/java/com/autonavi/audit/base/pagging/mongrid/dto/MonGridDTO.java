package com.autonavi.audit.base.pagging.mongrid.dto;

public class MonGridDTO {
	private int start = 0;
	private int limit = 0;
	private String sortname;
	private String sortorder;

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getSortname() {
		return sortname;
	}

	public void setSortname(String sortname) {
		this.sortname = sortname;
	}

	public String getSortorder() {
		return sortorder;
	}

	public void setSortorder(String sortorder) {
		this.sortorder = sortorder;
	}

	@Override
	public String toString() {
		return "MonGridDTO [start=" + start + ", limit=" + limit
				+ ", sortname=" + sortname + ", sortorder=" + sortorder + "]";
	}

}
