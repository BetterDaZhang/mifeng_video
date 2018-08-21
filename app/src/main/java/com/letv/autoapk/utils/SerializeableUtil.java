package com.letv.autoapk.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import com.letv.autoapk.common.utils.Logger;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

/**
 * 核心原理： 对象序列化 步骤 1.要保存的对象实现序列化Serializable
 * 2.将序列化的对象保存String（本文的做法是保存为byte数组在转为16进制的String类型保存起来） 3.将保存的String反序列化为对象
 */

public class SerializeableUtil {

	/**
	 * desc:保存对象
	 * 
	 * @param context
	 * @param key
	 * @param obj
	 *            要保存的对象，只能保存实现了serializable的对象 modified:
	 */

	public static void saveObject(Context context, String key, Object obj) {
		try {
			// 保存对象
			SharedPreferences.Editor editor = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).edit();
			// 先将序列化结果写到byte缓存中，其实就分配一个内存空间
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bos);
			// 将对象序列化写入byte缓存
			os.writeObject(obj);
			// 将序列化的数据转为16进制保存
			String bytesToHexString = bytesToHexString(bos.toByteArray());
			// 保存该16进制数组
			editor.putString(key, bytesToHexString).commit();
		} catch (IOException e) {
			Logger.log(e);
		}
	}

	/**
	 * desc:根据对象获取16进制字符串
	 * 
	 * @param context
	 * @param obj
	 */

	public static String getHexStringFromObject(Context context, Object obj) {
		try {
			// 先将序列化结果写到byte缓存中，其实就分配一个内存空间
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bos);
			// 将对象序列化写入byte缓存
			os.writeObject(obj);
			// 将序列化的数据转为16进制保存
			String bytesToHexString = bytesToHexString(bos.toByteArray());
			// 保存该16进制数组
			return bytesToHexString;
		} catch (IOException e) {
			Logger.log(e);
			return null;
		}
	}

	/**
	 * desc:将数组转为16进制
	 * 
	 * @param bArray
	 * @return modified:
	 */
	public static String bytesToHexString(byte[] bArray) {
		if (bArray == null) {
			return null;
		}
		if (bArray.length == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * desc:获取保存的Object对象
	 * 
	 * @param context
	 * @param key
	 * @return modified:
	 */
	public static Object readObject(Context context, String key) {
		try {
			SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
			if (sp.contains(key)) {
				String string = sp.getString(key, "");
				if (TextUtils.isEmpty(string)) {
					return null;
				} else {
					// 将16进制的数据转为数组，准备反序列化
					byte[] stringToBytes = hexStringToByte(string);
					ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
					ObjectInputStream is = new ObjectInputStream(bis);
					// 返回反序列化得到的对象
					Object readObject = is.readObject();
					return readObject;
				}
			}
		} catch (StreamCorruptedException e) {
			Logger.log(e);
		} catch (IOException e) {
			Logger.log(e);
		} catch (ClassNotFoundException e) {
			Logger.log(e);
		}
		// 所有异常返回null
		return null;

	}

	/**
	 * desc:根据对象对应的16进制字符串获取对象
	 * warning: 本方法多次使用得到的对象地址是固定的。
	 * @param context
	 * @param key
	 * @return modified:
	 */
	public static Object readObjectFromHexString(Context context, String hexString) {
		try {
			if (TextUtils.isEmpty(hexString)) {
				return null;
			} else {
				// 将16进制的数据转为数组，准备反序列化
				byte[] stringToBytes = hexStringToByte(hexString);
				ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
				ObjectInputStream is = new ObjectInputStream(bis);
				// 返回反序列化得到的对象
				Object readObject = is.readObject();
				return readObject;
			}
		} catch (StreamCorruptedException e) {
			Logger.log(e);
		} catch (IOException e) {
			Logger.log(e);
		} catch (ClassNotFoundException e) {
			Logger.log(e);
		}
		// 所有异常返回null
		return null;

	}

	/**
	 * desc:将16进制的数据转为数组
	 * 
	 * @param data
	 * @return modified:
	 */
	/*
	 * 把16进制字符串转换成字节数组 @param hex @return
	 */
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}
}
