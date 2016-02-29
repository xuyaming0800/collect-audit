package com.autonavi.audit.service.support;

import java.util.List;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.autonavi.audit.base.spring.ApplicationContextUtils;
import com.autonavi.audit.constant.AuditConstant;
import com.autonavi.audit.entity.CollectAuditClaim;
import com.autonavi.audit.service.AuditService;
import com.autonavi.audit.util.PropertiesConfig;

/**
 * @Title: setClaimExecutionListener.java
 * @Package com.autonavi.audit.service.support
 * @Description: 认领任务
 * @author 刘旭升
 * @date 2015年8月25日 上午9:44:33
 * @version V1.0
 */
public class SetClaimTaskListener implements TaskListener {

	private static final long serialVersionUID = 2273190656587545865L;
	private Logger logger = LogManager.getLogger(getClass());
	// 从Spring上下文中获取实例
	private TaskService taskService = ApplicationContextUtils
			.getApplicationContext().getBean(TaskService.class);
	private JedisPool jedisPool = ApplicationContextUtils
			.getApplicationContext().getBean(JedisPool.class);
	private AuditService auditService = ApplicationContextUtils
			.getApplicationContext().getBean(AuditService.class);

	@Override
	public void notify(DelegateTask delegateTask) {
		Object obj = delegateTask.getVariable("bsType");
		String sysType = (String) obj;
		if(!AuditConstant.systemId_ad.equals(sysType)){//兼容广告拍拍增加对项目的限制
			logger.info("进入初审设置认领人方法。。。。");
			String nameString = delegateTask.getName();
			logger.info("当前结点是："+nameString);
			if ("初审".equals(nameString)) { // 设置初审人
				taskService.setAssignee(delegateTask.getId(),
						getNameForAudit("audit_firstaudit_claim_name",1,sysType));
				logger.info("初审认领人是："+getNameForAudit("audit_firstaudit_claim_name",1,sysType));
			} else if("抽检".equals(nameString)){ // 设置二审人员
				taskService.setAssignee(delegateTask.getId(),
						getNameForAudit("audit_sampling_claim_name",2,sysType));
				logger.info("抽检认领人是："+getNameForAudit("audit_sampling_claim_name",2,sysType));
			} else if("申诉".equals(nameString)){
				taskService.setAssignee(delegateTask.getId(),
						getNameForAudit("audit_appeal_claim_name",4,sysType));
				logger.info("申诉认领人是："+getNameForAudit("audit_appeal_claim_name",3,sysType));
			}
		}
	}

	/**
	 * @Description: 返回认领人名称
	 * @author xusheng.liu
	 * @date 2015年11月18日 下午4:11:20 
	 * @version V1.0 
	 * @param key 
	 * @param type 1初审,2抽检,4申诉
	 * @param sysType 项目Id
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private String getNameForAudit(String key,Integer type,String sysType) {
		logger.info("-->进入获取认领人的方法:key是"+key);
		Jedis jedis = jedisPool.getResource();
		logger.info("---->获取到的jedis是:"+jedis);
		try {
			//20151118获取认领人,从数据库中获取认领人,如果没有则从配置文件中获取...
			List<CollectAuditClaim> auditClaims = auditService.getClaimUser(type,sysType);
			if(auditClaims!=null && auditClaims.size()>0){
				logger.info("---->获取认领人集合不为空,返回认领人="+auditClaims.get(0));
				return auditClaims.get(0).getClaimUserName();
			}
			
			String initValStr = PropertiesConfig.getProperty(key);
			if(initValStr!=null && initValStr.contains(",") ){
				logger.info("---->获取的认领人为多个："+ initValStr);
				String[] names = initValStr.split(",");
				String systermid = PropertiesConfig.getProperty("audit_sampling_systerm_id");
				logger.info("---->限制的项目id为：" + systermid);
				if(type==2 && sysType.equals(systermid)){
					logger.info("---->节点为抽检，并且当前项目id和限制的项目id匹配。。");
					return names[1];
				}else{
					return names[0];
				}
			}
			return initValStr;
		} finally {
			logger.info("把redis放回池中...");
			jedisPool.returnResource(jedis);
		}
	}

}
