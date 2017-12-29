package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;
import com.netease.cloud.services.nos.Nos;

/**
 * The InitiateMultipartUploadRequest contains the parameters used for the
 * InitiateMultipartUpload method.
 * <p>
 * Required Parameters: BucketName, Key
 *
 * @see Nos#initiateMultipartUpload(InitiateMultipartUploadRequest)
 */
public class InitiateMultipartUploadRequest extends WebServiceRequest {

    /**
     * The name of the bucket in which to create the new multipart upload, and
     * hence, the eventual object created from the multipart upload.
     */
    private String bucketName;

    /**
     * The key by which to store the new multipart upload, and hence, the
     * eventual object created from the multipart upload.
     */
    private String key;
    
   
    private String MD5Digest;

    /**
     * Additional information about the new object being created, such as
     * content type, content encoding, user metadata, etc.
     */
    public ObjectMetadata objectMetadata;

    /**
     * An optional canned Access Control List (ACL) to set permissions for the
     * new object created when the multipart upload is completed.
     */
    private CannedAccessControlList cannedACL;

    /**
     * The optional storage class to use when storing this upload's data in nos.
     * If not specified, the default storage class is used.
     */
    private StorageClass storageClass;


    /**
     * Constructs a request to initiate a new multipart upload in the specified
     * bucket, stored by the specified key.
     *
     * @param bucketName
     *            The name of the bucket in which to create the new multipart
     *            upload, and hence, the eventual object created from the
     *            multipart upload.
     * @param key
     *            The key by which to store the new multipart upload, and hence,
     *            the eventual object created from the multipart upload.
     */
    public InitiateMultipartUploadRequest(String bucketName, String key) {
        this.bucketName = bucketName;
        this.key = key;
    }
    

    /**
     * Constructs a request to initiate a new multipart upload in the specified
     * bucket, stored by the specified key, and with the additional specified
     * object metadata.
     *
     * @param bucketName
     *            The name of the bucket in which to create the new multipart
     *            upload, and hence, the eventual object created from the
     *            multipart upload.
     * @param key
     *            The key by which to store the new multipart upload, and hence,
     *            the eventual object created from the multipart upload.
     * @param objectMetadata
     *            Additional information about the new object being created,
     *            such as content type, content encoding, user metadata, etc.
     */
    public InitiateMultipartUploadRequest(String bucketName, String key, ObjectMetadata objectMetadata) {
        this.bucketName = bucketName;
        this.key = key;
        this.objectMetadata = objectMetadata;
    }
    

    /**
     * Returns the name of the bucket in which to create the new multipart
     * upload, and hence, the eventual object created from the multipart upload.
     *
     * @return The name of the bucket in which to create the new multipart
     *         upload, and hence, the eventual object created from the multipart
     *         upload.
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Sets the name of the bucket in which to create the new multipart upload,
     * and hence, the eventual object created from the multipart upload.
     *
     * @param bucketName
     *            The name of the bucket in which to create the new multipart
     *            upload, and hence, the eventual object created from the
     *            multipart upload.
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Sets the name of the bucket in which to create the new multipart upload,
     * and hence, the eventual object created from the multipart upload.
     * <p>
     * Returns this updated InitiateMultipartUploadRequest object so that
     * additional method calls can be chained together.
     *
     * @param bucketName
     *            The name of the bucket in which to create the new multipart
     *            upload, and hence, the eventual object created from the
     *            multipart upload.
     *
     * @return This updated InitiateMultipartUploadRequest object.
     */
    public InitiateMultipartUploadRequest withBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    /**
     * Returns the key by which to store the new multipart upload, and hence,
     * the eventual object created from the multipart upload.
     *
     * @return The key by which to store the new multipart upload, and hence,
     *         the eventual object created from the multipart upload.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key by which to store the new multipart upload, and hence, the
     * eventual object created from the multipart upload.
     *
     * @param key
     *            The key by which to store the new multipart upload, and hence,
     *            the eventual object created from the multipart upload.
     */
    public void setKey(String key) {
        this.key = key;
    }
    
    

    public String getMD5Digest() {
		return MD5Digest;
	}

	public void setMD5Digest(String mD5Digest) {
		MD5Digest = mD5Digest;
	}

