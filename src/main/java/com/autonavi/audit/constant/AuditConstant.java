package com.autonavi.audit.constant;

public class AuditConstant {
	/**
	 * 对于点的查询周边任务的半径大小
	 */
	public static final Double radius_point = 0.01;
	/**
	 * 对于面的查询周边任务的半径大小
	 */
	public static final Double radius_plane = 0.5;

	/**
	 * 广告拍拍的项目id，兼容经济环境
	 */
	public static final String systemId_ad = "1";
	/**
	 * 审核任务小项的审核通过标示
	 */
	public static final Integer status_ok = 1;
	/**
	 * 审核任务小项的审核失败标示
	 */
	public static final Integer status_fall = 0;
	/**
	 * 审核统计金额map中的key值：给采集用户的钱[原始]
	 */
	public static final String totalUserMoney = "totalUserMoney";
	/**
	 * 审核统计金额map中的key值：给采集用户的钱[修改]
	 */
	public static final String totalUserChangeMoney = "totalUserChangeMoney";
	/**
	 * 审核统计金额map中的key值：扣客户的钱
	 */
	public static final String totalCustomMoney = "totalCustomMoney";

	/**
	 * 返回所有项目
	 */
	public static final String ALL_PROJECT_INFO_CACHE_PREFIX = "ALL_PROJECT_INFO_CACHE_PREFIX";

	/**
	 * 返回所有正常的项目
	 */
	public static final String ALL_NORMAL_PROJECT_INFO_CACHE_PREFIX = "ALL_NORMAL_PROJECT_INFO_CACHE_PREFIX";
	/**
	 * 根据客户id分类，查询项目信息；后面直接加上客户id
	 */
	public static final String CUSTOM_PROJECT_INFO_CACHE_PREFIX = "CPICP_";
	/**
	 * 角色名：是否在审核中显示金额的角色
	 */
	public static final String AUDIT_SHOW_MONEY_ROLE = "AUDIT_SHOW_MONEY";
	/**
	 * 广告拍拍中:金额计算的类型名称
	 * ps：下线之后可以删除
	 */
	public static final String GGPP_TYPE_NAME = "公交站亭";
	/**
	 * 客户列表
	 */
	public static final String USER_LIST_TYPE_CACHE_PREFIX="BOSS_TYPE_LIST_"; 
}
