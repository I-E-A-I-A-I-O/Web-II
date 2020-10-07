package controllers;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
	public String HashPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(password.getBytes(StandardCharsets.UTF_8));
			byte[] digest = md.digest();
			 String hex = String.format("%064x", new BigInteger(1, digest));
			return hex;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "ERROR: No such algorithm!";
		}
	}
}
