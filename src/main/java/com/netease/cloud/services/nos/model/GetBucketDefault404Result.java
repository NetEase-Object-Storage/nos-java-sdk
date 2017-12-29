package com.netease.cloud.services.nos.model;

/**
 * <p>
 * Contain the return data of Get bucket default 404 operation.
 * </p>
 */
public class GetBucketDefault404Result {

	/**
	 * Specify the default 404 object name.
	 */
	private String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
}
