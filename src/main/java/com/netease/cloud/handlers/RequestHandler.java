package com.netease.cloud.handlers;

import com.netease.cloud.Request;
import com.netease.cloud.util.TimingInfo;

/**
 * Interface for addition request handling in clients. A request handler is
 * executed on a request object <b>before</b> it is sent to the client runtime
 * to be executed.
 */
public interface RequestHandler {

    /**
     * Runs any additional processing logic on the specified request (before it
     * is executed by the client runtime).
     *
     * @param request
     *            The low level request being processed.
     */
    public void beforeRequest(Request<?> request);

	/**
	 * Runs any additional processing logic on the specified request (after is
	 * has been executed by the client runtime).
	 *
	 * @param request
	 *            The low level request being processed.
	 * @param response
	 *            The response generated from the specified request.
	 * @param timingInfo
	 *            Timing information on the request's processing.
	 */
    public void afterResponse(Request<?> request, Object response, TimingInfo timingInfo);

	/**
	 * Runs any additional processing logic on a request after it has failed.
	 *
	 * @param request
	 *            The request that generated an error.
	 * @param e
	 *            The error that resulted from executing the request.
	 */
    public void afterError(Request<?> request, Exception e);

}
