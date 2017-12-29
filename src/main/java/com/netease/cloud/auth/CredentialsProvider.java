package com.netease.cloud.auth;

/**
 * Interface for providing credentials. Implementations are free to use any
 * strategy for providing credentials, such as simply providing static
 * credentials that don't change, or more complicated implementations, such as
 * integrating with existing key management systems.
 */
public interface CredentialsProvider {

	/**
	 * Returns Credentials which the caller can use to authorize an request.
	 * Each implementation of CredentialsProvider can chose its own strategy for
	 * loading credentials. For example, an implementation might load
	 * credentials from an existing key management system, or load new
	 * credentials when credentials are rotated.
	 * 
	 * @return Credentials which the caller can use to authorize an request.
	 */
	public Credentials getCredentials();

	/**
	 * Forces this credentials provider to refresh its credentials. For many
	 * implementations of credentials provider, this method may simply be a
	 * no-op, such as any credentials provider implementation that vends
	 * static/non-changing credentials. For other implementations that vend
	 * different credentials through out their lifetime, this method should
	 * force the credentials provider to refresh its credentials.
	 */
	public void refresh();

}
