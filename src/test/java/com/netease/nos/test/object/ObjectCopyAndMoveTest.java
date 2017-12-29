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
import com.netease.cloud.services.nos.model.CannedAccessControlList;
import com.netease.cloud.services.nos.model.CopyObjectRequest;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.GetObjectMetadataRequest;
import com.netease.cloud.services.nos.model.MoveObjectRequest;
import com.netease.cloud.services.nos.model.ObjectMetadata;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.cloud.services.nos.model.SetBucketAclRequest;
import com.netease.nos.test.bucket.BucketBasicTest;
import com.netease.nos.test.utils.NOSConstant;
import com.netease.nos.test.utils.TestHostConfig;

public class ObjectCopyAndMoveTest {

	Credentials credentials;
	Credentials credentials2;
	NosClient client;
	NosClient client2;
	
	private static final String credential = "credentials.properties";
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
		/**put source bucket **/
		CreateBucketRequest createBucketRequest = new CreateBucketRequest(
				DataProvidedForCopyObject.sourcebucketfortestcopyobject,TestHostConfig.region);
		if (!client.doesBucketExist(createBucketRequest.getBucketName())) {
			Bucket bucket = client.createBucket(createBucketRequest);
			Assert.assertNotNull(bucket);
			Assert.assertEquals(bucket.getName(), createBucketRequest.getBucketName());
			Assert.assertTrue(client.doesBucketExist(createBucketRequest.getBucketName()));
		}
		/**put dest bucket **/
		createBucketRequest = new CreateBucketRequest(
				DataProvidedForCopyObject.descbucketfortestcopyobject,TestHostConfig.region);
		if (!client.doesBucketExist(createBucketRequest.getBucketName())) {
			Bucket bucket = client.createBucket(createBucketRequest);
			Assert.assertNotNull(bucket);
			Assert.assertEquals(bucket.getName(), createBucketRequest.getBucketName());
			Assert.assertTrue(client.doesBucketExist(createBucketRequest.getBucketName()));
		}
		/**put source object **/
		PutObjectRequest putObjectRequest = new PutObjectRequest(DataProvidedForCopyObject.sourcebucketfortestcopyobject, 
				                                     DataProvidedForCopyObject.sourceobjectkey, new File(filePath));
		if (!client.doesObjectExist(putObjectRequest.getBucketName(), putObjectRequest.getKey())) {
			client.putObject(putObjectRequest);
		}
		
