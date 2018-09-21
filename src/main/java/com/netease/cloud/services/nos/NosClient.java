package com.netease.cloud.services.nos;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.netease.cloud.util.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netease.cloud.ClientConfiguration;
import com.netease.cloud.ClientException;
import com.netease.cloud.DefaultRequest;
import com.netease.cloud.HttpMethod;
import com.netease.cloud.Request;
import com.netease.cloud.ServiceException;
import com.netease.cloud.WebServiceClient;
import com.netease.cloud.WebServiceRequest;
import com.netease.cloud.WebServiceResponse;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.auth.CredentialsProvider;
import com.netease.cloud.auth.Signer;
import com.netease.cloud.handlers.HandlerChainFactory;
import com.netease.cloud.handlers.RequestHandler;
import com.netease.cloud.http.ExecutionContext;
import com.netease.cloud.http.HttpMethodName;
import com.netease.cloud.http.HttpResponseHandler;
import com.netease.cloud.internal.StaticCredentialsProvider;
import com.netease.cloud.services.nos.internal.BucketNameUtils;
import com.netease.cloud.services.nos.internal.Constants;
import com.netease.cloud.services.nos.internal.DeleteObjectsResponse;
import com.netease.cloud.services.nos.internal.InputSubstream;
import com.netease.cloud.services.nos.internal.MD5DigestCalculatingInputStream;
import com.netease.cloud.services.nos.internal.Mimetypes;
import com.netease.cloud.services.nos.internal.NosAclHeaderResponseHnadler;
import com.netease.cloud.services.nos.internal.NosErrorResponseHandler;
import com.netease.cloud.services.nos.internal.NosMetadataResponseHandler;
import com.netease.cloud.services.nos.internal.NosObjectResponseHandler;
import com.netease.cloud.services.nos.internal.NosQueryStringSigner;
import com.netease.cloud.services.nos.internal.NosSigner;
import com.netease.cloud.services.nos.internal.NosStringSigner;
import com.netease.cloud.services.nos.internal.NosXmlResponseHandler;
import com.netease.cloud.services.nos.internal.ObjectExpirationHeaderHandler;
import com.netease.cloud.services.nos.internal.ProgressReportingInputStream;
import com.netease.cloud.services.nos.internal.RepeatableFileInputStream;
import com.netease.cloud.services.nos.internal.RepeatableInputStream;
import com.netease.cloud.services.nos.internal.ResponseHeaderHandlerChain;
import com.netease.cloud.services.nos.internal.ServiceUtils;
import com.netease.cloud.services.nos.internal.SimpleDataResponseHandler;
import com.netease.cloud.services.nos.internal.XmlWriter;
import com.netease.cloud.services.nos.model.AbortMultipartUploadRequest;
import com.netease.cloud.services.nos.model.Bucket;
import com.netease.cloud.services.nos.model.BucketLifecycleConfiguration;
import com.netease.cloud.services.nos.model.BucketLifecycleConfiguration.Rule;
import com.netease.cloud.services.nos.model.CannedAccessControlList;
import com.netease.cloud.services.nos.model.CompleteMultipartUploadRequest;
import com.netease.cloud.services.nos.model.CompleteMultipartUploadResult;
import com.netease.cloud.services.nos.model.CopyObjectRequest;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.DeduplicateRequest;
import com.netease.cloud.services.nos.model.DeduplicateResult;
import com.netease.cloud.services.nos.model.DeleteBucketLifecycleConfigurationRequest;
import com.netease.cloud.services.nos.model.DeleteBucketRequest;
import com.netease.cloud.services.nos.model.DeleteObjectRequest;
import com.netease.cloud.services.nos.model.DeleteObjectsRequest;
import com.netease.cloud.services.nos.model.DeleteObjectsRequest.KeyVersion;
import com.netease.cloud.services.nos.model.DeleteObjectsResult;
import com.netease.cloud.services.nos.model.GeneratePresignedUrlRequest;
import com.netease.cloud.services.nos.model.GetBucketAclRequest;
import com.netease.cloud.services.nos.model.GetBucketDedupRequest;
import com.netease.cloud.services.nos.model.GetBucketDedupResult;
import com.netease.cloud.services.nos.model.GetBucketDefault404Request;
import com.netease.cloud.services.nos.model.GetBucketDefault404Result;
import com.netease.cloud.services.nos.model.GetBucketLifecycleConfigurationRequest;
import com.netease.cloud.services.nos.model.GetBucketLocationRequest;
import com.netease.cloud.services.nos.model.GetBucketStatsRequest;
import com.netease.cloud.services.nos.model.GetBucketStatsResult;
import com.netease.cloud.services.nos.model.GetImageMetaInfoRequest;
import com.netease.cloud.services.nos.model.GetImageMode;
import com.netease.cloud.services.nos.model.GetImageRequest;
import com.netease.cloud.services.nos.model.GetObjectMetadataRequest;
import com.netease.cloud.services.nos.model.GetObjectRequest;
import com.netease.cloud.services.nos.model.GetVideoMetaInfoRequest;
import com.netease.cloud.services.nos.model.HeadBucketRequest;
import com.netease.cloud.services.nos.model.ImageMetadata;
import com.netease.cloud.services.nos.model.InitiateMultipartUploadRequest;
import com.netease.cloud.services.nos.model.InitiateMultipartUploadResult;
import com.netease.cloud.services.nos.model.ListBucketsRequest;
import com.netease.cloud.services.nos.model.ListMultipartUploadsRequest;
import com.netease.cloud.services.nos.model.ListObjectsRequest;
import com.netease.cloud.services.nos.model.ListPartsRequest;
import com.netease.cloud.services.nos.model.MediaSaveAsRequest;
import com.netease.cloud.services.nos.model.MoveObjectRequest;
import com.netease.cloud.services.nos.model.MultiObjectDeleteException;
import com.netease.cloud.services.nos.model.MultipartUploadListing;
import com.netease.cloud.services.nos.model.NOSException;
import com.netease.cloud.services.nos.model.NOSObject;
import com.netease.cloud.services.nos.model.NOSObjectInputStream;
import com.netease.cloud.services.nos.model.ObjectListing;
import com.netease.cloud.services.nos.model.ObjectMetadata;
import com.netease.cloud.services.nos.model.PartETag;
import com.netease.cloud.services.nos.model.PartListing;
import com.netease.cloud.services.nos.model.ProgressEvent;
import com.netease.cloud.services.nos.model.ProgressListener;
import com.netease.cloud.services.nos.model.PutBucketDedupRequest;
import com.netease.cloud.services.nos.model.PutBucketDefault404Request;
import com.netease.cloud.services.nos.model.PutObjectMetaRequest;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.cloud.services.nos.model.PutObjectResult;
import com.netease.cloud.services.nos.model.Region;
import com.netease.cloud.services.nos.model.SetBucketAclRequest;
import com.netease.cloud.services.nos.model.SetBucketLifecycleConfigurationRequest;
import com.netease.cloud.services.nos.model.UploadPartRequest;
import com.netease.cloud.services.nos.model.UploadPartResult;
import com.netease.cloud.services.nos.model.VideoFrameRequest;
import com.netease.cloud.services.nos.model.VideoMetadata;
import com.netease.cloud.services.nos.model.VideoTranscodingRequest;
import com.netease.cloud.services.nos.model.transform.Unmarshallers;
import com.netease.cloud.services.nos.model.transform.XmlResponsesSaxParser.CompleteMultipartUploadHandler;
import com.netease.cloud.transform.Unmarshaller;
import com.netease.cloud.util.json.JSONException;
import com.netease.cloud.util.json.JSONObject;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * The client for accessing the Netease NOS web service.
 * </p>
 * <p>
 * The client provides storage service on the Internet.
 * </p>
 * <p>
 * This client can be used to store and retrieve any amount of data, at any time, from anywhere on the web.
 * </p>
 */
public class NosClient extends WebServiceClient implements Nos {

    /**
     * Shared logger for client events
     */
    private static Logger log = LoggerFactory.getLogger(NosClient.class);

    /**
     * Responsible for handling error responses from all NOS service calls.
     */
    private NosErrorResponseHandler errorResponseHandler = new NosErrorResponseHandler();

    /**
     * Shared response handler for operations with no response.
     */
    private NosXmlResponseHandler<Void> voidResponseHandler = new NosXmlResponseHandler<Void>(null);

    /**
     * Utilities for validating bucket names
     */
    private final BucketNameUtils bucketNameUtils = new BucketNameUtils();

    /**
     * Provider for credentials.
     */
    private CredentialsProvider CredentialsProvider;

    //由于目前服务端不支持在上传时设置元数据，所以由SDK负责在上传初始化时维护元数据Map，并在上传完成或终止时消费元数据Map
    private Map<String, Map<String, String>> specialHeadersMap = new ConcurrentHashMap<String, Map<String, String>>();

    /**
     * <p>
     * Constructs a new NOS client that will make <b>anonymous</b> requests to NOS.
     * </p>
     * <p>
     * Only a subset of the NOS API will work with anonymous (i.e. unsigned) requests, but this can prove useful in some
     * situations. For example:
     * </p>
     */
    public NosClient() {
        this((Credentials) null);
    }

    /**
     * Constructs a new NOS client using the specified credentials to access NOS.
     *
     * @param Credentials The credentials to use when making requests to NOS with this client.
     */
    public NosClient(Credentials Credentials) {
        this(Credentials, new ClientConfiguration());
    }

    /**
     * Constructs a new NOS client using the specified credentials and client configuration to access NOS.
     *
     * @param Credentials         The credentials to use when making requests to NOS with this client.
     * @param clientConfiguration The client configuration options controlling how this client connects to NOS (e.g. proxy settings,
     *                            retry counts, etc).
     */
    public NosClient(Credentials Credentials, ClientConfiguration clientConfiguration) {
        super(clientConfiguration);
        this.CredentialsProvider = new StaticCredentialsProvider(Credentials);
        init();
    }

    /**
     * Constructs a new NOS client using the specified credentials provider to access NOS.
     *
     * @param credentialsProvider The credentials provider which will provide credentials to authenticate requests with services.
     */
    public NosClient(CredentialsProvider credentialsProvider) {
        this(credentialsProvider, new ClientConfiguration());
    }

    /**
     * Constructs a new NOS client using the specified credentials and client configuration to access NOS.
     *
     * @param credentialsProvider The credentials provider which will provide credentials to authenticate requests with services.
     * @param clientConfiguration The client configuration options controlling how this client connects to NOS (e.g. proxy settings,
     *                            retry counts, etc).
     */
    public NosClient(CredentialsProvider credentialsProvider, ClientConfiguration clientConfiguration) {
        super(clientConfiguration);
        this.CredentialsProvider = credentialsProvider;
        init();
    }

    private void init() {
        HandlerChainFactory chainFactory = new HandlerChainFactory();
        requestHandlers.addAll(chainFactory.newRequestHandlerChain("/com/netease/cloud/services/nos/request.handlers"));
    }

