package com.netease.cloud.auth;

/**
 * Basic implementation of the Credentials interface that allows callers to pass
 * in the access key and secret access in the constructor.
 */
public class BasicCredentials implements Credentials {

	private final String accessKey;
	private final String secretKey;

	/**
	 * Constructs a new BasicCredentials object, with the specified access key
	 * and secret key.
	 * 
	 * @param accessKey
	 *            The access key.
	 * @param secretKey
	 *            The secret access key.
	 */
	public BasicCredentials(String accessKey, String secretKey) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.com.netease.cloud.auth.Credentials#getAccessKeyId()
	 */
	public String getAccessKeyId() {
		return accessKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netease.cloud.auth.Credentials#getSecretKey()
	 */
	public String getSecretKey() {
		return secretKey;
	}

}
