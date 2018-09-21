package com.netease.nos.test.bucket;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.netease.cloud.ServiceException;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.Bucket;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.PutBucketDedupRequest;
import com.netease.nos.test.utils.Clear;
import com.netease.nos.test.utils.NOSConstant;
import com.netease.nos.test.utils.TestHostConfig;

public class BucketDedupTest {

	Credentials credentials;
	NosClient client;
	
	private static final String credential = "credentials.properties";
	
	@BeforeClass
	public void before(){
		TestHostConfig.changeHost();
		String conf = System.getProperty("nos.credential", credential);
		InputStream confIn = BucketBasicTest.class.getClassLoader().getResourceAsStream(conf);
		Properties properties = new Properties();
		try {
			properties.load(confIn);
		} catch (IOException e) {
			
			System.exit(-1);
		}
		String accessKey = properties.getProperty("accessKey");
		String secretKey = properties.getProperty("secretKey");
		credentials = new BasicCredentials(accessKey, secretKey);
		client = new NosClient(credentials);
	}
	
	@Test(dataProvider= "getPutBucketAllAndNormal", dataProviderClass = DataProvidedForPutBucket.class)
	public void testPutBucketAllAndNormal(CreateBucketRequest createBucketRequest){
		if(client.doesBucketExist(createBucketRequest.getBucketName())){
			client.deleteBucket(createBucketRequest.getBucketName());
		}
		Bucket bucket = client.createBucket(createBucketRequest);
		Assert.assertNotNull(bucket);
		Assert.assertEquals(bucket.getName(),createBucketRequest.getBucketName());
		Assert.assertTrue(client.doesBucketExist(createBucketRequest.getBucketName()));
	}
	
	@Test(dataProvider= "putBucketDedupSuspended", dataProviderClass = DataProvidedForDedup.class, dependsOnMethods = {"testPutBucketAllAndNormal"})
	public void testPutBucketDedupSuspended(PutBucketDedupRequest putBucketDedupRequest){
		Assert.assertEquals(client.getBucketDedup(putBucketDedupRequest.getBucketName()).getStatus().toLowerCase(), "enabled");
		try {
			client.setBucketDedup(putBucketDedupRequest);
			Assert.assertEquals(client.getBucketDedup(putBucketDedupRequest.getBucketName()).getStatus(), "suspended");
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}		
	}
	
	@Test(dataProvider= "putBucketDedupDisabled", dataProviderClass = DataProvidedForDedup.class, dependsOnMethods = {"testPutBucketDedupSuspended"})
	public void testPutBucketDedupDisabled(PutBucketDedupRequest putBucketDedupRequest){
		Assert.assertEquals(client.getBucketDedup(putBucketDedupRequest.getBucketName()).getStatus(), "suspended");
		try {
			client.setBucketDedup(putBucketDedupRequest);
			Assert.assertEquals(client.getBucketDedup(putBucketDedupRequest.getBucketName()).getStatus(), "disabled");
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider= "putNotExistedBucketDedup", dataProviderClass = DataProvidedForDedup.class)
	public void testPutNotExistedBucketDedup(PutBucketDedupRequest putBucketDedupRequest){
		Assert.assertFalse(client.doesBucketExist(putBucketDedupRequest.getBucketName()));
		try {
			client.setBucketDedup(putBucketDedupRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchBucket).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}		
	}
	
	@Test(dataProvider= "putBucketIllegalDedup", dataProviderClass = DataProvidedForDedup.class, dependsOnMethods = {"testPutBucketAllAndNormal"})
	public void testPutBucketIllegalDedup(PutBucketDedupRequest putBucketDedupRequest){
		try {
			client.setBucketDedup(putBucketDedupRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.MalformedXML).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}	
	}
	
	@AfterClass
	public void after(){
		Clear.clear(client, DataProvidedForDedup.bucketallandnormal);
		Clear.clear(client, DataProvidedForDedup.bucketnotexisted);
	}
	
}
