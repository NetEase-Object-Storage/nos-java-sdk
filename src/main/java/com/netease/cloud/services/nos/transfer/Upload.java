package com.netease.cloud.services.nos.transfer;

import com.netease.cloud.ClientException;
import com.netease.cloud.ServiceException;
import com.netease.cloud.services.nos.transfer.model.UploadResult;

/**
 * Represents an asynchronous upload to Nos.
 * <p>
 * See {@link TransferManager} for more information about creating transfers.
 * </p>
 * 
 * @see TransferManager#upload(String, String, java.io.File)
 * @see TransferManager#upload(com.netease.cloud.services.nos.model.PutObjectRequest)
 */
public interface Upload extends Transfer {

	/**
	 * Waits for this upload to complete and returns the result of this upload.
	 * Be prepared to handle errors when calling this method. Any errors that
	 * occurred during the asynchronous transfer will be re-thrown through this
	 * method.
	 * 
	 * @return The result of this transfer.
	 * 
	 * @throws ClientException
	 *             If any errors were encountered in the client while making the
	 *             request or handling the response.
	 * @throws ServiceException
	 *             If any errors occurred in Nos while processing the request.
	 * @throws InterruptedException
	 *             If this thread is interrupted while waiting for the upload to
	 *             complete.
	 */
	public UploadResult waitForUploadResult() throws ClientException, ServiceException, InterruptedException;
}
