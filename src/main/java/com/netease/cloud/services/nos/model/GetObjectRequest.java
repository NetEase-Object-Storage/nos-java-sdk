package com.netease.cloud.services.nos.model;

import java.util.Date;

import com.netease.cloud.WebServiceRequest;

/**
 * <p>
 * Provides options for downloading an object.
 * </p>
 * <p>
 * All <code>GetObjectRequests</code> must specify a bucket name and key. Beyond
 * that, requests can also specify:
 * 
 * <ul>
 * <li>The range of bytes within the object to download,
 * <li>Constraints controlling if the object will be downloaded or not.
 * </ul>
 * </p>
 * 
 * @see GetObjectRequest#GetObjectRequest(String, String)
 * @see GetObjectRequest#GetObjectRequest(String, String, String)
 * @see GetObjectMetadataRequest
 */
public class GetObjectRequest extends WebServiceRequest {

	/** The name of the bucket containing the object to retrieve */
	private String bucketName;

	/** The key under which the desired object is stored */
	private String key;

	/**
	 * Optional version ID specifying which version of the object to download.
	 * If not specified, the most recent version will be downloaded.
	 * <p>
	 * For more information about enabling versioning for a bucket, see
	 * {@link Nos#setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest)}.
	 */
	//private String versionId;

	/** Optional member indicating the byte range of data to retrieve */
	private long[] range;

	/**
	 * Optional field that constrains this request to only be executed if the
	 * object has been modified since the specified date.
	 */
	private Date modifiedSinceConstraint;

	/**
	 * The optional progress listener for receiving updates about object
	 * download status.
	 */
	private ProgressListener progressListener;

	/**
	 * Constructs a new {@link GetObjectRequest} with all the required
	 * parameters.
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the desired object.
	 * @param key
	 *            The key in the specified bucket under which the object is
	 *            stored.
	 * 
	 * @see GetObjectRequest#GetObjectRequest(String, String)
	 */
	public GetObjectRequest(String bucketName, String key) {
		setBucketName(bucketName);
		setKey(key);
		//setVersionId(versionId);
	}

	/**
	 * Gets the name of the bucket containing the object to be downloaded.
	 * 
	 * @return The name of the bucket containing the object to be downloaded.
	 * 
	 * @see GetObjectRequest#setBucketName(String)
	 * @see GetObjectRequest#withBucketName(String)
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * Sets the name of the bucket containing the object to be downloaded.
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the object to be downloaded.
	 * 
	 * @see GetObjectRequest#getBucketName()
	 * @see GetObjectRequest#withBucketName(String)
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Sets the name of the bucket containing the object to be downloaded.
	 * Returns this {@link GetObjectRequest}, enabling additional method calls
	 * to be chained together.
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the object to be downloaded.
	 * 
	 * @return This {@link GetObjectRequest}, enabling additional method calls
	 *         to be chained together.
	 * 
	 * @see GetObjectRequest#getBucketName()
	 * @see GetObjectRequest#setBucketName(String)
	 */
	public GetObjectRequest withBucketName(String bucketName) {
		setBucketName(bucketName);
		return this;
	}

	/**
	 * Gets the key under which the object to be downloaded is stored.
	 * 
	 * @return The key under which the object to be downloaded is stored.
	 * 
	 * @see GetObjectRequest#setKey(String)
	 * @see GetObjectRequest#withKey(String)
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key under which the object to be downloaded is stored.
	 * 
	 * @param key
	 *            The key under which the object to be downloaded is stored.
	 * 
	 * @see GetObjectRequest#getKey()
	 * @see GetObjectRequest#withKey(String)
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Sets the key under which the object to be downloaded is stored. Returns
	 * this {@link GetObjectRequest}, enabling additional method calls to be
	 * chained together.
	 * 
	 * @param key
	 *            The key under which the object to be downloaded is stored.
	 * 
	 * @return This {@link GetObjectRequest}, enabling additional method calls
	 *         to be chained together.
	 * 
	 * @see GetObjectRequest#getKey()
	 * @see GetObjectRequest#setKey(String)
	 */
	public GetObjectRequest withKey(String key) {
		setKey(key);
		return this;
	}

