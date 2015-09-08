package edu.ncku.security;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import android.util.Log;

public class Security {
	
	private static final String KEY = "SteveChenLibNCKU";

	public static String encrypt(String input) {
		String key = KEY;		
		byte[] crypted = null;
		
		try {
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			crypted = cipher.doFinal(input.getBytes());
		}  catch (Exception e) {
			PrintWriter pw = new PrintWriter(new StringWriter());
			e.printStackTrace(pw);
			Log.e("Security", pw.toString());
		}
		return new String(Base64.encodeBase64(crypted));
	}

	public static String decrypt(String input) {
		String key = KEY;
		byte[] output = null;
		
		try {
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			output = cipher.doFinal(Base64.decodeBase64(input));
		} catch (Exception e) {
			PrintWriter pw = new PrintWriter(new StringWriter());
			e.printStackTrace(pw);
			Log.e("Security", pw.toString());
		}
		return new String(output);
	}
	
}
