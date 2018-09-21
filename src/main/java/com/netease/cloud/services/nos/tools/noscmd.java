package com.netease.cloud.services.nos.tools;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.netease.cloud.ClientException;
import com.netease.cloud.ServiceException;
import com.netease.cloud.WebServiceRequest;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.internal.Constants;
import com.netease.cloud.services.nos.model.AbortMultipartUploadRequest;
import com.netease.cloud.services.nos.model.Bucket;
import com.netease.cloud.services.nos.model.CannedAccessControlList;
import com.netease.cloud.services.nos.model.CompleteMultipartUploadRequest;
import com.netease.cloud.services.nos.model.CreateBucketRequest;
import com.netease.cloud.services.nos.model.GetObjectMetadataRequest;
import com.netease.cloud.services.nos.model.GetObjectRequest;
import com.netease.cloud.services.nos.model.HeadBucketRequest;
import com.netease.cloud.services.nos.model.InitiateMultipartUploadRequest;
import com.netease.cloud.services.nos.model.ListMultipartUploadsRequest;
import com.netease.cloud.services.nos.model.ListObjectsRequest;
import com.netease.cloud.services.nos.model.MultipartUpload;
import com.netease.cloud.services.nos.model.MultipartUploadListing;
import com.netease.cloud.services.nos.model.NOSObject;
import com.netease.cloud.services.nos.model.NOSObjectSummary;
import com.netease.cloud.services.nos.model.ObjectListing;
import com.netease.cloud.services.nos.model.ObjectMetadata;
import com.netease.cloud.services.nos.model.PartETag;
import com.netease.cloud.services.nos.model.PutObjectRequest;
import com.netease.cloud.services.nos.model.SetBucketAclRequest;
import com.netease.cloud.services.nos.model.StorageClass;
import com.netease.cloud.services.nos.model.UploadPartRequest;
import com.netease.cloud.services.nos.model.UploadPartResult;
import com.netease.cloud.services.nos.transfer.TransferManager;
import com.netease.cloud.services.nos.transfer.TransferManagerConfiguration;
import com.netease.cloud.services.nos.transfer.Upload;
import com.netease.cloud.services.nos.transfer.model.UploadResult;
import com.netease.cloud.util.Md5Utils;

/**
 * 
 * @Title noscmd.java
 * @Package com.netease.cloud.services.nos.tools
 * @Description command line tool for uploading, retrieving and managing data in
 *              NOS
 * @Company Netease
 * @author hzzhengbo@corp.netease.com
 * @date 2012-8-15 上午11:46:32
 */
public class noscmd {

	private TransferManager tx;
	private NosClient nosClient;
	private static Map<String, Integer> commands;
	private static Map<String, Integer> createBucketcommands;
	private static Logger log = Logger.getLogger(noscmd.class);
	private static final String credential = "/conf/credentials.properties";
	private static final String hostConfig = "/conf/host.properties";
	private static final String log4jConfig = "/conf/log4j.properties";
	private static  long TransferManagerUPloadPartSize = 5 * 1024 * 1024;
	/** files exist in nos bucket but not exist in local **/
	private Set<String> localLessSet = new HashSet<String>();
	/** files exist in local but not exist in nos bucket **/
	private Set<String> nosLessSet = new HashSet<String>();

	private String localString;

	private StorageClass storageClass;

	public noscmd() {

		/** Get the absolutely path of the file jar. **/
		String path = noscmd.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		String parent = new File(new File(path).getParent()).getParent();

		/** set log **/
		// FileAppender fielAppend;
		// try {
		// log.setLevel(Level.INFO);
		// fielAppend = new FileAppender(new
		// PatternLayout("[%p] %d - CLASS:%c - MESSAGE:<< %m%n"), parent
		// + "/logs/sdk-log", true);
		// log.addAppender(fielAppend);
		// } catch (IOException e) {
		// log.fatal("Errors occur when config log:" + e.getMessage());
		// System.out.println(-1);
		// System.exit(-1);
		// }

		PropertyConfigurator.configure(parent + log4jConfig);

		/** set host **/
		InputStream hostConfIn = null;
		try {
			hostConfIn = new FileInputStream(new File(parent + hostConfig));
		} catch (FileNotFoundException e1) {
		}
		Properties hostProperties = new Properties();
		try {
			hostProperties.load(hostConfIn);
		} catch (IOException e) {
			log.fatal("Errors occur when load the file 'conf/host.properties'.");
			System.out.println(-1);
			System.exit(-1);
		}
		Constants.NOS_HOST_NAME = hostProperties.getProperty("host");
		String storageClassString = hostProperties.getProperty("storageclass");
		String TXUploadPartSize = hostProperties.getProperty("TransferManagerUPloadPartSize");
		
		if (TXUploadPartSize != null){
			try{
			 TransferManagerUPloadPartSize = Long.valueOf(TXUploadPartSize) * 1024 * 1024;
			 if (TransferManagerUPloadPartSize > 100 * 1024 * 1024){
				 log.info("the maxmum Upload partSize is 100MB");
			 }
			 log.info("TransferManagerUPloadPartSize is :" + TransferManagerUPloadPartSize);
			}catch(NumberFormatException e){
				log.info("the TransferManagerUPloadPartSize is illegal, we we will the defalut 5MB");
			}
		}
		
		if (storageClassString != null) {
			storageClass = StorageClass.fromValue(storageClassString);
		}
		/**
		 * Read access key and secret key in file
		 * 'config/credentials.properties'.
		 **/
		InputStream keyConfIn = null;

		try {
			/** Maybe have space and chinese in path. **/
			path = java.net.URLDecoder.decode(path, "UTF-8");
		} catch (java.io.UnsupportedEncodingException e) {
			log.error("Failed to find the path of the jar.");
			System.out.println(-1);
			System.exit(-1);
		}

		try {
			keyConfIn = new FileInputStream(new File(parent + credential));
		} catch (FileNotFoundException e1) {
			log.error(e1.getMessage());
			System.out.println(-1);
			System.exit(-1);
		}
		Properties keyProperties = new Properties();
		try {
			keyProperties.load(keyConfIn);
		} catch (IOException e) {
			log.fatal("Errors occur when load the file 'conf/credentials.properties'.");
			System.out.println(-1);
			System.exit(-1);
		}
		String accessKey = keyProperties.getProperty("accessKey");
		String secretKey = keyProperties.getProperty("secretKey");
		if (accessKey == null || secretKey == null || secretKey.length() == 0 || accessKey.length() == 0) {
			log.error("Please check your accessKey and secret key in file 'conf/credentials.properties'.");
			System.out.println(-1);
			System.exit(-1);
		}

		/** Get the access client of the nos service. **/
		tx = new TransferManager(new BasicCredentials(accessKey, secretKey));
		TransferManagerConfiguration configuration = new TransferManagerConfiguration();
		
		configuration.setMinimumUploadPartSize(TransferManagerUPloadPartSize);
		
		tx.setConfiguration(configuration);
		nosClient = (NosClient) tx.getNosClient();

		// PropertyConfigurator.configure("Hello.properties");
	}

