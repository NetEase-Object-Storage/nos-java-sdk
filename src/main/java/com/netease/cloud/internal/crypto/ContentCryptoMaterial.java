package com.netease.cloud.internal.crypto;


import com.netease.cloud.ClientException;
import com.netease.cloud.WebServiceRequest;
import com.netease.cloud.services.nos.Headers;
import com.netease.cloud.services.nos.model.MaterialsDescriptionProvider;
import com.netease.cloud.services.nos.model.ObjectMetadata;
import com.netease.cloud.util.Base64;
import com.netease.cloud.util.Jackson;
import com.netease.cloud.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.*;

/**
 * Cryptographic material used for client-side content encrypt/decryption in NOS. This includes the
 * randomly generated one-time secured CEK (content-encryption-key) and the respective key wrapping
 * algorithm, if any, and the cryptographic scheme in use.
 */
final class ContentCryptoMaterial {
    // null if cek is not secured via key wrapping
    private final String keyWrappingAlgorithm;
    private final CipherLite cipherLite;

    private final Map<String, String> kekMaterialsDescription;
    private final byte[] encryptedCEK;

    ContentCryptoMaterial(Map<String, String> kekMaterialsDescription, byte[] encryptedCEK,
                          String keyWrappingAlgorithm, CipherLite cipherLite) {
        this.cipherLite = cipherLite;
        this.keyWrappingAlgorithm = keyWrappingAlgorithm;
        this.encryptedCEK = encryptedCEK.clone();
        this.kekMaterialsDescription = kekMaterialsDescription;
    }

    /**
     * Returns the key wrapping algorithm, or null if the content key is not secured via a key
     * wrapping algorithm.
     * <p>
     */
    String getKeyWrappingAlgorithm() {
        return keyWrappingAlgorithm;
    }

    /**
     * Returns the content crypto scheme.
     */
    ContentCryptoScheme getContentCryptoScheme() {
        return cipherLite.getContentCryptoScheme();
    }

    /**
     * Returns the given metadata updated with this content crypto material.
     */
    ObjectMetadata toObjectMetadata(ObjectMetadata metadata, CryptoMode mode) {
        return toObjectMetadata(metadata);
    }

    /**
     * Returns the metadata in the latest format.
     */
    private ObjectMetadata toObjectMetadata(ObjectMetadata metadata) {
        // If we generated a symmetric key to encrypt the data, store it in the
        // object metadata.
        byte[] encryptedCEK = getEncryptedCEK();
        metadata.addUserMetadata(Headers.CRYPTO_KEY_V2, Base64.encodeAsString(encryptedCEK));
        // Put the cipher initialization vector (IV) into the object metadata
        byte[] iv = cipherLite.getIV();
        metadata.addUserMetadata(Headers.CRYPTO_IV, Base64.encodeAsString(iv));
        // Put the materials description into the object metadata as JSON
        metadata.addUserMetadata(Headers.MATERIALS_DESCRIPTION, kekMaterialDescAsJson());
        // The CRYPTO_CEK_ALGORITHM, CRYPTO_TAG_LENGTH and
        // CRYPTO_KEYWRAP_ALGORITHM were not available in the Encryption Only
        // (EO) implementation
        ContentCryptoScheme scheme = getContentCryptoScheme();
        metadata.addUserMetadata(Headers.CRYPTO_CEK_ALGORITHM, scheme.getCipherAlgorithm());
        int tagLen = scheme.getTagLengthInBits();
        if (tagLen > 0)
            metadata.addUserMetadata(Headers.CRYPTO_TAG_LENGTH, String.valueOf(tagLen));
        String keyWrapAlgo = getKeyWrappingAlgorithm();
        if (keyWrapAlgo != null)
            metadata.addUserMetadata(Headers.CRYPTO_KEYWRAP_ALGORITHM, keyWrapAlgo);
        return metadata;
    }

    /**
     * Returns the json string in the latest format.
     */
    String toJsonString() {
        Map<String, String> map = new HashMap<String, String>();
        byte[] encryptedCEK = getEncryptedCEK();
        map.put(Headers.CRYPTO_KEY_V2, Base64.encodeAsString(encryptedCEK));
        byte[] iv = cipherLite.getIV();
        map.put(Headers.CRYPTO_IV, Base64.encodeAsString(iv));
        map.put(Headers.MATERIALS_DESCRIPTION, kekMaterialDescAsJson());
        ContentCryptoScheme scheme = getContentCryptoScheme();
        map.put(Headers.CRYPTO_CEK_ALGORITHM, scheme.getCipherAlgorithm());
        int tagLen = scheme.getTagLengthInBits();
        if (tagLen > 0)
            map.put(Headers.CRYPTO_TAG_LENGTH, String.valueOf(tagLen));
        String keyWrapAlgo = getKeyWrappingAlgorithm();
        if (keyWrapAlgo != null)
            map.put(Headers.CRYPTO_KEYWRAP_ALGORITHM, keyWrapAlgo);
        return Jackson.toJsonString(map);
    }

