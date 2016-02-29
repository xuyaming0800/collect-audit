package com.autonavi.audit.dao;

import java.util.List;

import org.activiti.engine.repository.ProcessDefinition;
import org.apache.ibatis.annotations.Param;

import com.autonavi.audit.base.mybatis.annotation.MyBatisRepository;
import com.autonavi.audit.entity.CollectAuditProchook;

@MyBatisRepository
public interface AuditProcHookDao {

	List<CollectAuditProchook> selectExpression(List<ProcessDefinition> pds);

	List<CollectAuditProchook> findAll();

	void insert(
			@Param(value = "processDefinitionId") String processDefinitionId,
			@Param(value = "expression") String expression,
			@Param(value = "userName") String userName);

	void delete(@Param(value = "processDefinitionId") String processDefinitionId);

	void updateExpression(
			@Param(value = "processDefinitionId") String processDefinitionId,
			@Param(value = "expression") String expression);

}
