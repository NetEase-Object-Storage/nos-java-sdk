package com.netease.cloud.services.nos.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netease.cloud.auth.AbstractStringSigner;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.auth.SigningAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NosStringSigner extends AbstractStringSigner {
	
	/** Shared log for signing debug output */
	private static final Logger log = LoggerFactory.getLogger(NosStringSigner.class);
	
	@Override
	public String sign(String stringToSign, Credentials credentials) {
		if (credentials == null) {
			log.debug("Canonical string will not be signed, as no  Secret Key was provided");
			return null;
		}

		Credentials sanitizedCredentials = sanitizeCredentials(credentials);
		String signature = super.signAndBase64Encode(stringToSign, sanitizedCredentials.getSecretKey(),
				SigningAlgorithm.HmacSHA256);
		
		return signature;
	}

}
