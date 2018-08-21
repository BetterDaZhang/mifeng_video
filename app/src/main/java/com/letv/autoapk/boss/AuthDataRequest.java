package com.letv.autoapk.boss;

/**
 * 鉴权
 */
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.common.net.RequestParam;

public class AuthDataRequest extends StringDataRequest {

	public AuthDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/getAuthenInfo";
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
		JSONObject jsonObj = new JSONObject(content);
		JSONObject data = jsonObj.optJSONObject("data");
		AuthInfo authInfo = (AuthInfo) outputData[0];
		authInfo.setStatus(data.optInt("status"));
		authInfo.setTryLookTime(data.optInt("tryLookTime"));
		authInfo.setTokenUserId(data.optString("tokenUserId"));
		authInfo.setToken(data.optString("token"));
		if (data.optInt("status") == 0) {
			return 1;
		}
		return statusCode;
	}

}
