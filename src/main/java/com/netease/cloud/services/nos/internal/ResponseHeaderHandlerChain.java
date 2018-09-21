package com.netease.cloud.services.nos.internal;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import com.netease.cloud.WebServiceResponse;
import com.netease.cloud.http.HttpResponse;
import com.netease.cloud.transform.Unmarshaller;


/**
 * An XML response handler that can also process an arbitrary number of headers
 * in the response.
 */
public class ResponseHeaderHandlerChain <T> extends NosXmlResponseHandler<T> {

    private final List<HeaderHandler<T>> headerHandlers;
    
    public ResponseHeaderHandlerChain(Unmarshaller<T, InputStream> responseUnmarshaller, HeaderHandler<T>... headerHandlers) {
        super(responseUnmarshaller);
        this.headerHandlers = Arrays.asList(headerHandlers);
    }

    /* (non-Javadoc)
     * @see com.netease.cloud.services.netease.cloud.internal.XmlResponseHandler#handle(com.netease.cloud.http.HttpResponse)
     */
    @Override
    public WebServiceResponse<T> handle(HttpResponse response) throws Exception {
        WebServiceResponse<T> Response = super.handle(response);
        
        T result = Response.getResult();
        if (result != null) {
            for (HeaderHandler<T> handler : headerHandlers) {
                handler.handle(result, response);
            }
        }
        
        return Response;
    }
}
