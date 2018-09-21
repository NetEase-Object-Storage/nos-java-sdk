package com.netease.nos.test.object;

import com.netease.cloud.ClientConfiguration;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.*;
import com.netease.nos.test.utils.Clear;
import com.netease.nos.test.utils.TestHelper;
import com.netease.nos.test.utils.TestHostConfig;
import org.junit.After;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * SSE和SSEC新接口测试
 */
public class ObjectSSECTest {
    Credentials credentials;
    Credentials credentials2;
    NosClient client;
    NosClient client2;

    private static final String credential = "credentials.properties";

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
        ClientConfiguration configuration=new ClientConfiguration();
        configuration.setIsSubdomain(true);
//        configuration.setProxyHost("localhost");
//        configuration.setProxyPort(8888);
        client = new NosClient(credentials,configuration);

        CreateBucketRequest createBucketRequest = new CreateBucketRequest(
                DataProvidedForPutObject.bucketfortestputobject, TestHostConfig.region, false);
        if (!client.doesBucketExist(createBucketRequest.getBucketName())) {
            Bucket bucket = client.createBucket(createBucketRequest);
            Assert.assertNotNull(bucket);
            Assert.assertEquals(bucket.getName(), createBucketRequest.getBucketName());
            Assert.assertTrue(client.doesBucketExist(createBucketRequest.getBucketName()));
        }
    }
    @AfterClass
    public void after(){
        try{
            Clear.clear(client, DataProvidedForPutObject.bucketfortestputobject);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @AfterMethod
    public void afterTest(){
        Clear.clearObjects(client, DataProvidedForPutObject.bucketfortestputobject);
    }

    @Test(dataProvider = "putObjectNormalMD5", dataProviderClass = DataProvidedForPutObject.class)
    public void testPutObjectSSE(PutObjectRequest putObjectRequest) {
        try {
            putObjectRequest.setSSEAlgorithm("AES256");
            client.putObject(putObjectRequest);
            NOSObject object = client.getObject(putObjectRequest.getBucketName(), putObjectRequest.getKey());
            String expectedMD5 = TestHelper.getMD5(putObjectRequest.getFile().getPath());
            String actualMd5 =TestHelper.getMD5(object.getObjectContent());
            Assert.assertEquals(actualMd5.toUpperCase(), expectedMD5);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test(dataProvider = "putObjectNormalMD5", dataProviderClass = DataProvidedForPutObject.class)
    public void testPutObjectSSEC(PutObjectRequest putObjectRequest) {
        try {
            String key=putObjectRequest.setSSECRandomKey("AES256");
            client.putObject(putObjectRequest);
            //测试链式接口获取object
            GetObjectRequest getObjectRequest=new GetObjectRequest(putObjectRequest.getBucketName(), putObjectRequest.getKey()).withSSECKey("AES256",key);
            NOSObject object = client.getObject(getObjectRequest);
            String expectedMD5 = TestHelper.getMD5(putObjectRequest.getFile().getPath());
            String actualMd5 =TestHelper.getMD5(object.getObjectContent());
            Assert.assertEquals(actualMd5.toUpperCase(), expectedMD5);
            //测试另一种接口方式获取object
            getObjectRequest=new GetObjectRequest(putObjectRequest.getBucketName(), putObjectRequest.getKey());
            getObjectRequest.setSSECKey("AES256",key);
            object = client.getObject(getObjectRequest);
            actualMd5 =TestHelper.getMD5(object.getObjectContent());
            Assert.assertEquals(actualMd5.toUpperCase(), expectedMD5);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

}
