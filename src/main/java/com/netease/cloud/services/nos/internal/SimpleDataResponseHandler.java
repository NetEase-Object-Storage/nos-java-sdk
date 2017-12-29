package com.netease.cloud.services.nos.internal;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.netease.cloud.WebServiceResponse;
import com.netease.cloud.http.HttpResponse;


public class SimpleDataResponseHandler  extends AbstractNosResponseHandler<InputStream>{
  	
    /** Shared logger for profiling information */
  private static final Log log = LogFactory.getLog("com.netease.cloud.request");

    /** Response headers from the processed response */
  private Map<String, String> responseHeaders;	


  public WebServiceResponse<InputStream> handle(HttpResponse response) throws Exception {
      WebServiceResponse<InputStream> Response = parseResponseMetadata(response);
      responseHeaders = response.getHeaders();
      log.trace("Beginning to parse service response XML");
      InputStream result = response.getContent();
      log.trace("Done parsing service response XML");
      Response.setResult(result);
      return Response;
  }

	/**
	 * The majority of Nos response handlers read the complete response while
	 * handling it, and don't need to manually manage the underlying HTTP
	 * connection.
	 * 
	 * @see com.netease.cloud.http.HttpResponseHandler#needsConnectionLeftOpen()
	 */
	public boolean needsConnectionLeftOpen() {
		return true;
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
