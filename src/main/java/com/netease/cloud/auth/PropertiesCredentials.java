package com.netease.cloud.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Simple implementation Credentials that reads in access keys from a properties
 * file. The access key is expected to be in the "accessKey" property and the
 * secret key id is expected to be in the "secretKey" property.
 */
public class PropertiesCredentials implements Credentials {

	private final String accessKey;
	private final String secretAccessKey;

	/**
	 * Reads the specified file as a Java properties file and extracts the
	 * access key from the "accessKey" property and secret access key from the
	 * "secretKey" property. If the specified file doesn't contain the access
	 * keys an IOException will be thrown.
	 * 
	 * @param file
	 *            The file from which to read the credentials properties.
	 * 
	 * @throws FileNotFoundException
	 *             If the specified file isn't found.
	 * @throws IOException
	 *             If any problems are encountered reading the access keys from
	 *             the specified file.
	 * @throws IllegalArgumentException
	 *             If the specified properties file does not contain the
	 *             required keys.
	 */
	public PropertiesCredentials(File file) throws FileNotFoundException, IOException, IllegalArgumentException {
		if (!file.exists()) {
			throw new FileNotFoundException("File doesn't exist:  " + file.getAbsolutePath());
		}

		Properties accountProperties = new Properties();
		accountProperties.load(new FileInputStream(file));

		if (accountProperties.getProperty("accessKey") == null || accountProperties.getProperty("secretKey") == null) {
			throw new IllegalArgumentException("The specified file (" + file.getAbsolutePath() + ") "
					+ "doesn't contain the expected properties 'accessKey' and 'secretKey'.");
		}

		accessKey = accountProperties.getProperty("accessKey");
		secretAccessKey = accountProperties.getProperty("secretKey");
	}

	/**
	 * Reads the specified input stream as a stream of Java properties file
	 * content and extracts the access key ID and secret access key from the
	 * properties.
	 * 
	 * @param inputStream
	 *            The input stream containing the credential properties.
	 * 
	 * @throws IOException
	 *             If any problems occur while reading from the input stream.
	 */
	public PropertiesCredentials(InputStream inputStream) throws IOException {
		Properties accountProperties = new Properties();
		try {
			accountProperties.load(inputStream);
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
			}
		}

		if (accountProperties.getProperty("accessKey") == null || accountProperties.getProperty("secretKey") == null) {
			throw new IllegalArgumentException("The specified properties data "
					+ "doesn't contain the expected properties 'accessKey' and 'secretKey'.");
		}

		accessKey = accountProperties.getProperty("accessKey");
		secretAccessKey = accountProperties.getProperty("secretKey");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netease.cloud.auth.Credentials#getAccessKeyId()
	 */
	public String getAccessKeyId() {
		return accessKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netease.cloud.auth.Credentials#getSecretKey()
	 */
	public String getSecretKey() {
		return secretAccessKey;
	}

}
