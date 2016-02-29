package com.autonavi.audit.util.watermark;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

/**
 * 下载远程文件
 *@author wenpeng.jin
 * @since 2015-7-02
 */
public class Download {
	private static Logger logger = Logger.getLogger(Download.class);
	public static String SUCC_DOWN = "succ_down";// 下载成功
	public static String FAIL_DOWN = "fail_down";// 下载失败
	
	static{
		//防止根目录未手工创建，初始化时检查一下，如果未创建根目录，则自动创建
		String rootPath = ParseProperties.getProperties("rootPath", "/");
		File file =new File(rootPath);    
		//如果文件夹不存在则创建    
		if  (!file .exists()  && !file .isDirectory()){       
		   logger.info(">>>>>>>根目录："+rootPath+"不存在，新建");
		   FileUtil.createRoot(rootPath.substring(0, rootPath.indexOf("/")+1), rootPath.substring(rootPath.indexOf("/")+1, rootPath.length()));
		} else{  
			logger.info(">>>>>>目录："+rootPath+"存在");
		} 
	}
	
	/**
	 * 下载图片
	 * @param urlStr
	 * @param imagePath
	 * @return
	 */
	public static String downloadImage(String urlStr, String imagePath) {
		logger.info(">>>>>>下载文件开始，下载path:"+urlStr+",保存path:"+imagePath);
		// 下载远程图片
		String result = FAIL_DOWN;
		//int bytesum = 0; 文件字节总长度
		int byteread = 0;//数组字节长度

		URL url = null;
		URLConnection conn = null;
		InputStream in = null;
		FileOutputStream fs = null;
		try {
			url = new URL(urlStr);
			conn = url.openConnection();
			in = conn.getInputStream();
			//创建不存在的目录
			FileUtil.createDir(ParseProperties.getProperties("rootPath", "/"), imagePath);
			fs = new FileOutputStream(ParseProperties.getProperties("rootPath", "/")+"/"+imagePath);
			byte[] buffer = new byte[1204];
			while ((byteread = in.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
//				bytesum += byteread;
			}
			result = SUCC_DOWN;
			logger.info(">>>>>>下载文件完成，下载path:"+urlStr+",保存path:"+imagePath);
		} catch (FileNotFoundException e) {
			logger.error(">>>>>>文件未找到:"+urlStr,e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(">>>>>>IO流异常:"+urlStr,e);
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error(">>>>>>IO输入流异常:"+urlStr,e);
					e.printStackTrace();
				}
			}
			if (fs != null) {
				try {
					fs.close();
				} catch (IOException e) {
					logger.error(">>>>>>IO输出流异常:"+urlStr,e);
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	/**
	 * 下载文件
	 * @param urlStr
	 * @param filePath
	 * @return
	 */
	public static String downloadFile(String urlStr, String filePath) {
		// 下载远程文件
		String result = FAIL_DOWN;
		URL url = null;
		URLConnection conn = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			url = new URL(urlStr);
			conn = url.openConnection();
			// 获取到输入流
			br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));
			// 获取到输入流
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath)));
			String s = null;
			while ((s = br.readLine()) != null) {
				bw.write(s);
			}
			result = SUCC_DOWN;
		} catch (FileNotFoundException e) {
			logger.error(">>>>>>文件未找到:"+urlStr,e);
			
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(">>>>>>IO流异常:"+urlStr,e);
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error(">>>>>>IO输入流异常:"+urlStr,e);
					e.printStackTrace();
				}
			}
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					logger.error(">>>>>>IO输出流异常:"+urlStr,e);
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	

}
