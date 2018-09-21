package com.netease.nos.test.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;

/**   
 * @Title: NosConstant.java
 * @Package com.netease.cloud.nos
 * @Description: NOS常数类，封装错误码
 * @Company Netease
 * @author laidongmin@corp.netease.com
 * @date 2012-6-11 上午11:22:19
 */

public class NOSConstant {
	
	public static final int BASE_CONSTANT = 3000;
	public static Map<Integer, NOSErrorCode> codeMap = new HashMap<Integer, NOSErrorCode>();
	public static Map<Byte, String> versionMap = new HashMap<Byte, String>();
	public static Map<Byte, String> versionMessageMap = new HashMap<Byte, String>();
	public static Map<Byte, String> aclMap = new HashMap<Byte, String>();
	public static Map<Byte, String> aclMessageMap = new HashMap<Byte, String>();
	public static Map<Byte, String> storageClassMap = new HashMap<Byte, String>();
	public static Map<Byte, String> deduplicateMap = new HashMap<Byte, String>();
	public static Map<Byte, String> deduplicateMessageMap = new HashMap<Byte, String>();
	public static Map<Byte, String> orderFieldMap = new HashMap<Byte, String>();
	public static Map<Byte, String> orderMap = new HashMap<Byte, String>();
	public static Map<Byte, String> eventTypeMap = new HashMap<Byte, String>();
	public static Map<Byte, String> responseTypeMap = new HashMap<Byte, String>();
	
	// NOS域名
	public static final String NOS_HOSTNAME = "http://nos.netease.com";
	public static final String NOS_DOMAIN_NAME = ".nos.netease.com";
	public static final String NOS_ABSOLUTE_NAME = "nos.netease.com.";
	
	// 部署路径
	public static final String ROOT_URI = "/";
	
	// XML头部字符串
	public static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
	
	// 默认编码方式
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	// 最多允许创建100个桶
	public static int MAX_BUCKET = 100;
	
	// 默认DATE请求头超时的时间为15分钟
	public static long DEFAULT_REQUEST_TIMEOUT = 15 * 60 * 1000;
	
	// 默认版本号，代表未设置版本号的桶里的对象的版本，以及对象的最新版本
	public static final String DEFAULT_OBJECT_VERSION = "0";
	
	// getObject versionid为空时 默认ID
	public static final long DEFAULT_VERSIONID = 0;
	
	// 相同名称的header合并其值，所使用的分隔符
	public static final String HEADER_VALUE_SEPERATOR = ";";
	
	// 访问控制 ACL
	public static final byte ACL_PRIVATE = 0;
	public static final byte ACL_PUBLIC_READ = 1;
	public static final byte DEFAULT_ACL = ACL_PRIVATE;
	
	// 标准存储，用于OpenAPI存用户数据，userType = 0
	public static final byte STORAGE_CLASS_STANDARD = (1 << 6);
	public static final byte STORAGE_CLASS_CRITICAL = (byte) (2 << 6);
	public static final byte STORAGE_CLASS_TRIVIAL = 0;
	public static final byte DEFAULT_STORAGE_CLASS = STORAGE_CLASS_STANDARD;

	// 廉价桌面盘，用于存备份和镜像，userType = 1
	public static final byte SATA_STANDARD = (1 << 6 | 1);
	public static final byte SATA_CRITICAL = (byte) (2 << 6 | 1);
	public static final byte SATA_TRIVIAL = 1;
	
	// SSD，用于存少量IO密集型数据，userType = 2
	public static final byte SSD_STANDARD = (1 << 6 | 2);
	public static final byte SSD_CRITICAL = (byte) (2 << 6 | 2);
	public static final byte SSD_TRIVIAL = 2;
	
	// 桶版本号的状态
	public static final byte VERSION_DISABLED  = 0;
	public static final byte VERSION_ENABLED   = 1;
	public static final byte VERSION_SUSPENDED = 2;
	public static final byte DEFAULT_VERSION_CONFIG = VERSION_DISABLED;
	
