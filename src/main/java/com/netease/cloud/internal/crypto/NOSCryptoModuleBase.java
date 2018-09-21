package com.netease.cloud.internal.crypto;


import com.netease.cloud.WebServiceRequest;
import com.netease.cloud.auth.CredentialsProvider;
import com.netease.cloud.ClientException;
import com.netease.cloud.internal.*;
import com.netease.cloud.services.nos.Headers;
import com.netease.cloud.services.nos.internal.InputSubstream;
import com.netease.cloud.services.nos.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.netease.cloud.internal.LengthCheckInputStream.EXCLUDE_SKIPPED_BYTES;
import static com.netease.cloud.services.nos.model.NosDataSource.Utils.cleanupDataSource;

/**
 * Common implementation for different NOS cryptographic modules.
 */
public abstract class NOSCryptoModuleBase extends NOSCryptoModule {
    private static final boolean IS_MULTI_PART = true;
    protected static final int DEFAULT_BUFFER_SIZE = 1024 * 2; // 2K
    protected final EncryptionMaterialsProvider kekMaterialsProvider;
    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final NOSCryptoScheme cryptoScheme;
    protected final ContentCryptoScheme contentCryptoScheme;
    /**
     * A read-only copy of the crypto configuration.
     */
    protected final CryptoConfiguration cryptoConfig;

    /**
     * Map of data about in progress encrypted multipart uploads.
     */
    protected final Map<String, MultipartUploadCryptoContext> multipartUploadContexts =
            Collections.synchronizedMap(new HashMap<String, MultipartUploadCryptoContext>());
    protected final NOSDirect NOS;

    /**
     * @param cryptoConfig a read-only copy of the crypto configuration.
     */
    protected NOSCryptoModuleBase(NOSDirect NOS,
                                  CredentialsProvider credentialsProvider,
                                  EncryptionMaterialsProvider kekMaterialsProvider, CryptoConfiguration cryptoConfig) {
        if (!cryptoConfig.isReadOnly())
            throw new IllegalArgumentException(
                    "The cryto configuration parameter is required to be read-only");
        this.kekMaterialsProvider = kekMaterialsProvider;
        this.NOS = NOS;
        this.cryptoConfig = cryptoConfig;
        this.cryptoScheme = NOSCryptoScheme.from(cryptoConfig.getCryptoMode());
        this.contentCryptoScheme = cryptoScheme.getContentCryptoScheme();
    }

    /**
     * Returns the length of the ciphertext computed from the length of the plaintext.
     *
     * @param plaintextLength a non-negative number
     * @return a non-negative number
     */
    protected abstract long ciphertextLength(long plaintextLength);

    //////////////////////// Common Implementation ////////////////////////
    @Override
    public PutObjectResult putObjectSecurely(PutObjectRequest req) {
        return putObjectUsingMetadata(req);
    }

    private PutObjectResult putObjectUsingMetadata(PutObjectRequest req) {
        ContentCryptoMaterial cekMaterial = createContentCryptoMaterial(req);
        // Wraps the object data with a cipher input stream
        final File fileOrig = req.getFile();
        final InputStream isOrig = req.getInputStream();
        PutObjectRequest wrappedReq = wrapWithCipher(req, cekMaterial);
        // Update the metadata
        req.setMetadata(updateMetadataWithContentCryptoMaterial(req.getMetadata(), req.getFile(),
                cekMaterial));
        // Put the encrypted object into NOS
        try {
            return NOS.putObject(wrappedReq);
        } finally {
            cleanupDataSource(req, fileOrig, isOrig, wrappedReq.getInputStream(), log);
        }
    }

    @Override
    public final void abortMultipartUploadSecurely(AbortMultipartUploadRequest req) {
        NOS.abortMultipartUpload(req);
        multipartUploadContexts.remove(req.getUploadId());
    }

    abstract MultipartUploadCryptoContext newUploadContext(InitiateMultipartUploadRequest req,
                                                           ContentCryptoMaterial cekMaterial);

