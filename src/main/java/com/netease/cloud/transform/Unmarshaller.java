package com.netease.cloud.transform;

public interface Unmarshaller<T, R> {

    public T unmarshall(R in) throws Exception;
    
}
