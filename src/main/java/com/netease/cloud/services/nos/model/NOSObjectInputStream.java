package com.netease.cloud.services.nos.model;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.EofSensorInputStream;

/**
 * Input stream representing the content of an {@link NOSObject}. In addition to
 * the methods supplied by the {@link InputStream} class,
 * {@link NOSObjectInputStream} supplies the abort() method, which will
 * terminate an HTTP connection to the Nos object.
 */
public class NOSObjectInputStream extends FilterInputStream {

	private final HttpRequestBase httpRequest;

	public NOSObjectInputStream(InputStream in, HttpRequestBase httpRequest) {
		super(in);
		this.httpRequest = httpRequest;
	}

	/**
	 * Aborts the underlying http request without reading any more data and
	 * closes the stream.
	 * <p>
	 * By default Apache {@link HttpClient} tries to reuse http connections by
	 * reading to the end of an attached input stream on
	 * {@link InputStream#close()}. This is efficient from a socket pool
	 * management perspective, but for objects with large payloads can incur
	 * significant overhead while bytes are read from Nos and discarded. It's up
	 * to clients to decide when to take the performance hit implicit in not
	 * reusing an http connection in order to not read unnecessary information
	 * from Nos.
	 * 
	 * @see EofSensorInputStream
	 */
	public void abort() throws IOException {
		getHttpRequest().abort();
		try {
			close();
		} catch (SocketException e) {
			// expected from some implementations because the stream is closed
		}
	}

	/**
	 * Returns the http request from which this input stream is derived.
	 */
	public HttpRequestBase getHttpRequest() {
		return httpRequest;
	}

}
