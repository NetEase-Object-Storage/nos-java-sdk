package com.netease.cloud.services.nos.model;

import java.util.Date;

import com.netease.cloud.services.nos.Nos;

/**
 * A multipart upload is an upload to Nos that is creating by uploading
 * individual pieces of an object, then telling Nos to complete the multipart
 * upload and concatenate all the individual pieces together into a single
 * object.
 * 
 * @see Nos#initiateMultipartUpload(InitiateMultipartUploadRequest)
 * @see Nos#uploadPart(UploadPartRequest)
 * @see Nos#completeMultipartUpload(CompleteMultipartUploadRequest)
 * @see Nos#abortMultipartUpload(AbortMultipartUploadRequest)
 */
public class MultipartUpload {

	/** The key by which this upload is stored. */
	private String key;

	/** The unique ID of this multipart upload. */
	private String uploadId;

	/** The owner of this multipart upload. */
	private Owner owner;

	/** The initiator of this multipart upload. */
	private Owner initiator;

	/**
	 * The storage class indicating how the data in this multipart upload will
	 * be stored.
	 */
	private String storageClass;

	/** The date at which this upload was initiated. */
	private Date initiated;

	/**
	 * Returns the key by which this upload is stored.
	 * 
	 * @return The key by which this upload is stored.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key by which this upload is stored.
	 * 
	 * @param key
	 *            The key by which this upload is stored.
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Returns the unique ID of this multipart upload.
	 * 
	 * @return The unique ID of this multipart upload.
	 */
	public String getUploadId() {
		return uploadId;
	}

	/**
	 * Sets the unique ID of this multipart upload.
	 * 
	 * @param uploadId
	 *            The unique ID of this multipart upload.
	 */
	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

	/**
	 * Returns the owner of this multipart upload.
	 * 
	 * @return The owner of this multipart upload.
	 */
	public Owner getOwner() {
		return owner;
	}

	/**
	 * Sets the owner of this multipart upload.
	 * 
	 * @param owner
	 *            The owner of this multipart upload.
	 */
	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	/**
	 * Returns the user who initiated this multipart upload.
	 * 
	 * @return The user who initiated this multipart upload.
	 */
	public Owner getInitiator() {
		return initiator;
	}

	/**
	 * Sets the user who initiated this multipart upload.
	 * 
	 * @param initiator
	 *            The user who initiated this multipart upload.
	 */
	public void setInitiator(Owner initiator) {
		this.initiator = initiator;
	}

	/**
	 * Returns the storage class indicating how the data in this multipart
	 * upload will be stored.
	 * 
	 * @return The storage class indicating how the data in this multipart
	 *         upload will be stored.
	 */
	public String getStorageClass() {
		return storageClass;
	}

	/**
	 * Sets the storage class indicating how the data in this multipart upload
	 * will be stored.
	 * 
	 * @param storageClass
	 *            The storage class indicating how the data in this multipart
	 *            upload will be stored.
	 */
	public void setStorageClass(String storageClass) {
		this.storageClass = storageClass;
	}

	/**
	 * Returns the date at which this upload was initiated.
	 * 
	 * @return The date at which this upload was initiated.
	 */
	public Date getInitiated() {
		return initiated;
	}

	/**
	 * Sets the date at which this upload was initiated.
	 * 
	 * @param initiated
	 *            The date at which this upload was initiated.
	 */
	public void setInitiated(Date initiated) {
		this.initiated = initiated;
	}

}
