package com.autonavi.audit.exception;

public enum BusinessExceptionEnum {
	PARAM_IS_NULL("201", "参数为空"),
	REDIS_EXCEPTION("904", "Redis出现问题"),
	TASK_CLAZZ_NOT_FOUND("4001", "任务类别未找到"),
	TASK_CLAZZ_MODIFY_LOCKED("4002", "任务类别正在被修改"),
	TASK_CLAZZ_IS_INVALID("4003", "任务类别已经被置为无效"),
	TASK_CLAZZ_IS_VALID("4005", "任务类别已经被置为有效"),
	QUERY_IS_NULL("404","查询结果为空"),
	;

	private String code;
	private String message;

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private BusinessExceptionEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public static String getMessage(String code) {
		for (BusinessExceptionEnum exp : values()) {
			if (exp.getCode().equals(code)) {
				return exp.getMessage();
			}
		}
		return null;
	}
}
