package com.autonavi.audit.workflow;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskQuery;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autonavi.audit.entity.CollectAudit;
import com.autonavi.audit.entity.HistoricalRecords;
import com.autonavi.audit.exception.TaskAlreadyClaimedException;
import com.autonavi.audit.exception.TaskNotFoundException;
import com.autonavi.audit.service.AuditProcHookService;
import com.autonavi.audit.util.PropConstants;
import com.autonavi.audit.util.PropertiesConfig;

/**
 * 审核流程类，调用工作流引擎
 * 
 * @author jia.miao
 *
 */
@Service
public class AuditProcess {

	/**
	 * 部署
	 * 
	 * @param resourceName
	 *            资源名称
	 * @param text
	 *            XML文件内容
	 * @return 部署ID
	 */
	public String deploy(String resourceName, String text) {
		resourceName = resourceName + ".bpmn";// 防止部署时不加扩展名导致部署出现其它状况
		DeploymentBuilder deploymentBuilder = repositoryService
				.createDeployment().enableDuplicateFiltering()
				.name(resourceName);
		// InputStream in=new ByteArrayInputStream(text.getBytes());
		Deployment deployment = deploymentBuilder.addString(resourceName, text)
		// .addInputStream(resourceName, in)
				.deploy();
		String deploymentId = deployment.getId();
		return deploymentId;
	}

	/**
	 * 根据部署ID查询流程定义ID
	 * 
	 * @param deploymentId
	 * @return
	 */
	public String getProcessDefinitionIdByDeploymentId(String deploymentId) {
		return repositoryService.createProcessDefinitionQuery()
				.deploymentId(deploymentId).singleResult().getId();
	}

	/**
	 * 移除部署
	 * 
	 * @param deploymentId
	 *            部署ID
	 */
	public void deleteDeployment(String deploymentId) {
		repositoryService.deleteDeployment(deploymentId, true);
	}

	/**
	 * 得到流程定义XML
	 * 
	 * @param deploymentId
	 *            部署ID
	 * @param resourceName
	 *            资源名
	 * @return XML流
	 */
	public InputStream getProcessXml(String deploymentId, String resourceName) {
		return repositoryService
				.getResourceAsStream(deploymentId, resourceName);
	}

	/**
	 * 得到流程图片（无渲染）
	 * 
	 * @param processDefinitionId
	 *            流程定义ID
	 * @return png图片流
	 */
	public InputStream getProcessDiagram(String processDefinitionId) {
		return repositoryService.getProcessDiagram(processDefinitionId);
	}

	/**
	 * 激活流程定义
	 * 
	 * @param processDefinitionId
	 *            流程定义ID
	 */
	public void activateProcessDefinitionById(String processDefinitionId) {
		repositoryService.activateProcessDefinitionById(processDefinitionId);
	}

	/**
	 * 激活流程定义
	 * 
	 * @param processDefinitionKey
	 *            流程定义Key
	 */
	public void activateProcessDefinitionByKey(String processDefinitionKey) {
		repositoryService.activateProcessDefinitionByKey(processDefinitionKey);
	}

	/**
	 * 挂起流程定义
	 * 
	 * @param processDefinitionId
	 *            流程定义ID
	 */
	public void suspendProcessDefinitionById(String processDefinitionId) {
		repositoryService.suspendProcessDefinitionById(processDefinitionId);
	}

	/**
	 * 挂起流程定义
	 * 
	 * @param processDefinitionKey
	 *            流程定义Key
	 */
	public void suspendProcessDefinitionByKey(String processDefinitionKey) {
		repositoryService.suspendProcessDefinitionByKey(processDefinitionKey);
	}

