package com.letv.autoapk.ui.login;

/**
 * 重置密码验证接口
 */
import java.util.Map;

import org.json.JSONException;

import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.context.MyApplication;

import android.content.Context;

class ResetPwdDataRequest extends StringDataRequest {

	Context context;
	
	public ResetPwdDataRequest(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected String getUrl() {
		return "/passwordReset";
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
