package com.netease.cloud.internal.crypto;


import com.netease.cloud.auth.CredentialsProvider;
import com.netease.cloud.ClientException;
import com.netease.cloud.internal.NOSDirect;
import com.netease.cloud.services.nos.model.*;
import com.netease.cloud.util.IOUtils;


import java.io.*;
import java.util.Collections;
import java.util.Map;

public class NOSCryptoModuleAE extends NOSCryptoModuleBase {

    static {
        // Enable bouncy castle if available
        CryptoRuntime.enableBouncyCastle();
    }

    public NOSCryptoModuleAE( NOSDirect NOS,
                             CredentialsProvider credentialsProvider,
                             EncryptionMaterialsProvider kekMaterialsProvider, CryptoConfiguration cryptoConfig) {
        super(NOS, credentialsProvider, kekMaterialsProvider, cryptoConfig);
    }

    /**
     * Returns true if a strict encryption mode is in use in the current crypto module; false
     * otherwise.
     */
    protected boolean isStrict() {
        return false;
    }

    @Override
    public NOSObject getObjectSecurely(GetObjectRequest req) {
        // Adjust the crypto range to retrieve all of the cipher blocks needed to contain the user's
        // desired
        // range of bytes.
        long[] desiredRange = req.getRange();
        if (isStrict() && (desiredRange != null))
            throw new SecurityException(
                    "Range get and getting a part are not allowed in strict crypto mode");
        long[] adjustedCryptoRange = getAdjustedCryptoRange(desiredRange);
        if (adjustedCryptoRange != null)
            req.setRange(adjustedCryptoRange[0], adjustedCryptoRange[1]);
        // Get the object from NOS
        NOSObject retrieved = NOS.getObject(req);
        // If the caller has specified constraints, it's possible that super.getObject(...)
        // would return null, so we simply return null as well.
        if (retrieved == null)
            return null;
        String suffix = null;
        if (req instanceof EncryptedGetObjectRequest) {
            EncryptedGetObjectRequest ereq = (EncryptedGetObjectRequest) req;
            suffix = ereq.getInstructionFileSuffix();
        }
        try {
            return  decipher(req, desiredRange, adjustedCryptoRange, retrieved);
        } catch (RuntimeException ex) {
            // If we're unable to set up the decryption, make sure we close the
            // HTTP connection
            IOUtils.closeQuietly(retrieved, log);
            throw ex;
        } catch (Error error) {
            IOUtils.closeQuietly(retrieved, log);
            throw error;
        }
    }

    private NOSObject decipher(GetObjectRequest req, long[] desiredRange, long[] cryptoRange,
            NOSObject retrieved) {
        NOSObjectWrapper wrapped = new NOSObjectWrapper(retrieved);
        // Check if encryption info is in object metadata
        if (wrapped.hasEncryptionInfo())
            //元数据解密数据
            return decipherWithMetadata(req, desiredRange, cryptoRange, wrapped);
        //如果未找到密钥，则视为无密钥，尝试使用普通方式下载
        log.warn(String.format(
                "Unable to detect encryption information for object '%s' in bucket '%s'. "
                        + "Returning object without decryption.",
                retrieved.getKey(), retrieved.getBucketName()));
        // Adjust the output to the desired range of bytes.
        NOSObjectWrapper adjusted = adjustToDesiredRange(wrapped, desiredRange, null);
        return adjusted.getNOSObject();
    }


    private NOSObject decipherWithMetadata(GetObjectRequest req, long[] desiredRange,
            long[] cryptoRange, NOSObjectWrapper retrieved) {
        boolean keyWrapExpected = isStrict();
        if (req instanceof EncryptedGetObjectRequest) {
            EncryptedGetObjectRequest ereq = (EncryptedGetObjectRequest) req;
            if (!keyWrapExpected)
                keyWrapExpected = ereq.isKeyWrapExpected();
        }
        ContentCryptoMaterial cekMaterial =
                ContentCryptoMaterial.fromObjectMetadata(retrieved.getObjectMetadata(),
                        kekMaterialsProvider, cryptoConfig.getCryptoProvider(),
                        // range is sometimes necessary to compute the adjusted IV
                        cryptoRange, keyWrapExpected);
        securityCheck(cekMaterial, retrieved);
        NOSObjectWrapper decrypted = decrypt(retrieved, cekMaterial, cryptoRange);
        // Adjust the output to the desired range of bytes.
        NOSObjectWrapper adjusted = adjustToDesiredRange(decrypted, desiredRange, null);
        return adjusted.getNOSObject();
    }

