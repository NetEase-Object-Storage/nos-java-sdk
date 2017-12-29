package com.netease.cloud.services.nos.model;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.netease.cloud.services.nos.Headers;
import com.netease.cloud.services.nos.internal.ObjectExpirationResult;

/**
 * Represents the object metadata that is stored with Nos. This includes custom
 * user-supplied metadata, as well as the standard HTTP headers that Nos sends
 * and receives (Content-Length, ETag, Content-MD5, etc.).
 */
public class ObjectMetadata implements ObjectExpirationResult {

	/*
	 * TODO: Might be nice to get as many of the internal use only methods out
	 * of here so users never even see them. Example: we could set the ETag
	 * header directly through the raw metadata map instead of having a setter
	 * for it.
	 */

	/**
	 * Custom user metadata, represented in responses with the x-amz-meta-
	 * header prefix
	 */
	private Map<String, String> userMetadata = new HashMap<String, String>();

	/**
	 * All other (non user custom) headers such as Content-Length, Content-Type,
	 * etc.
	 */
	private Map<String, Object> metadata = new HashMap<String, Object>();

	public static final String AES_256_SERVER_SIDE_ENCRYPTION = "AES256";

	/**
	 * The time this object expires, or null if it has no expiration.
	 * <p>
	 * This and the expiration time rule aren't stored in the metadata map
	 * because the header contains both the time and the rule.
	 */
	private Date expirationTime;

	/** The expiration rule for this object */
	private String expirationTimeRuleId;

	/**
	 * <p>
	 * Gets the custom user-metadata for the associated object.
	 * </p>
	 * <p>
	 * Nos can store additional metadata on objects by internally representing
	 * it as HTTP headers prefixed with "x-amz-meta-". Use user-metadata to
	 * store arbitrary metadata alongside their data in Nos. When setting user
	 * metadata, callers <i>should not</i> include the internal "x-amz-meta-"
	 * prefix; this library will handle that for them. Likewise, when callers
	 * retrieve custom user-metadata, they will not see the "x-amz-meta-" header
	 * prefix.
	 * </p>
	 * <p>
	 * User-metadata keys are <b>case insensitive</b> and will be returned as
	 * lowercase strings, even if they were originally specified with uppercase
	 * strings.
	 * </p>
	 * <p>
	 * Note that user-metadata for an object is limited by the HTTP request
	 * header limit. All HTTP headers included in a request (including user
	 * metadata headers and other standard HTTP headers) must be less than 8KB.
	 * </p>
	 * 
	 * @return The custom user metadata for the associated object.
	 * 
	 * @see ObjectMetadata#setUserMetadata(Map)
	 * @see ObjectMetadata#addUserMetadata(String, String)
	 */
	public Map<String, String> getUserMetadata() {
		return userMetadata;
	}

	/**
	 * <p>
	 * Sets the custom user-metadata for the associated object.
	 * </p>
	 * <p>
	 * Nos can store additional metadata on objects by internally representing
	 * it as HTTP headers prefixed with "x-amz-meta-". Use user-metadata to
	 * store arbitrary metadata alongside their data in Nos. When setting user
	 * metadata, callers <i>should not</i> include the internal "x-amz-meta-"
	 * prefix; this library will handle that for them. Likewise, when callers
	 * retrieve custom user-metadata, they will not see the "x-amz-meta-" header
	 * prefix.
	 * </p>
	 * <p>
	 * User-metadata keys are <b>case insensitive</b> and will be returned as
	 * lowercase strings, even if they were originally specified with uppercase
	 * strings.
	 * </p>
	 * <p>
	 * Note that user-metadata for an object is limited by the HTTP request
	 * header limit. All HTTP headers included in a request (including user
	 * metadata headers and other standard HTTP headers) must be less than 8KB.
	 * </p>
	 * 
	 * @param userMetadata
	 *            The custom user-metadata for the associated object. Note that
	 *            the key should not include the internal Nos HTTP header
	 *            prefix.
	 * @see ObjectMetadata#getUserMetadata()
	 * @see ObjectMetadata#addUserMetadata(String, String)
	 */
	public void setUserMetadata(Map<String, String> userMetadata) {
		this.userMetadata = userMetadata;
	}

	/**
	 * For internal use only. Sets a specific metadata header value. Not
	 * intended to be called by external code.
	 * 
	 * @param key
	 *            The name of the header being set.
	 * @param value
	 *            The value for the header.
	 */
	public void setHeader(String key, Object value) {
		metadata.put(key, value);
	}

