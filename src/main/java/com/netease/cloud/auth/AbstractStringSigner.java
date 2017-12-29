package com.netease.cloud.auth;

import java.io.UnsupportedEncodingException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.netease.cloud.ClientException;

public abstract class AbstractStringSigner implements StringSigner {
	/** The default encoding to use when URL encoding */
	private static final String DEFAULT_ENCODING = "UTF-8";

	/**
	 * Computes an RFC 2104-compliant HMAC signature and returns the result as a
	 * Base64 encoded string.
	 */
	protected String signAndBase64Encode(String data, String key, SigningAlgorithm algorithm) throws ClientException {
		try {
			return signAndBase64Encode(data.getBytes(DEFAULT_ENCODING), key, algorithm);
		} catch (UnsupportedEncodingException e) {
			throw new ClientException("Unable to calculate a request signature: " + e.getMessage(), e);
		}
	}

	/**
	 * Computes an RFC 2104-compliant HMAC signature for an array of bytes and
	 * returns the result as a Base64 encoded string.
	 */
	protected String signAndBase64Encode(byte[] data, String key, SigningAlgorithm algorithm) throws ClientException {
		try {
			byte[] signature = sign(data, key.getBytes(DEFAULT_ENCODING), algorithm);
			return new String(Base64.encodeBase64(signature));
		} catch (Exception e) {
			throw new ClientException("Unable to calculate a request signature: " + e.getMessage(), e);
		}
	}

	protected byte[] sign(String stringData, byte[] key, SigningAlgorithm algorithm) throws ClientException {
		try {
			byte[] data = stringData.getBytes(DEFAULT_ENCODING);
			return sign(data, key, algorithm);
		} catch (Exception e) {
			throw new ClientException("Unable to calculate a request signature: " + e.getMessage(), e);
		}
	}

	protected byte[] sign(byte[] data, byte[] key, SigningAlgorithm algorithm) throws ClientException {
		try {
			Mac mac = Mac.getInstance(algorithm.toString());
			mac.init(new SecretKeySpec(key, algorithm.toString()));
			return mac.doFinal(data);
		} catch (Exception e) {
			throw new ClientException("Unable to calculate a request signature: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Loads the individual access key ID and secret key from the specified
	 * credentials, ensuring that access to the credentials is synchronized on
	 * the credentials object itself, and trimming any extra whitespace from the
	 * credentials.
	 * <p>
	 * Returns either a {@link BasicSessionCredentials} or a
	 * {@link BasicCredentials} object, depending on the input type.
	 * 
	 * @param credentials
	 * @return A new credentials object with the sanitized credentials.
	 */
	protected Credentials sanitizeCredentials(Credentials credentials) {
		String accessKeyId = null;
		String secretKey = null;
		synchronized (credentials) {
			accessKeyId = credentials.getAccessKeyId();
			secretKey = credentials.getSecretKey();

		}
		if (secretKey != null)
			secretKey = secretKey.trim();
		if (accessKeyId != null)
			accessKeyId = accessKeyId.trim();

		return new BasicCredentials(accessKeyId, secretKey);
	}
}