    /**
     * Appends a request handler to the list of registered handlers that are run as part of a request's lifecycle.
     *
     * @param requestHandler The new handler to add to the current list of request handlers.
     */
    @Override
    public void addRequestHandler(RequestHandler requestHandler) {
        requestHandlers.add(requestHandler);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#listObjects(java.lang.String)
     */
    @Override
    public ObjectListing listObjects(String bucketName) throws ClientException, ServiceException {
        return listObjects(new ListObjectsRequest(bucketName, null, null, null, null));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#listObjects(com.netease.cloud.services .nos.model.ListObjectsRequest)
     */
    @Override
    public ObjectListing listObjects(ListObjectsRequest listObjectsRequest) throws ClientException, ServiceException {
        assertParameterNotNull(listObjectsRequest.getBucketName(),
                "The bucket name parameter must be specified when listing objects in a bucket");

        Request<ListObjectsRequest> request = createRequest(listObjectsRequest.getBucketName(), null,
                listObjectsRequest, HttpMethodName.GET);
        if (listObjectsRequest.getPrefix() != null) {
            request.addParameter("prefix", listObjectsRequest.getPrefix());
        }
        if (listObjectsRequest.getMarker() != null) {
            request.addParameter("marker", listObjectsRequest.getMarker());
        }
        if (listObjectsRequest.getDelimiter() != null) {
            request.addParameter("delimiter", listObjectsRequest.getDelimiter());
        }
        if (listObjectsRequest.getMaxKeys() != null && listObjectsRequest.getMaxKeys().intValue() >= 0) {
            request.addParameter("max-keys", listObjectsRequest.getMaxKeys().toString());
        }

        return invoke(request, new Unmarshallers.ListObjectsUnmarshaller(), listObjectsRequest.getBucketName(), null);
    }

    @Override
    public GetBucketStatsResult getBucketStats(String bucketName) throws ClientException, ServiceException {
        assertParameterNotNull(bucketName,
                "The bucket name parameter must be specified when get bucket stats");
        Request<GetBucketStatsRequest> request = createRequest(bucketName, null, new GetBucketStatsRequest(),
                HttpMethodName.GET);

        request.addParameter("bucketstat", null);

        return invoke(request, new Unmarshallers.GetBucketStatsUnmarshaller(), bucketName, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#listBuckets()
     */
    @Override
    public List<Bucket> listBuckets(ListBucketsRequest listBucketsRequest) throws ClientException, ServiceException {
        Request<ListBucketsRequest> request = createRequest(null, null, listBucketsRequest, HttpMethodName.GET);
        return invoke(request, new Unmarshallers.ListBucketsUnmarshaller(), null, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#listBuckets()
     */
    @Override
    public List<Bucket> listBuckets() throws ClientException, ServiceException {
        return listBuckets(new ListBucketsRequest());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#createBucket(java.lang.String)
     */
    @Override
    public Bucket createBucket(String bucketName) throws ClientException, ServiceException {
        return createBucket(new CreateBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#createBucket(java.lang.String, com.netease.cloud.services.nos.model.Region)
     */
    @Override
    public Bucket createBucket(String bucketName, Region region, boolean deduplicate) throws ClientException,
            ServiceException {
        return createBucket(new CreateBucketRequest(bucketName, region, deduplicate));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#createBucket(java.lang.String, java.lang.String)
     */
    @Override
    public Bucket createBucket(String bucketName, String region, boolean deduplicate) throws ClientException,
            ServiceException {
        return createBucket(new CreateBucketRequest(bucketName, region, deduplicate));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#createBucket(com.netease.cloud.services .nos.model.CreateBucketRequest)
     */
    @Override
    public Bucket createBucket(CreateBucketRequest createBucketRequest) throws ClientException, ServiceException {
        assertParameterNotNull(createBucketRequest,
                "The CreateBucketRequest parameter must be specified when creating a bucket");

        String bucketName = createBucketRequest.getBucketName();
        String region = createBucketRequest.getRegion();
        boolean deduplicate = createBucketRequest.isDeduplicate();
        CannedAccessControlList acl = createBucketRequest.getCannedAcl();

        assertParameterNotNull(bucketName, "The bucket name parameter must be specified when creating a bucket");

        if (bucketName != null) {
            bucketName = bucketName.trim();
        }
        bucketNameUtils.validateBucketName(bucketName);

        Request<CreateBucketRequest> request = createRequest(bucketName, null, createBucketRequest, HttpMethodName.PUT);
        if (acl != null) {
            request.addHeader(Headers.NOS_CANNED_ACL, acl.toString());
        } else {
            request.addHeader(Headers.NOS_CANNED_ACL, CannedAccessControlList.Private.toString());
        }

        XmlWriter xml = new XmlWriter();
        xml.start("CreateBucketConfiguration", "xmlns", Constants.XML_NAMESPACE);
        if (region != null) {
            xml.start("LocationConstraint").value(region).end();
        }
        xml.start("ObjectDeduplicate").value(Boolean.toString(deduplicate)).end();
        xml.end();

        request.setContent(new ByteArrayInputStream(xml.getBytes()));

        invoke(request, voidResponseHandler, bucketName, null);

        return new Bucket(bucketName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#getBucketAcl(java.lang.String)
     */
    @Override
    public CannedAccessControlList getBucketAcl(String bucketName) throws ClientException, ServiceException {
        assertParameterNotNull(bucketName, "The bucket name parameter must be specified when requesting a bucket's ACL");

        return getBucketAcl(new GetBucketAclRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#getBucketAcl(com.netease.cloud.services .NOS.GetBucketAclRequest)
     */
    @Override
    public CannedAccessControlList getBucketAcl(GetBucketAclRequest getBucketAclRequest) throws ClientException,
            ServiceException {

        assertParameterNotNull(getBucketAclRequest,
                "The getBucketAclRequest parameter must be specified when requesting a bucket's ACL");
        String bucketName = getBucketAclRequest.getBucketName();
        assertParameterNotNull(bucketName, "The bucket name parameter must be specified when requesting a bucket's ACL");

        Request<GetBucketAclRequest> request = createRequest(bucketName, null, getBucketAclRequest, HttpMethodName.GET);
        request.addParameter("acl", null);

        return invoke(request, new NosAclHeaderResponseHnadler(), bucketName, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#setBucketAcl(com.netease.cloud.services .NOS.SetBucketAclRequest)
     */
    @Override
    public void setBucketAcl(SetBucketAclRequest setBucketAclRequest) throws ClientException, ServiceException {
        String bucketName = setBucketAclRequest.getBucketName();
        CannedAccessControlList cannedAcl = setBucketAclRequest.getCannedAcl();
        assertParameterNotNull(bucketName, "The bucket name parameter must be specified when setting a bucket's ACL");
        assertParameterNotNull(cannedAcl, "The cannedAcl parameter must be specified when setting a bucket's ACL");

        Request<SetBucketAclRequest> request = createRequest(bucketName, null, setBucketAclRequest, HttpMethodName.PUT);
        request.addParameter("acl", null);
        request.addHeader(Headers.NOS_CANNED_ACL, cannedAcl.toString());

        invoke(request, voidResponseHandler, bucketName, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#setBucketAcl(java.lang.String,
     * com.netease.cloud.services.nos.model.CannedAccessControlList)
     */
    @Override
    public void setBucketAcl(String bucketName, CannedAccessControlList acl) throws ClientException, ServiceException {
        assertParameterNotNull(bucketName, "The bucket name parameter must be specified when setting a bucket's ACL");
        assertParameterNotNull(acl, "The ACL parameter must be specified when setting a bucket's ACL");

        setBucketAcl(new SetBucketAclRequest(bucketName, acl));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#getBucketDedup(java.lang.String)
     */
    @Override
    public GetBucketDedupResult getBucketDedup(String bucketName) throws ClientException, ServiceException {
        return getBucketDedup(new GetBucketDedupRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#getBucketDedup(com.netease.cloud.services .nos.model.GetBucketDedupRequest)
     */
    @Override
    public GetBucketDedupResult getBucketDedup(GetBucketDedupRequest getBucketDedupRequest) throws ClientException,
            ServiceException {
        assertParameterNotNull(getBucketDedupRequest,
                "The putBucketDedupRequest parameter must be specified when get bucket dedup");

        String bucketName = getBucketDedupRequest.getBucketName();

        assertParameterNotNull(bucketName, "The bucket name parameter must be specified when get bucket dedup");

        Request<GetBucketDedupRequest> request = createRequest(bucketName, null, getBucketDedupRequest,
                HttpMethodName.GET);

        request.addParameter("deduplication", null);

        return invoke(request, new Unmarshallers.GetBucketDedupResultUnmarshaller(), bucketName, null);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#setBucketDedup(java.lang.String, java.lang.String)
     */
    @Override
    public void setBucketDedup(String bucketName, String dedupStatus) throws ClientException, ServiceException {
        this.setBucketDedup(new PutBucketDedupRequest(bucketName, dedupStatus));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#setBucketDedup(com.netease.cloud.services .nos.model.PutBucketDedupRequest)
     */
    @Override
    public void setBucketDedup(PutBucketDedupRequest putBucketDedupRequest) throws ClientException, ServiceException {
        assertParameterNotNull(putBucketDedupRequest,
                "The putBucketDedupRequest parameter must be specified when set bucket dedup");

        String bucketName = putBucketDedupRequest.getBucketName();
        String dedupStatus = putBucketDedupRequest.getDedupStatus();

        assertParameterNotNull(bucketName, "The bucket name parameter must be specified when set bucket dedup");
        assertParameterNotNull(dedupStatus, "The dedupStatus name parameter must be specified when set bucket dedup");

        if (bucketName != null) {
            bucketName = bucketName.trim();
        }
        bucketNameUtils.validateBucketName(bucketName);

        Request<PutBucketDedupRequest> request = createRequest(bucketName, null, putBucketDedupRequest,
                HttpMethodName.PUT);

        /*
         * We can only send the CreateBucketConfiguration if we're *not* creating a bucket in the US region.
         */

        XmlWriter xml = new XmlWriter();
        xml.start("DeduplicationConfiguration", "xmlns", Constants.XML_NAMESPACE);
        xml.start("Status").value(dedupStatus).end();
        xml.end();

        request.addParameter("deduplication", null);
        request.setContent(new ByteArrayInputStream(xml.getBytes()));

        invoke(request, voidResponseHandler, bucketName, null);
    }

    public GetBucketDefault404Result getBucketDefault404(String bucketName) throws ClientException, ServiceException {
        return getBucketDefault404(new GetBucketDefault404Request(bucketName));
    }

    public GetBucketDefault404Result getBucketDefault404(GetBucketDefault404Request getBucketDefault404Request) throws ClientException, ServiceException {
        assertParameterNotNull(getBucketDefault404Request,
                "The putBucketDedupRequest parameter must be specified when get bucket default404");

        String bucketName = getBucketDefault404Request.getBucketName();
        assertParameterNotNull(bucketName, "The bucket name parameter must be specified when get bucket default404");

        Request<GetBucketDefault404Request> request = createRequest(bucketName, null, getBucketDefault404Request,
                HttpMethodName.GET);

        request.addParameter("default404", null);

        return invoke(request, new Unmarshallers.GetBucketDefault404Unmarshaller(), bucketName, null);
    }

    public void setBucketDefault404(String bucketName, String default404Object) throws ClientException, ServiceException {
        this.setBucketDefault404(new PutBucketDefault404Request(bucketName, default404Object));
    }

    public void setBucketDefault404(PutBucketDefault404Request putBucketDefault404Request) throws ClientException, ServiceException {
        assertParameterNotNull(putBucketDefault404Request,
                "The putBucketDefault404Request parameter must be specified when set bucket dedup");

        String bucketName = putBucketDefault404Request.getBucketName();
        String default404Object = putBucketDefault404Request.getDefault404Object();

        assertParameterNotNull(bucketName, "The bucket name parameter must be specified when set bucket default404");

        if (bucketName != null) {
            bucketName = bucketName.trim();
        }
        bucketNameUtils.validateBucketName(bucketName);

        Request<PutBucketDefault404Request> request = createRequest(bucketName, null, putBucketDefault404Request,
                HttpMethodName.PUT);

        XmlWriter xml = new XmlWriter();
        xml.start("Default404Configuration", "xmlns", Constants.XML_NAMESPACE);
        xml.start("Key").value(default404Object == null ? "" : default404Object).end();
        xml.end();

        request.addParameter("default404", null);
        request.setContent(new ByteArrayInputStream(xml.getBytes()));

        invoke(request, voidResponseHandler, bucketName, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#deduplicate(com.netease.cloud.services .model.DeduplicateRequest)
     */
    @Override
    public boolean isDeduplicate(DeduplicateRequest deduplicateRequest) throws ClientException, ServiceException {

        assertParameterNotNull(deduplicateRequest, "The DeduplicateRequest parameter must be specified");
        assertParameterNotNull(deduplicateRequest.getBucketName(), "The bucket name parameter must be specified");
        assertParameterNotNull(deduplicateRequest.getKey(), "The key parameter must be specified");
        assertParameterNotNull(deduplicateRequest.getMD5Digest(), "The MD5Digest parameter must be specified");

        Request<DeduplicateRequest> request = createRequest(deduplicateRequest.getBucketName(),
                deduplicateRequest.getKey(), deduplicateRequest, HttpMethodName.PUT);

        request.addHeader(Headers.X_NOS_OBJECT_MD5, deduplicateRequest.getMD5Digest());

        request.addParameter("deduplication", null);

        if (deduplicateRequest.getStorageClass() != null) {
            request.addHeader(Headers.STORAGE_CLASS, deduplicateRequest.getStorageClass());
        }

        if (deduplicateRequest.getObjectMetadata() != null) {
            populateRequestMetadata(request, deduplicateRequest.getObjectMetadata());
        }

        DeduplicateResult result = invoke(request, new Unmarshallers.DeduplicateResultUnmarshaller(),
                deduplicateRequest.getBucketName(), deduplicateRequest.getKey());

        return result.isObjectExist();

    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.services.nos.Nos#getBucketLocation(com.netease.cloud
     * .services.nos.model.GetBucketLocationRequest)
     */
    @Override
    public String getBucketLocation(GetBucketLocationRequest getBucketLocationRequest) throws ClientException,
            ServiceException {
        assertParameterNotNull(getBucketLocationRequest,
                "The request parameter must be specified when requesting a bucket's location");
        String bucketName = getBucketLocationRequest.getBucketName();
        assertParameterNotNull(bucketName,
                "The bucket name parameter must be specified when requesting a bucket's location");

        Request<GetBucketLocationRequest> request = createRequest(bucketName, null, getBucketLocationRequest,
                HttpMethodName.GET);
        request.addParameter("location", null);

        return invoke(request, new Unmarshallers.BucketLocationUnmarshaller(), bucketName, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.services.nos.Nos#getBucketLocation(java.lang.String)
     */
    @Override
    public String getBucketLocation(String bucketName) throws ClientException, ServiceException {
        return getBucketLocation(new GetBucketLocationRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#getObjectMetadata(java.lang.String, java.lang.String)
     */
    @Override
    public ObjectMetadata getObjectMetadata(String bucketName, String key) throws ClientException, ServiceException {
        return getObjectMetadata(new GetObjectMetadataRequest(bucketName, key));
    }

    public ObjectMetadata getObjectMetadata(String bucketName, String key, String versionId) throws ClientException,
            ServiceException {
        return getObjectMetadata(new GetObjectMetadataRequest(bucketName, key, versionId));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#getObjectMetadata(com.netease.cloud.services .nos.model.GetObjectMetadataRequest)
     */
    @Override
    public ObjectMetadata getObjectMetadata(GetObjectMetadataRequest getObjectMetadataRequest) throws ClientException,
            ServiceException {
        assertParameterNotNull(getObjectMetadataRequest,
                "The GetObjectMetadataRequest parameter must be specified when requesting an object's metadata");

        String bucketName = getObjectMetadataRequest.getBucketName();
        String key = getObjectMetadataRequest.getKey();
        String versionId = getObjectMetadataRequest.getVersionId();
        Date modifiedSinceConstraint = getObjectMetadataRequest.getModifiedSinceConstraint();

        assertParameterNotNull(bucketName,
                "The bucket name parameter must be specified when requesting an object's metadata");
        assertParameterNotNull(key, "The key parameter must be specified when requesting an object's metadata");

        Request<GetObjectMetadataRequest> request = createRequest(bucketName, key, getObjectMetadataRequest,
                HttpMethodName.HEAD);
        if (versionId != null) {
            request.addParameter("versionId", versionId);
        }
        if (modifiedSinceConstraint != null) {
            addDateHeader(request, Headers.GET_OBJECT_IF_MODIFIED_SINCE,
                    getObjectMetadataRequest.getModifiedSinceConstraint());
        }

        return invoke(request, new NosMetadataResponseHandler(), bucketName, key);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#getObjectVersions(com.netease.cloud.services .model.GetObjectVersionsRequest)
     */
	/*public GetObjectVersionsResult getObjectVersions(GetObjectVersionsRequest getObjectVersionsRequest)
			throws ClientException, ServiceException {

		assertParameterNotNull(getObjectVersionsRequest,
				"The GetObjectVersionsRequest parameter must be specified when requesting an object");
		assertParameterNotNull(getObjectVersionsRequest.getBucketName(),
				"The bucket name parameter must be specified when requesting an object");
		assertParameterNotNull(getObjectVersionsRequest.getKey(),
				"The key parameter must be specified when requesting an object");

		Request<GetObjectVersionsRequest> request = createRequest(getObjectVersionsRequest.getBucketName(),
				getObjectVersionsRequest.getKey(), getObjectVersionsRequest, HttpMethodName.GET);

		request.addParameter("versions", null);

		return invoke(request, new Unmarshallers.GetObejctVersionsResultUnmarshaller(),
				getObjectVersionsRequest.getBucketName(), getObjectVersionsRequest.getKey());

	}*/

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#getObjectVersions(java.lang.String, java.lang.String)
     */
	/*public GetObjectVersionsResult getObjectVersions(String bucketName, String key) throws ClientException,
			ServiceException {
		return getObjectVersions(new GetObjectVersionsRequest(bucketName, key));
	}*/

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#getObject(java.lang.String, java.lang.String)
     */
    @Override
    public NOSObject getObject(String bucketName, String key) throws ClientException, ServiceException {
        return getObject(new GetObjectRequest(bucketName, key));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#doesObjectExist(java.lang.String)
     */
    @Override
    public boolean doesObjectExist(String bucketName, String key, String versionId) {
        try {
            // if(!doesBucketExist(bucketName)){
            // return false;
            // }
            assertParameterNotNull(bucketName, "The bucketName parameter must be specified .");
            assertParameterNotNull(key, "The key parameter must be specified .");
            getObjectMetadata(bucketName, key, versionId);

            return true;
        } catch (ServiceException ase) {

            switch (ase.getStatusCode()) {
                case 403:
                    /*
                     * A permissions error don't know if the object is existed or not
                     */
                    throw ase;
                case 404:
                    return false;
                default:
                    throw ase;
            }
        }
    }

    /**
     * check bucket existence
     *
     * @param bucketName
     * @return true if bucket exists, ignoring ownerness
     */
    @Override
    public boolean doesBucketExist(String bucketName) {
        HeadBucketRequest headBucketRequest = new HeadBucketRequest(bucketName);
        return doesBucketExist(headBucketRequest);
    }

    /**
     * @param headBucketRequest
     * @return true if bucket exists, ignoring ownerness
     */
    public boolean doesBucketExist(HeadBucketRequest headBucketRequest) {

        try {
            assertParameterNotNull(headBucketRequest.getBucketName(), "The bucketName parameter must be specified .");
            Request<HeadBucketRequest> request = createRequest(headBucketRequest.getBucketName(), null,
                    headBucketRequest, HttpMethodName.HEAD);
            invoke(request, voidResponseHandler, headBucketRequest.getBucketName(), null);
            return true;
        } catch (ServiceException e) {

            /*
             * If we have no credentials, or we detect a problem with the credentials we used, go ahead and throw the
             * error so we don't mask that problem as thinking that the bucket does exist.
             */
            if (CredentialsProvider.getCredentials() == null) {
                throw e;
            }
            if ("InvalidAccessKeyId".equalsIgnoreCase(e.getErrorCode())
                    || "SignatureDoesNotMatch".equalsIgnoreCase(e.getErrorCode())) {
                throw e;
            }

            switch (e.getStatusCode()) {
                case 403:
                    /*
                     * bucket is unique in nos globally A permissions error means the bucket exists, but is owned by another
                     * account.
                     */
                    return true;
                case 404:
                    return false;
                default:
                    throw e;
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#getObject(com.netease.cloud.services.nos .model.GetObjectRequest)
     */
    @Override
    public NOSObject getObject(GetObjectRequest getObjectRequest) throws ClientException, ServiceException {
        assertParameterNotNull(getObjectRequest,
                "The GetObjectRequest parameter must be specified when requesting an object");
        assertParameterNotNull(getObjectRequest.getBucketName(),
                "The bucket name parameter must be specified when requesting an object");
        assertParameterNotNull(getObjectRequest.getKey(),
                "The key parameter must be specified when requesting an object");

        Request<GetObjectRequest> request = createRequest(getObjectRequest.getBucketName(), getObjectRequest.getKey(),
                getObjectRequest, HttpMethodName.GET);

        if (getObjectRequest.getVersionId() != null) {
            request.addParameter("versionId", getObjectRequest.getVersionId());
        }

        // Range
        if (getObjectRequest.getRange() != null) {
            long[] range = getObjectRequest.getRange();
            request.addHeader(Headers.RANGE, "bytes=" + Long.toString(range[0]) + "-" + Long.toString(range[1]));
        }

        addDateHeader(request, Headers.GET_OBJECT_IF_MODIFIED_SINCE, getObjectRequest.getModifiedSinceConstraint());

        ProgressListener progressListener = getObjectRequest.getProgressListener();
        try {
            NOSObject NOSObject = invoke(request, new NosObjectResponseHandler(), getObjectRequest.getBucketName(),
                    getObjectRequest.getKey());

            /*
             * TODO: For now, it's easiest to set there here in the client, but we could push this back into the
             * response handler with a little more work.
             */
            NOSObject.setBucketName(getObjectRequest.getBucketName());
            NOSObject.setKey(getObjectRequest.getKey());

            if (progressListener != null) {
                NOSObjectInputStream input = NOSObject.getObjectContent();
                ProgressReportingInputStream progressReportingInputStream = new ProgressReportingInputStream(input,
                        progressListener);
                progressReportingInputStream.setFireCompletedEvent(true);
                input = new NOSObjectInputStream(progressReportingInputStream, input.getHttpRequest());
                NOSObject.setObjectContent(input);
                fireProgressEvent(progressListener, ProgressEvent.STARTED_EVENT_CODE);
            }

            /*
             * TODO: It'd be nice to check the integrity of the data was received from NOS, but we'd have to read off
             * the stream and buffer the contents somewhere in order to do that.
             *
             * We could consider adding an option for this in the future, or wrapping the InputStream in another
             * implementation of FilterInputStream that would calculate the checksum when the user reads the data and
             * then notify them somehow if there was a problem.
             */
            return NOSObject;
        } catch (NOSException ase) {
            /*
             * If the request failed because one of the specified constraints was not met (ex: matching ETag, modified
             * since date, etc.), then return null, so that users don't have to wrap their code in try/catch blocks and
             * check for this status code if they want to use constraints.
             */
            if (ase.getStatusCode() == 412 || ase.getStatusCode() == 304) {
                fireProgressEvent(progressListener, ProgressEvent.CANCELED_EVENT_CODE);
                return null;
            }

            fireProgressEvent(progressListener, ProgressEvent.FAILED_EVENT_CODE);
            throw ase;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#getObject(com.netease.cloud.services.nos .model.GetObjectRequest, java.io.File)
     */
    @Override
    public ObjectMetadata getObject(GetObjectRequest getObjectRequest, File destinationFile) throws ClientException,
            ServiceException {
        assertParameterNotNull(destinationFile,
                "The destination file parameter must be specified when downloading an object directly to a file");

        NOSObject NOSObject = getObject(getObjectRequest);
        // getObject can return null if constraints were specified but not met
        if (NOSObject == null) {
            return null;
        }
        ServiceUtils.downloadObjectToFile(NOSObject, destinationFile);
        return NOSObject.getObjectMetadata();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#deleteBucket(java.lang.String)
     */
    @Override
    public void deleteBucket(String bucketName) throws ClientException, ServiceException {
        deleteBucket(new DeleteBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#deleteBucket(com.netease.cloud.services .nos.model.DeleteBucketRequest)
     */
    @Override
    public void deleteBucket(DeleteBucketRequest deleteBucketRequest) throws ClientException, ServiceException {
        assertParameterNotNull(deleteBucketRequest,
                "The DeleteBucketRequest parameter must be specified when deleting a bucket");

        String bucketName = deleteBucketRequest.getBucketName();
        assertParameterNotNull(bucketName, "The bucket name parameter must be specified when deleting a bucket");

        Request<DeleteBucketRequest> request = createRequest(bucketName, null, deleteBucketRequest,
                HttpMethodName.DELETE);
        invoke(request, voidResponseHandler, bucketName, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#putObject(java.lang.String, java.lang.String, java.io.File)
     */
    public PutObjectResult putObject(String bucketName, File file) throws ClientException, ServiceException {
        return putObject(new PutObjectRequest(bucketName, file.getName(), file).withMetadata(new ObjectMetadata()));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#putObject(java.lang.String, java.lang.String, java.io.File)
     */
    @Override
    public PutObjectResult putObject(String bucketName, String key, File file) throws ClientException, ServiceException {
        return putObject(new PutObjectRequest(bucketName, key, file).withMetadata(new ObjectMetadata()));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#putObject(java.lang.String, java.lang.String, java.io.InputStream,
     * com.netease.cloud.services.nos.model.NOSObjectMetadata)
     */
    @Override
    public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata)
            throws ClientException, ServiceException {
        return putObject(new PutObjectRequest(bucketName, key, input, metadata));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#putObject(com.netease.cloud.services.nos .model.PutObjectRequest)
     */
    @Override
    public PutObjectResult putObject(PutObjectRequest putObjectRequest) throws ClientException, ServiceException {
        assertParameterNotNull(putObjectRequest,
                "The PutObjectRequest parameter must be specified when uploading an object");

        String bucketName = putObjectRequest.getBucketName();
        String key = putObjectRequest.getKey();
        ObjectMetadata metadata = putObjectRequest.getMetadata();
        InputStream input = putObjectRequest.getInputStream();
        ProgressListener progressListener = putObjectRequest.getProgressListener();
        // putObjectRequest.getMetadata().setUserMetadata(userMetadata);
        if (metadata == null) {
            metadata = new ObjectMetadata();
        }

        assertParameterNotNull(bucketName, "The bucket name parameter must be specified when uploading an object");

        // If a file is specified for upload, we need to pull some additional
        // information from it to auto-configure a few options
        if (putObjectRequest.getFile() != null) {
            File file = putObjectRequest.getFile();

            // Always set the content length, even if it's already set
            metadata.setContentLength(file.length());

            // Only set the content type if it hasn't already been set
            if (metadata.getContentType() == null) {
                metadata.setContentType(Mimetypes.getInstance().getMimetype(file));
            }

            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                byte[] md5Hash = ServiceUtils.computeMD5Hash(fileInputStream);
                metadata.setContentMD5(BinaryUtils.toHex(md5Hash));
            } catch (Exception e) {
                throw new ClientException("Unable to calculate MD5 hash: " + e.getMessage(), e);
            } finally {
                try {
                    fileInputStream.close();
                } catch (Exception e) {
                }
            }

            try {
                input = new RepeatableFileInputStream(file);
            } catch (FileNotFoundException fnfe) {
                throw new ClientException("Unable to find file to upload", fnfe);
            }
        }

        Request<PutObjectRequest> request = createRequest(bucketName, key, putObjectRequest, HttpMethodName.PUT);

        if (putObjectRequest.getCannedAcl() != null) {
            request.addHeader(Headers.NOS_CANNED_ACL, putObjectRequest.getCannedAcl().toString());
        }

        if (putObjectRequest.getStorageClass() != null) {
            request.addHeader(Headers.STORAGE_CLASS, putObjectRequest.getStorageClass());
        }

        if (key == null) {
            request.addParameter("uploadObject", null);
        }

        // Use internal interface to differentiate 0 from unset.
        if (metadata.getRawMetadata().get(Headers.CONTENT_LENGTH) == null) {
            /*
             * There's nothing we can do except for let the HTTP client buffer the input stream contents if the caller
             * doesn't tell us how much data to expect in a stream since we have to explicitly tell NOS how much we're
             * sending before we start sending any of it.
             */
            log.warn("No content length specified for stream data.  "
                    + "Stream contents will be buffered in memory and could result in " + "out of memory errors.");
        }

        if (progressListener != null) {
            input = new ProgressReportingInputStream(input, progressListener);
            fireProgressEvent(progressListener, ProgressEvent.STARTED_EVENT_CODE);
        }
        if (!input.markSupported()) {
            input = new RepeatableInputStream(input, Constants.DEFAULT_STREAM_BUFFER_SIZE);
        }

        MD5DigestCalculatingInputStream md5DigestStream = null;
        if (metadata.getContentMD5() == null) {
            /*
             * If the user hasn't set the content MD5, then we don't want to buffer the whole stream in memory just to
             * calculate it. Instead, we can calculate it on the fly and validate it with the returned ETag from the
             * object upload.
             */
            try {
                md5DigestStream = new MD5DigestCalculatingInputStream(input);
                input = md5DigestStream;
            } catch (NoSuchAlgorithmException e) {
                log.warn("No MD5 digest algorithm available.  Unable to calculate "
                        + "checksum and verify data integrity.", e);
            }
        }

        if (metadata.getContentType() == null) {
            /*
             * Default to the "application/octet-stream" if the user hasn't specified a content type.
             */
            metadata.setContentType(Mimetypes.MIMETYPE_OCTET_STREAM);
        }

        populateRequestMetadata(request, metadata);
        request.setContent(input);

        ObjectMetadata returnedMetadata = null;
        try {
            returnedMetadata = invoke(request, new NosMetadataResponseHandler(), bucketName, key);
        } catch (ClientException ace) {
            fireProgressEvent(progressListener, ProgressEvent.FAILED_EVENT_CODE);
            throw ace;
        } finally {
            try {
                input.close();
            } catch (Exception e) {
                log.warn("Unable to cleanly close input stream: " + e.getMessage(), e);
            }
        }

        String contentMd5 = metadata.getContentMD5();

        if (returnedMetadata != null && contentMd5 != null) {
            byte[] clientSideHash;
            if (md5DigestStream != null) {
                try {
                    clientSideHash = Md5Utils.computeMD5Hash(md5DigestStream.getMd5Digest());
                } catch (NoSuchAlgorithmException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            clientSideHash = contentMd5.getBytes();
            byte[] serverSideHash = returnedMetadata.getETag().getBytes();

            if (!Arrays.equals(clientSideHash, serverSideHash)) {
                fireProgressEvent(progressListener, ProgressEvent.FAILED_EVENT_CODE);
                throw new ClientException("Unable to verify integrity of data upload.  "
                        + "Client calculated content hash didn't match hash calculated by  NOS.  "
                        + "You may need to delete the data stored in  NOS.");
            }
        }

        fireProgressEvent(progressListener, ProgressEvent.COMPLETED_EVENT_CODE);

        PutObjectResult result = new PutObjectResult();
        result.setETag(returnedMetadata.getETag());
        result.setVersionId(returnedMetadata.getVersionId());
        result.setExpirationTime(returnedMetadata.getExpirationTime());
        result.setExpirationTimeRuleId(returnedMetadata.getExpirationTimeRuleId());
        result.setObjectName(HttpUtils.urlDecode(returnedMetadata.getObjectName(), true));

        try {
            String callbackRet = returnedMetadata.getCallbackRet();
            if (callbackRet != null) {
                JSONObject retJson;
                retJson = new JSONObject(new String(BinaryUtils.fromBase64(callbackRet)));
                int code = (Integer) retJson.get("Code");
                String message = (String) retJson.get("Message");
                if (code != 0) {
                    result.setCallbackRetCode(code);
                    result.setCallbackRetMessage(new String(BinaryUtils.fromBase64(message)));
                }
            }
        } catch (JSONException e) {
            throw new ClientException("Parse x-nos-callback-ret header failed");
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#putObjectMeta(java.lang.String, java.lang.String,
     * com.netease.cloud.services.nos.model.NOSObjectMetadata)
     */
    @Override
    public void putObjectMeta(String bucketName, String key, ObjectMetadata metadata)
            throws ClientException, ServiceException {
        assertParameterNotNull(bucketName,
                "The source bucket name must be specified when putObjectMeta");
        assertParameterNotNull(key,
                "The source object key must be specified when putObjectMeta");
        assertParameterNotNull(metadata,
                "The metadata must be specified when putObjectMeta");
        PutObjectMetaRequest putObjectMetaRequest = new PutObjectMetaRequest();
        Request<PutObjectMetaRequest> request = createRequest(bucketName, key, putObjectMetaRequest, HttpMethodName.PUT);

        populateRequestMetadata(request, metadata);

        request.addParameter("meta", null);

        invoke(request, voidResponseHandler, bucketName, key);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#copyObject(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void copyObject(String sourceBucketName, String sourceKey, String destinationBucketName,
                           String destinationKey) throws ClientException, ServiceException {
        copyObject(new CopyObjectRequest(sourceBucketName, sourceKey, destinationBucketName, destinationKey));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#copyObject(com.netease.cloud.services. nos.model.CopyObjectRequest)
     */
    @Override
    public void copyObject(CopyObjectRequest copyObjectRequest) throws ClientException, ServiceException {
        assertParameterNotNull(copyObjectRequest.getSourceBucketName(),
                "The source bucket name must be specified when copying an object");
        assertParameterNotNull(copyObjectRequest.getSourceKey(),
                "The source object key must be specified when copying an object");
        assertParameterNotNull(copyObjectRequest.getDestinationBucketName(),
                "The destination bucket name must be specified when copying an object");
        assertParameterNotNull(copyObjectRequest.getDestinationKey(),
                "The destination object key must be specified when copying an object");

        String destinationKey = copyObjectRequest.getDestinationKey();
        String destinationBucketName = copyObjectRequest.getDestinationBucketName();

        Request<CopyObjectRequest> request = createRequest(destinationBucketName, destinationKey, copyObjectRequest,
                HttpMethodName.PUT);

        populateRequestWithCopyObjectParameters(request, copyObjectRequest);
        /*
         * We can't send the Content-Length header if the user specified it, otherwise it messes up the HTTP connection
         * when the remote server thinks there's more data to pull.
         */
        request.getHeaders().remove(Headers.CONTENT_LENGTH);

        invoke(request, voidResponseHandler, destinationBucketName, destinationKey);

    }

    @Override
    public void moveObject(String sourceBucketName, String sourceKey, String destinationBucketName,
                           String destinationKey) throws ClientException, ServiceException {
        moveObject(new MoveObjectRequest(sourceBucketName, sourceKey, destinationBucketName, destinationKey));
    }

    @Override
    public void moveObject(MoveObjectRequest moveObjectRequest) throws ClientException, ServiceException {
        assertParameterNotNull(moveObjectRequest.getSourceBucketName(),
                "The source bucket name must be specified when copying an object");
        assertParameterNotNull(moveObjectRequest.getSourceKey(),
                "The source object key must be specified when copying an object");
        assertParameterNotNull(moveObjectRequest.getDestinationBucketName(),
                "The destination bucket name must be specified when copying an object");
        assertParameterNotNull(moveObjectRequest.getDestinationKey(),
                "The destination object key must be specified when copying an object");

        String destinationKey = moveObjectRequest.getDestinationKey();
        String destinationBucketName = moveObjectRequest.getDestinationBucketName();

        Request<MoveObjectRequest> request = createRequest(destinationBucketName, destinationKey, moveObjectRequest,
                HttpMethodName.PUT);

        String moveSourceHeader = "/" + ServiceUtils.urlEncode(moveObjectRequest.getSourceBucketName()) + "/"
                + ServiceUtils.urlEncode(moveObjectRequest.getSourceKey());
        if (moveObjectRequest.getSourceVersionId() != null) {
            moveSourceHeader += "?versionId=" + moveObjectRequest.getSourceVersionId();
        }
        request.addHeader("x-nos-move-source", moveSourceHeader);

        invoke(request, voidResponseHandler, destinationBucketName, destinationKey);
    }

    public void mediaSaveAsObject(String sourceBucketName, String sourceKey, String destinationBucketName,
                                  String destinationKey, String mediaOperation) {
        mediaSaveAsObject(new MediaSaveAsRequest(sourceBucketName, sourceKey, destinationBucketName, destinationKey, mediaOperation));
    }

    public void mediaSaveAsObject(MediaSaveAsRequest mediaSaveAsRequest) {
        assertParameterNotNull(mediaSaveAsRequest.getSourceBucketName(),
                "The source bucket name must be specified when mediaSaveas an object");
        assertParameterNotNull(mediaSaveAsRequest.getSourceKey(),
                "The source object key must be specified when mediaSaveas an object");
        assertParameterNotNull(mediaSaveAsRequest.getDestinationBucketName(),
                "The destination bucket name must be specified when mediaSaveas an object");
        assertParameterNotNull(mediaSaveAsRequest.getDestinationKey(),
                "The destination object key must be specified when mediaSaveas an object");

        String destinationKey = mediaSaveAsRequest.getDestinationKey();
        String destinationBucketName = mediaSaveAsRequest.getDestinationBucketName();

        Request<MediaSaveAsRequest> request = createRequest(destinationBucketName, destinationKey, mediaSaveAsRequest,
                HttpMethodName.PUT);

        String mediaOpSourceHeader = "/" + ServiceUtils.urlEncode(mediaSaveAsRequest.getSourceBucketName()) + "/"
                + ServiceUtils.urlEncode(mediaSaveAsRequest.getSourceKey());
        request.addHeader("x-nos-media-source", mediaOpSourceHeader);
        request.addHeader("x-nos-media-op", ServiceUtils.urlEncode(mediaSaveAsRequest.getMediaOperation()));

        invoke(request, voidResponseHandler, destinationBucketName, destinationKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#deleteObject(java.lang.String, java.lang.String)
     */
    @Override
    public void deleteObject(String bucketName, String key) throws ClientException, ServiceException {
        deleteObject(new DeleteObjectRequest(bucketName, key));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#deleteObject(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void deleteObject(String bucketName, String key, String versionId) throws ClientException, ServiceException {
        deleteObject(new DeleteObjectRequest(bucketName, key, versionId));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#deleteObject(com.netease.cloud.services .NOS.DeleteObjectRequest)
     */
    @Override
    public void deleteObject(DeleteObjectRequest deleteObjectRequest) throws ClientException, ServiceException {
        assertParameterNotNull(deleteObjectRequest,
                "The delete object request must be specified when deleting an object");

        assertParameterNotNull(deleteObjectRequest.getBucketName(),
                "The bucket name must be specified when deleting an object");
        assertParameterNotNull(deleteObjectRequest.getKey(), "The key must be specified when deleting an object");

        String versionId = deleteObjectRequest.getVersionId();

        Request<DeleteObjectRequest> request = createRequest(deleteObjectRequest.getBucketName(),
                deleteObjectRequest.getKey(), deleteObjectRequest, HttpMethodName.DELETE);

        request.addParameter("versionId", versionId);

        invoke(request, voidResponseHandler, deleteObjectRequest.getBucketName(), deleteObjectRequest.getKey());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#deleteObjects(com.netease.cloud.services .nos.model.DeleteObjectsRequest)
     */
    @Override
    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest) {
        Request<DeleteObjectsRequest> request = createRequest(deleteObjectsRequest.getBucketName(), null,
                deleteObjectsRequest, HttpMethodName.POST);
        request.addParameter("delete", null);

        XmlWriter xml = new XmlWriter();

        xml.start("Delete");
        if (deleteObjectsRequest.getQuiet()) {
            xml.start("Quiet").value("true").end();
        } else {
            xml.start("Quiet").value("false").end();
        }
        for (KeyVersion keyVersion : deleteObjectsRequest.getKeys()) {
            xml.start("Object");
            /* sjl add test */
            /*
             * String key = keyVersion.getKey(); key = key.replaceAll("\"", "&quot"); System.out.println("convert key: "
             * + key); xml.start("Key").value(key).end();
             */
            xml.start("Key").value(keyVersion.getKey()).end();
            if (keyVersion.getVersion() != null) {
                xml.start("VersionId").value(keyVersion.getVersion()).end();
            }
            xml.end();
        }
        xml.end();
        byte[] content = xml.getBytes();
        request.addHeader("Content-Length", String.valueOf(content.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(content));
        try {
            byte[] md5 = ServiceUtils.computeMD5Hash(content);
            String md5Base64 = BinaryUtils.toHex(md5);
            request.addHeader("Content-MD5", md5Base64);
        } catch (Exception e) {
            throw new ClientException("Couldn't compute md5 sum", e);
        }

        DeleteObjectsResponse response = invoke(request, new Unmarshallers.DeleteObjectsResultUnmarshaller(),
                deleteObjectsRequest.getBucketName(), null);

        /*
         * If the result was only partially successful, throw an exception
         */
        if (!response.getErrors().isEmpty()) {
            throw new MultiObjectDeleteException(response.getErrors(), response.getDeletedObjects());
        }

        return new DeleteObjectsResult(response.getDeletedObjects());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#deleteObjectVersion(java.lang.String, java.lang.String, java.lang.String)
     */
	/*public void deleteVersion(String bucketName, String key, String versionId) throws ClientException, ServiceException {
		deleteVersion(new DeleteVersionRequest(bucketName, key, versionId));
	}*/

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#deleteVersion(com.netease.cloud.services .nos.model.DeleteVersionRequest)
     */
	/*public void deleteVersion(DeleteVersionRequest deleteVersionRequest) throws ClientException, ServiceException {
		assertParameterNotNull(deleteVersionRequest,
				"The delete version request object must be specified when deleting a version");

		String bucketName = deleteVersionRequest.getBucketName();
		String key = deleteVersionRequest.getKey();
		String versionId = deleteVersionRequest.getVersionId();

		assertParameterNotNull(bucketName, "The bucket name must be specified when deleting a version");
		assertParameterNotNull(key, "The key must be specified when deleting a version");
		assertParameterNotNull(versionId, "The version ID must be specified when deleting a version");

		Request<DeleteVersionRequest> request = createRequest(bucketName, key, deleteVersionRequest,
				HttpMethodName.DELETE);
		if (versionId != null)
			request.addParameter("versionId", versionId);

		invoke(request, voidResponseHandler, bucketName, key);
	}*/

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#setBucketVersioningConfiguration(com .netease
     * .cloud.services.nos.model.SetBucketVersioningConfigurationRequest)
     */
	/*public void setBucketVersioningConfiguration(
			SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest) throws ClientException,
			ServiceException {
		assertParameterNotNull(setBucketVersioningConfigurationRequest,
				"The SetBucketVersioningConfigurationRequest object must be specified when setting versioning configuration");

		String bucketName = setBucketVersioningConfigurationRequest.getBucketName();
		BucketVersioningConfiguration versioningConfiguration = setBucketVersioningConfigurationRequest
				.getVersioningConfiguration();

		assertParameterNotNull(bucketName,
				"The bucket name parameter must be specified when setting versioning configuration");
		assertParameterNotNull(versioningConfiguration,
				"The bucket versioning parameter must be specified when setting versioning configuration");

		Request<SetBucketVersioningConfigurationRequest> request = createRequest(bucketName, null,
				setBucketVersioningConfigurationRequest, HttpMethodName.PUT);
		request.addParameter("versioning", null);

		XmlWriter xml = new XmlWriter();
		xml.start("VersioningConfiguration", "xmlns", Constants.XML_NAMESPACE);
		xml.start("Status").value(versioningConfiguration.getStatus()).end();
		xml.end();

		byte[] bytes = xml.getBytes();
		request.setContent(new ByteArrayInputStream(bytes));

		invoke(request, voidResponseHandler, bucketName, null);
	}*/

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#getBucketVersioningConfiguration(java .lang.String)
     */
	/*public BucketVersioningConfiguration getBucketVersioningConfiguration(String bucketName) throws ClientException,
			ServiceException {
		assertParameterNotNull(bucketName,
				"The bucket name parameter must be specified when querying versioning configuration");

		Request<GenericBucketRequest> request = createRequest(bucketName, null, new GenericBucketRequest(bucketName),
				HttpMethodName.GET);
		request.addParameter("versioning", null);

		return invoke(request, new Unmarshallers.BucketVersioningConfigurationUnmarshaller(), bucketName, null);
	}*/

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#generatePresignedUrl(java.lang.String, java.lang.String, java.util.Date)
     */
    @Override
    public URL generatePresignedUrl(String bucketName, String key, Date expiration) throws ClientException {
        return generatePresignedUrl(bucketName, key, expiration, HttpMethod.GET);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#generatePresignedUrl(java.lang.String, java.lang.String, java.util.Date,
     * com.netease.cloud.HttpMethod)
     */
    @Override
    public URL generatePresignedUrl(String bucketName, String key, Date expiration, HttpMethod method)
            throws ClientException {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key, method);
        request.setExpiration(expiration);

        return generatePresignedUrl(request);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#generatePresignedUrl(com.anetease.cloud. *
     * .services.nos.model.GeneratePresignedUrlRequest)
     */
    @Override
    public URL generatePresignedUrl(GeneratePresignedUrlRequest generatePresignedUrlRequest) throws ClientException {
        assertParameterNotNull(generatePresignedUrlRequest,
                "The request parameter must be specified when generating a pre-signed URL");

        String bucketName = generatePresignedUrlRequest.getBucketName();
        String key = generatePresignedUrlRequest.getKey();

        assertParameterNotNull(bucketName,
                "The bucket name parameter must be specified when generating a pre-signed URL");
        assertParameterNotNull(generatePresignedUrlRequest.getMethod(),
                "The HTTP method request parameter must be specified when generating a pre-signed URL");

        if (generatePresignedUrlRequest.getExpiration() == null) {
            generatePresignedUrlRequest.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15));
        }

        HttpMethodName httpMethod = HttpMethodName.valueOf(generatePresignedUrlRequest.getMethod().toString());
        Request<GeneratePresignedUrlRequest> request = createRequest(bucketName, key, generatePresignedUrlRequest,
                httpMethod);
        for (Entry<String, String> entry : generatePresignedUrlRequest.getRequestParameters().entrySet()) {
            request.addParameter(entry.getKey(), entry.getValue());
        }

        presignRequest(request, generatePresignedUrlRequest.getMethod(), bucketName, key,
                generatePresignedUrlRequest.getExpiration(), null);

        /** handle download parameter **/
        if (generatePresignedUrlRequest.getDownload() != null) {
            String download = generatePresignedUrlRequest.getDownload();
            if (download.length() > 256) {
                throw new IllegalArgumentException("Download parameter should be less than 256 characters");
            }
            request.addParameter("download", download);
        }

        /** handle ifNotFound parameter **/
        if (generatePresignedUrlRequest.getIfNotFound() != null) {
            String ifNotFound = generatePresignedUrlRequest.getIfNotFound();
            request.addParameter("ifNotFound", ifNotFound);
        }

        return ServiceUtils.convertRequestToUrl(request);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#abortMultipartUpload(com.netease.cloud.
     * .services.nos.model.AbortMultipartUploadRequest)
     */
    @Override
    public void abortMultipartUpload(AbortMultipartUploadRequest abortMultipartUploadRequest) throws ClientException,
            ServiceException {
        assertParameterNotNull(abortMultipartUploadRequest,
                "The request parameter must be specified when aborting a multipart upload");
        assertParameterNotNull(abortMultipartUploadRequest.getBucketName(),
                "The bucket name parameter must be specified when aborting a multipart upload");
        assertParameterNotNull(abortMultipartUploadRequest.getKey(),
                "The key parameter must be specified when aborting a multipart upload");
        assertParameterNotNull(abortMultipartUploadRequest.getUploadId(),
                "The upload ID parameter must be specified when aborting a multipart upload");

        String bucketName = abortMultipartUploadRequest.getBucketName();
        String key = abortMultipartUploadRequest.getKey();

        Request<AbortMultipartUploadRequest> request = createRequest(bucketName, key, abortMultipartUploadRequest,
                HttpMethodName.DELETE);
        request.addParameter("uploadId", abortMultipartUploadRequest.getUploadId());

        //移除自定义元数据记录
        specialHeadersMap.remove(abortMultipartUploadRequest.getBucketName() + abortMultipartUploadRequest.getKey());

        invoke(request, voidResponseHandler, bucketName, key);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#completeMultipartUpload(com.netease.cloud.
     * .services.nos.model.CompleteMultipartUploadRequest)
     */
    @Override
    public CompleteMultipartUploadResult completeMultipartUpload(
            CompleteMultipartUploadRequest completeMultipartUploadRequest) throws ClientException, ServiceException {
        assertParameterNotNull(completeMultipartUploadRequest,
                "The request parameter must be specified when completing a multipart upload");

        String bucketName = completeMultipartUploadRequest.getBucketName();
        String key = completeMultipartUploadRequest.getKey();
        String uploadId = completeMultipartUploadRequest.getUploadId();
        assertParameterNotNull(bucketName,
                "The bucket name parameter must be specified when completing a multipart upload");
        assertParameterNotNull(key, "The key parameter must be specified when completing a multipart upload");
        assertParameterNotNull(uploadId, "The upload ID parameter must be specified when completing a multipart upload");
        assertParameterNotNull(completeMultipartUploadRequest.getPartETags(),
                "The part ETags parameter must be specified when completing a multipart upload");

        Request<CompleteMultipartUploadRequest> request = createRequest(bucketName, key,
                completeMultipartUploadRequest, HttpMethodName.POST);
        request.addParameter("uploadId", uploadId);

        //根据记录的自定义元数据上传元数据
        if (specialHeadersMap.get(completeMultipartUploadRequest.getBucketName() + completeMultipartUploadRequest.getKey()) != null) {
            Map<String, String> headers = specialHeadersMap.get(completeMultipartUploadRequest.getBucketName() + completeMultipartUploadRequest.getKey());
            for (Entry<String, String> entry : headers.entrySet()) {
                String k = entry.getKey();
                String value = entry.getValue();
                completeMultipartUploadRequest.addSpecialHeader(k, value);
            }
            specialHeadersMap.remove(completeMultipartUploadRequest.getBucketName() + completeMultipartUploadRequest.getKey());
        }

        List<PartETag> partETags = completeMultipartUploadRequest.getPartETags();

        XmlWriter xml = new XmlWriter();
        xml.start("CompleteMultipartUpload");
        if (partETags != null) {
            Collections.sort(partETags, new Comparator<PartETag>() {
                @Override
                public int compare(PartETag o1, PartETag o2) {
                    return (o1.getPartNumber() < o2.getPartNumber()) ? -1 : ((o1.getPartNumber() == o2.getPartNumber()) ? 0 : 1);
                }
            });

            for (PartETag partEtag : partETags) {
                xml.start("Part");
                xml.start("PartNumber").value(Integer.toString(partEtag.getPartNumber())).end();
                xml.start("ETag").value(partEtag.getETag()).end();
                xml.end();
            }
        }
        xml.end();

        byte[] xmlByte = xml.getBytes();
        request.addHeader("Content-Type", "text/plain");
        request.addHeader("Content-Length", String.valueOf(xmlByte.length));

        if (completeMultipartUploadRequest.getxNosObjectMD5() != null) {
            request.addHeader(Headers.X_NOS_OBJECT_MD5, completeMultipartUploadRequest.getxNosObjectMD5());
        }
        request.setContent(new ByteArrayInputStream(xmlByte));

        @SuppressWarnings("unchecked")
        ResponseHeaderHandlerChain<CompleteMultipartUploadHandler> responseHandler = new ResponseHeaderHandlerChain<CompleteMultipartUploadHandler>(
                new Unmarshallers.CompleteMultipartUploadResultUnmarshaller(),
                new ObjectExpirationHeaderHandler<CompleteMultipartUploadHandler>());
        CompleteMultipartUploadHandler handler = invoke(request, responseHandler, bucketName, key);
        if (handler.getCompleteMultipartUploadResult() != null) {
            String versionId = responseHandler.getResponseHeaders().get(Headers.NOS_VERSION_ID);

            try {
                String callbackRet = responseHandler.getResponseHeaders().get(Headers.X_NOS_CALLBACK_RET);
                if (callbackRet != null) {
                    JSONObject retJson;
                    retJson = new JSONObject(new String(BinaryUtils.fromBase64(callbackRet)));
                    int code = (Integer) retJson.get("Code");
                    String message = (String) retJson.get("Message");
                    if (code != 0) {
                        handler.getCompleteMultipartUploadResult().setCallbackRetCode(code);
                        handler.getCompleteMultipartUploadResult().setCallbackRetMessage(new String(BinaryUtils.fromBase64(message)));
                    }
                }
            } catch (JSONException e) {
                throw new ClientException("Parse x-nos-callback-ret header failed");
            }

            handler.getCompleteMultipartUploadResult().setVersionId(versionId);
            return handler.getCompleteMultipartUploadResult();
        } else {
            throw handler.getNOSException();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#initiateMultipartUpload(com.netease.cloud.
     * .services.nos.model.InitiateMultipartUploadRequest)
     */
    @Override
    public InitiateMultipartUploadResult initiateMultipartUpload(
            InitiateMultipartUploadRequest initiateMultipartUploadRequest) throws ClientException, ServiceException {
        assertParameterNotNull(initiateMultipartUploadRequest,
                "The request parameter must be specified when initiating a multipart upload");

        assertParameterNotNull(initiateMultipartUploadRequest.getBucketName(),
                "The bucket name parameter must be specified when initiating a multipart upload");
        assertParameterNotNull(initiateMultipartUploadRequest.getKey(),
                "The key parameter must be specified when initiating a multipart upload");

        Request<InitiateMultipartUploadRequest> request = createRequest(initiateMultipartUploadRequest.getBucketName(),
                initiateMultipartUploadRequest.getKey(), initiateMultipartUploadRequest, HttpMethodName.POST);
        request.addParameter("uploads", null);

        if (initiateMultipartUploadRequest.getStorageClass() != null) {
            request.addHeader(Headers.STORAGE_CLASS, initiateMultipartUploadRequest.getStorageClass().toString());
        }

        if (initiateMultipartUploadRequest.getCannedACL() != null) {
            request.addHeader(Headers.NOS_CANNED_ACL, initiateMultipartUploadRequest.getCannedACL().toString());
        }

        if (initiateMultipartUploadRequest.objectMetadata != null) {
            populateRequestMetadata(request, initiateMultipartUploadRequest.objectMetadata);
        }

        //此处针对特殊元数据进行记录
        Map<String, String> userHeaders = new HashMap<String, String>();
        //存储SpecialHeader和UserMetadata中的元数据，需要特别注意SpecialHeader可能为null
        if (initiateMultipartUploadRequest.copyPrivateRequestParameters() != null) {
            for (Entry<String, String> entry : initiateMultipartUploadRequest.copyPrivateRequestParameters().entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                if ("Content-Type".equals(k) ||
                        "Content-Encoding".equals(k) ||
                        "Cache-Control".equals(k) ||
                        "Content-Disposition".equals(k) ||
                        "Content-Language".equals(k) ||
                        "Expires".equals(k) ||
                        k.contains("x-nos-meta")) {
                    userHeaders.put(k, v);
                }
            }
        }
        for (Entry<String, String> entry : request.getHeaders().entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            if ("Content-Type".equals(k) ||
                    "Content-Encoding".equals(k) ||
                    "Cache-Control".equals(k) ||
                    "Content-Disposition".equals(k) ||
                    "Content-Language".equals(k) ||
                    "Expires".equals(k) ||
                    k.contains("x-nos-meta")) {
                userHeaders.put(k, v);
            }
        }
        if (userHeaders.size() > 0) {
            specialHeadersMap.put(initiateMultipartUploadRequest.getBucketName() + initiateMultipartUploadRequest.getKey(), userHeaders);
        }
        // Be careful that we don't send the object's total size as the content
        // length for the InitiateMultipartUpload request.
        // request.getHeaders().remove(Headers.CONTENT_LENGTH);
        // Set the request content to be empty (but not null) to force the
        // runtime to pass
        // any query params in the query string and not the request body, to
        // keep NOS happy.
        request.setContent(new ByteArrayInputStream(new byte[0]));
        request.addHeader("Content-Length", "0");
        if (initiateMultipartUploadRequest.getMD5Digest() != null) {
            request.addHeader("Content-MD5", initiateMultipartUploadRequest.getMD5Digest());
        }

        @SuppressWarnings("unchecked")
        ResponseHeaderHandlerChain<InitiateMultipartUploadResult> responseHandler = new ResponseHeaderHandlerChain<InitiateMultipartUploadResult>(
                new Unmarshallers.InitiateMultipartUploadResultUnmarshaller());
        return invoke(request, responseHandler, initiateMultipartUploadRequest.getBucketName(),
                initiateMultipartUploadRequest.getKey());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#listMultipartUploads(com.netease.cloud
     * .services.nos.model.ListMultipartUploadsRequest)
     */
    @Override
    public MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest listMultipartUploadsRequest)
            throws ClientException, ServiceException {
        assertParameterNotNull(listMultipartUploadsRequest,
                "The request parameter must be specified when listing multipart uploads");

        assertParameterNotNull(listMultipartUploadsRequest.getBucketName(),
                "The bucket name parameter must be specified when listing multipart uploads");

        Request<ListMultipartUploadsRequest> request = createRequest(listMultipartUploadsRequest.getBucketName(), null,
                listMultipartUploadsRequest, HttpMethodName.GET);
        request.addParameter("uploads", null);

        if (listMultipartUploadsRequest.getKeyMarker() != null) {
            request.addParameter("key-marker", listMultipartUploadsRequest.getKeyMarker());
        }
        if (listMultipartUploadsRequest.getMaxUploads() != null) {
            request.addParameter("max-uploads", listMultipartUploadsRequest.getMaxUploads().toString());
        }
        if (listMultipartUploadsRequest.getUploadIdMarker() != null) {
            request.addParameter("upload-id-marker", listMultipartUploadsRequest.getUploadIdMarker());
        }
        if (listMultipartUploadsRequest.getDelimiter() != null) {
            request.addParameter("delimiter", listMultipartUploadsRequest.getDelimiter());
        }
        if (listMultipartUploadsRequest.getPrefix() != null) {
            request.addParameter("prefix", listMultipartUploadsRequest.getPrefix());
        }

        return invoke(request, new Unmarshallers.ListMultipartUploadsResultUnmarshaller(),
                listMultipartUploadsRequest.getBucketName(), null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#listParts(com.netease.cloud.services.NOS .model.ListPartsRequest)
     */
    @Override
    public PartListing listParts(ListPartsRequest listPartsRequest) throws ClientException, ServiceException {
        assertParameterNotNull(listPartsRequest, "The request parameter must be specified when listing parts");

        assertParameterNotNull(listPartsRequest.getBucketName(),
                "The bucket name parameter must be specified when listing parts");
        assertParameterNotNull(listPartsRequest.getKey(), "The key parameter must be specified when listing parts");
        assertParameterNotNull(listPartsRequest.getUploadId(),
                "The upload ID parameter must be specified when listing parts");

        Request<ListPartsRequest> request = createRequest(listPartsRequest.getBucketName(), listPartsRequest.getKey(),
                listPartsRequest, HttpMethodName.GET);
        request.addParameter("uploadId", listPartsRequest.getUploadId());

        if (listPartsRequest.getMaxParts() != null) {
            request.addParameter("max-parts", listPartsRequest.getMaxParts().toString());
        }
        if (listPartsRequest.getPartNumberMarker() != null) {
            request.addParameter("part-number-marker", listPartsRequest.getPartNumberMarker().toString());
        }

        return invoke(request, new Unmarshallers.ListPartsResultUnmarshaller(), listPartsRequest.getBucketName(),
                listPartsRequest.getKey());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.cloud.nos.Nos#uploadPart(com.netease.cloud.services. nos.model.UploadPartRequest)
     */
    @Override
    public UploadPartResult uploadPart(UploadPartRequest uploadPartRequest) throws ClientException, ServiceException {
        assertParameterNotNull(uploadPartRequest, "The request parameter must be specified when uploading a part");

        String bucketName = uploadPartRequest.getBucketName();
        String key = uploadPartRequest.getKey();
        String uploadId = uploadPartRequest.getUploadId();
        int partNumber = uploadPartRequest.getPartNumber();
        long partSize = uploadPartRequest.getPartSize();

        assertParameterNotNull(bucketName, "The bucket name parameter must be specified when uploading a part");
        assertParameterNotNull(key, "The key parameter must be specified when uploading a part");
        assertParameterNotNull(uploadId, "The upload ID parameter must be specified when uploading a part");
        assertParameterNotNull(partNumber, "The part number parameter must be specified when uploading a part");
        assertParameterNotNull(partSize, "The part size parameter must be specified when uploading a part");

        Request<UploadPartRequest> request = createRequest(bucketName, key, uploadPartRequest, HttpMethodName.PUT);
        request.addParameter("uploadId", uploadId);
        request.addParameter("partNumber", Integer.toString(partNumber));

        if (uploadPartRequest.getMd5Digest() != null) {
            request.addHeader(Headers.CONTENT_MD5, uploadPartRequest.getMd5Digest());
        }

        request.addHeader(Headers.CONTENT_LENGTH, Long.toString(partSize));

        InputStream inputStream = null;
        if (uploadPartRequest.getInputStream() != null) {
            inputStream = uploadPartRequest.getInputStream();
        } else if (uploadPartRequest.getFile() != null) {
            try {
                inputStream = new InputSubstream(new RepeatableFileInputStream(uploadPartRequest.getFile()),
                        uploadPartRequest.getFileOffset(), partSize, true);
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("The specified file doesn't exist", e);
            }
        } else {
            throw new IllegalArgumentException("A File or InputStream must be specified when uploading part");
        }

        MD5DigestCalculatingInputStream md5DigestStream = null;
        if (uploadPartRequest.getMd5Digest() == null) {
            /*
             * If the user hasn't set the content MD5, then we don't want to buffer the whole stream in memory just to
             * calculate it. Instead, we can calculate it on the fly and validate it with the returned ETag from the
             * object upload.
             */
            try {
                md5DigestStream = new MD5DigestCalculatingInputStream(inputStream);
                inputStream = md5DigestStream;
            } catch (NoSuchAlgorithmException e) {
                log.warn("No MD5 digest algorithm available.  Unable to calculate "
                        + "checksum and verify data integrity.", e);
            }
        }

        ProgressListener progressListener = uploadPartRequest.getProgressListener();
        if (progressListener != null) {
            inputStream = new ProgressReportingInputStream(inputStream, progressListener);
            fireProgressEvent(progressListener, ProgressEvent.PART_STARTED_EVENT_CODE);
        }

        try {
            request.setContent(inputStream);
            ObjectMetadata metadata = invoke(request, new NosMetadataResponseHandler(), bucketName, key);

            if (metadata != null && md5DigestStream != null) {
                String contentMd5 = BinaryUtils.toBase64(md5DigestStream.getMd5Digest());
                byte[] clientSideHash = BinaryUtils.fromBase64(contentMd5);
                byte[] serverSideHash = BinaryUtils.fromHex(metadata.getETag());

                if (!Arrays.equals(clientSideHash, serverSideHash)) {
                    fireProgressEvent(progressListener, ProgressEvent.FAILED_EVENT_CODE);
                    throw new ClientException("Unable to verify integrity of data upload.  "
                            + "Client calculated content hash didn't match hash calculated by NOS.  "
                            + "You may need to delete the data stored in  NOS.");
                }
            }

            fireProgressEvent(progressListener, ProgressEvent.PART_COMPLETED_EVENT_CODE);

            UploadPartResult result = new UploadPartResult();
            result.setETag(metadata.getETag());
            result.setPartNumber(partNumber);
            return result;
        } catch (ClientException ace) {
            fireProgressEvent(progressListener, ProgressEvent.PART_FAILED_EVENT_CODE);
            throw ace;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /*
     * Private Interface
     */

    /**
     * <p>
     * Asserts that the specified parameter value is not <code>null</code> and if it is, throws an
     * <code>IllegalArgumentException</code> with the specified error message.
     * </p>
     *
     * @param parameterValue The parameter value being checked.
     * @param errorMessage   The error message to include in the IllegalArgumentException if the specified parameter is null.
     */
    private void assertParameterNotNull(Object parameterValue, String errorMessage) {
        if (parameterValue == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Fires a progress event with the specified event type to the specified listener.
     *
     * @param listener  The listener to receive the event.
     * @param eventType The type of event to fire.
     */
    private void fireProgressEvent(ProgressListener listener, int eventType) {
        if (listener == null) {
            return;
        }
        ProgressEvent event = new ProgressEvent(0);
        event.setEventCode(eventType);
        listener.progressChanged(event);
    }

    /**
     * Pre-signs the specified request, using a signature query-string parameter.
     *
     * @param request     The request to sign.
     * @param methodName  The HTTP method (GET, PUT, DELETE, HEAD) for the specified request.
     * @param bucketName  The name of the bucket involved in the request. If the request is not an operation on a bucket this
     *                    parameter should be null.
     * @param key         The object key involved in the request. If the request is not an operation on an object, this
     *                    parameter should be null.
     * @param expiration  The time at which the signed request is no longer valid, and will stop working.
     * @param subResource The optional sub-resource being requested as part of the request (e.g. "location", "acl", "logging",
     *                    or "torrent").
     */
    protected <T> void presignRequest(Request<T> request, HttpMethod methodName, String bucketName, String key,
                                      Date expiration, String subResource) {
        // Run any additional request handlers if present
        if (requestHandlers != null) {
            for (RequestHandler requestHandler : requestHandlers) {
                requestHandler.beforeRequest(request);
            }
        }

        String resourcePath = "/" + ((bucketName != null) ? bucketName + "/" : "")
                + ((key != null) ? ServiceUtils.urlEncode(key) : "") + ((subResource != null) ? "?" + subResource : "");

        Credentials credentials = CredentialsProvider.getCredentials();
        WebServiceRequest originalRequest = request.getOriginalRequest();
        if (originalRequest != null && originalRequest.getRequestCredentials() != null) {
            credentials = originalRequest.getRequestCredentials();
        }

        new NosQueryStringSigner<T>(methodName.toString(), resourcePath, expiration).sign(request, credentials);

        // The NOS DevPay token header is a special exception and can be
        // safely moved
        // from the request's headers into the query string to ensure that it
        // travels along
        // with the pre-signed URL when it's sent back to NOS.
        if (request.getHeaders().containsKey(Headers.SECURITY_TOKEN)) {
            String value = request.getHeaders().get(Headers.SECURITY_TOKEN);
            request.addParameter(Headers.SECURITY_TOKEN, value);
            request.getHeaders().remove(Headers.SECURITY_TOKEN);
        }
    }

    @SuppressWarnings("unused")
    private URI convertToPathStyleEndpoint() {
        try {
            return new URI(endpoint.getScheme() + "://" + endpoint.getAuthority() + Constants.PROJECT_NAME);
        } catch (URISyntaxException e) {
            throw new ClientException("Can't turn bucket name into a URI: " + e.getMessage(), e);
        }
    }

    /**
     * <p>
     * Populates the specified request object with the appropriate headers from the {@link ObjectMetadata} object.
     * </p>
     *
     * @param request  The request to populate with headers.
     * @param metadata The metadata containing the header information to include in the request.
     */
    protected static void populateRequestMetadata(Request<?> request, ObjectMetadata metadata) {
        Map<String, Object> rawMetadata = metadata.getRawMetadata();
        if (rawMetadata != null) {
            for (Entry<String, Object> entry : rawMetadata.entrySet()) {
                if (entry.getValue() != null) {
                    request.addHeader(entry.getKey(), entry.getValue().toString());
                }
            }
        }

        Map<String, String> userMetadata = metadata.getUserMetadata();
        if (userMetadata != null) {
            for (Entry<String, String> entry : userMetadata.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null) {
                    key = key.trim();
                }
                if (value != null) {
                    value = value.trim();
                }
                request.addHeader(Headers.NOS_USER_METADATA_PREFIX + key, value);
            }
        }
    }

    /**
     * <p>
     * Populates the specified request with the numerous options available in <code>CopyObjectRequest</code>.
     * </p>
     *
     * @param request           The request to populate with headers to represent all the options expressed in the
     *                          <code>CopyObjectRequest</code> object.
     * @param copyObjectRequest The object containing all the options for copying an object in NOS.
     */
    private static void populateRequestWithCopyObjectParameters(Request<?> request, CopyObjectRequest copyObjectRequest) {
        String copySourceHeader = "/" + ServiceUtils.urlEncode(copyObjectRequest.getSourceBucketName()) + "/"
                + ServiceUtils.urlEncode(copyObjectRequest.getSourceKey());
        if (copyObjectRequest.getSourceVersionId() != null) {
            copySourceHeader += "?versionId=" + copyObjectRequest.getSourceVersionId();
        }
        request.addHeader("x-nos-copy-source", copySourceHeader);

        if (copyObjectRequest.getCannedAccessControlList() != null) {
            request.addHeader(Headers.NOS_CANNED_ACL, copyObjectRequest.getCannedAccessControlList().toString());
        }

        if (copyObjectRequest.getStorageClass() != null) {
            request.addHeader(Headers.STORAGE_CLASS, copyObjectRequest.getStorageClass());
        }

        ObjectMetadata newObjectMetadata = copyObjectRequest.getNewObjectMetadata();
        if (newObjectMetadata != null) {
            populateRequestMetadata(request, newObjectMetadata);
        }
    }

    /**
     * <p>
     * Adds the specified date header in RFC 822 date format to the specified request. This method will not add a date
     * header if the specified date value is <code>null</code>.
     * </p>
     *
     * @param request The request to add the header to.
     * @param header  The header name.
     * @param value   The header value.
     */
    private static void addDateHeader(Request<?> request, String header, Date value) {
        if (value != null) {
            request.addHeader(header, ServiceUtils.formatRfc822Date(value));
        }
    }

    /**
     * Creates and initializes a new request object for the specified NOS resource. This method is responsible for
     * determining the right way to address resources. For example, bucket names that are not DNS addressable cannot be
     * addressed in V2, virtual host, style, and instead must use V1, path style. The returned request object has the
     * service name, endpoint and resource path correctly populated. Callers can take the request, add any additional
     * headers or parameters, then sign and execute the request.
     *
     * @param bucketName      An optional parameter indicating the name of the bucket containing the resource involved in the
     *                        request.
     * @param key             An optional parameter indicating the key under which the desired resource is stored in the specified
     *                        bucket.
     * @param originalRequest The original request, as created by the user.
     * @param httpMethod      The HTTP method to use when sending the request.
     * @return A new request object, populated with endpoint, resource path, and service name, ready for callers to
     * populate any additional headers or parameters, and execute.
     */
    protected <X extends WebServiceRequest> Request<X> createRequest(String bucketName, String key, X originalRequest,
                                                                     HttpMethodName httpMethod) {
        //强制Endpoint检查
        if (endpoint == null) {
            throw new ClientException("Please set the endpoint, the default endpoint is no longer supported in the new version SDK.");
        }

        Request<X> request = new DefaultRequest<X>(originalRequest, Constants.NOS_SERVICE_NAME);
        request.setHttpMethod(httpMethod);
        if (this.clientConfiguration.getIsSubdomain() == Boolean.TRUE) {
            if (bucketNameUtils.isDNSBucketName(bucketName)) {
                request.setEndpoint(convertToVirtualHostEndpoint(bucketName));
                request.setResourcePath(ServiceUtils.urlEncode(key));
            } else {
                request.setEndpoint(endpoint);
                if (bucketName != null) {
                    request.setResourcePath(bucketName + "/" + (key != null ? ServiceUtils.urlEncode(key) : ""));
                }
            }
        } else {
            try {
                request.setEndpoint(new URI(endpoint.getScheme() + "://" + endpoint.getAuthority()));
            } catch (URISyntaxException e) {
                throw new ClientException("Can't turn project name into a URI: " + e.getMessage(), e);
            }
            if (bucketName != null) {
                request.setResourcePath(bucketName + "/" + (key != null ? ServiceUtils.urlEncode(key) : ""));
            }

        }
        if (originalRequest.needSetLogInfo()) {
            request.addParameter(Constants.PARAM_LOG_ID, originalRequest.getLogID());
            request.addParameter(Constants.PARAM_LOG_SEQ, originalRequest.getLogSeq());
        }

        return request;
    }

    /**
     * Converts the current endpoint set for this client into virtual addressing style, by placing the name of the
     * specified bucket before the NOS service endpoint.
     *
     * @param bucketName The name of the bucket to use in the virtual addressing style of the returned URI.
     * @return A new URI, creating from the current service endpoint URI and the specified bucket.
     */
    private URI convertToVirtualHostEndpoint(String bucketName) {
        try {
            return new URI(endpoint.getScheme() + "://" + bucketName + "." + endpoint.getAuthority());
        } catch (URISyntaxException e) {
            throw new ClientException("Can't turn bucket name into a URI: " + e.getMessage(), e);
        }
    }

    private <X, Y extends WebServiceRequest> X invoke(Request<Y> request, Unmarshaller<X, InputStream> unmarshaller,
                                                      String bucketName, String key) {
        return invoke(request, new NosXmlResponseHandler<X>(unmarshaller), bucketName, key);
    }

    /**
     * this invoke request is just for return data just like resizeimage , and so on
     *
     * @param request
     * @param bucketName
     * @param key
     * @return inputstream
     */
    private <X, Y extends WebServiceRequest> InputStream invoke(Request<Y> request, String bucketName, String key) {
        return invoke(request, new SimpleDataResponseHandler(), bucketName, key);
    }

    private <X, Y extends WebServiceRequest> X invoke(Request<Y> request,
                                                      HttpResponseHandler<WebServiceResponse<X>> responseHandler, String bucket, String key) {

        if (request.getOriginalRequest().copyPrivateRequestParameters() != null) {
            for (Entry<String, String> entry : request.getOriginalRequest().copyPrivateRequestParameters().entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }

        /*
         * The string we sign needs to include the exact headers that we send with the request, but the client runtime
         * layer adds the Content-Type header before the request is sent if one isn't set, so we have to set something
         * here otherwise the request will fail.
         */
        if (request.getHeaders().get("Content-Type") == null) {
            request.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        }

        Credentials credentials = CredentialsProvider.getCredentials();
        WebServiceRequest originalRequest = request.getOriginalRequest();
        if (originalRequest != null && originalRequest.getRequestCredentials() != null) {
            credentials = originalRequest.getRequestCredentials();
        }

        ExecutionContext executionContext = createExecutionContext();
        executionContext.setSigner(createSigner(request, bucket, key));
        executionContext.setCredentials(credentials);
        executionContext.setToken(originalRequest.getToken());

        return (X) client.execute(request, responseHandler, errorResponseHandler, executionContext);
    }

    protected Signer createSigner(Request<?> request, String bucketName, String key) {
        String resourcePath = "/" + ((bucketName != null) ? bucketName + "/" : "")
                + ((key != null) ? ServiceUtils.urlEncode(key) : "");

        return new NosSigner(request.getHttpMethod().toString(), resourcePath);
    }

    /****** Operations about images *****************/
    /**
     * used to get the meta info of the correspond image
     *
     * @param bucketName
     * @param key
     * @return
     */
    @Override
    public ImageMetadata getImageInfo(String bucketName, String key) throws ClientException, ServiceException {
        return getImageInfo(new GetImageMetaInfoRequest(bucketName, key));
    }

    private ImageMetadata getImageInfo(GetImageMetaInfoRequest getImageMetaInfoRequest) throws ClientException,
            ServiceException {
        assertParameterNotNull(getImageMetaInfoRequest, "The GetImageMetaInfoRequest parameter must be specified");
        assertParameterNotNull(getImageMetaInfoRequest.getBucketName(), "The bucket name parameter must be specified");
        assertParameterNotNull(getImageMetaInfoRequest.getKey(), "The key name parameter must be specified");
        // add paramters
        Request<GetImageMetaInfoRequest> request = createRequest(getImageMetaInfoRequest.getBucketName(),
                getImageMetaInfoRequest.getKey(), getImageMetaInfoRequest, HttpMethodName.GET);

        request.addParameter("imageInfo", "");

        return invoke(request, new Unmarshallers.GetImageMetaInfoUnmarshaller(),
                getImageMetaInfoRequest.getBucketName(), getImageMetaInfoRequest.getKey());
    }

    /**
     * used to get the origin image
     *
     * @param bucketName
     * @param key
     * @return InputStream
     * @throws ClientException
     * @throws ServiceException
     */
    @Override
    public InputStream getImage(String bucketName, String key) throws ClientException, ServiceException {
        return getImage(new GetImageRequest(bucketName, key));
    }

    /**
     * used to do same operation(thumbnail / corp / watermark / ...) on the image,and return the operated image
     *
     * @param getImageRequest
     * @return InputStream
     * @throws ClientException
     * @throws ServiceException
     */
    @Override
    public InputStream getImage(GetImageRequest getImageRequest) throws ClientException, ServiceException {
        // check the params
        assertParameterNotNull(getImageRequest, "The GetImageRequest  parameter must be specified");
        assertParameterNotNull(getImageRequest.getBucketName(), "The bucket name parameter must be specified");
        assertParameterNotNull(getImageRequest.getKey(), "The key parameter must be specified");

        // add paramters
        Request<GetImageRequest> request = createRequest(getImageRequest.getBucketName(), getImageRequest.getKey(),
                getImageRequest, HttpMethodName.GET);

        String modeString;

        request.addParameter("imageView", "");

        if (GetImageMode.XMODE == getImageRequest.getMode()) {
            modeString = "x";
        } else if (GetImageMode.YMODE == getImageRequest.getMode()) {
            modeString = "y";
        } else if (GetImageMode.ZMODE == getImageRequest.getMode()) {
            modeString = "z";
        } else if (GetImageMode.WMODE == getImageRequest.getMode()) {
            modeString = "w";
        } else {
            modeString = null;
        }

        if (-1 != getImageRequest.getResizeX() || -1 != getImageRequest.getResizeY()) {
            if (modeString != null) {
                request.addParameter("thumbnail",
                        getImageRequest.getResizeX() + modeString + getImageRequest.getResizeY());
            } else {
                throw new IllegalArgumentException("the 'mode' param shoud be assigned");
            }
        }
        if (-1 != getImageRequest.getPixel()) {
            request.addParameter("pixel", Integer.toString(getImageRequest.getPixel()));
        }
        if (getImageRequest.CheckCropParam()) {
            request.addParameter("crop", getImageRequest.getCropX() + "_" + getImageRequest.getCropY() + "_"
                    + getImageRequest.getCropWidth() + "_" + getImageRequest.getCropHeight());
        }

        if (-1 != getImageRequest.getQuality()) {
            request.addParameter("quality", Integer.toString(getImageRequest.getQuality()));
        }

        if (null != getImageRequest.getType()) {
            request.addParameter("type", getImageRequest.getType());
        }

        /**
         * we waterMake may be very complex(use url Base64 encode and decode)
         */
        String code = null;
        if (null != getImageRequest.getWaterMark()) {
            try {
                code = new String(Base64.encodeBase64(getImageRequest.getWaterMark().getBytes("UTF-8")));
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("UnsupportedEncodingException");
            }
            request.addParameter("watermark", code);
        }
        if (-1 != getImageRequest.getAxis()) {
            request.addParameter("axis", Integer.toString(getImageRequest.getAxis()));
        }

        if (-1 != getImageRequest.getRotation()) {
            request.addParameter("rotate", Integer.toString(getImageRequest.getRotation()));
            ;
        }

        if (-1 != getImageRequest.getInterlace()) {
            request.addParameter("interlace", Integer.toString(getImageRequest.getInterlace()));
            ;
        }

        return invoke(request, getImageRequest.getBucketName(), getImageRequest.getKey());
    }

    /****** Operations about vedio *****************/

    /**
     * used to Frame image from vedio
     *
     * @param bucketName
     * @param key
     * @return InputStream
     * @throws ClientException
     * @throws ServiceException
     */
    @Override
    public InputStream videoFrame(String bucketName, String key) throws ClientException, ServiceException {
        return videoFrame(new VideoFrameRequest(bucketName, key));
    }

    /**
     * used to Frame image from vedio
     *
     * @param videoFrameRequest
     * @return InputStream
     * @throws ClientException
     * @throws ServiceException
     */
    @Override
    public InputStream videoFrame(VideoFrameRequest videoFrameRequest) throws ClientException, ServiceException {
        // check the params
        assertParameterNotNull(videoFrameRequest, "The ResizeImageRequest parameter must be specified");
        assertParameterNotNull(videoFrameRequest.getBucketName(), "The bucket name parameter must be specified");
        assertParameterNotNull(videoFrameRequest.getKey(), "The key parameter must be specified");
        // do the real request
        Request<VideoFrameRequest> request = createRequest(videoFrameRequest.getBucketName(),
                videoFrameRequest.getKey(), videoFrameRequest, HttpMethodName.GET);

        /*
         * add paramters but we not check the logical correctness of the param ,if it is not correct, the server will
         * return error
         */
        // the vedio frame command
        request.addParameter("vframe", "1");

        if (-1 != videoFrameRequest.getOffet()) {
            request.addParameter("offset", Long.toString(videoFrameRequest.getOffet()));
        }

        if (-1 != videoFrameRequest.getResizeX() && -1 != videoFrameRequest.getResizeY()) {
            if (videoFrameRequest.getResizeX() <= 0 && videoFrameRequest.getResizeY() <= 0) {
                throw new IllegalArgumentException("Invaild resize parameter");
            }
            request.addParameter("resize", videoFrameRequest.getResizeX() + "x" + videoFrameRequest.getResizeY());
        }

        if (null != videoFrameRequest.getDefaultCorp()) {
            if (Boolean.TRUE.equals(videoFrameRequest.getDefaultCorp())) {
                request.addParameter("corp", "");
            } else {
                if (videoFrameRequest.getLeft() >= videoFrameRequest.getRight()
                        || videoFrameRequest.getTop() >= videoFrameRequest.getBottom()) {
                    throw new IllegalArgumentException("Invaild crop range");
                }
                request.addParameter("corp", videoFrameRequest.getLeft() + "_" + videoFrameRequest.getTop() + "_"
                        + videoFrameRequest.getRight() + "_" + videoFrameRequest.getBottom());
            }
        }

        return invoke(request, videoFrameRequest.getBucketName(), videoFrameRequest.getKey());
    }

    /**
     * Get the basic video info include the Width,Height,FrameRate,VideoBitrate and so on
     *
     * @param bucketName
     * @param key
     * @return VideoMetadata
     * @throws ClientException
     * @throws ServiceException
     */
    @Override
    public VideoMetadata getVideoMetaInfo(String bucketName, String key) throws ClientException, ServiceException {
        return getVideoMetaInfo(new GetVideoMetaInfoRequest(bucketName, key));
    }

    /**
     * Get the basic video info include the Width,Height,FrameRate,VideoBitrate and so on
     *
     * @param getVideoMetaInfoRequest
     * @return
     * @throws ClientException
     * @throws ServiceException
     */
    private VideoMetadata getVideoMetaInfo(GetVideoMetaInfoRequest getVideoMetaInfoRequest) throws ClientException,
            ServiceException {
        assertParameterNotNull(getVideoMetaInfoRequest.getBucketName(), "The bucket name parameter must be specified");
        assertParameterNotNull(getVideoMetaInfoRequest.getKey(), "The key parameter must be specified");

        Request<GetVideoMetaInfoRequest> request = createRequest(getVideoMetaInfoRequest.getBucketName(),
                getVideoMetaInfoRequest.getKey(), getVideoMetaInfoRequest, HttpMethodName.GET);

        request.addParameter("vinfo", "");
        return invoke(request, new Unmarshallers.GetVideoMetaInfoUnmarshaller(),
                getVideoMetaInfoRequest.getBucketName(), getVideoMetaInfoRequest.getKey());
    }

    @Override
    public void VideoTranscoding(VideoTranscodingRequest videoTranscodingRequest) throws ClientException,
            ServiceException {
        assertParameterNotNull(videoTranscodingRequest.getBucketName(), "The bucket name parameter must be specified");
        assertParameterNotNull(videoTranscodingRequest.getKey(), "The key parameter must be specified");
        assertParameterNotNull(videoTranscodingRequest.getType(), "The type parameter must be specified");
        assertParameterNotNull(videoTranscodingRequest.getCallBackURL(), "The callBackURL parameter must be specified");

        Request<VideoTranscodingRequest> request = createRequest(videoTranscodingRequest.getBucketName(),
                videoTranscodingRequest.getKey(), videoTranscodingRequest, HttpMethodName.POST);

        request.addParameter("type", videoTranscodingRequest.getType());

        if (videoTranscodingRequest.getResolutionX() != -1 && videoTranscodingRequest.getResolutionY() != -1) {
            request.addParameter("resolution",
                    videoTranscodingRequest.getResolutionX() + "x" + videoTranscodingRequest.getResolutionY());
        }

        if (videoTranscodingRequest.getFps() != -1) {
            request.addParameter("fps", videoTranscodingRequest.getFps() + "");
        }

        if (videoTranscodingRequest.getVCodec() != null) {
            request.addParameter("vcodec", videoTranscodingRequest.getVCodec());
        }

        if (videoTranscodingRequest.getACodec() != null) {
            request.addParameter("acodec", videoTranscodingRequest.getACodec());
        }

        if (videoTranscodingRequest.getOffset() != -1) {
            request.addParameter("offset", videoTranscodingRequest.getOffset() + "");
        }

        if (videoTranscodingRequest.getLength() != -1) {
            request.addParameter("length", videoTranscodingRequest.getLength() + "");
        }

        if (!videoTranscodingRequest.getDefaultCrop()) {
            if (videoTranscodingRequest.checkCropParam()) {
                request.addParameter("crop", videoTranscodingRequest.genCropString());
            } else {
                throw new IllegalArgumentException("Invaild crop range");
            }
        }

        if (videoTranscodingRequest.getSegtime() != -1) {
            request.addParameter("segtime", videoTranscodingRequest.getSegtime() + "");
        }

        request.addHeader("x-nos-codec-source", "/" + videoTranscodingRequest.getBucketName() + "/"
                + videoTranscodingRequest.getKey());

        request.addHeader("x-nos-callbackurl", videoTranscodingRequest.getCallBackURL());
        invoke(request, voidResponseHandler, videoTranscodingRequest.getBucketName(), videoTranscodingRequest.getKey());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.services.nos.Nos#listVersions(java.lang.String, java.lang.String)
     */
	/*public VersionListing listVersions(String bucketName) throws ClientException, ServiceException {
		return listVersions(new ListVersionsRequest(bucketName, null, null, null));
	}*/

    /*
     * (non-Javadoc)
     *
     * @see com.netease.services.nos.Nos#listVersions(java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String, java.lang.Integer)
     */
	/*public VersionListing listVersions(String bucketName, String keyMarker, String versionIdMarker, Integer maxKeys)
			throws ClientException, ServiceException {

		ListVersionsRequest request = new ListVersionsRequest().withBucketName(bucketName).withKeyMarker(keyMarker)
				.withVersionIdMarker(versionIdMarker).withMaxResults(maxKeys);
		return listVersions(request);
	}*/

    /*
     * (non-Javadoc)
     *
     * @see com.netease.services.nos.Nos#listVersions(com.netease.services.nos.model .ListVersionsRequest)
     */
	/*public VersionListing listVersions(ListVersionsRequest listVersionsRequest) throws ClientException,
			ServiceException {
		assertParameterNotNull(listVersionsRequest.getBucketName(),
				"The bucket name parameter must be specified when listing versions in a bucket");

		Request<ListVersionsRequest> request = createRequest(listVersionsRequest.getBucketName(), null,
				listVersionsRequest, HttpMethodName.GET);
		request.addParameter("versions", null);

		if (listVersionsRequest.getKeyMarker() != null)
			request.addParameter("key-marker", listVersionsRequest.getKeyMarker());
		if (listVersionsRequest.getVersionIdMarker() != null)
			request.addParameter("version-id-marker", listVersionsRequest.getVersionIdMarker());
		if (listVersionsRequest.getMaxResults() != null && listVersionsRequest.getMaxResults().intValue() >= 0)
			request.addParameter("max-keys", listVersionsRequest.getMaxResults().toString());

		return invoke(request, new Unmarshallers.VersionListUnmarshaller(), listVersionsRequest.getBucketName(), null);
	}*/

    /*
     * (non-Javadoc)
     *
     * @see com.netease.services.nos.Nos#getBucketLifecycleConfiguration(java.lang.String)
     */
    @Override
    public BucketLifecycleConfiguration getBucketLifecycleConfiguration(String bucketName) throws ClientException,
            ServiceException {
        assertParameterNotNull(bucketName, "The bucket name must be specifed when retrieving the bucket lifecycle configuration.");

        Request<GetBucketLifecycleConfigurationRequest> request = createRequest(bucketName, null, new GetBucketLifecycleConfigurationRequest(), HttpMethodName.GET);
        request.addParameter("lifecycle", null);

        return invoke(request, new Unmarshallers.BucketLifecycleConfigurationUnmarshaller(), bucketName, null);
    }

    @Override
    public void setBucketLifecycleConfiguration(String bucketName, BucketLifecycleConfiguration bucketLifecycleConfiguration) throws ClientException,
            ServiceException {
        setBucketLifecycleConfiguration(new SetBucketLifecycleConfigurationRequest(bucketName, bucketLifecycleConfiguration));
    }

    @Override
    public void setBucketLifecycleConfiguration(SetBucketLifecycleConfigurationRequest setBucketLifecycleConfigurationRequest)
            throws ClientException, ServiceException {
        assertParameterNotNull(setBucketLifecycleConfigurationRequest,
                "The set bucket lifecycle configuration request object must be specified.");

        String bucketName = setBucketLifecycleConfigurationRequest.getBucketName();
        BucketLifecycleConfiguration bucketLifecycleConfiguration = setBucketLifecycleConfigurationRequest.getLifecycleConfiguration();

        assertParameterNotNull(bucketName,
                "The bucket name parameter must be specified when setting bucket lifecycle configuration.");
        assertParameterNotNull(bucketLifecycleConfiguration,
                "The lifecycle configuration parameter must be specified when setting bucket lifecycle configuration.");
        assertParameterNotNull(bucketLifecycleConfiguration.getRules(),
                "bucketLifecycleConfiguration.getRules() is null, The rules must be specified when setting bucket lifecycle configuration.");

        Request<SetBucketLifecycleConfigurationRequest> request = createRequest(bucketName, null, setBucketLifecycleConfigurationRequest, HttpMethodName.PUT);
        request.addParameter("lifecycle", null);

        XmlWriter xml = new XmlWriter();
        xml.start("LifecycleConfiguration");

        for (Rule rule : bucketLifecycleConfiguration.getRules()) {
            xml.start("Rule");
            if (rule.getId() != null) {
                xml.start("ID").value(rule.getId()).end();
            }
            if (rule.getPrefix() != null) {
                xml.start("Prefix").value(rule.getPrefix()).end();
            }
            if (rule.getStatus() != null) {
                xml.start("Status").value(rule.getStatus()).end();
            }
            xml.start("Expiration");
            if (rule.getExpirationInDays() != null) {
                xml.start("Days").value(Integer.toString(rule.getExpirationInDays())).end();
            }

            if (rule.getExpirationDate() != null) {
                xml.start("Date").value(new DateUtils().formatIso8601DateUTC(rule.getExpirationDate())).end();
            }
            xml.end();
            xml.end();
        }

        xml.end();

        byte[] xmlByte = xml.getBytes();

        request.addHeader("Content-Length", String.valueOf(xmlByte.length));
        request.addHeader("Content-Type", "application/xml");
        request.setContent(new ByteArrayInputStream(xmlByte));
        try {
            byte[] md5 = Md5Utils.computeMD5Hash(xmlByte);
            //String md5Base64 = BinaryUtils.toBase64(md5);
            request.addHeader("Content-MD5", Md5Utils.getHex(md5));
        } catch (Exception e) {
            throw new ClientException("Couldn't compute md5 sum", e);
        }

        invoke(request, voidResponseHandler, bucketName, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.netease.services.nos.Nos#deleteBucketLifecycleConfiguration(java.lang.String)
     */
    @Override
    public void deleteBucketLifecycleConfiguration(String bucketName) throws ClientException,
            ServiceException {
        assertParameterNotNull(bucketName,
                "The bucket name parameter must be specified when deleting bucket lifecycle configuration.");

        Request<DeleteBucketLifecycleConfigurationRequest> request = createRequest(bucketName, null, new DeleteBucketLifecycleConfigurationRequest(), HttpMethodName.DELETE);
        request.addParameter("lifecycle", null);

        invoke(request, voidResponseHandler, bucketName, null);
    }

    public String signString(String stringToSign) {

        Credentials credentials = CredentialsProvider.getCredentials();

        return (new NosStringSigner()).sign(stringToSign, credentials);
    }

    public boolean validateStringSignature(String stringToSign, String signature) {

        Credentials credentials = CredentialsProvider.getCredentials();

        String signatueLocal = (new NosStringSigner()).sign(stringToSign, credentials);

        return signatueLocal.equals(signature);
    }


    public static void main(String[] args) {
        XmlWriter xml = new XmlWriter();
        xml.start("Default404Configuration", "xmlns", Constants.XML_NAMESPACE);
        xml.start("Key").value("hello").end();
        xml.end();

        System.out.println(xml.toString());
    }

}
