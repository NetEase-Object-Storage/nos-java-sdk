package com.netease.cloud.services.nos.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.netease.cloud.WebServiceRequest;
import com.netease.cloud.services.nos.Nos;

/**
 * <p>
 * Provides options for deleting multiple objects in a specified bucket. Once
 * deleted, the object(s) can only be restored if versioning was enabled when
 * the object(s) was deleted.
 * </p>
 * 
 * @see Nos#deleteObjects(DeleteObjectsRequest)
 */
public class DeleteObjectsRequest extends WebServiceRequest {

	/**
	 * The name of the bucket containing the object(s) to delete.
	 */
	private String bucketName;

	/**
	 * Whether to enable quiet mode for the response. In quiet mode, only errors
	 * are reported. Defaults to false.
	 */
	private boolean quiet;


	/**
	 * List of keys to delete, with optional versions.
	 */
	private List<String> keys = new ArrayList<String>();

	/**
	 * Constructs a new {@link DeleteObjectsRequest}, specifying the objects'
	 * bucket name.
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the object(s) to delete.
	 */
	public DeleteObjectsRequest(String bucketName) {
		setBucketName(bucketName);
	}

	/**
	 * Gets the name of the bucket containing the object(s) to delete.
	 * 
	 * @return The name of the bucket containing the object(s) to delete.
	 * @see DeleteObjectsRequest#setBucketName(String)
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * Sets the name of the bucket containing the object(s) to delete.
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the object(s) to delete.
	 * @see DeleteObjectsRequest#getBucketName()
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Sets the name of the bucket containing the object(s) to delete and
	 * returns this object, enabling additional method calls to be chained
	 * together.
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the object(s) to delete.
	 * @return The updated {@link DeleteObjectsRequest} object, enabling
	 *         additional method calls to be chained together.
	 */
	public DeleteObjectsRequest withBucketName(String bucketName) {
		setBucketName(bucketName);
		return this;
	}

	/**
	 * Sets the quiet element for this request. When true, only errors will be
	 * returned in the service response.
	 */
	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}

	/**
	 * Returns the quiet element for this request. When true, only errors will
	 * be returned in the service response.
	 */
	public boolean getQuiet() {
		return quiet;
	}

	/**
	 * Sets the quiet element for this request. When true, only errors will be
	 * returned in the service response.
	 * 
	 * @return this, to chain multiple calls together.
	 */
	public DeleteObjectsRequest withQuiet(boolean quiet) {
		this.setQuiet(quiet);
		return this;
	}

	/**
	 * Sets the list of keys to delete from this bucket, clearing any existing
	 * list of keys.
	 * 
	 * @param keys
	 *            The list of keys to delete from this bucket
	 */
	public void setKeys(List<String> keys) {
		this.keys.clear();
		this.keys.addAll(keys);
	}

	/**
	 * Sets the list of keys to delete from this bucket, clearing any existing
	 * list of keys.
	 * 
	 * @param keys
	 *            The list of keys to delete from this bucket
	 * 
	 * @return this, to chain multiple calls togethers.
	 */
	public DeleteObjectsRequest withKeys(List<String> keys) {
		setKeys(keys);
		return this;
	}

	/**
	 * Returns the list of keys to delete from this bucket.
	 */
	public List<String> getKeys() {
		return keys;
	}

	/**
	 * Convenience method to specify a set of keys without versions.
	 * 
	 * @see DeleteObjectsRequest#withKeys(List)
	 */
	public DeleteObjectsRequest withKeys(String... keys) {
		List<String> keyVersions = new LinkedList<String>();
		for (String key : keys) {
			keyVersions.add(key);
		}
		setKeys(keyVersions);
		return this;
	}
}
