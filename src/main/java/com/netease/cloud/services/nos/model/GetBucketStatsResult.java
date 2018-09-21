package com.netease.cloud.services.nos.model;

public class GetBucketStatsResult {
	private String bucketName;
	private long objectCount;
	private long storageCapacity;
	private double deduplicationRate;
	
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	
	public String getBucketName() {
		return this.bucketName;
	}
	
	public void setObjectCount(long objectCount) {
		this.objectCount = objectCount;
	}
	
	public long getObjectCount() {
		return this.objectCount;
	}
	
	public void setStorageCapacity(long storageCapacity) {
		this.storageCapacity = storageCapacity;
	}
	
	public long getStorageCapacity() {
		return this.storageCapacity;
	}
	
	public void setDeduplicationRate(double deduplicationRate) {
		this.deduplicationRate = deduplicationRate;
	}
	
	public double getDeduplicationRate() {
		return this.deduplicationRate;
	}
}
