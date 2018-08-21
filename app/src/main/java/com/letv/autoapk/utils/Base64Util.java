package com.letv.autoapk.utils;

import org.apache.commons.codec.binary.Base64;

public class Base64Util {
	public static String encode(String s) {
		return encode(s, "utf-8");
	}
	
	public static String decode(String s) {
		return decode(s, "utf-8");
	}
	
	public static String encode(String s, String charset) {
		try {
			byte[] bytes = s.getBytes(charset);
			bytes = Base64.encodeBase64(bytes);
			return new String(bytes, charset);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String decode(String s, String charset) {
		try {
			byte[] bytes = s.getBytes(charset);
			bytes = Base64.decodeBase64(bytes);
			return new String(bytes, charset);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
