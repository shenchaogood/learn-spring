package sc.learn.common.util.security;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 * 基础加密组件
 * 
 * @version 1.0
 * @since 1.0
 */
public abstract class Coder {
	public static final String KEY_SHA = "SHA";
	public static final String KEY_MD5 = "MD5";

	/**
	 * BASE64解密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptBASE64(String key){
		return Base64.decodeBase64(key);
	}

	/**
	 * BASE64加密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptBASE64(byte[] key){
		return Base64.encodeBase64String(key);
	}

	/**
	 * MD5加密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptMD5(byte[] data){
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance(KEY_MD5);
			md5.update(data);
			return md5.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

	}
	
	
	/**
	 * MD5加密
	 * 
	 * @param data
	 * @return base64
	 * @throws Exception
	 */
	public static String encryptBase64MD5(String data) {
		try {
			MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
			md5.update(data.getBytes());
			return encryptBASE64(md5.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String encryptHexMD5(String data) {
		try {
			MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
			md5.update(data.getBytes());
			return Hex.encodeHexString(md5.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * SHA加密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptSHA(byte[] data) {
		MessageDigest sha;
		try {
			sha = MessageDigest.getInstance(KEY_SHA);
			sha.update(data);
			return sha.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		

	}

	/**
	 * 初始化HMAC密钥
	 * MAC算法可选以下多种算法
	 * <pre>
	 * HmacMD5  
	 * HmacSHA1  
	 * HmacSHA256  
	 * HmacSHA384  
	 * HmacSHA512
	 * </pre>
	 * @return
	 * @throws NoSuchAlgorithmException 
	 * @throws Exception
	 */
	public static String initMacKey(String keyMac) throws NoSuchAlgorithmException{
		
		KeyGenerator keyGenerator = KeyGenerator.getInstance(keyMac);
		SecretKey secretKey = keyGenerator.generateKey();
		return encryptBASE64(secretKey.getEncoded());
	}

	/**
	 * HMAC加密
	 * <pre>
	 * HmacMD5  
	 * HmacSHA1  
	 * HmacSHA256  
	 * HmacSHA384  
	 * HmacSHA512
	 * </pre>
	 * @param data
	 * @param key
	 * @return
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws Exception
	 */
	public static String encryptHexHMAC(byte[] data, String key ,String keyMac) throws NoSuchAlgorithmException, InvalidKeyException{
		SecretKey secretKey = new SecretKeySpec(decryptBASE64(key), keyMac);
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		return Hex.encodeHexString(mac.doFinal(data));
	}
}