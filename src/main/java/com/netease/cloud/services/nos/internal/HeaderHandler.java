package com.netease.cloud.services.nos.internal;

import com.netease.cloud.http.HttpResponse;

/**
 * Assistant response handler that can pull an HTTP header out of the response
 * and apply it to a response object.
 */
public interface HeaderHandler<T> {

    /**
     * Applies one or more headers to the response object given.
     * 
     * @param result
     *            The response object to be returned to the client.
     * @param response
     *            The HTTP response from nos.
     */
    public void handle(T result, HttpResponse response);
}
