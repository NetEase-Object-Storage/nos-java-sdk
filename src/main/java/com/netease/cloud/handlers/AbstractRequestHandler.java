package com.netease.cloud.handlers;

import com.netease.cloud.Request;
import com.netease.cloud.util.TimingInfo;

/**
 * Simple implementation of RequestHandler to stub out required methods.
 */
public abstract class AbstractRequestHandler implements RequestHandler {
	public void beforeRequest(Request<?> request) {}
	public void afterResponse(Request<?> request, Object response, TimingInfo timingInfo) {}
	public void afterError(Request<?> request, Exception e) {}
}
