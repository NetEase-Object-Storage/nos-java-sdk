package com.netease.nos.test.bucket;

import java.util.List;
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
import com.netease.nos.test.utils.Clear;
import com.netease.nos.test.utils.NOSConstant;
import com.netease.nos.test.utils.TestHostConfig;

public class BucketPutTest {
	
	Credentials credentials;
	Credentials credentials2;
	NosClient client;
	NosClient client2;
	
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
		String accessKey2 = properties.getProperty("accessKey2");
		String secretKey2 = properties.getProperty("secretKey2");
		credentials2 = new BasicCredentials(accessKey2, secretKey2);
		client2 = new NosClient(credentials2);
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
	
	/*@Test(dataProvider= "getPutBucketIncludeCN", dataProviderClass = DataProvidedForPutBucket.class)
	public void TestPutBucketIncludeCN(CreateBucketRequest createBucketRequest){
		if(client.doesBucketExist(createBucketRequest.getBucketName())){
			client.deleteBucket(createBucketRequest.getBucketName());
		}
		Bucket bucket = client.createBucket(createBucketRequest);
		Assert.assertNotNull(bucket);
		System.out.println(bucket.getName());
		Assert.assertEquals(bucket.getName(),createBucketRequest.getBucketName());
		Assert.assertTrue(client.doesBucketExist(createBucketRequest.getBucketName()));
	}*/
	
	@Test(dataProvider= "getPutBucketWithoutRegion", dataProviderClass = DataProvidedForPutBucket.class)
	public void testPutBucketWithoutRegion(CreateBucketRequest createBucketRequest){
		if(client.doesBucketExist(createBucketRequest.getBucketName())){
			client.deleteBucket(createBucketRequest.getBucketName());
		}
		Bucket bucket = client.createBucket(createBucketRequest);
		Assert.assertNotNull(bucket);
		Assert.assertEquals(bucket.getName(),createBucketRequest.getBucketName());
		Assert.assertTrue(client.doesBucketExist(createBucketRequest.getBucketName()));
	}
	
	@Test(dataProvider= "getPutBucketWithoutAcl", dataProviderClass = DataProvidedForPutBucket.class)
	public void testPutBucketWithoutAcl(CreateBucketRequest createBucketRequest){
		if(client.doesBucketExist(createBucketRequest.getBucketName())){
			client.deleteBucket(createBucketRequest.getBucketName());
		}
		Bucket bucket = client.createBucket(createBucketRequest);
		Assert.assertNotNull(bucket);
		Assert.assertEquals(bucket.getName(),createBucketRequest.getBucketName());
		Assert.assertTrue(client.doesBucketExist(createBucketRequest.getBucketName()));
	}
	
	/*@Test(dataProvider= "getPutBucketWithoutDedup", dataProviderClass = DataProvidedForPutBucket.class)
	public void TestPutBucketWithoutDedup(CreateBucketRequest createBucketRequest){
		if(client.doesBucketExist(createBucketRequest.getBucketName())){
			client.deleteBucket(createBucketRequest.getBucketName());
		}
		Bucket bucket = client.createBucket(createBucketRequest);
		Assert.assertNotNull(bucket);
		Assert.assertEquals(bucket.getName(),createBucketRequest.getBucketName());
		Assert.assertTrue(client.doesBucketExist(createBucketRequest.getBucketName()));
	}*/
	
	@Test(dataProvider= "getPutBucketIllegalRegion", dataProviderClass = DataProvidedForPutBucket.class, groups = "exception")
	public void testPutBucketIllegalRegion(CreateBucketRequest createBucketRequest){
		if(client.doesBucketExist(createBucketRequest.getBucketName())){
			client.deleteBucket(createBucketRequest.getBucketName());
		}
		try {
			client.createBucket(createBucketRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchZone).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}		
	}
	
	/*@Test(dataProvider= "getPutBucketIllegalDedup", dataProviderClass = DataProvidedForPutBucket.class, groups = "exception")
	//@ExpectedExceptions ({AmazonServiceException.class})
	public void TestPutBucketIllegalDedup(CreateBucketRequest createBucketRequest){
		client.createBucket(createBucketRequest);
	}*/
	
	@Test(dataProvider= "getPutBucketButBucketOwnedByYou", dataProviderClass = DataProvidedForPutBucket.class, groups = "exception", dependsOnMethods = {"testPutBucketAllAndNormal"})
	public void testPutBucketButBucketOwnedByYou(CreateBucketRequest createBucketRequest){
		try {
			client.createBucket(createBucketRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.BucketAlreadyOwnedByYou).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}		
	}
	
	@Test(dataProvider= "getPutBucketButBucketOwnedByOthers", dataProviderClass = DataProvidedForPutBucket.class, groups = "exception")
	public void testPutBucketButBucketOwnedByOthers(CreateBucketRequest createBucketRequest){
		try {
			client2.createBucket(createBucketRequest);
			Assert.assertTrue(client2.doesBucketExist(createBucketRequest.getBucketName()));
			client.createBucket(createBucketRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.BucketAlreadyExist).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}		
	}
	
	@Test(dataProvider= "getPutBucketwithIllegalName", dataProviderClass = DataProvidedForPutBucket.class, groups = "exception")
	public void testPutBucketwithIllegalName(CreateBucketRequest createBucketRequest){
		try {
			client.createBucket(createBucketRequest);
			Assert.fail("Can't catch expected exception!");
		} catch (Exception e) {
			String errorCode = e.getMessage();
		    System.out.println(errorCode);
		}	
	}
	
	@Test(dataProvider = "getPutBatchBucket", dataProviderClass = DataProvidedForPutBucket.class, groups = "exception")
	public void testPutBucketBeyondItsUpperLimit(
		CreateBucketRequest createBucketRequest) {
		String bucketName = createBucketRequest.getBucketName();
		List<Bucket> buckets = client.listBuckets();
		int count = 100 - buckets.size();
		try {
			for (int i = 0; i < count; i++) {
				CreateBucketRequest request = new CreateBucketRequest(bucketName + i);
				client.createBucket(request);
			}
			//创建第101个桶
			CreateBucketRequest request = new CreateBucketRequest(bucketName + 100);
			client.createBucket(request);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.TooManyBuckets).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		} finally {
			for (int i = 0; i < count; i++) {
				Clear.clear(client, bucketName + i);
			}
		}
	}
		
	@AfterClass
	public void after(){
		Clear.clear(client, DataProvidedForPutBucket.bucketallandnormal);
		Clear.clear(client2, DataProvidedForPutBucket.bucketbyothers);
		Clear.clear(client, DataProvidedForPutBucket.buckets);
		Clear.clear(client, DataProvidedForPutBucket.bucketwithoutacl);
		Clear.clear(client, DataProvidedForPutBucket.bucketwithoutdedup);
		Clear.clear(client, DataProvidedForPutBucket.bucketwithoutregion);
	}
	
}