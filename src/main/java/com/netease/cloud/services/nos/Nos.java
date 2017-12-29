package com.netease.cloud.services.nos;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import com.netease.cloud.ClientException;
import com.netease.cloud.HttpMethod;
import com.netease.cloud.ServiceException;
import com.netease.cloud.services.nos.model.AbortMultipartUploadRequest;
import com.netease.cloud.services.nos.model.Bucket;
import com.netease.cloud.services.nos.model.BucketLifecycleConfiguration;
import com.netease.cloud.services.nos.model.CannedAccessControlList;
import com.netease.cloud.services.nos.model.CompleteMultipartUploadRequest;
import com.netease.cloud.services.nos.model.CompleteMultipartUploadResult;
import com.netease.cloud.services.nos.model.CopyObjectRequest;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.DeleteBucketRequest;
import com.netease.cloud.services.nos.model.DeleteObjectRequest;
import com.netease.cloud.services.nos.model.DeleteObjectsRequest;
import com.netease.cloud.services.nos.model.DeleteObjectsResult;
import com.netease.cloud.services.nos.model.GeneratePresignedUrlRequest;
import com.netease.cloud.services.nos.model.GetBucketAclRequest;
import com.netease.cloud.services.nos.model.GetBucketLocationRequest;
import com.netease.cloud.services.nos.model.GetBucketStatsResult;
import com.netease.cloud.services.nos.model.GetObjectMetadataRequest;
import com.netease.cloud.services.nos.model.GetObjectRequest;
import com.netease.cloud.services.nos.model.InitiateMultipartUploadRequest;
import com.netease.cloud.services.nos.model.InitiateMultipartUploadResult;
import com.netease.cloud.services.nos.model.ListBucketsRequest;
import com.netease.cloud.services.nos.model.ListMultipartUploadsRequest;
import com.netease.cloud.services.nos.model.ListObjectsRequest;
import com.netease.cloud.services.nos.model.ListPartsRequest;
import com.netease.cloud.services.nos.model.MoveObjectRequest;
import com.netease.cloud.services.nos.model.MultiObjectDeleteException;
import com.netease.cloud.services.nos.model.MultipartUploadListing;
import com.netease.cloud.services.nos.model.NOSObject;
import com.netease.cloud.services.nos.model.ObjectListing;
import com.netease.cloud.services.nos.model.ObjectMetadata;
import com.netease.cloud.services.nos.model.PartListing;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.cloud.services.nos.model.PutObjectResult;
import com.netease.cloud.services.nos.model.Region;
import com.netease.cloud.services.nos.model.SetBucketAclRequest;
import com.netease.cloud.services.nos.model.SetBucketLifecycleConfigurationRequest;
import com.netease.cloud.services.nos.model.UploadPartRequest;
import com.netease.cloud.services.nos.model.UploadPartResult;

/**
 * <p>
 * The interface for accessing the Netease NOS web service.
 * </p>
 * <p>
 * Netease NOS provides storage service on the Internet.
 * </p>
 * <p>
 * This NOS Java SDK provides a simple interface that can be used to store and
 * retrieve any amount of data, at any time, from anywhere on the web.
 * </p>
 */
public interface Nos {

	/**
	 * <p>
	 * Returns a list of summary information about the objects in the specified
	 * buckets. List results are <i>always</i> returned in alphabetical order.
	 * </p>
	 * 
	 * @param bucketName
	 *            The name of the bucket to be listed.
	 * 
	 * @return a list of object summary information in specified bucket.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point.
	 * 
	 */
	public ObjectListing listObjects(String bucketName) throws ClientException, ServiceException;

	/**
	 * <p>
	 * According to some query condition to return a list of summary information
	 * about the objects in the specified buckets.
	 * </p>
	 * 
	 * @param listObjectsRequest
	 *            The request object containing all query conditions for listing
	 *            the objects in a specified bucket.
	 * 
	 * @return A listing of the objects in the specified bucket, along with any
	 *         other associated information, such as common prefixes (if a
	 *         delimiter was specified), the original request parameters, etc.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point.
	 * 
	 * @see Nos#listObjects(String)
	 */
	public ObjectListing listObjects(ListObjectsRequest listObjectsRequest) throws ClientException, ServiceException;

	/**
	 * <p>
	 * See if the specified object exists.
	 * <p>
	 * 
	 * @param bucketName
	 *            The name of the bucket will to be checked.
	 * @param key
	 *            The name of the object will to be checked.
	 * @return return <code>true</code> if the specified object exists in NOS;
	 *         otherwise return <code>false</code>.
	 * @throws ClientException
	 *             If any errors are occured in the client point. If any errors
	 *             occurred in NOS server point.
	 */
	public boolean doesObjectExist(String bucketName, String key) throws ClientException,
			ServiceException;

	/**
	 * <p>
	 * See if the specified bucket exists. Note:bucket in NOS is unique
	 * globally,It's mean that if someone has create a bucket ,anybody can't to
	 * create the bucket with the same bucket name.
	 * <p>
	 * 
	 * @param bucketName
	 *            The name of the bucket will to be checked.
	 * 
	 * @return return <code>true</code> if the specified bucket exists in NOS;
	 *         otherwise return <code>false</code>.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point.
	 * 
	 */
	public boolean doesBucketExist(String bucketName) throws ClientException, ServiceException;

	/**
	 * <p>
	 * List your all buckets owned by you.
	 * </p>
	 * 
	 * @return A list of all buckets owned by you.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point.
	 * 
	 */
	public List<Bucket> listBuckets() throws ClientException, ServiceException;

