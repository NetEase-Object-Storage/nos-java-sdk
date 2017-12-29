package com.netease.cloud.transform;

import org.w3c.dom.Node;

import com.netease.cloud.ServiceException;
import com.netease.cloud.ServiceException.ErrorType;
import com.netease.cloud.util.XpathUtils;

/**
 * Error unmarshaller that knows how to interpret a standard error message (i.e.
 * where to find the error code, the error message, etc.) and turn it into an
 * ServiceException.
 * 
 * @see LegacyErrorUnmarshaller
 */
public class StandardErrorUnmarshaller extends AbstractErrorUnmarshaller<Node> {

	/**
	 * Constructs a new unmarshaller that will unmarshall a standard error
	 * message as a generic ServiceException object.
	 */
	public StandardErrorUnmarshaller() {
	}

	/**
	 * Constructor allowing subclasses to specify a specific type of
	 * ServiceException to instantiating when populating the exception object
	 * with data from the error message.
	 * 
	 * @param exceptionClass
	 *            The class of ServiceException to create and populate when
	 *            unmarshalling the error message.
	 */
	protected StandardErrorUnmarshaller(Class<? extends ServiceException> exceptionClass) {
		super(exceptionClass);
	}

	/**
	 * @see com.netease.cloud.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	public ServiceException unmarshall(Node in) throws Exception {
		String errorCode = parseErrorCode(in);
		String errorType = XpathUtils.asString("ErrorResponse/Error/Type", in);
		String requestId = XpathUtils.asString("ErrorResponse/RequestId", in);
		String message = XpathUtils.asString("ErrorResponse/Error/Message", in);

		ServiceException ase = newException(message);
		ase.setErrorCode(errorCode);
		ase.setRequestId(requestId);

		if (errorType == null) {
			ase.setErrorType(ErrorType.Unknown);
		} else if (errorType.equalsIgnoreCase("Receiver")) {
			ase.setErrorType(ErrorType.Service);
		} else if (errorType.equalsIgnoreCase("Sender")) {
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
		return XpathUtils.asString("ErrorResponse/Error/Code", in);
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
		return "ErrorResponse/Error/" + property;
	}

}
