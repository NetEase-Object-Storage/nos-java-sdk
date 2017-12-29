package com.netease.cloud.services.nos.transfer.internal;

import com.netease.cloud.services.nos.model.ProgressEvent;
import com.netease.cloud.services.nos.model.ProgressListener;

public class TransferProgressUpdatingListener implements ProgressListener {
    private final TransferProgressImpl transferProgress;

    public TransferProgressUpdatingListener(TransferProgressImpl transferProgress) {
        this.transferProgress = transferProgress;
    }
        
    public void progressChanged(ProgressEvent progressEvent) {
        transferProgress.updateProgress(progressEvent.getBytesTransfered());
    }
}
