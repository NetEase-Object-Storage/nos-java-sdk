package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

/**
 * <p>
 * Provides options for deleting a specified object in a specified bucket. Once
 * deleted, the object can only be restored if versioning was enabled when the
 * object was deleted.
 * </p>
 * <p>
 * Note: If deleting an object that does not exist, returns a success message,
 * not an error message.
 * </p>
 * 
 */
public class DeleteObjectRequest extends WebServiceRequest {

	/**
	 * The name of the bucket containing the object to delete.
	 */
	private String bucketName;

	/**
	 * The key of the object to delete.
	 */
	private String key;

	/**
	 * the version id of the object to delete
	 */
	//private String versionId;

	public DeleteObjectRequest(String bucketName, String key) {
		setBucketName(bucketName);
		setKey(key);
		//setVersionId(null);
	}

	/**
	 * Gets the name of the bucket containing the object to delete.
	 * 
	 * @return The name of the bucket containing the object to delete.
	 * 
	 * @see DeleteObjectRequest#setBucketName(String)
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * Sets the name of the bucket containing the object to delete.
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the object to delete.
	 * @see DeleteObjectRequest#getBucketName()
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Sets the name of the bucket containing the object to delete and returns
	 * this object, enabling additional method calls to be chained together.
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the object to delete.
	 * 
	 * @return The updated {@link DeleteObjectRequest} object, enabling
	 *         additional method calls to be chained together.
	 */
	public DeleteObjectRequest withBucketName(String bucketName) {
		setBucketName(bucketName);
		return this;
	}

	/**
	 * Gets the key of the object to delete.
	 * 
	 * @return The key of the object to delete.
	 * 
	 * @see DeleteObjectRequest#setKey(String)
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key of the object to delete.
	 * 
	 * @param key
	 *            The key of the object to delete.
	 * 
	 * @see DeleteObjectRequest#getKey()
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Sets the key of the object to delete and returns this object, enabling
	 * additional method calls to be chained together.
	 * 
	 * @param key
	 *            The key of the object to delete.
	 * 
	 * @return The updated {@link DeleteObjectRequest} object, enabling
	 *         additional method calls to chained together.
	 */
	public DeleteObjectRequest withKey(String key) {
		setKey(key);
		return this;
	}

}
