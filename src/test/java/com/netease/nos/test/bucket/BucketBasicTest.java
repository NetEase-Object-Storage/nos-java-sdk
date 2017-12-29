package com.netease.nos.test.bucket;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
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
import com.netease.cloud.services.nos.model.CannedAccessControlList;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.DeleteBucketRequest;
import com.netease.cloud.services.nos.model.DeleteObjectRequest;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.nos.test.utils.Clear;
import com.netease.nos.test.utils.NOSConstant;
import com.netease.nos.test.utils.TestHostConfig;

public class BucketBasicTest {

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
	
//	@Test(dataProvider= "putBucketbj", dataProviderClass = DataProvidedForBasic.class)
//	public void TestPutBucketbj(CreateBucketRequest createBucketRequest){
//		if(!client.doesBucketExist(createBucketRequest.getBucketName())){
//			Bucket bucket = client.createBucket(createBucketRequest);
//			Assert.assertNotNull(bucket);
//			Assert.assertEquals(bucket.getName(),createBucketRequest.getBucketName());
//			Assert.assertTrue(client.doesBucketExist(createBucketRequest.getBucketName()));
//		}
//		
//		System.out.println(client.listBuckets().size());
//		client.listObjects("bucketmaybehaveobject");
//		
//	}
	
	
	@Test(dataProvider= "putBucket", dataProviderClass = DataProvidedForBasic.class)
	public void testPutBucketAllAndNormal(CreateBucketRequest createBucketRequest){
		if(!client.doesBucketExist(createBucketRequest.getBucketName())){
			Bucket bucket = client.createBucket(createBucketRequest);
			Assert.assertNotNull(bucket);
			Assert.assertEquals(bucket.getName(),createBucketRequest.getBucketName());
			Assert.assertTrue(client.doesBucketExist(createBucketRequest.getBucketName()));
		}		
	}
	
	@Test(dataProvider= "deleteNotExistedBucket", dataProviderClass = DataProvidedForBasic.class)
	public void testDeleteNotExistedBucket(DeleteBucketRequest deleteBucketRequest){		
		try {
			client.deleteBucket(deleteBucketRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchBucket).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
	}
	
	@Test(dataProvider= "putObject", dataProviderClass = DataProvidedForBasic.class, dependsOnMethods ={"testPutBucketAllAndNormal"})
	public void testPutObject(PutObjectRequest putObjectRequest){
		try {
			client.putObject(putObjectRequest);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}		
	}
	
	@Test(dataProvider= "deleteNotEmptyBucket", dataProviderClass = DataProvidedForBasic.class, dependsOnMethods ={"testPutObject"})
	public void testDeleteNotEmptyBucket(DeleteBucketRequest deleteBucketRequest){
		try {
			client.deleteBucket(deleteBucketRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.BucketNotEmpty).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
	}
	
	@Test(dataProvider= "deleteObject", dataProviderClass = DataProvidedForBasic.class, dependsOnMethods ={"testDeleteNotEmptyBucket"})
	public void testDeleteObject(DeleteObjectRequest deleteObjectRequest){	
		try {
			client.deleteObject(deleteObjectRequest);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}	
	}
	
	@Test(dataProvider= "deleteBucket", dataProviderClass = DataProvidedForBasic.class, dependsOnMethods ={"testDeleteObject"})
	public void testDeleteBucket(DeleteBucketRequest deleteBucketRequest){	
		try {
			client.deleteBucket(deleteBucketRequest);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}	
	}
	
	@Test()
	public void testListBucket(){
		List<Bucket> buckets = client.listBuckets();
		Assert.assertNotNull(buckets);
	}
	
	@Test
	public void putBucket(){
		String bucketname = "bucketname"+new Date().getTime();
		CreateBucketRequest request = new CreateBucketRequest(bucketname);
		request.setCannedAcl("public-read");
		//request.setDeduplicate(true);
		client.createBucket(request);
		Assert.assertTrue(client.doesBucketExist(bucketname));
		CannedAccessControlList acl =client.getBucketAcl(bucketname);
		Assert.assertNotNull(acl);
		Assert.assertEquals(acl.toString(), "public-read");
		/*GetBucketDedupResult result = client.getBucketDedup(bucketname);
		Assert.assertNotNull(result);
		Assert.assertEquals(result.getStatus().toLowerCase(), "enabled");
		*/
		Assert.assertEquals(client.getBucketLocation(bucketname), "HZ");
		client.deleteBucket(bucketname);
		Assert.assertFalse(client.doesBucketExist(bucketname));
	}
	
	@AfterClass
	public void after(){
		Clear.clear(client, DataProvidedForBasic.bucketmaybehaveobject);
		Clear.clear(client, DataProvidedForBasic.bucketnotexisted);
		Clear.clear(client, DataProvidedForBasic.hihihi);
	}
}
