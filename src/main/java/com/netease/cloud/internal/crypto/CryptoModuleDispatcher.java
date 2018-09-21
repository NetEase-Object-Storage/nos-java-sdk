package com.netease.cloud.internal.crypto;


import com.netease.cloud.ServiceException;
import com.netease.cloud.auth.CredentialsProvider;
import com.netease.cloud.ClientException;
import com.netease.cloud.internal.NOSDirect;
import com.netease.cloud.services.nos.model.*;

import java.io.File;

import static com.netease.cloud.internal.crypto.CryptoMode.AuthenticatedEncryption;

public class CryptoModuleDispatcher extends NOSCryptoModule {
    private final CryptoMode defaultCryptoMode;
    /**
     * Authenticated encryption (AE) cryptographic module.
     */
    private final NOSCryptoModuleAE ae;

    public CryptoModuleDispatcher(NOSDirect NOS,
                                  CredentialsProvider credentialsProvider,
                                  EncryptionMaterialsProvider encryptionMaterialsProvider,
                                  CryptoConfiguration cryptoConfig) {
        cryptoConfig = cryptoConfig.clone(); // make a clone
        CryptoMode cryptoMode = cryptoConfig.getCryptoMode();
        if (cryptoMode == null) {
            cryptoMode = AuthenticatedEncryption;
            cryptoConfig.setCryptoMode(cryptoMode); // defaults to AE
        }
        cryptoConfig = cryptoConfig.readOnly(); // make read-only
        this.defaultCryptoMode = cryptoConfig.getCryptoMode();
        switch (this.defaultCryptoMode) {
            //严格模式，暂时不考虑实现
//            case StrictAuthenticatedEncryption:
//                this.ae = new NOSCryptoModuleAEStrict( NOS, credentialsProvider,
//                        encryptionMaterialsProvider, cryptoConfig);
//                break;
            case AuthenticatedEncryption:
                //非严格模式
                this.ae = new NOSCryptoModuleAE(NOS, credentialsProvider,
                        encryptionMaterialsProvider, cryptoConfig);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public PutObjectResult putObjectSecurely(PutObjectRequest putObjectRequest) {
        return ae.putObjectSecurely(putObjectRequest);
    }

    @Override
    public NOSObject getObjectSecurely(GetObjectRequest req) {
        // AE module can handle NOS objects encrypted in either AE format
        return ae.getObjectSecurely(req);
    }

    @Override
    public ObjectMetadata getObjectSecurely(GetObjectRequest req, File destinationFile) {
        // AE module can handle NOS objects encrypted in either AE or EO format
        return ae.getObjectSecurely(req, destinationFile);
    }

    @Override
    public CompleteMultipartUploadResult completeMultipartUploadSecurely(
            CompleteMultipartUploadRequest req) throws ClientException, ServiceException {
        return ae.completeMultipartUploadSecurely(req);
    }

    @Override
    public void abortMultipartUploadSecurely(AbortMultipartUploadRequest req) {
        ae.abortMultipartUploadSecurely(req);
    }

    @Override
    public InitiateMultipartUploadResult initiateMultipartUploadSecurely(
            InitiateMultipartUploadRequest req) throws ClientException, ServiceException {
        return ae.initiateMultipartUploadSecurely(req);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * <b>NOTE:</b> Because the encryption process requires context from block N-1 in order to
     * encrypt block N, parts uploaded with the NOSEncryptionClient (as opposed to the normal
     * NOSClient) must be uploaded serially, and in order. Otherwise, the previous encryption
     * context isn't available to use when encrypting the current part.
     */
    @Override
    public UploadPartResult uploadPartSecurely(UploadPartRequest req)
            throws ClientException {
        return ae.uploadPartSecurely(req);
    }
}
