package com.netease.cloud.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;

import com.netease.cloud.ClientConfiguration;
import com.netease.cloud.ClientException;
import com.netease.cloud.Request;
import com.netease.cloud.ResponseMetadata;
import com.netease.cloud.ServiceException;
import com.netease.cloud.ServiceException.ErrorType;
import com.netease.cloud.WebServiceRequest;
import com.netease.cloud.WebServiceResponse;
import com.netease.cloud.handlers.RequestHandler;
import com.netease.cloud.internal.CustomBackoffStrategy;
import com.netease.cloud.util.ResponseMetadataCache;
import com.netease.cloud.util.TimingInfo;

public class NeteaseHttpClient {

	/**
	 * Logger providing detailed information on requests/responses.
	 */
	private static final Log requestLog = LogFactory.getLog("com.netease.cloud.request");

	/**
	 * Logger for more detailed debugging information, that might not be as
	 * useful for end users (ex: HTTP client configuration, etc).
	 */
	static final Log log = LogFactory.getLog(NeteaseHttpClient.class);

	/** Internal client for sending HTTP requests */
	private HttpClient httpClient;

	/** Maximum exponential back-off time before retrying a request */
	private static final int MAX_BACKOFF_IN_MILLISECONDS = 20 * 1000;

	/** Client configuration options, such as proxy settings, max retries, etc. */
	private final ClientConfiguration config;

	/** Cache of metadata for recently executed requests for diagnostic purposes */
	private ResponseMetadataCache responseMetadataCache = new ResponseMetadataCache(50);

	private static final Random random = new Random();

	private static HttpRequestFactory httpRequestFactory = new HttpRequestFactory();
	private static HttpClientFactory httpClientFactory = new HttpClientFactory();

	/**
	 * Constructs a new client using the specified client configuration options
	 * (ex: max retry attempts, proxy settings, etc).
	 * 
	 * @param clientConfiguration
	 *            Configuration options specifying how this client will
	 *            communicate .
	 */
	public NeteaseHttpClient(ClientConfiguration clientConfiguration) {
		this.config = clientConfiguration;
		this.httpClient = httpClientFactory.createHttpClient(config);
	}

	/**
	 * Returns additional response metadata for an executed request. Response
	 * metadata isn't considered part of the standard results returned by an
	 * operation, so it's accessed instead through this diagnostic interface.
	 * Response metadata is typically used for troubleshooting issues with
	 * support staff when services aren't acting as expected.
	 * 
	 * @param request
	 *            A previously executed WebServiceRequest object, whose
	 *            response metadata is desired.
	 * 
	 * @return The response metadata for the specified request, otherwise null
	 *         if there is no response metadata available for the request.
	 */
	public ResponseMetadata getResponseMetadataForRequest(WebServiceRequest request) {
		return responseMetadataCache.get(request);
	}

	/**
	 * Executes the request and returns the result.
	 * 
	 * @param request
	 *            The WebServices request to send to the remote server
	 * @param responseHandler
	 *            A response handler to accept a successful response from the
	 *            remote server
	 * @param errorResponseHandler
	 *            A response handler to accept an unsuccessful response from the
	 *            remote server
	 * @param executionContext
	 *            Additional information about the context of this web service
	 *            call
	 */
	public <T> T execute(Request<?> request, HttpResponseHandler<WebServiceResponse<T>> responseHandler,
			HttpResponseHandler<ServiceException> errorResponseHandler, ExecutionContext executionContext)
			throws ClientException, ServiceException {
		long startTime = System.currentTimeMillis();

		if (executionContext == null)
			throw new ClientException("Internal SDK Error: No execution context parameter specified.");
		List<RequestHandler> requestHandlers = executionContext.getRequestHandlers();
		if (requestHandlers == null)
			requestHandlers = new ArrayList<RequestHandler>();

		// Apply any additional service specific request handlers that need to
		// be run
		for (RequestHandler requestHandler : requestHandlers) {
			requestHandler.beforeRequest(request);
		}

		try {
			TimingInfo timingInfo = new TimingInfo(startTime);
			executionContext.setTimingInfo(timingInfo);
			T t = executeHelper(request, responseHandler, errorResponseHandler, executionContext);
			timingInfo.setEndTime(System.currentTimeMillis());

			for (RequestHandler handler : requestHandlers) {
				try {
					handler.afterResponse(request, t, timingInfo);
				} catch (ClassCastException cce) {
				}
			}
			return t;
		} catch (ClientException e) {
			for (RequestHandler handler : requestHandlers) {
				handler.afterError(request, e);
			}
			throw e;
		}
	}

