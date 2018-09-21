package com.netease.cloud.internal.crypto;



import com.netease.cloud.ClientException;
import com.netease.cloud.services.nos.Headers;
import com.netease.cloud.services.nos.model.NOSObject;
import com.netease.cloud.services.nos.model.NOSObjectInputStream;
import com.netease.cloud.services.nos.model.ObjectMetadata;
import com.netease.cloud.util.StringUtils;

import java.io.*;
import java.util.Map;

class NOSObjectWrapper implements Closeable {
    private final NOSObject NOSobj;

    NOSObjectWrapper(NOSObject NOSobj) {
        if (NOSobj == null)
            throw new IllegalArgumentException();
        this.NOSobj = NOSobj;
    }

    ObjectMetadata getObjectMetadata() {
        return NOSobj.getObjectMetadata();
    }

    void setObjectMetadata(ObjectMetadata metadata) {
        NOSobj.setObjectMetadata(metadata);
    }

    NOSObjectInputStream getObjectContent() {
        return NOSobj.getObjectContent();
    }

    void setObjectContent(NOSObjectInputStream objectContent) {
        NOSobj.setObjectContent(objectContent);
    }

    void setObjectContent(InputStream objectContent) {
        NOSobj.setObjectContent(objectContent);
    }

    String getBucketName() {
        return NOSobj.getBucketName();
    }

    void setBucketName(String bucketName) {
        NOSobj.setBucketName(bucketName);
    }

    String getKey() {
        return NOSobj.getKey();
    }

    void setKey(String key) {
        NOSobj.setKey(key);
    }

    @Override
    public String toString() {
        return NOSobj.toString();
    }

    /**
     * Returns true if this NOS object has the encryption information stored as user meta data; false
     * otherwise.
     */
    final boolean hasEncryptionInfo() {
        ObjectMetadata metadata = NOSobj.getObjectMetadata();
        Map<String, String> userMeta = metadata.getUserMetadata();
        return userMeta != null && userMeta.containsKey(Headers.CRYPTO_IV)
                && (userMeta.containsKey(Headers.CRYPTO_KEY_V2)
                        || userMeta.containsKey(Headers.CRYPTO_KEY));
    }

    /**
     * Converts and return the underlying NOS object as a json string.
     * 
     * @throws ClientException if failed in JSON conversion.
     */
    String toJsonString() {
        try {
            return from(NOSobj.getObjectContent());
        } catch (Exception e) {
            throw new ClientException("Error parsing JSON: " + e.getMessage());
        }
    }

    private static String from(InputStream is) throws IOException {
        if (is == null)
            return "";
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StringUtils.UTF8));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            is.close();
        }
        return sb.toString();
    }

    @Override
    public void close() throws IOException {
        NOSobj.close();
    }

    NOSObject getNOSObject() {
        return NOSobj;
    }

    /**
     * Returns the original crypto scheme used for encryption, which may differ from the crypto
     * scheme used for decryption during, for example, a range-get operation.
     * 
     * @param instructionFile the instruction file of the NOS object; or null if there is none.
     */
    ContentCryptoScheme encryptionSchemeOf(Map<String, String> instructionFile) {
        if (instructionFile != null) {
            String cekAlgo = instructionFile.get(Headers.CRYPTO_CEK_ALGORITHM);
            return ContentCryptoScheme.fromCEKAlgo(cekAlgo);
        }
        ObjectMetadata meta = NOSobj.getObjectMetadata();
        Map<String, String> userMeta = meta.getUserMetadata();
        String cekAlgo = userMeta.get(Headers.CRYPTO_CEK_ALGORITHM);
        return ContentCryptoScheme.fromCEKAlgo(cekAlgo);
    }
}