	/**
	 * <p>
	 * Adds the key value pair of custom user-metadata for the associated
	 * object. If the entry in the custom user-metadata map already contains the
	 * specified key, it will be replaced with these new contents.
	 * </p>
	 * <p>
	 * Nos can store additional metadata on objects by internally representing
	 * it as HTTP headers prefixed with "x-amz-meta-". Use user-metadata to
	 * store arbitrary metadata alongside their data in Nos. When setting user
	 * metadata, callers <i>should not</i> include the internal "x-amz-meta-"
	 * prefix; this library will handle that for them. Likewise, when callers
	 * retrieve custom user-metadata, they will not see the "x-amz-meta-" header
	 * prefix.
	 * </p>
	 * <p>
	 * Note that user-metadata for an object is limited by the HTTP request
	 * header limit. All HTTP headers included in a request (including user
	 * metadata headers and other standard HTTP headers) must be less than 8KB.
	 * </p>
	 * 
	 * @param key
	 *            The key for the custom user metadata entry. Note that the key
	 *            should not include the internal Nos HTTP header prefix.
	 * @param value
	 *            The value for the custom user-metadata entry.
	 * 
	 * @see ObjectMetadata#setUserMetadata(Map)
	 * @see ObjectMetadata#getUserMetadata()
	 */
	public void addUserMetadata(String key, String value) {
		this.userMetadata.put(key, value);
	}

	/**
	 * For internal use only. Gets a map of the raw metadata/headers for the
	 * associated object.
	 * 
	 * @return A map of the raw metadata/headers for the associated object.
	 */
	public Map<String, Object> getRawMetadata() {
		return Collections.unmodifiableMap(metadata);
	}

	/**
	 * Gets the value of the Last-Modified header, indicating the date and time
	 * at which Nos last recorded a modification to the associated object.
	 * 
	 * @return The date and time at which Nos last recorded a modification to
	 *         the associated object. Returns <code>null</code> if the
	 *         Last-Modified header hasn't been set.
	 */
	public Date getLastModified() {
		return (Date) metadata.get(Headers.LAST_MODIFIED);
	}

	/**
	 * For internal use only. Sets the Last-Modified header value indicating the
	 * date and time at which Nos last recorded a modification to the associated
	 * object.
	 * 
	 * @param lastModified
	 *            The date and time at which Nos last recorded a modification to
	 *            the associated object.
	 */
	public void setLastModified(Date lastModified) {
		metadata.put(Headers.LAST_MODIFIED, lastModified);
	}

	/**
	 * <p>
	 * Gets the Content-Length HTTP header indicating the size of the associated
	 * object in bytes.
	 * </p>
	 * <p>
	 * This field is required when uploading objects to Nos, but the Nos Java
	 * client will automatically set it when working directly with files. When
	 * uploading directly from a stream, set this field if possible. Otherwise
	 * the client must buffer the entire stream in order to calculate the
	 * content length before sending the data to Nos.
	 * </p>
	 * <p>
	 * For more information on the Content-Length HTTP header, see <a
	 * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.13">
	 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.13</a>
	 * </p>
	 * 
	 * @return The Content-Length HTTP header indicating the size of the
	 *         associated object in bytes. Returns <code>null</code> if it
	 *         hasn't been set yet.
	 * 
	 * @see ObjectMetadata#setContentLength(long)
	 */
	public long getContentLength() {
		Long contentLength = (Long) metadata.get(Headers.CONTENT_LENGTH);

		if (contentLength == null)
			return 0;
		return contentLength.longValue();
	}

	/**
	 * <p>
	 * Sets the Content-Length HTTP header indicating the size of the associated
	 * object in bytes.
	 * </p>
	 * <p>
	 * This field is required when uploading objects to Nos, but the Nos Java
	 * client will automatically set it when working directly with files. When
	 * uploading directly from a stream, set this field if possible. Otherwise
	 * the client must buffer the entire stream in order to calculate the
	 * content length before sending the data to Nos.
	 * </p>
	 * <p>
	 * For more information on the Content-Length HTTP header, see <a
	 * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.13">
	 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.13</a>
	 * </p>
	 * 
	 * @param contentLength
	 *            The Content-Length HTTP header indicating the size of the
	 *            associated object in bytes.
	 * 
	 * @see ObjectMetadata#getContentLength()
	 */
	public void setContentLength(long contentLength) {
		metadata.put(Headers.CONTENT_LENGTH, contentLength);
	}

	/**
	 * <p>
	 * Gets the Content-Type HTTP header, which indicates the type of content
	 * stored in the associated object. The value of this header is a standard
	 * MIME type.
	 * </p>
	 * <p>
	 * When uploading files, the Nos Java client will attempt to determine the
	 * correct content type if one hasn't been set yet. Users are responsible
	 * for ensuring a suitable content type is set when uploading streams. If no
	 * content type is provided and cannot be determined by the filename, the
	 * default content type, "application/octet-stream", will be used.
	 * </p>
	 * <p>
	 * For more information on the Content-Type header, see <a
	 * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17">
	 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17</a>
	 * </p>
	 * 
	 * @return The HTTP Content-Type header, indicating the type of content
	 *         stored in the associated Nos object. Returns <code>null</code> if
	 *         it hasn't been set.
	 * 
	 * @see ObjectMetadata#setContentType(String)
	 */
	public String getContentType() {
		return (String) metadata.get(Headers.CONTENT_TYPE);
	}