    /**
     * Adjusts the retrieved NOSObject so that the object contents contain only the range of bytes
     * desired by the user. Since encrypted contents can only be retrieved in CIPHER_BLOCK_SIZE (16
     * bytes) chunks, the NOSObject potentially contains more bytes than desired, so this method
     * adjusts the contents range.
     *
     * @param NOSObject The NOSObject retrieved from NOS that could possibly contain more bytes than
     *        desired by the user.
     * @param range A two-element array of longs corresponding to the start and finish (inclusive)
     *        of a desired range of bytes.
     * @param instruction Instruction file in JSON or null if no instruction file is involved
     * @return The NOSObject with adjusted object contents containing only the range desired by the
     *         user. If the range specified is invalid, then the NOSObject is returned without any
     *         modifications.
     */
    protected final NOSObjectWrapper adjustToDesiredRange(NOSObjectWrapper NOSObject, long[] range,
            Map<String, String> instruction) {
        if (range == null)
            return NOSObject;
        // Figure out the original encryption scheme used, which can be
        // different from the crypto scheme used for decryption.
        ContentCryptoScheme encryptionScheme = NOSObject.encryptionSchemeOf(instruction);
        // range get on data encrypted using AES_GCM
        final long instanceLen = NOSObject.getObjectMetadata().getContentLength();
        final long maxOffset = instanceLen - encryptionScheme.getTagLengthInBits() / 8 - 1;
        if (range[1] > maxOffset) {
            range[1] = maxOffset;
            if (range[0] > range[1]) {
                // Return empty content
                // First let's close the existing input stream to avoid resource
                // leakage
                IOUtils.closeQuietly(NOSObject.getObjectContent(), log);
                NOSObject.setObjectContent(new ByteArrayInputStream(new byte[0]));
                return NOSObject;
            }
        }
        if (range[0] > range[1]) {
            // Make no modifications if range is invalid.
            return NOSObject;
        }
        try {
            NOSObjectInputStream objectContent = NOSObject.getObjectContent();
            InputStream adjustedRangeContents =
                    new AdjustedRangeInputStream(objectContent, range[0], range[1]);
            NOSObject.setObjectContent(new NOSObjectInputStream(adjustedRangeContents,
                    objectContent.getHttpRequest()));
            return NOSObject;
        } catch (IOException e) {
            throw new ClientException(
                    "Error adjusting output to desired byte range: " + e.getMessage());
        }
    }

    @Override
    public ObjectMetadata getObjectSecurely(GetObjectRequest getObjectRequest,
                                            File destinationFile) {
        assertParameterNotNull(destinationFile,
                "The destination file parameter must be specified when downloading an object directly to a file");

        NOSObject NOSObject = getObjectSecurely(getObjectRequest);
        // getObject can return null if constraints were specified but not met
        if (NOSObject == null)
            return null;

        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(destinationFile));
            byte[] buffer = new byte[1024 * 10];
            int bytesRead;
            while ((bytesRead = NOSObject.getObjectContent().read(buffer)) > -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new ClientException(
                    "Unable to store object contents to disk: " + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(outputStream, log);
            IOUtils.closeQuietly(NOSObject.getObjectContent(), log);
        }

        /*
         * Unlike the standard Client, the Encryption Client does not do an MD5 check
         * here because the contents stored in NOS and the contents we just retrieved are different.  In
         * NOS, the stored contents are encrypted, and locally, the retrieved contents are decrypted.
         */

        return NOSObject.getObjectMetadata();
    }

    @Override
    final MultipartUploadCryptoContext newUploadContext(InitiateMultipartUploadRequest req,
                                                        ContentCryptoMaterial cekMaterial) {
        return new MultipartUploadCryptoContext(req.getBucketName(), req.getKey(), cekMaterial);
    }

    //// specific overrides for uploading parts.
    @Override
    final CipherLite cipherLiteForNextPart(MultipartUploadCryptoContext uploadContext) {
        return uploadContext.getCipherLite();
    }

    @Override
    final long computeLastPartSize(UploadPartRequest req) {
        return req.getPartSize() + (contentCryptoScheme.getTagLengthInBits() / 8);
    }

    /*
     * Private helper methods
     */

    /**
     * Returns an updated object where the object content input stream contains the decrypted
     * contents.
     *
     * @param wrapper The object whose contents are to be decrypted.
     * @param cekMaterial The instruction that will be used to decrypt the object data.
     * @return The updated object where the object content input stream contains the decrypted
     *         contents.
     */
    private NOSObjectWrapper decrypt(NOSObjectWrapper wrapper, ContentCryptoMaterial cekMaterial,
            long[] range) {
        NOSObjectInputStream objectContent = wrapper.getObjectContent();
        wrapper.setObjectContent(new NOSObjectInputStream(new CipherLiteInputStream(objectContent,
                cekMaterial.getCipherLite(), DEFAULT_BUFFER_SIZE), objectContent.getHttpRequest()));
        return wrapper;
    }

    /**
     * Asserts that the specified parameter value is not null and if it is, throws an
     * IllegalArgumentException with the specified error message.
     *
     * @param parameterValue The parameter value being checked.
     * @param errorMessage The error message to include in the IllegalArgumentException if the
     *        specified parameter is null.
     */
    private void assertParameterNotNull(Object parameterValue, String errorMessage) {
        if (parameterValue == null)
            throw new IllegalArgumentException(errorMessage);
    }

    @Override
    protected final long ciphertextLength(long originalContentLength) {
        // Add 16 bytes for the 128-bit tag length using AES/GCM
        return originalContentLength + contentCryptoScheme.getTagLengthInBits() / 8;
    }

}
