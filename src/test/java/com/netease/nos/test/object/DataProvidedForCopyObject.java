package com.netease.nos.test.object;

import java.io.File;

import org.testng.annotations.DataProvider;

import com.netease.cloud.services.nos.model.CopyObjectRequest;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.nos.test.utils.TestHostConfig;

public class DataProvidedForCopyObject {

	public static String sourcebucketfortestcopyobject = "sourcebucketforcopyobject"+TestHostConfig.region.toLowerCase();
	public static String descbucketfortestcopyobject = "descbucketforcopyobject"+TestHostConfig.region.toLowerCase();
	public static String sourcebucketnotexisted = "sourcebucketnotexisted";
	public static String descbucketnotexisted = "descbucketnotexisted";
	public static String sourceobjectkey = "sourceobjectkey";
	
	public static String othersbucketfortestcopyobject = "othersbucketfortestcopyobject"+TestHostConfig.region.toLowerCase();
	public static String othersobjectkey = "othersobjectkey";
	/** source bucket **/
	@DataProvider
	public static Object[][] putSourceBucket() {
		return new Object[][] { { new CreateBucketRequest(sourcebucketfortestcopyobject, TestHostConfig.region) } };
	}

	/** source object **/
	@DataProvider
	public static Object[][] putSourceObject() {
		PutObjectRequest putObjectRequest = new PutObjectRequest(sourcebucketfortestcopyobject, "sourceobjectkey",
				new File("TestFile" + File.separator + "123.txt"));
		return new Object[][] { { putObjectRequest } };
	}

	/** desc bucket **/
	@DataProvider
	public static Object[][] putDescBucket() {
		return new Object[][] { { new CreateBucketRequest(descbucketfortestcopyobject, TestHostConfig.region) } };
	}

	/** normal  **/
	@DataProvider
	public static Object[][] copyObjectNormal() {
		CopyObjectRequest copyObjectRequest = new CopyObjectRequest(sourcebucketfortestcopyobject, sourceobjectkey,
				descbucketfortestcopyobject, "descobjectkey");
		return new Object[][] { { copyObjectRequest } };
	}
	
	/** source bucket not existed **/
	@DataProvider
	public static Object[][] copyObjectSourceBucketNotExisted() {
		CopyObjectRequest copyObjectRequest = new CopyObjectRequest(sourcebucketnotexisted, sourceobjectkey,
				descbucketfortestcopyobject, "descobjectkey");
		return new Object[][] { { copyObjectRequest } };
	}
	
	/** source object not existed **/
	@DataProvider
	public static Object[][] copyObjectSourceObjectNotExisted() {
		CopyObjectRequest copyObjectRequest = new CopyObjectRequest(sourcebucketfortestcopyobject, "sourceobjectnotexisted",
				descbucketfortestcopyobject, "descobjectkey");
		return new Object[][] { { copyObjectRequest } };
	}
	
	/** desc bucket not existed **/
	@DataProvider
	public static Object[][] copyObjectDescBucketNotExisted() {
		CopyObjectRequest copyObjectRequest = new CopyObjectRequest(sourcebucketfortestcopyobject, sourceobjectkey,
				descbucketnotexisted, "descobjectkey");
		return new Object[][] { { copyObjectRequest } };
	}
	
	/** desc object  existed **/
	@DataProvider
	public static Object[][] copyObjectDescObjectHaveExisted() {
		CopyObjectRequest copyObjectRequest = new CopyObjectRequest(sourcebucketfortestcopyobject, sourceobjectkey,
				descbucketfortestcopyobject, "descobjectkeyExisted");
		return new Object[][] { { copyObjectRequest } };
	}
	
	@DataProvider
	public static Object[][] testCopyObjectFromOthersPrivateBucket() {
		CopyObjectRequest copyObjectRequest = new CopyObjectRequest(othersbucketfortestcopyobject, othersobjectkey,
				descbucketfortestcopyobject, "descobjectkey");
		return new Object[][] { { copyObjectRequest } };
	}
	
	@DataProvider
	public static Object[][] testCopyObjectFromOthersPublicBucket() {
		CopyObjectRequest copyObjectRequest = new CopyObjectRequest(othersbucketfortestcopyobject, othersobjectkey,
				descbucketfortestcopyobject, "objectfromothersbucket");
		return new Object[][] { { copyObjectRequest } };
	}
	
	@DataProvider
	public static Object[][] testCopyObjectToOthersBucket() {
		CopyObjectRequest copyObjectRequest = new CopyObjectRequest(sourcebucketfortestcopyobject, sourceobjectkey,
				othersbucketfortestcopyobject, "objectfromothersbucket");
		return new Object[][] { { copyObjectRequest } };
	}
}
