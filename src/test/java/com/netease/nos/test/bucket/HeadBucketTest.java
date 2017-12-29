package com.netease.nos.test.bucket;

import java.io.IOException;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.netease.cloud.auth.PropertiesCredentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.HeadBucketRequest;

/**
 * @author WANG Binghuan 2012-12-28
 * 
 */
public class HeadBucketTest {

	private NosClient nosClient;

	@BeforeClass
	public void beforeclass() {
		try {
			nosClient = new NosClient(new PropertiesCredentials(
					HeadBucketTest.class.getResourceAsStream("credentials.properties")));
			nosClient.setEndpoint("115.236.113.64");
		} catch (IOException e) {
			Assert.fail();
		}
	}

	@Test
	public void testHeadBucket() {
		System.out.println(nosClient.doesBucketExist("abc"));
	}

	@Test
	public void testDebugLog() {
		HeadBucketRequest request = new HeadBucketRequest("bucket");
		request.setLogID("111");
		request.setLogSeq("xxxx");
		System.out.println(nosClient.doesBucketExist(request));
		CreateBucketRequest r = new CreateBucketRequest("bucket-121");
		System.out.println(nosClient.createBucket(r));
	}

}