    @Override
    public InitiateMultipartUploadResult initiateMultipartUploadSecurely(
            InitiateMultipartUploadRequest req) {
        // Generate a one-time use symmetric key and initialize a cipher to
        // encrypt object data
        ContentCryptoMaterial cekMaterial = createContentCryptoMaterial(req);
//        if (cryptoConfig.getStorageMode() == CryptoStorageMode.ObjectMetadata) {
//            ObjectMetadata metadata = req.getObjectMetadata();
//            if (metadata == null)
//                metadata = new ObjectMetadata();
//            // Store encryption info in metadata
//            req.setObjectMetadata(
//                    updateMetadataWithContentCryptoMaterial(metadata, null, cekMaterial));
//        }
        InitiateMultipartUploadResult result = NOS.initiateMultipartUpload(req);
        MultipartUploadCryptoContext uploadContext = newUploadContext(req, cekMaterial);
        if (req instanceof MaterialsDescriptionProvider) {
            MaterialsDescriptionProvider p = (MaterialsDescriptionProvider) req;
            uploadContext.setMaterialsDescription(p.getMaterialsDescription());
        }
        multipartUploadContexts.put(result.getUploadId(), uploadContext);
        return result;
    }

    //// specific crypto module behavior for uploading parts.
    abstract CipherLite cipherLiteForNextPart(MultipartUploadCryptoContext uploadContext);

    abstract long computeLastPartSize(UploadPartRequest req);

    /**
     * {@inheritDoc}
     *
     * <p>
     * <b>NOTE:</b> Because the encryption process requires context from previous blocks, parts
     * uploaded with the NOSEncryptionClient (as opposed to the normal NOSClient) must be
     * uploaded serially, and in order. Otherwise, the previous encryption context isn't available
     * to use when encrypting the current part.
     */
    @Override
    public UploadPartResult uploadPartSecurely(UploadPartRequest req) {
        final int blockSize = contentCryptoScheme.getBlockSizeInBytes();
        final boolean isLastPart = req.isLastPart();
        final String uploadId = req.getUploadId();
        final long partSize = req.getPartSize();
        final boolean partSizeMultipleOfCipherBlockSize = 0 == (partSize % blockSize);
        if (!isLastPart && !partSizeMultipleOfCipherBlockSize) {
            throw new ClientException(
                    "Invalid part size: part sizes for encrypted multipart uploads must be multiples "
                            + "of the cipher block size (" + blockSize
                            + ") with the exception of the last part.");
        }
        final MultipartUploadCryptoContext uploadContext = multipartUploadContexts.get(uploadId);
        if (uploadContext == null) {
            throw new ClientException(
                    "No client-side information available on upload ID " + uploadId);
        }
        final UploadPartResult result;
        // Checks the parts are uploaded in series
        uploadContext.beginPartUpload(req.getPartNumber());
        CipherLite cipherLite = cipherLiteForNextPart(uploadContext);
        final File fileOrig = req.getFile();
        final InputStream isOrig = req.getInputStream();
        SdkFilterInputStream isCurr = null;
        try {
            CipherLiteInputStream clis = newMultipartNOSCipherInputStream(req, cipherLite);
            isCurr = clis; // so the clis will be closed (in the finally block below) upon
            // unexpected failure should we opened a file undereath
            req.setInputStream(isCurr);
            // Treat all encryption requests as input stream upload requests,
            // not as file upload requests.
            req.setFile(null);
            req.setFileOffset(0);
            // The last part of the multipart upload will contain an extra
            // 16-byte mac
            if (isLastPart) {
                // We only change the size of the last part
                long lastPartSize = computeLastPartSize(req);
                if (lastPartSize > -1)
                    req.setPartSize(lastPartSize);
                if (uploadContext.hasFinalPartBeenSeen()) {
                    throw new ClientException(
                            "This part was specified as the last part in a multipart upload, but a previous part was already marked as the last part.  "
                                    + "Only the last part of the upload should be marked as the last part.");
                }
            }

            result = NOS.uploadPart(req);
        } finally {
            cleanupDataSource(req, fileOrig, isOrig, isCurr, log);
            uploadContext.endPartUpload();
        }
        if (isLastPart)
            uploadContext.setHasFinalPartBeenSeen(true);
        return result;
    }

