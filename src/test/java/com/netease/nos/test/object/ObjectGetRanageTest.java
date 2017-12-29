package com.netease.nos.test.object;

import java.io.File;
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
import com.netease.cloud.services.nos.model.GetObjectRequest;
import com.netease.cloud.services.nos.model.ObjectMetadata;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.nos.test.bucket.BucketBasicTest;
import com.netease.nos.test.utils.NOSConstant;
import com.netease.nos.test.utils.TestHelper;
import com.netease.nos.test.utils.TestHostConfig;

public class ObjectGetRanageTest {
	
	Credentials credentials;
	NosClient client;
	
	String bucketName = DataProvidedForGetObjectRange.bucketfortestgetobjectrange;
	String filePath = DataProvidedForGetObjectRange.filePath;
	String objectKey = DataProvidedForGetObjectRange.objKey;
	
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
		
		CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName,TestHostConfig.region);
		if (!client.doesBucketExist(createBucketRequest.getBucketName())) {
			Bucket bucket = client.createBucket(createBucketRequest);
			Assert.assertNotNull(bucket);
			Assert.assertEquals(bucket.getName(), createBucketRequest.getBucketName());
			Assert.assertTrue(client.doesBucketExist(createBucketRequest.getBucketName()));
		}
		
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectKey, new File(filePath));
        if (!client.doesObjectExist(putObjectRequest.getBucketName(), putObjectRequest.getKey())) {
            client.putObject(putObjectRequest);
        }		
	}
	@AfterClass
	public void after(){
		//Clear.clearAllVersions(client, bucketName);
	}	
	
	@Test(dataProvider = "getObject", dataProviderClass = DataProvidedForGetObjectRange.class)
	public void testGetObjectRange(GetObjectRequest request) throws Exception{
		String destFile = "TestFile/range1";
		client.getObject(request, new File(destFile));
		String actualMD5 = TestHelper.getMD5(destFile);
		//计算预期的MD5
		String expectedPartFile = "TestFile/temp";
		TestHelper.getPartFile(filePath, request.getRange()[0], request.getRange()[1], expectedPartFile);
		String expectedMD5 = TestHelper.getMD5(expectedPartFile);
		//删除临时文件
		File file = new File(expectedPartFile);    
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	    }  
	    file = new File(destFile);    
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	    }  
		Assert.assertEquals(actualMD5, expectedMD5);
	}
	
	@Test(dataProvider = "getObjectWithInvaildRange", dataProviderClass = DataProvidedForGetObjectRange.class)
	public void testGetObjectWithInvaildRange(GetObjectRequest request){
		try {
			client.getObject(request, new File("TestFile/range2"));
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.InvalidRange).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}		
	}
	/**
	 * Range结束位置越界没问题， 只返回文件的真实长度
	 * @param request
	 * @throws Exception 
	 */
	@Test(dataProvider = "getObjectWithLargerRange", dataProviderClass = DataProvidedForGetObjectRange.class)
	public void testGetObjectWithLargerRange(GetObjectRequest request) throws Exception{
		String destFile = "TestFile/range3";
		ObjectMetadata objmeta = client.getObject(request, new File(destFile));
		String actualMD5 = TestHelper.getMD5(destFile);
		//计算预期的MD5
		String expectedMD5 = TestHelper.getMD5(filePath);
		//删除临时文件
	    File file = new File(destFile);    
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	    }  
		Assert.assertEquals(actualMD5, expectedMD5);
		System.out.println(objmeta.getETag());
	}
	
	@Test(dataProvider = "getObject2", dataProviderClass = DataProvidedForGetObjectRange.class/*, dependsOnMethods={"testPutObject"}*/)
	public void testGetObjectRange2(GetObjectRequest request){
		ObjectMetadata objmeta = client.getObject(request, new File("TestFile/range3"));
		System.out.println(objmeta.getContentLength());
		System.out.println(objmeta.getContentMD5());
		System.out.println(objmeta.getETag());
	}
	
	@Test(dataProvider = "getObject3", dataProviderClass = DataProvidedForGetObjectRange.class/*, dependsOnMethods={"testPutObject"}*/)
	public void testGetObjectRange3(GetObjectRequest request){
		try{
			client.getObject(request, new File("TestFile/range4"));
		}catch(Exception e){
		}
		
	}
}