	static {

		commands = new HashMap<String, Integer>();
		commands.put("-create", 1);
		commands.put("-putfile", 2);
		commands.put("-putstream", 3);
		commands.put("-getobject", 4);
		commands.put("-deleteobject", 5);
		commands.put("-deletebucket", 6);
		commands.put("-listobjects", 7);
		commands.put("-getbucketacl", 9);
		commands.put("-listbuckets", 10);
		commands.put("-lookup", 11);
		// commands.put("-lsobjversions", 12);
		commands.put("-setbucketacl", 13);
		commands.put("-copyobject", 14);
		commands.put("-moveobject", 15);

		commands.put("-syncput", 16);
		commands.put("-syncget", 17);
		commands.put("-syncdel", 18);
		commands.put("-syncdif", 19);

		commands.put("-decodeip", 20);

		commands.put("-uploaddirectory", 21);
		commands.put("-getobjects", 22);

		createBucketcommands = new HashMap<String, Integer>();
		createBucketcommands.put("--location", 1);
		createBucketcommands.put("--dedup", 2);
		createBucketcommands.put("--acl", 3);

	}

	public static void usage() {
		System.out.println("Usage: noscmd options [args...]");
		System.out.println("where options include:");
		System.out.println("\tCommand \tArguments\t\t\tDescribe");
		System.out.println("\t-create\t\t<bucket>\t\t\tcreate a bucket.");
		System.out.println("\t\t\t--location<location>\t\tspecify the location(HZ,BJ,GZ) of this bucket.");
		System.out.println("\t\t\t--dedup<dedup>\t\t\tspecify the dedup status(true,false) of this bucket.");
		System.out.println("\t\t\t--acl<acl>\t\t\tspecify the acl(public-read,private) of this bucket.");

		System.out.println("\t-listbuckets\t\t\t\t\tlist your all buckets owned by you.");
		System.out.println("\t-putfile <file, bucketname, [-key objectkey], [-replace false(default)|true]>\t"
				+ "upload a file or files.");
		System.out.println("\t\t\t\t\t\t\tIf $file is a file, you can using -key to set"
				+ " specific object key, if no set, object key equals to f.getAbsolutePath()");
		System.out.println("\t\t\t\t\t\t\tIf $file is a directory, the tool will "
				+ "recursively put all sub folder files. Each object key equals subfolder string plus file name.");
		System.out.println("\t\t\t\t\t\t\tIf $cover is true, the tool will replace all "
				+ "remote file according to file name.");
		System.out.println("\t-putstream\t<bucket,key [,file]>\t\tupload a object from standard input or file.");

		System.out.print("\t-lookup\t\t<bucket[,key]>\t\t\t");
		System.out.print("view the object or bucket is existed or not.\n\t\t\t\t\t\t\t\t");
		System.out.print("if object exist return it's size, otherwise reurn -1;");
		System.out.print("\n\t\t\t\t\t\t\t\tif your bucket has existed return 1, ");
		System.out.println("others' bucket return -2, otherwise return -1.");

		System.out.println("\t-getobject\t<bucket,key [,file]>\t\tdownload a file to standard output or file.");
		System.out.println("\t-getobjects\t<bucket,prefix,directory [isCover]>\t\t"
				+ "download files to specified directory.");
		System.out.println("\t-deletebucket\t<bucket>\t\t\tdelete a bucket.");
		System.out.println("\t-deleteobject\t<bucket,key>\t\t\tdelete a object in specified bucket.");
		System.out.println("\t-listobjects\t<bucket>\t\t\tlist all objects in specified bucket.");
		// System.out.println("\t-lsobjversions\t<bucket,key>\t\t\tlist all versions of specified object.");
		System.out.println("\t-getbucketacl\t<bucket>\t\t\tget the access control of the specified bucket.");
		System.out.println("\t-setbucketacl\t<bucket,acl>\t\t\tset the access control of the specified bucket.");

		System.out.println("\t-copyobject\t<srcbuc,srckey,desbuc,deskey>\tcopy a object from another bucket");
		System.out.println("\t-moveobject\t<srcbuc,srckey,desbuc,deskey>\tmove a object to another bucket");

		System.out.println("\t-syncput\t<localDirectory,bucket>\t\t"
				+ "put all objects in local directory but not in NOS bucket");
		System.out.println("\t-syncget\t<localDirectory,bucket>\t\t"
				+ "download all objects in NOS bucket but not in local directory");
		// System.out
		// .println("\t-syncdel\t<localDirectory,bucket>\t\tdelete the objects in NOS bucket but not in local directory");
		System.out.println("\t-syncdif\t<localDirectory,bucket>\t\tlist the different "
				+ "between NOS bucket and local directory");

		System.out.println("\t-uploaddirectory\t<localDirectory, bucket, [-replace false(default)|true]>"
				+ "\t\tput all objects in local directory");

		System.out.println("\t-decodeip\t<requestid>\t\t\tdecode ip from the requestid");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		noscmd testnoscmd = new noscmd();

		if (args.length < 1) {
			usage();
			System.exit(1);
		}

		String command = args[0];
		noscmd noscmd = new noscmd();

		if (!commands.containsKey(command)) {
			System.out.println("The option " + command + " can not be found");
			usage();
			System.exit(-1);
		}

		args = parseCommonParams(args, noscmd);

		try {
			switch (commands.get(command).intValue()) {
			/** create bucket **/
			case 1:
				// String bucketName = null;
				String location = "HZ";
				String acl = "private";
				boolean dedup = false;

				if (args.length % 2 != 0) {
					log.info("Error: missing  argument");
					usage();
					System.exit(-1);
				}

				for (int i = 2; i < args.length; i = i + 2) {
					if (!createBucketcommands.containsKey(args[i])) {
						System.out.println("The option:" + args[i] + " can not be found");
						System.exit(-1);
					}
					switch (createBucketcommands.get(args[i])) {
					case 1:
						location = args[i + 1];
						break;
					case 2:
						if ("true".equalsIgnoreCase(args[i + 1])) {
							dedup = true;
						} else if (!"false".equalsIgnoreCase(args[i + 1])) {
							System.out.println("Invaild argument dedup:(true,false)");
							System.exit(-1);
						}
						break;
					case 3:
						acl = args[i + 1];
						break;
					}
				}

				noscmd.createBucket(args[1], location, dedup, acl);
				break;

			/** upload file **/
			case 2:
				String putUsage = "putfile usage: noscmd -putfile <file, bucketname, [-key objectkey], [-replace false(default)|true]>";
				if (args.length != 3 && args.length != 5 && args.length != 7) {
					System.out.println("Error: missing argument");
					System.out.println(putUsage);
					System.exit(-1);
				}

				String objectKey = null;
				boolean replace = false;
				if (args.length > 3) {
					for (int i = 3; i < args.length; i += 2) {
						if ("-key".equalsIgnoreCase(args[i])) {
							if (args.length < i + 2) {
								System.out.println(putUsage);
								System.exit(-1);
							} else {
								objectKey = args[i + 1];
							}
						} else if ("-replace".equalsIgnoreCase(args[i])) {
							if (args.length < i + 2) {
								System.out.println(putUsage);
								System.exit(-1);
							} else {
								replace = Boolean.parseBoolean(args[i + 1]);
							}
						} else {
							System.out.println(putUsage);
							System.exit(-1);
						}
					}
				}
				noscmd.putFile(args[2], objectKey, new File(args[1]), replace);
				break;

			/** upload stream **/
			case 3:
				if (args.length == 3) {
					noscmd.putStream(args[1], args[2], null);
				} else if (args.length == 4) {
					noscmd.putStream(args[1], args[2], args[3]);
				} else {
					System.out.println("Error: missing  argument");
					System.out.println("putstream usage: noscmd -putstream <bucketname, objectkey[,file]>");
					System.exit(-1);
				}

				break;

			/** download the file as a stream **/
			case 4:
				if (args.length == 3) {
					noscmd.getObject(args[1], args[2], null);
				} else if (args.length == 4) {
					noscmd.getObject(args[1], args[2], args[3]);
				} else {
					System.out.println("Error: missing  argument");
					System.out.println("getobject usage: noscmd -getobject <bucketname, objectkey[,file]>");
					System.exit(-1);
				}
				break;

			/** delete object **/
			case 5:
				if (args.length != 3) {
					System.out.println("Error: missing  argument");
					System.out.println("deleteobject usage: noscmd -deleteobject <bucketname, objectkey>");
					System.exit(-1);
				}
				noscmd.deleteObject(args[1], args[2]);
				break;

			/** delete bucket **/
			case 6:
				if (args.length != 2) {
					System.out.println("Error: missing  argument");
					System.out.println("deletebucket usage: noscmd -deletebucket <bucketname>");
					System.exit(-1);
				}
				noscmd.deleteBucket(args[1]);
				break;

			/** get bucket(list objects) **/
			case 7:
				if (args.length != 2) {
					System.out.println("Error: missing  argument");
					System.out.println("listobjects usage: noscmd -listobjects <bucketname>");
					System.exit(-1);
				}
				noscmd.listObjects(args[1], null, null);
				break;

			/** get the acl of the specified bucket **/
			case 9:
				if (args.length != 2) {
					System.out.println("Error: missing  argument");
					System.out.println("getbucketacl usage: noscmd -getbucketacl <bucketname>");
					System.exit(-1);
				}
				noscmd.getBucketAcl(args[1]);
				break;

			/** list your all buckets owned by you. **/
			case 10:
				if (args.length != 1) {
					System.out.println("Error:List Bucket do not need any arguments");
					System.out.println("listbuckets usage: noscmd -listbuckets");
					System.exit(-1);
				}
				noscmd.listBucket();
				break;

			/** Is bucket or object is existed ? **/
			case 11:
				if (args.length == 3) {
					noscmd.lookup(args[1], args[2]);
				} else if (args.length == 2) {
					noscmd.lookup(args[1], null);
				} else {
					System.out.println("Error: missing  argument");
					System.out.println("lookup usage: noscmd -lookup <bucketname, key>");
					System.exit(-1);
				}

				break;

			// case 12:
			// if (args.length != 3) {
			// System.out.println("Error: missing  argument");
			// System.out.println("lsobjversions usage: noscmd -lsobjversions\t<bucket,key>");
			// System.exit(-1);
			// } else {
			// noscmd.listObjectVersions(args[1], args[2]);
			// }

			/** set the acl of specified bucket **/
			case 13:
				if (args.length != 3) {
					System.out.println("Error: missing  argument");
					System.out.println("setbucketacl usage: noscmd -setbucketacl <bucketname, acl>");
					System.exit(-1);
				}
				noscmd.setBucketAcl(args[1], args[2]);
				break;

			/** copy an object between same oe different buckets **/
			case 14:
				if (args.length != 5) {
					System.out.println("Error: missing  argument");
					System.out.println("copyobject usage: noscmd -copyobject <srcbucket,srckey,descbucket,desckey>");
					System.exit(-1);
				}
				noscmd.copyObject(args[1], args[2], args[3], args[4]);
				break;

			/** move an object to another bucket or alter name in same bucket **/
			case 15:
				if (args.length != 5) {
					System.out.println("Error: missing  argument");
					System.out.println("moveobject usage: noscmd -moveobject <srcbucket,srckey,descbucket,desckey>");
					System.exit(-1);
				}
				noscmd.moveObject(args[1], args[2], args[3], args[4]);
				break;

			/** put multiple objects **/
			case 16:
				if (args.length != 3) {
					System.out.println("Error: missing  argument");
					System.out.println("syncput usage: noscmd -syncput <localDirectory,bucket>");
					System.exit(-1);
				}
				noscmd.syncput(args[1], args[2]);
				break;

			/** get multiple objects **/
			case 17:
				if (args.length != 3) {
					System.out.println("Error: missing  argument");
					System.out.println("syncget usage: noscmd -syncget <localDirectory,bucket>");
					System.exit(-1);
				}
				noscmd.syncget(args[1], args[2]);
				break;

			/** delete multiple objects **/
			case 18:
				if (args.length != 3) {
					System.out.println("Error: missing  argument");
					System.out.println("syndelete usage: noscmd -syncdel <localDirectory,bucket>");
					System.exit(-1);
				}
				noscmd.syndelete(args[1], args[2]);
				break;

			/** see the different **/
			case 19:
				if (args.length != 3) {
					System.out.println("Error: missing  argument");
					System.out.println("syndiff usage: noscmd -syncdif <localDirectory,bucket>");
					System.exit(-1);
				}
				noscmd.syndiff(args[1], args[2]);
				break;

			case 20:
				if (args.length != 2) {
					System.out.println("Error: missing  argument");
					System.out.println("decodeip usage: noscmd -decodeip <requestid>");
					System.exit(-1);
				}
				noscmd.decodeRequestId(args[1]);
				break;

			case 21:
				if (args.length != 3 && args.length != 5) {
					System.out.println("Error: missing  argument");
					System.out
							.println("uploaddirectory usage: noscmd -uploaddirectory <localDirectory, bucket, [-replace false(default)|true]>");
					System.exit(-1);
				}
				replace = false;
				if (args.length == 5 && "true".equalsIgnoreCase(args[4])) {
					replace = true;
				}
				noscmd.uploadDir(args[2], null, new File(args[1]), true, replace);
				break;

			case 22:
				if (args.length != 5 && args.length != 4) {
					System.out.println("Error: missing  argument");
					System.out.println("getobjects usage: noscmd -getobjects <bucket,prefix,directory [isCover]>");
					System.exit(-1);
				}
				if (args.length < 5 || !args[4].equalsIgnoreCase("true")) {
					noscmd.getObjects(args[1], new File(args[3]), args[2], false);
				} else {
					noscmd.getObjects(args[1], new File(args[3]), args[2], true);
				}
				break;

			}
		} catch (ServiceException e) {
			log.error("The operation:" + command + " failed. caused:" + "Request ID:" + e.getRequestId()
					+ " Error code:" + e.getErrorCode() + " Message:" + e.getMessage());
			System.out.println(-1);
			System.exit(-1);
		} catch (ClientException e) {
			log.error("The operation:" + command + " failed. caused:" + "client error message:", e);
			System.out.println(-1);
			System.exit(-1);
		} catch (Exception e) {
			log.error("The operation:" + command + " failed. caused:", e);
			System.out.println(-1);
			System.exit(-1);
		}
		System.out.println(1);
	}

