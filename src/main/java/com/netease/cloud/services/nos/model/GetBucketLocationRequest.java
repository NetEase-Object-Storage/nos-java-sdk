package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

/**
 * Provides options for requesting an Nos bucket's location. You can choose a
 * bucket's location when creating it to ensure that the data stored in your
 * bucket is geographically close to the applications or customers accessing
 * your data.
 * 
 * @see CreateBucketRequest
 */
public class GetBucketLocationRequest extends WebServiceRequest {

	/** The name of the bucket whose location is being requested. */
	private String bucketName;

	/**
	 * Constructs a new request object to create a new bucket with the specified
	 * name.
	 * <p>
	 * When choosing a bucket name, keep in mind that Nos bucket names are
	 * globally unique.
	 * 
	 * @param bucketName
	 *            The name for the new bucket.
	 */
	public GetBucketLocationRequest(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Returns the name of the bucket whose location is being requested.
	 * 
	 * @return The name of the bucket whose location is being requested.
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * Sets the name of the bucket whose location is being requested.
	 * 
	 * @param bucketName
	 *            The name of the bucket whose location is being requested.
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Sets the name of the bucket whose location is being requested, and
	 * returns this updated object so that additional method calls can be
	 * chained together.
	 * 
	 * @param bucketName
	 *            The name of the bucket whose location is being requested.
	 * 
	 * @return This updated object, so that additional method calls can be
	 *         chained together.
	 */
	public GetBucketLocationRequest withBucketName(String bucketName) {
		setBucketName(bucketName);
		return this;
	}
}
