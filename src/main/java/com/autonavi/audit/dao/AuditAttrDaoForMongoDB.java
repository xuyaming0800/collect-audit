package com.autonavi.audit.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.autonavi.audit.entity.CollectAuditLogs;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

@Repository
public class AuditAttrDaoForMongoDB {

	/**
	 * 通过ID获取collect_audit_attr集合里的所有数据
	 * 
	 * @param bid
	 *            业务ID
	 * @return 该业务ID下的所有记录
	 */
	public Map<?, ?> getAllAttr(String bid) {
		DBCollection dbcoll = mongoTemplate.getDb().getCollection(
				"collect_audit_attr");
		// 查询条件
		BasicDBObject query = new BasicDBObject();
		query.put("baseId", bid);

		// 查询
		DBObject dbObject = dbcoll.findOne(query, new BasicDBObject("_id", 0));
		
		if(dbObject!=null){
			// 得到结果
			Map<?, ?> map = dbObject.toMap();
			return map;
		}return null;
	}

	/**
	 * 更新某一业务ID下的某一类别的状态
	 * 
	 * @param bid
	 *            业务ID
	 * @param type
	 *            业务类型
	 * @param status
	 *            要更新成的状态
	 * @param collectAuditLogs
	 * @param moneyTotal 
	 * @param auditName 
	 */
	public void updateStatus(String bid, String type,
			String taskClassNameForAudit, CollectAuditLogs collectAuditLogs, String auditName) {
		DBCollection dbcoll = mongoTemplate.getDb().getCollection(
				"collect_audit_attr");
		// 查询条件
		BasicDBObject query = new BasicDBObject();
		query.put("baseId", bid);
		query.put("attrList.name", type);
		//查询出来
		DBObject dbObject = dbcoll.findOne(query, new BasicDBObject("_id", 0).append("attrList.level", 1).append("attrList.name", 1));
		Object level = "";
		//赋值level
		if(dbObject.containsField("attrList")){
			BasicDBList object = (BasicDBList)dbObject.get("attrList");
			for (Object map : object) {
				BasicDBObject li = (BasicDBObject) map;
				if(type.equals(li.get("name"))){
					level = li.get("level");
					break;
				}
			}
		}

		if (collectAuditLogs != null) {
			// 添加字段
			BasicDBObject update = new BasicDBObject();
			if(collectAuditLogs.getStatus()!=null){
				update.put("attrList.$.status", collectAuditLogs.getStatus() + "");
				//20151014增加修改金额
				update.put("attrList.$.userChangeMoney", collectAuditLogs.getTotalMoney() + "");
				update.put(level.toString()+".userChangeMoney", collectAuditLogs.getTotalMoney() + "");
				dbcoll.update(query, new BasicDBObject("$set", update));
			}
			
			//插入审核通过或不通过原因数组
			BasicDBObject insert = new BasicDBObject();
			Map<String, Object> map = new HashMap<String, Object>();
			if(collectAuditLogs.getDamaged()!=""&&collectAuditLogs.getDamaged()!=null)
				map.put("damaged", collectAuditLogs.getDamaged());
			if(collectAuditLogs.getAppearance()!=""&&collectAuditLogs.getAppearance()!=null)
				map.put("appearance", collectAuditLogs.getAppearance());
			if(collectAuditLogs.getLighting()!=""&&collectAuditLogs.getLighting()!=null)
				map.put("lighting", collectAuditLogs.getLighting());
			if(collectAuditLogs.getOcclusion()!=""&&collectAuditLogs.getOcclusion()!=null)
				map.put("occlusion", collectAuditLogs.getOcclusion());
			if(collectAuditLogs.getAudit_task_name()!=""&&collectAuditLogs.getAudit_task_name()!=null)
				map.put("auditTaskName", collectAuditLogs.getAudit_task_name());
			if(collectAuditLogs.getNo_approval_reason()!=""&&collectAuditLogs.getNo_approval_reason()!=null)
				map.put("noApprovalReason", collectAuditLogs.getNo_approval_reason());
			if(collectAuditLogs.getComment_message()!=""&&collectAuditLogs.getComment_message()!=null)
				map.put("commentMessage", collectAuditLogs.getComment_message());
			if(taskClassNameForAudit!=""&&taskClassNameForAudit!=null)
				map.put("taskClassNameForAudit", taskClassNameForAudit);
			if(auditName!=""&&auditName!=null)
				map.put("auditName", auditName);
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
			map.put("createTime", df.format(new Date()));
			insert.put(level.toString()+".reasons", map);
			// 进行更新
			dbcoll.updateMulti(query, new BasicDBObject("$push", insert));
			if (!dbcoll.getWriteConcern().callGetLastError()) {
				throw new MongoException("更新mongodb失败");
			}
		}
	}

	/**
	 * @Description: 设置总金额
	 * @author 刘旭升
	 * @date 2015年8月31日 下午4:44:59 
	 * @version V1.0 
	 * @param bid 任务id
	 * @param moneyTotal 总金额
	 */
	public void setMoney(String bid,Double moneyTotal) {
		DBCollection dbcoll = mongoTemplate.getDb().getCollection(
				"collect_audit_attr");
		BasicDBObject query = new BasicDBObject();
		query.put("baseId", bid);
		// 添加字段
		BasicDBObject update = new BasicDBObject();
		if(moneyTotal>=0)
			update.put("money",moneyTotal);
		dbcoll.update(query, new BasicDBObject("$set", update));
	}
	
	/**
	 * @Description: 指定业务ID,类型,删除状态属性
	 * @author 刘旭升
	 * @date 2015年7月22日 上午10:12:02
	 * @version V1.0
	 * @param bid
	 *            业务ID
	 * @param type
	 *            类型(停车场,楼栋...)
	 */
	public void deleteStatus(String bid, String type) {
		DBCollection dbcoll = mongoTemplate.getDb().getCollection(
				"collect_audit_attr");
		// 查询条件
		BasicDBObject query = new BasicDBObject();
		query.put("baseId", bid);
		query.put("attrList.name", type);

		// 添加字段
		BasicDBObject update = new BasicDBObject();
		update.put("attrList.$.status", 0);

		// 进行更新
		dbcoll.update(query, new BasicDBObject("$unset", update));
		if (!dbcoll.getWriteConcern().callGetLastError()) {
			throw new MongoException("更新mongodb失败");
		}
	}

	/**
	 * @Description: 查询审核项通过数
	 * @author 刘旭升
	 * @date 2015年7月24日 下午2:27:58
	 * @version V1.0
	 * @param baseId
	 * @return
	 */
	public Map<?, ?> getStatusCount(String baseId) {
		DBCollection dbcoll = mongoTemplate.getDb().getCollection(
				"collect_audit_attr");
		// 查询条件
		BasicDBObject query = new BasicDBObject();
		query.put("baseId", baseId);

		// 查询
		DBObject dbObject = dbcoll.findOne(query,
				new BasicDBObject("_id", 0).append("attrList.status", 1).append("attrList.money", 1));

		// 得到结果
		Map<?, ?> map = dbObject.toMap();
		return map; 
	}

	@Autowired
	private MongoTemplate mongoTemplate = null;

}
