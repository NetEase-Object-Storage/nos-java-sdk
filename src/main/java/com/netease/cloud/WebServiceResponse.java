package com.netease.cloud;

/**
 * Represents the response from an service, including the result payload and any
 * response metadata. response metadata consists primarily of the request ID,
 * which can be used for debugging purposes when services aren't acting as
 * expected.
 * 
 * @param <T>
 *            The type of result contained by this response.
 */
public class WebServiceResponse<T> {

	/** The result contained by this response */
	private T result;

	/** Additional metadata for this response */
	private ResponseMetadata responseMetadata;

	/**
	 * Returns the result contained by this response.
	 * 
	 * @return The result contained by this response.
	 */
	public T getResult() {
		return result;
	}

	/**
	 * Sets the result contained by this response.
	 * 
	 * @param result
	 *            The result contained by this response.
	 */
	public void setResult(T result) {
		this.result = result;
	}

	/**
	 * Sets the response metadata associated with this response.
	 * 
	 * @param responseMetadata
	 *            The response metadata for this response.
	 */
	public void setResponseMetadata(ResponseMetadata responseMetadata) {
		this.responseMetadata = responseMetadata;
	}

	/**
	 * Returns the response metadata for this response. Response metadata
	 * provides additional information about a response that isn't necessarily
	 * directly part of the data the service is returning. Response metadata is
	 * primarily used for debugging issues with support when a service isn't
	 * working as expected.
	 * 
	 * @return The response metadata for this response.
	 */
	public ResponseMetadata getResponseMetadata() {
		return responseMetadata;
	}

	/**
	 * Returns the request ID from the response metadata section of an response.
	 * 
	 * @return The request ID from the response metadata section of an response.
	 */
	public String getRequestId() {
		if (responseMetadata == null)
			return null;
		return responseMetadata.getRequestId();
	}

}