	// 对象去重状态
	public static final byte DEDUPLICATE_DISABLED  = 0;
	public static final byte DEDUPLICATE_ENABLED   = 1;
	public static final byte DEDUPLICATE_SUSPENDED = 2;
	public static final byte DEFAULT_DEDUPLICATE = DEDUPLICATE_DISABLED;
	
	// Response Type
	public static final byte RESPONSE_XML = 0;
	public static final byte RESPONSE_JSON = 1;
	public static final byte RESPONSE_DEFAULT = RESPONSE_XML;
	
	// Event
	public static final int DEFAULT_MAX_RECORD = 100;
	public static final int DEFAULT_PAGE_NUM = 1;
	
	// Event升序降序
	public static final byte ORDER_FIELD_ASC = 0;
	public static final byte ORDER_FIELD_DESC = 1;
	public static final byte DEFAULT_EVENT_ORDER_FIELD = ORDER_FIELD_DESC;
	
	// Event排序类型
	public static final byte ORDER_FIELD_BUCKETNAME = 0;
	public static final byte ORDER_FIELD_EVENTTYPE = 1;
	public static final byte ORDER_FIELD_EVENTTIMESTAMP = 2;
	public static final byte DEFAULT_ORDER_FIELD = ORDER_FIELD_EVENTTIMESTAMP;
	
	// 其他
	public static final String VAILD_EXCEPT_VALUE = "100-continue";
	public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
	
	/**************************** BUCKET *******************************/
	// GetBucket
	public static final int GET_BUCKET_DEFAULT_MAXKEYS = 1000;
	public static final int GET_BUCKET_MIN_MAXKEYS = 0;
	public static final int GET_BUCKET_MAX_MAXKEYS = 1000;
	
	// GetBucketObjectVersions
	public static final int GET_BUCKET_HISTORY_DEFAULT_MAXKEYS = 1000;
	public static final int GET_BUCKET_HISTORY_MIN_MAXKEYS = 0;
	public static final int GET_BUCKET_HISTORY_MAX_MAXKEYS = 1000;
	
	/**************************** OBJECT *******************************/
	// PutObject
	public static final int PUT_OBJECT_MAX_OBJECT_SIZE = 100 * 1024 * 1024;
	public static final int PUT_OBJECT_MAX_KEY_LENGTH = 1000;
	public static final int DELETE_MULTI_OBJECT_MAX_COUNT = 1000;
	
	/************************** MULTIOBJECT ****************************/
	// ListPart
	public static final int LIST_PART_DEFAULT_MAXPARTS = 1000;
	public static final int LIST_PART_MIN_MAXPARTS = 0;
	public static final int LIST_PART_MAX_MAXPARTS = 1000;
	
	// UploadPart
	public static final int UPLOAD_PART_DEFAULT_NUMBER = 0;	// INVALID
	public static final int UPLOAD_PART_MIN_NUMBER = 1;
	public static final int UPLOAD_PART_MAX_NUMBER = 10000;
	
	public static final int UPLOAD_PART_MIN_PART_SIZE = 5 * 1024 * 1024;
	public static final int UPLOAD_PART_MIN_LAST_PART_SIZE = 1;
	public static final int UPLOAD_PART_MAX_PART_SIZE = 100 * 1024 * 1024;
	
	// ListMultiUpload
	public static final int LIST_MULTI_UPLOAD_DEFAULT_MAXUPLOADS = 1000; 
	public static final int LIST_MULTI_UPLOAD_MIN_MAXUPLOADS = 0; 
	public static final int LIST_MULTI_UPLOAD_MAX_MAXUPLOADS = 1000; 

	
	/**************************** OTHER *******************************/
	public static final int MYSQL_DUP_ENTRY = 1062;
	public static final int MYSQL_INVALID_STRING = 1366;
	
	public static final byte EVENT_ADD_BUCKET = 1;
	public static final byte EVENT_DELETE_BUCKET = 2;
	public static final byte EVENT_UPDATE_VERSION = 3;
	public static final byte EVENT_UPDATE_ACL = 4;
	public static final byte EVENT_UPDATE_DEDUP = 5;
	
