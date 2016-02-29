package com.autoanvi.audit;

import org.junit.Test;

import com.autonavi.audit.entity.CollectAudit;
import com.autonavi.audit.entity.ResultEntity;
import com.autonavi.audit.mq.RabbitMQUtils;

public class CopyOfNonStringTest {

	@Test
	public void test() {
		try {
			/**
			 * 启动流程
			 */
			ResultEntity resultEntity = new ResultEntity();
			CollectAudit collectAudit = new CollectAudit();
			collectAudit.setId("1426064213895");
			collectAudit.setSystem_type("1");

			resultEntity.setSuccess(false);
			resultEntity.setInfo(collectAudit);
			RabbitMQUtils.send("1", "10.19.3.158", 5672, null, null,
					"collect_out", resultEntity, true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
