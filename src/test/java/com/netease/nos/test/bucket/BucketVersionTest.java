package com.netease.nos.test.bucket;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.netease.cloud.auth.Credentials;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.Bucket;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.nos.test.utils.Clear;
import com.netease.nos.test.utils.TestHostConfig;

public class BucketVersionTest {
	
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
	
	/*@Test(dataProvider= "putBucketVersioningEnabled", dataProviderClass = DataProvidedForVersion.class, dependsOnMethods = {"testPutBucketAllAndNormal"})
	public void testPutBucketVersioningEnabled(SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest){
		Assert.assertEquals(client.getBucketVersioningConfiguration(setBucketVersioningConfigurationRequest.getBucketName()).getStatus().toLowerCase(), BucketVersioningConfiguration.DISABLED.toLowerCase());
		client.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest);
		Assert.assertEquals(client.getBucketVersioningConfiguration(setBucketVersioningConfigurationRequest.getBucketName()).getStatus(), setBucketVersioningConfigurationRequest.getVersioningConfiguration().getStatus());
	}
	
	@Test(dataProvider= "putBucketVersioningSuspended", dataProviderClass = DataProvidedForVersion.class, dependsOnMethods = {"testPutBucketAllAndNormal"})
	public void testPutBucketVersioningSuspended(SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest){
		client.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest);
		Assert.assertEquals(client.getBucketVersioningConfiguration(setBucketVersioningConfigurationRequest.getBucketName()).getStatus().toLowerCase(), setBucketVersioningConfigurationRequest.getVersioningConfiguration().getStatus().toLowerCase());
	}
	
	@Test(dataProvider= "putNotExistedBucketVersioning", dataProviderClass = DataProvidedForVersion.class)
	public void testPutNotExistedBucketVersioning(SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest){
		try {
			client.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchBucket).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
	}*/

	@AfterClass
	public void after(){
		Clear.clear(client, DataProvidedForVersion.bucketallandnormal);
		Clear.clear(client, DataProvidedForVersion.bucketNotExist);
	}
}
