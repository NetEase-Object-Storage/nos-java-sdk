package com.netease.cloud.services.nos.model;

import java.io.Serializable;

/**
 * <p>
 * An extension of {@link GetObjectRequest} to allow additional encryption material description to
 * be specified on a per-request basis. In particular, {@link EncryptedGetObjectRequest} is only
 * recognized by {@link NOSEncryptionClient}.
 * </p>
 * <p>
 * If {@link EncryptedGetObjectRequest} is used against the non-encrypting
 * {@link NOSEncryptionClient}, the additional attributes will be ignored.
 * </p>
 * <p>
 * The additional material description must not conflict with the existing one saved in NOS or else
 * will cause the get request to fail fast later on.
 */
public class EncryptedGetObjectRequest extends GetObjectRequest implements Serializable {
    /**
     * Used to retrieve the NOS encrypted object via instruction file with an explicit suffix.
     * Applicable only if specified (which means non-null and non-blank.)
     */
    private String instructionFileSuffix;
    /**
     * True if the retrieval of the encrypted object expects the CEK to have been key-wrapped;
     * Default is false.
     * <p>
     * Note, however, that if {@link CryptoMode#StrictAuthenticatedEncryption} is in use, key
     * wrapping is always expected for the CEK regardless.
     */
    private boolean keyWrapExpected;

    public EncryptedGetObjectRequest(String bucketName, String key) {
        this(bucketName, key, null);
    }

    public EncryptedGetObjectRequest(String bucketName, String key, String versionId) {
        super(bucketName, key, versionId);
        setKey(key);
        setVersionId(versionId);
    }

    public String getInstructionFileSuffix() {
        return instructionFileSuffix;
    }

    /**
     * Explicitly sets the suffix of an instruction file to be used to retrieve the NOS encrypted
     * object. Typically this is for more advanced use cases where multiple crypto instruction files
     * have been created for the same NOS object. Each instruction file contains the same CEK
     * encrypted under a different KEK, the IV, and other meta information (aka material
     * description).
     * 
     * @param instructionFileSuffix suffix of the instruction file to be used.
     * 
     * @see NOSEncryptionClient#putInstructionFile(PutInstructionFileRequest)
     */
    public void setInstructionFileSuffix(String instructionFileSuffix) {
        this.instructionFileSuffix = instructionFileSuffix;
    }

    /**
     * Fluent API to explicitly sets the suffix of an instruction file to be used to retrieve the NOS
     * encrypted object. Typically this is for more advanced use cases where multiple crypto
     * instruction files have been created for the same NOS object. Each instruction file contains
     * the same CEK encrypted under a different KEK, the IV, and other meta information (aka
     * material description).
     * 
     * @param instructionFileSuffix suffix of the instruction file to be used.
     * 
     * @see NOSEncryptionClient#putInstructionFile(PutInstructionFileRequest)
     */
    public EncryptedGetObjectRequest withInstructionFileSuffix(String instructionFileSuffix) {
        this.instructionFileSuffix = instructionFileSuffix;
        return this;
    }

    /**
     * Returns true if key wrapping is expected; false otherwise. Note, however, that if
     * {@link CryptoMode#StrictAuthenticatedEncryption} or KMS is in use, key wrapping is always
     * expected for the CEK regardless.
     */
    public boolean isKeyWrapExpected() {
        return keyWrapExpected;
    }

    /**
     * @param keyWrapExpected true if key wrapping is expected for the CEK; false otherwse. Note,
     *        however, that if {@link CryptoMode#StrictAuthenticatedEncryption} or KMS is in use,
     *        key wrapping is always expected for the CEK regardless.
     *        <p>
     *        If keyWrapExpected is set to true but the CEK is found to be not key-wrapped, it would
     *        cause a {@link KeyWrapException} to be thrown.
     */
    public void setKeyWrapExpected(boolean keyWrapExpected) {
        this.keyWrapExpected = keyWrapExpected;
    }

    /**
     * Fluent API for {@link #setKeyWrapExpected(boolean)}.
     */
    public EncryptedGetObjectRequest withKeyWrapExpected(boolean keyWrapExpected) {
        this.keyWrapExpected = keyWrapExpected;
        return this;
    }
}
