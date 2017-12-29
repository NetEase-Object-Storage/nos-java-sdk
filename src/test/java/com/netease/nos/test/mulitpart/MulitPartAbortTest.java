package com.netease.nos.test.mulitpart;

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
import com.netease.cloud.services.nos.model.AbortMultipartUploadRequest;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.InitiateMultipartUploadRequest;
import com.netease.cloud.services.nos.model.InitiateMultipartUploadResult;
import com.netease.cloud.services.nos.model.ListPartsRequest;
import com.netease.cloud.services.nos.model.PartListing;
import com.netease.cloud.services.nos.model.UploadPartRequest;
import com.netease.nos.test.bucket.BucketBasicTest;
import com.netease.nos.test.utils.Clear;
import com.netease.nos.test.utils.NOSConstant;
import com.netease.nos.test.utils.TestHostConfig;

public class MulitPartAbortTest {
	String bucketName = "bucketforabort"+TestHostConfig.region.toLowerCase();
	String objectKey1 = "objectKey1";
	String obj = "obg2";
	String objectKey2 = "objectKey2";
	
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
	public void testIlleageAbort() {
		if (!client.doesBucketExist(bucketName)) {
			CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName, TestHostConfig.region);
			client.createBucket(createBucketRequest);
		}
		
		InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName,
				objectKey1);
		InitiateMultipartUploadResult result = client.initiateMultipartUpload(initiateMultipartUploadRequest);
		Assert.assertEquals(objectKey1, result.getKey());
		String uploadId = result.getUploadId();
		
		client.uploadPart(new UploadPartRequest().withBucketName(bucketName).withUploadId(uploadId)
				.withInputStream(MulitPartAbortTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")))
				.withKey(objectKey1).withUploadId(uploadId).withPartSize(5*1024*1024).withPartNumber(1));
		
		/** not init ObjectKey **/
		try{
			AbortMultipartUploadRequest abortMultipartUploadRequest = new AbortMultipartUploadRequest(bucketName, obj, uploadId);
			client.abortMultipartUpload(abortMultipartUploadRequest);
			Assert.fail("No expected error!");
		}catch(ServiceException e){
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.InvalidArgument).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
		
		/** bucketName not existed **/
		try{
			AbortMultipartUploadRequest abortMultipartUploadRequest = new AbortMultipartUploadRequest("bucketnotexisted", objectKey1, uploadId);
			client.abortMultipartUpload(abortMultipartUploadRequest);
			Assert.fail("No expected error!");
		}catch(ServiceException e){
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchBucket).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
		
		/** uploadId not existed **/
		try{
			AbortMultipartUploadRequest abortMultipartUploadRequest = new AbortMultipartUploadRequest(bucketName, objectKey1, "84556546");
			client.abortMultipartUpload(abortMultipartUploadRequest);
			Assert.fail("No expected error!");
		}catch(ServiceException e){
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchUpload).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
		
		/** upload has been aborted **/
		try{
			AbortMultipartUploadRequest abortMultipartUploadRequest = new AbortMultipartUploadRequest(bucketName, objectKey1, uploadId);
			client.abortMultipartUpload(abortMultipartUploadRequest);
			client.abortMultipartUpload(abortMultipartUploadRequest);
			Assert.fail("No expected error!");
		}catch(ServiceException e){
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchUpload).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}

	}

	@Test
	public void testNormalAbort() {

		if (!client.doesBucketExist(bucketName)) {
			CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName, TestHostConfig.region);
			client.createBucket(createBucketRequest);
		}

		InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName,
				objectKey2);
		InitiateMultipartUploadResult result = client.initiateMultipartUpload(initiateMultipartUploadRequest);
		Assert.assertEquals(objectKey2, result.getKey());
		String uploadId = result.getUploadId();
		

		client.uploadPart(new UploadPartRequest().withBucketName(bucketName)
				.withInputStream(MulitPartAbortTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")))
				.withKey(objectKey2).withUploadId(uploadId).withPartNumber(1).withPartSize(5*1024*1024));
		
		ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, objectKey2, uploadId);
		PartListing Parts = client.listParts(listPartsRequest);
		Assert.assertEquals(1, Parts.getParts().size());
		
		AbortMultipartUploadRequest abortMultipartUploadRequest = new AbortMultipartUploadRequest(bucketName, objectKey2, uploadId);
		client.abortMultipartUpload(abortMultipartUploadRequest);
	
		try{
			Assert.assertEquals(0, client.listParts(new ListPartsRequest(bucketName, objectKey2, uploadId)).getParts().size());
		}catch(ServiceException e){
			///This UploadId has aborted
			e.getErrorCode();
		}
	}
	

}
