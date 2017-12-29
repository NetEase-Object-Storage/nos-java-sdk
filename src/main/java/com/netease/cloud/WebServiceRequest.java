package com.netease.cloud;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.netease.cloud.auth.Credentials;

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
	
}
