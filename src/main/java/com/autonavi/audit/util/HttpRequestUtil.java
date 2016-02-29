package com.autonavi.audit.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.autonavi.audit.entity.ResultEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpRequestUtil {
	 private static final Logger logger = Logger.getLogger(HttpRequestUtil.class);

	/**
	 * 
	 * @author wenpeng.jin
	 * @date 2015年9月9日
	 * @description  获取所有客户信息或者获取项目负责人信息
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> getUsers(String url,String userName)  throws Exception{
		logger.info("进入getUsers方法获取用户,url:"+url+",userName:"+userName+"-----start");
		try {
			String json = HttpClientUtil.get(url+"&userName="+userName, null);
			logger.info("获取内容："+json);
			if(StringUtils.isBlank(json)) {
				return null;
			}
			ObjectMapper objectMapper = new ObjectMapper();
			ResultEntity resultEntity = objectMapper.readValue(json,ResultEntity.class);
			List<Object> userList = (List<Object>) resultEntity.getInfo();
			Map userMaps = null;
			if(userList != null && userList.size() > 0) {
				userMaps =new HashMap();
				for(Object obj : userList) {
					Map userMap = (Map)obj;
					userMaps.put(userMap.get("id"), userMap);
				}
			}
			logger.info("进入getUsers方法获取用户,url:"+url+"-----end");
			return userMaps;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 
	 * @author wenpeng.jin
	 * @date 2015年9月22日
	 * @description 获取项目信息
	 * @param url
	 * @param projectName
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> getProjects(String url,String param)  throws Exception{
		logger.info("进入getProjects方法获取项目,url:"+url+",param:"+param+"-----start");
		try {
			String json = HttpClientUtil.get(url+param, null);
			logger.info("获取内容："+json);
			if(StringUtils.isBlank(json)) {
				return null;
			}
			ObjectMapper objectMapper = new ObjectMapper();
			ResultEntity resultEntity = objectMapper.readValue(json,ResultEntity.class);
			Map infoMap = (Map)resultEntity.getInfo();
			List<Object> projectList = (List<Object>)infoMap.get("objectList");
			Map projectMaps = null;
			if(projectList != null && projectList.size() > 0) {
				projectMaps =new HashMap();
				for(Object obj : projectList) {
					Map userMap = (Map)obj;
					projectMaps.put(userMap.get("id"), userMap);
				}
			}
			logger.info("进入getProjects方法获取项目,url:"+url+"-----end");
			return projectMaps;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 
	 * @author wenpeng.jin
	 * @date 2015年10月19日
	 * @description 获取用户List（客户信息,审核人,申请人,项目负责人） 
	 * @param url
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public static List<Object> getUserList(String url,String param)  throws Exception{
		logger.info("进入getUserList方法获取用户,url:"+url+",param:"+param+"-----start");
		try {
			String json = HttpClientUtil.get(url+"&"+param, null);
			logger.info("获取内容："+json);
			if(StringUtils.isBlank(json)) {
				return null;
			}
			ObjectMapper objectMapper = new ObjectMapper();
			ResultEntity resultEntity = objectMapper.readValue(json,ResultEntity.class);
			List<Object> userList = (List<Object>) resultEntity.getInfo();
			logger.info("进入getUserList方法获取用户,url:"+url+"-----end");
			return userList;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 
	 * @author wenpeng.jin
	 * @date 2015年10月19日
	 * @description 获取项目信息 
	 * @param url
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static List<Object> getProjectList(String url,String param)  throws Exception{
		logger.info("进入getProjectList方法获取项目,url:"+url+",param:"+param+"-----start");
		try {
			String json = HttpClientUtil.get(url+"&"+param, null);
			logger.info("获取内容："+json);
			if(StringUtils.isBlank(json)) {
				return null;
			}
			ObjectMapper objectMapper = new ObjectMapper();
			ResultEntity resultEntity = objectMapper.readValue(json,ResultEntity.class);
			Map infoMap = (Map)resultEntity.getInfo();
			List<Object> projectList = (List<Object>)infoMap.get("objectList");
			logger.info("进入getProjects方法获取项目,url:"+url+"-----end");
			return projectList;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
