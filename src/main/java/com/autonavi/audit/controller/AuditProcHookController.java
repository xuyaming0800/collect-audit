package com.autonavi.audit.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.autonavi.audit.entity.CollectAuditProchook;
import com.autonavi.audit.entity.ProcessDefinition;
import com.autonavi.audit.exception.TaskNotFoundException;
import com.autonavi.audit.service.AuditProcHookService;
import com.autonavi.audit.workflow.AuditProcess;

@Controller
@RequestMapping("/procHook")
public class AuditProcHookController {

	/**
	 * 主页
	 * 
	 * @return
	 */
	@RequestMapping("/list")
	public String list() {
		return "proc_hook/proc_hook";
	}

	/**
	 * 查询所有流程挂接信息
	 * 
	 * @return
	 */
	@RequestMapping("/findAll")
	public @ResponseBody List<CollectAuditProchook> findAll() {
		return logger.exit(auditProcHookService.findAll());
	}

	/**
	 * 查询流程定义明细
	 * 
	 * @param processDefinitionId
	 *            流程定义ID
	 * @return 流程定义明细
	 */
	@RequestMapping("/detail")
	public @ResponseBody ProcessDefinition detail(
			@RequestParam("processDefinitionId") String processDefinitionId) {
		return logger
				.exit(auditProcHookService
						.findProcessDefinitionByProcessDefinitionId(processDefinitionId));
	}

	/**
	 * 部署
	 * 
	 * @param resourceName
	 * @param text
	 * @return
	 */
	@RequestMapping("/doDeploy")
	public @ResponseBody String doDeploy(
			@RequestParam("resourceName") String resourceName,
			@RequestParam("text") String text,
			@RequestParam("expression") String expression) {
		String deploymentId = auditProcHookService.doDeploy(resourceName, text,
				expression);
		return deploymentId;
	}

	/**
	 * 删除部署
	 * 
	 * @param processDefinitionId
	 * @return
	 */
	@RequestMapping("/deleteDeployment")
	public @ResponseBody String deleteDeployment(
			@RequestParam("processDefinitionId") String processDefinitionId) {
		auditProcHookService.deleteDeployment(processDefinitionId);
		return "删除部署成功！";
	}

	/**
	 * 显示流程图
	 * 
	 * @param processDefinitionId
	 * @param respone
	 */
	@RequestMapping("/processDiagram.png")
	public void getProcessDiagram(
			@RequestParam("processDefinitionId") String processDefinitionId,
			HttpServletResponse respone) {
		OutputStream out = null;
		try {
			out = respone.getOutputStream();
			auditProcHookService.getProcessDiagram(processDefinitionId, out);
			out.flush();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * 激活流程定义
	 * 
	 * @param processDefinitionId
	 */
	@RequestMapping("/activateProcessDefinition")
	@ResponseBody
	public void activateProcessDefinition(
			@RequestParam("processDefinitionId") String processDefinitionId) {
		auditProcess.activateProcessDefinitionById(processDefinitionId);
	}

	/**
	 * 挂起流程定义
	 * 
	 * @param processDefinitionId
	 */
	@RequestMapping("/suspendProcessDefinition")
	@ResponseBody
	public void suspendProcessDefinition(
			@RequestParam("processDefinitionId") String processDefinitionId) {
		auditProcess.suspendProcessDefinitionById(processDefinitionId);
	}

	/**
	 * 下载流程定义文件
	 * 
	 * @param deploymentId
	 *            部署ID
	 * @param resourceName
	 *            资源名称
	 * @param respone
	 *            HttpServletResponse对象
	 */
	@RequestMapping("/downloadProcessDefinitionFile")
	public void downloadProcessDefinitionFile(
			@RequestParam("deploymentId") String deploymentId,
			@RequestParam("resourceName") String resourceName,
			HttpServletResponse respone) {
		OutputStream out = null;
		try {
			out = respone.getOutputStream();
			IOUtils.copy(
					auditProcess.getProcessXml(deploymentId, resourceName), out);
			out.flush();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * 修改表达式
	 * 
	 * @param processDefinitionId
	 * @param expression
	 */
	@RequestMapping("/updateExpression")
	@ResponseBody
	public void updateExpression(
			@RequestParam("processDefinitionId") String processDefinitionId,
			@RequestParam("expression") String expression) {
		auditProcHookService.updateExpression(processDefinitionId, expression);
	}

	@Autowired
	AuditProcHookService auditProcHookService = null;
	@Autowired
	AuditProcess auditProcess = null;
	private Logger logger = LogManager.getLogger(getClass());
}
