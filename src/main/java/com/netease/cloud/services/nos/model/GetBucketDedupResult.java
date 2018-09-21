package com.netease.cloud.services.nos.model;
/**
 * <p>
 * Contain the return data of Get Deduplicate operation.
 * </p>
 */
public class GetBucketDedupResult {

	/**
	 * Specify the Deduplicate status.
	 */
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
