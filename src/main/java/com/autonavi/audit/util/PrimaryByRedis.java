package com.autonavi.audit.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class PrimaryByRedis {
	@Autowired
	private JedisPool jedisPool;
	
	
	public Long generateEcode() {
		Jedis jedis = jedisPool.getResource();
		String key = PropertiesConfig
				.getProperty("audit.enterprise.generateEcode.key");// 获取redis
																			// key的名字
		try {
			if (!jedis.exists(key)) {// 如果不存在KEY，说明是第一次使用，添加初始量
				String initValStr = PropertiesConfig
						.getProperty("audit.enterprise.generateEcode.initial.value");// 获取初始量
				long initVal = 0;
				if (initValStr != null)
					initVal = Long.valueOf(initValStr);
				return jedis.incrBy(key, initVal);// 从initVal开始自增
			} else
				// 否则直接自增
				return jedis.incr(key);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}
}