	/**
	 * Internal method to execute the HTTP method given.
	 * 
	 * @see NeteaseHttpClient#execute(Request, HttpResponseHandler,
	 *      HttpResponseHandler)
	 * @see NeteaseHttpClient#execute(Request, HttpResponseHandler,
	 *      HttpResponseHandler, ExecutionContext)
	 */
	private <T extends Object> T executeHelper(Request<?> request,
			HttpResponseHandler<WebServiceResponse<T>> responseHandler,
			HttpResponseHandler<ServiceException> errorResponseHandler, ExecutionContext executionContext)
			throws ClientException, ServiceException {

		/*
		 * Depending on which response handler we end up choosing to handle the
		 * HTTP response, it might require us to leave the underlying HTTP
		 * connection open, depending on whether or not it reads the complete
		 * HTTP response stream from the HTTP connection, or if delays reading
		 * any of the content until after a response is returned to the caller.
		 */
		boolean leaveHttpConnectionOpen = false;

		if (requestLog.isDebugEnabled()) {
			requestLog.debug("Sending Request: " + request.toString());
		}

		// Apply whatever request options we know how to handle, such as
		// user-agent.
		applyRequestData(request);

		int retryCount = 0;
		URI redirectedURI = null;
		HttpEntity entity = null;
		ServiceException exception = null;

		// Make a copy of the original request params and headers so that we can
		// permute it in this loop and start over with the original every time.
		Map<String, String> originalParameters = new HashMap<String, String>();
		originalParameters.putAll(request.getParameters());
		Map<String, String> originalHeaders = new HashMap<String, String>();
		originalHeaders.putAll(request.getHeaders());

		while (true) {
			if (retryCount > 0) {
				request.setParameters(originalParameters);
				request.setHeaders(originalHeaders);
			}

			// Sign the request if a signer was provided
			if (executionContext.getSigner() != null && executionContext.getCredentials() != null) {
				executionContext.getSigner().sign(request, executionContext.getCredentials());
			} else if (executionContext.getToken() != null) {
				request.addHeader("x-nos-token", executionContext.getToken());
			}

			HttpRequestBase httpRequest = httpRequestFactory.createHttpRequest(request, config, entity,
					executionContext);

			if (httpRequest instanceof HttpEntityEnclosingRequest) {
				entity = ((HttpEntityEnclosingRequest) httpRequest).getEntity();
			}

			if (redirectedURI != null) {
				httpRequest.setURI(redirectedURI);
			}

			org.apache.http.HttpResponse response = null;
			try {
				if (retryCount > 0) {
					pauseExponentially(retryCount, exception, executionContext.getCustomBackoffStrategy());
					if (entity != null) {
						InputStream content = entity.getContent();
						if (content.markSupported()) {
							content.reset();
						}
					}
				}

				exception = null;
				retryCount++;

				response = httpClient.execute(httpRequest);

				if (isRequestSuccessful(response)) {
					/*
					 * If we get back any 2xx status code, then we know we
					 * should treat the service call as successful.
					 */
					leaveHttpConnectionOpen = responseHandler.needsConnectionLeftOpen();
					return handleResponse(request, responseHandler, httpRequest, response, executionContext);
				} else if (isTemporaryRedirect(response)) {
					Header[] locationHeaders = response.getHeaders("location");
					String redirectedLocation = locationHeaders[0].getValue();
					log.debug("Redirecting to: " + redirectedLocation);
					redirectedURI = URI.create(redirectedLocation);
					httpRequest.setURI(redirectedURI);
				} else {
					leaveHttpConnectionOpen = errorResponseHandler.needsConnectionLeftOpen();
					exception = handleErrorResponse(request, errorResponseHandler, httpRequest, response);

					if (!shouldRetry(httpRequest, exception, retryCount)) {
						throw exception;
					}
				}
			} catch (IOException ioe) {
				log.warn("Unable to execute HTTP request: " + ioe.getMessage(), ioe);

				if (!shouldRetry(httpRequest, ioe, retryCount)) {
					throw new ClientException("Unable to execute HTTP request: " + ioe.getMessage(), ioe);
				}
			} finally {
				/*
				 * Some response handlers need to manually manage the HTTP
				 * connection and will take care of releasing the connection on
				 * their own, but if this response handler doesn't need the
				 * connection left open, we go ahead and release the it to free
				 * up resources.
				 */
				if (!leaveHttpConnectionOpen) {
					try {
						response.getEntity().getContent().close();
					} catch (Throwable t) {
					}
				}
			}
		}
	}

