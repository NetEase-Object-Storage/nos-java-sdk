package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;
import com.netease.cloud.services.nos.Nos;

/**
 * <p>
 * Provides options for deleting a specific version of an object in the
 * specified bucket. Once deleted, there is no method to restore or undelete an
 * object version. This is the only way to permanently delete object versions
 * that are protected by versioning.
 * </p>
 * <p>
 * Because deleting an object version is permanent and irreversible, it is a
 * privileged operation that only the owner of the bucket containing the version
 * may perform.
 * </p>
 * <p>
 * An owner can only delete a version of an object if the owner has enabled
 * versioning for their bucket. For more information about enabling versioning
 * for a bucket, see
 * {@link Nos#setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest)}
 * .
 * </p>
 * <p>
 * Note: When attempting to delete an object that does not exist, returns a
 * success message, not an error message.
 * </p>
 */
public class DeleteVersionRequest extends WebServiceRequest {

	/**
	 * The name of the bucket containing the version to delete.
	 */
	private String bucketName;

	/**
	 * The key of the object version to delete.
	 */
	private String key;

	/**
	 * The version ID uniquely identifying which version of the object to
	 * delete.
	 */
	private String versionId;


	/**
	 * Constructs a new {@link DeleteVersionRequest} object, ready to be
	 * executed to delete the version identified by the specified version ID, in
	 * the specified bucket and key.
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the version to delete.
	 * @param key
	 *            The key of the object version to delete.
	 * @param versionId
	 *            The version ID identifying the version to delete.
	 * 
	 * @see DeleteVersionRequest#DeleteVersionRequest(String, String, String,
	 *      MultiFactorAuthentication)
	 */
	public DeleteVersionRequest(String bucketName, String key, String versionId) {
		this.bucketName = bucketName;
		this.key = key;
		this.versionId = versionId;
	}


	/**
	 * Gets the name of the bucket containing the version to delete.
	 * 
	 * @return The name of the bucket containing the version to delete.
	 * 
	 * @see DeleteVersionRequest#setBucketName(String)
	 * @see DeleteVersionRequest#withBucketName(String)
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * Sets the name of the bucket containing the version to delete.
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the version to delete.
	 * 
	 * @see DeleteVersionRequest#getBucketName()
	 * @see DeleteVersionRequest#withBucketName(String)
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Sets the name of the bucket containing the version to delete. Returns
	 * this {@link DeleteVersionRequest}, enabling additional method calls to be
	 * chained together.
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the version to delete.
	 * 
	 * @return The updated {@link DeleteVersionRequest} object, enabling
	 *         additional method calls to be chained together.
	 * 
	 * @see DeleteVersionRequest#getBucketName()
	 * @see DeleteVersionRequest#setBucketName(String)
	 */
	public DeleteVersionRequest withBucketName(String bucketName) {
		setBucketName(bucketName);
		return this;
	}

	/**
	 * Gets the key of the version to delete.
	 * 
	 * @return The key of the version to delete.
	 * 
	 * @see DeleteVersionRequest#setKey(String)
	 * @see DeleteVersionRequest#withKey(String)
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key of the version to delete.
	 * 
	 * @param key
	 *            The key of the version to delete.
	 * 
	 * @see DeleteVersionRequest#getKey()
	 * @see DeleteVersionRequest#withKey(String)
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Sets the key of the version to delete Returns this
	 * {@link DeleteVersionRequest}, enabling additional method calls to be
	 * chained together.
	 * 
	 * @param key
	 *            The key of the version to delete.
	 * 
	 * @return This {@link DeleteVersionRequest}, enabling additional method
	 *         calls to be chained together.
	 * 
	 * @see DeleteVersionRequest#getKey()
	 * @see DeleteVersionRequest#setKey(String)
	 */
	public DeleteVersionRequest withKey(String key) {
		setKey(key);
		return this;
	}

	/**
	 * Gets the version ID uniquely identifying which version of the object to
	 * delete.
	 * 
	 * @return The version ID uniquely identifying which version of the object
	 *         to delete.
	 * 
	 * @see DeleteVersionRequest#setVersionId(String)
	 * @see DeleteVersionRequest#withVersionId(String)
	 */
	public String getVersionId() {
		return versionId;
	}

	/**
	 * Sets the version ID uniquely identifying which version of the object to
	 * delete.
	 * 
	 * @param versionId
	 *            The version ID uniquely identifying which version of the
	 *            object to delete.
	 * 
	 * @see DeleteVersionRequest#getVersionId()
	 * @see DeleteVersionRequest#withVersionId(String)
	 */
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	/**
	 * Sets the version ID uniquely identifying which version of the object to
	 * delete Returns this {@link DeleteVersionRequest}, enabling additional
	 * method calls to be chained together.
	 * 
	 * @param versionId
	 *            The version ID uniquely identifying which version of the
	 *            object to delete.
	 * 
	 * @return This {@link DeleteVersionRequest}, enabling additional method
	 *         calls to be chained together.
	 * 
	 * @see DeleteVersionRequest#getVersionId()
	 * @see DeleteVersionRequest#setVersionId(String)
	 */
	public DeleteVersionRequest withVersionId(String versionId) {
		setVersionId(versionId);
		return this;
	}

}
