package com.letv.autoapk.boss;

/**
 * 兑换码的接口
 */
import java.util.Map;

import org.json.JSONException;

import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.common.net.RequestParam;

import android.content.Context;

class CdkeyDataRequest extends StringDataRequest {

	public CdkeyDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/exchangeVip";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	@Override
	protected void addHeaders(RequestParam params) {
		super.addHeaders(params);
		String authtoken = LoginInfoUtil.getToken(mContext);
		params.addHeader("authtoken", authtoken);
	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData) throws JSONException {
		return statusCode;
	}

}
