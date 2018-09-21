package com.netease.cloud.services.nos.model;

/**
 * Listener interface for transfer progress events.
 * 
 * @see ProgressEvent
 * @see PutObjectRequest#setProgressListener(ProgressListener)
 * @see UploadPartRequest#setProgressListener(ProgressListener)
 */
public interface ProgressListener {

    /**
     * Called when progress has changed, such as additional bytes transfered,
     * transfer failed, etc.
     * 
     * @param progressEvent
     *            The event describing the progress change.
     */
    public void progressChanged(ProgressEvent progressEvent);

}
