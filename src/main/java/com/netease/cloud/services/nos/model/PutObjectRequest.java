package com.netease.cloud.services.nos.model;

import java.io.File;
import java.io.InputStream;

import com.netease.cloud.WebServiceRequest;

/**
 * <p>
 * Uploads a new object to the specified Nos bucket. The PutObjectRequest
 * optionally uploads object metadata and applies a canned access control policy
 * to the new object.
 * </p>
 * <p>
 * Nos never stores partial objects; if during this call an exception wasn't
 * thrown, the entire object was stored.
 * </p>
 * <p>
 * Depending on whether a file or input stream is being uploaded, this request
 * has slightly different behavior.
 * </p>
 * <p>
 * When uploading a file:
 * </p>
 * <ul>
 * <li>
 * The client automatically computes a checksum of the file. Nos uses checksums
 * to validate the data in each file.</li>
 * <li>
 * Using the file extension, Nos attempts to determine the correct content type
 * and content disposition to use for the object.</li>
 * </ul>
 * <p>
 * When uploading directly from an input stream, content length <b>must</b> be
 * specified before data can be uploaded to Nos. If not provided, the library
 * will <b>have to</b> buffer the contents of the input stream in order to
 * calculate it. Nos explicitly requires that the content length be sent in the
 * request headers before any of the data is sent.</li>
 * <p>
 * Nos is a distributed system. If Nos receives multiple write requests for the
 * same object nearly simultaneously, all of the objects might be stored.
 * However, only one object will obtain the key.
 * </p>
 * <p>
 * Note: Nos does not provide object locking; if this is needed, make sure to
 * build it into the application layer.
 * </p>
 * <p>
 * If the caller specifies a location constraint when creating a bucket, all
 * objects added to the bucket are stored in the same region as the bucket. For
 * example, if specifying a Europe (EU) region constraint for a bucket, all of
 * that bucket's objects are stored in the EU region.
 * </p>
 *
 * @see PutObjectRequest#PutObjectRequest(String, String, File)
 * @see PutObjectRequest#PutObjectRequest(String, String, InputStream,
 *      ObjectMetadata)
 */
public class PutObjectRequest extends WebServiceRequest implements NosDataSource{

	/**
	 * The name of an existing bucket, to which this request will upload a new
	 * object. You must have {@link Permission#Write} permission granted to you
	 * in order to upload new objects to a bucket.
	 */
	private String bucketName;

	/**
	 * The key under which to store the new object.
	 */
	private String key;

	/**
	 * The file containing the data to be uploaded to Nos. You must either
	 * specify a file or an InputStream containing the data to be uploaded to
	 * Nos.
	 */
	private File file;

	/**
	 * The InputStream containing the data to be uploaded to Nos. You must
	 * either specify a file or an InputStream containing the data to be
	 * uploaded to Nos.
	 */
	private InputStream inputStream;

	/**
	 * Optional metadata instructing Nos how to handle the uploaded data (e.g.
	 * custom user metadata, hooks for specifying content type, etc.). If you
	 * are uploading from an InputStream, you <bold>should always</bold> specify
	 * metadata with the content size set, otherwise the contents of the
	 * InputStream will have to be buffered in memory before they can be sent to
	 * Nos, which can have very negative performance impacts.
	 */
	private ObjectMetadata metadata;

	/**
	 * An optional pre-configured access control policy to use for the new
	 * object.
	 */
	private CannedAccessControlList cannedAcl;

	/**
	 * The optional Nos storage class to use when storing the new object. If not
	 * specified, the default, standard storage class will be used.
	 * <p>
	 * For more information on Nos storage classes and available values, see the
	 * {@link StorageClass} enumeration.
	 */
	private String storageClass;

	/**
	 * The optional progress listener for receiving updates about object upload
	 * status.
	 */
	private ProgressListener progressListener;

	/**
	 * Constructs a new {@link PutObjectRequest} object to upload a file to the
	 * specified bucket and key. After constructing the request, users may
	 * optionally specify object metadata or a canned ACL as well.
	 *
	 * @param bucketName
	 *            The name of an existing bucket to which the new object will be
	 *            uploaded.
	 * @param key
	 *            The key under which to store the new object.
	 * @param file
	 *            The path of the file to upload to Nos.
	 *
	 * @see PutObjectRequest#PutObjectRequest(String, String, InputStream,
	 *      ObjectMetadata)
	 */
	public PutObjectRequest(String bucketName, String key, File file) {
		this.bucketName = bucketName;
		this.key = key;
		this.file = file;
	}

