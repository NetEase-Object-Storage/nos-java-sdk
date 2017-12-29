package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

/**
 * <p>
 * Provides options for moving an Nos object from a source location to a new
 * destination.
 * </p>
 */
public class MoveObjectRequest extends WebServiceRequest {
	/** The name of the bucket containing the object to be copied */
	private String sourceBucketName;

	/**
	 * The key in the source bucket under which the object to be copied is
	 * stored
	 */
	private String sourceKey;

	/**
	 * Optional version Id specifying which version of the source object to
	 * copy. If not specified, the most recent version of the source object will
	 * be copied.
	 * <p>
	 * For more information about enabling versioning for a bucket, see
	 * {@link Nos#setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest)}.
	 */
	//private String sourceVersionId;

	/** The name of the bucket to contain the copy of the source object */
	private String destinationBucketName;

	/**
	 * The key in the destination bucket under which the source object will be
	 * copied
	 */
	private String destinationKey;

	/**
	 * <p>
	 * Constructs a new
	 * {@link com.netease.cloud.services.nos.model#CopyObjectRequest} with only
	 * basic options.
	 * </p>
	 * 
	 * @param sourceBucketName
	 *            The name of the Nos bucket containing the object to copy.
	 * @param sourceKey
	 *            The source bucket key under which the object to copy is
	 *            stored.
	 * @param destinationBucketName
	 *            The name of the Nos bucket to which the new object will be
	 *            copied.
	 * @param destinationKey
	 *            The destination bucket key under which the new object will be
	 *            copied.
	 * 
	 * @see CopyObjectRequest#CopyObjectRequest(String, String, String, String,
	 *      String)
	 */
	public MoveObjectRequest(String sourceBucketName, String sourceKey,
			String destinationBucketName, String destinationKey) {
		this.sourceBucketName = sourceBucketName;
		this.sourceKey = sourceKey;
		//this.sourceVersionId = sourceVersionId;
		this.destinationBucketName = destinationBucketName;
		this.destinationKey = destinationKey;
	}

	/**
	 * Gets the name of the bucket containing the source object to be copied.
	 * 
	 * @return The name of the bucket containing the source object to be copied.
	 * 
	 * @see CopyObjectRequest#setSourceBucketName(String sourceBucketName)
	 */
	public String getSourceBucketName() {
		return sourceBucketName;
	}

	/**
	 * Sets the name of the bucket containing the source object to be copied.
	 * 
	 * @param sourceBucketName
	 *            The name of the bucket containing the source object to be
	 *            copied.
	 * @see CopyObjectRequest#getSourceBucketName()
	 */
	public void setSourceBucketName(String sourceBucketName) {
		this.sourceBucketName = sourceBucketName;
	}

	/**
	 * Sets the name of the bucket containing the source object to be copied,
	 * and returns this object, enabling additional method calls to be chained
	 * together.
	 * 
	 * @param sourceBucketName
	 *            The name of the bucket containing the source object to be
	 *            copied.
	 * 
	 * @return This <code>CopyObjectRequest</code> instance, enabling additional
	 *         method calls to be chained together.
	 */
	public MoveObjectRequest withSourceBucketName(String sourceBucketName) {
		setSourceBucketName(sourceBucketName);
		return this;
	}

	/**
	 * Gets the source bucket key under which the source object to be copied is
	 * stored.
	 * 
	 * @return The source bucket key under which the source object to be copied
	 *         is stored.
	 * 
	 * @see CopyObjectRequest#setSourceKey(String sourceKey)
	 */
	public String getSourceKey() {
		return sourceKey;
	}

