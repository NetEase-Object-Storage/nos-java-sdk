package com.netease.cloud.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

/**
 * Utility methods for computing MD5 sums.
 */
public class Md5Utils {

    /**
     * Computes the MD5 hash of the data in the given input stream and returns
     * it as an array of bytes.
     */
    public static byte[] computeMD5Hash(InputStream is) throws NoSuchAlgorithmException, IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[16384];
            int bytesRead = -1;
            while ( (bytesRead = bis.read(buffer, 0, buffer.length)) != -1 ) {
                messageDigest.update(buffer, 0, bytesRead);
            }
            return messageDigest.digest();
        } finally {
            try {
                bis.close();
            } catch ( Exception e ) {
                System.err.println("Unable to close input stream of hash candidate: " + e);
            }
        }
    }

    /**
     * Computes the MD5 hash of the given data and returns it as an array of
     * bytes.
     */
    public static byte[] computeMD5Hash(byte[] data) throws NoSuchAlgorithmException, IOException {
        return computeMD5Hash(new ByteArrayInputStream(data));
    }
    
	
	public static String getHex(byte[] raw) {

		if (raw == null) {
			return "null";
		}

		return new String(Hex.encodeHex(raw));
	}
}
