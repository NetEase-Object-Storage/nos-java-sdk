package com.netease.cloud.internal;


import com.netease.cloud.services.nos.model.*;

import java.io.File;

/**
 *
 */
public abstract class NOSDirect {
    public abstract PutObjectResult putObject(PutObjectRequest req);

    public abstract NOSObject getObject(GetObjectRequest req);

    public abstract ObjectMetadata getObject(GetObjectRequest req, File dest);

    public abstract CompleteMultipartUploadResult completeMultipartUpload(
            CompleteMultipartUploadRequest req);

    public abstract InitiateMultipartUploadResult initiateMultipartUpload(
            InitiateMultipartUploadRequest req);

    public abstract UploadPartResult uploadPart(UploadPartRequest req);


    public abstract void abortMultipartUpload(AbortMultipartUploadRequest req);
}