	/**
	 * parse common parameters
	 * 
	 * @param args
	 *            tool arguments
	 * @param noscmd
	 *            nos command tool instance
	 */
	public static String[] parseCommonParams(String[] args, noscmd noscmd) {

		if (args == null || noscmd == null) {
			return args;
		}

		List<String> checkedArgs = new ArrayList<String>();
		checkedArgs.add(args[0]);

		for (int i = 1; i < args.length; i++) {
			if ("-id".equalsIgnoreCase(args[i]) && (i + 1) < args.length) {
				noscmd.setLogID(args[i + 1]);
				i++;
				continue;
			}
			if ("-seq".equalsIgnoreCase(args[i]) && (i + 1) < args.length) {
				noscmd.setLogSeq(args[i + 1]);
				i++;
				continue;
			}
			checkedArgs.add(args[i]);
		}

		return checkedArgs.toArray(new String[checkedArgs.size()]);
	}

	/**
	 * set debug log info
	 * 
	 * @param request
	 */
	private void setLogSeqParams(WebServiceRequest request) {
		if (logID != null) {
			request.setLogID(getLogID());
		}
		if (logSeq != null) {
			request.setLogSeq(getLogSeq());
		}
	}

	/**
	 * set debug log info, and increment log sequence level
	 * 
	 * @param request
	 */
	private void setAndIncrementLogSeqParams(WebServiceRequest request) {
		if (logID != null) {
			request.setLogID(getLogID());
		}
		if (logSeq != null) {
			request.setLogSeq(getAndIncrementLogSeq());
		}
	}

