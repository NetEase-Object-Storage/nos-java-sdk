package com.netease.cloud.auth;

import com.netease.cloud.ClientException;

public interface StringSigner {
	public String sign(String stringToSign, Credentials credentials) throws ClientException;
}