    protected final CipherLiteInputStream newMultipartNOSCipherInputStream(UploadPartRequest req,
                                                                           CipherLite cipherLite) {
        final File fileOrig = req.getFile();
        final InputStream isOrig = req.getInputStream();
        InputStream isCurr = null;
        try {
            if (fileOrig == null) {
                if (isOrig == null) {
                    throw new IllegalArgumentException(
                            "A File or InputStream must be specified when uploading part");
                }
                isCurr = isOrig;
            } else {
                isCurr = new ResettableInputStream(fileOrig);
            }
            isCurr = new InputSubstream(isCurr, req.getFileOffset(), req.getPartSize(),
                    req.isLastPart());
            return cipherLite.markSupported()
                    ? new CipherLiteInputStream(isCurr, cipherLite, DEFAULT_BUFFER_SIZE,
                    IS_MULTI_PART, req.isLastPart())
                    : new RenewableCipherLiteInputStream(isCurr, cipherLite, DEFAULT_BUFFER_SIZE,
                    IS_MULTI_PART, req.isLastPart());
        } catch (Exception e) {
            cleanupDataSource(req, fileOrig, isOrig, isCurr, log);
            throw new ClientException("Unable to create cipher input stream", e);
        }
    }

    @Override
    public CompleteMultipartUploadResult completeMultipartUploadSecurely(
            CompleteMultipartUploadRequest req) {
        String uploadId = req.getUploadId();
        final MultipartUploadCryptoContext uploadContext = multipartUploadContexts.get(uploadId);

        if (cryptoConfig.getStorageMode() == CryptoStorageMode.ObjectMetadata) {
            ContentCryptoMaterial cekMaterial =uploadContext.getContentCryptoMaterial();
            for (Map.Entry<String, String> entry : updateMetadataWithContentCryptoMaterial(new ObjectMetadata(), null, cekMaterial).getUserMetadata().entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                req.addSpecialHeader("x-nos-meta-" + k, v);
            }
        }

        if (uploadContext != null && !uploadContext.hasFinalPartBeenSeen()) {
            throw new ClientException(
                    "Unable to complete an encrypted multipart upload without being told which part was the last.  "
                            + "Without knowing which part was the last, the encrypted data in NOS is incomplete and corrupt.");
        }
        CompleteMultipartUploadResult result = NOS.completeMultipartUpload(req);
        multipartUploadContexts.remove(uploadId);
        return result;
    }

    protected final ObjectMetadata updateMetadataWithContentCryptoMaterial(ObjectMetadata metadata,
                                                                           File file, ContentCryptoMaterial instruction) {
        if (metadata == null)
            metadata = new ObjectMetadata();
        return instruction.toObjectMetadata(metadata, cryptoConfig.getCryptoMode());
    }

    /**
     * Creates and returns a non-null content crypto material for the given request.
     */
    protected final ContentCryptoMaterial createContentCryptoMaterial(WebServiceRequest req) {
        if (req instanceof EncryptionMaterialsFactory) {
            // per request level encryption materials
            EncryptionMaterialsFactory f = (EncryptionMaterialsFactory) req;
            final EncryptionMaterials materials = f.getEncryptionMaterials();
            if (materials != null) {
                return buildContentCryptoMaterial(materials, cryptoConfig.getCryptoProvider(), req);
            }
        }
        if (req instanceof MaterialsDescriptionProvider) {
            // per request level material description
            MaterialsDescriptionProvider mdp = (MaterialsDescriptionProvider) req;
            Map<String, String> matdesc_req = mdp.getMaterialsDescription();
            ContentCryptoMaterial ccm = newContentCryptoMaterial(kekMaterialsProvider, matdesc_req,
                    cryptoConfig.getCryptoProvider(), req);
            if (ccm != null)
                return ccm;
            if (matdesc_req != null) {
                // check to see if KMS is in use and if so we should fall thru
                // to the NOS client level encryption material
                EncryptionMaterials material = kekMaterialsProvider.getEncryptionMaterials();
                throw new ClientException(
                        "No material available from the encryption material provider for description "
                                + matdesc_req);
            }
            // if there is no material description, fall thru to use
            // the per NOS client level encryption materials
        }
        // per NOS client level encryption materials
        return newContentCryptoMaterial(this.kekMaterialsProvider, cryptoConfig.getCryptoProvider(),
                req);
    }

    /**
     * Returns the content encryption material generated with the given kek material, material
     * description and security providers; or null if the encryption material cannot be found for
     * the specified description.
     */
    private ContentCryptoMaterial newContentCryptoMaterial(
            EncryptionMaterialsProvider kekMaterialProvider,
            Map<String, String> materialsDescription, Provider provider, WebServiceRequest req) {
        EncryptionMaterials kekMaterials =
                kekMaterialProvider.getEncryptionMaterials(materialsDescription);
        if (kekMaterials == null) {
            return null;
        }
        return buildContentCryptoMaterial(kekMaterials, provider, req);
    }

