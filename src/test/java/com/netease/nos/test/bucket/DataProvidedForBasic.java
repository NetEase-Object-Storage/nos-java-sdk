package com.netease.nos.test.bucket;

import java.io.File;

import org.testng.annotations.DataProvider;

import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.DeleteBucketRequest;
import com.netease.cloud.services.nos.model.DeleteObjectRequest;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.nos.test.utils.TestHostConfig;

public class DataProvidedForBasic {

	public static String bucketmaybehaveobject = "bucketmaybehaveobject"+TestHostConfig.region.toLowerCase();
	public static String bucketnotexisted = "bucketnotexisted";
	public static String hihihi = "hihihi"+TestHostConfig.region.toLowerCase();
	@DataProvider
	public static Object[][] putBucket() {
		return new Object[][] { { new CreateBucketRequest(bucketmaybehaveobject, TestHostConfig.region) } };
	}

	@DataProvider
	public static Object[][] putBucketbj() {
		return new Object[][] { { new CreateBucketRequest(hihihi, "BJ") } };
	}

	/** BUcket not existed **/
	@DataProvider
	public static Object[][] deleteNotExistedBucket() {
		return new Object[][] { { new DeleteBucketRequest(bucketnotexisted) } };
	}

	@DataProvider
	public static Object[][] putObject() {
		return new Object[][] { { new PutObjectRequest(bucketmaybehaveobject, "objectkey", new File("TestFile"
				+ File.separator + "123.txt")) } };
	}

	/** bucket not empty **/
	@DataProvider
	public static Object[][] deleteNotEmptyBucket() {
		return new Object[][] { { new DeleteBucketRequest(bucketmaybehaveobject) } };
	}

	@DataProvider
	public static Object[][] deleteObject() {
		return new Object[][] { { new DeleteObjectRequest(bucketmaybehaveobject, "objectkey") } };
	}

	@DataProvider
	public static Object[][] deleteBucket() {
		return new Object[][] { { new DeleteBucketRequest(bucketmaybehaveobject) } };
	}

}
