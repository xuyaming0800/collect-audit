package com.autonavi.audit.service;

import java.util.List;
import java.util.Map;

/**
 * 获取用户相关的，菜单，角色，权限信息
 * 
 * @author chunsheng.zhang
 *
 */
public interface ResourceService {
	/**
	 * 根据用户名获取用户信息
	 * 
	 * @param username
	 * @return
	 */
	public Map<String, Object> queryInfoByUserName(String username)
			throws Exception;

	/**
	 * 根据用户查询角色信息
	 * 
	 * @param username
	 * @return
	 */
	public List<String> queryRolesByUserName(String username) throws Exception;

	/**
	 * 根据用户信息查询权限信息
	 * 
	 * @param username
	 * @return
	 */
	public List<String> queryPermissionsByUserName(String username)
			throws Exception;

}
