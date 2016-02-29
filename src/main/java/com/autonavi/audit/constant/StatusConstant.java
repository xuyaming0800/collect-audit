package com.autonavi.audit.constant;

public class StatusConstant {
	public static final Integer preAudit = 1;// 待审核
	public static final Integer auditing = 2;// 审核中
	public static final Integer auditSuccess = 3;// 审核成功
	public static final Integer auditFailure = 4;// 审核失败
	public static final Integer auditTimeOut = 5;// 审核超时
	public static final Integer firstAuditSuccess = 6;// 初审成功
	public static final Integer firstAuditFailure = 7;// 初审失败
	public static final Integer appeal = 8;// 申诉

	public static String getName(Integer key) {
		if (key == preAudit)
			return "待审核";
		if (key == auditing)
			return "审核中";
		if (key == auditSuccess)
			return "审核成功";
		if (key == auditFailure)
			return "审核失败";
		if (key == auditTimeOut)
			return "审核超时";
		if (key == firstAuditSuccess)
			return "初审成功";
		if (key == firstAuditFailure)
			return "初审失败";
		if (key == appeal)
			return "申诉中";
		return null;
	}

}
