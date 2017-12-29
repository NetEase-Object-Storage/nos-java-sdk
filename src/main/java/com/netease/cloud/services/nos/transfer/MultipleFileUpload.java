package com.netease.cloud.services.nos.transfer;


/**
 * Multiple file download of an entire virtual directory.
 */
public interface  MultipleFileUpload extends Transfer {

    /**
     * Returns the key prefix of the virtual directory being downloaded.
     */
    public String getKeyPrefix();
    
    /**
     * Returns the name of the bucket from which files are downloaded.
     */
    public String getBucketName();
    
}
