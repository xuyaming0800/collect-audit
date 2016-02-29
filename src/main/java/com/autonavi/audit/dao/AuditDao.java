package com.autonavi.audit.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.autonavi.audit.base.mybatis.annotation.MyBatisRepository;
import com.autonavi.audit.entity.CollectAudit;
import com.autonavi.audit.entity.CollectAuditAndCoordinate;
import com.autonavi.audit.entity.CollectAuditImage;
import com.autonavi.audit.entity.CollectAuditLogs;
import com.autonavi.audit.entity.CollectAuditSpecimenImage;

@MyBatisRepository
public interface AuditDao {

	void insertCollectAudit(CollectAudit collectAudit);

	void updateCollectAudit(CollectAudit collectAudit);

	void insertOriginalCoordinates(@Param(value = "audit_id") String audit_id,
			@Param(value = "originalCoordinates") Double[] originalCoordinates);

	void insertCollectAuditImage(@Param(value = "audit_id") String audit_id,
			@Param(value = "images") List<CollectAuditImage> images);

	void insertCollectAuditSpecimenImage(
			@Param(value = "audit_id") String audit_id,
			@Param(value = "images") List<CollectAuditSpecimenImage> images);

	CollectAudit selectCollectAuditById(@Param(value = "id") String id,
			@Param(value = "system_type") String system_type,
			@Param(value = "type") Integer type);

	List<CollectAudit> selectCollectAuditByIds(
			List<Map<String, Object>> taskList);

	void audit(@Param(value = "id") String bsTaskId,
			@Param(value = "flag") boolean flag);

	void insertHistory(CollectAuditLogs collectAuditLogs);

	List<CollectAuditLogs> findHistory(
			@Param(value = "audit_id") String audit_id);

	List<CollectAudit> findMyselfInvolvedTask(
			@Param(value = "userName") String userName,
			@Param(value = "bsType") String bsType,
			@Param(value = "type") Integer type,
			@Param(value = "page") Integer page,
			@Param(value = "size") Integer size,
			@Param(value = "collectAudit") CollectAudit collectAudit);

	List<CollectAuditImage> findUserPhotos(
			@Param(value = "bsTaskId") String bsTaskId);

	List<Double> findOriginalCoordinate(
			@Param(value = "bsTaskId") String bsTaskId);

	List<CollectAuditSpecimenImage> findAuditSpecimenImageByTaskId(
			@Param(value = "bsTaskId") String bsTaskId);

	int findGISType(@Param(value = "system_type") String system_type);

	CollectAuditLogs findHistoryDtailByBsTaskId(
			@Param(value = "bsTaskId") String bsTaskId);

	void updateStatus(@Param(value = "bsTaskId") String bsTaskId,
			@Param(value = "status") Integer status);

	List<CollectAuditAndCoordinate> findCollectAuditCoordinateByLongitudeAndLatitude(
			@Param(value = "nMax") Double nMax,
			@Param(value = "nMin") Double nMin,
			@Param(value = "eMax") Double eMax,
			@Param(value = "eMin") Double eMin,
			@Param(value = "bsTaskId") String bsTaskId,
			@Param(value = "system_type") String system_type);

	CollectAudit findCollectAuditByTaskId(@Param(value = "taskId") String taskId);

	Double findNearImgPriceByIndex(@Param(value = "type") String type);

	Double findFarImgPriceByIndex(@Param(value = "type") String type);

	List<CollectAudit> findALlCollectAudit();

	void updateCollectAuditImageByPrimaryId(
			@Param(value = "isUsed") Boolean isUsed,
			@Param(value = "imgId") String imgId);

	CollectAuditImage findCollectAuditImageById(
			@Param(value = "imgId") String imgId);

	List<CollectAudit> selectCollectAuditByCondition(
			@Param(value = "bsType") String bsType,
			@Param(value = "pageSize") Integer pageSize,
			@Param(value = "pages") Integer pages,
			@Param(value = "submit_time_start") Date submit_time_start,
			@Param(value = "submit_time_end") Date submit_time_end,
			@Param(value = "collect_task_name") String collect_task_name,
			@Param(value = "user_name") String user_name,
			@Param(value = "task_class_name") String task_class_name);

	Long selectCollectAuditCountByCondition(
			@Param(value = "bsType") String bsType,
			@Param(value = "submit_time_start") Date submit_time_start,
			@Param(value = "submit_time_end") Date submit_time_end,
			@Param(value = "collect_task_name") String collect_task_name,
			@Param(value = "user_name") String user_name,
			@Param(value = "task_class_name") String task_class_name);

	List<CollectAudit> selectCollectAuditByConditionForInAudit(
			@Param(value = "bsType") String bsType,
			@Param(value = "pageSize") Integer pageSize,
			@Param(value = "pages") Integer pages,
			@Param(value = "submit_time_start") Date submit_time_start,
			@Param(value = "submit_time_end") Date submit_time_end,
			@Param(value = "collect_task_name") String collect_task_name,
			@Param(value = "user_name") String user_name,
			@Param(value = "task_class_name") String task_class_name);

	List<CollectAudit> selectCollectAuditByConditionForAuditComplete(
			@Param(value = "bsType") String bsType,
			@Param(value = "pageSize") Integer pageSize,
			@Param(value = "pages") Integer pages,
			@Param(value = "flag") Boolean flag,
			@Param(value = "submit_time_start") Date submit_time_start,
			@Param(value = "submit_time_end") Date submit_time_end,
			@Param(value = "collect_task_name") String collect_task_name,
			@Param(value = "user_name") String user_name,
			@Param(value = "task_class_name") String task_class_name);

	List<CollectAudit> selectCollectAuditByConditionForInAppeal(
			@Param(value = "bsType") String bsType,
			@Param(value = "pageSize") Integer pageSize,
			@Param(value = "pages") Integer pages,
			@Param(value = "submit_time_start") Date submit_time_start,
			@Param(value = "submit_time_end") Date submit_time_end,
			@Param(value = "collect_task_name") String collect_task_name,
			@Param(value = "user_name") String user_name,
			@Param(value = "task_class_name") String task_class_name);

	Long selectCollectAuditCountByConditionForInAudit(
			@Param(value = "bsType") String bsType,
			@Param(value = "submit_time_start") Date submit_time_start,
			@Param(value = "submit_time_end") Date submit_time_end,
			@Param(value = "collect_task_name") String collect_task_name,
			@Param(value = "user_name") String user_name,
			@Param(value = "task_class_name") String task_class_name);

	Long selectCollectAuditCountByConditionForAuditComplete(
			@Param(value = "bsType") String bsType,
			@Param(value = "flag") Boolean flag,
			@Param(value = "submit_time_start") Date submit_time_start,
			@Param(value = "submit_time_end") Date submit_time_end,
			@Param(value = "collect_task_name") String collect_task_name,
			@Param(value = "user_name") String user_name,
			@Param(value = "task_class_name") String task_class_name);

	Long selectCollectAuditCountByConditionForInAppeal(
			@Param(value = "bsType") String bsType,
			@Param(value = "submit_time_start") Date submit_time_start,
			@Param(value = "submit_time_end") Date submit_time_end,
			@Param(value = "collect_task_name") String collect_task_name,
			@Param(value = "user_name") String user_name,
			@Param(value = "task_class_name") String task_class_name);

	List<CollectAudit> findCollectAudit(@Param(value = "ca") CollectAudit collectAudit);

}
