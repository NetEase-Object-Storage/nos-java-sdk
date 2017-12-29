package com.netease.cloud.auth;

/**
 * Provides access to the credentials used for accessing services: access key ID
 * and secret access key. These credentials are used to securely sign requests
 * to services.
 * <p>
 * A basic implementation of this interface is provided in
 * {@link BasicCredentials}, but callers are free to provide their own
 * implementation, for example, to load credentials from an encrypted file.
 * <p>
 */
public interface Credentials {

	/**
	 * Returns the access key ID for this credentials object.
	 * 
	 * @return The access key ID for this credentials object.
	 */
	public String getAccessKeyId();

	/**
	 * Returns the secret access key for this credentials object.
	 * 
	 * @return The secret access key for this credentials object.
	 */
	public String getSecretKey();

}
