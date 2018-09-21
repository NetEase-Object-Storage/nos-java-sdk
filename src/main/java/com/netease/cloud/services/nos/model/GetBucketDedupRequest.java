package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;
/**
 * <p>
 * Provide the data of get Deduplicate config.
 * </p>
 */
public class GetBucketDedupRequest   extends WebServiceRequest{

	/**
	 * The name of the bucket.
	 */
	private String bucketName;

	public GetBucketDedupRequest(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

}