	/**
	 * create NOS bucket
	 * 
	 * @param bucketName
	 *            the name of the bucket
	 * @param location
	 *            the region of the bucket. 'HZ','BJ','GZ'
	 * @param dedup
	 *            is deduplicate or not .'true' or 'false'
	 * @param acl
	 *            access control. 'private' or 'public-read'
	 */
	public void createBucket(String bucketName, String location, boolean dedup, String acl) {
		CreateBucketRequest request = new CreateBucketRequest(bucketName);
		request.setCannedAcl(acl);
		request.setDeduplicate(dedup);
		request.setRegion(location);
		setLogSeqParams(request);
		nosClient.createBucket(request);
		log.info("BucketName:" + bucketName + " has been created finished.");
	}

	/**
	 * upload file
	 * 
	 * @param bucketName
	 *            bucket of the uploaded file in
	 * @param key
	 *            the file name in bucket
	 * @param file
	 *            actual file need to be uploaded
	 * @throws Exception
	 */
	public void putFile(String bucketName, String key, File file, boolean isCover) throws Exception {

		if (file == null || !file.exists()) {
			throw new IllegalArgumentException("Must provide a directory or file to upload");
		}

		if (file.isFile()) {

			if (key == null) {

				if ((key = getObjectKey(file, null)) == null) {
					String dir = file.getAbsolutePath();
					log.error("getObjectKey for " + dir +" error.");
					throw new ServiceException("put " + dir + " error.");
				}
			}

			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
			if (storageClass != null) {
				putObjectRequest.setStorageClass(storageClass);
			}

			if (isCover || !doesObjectExist(bucketName, key)) {
				log.info("Begin upload file:" + file.getName() + " to bucket:" + bucketName + " as key: " + key
						+ ", please waiting...");
				setAndIncrementLogSeqParams(putObjectRequest);
				Upload upload = tx.upload(putObjectRequest);
				upload.waitForUploadResult();
				tx.shutdownNow();
				log.info("File:" + file.getName() + " has been upload finished.");
			} else {
				log.warn("File:" + file.getName() + " is already exist");
			}

		} else {
			uploadDir(bucketName, null, file, true, isCover);
		}
	}

