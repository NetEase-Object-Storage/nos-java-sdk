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
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.Bucket;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.ListObjectsRequest;
import com.netease.cloud.services.nos.model.ObjectListing;
import com.netease.nos.test.bucket.BucketBasicTest;
import com.netease.nos.test.utils.NOSConstant;
import com.netease.nos.test.utils.TestHelper;
import com.netease.nos.test.utils.TestHostConfig;

public class ObjectListTest {

	Credentials credentials;
	NosClient client;
	
	private static final String credential = "credentials.properties";
	
	int objectCount = 1010;
	String[] objectNamePrefix = {"etc%admin%", "home#qa#"};
	String[] fileNames = {"TestFile"+File.separator+"123.txt"};
	String[] objectNames = null;
	
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

		CreateBucketRequest createBucketRequest = new CreateBucketRequest(
				DataProvidedForListObject.bucketfortestlistobject,TestHostConfig.region);
		if (!client.doesBucketExist(createBucketRequest.getBucketName())) {
			Bucket bucket = client.createBucket(createBucketRequest);
			Assert.assertNotNull(bucket);
			Assert.assertEquals(bucket.getName(), createBucketRequest.getBucketName());
			Assert.assertTrue(client.doesBucketExist(createBucketRequest.getBucketName()));
		}
		//向桶内上传1010个对象
		try {
		    objectNames = TestHelper.putObjects(client, DataProvidedForListObject.bucketfortestlistobject, 
		    		                            objectNamePrefix, fileNames, objectCount);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	@AfterClass
	public void after(){
		//Clear.clearAllVersions(client, DataProvidedForListObject.bucketfortestlistobject);
	}

	@Test(dataProvider= "listObjectDefault", dataProviderClass = DataProvidedForListObject.class)
	public void testListObjectDefault(ListObjectsRequest listObjectsRequest){
		ObjectListing objectListing = client.listObjects(listObjectsRequest);
		Assert.assertNotNull(objectListing);
		Assert.assertNotNull(objectListing.getObjectSummaries());
		Assert.assertEquals(objectListing.getObjectSummaries().size(), 1000);
		Assert.assertTrue(objectListing.isTruncated());
	}
	

	@Test(dataProvider= "listObjectSpecifiedPrefixNoResult", dataProviderClass = DataProvidedForListObject.class)
	public void testlistObjectSpecifiedPrefixNoResult(ListObjectsRequest listObjectsRequest){
		ObjectListing objectListing = client.listObjects(listObjectsRequest);
		Assert.assertNotNull(objectListing);
		Assert.assertNotNull(objectListing.getObjectSummaries());
		Assert.assertEquals(objectListing.getObjectSummaries().size(), 0);
		Assert.assertFalse(objectListing.isTruncated());
	}
	
	//符合条件的对象数目小于Maxkey，list出来的结果数=实际符合条件的对象数
	@Test(dataProvider= "listObjectSpecifiedPrefixSmallerMaxkey", dataProviderClass = DataProvidedForListObject.class)
	public void testListObjectSpecifiedPrefixSmallerThanMaxkey(ListObjectsRequest listObjectsRequest){
		ObjectListing objectListing = client.listObjects(listObjectsRequest);
		Assert.assertNotNull(objectListing);
		Assert.assertNotNull(objectListing.getObjectSummaries());
		int count = 0;
		for(int i = 0;i<objectNames.length;i++){
			if(objectNames[i].contains(listObjectsRequest.getPrefix())){
				count++;
			}
		}		
		Assert.assertEquals(objectListing.getObjectSummaries().size(), count);
		Assert.assertEquals(objectListing.getObjectSummaries().get(0).getBucketName(), listObjectsRequest.getBucketName());
		Assert.assertFalse(objectListing.isTruncated());
	}
	
	//符合条件的对象数目大于Maxkey，list出来的结果数=Maxkey
	@Test(dataProvider= "testListObjectLargerThanMaxkey", dataProviderClass = DataProvidedForListObject.class)
	public void testListObjectSpecifiedPrefixLargerThanMaxkey(ListObjectsRequest listObjectsRequest){
		ObjectListing objectListing = client.listObjects(listObjectsRequest);
		Assert.assertNotNull(objectListing);
		Assert.assertNotNull(objectListing.getObjectSummaries());
		Assert.assertEquals(objectListing.getObjectSummaries().size(), listObjectsRequest.getMaxKeys().intValue());
		Assert.assertEquals(objectListing.getObjectSummaries().get(0).getBucketName(), listObjectsRequest.getBucketName());
		Assert.assertTrue(objectListing.isTruncated());
	}
	
	@Test(dataProvider= "listObjectWrongMaxkey1", dataProviderClass = DataProvidedForListObject.class)
	public void testListObjectWrongMaxkey(ListObjectsRequest listObjectsRequest){
		try {
			client.listObjects(listObjectsRequest);
		} catch (ServiceException e) {
			if(listObjectsRequest.getMaxKeys()<0 || listObjectsRequest.getMaxKeys()>1000){
				String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.MalformedXML).getNosErrorCode();
				String errorCodeString = e.getErrorCode();
				Assert.assertEquals(errorCodeString, expectedErrorCode, "Failed to verifyERROR!");
			}else {
				Assert.fail("ListObject failed!");	
			}
		}
	}
	
