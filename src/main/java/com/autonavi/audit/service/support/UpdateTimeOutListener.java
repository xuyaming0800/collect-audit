package com.autonavi.audit.service.support;

import java.util.Calendar;
import java.util.Date;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
/**
 * @Description: 修改冻结期到期时间
 * @author 刘旭升
 * @date 2015年7月14日 上午9:49:03
 * @version V1.0
 */
public class UpdateTimeOutListener implements ExecutionListener {

	private static final long serialVersionUID = 4487443472556649786L;

	private Logger logger = LogManager.getLogger(getClass());
	
	@Override
	public void notify(DelegateExecution execution) {
		logger.trace("进入冻结期,根据节假日设置冻结期过期时间开始==");
		logger.entry(execution);
		Integer dayCount = 0;
		// 1.时间的获取
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		String duetime = "";
		// 周六或周日
		getDays(calendar,dayCount);
		Integer hours = 24+dayCount*24;
		duetime = "PT"+hours.toString()+"H";
		logger.trace("冻结期过期时间==="+duetime);
		logger.trace("假期天数:"+dayCount);
		execution.setVariable("duetime", duetime);
	}

	/**
	 * @Description: 判断下一天是不是假期
	 * @author 刘旭升
	 * @date 2015年7月14日 下午1:45:45 
	 * @version V1.0 
	 * @param calendar 日历
	 * @param dayCount 
	 * @param dayCount 假期天数
	 * @return
	 */
	private void getDays(Calendar calendar, Integer dayCount){
		logger.entry(calendar);
		if(calendar.get(Calendar.DAY_OF_WEEK)==6||calendar.get(Calendar.DAY_OF_WEEK)==7){//是假期
			logger.trace("当前时间是节假日===="+new Date(calendar.getTimeInMillis()).toString());
			dayCount+=1;
			calendar.add(Calendar.DATE, +1);// 日期加一天 
			getDays(calendar,dayCount);
		}
	}
}
