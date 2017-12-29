package com.netease.cloud.services.nos.transfer.internal;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netease.cloud.services.nos.Nos;
import com.netease.cloud.services.nos.model.AbortMultipartUploadRequest;
import com.netease.cloud.services.nos.model.CompleteMultipartUploadRequest;
import com.netease.cloud.services.nos.model.CompleteMultipartUploadResult;
import com.netease.cloud.services.nos.model.InitiateMultipartUploadRequest;
import com.netease.cloud.services.nos.model.PartETag;
import com.netease.cloud.services.nos.model.ProgressEvent;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.cloud.services.nos.model.PutObjectResult;
import com.netease.cloud.services.nos.model.StorageClass;
import com.netease.cloud.services.nos.model.UploadPartRequest;
import com.netease.cloud.services.nos.transfer.TransferManager;
import com.netease.cloud.services.nos.transfer.TransferManagerConfiguration;
import com.netease.cloud.services.nos.transfer.Transfer.TransferState;
import com.netease.cloud.services.nos.transfer.model.UploadResult;

public class UploadCallable implements Callable<UploadResult> {
	private final Nos nos;
	private final ExecutorService threadPool;
	private final PutObjectRequest putObjectRequest;
	private String multipartUploadId;
	private final UploadImpl upload;

	private static final Log log = LogFactory.getLog(UploadCallable.class);
	private final TransferManagerConfiguration configuration;
	private final ProgressListenerChain progressListenerChain;
	private final List<Future<PartETag>> futures = new ArrayList<Future<PartETag>>();

	public UploadCallable(TransferManager transferManager, ExecutorService threadPool, UploadImpl upload,
			PutObjectRequest putObjectRequest, ProgressListenerChain progressListenerChain) {
		this.nos = transferManager.getNosClient();
		this.configuration = transferManager.getConfiguration();

		this.threadPool = threadPool;
		this.putObjectRequest = putObjectRequest;
		this.progressListenerChain = progressListenerChain;
		this.upload = upload;
	}

	List<Future<PartETag>> getFutures() {
		return futures;
	}

	String getMultipartUploadId() {
		return multipartUploadId;
	}

	/**
	 * Returns true if this UploadCallable is processing a multipart upload.
	 * 
	 * @return True if this UploadCallable is processing a multipart upload.
	 */
	public boolean isMultipartUpload() {
		return TransferManagerUtils.shouldUseMultipartUpload(putObjectRequest, configuration);
	}

	public UploadResult call() throws Exception {
		upload.setState(TransferState.InProgress);
		if (isMultipartUpload()) {
			fireProgressEvent(ProgressEvent.STARTED_EVENT_CODE);
			return uploadInParts();
		} else {
			return uploadInOneChunk();
		}
	}

	/**
	 * Uploads the given request in a single chunk and returns the result.
	 */
	private UploadResult uploadInOneChunk() {
		PutObjectResult putObjectResult = nos.putObject(putObjectRequest);

		UploadResult uploadResult = new UploadResult();
		uploadResult.setBucketName(putObjectRequest.getBucketName());
		uploadResult.setKey(putObjectRequest.getKey());
		uploadResult.setETag(putObjectResult.getETag());
		//uploadResult.setVersionId(putObjectResult.getVersionId());
		return uploadResult;
	}