	/**
	 * Constructs a new {@link PutObjectRequest} object to upload a stream of
	 * data to the specified bucket and key. After constructing the request,
	 * users may optionally specify object metadata or a canned ACL as well.
	 * <p>
	 * Content length for the data stream <b>must</b> be specified in the object
	 * metadata parameter; Nos requires it be passed in before the data is
	 * uploaded. Failure to specify a content length will cause the entire
	 * contents of the input stream to be buffered locally in memory so that the
	 * content length can be calculated, which can result in negative
	 * performance problems.
	 * </p>
	 *
	 * @param bucketName
	 *            The name of an existing bucket to which the new object will be
	 *            uploaded.
	 * @param key
	 *            The key under which to store the new object.
	 * @param input
	 *            The stream of data to upload to Nos.
	 * @param metadata
	 *            The object metadata. At minimum this specifies the content
	 *            length for the stream of data being uploaded.
	 *
	 * @see PutObjectRequest#PutObjectRequest(String, String, File)
	 */
	public PutObjectRequest(String bucketName, String key, InputStream input, ObjectMetadata metadata) {
		this.bucketName = bucketName;
		this.key = key;
		this.inputStream = input;
		this.metadata = metadata;
	}

	/**
	 * Gets the name of the existing bucket where this request will upload a new
	 * object to.
	 *
	 * @return The name of an existing bucket where this request will upload a
	 *         new object to.
	 *
	 * @see PutObjectRequest#setBucketName(String)
	 * @see PutObjectRequest#withBucketName(String)
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * Sets the name of an existing bucket where this request will upload a new
	 * object to. In order to upload the object, users must have write
	 * permission granted.
	 *
	 * @param bucketName
	 *            The name of an existing bucket where this request will upload
	 *            a new object to.
	 *
	 * @see PutObjectRequest#getBucketName()
	 * @see PutObjectRequest#withBucketName(String)
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Sets the name of the bucket where this request will upload a new object
	 * to. Returns this object, enabling additional method calls to be chained
	 * together.
	 * <p>
	 *
	 * @param bucketName
	 *            The name of an existing bucket where this request will upload
	 *            a new object to.
	 *
	 * @return This {@link PutObjectRequest}, enabling additional method calls
	 *         to be chained together.
	 *
	 * @see PutObjectRequest#getBucketName()
	 * @see PutObjectRequest#setBucketName(String)
	 */
	public PutObjectRequest withBucketName(String bucketName) {
		setBucketName(bucketName);
		return this;
	}

	/**
	 * Gets the key under which to store the new object.
	 *
	 * @return The key under which to store the new object.
	 *
	 * @see PutObjectRequest#setKey(String)
	 * @see PutObjectRequest#withKey(String)
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key under which to store the new object.
	 *
	 * @param key
	 *            The key under which to store the new object.
	 *
	 * @see PutObjectRequest#getKey()
	 * @see PutObjectRequest#withKey(String)
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Sets the key under which to store the new object. Returns this object,
	 * enabling additional method calls to be chained together.
	 *
	 * @param key
	 *            The key under which to store the new object.
	 *
	 * @return This {@link PutObjectRequest}, enabling additional method calls
	 *         to be chained together.
	 *
	 * @see PutObjectRequest#getKey()
	 * @see PutObjectRequest#setKey(String)
	 */
	public PutObjectRequest withKey(String key) {
		setKey(key);
		return this;
	}

	/**
	 * Gets the optional Nos storage class to use when storing the new object.
	 * If not specified, the default standard storage class is used when storing
	 * the object.
	 * <p>
	 * For more information on available Nos storage classes, see the
	 * {@link StorageClass} enumeration.
	 * </p>
	 *
	 * @return The Nos storage class to use when storing the newly copied
	 *         object.
	 *
	 * @see PutObjectRequest#setStorageClass(String)
	 * @see PutObjectRequest#setStorageClass(StorageClass)
	 * @see PutObjectRequest#withStorageClass(StorageClass)
	 * @see PutObjectRequest#withStorageClass(String)
	 */
	public String getStorageClass() {
		return storageClass;
	}

