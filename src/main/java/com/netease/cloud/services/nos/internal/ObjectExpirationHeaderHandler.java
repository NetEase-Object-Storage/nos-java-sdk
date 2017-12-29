package com.netease.cloud.services.nos.internal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.netease.cloud.http.HttpResponse;
import com.netease.cloud.services.nos.Headers;

/**
 * Header handler to pull the NOS_VERSION_ID header out of the response. This
 * header is required for the copyPart and copyObject api methods.
 */
public class ObjectExpirationHeaderHandler<T extends ObjectExpirationResult> implements HeaderHandler<T> {

	/*
	 * expiry-date="Sun, 11 Dec 2012 00:00:00 GMT", rule-id="baz rule"
	 */

	private static final Pattern datePattern = Pattern.compile("expiry-date=\"(.*?)\"");
	private static final Pattern rulePattern = Pattern.compile("rule-id=\"(.*)\"");

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.netease.cloud.services.nos.internal.HeaderHandler#handle(java.lang
	 * .Object, com.netease.cloud.http.HttpResponse)
	 */
	@Override
	public void handle(T result, HttpResponse response) {
		String expirationHeader = response.getHeaders().get(Headers.EXPIRATION);
		if (expirationHeader != null) {
			result.setExpirationTime(parseDate(expirationHeader));
			result.setExpirationTimeRuleId(parseRuleId(expirationHeader));
		}
	}

	private String parseRuleId(String expirationHeader) {
		Matcher matcher = rulePattern.matcher(expirationHeader);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	private Date parseDate(String expirationHeader) {
		Matcher matcher = datePattern.matcher(expirationHeader);
		if (matcher.find()) {
			String date = matcher.group(1);
			SimpleDateFormat fmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:MM:SS z");
			try {
				return fmt.parse(date);
			} catch (ParseException e) {
				return null;
			}
		}

		return null;
	}
}
