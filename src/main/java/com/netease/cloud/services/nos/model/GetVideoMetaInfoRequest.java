package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

public class GetVideoMetaInfoRequest extends WebServiceRequest {
	/**
	 * The name of the bucket containing the object's whose metadata is being
	 * retrieved.
	 */
	private String bucketName;
	/**
	 * The key of the object whose metadata is being retrieved.
	 */
	private String key;

	public GetVideoMetaInfoRequest(String bucketName, String key){
		this.bucketName = bucketName;
		this.key = key;
	}
	
	public String getKey(){
		return this.key;
	}
	
	public String getBucketName(){
		return this.bucketName;
	}
}