package com.netease.cloud.services.nos.internal;

import org.w3c.dom.Document;

import com.netease.cloud.ServiceException;
import com.netease.cloud.ServiceException.ErrorType;
import com.netease.cloud.http.HttpResponse;
import com.netease.cloud.http.HttpResponseHandler;
import com.netease.cloud.services.nos.Headers;
import com.netease.cloud.services.nos.model.NOSException;
import com.netease.cloud.util.XpathUtils;

/**
 * Response handler for Nos error responses. Nos error responses are different
 * from other error responses in a few ways. Most error responses will contain
 * an XML body, but not all (ex: error responses to HEAD requests will not), so
 * this error handler has to account for that. The actual XML error response
 * body is slightly different than other services like SimpleDB or EC2 and some
 * information isn't explicitly represented in the XML error response body (ex:
 * error type/fault information) so it has to be inferred from other parts of
 * the error response.
 */
public class NosErrorResponseHandler implements HttpResponseHandler<ServiceException> {

	/**
	 * @see com.netease.cloud.http.HttpResponseHandler#handle(com.netease.cloud.http.HttpResponse)
	 */
	public ServiceException handle(HttpResponse errorResponse) throws Exception {
		/*
		 * We don't always get an error response body back from Nos. When we
		 * send a HEAD request, we don't receive a body, so we'll have to just
		 * return what we can.
		 */
		if (errorResponse.getContent() == null) {
			String requestId = errorResponse.getHeaders().get(Headers.REQUEST_ID);
			NOSException ase = new NOSException(errorResponse.getStatusText());
			ase.setStatusCode(errorResponse.getStatusCode());
			ase.setRequestId(requestId);
			fillInErrorType(ase, errorResponse);
			return ase;
		}

		Document document = XpathUtils.documentFrom(errorResponse.getContent());
		String message = XpathUtils.asString("Error/Message", document);
		String errorCode = XpathUtils.asString("Error/Code", document);
		String requestId = XpathUtils.asString("Error/RequestId", document);
		String resource = XpathUtils.asString("Error/Resource", document);

		NOSException ase = new NOSException(message);
		ase.setStatusCode(errorResponse.getStatusCode());
		ase.setErrorCode(errorCode);
		ase.setRequestId(requestId);
		ase.setResource(resource);
		fillInErrorType(ase, errorResponse);

		return ase;
	}

	/**
	 * Fills in the error type information in the specified ServiceException by
	 * looking at the HTTP status code in the error response. Nos error
	 * responses don't explicitly declare a sender or client fault like other
	 * services, so we have to use the HTTP status code to infer this
	 * information.
	 * 
	 * @param ase
	 *            The ServiceException to populate with error type information.
	 * @param errorResponse
	 *            The HTTP error response to use to determine the right error
	 *            type to set.
	 */
	private void fillInErrorType(ServiceException ase, HttpResponse errorResponse) {
		if (errorResponse.getStatusCode() >= 500) {
			ase.setErrorType(ErrorType.Service);
		} else {
			ase.setErrorType(ErrorType.Client);
		}
	}

	/**
	 * Since this response handler completely consumes all the data from the
	 * underlying HTTP connection during the handle method, we don't need to
	 * keep the HTTP connection open.
	 * 
	 * @see com.netease.cloud.http.HttpResponseHandler#needsConnectionLeftOpen()
	 */
	public boolean needsConnectionLeftOpen() {
		return false;
	}

}
