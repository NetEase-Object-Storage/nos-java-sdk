package com.netease.cloud.services.nos.transfer.internal;

import java.util.concurrent.Callable;

import com.netease.cloud.services.nos.Nos;
import com.netease.cloud.services.nos.model.PartETag;
import com.netease.cloud.services.nos.model.UploadPartRequest;

public class UploadPartCallable implements Callable<PartETag> {
    private final Nos nos;
    private final UploadPartRequest request;

    public UploadPartCallable(Nos nos, UploadPartRequest request) {
        this.nos = nos;
        this.request = request;
    }

    public PartETag call() throws Exception {
        return nos.uploadPart(request).getPartETag();
    }
}
