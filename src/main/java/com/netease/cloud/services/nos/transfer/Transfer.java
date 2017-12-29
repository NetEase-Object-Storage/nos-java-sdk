package com.netease.cloud.services.nos.transfer;

import com.netease.cloud.ClientException;
import com.netease.cloud.ServiceException;
import com.netease.cloud.services.nos.model.ProgressListener;

/**
 * Represents an asynchronous upload to or download from Nos. Use this class to
 * check a tranfer's progress, add listeners for progress events, check the
 * state of a transfer, or wait for the transfer to complete.
 * <p>
 * See {@link TransferManager} for more information about creating transfers.
 * 
 * @see TransferManager#upload(String, String, java.io.File)
 * @see TransferManager#upload(com.netease.cloud.services.nos.model.PutObjectRequest)
 */
public interface Transfer {

	/**
	 * Enumeration of the possible transfer states.
	 */
	public static enum TransferState {
		/**
		 * The transfer is waiting for resources to execute and has not started
		 * yet.
		 */
		Waiting,

		/**
		 * The transfer is actively uploading or downloading and hasn't finished
		 * yet.
		 */
		InProgress,

		/** The transfer completed successfully. */
		Completed,

		/** The transfer was canceled and did not complete successfully. */
		Canceled,

		/** The transfer failed. */
		Failed;
	}

	/**
	 * Returns whether or not the transfer is finished (i.e. completed
	 * successfully, failed, or was canceled).
	 * 
	 * @return Returns <code>true</code> if this transfer is finished (i.e.
	 *         completed successfully, failed, or was canceled). Returns
	 *         <code>false</code> if otherwise.
	 */
	public boolean isDone();

	/**
	 * Waits for this transfer to complete. This is a blocking call; the current
	 * thread is suspended until this transfer completes.
	 * 
	 * @throws ClientException
	 *             If any errors were encountered in the client while making the
	 *             request or handling the response.
	 * @throws ServiceException
	 *             If any errors occurred in Nos while processing the request.
	 * @throws InterruptedException
	 *             If this thread is interrupted while waiting for the transfer
	 *             to complete.
	 */
	public void waitForCompletion() throws ClientException, ServiceException, InterruptedException;

	/**
	 * Waits for this transfer to finish and returns any error that occurred, or
	 * returns <code>null</code> if no errors occurred. This is a blocking call;
	 * the current thread will be suspended until this transfer either fails or
	 * completes successfully.
	 * 
	 * @return Any error that occurred while processing this transfer. Otherwise
	 *         returns <code>null</code> if no errors occurred.
	 * 
	 * @throws InterruptedException
	 *             If this thread is interrupted while waiting for the transfer
	 *             to complete.
	 */
	public ClientException waitForException() throws InterruptedException;

	/**
	 * Returns a human-readable description of this transfer.
	 * 
	 * @return A human-readable description of this transfer.
	 */
	public String getDescription();

	/**
	 * Returns the current state of this transfer.
	 * 
	 * @return The current state of this transfer.
	 */
	public TransferState getState();

	/**
	 * Adds the specified progress listener to the list of listeners receiving
	 * updates about this transfer's progress.
	 * 
	 * @param listener
	 *            The progress listener to add.
	 */
	public void addProgressListener(ProgressListener listener);

	/**
	 * Removes the specified progress listener from the list of progress
	 * listeners receiving updates about this transfer's progress.
	 * 
	 * @param listener
	 *            The progress listener to remove.
	 */
	public void removeProgressListener(ProgressListener listener);

	/**
	 * Returns progress information about this transfer.
	 * 
	 * @return The progress information about this transfer.
	 */
	public TransferProgress getProgress();
}
