package com.netease.nos.test.object;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.log4j.lf5.util.StreamUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.internal.Constants;
import com.netease.cloud.services.nos.model.GetObjectRequest;
import com.netease.cloud.services.nos.model.NOSObject;

/**
 * @author WANG Binghuan 2013-1-22
 * 
 */
public class TestConcurrentRead {

	NosClient client;

	@BeforeClass
	public void beforeclass() {
		Constants.NOS_HOST_NAME = "172.17.2.64:8182";
		try {
			client = new NosClient(new BasicCredentials("93e802b1b7af47509000357d939f5999", "f8601529ab79429083d9e2431e00b3a1"));
			GetObjectRequest req = new GetObjectRequest("read", "test1");
			req.setRange(22000000, 22000010);
			NOSObject object = client.getObject(req);
			Assert.assertEquals("helloworld\n", new String(StreamUtils.getBytes(object.getObjectContent())));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testRead() {
		
	}
}
