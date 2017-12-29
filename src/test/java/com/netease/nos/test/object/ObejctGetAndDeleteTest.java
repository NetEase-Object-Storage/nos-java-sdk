package com.netease.nos.test.object;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ExpectedExceptions;
import org.testng.annotations.Test;

import com.netease.cloud.ServiceException;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.Bucket;
import com.netease.cloud.services.nos.model.CannedAccessControlList;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.DeleteObjectRequest;
import com.netease.cloud.services.nos.model.GetObjectRequest;
import com.netease.cloud.services.nos.model.NOSObject;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.cloud.services.nos.model.SetBucketAclRequest;
import com.netease.nos.test.bucket.BucketBasicTest;
import com.netease.nos.test.utils.Clear;
import com.netease.nos.test.utils.NOSConstant;
import com.netease.nos.test.utils.TestHostConfig;

@SuppressWarnings("deprecation")
public class ObejctGetAndDeleteTest {

	Credentials credentials;
	Credentials credentials2;
	NosClient client;
	NosClient client2;
	
	private static final String credential = "credentials.properties";
	private static final String BUCKET_NAME = DataProvidedForGetObject.bucketfortestgetobject;
	private static final String OTHERS_BUCKET = DataProvidedForGetObject.bucketownedbyothers;
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
		PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME,DataProvidedForGetObject.objectKey, 
				                                                 new File(filePath));
		if (!client.doesObjectExist(putObjectRequest.getBucketName(),putObjectRequest.getKey())) {
			client.putObject(putObjectRequest);
		}
		
		String accessKey2 = properties.getProperty("accessKey2");
		String secretKey2 = properties.getProperty("secretKey2");
		credentials2 = new BasicCredentials(accessKey2, secretKey2);
		client2 = new NosClient(credentials2);
		CreateBucketRequest createBucketRequest2 = new CreateBucketRequest(OTHERS_BUCKET, TestHostConfig.region);
		if (!client2.doesBucketExist(createBucketRequest2.getBucketName())) {
			Bucket bucket = client2.createBucket(createBucketRequest2);
			Assert.assertNotNull(bucket);
			Assert.assertEquals(bucket.getName(), createBucketRequest2.getBucketName());
			Assert.assertTrue(client.doesBucketExist(createBucketRequest2.getBucketName()));
		}
		PutObjectRequest putObjectRequest2 = new PutObjectRequest(OTHERS_BUCKET, 
				                                         DataProvidedForGetObject.othersobjectKey, new File(filePath));
		if (!client2.doesObjectExist(putObjectRequest2.getBucketName(),putObjectRequest2.getKey())) {
			client2.putObject(putObjectRequest2);
		}
	}
	@AfterClass
	public void after(){
		Clear.clear(client, BUCKET_NAME);
		Clear.clear(client2, OTHERS_BUCKET);
	}


	/*** getobject bucket not existed */
	@Test(dataProvider = "getObjectButBucketNotExisted", dataProviderClass = DataProvidedForGetObject.class)
	public void testGetObjectButBucketNotExisted(GetObjectRequest getObjectRequest) {
		try {
			client.getObject(getObjectRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchBucket).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
	}

	/*** getobject object not existed */
	@Test(dataProvider = "getObjectButObjectNotExisted", dataProviderClass = DataProvidedForGetObject.class)
	public void testGetObjectButObjectNotExisted(GetObjectRequest getObjectRequest) {
		try {
			client.getObject(getObjectRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchKey).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
	}

	/*** deleteobject  */
	@Test(dataProvider = "deleteObjectBucketNotExist", dataProviderClass = DataProvidedForGetObject.class)
	public void testDeleteObjectButBucketNotExisted(DeleteObjectRequest deleteObjectRequest) {
		client.deleteObject(deleteObjectRequest);
	}

	/*** getobject object涓嶅瓨鍦�*/
	@Test(dataProvider = "getObjectObjectNotExisted", dataProviderClass = DataProvidedForGetObject.class/*, dependsOnMethods ={"testPutBucket"}*/)
	@ExpectedExceptions({ ServiceException.class })
	public void testGetObjectButObjectNotExisted(DeleteObjectRequest deleteObjectRequest) {
		client.deleteObject(deleteObjectRequest);
	}

	/** 淇敼鏂囦欢涓夋锛屽苟浠ュ悓鍚峅bjectName涓婁紶, bucket 鏈紑鍚痸ersion **/
	@Test(dataProvider = "putObject", dataProviderClass = DataProvidedForGetObject.class/*, dependsOnMethods ={"testPutBucket"}*/)
	public void testPutObjectDisabledVersionStatus(PutObjectRequest putObjectRequest) {
		updateFile(putObjectRequest.getFile(), "TestFile/123.txt");
		client.putObject(putObjectRequest);
		updateFile(putObjectRequest.getFile(), "TestFile/1234.txt");
		//String versionid = client.putObject(putObjectRequest).getVersionId();

		/** 鏈缃増鏈俊鎭�versionId=null 鍒犻櫎鏈�柊鐗堟湰 **/
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(putObjectRequest.getBucketName(),
				putObjectRequest.getKey());
		client.deleteObject(deleteObjectRequest);

		//Assert.assertNull(versionid);
		Assert.assertFalse(client.doesObjectExist(putObjectRequest.getBucketName(), putObjectRequest.getKey()));

		updateFile(putObjectRequest.getFile(), "TestFile/12345.txt");
		client.putObject(putObjectRequest);
	}

	/** 閭ｄ箞涓嬭浇鐨刼bject灏辨槸鏈�柊鐗堟湰锛孌:/disabled.txt鏂囦欢鍖呭惈"鏈夋垜灏辨槸鏈�柊鐨� **/
	@Test(dataProvider = "getObject", dataProviderClass = DataProvidedForGetObject.class, dependsOnMethods = { "testPutObjectDisabledVersionStatus" })
	public void testGetObjectDisabledVersionStatus(GetObjectRequest getObjectRequest) {

		/** bucket 榛樿鐗堟湰鐘舵�涓篋isabled **/
		NOSObject NOSObject = client.getObject(getObjectRequest);
		InputStream in = NOSObject.getObjectContent();
		Assert.assertNotNull(in);
		readInputStream(in, new File("TestFile" + File.separator + "disabled.txt"));
	}

	/** 寮�惎bucket鐨剉ersion鍚庯紝鍐嶄笂浼犲悓涓�釜object澶氭 **/
	/*@Test(dataProvider = "putObject", dataProviderClass = DataProvidedForGetObject.class, dependsOnMethods = { "testGetObjectDisabledVersionStatus" })
	public void testPutObjectEnabledVersionStatus(PutObjectRequest putObjectRequest) {
	*/
		/** bucket 鐗堟湰鐘舵�缁欎綅Enabled **/
		/*SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest = new SetBucketVersioningConfigurationRequest(
				putObjectRequest.getBucketName(), new BucketVersioningConfiguration(
						BucketVersioningConfiguration.ENABLED));
		client.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest);
		Assert.assertEquals(client.getBucketVersioningConfiguration(putObjectRequest.getBucketName()).getStatus().toLowerCase(),
				BucketVersioningConfiguration.ENABLED.toLowerCase());

		updateFile(putObjectRequest.getFile(), "TestFile/123456.txt");
		client.putObject(putObjectRequest);
		Assert.assertTrue(client.doesObjectExist(putObjectRequest.getBucketName(), putObjectRequest.getKey(), null));

		updateFile(putObjectRequest.getFile(), "TestFile/1234567.txt");
		client.putObject(putObjectRequest);
		Assert.assertTrue(client.doesObjectExist(putObjectRequest.getBucketName(), putObjectRequest.getKey(),null));
		*/
		/** 璁剧疆鐗堟湰淇℃伅 versionId=null 鍒犻櫎鏈�柊鐗堟湰 **/
		/*DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(putObjectRequest.getBucketName(),
				putObjectRequest.getKey());
		client.deleteObject(deleteObjectRequest);

		Assert.assertFalse(client.doesObjectExist(deleteObjectRequest.getBucketName(), deleteObjectRequest.getKey(),
				null));*/
		

		/** 璁剧疆鐗堟湰淇℃伅 versionId涓嶄负null 鍒犻櫎鎸囧畾鐗堟湰 **/
//		DeleteObjectRequest deleteObjectRequest1 = new DeleteObjectRequest(putObjectRequest.getBucketName(),
//				putObjectRequest.getKey());
//		client.deleteObject(deleteObjectRequest1);
//		Assert.assertFalse(client.doesObjectExist(deleteObjectRequest1.getBucketName(), deleteObjectRequest1.getKey(),
//				oldversionid));
//		Assert.assertFalse(client.doesObjectExist(deleteObjectRequest.getBucketName(), deleteObjectRequest.getKey(),
//				null));

		/** 涓嶅瓨鍦ㄧ殑鍘嗗彶鐗堟湰 **/
		/*new DeleteObjectRequest(putObjectRequest.getBucketName(),
				putObjectRequest.getKey(), "545454545454");
		try {
			client.deleteObject(deleteObjectRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchBucket).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
	}*/
	/*** deleteobject*/
	@Test(dataProvider = "deleteObjectObjectNotExisted", dataProviderClass = DataProvidedForGetObject.class)	
	public void testDeleteObjectButObjectNotExisted(DeleteObjectRequest deleteObjectRequest) {
		try {
			client.deleteObject(deleteObjectRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchKey).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
	}	
	/**删除他人桶内的object
	 * 
	 */
	@Test(dataProvider = "testDeleteObjectButFromOthersBucket", dataProviderClass = DataProvidedForGetObject.class)
	public void testDeleteObjectButFromOthersBucket(DeleteObjectRequest deleteObjectRequest) {
		SetBucketAclRequest setBucketAclRequest = new SetBucketAclRequest(OTHERS_BUCKET, 
                                                          CannedAccessControlList.Private);
		client2.setBucketAcl(setBucketAclRequest.getBucketName(), setBucketAclRequest.getCannedAcl());
		Assert.assertEquals(client2.getBucketAcl(setBucketAclRequest.getBucketName()), setBucketAclRequest.getCannedAcl());
		try {
			client.deleteObject(deleteObjectRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.AccessDenied).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
		setBucketAclRequest = new SetBucketAclRequest(OTHERS_BUCKET, 
                                                      CannedAccessControlList.PublicRead);
		client2.setBucketAcl(setBucketAclRequest.getBucketName(), setBucketAclRequest.getCannedAcl());
		Assert.assertEquals(client2.getBucketAcl(setBucketAclRequest.getBucketName()), setBucketAclRequest.getCannedAcl());
		try {
			client.deleteObject(deleteObjectRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.AccessDenied).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
	}

//	@Test(dataProvider = "putObject", dataProviderClass = DataProvidedForGetObject.class)
//	public void testPutObjectDisabledVersionStatus(PutObjectRequest putObjectRequest) {
//		updateFile(putObjectRequest.getFile(), "TestFile/123.txt");
//		client.putObject(putObjectRequest);
//		updateFile(putObjectRequest.getFile(), "TestFile/1234.txt");
//		String versionid = client.putObject(putObjectRequest).getVersionId();
//
//		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(putObjectRequest.getBucketName(),
//				putObjectRequest.getKey());
//		client.deleteObject(deleteObjectRequest);
//
//		Assert.assertNull(versionid);
//		Assert.assertFalse(client.doesObjectExist(putObjectRequest.getBucketName(), putObjectRequest.getKey(), null));
//
//		updateFile(putObjectRequest.getFile(), "TestFile/12345.txt");
//		client.putObject(putObjectRequest);
//	}
//
//	@Test(dataProvider = "getObject", dataProviderClass = DataProvidedForGetObject.class, dependsOnMethods = { "testPutObjectDisabledVersionStatus" })
//	public void testGetObjectDisabledVersionStatus(GetObjectRequest getObjectRequest) {
//
//		NOSObject NOSObject = client.getObject(getObjectRequest);
//		InputStream in = NOSObject.getObjectContent();
//		Assert.assertNotNull(in);
//		readInputStream(in, new File("TestFile" + File.separator + "disabled.txt"));
//	}
//
//	@Test(dataProvider = "putObject", dataProviderClass = DataProvidedForGetObject.class, dependsOnMethods = { "testGetObjectDisabledVersionStatus" })
//	public void testPutObjectEnabledVersionStatus(PutObjectRequest putObjectRequest) {
//
//		SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest = new SetBucketVersioningConfigurationRequest(
//				putObjectRequest.getBucketName(), new BucketVersioningConfiguration(
//						BucketVersioningConfiguration.ENABLED));
//		client.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest);
//		Assert.assertEquals(client.getBucketVersioningConfiguration(putObjectRequest.getBucketName()).getStatus(),
//				BucketVersioningConfiguration.ENABLED);
//
//		updateFile(putObjectRequest.getFile(), "TestFile/123456.txt");
//		client.putObject(putObjectRequest);
//		Assert.assertTrue(client.doesObjectExist(putObjectRequest.getBucketName(), putObjectRequest.getKey(), null));
//
//		updateFile(putObjectRequest.getFile(), "TestFile/1234567.txt");
//		client.putObject(putObjectRequest);
//		Assert.assertTrue(client.doesObjectExist(putObjectRequest.getBucketName(), putObjectRequest.getKey(),null));
//
//		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(putObjectRequest.getBucketName(),
//				putObjectRequest.getKey());
//		client.deleteObject(deleteObjectRequest);
//
//		Assert.assertFalse(client.doesObjectExist(deleteObjectRequest.getBucketName(), deleteObjectRequest.getKey(),
//				null));
//		
////		DeleteObjectRequest deleteObjectRequest1 = new DeleteObjectRequest(putObjectRequest.getBucketName(),
////				putObjectRequest.getKey());
////		client.deleteObject(deleteObjectRequest1);
////		Assert.assertFalse(client.doesObjectExist(deleteObjectRequest1.getBucketName(), deleteObjectRequest1.getKey(),
////				oldversionid));
////		Assert.assertFalse(client.doesObjectExist(deleteObjectRequest.getBucketName(), deleteObjectRequest.getKey(),
////				null));
//
//		DeleteObjectRequest deleteObjectRequest2 = new DeleteObjectRequest(putObjectRequest.getBucketName(),
//				putObjectRequest.getKey(), "545454545454");
//		try {
//			client.deleteObject(deleteObjectRequest2);
//		} catch (ServiceException e) {
//			System.out.println(" ");
//		}
//
//		updateFile(putObjectRequest.getFile(), "TestFile/12345678.txt");
//		client.putObject(putObjectRequest);
//		updateFile(putObjectRequest.getFile(), "TestFile/123456789.txt");
//		client.putObject(putObjectRequest);
//	}
//
//	/** D:/enabledNew.txt **/
//	@Test(dataProvider = "getObject", dataProviderClass = DataProvidedForGetObject.class, dependsOnMethods = { "testPutObjectEnabledVersionStatus" })
//	public void testGetObjectEnabledVersionStatus(GetObjectRequest getObjectRequest) {
//
//		NOSObject NOSObject = client.getObject(getObjectRequest);
//		InputStream in = NOSObject.getObjectContent();
//		Assert.assertNotNull(in);
//		readInputStream(in, new File("TestFile" + File.separator + "enabledNew.txt"));
//
//		/** list  **/
//		GetObjectVersionsResult getObjectVersionsResult = client.getObjectVersions(getObjectRequest.getBucketName(),
//				getObjectRequest.getKey());
//		List<NOSVersionSummary> sum = getObjectVersionsResult.getVersionSummary();
//		Assert.assertNotNull(sum);
//
//		 
//		// Assert.assertEquals(sum.size(), 2);
//		String version = sum.get(0).getVersionId();
//		/** versionid download object **/
//		GetObjectRequest request = new GetObjectRequest(getObjectRequest.getBucketName(), getObjectRequest.getKey());
//		request.setVersionId(version);
//		NOSObject NOSObject2 = client.getObject(request);
//		InputStream inn = NOSObject2.getObjectContent();
//		Assert.assertNotNull(inn);
//		readInputStream(inn, new File("TestFile" + File.separator + "enabledOld.txt"));
//
//		 
//		try {
//			GetObjectRequest request2 = new GetObjectRequest(getObjectRequest.getBucketName(),
//					getObjectRequest.getKey());
//			request.setVersionId("njksbfskjgggggggggggggggggggggggggggghskjfh");
//			client.getObject(request2);
//		} catch (ServiceException e) {
//			System.out.println( "");
//		}
//
//	}

	

	public void updateFile(File file, String str) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < 10; i++) {
			writer.write(str);
		}
		writer.write("\n");
		writer.close();
	}

	public void readInputStream(InputStream in, File file) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			PrintWriter writer = new PrintWriter(file);
			String line = null;
			while ((line = reader.readLine()) != null) {
				writer.write(line);
			}

			reader.close();
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
