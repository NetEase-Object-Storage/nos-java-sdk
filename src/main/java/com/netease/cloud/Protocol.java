package com.netease.cloud;

/**
 * Represents the communication protocol to use when sending requests to AWS.
 * <p>
 */
public enum Protocol {

	/**
	 * HTTP Protocol -
	 */
	HTTP("http"),

	/**
	 * HTTPS Protocol - Using the HTTPS protocol is more secure than using the
	 * HTTP protocol.
	 */
	HTTPS("https");

	private final String protocol;

	private Protocol(String protocol) {
		this.protocol = protocol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return protocol;
	}
}
