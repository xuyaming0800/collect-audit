package com.autonavi.audit.util;

import java.math.BigDecimal;

public class CommonUtil {
	//默认除法运算精度
    private static final int DEF_DIV_SCALE = 6;
    
	 public static double add(double v1,double v2){
	        BigDecimal b1 = new BigDecimal(Double.toString(v1));
	        BigDecimal b2 = new BigDecimal(Double.toString(v2));
	        return b1.add(b2).doubleValue();
	    }
	    
	    public static String add(String v1,String v2){
	        BigDecimal b1 = new BigDecimal(v1);
	        BigDecimal b2 = new BigDecimal(v2);
	        return b1.add(b2).toString();
	    }
	   
	    public static double sub(double v1,double v2){
	        BigDecimal b1 = new BigDecimal(Double.toString(v1));
	        BigDecimal b2 = new BigDecimal(Double.toString(v2));
	        return b1.subtract(b2).doubleValue();
	    }
	    
	    public static String sub(String v1,String v2){
	        BigDecimal b1 = new BigDecimal(v1);
	        BigDecimal b2 = new BigDecimal(v2);
	        return b1.subtract(b2).toString();
	    }
	   
	    public static double mul(double v1,double v2){
	        BigDecimal b1 = new BigDecimal(Double.toString(v1));
	        BigDecimal b2 = new BigDecimal(Double.toString(v2));
	        return b1.multiply(b2).doubleValue();
	    }
	    
	    public static String mul(String v1,String v2){
	        BigDecimal b1 = new BigDecimal(v1);
	        BigDecimal b2 = new BigDecimal(v2);
	        return b1.multiply(b2).toString();
	    }
	    //求余
	    public static double remainder(double v1,double v2){
	        BigDecimal b1 = new BigDecimal(Double.toString(v1));
	        BigDecimal b2 = new BigDecimal(Double.toString(v2));
	        return b1.remainder(b2).doubleValue();
	    }
	    
	    public static String remainder(String v1,String v2){
	        BigDecimal b1 = new BigDecimal(v1);
	        BigDecimal b2 = new BigDecimal(v2);
	        return b1.remainder(b2).toString();
	    }
	    
	    /**
	     * -1 小于
	     *0 等于
	     *1 大于
	     * @param v1
	     * @param v2
	     * @return
	     */
	    public static int compare(double v1,double v2) {
	    	BigDecimal b1 = new BigDecimal(Double.toString(v1));
	        BigDecimal b2 = new BigDecimal(Double.toString(v2));
	         int result = b1.compareTo(b2);
	    	return result;
	    }
	    
	    /**
	     * -1 小于
	     *0 等于
	     *1 大于
	     * @param v1
	     * @param v2
	     * @return
	     */
	    public static int compare(String v1,String v2) {
	    	 BigDecimal b1 = new BigDecimal(v1);
	         BigDecimal b2 = new BigDecimal(v2);
	         int result = b1.compareTo(b2);
	    	return result;
	    }

	    public static double div(double v1,double v2){
	        return div(v1,v2,DEF_DIV_SCALE);
	    }

	   
	    public static double div(double v1,double v2,int scale){
	        if(scale<0){
	            throw new IllegalArgumentException(
	                "The scale must be a positive integer or zero");
	        }
	        BigDecimal b1 = new BigDecimal(Double.toString(v1));
	        BigDecimal b2 = new BigDecimal(Double.toString(v2));
	        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
	    }

	   
	    public static double round(double v,int scale){
	        if(scale<0){
	            throw new IllegalArgumentException(
	                "The scale must be a positive integer or zero");
	        }
	        BigDecimal b = new BigDecimal(Double.toString(v));
	        BigDecimal one = new BigDecimal("1");
	        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
	    }
	    
	    public static String div(String v1,String v2){
	        return div(v1,v2,DEF_DIV_SCALE);
	    }

	    public static String div(String v1,String v2,int scale){
	        if(scale<0){
	            throw new IllegalArgumentException(
	                "The scale must be a positive integer or zero");
	        }
	        BigDecimal b1 = new BigDecimal(v1);
	        BigDecimal b2 = new BigDecimal(v2);
	        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).toString();
	    }

	   
	    public static double round(String v,int scale){
	        if(scale<0){
	            throw new IllegalArgumentException(
	                "The scale must be a positive integer or zero");
	        }
	        BigDecimal b = new BigDecimal(v);
	        BigDecimal one = new BigDecimal("1");
	        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
	    }
}
