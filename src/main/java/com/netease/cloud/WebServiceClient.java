package com.netease.cloud;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.netease.cloud.handlers.RequestHandler;
import com.netease.cloud.http.NeteaseHttpClient;
import com.netease.cloud.http.ExecutionContext;

/**
 * Abstract base class for Web Service Java clients.
 * <p>
 * Responsible for basic client capabilities that are the same across all SDK
 * Java clients (ex: setting the client endpoint).
 */
public abstract class WebServiceClient {

	/** The service endpoint to which this client will send requests. */
	protected URI endpoint;

	/** The client configuration */
	protected ClientConfiguration clientConfiguration;

	/** Low level client for sending requests to services. */
	protected NeteaseHttpClient client;

	/** Optional request handlers for additional request processing. */
	protected final List<RequestHandler> requestHandlers;

	/**
	 * Constructs a new WebServiceClient object using the specified
	 * configuration.
	 * 
	 * @param clientConfiguration
	 *            The client configuration for this client.
	 */
	public WebServiceClient(ClientConfiguration clientConfiguration) {
		this.clientConfiguration = clientConfiguration;
		client = new NeteaseHttpClient(clientConfiguration);
		requestHandlers = Collections.synchronizedList(new LinkedList<RequestHandler>());
	}

	/**
	 * Overrides the default endpoint for this client. Callers can use this
	 * method to control which region they want to work with.
	 * <p>
	 * <b>This method is not threadsafe. Endpoints should be configured when the
	 * client is created and before any service requests are made. Changing it
	 * afterwards creates inevitable race conditions for any service requests in
	 * transit.</b>
	 * 
	 * @param endpoint
	 *            The endpoint or a full URL, including the protocol
	 * @throws IllegalArgumentException
	 *             If any problems are detected with the specified endpoint.
	 */
	public void setEndpoint(String endpoint) throws IllegalArgumentException {
		/*
		 * If the endpoint doesn't explicitly specify a protocol to use, then
		 * we'll defer to the default protocol specified in the client
		 * configuration.
		 */
		if (endpoint.contains("://") == false) {
			endpoint = clientConfiguration.getProtocol().toString() + "://" + endpoint;
		}

		try {
			this.endpoint = new URI(endpoint);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void setConfiguration(ClientConfiguration clientConfiguration) {
		this.clientConfiguration = clientConfiguration;
		client = new NeteaseHttpClient(clientConfiguration);
	}

	/**
	 * Shuts down this client object, releasing any resources that might be held
	 * open. This is an optional method, and callers are not expected to call
	 * it, but can if they want to explicitly release any open resources. Once a
	 * client has been shutdown, it should not be used to make any more
	 * requests.
	 */
	public void shutdown() {
		client.shutdown();
	}

	/**
	 * Appends a request handler to the list of registered handlers that are run
	 * as part of a request's lifecycle.
	 * 
	 * @param requestHandler
	 *            The new handler to add to the current list of request
	 *            handlers.
	 */
	public void addRequestHandler(RequestHandler requestHandler) {
		requestHandlers.add(requestHandler);
	}

	/**
	 * Removes a request handler from the list of registered handlers that are
	 * run as part of a request's lifecycle.
	 * 
	 * @param requestHandler
	 *            The handler to remove from the current list of request
	 *            handlers.
	 */
	public void removeRequestHandler(RequestHandler requestHandler) {
		requestHandlers.remove(requestHandler);
	}

	protected ExecutionContext createExecutionContext() {
		ExecutionContext executionContext = new ExecutionContext(requestHandlers);
		return executionContext;
	}

}
