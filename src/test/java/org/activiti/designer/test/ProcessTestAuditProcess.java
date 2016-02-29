package org.activiti.designer.test;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.TaskQuery;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

public class ProcessTestAuditProcess {

	private String filename = "D:/workspace/AutoNaviWorkspace/collect-audit-web Maven Webapp/src/main/resources/dev/deployments/MyProcess.bpmn";

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	@Test
	public void startProcess() throws Exception {
		RepositoryService repositoryService = activitiRule.getRepositoryService();
		repositoryService.createDeployment().addInputStream("AuditProcess.bpmn20.xml",
				new FileInputStream(filename)).deploy();
		RuntimeService runtimeService = activitiRule.getRuntimeService();
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("AuditProcess");
		
		TaskService taskService = activitiRule.getTaskService();
		TaskQuery taskQuery = taskService.createTaskQuery();
		
		//初审
		String taskId = taskQuery.processInstanceId(processInstance.getId()).singleResult().getId();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("bsType", 1);
		variableMap.put("flag", true);
		taskService.complete(taskId, variableMap);
		System.out.println("当前节点："+taskQuery.processInstanceId(processInstance.getId()).singleResult().getName());
		
		//抽检
		variableMap.put("bmpStatus", 3);
		taskId = taskQuery.processInstanceId(processInstance.getId()).singleResult().getId();
		taskService.complete(taskId, variableMap);
		System.out.println("当前节点："+taskQuery.processInstanceId(processInstance.getId()).singleResult().getName());

		taskId = taskQuery.processInstanceId(processInstance.getId()).singleResult().getId();
		taskService.complete(taskId, variableMap);
		System.out.println("当前节点："+taskQuery.processInstanceId(processInstance.getId()).singleResult().getName());
		
		taskId = taskQuery.processInstanceId(processInstance.getId()).singleResult().getId();
		taskService.complete(taskId, variableMap);
		System.out.println("当前节点："+taskQuery.processInstanceId(processInstance.getId()).singleResult().getName());
		
		//申诉
		taskId = taskQuery.processInstanceId(processInstance.getId()).singleResult().getId();
		variableMap.put("bmpStatus", 4);
		taskService.complete(taskId, variableMap);
		System.out.println("当前节点："+taskQuery.processInstanceId(processInstance.getId()).singleResult().getName());
		
		//结束
		taskId = taskQuery.processInstanceId(processInstance.getId()).singleResult().getId();
		taskService.complete(taskId, variableMap);
		System.out.println("当前节点："+taskQuery.processInstanceId(processInstance.getId()).singleResult().getName());
	}
}