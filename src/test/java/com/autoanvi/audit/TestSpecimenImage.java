package com.autoanvi.audit;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.autonavi.audit.entity.CollectAuditSpecimenImage;
import com.autonavi.audit.service.AuditService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext*.xml")
public class TestSpecimenImage {

	
	@Autowired
	private AuditService auditService;
	
	@Test
	public void testImage() {
		List<CollectAuditSpecimenImage> collectAuditSpecimenImages = auditService.findAuditSpecimenImageByTaskId("49");
		
		System.out.println(collectAuditSpecimenImages.size());
	}
}
