package com.netease.cloud.services.nos.model;

/**
 * Specifies constants defining Deduplicate Status
 * 
 */
public enum DeduplicateStatus {

	/** Means the bucket deduplicate function is used. **/
	Enabled("enabled"),

	/** Means the bucket deduplicate function is closed. **/
	Disabled("disabled"),

	/** Means the bucket deduplicate function is closed. **/
	Suspended("suspended");
	private final String deduplicate;

	private DeduplicateStatus(String deduplicate) {
		this.deduplicate = deduplicate;
	}

	public String toString() {
		return this.deduplicate;
	}

}