	/**
	 * <p>
	 * Sets the Content-Type HTTP header indicating the type of content stored
	 * in the associated object. The value of this header is a standard MIME
	 * type.
	 * </p>
	 * <p>
	 * When uploading files, the Nos Java client will attempt to determine the
	 * correct content type if one hasn't been set yet. Users are responsible
	 * for ensuring a suitable content type is set when uploading streams. If no
	 * content type is provided and cannot be determined by the filename, the
	 * default content type "application/octet-stream" will be used.
	 * </p>
	 * <p>
	 * For more information on the Content-Type header, see <a
	 * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17">
	 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17</a>
	 * </p>
	 * 
	 * @param contentType
	 *            The HTTP Content-Type header indicating the type of content
	 *            stored in the associated Nos object.
	 * 
	 * @see ObjectMetadata#getContentType()
	 */
	public void setContentType(String contentType) {
		metadata.put(Headers.CONTENT_TYPE, contentType);
	}

	/**
	 * <p>
	 * Sets the base64 encoded 128-bit MD5 digest of the associated object
	 * (content - not including headers) according to RFC 1864. This data is
	 * used as a message integrity check to verify that the data received by Nos
	 * is the same data that the caller sent.
	 * </p>
	 * <p>
	 * This field represents the base64 encoded 128-bit MD5 digest digest of an
	 * object's content as calculated on the caller's side. The ETag metadata
	 * field represents the hex encoded 128-bit MD5 digest as computed by Nos.
	 * </p>
	 * <p>
	 * The Nos Java client will attempt to calculate this field automatically
	 * when uploading files to Nos.
	 * </p>
	 * 
	 * @param md5Base64
	 *            The base64 encoded MD5 hash of the content for the object
	 *            associated with this metadata.
	 * 
	 * @see ObjectMetadata#getContentMD5()
	 */
	public void setContentMD5(String md5Base64) {
		metadata.put(Headers.CONTENT_MD5, md5Base64);
	}

	/**
	 * <p>
	 * Gets the base64 encoded 128-bit MD5 digest of the associated object
	 * (content - not including headers) according to RFC 1864. This data is
	 * used as a message integrity check to verify that the data received by Nos
	 * is the same data that the caller sent.
	 * </p>
	 * <p>
	 * This field represents the base64 encoded 128-bit MD5 digest digest of an
	 * object's content as calculated on the caller's side. The ETag metadata
	 * field represents the hex encoded 128-bit MD5 digest as computed by Nos.
	 * </p>
	 * <p>
	 * The Nos Java client will attempt to calculate this field automatically
	 * when uploading files to Nos.
	 * </p>
	 * 
	 * @return The base64 encoded MD5 hash of the content for the associated
	 *         object. Returns <code>null</code> if the MD5 hash of the content
	 *         hasn't been set.
	 * 
	 * @see ObjectMetadata#setContentMD5(String)
	 */
	public String getContentMD5() {
		return (String) metadata.get(Headers.CONTENT_MD5);
	}

	/**
	 * Gets the hex encoded 128-bit MD5 digest of the associated object
	 * according to RFC 1864. This data is used as an integrity check to verify
	 * that the data received by the caller is the same data that was sent by
	 * Nos.
	 * <p>
	 * This field represents the hex encoded 128-bit MD5 digest of an object's
	 * content as calculated by Nos. The ContentMD5 field represents the base64
	 * encoded 128-bit MD5 digest as calculated on the caller's side.
	 * </p>
	 * 
	 * @return The hex encoded MD5 hash of the content for the associated object
	 *         as calculated by Nos. Returns <code>null</code> if it hasn't been
	 *         set yet.
	 */
	public String getETag() {
		return (String) metadata.get(Headers.ETAG);
	}
	
	public String getObjectName() {
		return (String) metadata.get(Headers.X_NOS_OBJECT_NAME);
	}
	
	public String getCallbackRet() {
		return (String) metadata.get(Headers.X_NOS_CALLBACK_RET);
	}

	/**
	 * Returns the expiration time for this object, or null if it doesn't
	 * expire.
	 */
	public Date getExpirationTime() {
		return expirationTime;
	}

	/**
	 * Sets the expiration time for the object.
	 * 
	 * @param expirationTime
	 *            The expiration time for the object.
	 */
	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}

	/**
	 * 
	 * object's expiration, or null if it doesn't expire.
	 */
	public String getExpirationTimeRuleId() {
		return expirationTimeRuleId;
	}

	/**
	 * 
	 * expiration
	 * 
	 * @param expirationTimeRuleId
	 *            The rule ID for this object's expiration
	 */
	public void setExpirationTimeRuleId(String expirationTimeRuleId) {
		this.expirationTimeRuleId = expirationTimeRuleId;
	}

}
