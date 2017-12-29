package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

public class GetBucketDefault404Request extends WebServiceRequest {
	/**
	 * The name of the bucket.
	 */
	private String bucketName;

	public GetBucketDefault404Request(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
}
