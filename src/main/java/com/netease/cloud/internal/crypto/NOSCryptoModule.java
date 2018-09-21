package com.netease.cloud.internal.crypto;


import com.netease.cloud.services.nos.model.*;

import java.io.File;

public abstract class NOSCryptoModule {
    /**
     * @return the result of the putting the NOS object.
     */
    public abstract PutObjectResult putObjectSecurely(PutObjectRequest req);

    public abstract NOSObject getObjectSecurely(GetObjectRequest req);

    public abstract ObjectMetadata getObjectSecurely(GetObjectRequest req,
                                                     File dest);

    public abstract CompleteMultipartUploadResult completeMultipartUploadSecurely(
            CompleteMultipartUploadRequest req);

    public abstract InitiateMultipartUploadResult initiateMultipartUploadSecurely(
            InitiateMultipartUploadRequest req);

    public abstract UploadPartResult uploadPartSecurely(UploadPartRequest req);


    public abstract void abortMultipartUploadSecurely(AbortMultipartUploadRequest req);

    /**
     * 指令文件加密模式，上传指令文件接口，暂时不实现
     * @return the result of putting the instruction file in NOS; or null if the
     *         specified NOS object doesn't exist. The NOS object can be
     *         subsequently retrieved using the new instruction file via the
     *         usual get operation by specifying a
     *         {@link EncryptedGetObjectRequest}.
     *
     * @throws IllegalArgumentException
     *             if the specified NOS object doesn't exist.
     * @throws SecurityException
     *             if the protection level of the material in the new
     *             instruction file is lower than that of the original.
     *             Currently, this means if the original material has been
     *             secured via authenticated encryption, then the new
     *             instruction file cannot be created via an NOS encryption
     *             client configured with {@link CryptoMode#EncryptionOnly}.
     */
//    public abstract PutObjectResult putInstructionFileSecurely(
//            PutInstructionFileRequest req);

}
