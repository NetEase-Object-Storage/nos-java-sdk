package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

/**
 * <p>
 * Provides options for deleting a specified bucket. buckets can only be deleted
 * when empty.
 * </p>
 * <p>
 * Note: When attempting to delete a bucket that does not exist, returns a
 * success message, not an error message.
 * </p>
 */
public class DeleteBucketRequest extends WebServiceRequest {

	/**
	 * The name of the bucket to delete.
	 */
	private String bucketName;

	/**
	 * Constructs a new {@link DeleteBucketRequest}, ready to be executed to
	 * delete the specified bucket.
	 * 
	 * @param bucketName
	 *            The name of the bucket to delete.
	 */
	public DeleteBucketRequest(String bucketName) {
		setBucketName(bucketName);
	}

	/**
	 * Sets the name of the bucket to delete.
	 * 
	 * @param bucketName
	 *            The name of the bucket to delete.
	 * 
	 * @see DeleteBucketRequest#getBucketName()
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Gets the name of the bucket to delete.
	 * 
	 * @return The name of the bucket to delete.
	 * 
	 * @see DeleteBucketRequest#setBucketName(String)
	 */
	public String getBucketName() {
		return bucketName;
	}
}
