package com.autonavi.audit.util.watermark;

/**
 *  Copyright (c)  1993-2023 AutoNavi, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of AutoNavi, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with AutoNavi.
 */

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 * 服务器端的图片加水印
 * 
 * @author yuchao.wang
 * @since 2014-2-17
 */
public class WaterMark {
	private static Logger logger = Logger.getLogger(WaterMark.class);

	/**
	 * 全部参数设定好的方法，字体大小，颜色，文字。
	 * 
	 * @param time
	 * 
	 * @return
	 */
	public static boolean markByAutoNavi(String srcImgPath, String tarImgPath) {
		String logoText = ParseProperties.getProperties("logoText", "")
					+ new SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
							.format(new Date());
		logger.info("水印文字是：" + logoText);
		/** 旋转角度 */
		int degree = -30;
		/** 水印透明度 */
		float alpha = 0.3f;
		/** 水印文字字体 */
		Font font;
		/** 水印文字颜色 */
		Color color = Color.YELLOW;
		/** 添加水印 */
		boolean flag = false;
		InputStream inputstream = null;
		OutputStream outputstream = null;
		try {
			logger.info(">>>>>>图片加水印开始" + ",原图path:" + srcImgPath + ",目标图path:"
					+ ParseProperties.getProperties("rootWaterPath", "/")
					+ tarImgPath);
			// 1、源图片
			Image srcImg = ImageIO.read(new File(ParseProperties.getProperties(
					"rootWaterPath", "/") + srcImgPath));
			BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null),
					srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
			// 2、得到画笔对象
			Graphics2D graphics = buffImg.createGraphics();
			// 3、设置对线段的锯齿状边缘处理
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			/*
			 * 含图片缩放 如果不涉及图片缩放，最好不要使用getScaledInstance方法 比较费时
			 * getScaledInstance(int width, int height, int
			 * hints)，width,height为原来图片的高和宽，hints为缩放的比率（缩放后/原图）
			 * 
			 * graphics.drawImage(
			 * srcImg.getScaledInstance(srcImg.getWidth(null),
			 * srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0, null);
			 */
			graphics.drawImage(srcImg, 0, 0, null);
			/** 初始化数学数据 ,添加文字位置时使用 */
			int width = srcImg.getWidth(null);
			int height = srcImg.getHeight(null);
			int length = width > height ? width : height;
			font = new Font("宋体", Font.BOLD, length / 25);
			graphics.setColor(color); // 5、设置水印文字颜色
			graphics.setFont(font); // 6、设置水印文字Font
			graphics.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC, alpha));// 7、设置水印文字透明度
			if (width > height) {
				// 旋转中心点
				graphics.rotate(Math.toRadians(-(degree + 90)),
						(double) buffImg.getWidth() / 4,
						(double) buffImg.getHeight() / 2);
				graphics.drawString(logoText, 0, height / 2);
				graphics.drawString(logoText, 0 + width / 4, width / 4 * 3);
			} else {
				// 旋转中心点
				graphics.rotate(Math.toRadians(degree),
						(double) buffImg.getWidth() / 2,
						(double) buffImg.getHeight() / 4);
				graphics.drawString(logoText, 0, height / 4);
				graphics.drawString(logoText, 0 - width / 2, height / 4 * 3);
			}
			graphics.dispose();// 9、释放资源
			// 创建不存在的目录
			FileUtil.createDir(
					ParseProperties.getProperties("rootWaterPath", "/"),
					tarImgPath);

			outputstream = new FileOutputStream(ParseProperties.getProperties(
					"rootWaterPath", "/") + tarImgPath);// 10、生成图片
			ImageIO.write(buffImg, "JPG", outputstream);
			flag = true;
			logger.info(">>>>>>图片加水印结束" + ",原图path:"
					+ ParseProperties.getProperties("rootPath", "/")
					+ srcImgPath + ",目标图path:"
					+ ParseProperties.getProperties("rootWaterPath", "/")
					+ tarImgPath);
		} catch (Exception e) {
			logger.error(
					">>>>>>图片加水印异常："
							+ "原图path:"
							+ ParseProperties.getProperties("rootPath", "/")
							+ srcImgPath
							+ ",目标图path:"
							+ ParseProperties.getProperties("rootWaterPath",
									"/") + tarImgPath, e);
			e.printStackTrace();
			flag = false;
		} finally {
			try {
				if (null != inputstream)
					inputstream.close();
			} catch (Exception e) {
				logger.error(
						">>>>>>IO输入流异常:"
								+ "原图path:"
								+ ParseProperties
										.getProperties("rootPath", "/")
								+ srcImgPath + ",目标图path:" + tarImgPath, e);
				e.printStackTrace();
				flag = false;
			}
			try {
				if (null != outputstream)
					outputstream.close();
			} catch (Exception e) {
				logger.error(
						">>>>>>IO输出流异常件未找到:"
								+ "原图path:"
								+ ParseProperties
										.getProperties("rootPath", "/")
								+ srcImgPath + ",目标图path:" + tarImgPath, e);
				e.printStackTrace();
				flag = false;
			}
		}
		return flag;
	}
}