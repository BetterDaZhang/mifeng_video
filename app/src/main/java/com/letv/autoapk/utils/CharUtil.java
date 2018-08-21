package com.letv.autoapk.utils;

/**
 * 校验字符串
 * 
 * @author wangzhen5
 * 
 */
public class CharUtil {
	/**
	 * 字符串是否合法
	 * 
	 * @param target
	 * @return 1不合法  2长度不符合要求
	 */
	public int isValid(String target) {
		if (target == null) {
			return 0;
		}
		char[] chars = target.toCharArray();
		int chinese = 0;
		int letter = 0;
		int digit = 0;
		int underScore = 0;
		int len = 0;
		for (char ch : chars) {
			if (CharUtil.isChineseStrict(ch)) {
				chinese = 2;
				len += 2;
			} else if (Character.isLetter(ch)) {
				letter = 2;
				len += 1;
			} else if (Character.isDigit(ch)) {
				digit = 1;
				len += 1;
			} else if (ch == '_') {
				underScore = 1;
				len += 1;
			} else {
				return 0;
			}
		}
		if (chinese + letter + digit + underScore <= 1) {
			return 0;
		} else if (!(len >= 4 && len <= 32)) {
			return 1;
		}
		return 2;
	}

	/**
	 * 只包括汉字，不包括中文符号，或者韩文、日文等
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isChineseStrict(char ch) {
		return ch >= '\u4E00' && ch <= '\u9FBF';
	}
}
