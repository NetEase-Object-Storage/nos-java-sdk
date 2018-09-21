package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

public class GetImageMetaInfoRequest extends WebServiceRequest{
	private String bucketName;
	
	private String key;
	
	
	public GetImageMetaInfoRequest(String bucketName, String key){
		this.bucketName = bucketName;
		this.key = key;
	}
	public String getBucketName(){
		return this.bucketName;
	}
	
	public String getKey(){
		return this.key;
	}
	

}