    /**
     * Returns the key-encrypting-key material description as a non-null json string;
     */
    private String kekMaterialDescAsJson() {
        Map<String, String> kekMaterialDesc = getKEKMaterialsDescription();
        if (kekMaterialDesc == null)
            kekMaterialDesc = Collections.emptyMap();
        return Jackson.toJsonString(kekMaterialDesc);
    }

    /**
     * Returns the corresponding kek material description from the given json; or null if the input
     * is null.
     */
    @SuppressWarnings("unchecked")
    private static Map<String, String> matdescFromJson(String json) {
        Map<String, String> map = Jackson.fromJsonString(json, Map.class);
        return map == null ? null : Collections.unmodifiableMap(map);
    }

    /**
     * Returns the content encrypting key unwrapped or decrypted. Note if KMS is used for key
     * protection, a remote call will be made to KMS to decrypt the ciphertext blob.
     *
     * @param cekSecured       the content encrypting key in wrapped or encrypted form; must not be null
     * @param keyWrapAlgo      key wrapping algorithm; or null if direct encryption instead of key
     *                         wrapping is used
     * @param materials        the client key encrypting key material for the content encrypting key
     * @param securityProvider security provider or null if the default security provider of the JCE
     *                         is used
     */
    private static SecretKey cek(byte[] cekSecured, String keyWrapAlgo,
                                 EncryptionMaterials materials, Provider securityProvider,
                                 ContentCryptoScheme contentCryptoScheme) {
        Key kek;
        if (materials.getKeyPair() != null) {
            // Do envelope decryption with private key from key pair
            kek = materials.getKeyPair().getPrivate();
            if (kek == null) {
                throw new ClientException("Key encrypting key not available");
            }
        } else {
            // Do envelope decryption with symmetric key
            kek = materials.getSymmetricKey();
            if (kek == null) {
                throw new ClientException("Key encrypting key not available");
            }
        }

        try {
            if (keyWrapAlgo != null) {
                // Key wrapping specified
                Cipher cipher = securityProvider == null ? Cipher.getInstance(keyWrapAlgo)
                        : Cipher.getInstance(keyWrapAlgo, securityProvider);
                cipher.init(Cipher.UNWRAP_MODE, kek);
                return (SecretKey) cipher.unwrap(cekSecured, keyWrapAlgo, Cipher.SECRET_KEY);
            }
            // fall back to the Encryption Only (EO) key decrypting method
            Cipher cipher;
            if (securityProvider != null) {
                cipher = Cipher.getInstance(kek.getAlgorithm(), securityProvider);
            } else {
                cipher = Cipher.getInstance(kek.getAlgorithm());
            }
            cipher.init(Cipher.DECRYPT_MODE, kek);
            byte[] decryptedSymmetricKeyBytes = cipher.doFinal(cekSecured);
            return new SecretKeySpec(decryptedSymmetricKeyBytes,
                    JceEncryptionConstants.SYMMETRIC_KEY_ALGORITHM);
        } catch (Exception e) {
            throw new ClientException("Unable to decrypt symmetric key from object metadata", e);
        }
    }

    /**
     * @return a non-null content crypto material.
     */
    static ContentCryptoMaterial fromObjectMetadata(ObjectMetadata metadata,
                                                    EncryptionMaterialsAccessor kekMaterialAccessor, Provider securityProvider,
                                                    boolean keyWrapExpected) {
        return fromObjectMetadata0(metadata, kekMaterialAccessor, securityProvider, null,
                keyWrapExpected);
    }

    /**
     * Factory method to return the content crypto material from the NOS object meta data, using the
     * specified key encrypting key material accessor and an optional security provider.
     *
     * @return a non-null content crypto material.
     */
    static ContentCryptoMaterial fromObjectMetadata(ObjectMetadata metadata,
                                                    EncryptionMaterialsAccessor kekMaterialAccessor, Provider securityProvider,
                                                    long[] range, boolean keyWrapExpected) {
        return fromObjectMetadata0(metadata, kekMaterialAccessor, securityProvider, range,
                keyWrapExpected);
    }

