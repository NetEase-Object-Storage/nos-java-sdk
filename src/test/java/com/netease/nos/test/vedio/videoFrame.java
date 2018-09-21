package com.netease.nos.test.vedio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;

import com.netease.cloud.ClientException;
import com.netease.cloud.ServiceException;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.Bucket;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.cloud.services.nos.model.VideoFrameRequest;
import com.netease.nos.test.bucket.BucketBasicTest;
import com.netease.nos.test.utils.NOSConstant;
import com.netease.nos.test.utils.TestHostConfig;

public class videoFrame {
	Credentials credentials;
	Credentials credentials2;
	NosClient client;
	NosClient client2;
	
	private static final String credential = "credentials.properties";
	private static final String BUCKET_NAME = "sjl-first-bucket";
	//private static final String BUCKET_NAME = "sdsd";
	private static final String KeyName = "dsds";
	//private static final String OTHERS_BUCKET = DataProvidedForGetObject.bucketownedbyothers;
	//private static final String filePath = "TestFile" + File.separator + "123.txt";
	
    private static void displayTextInputStream(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;

            System.out.println("    " + line);
        }
        System.out.println();
    }	
  @Test
  public void testVideoFrame() {
	  
	  VideoFrameRequest videoFrameRequest = new VideoFrameRequest(BUCKET_NAME, KeyName);
	  
	  /*all kind of param set*/

	  try{
		  InputStream in = client.videoFrame(videoFrameRequest);
		  /*
		  byte b[]=new byte[20480];
		  try{
			  in.read(b);
			  System.out.println(b);
		  }catch(IOException e){
			  e.printStackTrace();
		  }
		  */
		  
		  
		  try{
			  displayTextInputStream(in);
		  }catch(IOException e){
			  e.printStackTrace();
		  }

		  
	  }catch(ServiceException e){
			String expectedErrorCode = NOSConstant.codeMap.get(NOSConstant.NoSuchKey).getNosErrorCode();
			String errorCode = e.getErrorCode();
			Assert.assertEquals(errorCode, expectedErrorCode, "Failed to verifyERROR!");
	  }catch(ClientException ce){
		  ce.printStackTrace();
	  }
  }
  /**
   * Creates a temporary file with text data.
   *
   * @return A newly created temporary file with text data.
   *
   * @throws IOException
   */
  private static File createSampleFile() throws IOException {
      File file = File.createTempFile("java-sdk-", ".txt");
      file.deleteOnExit();

      Writer writer = new OutputStreamWriter(new FileOutputStream(file));
      writer.write("abcdefghijklmnopqrstuvwxyz\n");
      writer.write("01234567890112345678901234\n");
      writer.write("!@#$%^&*()-=[]{};':',.<>/?\n");
      writer.write("01234567890112345678901234\n");
      writer.write("abcdefghijklmnopqrstuvwxyz\n");
      writer.close();

      return file;
  }
  
  @BeforeClass
  public void beforeClass()throws IOException {
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
		
		//check and create bucket
		CreateBucketRequest createBucketRequest = new CreateBucketRequest(BUCKET_NAME,TestHostConfig.region, false);
		if (!client.doesBucketExist(createBucketRequest.getBucketName())) {
			//Assert.assertTrue(false);
			Bucket bucket = client.createBucket(createBucketRequest);
			Assert.assertNotNull(bucket);
			Assert.assertEquals(bucket.getName(), createBucketRequest.getBucketName());
			Assert.assertTrue(client.doesBucketExist(createBucketRequest.getBucketName()));
		}
		
		//create object
		client.putObject(new PutObjectRequest(BUCKET_NAME, KeyName, createSampleFile()));
		
		
  }

  @AfterClass
  public void afterClass() {
		//Clear.clearAllVersions(client, BUCKET_NAME);
  }

}
