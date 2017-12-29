package com.netease.cloud.services.nos.internal;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netease.cloud.WebServiceResponse;
import com.netease.cloud.ResponseMetadata;
import com.netease.cloud.http.HttpResponse;
import com.netease.cloud.http.HttpResponseHandler;
import com.netease.cloud.services.nos.Headers;
import com.netease.cloud.services.nos.NosResponseMetadata;
import com.netease.cloud.services.nos.model.ObjectMetadata;

/**
 * Abstract HTTP response handler for nos responses. Provides common utilities
 * that other specialized nos response handlers need to share such as pulling
 * common response metadata (ex: request IDs) out of headers.
 * 
 * @param <T>
 *            The output type resulting from handling a response.
 */
public abstract class AbstractNosResponseHandler<T> implements HttpResponseHandler<WebServiceResponse<T>> {

	/** Shared logger */
	private static final Log log = LogFactory.getLog(NosMetadataResponseHandler.class);

	/** The set of response headers that aren't part of the object's metadata */
	private static final Set<String> ignoredHeaders;

	static {
		ignoredHeaders = new HashSet<String>();
		ignoredHeaders.add(Headers.DATE);
		ignoredHeaders.add(Headers.REQUEST_ID);
	}

	/**
	 * The majority of Nos response handlers read the complete response while
	 * handling it, and don't need to manually manage the underlying HTTP
	 * connection.
	 * 
	 * @see com.netease.cloud.http.HttpResponseHandler#needsConnectionLeftOpen()
	 */
	public boolean needsConnectionLeftOpen() {
		return false;
	}

	/**
	 * Parses the Nos response metadata (ex: request ID) from the specified
	 * response, and returns a WebServiceResponse<T> object ready for the result
	 * to be plugged in.
	 * 
	 * @param response
	 *            The response containing the response metadata to pull out.
	 * 
	 * @return A new, populated WebServiceResponse<T> object, ready for the
	 *         result to be plugged in.
	 */
	protected WebServiceResponse<T> parseResponseMetadata(HttpResponse response) {
		WebServiceResponse<T> Response = new WebServiceResponse<T>();
		String RequestId = response.getHeaders().get(Headers.REQUEST_ID);

		Map<String, String> metadataMap = new HashMap<String, String>();
		metadataMap.put(ResponseMetadata.REQUEST_ID, RequestId);
		Response.setResponseMetadata(new NosResponseMetadata(metadataMap));

		return Response;
	}

	/**
	 * Populates the specified NosObjectMetadata object with all object metadata
	 * pulled from the headers in the specified response.
	 * 
	 * @param response
	 *            The HTTP response containing the object metadata within the
	 *            headers.
	 * @param metadata
	 *            The metadata object to populate from the response's headers.
	 */
	protected void populateObjectMetadata(HttpResponse response, ObjectMetadata metadata) {
		for (Entry<String, String> header : response.getHeaders().entrySet()) {
			String key = header.getKey();
			if (key.startsWith(Headers.NOS_USER_METADATA_PREFIX)) {
				key = key.substring(Headers.NOS_USER_METADATA_PREFIX.length());
				metadata.addUserMetadata(key, header.getValue());
			} else if (ignoredHeaders.contains(key)) {
				// ignore...
			} else if (key.equals(Headers.LAST_MODIFIED)) {
				try {
					metadata.setHeader(key, ServiceUtils.parseRfc822Date(header.getValue()));
				} catch (ParseException pe) {
					log.warn("Unable to parse last modified date: " + header.getValue(), pe);
				}
			} else if (key.equals(Headers.CONTENT_LENGTH)) {
				try {
					metadata.setHeader(key, Long.parseLong(header.getValue()));
				} catch (NumberFormatException nfe) {
					log.warn("Unable to parse content length: " + header.getValue(), nfe);
				}
			} else if (key.equals(Headers.ETAG)) {
				metadata.setHeader(key, ServiceUtils.removeQuotes(header.getValue()));
			} else if (key.equals(Headers.EXPIRATION)) {
				new ObjectExpirationHeaderHandler<ObjectMetadata>().handle(metadata, response);
			} else {
				metadata.setHeader(key, header.getValue());
			}
		}
	}

}