	/**
	 * Sets the optional Nos storage class to use when storing the new object.
	 * If not specified, the default standard storage class will be used when
	 * storing the new object.
	 * <p>
	 * For more information on Nos storage classes and available values, see the
	 * {@link StorageClass} enumeration.
	 * </p>
	 *
	 * @param storageClass
	 *            The storage class to use when storing the new object.
	 *
	 *
	 * @see PutObjectRequest#getStorageClass()
	 * @see PutObjectRequest#withStorageClass(StorageClass)
	 * @see PutObjectRequest#withStorageClass(String)
	 */
	public void setStorageClass(String storageClass) {
		this.storageClass = storageClass;
	}

	/**
	 * Sets the optional Nos storage class to use when storing the new object.
	 * Returns this {@link PutObjectRequest}, enabling additional method calls
	 * to be chained together. If not specified, the default standard storage
	 * class will be used when storing the object.
	 * <p>
	 * For more information on Nos storage classes and available values, see the
	 * {@link StorageClass} enumeration.
	 * </p>
	 *
	 * @param storageClass
	 *            The storage class to use when storing the new object.
	 *
	 * @return This {@link PutObjectRequest}, enabling additional method calls
	 *         to be chained together.
	 *
	 * @see PutObjectRequest#getStorageClass()
	 * @see PutObjectRequest#setStorageClass(StorageClass)
	 * @see PutObjectRequest#setStorageClass(String)
	 * @see PutObjectRequest#withStorageClass(StorageClass)
	 */
	public PutObjectRequest withStorageClass(String storageClass) {
		setStorageClass(storageClass);
		return this;
	}

	/**
	 * Sets the optional Nos storage class to use when storing the new object.
	 * If not specified, the default standard storage class will be used when
	 * storing the object.
	 * <p>
	 * For more information on Nos storage classes and available values, see the
	 * {@link StorageClass} enumeration.
	 * </p>
	 *
	 * @param storageClass
	 *            The storage class to use when storing the new object.
	 *
	 * @see PutObjectRequest#getStorageClass()
	 * @see PutObjectRequest#setStorageClass(String)
	 */
	public void setStorageClass(StorageClass storageClass) {
		this.storageClass = storageClass.toString();
	}

	/**
	 * Sets the optional Nos storage class to use when storing the new object.
	 * Returns this {@link PutObjectRequest}, enabling additional method calls
	 * to be chained together. If not specified, the default standard storage
	 * class will be used when storing the object.
	 * <p>
	 * For more information on Nos storage classes and available values, see the
	 * {@link StorageClass} enumeration.
	 * </p>
	 *
	 * @param storageClass
	 *            The storage class to use when storing the new object.
	 *
	 * @return This {@link PutObjectRequest}, enabling additional method calls
	 *         to be chained together.
	 *
	 * @see PutObjectRequest#getStorageClass()
	 * @see PutObjectRequest#setStorageClass(StorageClass)
	 * @see PutObjectRequest#setStorageClass(String)
	 * @see PutObjectRequest#withStorageClass(String)
	 */
	public PutObjectRequest withStorageClass(StorageClass storageClass) {
		setStorageClass(storageClass);
		return this;
	}

	/**
	 * Gets the path and name of the file containing the data to be uploaded to
	 * Nos. Either specify a file or an input stream containing the data to be
	 * uploaded to Nos; both cannot be specified.
	 *
	 * @return The path and name of the file containing the data to be uploaded
	 *         to Nos.
	 *
	 * @see PutObjectRequest#setFile(File)
	 * @see PutObjectRequest#withFile(File)
	 * @see PutObjectRequest#setInputStream(InputStream)
	 * @see PutObjectRequest#withInputStream(InputStream)
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Sets the path and name of the file containing the data to be uploaded to
	 * Nos. Either specify a file or an input stream containing the data to be
	 * uploaded to Nos; both cannot be specified.
	 *
	 * @param file
	 *            The path and name of the file containing the data to be
	 *            uploaded to Nos.
	 *
	 * @see PutObjectRequest#getFile()
	 * @see PutObjectRequest#withFile(File)
	 * @see PutObjectRequest#getInputStream()
	 * @see PutObjectRequest#withInputStream(InputStream)
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Sets the file containing the data to be uploaded to Nos. Returns this
	 * {@link PutObjectRequest}, enabling additional method calls to be chained
	 * together.
	 * <p>
	 * Either specify a file or an input stream containing the data to be
	 * uploaded to Nos; both cannot be specified.
	 *
	 * @param file
	 *            The file containing the data to be uploaded to Nos.
	 *
	 * @return This {@link PutObjectRequest}, enabling additional method calls
	 *         to be chained together.
	 *
	 * @see PutObjectRequest#getFile()
	 * @see PutObjectRequest#setFile(File)
	 * @see PutObjectRequest#getInputStream()
	 * @see PutObjectRequest#setInputStream(InputStream)
	 */
	public PutObjectRequest withFile(File file) {
		setFile(file);
		return this;
	}

