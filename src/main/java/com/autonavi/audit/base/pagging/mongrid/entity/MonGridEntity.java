package com.autonavi.audit.base.pagging.mongrid.entity;

import java.util.List;

public class MonGridEntity {
	private long total = 0;
	private List<Rows> rows = null;

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<Rows> getRows() {
		return rows;
	}

	public void setRows(List<Rows> rows) {
		this.rows = rows;
	}

	@Override
	public String toString() {
		return "MonGridEntity [total=" + total + ", rows=" + rows + "]";
	}
}
