package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

public class MediaSaveAsRequest extends WebServiceRequest {

	/** The name of the bucket containing the object to be media operated */
	private String sourceBucketName;

	/**
	 * The key in the source bucket under which the object to be media operated 
	 * is stored
	 */
	private String sourceKey;

	/** The name of the bucket to contain the copy of the source object */
	private String destinationBucketName;

	/**
	 * The key in the destination bucket under which the source object will be
	 * copied
	 */
	private String destinationKey;
	
	private String mediaOperation;

	public MediaSaveAsRequest() {
	}
	
	public MediaSaveAsRequest(String sourceBucketName, String sourceKey, String destinationBucketName,
			String destinationKey, String mediaOperation) {
		this.sourceBucketName = sourceBucketName;
		this.sourceKey = sourceKey;
		this.destinationBucketName = destinationBucketName;
		this.destinationKey = destinationKey;
		this.mediaOperation = mediaOperation;
	}

	public String getSourceBucketName() {
		return sourceBucketName;
	}

	public void setSourceBucketName(String sourceBucketName) {
		this.sourceBucketName = sourceBucketName;
	}

	public String getSourceKey() {
		return sourceKey;
	}

	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}

	public String getDestinationBucketName() {
		return destinationBucketName;
	}

	public void setDestinationBucketName(String destinationBucketName) {
		this.destinationBucketName = destinationBucketName;
	}

	public String getDestinationKey() {
		return destinationKey;
	}

	public void setDestinationKey(String destinationKey) {
		this.destinationKey = destinationKey;
	}

	public String getMediaOperation() {
		return mediaOperation;
	}

	public void setMediaOperation(String mediaOperation) {
		this.mediaOperation = mediaOperation;
	}

}
