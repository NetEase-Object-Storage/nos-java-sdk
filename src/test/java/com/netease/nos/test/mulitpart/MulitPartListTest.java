package com.netease.nos.test.mulitpart;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.netease.cloud.ServiceException;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.InitiateMultipartUploadRequest;
import com.netease.cloud.services.nos.model.InitiateMultipartUploadResult;
import com.netease.cloud.services.nos.model.ListPartsRequest;
import com.netease.cloud.services.nos.model.PartListing;
import com.netease.cloud.services.nos.model.PartSummary;
import com.netease.cloud.services.nos.model.UploadPartRequest;
import com.netease.nos.test.bucket.BucketBasicTest;
import com.netease.nos.test.utils.Clear;
import com.netease.nos.test.utils.NOSConstant;
import com.netease.nos.test.utils.TestHostConfig;

public class MulitPartListTest {

	String bucketName = "bucketforabort"+TestHostConfig.region.toLowerCase();
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
	@AfterClass
	public void after(){
		Clear.clearMulitObject(client, bucketName);
	}

	@Test
	public void testIlleageList() {
		if (!client.doesBucketExist(bucketName)) {
			CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName, TestHostConfig.region);
			client.createBucket(createBucketRequest);
		}
		
		InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName,
				"objectKey1");
		InitiateMultipartUploadResult result = client.initiateMultipartUpload(initiateMultipartUploadRequest);
		Assert.assertEquals("objectKey1", result.getKey());
		String uploadId = result.getUploadId();
		
		client.uploadPart(new UploadPartRequest().withBucketName(bucketName).withUploadId(uploadId)
				.withInputStream(MulitPartInitiateTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")))
				.withKey("objectKey1").withUploadId(uploadId).withPartSize(5 * 1024 * 1024).withPartNumber(1));
		client.uploadPart(new UploadPartRequest().withBucketName(bucketName).withUploadId(uploadId)
				.withInputStream(MulitPartInitiateTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")))
				.withKey("objectKey1").withUploadId(uploadId).withPartSize(5 * 1024 * 1024).withPartNumber(2));
		client.uploadPart(new UploadPartRequest().withBucketName(bucketName).withUploadId(uploadId)
				.withInputStream(MulitPartInitiateTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")))
				.withKey("objectKey1").withUploadId(uploadId).withPartSize(5 * 1024 * 1024).withPartNumber(3));
		
		/** not init ObjectKey **/
		try{
			ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, "kjs", uploadId);
			client.listParts(listPartsRequest);
			Assert.fail("No expected error!");
		}catch(ServiceException e){
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.InvalidArgument).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
		
		/** not existed uploadId  **/
		try{
			ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, "objectKey1", "54645");
			client.listParts(listPartsRequest);
			Assert.fail("No expected error!");
		}catch(ServiceException e){
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchUpload).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
		
		/** part-number-marker **/
		try{
			ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, "objectKey1", uploadId);
			listPartsRequest.setPartNumberMarker(-1);
			client.listParts(listPartsRequest);
			Assert.fail("No expected error!");
		}catch(ServiceException e){
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.InvalidArgument).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
		
		/** max-parts **/
		try{
			ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, "objectKey1", uploadId);
			listPartsRequest.setMaxParts(-1);
			client.listParts(listPartsRequest);
			Assert.fail("No expected error!");
		}catch(ServiceException e){
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.InvalidArgument).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
		
		/**  max-parts larger than 1000 **/
		try{
			ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, "objectKey1", uploadId);
			listPartsRequest.setMaxParts(1001);
			client.listParts(listPartsRequest);
			Assert.fail("No expected error!");
		}catch(ServiceException e){
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.InvalidArgument).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
	}
	
	@Test
	public void testNormalListPart(){
		
		if (!client.doesBucketExist(bucketName)) {
			CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName, TestHostConfig.region);
			client.createBucket(createBucketRequest);
		}
		
		InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName,
				"objectKey1");
		InitiateMultipartUploadResult result = client.initiateMultipartUpload(initiateMultipartUploadRequest);
		Assert.assertEquals("objectKey1", result.getKey());
		String uploadId = result.getUploadId();
		ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, "objectKey1", uploadId);
		PartListing parts = client.listParts(listPartsRequest);
		//no part have uploaded  empty
		Assert.assertEquals(0, parts.getParts().size());
		
		client.uploadPart(new UploadPartRequest().withBucketName(bucketName).withUploadId(uploadId)
				.withInputStream(MulitPartInitiateTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")))
				.withKey("objectKey1").withUploadId(uploadId).withPartSize(5 * 1024 * 1024).withPartNumber(1));
		client.uploadPart(new UploadPartRequest().withBucketName(bucketName).withUploadId(uploadId)
				.withInputStream(MulitPartInitiateTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")))
				.withKey("objectKey1").withUploadId(uploadId).withPartSize(6 * 1024 * 1024).withPartNumber(2));
		client.uploadPart(new UploadPartRequest().withBucketName(bucketName).withUploadId(uploadId)
				.withInputStream(MulitPartInitiateTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")))
				.withKey("objectKey1").withUploadId(uploadId).withPartSize(6 * 1024 * 1024).withPartNumber(3));
		parts = client.listParts(listPartsRequest);
		Assert.assertEquals(bucketName, parts.getBucketName());
		Assert.assertEquals("objectKey1", parts.getKey());
		System.out.println(parts.getMaxParts());
		Assert.assertEquals(uploadId, parts.getUploadId());
		List<PartSummary>  sum = parts.getParts();
		Assert.assertEquals(3, sum.size());
		for(PartSummary part:sum){
			if(part.getPartNumber() == 1){
				System.out.println(part.getETag());
				Assert.assertEquals(5 * 1024 * 1024, part.getSize());
			}else{
				System.out.println(part.getETag());
				Assert.assertEquals(6 * 1024 * 1024, part.getSize());
			}			
		}	
	}	
}
