package com.netease.nos.test.object;

import com.netease.cloud.ClientConfiguration;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.internal.StaticCredentialsProvider;
import com.netease.cloud.internal.crypto.*;
import com.netease.cloud.services.nos.NOSEncryptionClient;
import com.netease.cloud.services.nos.model.*;
import com.netease.cloud.util.IOUtils;
import com.netease.nos.test.utils.TestHelper;
import com.netease.nos.test.utils.TestHostConfig;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Properties;

import static com.netease.nos.test.object.DataProvidedForPutObject.createSampleFile;

/**
 * 客户端加密测试
 */
public class ObjectEncryptTest {
    Credentials credentials;
    NOSEncryptionClient client;

    private static final String credential = "credentials.properties";
    private String bucketName = "sync-jd";
    private String key = "object" + new Date().getTime();
    private boolean aes=true;       //True==利用AES算法，False==利用RSA算法

    private static final String keyFilePath = "./secret.key";

    // 这里给出了一个产生和保存秘钥信息的示例, 推荐使用256位秘钥.
    public static void buildAndSaveSymmetricKey() throws IOException, NoSuchAlgorithmException {
        // Generate symmetric 256 bit AES key.
        KeyGenerator symKeyGenerator = KeyGenerator.getInstance("AES");
        // JDK默认不支持256位长度的AES秘钥, SDK内部默认使用AES256加密数据
        // 运行时会打印如下异常信息java.security.InvalidKeyException: Illegal key size or default parameters
        // 解决办法参考接口文档的FAQ
        symKeyGenerator.init(256);
        SecretKey symKey = symKeyGenerator.generateKey();

        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(symKey.getEncoded());
        FileOutputStream keyfos = new FileOutputStream(keyFilePath);
        keyfos.write(x509EncodedKeySpec.getEncoded());
        keyfos.close();
    }

    // 这里给出了一个加载秘钥的示例
    public static SecretKey loadSymmetricAESKey() throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException, InvalidKeyException {
        // Read private key from file.
        File keyFile = new File(keyFilePath);
        FileInputStream keyfis = new FileInputStream(keyFile);
        byte[] encodedPrivateKey = new byte[(int) keyFile.length()];
        keyfis.read(encodedPrivateKey);
        keyfis.close();

        // Generate secret key.
        return new SecretKeySpec(encodedPrivateKey, "AES");
    }

    //测试完成以后移除秘钥文件
    public static void removeSymmetricKey() {
        File keyFile = new File(keyFilePath);
        keyFile.delete();
    }

    private static final String pubKeyPath = "./pub.key";
    private static final String priKeyPath = "./pri.key";
    private static final SecureRandom srand = new SecureRandom();

    //生成RSA公钥和私钥
    private static void buildAndSaveAsymKeyPair() throws IOException, NoSuchAlgorithmException {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(1024, srand);
        KeyPair keyPair = keyGenerator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        FileOutputStream fos = new FileOutputStream(pubKeyPath);
        fos.write(x509EncodedKeySpec.getEncoded());
        fos.close();

        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        fos = new FileOutputStream(priKeyPath);
        fos.write(pkcs8EncodedKeySpec.getEncoded());
        fos.close();
    }

    private static KeyPair loadAsymKeyPair()
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // load public
        File filePublicKey = new File(pubKeyPath);
        FileInputStream fis = new FileInputStream(filePublicKey);
        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
        fis.read(encodedPublicKey);
        fis.close();

        // load private
        File filePrivateKey = new File(priKeyPath);
        fis = new FileInputStream(filePrivateKey);
        byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
        fis.read(encodedPrivateKey);
        fis.close();

        // build RSA KeyPair
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return new KeyPair(publicKey, privateKey);
    }

    public static void removeAsymKeyPair() {
        new File(pubKeyPath).delete();
        new File(priKeyPath).delete();
    }

    @BeforeClass
    public void before() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        buildAndSaveSymmetricKey();
        buildAndSaveAsymKeyPair();
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
        EncryptionMaterials encryptionMaterials;
        if(aes){
            SecretKey symKey = loadSymmetricAESKey();
            encryptionMaterials= new EncryptionMaterials(symKey);
        }else{
            KeyPair keyPair = loadAsymKeyPair();
            encryptionMaterials = new EncryptionMaterials(keyPair);
        }
        // 使用AES/GCM模式，并将加密信息存储在文件元信息中.
        CryptoConfiguration cryptoConf = new CryptoConfiguration(CryptoMode.AuthenticatedEncryption)
                .withStorageMode(CryptoStorageMode.ObjectMetadata);
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setIsSubdomain(true);
        clientConfiguration.setProxyHost("localhost");
        clientConfiguration.setProxyPort(8888);
        client = new NOSEncryptionClient(new StaticCredentialsProvider(credentials),
                new StaticEncryptionMaterialsProvider(encryptionMaterials), clientConfiguration,
                cryptoConf);
        client.setEndpoint("nos-jd.163yun.com");
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(
                bucketName);
        if (!client.doesBucketExist(createBucketRequest.getBucketName())) {
            Bucket bucket = client.createBucket(createBucketRequest);
            Assert.assertNotNull(bucket);
            Assert.assertEquals(bucket.getName(), createBucketRequest.getBucketName());
            Assert.assertTrue(client.doesBucketExist(createBucketRequest.getBucketName()));
        }
    }

    @AfterClass
    public void after() {
        removeSymmetricKey();
        removeAsymKeyPair();
//        Clear.clearAll(client, bucketName);
    }

    @Test
    public void testPutObjectAES() {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, createSampleFile());
            client.putObject(putObjectRequest);
            NOSObject object = client.getObject(putObjectRequest.getBucketName(), putObjectRequest.getKey());
            String expectedMD5 = TestHelper.getMD5(putObjectRequest.getFile().getPath());
            String actualMd5 = TestHelper.getMD5(object.getObjectContent());
            Assert.assertEquals(expectedMD5, actualMd5);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testRangeObjectAES(){
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, createSampleFile());
            client.putObject(putObjectRequest);
            NOSObject object = client.getObject(putObjectRequest.getBucketName(), putObjectRequest.getKey());
            String expectedMD5 = TestHelper.getMD5(putObjectRequest.getFile().getPath());
            String actualMd5 = TestHelper.getMD5(object.getObjectContent());
            Assert.assertEquals(expectedMD5, actualMd5);
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName,key);
            getObjectRequest.setRange(0, 10);
            NOSObject nosObject = client.getObject(getObjectRequest);
            System.out.println(IOUtils.toString(nosObject.getObjectContent()));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
