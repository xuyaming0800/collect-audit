package com.autoanvi.audit;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.autonavi.audit.entity.CollectAudit;
import com.autonavi.audit.entity.CollectAuditImage;
import com.autonavi.audit.entity.CollectAuditSpecimenImage;
import com.autonavi.audit.entity.ResultEntity;
import com.autonavi.audit.mq.RabbitMQUtils;

public class NonStringTest {

	@Test
	public void test() {
		try {
			/**
			 * 启动流程
			 */
			int sssss = 77777809;
			for (int i = sssss; i <= sssss; i++) {
			ResultEntity resultEntity = new ResultEntity();
			CollectAudit collectAudit = new CollectAudit();
			collectAudit.setId(i+"");
			collectAudit.setStatus(4);
			collectAudit.setLocation_name("大恒科技大厦");
			collectAudit.setLocation_address("北京市海淀区苏州街三号");
			//collectAudit.setAudit_task_name("正常18");
			collectAudit.setCollect_task_name(DateFormat.getDateInstance().format(new Date())+"-查询taskID"+i);
			//collectAudit.setOriginal_task_name("正常18");
			collectAudit.setUser_name("sheng");
			collectAudit.setTask_class_name("公交站亭");
			collectAudit.setSystem_type("1");
			collectAudit.setVerify_maintain_time(60);//审核时间，10秒
			collectAudit.setTask_freezing_time(10);
			collectAudit.setTask_amount(0.1D);
			collectAudit.setTask_freezing_time(72);
			collectAudit.setTask_class_name("公交站亭");
			// 原始坐标
//			collectAudit.setOriginalCoordinates(new Double[] {
//					116.309000000000000000, 39.990000000000000000,
//					116.304090000000000000, 39.989750000000000000,
//					116.305050000000000000, 39.987070000000000000 });

			//样张照片
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
			//照片
			collectAuditImage
					.setThumbnai_url("http://www.xinhuanet.com/auto/titlepic/12758/127584209_title0h.jpg");
			collectAuditImage
					.setImage_url("http://www.xinhuanet.com/auto/titlepic/12758/127584209_title0h.jpg");
			//录像
			collectAuditImage.setVideo_url("http://localhost/20150402_115549.mp4");
			collectAuditImage.setVideo_time(new Date());
			collectAuditImage.setNo_exist_reason("就是不存在");
			
			collectAuditImage.setGps_time(new Date());
			collectAuditImage.setLon(116.304090000000000000);
			collectAuditImage.setLat(39.989750000000000000);
			collectAuditImage.setPhotograph_time(new Date());
			collectAuditImage.setPoint_accury(1.0);
			collectAuditImage.setPoint_level(100);
			collectAuditImage.setPosition(1.0);
			collectAuditImage.setIndex(i);

			CollectAuditImage collectAuditImage2 = new CollectAuditImage();
			//照片
			collectAuditImage2
					.setThumbnai_url("http://www.xinhuanet.com/auto/titlepic/12758/127584209_title0h.jpg");
			collectAuditImage2
					.setImage_url("http://www.xinhuanet.com/auto/titlepic/12758/127584209_title0h.jpg");
			//录像
			collectAuditImage2.setVideo_url("http://localhost/20150402_115549.mp4");
			collectAuditImage2.setVideo_time(new Date());
			collectAuditImage2.setNo_exist_reason("就是不存在");
			
			collectAuditImage2.setGps_time(new Date());
			collectAuditImage2.setLon(116.308990000000000000);
			collectAuditImage2.setLat(39.981740000000000000);
			collectAuditImage2.setPhotograph_time(new Date());
			collectAuditImage2.setPoint_accury(100.0);
			collectAuditImage2.setPoint_level(10);
			collectAuditImage2.setPosition(1.0);
			collectAuditImage2.setIndex(i+1);

			List<CollectAuditImage> list = new ArrayList<CollectAuditImage>();
			list.add(collectAuditImage);
			list.add(collectAuditImage2);
			collectAudit.setImages(list);

			//提交时间
			collectAudit.setSubmit_time(new Date());
			

			resultEntity.setSuccess(true);
			resultEntity.setInfo(collectAudit);
			RabbitMQUtils.send("1", "123.57.213.13", 5672, null, null, "collect_dev_out",
					resultEntity, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
