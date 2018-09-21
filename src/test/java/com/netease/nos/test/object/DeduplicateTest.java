package com.netease.nos.test.object;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.netease.cloud.ServiceException;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.internal.ServiceUtils;
import com.netease.cloud.services.nos.model.DeduplicateRequest;
import com.netease.cloud.services.nos.model.GetBucketDedupResult;
import com.netease.cloud.util.Md5Utils;
import com.netease.nos.test.bucket.BucketBasicTest;
import com.netease.nos.test.utils.Clear;
import com.netease.nos.test.utils.NOSConstant;
import com.netease.nos.test.utils.TestHostConfig;

public class DeduplicateTest {

	Credentials credentials;
	Credentials credentials2;
	NosClient client;
	NosClient client2;
	
	String bucketName = "deduplicatebucket"+TestHostConfig.region.toLowerCase();
	String key = "dedupobject";
	String key2 = "newobject";
	
	String bucketName2 = "deduplicatebucket2"+TestHostConfig.region.toLowerCase();
	
	String testFilePath = "TestFile/123.txt";
	String newTestFilePath = "TestFile/321.txt";
	
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
		if(!client.doesBucketExist(bucketName)){
			client.createBucket(bucketName, TestHostConfig.region, true);
		}else{
			client.setBucketDedup(bucketName, "Enabled");
		}
		client.putObject(bucketName, key, new File(testFilePath));
		
		String accessKey2 = properties.getProperty("accessKey2");
		String secretKey2 = properties.getProperty("secretKey2");
		credentials2 = new BasicCredentials(accessKey2, secretKey2);
		client2 = new NosClient(credentials2);
		if(!client2.doesBucketExist(bucketName2)){
			client2.createBucket(bucketName2, TestHostConfig.region, true);
		}else{
			client2.setBucketDedup(bucketName2, "Enabled");
		}
		client2.putObject(bucketName2, key, new File(testFilePath));
	}
	
	@Test
	public void testDeduplicate() throws Exception{
		FileInputStream fileInputStream = new FileInputStream(new File(testFilePath));
		byte[] md5Hash = ServiceUtils.computeMD5Hash(fileInputStream);
		String md5 = Md5Utils.getHex(md5Hash);
		DeduplicateRequest request = new DeduplicateRequest(bucketName, key);
		request.setMD5Digest(md5);		
		boolean isDedup = client.isDeduplicate(request);
		Assert.assertTrue(isDedup);
	}
	
	@Test
	public void testDeduplicateWithoutMD5()
	{
		DeduplicateRequest request = new DeduplicateRequest(bucketName, key);		
		try {
			client.isDeduplicate(request);
			Assert.fail("No expected exception!");
		} catch (IllegalArgumentException e) {
			String errorCode = e.getMessage();
			System.out.println(errorCode);
		}
	}
	
	@Test
	public void testDeduplicateNewFile() throws NoSuchAlgorithmException, IOException
	{
		FileInputStream fileInputStream = new FileInputStream(new File(newTestFilePath));
		byte[] md5Hash = ServiceUtils.computeMD5Hash(fileInputStream);
		String md5 = Md5Utils.getHex(md5Hash);
		DeduplicateRequest request = new DeduplicateRequest(bucketName, key2);
		request.setMD5Digest(md5);		
		boolean isDedup = client.isDeduplicate(request);
		Assert.assertFalse(isDedup);
	}
	/**
	 * 使用deduplication时，传入不一致的md5,
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	@Test
	public void testDeduplicateWithInconsistentMD5() throws NoSuchAlgorithmException, IOException
	{
		FileInputStream fileInputStream = new FileInputStream(new File(newTestFilePath));
		byte[] md5Hash = ServiceUtils.computeMD5Hash(fileInputStream);
		String md5 = Md5Utils.getHex(md5Hash);
		DeduplicateRequest request = new DeduplicateRequest(bucketName, key);
		request.setMD5Digest(md5);		
		boolean isDedup = client.isDeduplicate(request);
		Assert.assertFalse(isDedup);
	}
	
	@Test
	public void testDeduplicateWithInvalidMD5()
	{
		DeduplicateRequest request = new DeduplicateRequest(bucketName, key);
		request.setMD5Digest("非法MD5");
		try {
			client.isDeduplicate(request);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.InvalidDigest).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}		
	}
	
	@Test
	public void testDeduplicateBetweenBuckets() throws NoSuchAlgorithmException, IOException
	{
		FileInputStream fileInputStream = new FileInputStream(new File(testFilePath));
		byte[] md5Hash = ServiceUtils.computeMD5Hash(fileInputStream);
		String md5 = Md5Utils.getHex(md5Hash);
		DeduplicateRequest request = new DeduplicateRequest(bucketName2, key);
		request.setMD5Digest(md5);		
		try {
			client.isDeduplicate(request);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.AccessDenied).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}		
	}
	
	@Test
	public void testDeduplicateWithNoDeduplicatedBucket() throws NoSuchAlgorithmException, IOException
	{
		client.setBucketDedup(bucketName, "Suspended");
		GetBucketDedupResult result = client.getBucketDedup(bucketName);
		Assert.assertTrue(result.getStatus().equalsIgnoreCase("disabled"));
		FileInputStream fileInputStream = new FileInputStream(new File(testFilePath));
		byte[] md5Hash = ServiceUtils.computeMD5Hash(fileInputStream);
		String md5 = Md5Utils.getHex(md5Hash);
		DeduplicateRequest request = new DeduplicateRequest(bucketName, key);
		request.setMD5Digest(md5);		
		boolean isDedup = client.isDeduplicate(request);
		Assert.assertFalse(isDedup);
	}
	
	@AfterClass
	public void after(){
		Clear.clear(client, bucketName);
		Clear.clear(client2, bucketName2);
	}
}
