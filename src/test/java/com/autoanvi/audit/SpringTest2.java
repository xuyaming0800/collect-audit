package com.autoanvi.audit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.autonavi.audit.entity.CollectAudit;
import com.autonavi.audit.entity.CollectAuditImage;
import com.autonavi.audit.entity.CollectAuditSpecimenImage;
import com.autonavi.audit.mq.RabbitMQUtils;
import com.autonavi.audit.service.AuditService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext*.xml")
public class SpringTest2 {

	@Autowired
	private AuditService auditService;

	@Test
	public void test() {

		try {
			/**
			 * 启动流程
			 */

			CollectAudit collectAudit = new CollectAudit();
			collectAudit.setId("44");
			collectAudit.setStatus(1);
			collectAudit.setAudit_task_name("正常1");
			collectAudit.setCollect_task_name("正常1");
			collectAudit.setOriginal_task_name("正常1");
			collectAudit.setUser_name("sheng");
			collectAudit.setSystem_type("1");
		//	collectAudit
			//		.setSpecimen_page_url("http://www.xinhuanet.com/auto/titlepic/12758/127584209_title0h.jpg");
			collectAudit.setOriginalCoordinates(new Double[] {
					116.309000000000000000, 39.990000000000000000,
					116.304090000000000000, 39.989750000000000000,
					116.305050000000000000, 39.987070000000000000 });
			
			
			
			
			
			
			
			CollectAuditSpecimenImage collectAuditSpecimenImage = new CollectAuditSpecimenImage();
			collectAuditSpecimenImage
					.setThumbnai_url("https://ss0.baidu.com/-Po3dSag_xI4khGko9WTAnF6hhy/image/h%3D360/sign=b1dce25c0a24ab18ff16e73105fbe69a/86d6277f9e2f0708cb822d7aea24b899a801f2c1.jpg");
			collectAuditSpecimenImage
					.setImage_url("https://ss0.baidu.com/-Po3dSag_xI4khGko9WTAnF6hhy/image/h%3D360/sign=b1dce25c0a24ab18ff16e73105fbe69a/86d6277f9e2f0708cb822d7aea24b899a801f2c1.jpg");
			
			CollectAuditSpecimenImage collectAuditSpecimenImage2 = new CollectAuditSpecimenImage();
			collectAuditSpecimenImage2
					.setThumbnai_url("https://ss3.baidu.com/9fo3dSag_xI4khGko9WTAnF6hhy/image/h%3D200/sign=f5ec0a3ecb3d70cf53faad0dc8ddd1ba/79f0f736afc379318087de2ee9c4b74542a911cd.jpg");
			collectAuditSpecimenImage2
					.setImage_url("https://ss3.baidu.com/9fo3dSag_xI4khGko9WTAnF6hhy/image/h%3D200/sign=f5ec0a3ecb3d70cf53faad0dc8ddd1ba/79f0f736afc379318087de2ee9c4b74542a911cd.jpg");
			
			List<CollectAuditSpecimenImage> collectAuditSpecimenImages = new ArrayList<CollectAuditSpecimenImage>();
			collectAuditSpecimenImages.add(collectAuditSpecimenImage);
			collectAuditSpecimenImages.add(collectAuditSpecimenImage2);
			
			collectAudit.setSpecimenImages(collectAuditSpecimenImages);
			
			
			
			
			
			
			
			CollectAuditImage collectAuditImage = new CollectAuditImage();
			collectAuditImage
					.setThumbnai_url("http://www.xinhuanet.com/auto/titlepic/12758/127584209_title0h.jpg");
			collectAuditImage
					.setImage_url("http://www.xinhuanet.com/auto/titlepic/12758/127584209_title0h.jpg");
			collectAuditImage.setGps_time(new Date());
			collectAuditImage.setLon(116.304090000000000000);
			collectAuditImage.setLat(39.989750000000000000);
			collectAuditImage.setPhotograph_time(new Date());
			collectAuditImage.setPoint_accury(1.0);
			collectAuditImage.setPoint_level(100);
			collectAuditImage.setPosition(1.0);
			
			CollectAuditImage collectAuditImage2 = new CollectAuditImage();
			collectAuditImage2
					.setThumbnai_url("http://www.xinhuanet.com/auto/titlepic/12758/127584209_title0h.jpg");
			collectAuditImage2
					.setImage_url("http://www.xinhuanet.com/auto/titlepic/12758/127584209_title0h.jpg");
			collectAuditImage2.setGps_time(new Date());
			collectAuditImage2.setLon(116.308990000000000000);
			collectAuditImage2.setLat(39.981740000000000000);
			collectAuditImage2.setPhotograph_time(new Date());
			collectAuditImage2.setPoint_accury(100.0);
			collectAuditImage2.setPoint_level(10);
			collectAuditImage2.setPosition(1.0);
			
			List<CollectAuditImage> list = new ArrayList<CollectAuditImage>();
			list.add(collectAuditImage);
			list.add(collectAuditImage2);
			collectAudit.setImages(list);
			RabbitMQUtils.send("1", "10.19.3.158", 5672, null, null, "in",
					collectAudit, true);
			//auditService.startTask();

			/**
			 * 查询待办未认领任务
			 */
			// auditService.findRepresentativeTask();
			/**
			 * 认领任务
			 */
			// auditService.claimTask(1, 2);

			/**
			 * 查询已认领任务
			 */
			// auditService.findClaimTask(1);

			/**
			 * 添加意见
			 */
			// auditService.addComment(1, 123, "哈哈哈");

			/**
			 * 历史记录
			 */
			// auditService.findHistory(1, 123);

			/**
			 * 是否已结束
			 */
			// System.out.println(auditService.isFinished(1, 123));
			/**
			 * 历史流程图片
			 */
			// OutputStream fileout = new FileOutputStream("d:/123.png");
			// auditService.processTracking("AuditProcess:5:17504", "20001",
			// fileout);
			// fileout.flush();
			// fileout.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
