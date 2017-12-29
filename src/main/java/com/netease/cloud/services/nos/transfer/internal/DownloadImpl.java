package com.netease.cloud.services.nos.transfer.internal;

import java.io.IOException;

import com.netease.cloud.services.nos.model.NOSObject;
import com.netease.cloud.services.nos.model.ObjectMetadata;
import com.netease.cloud.services.nos.transfer.Download;
import com.netease.cloud.services.nos.transfer.TransferProgress;

public class DownloadImpl extends AbstractTransfer implements Download {

	final NOSObject nosObject;

	public DownloadImpl(String description, TransferProgress transferProgress,
			ProgressListenerChain progressListenerChain, NOSObject nosObject, TransferStateChangeListener listener) {
		super(description, transferProgress, progressListenerChain, listener);
		this.nosObject = nosObject;
	}

	/**
	 * Returns the ObjectMetadata for the object being downloaded.
	 * 
	 * @return The ObjectMetadata for the object being downloaded.
	 */
	public ObjectMetadata getObjectMetadata() {
		return nosObject.getObjectMetadata();
	}

	/**
	 * The name of the bucket where the object is being downloaded from.
	 * 
	 * @return The name of the bucket where the object is being downloaded from.
	 */
	public String getBucketName() {
		return nosObject.getBucketName();
	}

	/**
	 * The key under which this object was stored in Nos.
	 * 
	 * @return The key under which this object was stored in Nos.
	 */
	public String getKey() {
		return nosObject.getKey();
	}

	/**
	 * Cancels this download.
	 * 
	 * @throws IOException
	 */
	public void abort() throws IOException {
		nosObject.getObjectContent().abort();
		setState(TransferState.Canceled);
	}

}