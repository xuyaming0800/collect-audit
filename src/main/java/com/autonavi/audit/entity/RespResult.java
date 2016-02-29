package com.autonavi.audit.entity;

import java.io.Serializable;

/**
 * 相应结果
 * @author jinwenpeng
 *
 */
public class RespResult implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5351645691435142215L;
	
	private String result;
	private Object data;
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}
