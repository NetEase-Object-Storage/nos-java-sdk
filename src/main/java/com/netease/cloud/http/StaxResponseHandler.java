package com.netease.cloud.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netease.cloud.WebServiceResponse;
import com.netease.cloud.ResponseMetadata;
import com.netease.cloud.transform.StaxUnmarshallerContext;
import com.netease.cloud.transform.Unmarshaller;
import com.netease.cloud.transform.VoidStaxUnmarshaller;

/**
 * Default implementation of HttpResponseHandler that handles a successful
 * response from an  service and unmarshalls the result using a StAX
 * unmarshaller.
 *
 * @param <T>
 *            Indicates the type being unmarshalled by this response handler.
 */
public class StaxResponseHandler<T> implements HttpResponseHandler<WebServiceResponse<T>> {

    /** The StAX unmarshaller to use when handling the response */
    private Unmarshaller<T, StaxUnmarshallerContext> responseUnmarshaller;

    /** Shared logger for profiling information */
    private static final Log log = LogFactory.getLog("com.netease.cloud.request");

    /** Shared factory for creating XML event readers */
	private static XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();


    /**
     * Constructs a new response handler that will use the specified StAX
     * unmarshaller to unmarshall the service response and uses the specified
     * response element path to find the root of the business data in the
     * service's response.
     *
     * @param responseUnmarshaller
     *            The StAX unmarshaller to use on the response.
     */
    public StaxResponseHandler(Unmarshaller<T, StaxUnmarshallerContext> responseUnmarshaller) {
        this.responseUnmarshaller = responseUnmarshaller;

        /*
         * Even if the invoked operation just returns null, we still need an
         * unmarshaller to run so we can pull out response metadata.
         *
         * We might want to pass this in through the client class so that we
         * don't have to do this check here.
         */
        if (this.responseUnmarshaller == null) {
            this.responseUnmarshaller = new VoidStaxUnmarshaller<T>();
        }
    }


    /**
     * @see com.netease.cloud.http.HttpResponseHandler#handle(com.netease.cloud.http.HttpResponse)
     */
	public WebServiceResponse<T> handle(HttpResponse response) throws Exception {
        log.trace("Parsing service response XML");
        InputStream content = response.getContent();
        if (content == null) content = new ByteArrayInputStream("<eof/>".getBytes());
       
		XMLEventReader eventReader = xmlInputFactory.createXMLEventReader(content);
        try {
            WebServiceResponse<T> Response = new WebServiceResponse<T>();
            StaxUnmarshallerContext unmarshallerContext = new StaxUnmarshallerContext(eventReader, response.getHeaders());
            unmarshallerContext.registerMetadataExpression("ResponseMetadata/RequestId", 2, ResponseMetadata.REQUEST_ID);
            unmarshallerContext.registerMetadataExpression("requestId", 2, ResponseMetadata.REQUEST_ID);
            registerAdditionalMetadataExpressions(unmarshallerContext);

            T result = responseUnmarshaller.unmarshall(unmarshallerContext);
            Response.setResult(result);

            Map<String, String> metadata = unmarshallerContext.getMetadata();
            Response.setResponseMetadata(new ResponseMetadata(metadata));

            log.trace("Done parsing service response");
            return Response;
        } finally {
            try {eventReader.close();} catch (Exception e) {}
        }
    }

    /**
     * Hook for subclasses to override in order to collect additional metadata
     * from service responses.
     *
     * @param unmarshallerContext
     *            The unmarshaller context used to process a service's response
     *            data.
     */
    protected void registerAdditionalMetadataExpressions(StaxUnmarshallerContext unmarshallerContext) {}

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
