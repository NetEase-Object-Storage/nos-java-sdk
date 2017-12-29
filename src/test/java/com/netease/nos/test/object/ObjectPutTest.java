package com.netease.nos.test.object;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.netease.cloud.ServiceException;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.Bucket;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.NOSObject;
import com.netease.cloud.services.nos.model.ObjectMetadata;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.nos.test.utils.NOSConstant;
import com.netease.nos.test.utils.TestHelper;
import com.netease.nos.test.utils.TestHostConfig;

public class ObjectPutTest {

	Credentials credentials;
	Credentials credentials2;
	NosClient client;
	NosClient client2;

	private static final String credential = "credentials.properties";
	

	@BeforeClass
	public void before() {
		TestHostConfig.changeHost();
		String conf = System.getProperty("nos.credential", credential);
		InputStream confIn = ObjectPutTest.class.getClassLoader().getResourceAsStream(conf);
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
		
		CreateBucketRequest createBucketRequest = new CreateBucketRequest(
				            DataProvidedForPutObject.bucketfortestputobject, TestHostConfig.region);
		if (!client.doesBucketExist(createBucketRequest.getBucketName())) {
			Bucket bucket = client.createBucket(createBucketRequest);
			Assert.assertNotNull(bucket);
			Assert.assertEquals(bucket.getName(), createBucketRequest.getBucketName());
			Assert.assertTrue(client.doesBucketExist(createBucketRequest.getBucketName()));
		}
		
		String accessKey2 = properties.getProperty("accessKey2");
		String secretKey2 = properties.getProperty("secretKey2");
		credentials2 = new BasicCredentials(accessKey2, secretKey2);
		client2 = new NosClient(credentials2);
		CreateBucketRequest createBucketRequest2 = new CreateBucketRequest(
	            DataProvidedForPutObject.bucketownedbyothers, TestHostConfig.region);
		if (!client.doesBucketExist(DataProvidedForPutObject.bucketownedbyothers)) {
			Bucket bucket2 = client2.createBucket(createBucketRequest2);
			Assert.assertNotNull(bucket2);
			Assert.assertEquals(bucket2.getName(), createBucketRequest2.getBucketName());
			Assert.assertTrue(client2.doesBucketExist(createBucketRequest2.getBucketName()));
		}
	}
	@AfterClass
	public void after(){
		try{
			//Clear.clearAllVersions(client, DataProvidedForPutObject.bucketfortestputobject);
			//Clear.clearAllVersions(client2, DataProvidedForPutObject.bucketownedbyothers);
		}catch(Exception e){

		}
	}
	
	@Test(dataProvider = "putObjectNormalMD5", dataProviderClass = DataProvidedForPutObject.class)
	public void testPutObjectNormalMD5(PutObjectRequest putObjectRequest) {
		try {
			client.putObject(putObjectRequest);
            NOSObject object = client.getObject(putObjectRequest.getBucketName(), putObjectRequest.getKey());
            String expectedMD5 = TestHelper.getMD5(putObjectRequest.getFile().getPath());
            String actualMd5 = object.getObjectMetadata().getETag();
            Assert.assertEquals(actualMd5, expectedMD5);
		} catch (Exception e) {
	        Assert.fail(e.getMessage());
		}		
		ObjectMetadata meta = client.getObjectMetadata(putObjectRequest.getBucketName(), putObjectRequest.getKey());
		Assert.assertNotNull(meta);
		Assert.assertEquals(meta.getETag().toLowerCase(), DataProvidedForPutObject.FileMD5);
	}

	@Test(dataProvider = "putObjectRightStorageClass", dataProviderClass = DataProvidedForPutObject.class)
	public void testPutObjectRightStorageClass(PutObjectRequest putObjectRequest) {
		try {
			client.putObject(putObjectRequest);
            NOSObject object = client.getObject(putObjectRequest.getBucketName(), putObjectRequest.getKey());
            String expectedMD5 = TestHelper.getMD5(putObjectRequest.getFile().getPath());
            String actualMd5 = object.getObjectMetadata().getETag();
            Assert.assertEquals(actualMd5, expectedMD5);
		} catch (Exception e) {
	        Assert.fail(e.getMessage());
		}
		ObjectMetadata meta = client.getObjectMetadata(putObjectRequest.getBucketName(), putObjectRequest.getKey());
		Assert.assertNotNull(meta);
		Assert.assertEquals(meta.getETag().toLowerCase(), DataProvidedForPutObject.FileMD5);
	}

	@Test(dataProvider = "putObjectWrongStorageClass", dataProviderClass = DataProvidedForPutObject.class)
	public void testPutObjectWrongStorageClass(PutObjectRequest putObjectRequest) {
		try {
			client.putObject(putObjectRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.InvalidStorageClass).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
	}

	/*@Test(dataProvider = "putObjectExistedObjectName", dataProviderClass = DataProvidedForPutObject.class, dependsOnMethods = { "testPutObjectNormalMD5" })
	public void testPutObjectExistedObjectName(PutObjectRequest putObjectRequest) {
		SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest = new SetBucketVersioningConfigurationRequest(
				putObjectRequest.getBucketName(), new BucketVersioningConfiguration(BucketVersioningConfiguration.ENABLED));
		client.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest);
		client.putObject(putObjectRequest);
		ObjectMetadata meta = client.getObjectMetadata(putObjectRequest.getBucketName(), putObjectRequest.getKey());
		Assert.assertNotNull(meta);
		Assert.assertEquals(meta.getETag().toLowerCase(), DataProvidedForPutObject.FileMD5);

		GetObjectVersionsRequest getObjectVersionsRequest = new GetObjectVersionsRequest(
				                                           putObjectRequest.getBucketName(), putObjectRequest.getKey());
		GetObjectVersionsResult result = client.getObjectVersions(getObjectVersionsRequest);
		List<NOSVersionSummary> sum = result.getVersionSummary();
		Assert.assertNotNull(sum);
		Assert.assertEquals(sum.get(0).getBucketName(), putObjectRequest.getBucketName());
		client.deleteObject(putObjectRequest.getBucketName(), putObjectRequest.getKey(), sum.get(0).getVersionId());
	}*/

	@Test(dataProvider = "putObjectBucketNotExist", dataProviderClass = DataProvidedForPutObject.class)
	public void testPutObjectBucketNotExist(PutObjectRequest putObjectRequest) {
		try {
			client.putObject(putObjectRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchBucket).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}

	}	
	
	@Test(dataProvider = "putObjectInputStream", dataProviderClass = DataProvidedForPutObject.class)
	public void testPutObjectInputStream(PutObjectRequest putObjectRequest, String filePath) {
		try {
			client.putObject(putObjectRequest);
            NOSObject object = client.getObject(putObjectRequest.getBucketName(), putObjectRequest.getKey());
            String expectedMD5 = TestHelper.getMD5(filePath);
            String actualMd5 = object.getObjectMetadata().getETag();
            Assert.assertEquals(actualMd5, expectedMD5);
		} catch (Exception e) {
	        Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "testPutObjectIntoBucketOwnedByOthers", dataProviderClass = DataProvidedForPutObject.class)
	public void testPutObjectIntoBucketOwnedByOthers(PutObjectRequest putObjectRequest) {
		try {
			client.putObject(putObjectRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.AccessDenied).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
		ObjectMetadata meta = client.getObjectMetadata(putObjectRequest.getBucketName(), putObjectRequest.getKey());
		Assert.assertNotNull(meta);
		Assert.assertEquals(meta.getETag().toLowerCase(), DataProvidedForPutObject.FileMD5);
	}
	
}
