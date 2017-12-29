package com.netease.cloud.services.nos.internal;

/**
 * Constants used by the NOS Java client.
 */
public class Constants {

	/** Default hostname for the NOS service endpoint */
	public static String NOS_HOST_NAME = "nos-eastchina1.126.net";

	public static String PROJECT_NAME = "/";

	/** Service name for Netease cloud NOS */
	public static String NOS_SERVICE_NAME = "Netease NOS";

	/** Default encoding used for text data */
	public static String DEFAULT_ENCODING = "UTF-8";

	/** HMAC/SHA1 Algorithm per RFC 2104, used when signing NOS requests */
	public static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	/** XML namespace URL used when sending NOS requests containing XML */
	public static final String XML_NAMESPACE = "http://nos.netease.com//";

	/** Represents a null NOS version ID */
	public static final String NULL_VERSION_ID = "null";

	/**
	 * HTTP status code indicating that preconditions failed and thus the
	 * request failed.
	 */
	public static final int FAILED_PRECONDITION_STATUS_CODE = 412;

	/** Kilobytes */
	public static final int KB = 1024;

	/** Megabytes */
	public static final int MB = 1024 * KB;

	/** Gigabytes */
	public static final long GB = 1024 * MB;

	/** The maximum allowed parts in a multipart upload. */
	public static final int MAXIMUM_UPLOAD_PARTS = 10000;

	/**
	 * The default size of the buffer when uploading data from a stream. A
	 * buffer of this size will be created and filled with the first bytes from
	 * a stream being uploaded so that any transmit errors that occur in that
	 * section of the data can be automatically retried without the caller's
	 * intervention.
	 */
	public static final int DEFAULT_STREAM_BUFFER_SIZE = 128 * KB;

	/**
	 * The default size of the buffer when uploading part from a stream ,also is
	 * the min size of part(not including the last part).
	 */
	public static final int DEFAULT_BUFFER_SIZE = 5 * 1024 * KB;
	/**
	 * debug log id param name
	 */
	public static final String PARAM_LOG_ID = "logid";
	/**
	 * debug log sequence param name
	 */
	public static final String PARAM_LOG_SEQ = "logseq";
}
