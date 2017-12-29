package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

/**
 * Container object for overriding headers on a {@link GetObjectRequest} or
 * {@link GeneratePresignedUrlRequest} response. Response headers can be set on
 * a {@link GetObjectRequest} or a {@link GeneratePresignedUrlRequest} in order
 * to control particular HTTP headers in the service response from those service
 * interfaces.
 * <p>
 * For example, a client could dynamically change the apparent
 * Content-Disposition header of a single object, so that it appears to have a
 * different file name for different callers. One client could be configured
 * return the object with
 * 
 * <pre>
 * Content-Disposition: attachment; filename=FileName1.exe
 * </pre>
 * 
 * while another could return that same object with headers
 * 
 * <pre>
 * Content-Disposition: attachment; filename=FileName2.pdf
 * </pre>
 * 
 * </p>
 * 
 */
public class ResponseHeaderOverrides extends WebServiceRequest {

	private String contentType;
	private String contentLanguage;
	private String expires;
	private String cacheControl;
	private String contentDisposition;
	private String contentEncoding;

	/**
	 * Returns the content type response header override if it has been
	 * specified, or null otherwise.
	 * 
	 * @return Returns the content type response header override if it has been
	 *         specified, or null otherwise.
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Sets the content type response header override.
	 * 
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Sets the content type response header override.
	 * 
	 * @return This {@link ResponseHeaderOverrides} object for method chaining.
	 */
	public ResponseHeaderOverrides withContentType(String contentType) {
		setContentType(contentType);
		return this;
	}

	/**
	 * Returns the content language response header override if it has been
	 * specified, or null otherwise.
	 * 
	 * @return Returns the content language response header override if it has
	 *         been specified, or null otherwise.
	 */
	public String getContentLanguage() {
		return contentLanguage;
	}

	/**
	 * Sets the content language response header override
	 * 
	 */
	public void setContentLanguage(String contentLanguage) {
		this.contentLanguage = contentLanguage;
	}

	/**
	 * Sets the content language response header override
	 * 
	 * @return This {@link ResponseHeaderOverrides} object for method chaining.
	 */
	public ResponseHeaderOverrides withContentLanguage(String contentLanguage) {
		setContentLanguage(contentLanguage);
		return this;
	}

	/**
	 * Returns the expires response header override if it has been specified, or
	 * null otherwise.
	 * 
	 * @return Returns the expires response header override if it has been
	 *         specified, or null otherwise.
	 */
	public String getExpires() {
		return expires;
	}

	/**
	 * Sets the expires response header override.
	 * 
	 */
	public void setExpires(String expires) {
		this.expires = expires;
	}

	/**
	 * Sets the expires response header override.
	 * 
	 * @return This {@link ResponseHeaderOverrides} object for method chaining.
	 */
	public ResponseHeaderOverrides withExpires(String expires) {
		setExpires(expires);
		return this;
	}

	/**
	 * Returns the cache control response header override if it has been
	 * specified, or null otherwise.
	 * 
	 * @return Returns the cache control response header override if it has been
	 *         specified, or null otherwise.
	 */
	public String getCacheControl() {
		return cacheControl;
	}

	/**
	 * Sets the cache control response header.
	 * 
	 */
	public void setCacheControl(String cacheControl) {
		this.cacheControl = cacheControl;
	}

	/**
	 * Sets the cache control response header.
	 * 
	 * @return This {@link ResponseHeaderOverrides} object for method chaining.
	 */
	public ResponseHeaderOverrides withCacheControl(String cacheControl) {
		setCacheControl(cacheControl);
		return this;
	}

	/**
	 * Returns the content disposition response header override if it has been
	 * specified, or null otherwise.
	 * 
	 * @return Returns the content disposition response header override if it
	 *         has been specified, or null otherwise.
	 */
	public String getContentDisposition() {
		return contentDisposition;
	}

	/**
	 * Sets the content disposition response header override.
	 * 
	 */
	public void setContentDisposition(String contentDisposition) {
		this.contentDisposition = contentDisposition;
	}

	/**
	 * Sets the content disposition response header override.
	 * 
	 */
	public ResponseHeaderOverrides withContentDisposition(String contentDisposition) {
		setContentDisposition(contentDisposition);
		return this;
	}

	/**
	 * Returns the content encoding response header override if it has been
	 * specified, or null otherwise.
	 * 
	 * @return Returns the content encoding response header override if it has
	 *         been specified, or null otherwise.
	 */
	public String getContentEncoding() {
		return contentEncoding;
	}

	/**
	 * Sets the content encoding response header override.
	 * 
	 */
	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

	/**
	 * Sets the content encoding response header override.
	 * 
	 * @return This {@link ResponseHeaderOverrides} object for method chaining.
	 */
	public ResponseHeaderOverrides withContentEncoding(String contentEncoding) {
		setContentEncoding(contentEncoding);
		return this;
	}

}
