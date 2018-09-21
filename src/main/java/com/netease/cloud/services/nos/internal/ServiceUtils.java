package com.netease.cloud.services.nos.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.netease.cloud.services.nos.transfer.Download;
import com.netease.cloud.services.nos.transfer.Transfer;
import com.netease.cloud.services.nos.transfer.internal.DownloadImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netease.cloud.ClientException;
import com.netease.cloud.Request;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.NOSObject;
import com.netease.cloud.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General utility methods used throughout the Java client.
 */
public class ServiceUtils {
	private static final Logger log = LoggerFactory.getLogger(ServiceUtils.class);

	protected static final DateUtils dateUtils = new DateUtils();

	public static Date parseIso8601Date(String dateString) throws ParseException {
		return dateUtils.parseIso8601Date(dateString);
	}

	public static String formatIso8601Date(Date date) {
		return dateUtils.formatIso8601Date(date);
	}

	public static Date parseRfc822Date(String dateString) throws ParseException {
		return dateUtils.parseRfc822Date(dateString);
	}

	public static String formatRfc822Date(Date date) {
		return dateUtils.formatRfc822Date(date);
	}
	
	public static String formatRfc822DateShangHai(Date date) {
		return dateUtils.formatRfc822DateShangHai(date);
	}

	/**
	 * Returns true if the specified ETag was from a multipart upload.
	 * 
	 * @param eTag
	 *            The ETag to test.
	 * 
	 * @return True if the specified ETag was from a multipart upload, otherwise
	 *         false it if belongs to an object that was uploaded in a single
	 *         part.
	 */
	public static boolean isMultipartUploadETag(String eTag) {
		if (eTag == null) {
			return false;
		}
		return eTag.contains("-");
	}

