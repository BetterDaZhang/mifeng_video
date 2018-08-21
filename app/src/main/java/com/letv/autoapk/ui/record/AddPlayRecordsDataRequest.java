package com.letv.autoapk.ui.record;

import java.util.Map;

import org.json.JSONException;

import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.common.net.RequestParam;

import android.content.Context;

class AddPlayRecordsDataRequest extends StringDataRequest {

	public AddPlayRecordsDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/addPlayRecords";
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
