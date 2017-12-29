package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

/**
 * Provides options for creating an bucket.
 * 
 * @see DeleteBucketRequest
 * @see CopyObjectRequest
 */
public class CreateBucketRequest extends WebServiceRequest {

	/** The name of the bucket to create. */
	private String bucketName;

	/** The name of the region in which to create this bucket. */
	private String region;

	/** The optional Canned ACL to set for the new bucket. */
	private CannedAccessControlList cannedAcl;

	/**
	 * Constructs a new {@link CreateBucketRequest}, ready to be executed to
	 * create the specified bucket in the <code>US_Standard</code> region.
	 * 
	 * @param bucketName
	 *            The name of the bucket to create.
	 * 
	 */
	public CreateBucketRequest(String bucketName) {
		this(bucketName, Region.CN_Hnagzhou);
	}

	/**
	 * Constructs a new {@link CreateBucketRequest}, ready to be executed to
	 * create the specified bucket in the specified region.
	 * 
	 * @param bucketName
	 *            The name of the bucket to create.
	 * @param region
	 *            The region in which to create this bucket.
	 * 
	 */
	public CreateBucketRequest(String bucketName, Region region) {
		this(bucketName, region.toString());
	}

	/**
	 * Constructs a new {@link CreateBucketRequest}, ready to be executed and
	 * create the specified bucket in the specified region.
	 * 
	 * @param bucketName
	 *            The name of the bucket to create.
	 * @param region
	 *            The region in which to create this bucket.
	 * 
	 */
	public CreateBucketRequest(String bucketName, String region) {
		setBucketName(bucketName);
		setRegion(region);
	}

	/**
	 * Sets the name of the bucket to create.
	 * 
	 * @param bucketName
	 *            The name of the bucket to create.
	 * 
	 * @see CreateBucketRequest#getBucketName()
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Gets the name of the bucket to create.
	 * 
	 * @return The name of the bucket to create.
	 * 
	 * @see CreateBucketRequest#setBucketName(String)
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * Sets the name of the region in which this bucket will be created.
	 * 
	 * @param region
	 *            The name of the region in which this bucket will be created.
	 * 
	 * @see CreateBucketRequest#getRegion()
	 */
	public void setRegion(String region) {
		this.region = region;
	}

	/**
	 * Gets the name of the region in which this bucket will be created.
	 * 
	 * @return The name of the region in which this bucket will be created.
	 * 
	 * @see CreateBucketRequest#setRegion(String)
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * Returns the optional Canned ACL to set for the new bucket.
	 * 
	 * @return The optional Canned ACL to set for the new bucket.
	 */
	public CannedAccessControlList getCannedAcl() {
		return cannedAcl;
	}

	/**
	 * Sets the optional Canned ACL to set for the new bucket.
	 * 
	 * @param cannedAcl
	 *            The optional Canned ACL to set for the new bucket.
	 */
	public void setCannedAcl(CannedAccessControlList cannedAcl) {
		this.cannedAcl = cannedAcl;
	}

	public void setCannedAcl(String cannedAcl) {
		if (cannedAcl != null) {
			boolean isRightAcl = false;
			for (CannedAccessControlList acl : CannedAccessControlList.values()) {
				if (acl.toString().equals(cannedAcl)) {
					this.cannedAcl = acl;
					isRightAcl = true;
					break;
				}
			}
			if (!isRightAcl) {
				throw new IllegalArgumentException("ACL is out of bond.");
			}
		}
	}

	/**
	 * Sets the optional Canned ACL to set for the new bucket, and returns this
	 * updated object so that additional method calls can be chained together.
	 * 
	 * @param cannedAcl
	 *            The optional Canned ACL to set for the new bucket.
	 * 
	 * @return This updated object, so that additional method calls can be
	 *         chained together.
	 */
	public CreateBucketRequest withCannedAcl(CannedAccessControlList cannedAcl) {
		setCannedAcl(cannedAcl);
		return this;
	}
}
