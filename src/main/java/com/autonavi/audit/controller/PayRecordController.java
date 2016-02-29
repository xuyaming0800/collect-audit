package com.autonavi.audit.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.autonavi.audit.entity.CollectAuditPayRecord;
import com.autonavi.audit.entity.Pagination;
import com.autonavi.audit.entity.ResultEntity;
import com.autonavi.audit.service.PayRecordService;
import com.autonavi.audit.util.SysErrorUtil;

/**
 * @Title: PayRecordController.java
 * @Package com.autonavi.audit.controller
 * @Description: 支付记录管理
 * @author xusheng.liu
 * @date 2015年10月29日 下午6:01:55
 * @version V1.0
 */
@Controller
@RequestMapping("/pay/record")
public class PayRecordController {

	@Autowired
	private PayRecordService payRecordService;
	private Logger logger = LogManager.getLogger(getClass());

	/**
	 * @Description: 支付记录列表
	 * @author xusheng.liu
	 * @date 2015年10月29日 下午6:01:35
	 * @version V1.0
	 * @return
	 */
	@RequestMapping("/list")
	public String list() {
		return "pay_record/list";
	}

	/**
	 * @Description: 支付记录查询
	 * @author xusheng.liu
	 * @date 2015年10月29日 下午6:06:29
	 * @version V1.0
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/query")
	public ResultEntity query(@RequestParam("pageNo") Integer pageNo,
			@RequestParam("pageSize") Integer pageSize,
			CollectAuditPayRecord capr) {
		logger.info("-->进入query查询支付记录方法");
		logger.info("---->入参：pageNo=" + pageNo + "；pageSize=" + pageSize
				+ "；capr=" + capr);
		ResultEntity resultEntity = new ResultEntity();
		try {
			Pagination pagination = payRecordService.query(pageNo, pageSize,
					capr);
			logger.info("-->查询支付记录结果=" + pagination);
			resultEntity.setSuccess(true);
			resultEntity.setInfo(pagination);
			logger.exit(resultEntity);
			return resultEntity;
		} catch (Exception e) {
			logger.error(e);
			resultEntity.setSuccess(false);
			resultEntity.setDesc(SysErrorUtil.SYS_ERROR_INFO);
			return resultEntity;
		}
	}

	/**
	 * @Description: 调用支付接口
	 * @author xusheng.liu
	 * @date 2015年10月30日 下午5:11:53
	 * @version V1.0
	 * @param pageNo
	 * @param pageSize
	 * @param capr
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/doPay")
	public ResultEntity doPay(@RequestParam("id") String id,
			@RequestParam("status") String status) {
		logger.info("-->支付调用");
		ResultEntity resultEntity = new ResultEntity();
		try {
			if ("1".equals(status)) {
				logger.info("---->已支付不能再次支付，方法执行结束");
				resultEntity.setSuccess(false);
				resultEntity.setDesc("已支付不能再次支付");
				return resultEntity;
			}
			ResultEntity doPay = payRecordService.doPay(id);
			logger.info("-->支付结束");
			resultEntity.setSuccess(true);
			resultEntity.setInfo(doPay);
			logger.exit(resultEntity);
			return resultEntity;
		} catch (Exception e) {
			logger.error(e);
			resultEntity.setSuccess(false);
			resultEntity.setDesc(SysErrorUtil.SYS_ERROR_INFO);
			return resultEntity;
		}
	}
}
