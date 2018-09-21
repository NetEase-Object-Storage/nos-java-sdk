package com.netease.nos.test.bucket;

import org.testng.annotations.DataProvider;

import com.netease.cloud.services.nos.model.PutBucketDedupRequest;
import com.netease.nos.test.utils.TestHostConfig;

public class DataProvidedForDedup {

	public static String bucketallandnormal = "bucketallandnormal"+TestHostConfig.region.toLowerCase();
	public static String bucketnotexisted = "bucketnotexisted";
	@DataProvider
	public static Object[][] putBucketDedupSuspended() {
		return new Object[][] { { new PutBucketDedupRequest(bucketallandnormal, "Suspended") } };
	}
	
	@DataProvider
	public static Object[][] putBucketDedupDisabled() {
		return new Object[][] { { new PutBucketDedupRequest(bucketallandnormal, "Disabled") } };
	}
	
	/** bucket not existed **/
	@DataProvider
	public static Object[][] putNotExistedBucketDedup() {
		return new Object[][] { { new PutBucketDedupRequest(bucketnotexisted, "Disabled") } };
	}
	
	/**  dedup status illeage **/
	@DataProvider
	public static Object[][] putBucketIllegalDedup() {
		return new Object[][] { { new PutBucketDedupRequest(bucketallandnormal, "Off") } };
	}

}
