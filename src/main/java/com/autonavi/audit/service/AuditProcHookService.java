package com.autonavi.audit.service;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import ognl.OgnlException;

import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.autonavi.audit.dao.AuditProcHookDao;
import com.autonavi.audit.entity.CollectAuditProchook;
import com.autonavi.audit.exception.TaskNotFoundException;
import com.autonavi.audit.util.OgnlUtils;
import com.autonavi.audit.workflow.AuditProcess;

@Service
public class AuditProcHookService {

	/**
	 * 使用表过式匹配唯一的流程定义
	 * 
	 * @param pds
	 * @param variables
	 * @return 流程定义ID
	 */
	public String matchingProcessDefinitionByVariables(
			List<ProcessDefinition> pds, Map<String, Object> variables) {
		List<CollectAuditProchook> prochooks = auditProcHookDao
				.selectExpression(pds);
		for (CollectAuditProchook prochook : prochooks) {
			try {
				String expression = prochook.getExpression();
				boolean isMatching = (Boolean) OgnlUtils.getValue(expression,
						variables);
				if (isMatching)// 如果找到匹配的，直接返回
					return prochook.getProc_def_id();
			} catch (OgnlException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return null;
	}

	/**
	 * 查询所有部署
	 * 
	 * @return
	 */
	public List<CollectAuditProchook> findAll() {
		List<CollectAuditProchook> prochookList = auditProcHookDao.findAll();
		return prochookList;
	}

	/**
	 * 根据流程实例ID查询流程定义
	 * 
	 * @param processDefinitionId
	 * @return
	 */
	public com.autonavi.audit.entity.ProcessDefinition findProcessDefinitionByProcessDefinitionId(
			String processDefinitionId) {
		ProcessDefinition processDefinition = auditProcess
				.findProcessDefinitionByProcessDefinitionId(processDefinitionId);
		com.autonavi.audit.entity.ProcessDefinition myProcessDefinition = new com.autonavi.audit.entity.ProcessDefinition();
		try {
			PropertyUtils
					.copyProperties(myProcessDefinition, processDefinition);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return myProcessDefinition;
	}

	/**
	 * 部署
	 * 
	 * @param resourceName
	 *            资源名称
	 * @param text
	 *            xml文本
	 * @param expression
	 *            表达式
	 * @return
	 */
	public String doDeploy(String resourceName, String text, String expression) {
		String deploymentId = auditProcess.deploy(resourceName, text);// 部署，得到部署ID
		String processDefinitionId = auditProcess
				.getProcessDefinitionIdByDeploymentId(deploymentId);// 通过部署ID查询流程定义ID
		String userName = (String) SecurityUtils.getSubject().getSession()
				.getAttribute("userName");// 从session中得到用户名
		auditProcHookDao.insert(processDefinitionId, expression, userName);// 插入信息表
		return deploymentId;
	}

	/**
	 * 删除部署
	 * 
	 * @param processDefinitionId
	 *            流程定义ID
	 */
	public void deleteDeployment(String processDefinitionId) {
		ProcessDefinition processDefinition = auditProcess
				.findProcessDefinitionByProcessDefinitionId(processDefinitionId);
		auditProcHookDao.delete(processDefinitionId);// 删除信息表
		auditProcess.deleteDeployment(processDefinition.getDeploymentId());// 删除流程表
	}

	/**
	 * 获取流程图片
	 * 
	 * @param processDefinitionId
	 *            流程定义ID
	 * @param out
	 *            OutputStream
	 * @throws IOException
	 *             如果操作流失败
	 */
	public void getProcessDiagram(String processDefinitionId, OutputStream out)
			throws IOException {
		IOUtils.copy(auditProcess.getProcessDiagram(processDefinitionId), out);
	}

	/**
	 * 更新表达式
	 * 
	 * @param processDefinitionId
	 *            流程定义ID
	 * @param expression
	 *            表达式
	 */
	public void updateExpression(String processDefinitionId, String expression) {
		auditProcHookDao.updateExpression(processDefinitionId, expression);
	}

	@Autowired
	private AuditProcHookDao auditProcHookDao;// 流程挂接DAO
	@Autowired
	AuditProcess auditProcess = null;
	private Logger logger = LogManager.getLogger(getClass());
}