	/**
	 * Sets the source bucket key under which the source object to be copied is
	 * stored.
	 * 
	 * @param sourceKey
	 *            The source bucket key under which the source object to be
	 *            copied is stored.
	 * 
	 * @see CopyObjectRequest#setSourceKey(String sourceKey)
	 */
	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}

	/**
	 * Sets the key in the source bucket under which the source object to be
	 * copied is stored and returns this object, enabling additional method
	 * calls to be chained together.
	 * 
	 * @param sourceKey
	 *            The key in the source bucket under which the source object to
	 *            be copied is stored.
	 * 
	 * @return This <code>CopyObjectRequest</code> instance, enabling additional
	 *         method calls to be chained together.
	 */
	public MoveObjectRequest withSourceKey(String sourceKey) {
		setSourceKey(sourceKey);
		return this;
	}

	/**
	 * <p>
	 * Gets the version ID specifying which version of the source object to
	 * copy. If not specified, the most recent version of the source object will
	 * be copied.
	 * </p>
	 * <p>
	 * Objects created before enabling versioning or when versioning is
	 * suspended are given the default <code>null</code> version ID (see
	 * {@link Constants#NULL_VERSION_ID}). Note that the <code>null</code>
	 * version ID is a valid version ID and is not the same as not having a
	 * version ID.
	 * </p>
	 * <p>
	 * For more information about enabling versioning for a bucket, see
	 * {@link Nos#setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest)}
	 * .
	 * </p>
	 * 
	 * @return The version ID specifying which version of the source object to
	 *         copy.
	 * 
	 * 
	 * @see Constants#NULL_VERSION_ID
	 * @see CopyObjectRequest#setSourceVersionId(String sourceVersionId)
	 */
	/*public String getSourceVersionId() {
		return sourceVersionId;
	}*/

	/**
	 * <p>
	 * Sets the optional version ID specifying which version of the source
	 * object to copy. If not specified, the most recent version of the source
	 * object will be copied.
	 * </p>
	 * <p>
	 * Objects created before enabling versioning or when versioning is
	 * suspended are given the default <code>null</code> version ID (see
	 * {@link Constants#NULL_VERSION_ID}). Note that the <code>null</code>
	 * version ID is a valid version ID and is not the same as not having a
	 * version ID.
	 * </p>
	 * <p>
	 * For more information about enabling versioning for a bucket, see
	 * {@link Nos#setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest)}
	 * .
	 * </p>
	 * 
	 * @param sourceVersionId
	 *            The optional version ID specifying which version of the source
	 *            object to copy.
	 */
	/*public void setSourceVersionId(String sourceVersionId) {
		this.sourceVersionId = sourceVersionId;
	}*/

	/**
	 * <p>
	 * Sets the optional version ID specifying which version of the source
	 * object to copy and returns this object, enabling additional method calls
	 * to be chained together. If not specified, the most recent version of the
	 * source object will be copied.
	 * </p>
	 * <p>
	 * Objects created before enabling versioning or when versioning is
	 * suspended are given the default <code>null</code> version ID (see
	 * {@link Constants#NULL_VERSION_ID}). Note that the <code>null</code>
	 * version ID is a valid version ID and is not the same as not having a
	 * version ID.
	 * </p>
	 * <p>
	 * For more information about enabling versioning for a bucket, see
	 * {@link Nos#setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest)}
	 * .
	 * </p>
	 * 
	 * @param sourceVersionId
	 *            The optional version ID specifying which version of the source
	 *            object to copy.
	 * 
	 * @return This <code>CopyObjectRequest</code>, enabling additional method
	 *         calls to be chained together.
	 */
	/*public MoveObjectRequest withSourceVersionId(String sourceVersionId) {
		setSourceVersionId(sourceVersionId);
		return this;
	}*/

	/**
	 * Gets the destination bucket name which will contain the new, copied
	 * object.
	 * 
	 * @return The name of the destination bucket which will contain the new,
	 *         copied object.
	 * 
	 * @see CopyObjectRequest#setDestinationBucketName(String
	 *      destinationBucketName)
	 */
	public String getDestinationBucketName() {
		return destinationBucketName;
	}

	/**
	 * Sets the destination bucket name which will contain the new, copied
	 * object.
	 * 
	 * @param destinationBucketName
	 *            The name of the destination bucket which will contain the new,
	 *            copied object.
	 * 
	 * @see CopyObjectRequest#getDestinationBucketName()
	 */
	public void setDestinationBucketName(String destinationBucketName) {
		this.destinationBucketName = destinationBucketName;
	}

	/**
	 * Sets the name of the destination bucket which will contain the new,
	 * copied object and returns this object, enabling additional method calls
	 * to be chained together.
	 * 
	 * @param destinationBucketName
	 *            The name of the destination bucket which will contain the new,
	 *            copied object.
	 * 
	 * @return This <code>CopyObjectRequest</code>, enabling additional method
	 *         calls to be chained together.
	 */
	public MoveObjectRequest withDestinationBucketName(String destinationBucketName) {
		setDestinationBucketName(destinationBucketName);
		return this;
	}

	/**
	 * Gets the destination bucket key under which the new, copied object will
	 * be stored.
	 * 
	 * @return The destination bucket key under which the new, copied object
	 *         will be stored.
	 * 
	 * @see CopyObjectRequest#setDestinationKey(String destinationKey)
	 */
	public String getDestinationKey() {
		return destinationKey;
	}

	/**
	 * Sets the destination bucket key under which the new, copied object will
	 * be stored.
	 * 
	 * @param destinationKey
	 *            The destination bucket key under which the new, copied object
	 *            will be stored.
	 * 
	 * @see CopyObjectRequest#getDestinationKey()
	 */
	public void setDestinationKey(String destinationKey) {
		this.destinationKey = destinationKey;
	}

	/**
	 * Sets the destination bucket key under which the new, copied object will
	 * be stored and returns this object, enabling additional method calls can
	 * be chained together.
	 * 
	 * @param destinationKey
	 *            The destination bucket key under which the new, copied object
	 *            will be stored.
	 * 
	 * @return This <code>CopyObjectRequest</code>, enabling additional method
	 *         calls to be chained together.
	 */
	public MoveObjectRequest withDestinationKey(String destinationKey) {
		setDestinationKey(destinationKey);
		return this;
	}

}
