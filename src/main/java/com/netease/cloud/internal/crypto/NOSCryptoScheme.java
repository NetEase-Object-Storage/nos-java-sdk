package com.netease.cloud.internal.crypto;

import java.security.SecureRandom;

/**
 * NOS cryptographic scheme that includes the content crypto scheme and key
 * wrapping scheme (for the content-encrypting-key).
 */
final class NOSCryptoScheme {
    static final String AES = "AES"; 
    static final String RSA = "RSA"; 
    private static final SecureRandom srand = new SecureRandom();
    //NOS密码包
    private final NOSKeyWrapScheme kwScheme;

    //内容加密方案
    private final ContentCryptoScheme contentCryptoScheme;

    private NOSCryptoScheme(ContentCryptoScheme contentCryptoScheme,
                            NOSKeyWrapScheme kwScheme) {
        this.contentCryptoScheme = contentCryptoScheme;
        this.kwScheme = kwScheme;
    }

    SecureRandom getSecureRandom() { return srand; }
    
    ContentCryptoScheme getContentCryptoScheme() {
        return contentCryptoScheme;
    }

    NOSKeyWrapScheme getKeyWrapScheme() { return kwScheme; }

    /**
     * Convenient method.
     */
    static boolean isAesGcm(String cipherAlgorithm) {
        return ContentCryptoScheme.AES_GCM.getCipherAlgorithm().equals(cipherAlgorithm);
    }

    static NOSCryptoScheme from(CryptoMode mode) {
        switch (mode) {
        case AuthenticatedEncryption:
        case StrictAuthenticatedEncryption:
            //返回加密方案
            return new NOSCryptoScheme(ContentCryptoScheme.AES_GCM,
                    new NOSKeyWrapScheme());
        default:
            throw new IllegalStateException();
        }
    }
}