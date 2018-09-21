package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;
import com.netease.cloud.services.nos.Nos;
import com.netease.cloud.services.nos.internal.Constants;

/**
 * Provides options for returning a list of summary information about the
 * versions in a specified bucket.
 * <p>
 * Returned version summaries are ordered first by key and then by version. Keys
 * are sorted lexicographically (i.e. alphabetically from a-Z) and versions are
 * sorted from the most recent to the least recent. Versions with data and
 * versions with delete markers are included in the results.
 * </p>
 * <p>
 * Buckets can contain a virtually unlimited number of keys, and the complete
 * results of a list query can be extremely large. To manage large result sets,
 * Nos uses pagination to split them into multiple responses. Always check the
 * {@link ObjectListing#isTruncated()} method to see if the returned listing is
 * complete, or if callers need to make additional calls to get more results.
 * </p>
 * <p>
 * Objects created before versioning was enabled or when versioning is suspended
 * will be given the default <code>null</code> version ID (see
 * {@link Constants#NULL_VERSION_ID}). Note that the <code>null</code> version
 * ID is a valid version ID and is not the same as not having a version ID.
 * </p>
 * <p>
 * Calling {@link ListVersionsRequest#setDelimiter(String)} sets the delimiter,
 * allowing groups of keys that share the delimiter-terminated prefix to be
 * included in the returned listing. This allows applications to organize and
 * browse their keys hierarchically, similar to how a file system organizes
 * files into directories. These common prefixes can be retrieved through the
 * {@link VersionListing#getCommonPrefixes()} method.
 * </p>
 * <p>
 * For example, consider a bucket that contains the following keys:
 * <ul>
 * <li>"foo/bar/baz"</li>
 * <li>"foo/bar/bash"</li>
 * <li>"foo/bar/bang"</li>
 * <li>"foo/boo"</li>
 * </ul>
 * If calling <code>listVersions</code> with a prefix value of "foo/" and a
 * delimiter value of "/" on this bucket, an <code>VersionListing</code> is
 * returned that contains one key ("foo/boo") and one entry in the common
 * prefixes list ("foo/bar/"). To see deeper into the virtual hierarchy, make
 * another call to <code>listVersions</code> setting the prefix parameter to any
 * interesting common prefix to list the individual keys under that prefix.
 * </p>
 * <p>
 * The total number of keys in a bucket doesn't substantially affect list
 * performance, nor does the presence or absence of additional request
 * parameters.
 * </p>
 * <p>
 * For more information about enabling versioning for a bucket, see
 * {@link Nos#setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest)}
 * .
 * </p>
 */
public class ListVersionsRequest extends WebServiceRequest {

	/** The name of the Nos bucket whose versions are to be listed. */
	private String bucketName;

	/**
	 * Optional parameter indicating where in the sorted list of all versions in
	 * the specified bucket to begin returning results. Results are always
	 * ordered first lexicographically (i.e. alphabetically) and then from most
	 * recent version to least recent version. If a keyMarker is used without a
	 * versionIdMarker, results begin immediately after that key's last version.
	 * When a keyMarker is used with a versionIdMarker, results begin
	 * immediately after the version with the specified key and version ID.
	 * <p>
	 * This enables pagination; to get the next page of results use the next key
	 * marker and next version ID marker (from
	 * {@link VersionListing#getNextKeyMarker()} and
	 * {@link VersionListing#getNextVersionIdMarker()}) as the markers for the
	 * next request to list versions. Or use the convenience method
	 * {@link Nos#listNextBatchOfVersions(VersionListing)}
	 */
	private String keyMarker;

	/**
	 * Optional parameter indicating where in the sorted list of all versions in
	 * the specified bucket to begin returning results. Results are always
	 * ordered first lexicographically (i.e. alphabetically) and then from most
	 * recent version to least recent version. A keyMarker must be specified
	 * when specifying a versionIdMarker. Results begin immediately after the
	 * version with the specified key and version ID.
	 * <p>
	 * This enables pagination; to get the next page of results use the next key
	 * marker and next version ID marker (from
	 * {@link VersionListing#getNextKeyMarker()} and
	 * {@link VersionListing#getNextVersionIdMarker()}) as the markers for the
	 * next request to list versions. Or use the convenience method
	 * {@link Nos#listNextBatchOfVersions(VersionListing)}
	 */
	private String versionIdMarker;

	/**
	 * Optional parameter indicating the maximum number of results to include in
	 * the response. Nos might return fewer than this, but will not return more.
	 * Even if maxKeys is not specified, Nos will limit the number of results in
	 * the response.
	 */
	private Integer maxResults;

