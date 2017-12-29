package com.netease.cloud.services.nos.transfer.internal;

import java.io.IOException;
import java.util.Collection;

import com.netease.cloud.services.nos.transfer.Download;
import com.netease.cloud.services.nos.transfer.MultipleFileDownload;
import com.netease.cloud.services.nos.transfer.Transfer;
import com.netease.cloud.services.nos.transfer.TransferProgress;

/**
 * Multiple file download when downloading an entire virtual directory.
 */
public class MultipleFileDownloadImpl extends MultipleFileTransfer implements MultipleFileDownload {

    private final String keyPrefix;
    private final String bucketName;
    
    public MultipleFileDownloadImpl(String description, TransferProgress transferProgress,
            ProgressListenerChain progressListenerChain, String keyPrefix, String bucketName, Collection<? extends Download> downloads) {
        super(description, transferProgress, progressListenerChain, downloads);
        this.keyPrefix = keyPrefix;
        this.bucketName = bucketName;
    }

    /**
     * Returns the key prefix of the virtual directory being downloaded.
     */
    public String getKeyPrefix() {
        return keyPrefix;
    }
    
    /**
     * Returns the name of the bucket from which files are downloaded.
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Aborts all outstanding downloads.
     */
    public void abort() throws IOException {
        for (Transfer fileDownload : subTransfers) {
            ((Download)fileDownload).abort();
        }
    }
}
