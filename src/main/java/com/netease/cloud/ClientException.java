package com.netease.cloud;

/**
 * Base exception class for any errors that occur while attempting to use an
 * client to make service calls to the Web Services.
 * 
 * Error responses from services will be handled as ServiceExceptions. This
 * class is primarily for errors that occur when unable to get a response from a
 * service, or when the client is unable to understand a response from a
 * service. For example, if a caller tries to use a client to make a service
 * call, but no network connection is present, an ClientException will be thrown
 * to indicate that the client wasn't able to successfully make the service
 * call, and no information from the service is available.
 * 
 * Callers should typically deal with exceptions through ServiceException, which
 * represent error responses returned by services. ServiceException has much
 * more information available for callers to appropriately deal with different
 * types of errors that can occur.
 * 
 * @see ServiceException
 */
public class ClientException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new ClientException with the specified message, and root cause.
	 * 
	 * @param message
	 *            An error message describing why this exception was thrown.
	 * @param t
	 *            The underlying cause of this exception.
	 */
	public ClientException(String message, Throwable t) {
		super(message, t);
	}

	/**
	 * Creates a new ClientException with the specified message.
	 * 
	 * @param message
	 *            An error message describing why this exception was thrown.
	 */
	public ClientException(String message) {
		super(message);
	}

}
