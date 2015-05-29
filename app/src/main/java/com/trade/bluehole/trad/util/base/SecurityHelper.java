package com.trade.bluehole.trad.util.base;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class SecurityHelper {
	//protected final static Log logger = LogFactory.getLog(SecurityHelper.class);
	private final static int ITERATIONS = 20;

	public static String encrypt(String key, String plainText) throws Exception {
		// String encryptTxt = "";
		try {
			byte[] salt = new byte[8];
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(key.getBytes());
			byte[] digest = md.digest();
			for (int i = 0; i < 8; i++) {
				salt[i] = digest[i];
			}
			PBEKeySpec pbeKeySpec = new PBEKeySpec(key.toCharArray());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey skey = keyFactory.generateSecret(pbeKeySpec);
			PBEParameterSpec paramSpec = new PBEParameterSpec(salt, ITERATIONS);
			Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
			cipher.init(Cipher.ENCRYPT_MODE, skey, paramSpec);
			byte[] cipherText = cipher.doFinal(plainText.getBytes());
			String saltString = new String(Base64.encode(salt));
			String ciphertextString = new String(Base64.encode(cipherText));
			return saltString + ciphertextString;
		} catch (Exception e) {
			throw new Exception("Encrypt Text Error:" + e.getMessage(), e);
		}
	}

	public static String decrypt(String key, String encryptTxt) throws Exception {
		int saltLength = 12;
		try {
			String salt = encryptTxt.substring(0, saltLength);
			String ciphertext = encryptTxt.substring(saltLength, encryptTxt.length());
			byte[] saltarray = Base64.decode(salt.getBytes());
			byte[] ciphertextArray = Base64.decode(ciphertext.getBytes());
			PBEKeySpec keySpec = new PBEKeySpec(key.toCharArray());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey skey = keyFactory.generateSecret(keySpec);
			PBEParameterSpec paramSpec = new PBEParameterSpec(saltarray, ITERATIONS);
			Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
			cipher.init(Cipher.DECRYPT_MODE, skey, paramSpec);
			byte[] plaintextArray = cipher.doFinal(ciphertextArray);
			return new String(plaintextArray);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
}