	/**
	 * Gets the optional metadata instructing Nos how to handle the uploaded
	 * data (e.g. custom user metadata, hooks for specifying content type,
	 * etc.).
	 * <p>
	 * If uploading from an input stream, <b>always</b> specify metadata with
	 * the content size set. Otherwise the contents of the input stream have to
	 * be buffered in memory before being sent to Nos. This can cause very
	 * negative performance impacts.
	 * </p>
	 *
	 * @return The optional metadata instructing Nos how to handle the uploaded
	 *         data (e.g. custom user metadata, hooks for specifying content
	 *         type, etc.).
	 *
	 * @see PutObjectRequest#setMetadata(ObjectMetadata)
	 * @see PutObjectRequest#withMetadata(ObjectMetadata)
	 */
	public ObjectMetadata getMetadata() {
		return metadata;
	}

	/**
	 * Sets the optional metadata instructing Nos how to handle the uploaded
	 * data (e.g. custom user metadata, hooks for specifying content type,
	 * etc.).
	 * <p>
	 * If uploading from an input stream, <b>always</b> specify metadata with
	 * the content size set. Otherwise the contents of the input stream have to
	 * be buffered in memory before being sent to Nos. This can cause very
	 * negative performance impacts.
	 * </p>
	 *
	 * @param metadata
	 *            The optional metadata instructing Nos how to handle the
	 *            uploaded data (e.g. custom user metadata, hooks for specifying
	 *            content type, etc.).
	 *
	 * @see PutObjectRequest#getMetadata()
	 * @see PutObjectRequest#withMetadata(ObjectMetadata)
	 */
	public void setMetadata(ObjectMetadata metadata) {
		this.metadata = metadata;
	}

	/**
	 * Sets the optional metadata instructing Nos how to handle the uploaded
	 * data (e.g. custom user metadata, hooks for specifying content type,
	 * etc.). Returns this {@link PutObjectRequest}, enabling additional method
	 * calls to be chained together.
	 * <p>
	 * If uploading from an input stream, <b>always</b> specify metadata with
	 * the content size set. Otherwise the contents of the input stream have to
	 * be buffered in memory before being sent to Nos. This can cause very
	 * negative performance impacts.
	 * </p>
	 *
	 * @param metadata
	 *            The optional metadata instructing Nos how to handle the
	 *            uploaded data (e.g. custom user metadata, hooks for specifying
	 *            content type, etc.).
	 *
	 * @return This {@link PutObjectRequest}, enabling additional method calls
	 *         to be chained together.
	 *
	 * @see PutObjectRequest#getMetadata()
	 * @see PutObjectRequest#setMetadata(ObjectMetadata)
	 */
	public PutObjectRequest withMetadata(ObjectMetadata metadata) {
		setMetadata(metadata);
		return this;
	}

	/**
	 * Gets the optional pre-configured access control policy to use for the new
	 * object.
	 *
	 * @return The optional pre-configured access control policy to use for the
	 *         new object.
	 *
	 * @see PutObjectRequest#setCannedAcl(CannedAccessControlList)
	 * @see PutObjectRequest#withCannedAcl(CannedAccessControlList)
	 */
	public CannedAccessControlList getCannedAcl() {
		return cannedAcl;
	}

	/**
	 * Sets the optional pre-configured access control policy to use for the new
	 * object.
	 *
	 * @param cannedAcl
	 *            The optional pre-configured access control policy to use for
	 *            the new object.
	 *
	 * @see PutObjectRequest#getCannedAcl()
	 * @see PutObjectRequest#withCannedAcl(CannedAccessControlList)
	 */
	public void setCannedAcl(CannedAccessControlList cannedAcl) {
		this.cannedAcl = cannedAcl;
	}

