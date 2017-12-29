package com.netease.nos.test.object;

import java.io.File;

import org.testng.annotations.DataProvider;

import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.DeleteObjectRequest;
import com.netease.cloud.services.nos.model.GetObjectRequest;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.nos.test.utils.TestHostConfig;

public class DataProvidedForGetObject {
	
	public static String bucketfortestgetobject = "bucketfortestgetobject"+TestHostConfig.region.toLowerCase();
	public static String bucketownedbyothers = "bucketownedbyothers"+TestHostConfig.region.toLowerCase();
	public static String bucketnotexisted ="bucketnotexisted";
	public static String objectKey = "objec_tkey1";
	public static String othersobjectKey = "othersobjectKey";
	@DataProvider
	public static Object[][] putBucket() {
		return new Object[][] { { new CreateBucketRequest(bucketfortestgetobject, TestHostConfig.region) } };
	}
	
	@DataProvider
	public static Object[][] putObject() {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketfortestgetobject, "obje c_tke+y1", new File("TestFile"+File.separator+"123.txt"));
		return new Object[][] { { putObjectRequest } };
	}
	
	
	/*** getobject bucket not existed */
	@DataProvider
	public static Object[][] getObjectButBucketNotExisted() {
		GetObjectRequest getObjectRequest = new GetObjectRequest(bucketnotexisted, objectKey);
		return new Object[][] { { getObjectRequest } };
	}
	
	/*** getobject object not existed */
	@DataProvider
	public static Object[][] getObjectButObjectNotExisted() {
		GetObjectRequest getObjectRequest = new GetObjectRequest(bucketfortestgetobject, "objectnotexisted");
		return new Object[][] { { getObjectRequest } };
	}
	
	/*** deleteobject bucket not existed */
	@DataProvider
	public static Object[][] deleteObjectBucketNotExist() {
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketnotexisted, objectKey);
		return new Object[][] { { deleteObjectRequest } };
	}
	
	/*** deleteobject object not existed */
	@DataProvider
	public static Object[][] deleteObjectObjectNotExisted() {
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketfortestgetobject, "objectnotexisted");
		return new Object[][] { { deleteObjectRequest } };
	}
	
	
	@DataProvider
	public static Object[][] getObject() {
		GetObjectRequest getObjectRequest = new GetObjectRequest(bucketfortestgetobject, objectKey);
		return new Object[][] { { getObjectRequest } };
	}
	
	@DataProvider
	public static Object[][] testDeleteObjectButFromOthersBucket() {
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketownedbyothers, othersobjectKey);
		return new Object[][] { { deleteObjectRequest } };
	}	
}
