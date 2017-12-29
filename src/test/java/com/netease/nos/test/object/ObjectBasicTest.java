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
import com.netease.cloud.util.Md5Utils;
import com.netease.nos.test.utils.Clear;
import com.netease.nos.test.utils.TestHostConfig;

public class ObjectBasicTest {

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
		byte[] by = "\n\nabcdefghij\nklmnopqrstuvwxyz\n\n".getBytes();
		ByteArrayInputStream input1 = new ByteArrayInputStream(by);
		ByteArrayInputStream input2 = new ByteArrayInputStream(by);
		String md5 = Md5Utils.getHex(Md5Utils.computeMD5Hash(input1));
		client.putObject(bucketName, key, input2, null);
		NOSObject nosobj = client.getObject(bucketName, key);
		Assert.assertNotNull(nosobj);
		ObjectMetadata meta = nosobj.getObjectMetadata();
		Assert.assertNotNull(meta);
		Assert.assertEquals(meta.getETag().toLowerCase(), md5);
	}

	/*@Test
	public void testVersionList() throws NoSuchAlgorithmException, IOException {
		byte[] by = "\n\nabcdefghij\nklmnopqrstuvwxyz\n\n".getBytes();
		ByteArrayInputStream input = new ByteArrayInputStream(by);
		client.setBucketVersioningConfiguration(new SetBucketVersioningConfigurationRequest(bucketName,
				new BucketVersioningConfiguration(BucketVersioningConfiguration.ENABLED)));
		client.putObject(bucketName, key, input, null);
		input = new ByteArrayInputStream(by);
		client.putObject(bucketName, key, input, null);
		input = new ByteArrayInputStream(by);
		client.putObject(bucketName, key, input, null);
		input = new ByteArrayInputStream(by);
		client.putObject(bucketName, key, input, null);
		client.listVersions(bucketName);
	}*/
	
	@Test
	public void testGetLocation(){
		Assert.assertEquals(client.getBucketLocation(bucketName), "HZ");
	}
}