	/**
	 * 根据流定义ID流程定义
	 * 
	 * @return 流程定义实体
	 */
	public ProcessDefinition findProcessDefinitionByProcessDefinitionId(
			String processDefinitionId) {
		ProcessDefinition processDefinition = repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId).singleResult();
		return processDefinition;
	}

	/**
	 * 查询所有流程定义（用于挂起或激活）
	 * 
	 * @return 流程定义List
	 */
	public List<ProcessDefinition> findProcessDefinitions() {
		List<ProcessDefinition> processDefinitionList = repositoryService
				.createProcessDefinitionQuery().orderByProcessDefinitionId()
				.orderByProcessDefinitionVersion().desc().list();
		return processDefinitionList;
	}

	/**
	 * 开始一个审核流程
	 * 
	 * @param userName
	 *            用户名
	 * @param businessKey
	 *            业务任务ID
	 * @param variableMap
	 *            内存变量
	 * @return 流程实例ID
	 * @throws Exception
	 */
	public Map<String, String> startAuditProcess(String userName,
			String businessKey, Map<String, Object> variableMap)
			throws Exception {
		Map<String, String> resultMap = new HashMap<String, String>();
		// 如果有开始用户，则插入
		if (userName != null && !userName.isEmpty())
			identityService.setAuthenticatedUserId(userName);
		// 查询所有已激活流程定义的最后一个版本
		List<ProcessDefinition> pds = repositoryService
				.createProcessDefinitionQuery().active().latestVersion().list();
		// 使用表过式匹配唯一的流程定义
		String processDefinitionKey = auditProcHookService
				.matchingProcessDefinitionByVariables(pds, variableMap);
		if (processDefinitionKey != null) {// 如果找到了匹配
			// 开始流程
			ProcessInstance processInstance = runtimeService
					.startProcessInstanceById(processDefinitionKey,
							businessKey, variableMap);
			if (processInstance == null
					|| processInstance.getProcessDefinitionId() == null)
				throw new Exception("流程未挂接");
			// 定义返回值
			resultMap.put("processDefinitionId",
					processInstance.getProcessDefinitionId());
			resultMap.put("processInstanceId", processInstance.getId());
			return resultMap;
		}
		return null;
	}

	/**
	 * 通过用户组（角色）返回待办未认领任务
	 * 
	 * @param userName
	 *            用户名
	 * @param groupNames
	 *            用户组（角色）名
	 * @param variableValue
	 *            其它流程变量
	 * @param page
	 *            页数 第一页为0
	 * @param collectAudit
	 * @return 返回包含业务ID（bsTaskId）和任务ID（taskId）的List Map
	 */
	public List<Map<String, Object>> findTaskByUserOrGroup(String userName,
			List<String> groupNames, Map<String, Object> variableValue,
			Integer page, CollectAudit collectAudit) {
		Integer size = Integer.parseInt(PropertiesConfig
				.getProperty(PropConstants.AUDIT_LIST_SIZE));
		Integer firstResult = page * size;
		TaskQuery taskQuery = taskService.createTaskQuery()
				.taskCandidateOrAssigned(userName)
				.taskCandidateGroupIn(groupNames).includeProcessVariables()
				.orderByTaskCreateTime().desc();
		this.equalsVV(variableValue, taskQuery);
		if (collectAudit != null)
			this.fullSearchCondition(collectAudit, taskQuery);
		List<Task> tasks = taskQuery.listPage(firstResult, size);
		return this.pack(tasks);
	}

	/**
	 * 添加查询条件
	 * 
	 * @param collectAudit
	 * @param taskQuery
	 */
	private void fullSearchCondition(CollectAudit collectAudit,
			TaskQuery taskQuery) {
		if (collectAudit.getSubmit_time_start() != null)
			taskQuery.processVariableValueGreaterThan("submit_time",
					collectAudit.getSubmit_time_start());

		if (collectAudit.getSubmit_time_end() != null)
			taskQuery.processVariableValueLessThan("submit_time",
					collectAudit.getSubmit_time_end());

		if (collectAudit.getLocation_name() != null
				&& !collectAudit.getLocation_name().trim().equals(""))
			taskQuery.processVariableValueLike("location_name", "%"
					+ collectAudit.getLocation_name() + "%");

		if (collectAudit.getLocation_address() != null
				&& !collectAudit.getLocation_address().trim().equals(""))
			taskQuery.processVariableValueLike("location_address", "%"
					+ collectAudit.getLocation_address() + "%");

		if (collectAudit.getOriginal_task_name() != null
				&& !collectAudit.getOriginal_task_name().trim().equals(""))
			taskQuery.processVariableValueLike("original_task_name", "%"
					+ collectAudit.getOriginal_task_name() + "%");

		if (collectAudit.getCollect_task_name() != null
				&& !collectAudit.getCollect_task_name().trim().equals(""))
			taskQuery.processVariableValueLike("collect_task_name", "%"
					+ collectAudit.getCollect_task_name() + "%");

		if (collectAudit.getUser_name() != null
				&& !collectAudit.getUser_name().trim().equals(""))
			taskQuery.processVariableValueLike("user_name",
					"%" + collectAudit.getUser_name() + "%");
	}

	/**
	 * 添加对比
	 * 
	 * @param variableValue
	 * @param taskQuery
	 */
	private void equalsVV(Map<String, Object> variableValue, TaskQuery taskQuery) {
		if (variableValue != null && !variableValue.isEmpty()) {
			Iterator<String> it = variableValue.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				taskQuery.processVariableValueEquals(key,
						variableValue.get(key));
			}
		}
	}

	/**
	 * 通过用户组（角色）返回待办未认领任务
	 * 
	 * @param userName
	 *            用户名
	 * @param groupName
	 *            用户组（角色）名
	 * @param variableValue
	 *            其它流程变量
	 * @param page
	 *            页数 第一页为0
	 * @return 返回包含业务ID（bsTaskId）和任务ID（taskId）的List Map
	 */
	public List<Map<String, Object>> findTaskByUserOrGroup(
			final String userName, final String groupName,
			Map<String, Object> variableValue, Integer page) {
		return this.findTaskByUserOrGroup(userName, new ArrayList<String>() {
			private static final long serialVersionUID = 8734635788655443400L;

			{
				add(groupName);
			}
		}, variableValue, page, null);
	}

	/**
	 * 通过条件查询待办未认领的任务
	 * 
	 * @param bsType
	 *            业务系统类型
	 * @param bsTaskId
	 *            业务任务ID
	 * @return 流程任务ID组
	 */
	public List<Map<String, Object>> findTasksByBsTaskId(String bsTaskId) {
		List<Task> tasks = taskService.createTaskQuery()
				.includeProcessVariables().processInstanceBusinessKey(bsTaskId)
				.orderByTaskDueDate().asc().list();
		return this.pack(tasks);
	}

	/**
	 * 通过taskId查询任务
	 * 
	 * @param taskId
	 * @return
	 */
	public Map<String, Object> findTaskByTaskId(String taskId) {
		TaskInfo task = taskService.createTaskQuery().includeProcessVariables()
				.taskId(taskId).singleResult();
		if (task == null) {// 如果run中查不到，说明流程可能已经结束，要到历史记录去查
			task = historyService.createHistoricTaskInstanceQuery()
					.includeProcessVariables().taskId(taskId).singleResult();
		}
		if (task != null) {
			return this.packTask(task);
		}
		return null;
	}

	/**
	 * 通过业务KEY获取taskid
	 * 
	 * @param businessKey
	 * @return
	 */
	public String getTaskIdByBusinessKey(String businessKey) {
		Task task = taskService.createTaskQuery()
				.processInstanceBusinessKey(businessKey).singleResult();
		if (task != null)
			return task.getId();
		return null;
	}

	/**
	 * 通过条件查询已认领的任务
	 * 
	 * @param bsTaskId
	 *            业务任务ID
	 * @param userName
	 *            用户名
	 * @param variableValue
	 *            其它流程变量
	 * @return 流程任务ID组
	 */
	public List<Map<String, Object>> findClaimTaskByBsTaskId(long bsTaskId,
			String userName, Map<String, Object> variableValue) {
		TaskQuery taskQuery = taskService.createTaskQuery();
		taskQuery.includeTaskLocalVariables().includeProcessVariables()
				.taskAssignee(userName)
				.processInstanceBusinessKey(String.valueOf(bsTaskId));
		this.equalsVV(variableValue, taskQuery);
		List<Task> tasks = taskQuery.orderByTaskDueDate().asc().list();
		return this.pack(tasks);
	}

	/**
	 * 查询用户的已认领任务
	 * 
	 * @param bsType
	 *            业务系统类型
	 * @param userName
	 *            用户名
	 * @param variableValue
	 *            内存变量
	 * @param collectAudit
	 * @return 流程任务ID组
	 */
	public List<Map<String, Object>> findClaimTask(String userName,
			Integer page, Map<String, Object> variableValue,
			CollectAudit collectAudit) {
		Integer size = Integer.parseInt(PropertiesConfig
				.getProperty(PropConstants.AUDIT_LIST_SIZE));
		Integer firstResult = page * size;
		TaskQuery taskQuery = taskService.createTaskQuery()
				.includeProcessVariables().taskAssignee(userName)
				.orderByTaskDueDate().asc();
		if (collectAudit != null)
			this.fullSearchCondition(collectAudit, taskQuery);
		this.equalsVV(variableValue, taskQuery);
		List<Task> tasks = taskQuery.listPage(firstResult, size);
		return this.pack(tasks);
	}

	/**
	 * 对查询结果进行包装
	 * 
	 * @param tasks
	 * @return List
	 */
	private List<Map<String, Object>> pack(List<Task> tasks) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (Task task : tasks) {
			resultList.add(this.packTask(task));
		}
		return resultList;
	}

	/**
	 * 包装任务结果
	 * 
	 * @param task
	 * @return
	 */
	private Map<String, Object> packTask(TaskInfo task) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> variableMap = task.getProcessVariables();
		resultMap.putAll(variableMap);
		// 查询业务ID
		String bsTaskId = runtimeService.createProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).singleResult()
				.getBusinessKey();
		resultMap.put("bsTaskId", bsTaskId);
		resultMap.put("taskId", task.getId());
		resultMap.put("processDefinitionId", task.getProcessDefinitionId());
		resultMap.put("processInstanceId", task.getProcessInstanceId());
		resultMap.put("executionId", task.getExecutionId());
		resultMap.put("taskDefinitionKey", task.getTaskDefinitionKey());
		resultMap.put("taskName", task.getName());
		return resultMap;
	}

	/**
	 * 认领任务
	 * 
	 * @param taskId
	 *            任务ID（非业务任务ID）
	 * @param userName
	 *            认领该任务的用户名
	 * @param variables
	 *            变量
	 * @throws TaskAlreadyClaimedException
	 *             任务已被其他用户认领
	 * @throws TaskNotFoundException
	 *             任务不存在
	 */
	public void claimTask(String taskId, String userName,
			Map<String, ? extends Object> variables)
			throws TaskAlreadyClaimedException, TaskNotFoundException {
		try {
			taskService.setVariablesLocal(taskId, variables);
			taskService.setVariables(taskId, variables);
			taskService.claim(taskId, userName);
		} catch (ActivitiTaskAlreadyClaimedException e) {
			throw new TaskAlreadyClaimedException("任务已被其他用户认领");
		} catch (ActivitiObjectNotFoundException e) {
			throw new TaskNotFoundException("任务不存在");
		}
	}

	/**
	 * 取消认领任务
	 * 
	 * @param taskId
	 *            任务ID（非业务任务ID）
	 * @throws TaskNotFoundException
	 *             任务不存在
	 */
	public void unclaimTask(String taskId, String... variableNames)
			throws TaskNotFoundException {
		try {
			List<String> list = new ArrayList<String>();
			for (String vname : variableNames)
				list.add(vname);
			taskService.removeVariables(taskId, list);
			taskService.removeVariablesLocal(taskId, list);
			taskService.unclaim(taskId);
		} catch (ActivitiObjectNotFoundException e) {
			throw new TaskNotFoundException("任务不存在");
		}
	}

	/**
	 * 通过任务
	 * 
	 * @param taskId
	 *            任务ID
	 * @param variableMap
	 *            参数（该参数需要用户灵活使用）
	 * @throws TaskNotFoundException
	 *             任务不存在
	 */
	public void complete(String taskId, Map<String, Object> variableMap)
			throws TaskNotFoundException {
		try {
			taskService.setVariablesLocal(taskId, variableMap);
			taskService.complete(taskId, variableMap);
		} catch (ActivitiObjectNotFoundException e) {
			throw new TaskNotFoundException("任务不存在");
		}
	}

	/**
	 * 查询经办任务
	 * 
	 * @param userName
	 *            经办用户的名字
	 * @return 经办任务信息
	 */
	public List<Map<String, Object>> findMyselfInvolvedTask(String userName) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> resultMap = null;
		List<HistoricTaskInstance> taskInsList = historyService
				.createHistoricTaskInstanceQuery().includeProcessVariables()
				.taskAssignee(userName).list();
		for (HistoricTaskInstance historicTaskInstance : taskInsList) {
			resultMap = new HashMap<String, Object>();
			Map<String, Object> variableMap = historicTaskInstance
					.getProcessVariables();
			resultMap.putAll(variableMap);
			resultMap.put("taskId", historicTaskInstance.getId());
			resultMap.put("processInstanceId",
					historicTaskInstance.getProcessInstanceId());
			resultMap.put("executionId", historicTaskInstance.getExecutionId());
			resultList.add(resultMap);
		}
		return resultList;
	}

	/**
	 * 通过流程实例ID查询业务KEY
	 * 
	 * @param processInstanceId
	 *            流程实例ID
	 * @return
	 */
	public String findBusinessKeyByProcessInstanceId(String processInstanceId) {
		String bsTaskId = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult()
				.getBusinessKey();
		return bsTaskId;
	}

	/**
	 * @Description: 设置变量
	 * @author 刘旭升
	 * @date 2015年7月7日 下午2:49:54
	 * @version V1.0
	 * @param executionId
	 *            执行Id
	 * @param variables
	 */
	public void setVariables(String executionId,
			Map<String, ? extends Object> variables) {
		runtimeService.setVariables(executionId, variables);
		runtimeService.setVariablesLocal(executionId, variables);
	}

	/**
	 * 通过流程实例ID查询processVariables
	 * 
	 * @param processInstanceId
	 *            流程实例ID
	 * @return
	 */
	public Map<String, Object> findProcessVariablesByProcessInstanceId(
			String processInstanceId) {
		Map<String, Object> processVariables = runtimeService
				.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult()
				.getProcessVariables();
		return processVariables;
	}

	/**
	 * 添加任务意见
	 * 
	 * @param taskId
	 *            任务ID
	 * @param variableMap
	 *            参数（该参数需要用户灵活使用）
	 * @throws TaskNotFoundException
	 *             任务不存在
	 */
	public void addComment(String taskId, String processInstanceId,
			String userId, String message) throws TaskNotFoundException {
		try {
			identityService.setAuthenticatedUserId(userId);
			taskService.addComment(taskId, processInstanceId, message);// 添加意见
		} catch (ActivitiObjectNotFoundException e) {
			throw new TaskNotFoundException("任务不存在");
		}
	}

	/**
	 * 删除意见
	 * 
	 * @param commentId
	 * @throws TaskNotFoundException
	 */
	public void deleteComment(String commentId) throws TaskNotFoundException {
		try {
			taskService.deleteComment(commentId);
		} catch (ActivitiObjectNotFoundException e) {
			throw new TaskNotFoundException("任务不存在");
		}
	}

	/**
	 * 添加附件
	 * 
	 * @param attachmentType
	 *            附件类型
	 * @param taskId
	 *            任务ID
	 * @param processInstanceId
	 *            线程实例ID
	 * @param userId
	 *            用户ID
	 * @param attachmentName
	 *            附件名称
	 * @param attachmentDescription
	 *            附件描述
	 * @param is
	 *            附件流
	 * @throws TaskNotFoundException
	 *             任务不存在
	 */
	public void addAttachment(String attachmentType, String taskId,
			String processInstanceId, String userId, String attachmentName,
			String attachmentDescription, InputStream is)
			throws TaskNotFoundException {
		try {
			identityService.setAuthenticatedUserId(userId);
			taskService.createAttachment(attachmentType, taskId,
					processInstanceId, attachmentName, attachmentDescription,
					is);// 添加附件
		} catch (ActivitiObjectNotFoundException e) {
			throw new TaskNotFoundException("任务不存在");
		}
	}

	/**
	 * 添加附件
	 * 
	 * @param attachmentType
	 *            附件类型
	 * @param taskId
	 *            任务ID
	 * @param processInstanceId
	 *            线程实例ID
	 * @param userId
	 *            用户ID
	 * @param attachmentName
	 *            附件名称
	 * @param attachmentDescription
	 *            附件描述
	 * @param url
	 *            附件地址
	 * @throws TaskNotFoundException
	 *             任务不存在
	 */
	public void addAttachment(String attachmentType, String taskId,
			String processInstanceId, String userId, String attachmentName,
			String attachmentDescription, String url)
			throws TaskNotFoundException {
		try {
			identityService.setAuthenticatedUserId(userId);
			taskService.createAttachment(attachmentType, taskId,
					processInstanceId, attachmentName, attachmentDescription,
					url);// 添加附件
		} catch (ActivitiObjectNotFoundException e) {
			throw new TaskNotFoundException("任务不存在");
		}
	}

	/**
	 * 删除附件
	 * 
	 * @param attachmentId
	 * @throws TaskNotFoundException
	 */
	public void deleteAttachment(String attachmentId)
			throws TaskNotFoundException {
		try {
			taskService.deleteAttachment(attachmentId);// 添加附件
		} catch (ActivitiObjectNotFoundException e) {
			throw new TaskNotFoundException("任务不存在");
		}
	}

	/**
	 * 获得意见
	 * 
	 * @param taskId
	 * @return
	 * @throws TaskNotFoundException
	 */
	public List<Comment> getComments(String taskId)
			throws TaskNotFoundException {
		try {
			return taskService.getTaskComments(taskId);
		} catch (ActivitiObjectNotFoundException e) {
			throw new TaskNotFoundException("任务不存在");
		}
	}

	/**
	 * 获得附件
	 * 
	 * @param taskId
	 * @return
	 * @throws TaskNotFoundException
	 */
	public List<Attachment> getAttachments(String taskId)
			throws TaskNotFoundException {
		try {
			return taskService.getTaskAttachments(taskId);
		} catch (ActivitiObjectNotFoundException e) {
			throw new TaskNotFoundException("任务不存在");
		}
	}

	/**
	 * 获得附件
	 * 
	 * @param attachmentId
	 * @return
	 * @throws TaskNotFoundException
	 */
	public Attachment getAttachment(String attachmentId)
			throws TaskNotFoundException {
		try {
			return taskService.getAttachment(attachmentId);
		} catch (ActivitiObjectNotFoundException e) {
			throw new TaskNotFoundException("任务不存在");
		}
	}

	/**
	 * 获得附件流
	 * 
	 * @param attachmentId
	 *            附件ID
	 * @return
	 * @throws TaskNotFoundException
	 */
	public InputStream getAttachmentContent(String attachmentId)
			throws TaskNotFoundException {
		try {
			return taskService.getAttachmentContent(attachmentId);
		} catch (ActivitiObjectNotFoundException e) {
			throw new TaskNotFoundException("任务不存在");
		}
	}

	/**
	 * 查询历史记录<br/>
	 * 按开始时间倒序排序
	 * 
	 * @param processInstanceId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws TaskNotFoundException
	 */
	public List<HistoricalRecords> findHistory(String processInstanceId)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, TaskNotFoundException {
		List<HistoricalRecords> hrs = new ArrayList<HistoricalRecords>();
		List<HistoricActivityInstance> list = historyService
				.createHistoricActivityInstanceQuery()
				.processInstanceId(processInstanceId)
				.orderByHistoricActivityInstanceStartTime().asc().list();
		for (HistoricActivityInstance historicActivityInstance : list) {
			HistoricalRecords _hr = new HistoricalRecords();
			_hr.setHistoricActivityInstance(historicActivityInstance);
			_hr.setComments(this.getComments(historicActivityInstance
					.getTaskId()));
			_hr.setAttachments(this.getAttachments(historicActivityInstance
					.getTaskId()));
			hrs.add(_hr);
		}
		return hrs;
	}

	/**
	 * 通用查询记录数
	 * 
	 * @param userName
	 * @param roles
	 * @param variableMap
	 */
	public long findCounts(String userName, List<String> groupNames,
			Map<String, Object> variableMap) {
		TaskQuery taskQuery = taskService.createTaskQuery();
		if (userName != null && !userName.isEmpty()) {
			taskQuery.taskAssignee(userName);
		}
		if (groupNames != null && !groupNames.isEmpty()) {
			taskQuery.taskCandidateGroupIn(groupNames);
		}
		this.equalsVV(variableMap, taskQuery);
		long count = taskQuery.count();
		return count;
	}

	/**
	 * 流程是否已经结束
	 * 
	 * @param processInstanceId
	 *            流程实例ID
	 * @return
	 */
	public boolean isFinished(String processInstanceId) {
		return historyService.createHistoricProcessInstanceQuery().finished()
				.processInstanceId(processInstanceId).count() > 0;
	}

	/**
	 * 流程跟踪图片
	 * 
	 * @param processDefinitionId
	 *            流程定义ID
	 * @param processInstanceId
	 *            流程运行ID
	 * @param out
	 *            输出流
	 * @throws Exception
	 */
	public void processTracking(String processDefinitionId,
			String processInstanceId, OutputStream out) throws Exception {
		// 当前活动节点、活动线
		List<String> activeActivityIds = new ArrayList<String>(), highLightedFlows = new ArrayList<String>();

		/**
		 * 获得当前活动的节点
		 */
		if (this.isFinished(processInstanceId)) {// 如果流程已经结束，则得到结束节点
			activeActivityIds.add(historyService
					.createHistoricActivityInstanceQuery()
					.processInstanceId(processInstanceId)
					.activityType("endEvent").singleResult().getActivityId());
		} else {// 如果流程没有结束，则取当前活动节点
			// 根据流程实例ID获得当前处于活动状态的ActivityId合集
			activeActivityIds = runtimeService
					.getActiveActivityIds(processInstanceId);
		}
		/**
		 * 获得当前活动的节点-结束
		 */

		/**
		 * 获得活动的线
		 */
		// 获得历史活动记录实体（通过启动时间正序排序，不然有的线可以绘制不出来）
		List<HistoricActivityInstance> historicActivityInstances = historyService
				.createHistoricActivityInstanceQuery()
				.processInstanceId(processInstanceId)
				.orderByHistoricActivityInstanceStartTime().asc().list();
		// 计算活动线
		highLightedFlows = this
				.getHighLightedFlows(
						(ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
								.getDeployedProcessDefinition(processDefinitionId),
						historicActivityInstances);
		/**
		 * 获得活动的线-结束
		 */

		/**
		 * 绘制图形
		 */
		if (null != activeActivityIds && null != highLightedFlows) {
			InputStream imageStream = null;
			try {
				// 获得流程引擎配置
				ProcessEngineConfiguration processEngineConfiguration = processEngine
						.getProcessEngineConfiguration();
				// 根据流程定义ID获得BpmnModel
				BpmnModel bpmnModel = repositoryService
						.getBpmnModel(processDefinitionId);
				// 输出资源内容到相应对象
				imageStream = new DefaultProcessDiagramGenerator()
						.generateDiagram(bpmnModel, "png", activeActivityIds,
								highLightedFlows, processEngineConfiguration
										.getActivityFontName(),
								processEngineConfiguration.getLabelFontName(),
								processEngineConfiguration.getClassLoader(),
								1.0);
				IOUtils.copy(imageStream, out);
			} finally {
				IOUtils.closeQuietly(imageStream);
			}
		}
	}

	/**
	 * 获得高亮线
	 * 
	 * @param processDefinitionEntity
	 *            流程定义实体
	 * @param historicActivityInstances
	 *            历史活动实体
	 * @return 线ID集合
	 */
	public List<String> getHighLightedFlows(
			ProcessDefinitionEntity processDefinitionEntity,
			List<HistoricActivityInstance> historicActivityInstances) {

		List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId
		for (int i = 0; i < historicActivityInstances.size(); i++) {// 对历史流程节点进行遍历
			ActivityImpl activityImpl = processDefinitionEntity
					.findActivity(historicActivityInstances.get(i)
							.getActivityId());// 得 到节点定义的详细信息
			List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();// 用以保存后需开始时间相同的节点
			if ((i + 1) >= historicActivityInstances.size()) {
				break;
			}
			ActivityImpl sameActivityImpl1 = processDefinitionEntity
					.findActivity(historicActivityInstances.get(i + 1)
							.getActivityId());// 将后面第一个节点放在时间相同节点的集合里
			sameStartTimeNodes.add(sameActivityImpl1);
			for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
				HistoricActivityInstance activityImpl1 = historicActivityInstances
						.get(j);// 后续第一个节点
				HistoricActivityInstance activityImpl2 = historicActivityInstances
						.get(j + 1);// 后续第二个节点
				if (activityImpl1.getStartTime().equals(
						activityImpl2.getStartTime())) {// 如果第一个节点和第二个节点开始时间相同保存
					ActivityImpl sameActivityImpl2 = processDefinitionEntity
							.findActivity(activityImpl2.getActivityId());
					sameStartTimeNodes.add(sameActivityImpl2);
				} else {// 有不相同跳出循环
					break;
				}
			}
			List<PvmTransition> pvmTransitions = activityImpl
					.getOutgoingTransitions();// 取出节点的所有出去的线
			for (PvmTransition pvmTransition : pvmTransitions) {// 对所有的线进行遍历
				ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition
						.getDestination();// 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
				if (sameStartTimeNodes.contains(pvmActivityImpl)) {
					highFlows.add(pvmTransition.getId());
				}
			}
		}
		return highFlows;
	}

	@Autowired
	private ProcessEngine processEngine;// 引擎
	@Autowired
	private RepositoryService repositoryService;// 仓库服务
	@Autowired
	private RuntimeService runtimeService;// 运行时服务
	@Autowired
	private TaskService taskService;// 任务服务
	@Autowired
	private HistoryService historyService;// 历史服务
	@Autowired
	private ManagementService managementService;// 管理服务
	@Autowired
	private IdentityService identityService;// ID服务
	@Autowired
	private AuditProcHookService auditProcHookService;// 流程挂接用户

	// private static final String processDefinitionKey = "AuditProcess";
}
