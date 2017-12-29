package com.netease.nos.test.mulitpart;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import com.netease.cloud.services.nos.model.CompleteMultipartUploadRequest;
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

public class MulitPartUploadTest {
	
	String bucketName = "bucketforupload"+TestHostConfig.region.toLowerCase();
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
		
		if (!client.doesBucketExist(bucketName)) {
			CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName, TestHostConfig.region);
			client.createBucket(createBucketRequest);
		}
	}
	@AfterClass
	public void after(){
		Clear.clearMulitObject(client, bucketName);
		Clear.clearMulitObject(client, "bucketfortestdeletemulitobject2");
	}


	@Test
	public void testIllegalUpload() {

		InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName,
				"objectKey1");
		InitiateMultipartUploadResult result = client.initiateMultipartUpload(initiateMultipartUploadRequest);
		Assert.assertEquals("objectKey1", result.getKey());
		String uploadId = result.getUploadId();

		/** not init ObjectKey **/
		try {
			UploadPartRequest uploadPartRequest = new UploadPartRequest();
			uploadPartRequest.setBucketName(bucketName);
			uploadPartRequest.setKey("hshsj");
			uploadPartRequest.setUploadId(uploadId);
			uploadPartRequest.withInputStream(MulitPartUploadTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")));
			uploadPartRequest.setPartNumber(1);
			uploadPartRequest.setPartSize(5 * 1024 * 1024);
			client.uploadPart(uploadPartRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.InvalidArgument).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}

		/** not init uploadId **/
		try {
			UploadPartRequest uploadPartRequest = new UploadPartRequest();
			uploadPartRequest.setBucketName(bucketName);
			uploadPartRequest.setKey("objectKey1");
			uploadPartRequest.setUploadId("jhfs56gd5fge4g6");
			uploadPartRequest.withInputStream(MulitPartUploadTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")));
			uploadPartRequest.setPartNumber(1);
			uploadPartRequest.setPartSize(5 * 1024 * 1024);
			client.uploadPart(uploadPartRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.InvalidArgument).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}

		/** bucketname not existed **/
		try {
			UploadPartRequest uploadPartRequest = new UploadPartRequest();
			uploadPartRequest.setBucketName("bbbb");
			uploadPartRequest.setKey("objectKey1");
			uploadPartRequest.setUploadId(uploadId);
			uploadPartRequest.withInputStream(MulitPartUploadTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")));
			uploadPartRequest.setPartNumber(1);
			uploadPartRequest.setPartSize(5 * 1024 * 1024);
			client.uploadPart(uploadPartRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchBucket).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}		
		
		/** partNumber illeage **/
		try {
			UploadPartRequest uploadPartRequest = new UploadPartRequest();
			uploadPartRequest.setBucketName(bucketName);
			uploadPartRequest.setKey("objectKey1");
			uploadPartRequest.setUploadId(uploadId);
			uploadPartRequest.withInputStream(MulitPartUploadTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")));
			uploadPartRequest.setPartNumber(0);
			uploadPartRequest.setPartSize(5 * 1024 * 1024);
			client.uploadPart(uploadPartRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.InvalidArgument).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
		
		/** content-MD5   **/
		try {
			UploadPartRequest uploadPartRequest = new UploadPartRequest();
			uploadPartRequest.setBucketName(bucketName);
			uploadPartRequest.setKey("objectKey1");
			uploadPartRequest.setUploadId(uploadId);
			uploadPartRequest.withInputStream(MulitPartUploadTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")));
			uploadPartRequest.setPartNumber(1);
			uploadPartRequest.setPartSize(5 * 1024 * 1024);
			uploadPartRequest.setMd5Digest("jhfdjkshfjssdf4s5s24g23");
			client.uploadPart(uploadPartRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.InvalidDigest).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
	}

	/** Size too small 
	   Multipart  Upload要求除最后一个Part以外，其他的Part大小都要大于5MB。但是Upload  Part
	     接口并不会立即校验上传Part的大小（因为不知道是否为最后一块）；只有当Complete Multipart 
	   Upload的时候才会校验。**/
	@Test
	public void testPartSizeTooSmall()
	{
		String ObjName = "objectKeyforSizeSmallTest";
		InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName,
				ObjName);
		InitiateMultipartUploadResult result = client.initiateMultipartUpload(initiateMultipartUploadRequest);
		Assert.assertEquals(ObjName, result.getKey());
		String uploadId = result.getUploadId();
		try {
			UploadPartRequest uploadPartRequest = new UploadPartRequest();
			uploadPartRequest.setBucketName(bucketName);
			uploadPartRequest.setKey(ObjName);
			uploadPartRequest.setUploadId(uploadId);
			uploadPartRequest.withInputStream(MulitPartUploadTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")));
			uploadPartRequest.setPartNumber(1);
			uploadPartRequest.setPartSize(4 * 1024 * 1024);
			client.uploadPart(uploadPartRequest);
			UploadPartRequest uploadPartRequest2 = new UploadPartRequest();
			uploadPartRequest2.setBucketName(bucketName);
			uploadPartRequest2.setKey(ObjName);
			uploadPartRequest2.setUploadId(uploadId);
			uploadPartRequest2.withInputStream(MulitPartUploadTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")));
			uploadPartRequest2.setPartNumber(2);
			uploadPartRequest2.setPartSize(4 * 1024 * 1024);
			client.uploadPart(uploadPartRequest2);
			
			ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, ObjName, uploadId);
			PartListing Parts = client.listParts(listPartsRequest);
			Assert.assertEquals(2, Parts.getParts().size());

			List<PartETag> partETags = new ArrayList<PartETag>();
			for(PartSummary par : Parts.getParts()){
				partETags.add(new PartETag(par.getPartNumber(), par.getETag()));
			}
			
			CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName, ObjName, uploadId, partETags);
			client.completeMultipartUpload(completeMultipartUploadRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.EntityTooSmall).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");			
		}finally{
			AbortMultipartUploadRequest request = new AbortMultipartUploadRequest(bucketName, ObjName, uploadId);
			client.abortMultipartUpload(request);
		}
	}
	
	/** Size too Large 
	   Multipart  Upload要求最大为100M。
	**/
	@Test
	public void testPartSizeTooLarge()
	{
		String ObjName = "objectKeyforSizeLargeTest";
		InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName,
				ObjName);
		InitiateMultipartUploadResult result = client.initiateMultipartUpload(initiateMultipartUploadRequest);
		Assert.assertEquals(ObjName, result.getKey());
		String uploadId = result.getUploadId();
		try {
			UploadPartRequest uploadPartRequest = new UploadPartRequest();
			uploadPartRequest.setBucketName(bucketName);
			uploadPartRequest.setKey(ObjName);
			uploadPartRequest.setUploadId(uploadId);
			uploadPartRequest.withInputStream(MulitPartUploadTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")));
			uploadPartRequest.setPartNumber(1);
			uploadPartRequest.setPartSize(101 * 1024 * 1024);
			client.uploadPart(uploadPartRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.EntityTooLarge).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");			
		}finally{
			AbortMultipartUploadRequest request = new AbortMultipartUploadRequest(bucketName, ObjName, uploadId);
			client.abortMultipartUpload(request);
		}
	}

	@Test
	public void testNormalUpload() {

		InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName,
				"objectKey2");
		InitiateMultipartUploadResult result = client.initiateMultipartUpload(initiateMultipartUploadRequest);
		Assert.assertEquals("objectKey2", result.getKey());
		String uploadId = result.getUploadId();

		UploadPartRequest uploadPartRequest = new UploadPartRequest();
		uploadPartRequest.setBucketName(bucketName);
		uploadPartRequest.withInputStream(MulitPartUploadTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")));
		uploadPartRequest.setKey("objectKey2");
		uploadPartRequest.setUploadId(uploadId);
		uploadPartRequest.setPartNumber(1);
		uploadPartRequest.setPartSize(5 * 1024 * 1024);
		client.uploadPart(uploadPartRequest);

		ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, "objectKey2", uploadId);
		PartListing Parts = client.listParts(listPartsRequest);
		Assert.assertNotNull(Parts);
		Assert.assertEquals("objectKey2", Parts.getKey());
		Assert.assertEquals(bucketName, Parts.getBucketName());
		Assert.assertEquals(uploadId, Parts.getUploadId());
		Assert.assertNotNull(Parts.getParts());
		Assert.assertEquals(1, Parts.getParts().size());
		Assert.assertEquals(1, Parts.getParts().get(0).getPartNumber());
		Assert.assertNotNull(Parts.getParts().get(0).getLastModified());
		Assert.assertNotNull(Parts.getParts().get(0).getETag());
		Assert.assertEquals(5 * 1024 * 1024, Parts.getParts().get(0).getSize());
		
		AbortMultipartUploadRequest request = new AbortMultipartUploadRequest(bucketName, "objectKey2", uploadId);
		client.abortMultipartUpload(request);
	}

	// partNumber not by order
	@Test
	public void testPartNumDisorder() {

		InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName,
				"objectKey3");
		InitiateMultipartUploadResult result = client.initiateMultipartUpload(initiateMultipartUploadRequest);
		Assert.assertEquals("objectKey3", result.getKey());
		String uploadId = result.getUploadId();

		/** partNumber  **/
		client.uploadPart(new UploadPartRequest().withBucketName(bucketName).withUploadId(uploadId)
				.withInputStream(MulitPartUploadTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")))
				.withKey("objectKey3").withUploadId(uploadId).withPartSize(5 * 1024 * 1024).withPartNumber(2));

		client.uploadPart(new UploadPartRequest().withBucketName(bucketName).withUploadId(uploadId)
				.withInputStream(MulitPartUploadTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")))
				.withKey("objectKey3").withUploadId(uploadId).withPartSize(6 * 1024 * 1024).withPartNumber(1));
		client.uploadPart(new UploadPartRequest().withBucketName(bucketName).withUploadId(uploadId)
				.withInputStream(MulitPartUploadTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")))
				.withKey("objectKey3").withUploadId(uploadId).withPartSize(6 * 1024 * 1024).withPartNumber(3));
		ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, "objectKey3", uploadId);
		PartListing Parts = client.listParts(listPartsRequest);
		Assert.assertNotNull(Parts);
		Assert.assertEquals("objectKey3", Parts.getKey());
		Assert.assertEquals(bucketName, Parts.getBucketName());
		Assert.assertEquals(uploadId, Parts.getUploadId());
		List<PartETag> partETags = new ArrayList<PartETag>();
		for(PartSummary par : Parts.getParts()){
			partETags.add(new PartETag(par.getPartNumber(), par.getETag()));
		}
		Collections.shuffle(partETags);
		try {
			//partETags乱序后传入也不会出错，SDK内部会排序，这点与接口里的规则不一样
			CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(
					bucketName, "objectKey3", uploadId, partETags);
			client.completeMultipartUpload(completeMultipartUploadRequest);
		} catch (ServiceException e) {
			Assert.fail(e.getMessage());
			AbortMultipartUploadRequest request = new AbortMultipartUploadRequest(
					bucketName, "objectKey3", uploadId);
			client.abortMultipartUpload(request);
		} 
	}

	//partNumber dedicate
	//如果用同一个part号码，上传了
	//新的数据，那么NOS上已有的这个号码的Part数据将被覆盖
	@Test
	public void testPartNumReduplicate() {

		InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName,
				"objectKey4");
		InitiateMultipartUploadResult result = client.initiateMultipartUpload(initiateMultipartUploadRequest);
		Assert.assertEquals("objectKey4", result.getKey());
		String uploadId = result.getUploadId();


		client.uploadPart(new UploadPartRequest().withBucketName(bucketName).withUploadId(uploadId)
				.withInputStream(MulitPartUploadTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")))
				.withKey("objectKey4").withUploadId(uploadId).withPartSize(5 * 1024 * 1024).withPartNumber(1));

		client.uploadPart(new UploadPartRequest().withBucketName(bucketName).withUploadId(uploadId)
				.withInputStream(MulitPartUploadTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")))
				.withKey("objectKey4").withUploadId(uploadId).withPartSize(6 * 1024 * 1024).withPartNumber(1));

		
		ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, "objectKey4", uploadId);
		PartListing Parts = client.listParts(listPartsRequest);
		Assert.assertNotNull(Parts);
		Assert.assertEquals("objectKey4", Parts.getKey());
		Assert.assertEquals(bucketName, Parts.getBucketName());
		Assert.assertEquals(uploadId, Parts.getUploadId());
		Assert.assertNotNull(Parts.getParts());
		Assert.assertEquals(1, Parts.getParts().size());
		Assert.assertEquals(1, Parts.getParts().get(0).getPartNumber());
		Assert.assertNotNull(Parts.getParts().get(0).getLastModified());
		Assert.assertNotNull(Parts.getParts().get(0).getETag());
		Assert.assertEquals(6 * 1024 * 1024, Parts.getParts().get(0).getSize());
	}
	
}
