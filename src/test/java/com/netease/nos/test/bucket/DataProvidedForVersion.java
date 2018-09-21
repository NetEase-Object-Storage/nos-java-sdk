package com.netease.nos.test.bucket;

import org.testng.annotations.DataProvider;

import com.netease.cloud.services.nos.model.BucketVersioningConfiguration;
import com.netease.cloud.services.nos.model.ListVersionsRequest;
import com.netease.cloud.services.nos.model.SetBucketVersioningConfigurationRequest;
import com.netease.nos.test.utils.TestHostConfig;

public class DataProvidedForVersion {
	
	public static String bucketallandnormal = "bucketallandnormal"+TestHostConfig.region.toLowerCase();
	public static String bucketNotExist ="bucketNotExist";
	@DataProvider
	public static Object[][] putBucketVersioningEnabled() {
		return new Object[][] { { new SetBucketVersioningConfigurationRequest(bucketallandnormal, new BucketVersioningConfiguration(BucketVersioningConfiguration.ENABLED)) } };
	}
	
	@DataProvider
	public static Object[][] putBucketVersioningSuspended() {
		return new Object[][] { { new SetBucketVersioningConfigurationRequest(bucketallandnormal, new BucketVersioningConfiguration(BucketVersioningConfiguration.SUSPENDED)) } };
	}
	
	@DataProvider
	public static Object[][] putNotExistedBucketVersioning() {
		return new Object[][] { { new SetBucketVersioningConfigurationRequest(bucketNotExist, new BucketVersioningConfiguration(BucketVersioningConfiguration.SUSPENDED)) } };
	}
    
	@DataProvider
	public static Object[][] testListVersionsbyArguments() {
		return new Object[][] { { "etc", null, new Integer(500)},
				                { "etc", null, new Integer(1500)}
		};
	}
	
	@DataProvider
	public static Object[][] testListVersionsbyRequest() {
		return new Object[][] { { new ListVersionsRequest(null, "etc", null, new Integer(500))},
				                { new ListVersionsRequest(null, "etc", null, new Integer(1500))}
		};
	}
}
