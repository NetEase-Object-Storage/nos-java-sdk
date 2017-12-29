package com.netease.cloud.internal;

import com.netease.cloud.auth.Credentials;
import com.netease.cloud.auth.CredentialsProvider;

/**
 * Simple implementation of CredentialsProvider that just wraps static Credentials.
 */
public class StaticCredentialsProvider implements CredentialsProvider {

    private final Credentials credentials;

    public StaticCredentialsProvider(Credentials credentials) {
        this.credentials = credentials;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void refresh() {}

}
