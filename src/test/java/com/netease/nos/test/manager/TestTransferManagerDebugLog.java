package com.netease.nos.test.manager;

import java.io.File;
import java.util.UUID;

import org.testng.annotations.Test;

import com.netease.cloud.ClientException;
import com.netease.cloud.ServiceException;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.HeadBucketRequest;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.cloud.services.nos.transfer.TransferManager;
import com.netease.cloud.services.nos.transfer.Upload;
import com.netease.cloud.services.nos.transfer.model.UploadResult;

/**
 * @author WANG Binghuan 2013-1-30
 * 
 */
public class TestTransferManagerDebugLog {
	
	@Test
	public void testHeadBucket() {
		NosClient nos = new NosClient(new BasicCredentials("93e802b1b7af47509000357d939f5999",
				"f8601529ab79429083d9e2431e00b3a1"));
		nos.setEndpoint("fs-4.photo.163.org:8182");
		
		HeadBucketRequest request = new HeadBucketRequest("read");
		request.setLogID("xxx");
		request.setLogSeq("3.4");
		System.out.println(nos.doesBucketExist(request));
	}

	@Test
	public void testDebugLog() {
		NosClient nos = new NosClient(new BasicCredentials("93e802b1b7af47509000357d939f5999",
				"f8601529ab79429083d9e2431e00b3a1"));
		nos.setEndpoint("fs-4.photo.163.org:8182");
		TransferManager tm = new TransferManager(nos);
		PutObjectRequest request = new PutObjectRequest("read", "1.exe", new File("D:\\tmp\\1.exe"));
		request.setLogID(UUID.randomUUID().toString());
		request.setLogSeq("4.5");
		Upload upload = tm.upload(request);
		try {
			UploadResult result = upload.waitForUploadResult();
			System.out.println(result);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
