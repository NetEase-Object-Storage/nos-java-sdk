package com.netease.nos.test.mulitpart;

import java.io.File;

import org.testng.annotations.DataProvider;

import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.InitiateMultipartUploadRequest;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.nos.test.utils.TestHostConfig;

public class DataProvidedForInitiateMulitPart {

	public static String bucketfortestmulitupload = "bucketfortestmulitupload"+TestHostConfig.region.toLowerCase();
	public static String bucketnotexisted = "bucketnotexisted";
	public static String objectname = "objectkey1";
	@DataProvider
	public static Object[][] putBucket() {
		return new Object[][] { { new CreateBucketRequest(bucketfortestmulitupload, TestHostConfig.region) } };
	}

	
	@DataProvider
	public static Object[][] putObject() {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketfortestmulitupload, objectname, new File(
				"TestFile" + File.separator + "123.txt"));
		return new Object[][] { { putObjectRequest } };
	}

	
	@DataProvider
	public static Object[][] initiateNotExistedBucket() {
		return new Object[][] { { new InitiateMultipartUploadRequest(bucketnotexisted, "key") } };
	}
	
	
	
	@DataProvider
	public static Object[][] initiateObjectHasExisted() {
		return new Object[][] { { new InitiateMultipartUploadRequest(bucketfortestmulitupload, objectname) } };
	}
	
	
	@DataProvider
	public static Object[][] initiateNormal() {
		return new Object[][] { { new InitiateMultipartUploadRequest(bucketfortestmulitupload, "normalkey") } };
	}
	
	@DataProvider
	public static Object[][] initiateWithObjNameToLong() {
		int maxLen = 1000;
		StringBuilder longObjectName = new StringBuilder("");
		for(int i=0; i<(maxLen+1); i++){
			longObjectName.append("a");
		}
		return new Object[][] { { new InitiateMultipartUploadRequest(bucketfortestmulitupload, longObjectName.toString()) } };
	}
}