	/**
	 * @see Nos#listBuckets()
	 */
	public List<Bucket> listBuckets(ListBucketsRequest listBucketsRequest) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Creates a new NOS bucket ,and you can specified the region,ACL,and
	 * whether this bucket is deduplicate supported or not.
	 * </p>
	 * 
	 * @param createBucketRequest
	 *            The request object containing all options for creating an NOS
	 *            bucket.
	 * @return The newly created bucket.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point.
	 * 
	 * @see Nos#createBucket(String)
	 */
	public Bucket createBucket(CreateBucketRequest createBucketRequest) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Creates a new NOS bucket in the default region,
	 * {@link Region#CN_Hnagzhou}.and default ACL
	 * {@link CannedAccessControlList#Private},and default deduplicate status
	 * {@link DeduplicateStatus#Disabled}
	 * </p>
	 * 
	 * 
	 * @param bucketName
	 *            The name of the bucket to create. All buckets in NOS share a
	 *            single namespace; ensure the bucket is given a unique name. In
	 *            addition,the bucket name must follow orders:
	 *            <ul>
	 *            <li>Bucket names should not contain underscores</li>
	 *            <li>Bucket names should be between 3 and 63 characters long</li>
	 *            <li>Bucket names should not end with a dash</li>
	 *            <li>Bucket names cannot contain adjacent periods</li>
	 *            <li>Bucket names cannot contain dashes next to periods (e.g.,
	 *            "my-.bucket.com" and "my.-bucket" are invalid)</li>
	 *            <li>Bucket names cannot contain uppercase characters</li>
	 *            </ul>
	 * 
	 * @return The newly created bucket.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point.
	 * 
	 * @see Nos#createBucket(CreateBucketRequest)
	 */
	public Bucket createBucket(String bucketName) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Creates a new NOS bucket with the specified name in the specified
	 * region,and is supported dedupicate or not.
	 * </p>
	 * 
	 * @param bucketName
	 *            The name of the bucket to create. All buckets in NOS share a
	 *            single namespace; ensure the bucket is given a unique name. In
	 *            addition,the bucket name must follow orders:
	 *            <ul>
	 *            <li>Bucket names should not contain underscores</li>
	 *            <li>Bucket names should be between 3 and 63 characters long</li>
	 *            <li>Bucket names should not end with a dash</li>
	 *            <li>Bucket names cannot contain adjacent periods</li>
	 *            <li>Bucket names cannot contain dashes next to periods (e.g.,
	 *            "my-.bucket.com" and "my.-bucket" are invalid)</li>
	 *            <li>Bucket names cannot contain uppercase characters</li>
	 *            </ul>
	 * @param region
	 *            The region in which to create the new bucket.
	 * @param deduplicate
	 *            If the value is <code>true</code> then this bucket will
	 *            deduplicate supported, means if you want to upload an existed
	 *            object in this bucket,it's not be to upload repeatedly.
	 * @return The newly created bucket.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 */
	public Bucket createBucket(String bucketName, Region region) throws ClientException,
			ServiceException;

	/**
	 * <p>
	 * Creates a new NOS bucket with the specified name in the specified
	 * region,and is supported dedupicate or not.
	 * </p>
	 * 
	 * @param bucketName
	 *            The name of the bucket to create. All buckets in NOS share a
	 *            single namespace; ensure the bucket is given a unique name. In
	 *            addition,the bucket name must follow orders:
	 *            <ul>
	 *            <li>Bucket names should not contain underscores</li>
	 *            <li>Bucket names should be between 3 and 63 characters long</li>
	 *            <li>Bucket names should not end with a dash</li>
	 *            <li>Bucket names cannot contain adjacent periods</li>
	 *            <li>Bucket names cannot contain dashes next to periods (e.g.,
	 *            "my-.bucket.com" and "my.-bucket" are invalid)</li>
	 *            <li>Bucket names cannot contain uppercase characters</li>
	 *            </ul>
	 * @param region
	 *            The NOS region in which to create the new bucket. String value
	 *            of {@link Region}.
	 * @param deduplicate
	 *            If the value is <code>true</code> then this bucket will
	 *            deduplicate supported, means if you want to upload an existed
	 *            object in this bucket,it's not be to upload repeatedly.
	 * @return The newly created bucket.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 */
	public Bucket createBucket(String bucketName, String region) throws ClientException,
			ServiceException;

	/**
	 * <p>
	 * Gets the {@link CannedAccessControlList} (ACL) of the specified
	 * bucket,Each bucket has an ACL that defines its access control policy.
	 * </p>
	 * 
	 * 
	 * @param bucketName
	 *            The name of the bucket whose ACL is being retrieved.
	 * 
	 * @return The <code>CannedAccessControlList</code> for the specified NOS
	 *         bucket.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 */
	public CannedAccessControlList getBucketAcl(String bucketName) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Sets the {@link CannedAccessControlList} (ACL)for the specified
	 * bucket,Each bucket has an ACL that defines its access control policy.if
	 * the ACL is private,anybody expect you can access objects in the bucket,if
	 * the ACL is private-read,then others can download the objects in this
	 * bucket.
	 * </p>
	 * 
	 * @param setBucketAclRequest
	 *            The request object containing the bucket to modify and the ACL
	 *            to set.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 */
	public void setBucketAcl(SetBucketAclRequest setBucketAclRequest) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Gets the (ACL) of the specified bucket,Each bucket has an ACL that
	 * defines its access control policy.
	 * </p>
	 * 
	 * @param getBucketAclRequest
	 *            The request containing the name of the bucket whose ACL is
	 *            being retrieved.
	 * 
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 */
	public CannedAccessControlList getBucketAcl(GetBucketAclRequest getBucketAclRequest) throws ClientException,
			ServiceException;

	/**
	 * <p>
	 * Sets the {@link CannedAccessControlList} (ACL)for the specified
	 * bucket,Each bucket has an ACL that defines its access control policy.if
	 * the ACL is private,anybody expect you can access objects in the bucket,if
	 * the ACL is private-read,then others can download the objects in this
	 * bucket.
	 * </p>
	 * 
	 * @param bucketName
	 *            The name of the bucket whose ACL is being set.
	 * @param acl
	 *            The pre-configured <code>CannedAccessControlLists</code> to
	 *            set for the specified bucket.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 */
	public void setBucketAcl(String bucketName, CannedAccessControlList acl) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Gets the geographical region where Nos stores the specified bucket.
	 * </p>
	 * <p>
	 * To view the location constraint of a bucket, the user must be the bucket
	 * owner.
	 * </p>
	 * <p>
	 * Note that <code>Region</code> enumeration values are not returned
	 * directly from this method.
	 * </p>
	 * 
	 * @param bucketName
	 *            The name of the Nos bucket to look up. This must be a bucket
	 *            the user owns.
	 * 
	 * @return The location of the specified Nos bucket.
	 * 
	 * @throws ClientException
	 *             If any errors are encountered in the client point.
	 * @throws ServiceException
	 *             If any errors occurred in the server point.
	 * 
	 */
	public String getBucketLocation(String bucketName) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Gets the geographical region where Nos stores the specified bucket.
	 * </p>
	 * <p>
	 * To view the location constraint of a bucket, the user must be the bucket
	 * owner.
	 * </p>
	 * <p>
	 * Note that <code>Region</code> enumeration values are not returned
	 * directly from this method.
	 * </p>
	 * 
	 * @param getBucketLocationRequest
	 *            The request object containing the name of the Nos bucket to
	 *            look up. This must be a bucket the user owns.
	 * 
	 * @return The location of the specified Nos bucket.
	 * 
	 * @throws ClientException
	 *             If any errors are encountered in the client point.
	 * @throws ServiceException
	 *             If any errors occurred in the server point.
	 * 
	 */
	public String getBucketLocation(GetBucketLocationRequest getBucketLocationRequest) throws ClientException,
			ServiceException;

	/**
	 * <p>
	 * Gets the metadata for the specified object without actually fetching the
	 * object itself. This is useful in obtaining only the object metadata, and
	 * avoids wasting bandwidth on fetching the object data.
	 * </p>
	 * <p>
	 * The object metadata contains information such as content type, content
	 * disposition, etc., as well as custom user metadata that can be associated
	 * with an object.
	 * </p>
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the object's whose metadata
	 *            is being retrieved.
	 * @param key
	 *            The key of the object whose metadata is being retrieved.
	 * 
	 * @return All object metadata for the specified bucket.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 * @see Nos#getObjectMetadata(GetObjectMetadataRequest)
	 */
	public ObjectMetadata getObjectMetadata(String bucketName, String key) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Gets the metadata for the specified object without actually fetching the
	 * object itself. This is useful in obtaining only the object metadata, and
	 * avoids wasting bandwidth on fetching the object data.
	 * </p>
	 * <p>
	 * The object metadata contains information such as content type, content
	 * disposition, etc., as well as custom user metadata that can be associated
	 * with an object.
	 * </p>
	 * 
	 * @param getObjectMetadataRequest
	 *            The request object specifying the bucket, key and optional
	 *            version ID of the object whose metadata is being retrieved.
	 * 
	 * @return All object metadata for the specified object.
	 * 
	 * @throws ClientException
	 *             If any errors are encountered on the client while making the
	 *             request or
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 * @see Nos#getObjectMetadata(String, String)
	 */
	public ObjectMetadata getObjectMetadata(GetObjectMetadataRequest getObjectMetadataRequest) throws ClientException,
			ServiceException;

	/**
	 * <p>
	 * Gets the object stored in NOS under the specified bucket and key.
	 * </p>
	 * <p>
	 * Be extremely careful when using this method; the returned NOS object
	 * contains a direct stream of data from the HTTP connection. The underlying
	 * HTTP connection cannot be closed until the user finishes reading the data
	 * and closes the stream. Therefore:
	 * </p>
	 * <ul>
	 * <li>Use the data from the input stream in NOS object as soon as possible</li>
	 * <li>Close the input stream in NOS object as soon as possible</li>
	 * </ul>
	 * If these rules are not followed, the client can run out of resources by
	 * allocating too many open, but unused, HTTP connections. </p>
	 * <p>
	 * If the object fetched is publicly readable, it can also read it by
	 * pasting its URL into a browser.
	 * </p>
	 * <p>
	 * For more advanced options (such as downloading only a range of an
	 * object's content, or placing constraints on when the object should be
	 * downloaded) callers can use {@link #getObject(GetObjectRequest)}.
	 * </p>
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the desired object.
	 * @param key
	 *            The key under which the desired object is stored.
	 * 
	 * @return The object stored in NOS in the specified bucket and key.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 * @see Nos#getObject(GetObjectRequest)
	 * @see Nos#getObject(GetObjectRequest, File)
	 */
	public NOSObject getObject(String bucketName, String key) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Gets the object stored in NOS under the specified bucket and key. Returns
	 * <code>null</code> if the specified constraints weren't met.
	 * </p>
	 * <p>
	 * Callers should be very careful when using this method; the returned NOS
	 * object contains a direct stream of data from the HTTP connection. The
	 * underlying HTTP connection cannot be closed until the user finishes
	 * reading the data and closes the stream. Callers should therefore:
	 * </p>
	 * <ul>
	 * <li>Use the data from the input stream in NOS object as soon as possible,
	 * </li>
	 * <li>Close the input stream in NOS object as soon as possible.</li>
	 * </ul>
	 * <p>
	 * If callers do not follow those rules, then the client can run out of
	 * resources if allocating too many open, but unused, HTTP connections.
	 * </p>
	 * <p>
	 * If the object fetched is publicly readable, it can also read it by
	 * pasting its URL into a browser.
	 * </p>
	 * <p>
	 * When specifying constraints in the request object, the client needs to be
	 * prepared to handle this method returning <code>null</code> if the
	 * provided constraints aren't met when NOS receives the request.
	 * </p>
	 * <p>
	 * If the advanced options provided in {@link GetObjectRequest} aren't
	 * needed, use the simpler
	 * {@link Nos#getObject(String bucketName, String key)} method.
	 * </p>
	 * 
	 * @param getObjectRequest
	 *            The request object containing all the options on how to
	 *            download the object.
	 * 
	 * @return The object stored in NOS in the specified bucket and key. Returns
	 *         <code>null</code> if constraints were specified but not met.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * @see Nos#getObject(String, String)
	 * @see Nos#getObject(GetObjectRequest, File)
	 */
	public NOSObject getObject(GetObjectRequest getObjectRequest) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Gets the object metadata for the object stored in NOS under the specified
	 * bucket and key, and saves the object contents to the specified file.
	 * Returns <code>null</code> if the specified constraints weren't met.
	 * </p>
	 * <p>
	 * Instead of using {@link Nos#getObject(GetObjectRequest)}, use this method
	 * to ensure that the underlying HTTP stream resources are automatically
	 * closed as soon as possible. The NOS clients handles immediate storage of
	 * the object contents to the specified file.
	 * </p>
	 * <p>
	 * If the object fetched is publicly readable, it can also read it by
	 * pasting its URL into a browser.
	 * </p>
	 * <p>
	 * When specifying constraints in the request object, the client needs to be
	 * prepared to handle this method returning <code>null</code> if the
	 * provided constraints aren't met when NOS receives the request.
	 * </p>
	 * 
	 * @param getObjectRequest
	 *            The request object containing all the options on how to
	 *            download the NOS object content.
	 * @param destinationFile
	 *            Indicates the file (which might already exist) where to save
	 *            the object content being downloading from NOS.
	 * 
	 * @return All NOS object metadata for the specified object. Returns
	 *         <code>null</code> if constraints were specified but not met.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 * @see Nos#getObject(String, String)
	 * @see Nos#getObject(GetObjectRequest)
	 */
	public ObjectMetadata getObject(GetObjectRequest getObjectRequest, File destinationFile) throws ClientException,
			ServiceException;

	/**
	 * Returns a list of object history versions in specified bucket.
	 * 
	 * @param bucketName
	 *            The name of the nos bucket whose versions are to be listed.
	 * 
	 * @param key
	 *            The object need to list it's all history versions
	 * 
	 * @return A listing of the versions of the object in the specified bucket,
	 *         along with any other associated information and original request
	 *         parameters.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 */
	//public GetObjectVersionsResult getObjectVersions(String bucketName, String key) throws ClientException,
	//		ServiceException;

	/**
	 * Returns a list of object history versions in specified bucket.
	 * 
	 * @param getObjectVersionsRequest
	 *            The request object containing the bucket and the object key
	 *            information.
	 * 
	 * 
	 * @return A listing of the versions of the object in the specified bucket,
	 *         along with any other associated information and original request
	 *         parameters.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 */
	//public GetObjectVersionsResult getObjectVersions(GetObjectVersionsRequest getObjectVersionsRequest)
	//		throws ClientException, ServiceException;

	/**
	 * <p>
	 * Deletes the specified bucket. All objects (and all object versions, if
	 * versioning was ever enabled) in the bucket must be deleted before the
	 * bucket itself can be deleted.
	 * </p>
	 * <p>
	 * Only the owner of a bucket can delete it, regardless of the bucket's
	 * access control policy (ACL).
	 * </p>
	 * 
	 * @param deleteBucketRequest
	 *            The request object containing all options for deleting an NOS
	 *            bucket.
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 * @see Nos#deleteBucket(String)
	 */
	public void deleteBucket(DeleteBucketRequest deleteBucketRequest) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Deletes the specified bucket. All objects (and all object versions, if
	 * versioning was ever enabled) in the bucket must be deleted before the
	 * bucket itself can be deleted.
	 * </p>
	 * <p>
	 * Only the owner of a bucket can delete it, regardless of the bucket's
	 * access control policy.
	 * </p>
	 * 
	 * @param bucketName
	 *            The name of the bucket to delete.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 * @see Nos#deleteBucket(String)
	 */
	public void deleteBucket(String bucketName) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Uploads a new object to the specified NOS bucket. The
	 * <code>PutObjectRequest</code> contains all the details of the request,
	 * including the bucket to upload to, the key the object will be uploaded
	 * under, and the file or input stream containing the data to upload.
	 * </p>
	 * <p>
	 * NOS never stores partial objects; if during this call an exception wasn't
	 * thrown, the entire object was stored.
	 * </p>
	 * <p>
	 * Depending on whether a file or input stream is being uploaded, this
	 * method has slightly different behavior.
	 * </p>
	 * <p>
	 * When uploading a file:
	 * </p>
	 * <ul>
	 * <li>
	 * The client automatically computes a checksum of the file. NOS uses
	 * checksums to validate the data in each file.</li>
	 * <li>
	 * Using the file extension, NOS attempts to determine the correct content
	 * type and content disposition to use for the object.</li>
	 * </ul>
	 * <p>
	 * When uploading directly from an input stream:
	 * </p>
	 * <ul>
	 * <li>Be careful to set the correct content type in the metadata object
	 * before directly sending a stream. Unlike file uploads, content types from
	 * input streams cannot be automatically determined. If the caller doesn't
	 * explicitly set the content type, it will not be set in NOS.</li>
	 * <li>Content length <b>must</b> be specified before data can be uploaded
	 * to NOS. NOS explicitly requires that the content length be sent in the
	 * request headers before it will accept any of the data. If the caller
	 * doesn't provide the length, the library must buffer the contents of the
	 * input stream in order to calculate it.
	 * </ul>
	 * <p>
	 * If versioning is enabled for the specified bucket, this operation will
	 * never overwrite an existing object with the same key, but will keep the
	 * existing object as an older version until that version is explicitly
	 * deleted (see {@link Nos#deleteVersion(String, String, String)}.
	 * </p>
	 * 
	 * <p>
	 * If versioning is not enabled, this operation will overwrite an existing
	 * object with the same key; NOS will store the last write request. NOS does
	 * not provide object locking. If NOS receives multiple write requests for
	 * the same object nearly simultaneously, all of the objects might be
	 * stored. However, a single object will be stored with the final write
	 * request.
	 * </p>
	 * 
	 * <p>
	 * When specifying a location constraint when creating a bucket, all objects
	 * added to the bucket are stored in the bucket's region. For example, if
	 * specifying a Europe (EU) region constraint for a bucket, all of that
	 * bucket's objects are stored in the EU region.
	 * </p>
	 * <p>
	 * 
	 * @param putObjectRequest
	 *            The request object containing all the parameters to upload a
	 *            new object to NOS.
	 * 
	 * @return A {@link PutObjectResult} object containing the information
	 *         returned by NOS for the newly created object.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 * @see Nos#putObject(String, String, File)
	 * @see Nos#putObject(String, String, InputStream, ObjectMetadata)
	 */
	public PutObjectResult putObject(PutObjectRequest putObjectRequest) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Uploads the specified file to NOS under the specified bucket and key
	 * name.
	 * </p>
	 * <p>
	 * NOS never stores partial objects; if during this call an exception wasn't
	 * thrown, the entire object was stored.
	 * </p>
	 * <p>
	 * The client automatically computes a checksum of the file. NOS uses
	 * checksums to validate the data in each file.
	 * </p>
	 * <p>
	 * Using the file extension, NOS attempts to determine the correct content
	 * type and content disposition to use for the object.
	 * </p>
	 * <p>
	 * If versioning is enabled for the specified bucket, this operation will
	 * this operation will never overwrite an existing object with the same key,
	 * but will keep the existing object as an older version until that version
	 * is explicitly deleted (see
	 * {@link Nos#deleteVersion(String, String, String)}.
	 * </p>
	 * <p>
	 * If versioning is not enabled, this operation will overwrite an existing
	 * object with the same key; NOS will store the last write request. NOS does
	 * not provide object locking. If NOS receives multiple write requests for
	 * the same object nearly simultaneously, all of the objects might be
	 * stored. However, a single object will be stored with the final write
	 * request.
	 * </p>
	 * 
	 * <p>
	 * When specifying a location constraint when creating a bucket, all objects
	 * added to the bucket are stored in the bucket's region. For example, if
	 * specifying a Europe (EU) region constraint for a bucket, all of that
	 * bucket's objects are stored in EU region.
	 * </p>
	 * <p>
	 * 
	 * @param bucketName
	 *            The name of an existing bucket, to which you have
	 * @param key
	 *            The key under which to store the specified file.
	 * @param file
	 *            The file containing the data to be uploaded to NOS.
	 * 
	 * @return A {@link PutObjectResult} object containing the information
	 *         returned by NOS for the newly created object.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 * @see Nos#putObject(PutObjectRequest)
	 * @see Nos#putObject(String, String, InputStream, ObjectMetadata)
	 */
	public PutObjectResult putObject(String bucketName, String key, File file) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Uploads the specified input stream and object metadata to NOS under the
	 * specified bucket and key name.
	 * </p>
	 * <p>
	 * NOS never stores partial objects; if during this call an exception wasn't
	 * thrown, the entire object was stored.
	 * </p>
	 * <p>
	 * The client automatically computes a checksum of the file. This checksum
	 * is verified against another checksum that is calculated once the data
	 * reaches NOS, ensuring the data has not corrupted in transit over the
	 * network.
	 * </p>
	 * <p>
	 * Using the file extension, NOS attempts to determine the correct content
	 * type and content disposition to use for the object.
	 * </p>
	 * <p>
	 * Content length <b>must</b> be specified before data can be uploaded to
	 * NOS. If the caller doesn't provide it, the library will <b>have to</b>
	 * buffer the contents of the input stream in order to calculate it because
	 * NOS explicitly requires that the content length be sent in the request
	 * headers before any of the data is sent.
	 * </p>
	 * <p>
	 * If versioning is enabled for the specified bucket, this operation will
	 * never overwrite an existing object at the same key, but instead will keep
	 * the existing object around as an older version until that version is
	 * explicitly deleted (see {@link Nos#deleteVersion(String, String, String)}
	 * .
	 * </p>
	 * 
	 * <p>
	 * If versioning is not enabled, this operation will overwrite an existing
	 * object with the same key; NOS will store the last write request. NOS does
	 * not provide object locking. If NOS receives multiple write requests for
	 * the same object nearly simultaneously, all of the objects might be
	 * stored. However, a single object will be stored with the final write
	 * request.
	 * </p>
	 * 
	 * <p>
	 * When specifying a location constraint when creating a bucket, all objects
	 * added to the bucket are stored in the bucket's region. For example, if
	 * specifying a Europe (EU) region constraint for a bucket, all of that
	 * bucket's objects are stored in EU region.
	 * </p>
	 * 
	 * @param bucketName
	 *            The name of an existing bucket, to which you have
	 * @param key
	 *            The key under which to store the specified file.
	 * @param input
	 *            The input stream containing the data to be uploaded to NOS.
	 * @param metadata
	 *            Additional metadata instructing NOS how to handle the uploaded
	 *            data (e.g. custom user metadata, hooks for specifying content
	 *            type, etc.).
	 * 
	 * @return A {@link PutObjectResult} object containing the information
	 *         returned by NOS for the newly created object.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 * @see Nos#putObject(String, String, File)
	 * @see Nos#putObject(PutObjectRequest)
	 */
	public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata)
			throws ClientException, ServiceException;

	public void putObjectMeta(String bucketName, String key, ObjectMetadata metadata)
			throws ClientException, ServiceException;
	
	/**
	 * <p>
	 * move a source object to a new destination in NOS.
	 * </p>
	 * <p>
	 * To move an object, the caller's account must have read access to the
	 * source object and write access to the destination bucket
	 * </p>
	 * 
	 * @param sourceBucketName
	 *            The name of the bucket containing the source object to move.
	 * @param sourceKey
	 *            The key in the source bucket under which the source object is
	 *            stored.
	 * @param destinationBucketName
	 *            The name of the bucket in which the new object will be
	 *            created. This can be the same name as the source bucket's.
	 * @param destinationKey
	 *            The key in the destination bucket under which the new object
	 *            will be created.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 */
	public void moveObject(String sourceBucketName, String sourceKey, String destinationBucketName,
			String destinationKey) throws ClientException, ServiceException;

	/**
	 * <p>
	 * move a source object to a new destination in NOS.
	 * </p>
	 * <p>
	 * To move an object, the caller's account must have read access to the
	 * source object and write access to the destination bucket
	 * </p>
	 * 
	 * @param moveObjectRequest
	 *            The request object containing all the options for moving an
	 *            NOS object.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 */
	public void moveObject(MoveObjectRequest moveObjectRequest) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Copies a source object to a new destination in NOS.
	 * </p>
	 * <p>
	 * By default, all object metadata for the source object are copied to the
	 * new destination object. The NOS <code>AcccessControlList</code> (ACL) is
	 * <b>not</b> copied to the new object; the new object will have the default
	 * NOS ACL, {@link CannedAccessControlList#Private}.
	 * </p>
	 * <p>
	 * To copy an object, the caller's account must have read access to the
	 * source object and write access to the destination bucket
	 * </p>
	 * <p>
	 * This method only exposes the basic options for copying an NOS object.
	 * Additional options are available by calling the
	 * {@link NosClient#copyObject(CopyObjectRequest)} method, including
	 * conditional constraints for copying objects, setting ACLs, overwriting
	 * object metadata, etc.
	 * </p>
	 * 
	 * @param sourceBucketName
	 *            The name of the bucket containing the source object to copy.
	 * @param sourceKey
	 *            The key in the source bucket under which the source object is
	 *            stored.
	 * @param destinationBucketName
	 *            The name of the bucket in which the new object will be
	 *            created. This can be the same name as the source bucket's.
	 * @param destinationKey
	 *            The key in the destination bucket under which the new object
	 *            will be created.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 * @see NosClient#copyObject(CopyObjectRequest)
	 */
	public void copyObject(String sourceBucketName, String sourceKey, String destinationBucketName,
			String destinationKey) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Copies a source object to a new destination in NOS.
	 * </p>
	 * <p>
	 * By default, all object metadata for the source object are copied to the
	 * new destination object, unless new object metadata in the specified
	 * {@link CopyObjectRequest} is provided.
	 * </p>
	 * <p>
	 * The NOS Acccess Control List (ACL) is <b>not</b> copied to the new
	 * object. The new object will have the default NOS ACL,
	 * {@link CannedAccessControlList#Private}, unless one is explicitly
	 * provided in the specified {@link CopyObjectRequest}.
	 * </p>
	 * <p>
	 * To copy an object, the caller's account must have read access to the
	 * source object and write access to the destination bucket.
	 * </p>
	 * <p>
	 * This method exposes all the advanced options for copying an NOS object.
	 * For simple needs, use the
	 * {@link NosClient#copyObject(String, String, String, String)} method.
	 * </p>
	 * 
	 * @param copyObjectRequest
	 *            The request object containing all the options for copying an
	 *            NOS object.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 * @see NosClient#copyObject(String, String, String, String)
	 */
	public void copyObject(CopyObjectRequest copyObjectRequest) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Deletes the specified object in the specified bucket. Once deleted, the
	 * object can only be restored if versioning was enabled when the object was
	 * deleted.
	 * </p>
	 * <p>
	 * If attempting to delete an object that does not exist, NOS returns a
	 * success message instead of an error message.
	 * </p>
	 * 
	 * @param bucketName
	 *            The name of the NOS bucket containing the object to delete.
	 * @param key
	 *            The key of the object to delete.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 * @see NosClient#deleteObject(DeleteObjectRequest)
	 */
	public void deleteObject(String bucketName, String key) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Deletes the specified object in the specified bucket. Once deleted, the
	 * object can only be restored if versioning was enabled when the object was
	 * deleted.
	 * </p>
	 * <p>
	 * If attempting to delete an object that does not exist, NOS will return a
	 * success message instead of an error message.
	 * </p>
	 * 
	 * @param deleteObjectRequest
	 *            The request object containing all options for deleting an NOS
	 *            object.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 * @see NosClient#deleteObject(String, String)
	 */
	public void deleteObject(DeleteObjectRequest deleteObjectRequest) throws ClientException, ServiceException;

	/**
	 * Deletes multiple objects in a single bucket from NOS.
	 * <p>
	 * In some cases, some objects will be successfully deleted, while some
	 * attempts will cause an error. If any object in the request cannot be
	 * deleted, this method throws a {@link MultiObjectDeleteException} with
	 * details of the error.
	 * 
	 * @param deleteObjectsRequest
	 *            The request object containing all options for deleting
	 *            multiple objects.
	 * @throws MultiObjectDeleteException
	 *             if one or more of the objects couldn't be deleted.
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 */
	public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest) throws ClientException,
			ServiceException;

	/**
	 * <p>
	 * Deletes a specific version of the specified object in the specified
	 * bucket. Once deleted, there is no method to restore or undelete an object
	 * version. This is the only way to permanently delete object versions that
	 * are protected by versioning.
	 * </p>
	 * <p>
	 * Deleting an object version is permanent and irreversible. It is a
	 * privileged operation that only the owner of the bucket containing the
	 * version can perform.
	 * </p>
	 * <p>
	 * Users can only delete a version of an object if versioning is enabled for
	 * the bucket. For more information about enabling versioning for a bucket,
	 * see
	 * {@link #setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest)}
	 * .
	 * </p>
	 * <p>
	 * If attempting to delete an object that does not exist, NOS will return a
	 * success message instead of an error message.
	 * </p>
	 * 
	 * @param bucketName
	 *            The name of the NOS bucket containing the object to delete.
	 * @param key
	 *            The key of the object to delete.
	 * @param versionId
	 *            The version of the object to delete.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 */
	//public void deleteVersion(String bucketName, String key, String versionId) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Deletes a specific version of an object in the specified bucket. Once
	 * deleted, there is no method to restore or undelete an object version.
	 * This is the only way to permanently delete object versions that are
	 * protected by versioning.
	 * </p>
	 * <p>
	 * Deleting an object version is permanent and irreversible. It is a
	 * privileged operation that only the owner of the bucket containing the
	 * version can perform.
	 * </p>
	 * <p>
	 * Users can only delete a version of an object if versioning is enabled for
	 * the bucket. For more information about enabling versioning for a bucket,
	 * see
	 * {@link #setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest)}
	 * .
	 * </p>
	 * <p>
	 * If attempting to delete an object that does not exist, NOS will return a
	 * success message instead of an error message.
	 * </p>
	 * 
	 * @param deleteVersionRequest
	 *            The request object containing all options for deleting a
	 *            specific version of an NOS object.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 */
	//public void deleteVersion(DeleteVersionRequest deleteVersionRequest) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Returns the versioning configuration for the specified bucket.
	 * </p>
	 * <p>
	 * A bucket's versioning configuration can be in one of three possible
	 * states:
	 * <ul>
	 * <li>{@link BucketVersioningConfiguration#DISABLED}
	 * <li>{@link BucketVersioningConfiguration#ENABLED}
	 * <li>{@link BucketVersioningConfiguration#SUSPENDED}
	 * </ul>
	 * </p>
	 * <p>
	 * By default, new buckets are in the
	 * {@link BucketVersioningConfiguration#DISABLED DISABLED} state. Once
	 * versioning is enabled for a bucket the status can never be reverted to
	 * {@link BucketVersioningConfiguration#DISABLED DISABLED}.
	 * </p>
	 * <p>
	 * The versioning configuration of a bucket has different implications for
	 * each operation performed on that bucket or for objects within that
	 * bucket. For example, when versioning is enabled a <code>PutObject</code>
	 * operation creates a unique object version-id for the object being
	 * uploaded. The The <code>PutObject</code> API guarantees that, if
	 * versioning is enabled for a bucket at the time of the request, the new
	 * object can only be permanently deleted using a <code>DeleteVersion</code>
	 * operation. It can never be overwritten. Additionally, the
	 * <code>PutObject</code> API guarantees that, if versioning is enabled for
	 * a bucket the request, no other object will be overwritten by that
	 * request. Refer to the documentation sections for each API for information
	 * on how versioning status affects the semantics of that particular API.
	 * </p>
	 * <p>
	 * NOS is eventually consistent. It can take time for the versioning status
	 * of a bucket to be propagated throughout the system.
	 * </p>
	 * 
	 * @param bucketName
	 *            The bucket whose versioning configuration will be retrieved.
	 * 
	 * @return The bucket versioning configuration for the specified bucket.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 * @see Nos#setBucketVersioningConfiguration(SetBucketVersioningConfigurationRequest)
	 */
	//public BucketVersioningConfiguration getBucketVersioningConfiguration(String bucketName) throws ClientException,
	//		ServiceException;

	/**
	 * <p>
	 * Sets the versioning configuration for the specified bucket.
	 * </p>
	 * <p>
	 * A bucket's versioning configuration can be in one of three possible
	 * states:
	 * <ul>
	 * <li>{@link BucketVersioningConfiguration#DISABLED}
	 * <li>{@link BucketVersioningConfiguration#ENABLED}
	 * <li>{@link BucketVersioningConfiguration#SUSPENDED}
	 * </ul>
	 * </p>
	 * <p>
	 * By default, new buckets are in the
	 * {@link BucketVersioningConfiguration#DISABLED DISABLED} state. Once
	 * versioning is enabled for a bucket the status can never be reverted to
	 * {@link BucketVersioningConfiguration#DISABLED DISABLED}.
	 * </p>
	 * <p>
	 * Objects created before versioning was enabled or when versioning is
	 * suspended will be given the default <code>null</code> version ID (see
	 * {@link Constants#NULL_VERSION_ID}). Note that the <code>null</code>
	 * version ID is a valid version ID and is not the same as not having a
	 * version ID.
	 * </p>
	 * <p>
	 * The versioning configuration of a bucket has different implications for
	 * each operation performed on that bucket or for objects within that
	 * bucket. For example, when versioning is enabled a <code>PutObject</code>
	 * operation creates a unique object version-id for the object being
	 * uploaded. The The <code>PutObject</code> API guarantees that, if
	 * versioning is enabled for a bucket at the time of the request, the new
	 * object can only be permanently deleted using a <code>DeleteVersion</code>
	 * operation. It can never be overwritten. Additionally, the
	 * <code>PutObject</code> API guarantees that, if versioning is enabled for
	 * a bucket the request, no other object will be overwritten by that
	 * request. Refer to the documentation sections for each API for information
	 * on how versioning status affects the semantics of that particular API.
	 * </p>
	 * <p>
	 * NOS is eventually consistent. It can take time for the versioning status
	 * of a bucket to be propagated throughout the system.
	 * </p>
	 * 
	 * @param setBucketVersioningConfigurationRequest
	 *            The request object containing all options for setting the
	 *            bucket versioning configuration.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 * 
	 * @see Nos#getBucketVersioningConfiguration(String)
	 */
	/*public void setBucketVersioningConfiguration(
			SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest) throws ClientException,
			ServiceException;
			*/

	/**
	 * <p>
	 * Returns a pre-signed URL for accessing an NOS resource.
	 * </p>
	 * <p>
	 * Pre-signed URLs allow clients to form a URL for an NOS resource, and then
	 * sign it with the current security credentials. The pre-signed URL can be
	 * shared to other users, allowing access to the resource without providing
	 * an account's security credentials.
	 * </p>
	 * <p>
	 * Pre-signed URLs are useful in many situations where security credentials
	 * aren't available from the client that needs to make the actual request to
	 * NOS.
	 * </p>
	 * <p>
	 * For example, an application may need remote users to upload files to the
	 * application owner's NOS bucket, but doesn't need to ship the security
	 * credentials with the application. A pre-signed URL to PUT an object into
	 * the owner's bucket can be generated from a remote location with the
	 * owner's security credentials, then the pre-signed URL can be passed to
	 * the end user's application to use.
	 * </p>
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the desired object.
	 * @param key
	 *            The key in the specified bucket under which the desired object
	 *            is stored.
	 * @param expiration
	 *            The time at which the returned pre-signed URL will expire.
	 * 
	 * @return A pre-signed URL which expires at the specified time, and can be
	 *         used to allow anyone to download the specified object from NOS,
	 *         without exposing the owner's secret access key.
	 * 
	 * @throws ClientException
	 *             If there were any problems pre-signing the request for the
	 *             specified NOS object.
	 * 
	 * @see Nos#generatePresignedUrl(String, String, Date, HttpMethod)
	 * @see Nos#generatePresignedUrl(GeneratePresignedUrlRequest)
	 */
	public URL generatePresignedUrl(String bucketName, String key, Date expiration) throws ClientException;

	/**
	 * <p>
	 * Returns a pre-signed URL for accessing an NOS resource.
	 * </p>
	 * <p>
	 * Pre-signed URLs allow clients to form a URL for an NOS resource, and then
	 * sign it with the current security credentials. The pre-signed URL can be
	 * shared to other users, allowing access to the resource without providing
	 * an account's security credentials.
	 * </p>
	 * <p>
	 * Pre-signed URLs are useful in many situations where security credentials
	 * aren't available from the client that needs to make the actual request to
	 * NOS.
	 * </p>
	 * <p>
	 * For example, an application may need remote users to upload files to the
	 * application owner's NOS bucket, but doesn't need to ship the security
	 * credentials with the application. A pre-signed URL to PUT an object into
	 * the owner's bucket can be generated from a remote location with the
	 * owner's security credentials, then the pre-signed URL can be passed to
	 * the end user's application to use.
	 * </p>
	 * 
	 * @param bucketName
	 *            The name of the bucket containing the desired object.
	 * @param key
	 *            The key in the specified bucket under which the desired object
	 *            is stored.
	 * @param expiration
	 *            The time at which the returned pre-signed URL will expire.
	 * @param method
	 *            The HTTP method verb to use for this URL
	 * 
	 * @return A pre-signed URL which expires at the specified time, and can be
	 *         used to allow anyone to download the specified object from NOS,
	 *         without exposing the owner's secret access key.
	 * 
	 * @throws ClientException
	 *             If there were any problems pre-signing the request for the
	 *             specified NOS object.
	 * 
	 * @see Nos#generatePresignedUrl(String, String, Date)
	 * @see Nos#generatePresignedUrl(GeneratePresignedUrlRequest)
	 */
	public URL generatePresignedUrl(String bucketName, String key, Date expiration, HttpMethod method)
			throws ClientException;

	/**
	 * <p>
	 * Returns a pre-signed URL for accessing an NOS resource.
	 * </p>
	 * <p>
	 * Pre-signed URLs allow clients to form a URL for an NOS resource, and then
	 * sign it with the current security credentials. The pre-signed URL can be
	 * shared to other users, allowing access to the resource without providing
	 * an account's security credentials.
	 * </p>
	 * <p>
	 * Pre-signed URLs are useful in many situations where security credentials
	 * aren't available from the client that needs to make the actual request to
	 * NOS.
	 * </p>
	 * <p>
	 * For example, an application may need remote users to upload files to the
	 * application owner's NOS bucket, but doesn't need to ship the security
	 * credentials with the application. A pre-signed URL to PUT an object into
	 * the owner's bucket can be generated from a remote location with the
	 * owner's security credentials, then the pre-signed URL can be passed to
	 * the end user's application to use.
	 * </p>
	 * <p>
	 * Note that presigned URLs cannot be used to upload an object with an
	 * attached policy, as described in <a href=
	 * "https://..com/articles/1434?_encoding=UTF8&queryArg=searchQuery&x=0&fromSearch=1&y=0&searchPath=all"
	 * >this blog post</a>. That method is only suitable for POSTs from HTML
	 * forms by browsers.
	 * </p>
	 * 
	 * @param generatePresignedUrlRequest
	 *            The request object containing all the options for generating a
	 *            pre-signed URL (bucket name, key, expiration date, etc).
	 * @return A pre-signed URL that can be used to access an NOS resource
	 *         without requiring the user of the URL to know the account's
	 *         security credentials.
	 * @throws ClientException
	 *             If there were any problems pre-signing the request for the
	 *             NOS resource.
	 * @see Nos#generatePresignedUrl(String, String, Date)
	 * @see Nos#generatePresignedUrl(String, String, Date, HttpMethod)
	 */
	public URL generatePresignedUrl(GeneratePresignedUrlRequest generatePresignedUrlRequest) throws ClientException;

	/**
	 * Initiates a multipart upload and returns an InitiateMultipartUploadResult
	 * which contains an upload ID. This upload ID associates all the parts in
	 * the specific upload and is used in each of your subsequent
	 * {@link #uploadPart(UploadPartRequest)} requests. You also include this
	 * upload ID in the final request to either complete, or abort the multipart
	 * upload request.
	 * 
	 * @param request
	 *            The InitiateMultipartUploadRequest object that specifies all
	 *            the parameters of this operation.
	 * 
	 * @return An InitiateMultipartUploadResult from NOS.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 */
	public InitiateMultipartUploadResult initiateMultipartUpload(InitiateMultipartUploadRequest request)
			throws ClientException, ServiceException;

	/**
	 * Uploads a part in a multipart upload. You must initiate a multipart
	 * upload before you can upload any part.
	 * <p>
	 * Your UploadPart request must include an upload ID and a part number. The
	 * upload ID is the ID returned by NOS in response to your Initiate
	 * Multipart Upload request. Part number can be any number between 1 and
	 * 10,000, inclusive. A part number uniquely identifies a part and also
	 * defines its position within the object being uploaded. If you upload a
	 * new part using the same part number that was specified in uploading a
	 * previous part, the previously uploaded part is overwritten.
	 * <p>
	 * To ensure data is not corrupted traversing the network, specify the
	 * Content-MD5 header in the Upload Part request. NOS checks the part data
	 * against the provided MD5 value. If they do not match, NOS returns an
	 * error.
	 * <p>
	 * When you upload a part, the returned UploadPartResult contains an ETag
	 * property. You should record this ETag property value and the part number.
	 * After uploading all parts, you must send a CompleteMultipartUpload
	 * request. At that time NOS constructs a complete object by concatenating
	 * all the parts you uploaded, in ascending order based on the part numbers.
	 * The CompleteMultipartUpload request requires you to send all the part
	 * numbers and the corresponding ETag values.
	 * 
	 * @param request
	 *            The UploadPartRequest object that specifies all the parameters
	 *            of this operation.
	 * 
	 * @return An UploadPartResult from NOS containing the part number and ETag
	 *         of the new part.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 */
	public UploadPartResult uploadPart(UploadPartRequest request) throws ClientException, ServiceException;

	/**
	 * Lists the parts that have been uploaded for a specific multipart upload.
	 * <p>
	 * This method must include the upload ID, returned by the
	 * {@link #initiateMultipartUpload(InitiateMultipartUploadRequest)}
	 * operation. This request returns a maximum of 1000 uploaded parts by
	 * default. You can restrict the number of parts returned by specifying the
	 * MaxParts property on the ListPartsRequest. If your multipart upload
	 * consists of more parts than allowed in the ListParts response, the
	 * response returns a IsTruncated field with value true, and a
	 * NextPartNumberMarker property. In subsequent ListParts request you can
	 * include the PartNumberMarker property and set its value to the
	 * NextPartNumberMarker property value from the previous response.
	 * 
	 * @param request
	 *            The ListPartsRequest object that specifies all the parameters
	 *            of this operation.
	 * 
	 * @return Returns a PartListing from NOS.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 */
	public PartListing listParts(ListPartsRequest request) throws ClientException, ServiceException;

	/**
	 * Aborts a multipart upload. After a multipart upload is aborted, no
	 * additional parts can be uploaded using that upload ID. The storage
	 * consumed by any previously uploaded parts will be freed. However, if any
	 * part uploads are currently in progress, those part uploads may or may not
	 * succeed. As a result, it may be necessary to abort a given multipart
	 * upload multiple times in order to completely free all storage consumed by
	 * all parts.
	 * 
	 * @param request
	 *            The AbortMultipartUploadRequest object that specifies all the
	 *            parameters of this operation.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 */
	public void abortMultipartUpload(AbortMultipartUploadRequest request) throws ClientException, ServiceException;

	/**
	 * Completes a multipart upload by assembling previously uploaded parts.
	 * <p>
	 * You first upload all parts using the
	 * {@link #uploadPart(UploadPartRequest)} method. After successfully
	 * uploading all individual parts of an upload, you call this operation to
	 * complete the upload. Upon receiving this request, NOS concatenates all
	 * the parts in ascending order by part number to create a new object. In
	 * the CompleteMultipartUpload request, you must provide the parts list. For
	 * each part in the list, you provide the part number and the ETag header
	 * value, returned after that part was uploaded.
	 * <p>
	 * Processing of a CompleteMultipartUpload request may take several minutes
	 * to complete.
	 * 
	 * @param request
	 *            The CompleteMultipartUploadRequest object that specifies all
	 *            the parameters of this operation.
	 * 
	 * @return A CompleteMultipartUploadResult from NOS containing the ETag for
	 *         the new object composed of the individual parts.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point
	 */
	public CompleteMultipartUploadResult completeMultipartUpload(CompleteMultipartUploadRequest request)
			throws ClientException, ServiceException;

	/**
	 * Lists in-progress multipart uploads. An in-progress multipart upload is a
	 * multipart upload that has been initiated, using the
	 * InitiateMultipartUpload request, but has not yet been completed or
	 * aborted.
	 * <p>
	 * This operation returns at most 1,000 multipart uploads in the response by
	 * default. The number of multipart uploads can be further limited using the
	 * MaxUploads property on the request parameter. If there are additional
	 * multipart uploads that satisfy the list criteria, the response will
	 * contain an IsTruncated property with the value set to true. To list the
	 * additional multipart uploads use the KeyMarker and UploadIdMarker
	 * properties on the request parameters.
	 * 
	 * @param request
	 *            The ListMultipartUploadsRequest object that specifies all the
	 *            parameters of this operation.
	 * 
	 * @return A MultipartUploadListing from NOS.
	 * 
	 * @throws ClientException
	 *             If any errors are occured in the client point.
	 * 
	 * @throws ServiceException
	 *             If any errors occurred in NOS server point.
	 */
	public MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest request) throws ClientException,
			ServiceException;

	/**
	 * Shuts down this client object, releasing any resources that might be held
	 * open. This is an optional method, and callers are not expected to call
	 * it, but can if they want to explicitly release any open resources. Once a
	 * client has been shutdown, it should not be used to make any more
	 * requests.
	 */
	
	public void shutdown();

	/**
	 * <p>
	 * Returns a list of summary information about the versions in the specified
	 * bucket.
	 * </p>
	 * <p>
	 * The returned version summaries are ordered first by key and then by
	 * version. Keys are sorted lexicographically (alphabetically) while
	 * versions are sorted from most recent to least recent. Both versions with
	 * data and delete markers are included in the results.
	 * </p>
	 * 
	 * 
	 * @param bucketName
	 *            The name of the bucket whose versions are to be listed.
	 * 
	 * 
	 * @return A listing of the versions in the specified bucket, along with any
	 *         other associated information and original request parameters.
	 * 
	 * @throws ClientException
	 *             If any errors are encountered in the client .
	 * @throws ServiceException
	 *             If any errors occurred in nos server.
	 * 
	 */
	//public VersionListing listVersions(String bucketName) throws ClientException, ServiceException;

	/**
	 * <p>
	 * Returns a list of summary information about the versions in the specified
	 * bucket.
	 * </p>
	 * <p>
	 * The returned version summaries are ordered first by key and then by
	 * version. Keys are sorted lexicographically (alphabetically) and versions
	 * are sorted from most recent to least recent. Versions with data and
	 * delete markers are included in the results.
	 * </p>
	 * 
	 * 
	 * @param bucketName
	 *            The name of the bucket whose versions are to be listed.
	 * 
	 * @param keyMarker
	 *            Optional parameter indicating where in the sorted list of all
	 *            versions in the specified bucket to begin returning results.
	 * 
	 * @param versionIdMarker
	 *            Optional parameter indicating where in the sorted list of all
	 *            versions in the specified bucket to begin returning results.
	 * 
	 * 
	 * @param maxResults
	 *            Optional parameter indicating the maximum number of results to
	 *            include in the response. nos might return fewer than this, but
	 *            will not return more. Even if maxKeys is not specified, nos
	 *            will limit the number of results in the response.
	 * 
	 * @return A listing of the versions in the specified bucket, along with any
	 *         other associated information such as common prefixes (if a
	 *         delimiter was specified), the original request parameters, etc.
	 * 
	 * @throws ClientException
	 *             If any errors are encountered in the client.
	 * @throws ServiceException
	 *             If any errors occurred in nos server.
	 * 
	 */
	//public VersionListing listVersions(String bucketName, String keyMarker, String versionIdMarker, Integer maxResults)
	//		throws ClientException, ServiceException;

	/**
	 * <p>
	 * Returns a list of summary information about the versions in the specified
	 * bucket.
	 * </p>
	 * <p>
	 * The returned version summaries are ordered first by key and then by
	 * version. Keys are sorted lexicographically (alphabetically) and versions
	 * are sorted from most recent to least recent. Versions with data and
	 * delete markers are included in the results.
	 * </p>
	 * 
	 * 
	 * @param listVersionsRequest
	 *            The request object containing all options for listing the
	 *            versions in a specified bucket.
	 * 
	 * @return A listing of the versions in the specified bucket.
	 * 
	 * @throws ClientException
	 *             If any errors are encountered in the client .
	 * @throws ServiceException
	 *             If any errors occurred in nos server.
	 * 
	 */
	//public VersionListing listVersions(ListVersionsRequest listVersionsRequest) throws ClientException,
	//		ServiceException;

	/**
     * Gets the lifecycle configuration for the specified bucket.
     *
     * @param bucketName
     *            The name of the bucket for which to retrieve lifecycle
     *            configuration.
     */
	public BucketLifecycleConfiguration getBucketLifecycleConfiguration(String bucketName) throws ClientException,
			ServiceException;
	
	/**
     * Sets the lifecycle configuration for the specified bucket.
     *
     * @param bucketName
     *            The name of the bucket for which to set the lifecycle
     *            configuration.
     * @param bucketLifecycleConfiguration
     *            The new lifecycle configuration for this bucket, which
     *            completely replaces any existing configuration.
     */
	public void setBucketLifecycleConfiguration(String bucketName, BucketLifecycleConfiguration bucketLifecycleConfiguration) throws ClientException,
			ServiceException;
	
	/**
     * Sets the lifecycle configuration for the specified bucket.
     *
     * @param setBucketLifecycleConfigurationRequest
     *            The request object containing all options for setting the
     *            bucket lifecycle configuration.
     */
	public void setBucketLifecycleConfiguration(SetBucketLifecycleConfigurationRequest setBucketLifecycleConfigurationRequest)
    		throws ClientException, ServiceException;
	
	/**
     * Removes the lifecycle configuration for the bucket specified.
     *
     * @param bucketName
     *            The name of the bucket for which to remove the lifecycle
     *            configuration.
     */
	public void deleteBucketLifecycleConfiguration(String bucketName) throws ClientException, ServiceException;
	
	/**
     * Gets the stats(objectCount, storageCapacity, deduplicationRate) for the bucket specified.
     *
     * @param bucketName
     *            The name of the bucket for which to get the stats
     */
	public GetBucketStatsResult getBucketStats(String bucketName) throws ClientException, ServiceException;
	
}
