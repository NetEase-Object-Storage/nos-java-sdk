package com.netease.cloud.internal.crypto;

import com.netease.cloud.ClientException;
import org.apache.http.annotation.GuardedBy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Contextual information for an in-flight multipart upload.
 */
public class MultipartUploadCryptoContext {
    private final String bucketName;
    private final String key;
    private boolean hasFinalPartBeenSeen;
    /**
     * the materialDescription is an optional attribute that is only non-null when the material
     * description is set on a per request basis
     */
    private Map<String, String> materialsDescription;

    private final ContentCryptoMaterial cekMaterial;
    /**
     * Can be used to enforce serial uploads.
     */
    @GuardedBy("this")
    private int partNumber;
    /**
     * True if a multi-part upload is currently in progress; false otherwise.
     */
    private volatile boolean partUploadInProgress;

    protected MultipartUploadCryptoContext(String bucketName, String key,
                                           ContentCryptoMaterial cekMaterial) {
        this.bucketName = bucketName;
        this.key = key;
        this.cekMaterial = cekMaterial;
    }

    public final String getBucketName() {
        return bucketName;
    }

    public final String getKey() {
        return key;
    }

    public final boolean hasFinalPartBeenSeen() {
        return hasFinalPartBeenSeen;
    }

    public final void setHasFinalPartBeenSeen(boolean hasFinalPartBeenSeen) {
        this.hasFinalPartBeenSeen = hasFinalPartBeenSeen;
    }

    /**
     * @return the materialsDescription
     */
    public final Map<String, String> getMaterialsDescription() {
        return materialsDescription;
    }

    /**
     * @param materialsDescription the materialsDescription to set
     */
    public final void setMaterialsDescription(Map<String, String> materialsDescription) {
        this.materialsDescription = materialsDescription == null ? null
                : Collections.unmodifiableMap(new HashMap<String, String>(materialsDescription));
    }

    /**
     * Convenient method to return the content encrypting cipher lite (which is stateful) for the
     * multi-part uploads.
     */
    CipherLite getCipherLite() {
        return cekMaterial.getCipherLite();
    }

    /**
     * Returns the content encrypting cryptographic material for the multi-part uploads.
     */
    ContentCryptoMaterial getContentCryptoMaterial() {
        return cekMaterial;
    }

    /**
     * Can be used to check the next part number must either be the same (if it was an retry) or
     * increment by exactly 1 during a serial part uploads.
     * <p>
     * As a side effect, the {@link #partUploadInProgress} will be set to true upon successful
     * completion of this method. Caller of this method is responsible to call
     * {@link #endPartUpload()} in a finally block once the respective part-upload is completed
     * (either normally or abruptly).
     *
     * @see #endPartUpload()
     *
     * @throws SdkClientException if parallel part upload is detected
     */
    void beginPartUpload(final int nextPartNumber) throws ClientException {
        if (nextPartNumber < 1)
            throw new IllegalArgumentException("part number must be at least 1");
        if (partUploadInProgress) {
            throw new ClientException("Parts are required to be uploaded in series");
        }
        synchronized (this) {
            if (partUploadInProgress) {
                throw new ClientException("Parts are required to be uploaded in series");
            }
            if (nextPartNumber - partNumber <= 1) {
                partNumber = nextPartNumber;
                partUploadInProgress = true;
            } else {
                throw new ClientException(
                        "Parts are required to be uploaded in series (partNumber=" + partNumber
                                + ", nextPartNumber=" + nextPartNumber + ")");
            }
        }
    }

    /**
     * Used to mark the completion of a part upload before the next. Should be invoked in a finally
     * block, and must be preceded previously by a call to {@link #beginPartUpload(int)}.
     * 
     * @see #beginPartUpload(int)
     */
    void endPartUpload() {
        partUploadInProgress = false;
    }
}