	/**
	 * Applies any additional options set in the request.
	 */
	private void applyRequestData(Request<?> request) {
		if (request.getOriginalRequest() != null && request.getOriginalRequest().getRequestClientOptions() != null
				&& request.getOriginalRequest().getRequestClientOptions().getClientMarker() != null) {
			request.addHeader(
					"User-Agent",
					createUserAgentString(config.getUserAgent(), request.getOriginalRequest().getRequestClientOptions()
							.getClientMarker()));
		}
	}

	/**
	 * Appends the given user-agent string to the existing one and returns it.
	 */
	private static String createUserAgentString(String existingUserAgentString, String userAgent) {
		if (existingUserAgentString.contains(userAgent)) {
			return existingUserAgentString;
		} else {
			return existingUserAgentString + " " + userAgent;
		}
	}

	/**
	 * Shuts down this HTTP client object, releasing any resources that might be
	 * held open. This is an optional method, and callers are not expected to
	 * call it, but can if they want to explicitly release any open resources.
	 * Once a client has been shutdown, it cannot be used to make more requests.
	 */
	public void shutdown() {
		IdleConnectionReaper.removeConnectionManager(httpClient.getConnectionManager());
		httpClient.getConnectionManager().shutdown();
	}

	/**
	 * Returns true if a failed request should be retried.
	 * 
	 * @param method
	 *            The current HTTP method being executed.
	 * @param exception
	 *            The exception from the failed request.
	 * @param retries
	 *            The number of times the current request has been attempted.
	 * 
	 * @return True if the failed request should be retried.
	 */
	private boolean shouldRetry(HttpRequestBase method, Exception exception, int retries) {
		if (retries > config.getMaxErrorRetry())
			return false;

		if (method instanceof HttpEntityEnclosingRequest) {
			HttpEntity entity = ((HttpEntityEnclosingRequest) method).getEntity();
			if (entity != null && !entity.isRepeatable())
				return false;
		}

		if (exception instanceof NoHttpResponseException || exception instanceof SocketException
				|| exception instanceof SocketTimeoutException) {
			if (log.isDebugEnabled()) {
				log.debug("Retrying on " + exception.getClass().getName() + ": " + exception.getMessage());
			}
			return true;
		}

		if (exception instanceof ServiceException) {
			ServiceException ase = (ServiceException) exception;

			/*
			 * For 500 internal server errors and 503 service unavailable
			 * errors, we want to retry, but we need to use an exponential
			 * back-off strategy so that we don't overload a server with a flood
			 * of retries. If we've surpassed our retry limit we handle the
			 * error response as a non-retryable error and go ahead and throw it
			 * back to the user as an exception.
			 */
			if (ase.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR
					|| ase.getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE) {
				return true;
			}

			/*
			 * Throttling is reported as a 400 error from newer services. To try
			 * and smooth out an occasional throttling error, we'll pause and
			 * retry, hoping that the pause is long enough for the request to
			 * get through the next time.
			 */
			if (isThrottlingException(ase))
				return true;
		}

		return false;
	}

	private boolean isTemporaryRedirect(org.apache.http.HttpResponse response) {
		int status = response.getStatusLine().getStatusCode();
		return status == HttpStatus.SC_TEMPORARY_REDIRECT && response.getHeaders("Location") != null
				&& response.getHeaders("Location").length > 0;
	}

	private boolean isRequestSuccessful(org.apache.http.HttpResponse response) {
		int status = response.getStatusLine().getStatusCode();
		return status / 100 == HttpStatus.SC_OK / 100;
	}

