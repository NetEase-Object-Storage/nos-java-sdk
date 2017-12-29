package com.netease.cloud.services.nos;

/**
 * Common NOS HTTP header values used throughout the NOS Java client.
 */
public interface Headers {

	/*
	 * Standard HTTP Headers
	 */
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String CONTENT_MD5 = "Content-MD5";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String DATE = "Date";
	public static final String ETAG = "ETag";
	public static final String LAST_MODIFIED = "Last-Modified";

	/*
	 * HTTP Headers
	 */

	/** Prefix for general headers: x-nos- */
	public static final String NOS_PREFIX = "x-nos-";

	/** NOS's canned ACL header: x-nos-acl */
	public static final String NOS_CANNED_ACL = "x-nos-acl";

	/** NOS's alternative date header: x-nos-date */
	public static final String NOS_ALTERNATE_DATE = "x-nos-date";

	/** Prefix for NOS user metadata: x-nos-meta- */
	public static final String NOS_USER_METADATA_PREFIX = "x-nos-meta-";

	/** NOS's version ID header */
	public static final String NOS_VERSION_ID = "x-nos-version-id";

	/** NOS response header for a request's request ID */
	public static final String REQUEST_ID = "x-nos-request-id";

	/** DevPay token header */
	public static final String SECURITY_TOKEN = "x-nos-security-token";

	/** Header describing what class of storage a user wants */
	public static final String STORAGE_CLASS = "x-nos-storage-class";

	/** Header for optional object expiration */
	public static final String EXPIRATION = "x-nos-expiration";

	/** Range header for the get object request */
	public static final String RANGE = "Range";

	/** Modified since constraint header for the get object request */
	public static final String GET_OBJECT_IF_MODIFIED_SINCE = "If-Modified-Since";

	public static final String X_NOS_OBJECT_MD5 = "x-nos-object-md5";
	
	public static final String X_NOS_OBJECT_NAME = "x-nos-object-name";
	
	public static final String X_NOS_CALLBACK_RET = "x-nos-callback-ret";
}
