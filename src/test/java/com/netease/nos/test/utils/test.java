package com.netease.nos.test.utils;

import java.io.File;

import com.netease.cloud.auth.Credentials;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.PutObjectRequest;

public class test {
	public static void main(String[] args){
	
//		AWSCredentials credentials = new BasicAWSCredentials("123", "fgd");
//		NosClient client = new NosClient(credentials);
//		
//		/** 创建bucket **/
//		CreateBucketRequest createBucketRequest = new CreateBucketRequest("bucketda");
//		client.createBucket(createBucketRequest);
//		
//		/** 上传object **/
//		PutObjectRequest putObjectRequest = new PutObjectRequest("bucketda", "objectkey", new File("D:"+File.separator+"123.txt"));
//		client.putObject(putObjectRequest);
//		
		
		Credentials credentials = new BasicCredentials("ddd", "sda");
		NosClient client = new NosClient(credentials);
		

		PutObjectRequest putObjectRequest = new PutObjectRequest("hshshshsh", "hkjshh/dhbxhghj/dhj", new File("D:/123.txt"));
		client.putObject(putObjectRequest);
		System.out.println("dfdfssvs");
	}

}
