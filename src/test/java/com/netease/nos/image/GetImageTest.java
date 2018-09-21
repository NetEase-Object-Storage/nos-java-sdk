package com.netease.nos.image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

import com.netease.cloud.ClientConfiguration;
import com.netease.cloud.services.nos.model.*;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.netease.cloud.ServiceException;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.nos.test.bucket.BucketBasicTest;
import com.netease.nos.test.utils.WriteDisk;
import com.netease.nos.test.utils.TestHostConfig;

public class GetImageTest {
	Credentials credentials;
	NosClient client;
	
//	private static final String bucketName = "hzfutianhui-test";
	private static final String bucketName = "sync-jd";
	private static final String key = "aa.gif";
	private static final String noimagekey = "http.py";
	
	private static final String credential = "credentials.properties";
	private static final String writeTmpFileDir = "D:" + File.separator + "WorkSpace" + File.separator + "test";
	
	/*
	 private  void displayTextInputStream(InputStream input) throws IOException {
	        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	        while (true) {
	            String line = reader.readLine();
	            if (line == null) break;

	            System.out.println("    " + line);
	        }
	        System.out.println();
	}	
	*/
	
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
		ClientConfiguration clientConfiguration=new ClientConfiguration();
//		clientConfiguration.setProxyHost("localhost");
//		clientConfiguration.setProxyPort(8888);
		clientConfiguration.setIsSubdomain(true);
		client = new NosClient(credentials,clientConfiguration);

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
			//Clear.clearAllVersions(client, bucketName);
	  }
	  
	  @Test
		public void testgetimagebucketnotexist(){
		  	String bucketName = "bucdfdf";
			String key = "notexist.jpg";
			InputStream in = null;
			try{
				in = client.getImage(bucketName, key);	
				// displayTextInputStream(in);
			}catch(ServiceException e){
				Assert.assertEquals(e.getErrorCode(),"NoSuchBucket");
			}finally{
				if (in != null){
					try{
						in.close();
					}catch(IOException e){
						System.out.println("close inputstream error");
					}
				}
			}
		}
	  
	@Test
	public void testgetimagekeynotexist(){
		String key = "notexist.jpg";
		InputStream in = null;
		try{
			 in = client.getImage(bucketName, key);	
			// displayTextInputStream(in);
		}catch(ServiceException e){
			Assert.assertEquals(e.getErrorCode(),"NoSuchKey");
		}finally{
			if (in != null){
				try{
					in.close();
				}catch(IOException e){
					System.out.println("close inputstream error");
				}
			}
		}
	}
	  
	  
	@Test
	public void testgetimageok(){
		InputStream in = null;
		try{
			 in = client.getImage(bucketName, key);	
			// displayTextInputStream(in);
		}catch(ServiceException e){
			Assert.assertNotNull(null);
		}finally{
			if (in != null){
				try{
					in.close();
				}catch(IOException e){
					System.out.println("close inputstream error");
				}
			}
		}
	}
	
	
	@Test
	public void testgetquality(){
		InputStream in = null;
		try{
			//test mode not setted
			 GetImageRequest req =  new GetImageRequest(bucketName,key);
			 req.setQuality(50);
			 in = client.getImage(req);	
			// displayTextInputStream(in);
		}catch(ServiceException e){
			Assert.assertNotNull(null);
		}finally{
			if (in != null){
				try{
					in.close();
				}catch(IOException e){
					System.out.println("close inputstream error");
				}
			}
		}
	}
	
	
	
	@Test
	public void testnotimage(){
		InputStream  in = null;
		GetImageRequest req = new GetImageRequest(bucketName,noimagekey);
		try{
			 in = client.getImage(req);
		}catch(ServiceException e){
			Assert.assertEquals(e.getErrorCode(),"NotAnImage");
		}finally{
			if (in != null){
				try{
					in.close();
				}catch(IOException e){
					System.out.println("close inputstream error");
				}
			}
		}
	}
	
	
	@Test
	public void testthumbnail(){
		
		InputStream in = null;
		
		
		//test mode not setted
		GetImageRequest req =  new GetImageRequest(bucketName,key);
		
		req.setResizeX(100);
		req.setResizeY(100);
		try{
			 in = client.getImage(req);
		}catch (Exception e){
			System.out.println(e.getClass());
			Assert.assertEquals(e.getClass().toString(), "class java.lang.IllegalArgumentException");
		}finally{
			if (in != null){
				try{
					in.close();
				}catch(IOException e){
					System.out.println("close inputstream error");
				}
			}
		}
		//test mode "XMODE"
		req.setResizeXY(200, 200);
		req.setMode(GetImageMode.XMODE);
		try{
			in = client.getImage(req);
		}catch (ServiceException e){
			System.out.println("error code is:" + e.getErrorCode());
			Assert.assertNotNull(null);
		}finally{
			if (in != null){
				try{
					in.close();
				}catch(IOException e){
					System.out.println("close inputstream error");
				}
			}
		}
		//test mode "YMODE"
		req.setResizeXY(750, 200);
		req.setAxis(5);
		req.setMode(GetImageMode.YMODE);
		try{
			in = client.getImage(req);
		}catch (ServiceException e){
			System.out.println("error code is:" + e.getErrorCode());
			Assert.assertNotNull(null);
		}finally{
			if (in != null){
				try{
					in.close();
				}catch(IOException e){
					System.out.println("close inputstream error");
				}
			}
		}		
		
		//test mode "ZMODE"
		req.setResizeXY(750, 200);
		req.setMode(GetImageMode.ZMODE);
		try{
			in = client.getImage(req);
		}catch (ServiceException e){
			System.out.println("error code is:" + e.getErrorCode());
			Assert.assertNotNull(null);
		}finally{
			if (in != null){
				try{
					in.close();
				}catch(IOException e){
					System.out.println("close inputstream error");
				}
			}
		}
		
	}
	
	
	@Test
	public void testcrop(){
		
		InputStream in = null;
		
		GetImageRequest req =  new GetImageRequest(bucketName,key);
		req.setCropParam(10, 10, 100, 100);
		try{
			 in = client.getImage(req);
		}catch (Exception e){
			System.out.println(e.getClass());
			Assert.assertEquals(e.getClass().toString(), "class java.lang.IllegalArgumentException");
		}finally{
			if (in != null){
				try{
					in.close();
				}catch(IOException e){
					System.out.println("close inputstream error");
				}
			}
		}
		
		req.setCropX(10);
		req.setCropY(10);
		req.setCropWidth(100);
		req.setCropHeight(100);
		req.setMode(GetImageMode.XMODE);
		try{
			in = client.getImage(req);
		}catch (ServiceException e){
			Assert.assertNotNull(null);
		}finally{
			if (in != null){
				try{
					in.close();
				}catch(IOException e){
					System.out.println("close inputstream error");
				}
			}
		}
	}
	
	
	
	@Test
	public void testrighttype(){
		InputStream in = null;
		GetImageRequest req = new GetImageRequest(bucketName,key);
		req.setType("png");
		try{
			 in = client.getImage(req);
		}catch(ServiceException e){		
			Assert.assertNotNull(null);
		}finally{
			if (in != null){
				try{
					in.close();
				}catch(IOException e){
					System.out.println("close inputstream error");
				}
			}
		}
		
		req.setType("jpg");
		try{
			 in = client.getImage(req);
		}catch(ServiceException e){		
			Assert.assertNotNull(null);
		}finally{
			if (in != null){
				try{
					in.close();
				}catch(IOException e){
					System.out.println("close inputstream error");
				}
			}
		}

		req.setType("jpeg");
		try{
			 in = client.getImage(req);
		}catch(ServiceException e){		
			Assert.assertNotNull(null);
		}finally{
			if (in != null){
				try{
					in.close();
				}catch(IOException e){
					System.out.println("close inputstream error");
				}
			}
		}
		
		req.setType("bmp");
		try{
			 in = client.getImage(req);
		}catch(ServiceException e){		
			Assert.assertNotNull(null);
		}finally{
			if (in != null){
				try{
					in.close();
				}catch(IOException e){
					System.out.println("close inputstream error");
				}
			}
		}	
	}
	
	@Test
	public void testerrortype(){
		InputStream in = null;
		GetImageRequest req = new GetImageRequest(bucketName,key);
		req.setType("ds");
		try{
			 in = client.getImage(req);
		}catch(ServiceException e){
			System.out.println("++++++aa++++++" + e.getErrorCode());
			System.out.println("+++++aa+++++++" + e.getMessage());
			//System.out.println("+++++aa+++++++" + e.getResource());
			//System.out.println("+++++aa+++++++" + e.getRequestId());			
			Assert.assertEquals(e.getErrorCode(),"ImageFormatNotSupported");
		}finally{
			if (in != null){
				try{
					in.close();
				}catch(IOException e){
					System.out.println("close inputstream error");
				}
			}
		}
	}
	
	
	@Test
	public void testwaterMark(){
		GetImageRequest req = new GetImageRequest(bucketName,key);
		req.setWaterMark("水印");
		try{
			InputStream in = client.getImage(req);
			WriteDisk.writeToDisk(in, writeTmpFileDir, key,  Boolean.FALSE );
		}catch (ServiceException e){
			System.out.println("++++++++++++" + e.getErrorCode());
			System.out.println("++++++++++++" + e.getMessage());
			//System.out.println("++++++++++++" + e.getResource());
			//System.out.println("++++++++++++" + e.getRequestId());			
		}	
	}
	
	@Test
	public void testpixel(){
		GetImageRequest req = new GetImageRequest(bucketName,key);
		req.setPixel(-3);
		try{
			client.getImage(req);
		}catch (ServiceException e){
			Assert.assertEquals(e.getErrorCode(),"InvalidArgument");
			System.out.println("++++++ss++++++" + e.getErrorCode());
			System.out.println("++++++ss++++++" + e.getMessage());
			//System.out.println("++++++ss++++++" + e.getResource());
			//System.out.println("++++++ss++++++" + e.getRequestId());			
		}	
	}

	@Test
	public void testImageInfo(){
		ImageMetadata imageMetadata=client.getImageInfo(bucketName,key);
		Assert.assertEquals(imageMetadata.getImageType(),"JPEG");
	}
	
}

