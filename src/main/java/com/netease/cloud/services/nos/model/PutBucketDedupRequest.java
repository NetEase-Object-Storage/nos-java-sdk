package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

public class PutBucketDedupRequest extends WebServiceRequest{
	   
	/** The name of the Nos bucket to create. */
    private String bucketName;
    
    /**the policy of object deduplicate**/
    private String dedupStatus;

    public PutBucketDedupRequest(String bucketName, String dedupStatus){
    	setBucketName(bucketName);
    	setDedupStatus(dedupStatus);
    }
    
    public PutBucketDedupRequest(String bucketName, DeduplicateStatus dedupStatus){
    	this(bucketName, dedupStatus.toString());
    }
    
	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getDedupStatus() {
		return dedupStatus;
	}

	public void setDedupStatus(String dedupStatus) {
		this.dedupStatus = dedupStatus;
	}
    

}
