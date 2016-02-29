package com.autonavi.audit.base.pagging.mongrid.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.autonavi.audit.base.pagging.mongrid.entity.MonGridEntity;
import com.autonavi.audit.base.pagging.mongrid.entity.Rows;

@Service
public class MonGridService {
	private Logger log = LogManager.getLogger(this.getClass());

	/**
	 * 将用户的实体转变为flexigrid实体
	 * 
	 * @param list
	 *            用户从数据查询出来的数据
	 * @param page
	 *            当前页数
	 * @return
	 */
	public MonGridEntity conversionToMonGridEntity(Class<?> clazz, List list,
			long total) throws Exception {
		MonGridEntity monGridEntity = new MonGridEntity();
		Rows rows = null;
		List<Rows> rowsList = new ArrayList<Rows>();
		Method method;

		try {
			method = clazz.getMethod("getId", null);
		} catch (NoSuchMethodException e) {
			log.error("实体类：" + clazz.getName() + "没有getId方法。\n"
					+ e.getMessage());
			e.printStackTrace();
			throw new NoSuchMethodException(e.getMessage());
		}

		for (Object t : list) {
			rows = new Rows();
			rows.setId((String)method.invoke(t, null));
			rows.setCell(t);
			rowsList.add(rows);
		}
		monGridEntity.setRows(rowsList);
		monGridEntity.setTotal(total);
		return monGridEntity;
	}

}
