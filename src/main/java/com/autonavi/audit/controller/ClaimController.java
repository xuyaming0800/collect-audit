package com.autonavi.audit.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.autonavi.audit.entity.CollectAuditClaim;
import com.autonavi.audit.entity.Pagination;
import com.autonavi.audit.entity.ResultEntity;
import com.autonavi.audit.service.ClaimService;
import com.autonavi.audit.service.Config;
import com.autonavi.audit.util.SysErrorUtil;

/**
 * @Title: ClaimController.java
 * @Package com.autonavi.audit.controller
 * @Description: 认领管理
 * @author xusheng.liu
 * @date 2015年11月16日 下午3:08:15
 * @version V1.0
 */
@Controller
@RequestMapping("/claim")
public class ClaimController {

	@Autowired
	private ClaimService claimService;
	private Logger logger = LogManager.getLogger(getClass());

	/**
	 * @Description: 任务认领列表
	 * @author xusheng.liu
	 * @date 2015年11月16日 下午3:42:24
	 * @version V1.0
	 * @return
	 */
	@RequestMapping("/list")
	public String list() {
		return "claim/list";
	}

	/**
	 * @Description: 获取客户列表
	 * @author xusheng.liu
	 * @date 2015年11月16日 下午5:48:00
	 * @version V1.0
	 * @param customName
	 *            和customName相关的用户名
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getCustoms")
	public ResultEntity getCustoms(@RequestParam("customName") String customName) {
		logger.info("-->进入getCustoms查询客户信息方法");
		logger.info("---->入参：customName=" + customName);
		String userType = "3";// 客户类型为3
		ResultEntity re = new ResultEntity();
		try {
			List<Object> customIdList = claimService.getCustomIdList(
					customName, userType, Config.get_custom_url+"&userType="+userType+"&accountType=3");
			if (customIdList != null) {
				re.setInfo(customIdList);
			}
			re.setSuccess(true);
			re.setDesc("查询成功");
			logger.info("-->getCustoms查询成功");
		} catch (Exception e) {
			re.setSuccess(false);
			re.setDesc(SysErrorUtil.SYS_ERROR_INFO);
			logger.error("getCustoms获取客户信息失败", e);
		}
		return re;
	}

	@ResponseBody
	@RequestMapping("/getClaimUsers")
	public ResultEntity getClaimUsers(@RequestParam("customName") String customName) {
		logger.info("-->进入getClaimUsers查询客户信息方法");
		logger.info("---->入参：customName=" + customName);
		String userType = "2";
		ResultEntity re = new ResultEntity();
		try {
			List<Object> customIdList = claimService.getCustomIdList(
					customName, userType, Config.get_users_url+"&userType="+userType);
			if (customIdList != null) {
				re.setInfo(customIdList);
			}
			re.setSuccess(true);
			re.setDesc("查询成功");
			logger.info("-->getClaimUsers查询成功");
		} catch (Exception e) {
			re.setSuccess(false);
			re.setDesc(SysErrorUtil.SYS_ERROR_INFO);
			logger.error("getClaimUsers获取客户信息失败", e);
		}
		return re;
	}
	/**
	 * @Description: 根据客户id，查询项目列表
	 * @author xusheng.liu
	 * @date 2015年11月17日 上午9:38:28
	 * @version V1.0
	 * @param customId
	 *            客户id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getProjects")
	public ResultEntity getProjects(@RequestParam("customId") String customId) {
		logger.info("-->进入getProjects查询项目信息");
		logger.info("---->入参：customId=" + customId);
		ResultEntity re = new ResultEntity();
		try {
			re = claimService.getProjects(customId);
			logger.info("---->查询返回结果：" + re);
		} catch (Exception e) {
			re.setSuccess(false);
			re.setDesc(SysErrorUtil.SYS_ERROR_INFO);
			logger.error("getCustoms获取客户信息失败", e);
		}
		return re;
	}

	@ResponseBody
	@RequestMapping("/add")
	public ResultEntity add(CollectAuditClaim collectAuditClaim) {
		ResultEntity re = new ResultEntity();
		logger.info("-->进入保存add方法");
		logger.info("---->入参：collectAuditClaim=" + collectAuditClaim);
		try {
			return this.claimService.addCollectAuditClaim(collectAuditClaim);
		} catch (Exception e) {
			re.setSuccess(false);
			re.setDesc(SysErrorUtil.SYS_ERROR_INFO);
			logger.error("add保存失败", e);
		}
		return re;
	}
	
	@ResponseBody
	@RequestMapping("/del")
	public ResultEntity del(CollectAuditClaim collectAuditClaim) {
		ResultEntity re = new ResultEntity();
		logger.info("-->进入删除del方法");
		logger.info("---->入参：collectAuditClaim=" + collectAuditClaim);
		try {
			return this.claimService.delCollectAuditClaim(collectAuditClaim);
		} catch (Exception e) {
			re.setSuccess(false);
			re.setDesc(SysErrorUtil.SYS_ERROR_INFO);
			logger.error("del删除失败", e);
		}
		return re;
	}

	@ResponseBody
	@RequestMapping("/queryClaimList")
	public ResultEntity queryClaimList(@RequestParam("pageNo") Integer pageNo,
			@RequestParam("pageSize") Integer pageSize,
			CollectAuditClaim collectAuditClaim) {
		ResultEntity re = new ResultEntity();
		logger.info("-->进入查询queryClaimList方法");
		logger.info("---->入参：pageNo=" + pageNo + "；pageSize=" + pageSize
				+ "；collectAuditClaim=" + collectAuditClaim);
		try {
			Pagination pagination = this.claimService.queryCollectAuditClaim(pageNo, pageSize,
					collectAuditClaim);
			logger.info("-->查询支付记录结果=" + pagination);
			re.setSuccess(true);
			re.setInfo(pagination);
			logger.exit(re);
			return re;
		} catch (Exception e) {
			logger.error(e);
			re.setSuccess(false);
			re.setDesc(SysErrorUtil.SYS_ERROR_INFO);
			return re;
		}
	}
}
