package com.netease.cloud.services.nos.internal;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.netease.cloud.ClientException;

/**
 * InputStream wrapper that calculates a client-side MD5 checksum of the data as
 * it is read from the wrapped InputStream, then when the stream is closed, the
 * client-side calculated MD5 checksum is compared with the server-side MD5
 * checksum, and if they don't match, then close() throws an exception to alert
 * the user to the likely data corruption.
 */
public class ChecksumValidatingInputStream extends FilterInputStream {

	/** The checksum we expect to match this InputStream's content. */
	private final byte[] expectedChecksum;

	/**
	 * The private DigestInputStream we use to wrap the source input stream and
	 * compute a client-side MD5 checksum.
	 */
	private final DigestInputStream digestInputStream;

	/**
	 * Description of the source object from Nos in case we need to alert the
	 * user that the checksums didn't match.
	 */
	private final String sourceObject;

	/**
	 * Keeps track of whether all content has been read from the wrapped
	 * InputStream or not, since we can't compare checksums unless we've read
	 * all of the content from the wrapped InputStream, otherwise we'll have an
	 * incorrect client-side checksum.
	 */
	private boolean hasReadAllContent = false;

	/**
	 * Constructs a new InputStream that wraps an existing InputStream and
	 * calculates a client-side MD5 checksum as the user reads data from the
	 * stream. When the stream is completely read, if the calculated,
	 * client-side MD5 checksum doesn't match the server-side MD5 checksum from
	 * Nos, then the close() method will throw an exception to alert the user
	 * about the likely data corruption.
	 * 
	 * @param in
	 *            The InputStream to wrap, containing the content to calculate
	 *            the MD5 checksum for.
	 * @param expectedChecksum
	 *            The expected MD5 checksum for the data.
	 * @param sourceObject
	 *            A text description of the original source object in Nos; only
	 *            used in an error message to give the user more context about
	 *            what failed if data corruption is detected.
	 * 
	 * @throws NoSuchAlgorithmException
	 *             If the running JVM doesn't support MD5 MessageDigests.
	 */
	public ChecksumValidatingInputStream(InputStream in, byte[] expectedChecksum, String sourceObject)
			throws NoSuchAlgorithmException {
		super(new DigestInputStream(in, MessageDigest.getInstance("MD5")));
		this.expectedChecksum = expectedChecksum;
		this.sourceObject = sourceObject;
		this.digestInputStream = (DigestInputStream) super.in;
	}

	@Override
	public int read() throws IOException {
		int read = super.read();
		hasReadAllContent = (read == -1);
		return read;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int read = super.read(b, off, len);
		hasReadAllContent = (read == 0);
		return read;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int read = super.read(b);
		hasReadAllContent = (read == 0);
		return read;
	}

	@Override
	public void close() throws IOException {
		super.close();

		// If we haven't read all the content from the wrapped InputStream, then
		// we can't try to compare the two checksums, so just bail out.
		if (hasReadAllContent == false)
			return;

		byte[] clientSideHash = digestInputStream.getMessageDigest().digest();
		if (!Arrays.equals(clientSideHash, expectedChecksum)) {
			throw new ClientException("Unable to verify integrity of data download.  "
					+ "Client calculated content hash didn't match hash calculated by Nos.  " + "The data read from '"
					+ sourceObject + "' may be corrupt.");
		}
	}
}
