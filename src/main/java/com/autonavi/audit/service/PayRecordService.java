package com.autonavi.audit.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autonavi.audit.constant.PayStatusConstant;
import com.autonavi.audit.dao.AuditAttrDaoForMongoDB;
import com.autonavi.audit.dao.AuditPayRecordDao;
import com.autonavi.audit.entity.CollectAuditPayRecord;
import com.autonavi.audit.entity.Pagination;
import com.autonavi.audit.entity.ResultEntity;
import com.autonavi.audit.util.HttpClientUtil;
import com.autonavi.audit.util.JsonBinder;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * 审核系统配置管理
 * @author chunsheng.zhang
 *
 */
@Service
public class PayRecordService {

	@Autowired
	private AuditPayRecordDao auditPayRecordDao;
	@Autowired
	private AuditAttrDaoForMongoDB auditAttrDaoForMongoDB;
	
	private Logger logger = LogManager.getLogger(getClass());
	
	/**
	 * @Description: 查询
	 * @author xusheng.liu
	 * @date 2015年10月29日 下午6:09:16 
	 * @version V1.0 
	 * @param pageSize 
	 * @param pageNo 
	 * @param capr 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public Pagination query(Integer pageNo, Integer pageSize,
			CollectAuditPayRecord capr) throws Exception {
		logger.info("-->执行方法query");
		Pagination pagination = null;
		if(pageNo!=null && pageSize != null){
			logger.info("---->必须参数不为空");
			pagination = new Pagination(pageNo,pageSize);
			List<CollectAuditPayRecord> list = auditPayRecordDao.query(pagination.getStart(),pageSize,capr);
			logger.info("---->查询获取结果大小："+list.size());
			long total = auditPayRecordDao.queryCount(capr);
			logger.info("---->该条件下的总数为："+total);
			pagination.setTotalCount(total);
			pagination.setObjectList(list);
		}
		logger.info("-->query查询结束");
		return pagination;
	}

	/**
	 * @Description: 执行支付
	 * @author xusheng.liu
	 * @date 2015年10月30日 下午5:25:08 
	 * @version V1.0 
	 * @param bsTaskId
	 * @return 
	 * @throws IOException
	 */
	public ResultEntity doPay(String id) throws IOException {
		logger.info("-->执行支付接口调用");
		logger.info("-->记录id==" + id);
		CollectAuditPayRecord record = this.auditPayRecordDao.queryAuditPayRecordByPrimaryKey(id);
		logger.info("-->查询支付记录对象为：" + record);
		if(record!=null){
			Map<?, ?> map = auditAttrDaoForMongoDB.getAllAttr(record.getTaskId());
			String content = packageContentForPay(record.getTaskId(), map);
			Map<String, String> mapForContent = new HashMap<String, String>();
			mapForContent.put("content", content);
			String json = HttpClientUtil.post(Config.audit_comment_pay_url,
					mapForContent, "utf-8");
			logger.info("---->支付远程接口返回结果:json==" + json);
			if (!StringUtils.isBlank(json)) {
				ObjectMapper objectMapper = new ObjectMapper();
				ResultEntity resultEntity = objectMapper.readValue(json,
						ResultEntity.class);
				logger.info("---->支付返回结果:resultEntity==" + resultEntity);
				if (resultEntity != null) {// 支付成功
					CollectAuditPayRecord auditPayRecord = new CollectAuditPayRecord();
					auditPayRecord.setContent(JsonBinder.buildNonDefaultBinder().toJson(map));
					auditPayRecord.setTaskId(record.getTaskId());
					if (resultEntity.isSuccess())
						auditPayRecord.setStatus(PayStatusConstant.paySuccess);
					else
						auditPayRecord.setStatus(PayStatusConstant.payFall);
					logger.info("---->封装支付记录对象==" + auditPayRecord);
					logger.info("---->支付接口调用结束");
					this.auditPayRecordDao
					.updateCollectAuditPayRecord(auditPayRecord);
					return resultEntity;
				}
			}
		}
		return null;
	}

	/**
	 * @Description: 封装接口参数
	 * @author xusheng.liu
	 * @date 2015年10月30日 下午5:21:05 
	 * @version V1.0 
	 * @param bsTaskId
	 * @param map
	 * @return
	 */
	private String packageContentForPay(String bsTaskId, Map<?, ?> map) {
		String content = "";
		if (map != null && map.containsKey("attrs")) {
			@SuppressWarnings("unchecked")
			Map<String, ?> attrs = (Map<String, ?>) map.get("attrs");
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			for (Entry<String, ?> entry : attrs.entrySet()) {
				@SuppressWarnings("rawtypes")
				Map m = (Map) entry.getValue();
				String ownerId = (String) m.get("ownerId");
				String collectClassParentId = (String) m
						.get("collectClassParentId");
				String collectClassId = (String) m.get("collectClassId");
				String userMoney = (String) m.get("userMoney");
				sb.append("{\"ownerId\":\"" + ownerId + "\"");
				sb.append(",\"collectClassParentId\":\"" + collectClassParentId
						+ "\"");
				sb.append(",\"collectClassId\":\"" + collectClassId + "\"");
				sb.append(",\"userMoney\":\"" + userMoney + "\"");
				sb.append(",\"taskId\":\"" + bsTaskId + "\"},");
			}
			content = sb.substring(0, sb.length() - 1) + "]";
		}
		return content;
	}
}
