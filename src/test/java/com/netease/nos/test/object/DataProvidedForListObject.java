package com.netease.nos.test.object;

import java.io.File;

import org.testng.annotations.DataProvider;

import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.ListObjectsRequest;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.nos.test.utils.TestHostConfig;

public class DataProvidedForListObject {

	public static String bucketfortestlistobject = "bucketforlistobject-sdk"+TestHostConfig.region.toLowerCase();
	@DataProvider
	public static Object[][] putBucket() {
		return new Object[][] { { new CreateBucketRequest(bucketfortestlistobject, TestHostConfig.region) } };
	}
	
	@DataProvider
	public static Object[][] putObject() {
		return new Object[][] { { new PutObjectRequest(bucketfortestlistobject, "objectkey", new File("TestFile"+File.separator+"123.txt")) } };
	}
	
	/** empty query et Bucket **/
	@DataProvider
	public static Object[][] listObjectDefault() {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		listObjectsRequest.setBucketName(bucketfortestlistobject);
		return new Object[][] { { listObjectsRequest }};
	}
	
	/** prefix not empty have object **/
	@DataProvider
	public static Object[][] listObjectSpecifiedPrefixNoResult() {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		listObjectsRequest.setBucketName(bucketfortestlistobject);
		listObjectsRequest.setPrefix("no");
		return new Object[][] { { listObjectsRequest }};
	}
	
	/** prefix not empty object number<=max-keys **/
	@DataProvider
	public static Object[][] listObjectSpecifiedPrefixSmallerMaxkey() {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		listObjectsRequest.setBucketName(bucketfortestlistobject);
		listObjectsRequest.setPrefix("etc");
		listObjectsRequest.setMaxKeys(800);
		return new Object[][] { { listObjectsRequest }};
	}
	
	/** prefix not empty object number>=max-keys **/
	
	/** 0<max-keys< 1000 **/
	@DataProvider
	public static Object[][] testListObjectLargerThanMaxkey() {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		listObjectsRequest.setBucketName(bucketfortestlistobject);
		listObjectsRequest.setPrefix("etc");
		listObjectsRequest.setMaxKeys(200);
		return new Object[][] { { listObjectsRequest }};
	}
	
	/** 0>max-keys or max-keys>1000 **/
	@DataProvider
	public static Object[][] listObjectWrongMaxkey1() {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		listObjectsRequest.setBucketName(bucketfortestlistobject);
		listObjectsRequest.setMaxKeys(1001);
		return new Object[][] { { listObjectsRequest }};
	}
	/** 0>max-keys or max-keys>1000 **/
	@DataProvider
	public static Object[][] listObjectWrongMaxkey2() {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		listObjectsRequest.setBucketName(bucketfortestlistobject);
		listObjectsRequest.setMaxKeys(-1);
		return new Object[][] { { listObjectsRequest }};
	}
	
	/** maker not empty no result **/
	@DataProvider
	public static Object[][] listObjectSpecifiedMarkerNoResult() {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		listObjectsRequest.setBucketName(bucketfortestlistobject);
		listObjectsRequest.setMarker("zzz");
		return new Object[][] { { listObjectsRequest }};
	}
	
	
	@DataProvider
	public static Object[][] listObjectSpecifiedMarkersmallerMaxkey() {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		listObjectsRequest.setBucketName(bucketfortestlistobject);
		listObjectsRequest.setMarker("etc");
		listObjectsRequest.setMaxKeys(800);
		return new Object[][] { { listObjectsRequest, "home"}};
	}
	
	@DataProvider
	public static Object[][] listObjectSpecifiedMarkerLargerMaxkey() {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		listObjectsRequest.setBucketName(bucketfortestlistobject);
		listObjectsRequest.setMarker("etc");
		listObjectsRequest.setMaxKeys(200);
		return new Object[][] { { listObjectsRequest, "home"}};
	}
	
	@DataProvider
	public static Object[][] listObjectSpecifiedDelimiter() {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		listObjectsRequest.setBucketName(bucketfortestlistobject);
		listObjectsRequest.setDelimiter("qa");
		return new Object[][] { { listObjectsRequest, "home#qa"}};
	}
	
	@DataProvider
	public static Object[][] listObjectSpecifiedDelimiterAndWrongPrefix() {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		listObjectsRequest.setBucketName(bucketfortestlistobject);
		listObjectsRequest.setPrefix("etc");
		listObjectsRequest.setDelimiter("qa");
		return new Object[][] { { listObjectsRequest }};
	}
	
	@DataProvider
	public static Object[][] listObjectSpecifiedDelimiterAndRightPrefix() {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		listObjectsRequest.setBucketName(bucketfortestlistobject);
		listObjectsRequest.setPrefix("home");
		listObjectsRequest.setDelimiter("qa");
		return new Object[][] { { listObjectsRequest, "home#qa"}};
	}
	
	
	/**delimiter **/
	@DataProvider
	public static Object[][] listObjectSpecifiedWrongDelimiter() {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		listObjectsRequest.setBucketName(bucketfortestlistobject);
		listObjectsRequest.setDelimiter("sdfhj");
		return new Object[][] { { listObjectsRequest }};
	}
	
	@DataProvider
	public static Object[][] getObjectsDefault() {
		String bucket = DataProvidedForListObject.bucketfortestlistobject;
		String prefix = "home";
		String dir = "D:" + File.separator + "test";
		
		return new Object[][] { { bucket, new File(dir) ,prefix, true }};
	}
}
