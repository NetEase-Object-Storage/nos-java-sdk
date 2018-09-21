package com.netease.cloud.services.nos.model;

import java.util.List;

/**
 * Contains the results of listing the versions in an NOS bucket, including a
 * list of {@link NOSVersionSummary} objects describing each version,
 * information describing if this is a complete or partial listing, and the
 * original request parameters.
 * 
 * @see NOS#listVersions(String, String)
 * @see NOS#listVersions(ListVersionsRequest)
 * @see NOS#listNextBatchOfVersions(VersionListing)
 */
public class VersionListing {

	/**
	 * A list of summary information describing the versions stored in the
	 * bucket
	 */
	private List<NOSVersionSummary> versionSummaries;


	/** The name of the NOS bucket containing the listed versions */
	private String bucketName;


	/**
	 * Indicates if this is a complete listing, or if the caller needs to make
	 * additional requests to NOS to see the full object listing for an NOS
	 * bucket
	 */
	private boolean isTruncated;

	/* Original Request Parameters */

	/**
	 * The prefix parameter originally specified by the caller when this version
	 * listing was returned
	 */
	private String prefix;

	/**
	 * The key marker parameter originally specified by the caller when this
	 * version listing was returned
	 */
	private String keyMarker;

	/**
	 * The version ID marker parameter originally specified by the caller when
	 * this version listing was returned
	 */
	private String versionIdMarker;

	/**
	 * The maxKeys parameter originally specified by the caller when this
	 * version listing was returned
	 */
	private int maxKeys;


	/**
	 * Gets the list of version summaries describing the versions stored in the
	 * associated NOS bucket. Callers should remember that listings for large
	 * buckets can be truncated for performance reasons, so callers might need
	 * to make additional calls to {@link NOS#listVersions(ListVersionsRequest)}
	 * to get additional results. Callers should always check
	 * {@link VersionListing#isTruncated()} to determine if a listing is
	 * truncated or not.
	 * 
	 * @return A list of the version summaries describing the versions stored in
	 *         the associated NOS bucket.
	 */
	public List<NOSVersionSummary> getVersionSummaries() {
		return this.versionSummaries;
	}

	/**
	 * For internal use only. Sets the list of version summaries describing the
	 * versions stored in the associated NOS bucket.
	 * 
	 * @param versionSummaries
	 *            The version summaries describing the versions stored in the
	 *            associated NOS bucket.
	 */
	public void setVersionSummaries(List<NOSVersionSummary> versionSummaries) {
		this.versionSummaries = versionSummaries;
	}


	/**
	 * Gets the name of the NOS bucket containing the versions listed in this
	 * {@link VersionListing}.
	 * 
	 * @return The name of the NOS bucket containing the versions listed in this
	 *         NOSVersionListing.
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * For internal use only. Sets the name of the NOS bucket containing the
	 * versions listed in this NOSVersionListing.
	 * 
	 * @param bucketName
	 *            The name of the NOS bucket containing the versions listed in
	 *            this NOSVersionListing.
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * The prefix parameter originally used to request this version listing, or
	 * <code>null</code> if no prefix was specified. All object keys included in
	 * this version listing start with the specified prefix.
	 * 
	 * @return The prefix parameter originally used to request this version
	 *         listing, or <code>null</code> if no prefix was specified.
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * For internal use only. Sets the prefix parameter originally used to
	 * request this version listing.
	 * 
	 * @param prefix
	 *            The prefix parameter originally used to request this version
	 *            listing.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * The key marker parameter originally used to request this version listing,
	 * or <code>null</code> if no key marker was specified. If specified, all
	 * object keys included in this version listing will occur lexically
	 * (alphabetically) after the specified key marker.
	 * 
	 * @return The key marker parameter originally used to request this version
	 *         listing, or <code>null</code> if no key marker was specified.
	 */
	public String getKeyMarker() {
		return keyMarker;
	}

	/**
	 * For internal use only. Sets the key marker parameter originally used to
	 * request this version listing.
	 * 
	 * @param keyMarker
	 *            The key marker parameter originally used to request this
	 *            version listing.
	 */
	public void setKeyMarker(String keyMarker) {
		this.keyMarker = keyMarker;
	}

	/**
	 * Gets the value of the version ID marker parameter used to request this
	 * version listing. Returns <code>null</code> if no version ID marker was
	 * otherwise specified.
	 * 
	 * @return The version ID marker parameter originally used to request this
	 *         version listing. Returns <code>null</code> if no version ID
	 *         marker otherwise was specified.
	 */
	public String getVersionIdMarker() {
		return versionIdMarker;
	}

	/**
	 * For internal use only. Sets the version ID marker parameter originally
	 * used to request this version listing.
	 * 
	 * @param versionIdMarker
	 *            The version ID marker parameter originally used to request
	 *            this version listing.
	 */
	public void setVersionIdMarker(String versionIdMarker) {
		this.versionIdMarker = versionIdMarker;
	}

	/**
	 * Gets the value of the <code>maxKeys</code> parameter used to request this
	 * version listing. Returns the default <code>maxKeys</code> value provided
	 * by NOS if no parameter value was otherwise specified.
	 * <p>
	 * The <code>maxKeys</code> parameter limits the number of versions included
	 * in this version listing. A version listing will never contain more
	 * versions than indicated by <code>maxKeys</code> , but can contain less.
	 * </p>
	 * 
	 * @return The value of the <code>maxKeys</code> parameter used to request
	 *         this version listing. Returns the default <code>maxKeys</code>
	 *         value provided by NOS if no parameter value was otherwise
	 *         specified.
	 */
	public int getMaxKeys() {
		return maxKeys;
	}

	/**
	 * For internal use only. Sets the maxKeys parameter originally used to
	 * request this object listing, or the default maxKeys applied by NOS if the
	 * requester didn't specify a value.
	 * 
	 * @param maxKeys
	 *            The maxKeys parameter originally used to request this version
	 *            listing, or the default maxKeys value applied by NOS if the
	 *            requester didn't specify a value.
	 */
	public void setMaxKeys(int maxKeys) {
		this.maxKeys = maxKeys;
	}

	/**
	 * Gets whether or not the version listing is complete, indicating if
	 * additional calls to NOS are needed to obtain complete version listing
	 * results.
	 * 
	 * @return The value <code>true</code> if this version listing is complete,
	 *         indicating additional calls to NOS to NOS are needed to obtain
	 *         complete version listing results. Returns the value
	 *         <code>false</code> if otherwise.
	 */
	public boolean isTruncated() {
		return isTruncated;
	}

	/**
	 * For internal use only. Sets the truncated property for this version
	 * listing, indicating if this is a complete listing or not and whether the
	 * caller needs to make additional calls to NOS to get more version
	 * summaries.
	 * 
	 * @param isTruncated
	 *            True if this version listing is <b>not complete</b> and the
	 *            caller needs to make additional NOS calls to get additional
	 *            version summaries.
	 */
	public void setTruncated(boolean isTruncated) {
		this.isTruncated = isTruncated;
	}

}
