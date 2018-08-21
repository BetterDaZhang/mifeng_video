package com.letv.autoapk.base.net;

import android.content.Context;
import com.letv.autoapk.base.net.LoginInfo;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.utils.SerializeableUtil;

public class LoginInfoUtil {
	public final static String USER_INFO = "userinfo";

	public static LoginInfo getLoginInfo(Context context) {
		return (LoginInfo) SerializeableUtil.readObject(context.getApplicationContext(), USER_INFO);
	}

	/** 用户登录则保存userId，否则返回空 */
	public static String getUserId(Context context) {
		LoginInfo loginInfo = getLoginInfo(context);
		if (loginInfo == null) {
			return "";
		}
		return loginInfo.getUserId();
	}

	/** 是否是会员 */
	public static boolean isVip(Context context) {
		LoginInfo loginInfo = getLoginInfo(context);
		if (loginInfo != null && loginInfo.getIsVip() == 0) {
			return true;
		}
		return false;
	}
	/** 是否是会员 */
	public static void setIsVip(int isVip,Context context) {
		LoginInfo loginInfo = getLoginInfo(context);
		if (loginInfo != null) {
			loginInfo.setIsVip(0);
		}
		SerializeableUtil.saveObject(context, USER_INFO, loginInfo);
	}

	public static String getUserName(Context context) {
		LoginInfo loginInfo = getLoginInfo(context);
		if (loginInfo == null) {
			return "";
		}
		return loginInfo.getNickName();
	}

	/** 用户登录则保存userId，否则返回空 */
	public static String getToken(Context context) {
		LoginInfo loginInfo = getLoginInfo(context);
		if (loginInfo == null) {
			return "";
		}
		return loginInfo.getToken();
	}

	public static String getUserInfoPhoto(Context context) {
		String headPohto = MyApplication.getInstance().getString("iconUrl" + LoginInfoUtil.getUserId(context));
		if (headPohto != null && !headPohto.isEmpty()) {
			headPohto = headPohto + "?time=" + System.currentTimeMillis();
		}
		return headPohto;
	}

	public static boolean isPasswordValid(String target) {
		if (target == null) {
			return false;
		}
		char[] chars = target.toCharArray();
		int letter = 0;
		int digit = 0;
		int other = 0;
		for (char ch : chars) {
			if (Character.isLetter(ch)) {
				letter = 1;
			} else if (Character.isDigit(ch)) {
				digit = 1;
			} else {
				other = 1;
			}
		}

		return letter == 1 && (other + digit > 0) && chars.length >= 6 && chars.length <= 20;
	}
}
