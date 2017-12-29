package com.netease.nos.test.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.netease.cloud.ClientException;
import com.netease.cloud.ServiceException;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.Nos;
import com.netease.cloud.services.nos.model.InitiateMultipartUploadRequest;
import com.netease.cloud.services.nos.model.InitiateMultipartUploadResult;
import com.netease.cloud.services.nos.model.ListPartsRequest;
import com.netease.cloud.services.nos.model.PartListing;
import com.netease.cloud.services.nos.model.UploadPartRequest;
import com.netease.cloud.services.nos.transfer.Download;
import com.netease.cloud.services.nos.transfer.MultipleFileUpload;
import com.netease.cloud.services.nos.transfer.TransferManager;
import com.netease.cloud.services.nos.transfer.Upload;
import com.netease.cloud.services.nos.transfer.model.UploadResult;
import com.netease.nos.test.bucket.BucketBasicTest;
import com.netease.nos.test.mulitpart.MulitPartUploadTest;
import com.netease.nos.test.utils.Clear;
import com.netease.nos.test.utils.NOSConstant;
import com.netease.nos.test.utils.TestHelper;
import com.netease.nos.test.utils.TestHostConfig;

public class TestManager {
	
	private TransferManager tx;
	private Nos client;
	Credentials credentials;
	String bucketName = "buckettestformanager"+TestHostConfig.region.toLowerCase();
	
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
		tx = new TransferManager(new BasicCredentials(accessKey, secretKey));
		client = tx.getNosClient();
		
		client.createBucket(bucketName, TestHostConfig.region);		
	}
	@AfterClass
	public void after(){
		Clear.clearMulitObject(client, bucketName);
	}
	/**
	 * 文件上传功能测试，测试数据需要把大文件(>16M)和小文件都覆盖到
	 * @throws Exception
	 */
	@Test(dataProvider = "testUploadFile", dataProviderClass = DataProvidedForManager.class)
	public void testUploadAndDownloadFile(String filePath) throws Exception{
		Upload upload = tx.upload(bucketName, "smallkey", new File(filePath));
		UploadResult  result = upload.waitForUploadResult();
        String key = result.getKey();
        Assert.assertEquals(key, "uploadkey");
        File file = new File("TestFile/uploadtmp.txt");
        Download download = tx.download(bucketName, "smallkey", file);
        download.waitForCompletion();
        String actualMD5 = TestHelper.getMD5("TestFile/uploadtmp.txt");
        file.delete();
		//计算预期的MD5
		String expectedMD5 = TestHelper.getMD5(filePath);
		Assert.assertEquals(actualMD5, expectedMD5);
	}
    /**
     * 文件夹上传功能测试(不上传文件夹下的目录)
     * @throws ServiceException
     * @throws ClientException
     * @throws InterruptedException
     */
	@Test
	public void testUploadDirectory() throws ServiceException, ClientException, InterruptedException{
		String dirPath = "TestFile";
		MultipleFileUpload result = tx.uploadDirectory(bucketName, null, new File(dirPath), false);
		result.waitForCompletion();
		
		File dir = new File(dirPath); 
        File[] files = dir.listFiles();
        if (files != null) {
        	for (int i = 0; i < files.length; i++) {
        		//如果是目录，直接跳过
                if (files[i].isDirectory()) { 
                    continue; 
                } else { 
                    Assert.assertTrue(client.doesObjectExist(bucketName, files[i].getName()), 
                    		                                 "Object doesn't exist!");
                } 
            } 
        }
	}
	@Test
	public void testUploadStream() throws Exception{
		String filePath = "src/test/resources/apache.tar.rar";
		String tmpFile = "TestFile/streamtmp.txt";
		Upload upload = tx.upload(bucketName, "streamkey", new FileInputStream(new File(filePath)), null);
		UploadResult  result = upload.waitForUploadResult();
		String key = result.getKey();
        Assert.assertEquals(key, "streamkey");
        File file = new File(tmpFile);
        Download download = tx.download(bucketName, "streamkey", file);
        download.waitForCompletion();
        String actualMD5 = TestHelper.getMD5(tmpFile);
        file.delete();
		//计算预期的MD5
		String expectedMD5 = TestHelper.getMD5(filePath);
		Assert.assertEquals(actualMD5, expectedMD5);
	}

	@Test
	public void testAbort(){
		InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName,
				"abortKey");
		InitiateMultipartUploadResult result = client.initiateMultipartUpload(initiateMultipartUploadRequest);
		Assert.assertEquals("abortKey", result.getKey());
		String uploadId = result.getUploadId();

		UploadPartRequest uploadPartRequest = new UploadPartRequest();
		uploadPartRequest.setBucketName(bucketName);
		uploadPartRequest.withInputStream(MulitPartUploadTest.class.getClassLoader().getResourceAsStream(System.getProperty("djh", "apache.tar.rar")));
		uploadPartRequest.setKey("abortKey");
		uploadPartRequest.setUploadId(uploadId);
		uploadPartRequest.setPartNumber(1);
		uploadPartRequest.setPartSize(5 * 1024 * 1024);
		client.uploadPart(uploadPartRequest);

		ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, "abortKey", uploadId);
		PartListing Parts = client.listParts(listPartsRequest);
		Assert.assertNotNull(Parts);
		Assert.assertEquals("abortKey", Parts.getKey());
		Assert.assertEquals(bucketName, Parts.getBucketName());
		Assert.assertEquals(uploadId, Parts.getUploadId());
		Assert.assertEquals(1, Parts.getParts().size());
	
		Date date = new Date();
		date.getTime();
		//bug:CLOUD-1667,fixed
		tx.abortMultipartUploads(bucketName, date);
		try {
			Parts = client.listParts(listPartsRequest);
			Assert.fail("No expected error!");
		} catch (ServiceException e) {
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchUpload).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
		}	
	}	
}
