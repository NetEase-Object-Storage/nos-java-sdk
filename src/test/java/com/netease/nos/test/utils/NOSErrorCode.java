package com.netease.nos.test.utils;

/**   
 * @Title: NosErrorCode.java
 * @Package com.netease.cloud.nos
 * @Description: 封装Nos错误码和Http错误码
 * @Company Netease
 * @author laidongmin@corp.netease.com
 * @date 2012-6-12 上午11:48:16
 */

public class NOSErrorCode {
	private String nosCode;
	private int httpCode;
	
	public NOSErrorCode(String nos, int http) {
		this.nosCode = nos;
		this.httpCode = http;
	}
	
	public String getNosErrorCode() {
		return nosCode;
	}
	
	public int getHttpErrorCode() {
		return httpCode;
	}
	
	@Override
	public String toString() {
		return "{" + httpCode + ":" + nosCode + "}";
	}
}
