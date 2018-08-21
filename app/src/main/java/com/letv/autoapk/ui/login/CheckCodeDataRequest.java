package com.letv.autoapk.ui.login;

/**
 * 验证验证码的接口
 */
import java.util.Map;

import org.json.JSONException;

import com.letv.autoapk.base.net.StringDataRequest;

import android.content.Context;

class CheckCodeDataRequest extends StringDataRequest {

	public CheckCodeDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/checkVCode";
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