    /**
     * Returns a non-null content encryption material generated with the given kek material and
     * security providers.
     */
    private ContentCryptoMaterial newContentCryptoMaterial(
            EncryptionMaterialsProvider kekMaterialProvider, Provider provider,
            WebServiceRequest req) {
        EncryptionMaterials kekMaterials = kekMaterialProvider.getEncryptionMaterials();
        if (kekMaterials == null)
            throw new ClientException(
                    "No material available from the encryption material provider");
        return buildContentCryptoMaterial(kekMaterials, provider, req);
    }

    /**
     * @param materials a non-null encryption material
     */
    private ContentCryptoMaterial buildContentCryptoMaterial(EncryptionMaterials materials,
                                                             Provider provider, WebServiceRequest req) {
        // Randomly generate the IV
        final byte[] iv = new byte[contentCryptoScheme.getIVLengthInBytes()];
        cryptoScheme.getSecureRandom().nextBytes(iv);

        // Generate a one-time use symmetric key and initialize a cipher to encrypt object data
        return ContentCryptoMaterial.create(generateCEK(materials, provider), iv, materials,
                cryptoScheme, provider, req);
    }

    /**
     * @param kekMaterials non-null encryption materials
     */
    protected final SecretKey generateCEK(final EncryptionMaterials kekMaterials,
                                          final Provider providerIn) {
        final String keygenAlgo = contentCryptoScheme.getKeyGeneratorAlgorithm();
        KeyGenerator generator;
        try {
            generator = providerIn == null ? KeyGenerator.getInstance(keygenAlgo)
                    : KeyGenerator.getInstance(keygenAlgo, providerIn);
            generator.init(contentCryptoScheme.getKeyLengthInBits(),
                    cryptoScheme.getSecureRandom());
            // Set to true if the key encryption involves the use of BC's public key
            boolean involvesBCPublicKey = false;
            KeyPair keypair = kekMaterials.getKeyPair();
            if (keypair != null) {
                String keyWrapAlgo =
                        cryptoScheme.getKeyWrapScheme().getKeyWrapAlgorithm(keypair.getPublic());
                if (keyWrapAlgo == null) {
                    Provider provider = generator.getProvider();
                    String providerName = provider == null ? null : provider.getName();
                    involvesBCPublicKey = CryptoRuntime.BOUNCY_CASTLE_PROVIDER.equals(providerName);
                }
            }
            SecretKey secretKey = generator.generateKey();
            if (!involvesBCPublicKey || secretKey.getEncoded()[0] != 0)
                return secretKey;
            for (int retry = 0; retry < 10; retry++) {
                secretKey = generator.generateKey();
                if (secretKey.getEncoded()[0] != 0)
                    return secretKey;
            }
            // The probability of getting here is 2^80, which is impossible in practice.
            throw new ClientException("Failed to generate secret key");
        } catch (NoSuchAlgorithmException e) {
            throw new ClientException(
                    "Unable to generate envelope symmetric key:" + e.getMessage(), e);
        }
    }

    /**
     * Returns the given <code>PutObjectRequest</code> but has the content as input stream wrapped
     * with a cipher, and configured with some meta data and user metadata.
     */
    protected final <R extends PutObjectRequest> R wrapWithCipher(final R request,
                                                                  ContentCryptoMaterial cekMaterial) {
        // Create a new metadata object if there is no metadata already.
        ObjectMetadata metadata = request.getMetadata();
        if (metadata == null) {
            metadata = new ObjectMetadata();
        }

        // Record the original Content MD5, if present, for the unencrypted data
        if (metadata.getContentMD5() != null) {
            metadata.addUserMetadata(Headers.UNENCRYPTED_CONTENT_MD5, metadata.getContentMD5());
        }

        // Removes the original content MD5 if present from the meta data.
        metadata.setContentMD5(null);

        // Record the original, unencrypted content-length so it can be accessed
        // later
        final long plaintextLength = plaintextLength(request, metadata);
        if (plaintextLength >= 0) {
            metadata.addUserMetadata(Headers.UNENCRYPTED_CONTENT_LENGTH,
                    Long.toString(plaintextLength));
            // Put the ciphertext length in the metadata
            metadata.setContentLength(ciphertextLength(plaintextLength));
        }
        request.setMetadata(metadata);
        request.setInputStream(newNOSCipherLiteInputStream(request, cekMaterial, plaintextLength));
        // Treat all encryption requests as input stream upload requests, not as
        // file upload requests.
        request.setFile(null);
        return request;
    }

