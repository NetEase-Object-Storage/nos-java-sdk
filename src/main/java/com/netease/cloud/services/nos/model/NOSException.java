package com.netease.cloud.services.nos.model;

import com.netease.cloud.ServiceException;

/**
 * Provides an extension of the ServiceException for errors reported by Nos
 * while processing a request. In particular, this class provides access to
 * Nos's extended request ID. This ID is required debugging information in the
 * case the user needs to contact about an issue where Nos is incorrectly
 * handling a request.
 */
public class NOSException extends ServiceException {
	private static final long serialVersionUID = 7573680383273658477L;

	/**
	 * Constructs a new {@link NOSException} with the specified message.
	 * 
	 * @param message
	 *            The error message describing why this exception was thrown.
	 * 
	 */
	public NOSException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link NOSException} with the specified message and root
	 * cause.
	 * 
	 * @param message
	 *            The error message describing why this exception was thrown.
	 * @param cause
	 *            The root exception that caused this exception to be thrown.
	 */
	public NOSException(String message, Exception cause) {
		super(message, cause);
	}

}
