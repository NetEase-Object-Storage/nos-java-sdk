package com.netease.nos.test.object;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.netease.cloud.HttpMethod;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.nos.test.utils.Clear;
import com.netease.nos.test.utils.TestHelper;
import com.netease.nos.test.utils.TestHostConfig;

public class UrlDownloadTest {
	Credentials credentials;
	NosClient client;
	String bucketName = DataProvidedForPutObject.bucketfortestputobject;
	String key = "urlDownloadKey";
	String testFilePath = "TestFile/123.txt";

	private static final String credential = "credentials.properties";

	@BeforeClass
	public void before() {
		TestHostConfig.changeHost();
		String conf = System.getProperty("nos.credential", credential);
		InputStream confIn = ObjectPutTest.class.getClassLoader().getResourceAsStream(conf);
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
			client.createBucket(bucketName, TestHostConfig.region);
		}else{
			//client.setBucketDedup(bucketName, "Enabled");
		}
		client.putObject(bucketName, key, new File(testFilePath));
		
	}
	@AfterClass
	public void after(){
		Clear.clear(client, bucketName);
	}
	
	@Test
	public void testUrlDownload() throws Exception{
		Date expiration = new Date(System.currentTimeMillis()+10000);
		URL objurl = client.generatePresignedUrl(bucketName, key, expiration, HttpMethod.GET);
		URLConnection connection = objurl.openConnection();
		connection.connect();
		
		String getFilePath = "TestFile/getFile.txt";
		InputStream in = connection.getInputStream();
		File file = new File(testFilePath);
		byte[] buf = new byte[(int) (file.length())];
	    in.read(buf);
	    FileOutputStream fos = new FileOutputStream(getFilePath);
	    fos.write(buf);
	    fos.flush();
		fos.close();
		in.close();		
		
		String expectedMD5 = TestHelper.getMD5(testFilePath);
		String actualMD5 = TestHelper.getMD5(getFilePath);
		
		file = new File(getFilePath);    
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	    }  
	    
		Assert.assertEquals(actualMD5, expectedMD5);
	}
	
	@Test
	public void testUrlDownloadWithInvaildExpiration() throws IOException
	{
		Date expiration = new Date(System.currentTimeMillis()-10000);
		URL objurl = client.generatePresignedUrl(bucketName, key, expiration, HttpMethod.GET);
		URLConnection connection = objurl.openConnection();
		connection.connect();
		try {
			connection.getInputStream();
			Assert.fail("No expected Error!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
}
