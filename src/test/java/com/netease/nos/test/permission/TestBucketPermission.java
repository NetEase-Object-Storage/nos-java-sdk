package com.netease.nos.test.permission;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.AfterClass;

import com.netease.cloud.ServiceException;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.CannedAccessControlList;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.GetObjectRequest;
import com.netease.nos.test.bucket.BucketBasicTest;
import com.netease.nos.test.utils.NOSConstant;
import com.netease.nos.test.utils.TestHostConfig;

public class TestBucketPermission {
	Credentials credentials1;
	Credentials credentials2;
	NosClient client1, client2;
	private static final String credential = "credentials.properties";
	String privatebucketownedby1 = "privatebucketowned1"+TestHostConfig.region.toLowerCase();
	String publicbucketownedby1 = "publicbucketowned1"+TestHostConfig.region.toLowerCase();
	String privatebucketownedby2 =  "privatebucketowned2"+TestHostConfig.region.toLowerCase();
	String publicbucketownedby2 = "publicbucketowned2"+TestHostConfig.region.toLowerCase();
	
	public void testPermission() {
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
		credentials1 = new BasicCredentials(accessKey, secretKey);
		client1 = new NosClient(credentials1);
		
		String accessKey2 = properties.getProperty("accessKey2");
		String secretKey2 = properties.getProperty("secretKey2");
		credentials2 = new BasicCredentials(accessKey2, secretKey2);
		client2 = new NosClient(credentials2);

		// owner1's private bucket
		CreateBucketRequest createBucketRequest11 = new CreateBucketRequest(privatebucketownedby1, TestHostConfig.region);
		if (!client1.doesBucketExist(privatebucketownedby1)) {
			createBucketRequest11.setCannedAcl(CannedAccessControlList.Private);
			client1.createBucket(createBucketRequest11);
		}

		// owner1's public-read bucket
		CreateBucketRequest createBucketRequest12 = new CreateBucketRequest(publicbucketownedby1, TestHostConfig.region);
		if (!client1.doesBucketExist(publicbucketownedby1)) {
			createBucketRequest12.setCannedAcl(CannedAccessControlList.PublicRead);
			client1.createBucket(createBucketRequest12);
		}

		// owner2's private bucket
		CreateBucketRequest createBucketRequest21 = new CreateBucketRequest(privatebucketownedby2, TestHostConfig.region);
		if (!client2.doesBucketExist(privatebucketownedby2)) {
			createBucketRequest21.setCannedAcl(CannedAccessControlList.Private);
			client2.createBucket(createBucketRequest21);
		}
		if (!client2.doesObjectExist("privatebucketownedby2", "zbkey")) {
			client2.putObject(privatebucketownedby2, "zbkey", new File("TestFile/123.txt"));
		}

		// owner2's public-read bucket
		CreateBucketRequest createBucketRequest22 = new CreateBucketRequest(publicbucketownedby2, TestHostConfig.region);
		if (!client2.doesBucketExist(publicbucketownedby2)) {
			createBucketRequest22.setCannedAcl(CannedAccessControlList.PublicRead);
			client2.createBucket(createBucketRequest22);
		}
		if (!client2.doesObjectExist(publicbucketownedby2, "zbkey")) {
			client2.putObject(publicbucketownedby2, "zbkey", new File("TestFile/123.txt"));
		}

		/** owner1 want to delete owner2's public-read bucket **/
		try {
			client1.deleteBucket(publicbucketownedby2);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.AccessDenied).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}

		/** owner1 want to delete owner2's private bucket **/
		try {
			client1.deleteBucket(privatebucketownedby2);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.AccessDenied).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}

		/** owner1 want to download owner2's public-read bucket **/

		GetObjectRequest getObjectRequest1 = new GetObjectRequest(publicbucketownedby2, "zbkey");
		client1.getObject(getObjectRequest1, new File("TestFile/downloadDiffOwnerpub.txt"));
		File file = new File("TestFile/downloadDiffOwnerpub.txt");    
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	    }  

		/** owner1 want to download owner2's private bucket **/
		try {
			GetObjectRequest getObjectRequest2 = new GetObjectRequest(privatebucketownedby2, "zbkey");
			client1.getObject(getObjectRequest2, new File("TestFile/downloadDiffOwnerprivate.txt"));
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.AccessDenied).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}
	}
	
	@AfterClass
	public void after(){
		/*Clear.clearAllVersions(client1, privatebucketownedby1);
		Clear.clearAllVersions(client2, privatebucketownedby2);
		Clear.clearAllVersions(client1, publicbucketownedby1);
		Clear.clearAllVersions(client2, publicbucketownedby2);*/
	}
}