	/**
	 * Safely converts a string to a byte array, first attempting to explicitly
	 * use our preferred encoding (UTF-8), and then falling back to the
	 * platform's default encoding if for some reason our preferred encoding
	 * isn't supported.
	 * 
	 * @param s
	 *            The string to convert to a byte array.
	 * 
	 * @return The byte array contents of the specified string.
	 */
	public static byte[] toByteArray(String s) {
		try {
			return s.getBytes(Constants.DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.warn("Encoding " + Constants.DEFAULT_ENCODING + " is not supported", e);
			return s.getBytes();
		}
	}

	/**
	 * Computes the MD5 hash of the data in the given input stream and returns
	 * it as a hex string.
	 * 
	 * @param is
	 * @return MD5 hash
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static byte[] computeMD5Hash(InputStream is) throws NoSuchAlgorithmException, IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[16384];
			int bytesRead = -1;
			while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
				messageDigest.update(buffer, 0, bytesRead);
			}
			return messageDigest.digest();
		} finally {
			try {
				bis.close();
			} catch (Exception e) {
				System.err.println("Unable to close input stream of hash candidate: " + e);
			}
		}
	}

	/**
	 * Computes the MD5 hash of the given data and returns it as a hex string.
	 * 
	 * @param data
	 * @return MD5 hash.
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static byte[] computeMD5Hash(byte[] data) throws NoSuchAlgorithmException, IOException {
		return computeMD5Hash(new ByteArrayInputStream(data));
	}

	/**
	 * Removes any surrounding quotes from the specified string and returns a
	 * new string.
	 * 
	 * @param s
	 *            The string to check for surrounding quotes.
	 * 
	 * @return A new string created from the specified string, minus any
	 *         surrounding quotes.
	 */
	public static String removeQuotes(String s) {
		if (s == null)
			return null;

		s = s.trim();
		if (s.startsWith("\""))
			s = s.substring(1);
		if (s.endsWith("\""))
			s = s.substring(0, s.length() - 1);

		return s;
	}

	/**
	 * URL encodes the specified string and returns it. All keys specified by
	 * users need to URL encoded. The URL encoded key needs to be used in the
	 * string to sign (canonical resource path).
	 * 
	 * @param s
	 *            The string to URL encode.
	 * 
	 * @return The new, URL encoded, string.
	 */
	public static String urlEncode(String s) {
		if (s == null)
			return null;

		try {
			String encodedString = URLEncoder.encode(s, Constants.DEFAULT_ENCODING);
			// return encodedString;
			// Web browsers do not always handle '+' characters well, use the
			// well-supported '%20' instead.
			return encodedString.replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new ClientException("Unable to encode path: " + s, e);
		}
	}

	/**
	 * Converts the specified request object into a URL, containing all the
	 * specified parameters, the specified request endpoint, etc.
	 * 
	 * @param request
	 *            The request to convert into a URL.
	 * @return A new URL representing the specified request.
	 * 
	 * @throws ClientException
	 *             If the request cannot be converted to a well formed URL.
	 */
	public static URL convertRequestToUrl(Request<?> request) {
		String urlString = request.getEndpoint() + "/" + request.getResourcePath();

		boolean firstParam = true;
		for (String param : request.getParameters().keySet()) {
			if (firstParam) {
				urlString += "?";
				firstParam = false;
			} else {
				urlString += "&";
			}

			String value = request.getParameters().get(param);
			urlString += param + "=" + ServiceUtils.urlEncode(value);
		}

		try {
			return new URL(urlString);
		} catch (MalformedURLException e) {
			throw new ClientException("Unable to convert request to well formed URL: " + e.getMessage(), e);
		}
	}

	/**
	 * Returns a new string created by joining each of the strings in the
	 * specified list together, with a comma between them.
	 * 
	 * @param strings
	 *            The list of strings to join into a single, comma delimited
	 *            string list.
	 * @return A new string created by joining each of the strings in the
	 *         specified list together, with a comma between strings.
	 */
	public static String join(List<String> strings) {
		String result = "";

		boolean first = true;
		for (String s : strings) {
			if (!first)
				result += ", ";

			result += s;
			first = false;
		}

		return result;
	}

	/**
	 * Downloads an Object, as returned from
	 * {@link NosClient#getObject(com.netease.cloud.services.nos.model.GetObjectRequest)}
	 * , to the specified file.
	 * 
	 * @param Object
	 *            The Object containing a reference to an InputStream containing
	 *            the object's data.
	 * @param destinationFile
	 *            The file to store the object's data in.
	 */
	public static void downloadObjectToFile(NOSObject Object, File destinationFile) {

		// attempt to create the parent if it doesn't exist
		File parentDirectory = destinationFile.getParentFile();
		if (parentDirectory != null && !parentDirectory.exists()) {
			parentDirectory.mkdirs();
		}
		OutputStream outputStream = null;
		try {
			outputStream = new BufferedOutputStream(new FileOutputStream(destinationFile));
			byte[] buffer = new byte[1024 * 10];
			int bytesRead;
            long contentLength = Object.getObjectMetadata().getContentLength();
			int byteReadAll = 0;
			while ((bytesRead = Object.getObjectContent().read(buffer)) > -1) {
				outputStream.write(buffer, 0, bytesRead);
				byteReadAll += bytesRead;
			}
			if(byteReadAll != contentLength){
			    throw new IOException();
            }
		} catch (IOException e) {
			try {
				Object.getObjectContent().abort();
			} catch (IOException abortException) {
				log.warn("Couldn't abort stream", e);
			}
			throw new ClientException("Unable to store object contents to disk: " + e.getMessage(), e);
		} finally {
			try {
				outputStream.close();
			} catch (Exception e) {
			}
			try {
				Object.getObjectContent().close();
			} catch (Exception e) {
			}
		}
	}

	public static void downloadObjectToFile(DownloadImpl download, File destinationFile, long rangeBegin, long rangeEnd) {

		// attempt to create the parent if it doesn't exist
		File parentDirectory = destinationFile.getParentFile();
		if (parentDirectory != null && !parentDirectory.exists()) {
			parentDirectory.mkdirs();
		}
		OutputStream outputStream = null;
		NOSObject nosObject = download.getNosObject();
		try {

			outputStream = new BufferedOutputStream(new FileOutputStream(destinationFile,true));
			byte[] buffer = new byte[1024 * 10];
			int bytesRead;
			Map<String,String> rawMetaData = nosObject.getObjectMetadata().getUserMetadata();
			long contentLength  = rangeEnd - rangeBegin + 1;
			int byteReadAll = 0;
			while ((bytesRead = nosObject.getObjectContent().read(buffer)) > -1 &&
					download.getState() == Transfer.TransferState.InProgress) {
				outputStream.write(buffer, 0, bytesRead);
				byteReadAll += bytesRead;
			}
			if(byteReadAll != contentLength && download.getState() == Transfer.TransferState.InProgress){
				throw new IOException();
			}
		} catch (IOException e) {
			try {
				nosObject.getObjectContent().abort();
			} catch (IOException abortException) {
				log.warn("Couldn't abort stream", e);
			}
			throw new ClientException("Unable to store object contents to disk: " + e.getMessage(), e);
		} finally {
			try {
				outputStream.close();
			} catch (Exception e) {
			}
			try {
				nosObject.getObjectContent().close();
			} catch (Exception e) {
			}
		}
	}
}
