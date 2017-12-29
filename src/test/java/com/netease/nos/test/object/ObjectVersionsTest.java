package com.netease.nos.test.object;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.Bucket;
import com.netease.nos.test.bucket.BucketBasicTest;
import com.netease.nos.test.utils.TestHostConfig;

public class ObjectVersionsTest {

	Credentials credentials;
	NosClient client;
	
	private static final String credential = "credentials.properties";
	private static final String BUCKET_NAME = "bucketforobjectversions"+TestHostConfig.region.toLowerCase();
	
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
		//创建桶
		if (!client.doesBucketExist(BUCKET_NAME)) {
			Bucket bucket = client.createBucket(BUCKET_NAME);
			Assert.assertNotNull(bucket);
			Assert.assertEquals(bucket.getName(),BUCKET_NAME);
			Assert.assertTrue(client.doesBucketExist(BUCKET_NAME));
		}
		
	}
	
	@AfterClass
	public void after(){
		//Clear.clearAllVersions(client, DataProvidedForObjectVersions.bucketfortestobjectversions);
	}

	//桶关闭历史版本且对象不存在历史版本，调用getObjectVersions抛出异常
	/*@Test(dataProvider = "testGetObjectVersionsWithDisabledVersion", dataProviderClass = DataProvidedForObjectVersions.class)
	public void testGetObjectVersionsWithDisabledVersion(PutObjectRequest putObjectRequest) {
	
		SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest = new SetBucketVersioningConfigurationRequest(
				                              BUCKET_NAME, new BucketVersioningConfiguration(BucketVersioningConfiguration.SUSPENDED));
		client.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest);
		String statusNow = client.getBucketVersioningConfiguration(BUCKET_NAME).getStatus();
		Assert.assertEquals(statusNow, BucketVersioningConfiguration.SUSPENDED);

		/** upload a object **/
		/*putObjectRequest.setBucketName(BUCKET_NAME);
		client.putObject(putObjectRequest);*/

		/** get the meatadata of this object **/
		/*GetObjectMetadataRequest getObjectMetadataRequest = new GetObjectMetadataRequest(BUCKET_NAME, putObjectRequest.getKey());
		client.getObjectMetadata(getObjectMetadataRequest);*/

		/** upload another two object **/
		/*//版本未开启，因此直接覆盖，且不产生历史版本
		client.putObject(putObjectRequest);
		client.putObject(putObjectRequest);
		try {
			//桶关闭历史版本且对象不存在历史版本，调用getObjectVersions抛出异常，提示NoSuchKey(其实不是很合理，不能和传入Key不存在的情况区分开来)
			GetObjectVersionsRequest getObjectVersionsRequest = new GetObjectVersionsRequest(BUCKET_NAME,putObjectRequest.getKey());
			client.getObjectVersions(getObjectVersionsRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchKey).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
		
	}*/

	/*@Test(dataProvider = "testGetObjectVersionsWithEnbledVersion", dataProviderClass = DataProvidedForObjectVersions.class)
	public void testGetObjectVersionsWithEnbledVersion(PutObjectRequest putObjectRequest) throws Exception {
		
		SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest = new SetBucketVersioningConfigurationRequest(
				                                    BUCKET_NAME, new BucketVersioningConfiguration(BucketVersioningConfiguration.ENABLED));
		client.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest);
		String statusNow = client.getBucketVersioningConfiguration(BUCKET_NAME).getStatus();
		Assert.assertEquals(statusNow, BucketVersioningConfiguration.ENABLED);
		putObjectRequest.setBucketName(BUCKET_NAME);
		client.putObject(putObjectRequest);
		client.putObject(putObjectRequest);
		
		GetObjectVersionsRequest getObjectVersionsRequest = new GetObjectVersionsRequest(BUCKET_NAME,putObjectRequest.getKey());
		GetObjectVersionsResult getObjectVersionsResult = client.getObjectVersions(getObjectVersionsRequest);
		List<NOSVersionSummary> versionSummaryList = getObjectVersionsResult.getVersionSummary();
		String expectedETag = null;
		//判断历史版本内容是否正确
        for (NOSVersionSummary nosVersionSummary : versionSummaryList) {
        	expectedETag = TestHelper.getMD5(putObjectRequest.getFile().getPath());
        	String retETagString = nosVersionSummary.getETag();
			Assert.assertEquals(retETagString, expectedETag); 
		}
        
		//关闭版本号前有历史版本未删除的桶，关闭版本号后仍然可以读到历史版本
		SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest1 = new SetBucketVersioningConfigurationRequest(
				BUCKET_NAME, new BucketVersioningConfiguration(BucketVersioningConfiguration.SUSPENDED));
		client.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest1);
		Assert.assertEquals(client.getBucketVersioningConfiguration(BUCKET_NAME).getStatus(), BucketVersioningConfiguration.SUSPENDED);
		try {
			GetObjectVersionsResult getObjectVersionsResult1 = getObjectVersionsResult = client.getObjectVersions(getObjectVersionsRequest);
			Assert.assertNotNull(getObjectVersionsResult1.getVersionSummary());
			versionSummaryList = getObjectVersionsResult1.getVersionSummary();
	        for (NOSVersionSummary nosVersionSummary : versionSummaryList) {
	        	expectedETag = TestHelper.getMD5(putObjectRequest.getFile().getPath());
	        	String retETagString = nosVersionSummary.getETag();
				Assert.assertEquals(retETagString, expectedETag); 
			}
		} catch (Exception e) {
			Assert.fail("GetObjectversions Failed!");
		}
	}*/

}
