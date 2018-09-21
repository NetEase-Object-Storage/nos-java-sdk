package com.netease.cloud.internal.crypto;

import java.security.Key;

class NOSKeyWrapScheme {
    public static final String AESWrap = "AESWrap"; 
    public static final String RSA_ECB_OAEPWithSHA256AndMGF1Padding = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    /**
     * @param kek
     *            the key encrypting key, which is either an AES key or a public
     *            key
     */
    String getKeyWrapAlgorithm(Key kek) {
        String algorithm = kek.getAlgorithm();
        if (NOSCryptoScheme.AES.equals(algorithm)) {
            return AESWrap;
        }
        if (NOSCryptoScheme.RSA.equals(algorithm)) {
            if (CryptoRuntime.isRsaKeyWrapAvailable())
                return RSA_ECB_OAEPWithSHA256AndMGF1Padding;
        }
        throw new IllegalArgumentException("Unsupported key wrap algorithm " + algorithm);
    }

    @Override public String toString() { return "NOSKeyWrapScheme"; }
}