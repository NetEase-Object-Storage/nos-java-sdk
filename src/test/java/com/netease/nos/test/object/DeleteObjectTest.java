package com.netease.nos.test.object;

import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.services.nos.NosClient;

/**
 * @author WANG Binghuan 2012-12-20
 * 
 */
public class DeleteObjectTest {
	private static String bucket = "rds-snapshot1";
	private static String object = "80296e4ac4a24a51af0fcd6489f069aa/mytest:80296e4ac4a24a51af0fcd6489f069aa/myst:80296e4ac4a24a51af0fcd6489f069aa";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// PropertiesCredentials credentials = new PropertiesCredentials(
		// DeleteObjectTest.class.getResourceAsStream("credentials.properties"));
		BasicCredentials credentials = new BasicCredentials("d0b1db0c073249758a80b8ebb85b2bfa",
				"f8a4a6c690d94bc49866b4cd52e9960f");
		System.out.println(credentials.getAccessKeyId() + ":" + credentials.getSecretKey());
		NosClient client = new NosClient(credentials);
		client.setEndpoint("http://114.113.199.19:8181");
		if (client.doesObjectExist(bucket, object)) {
			System.out.println(object + " exists.");
			client.deleteObject(bucket, object);
			System.out.println(client.doesObjectExist(bucket, object));
		} else {
			System.out.println(object + " does not exist.");
		}
	}
}
