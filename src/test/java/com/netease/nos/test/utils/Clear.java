package com.netease.nos.test.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.Nos;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.AbortMultipartUploadRequest;
import com.netease.cloud.services.nos.model.Bucket;
import com.netease.cloud.services.nos.model.ListMultipartUploadsRequest;
import com.netease.cloud.services.nos.model.ListObjectsRequest;
import com.netease.cloud.services.nos.model.MultipartUpload;
import com.netease.cloud.services.nos.model.MultipartUploadListing;
import com.netease.cloud.services.nos.model.NOSObjectSummary;
import com.netease.cloud.services.nos.model.ObjectListing;
import com.netease.nos.test.object.ObjectPutTest;

public class Clear {

	
	
	public static void listAllObjects(Nos nosClient ,String bucketName, String prefix, List<String> keys) {
		ObjectListing objectListing;
		String marker = null;
		int size = 0;
		do{
			ListObjectsRequest request = new ListObjectsRequest();
			request.setPrefix(prefix);
			request.setBucketName(bucketName);
			request.setMarker(marker);
			request.setMaxKeys(100);
			objectListing = nosClient.listObjects(request);
			List<NOSObjectSummary> sum = objectListing.getObjectSummaries();
			if (sum.size() == 0) {
				System.out.println(-1);
			} else {
				// object name length verify,how to make Align.
				size += sum.size();
				for (NOSObjectSummary obj : sum) {
					if(keys != null){
						keys.add(obj.getKey());
					} else{
						System.out.println("ObjectKey:" + obj.getKey().trim());
						System.out.println("ObjectSize:" + obj.getSize());
						System.out.println("LastModified:" + obj.getLastModified());
						System.out.println("-----------------------------------------");
					}
					marker = obj.getKey();
				}
				request.setMarker(marker);
			}
		} while (objectListing.isTruncated());
		
		if (keys == null) {
			System.out.println(size + " objects is listed");
		}
	}
	public static void clear(Nos client, String bucketName) {
		if (!client.doesBucketExist(bucketName))
			return;
		List<String> keys = new ArrayList<String>();
		listAllObjects(client, bucketName,"",keys);
		//List<NOSObjectSummary> sum = objectListing.getObjectSummaries();
		for(String key : keys) {
			client.deleteObject(bucketName,key);
		}
		client.deleteBucket(bucketName);
	}

	public static void clearMulitObject(Nos client, String bucketName) {
		 if (!client.doesBucketExist(bucketName))
		 return;

		ListMultipartUploadsRequest listRequest = new ListMultipartUploadsRequest(bucketName);

		MultipartUploadListing result = client.listMultipartUploads(listRequest);
		List<MultipartUpload> mulitUploads = result.getMultipartUploads();
		for (MultipartUpload upload : mulitUploads) {
			AbortMultipartUploadRequest abortRequest = new AbortMultipartUploadRequest(bucketName, upload.getKey(),
					upload.getUploadId());
			client.abortMultipartUpload(abortRequest);
		}
		clear(client, bucketName);
	}

	public static void clearAll(Nos client, String bucketName){
		
		 if (!client.doesBucketExist(bucketName))
			 return;
		
		/** 删除桶内的所有对象 **/
		ObjectListing objectListing = client.listObjects(bucketName);
		List<NOSObjectSummary> sum = objectListing.getObjectSummaries();
		for (NOSObjectSummary obj : sum) {
			client.deleteObject(bucketName, obj.getKey());
		}
			
		/**删除该桶的所有历史版本***/
		/*VersionListing  verlist= client.listVersions(bucketName);
		if(verlist != null){
			List<NOSVersionSummary>  versumList = verlist.getVersionSummaries();
			if(versumList != null){
				for(NOSVersionSummary versum : versumList){
					if(versum != null){
						client.deleteObject(bucketName, versum.getKey(), versum.getVersionId());
					}
				}
			}
		}*/
		
		/**删除未complete的分块**/
		ListMultipartUploadsRequest listRequest = new ListMultipartUploadsRequest(bucketName);
		MultipartUploadListing result = client.listMultipartUploads(listRequest);
		List<MultipartUpload> mulitUploads = result.getMultipartUploads();
		for (MultipartUpload upload : mulitUploads) {
			AbortMultipartUploadRequest abortRequest = new AbortMultipartUploadRequest(bucketName, upload.getKey(),
					upload.getUploadId());
			client.abortMultipartUpload(abortRequest);
		}
		
		/** 删除该桶**/
		client.deleteBucket(bucketName);
	}
	
	private static void clearMyAllBuckets(Nos client){
		
		List<Bucket>  buckets = client.listBuckets();
		for(Bucket bucket : buckets){
			clearAll(client, bucket.getName());
		}
	}
	
	public static void main(String[] args) {
		
		Credentials credentials;
		NosClient client;

		String credential = "credentials.properties";

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
			
			clearMyAllBuckets(client);
	}
	
}