	/**
	 * Sets the optional pre-configured access control policy to use for the new
	 * object. Returns this {@link PutObjectRequest}, enabling additional method
	 * calls to be chained together.
	 *
	 * @param cannedAcl
	 *            The optional pre-configured access control policy to use for
	 *            the new object.
	 *
	 * @return This {@link PutObjectRequest}, enabling additional method calls
	 *         to be chained together.
	 *
	 * @see PutObjectRequest#getCannedAcl()
	 * @see PutObjectRequest#setCannedAcl(CannedAccessControlList)
	 */
	public PutObjectRequest withCannedAcl(CannedAccessControlList cannedAcl) {
		setCannedAcl(cannedAcl);
		return this;
	}

	/**
	 * Gets the input stream containing the data to be uploaded to Nos. The user
	 * of this request must either specify a file or an input stream containing
	 * the data to be uploaded to Nos; both cannot be specified.
	 *
	 * @return The input stream containing the data to be uploaded to Nos.
	 *         Either specify a file or an input stream containing the data to
	 *         be uploaded to Nos, not both.
	 *
	 * @see PutObjectRequest#setInputStream(InputStream)
	 * @see PutObjectRequest#withInputStream(InputStream)
	 * @see PutObjectRequest#setFile(File)
	 * @see PutObjectRequest#withFile(File)
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * Sets the input stream containing the data to be uploaded to Nos. Either
	 * specify a file or an input stream containing the data to be uploaded to
	 * Nos; both cannot be specified.
	 *
	 * @param inputStream
	 *            The input stream containing the data to be uploaded to Nos.
	 *            Either specify a file or an input stream containing the data
	 *            to be uploaded to Nos, not both.
	 *
	 * @see PutObjectRequest#getInputStream()
	 * @see PutObjectRequest#withInputStream(InputStream)
	 * @see PutObjectRequest#getFile()
	 * @see PutObjectRequest#withFile(File)
	 */
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * Sets the input stream containing the data to be uploaded to Nos. Returns
	 * this {@link PutObjectRequest}, enabling additional method calls to be
	 * chained together.
	 * <p>
	 * Either specify a file or an input stream containing the data to be
	 * uploaded to Nos; both cannot be specified.
	 * </p>
	 *
	 * @param inputStream
	 *            The InputStream containing the data to be uploaded to Nos.
	 *
	 * @return This PutObjectRequest, so that additional method calls can be
	 *         chained together.
	 *
	 * @see PutObjectRequest#getInputStream()
	 * @see PutObjectRequest#setInputStream(InputStream)
	 * @see PutObjectRequest#getFile()
	 * @see PutObjectRequest#setFile(File)
	 */
	public PutObjectRequest withInputStream(InputStream inputStream) {
		setInputStream(inputStream);
		return this;
	}

	/**
	 * Sets the optional progress listener for receiving updates about object
	 * upload status.
	 *
	 * @param progressListener
	 *            The new progress listener.
	 */
	public void setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	/**
	 * Returns the optional progress listener for receiving updates about object
	 * upload status.
	 *
	 * @return the optional progress listener for receiving updates about object
	 *         upload status.
	 */
	public ProgressListener getProgressListener() {
		return progressListener;
	}

	/**
	 * Sets the optional progress listener for receiving updates about object
	 * upload status, and returns this updated object so that additional method
	 * calls can be chained together.
	 *
	 * @param progressListener
	 *            The new progress listener.
	 *
	 * @return This updated PutObjectRequest object.
	 */
	public PutObjectRequest withProgressListener(ProgressListener progressListener) {
		setProgressListener(progressListener);
		return this;
	}

	@Override
	public PutObjectRequest clone()  {
		PutObjectRequest cloneObjRequest=new PutObjectRequest(bucketName,key,file);
		cloneObjRequest.inputStream=inputStream;
		cloneObjRequest.metadata=metadata;
		cloneObjRequest.storageClass=storageClass;
		cloneObjRequest.cannedAcl=cannedAcl;
		cloneObjRequest.progressListener=progressListener;
        return cloneObjRequest;
	}
}