    /**
     * @return a non-null content crypto material.
     */
    private static ContentCryptoMaterial fromObjectMetadata0(ObjectMetadata metadata,
                                                             EncryptionMaterialsAccessor kekMaterialAccessor, Provider securityProvider,
                                                             long[] range, boolean keyWrapExpected) {
        // CEK and IV
        Map<String, String> userMeta = metadata.getUserMetadata();
        String b64key = userMeta.get(Headers.CRYPTO_KEY_V2);
        if (b64key == null) {
            b64key = userMeta.get(Headers.CRYPTO_KEY);
            if (b64key == null)
                throw new ClientException("Content encrypting key not found.");
        }
        byte[] cekWrapped = Base64.decode(b64key);
        byte[] iv = Base64.decode(userMeta.get(Headers.CRYPTO_IV));
        if (cekWrapped == null || iv == null) {
            throw new ClientException("Content encrypting key or IV not found.");
        }
        // Material description
        String matdescStr = userMeta.get(Headers.MATERIALS_DESCRIPTION);
        final String keyWrapAlgo = userMeta.get(Headers.CRYPTO_KEYWRAP_ALGORITHM);
        final Map<String, String> core = matdescFromJson(matdescStr);
        final EncryptionMaterials materials;

        materials = kekMaterialAccessor == null ? null
                : kekMaterialAccessor.getEncryptionMaterials(core);
        if (materials == null) {
            throw new ClientException("Unable to retrieve the client encryption materials");
        }

        // CEK algorithm
        String cekAlgo = userMeta.get(Headers.CRYPTO_CEK_ALGORITHM);
        boolean isRangeGet = range != null;
        // The content crypto scheme may vary depending on whether
        // it is a range get operation
        ContentCryptoScheme contentCryptoScheme =
                ContentCryptoScheme.fromCEKAlgo(cekAlgo, isRangeGet);
        if (isRangeGet) {
            // Adjust the IV as needed
            iv = contentCryptoScheme.adjustIV(iv, range[0]);
        } else {
            // Validate the tag length supported
            int tagLenExpected = contentCryptoScheme.getTagLengthInBits();
            if (tagLenExpected > 0) {
                String s = userMeta.get(Headers.CRYPTO_TAG_LENGTH);
                int tagLenActual = Integer.parseInt(s);
                if (tagLenExpected != tagLenActual) {
                    throw new ClientException("Unsupported tag length: " + tagLenActual
                            + ", expected: " + tagLenExpected);
                }
            }
        }
        // Unwrap or decrypt the CEK
        if (keyWrapExpected && keyWrapAlgo == null)
            throw newKeyWrapException();
        SecretKey cek =
                cek(cekWrapped, keyWrapAlgo, materials, securityProvider, contentCryptoScheme);
        return new ContentCryptoMaterial(core, cekWrapped, keyWrapAlgo, contentCryptoScheme
                .createCipherLite(cek, iv, Cipher.DECRYPT_MODE, securityProvider));
    }

    private static ClientException newKeyWrapException() {
        return new ClientException("Missing key-wrap for the content-encrypting-key");
    }

