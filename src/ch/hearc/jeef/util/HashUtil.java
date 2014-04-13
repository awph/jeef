package ch.hearc.jeef.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class HashUtil {

    /**
     * http://stackoverflow.com/a/11009612/2648956
     *
     * @param string the string to hash
     * @return the string hashed (MD5)
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static final String hashMD5(String string) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hash = digest.digest(string.getBytes("UTF-8"));
        return bytesToString(hash);
    }

    /**
     * http://stackoverflow.com/a/11009612/2648956
     *
     * @param string the string to hash
     * @return the string hashed (SHA512)
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static final String hashSHA512(String string) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] hash = digest.digest(string.getBytes("UTF-8"));
        return bytesToString(hash);
    }

    /**
     * http://stackoverflow.com/a/18268562/2648956
     *
     * @param size the size of the desired salt
     * @return the salt
     */
    public static final String generateSalt(final Integer size) {
        final Random r = new SecureRandom();
        byte[] salt = new byte[size / 2];
        r.nextBytes(salt);
        return bytesToString(salt);
    }

    private static final String bytesToString(byte[] bytes) {
        StringBuffer encryptedUserID = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i) {
            String hex = Integer.toHexString(0xff & bytes[i]);
            if (hex.length() == 1) {
                encryptedUserID.append('0');
            }
            encryptedUserID.append(hex);
        }

        return encryptedUserID.toString();
    }
}
