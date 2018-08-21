package com.letv.autoapk.ui.login;

/**
 * 获取验证码的接口
 */
import java.util.Map;

import org.json.JSONException;

import com.letv.autoapk.base.net.StringDataRequest;

import android.content.Context;

class CheckCodeResetPwdDataRequest extends StringDataRequest {

	public CheckCodeResetPwdDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/passwordResetCheckCode";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage,
			String content, Object[] outputData) throws JSONException {
		return statusCode;
	}

}
