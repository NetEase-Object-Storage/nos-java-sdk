package com.netease.nos.test.bucket;

import org.testng.annotations.DataProvider;

import com.netease.cloud.services.nos.model.CannedAccessControlList;
import com.netease.cloud.services.nos.model.GetBucketAclRequest;
import com.netease.cloud.services.nos.model.SetBucketAclRequest;
import com.netease.nos.test.utils.TestHostConfig;

public class DataProvidedForAcl {
	
	public static String bucketallandnormal ="bucketallandnormal"+TestHostConfig.region.toLowerCase();
	public static String bucketnotexist = "bucketnotexist";
	
	@DataProvider
	public static Object[][] getBucketAcl() {
		return new Object[][] { { new GetBucketAclRequest(bucketallandnormal) } };
	}
	
	/** Bucket not existed **/
	@DataProvider
	public static Object[][] getNotExistedBucketAcl() {
		return new Object[][] { { new GetBucketAclRequest(bucketnotexist) } };
	}
	
	@DataProvider
	public static Object[][] setBucketAcl() {
		return new Object[][] { { new SetBucketAclRequest(bucketallandnormal, CannedAccessControlList.PublicRead) } };
	}
	
	/** Bucket not existed **/
	@DataProvider
	public static Object[][] setNotExistedBucketAcl() {
		return new Object[][] { { new SetBucketAclRequest(bucketnotexist, CannedAccessControlList.PublicRead) } };
	}
	

}
