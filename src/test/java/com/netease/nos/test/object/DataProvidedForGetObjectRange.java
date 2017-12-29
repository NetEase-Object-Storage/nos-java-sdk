package com.netease.nos.test.object;

import java.io.File;

import org.testng.annotations.DataProvider;

import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.GetObjectRequest;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.nos.test.utils.TestHostConfig;

public class DataProvidedForGetObjectRange {

	public static String bucketfortestgetobjectrange = "bucketforgetobjectrange"+TestHostConfig.region.toLowerCase();
	public static String objKey = "objectkey1";
	public static String filePath = "TestFile"+File.separator+"123.txt";
	@DataProvider
	public static Object[][] putBucket() {
		return new Object[][] { { new CreateBucketRequest(bucketfortestgetobjectrange, TestHostConfig.region) } };
	}
	
	@DataProvider
	public static Object[][] putObject() {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketfortestgetobjectrange, objKey, new File(filePath));
		return new Object[][] { { putObjectRequest } };
	}
	
	@DataProvider
	public static Object[][] getObject() {
		GetObjectRequest request = new GetObjectRequest(bucketfortestgetobjectrange, objKey);
		request.setRange(0, 8);
		return new Object[][] { { request } };
	}
	
	@DataProvider
	public static Object[][] getObjectWithInvaildRange() {
		GetObjectRequest request = new GetObjectRequest(bucketfortestgetobjectrange, objKey);
		request.setRange(0, -1);
		return new Object[][] { { request } };
	}
	
	@DataProvider
	public static Object[][] getObjectWithLargerRange() {
		GetObjectRequest request = new GetObjectRequest(bucketfortestgetobjectrange, objKey);
		File file = new File(filePath);
		request.setRange(0, file.length()+10);
		return new Object[][] { { request } };
	}
	
	@DataProvider
	public static Object[][] getObject3() {
		GetObjectRequest request = new GetObjectRequest(bucketfortestgetobjectrange, objKey);
		request.setRange(100000, 100000000);
		return new Object[][] { { request } };
	}
}
