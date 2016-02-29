package com.autonavi.audit.util.watermark;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ParseProperties {
	private static Logger logger = Logger.getLogger(ParseProperties.class);
	private static final String remarkConfig = "remarkConfig.properties";

	public static String getProperties(String key, String value) {
		Properties p = null;
		try {
			// 生成输入流
			InputStream ins = ParseProperties.class.getClassLoader()
					.getResourceAsStream(remarkConfig);
			// 生成properties对象
			p = new Properties();
			p.load(ins);
			// config配置文件放置jar包外时，可通过以下方式加载配置文件（脚本启动时为当前用户根目录，在工程中启动为 工程名根目录）
			// File file = new File(System.getProperty("user.dir") +
			// File.separator +"config.properties");
			// p = new Properties();
			// p.load(new FileInputStream(file));
		} catch (Exception e) {
			logger.error("读取配置文件出错", e);
			e.printStackTrace();
		}
		// 输出properties文件的内容
		return p.getProperty(key) == null ? value : p.getProperty(key);
	}

	public static void main(String args[]) {
		System.out.println(getProperties("logoText", ""));

	}
}
