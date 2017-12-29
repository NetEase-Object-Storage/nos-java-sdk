package com.netease.nos.test.object;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.netease.cloud.auth.Credentials;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.Bucket;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.DeleteObjectsRequest;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.nos.test.bucket.BucketBasicTest;
import com.netease.nos.test.utils.TestHostConfig;

public class ObjectDeleteMulitTest {

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
		
		String bucketName = DataProvidedForDeleteMulitObject.bucketfortestdeletemulitobject;
		CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName,TestHostConfig.region);
		if (!client.doesBucketExist(createBucketRequest.getBucketName())) {
			Bucket bucket = client.createBucket(createBucketRequest);
			Assert.assertNotNull(bucket);
			Assert.assertEquals(bucket.getName(), createBucketRequest.getBucketName());
			Assert.assertTrue(client.doesBucketExist(createBucketRequest.getBucketName()));
		}
		
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, "objectkey1", new File("TestFile"+File.separator+"123.txt"));
		client.putObject(putObjectRequest);
		client.putObject(putObjectRequest);
		putObjectRequest.setKey("objectkey2");
		client.putObject(putObjectRequest);
	}
	@AfterClass
	public void after(){
		//Clear.clearAllVersions(client, DataProvidedForDeleteMulitObject.bucketfortestdeletemulitobject);
	}

	@Test(dataProvider = "deleteMulitObject", dataProviderClass = DataProvidedForDeleteMulitObject.class)
	public void testDeleteMulitObject(DeleteObjectsRequest deleteObjectsRequest) {
		deleteObjectsRequest.setQuiet(true);
		client.deleteObjects(deleteObjectsRequest);
		List<String> keys = deleteObjectsRequest.getKeys();
		for (String key : keys) {
			Assert.assertFalse(client.doesObjectExist(deleteObjectsRequest.getBucketName(), key));
		}
	}
	
    //删除历史版本
	/*@Test(dataProvider = "putObject", dataProviderClass = DataProvidedForDeleteMulitObject.class)
	public void testPutObjectVersionEnabled(PutObjectRequest putObjectRequest) {
		SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest = new SetBucketVersioningConfigurationRequest(
				putObjectRequest.getBucketName(), new BucketVersioningConfiguration(
						BucketVersioningConfiguration.ENABLED));
		client.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest);
		String versionStatus = client.getBucketVersioningConfiguration(putObjectRequest.getBucketName()).getStatus();
		Assert.assertTrue(versionStatus.equalsIgnoreCase(BucketVersioningConfiguration.ENABLED));
		
		client.putObject(putObjectRequest.withKey("key1"));
		client.putObject(putObjectRequest.withKey("key2"));
		//覆盖原来的key，产生历史版本
		client.putObject(putObjectRequest.withKey("key1"));
		client.putObject(putObjectRequest.withKey("key2"));
		List<KeyVersion> keyVersions = new ArrayList<KeyVersion>();
		
		VersionListing hisversions = client.listVersions(putObjectRequest.getBucketName());
		List<NOSVersionSummary> versionSummaries = hisversions.getVersionSummaries();
		for (NOSVersionSummary nosVersionSummary : versionSummaries) {
			keyVersions.add(new KeyVersion(nosVersionSummary.getKey(), nosVersionSummary.getVersionId()));
		}
		//带版本号的删除，最新对象依然存在
		DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(putObjectRequest.getBucketName());
		deleteObjectsRequest.setKeys(keyVersions);
		deleteObjectsRequest.setQuiet(true);
		client.deleteObjects(deleteObjectsRequest);	
		Assert.assertTrue(client.doesObjectExist(putObjectRequest.getBucketName(), "key1", null));
		Assert.assertTrue(client.doesObjectExist(putObjectRequest.getBucketName(), "key2", null));
	}*/
}
