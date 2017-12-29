package com.netease.cloud.services.nos.transfer.internal;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.netease.cloud.ClientException;
import com.netease.cloud.ServiceException;
import com.netease.cloud.services.nos.transfer.Upload;
import com.netease.cloud.services.nos.transfer.model.UploadResult;

public class UploadImpl extends AbstractTransfer implements Upload {
    
    public UploadImpl(String description, TransferProgressImpl transferProgressInternalState,
            ProgressListenerChain progressListenerChain, TransferStateChangeListener listener) {
        super(description, transferProgressInternalState, progressListenerChain, listener);
    }

    /**
     * Waits for this upload to complete and returns the result of this
     * upload. Be prepared to handle errors when calling this method. Any
     * errors that occurred during the asynchronous transfer will be re-thrown
     * through this method.
     * 
     * @return The result of this transfer.
     * 
     * @throws ClientException
     *             If any errors were encountered in the client while making the
     *             request or handling the response.
     * @throws ServiceException
     *             If any errors occurred in Amazon Nos while processing the
     *             request.
     * @throws InterruptedException
     *             If this thread is interrupted while waiting for the upload to
     *             complete.
     */
    public UploadResult waitForUploadResult() 
            throws ClientException, ServiceException, InterruptedException {
        try {
            UploadResult result = null;
            while (!monitor.isDone() || result == null) {
                Future<?> f = monitor.getFuture();
                result = (UploadResult)f.get();
            }
            return result;
        } catch (ExecutionException e) {
            rethrowExecutionException(e);
            return null;
        }
    }

}
