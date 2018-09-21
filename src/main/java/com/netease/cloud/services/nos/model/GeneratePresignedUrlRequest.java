package com.netease.cloud.services.nos.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.netease.cloud.WebServiceRequest;
import com.netease.cloud.HttpMethod;
import com.netease.cloud.services.nos.Nos;

/**
 * <p>
 * Contains options to genearate a pre-signed URL for an resource.
 * </p>
 * <p>
 * Pre-signed URLs allow clients to form a URL for an resource and sign it with
 * the current security credentials. A pre-signed URL may be passed around for
 * other users to access the resource without providing them access to an
 * account's security credentials.
 * </p>
 * 
 * @see Nos#generatePresignedUrl(GeneratePresignedUrlRequest)
 */
public class GeneratePresignedUrlRequest extends WebServiceRequest {

	/**
	 * The HTTP method (GET, PUT, DELETE, HEAD) to be used in this request and
	 * when the pre-signed URL is used
	 */
	private HttpMethod method;

	/** The name of the bucket involved in this request */
	private String bucketName;

	/** The key of the object involved in this request */
	private String key;

	/**
	 * An optional expiration date at which point the generated pre-signed URL
	 * will no longer be accepted by . If not specified, a default value will be
	 * supplied.
	 */
	private Date expiration;
	
	/**
	 * An optional parameter. If specified, nos response will set content-disposition
	 * header to attachment so browsers will try to download the object rather than 
	 * open it in the browser.
	 */
	private String download;
	
	/**
	 * 
	 */
	private String ifNotFound;

	/**
	 * An optional map of additional parameters to include in the pre-signed
	 * URL. Adding additional request parameters enables more advanced
	 * pre-signed URLs, such as accessing 's torrent resource for an object, or
	 * for specifying a version ID when accessing an object.
	 */
	private Map<String, String> requestParameters = new HashMap<String, String>();

	/**
	 * Optional field that overrides headers on the response.
	 */
	private ResponseHeaderOverrides responseHeaders;

	/**
	 * Creates a new request for generating a pre-signed URL that can be used as
	 * part of an HTTP GET request to access the object stored under the
	 * specified key in the specified bucket.
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the desired object.
	 * @param key
	 *            The key under which the desired object is stored.
	 */
	public GeneratePresignedUrlRequest(String bucketName, String key) {
		this(bucketName, key, HttpMethod.GET);
	}

	/**
	 * <p>
	 * Creates a new request for generating a pre-signed URL that can be used as
	 * part of an HTTP request to access the specified resource.
	 * </p>
	 * <p>
	 * When specifying an HTTP method, you <b>must</b> send the pre-signed URL
	 * with the same HTTP method in order to successfully use the pre-signed
	 * URL.
	 * </p>
	 * 
	 * @param bucketName
	 *            The name of the bucket involved in the operation.
	 * @param key
	 *            The key of the object involved in the operation.
	 * @param method
	 *            The HTTP method (GET, PUT, DELETE, HEAD) to be used in the
	 *            request when the pre-signed URL is used.
	 */
	public GeneratePresignedUrlRequest(String bucketName, String key, HttpMethod method) {
		this.bucketName = bucketName;
		this.key = key;
		this.method = method;
	}

	/**
	 * The HTTP method (GET, PUT, DELETE, HEAD) to be used in this request. The
	 * same HTTP method <b>must</b> be used in the request when the pre-signed
	 * URL is used.
	 * 
	 * @return The HTTP method (GET, PUT, DELETE, HEAD) to be used in this
	 *         request and when the pre-signed URL is used.
	 */
	public HttpMethod getMethod() {
		return method;
	}

	/**
	 * Sets the HTTP method (GET, PUT, DELETE, HEAD) to be used in this request.
	 * The same HTTP method <b>must</b> be used in the request when the
	 * pre-signed URL is used.
	 * 
	 * @param method
	 *            The HTTP method (GET, PUT, DELETE, HEAD) to be used in this
	 *            request.
	 */
	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	/**
	 * Sets the HTTP method (GET, PUT, DELETE, HEAD) to be used in this request,
	 * and returns this request object to enable additional method calls to be
	 * chained together.
	 * <p>
	 * The same HTTP method <b>must</b> be used in the request when the
	 * pre-signed URL is used.
	 * 
	 * @param method
	 *            The HTTP method (GET, PUT, DELETE, HEAD) to be used in this
	 *            request.
	 * 
	 * @return The updated request object, so that additional method calls can
	 *         be chained together.
	 */
	public GeneratePresignedUrlRequest withMethod(HttpMethod method) {
		setMethod(method);
		return this;
	}

