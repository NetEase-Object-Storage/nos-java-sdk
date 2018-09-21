package com.netease.nos.test.object;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.NOSObject;
import com.netease.cloud.services.nos.model.ObjectMetadata;
import com.netease.nos.test.utils.Clear;
import com.netease.nos.test.utils.TestHostConfig;

/**
 * 
 * @Title ListAboutTest.java
 * @Package com.netease.nos.test.object
 * @Description 主要测试各种list操作的全部标签是否完善，已备遗漏
 * @Company Netease
 * @author hzzhengbo@corp.netease.com
 * @date 2012-9-13 下午3:02:54
 */
public class ListAboutTest {

	Credentials credentials;
	NosClient client;

	private static final String credential = "credentials.properties";
	private String bucketName = "bucket" + new Date().getTime();
	private String key = "object" + new Date().getTime();

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
		client.createBucket(bucketName);
	}

	@AfterClass
	public void after() {
		Clear.clearAll(client, bucketName);
	}
	
	@Test
	public void test() throws NoSuchAlgorithmException, IOException {
		
		/** 开启桶的版本号**/
		//client.setBucketVersioningConfiguration(new SetBucketVersioningConfigurationRequest(bucketName, "Enabled"));
		
		byte[] by = "\n\nabcdefghij\nklmnopqrstuvwxyz\n\n".getBytes();
		ByteArrayInputStream input = new ByteArrayInputStream(by);
		client.putObject(bucketName, key, input, null);
		NOSObject nosobj = client.getObject(bucketName, key);
		Assert.assertNotNull(nosobj);
		ObjectMetadata meta = nosobj.getObjectMetadata();
		Assert.assertNotNull(meta);
		
		input = new ByteArrayInputStream(by);
		client.putObject(bucketName, key, input, null);
		
		input = new ByteArrayInputStream(by);
		client.putObject(bucketName, key, input, null);
		
		input = new ByteArrayInputStream(by);
		client.putObject(bucketName, key, input, null);
		
		/*GetObjectVersionsResult verResult = client.getObjectVersions(bucketName, key);
		Assert.assertNotNull(verResult);
		List<NOSVersionSummary> versum = verResult.getVersionSummary();
		Assert.assertNotNull(versum);
		
		Assert.assertEquals(versum.size(), 3);
		for(NOSVersionSummary ver : versum){
			System.out.println(ver.getBucketName());
			System.out.println(ver.getETag());
			System.out.println(ver.getKey());
			System.out.println(ver.getSize());
			System.out.println(ver.getStorageClass());
			System.out.println(ver.getVersionId());
			System.out.println(ver.getLastModified());
			System.out.println(ver.getOwner());
		}*/
	}
}
