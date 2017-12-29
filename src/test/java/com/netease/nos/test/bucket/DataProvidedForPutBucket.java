package com.netease.nos.test.bucket;

import org.testng.annotations.DataProvider;

import com.netease.cloud.services.nos.model.CannedAccessControlList;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.nos.test.utils.TestHostConfig;

public class DataProvidedForPutBucket {
	
	public static String bucketallandnormal = "bucketallandnormal"+TestHostConfig.region.toLowerCase();
	public static String bucketbyothers = "bucketbyothers"+TestHostConfig.region.toLowerCase();
	public static String buckets = "buckets"+TestHostConfig.region.toLowerCase();
	public static String batchbuckets = "batchbuckets"+TestHostConfig.region.toLowerCase();
	public static String bucketwithoutregion = "bucketwithoutregion"+TestHostConfig.region.toLowerCase();
	public static String bucketwithoutacl = "bucketwithoutacl"+TestHostConfig.region.toLowerCase();
	public static String bucketwithoutdedup = "bucketwithoutdedup"+TestHostConfig.region.toLowerCase();
	
	public static CreateBucketRequest putBucket(String bucketName, String region, CannedAccessControlList cannedAcl){
		CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
		createBucketRequest.setRegion(region);
		//createBucketRequest.setDeduplicate(deduplicate);
		createBucketRequest.setCannedAcl(cannedAcl);
		return createBucketRequest;
	}
	
	/** right **/
	@DataProvider
	public static Object[][] getPutBucketAllAndNormal() {
		return new Object[][] { { putBucket(bucketallandnormal, TestHostConfig.region, CannedAccessControlList.Private) } };
	}
	
	/**  Chinal bucket **/
	/*@DataProvider
	public static Object[][] getPutBucketIncludeCN() {
		return new Object[][] { { putBucket(buckets, TestHostConfig.region, true, CannedAccessControlList.Private) } };
	}*/
	
	/** missing an augument**/
	@DataProvider
	public static Object[][] getPutBucketWithoutRegion() {
		return new Object[][] { { putBucket(bucketwithoutregion, null, CannedAccessControlList.Private) } };
	}
	@DataProvider
	public static Object[][] getPutBucketWithoutAcl() {
		return new Object[][] { { putBucket(bucketwithoutacl, TestHostConfig.region, null) } };
	}
	@DataProvider
	public static Object[][] getPutBucketWithoutDedup() {
		return new Object[][] { { putBucket(bucketwithoutdedup, TestHostConfig.region, CannedAccessControlList.Private) } };
	}
	
	/** liieage argument **/
	@DataProvider
	public static Object[][] getPutBucketIllegalRegion() {
		return new Object[][] { { putBucket("bucketillegalregion1", "NJ", CannedAccessControlList.Private) } };
	}
//	@DataProvider
//	public static Object[][] getPutBucketIllegalAcl() {
//		return new Object[][] { { putBucket("bucketillegalacl", "HZ", "Disabled", CannedAccessControlList.) } };
//	}
	@DataProvider
	public static Object[][] getPutBucketIllegalDedup() {
		return new Object[][] { { putBucket("bucketillegaldedupa1", TestHostConfig.region, CannedAccessControlList.Private) } };
	}
	
	/** exception **/
	//Bucket name not existed
	@DataProvider
	public static Object[][] getPutBucketButBucketOwnedByYou() {
		return new Object[][] { { putBucket(bucketallandnormal, TestHostConfig.region, CannedAccessControlList.Private) } };
	}
	
	@DataProvider
	public static Object[][] getPutBucketButBucketOwnedByOthers() {
		return new Object[][] { { putBucket(bucketbyothers, TestHostConfig.region, CannedAccessControlList.Private) } };
	}
	//Bucket name illeage
	@DataProvider
	public static Object[][] getPutBucketwithIllegalName() {
		return new Object[][] {
				{ putBucket("192.5.2.58", "HZ", CannedAccessControlList.Private) },
				{ putBucket("abVMcd", "HZ", CannedAccessControlList.Private) },
				{ putBucket("ab--cd", "HZ", CannedAccessControlList.Private) },
				{ putBucket("a..12", "HZ", CannedAccessControlList.Private) },
				{ putBucket(".a12", "HZ", CannedAccessControlList.Private) },
				{ putBucket("-a12", "HZ", CannedAccessControlList.Private) },
				{ putBucket("12", "HZ", CannedAccessControlList.Private) },
				{ putBucket("1#*2", "HZ", CannedAccessControlList.Private) },
				{ putBucket("a123456789012345678901234567890123456789012345678901234567890vcb", "HZ", CannedAccessControlList.Private) },
		};
	}
	
	@DataProvider
	public static Object[][] getPutBatchBucket() {
		return new Object[][] { { putBucket(batchbuckets, TestHostConfig.region, CannedAccessControlList.Private) } };
	}

}