		String accessKey2 = properties.getProperty("accessKey2");
		String secretKey2 = properties.getProperty("secretKey2");
		credentials2 = new BasicCredentials(accessKey2, secretKey2);
		client2 = new NosClient(credentials2);
		CreateBucketRequest createBucketRequest2 = new CreateBucketRequest(
				DataProvidedForCopyObject.othersbucketfortestcopyobject, TestHostConfig.region);
		if (!client2.doesBucketExist(createBucketRequest2.getBucketName())) {
			Bucket bucket = client2.createBucket(createBucketRequest2);
			Assert.assertNotNull(bucket);
			Assert.assertEquals(bucket.getName(), createBucketRequest2.getBucketName());
			Assert.assertTrue(client.doesBucketExist(createBucketRequest2.getBucketName()));
		}
		PutObjectRequest putObjectRequest2 = new PutObjectRequest(
				DataProvidedForCopyObject.othersbucketfortestcopyobject,
				DataProvidedForCopyObject.othersobjectkey, new File(filePath));
		if (!client2.doesObjectExist(putObjectRequest2.getBucketName(),putObjectRequest2.getKey())) {
			client2.putObject(putObjectRequest2);
		}
	}
	@AfterClass
	public void after(){
		//Clear.clearAllVersions(client, DataProvidedForCopyObject.sourcebucketfortestcopyobject);
		//Clear.clearAllVersions(client, DataProvidedForCopyObject.descbucketfortestcopyobject);
		//Clear.clearAllVersions(client2, DataProvidedForCopyObject.othersbucketfortestcopyobject);
	}
	
	@Test(dataProvider = "copyObjectNormal", dataProviderClass = DataProvidedForCopyObject.class)
	public void testCopyObjectNormal(CopyObjectRequest copyObjectRequest) {
		// sourcebucket object copy to desc bucket and get descobject
		client.copyObject(copyObjectRequest);
		Assert.assertTrue(client.doesObjectExist(copyObjectRequest.getDestinationBucketName(),
				copyObjectRequest.getDestinationKey()));
		GetObjectMetadataRequest getObjectMetadataRequest = new GetObjectMetadataRequest(
				copyObjectRequest.getDestinationBucketName(), copyObjectRequest.getDestinationKey());
		ObjectMetadata objectMetadata = client.getObjectMetadata(getObjectMetadataRequest);
		Assert.assertNotNull(objectMetadata);
		System.out.println(objectMetadata.getContentLength());
		System.out.println(objectMetadata.getContentType());
		System.out.println(objectMetadata.getRawMetadata().get("Content-Type"));
		System.out.println(objectMetadata.getRawMetadata().get("Content-Length"));

		// copy from and to same bucket
		client.copyObject(copyObjectRequest.getDestinationBucketName(), copyObjectRequest.getDestinationKey(),
				copyObjectRequest.getDestinationBucketName(), "objectcopyfromsamebucket");
		Assert.assertTrue(client.doesObjectExist(copyObjectRequest.getDestinationBucketName(), "objectcopyfromsamebucket"));

		// descbucket descobject move to sourcebucket get a new object
		// "moveobject"
		client.moveObject(copyObjectRequest.getDestinationBucketName(), copyObjectRequest.getDestinationKey(),
				copyObjectRequest.getSourceBucketName(), "moveobject");
		Assert.assertTrue(client.doesObjectExist(copyObjectRequest.getSourceBucketName(), "moveobject"));
		
		// move from and to same bucket
		client.moveObject(copyObjectRequest.getSourceBucketName(), copyObjectRequest.getSourceKey(),
				copyObjectRequest.getSourceBucketName(), "objectmovefromsamebucket");
		Assert.assertTrue(client.doesObjectExist(copyObjectRequest.getSourceBucketName(), "objectmovefromsamebucket"));
	}

	@Test(dataProvider = "copyObjectSourceBucketNotExisted", dataProviderClass = DataProvidedForCopyObject.class)
	public void testCopyObjectSourceBucketNotExisted(CopyObjectRequest copyObjectRequest) {
		// copy
		try {
			client.copyObject(copyObjectRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchBucket).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
		// move
		try {
			client.moveObject(new MoveObjectRequest(copyObjectRequest.getSourceBucketName(), copyObjectRequest
					.getSourceKey(), copyObjectRequest.getDestinationBucketName(), copyObjectRequest.getDestinationKey()));
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchBucket).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
	}

	/** source object **/
	@Test(dataProvider = "copyObjectSourceObjectNotExisted", dataProviderClass = DataProvidedForCopyObject.class)
	public void testCopyObjectSourceObjectNotExisted(CopyObjectRequest copyObjectRequest) {
		// copy
		try {
			client.copyObject(copyObjectRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchKey).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
		// move
		try {
			client.moveObject(new MoveObjectRequest(copyObjectRequest.getSourceBucketName(), copyObjectRequest
					.getSourceKey(), copyObjectRequest.getDestinationBucketName(), copyObjectRequest
					.getDestinationKey()));
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchKey).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
	}

	/** desc bucket **/
	@Test(dataProvider = "copyObjectDescBucketNotExisted", dataProviderClass = DataProvidedForCopyObject.class)
	public void testCopyObjectDescBucketNotExisted(CopyObjectRequest copyObjectRequest) {
		// copy
		try {
			client.copyObject(copyObjectRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchBucket).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
		// move
		try {
			client.moveObject(new MoveObjectRequest(copyObjectRequest.getSourceBucketName(), copyObjectRequest
					.getSourceKey(), copyObjectRequest.getDestinationBucketName(), copyObjectRequest
					.getDestinationKey()));
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchBucket).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
	}

	/** desc object **/
	@Test(dataProvider = "copyObjectDescObjectHaveExisted", dataProviderClass = DataProvidedForCopyObject.class)
	public void testCopyObjectDescObjectHaveExisted(CopyObjectRequest copyObjectRequest) {
		// copy
		try {
			client.copyObject(copyObjectRequest);
			client.copyObject(copyObjectRequest);
		} catch (ServiceException e) {
            Assert.fail(e.getMessage());
		}
		// move
		try {
			client.moveObject(new MoveObjectRequest(copyObjectRequest.getSourceBucketName(), copyObjectRequest
					.getSourceKey(), copyObjectRequest.getDestinationBucketName(), copyObjectRequest
					.getDestinationKey()));
		} catch (ServiceException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "testCopyObjectFromOthersPrivateBucket", dataProviderClass = DataProvidedForCopyObject.class)
	public void testCopyObjectFromOthersPrivateBucket(CopyObjectRequest copyObjectRequest) {
		SetBucketAclRequest setBucketAclRequest = new SetBucketAclRequest(copyObjectRequest.getSourceBucketName(), 
				                                                     CannedAccessControlList.Private);
		client2.setBucketAcl(setBucketAclRequest.getBucketName(), setBucketAclRequest.getCannedAcl());
		Assert.assertEquals(client2.getBucketAcl(setBucketAclRequest.getBucketName()), setBucketAclRequest.getCannedAcl());
		// copy
		try {
			client.copyObject(copyObjectRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.AccessDenied).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
		// move
		try {
			client.moveObject(new MoveObjectRequest(copyObjectRequest.getSourceBucketName(), copyObjectRequest
					.getSourceKey(), copyObjectRequest.getDestinationBucketName(), copyObjectRequest
					.getDestinationKey()));
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.AccessDenied).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
	}
	
	@Test(dataProvider = "testCopyObjectFromOthersPublicBucket", dataProviderClass = DataProvidedForCopyObject.class)
	public void testCopyObjectFromOthersPublicBucket(CopyObjectRequest copyObjectRequest) {
		SetBucketAclRequest setBucketAclRequest = new SetBucketAclRequest(copyObjectRequest.getSourceBucketName(),
				                                      CannedAccessControlList.PublicRead);
		client2.setBucketAcl(setBucketAclRequest.getBucketName(), setBucketAclRequest.getCannedAcl());
		Assert.assertEquals(
				client2.getBucketAcl(setBucketAclRequest.getBucketName()), setBucketAclRequest.getCannedAcl());
		// copy
		try {
			client.copyObject(copyObjectRequest);
			Assert.assertTrue(client.doesObjectExist(copyObjectRequest.getDestinationBucketName(),
					copyObjectRequest.getDestinationKey()));
		} catch (ServiceException e) {
			Assert.fail(e.getErrorCode());
		}
		// move
		try {
			client.moveObject(new MoveObjectRequest(copyObjectRequest.getSourceBucketName(), copyObjectRequest.getSourceKey(),
					copyObjectRequest.getDestinationBucketName(),copyObjectRequest.getDestinationKey()));
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.AccessDenied).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode,"Failed to verifyERROR!");
		}
	}
	
	@Test(dataProvider = "testCopyObjectToOthersBucket", dataProviderClass = DataProvidedForCopyObject.class)
	public void testCopyObjectToOthersBucket(CopyObjectRequest copyObjectRequest) {
		//其他人的桶不管公开读还是私有，都不能向里面拷贝对象
		for(int i = 0; i<2; i++)
		{
			if (i==0) {
				SetBucketAclRequest setBucketAclRequest = new SetBucketAclRequest(copyObjectRequest.getDestinationBucketName(),
						CannedAccessControlList.Private);
                client2.setBucketAcl(setBucketAclRequest.getBucketName(), setBucketAclRequest.getCannedAcl());
                Assert.assertEquals(client2.getBucketAcl(setBucketAclRequest.getBucketName()), setBucketAclRequest.getCannedAcl());
			}else{
				SetBucketAclRequest setBucketAclRequest = new SetBucketAclRequest(copyObjectRequest.getDestinationBucketName(),
                        CannedAccessControlList.PublicRead);
                client2.setBucketAcl(setBucketAclRequest.getBucketName(), setBucketAclRequest.getCannedAcl());
                Assert.assertEquals(client2.getBucketAcl(setBucketAclRequest.getBucketName()), setBucketAclRequest.getCannedAcl());
			}
			// copy
			try {
				client.copyObject(copyObjectRequest);
				Assert.fail("No expected error!");
			} catch (ServiceException e) {
				String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.AccessDenied).getNosErrorCode();
				String errorCode = e.getErrorCode();
				Assert.assertEquals(errorCode, expectedErrorCode,"Failed to verifyERROR!");
			}
			// move
			try {
				client.moveObject(new MoveObjectRequest(copyObjectRequest.getSourceBucketName(), copyObjectRequest.getSourceKey(),
						copyObjectRequest.getDestinationBucketName(),copyObjectRequest.getDestinationKey()));
				Assert.fail("No expected error!");
			} catch (ServiceException e) {
				String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.AccessDenied).getNosErrorCode();
				String errorCode = e.getErrorCode();
				Assert.assertEquals(errorCode, expectedErrorCode,"Failed to verifyERROR!");
			}
		}	
	}
}
