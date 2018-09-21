package com.netease.nos.test.object;

import java.io.File;

import com.netease.cloud.auth.PropertiesCredentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.PutObjectRequest;

/**
 * @author WANG Binghuan 2012-11-13
 * 
 */
public class PutObjectRetryTest {

	private static final String bucketName = "testbucket";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		NosClient client = new NosClient(new PropertiesCredentials(PutObjectRetryTest.class.getClassLoader()
				.getResourceAsStream("credentials.properties")));
//		client.setEndpoint("115.236.113.64");
		client.setEndpoint("172.17.2.64:8182");
//		if (!client.doesBucketExist(bucketName)) {
//			System.out.println("Creating bucket " + bucketName + "\n");
//			client.createBucket(bucketName);
//		}
		PutObjectRequest req = new PutObjectRequest(bucketName, "1.tar", new File("D:\\tmp\\1.tar"));
		req.setStorageClass("standard");
		client.putObject(req);
		
	}

}