	@Test(dataProvider= "listObjectSpecifiedMarkerNoResult", dataProviderClass = DataProvidedForListObject.class)
	public void testListObjectSpecifiedMarkerNoResult(ListObjectsRequest listObjectsRequest){
		ObjectListing objectListing = client.listObjects(listObjectsRequest);
		Assert.assertNotNull(objectListing);
		Assert.assertNotNull(objectListing.getObjectSummaries());
		Assert.assertEquals(objectListing.getObjectSummaries().size(), 0);
		Assert.assertFalse(objectListing.isTruncated());
	}
	
	
	@Test(dataProvider= "listObjectSpecifiedMarkersmallerMaxkey", dataProviderClass = DataProvidedForListObject.class)
	public void testListObjectSpecifiedMarkersmallerThanMaxkey(ListObjectsRequest listObjectsRequest, String resultContainString){
		ObjectListing objectListing = client.listObjects(listObjectsRequest);
		Assert.assertNotNull(objectListing);
		Assert.assertNotNull(objectListing.getObjectSummaries());
		int count = 0;
		for(int i = 0;i<objectNames.length;i++){
			if(objectNames[i].contains(resultContainString)){
				count++;
			}
		}	
		Assert.assertEquals(objectListing.getObjectSummaries().size(), count);
		Assert.assertEquals(objectListing.getObjectSummaries().get(0).getBucketName(), listObjectsRequest.getBucketName());
		Assert.assertFalse(objectListing.isTruncated());
	}
	
	@Test(dataProvider= "listObjectSpecifiedMarkerLargerMaxkey", dataProviderClass = DataProvidedForListObject.class)
	public void testListObjectSpecifiedMarkerLargerThanMaxkey(ListObjectsRequest listObjectsRequest, String resultContainString){
		ObjectListing objectListing = client.listObjects(listObjectsRequest);
		Assert.assertNotNull(objectListing);
		Assert.assertNotNull(objectListing.getObjectSummaries());
		Assert.assertEquals(objectListing.getObjectSummaries().size(), listObjectsRequest.getMaxKeys().intValue());
		Assert.assertEquals(objectListing.getObjectSummaries().get(0).getBucketName(), listObjectsRequest.getBucketName());
		Assert.assertTrue(objectListing.isTruncated());
	}
	

	@Test(dataProvider= "listObjectSpecifiedDelimiter", dataProviderClass = DataProvidedForListObject.class)
	public void TestListObjectSpecifiedDelimiter(ListObjectsRequest listObjectsRequest, String commonPrefixes){
		ObjectListing objectListing = client.listObjects(listObjectsRequest);
		Assert.assertNotNull(objectListing);
		Assert.assertNotNull(objectListing.getObjectSummaries());
		int count = 0;
		for(int i = 0;i<objectNames.length;i++){
			if(!objectNames[i].contains(listObjectsRequest.getDelimiter())){
				count++;
			}
		}	
		Assert.assertEquals(objectListing.getObjectSummaries().size(), count);
		Assert.assertEquals(objectListing.getObjectSummaries().get(0).getBucketName(), listObjectsRequest.getBucketName());
		Assert.assertEquals(objectListing.getCommonPrefixes().get(0), commonPrefixes);
	}
	
	
	@Test(dataProvider= "listObjectSpecifiedDelimiterAndWrongPrefix", dataProviderClass = DataProvidedForListObject.class)
	public void testListObjectSpecifiedDelimiterAndWrongPrefix(ListObjectsRequest listObjectsRequest){
		ObjectListing objectListing = client.listObjects(listObjectsRequest);
		Assert.assertNotNull(objectListing);
		Assert.assertNotNull(objectListing.getObjectSummaries());
		Assert.assertEquals(objectListing.getObjectSummaries().size(), 0);
		Assert.assertFalse(objectListing.isTruncated());
		Assert.assertEquals(objectListing.getCommonPrefixes().size(), 0);
	}
	

	@Test(dataProvider= "listObjectSpecifiedDelimiterAndRightPrefix", dataProviderClass = DataProvidedForListObject.class)
	public void testListObjectSpecifiedDelimiterAndRightPrefix(ListObjectsRequest listObjectsRequest, String commonPrefixes){
		ObjectListing objectListing = client.listObjects(listObjectsRequest);
		Assert.assertNotNull(objectListing);
		Assert.assertNotNull(objectListing.getObjectSummaries());
		Assert.assertEquals(objectListing.getObjectSummaries().size(), 0);
		Assert.assertFalse(objectListing.isTruncated());
		Assert.assertEquals(objectListing.getCommonPrefixes().get(0), commonPrefixes);
	}
	
	
	@Test(dataProvider= "listObjectSpecifiedWrongDelimiter", dataProviderClass = DataProvidedForListObject.class)
	public void TestListObjectSpecifiedWrongDelimiter(ListObjectsRequest listObjectsRequest){
		ObjectListing objectListing = client.listObjects(listObjectsRequest);
		Assert.assertNotNull(objectListing);
		Assert.assertNotNull(objectListing.getObjectSummaries());
		Assert.assertEquals(objectListing.getObjectSummaries().size(), 1000);
		Assert.assertTrue(objectListing.isTruncated());
		Assert.assertEquals(objectListing.getCommonPrefixes().size(), 0);
	}
}
