package com.letv.autoapk.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Base64;

import android.util.Log;

public class DESUtils {
	/**
	 * 
	 * 数据加密，算法（DES）
	 *
	 * 
	 * 
	 * @param data
	 * 
	 *            要进行加密的数据
	 * 
	 * @return 加密后的数据
	 * 
	 */
	private static final String DES_KEY = "mysaasismyplaceyoudoyoucan";

	public static String encryptBasedDes(String data) {

		String encryptedData = null;
		try {
			byte[] DES_KEY_BYTES = DES_KEY.getBytes("UTF-8");

			// DES算法要求有一个可信任的随机数源
			DESedeKeySpec deskey = new DESedeKeySpec(DES_KEY_BYTES);

			// 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
			SecretKey key = keyFactory.generateSecret(deskey);

			// 加密对象
			Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);

			// 加密，并把字节数组编码成字符串
			byte[] encodeBytes = new Base64().encodeBase64(cipher.doFinal(data.getBytes()));
			encryptedData = new String(encodeBytes, "utf-8");
		} catch (Exception e) {
			Log.i("", "加密错误，错误信息：" + e);
			throw new RuntimeException("加密错误，错误信息：", e);
		}
		return encryptedData;
	}

}
