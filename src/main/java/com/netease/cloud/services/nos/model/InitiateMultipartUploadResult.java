package com.netease.cloud.services.nos.model;

import com.netease.cloud.services.nos.Nos;

/**
 * Contains the results of initiating a multipart upload, particularly the
 * unique ID of the new multipart upload.
 *
 * @see Nos#initiateMultipartUpload(InitiateMultipartUploadRequest)
 */
public class InitiateMultipartUploadResult {

    /** The name of the bucket in which the new multipart upload was initiated */
    private String bucketName;

    /** The object key for which the multipart upload was initiated */
    private String key;

    /** The unique ID of the new multipart upload */
    private String uploadId;

    /**
     * Returns the name of the bucket in which the new multipart upload was
     * initiated.
     *
     * @return The name of the bucket in which the new multipart upload was
     *         initiated.
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Sets the name of the bucket in which the new multipart upload was
     * initiated.
     *
     * @param bucketName
     *            The name of the bucket in which the new multipart upload was
     *            initiated.
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Returns the object key for which the multipart upload was initiated.
     *
     * @return The object key for which the multipart upload was initiated.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the object key for which the multipart upload was initiated.
     *
     * @param key
     *            The object key for which the multipart upload was initiated.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Returns the initiated multipart upload ID.
     *
     * @return the initiated multipart upload ID.
     */
    public String getUploadId() {
        return uploadId;
    }

    /**
     * Sets the initiated multipart upload ID.
     *
     * @param uploadId
     *            The initiated multipart upload ID.
     */
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }
}
