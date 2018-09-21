package com.netease.cloud.services.nos.model;

import java.util.Date;

/**
 * Contains the summary of an object stored in an Nos bucket. This object
 * doesn't contain contain the object's full metadata or any of its contents.
 * 
 * @see NOSObject
 */
public class NOSObjectSummary {

	/** The name of the bucket in which this object is stored */
	protected String bucketName;

	/** The key under which this object is stored */
	protected String key;

	/** Hex encoded MD5 hash of this object's contents, as computed by Nos */
	protected String eTag;

	/** The size of this object, in bytes */
	protected long size;

	/** The date, according to Nos, when this object was last modified */
	protected Date lastModified;

	/** The class of storage used by Nos to store this object */
	protected String storageClass;

	/**
	 * The owner of this object - can be null if the requester doesn't have
	 * permission to view object ownership information
	 */
	protected Owner owner;

	/**
	 * Gets the name of the Nos bucket in which this object is stored.
	 * 
	 * @return The name of the Nos bucket in which this object is stored.
	 * 
	 * @see NOSObjectSummary#setBucketName(String)
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * Sets the name of the Nos bucket in which this object is stored.
	 * 
	 * @param bucketName
	 *            The name of the Nos bucket in which this object is stored.
	 * 
	 * @see NOSObjectSummary#getBucketName()
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Gets the key under which this object is stored in Nos.
	 * 
	 * @return The key under which this object is stored in Nos.
	 * 
	 * @see NOSObjectSummary#setKey(String)
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key under which this object is stored in Nos.
	 * 
	 * @param key
	 *            The key under which this object is stored in Nos.
	 * 
	 * @see NOSObjectSummary#getKey()
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Gets the hex encoded 128-bit MD5 hash of this object's contents as
	 * computed by Nos.
	 * 
	 * @return The hex encoded 128-bit MD5 hash of this object's contents as
	 *         computed by Nos.
	 * 
	 * @see NOSObjectSummary#setETag(String)
	 */
	public String getETag() {
		return eTag;
	}

	/**
	 * Sets the hex encoded 128-bit MD5 hash of this object's contents as
	 * computed by Nos.
	 * 
	 * @param eTag
	 *            The hex encoded 128-bit MD5 hash of this object's contents as
	 *            computed by Nos.
	 * 
	 * @see NOSObjectSummary#getETag()
	 */
	public void setETag(String eTag) {
		this.eTag = eTag;
	}

	/**
	 * Gets the size of this object in bytes.
	 * 
	 * @return The size of this object in bytes.
	 * 
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Sets the size of this object in bytes.
	 * 
	 * @param size
	 *            The size of this object in bytes.
	 * 
	 * @see NOSObjectSummary#getSize()
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * Gets the date when, according to Nos, this object was last modified.
	 * 
	 * @return The date when, according to Nos, this object was last modified.
	 * 
	 * @see NOSObjectSummary#setLastModified(Date)
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * Sets the date, according to Nos, this object was last modified.
	 * 
	 * @param lastModified
	 *            The date when, according to Nos, this object was last
	 *            modified.
	 * 
	 * @see NOSObjectSummary#getLastModified()
	 */
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * Gets the owner of this object. 
	 * 
	 * @return The owner of this object. Returns <code>null</code> if the
	 *         requester doesn't have permission to see object ownership.
	 * 
	 * @see NOSObjectSummary#setOwner(Owner)
	 */
	public Owner getOwner() {
		return owner;
	}

	/**
	 * Sets the owner of this object.
	 * 
	 * @param owner
	 *            The owner of this object.
	 * 
	 * @see NOSObjectSummary#getOwner()
	 */
	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	/**
	 * Gets the storage class used by Nos for this object.
	 * 
	 * @return The storage class used by Nos for this object.
	 * 
	 * @see NOSObjectSummary#setStorageClass(String)
	 */
	public String getStorageClass() {
		return storageClass;
	}

	/**
	 * Sets the storage class used by Nos for this object.
	 * 
	 * @param storageClass
	 *            The storage class used by Nos for this object.
	 * 
	 * @see NOSObjectSummary#getStorageClass()
	 */
	public void setStorageClass(String storageClass) {
		this.storageClass = storageClass;
	}

}
