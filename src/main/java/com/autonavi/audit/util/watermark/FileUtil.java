package com.autonavi.audit.util.watermark;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

/**
 * 
 * 文件操作公共类
 *
 */
public class FileUtil {
	private static Logger logger = Logger.getLogger(FileUtil.class);

	/**
	 * 创建不存在的目录
	 * 
	 * @param rootPath
	 * @param path
	 *            中包含file名称 如 collect-water-img/img/test.jpg,但test.jpg不是目录 所以-1
	 */
	public static void createDir(String rootPath, String path) {
		String[] paths = path.split("/");
		for (int i = 0; i < paths.length - 1; i++) {
			String dirPath = rootPath;
			for (int j = 0; j <= i; j++) {
				dirPath = dirPath + paths[j] + "/";
			}
			File file = new File(dirPath);
			// 如果文件夹不存在则创建
			if (!file.exists() && !file.isDirectory()) {
				logger.info(">>>>>>>目录：" + dirPath + "不存在，新建");
				file.mkdir();
			} else {
				logger.info(">>>>>>目录：" + dirPath + "存在");
			}
		}
	}

	/**
	 * 创建不存在的根目录
	 * 
	 * @param rootPath
	 * @param path
	 *            中不包含file名称 如 collect-water-img/img/
	 */
	public static void createRoot(String rootPath, String path) {
		String[] paths = path.split("/");
		for (int i = 0; i < paths.length; i++) {
			String dirPath = rootPath;
			for (int j = 0; j <= i; j++) {
				dirPath = dirPath + paths[j] + "/";
			}
			File file = new File(dirPath);
			// 如果文件夹不存在则创建
			if (!file.exists() && !file.isDirectory()) {
				logger.info(">>>>>>>目录：" + dirPath + "不存在，新建");
				file.mkdir();
			} else {
				logger.info(">>>>>>目录：" + dirPath + "存在");
			}
		}
	}

	public static void writeFile(String mess, String taskId, String userId) {
		FileOutputStream out = null;
		OutputStreamWriter writer = null;
		BufferedWriter bw = null;
		Thread current = Thread.currentThread();
		try {
			out = new FileOutputStream(
					new File(ParseProperties.getProperties("errorPath", "/")
							+ "errormess_" + current.getId() + ".log"), true);
			writer = new OutputStreamWriter(out, "UTF-8");
			bw = new BufferedWriter(writer);
			bw.write("线程ID：" + current.getId() + ",任务ID：" + taskId + ",用户ID："
					+ userId + ",下载图片地址：" + mess);
			bw.newLine();
		} catch (FileNotFoundException e) {
			logger.error(">>>>>>下载图片出现异常记录信息，出现找不到文件异常，线程ID：" + current.getId()
					+ ",任务ID：" + taskId + ",用户ID：" + userId + ",图片地址：" + mess,
					e);
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			logger.error(
					">>>>>>下载图片出现异常记录信息，出现UnsupportedEncodingException异常，线程ID："
							+ current.getId() + ",任务ID：" + taskId + ",用户ID："
							+ userId + ",图片地址：" + mess, e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(">>>>>>下载图片出现异常记录信息时，出现IO异常，线程ID：" + current.getId()
					+ ",任务ID：" + taskId + ",用户ID：" + userId + ",图片地址：" + mess,
					e);
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					logger.error(
							">>>>>>下载图片出现异常记录信息时，出现BufferedWriter-IO关闭异常，线程ID："
									+ current.getId() + ",任务ID：" + taskId
									+ ",用户ID：" + userId + ",图片地址：" + mess, e);
					e.printStackTrace();
				}
			}
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					logger.error(
							">>>>>>下载图片出现异常记录信息时，出现OutputStreamWriter-IO关闭异常，线程ID："
									+ current.getId() + ",任务ID：" + taskId
									+ ",用户ID：" + userId + ",图片地址：" + mess, e);
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error(
							">>>>>>下载图片出现异常记录信息时，出现FileOutputStream-IO关闭异常，线程ID："
									+ current.getId() + ",任务ID：" + taskId
									+ ",用户ID：" + userId + ",图片地址：" + mess, e);
					e.printStackTrace();
				}
			}
		}
	}

}