	/*
	 * Optional Request Parameters
	 */

	/**
	 * <p>
	 * Gets the optional inclusive byte range within the desired object that
	 * will be downloaded by this request.
	 * </p>
	 * <p>
	 * The range is returned as a two element array, containing the start and
	 * end index of the byte range. If no byte range has been specified, the
	 * entire object is downloaded and this method returns <code>null</code>.
	 * </p>
	 * 
	 * @return A two element array indicating the inclusive start index and end
	 *         index within the object being downloaded by this request. Returns
	 *         <code>null</code> if no range has been specified, and the whole
	 *         object is to be downloaded.
	 * 
	 */
	public long[] getRange() {
		return range;
	}

	/**
	 * <p>
	 * Sets the optional inclusive byte range within the desired object that
	 * will be downloaded by this request.
	 * </p>
	 * <p>
	 * The first byte in an object has position 0; as an example, the first ten
	 * bytes of an object can be downloaded by specifying a range of 0 to 9.
	 * </p>
	 * <p>
	 * If no byte range is specified, this request downloads the entire object
	 * from .
	 * </p>
	 * 
	 * @param start
	 *            The start of the inclusive byte range to download.
	 * @param end
	 *            The end of the inclusive byte range to download.
	 * 
	 * @see GetObjectRequest#withRange(long, long)
	 */
	public void setRange(long start, long end) {
		range = new long[] { start, end };
	}

	/**
	 * <p>
	 * Sets the optional inclusive byte range within the desired object that
	 * will be downloaded by this request. Returns this {@link GetObjectRequest}
	 * , enabling additional method calls to be chained together.
	 * </p>
	 * <p>
	 * The first byte in an object has position 0; as an example, the first ten
	 * bytes of an object can be downloaded by specifying a range of 0 to 9.
	 * </p>
	 * <p>
	 * If no byte range is specified, this request downloads the entire object
	 * from .
	 * </p>
	 * 
	 * @param start
	 *            The start of the inclusive byte range to download.
	 * @param end
	 *            The end of the inclusive byte range to download.
	 * 
	 * @return This {@link GetObjectRequest}, enabling additional method calls
	 *         to be chained together.
	 * 
	 * @see GetObjectRequest#getRange()
	 * @see GetObjectRequest#setRange(long, long)
	 */
	public GetObjectRequest withRange(long start, long end) {
		setRange(start, end);
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
	 * Note that will ignore any dates occurring in the future.
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

	/**
	 * Sets the optional modified constraint that restricts this request to
	 * executing only if the object <b>has</b> been modified after the specified
	 * date. Returns this {@link GetObjectRequest}, enabling additional method
	 * calls to be chained together.
	 * <p>
	 * Note that will ignore any dates occurring in the future.
	 * 
	 * @param date
	 *            The modified constraint that restricts this request to
	 *            executing only if the object <b>has</b> been modified after
	 *            the specified date.
	 * 
	 * @return This {@link GetObjectRequest}, enabling additional method calls
	 *         to be chained together.
	 * 
	 * @see GetObjectRequest#getModifiedSinceConstraint()
	 * @see GetObjectRequest#setModifiedSinceConstraint(Date)
	 */
	public GetObjectRequest withModifiedSinceConstraint(Date date) {
		setModifiedSinceConstraint(date);
		return this;
	}

	/**
	 * Sets the optional progress listener for receiving updates about object
	 * download status.
	 * 
	 * @param progressListener
	 *            The new progress listener.
	 */
	public void setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	/**
	 * Returns the optional progress listener for receiving updates about object
	 * download status.
	 * 
	 * @return the optional progress listener for receiving updates about object
	 *         download status.
	 */
	public ProgressListener getProgressListener() {
		return progressListener;
	}

	/**
	 * Sets the optional progress listener for receiving updates about object
	 * download status, and returns this updated object so that additional
	 * method calls can be chained together.
	 * 
	 * @param progressListener
	 *            The new progress listener.
	 * 
	 * @return This updated GetObjectRequest object.
	 */
	public GetObjectRequest withProgressListener(ProgressListener progressListener) {
		setProgressListener(progressListener);
		return this;
	}

}
