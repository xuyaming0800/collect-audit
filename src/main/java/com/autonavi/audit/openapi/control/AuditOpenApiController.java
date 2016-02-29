package com.autonavi.audit.openapi.control;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.autonavi.audit.entity.ExportTask;
import com.autonavi.audit.entity.ResultEntity;
import com.autonavi.audit.service.AuditService;

/**
 * @Title: UserRegistOpenApiController.java
 * @Package com.autonavi.audit.openapi.control
 * @Description: 审核对外接口
 * @author xusheng.liu
 * @date 2015年11月23日 上午11:39:00
 * @version V1.0
 */
@Controller
public class AuditOpenApiController {
	private Logger logger = LogManager.getLogger(getClass());
	@Autowired
	AuditService auditService;

	/**
	 * @Description: 获取审核信息，客户中心下载中心,（提交时间参数为yyyy-MM-dd HH:mm:ss类型）
	 * @author xusheng.liu
	 * @date 2015年11月23日 上午11:38:48
	 * @version V1.0
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/openapi/audit", params = { "serviceid=809000" })
	public @ResponseBody ResultEntity getInfo(HttpServletRequest request) {
		logger.info("-->进入审核[客户中心下载中心]接口");
		ResultEntity result = new ResultEntity();
		try {
			String systemId = request.getParameter("systemId");
			String submit_time_start = request
					.getParameter("submit_time_start");
			String submit_time_end = request.getParameter("submit_time_end");
			logger.info("-->参数为：systemId=" + systemId + ";submit_time_start="
					+ submit_time_start + ";submit_time_end=" + submit_time_end);
			if (systemId != null && !"".equals(systemId)
					|| submit_time_start != null
					&& !"".equals(submit_time_start) || submit_time_end != null
					&& !"".equals(submit_time_end)) {
				logger.info("---->参数完整");
				List<ExportTask> infoList = auditService.findInfoBySysId(
						systemId, submit_time_start,
						submit_time_end);
				result.setOtherInfo(auditService.getPropertiseNames(systemId));
				result.setInfo(infoList);
				result.setSuccess(true);
				result.setDesc("查询成功");
			} else {
				logger.info("-->参数不完整，查询失败");
				result.setDesc("必填参数不完整，查询失败");
			}
		} catch (Exception e) {
			logger.info("-->系统发生错误，查询失败。 error：" + e);
			result.setDesc("查询失败!" + e);
		}
		return result;
	}
}
