package com.netease.cloud.handlers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.netease.cloud.ClientException;

/**
 * Factory for creating request/response handler chains.
 */
public class HandlerChainFactory {

	/**
	 * Constructs a new request handler chain by analyzing the specified
	 * classpath resource.
	 * 
	 * @param resource
	 *            The resource to load from the classpath containing the list of
	 *            request handlers to instantiate.
	 * 
	 * @return A list of request handlers based on the handlers referenced in
	 *         the specified resource.
	 */
	public List<RequestHandler> newRequestHandlerChain(String resource) {
		List<RequestHandler> handlers = new ArrayList<RequestHandler>();

		try {
			InputStream input = getClass().getResourceAsStream(resource);
			if (input == null)
				return handlers;

			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			while (true) {
				String requestHandlerClassName = reader.readLine();
				if (requestHandlerClassName == null)
					break;
				requestHandlerClassName = requestHandlerClassName.trim();
				if (requestHandlerClassName.equals(""))
					continue;

				Class<?> requestHandlerClass = getClass().getClassLoader().loadClass(requestHandlerClassName);
				Object requestHandlerObject = requestHandlerClass.newInstance();
				if (requestHandlerObject instanceof RequestHandler) {
					handlers.add((RequestHandler) requestHandlerObject);
				} else {
					throw new ClientException("Unable to instantiate request handler chain for client.  "
							+ "Listed request handler ('" + requestHandlerClassName + "') "
							+ "does not implement the RequestHandler interface.");
				}
			}
		} catch (Exception e) {
			throw new ClientException("Unable to instantiate request handler chain for client: " + e.getMessage(), e);
		}

		return handlers;
	}
}
