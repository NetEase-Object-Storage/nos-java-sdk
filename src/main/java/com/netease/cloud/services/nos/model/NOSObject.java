package com.netease.cloud.services.nos.model;

import java.io.InputStream;

import com.netease.cloud.services.nos.Nos;

/**
 * Represents an object stored in Nos. This object contains the data content and
 * the object metadata stored by Nos, such as content type, content length, etc.
 * 
 * @see ObjectMetadata
 */
public class NOSObject {

	/** The key under which this object is stored */
	private String key = null;

	/** The name of the bucket in which this object is contained */
	private String bucketName = null;

	/** The metadata stored by Nos for this object */
	private ObjectMetadata metadata = new ObjectMetadata();

	/** The stream containing the contents of this object from Nos */
	private NOSObjectInputStream objectContent;

	/**
	 * Gets the metadata stored by Nos for this object. The
	 * {@link ObjectMetadata} object includes any custom user metadata supplied
	 * by the caller when the object was uploaded, as well as HTTP metadata such
	 * as content length and content type.
	 * 
	 * @return The metadata stored by Nos for this object.
	 * 
	 * @see NOSObject#getObjectContent()
	 */
	public ObjectMetadata getObjectMetadata() {
		return metadata;
	}

	/**
	 * Sets the object metadata for this object.
	 * <p>
	 * <b>NOTE:</b> This does not update the object metadata stored in Nos, but
	 * only updates this object in local memory. To update an object's metadata
	 * in Nos, use {@link Nos#copyObject(CopyObjectRequest)} to copy the object
	 * to a new (or the same location) and specify new object metadata then.
	 * 
	 * @param metadata
	 *            The new metadata to set for this object in memory.
	 */
	public void setObjectMetadata(ObjectMetadata metadata) {
		this.metadata = metadata;
	}

	/**
	 * Gets an input stream containing the contents of this object. Callers
	 * should close this input stream as soon as possible, because the object
	 * contents aren't buffered in memory and stream directly from Nos.
	 * 
	 * @return An input stream containing the contents of this object.
	 * 
	 * @see NOSObject#getObjectMetadata()
	 * @see NOSObject#setObjectContent(InputStream)
	 */
	public NOSObjectInputStream getObjectContent() {
		return objectContent;
	}

	/**
	 * Sets the input stream containing this object's contents.
	 * 
	 * @param objectContent
	 *            The input stream containing this object's contents.
	 * 
	 * @see NOSObject#getObjectContent()
	 */
	public void setObjectContent(NOSObjectInputStream objectContent) {
		this.objectContent = objectContent;
	}

	/**
	 * Sets the input stream containing this object's contents.
	 * 
	 * @param objectContent
	 *            The input stream containing this object's contents. Will get
	 *            wrapped in an NosObjectInputStream.
	 * @see NOSObject#getObjectContent()
	 */
	public void setObjectContent(InputStream objectContent) {
		setObjectContent(new NOSObjectInputStream(objectContent,
				this.objectContent != null ? this.objectContent.getHttpRequest() : null));
	}

	/**
	 * Gets the name of the bucket in which this object is contained.
	 * 
	 * @return The name of the bucket in which this object is contained.
	 * 
	 * @see NOSObject#setBucketName(String)
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * Sets the name of the bucket in which this object is contained.
	 * 
	 * @param bucketName
	 *            The name of the bucket containing this object.
	 * 
	 * @see NOSObject#getBucketName()
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Gets the key under which this object is stored.
	 * 
	 * @return The key under which this object is stored.
	 * 
	 * @see NOSObject#setKey(String)
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key under which this object is stored.
	 * 
	 * @param key
	 *            The key under which this object is stored.
	 * 
	 * @see NOSObject#getKey()
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "NosObject [key=" + getKey() + ",bucket=" + (bucketName == null ? "<Unknown>" : bucketName) + "]";
	}
}