	public static final int NOS_WRITE_BUFFER_SIZE = 1024 * 1024;
	
	public static final String KEY_FILE = "/secret.key";
	
	static {
		versionMap.put(VERSION_DISABLED, "disabled");
		versionMap.put(VERSION_ENABLED, "enabled");
		versionMap.put(VERSION_SUSPENDED, "suspended");
		
		versionMessageMap.put(VERSION_DISABLED, "关闭");
		versionMessageMap.put(VERSION_ENABLED, "开启");
		versionMessageMap.put(VERSION_SUSPENDED, "挂起");
		
		aclMap.put(ACL_PRIVATE, "private");
		aclMap.put(ACL_PUBLIC_READ, "public-read");
		
		aclMessageMap.put(ACL_PRIVATE, "私有");
		aclMessageMap.put(ACL_PUBLIC_READ, "公有读");
		
		storageClassMap.put(STORAGE_CLASS_STANDARD, "standard");
		storageClassMap.put(STORAGE_CLASS_CRITICAL, "critical");
		storageClassMap.put(STORAGE_CLASS_TRIVIAL, "trivial");
		
		storageClassMap.put(SATA_STANDARD, "sata_standard");
		storageClassMap.put(SATA_CRITICAL, "sata_critical");
		storageClassMap.put(SATA_TRIVIAL, "sata_trivial");
		
		storageClassMap.put(SSD_STANDARD, "ssd_standard");
		storageClassMap.put(SSD_CRITICAL, "ssd_critical");
		storageClassMap.put(SSD_TRIVIAL, "ssd_trivial");
		
		deduplicateMap.put(DEDUPLICATE_DISABLED, "disabled");
		deduplicateMap.put(DEDUPLICATE_ENABLED, "enabled");
		deduplicateMap.put(DEDUPLICATE_SUSPENDED, "suspended");
		
		deduplicateMessageMap.put(DEDUPLICATE_DISABLED, "关闭");
		deduplicateMessageMap.put(DEDUPLICATE_ENABLED, "开启");
		deduplicateMessageMap.put(DEDUPLICATE_SUSPENDED, "挂起");

		orderFieldMap.put(ORDER_FIELD_EVENTTIMESTAMP, "eventtimestamp");
		orderFieldMap.put(ORDER_FIELD_EVENTTYPE, "eventtype");
		orderFieldMap.put(ORDER_FIELD_BUCKETNAME, "sourceidentifier");
		
		orderMap.put(ORDER_FIELD_ASC, "asc");
		orderMap.put(ORDER_FIELD_DESC, "desc");
		
		responseTypeMap.put(RESPONSE_JSON, "json");
		responseTypeMap.put(RESPONSE_XML, "xml");
		
		eventTypeMap.put(EVENT_ADD_BUCKET, "建桶");
		eventTypeMap.put(EVENT_DELETE_BUCKET, "删桶");
		eventTypeMap.put(EVENT_UPDATE_ACL, "更新桶访问控制权限");
		eventTypeMap.put(EVENT_UPDATE_DEDUP, "更新桶去重状态");
		eventTypeMap.put(EVENT_UPDATE_VERSION, "更新桶版本状态");
	}
	
