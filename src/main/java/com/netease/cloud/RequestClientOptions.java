package com.netease.cloud;

/**
 * Arbitrary options storage for individual {@link WebServiceRequest}s. This
 * class is not intended to be used by clients.
 */
public final class RequestClientOptions {

	private String clientMarker;

	/**
	 * Returns all client markers as a space-delimited string.
	 */
	public String getClientMarker() {
		return clientMarker;
	}

	/**
	 * Adds a client marker, if it wasn't already present.
	 */
	public void addClientMarker(String clientMarker) {
		if (this.clientMarker == null)
			this.clientMarker = "";

		this.clientMarker = createClientMarkerString(clientMarker);
	}

	/**
	 * Appends the given client marker string to the existing one and returns
	 * it.
	 */
	private String createClientMarkerString(String clientMarker) {
		if (this.clientMarker.contains(clientMarker)) {
			return this.clientMarker;
		} else {
			return this.clientMarker + " " + clientMarker;
		}
	}
}
