package com.netease.nos.test.object;

import java.io.File;

import org.testng.annotations.DataProvider;

import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.nos.test.utils.TestHostConfig;

public class DataProvidedForObjectVersions {

	public static String bucketfortestobjectversions = "bucketforobjectversions"+TestHostConfig.region.toLowerCase();
	@DataProvider
	public static Object[][] putBucket() {
		return new Object[][] { { new CreateBucketRequest(bucketfortestobjectversions, TestHostConfig.region) } };
	}

	
	@DataProvider
	public static Object[][] testGetObjectVersionsWithDisabledVersion() {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketfortestobjectversions, "objectkey2", new File(
				"TestFile" + File.separator + "123.txt"));
		return new Object[][] { { putObjectRequest} };
	}

	@DataProvider
	public static Object[][] testGetObjectVersionsWithEnbledVersion() {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketfortestobjectversions, "objectkey3", new File(
				"TestFile" + File.separator + "123.txt"));
		return new Object[][] { { putObjectRequest} };
	}
}
