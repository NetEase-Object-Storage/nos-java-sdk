package com.netease.cloud.services.nos.internal;

import com.netease.cloud.WebServiceResponse;
import com.netease.cloud.http.HttpResponse;
import com.netease.cloud.services.nos.model.ObjectMetadata;

/**
 * Nos response handler that knows how to pull Nos object metadata out of a
 * response and unmarshall it into an  ObjectMetadata object.
 */
public class NosMetadataResponseHandler extends AbstractNosResponseHandler<ObjectMetadata> {

    /**
     * @see com.netease.cloud.http.HttpResponseHandler#handle(com.netease.cloud.http.HttpResponse)
     */
    public WebServiceResponse<ObjectMetadata> handle(HttpResponse response) throws Exception {
        ObjectMetadata metadata = new ObjectMetadata();
        populateObjectMetadata(response, metadata);

        WebServiceResponse<ObjectMetadata> nosResponse = parseResponseMetadata(response);
        nosResponse.setResult(metadata);
        return nosResponse;
    }

}
