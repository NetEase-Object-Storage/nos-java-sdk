package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

/**
 * @author WANG Binghuan 2012-12-28
 * 
 */
public class HeadBucketRequest extends WebServiceRequest {

	private String bucketName;

	/**
	 * @param bucketName
	 */
	public HeadBucketRequest(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getBucketName() {
		return bucketName;
	}

}
