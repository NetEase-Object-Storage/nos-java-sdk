package com.netease.cloud.services.nos;
import com.netease.cloud.ClientConfiguration;
import com.netease.cloud.ClientException;
import com.netease.cloud.auth.CredentialsProvider;

import com.netease.cloud.internal.NOSDirect;
import com.netease.cloud.internal.crypto.CryptoConfiguration;
import com.netease.cloud.internal.crypto.CryptoModuleDispatcher;
import com.netease.cloud.internal.crypto.EncryptionMaterialsProvider;
import com.netease.cloud.internal.crypto.NOSCryptoModule;
import com.netease.cloud.services.nos.model.*;

import java.io.File;

public class NOSEncryptionClient extends NosClient {

    private final NOSCryptoModule crypto;

    public NOSEncryptionClient(CredentialsProvider credentialsProvider,
                               EncryptionMaterialsProvider kekMaterialsProvider, ClientConfiguration clientConfig,
                               CryptoConfiguration cryptoConfig) {
        super(credentialsProvider.getCredentials(), clientConfig);
        assertParameterNotNull(kekMaterialsProvider,
                "EncryptionMaterialsProvider parameter must not be null.");
        assertParameterNotNull(cryptoConfig, "CryptoConfiguration parameter must not be null.");
        this.crypto = new CryptoModuleDispatcher(new NOSDirectImpl(), credentialsProvider,
                kekMaterialsProvider, cryptoConfig);
    }

    private void assertParameterNotNull(Object parameterValue, String errorMessage) {
        if (parameterValue == null)
            throw new IllegalArgumentException(errorMessage);
    }

    @Override
    public PutObjectResult putObject(PutObjectRequest req) {
        return crypto.putObjectSecurely(req);
    }

    @Override
    public NOSObject getObject(GetObjectRequest req) {
        return crypto.getObjectSecurely(req);
    }

    @Override
    public ObjectMetadata getObject(GetObjectRequest req, File dest) {
        return crypto.getObjectSecurely(req, dest);
    }

    /**
     * @param req
     */
    @Override
    public void deleteObject(DeleteObjectRequest req) {
        // Delete the object
        super.deleteObject(req);
    }

    @Override
    public CompleteMultipartUploadResult completeMultipartUpload(
            CompleteMultipartUploadRequest req) {
        return crypto.completeMultipartUploadSecurely(req);
    }

    @Override
    public InitiateMultipartUploadResult initiateMultipartUpload(
            InitiateMultipartUploadRequest req) {
        boolean isCreateEncryptionMaterial = true;
        if (req instanceof EncryptedInitiateMultipartUploadRequest) {
            EncryptedInitiateMultipartUploadRequest cryptoReq =
                    (EncryptedInitiateMultipartUploadRequest) req;
            isCreateEncryptionMaterial = cryptoReq.isCreateEncryptionMaterial();
        }
        return isCreateEncryptionMaterial ? crypto.initiateMultipartUploadSecurely(req)
                : super.initiateMultipartUpload(req);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Because the encryption process requires context from block N-1 in order to encrypt block N,
     * parts uploaded with the NOSEncryptionClient (as opposed to the normal NOSClient) must be
     * uploaded serially, and in order. Otherwise, the previous encryption context isn't available
     * to use when encrypting the current part.
     */
    @Override
    public UploadPartResult uploadPart(UploadPartRequest uploadPartRequest)
            throws ClientException {
        return crypto.uploadPartSecurely(uploadPartRequest);
    }


    @Override
    public void abortMultipartUpload(AbortMultipartUploadRequest req) {
        crypto.abortMultipartUploadSecurely(req);
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the a default internal KMS client has been constructed, it will also be shut down by
     * calling this method. Otherwise, users who provided the KMS client would be responsible to
     * shut down the KMS client extrinsic to this method.
     */
    @Override
    public void shutdown() {
        super.shutdown();
        // if (isKMSClientInternal)
        // kms.shutdown();
    }

    // /////////////////// Access to the methods in the super class //////////

    /**
     * An internal implementation used to provide limited but direct access to the underlying
     * methods of NOSClient without any encryption or decryption operations.
     */
    private final class NOSDirectImpl extends NOSDirect {
        @Override
        public PutObjectResult putObject(PutObjectRequest req) {
            return NOSEncryptionClient.super.putObject(req);
        }

        @Override
        public NOSObject getObject(GetObjectRequest req) {
            return NOSEncryptionClient.super.getObject(req);
        }

        @Override
        public ObjectMetadata getObject(GetObjectRequest req, File dest) {
            return NOSEncryptionClient.super.getObject(req, dest);
        }

        @Override
        public CompleteMultipartUploadResult completeMultipartUpload(
                CompleteMultipartUploadRequest req) {
            return NOSEncryptionClient.super.completeMultipartUpload(req);
        }

        @Override
        public InitiateMultipartUploadResult initiateMultipartUpload(
                InitiateMultipartUploadRequest req) {
            return NOSEncryptionClient.super.initiateMultipartUpload(req);
        }

        @Override
        public UploadPartResult uploadPart(UploadPartRequest req)
                throws ClientException {
            return NOSEncryptionClient.super.uploadPart(req);
        }

        @Override
        public void abortMultipartUpload(AbortMultipartUploadRequest req) {
            NOSEncryptionClient.super.abortMultipartUpload(req);
        }
    }

}
