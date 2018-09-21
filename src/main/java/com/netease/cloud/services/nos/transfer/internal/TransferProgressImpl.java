package com.netease.cloud.services.nos.transfer.internal;

import com.netease.cloud.services.nos.transfer.TransferProgress;

public class TransferProgressImpl extends TransferProgress {
    
    public synchronized void updateProgress(long bytes) {
        this.bytesTransfered += bytes;
    }

    public void setBytesTransfered(long bytesTransfered) {
        this.bytesTransfered = bytesTransfered;
    }

    public void setTotalBytesToTransfer(long totalBytesToTransfer) {
        this.totalBytesToTransfer = totalBytesToTransfer;
    }
}
