package com.netease.cloud.services.nos;

import java.util.Map;

import com.netease.cloud.ResponseMetadata;

/**
 * Extension of {@link ResponseMetadata} with nos specific data. In
 * addition to the standard  request ID contained in all services' response
 * metadata, nos also includes a host ID that can be provided to 
 * support when debugging an issue with NOS.
 */
public class NosResponseMetadata extends ResponseMetadata {
    public static final String HOST_ID = "HOST_ID";

    /**
     * Creates a new ResponseMetadata object from a specified map of metadata
     * information.
     *
     * @param metadata
     *            The raw metadata for the new ResponseMetadata object.
     */
    public NosResponseMetadata(Map<String, String> metadata) {
        super(metadata);
    }

    /**
     * Creates a new ResponseMetadata object from an existing ResponseMetadata
     * object.
     *
     * @param originalResponseMetadata
     *            The ResponseMetadata object from which to create the new
     *            object.
     */
    public NosResponseMetadata(ResponseMetadata originalResponseMetadata) {
        super(originalResponseMetadata);
    }

    /**
     * Returns the  host ID, providing additional debugging information
     * about how a request was handled. 
     * @return The host ID, providing additional debugging information
     *         about how a request was handled.
     */
    public String getHostId() {
        return metadata.get(HOST_ID);
    }

}