    /**
     * Converts the contents of an input stream to a String
     */
    private static String convertStreamToString(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            try {
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(inputStream, StringUtils.UTF8));
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } finally {
                inputStream.close();
            }
            return stringBuilder.toString();
        }
    }

    /**
     * Return the cipher lite used for content encryption/decryption purposes.
     */
    CipherLite getCipherLite() {
        return cipherLite;
    }

    /**
     * Returns the description of the kek materials that were used to encrypt the cek.
     */
    Map<String, String> getKEKMaterialsDescription() {
        return this.kekMaterialsDescription;
    }

    /**
     * Returns an array of bytes representing the encrypted envelope symmetric key.
     *
     * @return an array of bytes representing the encrypted envelope symmetric key.
     */
    byte[] getEncryptedCEK() {
        return this.encryptedCEK.clone();
    }

    /**
     * Recreates a new content crypto material from the current material given a new KEK
     * material-descriptions. The purpose is to re-encrypt the CEK under a different KEK.
     * <p>
     * Note network calls are involved if the CEK has been or is to be protected by KMS.
     *
     * @param newKEKMatDesc material descriptions for the new KEK; never null
     * @param accessor      used to retrieve the KEK given the corresponding material description
     * @param targetScheme  the target crypto scheme to be used for key wrapping, etc.
     * @param p             optional security provider; null means to use the default.
     * @throws SecurityException if the old and new material description are the same; or if the old
     *                           and new KEK are the same
     */
    ContentCryptoMaterial recreate(Map<String, String> newKEKMatDesc,
                                   EncryptionMaterialsAccessor accessor, NOSCryptoScheme targetScheme, Provider p, WebServiceRequest req) {
        if (newKEKMatDesc.equals(kekMaterialsDescription)) {
            throw new SecurityException(
                    "Material description of the new KEK must differ from the current one");
        }
        final EncryptionMaterials origKEK;

        origKEK = accessor.getEncryptionMaterials(kekMaterialsDescription);
        if (origKEK == null) {
            throw new ClientException("Unable to retrieve the origin encryption materials");
        }
        EncryptionMaterials newKEK = accessor.getEncryptionMaterials(newKEKMatDesc);
        if (newKEK == null) {
            throw new ClientException("No material available with the description "
                    + newKEKMatDesc + " from the encryption material provider");
        }
        SecretKey cek =
                cek(encryptedCEK, keyWrappingAlgorithm, origKEK, p, getContentCryptoScheme());
        ContentCryptoMaterial output =
                create(cek, cipherLite.getIV(), newKEK, getContentCryptoScheme(), // must use same
                        // content crypto
                        // scheme
                        targetScheme, p, req);
        if (Arrays.equals(output.encryptedCEK, encryptedCEK)) {
            throw new SecurityException("The new KEK must differ from the original");
        }
        return output;
    }

    /**
     * Recreates a new content crypto material from the current material given a new KEK encryption
     * materials. The purpose is to re-encrypt the CEK under the new KEK.
     * <p>
     * Note network calls are involved if the CEK has been or is to be protected by KMS.
     *
     * @param newKEK       encryption materials for the new KEK; must not be null
     * @param accessor     used to retrieve the original KEK given the corresponding material
     *                     description
     * @param targetScheme the target crypto scheme to use for recreating the content crypto
     *                     material
     * @param p            optional security provider; null means to use the default.
     * @throws SecurityException if the old and new material description are the same; or if the old
     *                           and new KEK are the same
     */
    ContentCryptoMaterial recreate(EncryptionMaterials newKEK, EncryptionMaterialsAccessor accessor,
                                   NOSCryptoScheme targetScheme, Provider p, WebServiceRequest req) {
        if (newKEK.getMaterialsDescription().equals(kekMaterialsDescription)) {
            throw new SecurityException(
                    "Material description of the new KEK must differ from the current one");
        }
        final EncryptionMaterials origKEK;

        origKEK = accessor.getEncryptionMaterials(kekMaterialsDescription);
        if (origKEK == null) {
            throw new ClientException("Unable to retrieve the origin encryption materials");
        }
        SecretKey cek =
                cek(encryptedCEK, keyWrappingAlgorithm, origKEK, p, getContentCryptoScheme());
        ContentCryptoMaterial output =
                create(cek, cipherLite.getIV(), newKEK, getContentCryptoScheme(), // must use same
                        // content crypto
                        // scheme
                        targetScheme, // target scheme used to recreate the content crypto material
                        p, req);
        if (Arrays.equals(output.encryptedCEK, encryptedCEK)) {
            throw new SecurityException("The new KEK must differ from the original");
        }
        return output;
    }

    /**
     * Returns a new instance of <code>ContentCryptoMaterial</code> for the input parameters using
     * the specified content crypto scheme, and the key wrapping and secure randomness specified of
     * the specified NOS crypto scheme.
     * <p>
     * Note network calls are involved if the CEK is to be protected by KMS.
     *
     * @param cek                 content encrypting key; must not be null.
     * @param iv                  initialization vector; must not be null.
     * @param contentCryptoScheme content crypto scheme to be used
     * @param targetScheme        the target NOS crypto scheme to be used for recreating the content crypto
     *                            material by providing the key wrapping scheme and mechanism for secure randomness
     * @param provider            optional security provider
     */
    static ContentCryptoMaterial create(SecretKey cek, byte[] iv, EncryptionMaterials kekMaterials,
                                        ContentCryptoScheme contentCryptoScheme, NOSCryptoScheme targetScheme,
                                        Provider provider, WebServiceRequest req) {
        return doCreate(cek, iv, kekMaterials, contentCryptoScheme, targetScheme, provider,
                req);
    }

    /**
     * Returns a new instance of <code>ContentCryptoMaterial</code> for the input parameters using
     * the specified NOS crypto scheme. Note network calls are involved if the CEK is to be
     * protected by KMS.
     *
     * @param cek          content encrypting key
     * @param iv           initialization vector
     * @param kekMaterials kek encryption material used to secure the CEK; can be KMS enabled.
     * @param scheme       NOS crypto scheme to be used for the content crypto material by providing the
     *                     content crypto scheme, key wrapping scheme and mechanism for secure randomness
     * @param provider     optional security provider
     * @param req          originating service request
     */
    static ContentCryptoMaterial create(SecretKey cek, byte[] iv, EncryptionMaterials kekMaterials,
                                        NOSCryptoScheme scheme, Provider provider, WebServiceRequest req) {
        return doCreate(cek, iv, kekMaterials, scheme.getContentCryptoScheme(), scheme, provider,
                req);
    }

    /**
     * Returns a new instance of <code>ContentCryptoMaterial</code> for the given input parameters
     * by using the specified content crypto scheme, and NOS crypto scheme.
     * <p>
     * Note network calls are involved if the CEK is to be protected by KMS.
     *
     * @param cek                   content encrypting key
     * @param iv                    initialization vector
     * @param kekMaterials          kek encryption material used to secure the CEK; can be KMS enabled.
     * @param contentCryptoScheme   content crypto scheme to be used, which can differ from the one of
     *                              <code>targetNOSCryptoScheme</code>
     * @param targetNOSCryptoScheme the target NOS crypto scheme to be used for providing the key
     *                              wrapping scheme and mechanism for secure randomness
     * @param provider              security provider
     * @param req                   the originating NOS service request
     */
    private static ContentCryptoMaterial doCreate(SecretKey cek, byte[] iv,
                                                  EncryptionMaterials kekMaterials, ContentCryptoScheme contentCryptoScheme,
                                                  NOSCryptoScheme targetNOSCryptoScheme, Provider provider,
                                                  WebServiceRequest req) {
        // Secure the envelope symmetric key either by encryption, key wrapping
        // or KMS.
        SecuredCEK cekSecured =
                secureCEK(cek, kekMaterials, targetNOSCryptoScheme.getKeyWrapScheme(),
                        targetNOSCryptoScheme.getSecureRandom(), provider, req);
        return wrap(cek, iv, contentCryptoScheme, provider, cekSecured);
    }

    /**
     * Returns a new instance of <code>ContentCryptoMaterial</code> by wrapping the input
     * parameters, including the already secured CEK. No network calls are involved.
     */
    public static ContentCryptoMaterial wrap(SecretKey cek, byte[] iv,
                                             ContentCryptoScheme contentCryptoScheme, Provider provider, SecuredCEK cekSecured) {
        return new ContentCryptoMaterial(cekSecured.getMaterialDescription(),
                cekSecured.getEncrypted(), cekSecured.getKeyWrapAlgorithm(),
                contentCryptoScheme.createCipherLite(cek, iv, Cipher.ENCRYPT_MODE, provider));
    }

    /**
     * Secure the given CEK. Note network calls are involved if the CEK is to be protected by KMS.
     *
     * @param cek       content encrypting key to be secured
     * @param materials used to provide the key-encryption-key (KEK); or if it is KMS-enabled, the
     *                  customer master key id and material description.
     * @param p         optional security provider; can be null if the default is used.
     * @return a secured CEK in the form of ciphertext or ciphertext blob.
     */
    private static SecuredCEK secureCEK(SecretKey cek, EncryptionMaterials materials,
                                        NOSKeyWrapScheme kwScheme, SecureRandom srand, Provider p,
                                        WebServiceRequest req) {
        final Map<String, String> matdesc;
        matdesc = materials.getMaterialsDescription();
        Key kek;
        if (materials.getKeyPair() != null) {
            // Do envelope encryption with public key from key pair
            kek = materials.getKeyPair().getPublic();
        } else {
            // Do envelope encryption with symmetric key
            kek = materials.getSymmetricKey();
        }
        String keyWrapAlgo = kwScheme.getKeyWrapAlgorithm(kek);
        try {
            Cipher cipher = p == null ? Cipher.getInstance(keyWrapAlgo)
                    : Cipher.getInstance(keyWrapAlgo, p);
            cipher.init(Cipher.WRAP_MODE, kek, srand);
            return new SecuredCEK(cipher.wrap(cek), keyWrapAlgo, matdesc);
        } catch (Exception e) {
            throw new ClientException("Unable to encrypt symmetric key", e);
        }
    }

    static Map<String, String> mergeMaterialDescriptions(EncryptionMaterials materials,
                                                         WebServiceRequest req) {
        Map<String, String> matdesc = materials.getMaterialsDescription();
        if (req instanceof MaterialsDescriptionProvider) {
            MaterialsDescriptionProvider mdp = (MaterialsDescriptionProvider) req;
            Map<String, String> matdesc_req = mdp.getMaterialsDescription();
            if (matdesc_req != null) {
                matdesc = new TreeMap<String, String>(matdesc);
                matdesc.putAll(matdesc_req); // request takes precedence
            }
        }
        return matdesc;
    }
}
