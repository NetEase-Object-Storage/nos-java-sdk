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
import com.netease.cloud.services.nos.model.CannedAccessControlList;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.GetBucketAclRequest;
import com.netease.cloud.services.nos.model.SetBucketAclRequest;
import com.netease.nos.test.utils.Clear;
import com.netease.nos.test.utils.NOSConstant;
import com.netease.nos.test.utils.TestHostConfig;

public class BucketAclTest {
    
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
	
	@Test(dataProvider= "getBucketAcl", dataProviderClass = DataProvidedForAcl.class, dependsOnMethods = {"testPutBucketAllAndNormal"})
	public void testGetBucketAcl(GetBucketAclRequest getBucketAclRequest){
		Assert.assertEquals(client.getBucketAcl(getBucketAclRequest.getBucketName()),CannedAccessControlList.Private);
	}
	
	@Test(dataProvider= "getNotExistedBucketAcl", dataProviderClass = DataProvidedForAcl.class)
	public void testGetNotExistedBucketAcl(GetBucketAclRequest getBucketAclRequest){
		Assert.assertFalse(client.doesBucketExist(getBucketAclRequest.getBucketName()));
		try {
			client.getBucketAcl(getBucketAclRequest.getBucketName());
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchBucket).getNosErrorCode();
			String errorCodeString = e.getErrorCode();
			Assert.assertEquals(errorCodeString, expectedErrorCode, "Failed to verifyERROR!");
		}		
	}
	
	@Test(dataProvider= "setBucketAcl", dataProviderClass = DataProvidedForAcl.class, dependsOnMethods = {"testPutBucketAllAndNormal"})
	public void testSetBucketAcl(SetBucketAclRequest setBucketAclRequest){
		client.setBucketAcl(setBucketAclRequest.getBucketName(), setBucketAclRequest.getCannedAcl());
		Assert.assertEquals(client.getBucketAcl(setBucketAclRequest.getBucketName()), setBucketAclRequest.getCannedAcl());
	}
	
	@Test(dataProvider= "setNotExistedBucketAcl", dataProviderClass = DataProvidedForAcl.class)
	public void testSetNotExistedBucketAcl(SetBucketAclRequest setBucketAclRequest){
		Assert.assertFalse(client.doesBucketExist(setBucketAclRequest.getBucketName()));
		try {
			client.setBucketAcl(setBucketAclRequest.getBucketName(), setBucketAclRequest.getCannedAcl());
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchBucket).getNosErrorCode();
			String errorCodeString = e.getErrorCode();
			Assert.assertEquals(errorCodeString, expectedErrorCode, "Failed to verifyERROR!");
		}
	}
	
	@AfterClass
	public void after(){
		Clear.clear(client, DataProvidedForAcl.bucketallandnormal);
		Clear.clear(client, DataProvidedForAcl.bucketnotexist);
	}
}
