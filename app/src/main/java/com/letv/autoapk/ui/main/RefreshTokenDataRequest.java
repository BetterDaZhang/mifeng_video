package com.letv.autoapk.ui.main;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.letv.autoapk.base.net.LoginInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.common.net.RequestParam;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.utils.SerializeableUtil;

import android.content.Context;
import android.text.TextUtils;

/**
 * token刷新接口<br>
 * token刷新逻辑，每次进入app刷新token<br>
 * 因为用户在修改密码等情况下，token会失效，所以请求该接口后需要提示用户重新登录
 */
class RefreshTokenDataRequest extends StringDataRequest {

	public RefreshTokenDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/refreshToken";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {
	}

	@Override
	protected void addHeaders(RequestParam params) {
		super.addHeaders(params);
		params.addHeader("clientId", MyApplication.getInstance().getClientId());
	}

	@Override
	protected int onParseResponse(int statusCode, final String alertMessage, String content, Object[] outputData) throws JSONException {
		if (!TextUtils.isEmpty(content)) {
			JSONObject jsonObject = new JSONObject(content);
			jsonObject.optString("expires_in");
			LoginInfo info = (LoginInfo) SerializeableUtil.readObject(mContext, MyApplication.USER_INFO);
			if (info != null) {
				info.setToken(jsonObject.optString("access_token"));
				SerializeableUtil.saveObject(mContext, MyApplication.USER_INFO, info);
			}
			MyApplication.getInstance().putString(MyApplication.REFRESH_TOKEN, jsonObject.optString("refresh_token"));
		}
		return statusCode;
	}

}
