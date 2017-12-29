package com.netease.cloud.services.nos.internal;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netease.cloud.ClientException;
import com.netease.cloud.Request;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.auth.AbstractSigner;
import com.netease.cloud.auth.Signer;
import com.netease.cloud.auth.SigningAlgorithm;
import com.netease.cloud.services.nos.Headers;

/**
 * Implementation of the {@linkplain Signer} interface specific to Nos's signing
 * algorithm.
 */
public class NosSigner extends AbstractSigner {

	/** Shared log for signing debug output */
	private static final Log log = LogFactory.getLog(NosSigner.class);

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
	 * The canonical resource path portion of the S3 string to sign. Examples:
	 * "/", "/<bucket name>/", or "/<bucket name>/<key>"
	 * 
	 * TODO: We don't want to hold the resource path as member data in the S3
	 * signer, but we need access to it and can't get it through the request
	 * yet.
	 */
	private final String resourcePath;

	/**
	 * Constructs a new S3Signer to sign requests based on the credentials, HTTP
	 * method and canonical S3 resource path.
	 * 
	 * @param httpVerb
	 *            The HTTP verb (GET, PUT, POST, HEAD, DELETE) the request is
	 *            using.
	 * @param resourcePath
	 *            The canonical Nos resource path (ex: "/", "/<bucket name>/",
	 *            or "/<bucket name>/<key>".
	 */
	public NosSigner(String httpVerb, String resourcePath) {
		this.httpVerb = httpVerb;
		this.resourcePath = resourcePath;

		if (resourcePath == null)
			throw new IllegalArgumentException("Parameter resourcePath is empty");
	}

	public void sign(Request<?> request, Credentials credentials) throws ClientException {
		if (credentials == null) {
			log.debug("Canonical string will not be signed, as no  Secret Key was provided");
			return;
		}

		Credentials sanitizedCredentials = sanitizeCredentials(credentials);

		request.addHeader(Headers.DATE, ServiceUtils.formatRfc822DateShangHai(new Date()));
		//request.addHeader(Headers.DATE, ServiceUtils.formatRfc822Date(new Date()));
		String canonicalString = RestUtils.makeNosCanonicalString(httpVerb, resourcePath, request, null);
		log.debug("Calculated string to sign:\n\"" + canonicalString + "\"");

		String signature = super.signAndBase64Encode(canonicalString, sanitizedCredentials.getSecretKey(),
				SigningAlgorithm.HmacSHA256);
		request.addHeader("Authorization", "NOS " + sanitizedCredentials.getAccessKeyId() + ":" + signature);
	}

}
