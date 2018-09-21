package com.netease.nos.image;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.netease.cloud.ServiceException;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.Bucket;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.ImageMetadata;
import com.netease.nos.test.bucket.BucketBasicTest;
import com.netease.nos.test.utils.TestHostConfig;

public class GetImageInfoTest {
	Credentials credentials;
	NosClient client;
//	private static final String bucketName = "sjl-imagetest-bucket";
	private static final String bucketName = "sync-jd";
	private static final String key = "aa.gif";
	private static final String credential = "credentials.properties";
	@BeforeClass
	public void before(){
		TestHostConfig.changeHost();
		String conf = System.getProperty("credentials.properties", credential);
		InputStream confIn = BucketBasicTest.class.getClassLoader().getResourceAsStream(conf);
		Properties properties = new Properties();
		try {
			properties.load(confIn);
		} catch (IOException e) {
			System.exit(-1);
		}
		String accessKey = properties.getProperty("accessKey");
		String secretKey = properties.getProperty("secretKey");
		//String accessKey="3b098d661e2d4c4aa3704c7860f0ba55";
		//String	secretKey="a3db18a0f3b54a8f9ea853123c8245f5";
		credentials = new BasicCredentials(accessKey, secretKey);
		client = new NosClient(credentials);

		CreateBucketRequest createBucketRequest = new CreateBucketRequest(
				bucketName,TestHostConfig.region, false);
		if (!client.doesBucketExist(createBucketRequest.getBucketName())) {
			Bucket bucket = client.createBucket(createBucketRequest);
			Assert.assertNotNull(bucket);
			Assert.assertEquals(bucket.getName(), createBucketRequest.getBucketName());
			Assert.assertTrue(client.doesBucketExist(createBucketRequest.getBucketName()));
		}
	}
	 @AfterClass
	 public void afterClass() {
	 }
	
	 
	 @Test
	 public void GetImageBucketNotExist(){
		 String bucketName = "dadssad";
		 ImageMetadata imageMetadata = new ImageMetadata();
		 try{
		  imageMetadata  = client.getImageInfo(bucketName,key);
		  
		  System.out.println("type = " +  imageMetadata.getImageType() 				 	
			 	  + "\n width = " + imageMetadata.getImageHeight()
			 	  + "\n height= " + imageMetadata.getImageHeight());		  
		 }catch(ServiceException e){
				System.out.println("++++++++++++" + e.getErrorCode());
				System.out.println("++++++++++++" + e.getMessage());
			//	System.out.println("++++++++++++" + e.getResource());
			//	System.out.println("++++++++++++" + e.getRequestId());				 
		 }
		
	 }	 
	 @Test
	 public void gettime(){
		 DateTimeFormatter rfc822DateFormat = DateTimeFormat
				 .forPattern("EEE, dd MMM yyyy HH:mm:ss ZZZ").withLocale(Locale.US);
		 System.out.println("---------------" + rfc822DateFormat.print(System.currentTimeMillis()));
	 }
	 
	 @Test
	 public void GetImageInfoKeyNotExist(){
		 String key = "dadssad";
		 ImageMetadata imageMetadata = new ImageMetadata();
		 try{
		  imageMetadata  = client.getImageInfo(bucketName,key);
		  
		  System.out.println("type = " +  imageMetadata.getImageType() 				 	
			 	  + "\n width = " + imageMetadata.getImageHeight()
			 	  + "\n height= " + imageMetadata.getImageHeight());		  
		 }catch(ServiceException e){
				System.out.println("++++++++++++" + e.getErrorCode());
				System.out.println("++++++++++++" + e.getMessage());
				//System.out.println("++++++++++++" + e.getResource());
				//System.out.println("++++++++++++" + e.getRequestId());				 
		 }
		
	 }
	 
	 @Test
	 public void GetImageInfook(){
		 ImageMetadata imageMetadata = new ImageMetadata();
		 try{
		  imageMetadata  = client.getImageInfo(bucketName,key);
		  
		  System.out.println("type = " +  imageMetadata.getImageType() 				 	
			 	  + "\n width = " + imageMetadata.getImageWidth()
			 	  + "\n height= " + imageMetadata.getImageHeight());		  
		 }catch(ServiceException e){
				System.out.println("++++++++++++" + e.getErrorCode());
				System.out.println("++++++++++++" + e.getMessage());
				System.out.println("++++++++++++" + e.getResource());
				System.out.println("++++++++++++" + e.getRequestId());				 
		 }
		
	 }
	
	 @Test
	 public void GetImageInfoNotImage(){
		 String key = "http.py";
		 ImageMetadata imageMetadata = new ImageMetadata();
		 try{
		  imageMetadata  = client.getImageInfo(bucketName,key);
		  System.out.println("type = " +  imageMetadata.getImageType() 				 	
				 	  + "\n width = " + imageMetadata.getImageWidth() 
				 	  + "\n height= " + imageMetadata.getImageHeight());		  
		 }catch(ServiceException e){
				System.out.println("++++++++++++" + e.getErrorCode());
				System.out.println("++++++++++++" + e.getMessage());
				System.out.println("++++++++++++" + e.getResource());
				System.out.println("++++++++++++" + e.getRequestId());
				 Assert.assertEquals(e.getErrorCode(), "NotAnImage");
		 }
	 }

}