	// 权限错误
	public static final int AccessDenied = BASE_CONSTANT + 0;
	// 帐号错误，需要联系客服
	public static final int AccountProblem = BASE_CONSTANT + 1;
	// 一个邮件地址被多个帐号使用
	public static final int AmbiguousGrantByEmailAddress = BASE_CONSTANT + 2;
	// 提供的MD5值与服务器收到的二进制内容不匹配
	public static final int BadDigest = BASE_CONSTANT + 3;
	// 创建桶时，桶名已存在
	public static final int BucketAlreadyExist = BASE_CONSTANT + 4;
	// 创建桶时，该桶已经属于你，重复创建了
	public static final int BucketAlreadyOwnedByYou = BASE_CONSTANT + 5;
	// 尝试删的桶非空
	public static final int BucketNotEmpty = BASE_CONSTANT + 6;
	// 该请求不支持认证凭证
	public static final int CredentialsNotSupported = BASE_CONSTANT + 7;
	// 一个地理分区的桶无法把日志写到另外一个分区的桶里面
	public static final int CrossLocationLoggingProhibited = BASE_CONSTANT + 8;
	// 提交的请求小于允许的对象的最小值
	public static final int EntityTooSmall = BASE_CONSTANT + 9;
	// 提交的请求大于允许的对象的最大值
	public static final int EntityTooLarge = BASE_CONSTANT + 10;
	// 提供的Token已过期
	public static final int ExpiredToken = BASE_CONSTANT + 11;
	// 版本号配置无效
	public static final int IllegalVersioningConfigurationException = BASE_CONSTANT + 12;
	// 上传的数据量小于  HTTP 头中的Content-Length
	public static final int IncompleteBody = BASE_CONSTANT + 13;
	// POST请求只允许一个文件
	public static final int IncorrectNumberOfFilesInPostRequest = BASE_CONSTANT + 14;
	// 内联数据超过最大值
	public static final int InlineDataTooLarge = BASE_CONSTANT + 15;
	// 服务器内部错误，请重试
	public static final int InternalError = BASE_CONSTANT + 16;
	// AccessKey找不到匹配的记录
	public static final int InvalidAccessKeyId = BASE_CONSTANT + 17;
	// 需要指定匿名角色
	public static final int InvalidAddressingHeader = BASE_CONSTANT + 18;
	// 无效参数
	public static final int InvalidArgument = BASE_CONSTANT + 19;
	// 无效桶名称
	public static final int InvalidBucketName = BASE_CONSTANT + 20;
	// 对于当前的桶状态，这是一个无效请求
	public static final int InvalidBucketState = BASE_CONSTANT + 21;
	// 不是有效的Content-MD5
	public static final int InvalidDigest = BASE_CONSTANT + 22;
	// 无效区域限制
	public static final int InvalidLocationConstraint = BASE_CONSTANT + 23;
	// 无效的上传块
	public static final int InvalidPart = BASE_CONSTANT + 24;
	// 上传块的顺序有错误
	public static final int InvalidPartOrder = BASE_CONSTANT + 25;
	// 禁止该对象的所有访问
	public static final int InvalidPayer = BASE_CONSTANT + 26;
	// 表单内容跟策略文档里的限制不一致
	public static final int InvalidPolicyDocument = BASE_CONSTANT + 27;
	// 请求的Range不合法
	public static final int InvalidRange = BASE_CONSTANT + 28;
	// 非法请求
	public static final int InvalidRequest = BASE_CONSTANT + 29;
	// 无效的安全凭证
	public static final int InvalidSecurity = BASE_CONSTANT + 30;
	// 无效的存储级别
	public static final int InvalidStorageClass = BASE_CONSTANT + 31;
	// 需要记日志的目标桶无效
	public static final int InvalidTargetBucketForLogging = BASE_CONSTANT + 32;
	// 无法解析该URL
	public static final int InvalidURI = BASE_CONSTANT + 33;
	// Object Key长度太长
	public static final int KeyTooLong = BASE_CONSTANT + 34;
	// POST请求的Body格式错误
	public static final int MalformedPOSTRequest = BASE_CONSTANT + 35;
	// XML格式错误
	public static final int MalformedXML = BASE_CONSTANT + 36;
	// 请求过大
	public static final int MaxMessageLengthExceeded = BASE_CONSTANT + 37;
	// 上传文件之前的POST请求域过长
	public static final int MaxPostPreDataLengthExceededError = BASE_CONSTANT + 38;
	// 元数据过大
	public static final int MetadataTooLarge = BASE_CONSTANT + 39;
	// 请求的HTTP Method不允许访问
	public static final int MethodNotAllowed = BASE_CONSTANT + 40;
	// 缺少 HTTP Header Content-Length
	public static final int MissingContentLength = BASE_CONSTANT + 41;
	// 缺少请求体
	public static final int MissingRequestBodyError = BASE_CONSTANT + 42;
	// 请求缺少安全头
	public static final int MissingSecurityHeader = BASE_CONSTANT + 43;
	// 请求的HTTP Method不允许访问
	public static final int NoLoggingStatusForKey = BASE_CONSTANT + 44;
	// 请求的桶不存在
	public static final int NoSuchBucket = BASE_CONSTANT + 45;
	// 没有这个key
	public static final int NoSuchKey = BASE_CONSTANT + 46;
	// 请求的桶生命周期配置不存在
	public static final int NoSuchLifecycleConfiguration = BASE_CONSTANT + 47;
	// 请求超时，服务端已放弃该连接，请重试
	public static final int NoSuchUpload = BASE_CONSTANT + 48;
	// 创建的桶数目超过了极限
	public static final int NoSuchVersion = BASE_CONSTANT + 49;
	// 该项功能尚未实现
	public static final int NotImplemented = BASE_CONSTANT + 50;
	// 未注册的的帐号
	public static final int NotSignedUp = BASE_CONSTANT + 51;
	// 指定的桶没有指定访问策略
	public static final int NotSuchBucketPolicy = BASE_CONSTANT + 52;
	// 要访问的资源正在进行其他操作，与该请求相冲突，需要稍后重试
	public static final int OperationAborted = BASE_CONSTANT + 53;
	// 请求的endpoint重定向到新的地址
	public static final int PermanentRedirect = BASE_CONSTANT + 54;
	// 前置条件不满足
	public static final int PreconditionFailed = BASE_CONSTANT + 55;
	// 临时重定向
	public static final int Redirect = BASE_CONSTANT + 56;
	// 请求超时
	public static final int RequestTimeout = BASE_CONSTANT + 57;
	// 请求时间戳和服务器时间戳差距过大
	public static final int RequestTimeTooSkewed = BASE_CONSTANT + 58;
	// 不允许请求桶的Torrent文件
	public static final int RequestTorrentOfBucketError = BASE_CONSTANT + 59;
	// 请求的签名与服务器计算的签名不符
	public static final int SignatureDoesNotMatch = BASE_CONSTANT + 60;
	// 服务不可用
	public static final int ServiceUnavailable = BASE_CONSTANT + 61;
	// 请降低访问频率
	public static final int SlowDown = BASE_CONSTANT + 62;
	// 因为DNS更新导致重定向
	public static final int TemporaryRedirect = BASE_CONSTANT + 63;
	// 创建了过多的桶
	public static final int TooManyBuckets = BASE_CONSTANT + 64;
	// 该请求不支持内容
	public static final int UnexpectedContent = BASE_CONSTANT + 65;
	// 提供的邮件地址没有匹配到记录
	public static final int UnresolvableGrantByEmailAddress = BASE_CONSTANT + 66;
	// 桶POST请求需要包含特定的头
	public static final int UserKeyMustBeSpecified = BASE_CONSTANT + 67;
	// 无效的Zone
	public static final int NoSuchZone = BASE_CONSTANT + 68;
	// 对象太大
	public static final int ObjectTooLarge = BASE_CONSTANT + 69;
	// 非法编码
	public static final int InvalidEncoding = BASE_CONSTANT + 70;
	
