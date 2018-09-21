package com.netease.cloud.auth;

import com.netease.cloud.ClientException;
import com.netease.cloud.Request;

public interface Signer {
    public void sign(Request<?> request, Credentials credentials) throws ClientException;
}
