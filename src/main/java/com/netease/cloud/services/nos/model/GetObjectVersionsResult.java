package com.netease.cloud.services.nos.model;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Contain the return data of get object history versions operation.
 * </p>
 */
public class GetObjectVersionsResult {

	/**
	 * A list of summary information describing the objects stored in the bucket
	 */
	private List<NOSVersionSummary> versionSummary = new ArrayList<NOSVersionSummary>();

	/** The name of the bucket containing the listed objects */
	private String bucketName;

	private String key;

	public List<NOSVersionSummary> getVersionSummary() {
		return versionSummary;
	}

	public void setVersionSummary(List<NOSVersionSummary> versionSummary) {
		this.versionSummary = versionSummary;
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
