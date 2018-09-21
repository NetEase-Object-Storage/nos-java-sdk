package com.netease.cloud.http;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.netease.cloud.ClientConfiguration;

/** Responsible for creating and configuring instances of Apache HttpClient4's Connection Manager. */
class ConnectionManagerFactory {

    private static final String HTTP  = "http";
    private static final String HTTPS = "https";

    private static Registry<ConnectionSocketFactory> getSsFactory(){
        return RegistryBuilder.<ConnectionSocketFactory>create()
                .register(HTTP, PlainConnectionSocketFactory.getSocketFactory())
                .register(HTTPS, SSLConnectionSocketFactory.getSocketFactory())
                .build();

    }

    public static PoolingHttpClientConnectionManager createPoolingClientConnManager(ClientConfiguration configuration){
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(getSsFactory());
        poolingHttpClientConnectionManager.setMaxTotal(configuration.getMaxConnections());
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(configuration.getMaxConnections());
        poolingHttpClientConnectionManager.setValidateAfterInactivity(1000);//1s equal setStaleConnectionCheckEnabled
        IdleConnectionReaper.registerConnectionManager(poolingHttpClientConnectionManager);
        return poolingHttpClientConnectionManager;
    }
}
