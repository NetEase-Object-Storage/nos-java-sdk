package com.netease.cloud.services.nos.model;

import com.netease.cloud.WebServiceRequest;

/**
 * Generic request container for web service requests on buckets.
 */
public class GenericBucketRequest extends WebServiceRequest {

    private final String bucket;

    public GenericBucketRequest(String bucket) {
        super();
        this.bucket = bucket;
    }

    public String getBucket() {
        return bucket;
    }

}
