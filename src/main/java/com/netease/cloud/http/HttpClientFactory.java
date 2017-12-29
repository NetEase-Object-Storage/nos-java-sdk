package com.netease.cloud.http;


import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.netease.cloud.ClientConfiguration;

/** Responsible for creating and configuring instances of Apache HttpClient4. */
class HttpClientFactory {

    /**
	 * Creates a new HttpClient object using the specified 
	 * ClientConfiguration to configure the client.
	 *
	 * @param config
	 *            Client configuration options (ex: proxy settings, connection
	 *            limits, etc).
	 *
	 * @return The new, configured HttpClient.
	 */
	public HttpClient createHttpClient(ClientConfiguration config) {
        /* Form User-Agent information */
        String userAgent = config.getUserAgent();
        if (!(userAgent.equals(ClientConfiguration.DEFAULT_USER_AGENT))) {
            userAgent += ", " + ClientConfiguration.DEFAULT_USER_AGENT;
        }

        /* Set HTTP client parameters */
        HttpParams httpClientParams = new BasicHttpParams();
        HttpProtocolParams.setUserAgent(httpClientParams, userAgent);
        HttpConnectionParams.setConnectionTimeout(httpClientParams, config.getConnectionTimeout());
        HttpConnectionParams.setSoTimeout(httpClientParams, config.getSocketTimeout());
        HttpConnectionParams.setStaleCheckingEnabled(httpClientParams, true);
        HttpConnectionParams.setTcpNoDelay(httpClientParams, true);

        int socketSendBufferSizeHint = config.getSocketBufferSizeHints()[0];
        int socketReceiveBufferSizeHint = config.getSocketBufferSizeHints()[1];
        if (socketSendBufferSizeHint > 0 || socketReceiveBufferSizeHint > 0) {
        	HttpConnectionParams.setSocketBufferSize(httpClientParams,
        			Math.max(socketSendBufferSizeHint, socketReceiveBufferSizeHint));
        }

        /* Set connection manager */
        ThreadSafeClientConnManager connectionManager = ConnectionManagerFactory.createThreadSafeClientConnManager( config, httpClientParams );
        DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager, httpClientParams);


        /* Set proxy if configured */
        String proxyHost = config.getProxyHost();
        int proxyPort = config.getProxyPort();
        if (proxyHost != null && proxyPort > 0) {
            NeteaseHttpClient.log.info("Configuring Proxy. Proxy Host: " + proxyHost + " " + "Proxy Port: " + proxyPort);
            HttpHost proxyHttpHost = new HttpHost(proxyHost, proxyPort);
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHttpHost);

            String proxyUsername    = config.getProxyUsername();
            String proxyPassword    = config.getProxyPassword();
            String proxyDomain      = config.getProxyDomain();
            String proxyWorkstation = config.getProxyWorkstation();

            if (proxyUsername != null && proxyPassword != null) {
        		httpClient.getCredentialsProvider().setCredentials(
        				new AuthScope(proxyHost, proxyPort),
        				new NTCredentials(proxyUsername, proxyPassword, proxyWorkstation, proxyDomain));
            }
        }

        return httpClient;
	}

}