	/**
	 * Handles a successful response from a service call by unmarshalling the
	 * results using the specified response handler.
	 * 
	 * @param <T>
	 *            The type of object expected in the response.
	 * 
	 * @param request
	 *            The original request that generated the response being
	 *            handled.
	 * @param responseHandler
	 *            The response unmarshaller used to interpret the contents of
	 *            the response.
	 * @param method
	 *            The HTTP method that was invoked, and contains the contents of
	 *            the response.
	 * @param executionContext
	 *            Extra state information about the request currently being
	 *            executed.
	 * @return The contents of the response, unmarshalled using the specified
	 *         response handler.
	 * 
	 * @throws IOException
	 *             If any problems were encountered reading the response
	 *             contents from the HTTP method object.
	 */
	private <T> T handleResponse(Request<?> request, HttpResponseHandler<WebServiceResponse<T>> responseHandler,
			HttpRequestBase method, org.apache.http.HttpResponse apacheHttpResponse, ExecutionContext executionContext)
			throws IOException {

		HttpResponse httpResponse = createResponse(method, request, apacheHttpResponse);
		if (responseHandler.needsConnectionLeftOpen() && method instanceof HttpEntityEnclosingRequest) {
			HttpEntityEnclosingRequest httpEntityEnclosingRequest = (HttpEntityEnclosingRequest) method;
			httpResponse.setContent(new HttpMethodReleaseInputStream(httpEntityEnclosingRequest));
		}

		try {

			WebServiceResponse<? extends T> Response = responseHandler.handle(httpResponse);
			if (Response == null)
				throw new RuntimeException("Unable to unmarshall response metadata");

			responseMetadataCache.add(request.getOriginalRequest(), Response.getResponseMetadata());

			if (requestLog.isDebugEnabled()) {
				requestLog.debug("Received successful response: " + apacheHttpResponse.getStatusLine().getStatusCode()
						+ ", Request ID: " + Response.getRequestId());
			}

			return Response.getResult();
		} catch (Exception e) {
			String errorMessage = "Unable to unmarshall response (" + e.getMessage() + ")";
			throw new ClientException(errorMessage, e);
		}
	}

	/**
	 * Responsible for handling an error response, including unmarshalling the
	 * error response into the most specific exception type possible, and
	 * throwing the exception.
	 * 
	 * @param request
	 *            The request that generated the error response being handled.
	 * @param errorResponseHandler
	 *            The response handler responsible for unmarshalling the error
	 *            response.
	 * @param method
	 *            The HTTP method containing the actual response content.
	 * 
	 * @throws IOException
	 *             If any problems are encountering reading the error response.
	 */
	private ServiceException handleErrorResponse(Request<?> request,
			HttpResponseHandler<ServiceException> errorResponseHandler, HttpRequestBase method,
			org.apache.http.HttpResponse apacheHttpResponse) throws IOException {

		int status = apacheHttpResponse.getStatusLine().getStatusCode();
		HttpResponse response = createResponse(method, request, apacheHttpResponse);
		if (errorResponseHandler.needsConnectionLeftOpen() && method instanceof HttpEntityEnclosingRequestBase) {
			HttpEntityEnclosingRequestBase entityEnclosingRequest = (HttpEntityEnclosingRequestBase) method;
			response.setContent(new HttpMethodReleaseInputStream(entityEnclosingRequest));
		}
		ServiceException exception = null;
		//clone input stream
		InputStream  oldInputStream = response.getContent();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (null != oldInputStream){
			byte[] buf = new byte[1024];
			int len;
			while ((len = oldInputStream.read(buf)) > -1){
				baos.write(buf, 0, len);	
			}
			baos.flush();		
		}
		
		InputStream copyInputStream1 = (oldInputStream == null ? null : new ByteArrayInputStream(baos.toByteArray()));
		InputStream copyInputStream2 = (oldInputStream == null ? null : new ByteArrayInputStream(baos.toByteArray()));
		
		try {
			response.setContent(copyInputStream1);
			exception = errorResponseHandler.handle(response);
			requestLog.debug("Received error response: " + exception.toString());
			response.setContent(oldInputStream);
		} catch (Exception e) {
			// If the errorResponseHandler doesn't work, then check for error
			// responses that don't have any content
			response.setContent(oldInputStream);
			if (status == 404) {
				exception = new ServiceException("Request object not exist");
				exception.setServiceName(request.getServiceName());
				exception.setStatusCode(404);
				exception.setErrorType(ErrorType.Client);
				exception.setErrorCode("Request object not exist");
			} else if (status == 413) {
				exception = new ServiceException("Request entity too large");
				exception.setServiceName(request.getServiceName());
				exception.setStatusCode(413);
				exception.setErrorType(ErrorType.Client);
				exception.setErrorCode("Request entity too large");
			} else if (status == 503
					&& "Service Unavailable".equalsIgnoreCase(apacheHttpResponse.getStatusLine().getReasonPhrase())) {
				exception = new ServiceException("Service unavailable");
				exception.setServiceName(request.getServiceName());
				exception.setStatusCode(503);
				exception.setErrorType(ErrorType.Service);
				exception.setErrorCode("Service unavailable");
			} else {
				StringBuilder errorMessage = new StringBuilder("Unable to unmarshall error response");
				
				errorMessage.append("code:");
				errorMessage.append(response.getStatusCode());
				errorMessage.append("; ");
				Map<String,String> headers = response.getHeaders();
				for(String key : headers.keySet()){
					errorMessage.append(key);
					errorMessage.append(":");
					errorMessage.append(headers.get(key));
					errorMessage.append("; ");
				}
				errorMessage.append("\n");
				errorMessage.append("body:");
				int by = 0;
				if (copyInputStream2 != null) {
					while ((by = copyInputStream2.read()) != -1) {
						errorMessage.append((char) by);
					}
				}
				if (copyInputStream1 != null) {
					copyInputStream1.close();
				}
				if (copyInputStream2 != null) {
					copyInputStream2.close();
				}
				throw new ClientException(errorMessage.toString(), e);
			}
		}
		if (copyInputStream1 != null) {
			copyInputStream1.close();
		}
		if (copyInputStream2 != null) {
			copyInputStream2.close();
		}
		
		exception.setStatusCode(status);
		exception.setServiceName(request.getServiceName());
		exception.fillInStackTrace();
		return exception;
	}

