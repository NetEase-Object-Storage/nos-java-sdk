package com.netease.cloud.services.nos.transfer;

import java.io.IOException;

import com.netease.cloud.services.nos.model.ObjectMetadata;

/**
 * Represents an asynchronous download from Nos.
 * <p>
 * See {@link TransferManager} for more information about creating transfers.
 * </p>
 * 
 */
public interface Download extends Transfer {

	/**
	 * Returns the ObjectMetadata for the object being downloaded.
	 * 
	 * @return The ObjectMetadata for the object being downloaded.
	 */
	public ObjectMetadata getObjectMetadata();

	/**
	 * The name of the bucket where the object is being downloaded from.
	 * 
	 * @return The name of the bucket where the object is being downloaded from.
	 */
	public String getBucketName();

	/**
	 * The key under which this object was stored in Nos.
	 * 
	 * @return The key under which this object was stored in Nos.
	 */
	public String getKey();

	/**
	 * Cancels this download.
	 * 
	 * @throws IOException
	 */
	public void abort() throws IOException;

}