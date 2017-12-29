package com.netease.cloud.services.nos.internal;

import com.netease.cloud.http.HttpResponse;
import com.netease.cloud.services.nos.Headers;
import com.netease.cloud.services.nos.model.transform.XmlResponsesSaxParser.CopyObjectResultHandler;

/**
 * Header handler to pull the NOS_VERSION_ID header out of the response. This
 * header is required for the copyPart and copyObject api methods.
 */
public class NosVersionHeaderHandler implements HeaderHandler<CopyObjectResultHandler> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.netease.cloud.services.nos.internal.HeaderHandler#handle(java.lang.Object,
     * com.netease.cloud.http.HttpResponse)
     */
    @Override
    public void handle(CopyObjectResultHandler result, HttpResponse response) {
        result.setVersionId(response.getHeaders().get(Headers.NOS_VERSION_ID));
    }
}
