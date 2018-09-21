package com.netease.cloud.http;

import java.util.List;

import com.netease.cloud.auth.Credentials;
import com.netease.cloud.auth.Signer;
import com.netease.cloud.handlers.RequestHandler;
import com.netease.cloud.internal.CustomBackoffStrategy;
import com.netease.cloud.util.TimingInfo;

public class ExecutionContext {
	private List<RequestHandler> requestHandlers;
	private String contextUserAgent;
	private TimingInfo timingInfo;
	private CustomBackoffStrategy backoffStrategy;

	/** Optional signer to enable the runtime layer to handle signing requests (and resigning on retries). */
	private Signer signer;

	/** Optional credentials to enable the runtime layer to handle signing requests (and resigning on retries). */
	private Credentials credentials;
	
	/** Optional **/
	private String token;


    public String getContextUserAgent() {
        return contextUserAgent;
    }

    public void setContextUserAgent(String contextUserAgent) {
        this.contextUserAgent = contextUserAgent;
    }

    public ExecutionContext() {}

	public ExecutionContext(List<RequestHandler> requestHandlers) {
		this.requestHandlers = requestHandlers;
	}

	/**
	 * Returns a list of request handlers that should be run for a given
	 * request's execution.
	 *
	 * @return The list of request handlers to run for the current request.
	 */
	public List<RequestHandler> getRequestHandlers() {
		return requestHandlers;
	}

	public TimingInfo getTimingInfo() {
		return timingInfo;
	}

	public void setTimingInfo(TimingInfo timingInfo) {
		this.timingInfo = timingInfo;
	}

	/**
	 * Returns the optional signer used to sign the associated request.
	 *
	 * @return The optional signer used to sign the associated request.
	 */
	public Signer getSigner() {
		return signer;
	}

	/**
	 * Sets the optional signer used to sign the associated request. If no
	 * signer is specified as part of a request's ExecutionContext, then the
	 * runtime layer will not attempt to sign (or resign on retries) requests.
	 *
	 * @param signer
	 *            The optional signer used to sign the associated request.
	 */
	public void setSigner(Signer signer) {
		this.signer = signer;
	}

	/**
	 * Returns the optional credentials used to sign the associated request.
	 *
	 * @return The optional credentials used to sign the associated request.
	 */
	public Credentials getCredentials() {
		return credentials;
	}

	/**
	 * Sets the optional credentials used to sign the associated request. If no
	 * credentials are specified as part of a request's ExecutionContext, then
	 * the runtime layer will not attempt to sign (or resign on retries)
	 * requests.
	 *
	 * @param credentials
	 *            The optional credentials used to sign the associated request.
	 */
	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

    /**
     * Returns the optional custom backoff strategy for controlling how long
     * between retries on error responses. If no custom backoff strategy is
     * specified, a default exponential backoff strategy is used.
     *
     * @return the optional custom backoff strategy for the associated request.
     */
    public CustomBackoffStrategy getCustomBackoffStrategy() {
        return backoffStrategy;
    }

    /**
     * Sets the optional custom backoff strategy for controlling how long
     * between retries on error responses. If no custom backoff strategy is
     * specified, a default exponential backoff strategy is used.
     *
     * @param backoffStrategy
     *            The optional custom backoff strategy for the associated
     *            request.
     */
    public void setCustomBackoffStrategy(CustomBackoffStrategy backoffStrategy) {
        this.backoffStrategy = backoffStrategy;
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
    
}