	/**
	 * Constructs a new {@link ListVersionsRequest} object. The caller must
	 * populate the fields before the request is executed.
	 * 
	 * @see ListVersionsRequest#ListVersionsRequest(String, String, String,
	 *      String, String, Integer)
	 */
	public ListVersionsRequest() {
	}

	/**
	 * Constructs a new {@link ListVersionsRequest} object and initializes all
	 * required and optional fields.
	 * 
	 * @param bucketName
	 *            The name of the bucket whose versions are to be listed.
	 * @param keyMarker
	 *            The key marker indicating where results should begin.
	 * @param versionIdMarker
	 *            The version ID marker indicating where results should begin.
	 * @param delimiter
	 *            The delimiter for condensing common prefixes in returned
	 *            results.
	 * @param maxResults
	 *            The maximum number of results to return.
	 * 
	 * @see ListVersionsRequest#ListVersionsRequest()
	 */
	public ListVersionsRequest(String bucketName,String keyMarker, String versionIdMarker, Integer maxResults) {
		setBucketName(bucketName);
		setKeyMarker(keyMarker);
		setVersionIdMarker(versionIdMarker);
		setMaxResults(maxResults);
	}

	/**
	 * Gets the name of the Nos bucket whose versions are to be listed.
	 * 
	 * @return The name of the Nos bucket whose versions are to be listed.
	 * 
	 * @see ListVersionsRequest#setBucketName(String)
	 * @see ListVersionsRequest#withBucketName(String)
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * Sets the name of the Nos bucket whose versions are to be listed.
	 * 
	 * @param bucketName
	 *            The name of the Nos bucket whose versions are to be listed.
	 * 
	 * @see ListVersionsRequest#getBucketName()
	 * @see ListVersionsRequest#withBucketName(String)
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Sets the name of the Nos bucket whose versions are to be listed. Returns
	 * this {@link ListVersionsRequest}, enabling additional method calls to be
	 * chained together.
	 * 
	 * @param bucketName
	 *            The name of the Nos bucket whose versions are to be listed.
	 * 
	 * @return This {@link ListVersionsRequest}, enabling additional method
	 *         calls to be chained together.
	 * 
	 * @see ListVersionsRequest#getBucketName()
	 * @see ListVersionsRequest#setBucketName(String)
	 */
	public ListVersionsRequest withBucketName(String bucketName) {
		setBucketName(bucketName);
		return this;
	}


	/**
	 * Gets the optional <code>keyMarker</code> parameter indicating where in
	 * the sorted list of all versions in the specified bucket to begin
	 * returning results. Results are always ordered first lexicographically
	 * (i.e. alphabetically) and then from most recent version to least recent
	 * version.
	 * <p>
	 * If a <code>keyMarker</code> is used without a version ID marker, results
	 * begin immediately after that key's last version. When a
	 * <code>keyMarker</code> is used with a version ID marker, results begin
	 * immediately after the version with the specified key and version ID.
	 * </p>
	 * 
	 * @return The optional <code>keyMarker</code> parameter indicating where in
	 *         the sorted list of all versions in the specified bucket to begin
	 *         returning results.
	 * 
	 * @see ListVersionsRequest#setKeyMarker(String)
	 * @see ListVersionsRequest#withKeyMarker(String)
	 */
	public String getKeyMarker() {
		return keyMarker;
	}

	/**
	 * Sets the optional <code>keyMarker</code> parameter indicating where in
	 * the sorted list of all versions in the specified bucket to begin
	 * returning results.
	 * 
	 * @param keyMarker
	 *            The optional <code>keyMarker</code> parameter indicating where
	 *            in the sorted list of all versions in the specified bucket to
	 *            begin returning results.
	 * 
	 * @see ListVersionsRequest#getKeyMarker()
	 * @see ListVersionsRequest#withKeyMarker(String)
	 */
	public void setKeyMarker(String keyMarker) {
		this.keyMarker = keyMarker;
	}

	/**
	 * Sets the optional <code>keyMarker</code> parameter indicating where in
	 * the sorted list of all versions in the specified bucket to begin
	 * returning results. Returns this {@link ListVersionsRequest}, enabling
	 * additional method calls to be chained together.
	 * 
	 * @param keyMarker
	 *            The optional <code>keyMarker</code> parameter indicating where
	 *            in the sorted list of all versions in the specified bucket to
	 *            begin returning results.
	 * 
	 * @return This {@link ListVersionsRequest}, enabling additional method
	 *         calls to be chained together.
	 * 
	 * @see ListVersionsRequest#getKeyMarker()
	 * @see ListVersionsRequest#setKeyMarker(String)
	 */
	public ListVersionsRequest withKeyMarker(String keyMarker) {
		setKeyMarker(keyMarker);
		return this;
	}

