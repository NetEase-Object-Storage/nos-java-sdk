package com.netease.cloud.services.nos.model;

import com.netease.cloud.services.nos.Nos;

/**
 * Represents the versioning configuration for a bucket.
 * <p>
 * A bucket's versioning configuration can be in one of three possible states:
 * <ul>
 * <li>{@link BucketVersioningConfiguration#DISABLED}
 * <li>{@link BucketVersioningConfiguration#ENABLED}
 * <li>{@link BucketVersioningConfiguration#SUSPENDED}
 * </ul>
 * </p>
 * <p>
 * By default, new buckets are in the
 * {@link BucketVersioningConfiguration#DISABLED off} state. Once versioning is
 * enabled for a bucket the status can never be reverted to
 * {@link BucketVersioningConfiguration#DISABLED off}.
 * </p>
 * <p>
 * The versioning configuration of a bucket has different implications for each
 * operation performed on that bucket or for objects within that bucket. For
 * instance, when versioning is enabled, a PutObject operation creates a unique
 * object version-id for the object being uploaded. The PutObject API guarantees
 * that, if versioning is enabled for a bucket at the time of the request, the
 * new object can only be permanently deleted using the DeleteVersion operation.
 * It can never be overwritten. Additionally, PutObject guarantees that, if
 * versioning is enabled for a bucket the request, no other object will be
 * overwritten by that request. Refer to the documentation sections for each API
 * for information on how versioning status affects the semantics of that
 * particular API.
 * <p>
 * Nos is eventually consistent. It may take time for the versioning status of a
 * bucket to be propagated throughout the system.
 * 
 * @see Nos#getBucketVersioningConfiguration(String)
 * @see Nos#setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest)
 */
public class BucketVersioningConfiguration {

	/**
	 * Nos bucket versioning status indicating that versioning is off for a
	 * bucket. By default, all buckets start off with versioning off. Once you
	 * enable versioning for a bucket, you can never set the status back to
	 * "Off". You can only suspend versioning on a bucket once you've enabled.
	 */
	public static final String DISABLED = "Disabled";

	/**
	 * Nos bucket versioning status indicating that versioning is suspended for
	 * a bucket. Use the "Suspended" status when you want to disable versioning
	 * on a bucket that has versioning enabled.
	 */
	public static final String SUSPENDED = "Suspended";

	/**
	 * Nos bucket versioning status indicating that versioning is enabled for a
	 * bucket.
	 */
	public static final String ENABLED = "Enabled";

	/** The current status of versioning */
	private String status;


	/**
	 * Creates a new bucket versioning configuration object which defaults to
	 * {@link #DISABLED} status.
	 */
	public BucketVersioningConfiguration() {
		setStatus(DISABLED);
	}

	/**
	 * Creates a new bucket versioning configuration object with the specified
	 * status.
	 * <p>
	 * Note that once versioning has been enabled for a bucket, its status can
	 * only be {@link #SUSPENDED suspended} and can never be set back to
	 * {@link #DISABLED off}.
	 * 
	 * @param status
	 *            The desired bucket versioning status for the new configuration
	 *            object.
	 * 
	 * @see #ENABLED
	 * @see #SUSPENDED
	 */
	public BucketVersioningConfiguration(String status) {
		setStatus(status);
	}

	/**
	 * Returns the current status of versioning for this bucket versioning
	 * configuration object, indicating if versioning is enabled or not for a
	 * bucket.
	 * 
	 * @return The current status of versioning for this bucket versioning
	 *         configuration.
	 * 
	 * @see #DISABLED
	 * @see #ENABLED
	 * @see #SUSPENDED
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the desired status of versioning for this bucket versioning
	 * configuration object.
	 * <p>
	 * Note that once versioning has been enabled for a bucket, its status can
	 * only be {@link #SUSPENDED suspended} and can never be set back to
	 * {@link #DISABLED off}.
	 * 
	 * @param status
	 *            The desired status of versioning for this bucket versioning
	 *            configuration.
	 * 
	 * @see #ENABLED
	 * @see #SUSPENDED
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Sets the desired status of versioning for this bucket versioning
	 * configuration object, and returns this object so that additional method
	 * calls may be chained together.
	 * <p>
	 * Note that once versioning has been enabled for a bucket, its status can
	 * only be {@link #SUSPENDED suspended} and can never be set back to
	 * {@link #DISABLED off}.
	 * 
	 * @param status
	 *            The desired status of versioning for this bucket versioning
	 *            configuration.
	 * 
	 * @return The updated BucketVersioningConfiguration object so that
	 *         additional method calls may be chained together.
	 * 
	 * @see #ENABLED
	 * @see #SUSPENDED
	 */
	public BucketVersioningConfiguration withStatus(String status) {
		setStatus(status);
		return this;
	}

}
