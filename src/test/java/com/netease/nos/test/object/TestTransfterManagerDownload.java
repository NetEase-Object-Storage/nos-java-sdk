package com.netease.nos.test.object;


import com.netease.cloud.ClientConfiguration;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.*;
import com.netease.cloud.services.nos.transfer.Download;
import com.netease.cloud.services.nos.transfer.TransferManager;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestTransfterManagerDownload {


    private static String bucketName = "hzfutianhui-test";
    private static String key = "sts.zip";
//    private static String key = "aa.gif";
    private static String accessKey = "d5af2030a48a4a2ab320ff183f11ded1";
    private String secretKey = "e40bdfb292b742b28ef142b734b9f084";
    private NosClient nosClient;

    @BeforeClass
    public void beforeClass(){
        Credentials credentials = new BasicCredentials(accessKey, secretKey);
        ClientConfiguration conf = new ClientConfiguration();
        // 设置 NosClient 使用的最大连接数
        conf.setMaxConnections(200);
        // 设置 socket 超时时间
        conf.setSocketTimeout(10000);
        // 设置失败请求重试次数
        conf.setMaxErrorRetry(2);
        conf.setIsSubdomain(true);
        nosClient = new NosClient(credentials,conf);
        nosClient.setEndpoint("nos-eastchina1.126.net");
//        nosClient.setEndpoint("nos.netease.com");
    }
    public void testUploadBigFile() throws IOException {
        InputStream is = TestTransfterManagerDownload.class.getClassLoader().getResourceAsStream("sts.zip");;
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName,
                key);
//你还可以在初始化分块上传时，设置文件的Content-Type
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("application/octet-stream");
        initRequest.setObjectMetadata(objectMetadata);
        InitiateMultipartUploadResult initResult = nosClient.initiateMultipartUpload(initRequest);
        String uploadId = initResult.getUploadId();

        byte[] buffer = new byte[10 * 1024 * 1024];
        int readLen;
        int buffSize = buffer.length;
        int partIndex = 1;
        while ((readLen = is.read(buffer, 0, buffSize)) != -1 ){
            InputStream partStream = new ByteArrayInputStream(buffer);
            nosClient.uploadPart(new UploadPartRequest().withBucketName(bucketName)
                    .withUploadId(uploadId).withInputStream(partStream)
                    .withKey(key).withPartSize(readLen).withPartNumber(partIndex));
            partIndex++;
        }

        List partETags = new ArrayList();

        int nextMarker = 0;
        while (true) {
            ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, key, uploadId);
            listPartsRequest.setPartNumberMarker(nextMarker);
            PartListing partList = nosClient.listParts(listPartsRequest);
            for (PartSummary ps : partList.getParts()) {
                nextMarker++;
                partETags.add(new PartETag(ps.getPartNumber(), ps.getETag()));
            }
            if (!partList.isTruncated()) {
                break;
            }
        }
        CompleteMultipartUploadRequest completeRequest =  new CompleteMultipartUploadRequest(bucketName,
                key, uploadId, partETags);
        CompleteMultipartUploadResult completeResult = nosClient.completeMultipartUpload(completeRequest);
    }


    @Test
    public void testRange1(){
        String key = "POPO.dmg";
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName,key);
        File file = new File("range1" + key);//755570 416500381

        TransferManager transferManager = new TransferManager(nosClient);
//        getObjectRequest.setRange(0,20643704 - 1);
        try {
            getObjectRequest.setRangeLength(20643704 + 20);//当rangeLength > content-length时，就直接下载，而不是在使用range下载了
        } catch (Exception e) {
            e.printStackTrace();
        }
        Download download = transferManager.download(getObjectRequest,file);
        try {
            download.waitForCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testRange2(){
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName,key);
        File file = new File("range2" + key);//755570 416500381

        TransferManager transferManager = new TransferManager(nosClient);
        getObjectRequest.setRange(0,416500381 - 1);

        Download download = transferManager.download(getObjectRequest,file);
        try {
            download.waitForCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testRange3(){
        String key = "POPO.dmg";
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName,key);
        File file = new File("range3" + key);//755570 416500381

        TransferManager transferManager = new TransferManager(nosClient);
        getObjectRequest.setRange(0,19976817 - 1);
        try {
            getObjectRequest.setRangeLength(3 * 1024 * 1024);//当rangeLength > content-length时，就直接下载，而不是在使用range下载了
        } catch (Exception e) {
            e.printStackTrace();
        }
        Download download = transferManager.download(getObjectRequest,file);
        try {
            download.waitForCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRange4(){
        String key = "POPO.dmg";
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName,key);
        try {
            getObjectRequest.setRangeLength(0);//当rangeLength > content-length时，就直接下载，而不是在使用range下载了
        } catch (Exception e) {
            e.getMessage().equalsIgnoreCase("angeLength must be bigger than zero");
        }
    }

    @Test
    public void testGetObject1(){

        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName,"POPO.dmg");
        File file = new File("popoObject1.dmg");//755570 416500381
        ObjectMetadata objectMetadata = nosClient.getObject(getObjectRequest,file);
    }
    @Test
    public void testGetObject2(){
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName,key);
        File file = new File("boect2" + key);//755570 416500381
        ObjectMetadata objectMetadata = nosClient.getObject(getObjectRequest,file);
    }

    @Test
    public void testDownload1(){
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName,key);
        File file = new File("download1" + key);//755570 416500381
        TransferManager transferManager = new TransferManager(nosClient);
        Download download = transferManager.download(getObjectRequest,file);
        try {
            download.waitForCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDownload2(){
        String key = "aa.gif";
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName,key);
        File file = new File("download2" + key);//755570 416500381
        TransferManager transferManager = new TransferManager(nosClient);
        Download download = transferManager.download(getObjectRequest,file);
        try {
            download.waitForCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
