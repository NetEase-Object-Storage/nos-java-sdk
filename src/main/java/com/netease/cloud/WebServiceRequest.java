package com.netease.cloud;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.internal.Constants;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Base class for all user facing web service request objects.
 */
public abstract class WebServiceRequest {

	/**
	 * Arbitrary options storage for individual {@link WebServiceRequest} s.
	 * This field is not intended to be used by clients.
	 */
	private final RequestClientOptions requestClientOptions = new RequestClientOptions();

	/**
	 * The optional credentials to use for this request - overrides the default
	 * credentials set at the client level.
	 */
	private Credentials credentials;

	private String token;

	private Map<String, String> specialHeaders = null;

	/**
	 * Sets the optional credentials to use for this request, overriding the
	 * default credentials set at the client level.
	 *
	 * @param credentials
	 *            The optional security credentials to use for this request,
	 *            overriding the default credentials set at the client level.
	 */
	public void setRequestCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	/**
	 * Returns the optional credentials to use to sign this request, overriding
	 * the default credentials set at the client level.
	 *
	 * @return The optional credentials to use to sign this request, overriding
	 *         the default credentials set at the client level.
	 */
	public Credentials getRequestCredentials() {
		return credentials;
	}

	/**
	 * Internal only method for accessing private, internal request parameters.
	 * Not intended for direct use by callers.
	 *
	 * @return private, internal request parameter information.
	 */
	public Map<String, String> copyPrivateRequestParameters() {
		return specialHeaders;
	}

	public void addSpecialHeader(String key, String value) {
		if (specialHeaders == null) {
			specialHeaders = new HashMap<String, String>();
		}
		specialHeaders.put(key, value);
	}

	/**
	 * Gets the options stored with this request object. Intended for internal
	 * use only.
	 */
	public RequestClientOptions getRequestClientOptions() {
		return requestClientOptions;
	}

	/**
	 * internal module debugging log info
	 */
	protected String logID;
	protected String logSeq;
	private AtomicLong seqID = new AtomicLong(1);

	public String getLogID() {
		return logID;
	}

	public void setLogID(String logID) {
		this.logID = logID;
	}

	public String getLogSeq() {
		return logSeq;
	}

	public String getAndIncrementLogSeq() {
		if (logSeq == null || logSeq.length() == 0) {
			return Long.toString(seqID.getAndIncrement());
		} else {
			return logSeq + "." + seqID.getAndIncrement();
		}
	}

	public void setLogSeq(String logSeq) {
		this.logSeq = logSeq;
	}

	public boolean needSetLogInfo() {
		return logID != null || logSeq != null;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * SSE支持
	 * @param algorithm SSE算法
	 */
	public void setSSEAlgorithm(String algorithm){
		addSpecialHeader("x-nos-server-side-encryption",algorithm );
	}

	/**
	 * SSE的链式方法
	 */
	public <T> T withSSEAlgorithm(String algorithm){
		setSSEAlgorithm(algorithm);
		@SuppressWarnings("unchecked")
		T t=(T)this;
		return t;
	}

	/**
	 * SSEC接口，指定算法自动生成秘钥，目前只支持AES256
	 */
	public String setSSECRandomKey(String algorithm) throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator=null;
		if ("AES256".equals(algorithm)) {/* 生成 AES256 加密密钥 */
			keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(256, new SecureRandom());
		} else {
			return "";
		}
		SecretKey secretKey = keyGenerator.generateKey();
		/* 数据加密密钥 */
		String key = Base64.encodeBase64String(secretKey.getEncoded());
		setSSECKey(algorithm,key);
		return key;
	}

	/**
	 * SSEC支持
	 */
	public void setSSECKey(String algorithm,String key){
		addSpecialHeader(Constants.X_NOS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM, algorithm);
		String keyMd5 = Base64.encodeBase64String(DigestUtils.md5(Base64.decodeBase64(key)));
		addSpecialHeader(Constants.X_NOS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY, key);
		addSpecialHeader(Constants.X_NOS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD_5, keyMd5);
	}

	/**
	 * SSEC的链式方法
	 */
	public <T> T withSSECKey(String algorithm,String key){
		setSSECKey(algorithm,key);
		@SuppressWarnings("unchecked")
		T t=(T)this;
		return t;
	}

}
