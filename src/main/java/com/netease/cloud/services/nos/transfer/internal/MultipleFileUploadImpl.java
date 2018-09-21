package com.netease.cloud.services.nos.transfer.internal;

import java.util.Collection;

import com.netease.cloud.services.nos.transfer.MultipleFileUpload;
import com.netease.cloud.services.nos.transfer.TransferProgress;
import com.netease.cloud.services.nos.transfer.Upload;

/**
 * Multiple file upload when uploading an entire directory.
 */
public class MultipleFileUploadImpl extends MultipleFileTransfer implements MultipleFileUpload {

	private final String keyPrefix;
	private final String bucketName;

	public MultipleFileUploadImpl(String description, TransferProgress transferProgress,
			ProgressListenerChain progressListenerChain, String keyPrefix, String bucketName,
			Collection<? extends Upload> subTransfers) {
		super(description, transferProgress, progressListenerChain, subTransfers);
		this.keyPrefix = keyPrefix;
		this.bucketName = bucketName;
	}

	/**
	 * Returns the key prefix of the virtual directory being uploaded to.
	 */
	public String getKeyPrefix() {
		return keyPrefix;
	}

	/**
	 * Returns the name of the bucket to which files are uploaded.
	 */
	public String getBucketName() {
		return bucketName;
	}
}