	/**
	 * upload data to specified bucket as an object,and support the stream way
	 * 
	 * @param bucketName
	 *            bucketName bucket of the uploaded file in
	 * @param key
	 *            the file name in bucket
	 * @param file
	 *            conditianla,file actual file need to be uploaded,the parameter
	 *            is null ,then read data from stdin
	 */
	public void putStream(String bucketName, String key, String file) {
		try {
			PutObjectRequest putObjectRequest = null;
			if (file == null) {
				InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, key);
				if (storageClass != null) {
					request.setStorageClass(storageClass);
				}
				setAndIncrementLogSeqParams(request);
				String uploadId = nosClient.initiateMultipartUpload(request).getUploadId();
				List<PartETag> partETags = new ArrayList<PartETag>();
				int partNumber = 1;
				
				if (TransferManagerUPloadPartSize > Integer.MAX_VALUE){
					log.info("the TransferManagerUPloadPartSize size if too big ");
				}
				ByteBuffer nosBuffer = ByteBuffer.allocate((int)TransferManagerUPloadPartSize);
				DataInputStream input = new DataInputStream(System.in);
				byte[] tmpbuffer = new byte[1024 * 1024];
				int length = -1;
				while ((length = input.read(tmpbuffer)) != -1) {
					if (nosBuffer.remaining() >= length) {
						nosBuffer.put(tmpbuffer, 0, length);
					} else {
						int nosBufferRemain = nosBuffer.remaining();
						int tmpBufferUnread = length - nosBufferRemain;
						nosBuffer.put(tmpbuffer, 0, nosBufferRemain);
						uploadPart(nosBuffer, nosBuffer.capacity(), bucketName, key, uploadId, partNumber++, partETags);
						nosBuffer.clear();
						nosBuffer.put(tmpbuffer, nosBufferRemain, tmpBufferUnread);
					}
				}
				uploadPart(nosBuffer, nosBuffer.position(), bucketName, key, uploadId, partNumber++, partETags);
				System.in.close();
				input.close();
				CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest(bucketName, key,
						uploadId, partETags);
				setAndIncrementLogSeqParams(completeRequest);
				nosClient.completeMultipartUpload(completeRequest);
				log.info("Upload to bucket:" + bucketName + " as key:" + key + " finished.");
				System.out.println(1);
				System.exit(1);

			} else {
				FileInputStream input = null;
				input = new FileInputStream(new File(file));
				putObjectRequest = new PutObjectRequest(bucketName, key, input, null);
				setLogSeqParams(putObjectRequest);
				log.info("Begin upload file:" + file + " to bucket:" + bucketName + ", please waiting...");
				Upload upload = tx.upload(putObjectRequest);
				UploadResult result;
				result = upload.waitForUploadResult();
				log.info(result.getBucketName() + "/" + result.getKey());
				tx.shutdownNow();

				log.info("File has been upload finished.");
				System.out.println(1);
				System.exit(1);
			}
		} catch (ServiceException e) {
			log.error("Failed.Error code:" + e.getErrorCode() + "Error type:" + e.getErrorType() + "message:"
					+ e.getMessage());
			System.err.println(-1);
			System.exit(-1);
		} catch (ClientException e) {
			log.error("Failed." + "message:" + e.getMessage());
			System.err.println(-1);
			System.exit(-1);
		} catch (Exception e) {
			log.error("Failed." + "message:" + e.getMessage());
			System.err.println(-1);
			System.exit(-1);
		}

	}

	/**
	 * upload a part to specified bucket
	 * 
	 * @param bb
	 * @param length
	 * @param bucketName
	 * @param key
	 * @param uploadId
	 * @param partNumber
	 * @param partETags
	 */
	private void uploadPart(ByteBuffer bb, int length, String bucketName, String key, String uploadId, int partNumber,
			List<PartETag> partETags) {
		InputStream ret = new ByteArrayInputStream(bb.array());
		UploadPartRequest request = new UploadPartRequest().withBucketName(bucketName).withKey(key)
				.withInputStream(ret).withPartSize(length).withUploadId(uploadId).withPartNumber(partNumber);
		setAndIncrementLogSeqParams(request);
		UploadPartResult result = nosClient.uploadPart(request);
		log.info(result.getPartNumber());
		log.info(result.getETag());
		partETags.add(new PartETag(result.getPartNumber(), result.getETag()));
	}

	/**
	 * download the file in specified bucket
	 * 
	 * @param bucketName
	 * @param key
	 * @param fileStr
	 */
	public void getObject(String bucketName, String key, String fileStr) {

		try {
			GetObjectRequest request = new GetObjectRequest(bucketName, key);
			setLogSeqParams(request);
			NOSObject nosobj = nosClient.getObject(request);
			InputStream input = nosobj.getObjectContent();
			OutputStream outputStream = null;

			/** The real length of the file **/
			long expectLength = nosobj.getObjectMetadata().getContentLength();
			long actualLength = 0;

			int bytesRead;
			if (fileStr == null) {
				byte[] buffer = new byte[1024 * 1024];
				while ((bytesRead = input.read(buffer)) != -1) {
					System.out.write(buffer, 0, bytesRead);
					actualLength += bytesRead;
				}
				System.out.flush();
			} else {
				log.info("Begin download file:" + key + " in bucket:" + bucketName + " , please wait...");

				// create the file and the directory if have.
				File file = new File(fileStr);
				// file.getParentFile().mkdirs();
				if (!file.exists()) {
					file.createNewFile();
				}

				outputStream = new BufferedOutputStream(new FileOutputStream(file));
				byte[] buffer = new byte[1024 * 1024];
				while ((bytesRead = input.read(buffer)) > -1) {
					outputStream.write(buffer, 0, bytesRead);
					actualLength = actualLength + bytesRead;
				}
				outputStream.close();
				log.info("Download file finished");
			}

			/**
			 * Because of some cause,the operate of download file is
			 * interrupted.
			 **/
			if (actualLength != expectLength) {
				log.fatal("Download failed . File:" + key + " in bucket:" + bucketName + " may be download partial.");
				System.err.println(-1);
				System.exit(-1);
			}

			if (input != null) {
				input.close();
			}

			log.info("Finashed download file:" + key + " in bucket:" + bucketName);
		} catch (ServiceException e) {
			log.error("Failed.Error code:" + e.getErrorCode() + "Error type:" + e.getErrorType() + "message:"
					+ e.getMessage());
			System.exit(-1);
		} catch (ClientException e) {
			log.error("Failed." + "message:", e);
			System.exit(-1);
		} catch (Exception e) {
			log.error("Failed." + "message:", e);
			System.exit(-1);
		}
	}

	/**
	 * delete the bucket
	 * 
	 * @param bucketName
	 */
	public void deleteBucket(String bucketName) {
		nosClient.deleteBucket(bucketName);
		log.info("Bucket " + bucketName + " has been deleted.");
	}

	/**
	 * delete an object in specified bucket
	 * 
	 * @param bucketName
	 * @param key
	 */
	public void deleteObject(String bucketName, String key) {
		// System.out.println("Begin delete object ing...");
		nosClient.deleteObject(bucketName, key);
		log.info("Object " + key + " in bucket " + bucketName + " has been deleted.");
		System.out.println(1);
	}

	/**
	 * list all object in specified bucket
	 * 
	 * @param bucketName
	 */
	public void listObjects(String bucketName, String prefix, List<String> keys) {
		ObjectListing objectListing;
		String marker = null;
		int size = 0;
		do {
			ListObjectsRequest request = new ListObjectsRequest();
			request.setPrefix(prefix);
			request.setBucketName(bucketName);
			request.setMarker(marker);
			request.setMaxKeys(100);
			objectListing = nosClient.listObjects(request);
			List<NOSObjectSummary> sum = objectListing.getObjectSummaries();
			if (sum.size() == 0) {
				log.info(bucketName + " is Empty.");
				System.out.println(-1);
			} else {
				// object name length verify,how to make Align.
				size += sum.size();
				for (NOSObjectSummary obj : sum) {
					if (keys != null) {
						keys.add(obj.getKey());
					} else {
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

	public void getObjects(String bucket, File directory, String prefix, boolean isCover) throws IOException {
		List<String> keys = new ArrayList<String>();
		listObjects(bucket, prefix, keys);

		int finishedDownload = 0;
		int failedDownload = 0;
		Set<String> failedFiles = new HashSet<String>();

		System.out.println("Begin to download,please wait...");
		for (String key : keys) {
			File file = new File(directory + File.separator + key);
			file.getParentFile().mkdirs();

			if (isCover || !isCover && !file.exists()) {
				file.createNewFile();

				try {
					GetObjectRequest request = new GetObjectRequest(bucket, key);
					setLogSeqParams(request);
					nosClient.getObject(request, file);
				} catch (Exception e) {
					failedDownload++;
					failedFiles.add(key);
					continue;
				}
				finishedDownload++;
			}
		}
		System.out.println("-----------------------------");
		System.out.println("Finished download files number:" + finishedDownload);
		System.out.println("Failed download files number:" + failedDownload);
		if (failedFiles.size() != 0) {
			System.out.println("Failed download files:");
			for (String fail : failedFiles)
				System.out.println(fail);
		}
	}

	/**
	 * get the acl of specified bucket,return 'public-read' or 'private'
	 * 
	 * @param bucketName
	 */
	public void getBucketAcl(String bucketName) {
		CannedAccessControlList acl = nosClient.getBucketAcl(bucketName);
		System.out.println("bucket:" + bucketName + "\t" + "acl:" + acl.toString());
	}

	/**
	 * list all your bucket
	 */
	public void listBucket() {
		List<Bucket> buckets = nosClient.listBuckets();
		if (buckets.size() == 0) {
			System.out.println(-1);
			log.info("You have not bucket.");

		} else {
			for (Bucket buc : buckets) {
				System.out.println("Bucket Name:" + buc.getName().trim());
			}
		}
	}

	/**
	 * view a bucket or object ia exist or not. If the object is not exists, try
	 * list partial part and abort the upload
	 * 
	 * @param bucketName
	 * @param key
	 *            objectKey, if null, check bucket existence.
	 */
	public void lookup(String bucketName, String key) {

		if (key == null) {
			HeadBucketRequest request = new HeadBucketRequest(bucketName);
			setLogSeqParams(request);
			if (nosClient.doesBucketExist(request)) {
				System.out.println(1);
				System.exit(1);
			} else {
				System.out.println(-1);
				System.exit(-1);
			}
			return;
		}
		try {
			GetObjectMetadataRequest request = new GetObjectMetadataRequest(bucketName, key);
			setAndIncrementLogSeqParams(request);
			ObjectMetadata objectMetadata = nosClient.getObjectMetadata(request);
			System.out.println(objectMetadata.getContentLength());
			System.exit(1);
		} catch (ServiceException ase) {

			switch (ase.getStatusCode()) {
			case 403:
				throw ase;
			case 404:
				ListMultipartUploadsRequest listRequest = new ListMultipartUploadsRequest(bucketName);
				setAndIncrementLogSeqParams(listRequest);
				MultipartUploadListing result = nosClient.listMultipartUploads(listRequest);
				List<MultipartUpload> mulitUploads = result.getMultipartUploads();
				for (MultipartUpload upload : mulitUploads) {
					if (key.equals(upload.getKey())) {
						AbortMultipartUploadRequest abortRequest = new AbortMultipartUploadRequest(bucketName, key,
								upload.getUploadId());
						setAndIncrementLogSeqParams(abortRequest);
						nosClient.abortMultipartUpload(abortRequest);
					}
				}
				System.out.println(-1);
				System.exit(-1);
				break;
			default:
				throw ase;
			}
		}

	}

	/**
	 * set the acl of the specified bucket
	 * 
	 * @param bucketName
	 * @param acl
	 */
	public void setBucketAcl(String bucketName, String acl) {
		SetBucketAclRequest setBucketAclRequest = new SetBucketAclRequest(bucketName, acl);
		nosClient.setBucketAcl(setBucketAclRequest);
		System.out.println("The acl of bucket:" + bucketName + " has changed to " + acl);
	}

	/**
	 * copy an object to another bucket
	 * 
	 * @param sourceBucketName
	 * @param sourceKey
	 * @param destinationBucketName
	 * @param destinationKey
	 */
	public void copyObject(String sourceBucketName, String sourceKey, String destinationBucketName,
			String destinationKey) {
		log.info("The copy option is begining,please waiting...");
		nosClient.copyObject(sourceBucketName, sourceKey, destinationBucketName, destinationKey);
		System.out.println(1);
		log.info("Copy finished.");
	}

	/**
	 * move an object to another bucket
	 * 
	 * @param sourceBucketName
	 * @param sourceKey
	 * @param destinationBucketName
	 * @param destinationKey
	 */
	public void moveObject(String sourceBucketName, String sourceKey, String destinationBucketName,
			String destinationKey) {
		log.info("The move option is begining,please waiting...");
		nosClient.moveObject(sourceBucketName, sourceKey, destinationBucketName, destinationKey);
		System.out.println(1);
		log.info("Move finished.");
	}

	/**
	 * view the differences between local directory and NOS bucket
	 * 
	 * @param localDir
	 * @param bucket
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void syndiff(String localDir, String bucket) throws NoSuchAlgorithmException, FileNotFoundException,
			IOException {
		localNotHave(localDir, bucket);
		nosNotHave(localDir, bucket);
		System.out.println("local exist But nos bucket not:");
		if (nosLessSet.size() == 0) {
			System.out.println("---");
		}
		for (String filenameString : nosLessSet) {
			System.out.println(filenameString);
		}
		System.out.println("local not exist But nos bucket exist:");
		if (localLessSet.size() == 0) {
			System.out.println("---");
		}
		for (String filenameString : localLessSet) {
			System.out.println(filenameString);
		}
	}

	/**
	 * delete the objects which loacl directory not have bu exist in bucket
	 * 
	 * @param localDir
	 * @param bucket
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void syndelete(String localDir, String bucket) throws NoSuchAlgorithmException, FileNotFoundException,
			IOException {
		localNotHave(localDir, bucket);
		/** only upload the objects not exist in NOS bucket **/
		for (String fileNameString : localLessSet) {
			nosClient.deleteObject(bucket, fileNameString);
			System.out.println("file:" + fileNameString + " have delete finished.");
		}
	}

	/**
	 * down the objects which loacl directory not have bu exist in bucket
	 * 
	 * @param localDir
	 * @param bucket
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void syncget(String localDir, String bucket) throws NoSuchAlgorithmException, FileNotFoundException,
			IOException {
		localNotHave(localDir, bucket);
		/** only upload the objects not exist in NOS bucket **/
		for (String fileNameString : localLessSet) {
			File file = new File(localDir + File.separator + fileNameString);
			file.getParentFile().mkdirs();
			if (!file.exists()) {
				file.createNewFile();
			}
			System.out.println("file:" + fileNameString + " begin to download,please wait...");
			nosClient.getObject(new GetObjectRequest(bucket, fileNameString), file);
			System.out.println("file:" + fileNameString + " have download finished.");
		}
	}

	/**
	 * upload the objects which local directory have but not eaist in bucket
	 * 
	 * @param localDir
	 * @param bucket
	 * @throws ServiceException
	 * @throws ClientException
	 * @throws InterruptedException
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void syncput(String localDir, String bucket) throws ServiceException, ClientException,
			InterruptedException, NoSuchAlgorithmException, FileNotFoundException, IOException {

		Set<String> failedFiles = new HashSet<String>();
		int finishedFileNum = 0;
		int failedFileNum = 0;

		nosNotHave(localDir, bucket);
		/** only upload the objects not exist in NOS bucket **/
		for (String fileNameString : nosLessSet) {
			File file = new File(localDir + File.separator + fileNameString);
			try {
				if (file.length() < Constants.DEFAULT_BUFFER_SIZE) {

					nosClient.putObject(bucket, fileNameString, file);

				} else {
					Upload upload = tx.upload(bucket, fileNameString, file);
					upload.waitForUploadResult();
				}
			} catch (ServiceException e) {
				log.error("File:" + fileNameString + " uploaded failed. caused:" + "Request ID:" + e.getRequestId()
						+ " Error code:" + e.getErrorCode() + " Message:" + e.getMessage());
				failedFiles.add(fileNameString);
				failedFileNum++;
				continue;
			} catch (ClientException e) {
				log.error("File:" + fileNameString + " uploaded failed. caused:" + "client error message:"
						+ e.getMessage());
				failedFiles.add(fileNameString);
				failedFileNum++;
				continue;
			} catch (Exception e) {
				log.error("File:" + fileNameString + " uploaded failed. caused:" + e.getMessage());
				failedFiles.add(fileNameString);
				failedFileNum++;
				continue;
			}
			finishedFileNum++;
			System.out.println("file:" + fileNameString + " have upload finished.");
		}

		System.out.println("-----------------------------");
		System.out.println("Finished files number:" + finishedFileNum);
		System.out.println("Failed files number:" + failedFileNum);
		if (failedFiles.size() == 0) {
			System.out.println("All files in directory" + localDir + " have upload finished.");
		} else {
			System.out.println("Files upload failed:");
			for (String failedFile : failedFiles) {
				System.out.println(failedFile);
			}
		}

		tx.shutdownNow();
	}

	public void localNotHave(String localDir, String bucket) throws NoSuchAlgorithmException, FileNotFoundException,
			IOException {

		/** get md5 of all objects in bucket **/
		/**
		 * .........the big object which upload through mulit upload should be
		 * special handle but not......................
		 **/
		Map<String, String> nosEtags = new HashMap<String, String>();
		Map<String, String> nosFileSizes = new HashMap<String, String>();
		List<NOSObjectSummary> objectSummary = nosClient.listObjects(bucket).getObjectSummaries();
		for (NOSObjectSummary objsum : objectSummary) {
			nosEtags.put(objsum.getKey(), objsum.getETag());
			nosFileSizes.put(objsum.getKey(), objsum.getSize() + "");
		}

		Map<String, String> localFileSizes = new HashMap<String, String>();
		localFileSizes(localFileSizes, localDir, "");

		for (String nosKey : nosFileSizes.keySet()) {
			if (!localFileSizes.containsValue(nosFileSizes.get(nosKey))) {
				localLessSet.add(nosKey);
			} else {
				boolean isSame = false;
				for (String localKeyString : localFileSizes.keySet()) {
					if (localFileSizes.get(localKeyString).equals(nosFileSizes.get(nosKey))) {
						if (Long.parseLong(localFileSizes.get(localKeyString)) > Constants.DEFAULT_BUFFER_SIZE) {
							isSame = true;
							break;
						} else if (nosEtags.get(nosKey).equalsIgnoreCase(
								Md5Utils.getHex(Md5Utils.computeMD5Hash(new FileInputStream(localDir + File.separator
										+ localKeyString))))) {
							isSame = true;
							break;
						}
					}
				}
				if (!isSame) {
					localLessSet.add(nosKey);
				}
			}
		}
	}

	/**
	 * Get the size of all files in local directory.
	 * 
	 * @param localFileSizes
	 * @param localDir
	 */
	private void localFileSizes(Map<String, String> localFileSizes, String localDir, String parent) {
		if (parent.equals("")) {
			localString = localDir;
		}
		/** get all files in specified director expected subdir. **/
		File dir = new File(localDir);
		String[] fileNames;

		if (dir.isDirectory()) {
			fileNames = dir.list();
			for (int i = 0; i < fileNames.length; i++) {
				File file = new File(localDir + File.separator + parent + fileNames[i]);
				if (!file.isDirectory()) {
					localFileSizes.put(parent + fileNames[i], file.length() + "");
				} else {
					localFileSizes(localFileSizes, file.getAbsolutePath(),
							file.getAbsolutePath().substring(localString.length() + 1) + File.separator);
				}
			}
		} else {
			System.out.println("first parameter must be directory.");
			System.exit(-1);
		}
	}

	public void nosNotHave(String localDir, String bucket) throws NoSuchAlgorithmException, FileNotFoundException,
			IOException {

		/** get md5 of all objects in bucket **/
		Set<String> nosEtags = new HashSet<String>();
		Set<String> nosFileSizes = new HashSet<String>();
		List<NOSObjectSummary> objectSummary = nosClient.listObjects(bucket).getObjectSummaries();
		for (NOSObjectSummary objsum : objectSummary) {
			nosFileSizes.add(objsum.getSize() + "");
			if (objsum.getSize() <= Constants.DEFAULT_BUFFER_SIZE) {
				nosEtags.add(objsum.getETag().toLowerCase());
			}
		}

		nosNotHave(localDir, nosFileSizes, nosEtags, "");
	}

	public void nosNotHave(String localDir, Set<String> nosFileSizes, Set<String> nosEtags, String parent)
			throws NoSuchAlgorithmException, FileNotFoundException, IOException {
		if (parent.equals("")) {
			localString = localDir;
		}
		/** get all files in specified director expected subdir. **/
		File dir = new File(localDir);
		String[] fileNames;
		if (dir.isDirectory()) {
			fileNames = dir.list();
			for (int i = 0; i < fileNames.length; i++) {
				File file = new File(localDir + File.separator + fileNames[i]);

				/** only upload the objects not exist in NOS bucket **/
				if (!file.isDirectory()) {
					if (file.length() > Constants.DEFAULT_BUFFER_SIZE) {
						if (!nosFileSizes.contains(file.length() + "")) {
							nosLessSet.add(parent + fileNames[i]);
						}
					} else if (!nosFileSizes.contains(file.length() + "")
							|| !nosEtags.contains(Md5Utils.getHex(Md5Utils.computeMD5Hash(new FileInputStream(file))))) {
						nosLessSet.add(parent + fileNames[i]);
					}
				} else {
					nosNotHave(file.getAbsolutePath(), nosFileSizes, nosEtags,
							file.getAbsolutePath().substring(localString.length()) + File.separator);
				}
			}
		} else {
			System.out.println("first parameter must be directory.");
			System.exit(-1);
		}
	}

	/**
	 * guess objectkey for local files
	 * 
	 * @param f
	 *            sub directory and file
	 * @param directory
	 *            root directory
	 * @return guessed object key
	 */
	public String getObjectKey(File f, File directory) {

		if (f == null || f.isDirectory()) {
			log.warn("invalid parameter.");
			return null;
		}

		if (directory == null) {
			return f.getAbsolutePath();
		}

		if (directory.isFile()) {
			log.warn(directory.getAbsolutePath() + " should be directory.");
			return null;
		}

		return f.getAbsolutePath().substring(directory.getAbsolutePath().length() + 1).replaceAll("\\\\", "/");
	}

	public void uploadDir(String bucketName, String virtualDirectoryKeyPrefix, File directory,
			boolean includeSubdirectories, boolean iscover) throws ServiceException, ClientException,
			InterruptedException {
		Set<String> failedFiles = new HashSet<String>();
		int finishedFileNum = 0;
		int failedFileNum = 0;

		if (directory == null || !directory.exists() || !directory.isDirectory()) {
			throw new IllegalArgumentException("Must provide a directory to upload");
		}

		if (virtualDirectoryKeyPrefix == null || virtualDirectoryKeyPrefix.length() == 0) {
			virtualDirectoryKeyPrefix = null;
		} else if (!virtualDirectoryKeyPrefix.endsWith("/")) {
			virtualDirectoryKeyPrefix = virtualDirectoryKeyPrefix + "/";
		}

		List<File> files = new LinkedList<File>();
		listFiles(directory, files, includeSubdirectories);
		for (File f : files) {
			String key = getObjectKey(f, directory);
			try {
				String nosKey = key;
				if (virtualDirectoryKeyPrefix != null) {
					if (key.contains(virtualDirectoryKeyPrefix)) {
						nosKey = key.replace(virtualDirectoryKeyPrefix, "");
					} else {
						nosKey = virtualDirectoryKeyPrefix + key;
					}
				}

				if (iscover || !doesObjectExist(bucketName, nosKey)) {
					PutObjectRequest request = new PutObjectRequest(bucketName, nosKey, f);
					if (storageClass != null) {
						request.setStorageClass(storageClass);
					}
					Upload upload = tx.upload(request);
					setLogSeqParams(request);
					upload.waitForUploadResult();
					finishedFileNum++;
					System.out.println("File:" + key + " upload finished");
				} else {
					finishedFileNum++;
					System.out.println("File:" + key + " already exist");
				}
			} catch (ServiceException e) {
				log.error("File:" + key + " uploaded failed. caused:" + "Request ID:" + e.getRequestId()
						+ " Error code:" + e.getErrorCode() + " Message:" + e.getMessage());
				failedFiles.add(key);
				failedFileNum++;
				continue;
			} catch (ClientException e) {
				log.error("File:" + key + " uploaded failed. caused:" + "client error message:" + e.getMessage());
				failedFiles.add(key);
				failedFileNum++;
				continue;
			} catch (Exception e) {
				log.error("File:" + key + " uploaded failed. caused:" + e.getMessage());
				failedFiles.add(key);
				failedFileNum++;
				continue;
			}

		}

		System.out.println("-----------------------------");
		System.out.println("Finished files number:" + finishedFileNum);
		System.out.println("Failed files number:" + failedFileNum);
		if (failedFiles.size() == 0) {
			System.out.println("All files in directory" + directory + " have upload finished.");
		} else {
			System.out.println("Files upload failed:");
			for (String failedFile : failedFiles) {
				System.out.println(failedFile);
			}
		}
		tx.shutdownNow();
	}

	public boolean doesObjectExist(String bucket, String object) {
		GetObjectMetadataRequest request = new GetObjectMetadataRequest(bucket, object);
		setAndIncrementLogSeqParams(request);
		try {
			nosClient.getObjectMetadata(request);
			return true;
		} catch (ServiceException ase) {

			switch (ase.getStatusCode()) {
			case 403:
				/*
				 * A permissions error don't know if the object is existed or
				 * not
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
	 * Lists files in the directory given and adds them to the result list
	 * passed in, optionally adding subdirectories recursively.
	 */
	private void listFiles(File dir, List<File> results, boolean includeSubDirectories) {
		File[] found = dir.listFiles();
		if (found != null) {
			for (File f : found) {
				if (f.isDirectory()) {
					if (includeSubDirectories) {
						listFiles(f, results, includeSubDirectories);
					}
				} else {
					results.add(f);
				}
			}
		}
	}

	/**
	 * decode a request id, output timestamp, ip address, etc
	 * 
	 * @param rid
	 *            request id
	 * @throws DecoderException
	 */
	public void decodeRequestId(String rid) throws DecoderException {
		byte[] data = Hex.decodeHex(rid.toCharArray());
		ByteBuffer tbb = ByteBuffer.allocate(8);
		tbb.put(data, 6, 8);
		tbb.flip();

		ByteBuffer ibb = ByteBuffer.allocate(4);
		ibb.put(data, 4, 2);
		ibb.put(data, 14, 2);
		ibb.flip();

		System.out.println(ip2String(ibb.getInt()));
	}

	public String ip2String(int ip) {
		byte[] buf = i2b(ip);
		try {
			return InetAddress.getByAddress(buf).getHostAddress();
		} catch (UnknownHostException e) {
		}
		return "127.0.0.1";
	}

	/**
	 * int转换成4字节byte数组
	 * 
	 * @param value
	 * @return
	 */
	public byte[] i2b(int value) {
		byte[] bytes = new byte[4];
		int temp = value;

		bytes[3] = (byte) temp;
		temp = temp >> 8;
		bytes[2] = (byte) temp;
		temp = temp >> 8;
		bytes[1] = (byte) temp;
		temp = temp >> 8;
		bytes[0] = (byte) temp;
		return bytes;
	}

	/**
	 * internal module debugging log info
	 */
	private String logID;
	private String logSeq;
	private AtomicLong seqID = new AtomicLong(1);

	public String getLogID() {
		return logID;
	}

	public void setLogID(String logID) {
		this.logID = logID;
	}

	public String getLogSeq() {
		return logSeq;
	}

	public String getAndIncrementLogSeq() {
		if (logSeq == null || logSeq.length() == 0) {
			return Long.toString(seqID.getAndIncrement());
		} else {
			return logSeq + "." + seqID.getAndIncrement();
		}
	}

	public void setLogSeq(String logSeq) {
		this.logSeq = logSeq;
	}
}
