package com.netease.nos.test.object;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.testng.annotations.DataProvider;

import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.ObjectMetadata;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.cloud.services.nos.model.StorageClass;
import com.netease.cloud.util.Md5Utils;
import com.netease.nos.test.utils.TestHostConfig;


public class DataProvidedForPutObject {
	
	public static String bucketfortestputobject = "bucketforhiputobjecthis"+TestHostConfig.region.toLowerCase();
	public static String bucketownedbyothers = "bucketownedbyothers"+TestHostConfig.region.toLowerCase();
	public static String bucketnotexisted = "bucketnotexisted";
	public static String FileMD5;
	static{
		try{
			FileMD5 = Md5Utils.getHex(Md5Utils.computeMD5Hash(new FileInputStream(createSampleFile())));
		}catch(Exception e){}
		
	}
	
	@DataProvider
	public static Object[][] putBucket() {
		return new Object[][] { { new CreateBucketRequest(bucketfortestputobject, TestHostConfig.region) } };
	}

	/** minssing Content-Length  ......**/
	@DataProvider
	public static Object[][] putObjectMissingContentLength() {
		return new Object[][] { { } };
	}

	/** Content-Length < object **/
	@DataProvider
	public static Object[][] putObjectSmallerContentLength() {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketfortestputobject, "key1", createSampleFile());
		ObjectMetadata  objectMetadata = new  ObjectMetadata();
		objectMetadata.setContentLength(1L);
		putObjectRequest.setMetadata(objectMetadata);
		return new Object[][] { { putObjectRequest } };
	}
	
	/** Content-Length > object **/
	@DataProvider
	public static Object[][] putObjectBiggerContentLength() {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketfortestputobject, "key2", createSampleFile());
		putObjectRequest.getMetadata().setContentLength(1000000000000L);
		return new Object[][] { { } };
	}
	
	/** MD5 normal  **/
	@DataProvider
	public static Object[][] putObjectNormalMD5() {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketfortestputobject, "key3", createSampleFile());
		return new Object[][] { { putObjectRequest } };
	}
	
	/** inputstream as the parameter  
	 * @throws FileNotFoundException **/
	@DataProvider
	public static Object[][] putObjectInputStream() throws FileNotFoundException {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketfortestputobject, "key4", new FileInputStream(createSampleFile()), new ObjectMetadata());
		return new Object[][] { { putObjectRequest, "TestFile"+File.separator+"123.txt" } };
	}
	
	/** MD5  not right  **/
	@DataProvider
	public static Object[][] putObjectWrongMD5() {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketfortestputobject, "key", createSampleFile());
		putObjectRequest.getMetadata().setContentMD5("gyughyghg");
		return new Object[][] { { putObjectRequest } };
	}
	
		
	/**  storsge class  **/
	@DataProvider
	public static Object[][] putObjectRightStorageClass() {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketfortestputobject, "key5", createSampleFile());
		putObjectRequest.setStorageClass(StorageClass.Standard);
		return new Object[][] { { putObjectRequest } };
	}
	
	/** wrong  storsge class  **/
	@DataProvider
	public static Object[][] putObjectWrongStorageClass() {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketfortestputobject, "key", createSampleFile());
		putObjectRequest.setStorageClass("hi");
		return new Object[][] { { putObjectRequest } };
	}
	
	/** meta-  **/
	@DataProvider
	public static Object[][] putObjectIncludingMetaData() {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketfortestputobject, "key6", createSampleFile());
		putObjectRequest.getMetadata().addUserMetadata("x-nos-meta-haha", "value1");
		putObjectRequest.getMetadata().addUserMetadata("x-nos-meta-hahaha", "value2");
		return new Object[][] { { putObjectRequest } };
	}
	
	/** objectname have existed **/
	@DataProvider
	public static Object[][] putObjectExistedObjectName() {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketfortestputobject, "key3", createSampleFile());
		return new Object[][] { { putObjectRequest } };
	}
	
	/** bucket not existed **/
	@DataProvider
	public static Object[][] putObjectBucketNotExist() {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketnotexisted, "key", createSampleFile());
		return new Object[][] { { putObjectRequest } };
	}
	
	 private static File createSampleFile() {
	       try{
	    	   File file = File.createTempFile("for-test-sdk", ".txt");
	    	   file.deleteOnExit();

		        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
		        writer.write("abcdefghijklmnopqrstuvwxyz\n");
		        writer.write("01234567890112345678901234\n");
		        writer.write("!@#$%^&*()-=[]{};':',.<>/?\n");
		        writer.write("01234567890112345678901234\n");
		        writer.write("abcdefghijklmnopqrstuvwxyz\n");
		        writer.close();

		        return file;
	       }catch(Exception e){
	    	   return null;
	       }
	       
	    }
	/** bucket owned by others **/
	@DataProvider
	public static Object[][] testPutObjectIntoBucketOwnedByOthers() {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketownedbyothers, "key", new File("TestFile"+File.separator+"123.txt"));
		return new Object[][] { { putObjectRequest } };
	}
 

}
