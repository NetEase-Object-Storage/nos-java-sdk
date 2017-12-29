package com.netease.cloud.services.nos.internal;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.netease.cloud.Request;
import com.netease.cloud.services.nos.Headers;

/**
 * Utilities useful for REST/HTTP NosService implementations.
 */
public class RestUtils {
	/**
	 * The set of request parameters which must be included in the canonical
	 * string to sign.
	 */
	private static final List<String> SIGNED_PARAMETERS = Arrays
			.asList(new String[] { "acl", "location", "versioning", "versions", "versionId", "uploadId", "uploads",
					"partNumber", "delete", "deduplication" });

	/**
	 * Calculate the canonical string for a REST/HTTP request to Nos.
	 * 
	 * When expires is non-null, it will be used instead of the Date header.
	 */
	public static <T> String makeNosCanonicalString(String method, String resource, Request<T> request, String expires) {
		StringBuilder buf = new StringBuilder();
		buf.append(method + "\n");

		// Add all interesting headers to a list, then sort them. "Interesting"
		// is defined as Content-MD5, Content-Type, Date, and x-nos-
		Map<String, String> headersMap = request.getHeaders();
		SortedMap<String, String> interestingHeaders = new TreeMap<String, String>();
		if (headersMap != null && headersMap.size() > 0) {
			Iterator<Map.Entry<String, String>> headerIter = headersMap.entrySet().iterator();
			while (headerIter.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) headerIter.next();
				String key = entry.getKey();
				String value = entry.getValue();

				if (key == null)
					continue;
				String lk = key.toString().toLowerCase(Locale.getDefault());

				// Ignore any headers that are not particularly interesting.
				if (lk.equals("content-type") || lk.equals("content-md5") || lk.equals("date")
						|| lk.startsWith(Headers.NOS_PREFIX)) {
					interestingHeaders.put(lk, value);
				}
			}
		}

		// Remove default date timestamp if "x-nos-date" is set.
		if (interestingHeaders.containsKey(Headers.NOS_ALTERNATE_DATE)) {
			interestingHeaders.put("date", "");
		}

		// Use the expires value as the timestamp if it is available. This
		// trumps both the default
		// "date" timestamp, and the "x-nos-date" header.
		if (expires != null) {
			interestingHeaders.put("date", expires);
		}

		// These headers require that we still put a new line in after them,
		// even if they don't exist.
		if (!interestingHeaders.containsKey("content-type")) {
			interestingHeaders.put("content-type", "");
		}
		if (!interestingHeaders.containsKey("content-md5")) {
			interestingHeaders.put("content-md5", "");
		}

		// Any parameters that are prefixed with "x-nos-" need to be included
		// in the headers section of the canonical string to sign
		for (Map.Entry<String, String> parameter : request.getParameters().entrySet()) {
			if (parameter.getKey().startsWith("x-nos-")) {
				interestingHeaders.put(parameter.getKey(), parameter.getValue());
			}
		}

		// Add all the interesting headers (i.e.: all that startwith x-nos- ;-))
		for (Iterator<Map.Entry<String, String>> i = interestingHeaders.entrySet().iterator(); i.hasNext();) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) i.next();
			String key = (String) entry.getKey();
			Object value = entry.getValue();

			if (key.startsWith(Headers.NOS_PREFIX)) {
				buf.append(key).append(':').append(value);
			} else {
				buf.append(value);
			}
			buf.append("\n");
		}

		// Add all the interesting parameters
		buf.append(resource);
		String[] parameterNames = request.getParameters().keySet().toArray(new String[request.getParameters().size()]);
		Arrays.sort(parameterNames);
		char separator = '?';
		for (String parameterName : parameterNames) {
			// Skip any parameters that aren't part of the canonical signed
			// string
			if (SIGNED_PARAMETERS.contains(parameterName) == false)
				continue;

			buf.append(separator);
			buf.append(parameterName);
			String parameterValue = request.getParameters().get(parameterName);
			if (parameterValue != null) {
				buf.append("=").append(parameterValue);
			}

			separator = '&';
		}

		return buf.toString();
	}

}
