package com.netease.cloud.services.nos.transfer.internal;

import java.io.File;

import com.netease.cloud.services.nos.internal.InputSubstream;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.cloud.services.nos.model.UploadPartRequest;

/**
 * Factory for creating all the individual UploadPartRequest objects for a multipart upload.
 * <p>
 * This allows us to delay creating each UploadPartRequest until we're ready for it, instead of immediately creating
 * thousands of UploadPartRequest objects for each large upload, when we won't need most of those request objects for a
 * while.
 */
public class UploadPartRequestFactory {
	private final String bucketName;
	private final String key;
	private final String uploadId;
	private final long optimalPartSize;
	private final File file;
	private final PutObjectRequest putObjectRequest;
	private int partNumber = 1;
	private long offset = 0;
	private long remainingBytes;

	public UploadPartRequestFactory(PutObjectRequest putObjectRequest, String uploadId, long optimalPartSize) {
		this.putObjectRequest = putObjectRequest;
		this.uploadId = uploadId;
		this.optimalPartSize = optimalPartSize;
		this.bucketName = putObjectRequest.getBucketName();
		this.key = putObjectRequest.getKey();
		this.file = TransferManagerUtils.getRequestFile(putObjectRequest);
		this.remainingBytes = TransferManagerUtils.getContentLength(putObjectRequest);
	}

	public synchronized boolean hasMoreRequests() {
		return (remainingBytes > 0);
	}

	public synchronized UploadPartRequest getNextUploadPartRequest() {
		long partSize = Math.min(optimalPartSize, remainingBytes);
		boolean isLastPart = (remainingBytes - partSize <= 0);

		UploadPartRequest request = null;
		if (putObjectRequest.getInputStream() != null) {
			request = new UploadPartRequest().withBucketName(bucketName).withKey(key).withUploadId(uploadId)
					.withInputStream(new InputSubstream(putObjectRequest.getInputStream(), 0, partSize, isLastPart))
					.withPartNumber(partNumber++).withPartSize(partSize);
		} else {
			request = new UploadPartRequest().withBucketName(bucketName).withKey(key).withUploadId(uploadId)
					.withFile(file).withFileOffset(offset).withPartNumber(partNumber++).withPartSize(partSize);
		}

		offset += partSize;
		remainingBytes -= partSize;

		request.setLastPart(isLastPart);
		request.setProgressListener(putObjectRequest.getProgressListener());
		if (putObjectRequest.needSetLogInfo()) {
			request.setLogID(putObjectRequest.getLogID());
			request.setLogSeq(putObjectRequest.getAndIncrementLogSeq());
		}

		return request;
	}
}