	static {
		codeMap.put(AccessDenied, new NOSErrorCode("AccessDenied", HttpStatus.SC_FORBIDDEN));
		codeMap.put(AccountProblem, new NOSErrorCode("AccountProblem", HttpStatus.SC_FORBIDDEN));
		codeMap.put(AmbiguousGrantByEmailAddress, new NOSErrorCode("AmbiguousGrantByEmailAddress", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(BadDigest, new NOSErrorCode("BadDigest", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(BucketAlreadyExist, new NOSErrorCode("BucketAlreadyExist", HttpStatus.SC_CONFLICT));
		codeMap.put(BucketAlreadyOwnedByYou, new NOSErrorCode("BucketAlreadyOwnedByYou", HttpStatus.SC_CONFLICT));
		codeMap.put(BucketNotEmpty, new NOSErrorCode("BucketNotEmpty", HttpStatus.SC_CONFLICT));
		codeMap.put(CredentialsNotSupported, new NOSErrorCode("CredentialsNotSupported", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(CrossLocationLoggingProhibited, new NOSErrorCode("CrossLocationLoggingProhibited", HttpStatus.SC_FORBIDDEN));
		codeMap.put(EntityTooSmall, new NOSErrorCode("EntityTooSmall", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(EntityTooLarge, new NOSErrorCode("EntityTooLarge", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(ExpiredToken, new NOSErrorCode("ExpiredToken", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(IllegalVersioningConfigurationException, new NOSErrorCode("IllegalVersioningConfigurationException", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(IncompleteBody, new NOSErrorCode("IncompleteBody", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(IncorrectNumberOfFilesInPostRequest, new NOSErrorCode("IncorrectNumberOfFilesInPostRequest", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(InlineDataTooLarge, new NOSErrorCode("InlineDataTooLarge", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(InternalError, new NOSErrorCode("InternalError", HttpStatus.SC_INTERNAL_SERVER_ERROR));
		codeMap.put(InvalidAccessKeyId, new NOSErrorCode("InvalidAccessKeyId", HttpStatus.SC_FORBIDDEN));
		codeMap.put(InvalidAddressingHeader, new NOSErrorCode("InvalidAddressingHeader", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(InvalidArgument, new NOSErrorCode("InvalidArgument", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(InvalidBucketName, new NOSErrorCode("InvalidBucketName", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(InvalidBucketState, new NOSErrorCode("InvalidBucketState", HttpStatus.SC_CONFLICT));
		codeMap.put(InvalidDigest, new NOSErrorCode("InvalidDigest", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(InvalidLocationConstraint, new NOSErrorCode("InvalidLocationConstraint", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(InvalidPart, new NOSErrorCode("InvalidPart", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(InvalidPartOrder, new NOSErrorCode("InvalidPartOrder", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(InvalidPayer, new NOSErrorCode("InvalidPayer", HttpStatus.SC_FORBIDDEN));
		codeMap.put(InvalidPolicyDocument, new NOSErrorCode("InvalidPolicyDocument", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(InvalidRange, new NOSErrorCode("InvalidRange", HttpStatus.SC_REQUESTED_RANGE_NOT_SATISFIABLE));
		codeMap.put(InvalidRequest, new NOSErrorCode("InvalidRequest", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(InvalidSecurity, new NOSErrorCode("InvalidSecurity", HttpStatus.SC_FORBIDDEN));
		codeMap.put(InvalidStorageClass, new NOSErrorCode("InvalidStorageClass", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(InvalidTargetBucketForLogging, new NOSErrorCode("InvalidTargetBucketForLogging", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(InvalidURI, new NOSErrorCode("InvalidURI", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(KeyTooLong, new NOSErrorCode("KeyTooLong", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(MalformedPOSTRequest, new NOSErrorCode("MalformedPOSTRequest", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(MalformedXML, new NOSErrorCode("MalformedXML", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(MaxMessageLengthExceeded, new NOSErrorCode("MaxMessageLengthExceeded", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(MaxPostPreDataLengthExceededError, new NOSErrorCode("MaxPostPreDataLengthExceededError", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(MetadataTooLarge, new NOSErrorCode("MetadataTooLarge", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(MethodNotAllowed, new NOSErrorCode("MethodNotAllowed", HttpStatus.SC_METHOD_NOT_ALLOWED));
		codeMap.put(MissingContentLength, new NOSErrorCode("MissingContentLength", HttpStatus.SC_LENGTH_REQUIRED));
		codeMap.put(MissingRequestBodyError, new NOSErrorCode("MissingRequestBodyError", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(MissingSecurityHeader, new NOSErrorCode("MissingSecurityHeader", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(NoLoggingStatusForKey, new NOSErrorCode("NoLoggingStatusForKey", HttpStatus.SC_METHOD_NOT_ALLOWED));
		codeMap.put(NoSuchBucket, new NOSErrorCode("NoSuchBucket", HttpStatus.SC_NOT_FOUND));
		codeMap.put(NoSuchKey, new NOSErrorCode("NoSuchKey", HttpStatus.SC_NOT_FOUND));
		codeMap.put(NoSuchLifecycleConfiguration, new NOSErrorCode("NoSuchLifecycleConfiguration", HttpStatus.SC_NOT_FOUND));
		codeMap.put(NoSuchUpload, new NOSErrorCode("NoSuchUpload", HttpStatus.SC_NOT_FOUND));
		codeMap.put(NoSuchVersion, new NOSErrorCode("NoSuchVersion", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(NotImplemented, new NOSErrorCode("NotImplemented", HttpStatus.SC_NOT_IMPLEMENTED));
		codeMap.put(NotSignedUp, new NOSErrorCode("NotSignedUp", HttpStatus.SC_FORBIDDEN));
		codeMap.put(NotSuchBucketPolicy, new NOSErrorCode("NotSuchBucketPolicy", HttpStatus.SC_NOT_FOUND));
		codeMap.put(OperationAborted, new NOSErrorCode("OperationAborted", HttpStatus.SC_CONFLICT));
		codeMap.put(PermanentRedirect, new NOSErrorCode("PermanentRedirect", HttpStatus.SC_MOVED_PERMANENTLY));
		codeMap.put(PreconditionFailed, new NOSErrorCode("PreconditionFailed", HttpStatus.SC_PRECONDITION_FAILED));
		codeMap.put(Redirect, new NOSErrorCode("Redirect", HttpStatus.SC_TEMPORARY_REDIRECT));
		codeMap.put(RequestTimeout, new NOSErrorCode("RequestTimeout", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(RequestTimeTooSkewed, new NOSErrorCode("RequestTimeTooSkewed", HttpStatus.SC_FORBIDDEN));
		codeMap.put(RequestTorrentOfBucketError, new NOSErrorCode("RequestTorrentOfBucketError", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(SignatureDoesNotMatch, new NOSErrorCode("SignatureDoesNotMatch", HttpStatus.SC_FORBIDDEN));
		codeMap.put(ServiceUnavailable, new NOSErrorCode("ServiceUnavailable", HttpStatus.SC_SERVICE_UNAVAILABLE));
		codeMap.put(SlowDown, new NOSErrorCode("SlowDown", HttpStatus.SC_SERVICE_UNAVAILABLE));
		codeMap.put(TemporaryRedirect, new NOSErrorCode("TemporaryRedirect", HttpStatus.SC_TEMPORARY_REDIRECT));
		codeMap.put(TooManyBuckets, new NOSErrorCode("TooManyBuckets", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(UnexpectedContent, new NOSErrorCode("UnexpectedContent", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(UnresolvableGrantByEmailAddress, new NOSErrorCode("UnresolvableGrantByEmailAddress", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(UserKeyMustBeSpecified, new NOSErrorCode("UserKeyMustBeSpecified", HttpStatus.SC_BAD_REQUEST));
		codeMap.put(NoSuchZone, new NOSErrorCode("NoSuchZone", HttpStatus.SC_NOT_FOUND));
		codeMap.put(ObjectTooLarge, new NOSErrorCode("ObjectTooLarge", HttpStatus.SC_FORBIDDEN));
		codeMap.put(InvalidEncoding, new NOSErrorCode("InvalidEncoding", HttpStatus.SC_BAD_REQUEST));
	}

	public static String getNOSErrorMessage(int nosErrorCode) {
		return codeMap.get(nosErrorCode).getNosErrorCode();
	}
	
	public static final int AUTH_OK = 200;
	public static final int AUTH_INVLIAD_ACCESSKEY = 700;
	public static final int AUTH_PARAMETER_ERROR = 701;
	public static final int AUTH_UNKNOWN_ERROR = 0;
}
