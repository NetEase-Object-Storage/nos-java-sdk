package com.netease.nos.test.bucket;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import com.netease.cloud.services.nos.model.Bucket;
import com.netease.cloud.services.nos.model.BucketVersioningConfiguration;
import com.netease.cloud.services.nos.model.DeleteObjectsRequest;
import com.netease.cloud.services.nos.model.ListVersionsRequest;
import com.netease.cloud.services.nos.model.NOSObjectSummary;
import com.netease.cloud.services.nos.model.NOSVersionSummary;
import com.netease.cloud.services.nos.model.ObjectListing;
import com.netease.cloud.services.nos.model.SetBucketVersioningConfigurationRequest;
import com.netease.cloud.services.nos.model.VersionListing;
import com.netease.cloud.services.nos.model.DeleteObjectsRequest.KeyVersion;
import com.netease.nos.test.utils.Clear;
import com.netease.nos.test.utils.NOSConstant;
import com.netease.nos.test.utils.TestHelper;
import com.netease.nos.test.utils.TestHostConfig;

public class ListVersionsTest {
	Credentials credentials;
	NosClient client;
	
	private static final String credential = "credentials.properties";
	
	private static final String BUCKET_NAME = "bucketforlistversionstest";
	
	int objectCount = 1010;
	String[] objectNamePrefix = {"etc%admin%", "home#qa#"};
	String[] fileNames = {"TestFile"+File.separator+"123.txt"};
	String[] objectNames = null;
	
	/*@BeforeClass
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
		//创建桶
		Bucket bucket = client.createBucket(BUCKET_NAME);
		Assert.assertNotNull(bucket);
		Assert.assertEquals(bucket.getName(),BUCKET_NAME);
		Assert.assertTrue(client.doesBucketExist(BUCKET_NAME));
		//开启桶的版本号
		SetBucketVersioningConfigurationRequest request = new SetBucketVersioningConfigurationRequest(BUCKET_NAME, 
                                                                        new BucketVersioningConfiguration("enabled"));
		client.setBucketVersioningConfiguration(request);
		String versionStatus = client.getBucketVersioningConfiguration(BUCKET_NAME).getStatus();
		Assert.assertTrue(versionStatus.equalsIgnoreCase(BucketVersioningConfiguration.ENABLED));
		//向桶内上传对象，并删除，产生历史版本
		try {
		    objectNames = TestHelper.putObjects(client, BUCKET_NAME, objectNamePrefix, fileNames, objectCount);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		DeleteObjectsRequest  deleteObjectsRequest = new DeleteObjectsRequest(BUCKET_NAME);
		ObjectListing objectListing = client.listObjects(BUCKET_NAME);
		List<NOSObjectSummary> sum = objectListing.getObjectSummaries();
		List<KeyVersion> keys = new ArrayList<KeyVersion>();
		//1次最多只能删除1000个对象
		while(sum.size() != 0)
		{
			keys.clear();
			for (NOSObjectSummary obj : sum) {
				keys.add(new KeyVersion(obj.getKey()));
			}
			deleteObjectsRequest.setKeys(keys);
			client.deleteObjects(deleteObjectsRequest);
			objectListing = client.listObjects(BUCKET_NAME);
			sum = objectListing.getObjectSummaries();
		}
	}*/
	
	/*@Test
	public void testListVersionsbyBucketName(){
		try {
			VersionListing versions = client.listVersions(BUCKET_NAME);
			List<NOSVersionSummary> versionSummaries = versions.getVersionSummaries();
			//默认最大返回1000条
			if(objectCount<=1000){
				Assert.assertEquals(versionSummaries.size(), objectCount);
			}else{
				Assert.assertEquals(versionSummaries.size(), 1000);
			}			
			for (NOSVersionSummary nosVersionSummary : versionSummaries) {
				Assert.assertEquals(nosVersionSummary.getBucketName(),BUCKET_NAME);
			}
		} catch (ServiceException e) {
			Assert.fail("ListVersionsbyBucketName failed!");
		}
	}*/
	
	/**
	 * 为方便测试，此处要求keyMarker和maxKeys都不为空
	 * @param keyMarker
	 * @param versionIdMarker
	 * @param maxKeys
	 */
	/*@Test(dataProvider = "testListVersionsbyArguments", dataProviderClass = DataProvidedForVersion.class)
	public void testListVersionsbyArguments(String keyMarker, String versionIdMarker, Integer maxKeys){
		try {
			VersionListing versions = client.listVersions(BUCKET_NAME, keyMarker, versionIdMarker, maxKeys);
			List<NOSVersionSummary> versionSummaries = versions.getVersionSummaries();
			List<String> expectedObjectList = new ArrayList<String>();
			for(String key : objectNames){
				if(key.contains(keyMarker)){
					expectedObjectList.add(key);
				}
			}
			int expectedCount = maxKeys > expectedObjectList.size() ? expectedObjectList.size() : maxKeys;
			Assert.assertEquals(versionSummaries.size(), expectedCount);			
			for (NOSVersionSummary nosVersionSummary : versionSummaries) {
				Assert.assertEquals(nosVersionSummary.getBucketName(),BUCKET_NAME);
			}
		} catch (ServiceException e) {
			if(maxKeys<0 || maxKeys>1000)
			{
				String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.MalformedXML).getNosErrorCode();
				String errorCodeString = e.getErrorCode();
				Assert.assertEquals(errorCodeString, expectedErrorCode, "Failed to verifyERROR!");
			}else {
				Assert.fail("ListVersionsbyBucketName failed!");	
			}
		}
	}*/
	
	/**
	 * 为方便测试，此处要求keyMarker和maxKeys都不为空
	 * @param keyMarker
	 * @param versionIdMarker
	 * @param maxKeys
	 */
	/*@Test(dataProvider = "testListVersionsbyRequest", dataProviderClass = DataProvidedForVersion.class)
	public void testListVersionsbyRequest(ListVersionsRequest listVersionsRequest){
		listVersionsRequest.setBucketName(BUCKET_NAME);
		int maxKeys = listVersionsRequest.getMaxResults();
		try {
			VersionListing versions = client.listVersions(listVersionsRequest);
			List<NOSVersionSummary> versionSummaries = versions.getVersionSummaries();
			List<String> expectedObjectList = new ArrayList<String>();
			for(String key : objectNames){
				if(key.contains(listVersionsRequest.getKeyMarker())){
					expectedObjectList.add(key);
				}
			}
			int expectedCount = maxKeys > expectedObjectList.size() ? expectedObjectList.size() : maxKeys;
			Assert.assertEquals(versionSummaries.size(), expectedCount);			
			for (NOSVersionSummary nosVersionSummary : versionSummaries) {
				Assert.assertEquals(nosVersionSummary.getBucketName(),BUCKET_NAME);
			}
		} catch (ServiceException e) {
			if(maxKeys<0 || maxKeys>1000)
			{
				String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.MalformedXML).getNosErrorCode();
				String errorCodeString = e.getErrorCode();
				Assert.assertEquals(errorCodeString, expectedErrorCode, "Failed to verifyERROR!");
			}else {
				Assert.fail("ListVersionsbyBucketName failed!");	
			}
		}
	}
	
	@AfterClass
	public void after(){
		Clear.clearAllVersions(client, BUCKET_NAME);
		Clear.clear(client, BUCKET_NAME);
	}*/
}
