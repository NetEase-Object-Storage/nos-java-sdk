package com.netease.cloud;

import java.util.Map;

/**
 * Represents additional metadata included with a response . Response metadata
 * varies by service, but all services return an request ID that can be used in
 * the event a service call isn't working as expected and you need to work with
 * support to debug an issue.
 */
public class ResponseMetadata {
	public static final String REQUEST_ID = "REQUEST_ID";

	protected final Map<String, String> metadata;

	/**
	 * Creates a new ResponseMetadata object from a specified map of raw
	 * metadata information.
	 * 
	 * @param metadata
	 *            The raw metadata for the new ResponseMetadata object.
	 */
	public ResponseMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	/**
	 * Creates a new ResponseMetadata object from an existing ResponseMetadata
	 * object.
	 * 
	 * @param originalResponseMetadata
	 *            The ResponseMetadata object from which to create the new
	 *            object.
	 */
	public ResponseMetadata(ResponseMetadata originalResponseMetadata) {
		this(originalResponseMetadata.metadata);
	}

	/**
	 * Returns the request ID contained in this response metadata object. The
	 * request IDs can be used in the event a service call isn't working as
	 * expected and you need to work with The support to debug an issue.
	 * 
	 * @return The request ID contained in this response metadata object.
	 */
	public String getRequestId() {
		return metadata.get(REQUEST_ID);
	}

	@Override
	public String toString() {
		if (metadata == null)
			return "{}";
		return metadata.toString();
	}

}
