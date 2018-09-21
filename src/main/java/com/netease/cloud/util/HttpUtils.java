package com.netease.cloud.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.netease.cloud.Request;
import com.netease.cloud.http.HttpMethodName;

public class HttpUtils {

    private static final String DEFAULT_ENCODING = "UTF-8";

    public static String urlEncode(String value, boolean path) {
        if (value == null) return "";

        try {
            String encoded = URLEncoder.encode(value, DEFAULT_ENCODING)
                    .replace("+", "%20").replace("*", "%2A")
                    .replace("%7E", "~");
            if (path) {
                encoded = encoded.replace("%2F", "/");
            }

            return encoded;
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static String urlDecode(String value, boolean path) {
    	if (value == null) return "";
    	
    	try {
    		String decoded = URLDecoder.decode(value, DEFAULT_ENCODING)
    				.replace("%20", "+").replace("%2A", "*")
    				.replace("%7E", "~");
    		if (path) {
    			decoded = decoded.replace("%2F", "/");
    		}
    		
    		return decoded;
    	} catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Returns true if the specified URI is using a non-standard port (i.e. any
     * port other than 80 for HTTP URIs or any port other than 443 for HTTPS
     * URIs).
     *
     * @param uri
     *
     * @return True if the specified URI is using a non-standard port, otherwise
     *         false.
     */
    public static boolean isUsingNonDefaultPort(URI uri) {
        String scheme = uri.getScheme().toLowerCase();
        int port = uri.getPort();

        if (port <= 0) return false;
        if (scheme.equals("http") && port == 80) return false;
        if (scheme.equals("https") && port == 443) return false;

        return true;
    }

    public static boolean usePayloadForQueryParameters(Request<?> request) {
        boolean requestIsPOST = HttpMethodName.POST.equals(request.getHttpMethod());
        boolean requestHasNoPayload = (request.getContent() == null);

        return requestIsPOST && requestHasNoPayload;
    }

    /**
     * Creates an encoded query string from all the parameters in the specified
     * request.
     *
     * @param request
     *            The request containing the parameters to encode.
     *
     * @return Null if no parameters were present, otherwise the encoded query
     *         string for the parameters present in the specified request.
     */
    public static String encodeParameters(Request<?> request) {
        List<NameValuePair> nameValuePairs = null;
        if (request.getParameters().size() > 0) {
            nameValuePairs = new ArrayList<NameValuePair>(request.getParameters().size());
            for (Entry<String, String> entry : request.getParameters().entrySet()) {
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }

        String encodedParams = null;
        if (nameValuePairs != null) {
            encodedParams = URLEncodedUtils.format(nameValuePairs, DEFAULT_ENCODING);
        }

        return encodedParams;
    }

}
