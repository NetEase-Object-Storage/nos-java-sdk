package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

public class PutBucketDefault404Request extends WebServiceRequest {
	
	/** bucket name to be modified **/
	private String bucketName;
	
	/** bucket default 404 object name to set, null to unset **/
	private String default404Object;
	
	public PutBucketDefault404Request(String bucketName, String default404Object) {
		this.bucketName = bucketName;
		this.default404Object = default404Object;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getDefault404Object() {
		return default404Object;
	}

	public void setDefault404Object(String default404Object) {
		this.default404Object = default404Object;
	}
	
	public void deleteDefault404Object() {
		this.default404Object = null;
	}

}
