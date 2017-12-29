package com.netease.cloud.transform;

import java.lang.reflect.Constructor;

import org.w3c.dom.Node;

import com.netease.cloud.ServiceException;
import com.netease.cloud.ServiceException.ErrorType;
import com.netease.cloud.util.XpathUtils;

/**
 * Unmarshalls an error response into an ServiceException, or optionally, a
 * subclass of ServiceException if this class is extended.
 */
public class LegacyErrorUnmarshaller implements Unmarshaller<ServiceException, Node> {
	/**
	 * The type of ServiceException that will be instantiated. Subclasses
	 * specialized for a specific type of exception can control this through the
	 * protected constructor.
	 */
	private final Class<? extends ServiceException> exceptionClass;

	/**
	 * Constructs a new unmarshaller that will unmarshall error responses as a
	 * generic ServiceException object.
	 */
	public LegacyErrorUnmarshaller() {
		this(ServiceException.class);
	}

	/**
	 * Constructor allowing subclasses to specify a specific type of
	 * ServiceException to instantiating when populating the exception object
	 * with data from the error response.
	 * 
	 * @param exceptionClass
	 *            The class of ServiceException to create and populate when
	 *            unmarshalling the error response.
	 */
	protected LegacyErrorUnmarshaller(Class<? extends ServiceException> exceptionClass) {
		this.exceptionClass = exceptionClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netease.cloud.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	public ServiceException unmarshall(Node in) throws Exception {
		String errorCode = parseErrorCode(in);
		String message = XpathUtils.asString("Response/Errors/Error/Message", in);
		String requestId = XpathUtils.asString("Response/RequestID", in);
		String errorType = XpathUtils.asString("Response/Errors/Error/Type", in);

		Constructor<? extends ServiceException> constructor = exceptionClass.getConstructor(String.class);
		ServiceException ase = constructor.newInstance(message);
		ase.setErrorCode(errorCode);
		ase.setRequestId(requestId);

		if (errorType == null) {
			ase.setErrorType(ErrorType.Unknown);
		} else if (errorType.equalsIgnoreCase("server")) {
			ase.setErrorType(ErrorType.Service);
		} else if (errorType.equalsIgnoreCase("client")) {
			ase.setErrorType(ErrorType.Client);
		}

		return ase;
	}

	/**
	 * Returns the error code for the specified error response.
	 * 
	 * @param in
	 *            The DOM tree node containing the error response.
	 * 
	 * @return The error code contained in the specified error response.
	 * 
	 * @throws Exception
	 *             If any problems were encountered pulling out the error code.
	 */
	public String parseErrorCode(Node in) throws Exception {
		return XpathUtils.asString("Response/Errors/Error/Code", in);
	}

	/**
	 * Returns the path to the specified property within an error response.
	 * 
	 * @param property
	 *            The name of the desired property.
	 * 
	 * @return The path to the specified property within an error message.
	 */
	public String getErrorPropertyPath(String property) {
		return "Response/Errors/Error/" + property;
	}

}
