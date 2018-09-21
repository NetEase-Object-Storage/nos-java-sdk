package com.netease.cloud.http;


import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

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
	public CloseableHttpClient createHttpClient(ClientConfiguration config, PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
        /* Form User-Agent information */
        String userAgent = config.getUserAgent();
        if (!(userAgent.equals(ClientConfiguration.DEFAULT_USER_AGENT))) {
            userAgent += ", " + ClientConfiguration.DEFAULT_USER_AGENT;
        }

        /* Set HTTP client parameters */

        int socketSendBufferSizeHint = config.getSocketBufferSizeHints()[0];
        int socketReceiveBufferSizeHint = config.getSocketBufferSizeHints()[1];
        ConnectionConfig connectionConfig;
        if (socketSendBufferSizeHint > 0 || socketReceiveBufferSizeHint > 0) {
            connectionConfig =  ConnectionConfig.custom().setBufferSize(Math.max(socketSendBufferSizeHint,socketReceiveBufferSizeHint)).build();
        } else {
            connectionConfig =  ConnectionConfig.custom().build();
        }

        /* Set connection manager */
        RequestConfig requestConfig = RequestConfig.custom().
                setConnectTimeout(config.getConnectionTimeout()).
                setConnectionRequestTimeout(config.getConnectionTimeout()).build();

        DefaultProxyRoutePlanner defaultProxyRoutePlanner = null;
        SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).setSoTimeout(config.getSocketTimeout()).build();
        CredentialsProvider credentialsProvider = null;

        /* Set proxy if configured */
        String proxyHost = config.getProxyHost();
        int proxyPort = config.getProxyPort();
        if (proxyHost != null && proxyPort > 0) {
            NeteaseHttpClient.log.info("Configuring Proxy. Proxy Host: " + proxyHost + " " + "Proxy Port: " + proxyPort);
            HttpHost proxyHttpHost = new HttpHost(proxyHost, proxyPort);
            defaultProxyRoutePlanner = new DefaultProxyRoutePlanner(proxyHttpHost);

            String proxyUsername    = config.getProxyUsername();
            String proxyPassword    = config.getProxyPassword();
            String proxyDomain      = config.getProxyDomain();
            String proxyWorkstation = config.getProxyWorkstation();

            if (proxyUsername != null && proxyPassword != null) {
                credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(new AuthScope(proxyHost, proxyPort),
                        new NTCredentials(proxyUsername, proxyPassword, proxyWorkstation, proxyDomain));
            }
        }


        return HttpClients.custom()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultConnectionConfig(connectionConfig)
                .setUserAgent(userAgent)
                .setDefaultSocketConfig(socketConfig)
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCredentialsProvider(credentialsProvider)
                .setRoutePlanner(defaultProxyRoutePlanner)
                .build();

	}

}
