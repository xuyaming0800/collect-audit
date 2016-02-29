package com.autoanvi.audit;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Test;

import com.autonavi.audit.entity.CollectAudit;

public class VTest {

	@Test
	public void vTest() {
		try {
			CollectAudit collectAudit = new CollectAudit();
			ValidatorFactory factory = Validation
					.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();
			Set<ConstraintViolation<CollectAudit>> constraintViolations = validator
					.validate(collectAudit);
			Iterator<ConstraintViolation<CollectAudit>> it = constraintViolations
					.iterator();
			while (it.hasNext()) {
				ConstraintViolation<CollectAudit> cv = it.next();
				System.out.println(cv.getPropertyPath() + cv.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
