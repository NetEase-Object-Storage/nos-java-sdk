package com.netease.cloud.services.nos.internal;

import java.util.Date;

import com.netease.cloud.ClientException;
import com.netease.cloud.Request;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.auth.AbstractSigner;
import com.netease.cloud.auth.SigningAlgorithm;

public class NosQueryStringSigner<T> extends AbstractSigner {

	/**
	 * The HTTP verb (GET, PUT, HEAD, DELETE) the request to sign is using.
	 * 
	 * TODO: We need to know the HTTP verb in order to create the authentication
	 * signature, but we don't have easy access to it through the request
	 * object.
	 * 
	 * Maybe it'd be better for the Nos signer (or all signers?) to work
	 * directly off of the HttpRequest instead of the Request object?
	 */
	private final String httpVerb;

	/**
	 * The canonical resource path portion of the Nos string to sign. Examples:
	 * "/", "/<bucket name>/", or "/<bucket name>/<key>"
	 * 
	 * TODO: We don't want to hold the resource path as member data in the Nos
	 * signer, but we need access to it and can't get it through the request
	 * yet.
	 */
	private final String resourcePath;

	private final Date expiration;

	public NosQueryStringSigner(String httpVerb, String resourcePath, Date expiration) {
		this.httpVerb = httpVerb;
		this.resourcePath = resourcePath;
		this.expiration = expiration;

		if (resourcePath == null)
			throw new IllegalArgumentException("Parameter resourcePath is empty");
	}

	public void sign(Request<?> request, Credentials credentials) throws ClientException {
		Credentials sanitizedCredentials = sanitizeCredentials(credentials);

		/** miao **/
		String expirationInSeconds = Long.toString(expiration.getTime() / 1000);

		String canonicalString = RestUtils.makeNosCanonicalString(httpVerb, resourcePath, request, expirationInSeconds);
		String signature = super.signAndBase64Encode(canonicalString, sanitizedCredentials.getSecretKey(),
				SigningAlgorithm.HmacSHA256);

		request.addParameter("NOSAccessKeyId", sanitizedCredentials.getAccessKeyId());
		request.addParameter("Expires", expirationInSeconds);
		request.addParameter("Signature", signature);
	}

}
