package com.netease.cloud.transform;

import java.lang.reflect.Constructor;

import com.netease.cloud.ServiceException;

public abstract class AbstractErrorUnmarshaller<T> implements Unmarshaller<ServiceException, T> {

	/**
	 * The type of ServiceException that will be instantiated. Subclasses
	 * specialized for a specific type of exception can control this through the
	 * protected constructor.
	 */
	protected final Class<? extends ServiceException> exceptionClass;

	/**
	 * Constructs a new error unmarshaller that will unmarshall error responses
	 * into ServiceException objects.
	 */
	public AbstractErrorUnmarshaller() {
		this(ServiceException.class);
	}

	/**
	 * Constructs a new error unmarshaller that will unmarshall error responses
	 * into objects of the specified class, extending ServiceException.
	 * 
	 * @param exceptionClass
	 *            The subclass of ServiceException which will be instantiated
	 *            and populated by this class.
	 */
	public AbstractErrorUnmarshaller(Class<? extends ServiceException> exceptionClass) {
		this.exceptionClass = exceptionClass;
	}

	/**
	 * Constructs a new exception object of the type specified in this class's
	 * constructor and sets the specified error message.
	 * 
	 * @param message
	 *            The error message to set in the new exception object.
	 * 
	 * @return A new exception object of the type specified in this class's
	 *         constructor and sets the specified error message.
	 * 
	 * @throws Exception
	 *             If there are any problems using reflection to invoke the
	 *             exception class's constructor.
	 */
	protected ServiceException newException(String message) throws Exception {
		Constructor<? extends ServiceException> constructor = exceptionClass.getConstructor(String.class);
		return constructor.newInstance(message);
	}

}
