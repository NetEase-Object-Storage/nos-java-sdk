package com.netease.nos.test.object;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.DataProvider;

import com.netease.cloud.services.nos.model.DeleteObjectsRequest;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.nos.test.utils.TestHostConfig;

public class DataProvidedForDeleteMulitObject {

	public static String bucketfortestdeletemulitobject = "bucketfordeletemulitobject"+TestHostConfig.region.toLowerCase();

	@DataProvider
	public static Object[][] putObject() {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketfortestdeletemulitobject, "objectkey1", new File("TestFile"+File.separator+"123.txt"));
		return new Object[][] { { putObjectRequest } };
	}
	
	@DataProvider
	public static Object[][] deleteMulitObject() {
		DeleteObjectsRequest  deleteObjectsRequest = new DeleteObjectsRequest(bucketfortestdeletemulitobject);
		List<String> keys = new ArrayList<String>();
		keys.add("objectkey1");
		keys.add("objectkey2");
		deleteObjectsRequest.setKeys(keys);
		return new Object[][] { { deleteObjectsRequest } };
	}
}
