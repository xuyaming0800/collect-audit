package com.autonavi.audit.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 分页对象
 * @author wenpeng.jin
 *
 */
public class Pagination implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2054264977101577463L;
	public List<?> objectList;//查询对象
	public long totalCount;//总记录数
	public int pageNo;//当前页码
	public int limit;//每页记录数
	public int start;//开始记录

	public Pagination() {
	}

	public Pagination(int pageNo, int limit) throws Exception {
		if (pageNo < 1 || limit < 0)
			throw new Exception("页码参数类型不正确");
		this.start = (pageNo - 1) * limit;
		this.limit = limit;
	}

	public Pagination(String pageNo, String limit) throws Exception {
		try {
			int pNumber = Integer.valueOf(pageNo);
			int pSize = Integer.valueOf(limit);
			if (pNumber < 1 || pSize < 0)
				throw new Exception("页码参数类型不正确");
			this.start = (pNumber - 1) * pSize;
			this.limit = pSize;
		} catch (NumberFormatException e) {
			throw new Exception("页码参数类型不正确");
		}
	}

	public List<?> getObjectList() {
		return objectList;
	}

	public void setObjectList(List<?> objectList) {
		this.objectList = objectList;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

}