	/**
	 * Creates and initializes an HttpResponse object suitable to be passed to
	 * an HTTP response handler object.
	 * 
	 * @param method
	 *            The HTTP method that was invoked to get the response.
	 * @param request
	 *            The HTTP request associated with the response.
	 * 
	 * @return The new, initialized HttpResponse object ready to be passed to an
	 *         HTTP response handler object.
	 * 
	 * @throws IOException
	 *             If there were any problems getting any response information
	 *             from the HttpClient method object.
	 */
	private HttpResponse createResponse(HttpRequestBase method, Request<?> request,
			org.apache.http.HttpResponse apacheHttpResponse) throws IOException {
		HttpResponse httpResponse = new HttpResponse(request, method);

		if (apacheHttpResponse.getEntity() != null) {
			httpResponse.setContent(apacheHttpResponse.getEntity().getContent());
		}

		httpResponse.setStatusCode(apacheHttpResponse.getStatusLine().getStatusCode());
		httpResponse.setStatusText(apacheHttpResponse.getStatusLine().getReasonPhrase());
		for (Header header : apacheHttpResponse.getAllHeaders()) {
			httpResponse.addHeader(header.getName(), header.getValue());
		}

		return httpResponse;
	}

	/**
	 * Exponential sleep on failed request to avoid flooding a service with
	 * retries.
	 * 
	 * @param retries
	 *            Current retry count.
	 * @param previousException
	 *            Exception information for the previous attempt, if any.
	 */
	private void pauseExponentially(int retries, ServiceException previousException,
			CustomBackoffStrategy backoffStrategy) {
		long delay = 0;
		if (backoffStrategy != null) {
			delay = backoffStrategy.getBackoffPeriod(retries);
		} else {
			long scaleFactor = 300;
			if (isThrottlingException(previousException)) {
				scaleFactor = 500 + random.nextInt(100);
			}
			delay = (long) (Math.pow(2, retries) * scaleFactor);
		}

		delay = Math.min(delay, MAX_BACKOFF_IN_MILLISECONDS);
		if (log.isDebugEnabled()) {
			log.debug("Retriable error detected, " + "will retry in " + delay + "ms, attempt number: " + retries);
		}

		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			throw new ClientException(e.getMessage(), e);
		}
	}

	/**
	 * Returns true if the specified exception is a throttling error.
	 * 
	 * @param ase
	 *            The exception to test.
	 * 
	 * @return True if the exception resulted from a throttling error message
	 *         from a service, otherwise false.
	 */
	private boolean isThrottlingException(ServiceException ase) {
		if (ase == null)
			return false;
		return "Throttling".equals(ase.getErrorCode()) || "ThrottlingException".equals(ase.getErrorCode())
				|| "ProvisionedThroughputExceededException".equals(ase.getErrorCode());
	}

	@Override
	protected void finalize() throws Throwable {
		this.shutdown();
		super.finalize();
	}
}