	/**
	 * Gets the optional <code>versionIdMarker</code> parameter indicating where
	 * in the sorted list of all versions in the specified bucket to begin
	 * returning results. Results are always ordered first lexicographically
	 * (i.e. alphabetically) and then from most recent version to least recent
	 * version.
	 * <p>
	 * A key marker must be specified when specifying a
	 * <code>versionIdMarker</code>. Results begin immediately after the version
	 * with the specified key and version ID.
	 * </p>
	 * 
	 * @return The optional <code>versionIdMarker</code> parameter indicating
	 *         where in the sorted list of all versions in the specified bucket
	 *         to begin returning results.
	 * 
	 * @see ListVersionsRequest#setVersionIdMarker(String)
	 * @see ListVersionsRequest#withVersionIdMarker(String)
	 */
	public String getVersionIdMarker() {
		return versionIdMarker;
	}

	/**
	 * Sets the optional <code>versionIdMarker</code> parameter indicating where
	 * in the sorted list of all versions in the specified bucket to begin
	 * returning results.
	 * 
	 * @param versionIdMarker
	 *            The optional <code>versionIdMarker</code> parameter indicating
	 *            where in the sorted list of all versions in the specified
	 *            bucket to begin returning results.
	 * 
	 * @see ListVersionsRequest#getVersionIdMarker()
	 * @see ListVersionsRequest#withVersionIdMarker(String)
	 */
	public void setVersionIdMarker(String versionIdMarker) {
		this.versionIdMarker = versionIdMarker;
	}

	/**
	 * Sets the optional <code>versionIdMarker</code> parameter indicating where
	 * in the sorted list of all versions in the specified bucket to begin
	 * returning results. Returns this {@link ListVersionsRequest}, enabling
	 * additional method calls to be chained together.
	 * 
	 * @param versionIdMarker
	 *            The optional <code>versionIdMarker</code> parameter indicating
	 *            where in the sorted list of all versions in the specified
	 *            bucket to begin returning results.
	 * 
	 * @return This {@link ListVersionsRequest}, enabling additional method
	 *         calls to be chained together.
	 * 
	 * @see ListVersionsRequest#getVersionIdMarker()
	 * @see ListVersionsRequest#setVersionIdMarker(String)
	 */
	public ListVersionsRequest withVersionIdMarker(String versionIdMarker) {
		setVersionIdMarker(versionIdMarker);
		return this;
	}


	/**
	 * Gets the optional <code>maxResults</code> parameter indicating the
	 * maximum number of results to include in the response. Nos might return
	 * fewer than this, but will never return more. Even if
	 * <code>maxResults</code> is not specified, Nos will limit the number of
	 * results in the response.
	 * 
	 * @return The optional <code>maxResults</code> parameter indicating the
	 *         maximum number of results
	 * 
	 * @see ListVersionsRequest#setMaxResults(Integer)
	 * @see ListVersionsRequest#withMaxResults(Integer)
	 */
	public Integer getMaxResults() {
		return maxResults;
	}

	/**
	 * Sets the optional <code>maxResults</code> parameter indicating the
	 * maximum number of results to include in the response.
	 * 
	 * @param maxResults
	 *            The optional <code>maxResults</code> parameter indicating the
	 *            maximum number of results to include in the response.
	 * 
	 * @see ListVersionsRequest#getMaxResults()
	 * @see ListVersionsRequest#withMaxResults(Integer)
	 */
	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}

	/**
	 * Sets the optional <code>maxResults</code> parameter indicating the
	 * maximum number of results to include in the response. Returns this
	 * {@link ListVersionsRequest}, enabling additional method calls to be
	 * chained together.
	 * 
	 * @param maxResults
	 *            The optional <code>maxResults</code> parameter indicating the
	 *            maximum number of results to include in the response.
	 * 
	 * @return This {@link ListVersionsRequest}, enabling additional method
	 *         calls to be chained together.
	 * 
	 * @see ListVersionsRequest#getMaxResults()
	 * @see ListVersionsRequest#setMaxResults(Integer)
	 */
	public ListVersionsRequest withMaxResults(Integer maxResults) {
		setMaxResults(maxResults);
		return this;
	}

}
