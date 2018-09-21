package com.netease.cloud.services.nos.transfer.internal;

import static com.netease.cloud.services.nos.internal.Constants.*;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.cloud.services.nos.transfer.TransferManagerConfiguration;

/**
 * Internal utilities for multipart uploads with TransferManager.
 */
public class TransferManagerUtils {

	/**
	 * Returns a new thread pool configured with the default settings.
	 * 
	 * @return A new thread pool configured with the default settings.
	 */
	public static ThreadPoolExecutor createDefaultExecutorService() {
		ThreadFactory threadFactory = new ThreadFactory() {
			private int threadCount = 1;

			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setName("Nos-transfer-manager-worker-" + threadCount++);
				return thread;
			}
		};
		return (ThreadPoolExecutor) Executors.newFixedThreadPool(10, threadFactory);
	}

	/**
	 * Returns true if the specified upload request can use parallel part
	 * uploads for increased performance.
	 * 
	 * @param putObjectRequest
	 *            The request to check.
	 * 
	 * @return True if this request can use parallel part uploads for faster
	 *         uploads.
	 */
	public static boolean isUploadParallelizable(final PutObjectRequest putObjectRequest) {

		// Otherwise, if there's a file, we can process the uploads
		// concurrently.
		return (getRequestFile(putObjectRequest) != null);
	}

	/**
	 * Returns the size of the data in this request, otherwise -1 if the content
	 * length is unknown.
	 * 
	 * @param putObjectRequest
	 *            The request to check.
	 * 
	 * @return The size of the data in this request, otherwise -1 if the size of
	 *         the data is unknown.
	 */
	public static long getContentLength(PutObjectRequest putObjectRequest) {
		File file = getRequestFile(putObjectRequest);
		if (file != null)
			return file.length();

		if (putObjectRequest.getInputStream() != null) {
			if (putObjectRequest.getMetadata().getContentLength() > 0) {
				return putObjectRequest.getMetadata().getContentLength();
			}
			// else{
			// return Long.MAX_VALUE;
			// }
		}

		return -1;
	}

	/**
	 * Returns the optimal part size, in bytes, for each individual part upload
	 * in a multipart upload.
	 * 
	 * @param putObjectRequest
	 *            The request containing all the details of the upload.
	 * @param configuration
	 *            Configuration values to use when calculating size.
	 * 
	 * @return The optimal part size, in bytes, for each individual part upload
	 *         in a multipart upload.
	 */
	public static long calculateOptimalPartSize(PutObjectRequest putObjectRequest,
			TransferManagerConfiguration configuration) {
		double contentLength = TransferManagerUtils.getContentLength(putObjectRequest);
		double optimalPartSize = (double) contentLength / (double) MAXIMUM_UPLOAD_PARTS;
		// round up so we don't push the upload over the maximum number of parts
		optimalPartSize = Math.ceil(optimalPartSize);
		return (long) Math.max(optimalPartSize, configuration.getMinimumUploadPartSize());
	}

	/**
	 * Returns true if the the specified request should be processed as a
	 * multipart upload (instead of a single part upload).
	 * 
	 * @param putObjectRequest
	 *            The request containing all the details of the upload.
	 * @param configuration
	 *            Configuration settings controlling how transfer manager
	 *            processes requests.
	 * 
	 * @return True if the the specified request should be processed as a
	 *         multipart upload.
	 */
	public static boolean shouldUseMultipartUpload(PutObjectRequest putObjectRequest,
			TransferManagerConfiguration configuration) {
		long contentLength = TransferManagerUtils.getContentLength(putObjectRequest);
		return (contentLength > configuration.getMultipartUploadThreshold());
	}

	/**
	 * Convenience method for getting the file specified in a request.
	 */
	public static File getRequestFile(final PutObjectRequest putObjectRequest) {
		if (putObjectRequest.getFile() != null)
			return putObjectRequest.getFile();
		return null;
	}

}
