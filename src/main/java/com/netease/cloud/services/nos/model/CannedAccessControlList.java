package com.netease.cloud.services.nos.model;

/**
 * Specifies constants defining a canned access control list.
 * <p>
 * Canned access control lists are commonly used access control lists (ACL) that
 * can be used as a shortcut when applying an access control list to Nos buckets
 * and objects. Only a few commonly used configurations are available, but they
 * offer an alternative to manually creating a custom ACL. If more specific
 * access control is desired, users can create a custom
 * </p>
 * 
 */
public enum CannedAccessControlList {
	/**
	 * <p>
	 * This is the default access control policy for any new buckets or objects.
	 * </p>
	 */
	Private("private"),

	/**
	 * <p>
	 * If this policy is used on an object, it can be read from a browser
	 * without authentication.
	 * </p>
	 */
	PublicRead("public-read"),

	/**
	 * <p>
	 * This access policy is not recommended for general use.
	 * </p>
	 */
	// PublicReadWrite("public-read-write"),

	// AuthenticatedRead("authenticated-read"),

	/**
	 * <p>
	 * Use this access policy to enable Nos bucket logging for a bucket. The
	 * destination bucket requires these permissions so that access logs can be
	 * delivered.
	 * </p>
	 */
	// LogDeliveryWrite("log-delivery-write"),

	/**
	 * Specifies the owner of the bucket, but not necessarily the same as the
	 * owner of the object, is granted {@link Permission#Read}.
	 * <p>
	 * Use this access policy when uploading objects to another owner's bucket.
	 * This access policy grants the bucket owner read access to the object, but
	 * does not give read access for all users.
	 * </p>
	 */
	// BucketOwnerRead("bucket-owner-read"),

	/**
	 * Specifies the owner of the bucket, but not necessarily the same as the
	 * owner of the object, is granted {@link Permission#FullControl}.
	 * <p>
	 * Use this access policy to upload objects to another owner's bucket. This
	 * access policy grants the bucket owner full access to the object, but does
	 * not give full access to all users.
	 * </p>
	 */
	// BucketOwnerFullControl("bucket-owner-full-control")
	;

	/** The Nos x-nos-acl header value representing the canned acl */
	private final String cannedAclHeader;

	private CannedAccessControlList(String cannedAclHeader) {
		this.cannedAclHeader = cannedAclHeader;
	}

	/**
	 * Returns the Nos x-nos-acl header value for this canned acl.
	 */
	public String toString() {
		return cannedAclHeader;
	}

}
