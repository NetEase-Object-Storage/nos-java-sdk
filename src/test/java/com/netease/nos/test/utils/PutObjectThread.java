package com.netease.nos.test.utils;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import org.testng.Assert;

import com.netease.cloud.services.nos.NosClient;

public class PutObjectThread extends Thread {
	@SuppressWarnings("unused")
	private int threadNo;
	@SuppressWarnings("unused")
	private int threadNum;
	@SuppressWarnings("unused")
	private int averageRunTimes;
	private int runNum;
	private String bucketName;
	private String objectName;
	private String objectNamePrefix;
	private String fileName;
	private CountDownLatch doneSignal;
	private NosClient client;
	public PutObjectThread(NosClient client, int threadNo, int threadNum, int averageRunTimes,int runNum, 
			               CountDownLatch doneSignal, String bucketName, String objectName, String fileName) {
		this.client = client;
		this.threadNo = threadNo;
		this.threadNum = threadNum;
		this.runNum = runNum;
		this.averageRunTimes = averageRunTimes;
		this.objectNamePrefix = objectName;
		this.objectName = objectName;
		this.doneSignal = doneSignal;
        this.bucketName = bucketName;
        this.fileName = fileName;
	}

	public void run() {
		try {
			for (int i = 0; i < runNum; i++) {
				objectName = objectNamePrefix + "_" + i;
				client.putObject(bucketName, objectName, new File(fileName));
			}
		} catch (Exception e) {
			Assert.fail("Caught Exception:", e);
		} finally {
			doneSignal.countDown();
		}
	}
}