	/**
     * Sets the key by which to store the new multipart upload, and hence, the
     * eventual object created from the multipart upload.
     * <p>
     * Returns this updated InitiateMultipartUploadRequest object so that
     * additional method calls can be chained together.
     *
     * @param key
     *            The key by which to store the new multipart upload, and hence,
     *            the eventual object created from the multipart upload.
     *
     * @return This updated InitiateMultipartUploadRequest object.
     */
    public InitiateMultipartUploadRequest withKey(String key) {
        this.key = key;
        return this;
    }

    /**
     * Returns the optional canned Access Control List (ACL) to set permissions
     * for the new object created when the multipart upload is completed.
     *
     * @return The optional canned Access Control List (ACL) to set permissions
     *         for the new object created when the multipart upload is
     *         completed.
     *
     * @see CannedAccessControlList
     */
    public CannedAccessControlList getCannedACL() {
        return cannedACL;
    }

    /**
     * Sets the optional canned Access Control List (ACL) to set permissions for
     * the new object created when the multipart upload is completed.
     *
     * @param cannedACL
     *            The canned Access Control List (ACL) to set permissions for
     *            the new object created when the multipart upload is completed.
     *
     * @see CannedAccessControlList
     */
    public void setCannedACL(CannedAccessControlList cannedACL) {
        this.cannedACL = cannedACL;
    }

    /**
     * Sets the optional canned Access Control List (ACL) to set permissions for
     * the new object created when the multipart upload is completed.
     * <p>
     * Returns this updated InitiateMultipartUploadRequest object so that
     * additional method calls can be chained together.
     *
     * @param acl
     *            The optional canned Access Control List (ACL) to set
     *            permissions for the new object created when the multipart
     *            upload is completed.
     *
     * @return This updated InitiateMultipartUploadRequest object.
     */
    public InitiateMultipartUploadRequest withCannedACL(CannedAccessControlList acl) {
        this.cannedACL = acl;
        return this;
    }

    /**
     * Returns the optional storage class to use when storing this upload's data
     * in nos. If not specified, the default storage class is used.
     * <p>
     * If not specified, the default is {@link StorageClass#Standard}.
     *
     * @return The optional storage class to use when storing this upload's data
     *         in Nos. If not specified, the default storage class is used.
     *
     * @see StorageClass
     */
    public StorageClass getStorageClass() {
        return storageClass;
    }

    /**
     * Sets the optional storage class to use when storing this upload's data in
     * Nos. If not specified, the default storage class is used.
     * <p>
     * If not specified, the default is {@link StorageClass#Standard}.
     *
     * @param storageClass
     *            The optional storage class to use when storing this upload's
     *            data in Nos. If not specified, the default storage class is
     *            used.
     *
     * @see StorageClass
     */
    public void setStorageClass(StorageClass storageClass) {
        this.storageClass = storageClass;
    }

    /**
     * Sets the optional storage class to use when storing this upload's data in
     * Nos. If not specified, the default storage class is used.
     * <p>
     * Returns this updated InitiateMultipartUploadRequest object so that
     * additional method calls can be chained together.
     *
     * @param storageClass
     *            The optional storage class to use when storing this upload's
     *            data in Nos. If not specified, the default storage class is
     *            used.
     *
     * @return This updated InitiateMultipartUploadRequest object.
     */
    public InitiateMultipartUploadRequest withStorageClass(StorageClass storageClass) {
        this.storageClass = storageClass;
        return this;
    }

    /**
     * Returns the additional information about the new object being created,
     * such as content type, content encoding, user metadata, etc.
     *
     * @return The additional information about the new object being created,
     *         such as content type, content encoding, user metadata, etc.
     */
    public ObjectMetadata getObjectMetadata() {
        return objectMetadata;
    }

    /**
     * Sets the additional information about the new object being created, such
     * as content type, content encoding, user metadata, etc.
     *
     * @param objectMetadata
     *            Additional information about the new object being created,
     *            such as content type, content encoding, user metadata, etc.
     */
    public void setObjectMetadata(ObjectMetadata objectMetadata) {
        this.objectMetadata = objectMetadata;
    }

    /**
     * Sets the additional information about the new object being created, such
     * as content type, content encoding, user metadata, etc.
     * <p>
     * Returns this updated InitiateMultipartUploadRequest object so that
     * additional method calls can be chained together.
     *
     * @param objectMetadata
     *            Additional information about the new object being created,
     *            such as content type, content encoding, user metadata, etc.
     *
     * @return This updated InitiateMultipartUploadRequest object.
     */
    public InitiateMultipartUploadRequest withObjectMetadata(ObjectMetadata objectMetadata) {
        setObjectMetadata(objectMetadata);
        return this;
    }

}
