package com.autonavi.audit.util;

import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;


public class PropertiesConfig {

	private static Logger log = LogManager.getLogger(PropertiesConfig.class);
	private static final String configFile = "conf.properties";
    private static Properties properties = new Properties();
    
    public static String getProperty(String key) {
    	try {
    	    if (properties.isEmpty()) {
    		    InputStream is = PropertiesConfig.class.getClassLoader().getResourceAsStream(configFile);
    	        properties.load(is);
    	    }
    	}
    	catch (Exception e) {
    		log.error("读取配置文件出错", e);
    	}
    	return properties.getProperty(key);
    }
    
    public static String getProperty(String key, String defaultValue) {
    	String result = getProperty(key);
    	if (StringUtils.isEmpty(result)) result = defaultValue;
    	return result;
    }
    

}
