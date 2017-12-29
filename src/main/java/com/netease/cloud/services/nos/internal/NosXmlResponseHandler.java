package com.netease.cloud.services.nos.internal;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netease.cloud.WebServiceResponse;
import com.netease.cloud.http.HttpResponse;
import com.netease.cloud.transform.Unmarshaller;

/**
 * Nos Implementation of HttpResponseHandler. Relies on a SAX unmarshaller for
 * handling the response.
 */
public class NosXmlResponseHandler<T> extends AbstractNosResponseHandler<T> {

    /** The SAX unmarshaller to use when handling the response from Nos */
    private Unmarshaller<T, InputStream> responseUnmarshaller;

    /** Shared logger for profiling information */
    private static final Log log = LogFactory.getLog("com.netease.cloud.request");

    /** Response headers from the processed response */
    private Map<String, String> responseHeaders;

    /**
     * Constructs a new Nos response handler that will use the specified SAX
     * unmarshaller to turn the response into an object.
     *
     * @param responseUnmarshaller
     *            The SAX unmarshaller to use on the response from Nos.
     */
    public NosXmlResponseHandler(Unmarshaller<T, InputStream> responseUnmarshaller) {
        this.responseUnmarshaller = responseUnmarshaller;
    }

    /**
     * @see com.netease.cloud.http.HttpResponseHandler#handle(com.netease.cloud.http.HttpResponse)
     */
    public WebServiceResponse<T> handle(HttpResponse response) throws Exception {
        WebServiceResponse<T> Response = parseResponseMetadata(response);
        responseHeaders = response.getHeaders();

        if (responseUnmarshaller != null) {
            log.trace("Beginning to parse service response XML");
            T result = responseUnmarshaller.unmarshall(response.getContent());
            log.trace("Done parsing service response XML");
            Response.setResult(result);
        }

        return Response;
    }

    /**
     * Returns the headers from the processed response. Will return null until a
     * response has been handled.
     *
     * @return the headers from the processed response.
     */
    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

}