    private CipherLiteInputStream newNOSCipherLiteInputStream(PutObjectRequest req,
                                                              ContentCryptoMaterial cekMaterial, long plaintextLength) {
        final File fileOrig = req.getFile();
        final InputStream isOrig = req.getInputStream();
        InputStream isCurr = null;
        try {
            if (fileOrig == null) {
                // When input is a FileInputStream, this wrapping enables
                // unlimited mark-and-reset
                isCurr = isOrig == null ? null : ReleasableInputStream.wrap(isOrig);
            } else {
                isCurr = new ResettableInputStream(fileOrig);
            }
            if (plaintextLength > -1) {
                // NOS allows a single PUT to be no more than 5GB, which
                // therefore won't exceed the maximum length that can be
                // encrypted either using any cipher such as CBC or GCM.

                // This ensures the plain-text read from the underlying data
                // stream has the same length as the expected total.
                isCurr = new LengthCheckInputStream(isCurr, plaintextLength, EXCLUDE_SKIPPED_BYTES);
            }
            final CipherLite cipherLite = cekMaterial.getCipherLite();

            if (cipherLite.markSupported()) {
                return new CipherLiteInputStream(isCurr, cipherLite, DEFAULT_BUFFER_SIZE);
            } else {
                return new RenewableCipherLiteInputStream(isCurr, cipherLite, DEFAULT_BUFFER_SIZE);
            }
        } catch (Exception e) {
            cleanupDataSource(req, fileOrig, isOrig, isCurr, log);
            throw new ClientException("Unable to create cipher input stream", e);
        }
    }

    /**
     * Returns the plaintext length from the request and metadata; or -1 if unknown.
     */
    protected final long plaintextLength(PutObjectRequest request,
                                         ObjectMetadata metadata) {
        if (request.getFile() != null) {
            return request.getFile().length();
        } else if (request.getInputStream() != null
                && metadata.getRawMetadata() != null) {
            return metadata.getContentLength();
        }
        return -1;
    }

    public final NOSCryptoScheme getNOSCryptoScheme() {
        return cryptoScheme;
    }

    /**
     * Checks if the the crypto scheme used in the given content crypto material is allowed to be
     * used in this crypto module. Default is no-op. Subclass may override.
     *
     * @throws SecurityException if the crypto scheme used in the given content crypto material is
     *                           not allowed in this crypto module.
     */
    protected void securityCheck(ContentCryptoMaterial cekMaterial, NOSObjectWrapper retrieved) {
    }


    static long[] getAdjustedCryptoRange(long[] range) {
        // If range is invalid, then return null.
        if (range == null || range[0] > range[1]) {
            return null;
        }
        long[] adjustedCryptoRange = new long[2];
        adjustedCryptoRange[0] = getCipherBlockLowerBound(range[0]);
        adjustedCryptoRange[1] = getCipherBlockUpperBound(range[1]);
        return adjustedCryptoRange;
    }

    private static long getCipherBlockLowerBound(long leftmostBytePosition) {
        long cipherBlockSize = JceEncryptionConstants.SYMMETRIC_CIPHER_BLOCK_SIZE;
        long offset = leftmostBytePosition % cipherBlockSize;
        long lowerBound = leftmostBytePosition - offset - cipherBlockSize;
        return lowerBound < 0 ? 0 : lowerBound;
    }

    /**
     * Takes the position of the rightmost desired byte of a user specified range and returns the
     * position of the end of the following cipher block; or {@value Long#MAX_VALUE} if the
     * resultant position has a value that exceeds {@value Long#MAX_VALUE}.
     */
    private static long getCipherBlockUpperBound(final long rightmostBytePosition) {
        long cipherBlockSize = JceEncryptionConstants.SYMMETRIC_CIPHER_BLOCK_SIZE;
        long offset = cipherBlockSize - (rightmostBytePosition % cipherBlockSize);
        long upperBound = rightmostBytePosition + offset + cipherBlockSize;
        return upperBound < 0 ? Long.MAX_VALUE : upperBound;
    }
}
