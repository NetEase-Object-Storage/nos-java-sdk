package com.netease.cloud.services.nos.transfer.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.netease.cloud.ClientException;
import com.netease.cloud.services.nos.Nos;
import com.netease.cloud.services.nos.model.CompleteMultipartUploadRequest;
import com.netease.cloud.services.nos.model.CompleteMultipartUploadResult;
import com.netease.cloud.services.nos.model.PartETag;
import com.netease.cloud.services.nos.model.ProgressEvent;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.cloud.services.nos.transfer.TransferManager;
import com.netease.cloud.services.nos.transfer.TransferManagerConfiguration;
import com.netease.cloud.services.nos.transfer.Transfer.TransferState;
import com.netease.cloud.services.nos.transfer.model.UploadResult;

/**
 * Manages an upload by periodically checking to see if the upload is done, and returning a result if so. Otherwise,
 * schedules a copy of itself to be run in the future and returns null. When waiting on the result of this class via a
 * Future object, clients must call {@link UploadMonitor#isDone()} and {@link UploadMonitor#getFuture()}
 */
public class UploadMonitor implements Callable<UploadResult>, TransferMonitor {

	private final Nos nos;
	private final ExecutorService threadPool;
	private final PutObjectRequest putObjectRequest;
	private ScheduledExecutorService timedThreadPool;

	private final ProgressListenerChain progressListenerChain;
	private final UploadCallable multipartUploadCallable;
	private final UploadImpl transfer;

	@SuppressWarnings("unused")
	private TransferManagerConfiguration configuration;
	/*
	 * State for tracking the upload's progress
	 */
	private String uploadId;
	private final List<Future<PartETag>> futures = new ArrayList<Future<PartETag>>();

	/*
	 * State for clients wishing to poll for completion
	 */
	private boolean isUploadDone = false;
	private Future<UploadResult> nextFuture;

	public synchronized Future<UploadResult> getFuture() {
		return nextFuture;
	}

	private synchronized void setNextFuture(Future<UploadResult> nextFuture) {
		this.nextFuture = nextFuture;
	}

	public synchronized boolean isDone() {
		return isUploadDone;
	}

	private synchronized void markAllDone() {
		isUploadDone = true;
	}

	// TODO: this could be configured in the configuration object (which we're
	// not using right now)
	private int pollInterval = 5000;

	/**
	 * Constructs a new upload watcher, which immediately submits itself to the thread pool.
	 * 
	 * @param manager
	 *            The {@link TransferManager} that owns this upload.
	 * @param transfer
	 *            The transfer being processed.
	 * @param threadPool
	 *            The {@link ExecutorService} to which we should submit new tasks.
	 * @param multipartUploadCallable
	 *            The callable responsible for processing the upload asynchronously
	 * @param putObjectRequest
	 *            The original putObject request
	 * @param progressListenerChain
	 *            A chain of listeners that wish to be notified of upload progress
	 */
	public UploadMonitor(TransferManager manager, UploadImpl transfer, ExecutorService threadPool,
			UploadCallable multipartUploadCallable, PutObjectRequest putObjectRequest,
			ProgressListenerChain progressListenerChain) {

		this.nos = manager.getNosClient();
		this.configuration = manager.getConfiguration();

		this.multipartUploadCallable = multipartUploadCallable;
		this.threadPool = threadPool;
		this.putObjectRequest = putObjectRequest;
		this.progressListenerChain = progressListenerChain;
		this.transfer = transfer;

		setNextFuture(threadPool.submit(this));
	}

	public void setTimedThreadPool(ScheduledExecutorService timedThreadPool) {
		this.timedThreadPool = timedThreadPool;
	}

	@Override
	public UploadResult call() throws Exception {
		try {
			if (uploadId == null) {
				return upload();
			} else {
				return poll();
			}
		} catch (CancellationException e) {
			transfer.setState(TransferState.Canceled);
			fireProgressEvent(ProgressEvent.CANCELED_EVENT_CODE);
			throw new ClientException("Upload canceled");
		} catch (Exception e) {
			transfer.setState(TransferState.Failed);
			fireProgressEvent(ProgressEvent.FAILED_EVENT_CODE);
			throw e;
		}
	}

	/**
	 * Polls for a result from a multipart upload and either returns it if complete, or reschedules to poll again later
	 * if not.
	 */
	private UploadResult poll() throws InterruptedException {
		for (Future<PartETag> f : futures) {
			if (!f.isDone()) {
				reschedule();
				return null;
			}
		}

		for (Future<PartETag> f : futures) {
			if (f.isCancelled()) {
				throw new CancellationException();
			}
		}

		return completeMultipartUpload();
	}

	/**
	 * Initiates the upload and checks on the result. If it has completed, returns the result; otherwise, reschedules to
	 * check back later.
	 */
	private UploadResult upload() throws Exception, InterruptedException {

		UploadResult result = multipartUploadCallable.call();

		if (result != null) {
			uploadComplete();
		} else {
			uploadId = multipartUploadCallable.getMultipartUploadId();
			futures.addAll(multipartUploadCallable.getFutures());
			reschedule();
		}

		return result;
	}

	private void uploadComplete() {
		markAllDone();
		transfer.setState(TransferState.Completed);

		// NosClient takes care of all the events for single part uploads,
		// so we only need to send a completed event for multipart uploads.
		if (multipartUploadCallable.isMultipartUpload()) {
			fireProgressEvent(ProgressEvent.COMPLETED_EVENT_CODE);
		}
	}

	private void reschedule() {
		setNextFuture(timedThreadPool.schedule(new Callable<UploadResult>() {
			public UploadResult call() throws Exception {
				setNextFuture(threadPool.submit(UploadMonitor.this));
				return null;
			}
		}, pollInterval, TimeUnit.MILLISECONDS));
	}

	private void fireProgressEvent(int eventType) {
		if (progressListenerChain == null)
			return;
		ProgressEvent event = new ProgressEvent(0);
		event.setEventCode(eventType);
		progressListenerChain.progressChanged(event);
	}

	/**
	 * Completes the multipart upload and returns the result.
	 */
	private UploadResult completeMultipartUpload() {

		CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(putObjectRequest.getBucketName(),
				putObjectRequest.getKey(), uploadId, collectPartETags());
		if (putObjectRequest.needSetLogInfo()) {
			request.setLogID(putObjectRequest.getLogID());
			request.setLogSeq(putObjectRequest.getAndIncrementLogSeq());
		}
		CompleteMultipartUploadResult completeMultipartUploadResult = nos.completeMultipartUpload(request);

		uploadComplete();

		UploadResult uploadResult = new UploadResult();
		uploadResult.setBucketName(completeMultipartUploadResult.getBucketName());
		uploadResult.setKey(completeMultipartUploadResult.getKey());
		uploadResult.setETag(completeMultipartUploadResult.getETag());
		uploadResult.setVersionId(completeMultipartUploadResult.getVersionId());
		return uploadResult;
	}

	private List<PartETag> collectPartETags() {
		final List<PartETag> partETags = new ArrayList<PartETag>(futures.size());
		for (Future<PartETag> future : futures) {
			try {
				partETags.add(future.get());
			} catch (Exception e) {
				throw new ClientException("Unable to upload part: " + e.getCause().getMessage(), e.getCause());
			}
		}
		return partETags;
	}
}
