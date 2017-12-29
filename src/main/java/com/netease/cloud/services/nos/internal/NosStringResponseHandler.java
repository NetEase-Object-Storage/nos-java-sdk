package com.netease.cloud.services.nos.internal;

import java.io.InputStream;

import com.netease.cloud.WebServiceResponse;
import com.netease.cloud.http.HttpResponse;

/**
 * Custom Nos response handler for responses that simply contain text data that
 * doesn't need to be parsed as XML.
 */
public class NosStringResponseHandler extends AbstractNosResponseHandler<String> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netease.cloud.http.HttpResponseHandler#handle(com.netease.cloud.http
	 * .HttpResponse)
	 */
	public WebServiceResponse<String> handle(HttpResponse response) throws Exception {
		WebServiceResponse<String> Response = parseResponseMetadata(response);

		int bytesRead;
		byte[] buffer = new byte[1024];
		StringBuilder builder = new StringBuilder();
		InputStream content = response.getContent();
		while ((bytesRead = content.read(buffer)) > 0) {
			builder.append(new String(buffer, 0, bytesRead));
		}
		Response.setResult(builder.toString());

		return Response;
	}

}
