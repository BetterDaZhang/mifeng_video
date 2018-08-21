package com.letv.autoapk.ui.login;

/**
 * 获取验证码的接口
 */
import java.util.Map;

import org.json.JSONException;

import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.context.MyApplication;

import android.content.Context;

class CheckCodeRegisterDataRequest extends StringDataRequest {

	Context context;
	
	public CheckCodeRegisterDataRequest(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected String getUrl() {
		return "/sendMobileCheckCode";
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
