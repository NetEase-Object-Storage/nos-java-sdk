package com.netease.cloud.services.nos.tools;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.netease.cloud.auth.PropertiesCredentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.ListObjectsRequest;
import com.netease.cloud.services.nos.model.NOSObjectSummary;
import com.netease.cloud.services.nos.model.ObjectListing;
import com.netease.cloud.services.nos.model.ObjectMetadata;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.cloud.services.nos.transfer.Download;
import com.netease.cloud.services.nos.transfer.TransferManager;
import com.netease.cloud.services.nos.transfer.Upload;


public class SyncNOS {
	
	private static LinkedBlockingQueue<NOSObjectSummary> fetches = new LinkedBlockingQueue<NOSObjectSummary>(15000);
	private static boolean end = false;
	private static int maxKeys = 1000;
	private static AtomicInteger count = new AtomicInteger(5);
	private static TransferManager srcTM;
	private static TransferManager destTM;
	
	public static class ReaderThread extends Thread {
		NosClient src;
		String srcBucketName;
		
		public ReaderThread(NosClient client, String srcBucketName) {
			src = client;
			this.srcBucketName = srcBucketName;
		}
		
		@Override
		public void run() {
			int count = 0;
			boolean hasNext = true;
			String nextMarker = null;
			while(hasNext) {
				ListObjectsRequest request = new ListObjectsRequest();
				request.setBucketName(srcBucketName);
				request.setMaxKeys(maxKeys);
				if(nextMarker != null) {
					request.setMarker(nextMarker);
				}
				if(fetches.size() > 10000) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// ignore
					}
					continue;
				}
				ObjectListing ol = src.listObjects(request);
				hasNext = ol.isTruncated();
				nextMarker = ol.getNextMarker();
				for(NOSObjectSummary os : ol.getObjectSummaries()) {
					fetches.offer(os);
					count++;
				}
			}
			end = true;
			System.out.println("totalsync:" + count);
		}
	}
	
	public static class WriterThread extends Thread {
		NosClient src;
		NosClient dest;
		String srcBucketName;
		String destBucketName;
		int seq;
		
		public WriterThread(NosClient src, NosClient dest, String srcBucketName, String destBucketName, int seq) {
			this.src = src;
			this.dest = dest;
			this.srcBucketName = srcBucketName;
			this.destBucketName = destBucketName;
			this.seq = seq;
		}
		
		@Override
		public void run() {
			NOSObjectSummary os = null;
			String tmpFile = "tmp-" + Thread.currentThread().getId() + "-" + seq;
			File file = null;
			while(!end || fetches.size() > 0) {
				try {
					os = fetches.poll(5000, TimeUnit.MILLISECONDS);
					if(os == null) {
						continue;
					}
					if(dest.doesObjectExist(destBucketName, os.getKey())) {
						System.out.println("key:" + os.getKey() + " already exist in bucket:" + destBucketName);
					} else {
						System.out.println("sync key:" + os.getKey());
						file = new File(tmpFile);
						if(file.exists()) {
							file.delete();
						}
						file.createNewFile();
						Download download = srcTM.download(srcBucketName, os.getKey(), file);
						download.waitForCompletion();
						PutObjectRequest request = new PutObjectRequest(destBucketName, os.getKey(), file);
						ObjectMetadata omd = new ObjectMetadata();
						omd.setContentType(download.getObjectMetadata().getContentType());
						request.setMetadata(omd);
						Upload upload = destTM.upload(request);
						upload.waitForCompletion();
						System.out.println(os.getKey() + " synced!");
					}
				} catch (Exception e) {
					e.printStackTrace();
					if(os != null) {
						System.err.println("REDO:" + os.getKey() + " sycn failed!");
						System.err.flush();
					}
				} finally {
					if(file != null) {
						try {
							file.delete();
						} catch (Exception e) {// ignore
						}
					}
				}
			}
			
			if(count.decrementAndGet() == 0) {
				srcTM.shutdownNow();
				destTM.shutdownNow();
			}
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Properties hostConf = new Properties();
		hostConf.load(new FileInputStream("conf/host.properties"));
		String srcHost = hostConf.getProperty("srcHost");
		if(srcHost == null) {
			System.err.println("config the src NOS hsot!");
			System.exit(-1);
		}
		
		String destHost = hostConf.getProperty("destHost");
		if(destHost == null) {
			System.err.println("config the dest NOS hsot!");
			System.exit(-1);
		}
		
		String srcBucketName = hostConf.getProperty("srcBucketName");
		if(srcBucketName == null) {
			System.err.println("config the src bucket name.");
			System.exit(-1);
		}
		
		String destBucketName = hostConf.getProperty("destBucketName");
		if(destBucketName == null) {
			System.err.println("config the dest bucket name.");
			System.exit(-1);
		}
		
		
		try {
			maxKeys = Integer.valueOf(hostConf.getProperty("maxKeys"));
			System.out.println("change maxKeys to:" + maxKeys);
		} catch (Exception e) {
			// ignore
		}
		
		System.out.println("sync src:" + srcHost + ",dest:" + destHost + ",srcBucket:" + srcBucketName + ",destBucket:" + destBucketName);
		PropertiesCredentials srcCread = new PropertiesCredentials(new FileInputStream("conf/srcCredentials.properties"));
		PropertiesCredentials destCread = new PropertiesCredentials(new FileInputStream("conf/destCredentials.properties"));
		NosClient src = new NosClient(srcCread);
		src.setEndpoint(srcHost);
		NosClient dest = new NosClient(destCread);
		dest.setEndpoint(destHost);
		srcTM = new TransferManager(src);
		destTM = new TransferManager(dest);
		ReaderThread readerThread = new ReaderThread(src, srcBucketName);
		readerThread.start();
		for(int i = 0; i < 5; i++) {
			WriterThread writerThread = new WriterThread(src, dest, srcBucketName, destBucketName, i);
			writerThread.start();
		}
	}
}