	/**
	 * Uploads the request in multiple chunks, submitting each upload chunk task to the thread pool and recording its
	 * corresponding Future object, as well as the multipart upload id.
	 */
	private UploadResult uploadInParts() throws Exception {
		final String bucketName = putObjectRequest.getBucketName();
		final String key = putObjectRequest.getKey();

		long optimalPartSize = getOptimalPartSize(false);

		multipartUploadId = initiateMultipartUpload(putObjectRequest);

		try {
			UploadPartRequestFactory requestFactory = new UploadPartRequestFactory(putObjectRequest, multipartUploadId,
					optimalPartSize);

			if (TransferManagerUtils.isUploadParallelizable(putObjectRequest)) {
				uploadPartsInParallel(requestFactory);

				return null;
			} else {
				return uploadPartsInSeries(requestFactory);
			}
		} catch (Exception e) {
			fireProgressEvent(ProgressEvent.FAILED_EVENT_CODE);

			try {
				nos.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, key, multipartUploadId));
			} catch (Exception e2) {
				log.info(
						"Unable to abort multipart upload, you may need to manually remove uploaded parts: "
								+ e2.getMessage(), e2);
			}
			throw e;
		} finally {
			if (putObjectRequest.getInputStream() != null) {
				try {
					putObjectRequest.getInputStream().close();
				} catch (Exception e) {
					log.warn("Unable to cleanly close input stream: " + e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Computes and returns the optimal part size for the upload.
	 */
	private long getOptimalPartSize(boolean isUsingEncryption) {
		long optimalPartSize = TransferManagerUtils.calculateOptimalPartSize(putObjectRequest, configuration);
		if (isUsingEncryption && optimalPartSize % 32 > 0) {
			// When using encryption, parts must line up correctly along cipher
			// block boundaries
			optimalPartSize = optimalPartSize - (optimalPartSize % 32) + 32;
		}
		log.debug("Calculated optimal part size: " + optimalPartSize);
		return optimalPartSize;
	}

	/**
	 * Uploads all parts in the request in serial in this thread, then completes the upload and returns the result.
	 */
	private UploadResult uploadPartsInSeries(UploadPartRequestFactory requestFactory) {

		final List<PartETag> partETags = new ArrayList<PartETag>();

		while (requestFactory.hasMoreRequests()) {
			if (threadPool.isShutdown())
				throw new CancellationException("TransferManager has been shutdown");
			UploadPartRequest uploadPartRequest = requestFactory.getNextUploadPartRequest();
			// Mark the stream in case we need to reset it
			InputStream inputStream = uploadPartRequest.getInputStream();
			if (inputStream != null && inputStream.markSupported()) {
				if (uploadPartRequest.getPartSize() >= Integer.MAX_VALUE) {
					inputStream.mark(Integer.MAX_VALUE);
				} else {
					inputStream.mark((int) uploadPartRequest.getPartSize());
				}
			}
			partETags.add(nos.uploadPart(uploadPartRequest).getPartETag());
		}

		CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(putObjectRequest.getBucketName(),
				putObjectRequest.getKey(), multipartUploadId, partETags);
		if (putObjectRequest.needSetLogInfo()) {
			request.setLogID(putObjectRequest.getLogID());
			request.setLogSeq(putObjectRequest.getAndIncrementLogSeq());
		}

		CompleteMultipartUploadResult completeMultipartUploadResult = nos.completeMultipartUpload(request);

		fireProgressEvent(ProgressEvent.COMPLETED_EVENT_CODE);

		UploadResult uploadResult = new UploadResult();
		uploadResult.setBucketName(completeMultipartUploadResult.getBucketName());
		uploadResult.setKey(completeMultipartUploadResult.getKey());
		uploadResult.setETag(completeMultipartUploadResult.getETag());
		uploadResult.setVersionId(completeMultipartUploadResult.getVersionId());

		return uploadResult;
	}

	/**
	 * Submits a callable for each part to upload to our thread pool and records its corresponding Future.
	 */
	private void uploadPartsInParallel(UploadPartRequestFactory requestFactory) {
		while (requestFactory.hasMoreRequests()) {
			if (threadPool.isShutdown())
				throw new CancellationException("TransferManager has been shutdown");
			UploadPartRequest request = requestFactory.getNextUploadPartRequest();
			futures.add(threadPool.submit(new UploadPartCallable(nos, request)));
		}
	}

	/**
	 * Initiates a multipart upload and returns the upload id
	 */
	private String initiateMultipartUpload(PutObjectRequest putObjectRequest) {

		InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(
				putObjectRequest.getBucketName(), putObjectRequest.getKey()).withCannedACL(
				putObjectRequest.getCannedAcl()).withObjectMetadata(putObjectRequest.getMetadata());
		if (putObjectRequest.needSetLogInfo()) {
			initiateMultipartUploadRequest.setLogID(putObjectRequest.getLogID());
			initiateMultipartUploadRequest.setLogSeq(putObjectRequest.getAndIncrementLogSeq());
		}

		if (putObjectRequest.getStorageClass() != null) {
			initiateMultipartUploadRequest.setStorageClass(StorageClass.fromValue(putObjectRequest.getStorageClass()));
		}

		String uploadId = nos.initiateMultipartUpload(initiateMultipartUploadRequest).getUploadId();
		log.debug("Initiated new multipart upload: " + uploadId);

		return uploadId;
	}

	private void fireProgressEvent(int eventType) {
		if (progressListenerChain == null)
			return;
		ProgressEvent event = new ProgressEvent(0);
		event.setEventCode(eventType);
		progressListenerChain.progressChanged(event);
	}
}
