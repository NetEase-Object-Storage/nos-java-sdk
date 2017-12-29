package com.netease.nos.test.mulitpart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
import com.netease.cloud.services.nos.model.CompleteMultipartUploadRequest;
import com.netease.cloud.services.nos.model.CompleteMultipartUploadResult;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.InitiateMultipartUploadRequest;
import com.netease.cloud.services.nos.model.InitiateMultipartUploadResult;
import com.netease.cloud.services.nos.model.ListPartsRequest;
import com.netease.cloud.services.nos.model.PartETag;
import com.netease.cloud.services.nos.model.PartListing;
import com.netease.cloud.services.nos.model.PartSummary;
import com.netease.cloud.services.nos.model.UploadPartRequest;
import com.netease.nos.test.bucket.BucketBasicTest;
import com.netease.nos.test.utils.Clear;
import com.netease.nos.test.utils.NOSConstant;
import com.netease.nos.test.utils.TestHostConfig;

public class MulitPartCompleteTest {

	String bucketName = "bucketforcomplete"+TestHostConfig.region.toLowerCase();
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
	public void testIlleageComplete() {
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
				.withInputStream(MulitPartCompleteTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")))
				.withKey("objectKey1").withUploadId(uploadId).withPartSize(5 * 1024 * 1024).withPartNumber(1));
		
		ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, "objectKey1", uploadId);
		PartListing Parts = client.listParts(listPartsRequest);
		List<PartETag> partETags = new ArrayList<PartETag>();
		partETags.add(new PartETag(1, Parts.getParts().get(0).getETag()));
		
		/** not init ObjectKey **/
		try{
			
			CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName, "obj", uploadId, partETags);
			client.completeMultipartUpload(completeMultipartUploadRequest);
			Assert.fail("No expected error!");
		}catch(ServiceException e){
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.InvalidArgument).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
		
		/** bucketName not existed **/
		try{
			CompleteMultipartUploadRequest completeMultipartUploadRequest1 = new CompleteMultipartUploadRequest("bucknotexisted", result.getKey(), uploadId, partETags);
			client.completeMultipartUpload(completeMultipartUploadRequest1);
			Assert.fail("No expected error!");
		}catch(ServiceException e){
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchBucket).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
		
		/** x-nos-object-md5  **/
		try{
			CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName, result.getKey(), uploadId, partETags);
			completeMultipartUploadRequest.setxNosObjectMD5("kjhfdjkshfvsd45s4vf6s6");
			client.completeMultipartUpload(completeMultipartUploadRequest);
			Assert.fail("No expected error!");
		}catch(ServiceException e){
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.InvalidDigest).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}

	}

	@Test
	public void testNormalUpload() {

		if (!client.doesBucketExist(bucketName)) {
			CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName, TestHostConfig.region);
			client.createBucket(createBucketRequest);
		}

		InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName,
				"objectKey6");
		InitiateMultipartUploadResult result = client.initiateMultipartUpload(initiateMultipartUploadRequest);
		Assert.assertEquals("objectKey6", result.getKey());
		String uploadId = result.getUploadId();

		client.uploadPart(new UploadPartRequest().withBucketName(bucketName)
				.withInputStream(MulitPartCompleteTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")))
				.withKey("objectKey6").withUploadId(uploadId).withPartNumber(1).withPartSize(5 * 1024 * 1024));

		client.uploadPart(new UploadPartRequest().withBucketName(bucketName)
				.withInputStream(MulitPartCompleteTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")))
				.withKey("objectKey6").withUploadId(uploadId).withPartNumber(2).withPartSize(6 * 1024 * 1024));
		
		ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, "objectKey6", uploadId);
		PartListing Parts = client.listParts(listPartsRequest);
		Assert.assertEquals(2, Parts.getParts().size());
		
		//complete number 2
		List<PartETag> partETags = new ArrayList<PartETag>();
		for(PartSummary par : Parts.getParts()){
			partETags.add(new PartETag(par.getPartNumber(), par.getETag()));
		}
		
		CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName, "objectKey6", uploadId, partETags);
		CompleteMultipartUploadResult completeResult = client.completeMultipartUpload(completeMultipartUploadRequest);
		Assert.assertEquals("objectKey6", completeResult.getKey());
		Assert.assertEquals(bucketName, completeResult.getBucketName());
	}
	
	public void readInputStream(InputStream in, File file) {
		try {
			
			OutputStream writer = new FileOutputStream(file);
			int  by ;
			while ((by = in.read()) != -1) {
				writer.write(by);
			}
			in.close();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
