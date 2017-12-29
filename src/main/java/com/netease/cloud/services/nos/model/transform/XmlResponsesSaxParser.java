package com.netease.cloud.services.nos.model.transform;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.netease.cloud.ClientException;
import com.netease.cloud.services.nos.internal.Constants;
import com.netease.cloud.services.nos.internal.DeleteObjectsResponse;
import com.netease.cloud.services.nos.internal.ObjectExpirationResult;
import com.netease.cloud.services.nos.internal.ServiceUtils;
import com.netease.cloud.services.nos.model.Bucket;
import com.netease.cloud.services.nos.model.BucketLifecycleConfiguration;
import com.netease.cloud.services.nos.model.BucketLifecycleConfiguration.Rule;
import com.netease.cloud.services.nos.model.CompleteMultipartUploadResult;
import com.netease.cloud.services.nos.model.DeleteObjectsResult;
import com.netease.cloud.services.nos.model.DeleteObjectsResult.DeletedObject;
import com.netease.cloud.services.nos.model.GetBucketDefault404Result;
import com.netease.cloud.services.nos.model.GetBucketStatsResult;
import com.netease.cloud.services.nos.model.InitiateMultipartUploadResult;
import com.netease.cloud.services.nos.model.MultiObjectDeleteException;
import com.netease.cloud.services.nos.model.MultiObjectDeleteException.DeleteError;
import com.netease.cloud.services.nos.model.MultipartUpload;
import com.netease.cloud.services.nos.model.MultipartUploadListing;
import com.netease.cloud.services.nos.model.NOSException;
import com.netease.cloud.services.nos.model.NOSObjectSummary;
import com.netease.cloud.services.nos.model.ObjectListing;
import com.netease.cloud.services.nos.model.Owner;
import com.netease.cloud.services.nos.model.PartListing;
import com.netease.cloud.services.nos.model.PartSummary;
import com.netease.cloud.util.DateUtils;

/**
 * XML Sax parser to read XML documents returned by Nos via the REST interface,
 * converting these documents into objects.
 */
public class XmlResponsesSaxParser {
	private static final Log log = LogFactory.getLog(XmlResponsesSaxParser.class);

	private XMLReader xr = null;

	private boolean sanitizeXmlDocument = true;

	/**
	 * Constructs the XML SAX parser.
	 * 
	 * @throws ClientException
	 */
	public XmlResponsesSaxParser() throws ClientException {
		// Ensure we can load the XML Reader.
		try {
			xr = XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			// oops, lets try doing this (needed in 1.4)
			System.setProperty("org.xml.sax.driver", "org.apache.crimson.parser.XMLReaderImpl");
			try {
				// Try once more...
				xr = XMLReaderFactory.createXMLReader();
			} catch (SAXException e2) {
				throw new ClientException("Couldn't initialize a sax driver for the XMLReader");
			}
		}
	}
	

	/**
	 * Parses an XML document from an input stream using a document handler.
	 * 
	 * @param handler
	 *            the handler for the XML document
	 * @param inputStream
	 *            an input stream containing the XML document to parse
	 * 
	 * @throws ClientException
	 *             any parsing, IO or other exceptions are wrapped in an
	 *             serviceException.
	 */
	protected void parseXmlInputStream(DefaultHandler handler, InputStream inputStream) throws ClientException {
		try {
			if (log.isDebugEnabled()) {
				log.debug("Parsing XML response document with handler: " + handler.getClass());
			}

			BufferedReader breader = new BufferedReader(new InputStreamReader(inputStream, Constants.DEFAULT_ENCODING));
			xr.setContentHandler(handler);
			xr.setErrorHandler(handler);
			xr.parse(new InputSource(breader));
		} catch (Throwable t) {
			try {
				inputStream.close();
			} catch (IOException e) {
				if (log.isErrorEnabled()) {
					log.error("Unable to close response InputStream up after XML parse failure", e);
				}
			}
			throw new ClientException("Failed to parse XML document with handler " + handler.getClass(), t);
		}
	}

	protected InputStream sanitizeXmlDocument(DefaultHandler handler, InputStream inputStream) throws ClientException {
		if (!sanitizeXmlDocument) {
			// No sanitizing will be performed, return the original input stream
			// unchanged.
			return inputStream;
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Sanitizing XML document destined for handler " + handler.getClass());
			}

			InputStream sanitizedInputStream = null;

			try {
				/*
				 * Read object listing XML document from input stream provided
				 * into a string buffer, so we can replace troublesome
				 * characters before sending the document to the XML parser.
				 */
				StringBuilder listingDocBuffer = new StringBuilder();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Constants.DEFAULT_ENCODING));

				char[] buf = new char[8192];
				int read = -1;
				while ((read = br.read(buf)) != -1) {
					listingDocBuffer.append(buf, 0, read);
				}
				br.close();

				/*
				 * Replace any carriage return (\r) characters with explicit XML
				 * character entities, to prevent the SAX parser from
				 * misinterpreting 0x0D characters as 0x0A and being unable to
				 * parse the XML.
				 */
				// String listingDoc =
				// listingDocBuffer.toString().replaceAll("\r", "&#013;");