	/**
	 * Returns the name of the bucket involved in this request.
	 * 
	 * @return the name of the bucket involved in this request.
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * Sets the name of the bucket involved in this request.
	 * 
	 * @param bucketName
	 *            the name of the bucket involved in this request.
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Sets the name of the bucket involved in this request, and returns this
	 * request object to enable additional method calls to be chained together.
	 * 
	 * @param bucketName
	 *            the name of the bucket involved in this request.
	 * 
	 * @return The updated request object, so that additional method calls can
	 *         be chained together.
	 */
	public GeneratePresignedUrlRequest withBucketName(String bucketName) {
		setBucketName(bucketName);
		return this;
	}

	/**
	 * Returns the key of the object involved in this request.
	 * 
	 * @return The key of the object involved in this request.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key of the object involved in this request.
	 * 
	 * @param key
	 *            the key of the object involved in this request.
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Sets the key of the object involved in this request, and returns this
	 * request object to enable additional method calls to be chained together.
	 * 
	 * @param key
	 *            the key of the object involved in this request.
	 * 
	 * @return The updated request object, so that additional method calls can
	 *         be chained together.
	 */
	public GeneratePresignedUrlRequest withKey(String key) {
		setKey(key);
		return this;
	}

	/**
	 * The expiration date at which point the new pre-signed URL will no longer
	 * be accepted by . If not specified, a default value will be supplied.
	 * 
	 * @return The expiration date at which point the new pre-signed URL will no
	 *         longer be accepted by .
	 */
	public Date getExpiration() {
		return expiration;
	}

	/**
	 * Sets the expiration date at which point the new pre-signed URL will no
	 * longer be accepted by . If not specified, a default value will be
	 * supplied.
	 * 
	 * @param expiration
	 *            The expiration date at which point the new pre-signed URL will
	 *            no longer be accepted by .
	 */
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	
	/**
	 * The download parameter which indicate the default name when browser
	 * try to download the object
	 * 
	 * @return the download parameter which indicate the default name when browser
	 * 		   try to download the object.
	 */
	public String getDownload() {
		return download;
	}
	
	/**
	 * Sets the download parameter which browser will set to default file name when 
	 * try to download it.
	 * 
	 * @param download
	 * 			the download parameter which indicate the default name when browser
	 * 			try to download the object
	 */
	public void setDownload(String download) {
		this.download = download;
	}
	
	public String getIfNotFound() {
		return ifNotFound;
	}

	public void setIfNotFound(String ifNotFound) {
		this.ifNotFound = ifNotFound;
	}

	/**
	 * Sets the expiration date at which point the new pre-signed URL will no
	 * longer be accepted by , and returns this request object to enable
	 * additional method calls to be chained together.
	 * <p>
	 * If not specified, a default value will be supplied.
	 * 
	 * @param expiration
	 *            The expiration date at which point the new pre-signed URL will
	 *            no longer be accepted by .
	 * 
	 * @return The updated request object, so that additional method calls can
	 *         be chained together.
	 */
	public GeneratePresignedUrlRequest withExpiration(Date expiration) {
		setExpiration(expiration);
		return this;
	}

	/**
	 * Adds an additional request parameter to be included in the pre-signed
	 * URL. Adding additional request parameters enables more advanced
	 * pre-signed URLs, such as accessing 's torrent resource for an object, or
	 * for specifying a version ID when accessing an object.
	 * 
	 * @param key
	 *            The name of the request parameter, as it appears in the URL's
	 *            query string (e.g. versionId).
	 * @param value
	 *            The (optional) value of the request parameter being added.
	 */
	public void addRequestParameter(String key, String value) {
		requestParameters.put(key, value);
	}

	/**
	 * Returns the complete map of additional request parameters to be included
	 * in the pre-signed URL.
	 * 
	 * @return The complete map of additional request parameters to be included
	 *         in the pre-signed URL.
	 */
	public Map<String, String> getRequestParameters() {
		return requestParameters;
	}

	/**
	 * Returns the headers to be overridden in the service response.
	 * 
	 * @return the headers to be overridden in the service response.
	 */
	public ResponseHeaderOverrides getResponseHeaders() {
		return responseHeaders;
	}

	/**
	 * Sets the headers to be overridden in the service response.
	 * 
	 * @param responseHeaders
	 *            The headers to be overridden in the service response.
	 */
	public void setResponseHeaders(ResponseHeaderOverrides responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	/**
	 * Sets the headers to be overridden in the service response and returns
	 * this object, for method chaining.
	 * 
	 * @param responseHeaders
	 *            The headers to be overridden in the service response.
	 * 
	 * 
	 * @return This {@link GeneratePresignedUrlRequest} for method chaining.
	 */
	public GeneratePresignedUrlRequest withResponseHeaders(ResponseHeaderOverrides responseHeaders) {
		setResponseHeaders(responseHeaders);
		return this;
	}
}
