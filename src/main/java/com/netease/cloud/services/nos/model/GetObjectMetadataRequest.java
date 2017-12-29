package com.netease.cloud.services.nos.model;

import java.util.Date;

import com.netease.cloud.WebServiceRequest;
import com.netease.cloud.services.nos.Nos;

/**
 * <p>
 * Provides options for obtaining the metadata for the specified object without
 * actually fetching the object contents. This is useful if obtaining only
 * object metadata, and avoids wasting bandwidth from retrieving the object
 * data.
 * </p>
 * <p>
 * The object metadata contains information such as content type, content
 * disposition, etc., as well as custom user metadata that can be associated
 * with an object in .
 * </p>
 * <p>
 * For more information about enabling versioning for a bucket, see
 * {@link Nos#setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest)}
 * .
 * </p>
 * 
 * @see GetObjectMetadataRequest#GetObjectMetadataRequest(String, String)
 * @see GetObjectMetadataRequest#GetObjectMetadataRequest(String, String,
 *      String)
 * @see GetObjectRequest
 */
public class GetObjectMetadataRequest extends WebServiceRequest {

	/**
	 * The name of the bucket containing the object's whose metadata is being
	 * retrieved.
	 */
	private String bucketName;

	/**
	 * The key of the object whose metadata is being retrieved.
	 */
	private String key;

	/**
	 * The optional version ID of the object version whose metadata is being
	 * retrieved. If not specified, the latest version will be used.
	 */
	//private String versionId;

	/**
	 * Optional field that constrains this request to only be executed if the
	 * object has been modified since the specified date.
	 */
	private Date modifiedSinceConstraint;

	/**
	 * Constructs a new {@link GetObjectMetadataRequest} used to retrieve a
	 * specified object's metadata.
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the object whose metadata is
	 *            being retrieved.
	 * @param key
	 *            The key of the object whose metadata is being retrieved.
	 * 
	 * @see GetObjectMetadataRequest#GetObjectMetadataRequest(String bucketName,
	 *      String key, String versionId)
	 */
	public GetObjectMetadataRequest(String bucketName, String key) {
		setBucketName(bucketName);
		setKey(key);
	}

	/**
	 * Gets the name of the bucket containing the object whose metadata is being
	 * retrieved.
	 * 
	 * @return The name of the bucket containing the object whose metadata is
	 *         being retrieved.
	 * 
	 * @see GetObjectMetadataRequest#setBucketName(String bucketName)
	 * @see GetObjectMetadataRequest#withBucketName(String)
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * Sets the name of the bucket containing the object whose metadata is being
	 * retrieved.
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the object's whose metadata
	 *            is being retrieved.
	 * 
	 * @see GetObjectMetadataRequest#getBucketName()
	 * @see GetObjectMetadataRequest#withBucketName(String)
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Sets the name of the bucket containing the object whose metadata is being
	 * retrieved. Returns this {@link GetObjectMetadataRequest}, enabling
	 * additional method calls to be chained together.
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the object's whose metadata
	 *            is being retrieved.
	 * 
	 * @return This {@link GetObjectMetadataRequest}, enabling additional method
	 *         calls to be chained together.
	 * 
	 * @see GetObjectMetadataRequest#getBucketName()
	 * @see GetObjectMetadataRequest#setBucketName(String bucketName)
	 */
	public GetObjectMetadataRequest withBucketName(String bucketName) {
		setBucketName(bucketName);
		return this;
	}

	/**
	 * Gets the key of the object whose metadata is being retrieved.
	 * 
	 * @return The key of the object whose metadata is being retrieved.
	 * 
	 * @see GetObjectMetadataRequest#setKey(String)
	 * @see GetObjectMetadataRequest#withKey(String)
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key of the object whose metadata is being retrieved.
	 * 
	 * @param key
	 *            The key of the object whose metadata is being retrieved.
	 * 
	 * @see GetObjectMetadataRequest#getKey()
	 * @see GetObjectMetadataRequest#withKey(String)
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Sets the key of the object whose metadata is being retrieved. Returns
	 * this {@link GetObjectMetadataRequest}, enabling additional method calls
	 * to be chained together.
	 * 
	 * @param key
	 *            The key of the object whose metadata is being retrieved.
	 * 
	 * @return This {@link GetObjectMetadataRequest}, enabling additional method
	 *         calls to be chained together.
	 * 
	 * @see GetObjectMetadataRequest#getKey()
	 * @see GetObjectMetadataRequest#setKey(String)
	 */
	public GetObjectMetadataRequest withKey(String key) {
		setKey(key);
		return this;
	}

	/**
	 * Gets the optional modified constraint that restricts this request to
	 * executing only if the object <b>has</b> been modified after the specified
	 * date.
	 * 
	 * @return The optional modified constraint that restricts this request to
	 *         executing only if the object <b>has</b> been modified after the
	 *         specified date.
	 * 
	 * @see GetObjectRequest#setModifiedSinceConstraint(Date)
	 * @see GetObjectRequest#withModifiedSinceConstraint(Date)
	 */
	public Date getModifiedSinceConstraint() {
		return modifiedSinceConstraint;
	}

	/**
	 * Sets the optional modified constraint that restricts this request to
	 * executing only if the object <b>has</b> been modified after the specified
	 * date.
	 * <p>
	 * Note that Nos will ignore any dates occurring in the future.
	 * </p>
	 * 
	 * @param date
	 *            The modified constraint that restricts this request to
	 *            executing only if the object <b>has</b> been modified after
	 *            the specified date.
	 * 
	 * @see GetObjectRequest#getModifiedSinceConstraint()
	 * @see GetObjectRequest#withModifiedSinceConstraint(Date)
	 */
	public void setModifiedSinceConstraint(Date date) {
		this.modifiedSinceConstraint = date;
	}

}
