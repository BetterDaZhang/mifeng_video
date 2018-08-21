package com.letv.autoapk.ui.login;

/**
 * 注册接口
 */
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.net.LoginInfo;
import com.letv.autoapk.base.net.StringDataRequest;

class RegisterDataRequest extends StringDataRequest {

	public RegisterDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/mobileRegister";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {
	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage,
			String content, Object[] outputData) throws JSONException {
		if (statusCode == 0 && !TextUtils.isEmpty(content)) {
			List<LoginInfo> infos = (List<LoginInfo>) outputData[0];
			JSONObject jsonObj = new JSONObject(content);
			JSONArray listJson = jsonObj.getJSONArray(LIST);

			LoginInfo loginInfo = new LoginInfo();
			JSONObject obj = listJson.getJSONObject(0);
			loginInfo.setUserId(obj.optString("userId"));
			loginInfo.setUserIcon(obj.optString("userIcon"));
			loginInfo.setToken(obj.optString("token"));
			loginInfo.setNickName(obj.optString("nickName"));
			loginInfo.setBirthday(obj.optLong("birthday"));
			loginInfo.setGender(obj.optInt("gender"));
//			loginInfo.setPhoneNumber(obj.optString("phoneNumber"));
			
			infos.add(loginInfo);
		}
		return statusCode;
	}

}
