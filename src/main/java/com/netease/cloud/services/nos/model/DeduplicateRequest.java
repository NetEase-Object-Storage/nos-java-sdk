package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

/**
 * <p>
 * Provide the data of Deduplicate operation, used to view if the same object is
 * existed or not.
 * </p>
 */
public class DeduplicateRequest extends WebServiceRequest {

	/**
	 * The name of the bucket containing the object's whose metadata is being
	 * retrieved.
	 */
	private String bucketName;

	/**
	 * The key of the object whose metadata is being retrieved.
	 */
	private String key;

	/**
     * 
     * 
     */
	private String MD5Digest;
	
	private ObjectMetadata metadata;

	/**
	 * The optional Amazon S3 storage class to use when storing the new object.
	 * If not specified, the default, standard storage class will be used.
	 * <p>
	 * For more information on Amazon S3 storage classes and available values,
	 * see the {@link StorageClass} enumeration.
	 */
	private String storageClass;

	public DeduplicateRequest(String bucketName, String key) {
		setBucketName(bucketName);
		setKey(key);
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getMD5Digest() {
		return MD5Digest;
	}

	public void setMD5Digest(String mD5Digest) {
		MD5Digest = mD5Digest;
	}

	public String getStorageClass() {
		return storageClass;
	}

	public void setStorageClass(String storageClass) {
		this.storageClass = storageClass;
	}

	public void setStorageClass(StorageClass storageClass) {
		this.storageClass = storageClass.toString();
	}

	public void setObjectMetadata(ObjectMetadata metadata) {
		this.metadata = metadata;
	}
	
	public ObjectMetadata getObjectMetadata() {
		return this.metadata;
	}
}
