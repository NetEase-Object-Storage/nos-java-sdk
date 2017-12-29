package com.netease.cloud.http;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.netease.cloud.ClientException;
import com.netease.cloud.ServiceException;
import com.netease.cloud.transform.Unmarshaller;
import com.netease.cloud.util.XpathUtils;

/**
 * Implementation of HttpResponseHandler that handles only error responses from
 * Web Services. A list of unmarshallers is passed into the constructor, and
 * while handling a response, each unmarshaller is tried, in order, until one is
 * found that can successfully unmarshall the error response. If no unmarshaller
 * is found that can unmarshall the error response, a generic ServiceException
 * is created and populated with the error response information (error message,
 * error code, request ID, etc).
 */
public class DefaultErrorResponseHandler implements HttpResponseHandler<ServiceException> {

	/**
	 * The list of error response unmarshallers to try to apply to error
	 * responses.
	 */
	private List<Unmarshaller<ServiceException, Node>> unmarshallerList;

	/**
	 * Constructs a new DefaultErrorResponseHandler that will handle error
	 * responses from services using the specified list of unmarshallers. Each
	 * unmarshaller will be tried, in order, until one is found that can
	 * unmarshall the error response.
	 * 
	 * @param unmarshallerList
	 *            The list of unmarshallers to try using when handling an error
	 *            response.
	 */
	public DefaultErrorResponseHandler(List<Unmarshaller<ServiceException, Node>> unmarshallerList) {
		this.unmarshallerList = unmarshallerList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netease.cloud.http.HttpResponseHandler#handle(com.netease.cloud.http
	 * .HttpResponse)
	 */
	public ServiceException handle(HttpResponse errorResponse) throws Exception {
		Document document = XpathUtils.documentFrom(errorResponse.getContent());

		/*
		 * We need to select which exception unmarshaller is the correct one to
		 * use from all the possible exceptions this operation can throw.
		 * Currently we rely on the unmarshallers to return null if they can't
		 * unmarshall the response, but we might need something a little more
		 * sophisticated in the future.
		 */
		for (Unmarshaller<ServiceException, Node> unmarshaller : unmarshallerList) {
			ServiceException ase = unmarshaller.unmarshall(document);
			if (ase != null) {
				ase.setStatusCode(errorResponse.getStatusCode());
				return ase;
			}
		}

		throw new ClientException("Unable to unmarshall error response from service");
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
