package model;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
	public String getSHA256(String inString) {
		String outString = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.reset();
			digest.update(inString.getBytes("utf8"));
			outString = String.format("%064x", new BigInteger(1, digest.digest()));
		}catch(NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return outString;
	}
	
	public String getSHA512(String inString) {
		String outString = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			digest.reset();
			digest.update(inString.getBytes("utf8"));
			outString = String.format("%0128x", new BigInteger(1, digest.digest()));
		}catch(NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return outString;
	}
}