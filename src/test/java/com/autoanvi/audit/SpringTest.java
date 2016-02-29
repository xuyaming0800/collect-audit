package com.autoanvi.audit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class SpringTest {

	@Autowired
	private ProcessEngine processEngine;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private ManagementService managementService;

	private String deploymentId;

	@Test
	public void test() {
		try {
			Map<String, Object> variableMap = new HashMap<String, Object>();
			variableMap.put("bsType", 1);
			ProcessInstance processInstance = runtimeService
					.startProcessInstanceByKey("AuditProcess", variableMap);
			System.out.println("id " + processInstance.getId() + " "
					+ processInstance.getProcessDefinitionId());
			List<Task> tasks = taskService.createTaskQuery()
					.taskCandidateGroup("a").list();
			for (Task task : tasks) {
				System.out
						.println("a用户组任务名: "
								+ task.getName());
				// claim it
				taskService.claim(task.getId(), "jia.miao");
			}
			// Verify Fozzie can now retrieve the task
			tasks = taskService.createTaskQuery().taskAssignee("jia.miao")
					.list();
			for (Task task : tasks) {
				System.out.println("Task for jia.miao: " + task.getName());
				Map<String, Object> variableMap2 = new HashMap<String, Object>();
				variableMap2.put("beConfirmed", false);
				// Complete the task
				taskService.complete(task.getId(),variableMap2);
			}
			System.out.println("Number of tasks for fozzie: "
					+ taskService.createTaskQuery().taskAssignee("jia.miao")
							.count());
			// Retrieve and claim the second task
			tasks = taskService.createTaskQuery().taskCandidateGroup("b")
					.list();
			for (Task task : tasks) {
				System.out
						.println("b用户组任务名:"
								+ task.getName());
				taskService.claim(task.getId(), "kermit");
			}
			// Completing the second task ends the process
			for (Task task : tasks) {
				Map<String, Object> variableMap3 = new HashMap<String, Object>();
				variableMap3.put("beConfirmed", true);
				taskService.complete(task.getId(), variableMap3);
			}
			tasks = taskService.createTaskQuery().taskCandidateGroup("c")
					.list();
			for (Task task : tasks) {
				System.out
						.println("c用户组任务名:"
								+ task.getName());
				taskService.claim(task.getId(), "daisy");
			}
			// Completing the second task ends the process
			for (Task task : tasks) {
				taskService.complete(task.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
