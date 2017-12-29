package com.netease.nos.test.mulitpart;

import java.io.File;
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
import com.netease.cloud.services.nos.model.InitiateMultipartUploadRequest;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.nos.test.bucket.BucketBasicTest;
import com.netease.nos.test.utils.Clear;
import com.netease.nos.test.utils.NOSConstant;
import com.netease.nos.test.utils.TestHostConfig;

public class MulitPartInitiateTest {

	Credentials credentials;
	NosClient client;
	
	private static final String credential = "credentials.properties";
	private static final String BUCKET_NAME = DataProvidedForInitiateMulitPart.bucketfortestmulitupload;
	private static final String filePath = "TestFile" + File.separator + "123.txt";
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
		
		CreateBucketRequest createBucketRequest = new CreateBucketRequest(BUCKET_NAME,TestHostConfig.region);
		if (!client.doesBucketExist(createBucketRequest.getBucketName())) {
			Bucket bucket = client.createBucket(createBucketRequest);
			Assert.assertNotNull(bucket);
			Assert.assertEquals(bucket.getName(), createBucketRequest.getBucketName());
			Assert.assertTrue(client.doesBucketExist(createBucketRequest.getBucketName()));
		}
		PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, 
                                        DataProvidedForInitiateMulitPart.objectname, new File(filePath));
		if (!client.doesObjectExist(putObjectRequest.getBucketName(), putObjectRequest.getKey())) {
			client.putObject(putObjectRequest);
}
	}
	@AfterClass
	public void after(){
		Clear.clearMulitObject(client, BUCKET_NAME);
	}
	
	@Test(dataProvider = "initiateNotExistedBucket", dataProviderClass = DataProvidedForInitiateMulitPart.class)
	public void testInitiateNotExistedBucket(InitiateMultipartUploadRequest initiateMultipartUploadRequest) {
		try {
			client.initiateMultipartUpload(initiateMultipartUploadRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchBucket).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
	}

	/** normal  **/
	@Test(dataProvider = "initiateNormal", dataProviderClass = DataProvidedForInitiateMulitPart.class)
	public void testInitiateNormal(InitiateMultipartUploadRequest initiateMultipartUploadRequest) {
		String uploadid = client.initiateMultipartUpload(initiateMultipartUploadRequest).getUploadId();
		Assert.assertNotNull(uploadid);
	}

	/** object  existed
	   同Object能进行多次初始化，得到多个UploadId，后完成的多块上传将覆盖先完成的Object
        （如果开启版本号，先完成的多块上传将入历史版本）。 **/
	@Test(dataProvider = "initiateObjectHasExisted", dataProviderClass = DataProvidedForInitiateMulitPart.class)
	public void testInitiateObjectHasExisted(InitiateMultipartUploadRequest initiateMultipartUploadRequest) {
		String uploadid1 = client.initiateMultipartUpload(initiateMultipartUploadRequest).getUploadId();
		String uploadid2 = client.initiateMultipartUpload(initiateMultipartUploadRequest).getUploadId();
		Assert.assertNotEquals(uploadid1, uploadid2);
	}
	/**
	 * Object名最大长度为1000
	 * @param initiateMultipartUploadRequest
	 */
	@Test(dataProvider = "initiateWithObjNameToLong", dataProviderClass = DataProvidedForInitiateMulitPart.class)
	public void testInitiateWithObjNameToLong(InitiateMultipartUploadRequest initiateMultipartUploadRequest) {
		try {
			client.initiateMultipartUpload(initiateMultipartUploadRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.InvalidArgument).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
	}

}
