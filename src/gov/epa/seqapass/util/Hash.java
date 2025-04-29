package gov.epa.seqapass.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
	   public static String generateSHA256(String message) throws UnsupportedEncodingException {
	        try {
				return hashString(message, "SHA-256");
			} catch (UnsupportedEncodingException | NoSuchAlgorithmException  e) {
//				e.printStackTrace();
			}
			return message;
	    }
	 
	    private static String hashString(String message, String algorithm) throws UnsupportedEncodingException, NoSuchAlgorithmException{
	 
	       
	            MessageDigest digest;
				try {
					digest = MessageDigest.getInstance(algorithm);
	            byte[] hashedBytes = digest.digest(message.getBytes("UTF-8"));
				

	            return convertByteArrayToHexString(hashedBytes);
		        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
//					e.printStackTrace();
				}
				return algorithm;
	    }
	 
	    private static String convertByteArrayToHexString(byte[] arrayBytes) {
	        StringBuffer stringBuffer = new StringBuffer();
	        for (int i = 0; i < arrayBytes.length; i++) {
	            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
	                    .substring(1));
	        }
	        return stringBuffer.toString();
	    }
}
