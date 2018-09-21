package com.netease.cloud.services.nos.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 对分块上传初始化请求进行了一定程度的封装，使得初始化请求可以注入加密算法，用于分块上传
 * This class is an extension of {@link InitiateMultipartUploadRequest} to allow
 * additional crypto related attributes to be specified.
 * <p>
 * In particular, this includes the options to
 * <ul>
 * <li>specify encryption material description on a per-request basis;</li>
 * <li>specify whether a new set of encryption material is to be created for the
 * upload or not;</li>
 * </ul>
 * In particular, {@link EncryptedInitiateMultipartUploadRequest} is only
 * recognized by {@link NOSEncryptionClient}.
 * </p>
 * <p>
 * If {@link EncryptedInitiateMultipartUploadRequest} is used against the
 * non-encrypting {@link NOSClient}, these additional attributes will be
 * ignored.
 */
public class EncryptedInitiateMultipartUploadRequest extends
        InitiateMultipartUploadRequest implements MaterialsDescriptionProvider, Serializable {
    /**
     * description of encryption materials to be used with this request.
     */
    private Map<String, String> materialsDescription;
    /**
     * True if a new set of encryption material is to be created; false
     * otherwise. Default is true.
     */
    private boolean createEncryptionMaterial = true;

    public EncryptedInitiateMultipartUploadRequest(String bucketName, String key) {
        super(bucketName, key);
    }

    public EncryptedInitiateMultipartUploadRequest(String bucketName, String key, ObjectMetadata objectMetadata) {
        super(bucketName, key, objectMetadata);
    }

    @Override
    public Map<String, String> getMaterialsDescription() {
        return materialsDescription;
    }

    /**
     * sets the materials description for the encryption materials to be used with the current Multi Part Upload Request.
     * @param materialsDescription the materialsDescription to set
     */
    public void setMaterialsDescription(Map<String, String> materialsDescription) {
        this.materialsDescription = materialsDescription == null
                ? null
                : Collections.unmodifiableMap(new HashMap<String,String>(materialsDescription))
                ;
    }

    /**
     * sets the materials description for the encryption materials to be used with the current Multi Part Upload Request.
     * @param materialsDescription the materialsDescription to set
     */
    public EncryptedInitiateMultipartUploadRequest withMaterialsDescription(Map<String, String> materialsDescription) {
        setMaterialsDescription(materialsDescription);
        return this;
    }

    /**
     * Returns true if a new set of encryption material is to be created; false
     * otherwise.  Default is true.
     */
    public boolean isCreateEncryptionMaterial() {
        return createEncryptionMaterial;
    }

    /**
     * @param createEncryptionMaterial
     *            true if a new set of encryption material is to be created;
     *            false otherwise.
     */
    public void setCreateEncryptionMaterial(boolean createEncryptionMaterial) {
        this.createEncryptionMaterial = createEncryptionMaterial;
    }

    /**
     * @param createEncryptionMaterial
     *            true if a new set of encryption material is to be created;
     *            false otherwise.
     */
    public EncryptedInitiateMultipartUploadRequest withCreateEncryptionMaterial(
            boolean createEncryptionMaterial) {
        this.createEncryptionMaterial = createEncryptionMaterial;
        return this;
    }
}