				sanitizedInputStream = new ByteArrayInputStream(listingDocBuffer.toString().getBytes(
						Constants.DEFAULT_ENCODING));
			} catch (Throwable t) {
				try {
					inputStream.close();
				} catch (IOException e) {
					if (log.isErrorEnabled()) {
						log.error("Unable to close response InputStream after failure sanitizing XML document", e);
					}
				}
				throw new ClientException("Failed to sanitize XML document destined for handler " + handler.getClass(),
						t);
			}
			return sanitizedInputStream;
		}
	}

	/**
	 * Checks if the specified string is empty or null and if so, returns null.
	 * Otherwise simply returns the string.
	 * 
	 * @param s
	 *            The string to check.
	 * @return Null if the specified string was null, or empty, otherwise
	 *         returns the string the caller passed in.
	 */
	private String checkForEmptyString(String s) {
		if (s == null)
			return null;
		if (s.length() == 0)
			return null;

		return s;
	}

	/**
	 * Safely parses the specified string as an integer and returns the value.
	 * If a NumberFormatException occurs while parsing the integer, an error is
	 * logged and -1 is returned.
	 * 
	 * @param s
	 *            The string to parse and return as an integer.
	 * 
	 * @return The integer value of the specified string, otherwise -1 if there
	 *         were any problems parsing the string as an integer.
	 */
	private int parseInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			log.error("Unable to parse integer value '" + s + "'", nfe);
		}

		return -1;
	}

	/**
	 * Safely parses the specified string as a long and returns the value. If a
	 * NumberFormatException occurs while parsing the long, an error is logged
	 * and -1 is returned.
	 * 
	 * @param s
	 *            The string to parse and return as a long.
	 * 
	 * @return The long value of the specified string, otherwise -1 if there
	 *         were any problems parsing the string as a long.
	 */
	private long parseLong(String s) {
		try {
			return Long.parseLong(s);
		} catch (NumberFormatException nfe) {
			log.error("Unable to parse long value '" + s + "'", nfe);
		}

		return -1;
	}

	/**
	 * Parses a ListBucket response XML document from an input stream.
	 * 
	 * @param inputStream
	 *            XML data input stream.
	 * @return the XML handler object populated with data parsed from the XML
	 *         stream.
	 * @throws ClientException
	 */
	public ListBucketHandler parseListBucketObjectsResponse(InputStream inputStream) throws ClientException {
		ListBucketHandler handler = new ListBucketHandler();
		parseXmlInputStream(handler, sanitizeXmlDocument(handler, inputStream));
		return handler;
	}

	/**
	 * Parses a ListAllMyBuckets response XML document from an input stream.
	 * 
	 * @param inputStream
	 *            XML data input stream.
	 * @return the XML handler object populated with data parsed from the XML
	 *         stream.
	 * @throws ClientException
	 */
	public ListAllMyBucketsHandler parseListMyBucketsResponse(InputStream inputStream) throws ClientException {
		ListAllMyBucketsHandler handler = new ListAllMyBucketsHandler();
		parseXmlInputStream(handler, sanitizeXmlDocument(handler, inputStream));
		return handler;
	}

	public String parseBucketLocationResponse(InputStream inputStream) throws ClientException {
		BucketLocationHandler handler = new BucketLocationHandler();
		parseXmlInputStream(handler, inputStream);
		return handler.getLocation();
	}

	public DeleteObjectsHandler parseDeletedObjectsResult(InputStream inputStream) {
		DeleteObjectsHandler handler = new DeleteObjectsHandler();
		parseXmlInputStream(handler, inputStream);
		return handler;
	}

	public CopyObjectResultHandler parseCopyObjectResponse(InputStream inputStream) throws ClientException {
		CopyObjectResultHandler handler = new CopyObjectResultHandler();
		parseXmlInputStream(handler, inputStream);
		return handler;
	}

	public CompleteMultipartUploadHandler parseCompleteMultipartUploadResponse(InputStream inputStream)
			throws ClientException {
		CompleteMultipartUploadHandler handler = new CompleteMultipartUploadHandler();
		parseXmlInputStream(handler, inputStream);
		return handler;
	}

	public InitiateMultipartUploadHandler parseInitiateMultipartUploadResponse(InputStream inputStream)
			throws ClientException {
		InitiateMultipartUploadHandler handler = new InitiateMultipartUploadHandler();
		parseXmlInputStream(handler, inputStream);
		return handler;
	}

	public ListMultipartUploadsHandler parseListMultipartUploadsResponse(InputStream inputStream)
			throws ClientException {
		ListMultipartUploadsHandler handler = new ListMultipartUploadsHandler();
		parseXmlInputStream(handler, inputStream);
		return handler;
	}

	public ListPartsHandler parseListPartsResponse(InputStream inputStream) throws ClientException {
		ListPartsHandler handler = new ListPartsHandler();
		parseXmlInputStream(handler, inputStream);
		return handler;
	}

	public GetBucketStatsHandler parseGetBucketStats(InputStream inputStream) throws ClientException {
		GetBucketStatsHandler handler = new GetBucketStatsHandler();
		parseXmlInputStream(handler, inputStream);
		return handler;
	}
	
	public GetBucketDefault404Handler parseGetBucketDefault404Response(InputStream inputStream) throws ClientException {
		GetBucketDefault404Handler handler = new GetBucketDefault404Handler();
		parseXmlInputStream(handler, inputStream);
		return handler;
	}

	/**
	 * @param inputStream
	 * 
	 * @return true if the bucket's is configured as Requester Pays, false if it
	 *         is configured as Owner pays.
	 * 
	 * @throws ClientException
	 */
	public boolean parseRequestPaymentConfigurationResponse(InputStream inputStream) throws ClientException {
		RequestPaymentConfigurationHandler handler = new RequestPaymentConfigurationHandler();
		parseXmlInputStream(handler, inputStream);
		return handler.isRequesterPays();
	}
	
	public BucketLifecycleConfigurationHandler parseBucketLifecycleConfigurationResponse(InputStream inputStream)
            throws IOException {
        BucketLifecycleConfigurationHandler handler = new BucketLifecycleConfigurationHandler();
        parseXmlInputStream(handler, inputStream);
        return handler;
    }
	
	public class ListBucketHandler extends DefaultHandler {
		private NOSObjectSummary currentObject = null;
		private Owner currentOwner = null;
		private StringBuilder currText = null;

		private ObjectListing objectListing = null;
		private List<String> commonPrefixes = null;

		private boolean insideCommonPrefixes = false;
		private boolean initCommonPrefixes = false;

		public ObjectListing getObjectListing() {
			return objectListing;
		}

		@Override
		public void startDocument() {
			currText = new StringBuilder();
		}

		public void startElement(String uri, String name, String qName, Attributes attrs) {
			if (name.equals("ListBucketResult")) {
				objectListing = new ObjectListing();
			} else if (name.equals("Contents")) {
				currentObject = new NOSObjectSummary();
			} else if (name.equals("Owner")) {
				currentOwner = new Owner();
			} else if (name.equals("CommonPrefixes")) {
				if(!initCommonPrefixes) {
					commonPrefixes = new ArrayList<String>();
					initCommonPrefixes = true;
				}
				insideCommonPrefixes = true;
			}
			currText.setLength(0);
		}

		public void endElement(String uri, String name, String qName) {
			String elementText = this.currText.toString().trim();
			if (name.equals("Name")) {
				objectListing.setBucketName(elementText);
			} else if (name.equals("Prefix") && !insideCommonPrefixes) {
				objectListing.setPrefix(elementText);
			} else if (name.equals("Marker")) {
				objectListing.setMarker(elementText);
			} else if (name.equals("NextMarker")) {
				objectListing.setNextMarker(elementText);
			} else if (name.equals("MaxKeys")) {
				objectListing.setMaxKeys(parseInt(elementText));
			} else if (name.equals("Delimiter")) {
				objectListing.setDelimiter(elementText);
			} else if (name.equals("IsTruncated")) {
				String isTruncatedStr = elementText.toLowerCase(Locale.getDefault());
				if (isTruncatedStr.startsWith("false")) {
					objectListing.setTruncated(false);
				} else if (isTruncatedStr.startsWith("true")) {
					objectListing.setTruncated(true);
				} else {
					throw new RuntimeException("Invalid value for IsTruncated field: " + isTruncatedStr);
				}
			}
			// Object details.
			else if (name.equals("Contents")) {
				if (currentObject.getKey() != null && !currentObject.getKey().equals("")) {
					currentObject.setBucketName(objectListing.getBucketName());
					objectListing.getObjectSummaries().add(currentObject);
				}

			} else if (name.equals("Key")) {
				currentObject.setKey(elementText);
			} else if (name.equals("LastModified")) {
				try {
					currentObject.setLastModified(ServiceUtils.parseIso8601Date(elementText));
				} catch (ParseException e) {
					throw new RuntimeException("Non-ISO8601 date for LastModified in bucket's object listing output: "
							+ elementText, e);
				}
			} else if (name.equals("ETag")) {
				currentObject.setETag(ServiceUtils.removeQuotes(elementText));
			} else if (name.equals("Size")) {
				currentObject.setSize(parseLong(elementText));
			} else if (name.equals("StorageClass")) {
				currentObject.setStorageClass(elementText);
			}
			// Owner details.
			else if (name.equals("Owner")) {
				currentObject.setOwner(currentOwner);
			} else if (name.equals("ID")) {
				currentOwner.setId(elementText);
			} else if (name.equals("DisplayName")) {
				currentOwner.setDisplayName(elementText);
			}
			// Common prefixes.
			else if (name.equals("Prefix") && insideCommonPrefixes) {
				commonPrefixes.add(elementText);
			} else if (name.equals("CommonPrefixes")) {
				objectListing.setCommonPrefixes(commonPrefixes);
				insideCommonPrefixes = false;
			}
			currText.setLength(0);
		}

		public void characters(char ch[], int start, int length) {
			this.currText.append(ch, start, length);
		}
	}

	/**
	 * Handler for ListAllMyBuckets response XML documents. The document is
	 * parsed into {@link Bucket}s available via the {@link #getBuckets()}
	 * method.
	 */
	public class ListAllMyBucketsHandler extends DefaultHandler {
		private Owner bucketsOwner = null;
		private Bucket currentBucket = null;
		private StringBuilder currText = null;

		private List<Bucket> buckets = null;

		public ListAllMyBucketsHandler() {
			super();
			buckets = new ArrayList<Bucket>();
			this.currText = new StringBuilder();
		}

		/**
		 * @return the buckets listed in the document.
		 */
		public List<Bucket> getBuckets() {
			return buckets;
		}

		/**
		 * @return the owner of the buckets.
		 */
		public Owner getOwner() {
			return bucketsOwner;
		}

		public void startDocument() {
		}

		public void endDocument() {
		}

		public void startElement(String uri, String name, String qName, Attributes attrs) {
			if (name.equals("Bucket")) {
				currentBucket = new Bucket();
			} else if (name.equals("Owner")) {
				bucketsOwner = new Owner();
			}
		}

		public void endElement(String uri, String name, String qName) {
			String elementText = this.currText.toString().trim();
			// Listing details.
			if (name.equals("ID")) {
				bucketsOwner.setId(elementText);
			} else if (name.equals("DisplayName")) {
				bucketsOwner.setDisplayName(elementText);
			}
			// Bucket item details.
			else if (name.equals("Bucket")) {
				currentBucket.setOwner(bucketsOwner);
				buckets.add(currentBucket);
			} else if (name.equals("Name")) {
				currentBucket.setName(elementText);
			} else if (name.equals("CreationDate")) {
				
				try {
					 currentBucket.setCreationDate(ServiceUtils.parseIso8601Date(elementText));
				} catch (ParseException e) {
					throw new RuntimeException("Non-ISO8601 date for CreationDate in list buckets output: "
							+ elementText, e);
				}
			}
			this.currText = new StringBuilder();
		}

		public void characters(char ch[], int start, int length) {
			this.currText.append(ch, start, length);
		}
	}

	/**
	 * Handler for CreateBucketConfiguration response XML documents for a
	 * bucket. The document is parsed into a String representing the bucket's
	 * location, available via the {@link #getLocation()} method.
	 */
	public class BucketLocationHandler extends DefaultHandler {
		private String location = null;

		private StringBuilder currText = null;

		public BucketLocationHandler() {
			super();
			this.currText = new StringBuilder();
		}

		/**
		 * @return the bucket's location.
		 */
		public String getLocation() {
			return location;
		}

		public void startDocument() {
		}

		public void endDocument() {
		}

		public void startElement(String uri, String name, String qName, Attributes attrs) {
			if (name.equals("LocationConstraint")) {
			}
		}

		public void endElement(String uri, String name, String qName) {
			String elementText = this.currText.toString().trim();
			if (name.equals("LocationConstraint")) {
				if (elementText.length() == 0) {
					location = null;
				} else {
					location = elementText;
				}
			}
			this.currText = new StringBuilder();
		}

		public void characters(char ch[], int start, int length) {
			this.currText.append(ch, start, length);
		}
	}

	public class CopyObjectResultHandler extends DefaultHandler implements ObjectExpirationResult {

		// Data items for successful copy
		private String etag = null;
		private Date lastModified = null;
		private String versionId = null;
		private String serverSideEncryption;
		private Date expirationTime;
		private String expirationTimeRuleId;

		// Data items for failed copy
		private String errorCode = null;
		private String errorMessage = null;
		private String errorRequestId = null;
		private String errorHostId = null;
		private boolean receivedErrorResponse = false;

		private StringBuilder currText = null;

		public CopyObjectResultHandler() {
			super();
			this.currText = new StringBuilder();
		}

		public Date getLastModified() {
			return lastModified;
		}

		public String getVersionId() {
			return versionId;
		}

		public void setVersionId(String versionId) {
			this.versionId = versionId;
		}

		public String getServerSideEncryption() {
			return serverSideEncryption;
		}

		public void setServerSideEncryption(String serverSideEncryption) {
			this.serverSideEncryption = serverSideEncryption;
		}

		public Date getExpirationTime() {
			return expirationTime;
		}

		public void setExpirationTime(Date expirationTime) {
			this.expirationTime = expirationTime;
		}

		public String getExpirationTimeRuleId() {
			return expirationTimeRuleId;
		}

		public void setExpirationTimeRuleId(String expirationTimeRuleId) {
			this.expirationTimeRuleId = expirationTimeRuleId;
		}

		public String getETag() {
			return etag;
		}

		public String getErrorCode() {
			return errorCode;
		}

		public String getErrorHostId() {
			return errorHostId;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public String getErrorRequestId() {
			return errorRequestId;
		}

		public boolean isErrorResponse() {
			return receivedErrorResponse;
		}

		public void startDocument() {
		}

		public void endDocument() {
		}

		public void startElement(String uri, String name, String qName, Attributes attrs) {
			if (name.equals("CopyObjectResult")) {
				receivedErrorResponse = false;
			} else if (name.equals("Error")) {
				receivedErrorResponse = true;
			}
		}

		public void endElement(String uri, String name, String qName) {
			String elementText = this.currText.toString().trim();

			if (name.equals("LastModified")) {
				try {
					lastModified = ServiceUtils.parseIso8601Date(elementText);
				} catch (ParseException e) {
					throw new RuntimeException("Non-ISO8601 date for LastModified in copy object output: "
							+ elementText, e);
				}
			} else if (name.equals("ETag")) {
				etag = ServiceUtils.removeQuotes(elementText);
			} else if (name.equals("Code")) {
				errorCode = elementText;
			} else if (name.equals("Message")) {
				errorMessage = elementText;
			} else if (name.equals("RequestId")) {
				errorRequestId = elementText;
			} else if (name.equals("HostId")) {
				errorHostId = elementText;
			}

			this.currText = new StringBuilder();
		}

		public void characters(char ch[], int start, int length) {
			this.currText.append(ch, start, length);
		}
	}

	/**
	 * Handler for RequestPaymentConfiguration response XML documents for a
	 * bucket. The document is parsed into a boolean value: true if the bucket's
	 * is configured as Requester Pays, false if it is configured as Owner pays.
	 * This boolean value is available via the {@link #isRequesterPays()}
	 * method.
	 */
	public class RequestPaymentConfigurationHandler extends DefaultHandler {
		private String payer = null;

		private StringBuilder currText = null;

		public RequestPaymentConfigurationHandler() {
			super();
			this.currText = new StringBuilder();
		}

		/**
		 * @return true if the bucket's is configured as Requester Pays, false
		 *         if it is configured as Owner pays.
		 */
		public boolean isRequesterPays() {
			return "Requester".equals(payer);
		}

		public void startDocument() {
		}

		public void endDocument() {
		}

		public void startElement(String uri, String name, String qName, Attributes attrs) {
			if (name.equals("RequestPaymentConfiguration")) {
			}
		}

		public void endElement(String uri, String name, String qName) {
			String elementText = this.currText.toString().trim();
			if (name.equals("Payer")) {
				payer = elementText;
			}
			this.currText = new StringBuilder();
		}

		public void characters(char ch[], int start, int length) {
			this.currText.append(ch, start, length);
		}
	}

	public class CompleteMultipartUploadHandler extends DefaultHandler implements ObjectExpirationResult {

		private StringBuilder text;

		// Successful completion
		private CompleteMultipartUploadResult result;

		/**
		 * @see com.netease.cloud.services.nos.model.CompleteMultipartUploadResult#getExpirationTime()
		 */
		public Date getExpirationTime() {
			if (result != null)
				return result.getExpirationTime();
			return null;
		}

		/**
		 * @see com.netease.cloud.services.nos.model.CompleteMultipartUploadResult#setExpirationTime(java.util.Date)
		 */
		public void setExpirationTime(Date expirationTime) {
			if (result != null)
				result.setExpirationTime(expirationTime);
		}

		/**
		 * @see com.netease.cloud.services.nos.model.CompleteMultipartUploadResult#getExpirationTimeRuleId()
		 */
		public String getExpirationTimeRuleId() {
			if (result != null)
				return result.getExpirationTimeRuleId();
			return null;
		}

		/**
		 * @see com.netease.cloud.services.nos.model.CompleteMultipartUploadResult#setExpirationTimeRuleId(java.lang.String)
		 */
		public void setExpirationTimeRuleId(String expirationTimeRuleId) {
			if (result != null)
				result.setExpirationTimeRuleId(expirationTimeRuleId);
		}

		// Error during completion
		private NOSException ase;
		private String requestId;
		private String errorCode;

		public CompleteMultipartUploadResult getCompleteMultipartUploadResult() {
			return result;
		}

		public NOSException getNOSException() {
			return ase;
		}

		@Override
		public void startDocument() {
			text = new StringBuilder();
		}

		@Override
		public void startElement(String uri, String name, String qName, Attributes attrs) {
			// Success response XML elements
			if (name.equals("CompleteMultipartUploadResult")) {
				result = new CompleteMultipartUploadResult();
			} else if (name.equals("Location")) {
			} else if (name.equals("Bucket")) {
			} else if (name.equals("Key")) {
			} else if (name.equals("ETag")) {
			}

			// Error response XML elements
			if (name.equals("Error")) {
			} else if (name.equals("Code")) {
			} else if (name.equals("Message")) {
			} else if (name.equals("RequestId")) {
			}
			text.setLength(0);
		}

		@Override
		public void endElement(String uri, String name, String qName) throws SAXException {
			if (result != null) {
				// Success response XML elements
				if (name.equals("CompleteMultipartUploadResult")) {
				} else if (name.equals("Location")) {
					result.setLocation(text.toString());
				} else if (name.equals("Bucket")) {
					result.setBucketName(text.toString());
				} else if (name.equals("Key")) {
					result.setKey(text.toString());
				} else if (name.equals("ETag")) {
					result.setETag(ServiceUtils.removeQuotes(text.toString()));
				}
			} else {
				// Error response XML elements
				if (name.equals("Error")) {
					ase.setErrorCode(errorCode);
					ase.setRequestId(requestId);
				} else if (name.equals("Code")) {
					errorCode = text.toString();
				} else if (name.equals("Message")) {
					ase = new NOSException(text.toString());
				} else if (name.equals("RequestId")) {
					requestId = text.toString();
				}
			}
		}

		@Override
		public void characters(char ch[], int start, int length) {
			this.text.append(ch, start, length);
		}
	}

	public class InitiateMultipartUploadHandler extends DefaultHandler {
		private StringBuilder text;

		private InitiateMultipartUploadResult result;

		public InitiateMultipartUploadResult getInitiateMultipartUploadResult() {
			return result;
		}

		@Override
		public void startDocument() {
			text = new StringBuilder();
		}

		@Override
		public void startElement(String uri, String name, String qName, Attributes attrs) {
			if (name.equals("InitiateMultipartUploadResult")) {
				result = new InitiateMultipartUploadResult();
			} else if (name.equals("Bucket")) {
			} else if (name.equals("Key")) {
			} else if (name.equals("UploadId")) {
			}
			text.setLength(0);
		}

		@Override
		public void endElement(String uri, String name, String qName) throws SAXException {
			if (name.equals("InitiateMultipartUploadResult")) {
			} else if (name.equals("Bucket")) {
				result.setBucketName(text.toString());
			} else if (name.equals("Key")) {
				result.setKey(text.toString());
			} else if (name.equals("UploadId")) {
				result.setUploadId(text.toString());
			}
		}

		@Override
		public void characters(char ch[], int start, int length) {
			this.text.append(ch, start, length);
		}
	}

	public class ListMultipartUploadsHandler extends DefaultHandler {
		private StringBuilder text;

		private MultipartUploadListing result;

		private MultipartUpload currentMultipartUpload;
		private Owner currentOwner;
		private Owner currentInitiator;

		boolean inCommonPrefixes = false;

		public MultipartUploadListing getListMultipartUploadsResult() {
			return result;
		}

		@Override
		public void startDocument() {
			text = new StringBuilder();
		}

		@Override
		public void startElement(String uri, String name, String qName, Attributes attrs) {
			if (name.equals("ListMultipartUploadsResult")) {
				result = new MultipartUploadListing();
			} else if (name.equals("Bucket")) {
			} else if (name.equals("KeyMarker")) {
			} else if (name.equals("Delimiter")) {
			} else if (name.equals("UploadIdMarker")) {
			} else if (name.equals("NextKeyMarker")) {
			} else if (name.equals("NextUploadIdMarker")) {
			} else if (name.equals("MaxUploads")) {
			} else if (name.equals("IsTruncated")) {
			} else if (name.equals("Upload")) {
				currentMultipartUpload = new MultipartUpload();
			} else if (name.equals("Key")) {
			} else if (name.equals("UploadId")) {
			} else if (name.equals("Owner")) {
				currentOwner = new Owner();
			} else if (name.equals("Initiator")) {
				currentInitiator = new Owner();
			} else if (name.equals("ID")) {
			} else if (name.equals("DisplayName")) {
			} else if (name.equals("StorageClass")) {
			} else if (name.equals("Initiated")) {
			} else if (name.equals("CommonPrefixes")) {
				inCommonPrefixes = true;
			}
			text.setLength(0);
		}

		@Override
		public void endElement(String uri, String name, String qName) throws SAXException {
			if (name.equals("ListMultipartUploadsResult")) {
			} else if (name.equals("Bucket")) {
				result.setBucketName(text.toString());
			} else if (name.equals("KeyMarker")) {
				result.setKeyMarker(checkForEmptyString(text.toString()));
			} else if (name.equals("Delimiter")) {
				result.setDelimiter(checkForEmptyString(text.toString()));
			} else if (name.equals("Prefix") && inCommonPrefixes == false) {
				result.setPrefix(checkForEmptyString(text.toString()));
			} else if (name.equals("Prefix") && inCommonPrefixes == true) {
				result.getCommonPrefixes().add(text.toString());
			} else if (name.equals("UploadIdMarker")) {
				result.setUploadIdMarker(checkForEmptyString(text.toString()));
			} else if (name.equals("NextKeyMarker")) {
				result.setNextKeyMarker(checkForEmptyString(text.toString()));
			} else if (name.equals("NextUploadIdMarker")) {
				result.setNextUploadIdMarker(checkForEmptyString(text.toString()));
			} else if (name.equals("MaxUploads")) {
				result.setMaxUploads(Integer.parseInt(text.toString()));
			} else if (name.equals("IsTruncated")) {
				result.setTruncated(Boolean.parseBoolean(text.toString()));
			} else if (name.equals("Upload")) {
				result.getMultipartUploads().add(currentMultipartUpload);
			} else if (name.equals("Key")) {
				currentMultipartUpload.setKey(text.toString());
			} else if (name.equals("UploadId")) {
				currentMultipartUpload.setUploadId(text.toString());
			} else if (name.equals("Owner")) {
				currentMultipartUpload.setOwner(currentOwner);
				currentOwner = null;
			} else if (name.equals("Initiator")) {
				currentMultipartUpload.setInitiator(currentInitiator);
				currentInitiator = null;
			} else if (name.equals("ID") && currentOwner != null) {
				currentOwner.setId(checkForEmptyString(text.toString()));
			} else if (name.equals("DisplayName") && currentOwner != null) {
				currentOwner.setDisplayName(checkForEmptyString(text.toString()));
			} else if (name.equals("ID") && currentInitiator != null) {
				currentInitiator.setId(checkForEmptyString(text.toString()));
			} else if (name.equals("DisplayName") && currentInitiator != null) {
				currentInitiator.setDisplayName(checkForEmptyString(text.toString()));
			} else if (name.equals("StorageClass")) {
				currentMultipartUpload.setStorageClass(text.toString());
			} else if (name.equals("Initiated")) {
				try {
					currentMultipartUpload.setInitiated(ServiceUtils.parseIso8601Date(text.toString()));
				} catch (ParseException e) {
					throw new SAXException("Non-ISO8601 date for Initiated in initiate multipart upload result: "
							+ text.toString(), e);
				}
			} else if (name.equals("CommonPrefixes")) {
				inCommonPrefixes = false;
			}
		}

		@Override
		public void characters(char ch[], int start, int length) {
			this.text.append(ch, start, length);
		}
	}

	public class ListPartsHandler extends DefaultHandler {
		private StringBuilder text;

		private PartListing result;
		private Owner currentOwner;
		private Owner currentInitiator;
		private PartSummary currentPart;

		public PartListing getListPartsResult() {
			return result;
		}

		@Override
		public void startDocument() {
			text = new StringBuilder();
		}

		@Override
		public void startElement(String uri, String name, String qName, Attributes attrs) {
			if (name.equals("ListPartsResult")) {
				result = new PartListing();
			} else if (name.equals("Bucket")) {
			} else if (name.equals("Key")) {
			} else if (name.equals("UploadId")) {
			} else if (name.equals("Owner")) {
				currentOwner = new Owner();
			} else if (name.equals("Initiator")) {
				currentInitiator = new Owner();
			} else if (name.equals("ID")) {
			} else if (name.equals("DisplayName")) {
			} else if (name.equals("StorageClass")) {
			} else if (name.equals("PartNumberMarker")) {
			} else if (name.equals("NextPartNumberMarker")) {
			} else if (name.equals("MaxParts")) {
			} else if (name.equals("IsTruncated")) {
			} else if (name.equals("Part")) {
				currentPart = new PartSummary();
			} else if (name.equals("PartNumber")) {
			} else if (name.equals("LastModified")) {
			} else if (name.equals("ETag")) {
			} else if (name.equals("Size")) {
			}
			text.setLength(0);
		}

		private Integer parseInteger(String text) {
			text = checkForEmptyString(text.toString());
			if (text == null)
				return null;
			return Integer.parseInt(text);
		}

		@Override
		public void endElement(String uri, String name, String qName) throws SAXException {
			if (name.equals("ListPartsResult")) {
			} else if (name.equals("Bucket")) {
				result.setBucketName(text.toString());
			} else if (name.equals("Key")) {
				result.setKey(text.toString());
			} else if (name.equals("UploadId")) {
				result.setUploadId(text.toString());
			} else if (name.equals("Owner")) {
				result.setOwner(currentOwner);
				currentOwner = null;
			} else if (name.equals("Initiator")) {
				result.setInitiator(currentInitiator);
				currentInitiator = null;
			} else if (name.equals("ID") && currentOwner != null) {
				currentOwner.setId(checkForEmptyString(text.toString()));
			} else if (name.equals("DisplayName") && currentOwner != null) {
				currentOwner.setDisplayName(checkForEmptyString(text.toString()));
			} else if (name.equals("ID") && currentInitiator != null) {
				currentInitiator.setId(checkForEmptyString(text.toString()));
			} else if (name.equals("DisplayName") && currentInitiator != null) {
				currentInitiator.setDisplayName(checkForEmptyString(text.toString()));
			} else if (name.equals("StorageClass")) {
				result.setStorageClass(text.toString());
			} else if (name.equals("PartNumberMarker")) {
				result.setPartNumberMarker(parseInteger(text.toString()));
			} else if (name.equals("NextPartNumberMarker")) {
				result.setNextPartNumberMarker(parseInteger(text.toString()));
			} else if (name.equals("MaxParts")) {
				result.setMaxParts(parseInteger(text.toString()));
			} else if (name.equals("IsTruncated")) {
				result.setTruncated(Boolean.parseBoolean(text.toString()));
			} else if (name.equals("Part")) {
				result.getParts().add(currentPart);
			} else if (name.equals("PartNumber")) {
				currentPart.setPartNumber(Integer.parseInt(text.toString()));
			} else if (name.equals("LastModified")) {
				try {
					currentPart.setLastModified(ServiceUtils.parseIso8601Date(text.toString()));
				} catch (ParseException e) {
					throw new SAXException(
							"Non-ISO8601 date for LastModified in list parts result: " + text.toString(), e);
				}
			} else if (name.equals("ETag")) {
				currentPart.setETag(ServiceUtils.removeQuotes(text.toString()));
			} else if (name.equals("Size")) {
				currentPart.setSize(Long.parseLong(text.toString()));
			}
		}

		@Override
		public void characters(char ch[], int start, int length) {
			this.text.append(ch, start, length);
		}
	}

	public class DeleteObjectsHandler extends DefaultHandler {
		private StringBuilder text;

		private DeletedObject deletedObject = null;
		private DeleteError error = null;
		private List<DeletedObject> deletedObjects = new LinkedList<DeleteObjectsResult.DeletedObject>();
		private List<DeleteError> deleteErrors = new LinkedList<MultiObjectDeleteException.DeleteError>();

		public DeleteObjectsResponse getDeleteObjectResult() {
			return new DeleteObjectsResponse(deletedObjects, deleteErrors);
		}

		@Override
		public void startDocument() {
			text = new StringBuilder();
		}

		@Override
		public void startElement(String uri, String name, String qName, Attributes attrs) {
			if (name.equals("Deleted")) {
				deletedObject = new DeletedObject();
			} else if (name.equals("Error")) {
				error = new DeleteError();
			} else if (name.equals("Key")) {
			} else if (name.equals("VersionId")) {
			} else if (name.equals("Code")) {
			} else if (name.equals("Message")) {
			} else if (name.equals("DeleteMarker")) {
			} else if (name.equals("DeleteMarkerVersionId")) {
			} else if (name.equals("DeleteResult")) {
			} else {
				log.warn("Unexpected tag: " + name);
			}
			text.setLength(0);
		}

		@Override
		public void endElement(String uri, String name, String qName) throws SAXException {
			if (name.equals("Deleted")) {
				deletedObjects.add(deletedObject);
				deletedObject = null;
			} else if (name.equals("Error")) {
				deleteErrors.add(error);
				error = null;
			} else if (name.equals("Key")) {
				if (deletedObject != null) {
					deletedObject.setKey(text.toString());
				} else if (error != null) {
					error.setKey(text.toString());
				}
			} else if (name.equals("VersionId")) {
				/*if (deletedObject != null) {
					deletedObject.setVersionId(text.toString());
				} else if (error != null) {
					error.setVersionId(text.toString());
				}*/
			} else if (name.equals("Code")) {
				if (error != null) {
					error.setCode(text.toString());
				}
			} else if (name.equals("Message")) {
				if (error != null) {
					error.setMessage(text.toString());
				}
			} else if (name.equals("DeleteMarker")) {
				if (deletedObject != null) {
					deletedObject.setDeleteMarker(text.toString().equals("true"));
				}
			} else if (name.equals("DeleteMarkerVersionId")) {
				if (deletedObject != null) {
					deletedObject.setDeleteMarkerVersionId(text.toString());
				}
			}
		}

		@Override
		public void characters(char ch[], int start, int length) {
			this.text.append(ch, start, length);
		}
	}

	public class GetBucketStatsHandler extends DefaultHandler {
		private GetBucketStatsResult result = null;
		private StringBuilder text;

		public GetBucketStatsResult getResult() {
			return result;
		}

		@Override
		public void startDocument() {
			text = new StringBuilder();
		}

		@Override
		public void startElement(String uri, String name, String qName, Attributes attrs) {
			if (name.equals("BucketStats")) {
				result = new GetBucketStatsResult();
			} 
			
			text.setLength(0);
		}

		@Override
		public void endElement(String uri, String name, String qName) throws SAXException {
			if (name.equals("BucketStats")) {
			} else if (name.equals("BucketName")) {
				result.setBucketName(text.toString());
			} else if (name.equals("ObjectCount")) {
				result.setObjectCount(Long.parseLong(text.toString()));
			} else if (name.equals("StorageCapacity")) {
				result.setStorageCapacity(Long.parseLong(text.toString()));
			} else if (name.equals("DeduplicationRate")) {
				result.setDeduplicationRate(Double.parseDouble(text.toString()));
			} else {
				log.warn("Ignoring unexpected tag <" + name + ">");
			}
			text.setLength(0);
		}

		@Override
		public void characters(char ch[], int start, int length) {
			this.text.append(ch, start, length);
		}
	}
	
	public class GetBucketDefault404Handler extends DefaultHandler {
		private GetBucketDefault404Result result = null;
		private StringBuilder text;

		public GetBucketDefault404Result getResult() {
			return result;
		}

		@Override
		public void startDocument() {
			text = new StringBuilder();
		}

		@Override
		public void startElement(String uri, String name, String qName, Attributes attrs) {
			if (name.equals("Default404Configuration")) {
				result = new GetBucketDefault404Result();
			} else if (name.equals("Key")) {
			} else {
				log.warn("Ignoring unexpected tag <" + name + ">");
			}
			text.setLength(0);
		}

		@Override
		public void endElement(String uri, String name, String qName) throws SAXException {
			if (name.equals("Default404Configuration")) {
			} else if (name.equals("Key")) {
				result.setKey(text.toString());
			} else {
				log.warn("Ignoring unexpected tag <" + name + ">");
			}
			text.setLength(0);
		}

		@Override
		public void characters(char ch[], int start, int length) {
			this.text.append(ch, start, length);
		}
	}
	 
    public class BucketLifecycleConfigurationHandler extends DefaultHandler {

        private BucketLifecycleConfiguration configuration;
        private Rule currentRule;
        private StringBuilder text;
        
        public BucketLifecycleConfiguration getConfiguration() {
            return configuration;
        }
        
        @Override
		public void startDocument() {
			text = new StringBuilder();
		}

        @Override
		public void startElement(
                String uri,
                String name,
                String qName,
                Attributes attrs) {

            if (name.equals("LifecycleConfiguration")) {
            	configuration = new BucketLifecycleConfiguration(new ArrayList<Rule>());
            } else if (name.equals("Rule")) {
            	currentRule = new Rule();
            }
            text.setLength(0);
        }

        @Override
		public void endElement(String uri, String name, String qName) {
            if (name.equals("LifecycleConfiguration")) {
            } else if (name.equals("Rule")) {
            	configuration.getRules().add(currentRule);
            } else if (name.equals("ID")) {
            	currentRule.setId(text.toString());
            } else if (name.equals("Prefix")) {
            	currentRule.setPrefix(text.toString());
            } else if (name.equals("Status")) {
            	currentRule.setStatus(text.toString());
            } else if (name.equals("Expiration")) {
            } else if (name.equals("Date")) {
            	try {
					currentRule.setExpirationDate(new DateUtils().parseIso8601Date(text.toString()));
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
            } else if (name.equals("Days")) {
            	currentRule.setExpirationInDays(Integer.parseInt(text.toString()));
            } else {
				log.warn("Ignoring unexpected tag <" + name + ">");
			}
        }
        
        public void characters(char ch[], int start, int length) {
			this.text.append(ch, start, length);
		}
    }
}
