package com.netease.cloud.auth;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.netease.cloud.ClientException;
import com.netease.cloud.Request;
import com.netease.cloud.util.HttpUtils;
import com.netease.cloud.util.StringInputStream;

/**
 * Abstract base class for signing protocol implementations. Provides utilities
 * commonly needed by signing protocols such as computing canonicalized host
 * names, query string parameters, etc.
 * <p>
 * Not intended to be sub-classed by developers.
 */
public abstract class AbstractSigner implements Signer {

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
	 * Hashes the string contents (assumed to be UTF-8) using the SHA-256
	 * algorithm.
	 * 
	 * @param text
	 *            The string to hash.
	 * 
	 * @return The hashed bytes from the specified string.
	 * 
	 * @throws ClientException
	 *             If the hash cannot be computed.
	 */
	protected byte[] hash(String text) throws ClientException {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(text.getBytes(DEFAULT_ENCODING));
			return md.digest();
		} catch (Exception e) {
			throw new ClientException("Unable to compute hash while signing request: " + e.getMessage(), e);
		}
	}

	/**
	 * Examines the specified query string parameters and returns a
	 * canonicalized form.
	 * <p>
	 * The canonicalized query string is formed by first sorting all the query
	 * string parameters, then URI encoding both the key and value and then
	 * joining them, in order, separating key value pairs with an '&'.
	 * 
	 * @param parameters
	 *            The query string parameters to be canonicalized.
	 * 
	 * @return A canonicalized form for the specified query string parameters.
	 */
	protected String getCanonicalizedQueryString(Map<String, String> parameters) {
		SortedMap<String, String> sorted = new TreeMap<String, String>();
		sorted.putAll(parameters);

		StringBuilder builder = new StringBuilder();
		Iterator<Map.Entry<String, String>> pairs = sorted.entrySet().iterator();
		while (pairs.hasNext()) {
			Map.Entry<String, String> pair = pairs.next();
			String key = pair.getKey();
			String value = pair.getValue();
			builder.append(HttpUtils.urlEncode(key, false));
			builder.append("=");
			builder.append(HttpUtils.urlEncode(value, false));
			if (pairs.hasNext()) {
				builder.append("&");
			}
		}

		return builder.toString();
	}

	protected String getCanonicalizedQueryString(Request<?> request) {
		/*
		 * If we're using POST and we don't have any request payload content,
		 * then any request query parameters will be sent as the payload, and
		 * not in the actual query string.
		 */
		if (HttpUtils.usePayloadForQueryParameters(request))
			return "";
		else
			return this.getCanonicalizedQueryString(request.getParameters());
	}

	protected String getRequestPayload(Request<?> request) {
		if (HttpUtils.usePayloadForQueryParameters(request)) {
			String encodedParameters = HttpUtils.encodeParameters(request);
			if (encodedParameters == null)
				return "";
			return encodedParameters;
		}

		return getRequestPayloadWithoutQueryParams(request);
	}

	protected String getRequestPayloadWithoutQueryParams(Request<?> request) {
		try {
			InputStream content = request.getContent();
			if (content == null)
				return "";

			if (content instanceof StringInputStream) {
				return ((StringInputStream) content).getString();
			}

			if (!content.markSupported()) {
				throw new ClientException("Unable to read request payload to sign request.");
			}

			StringBuilder sb = new StringBuilder();
			content.mark(-1);
			int b;
			while ((b = content.read()) > -1) {
				sb.append((char) b);
			}
			content.reset();
			return sb.toString();
		} catch (Exception e) {
			throw new ClientException("Unable to read request payload to sign request: " + e.getMessage(), e);
		}
	}

	protected String getCanonicalizedResourcePath(String resourcePath) {
		if (resourcePath == null || resourcePath.length() == 0) {
			return "/";
		} else {
			return HttpUtils.urlEncode(resourcePath, true);
		}
	}

	protected String getCanonicalizedEndpoint(URI endpoint) {
		String endpointForStringToSign = endpoint.getHost().toLowerCase();
		/*
		 * Apache HttpClient will omit the port in the Host header for default
		 * port values (i.e. 80 for HTTP and 443 for HTTPS) even if we
		 * explicitly specify it, so we need to be careful that we use the same
		 * value here when we calculate the string to sign and in the Host
		 * header we send in the HTTP request.
		 */
		if (HttpUtils.isUsingNonDefaultPort(endpoint)) {
			endpointForStringToSign += ":" + endpoint.getPort();
		}

		return endpointForStringToSign;
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
