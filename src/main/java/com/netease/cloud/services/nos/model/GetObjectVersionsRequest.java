package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

/**
 * <p>
 * Provides options for returning a list of summary information about the
 * versions in a specified bucket.
 * <p>
 */
public class GetObjectVersionsRequest extends WebServiceRequest {
	
    /** The name of the bucket containing the object to retrieve */
    private String bucketName;
    
    /** The key under which the desired object is stored */
    private String key;
    
    public GetObjectVersionsRequest(String bucketName, String key){
    	setBucketName(bucketName);
    	setKey(key);
    }

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
    
    
}
