package com.netease.cloud.services.nos.model;

/**
 * <p>
 * Contain the return data of Deduplicate operation, if the same object is
 * existed then isObjectExist=true ,otherwise is false.
 * </p>
 */
public class DeduplicateResult {

	private String bucketName;
	private String key;
	private boolean isObjectExist;

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

	public boolean isObjectExist() {
		return isObjectExist;
	}

	public void setObjectExist(boolean isObjectExist) {
		this.isObjectExist = isObjectExist;
	}

}
