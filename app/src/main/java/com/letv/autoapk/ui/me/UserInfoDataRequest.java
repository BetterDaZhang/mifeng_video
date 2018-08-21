package com.letv.autoapk.ui.me;

/**
 * 获取用户信息的接口
 */
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.letv.autoapk.base.net.LoginInfo;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.boss.VipInfo;
import com.letv.autoapk.common.net.RequestParam;
import com.letv.autoapk.context.MyApplication;

import android.content.Context;
import android.text.TextUtils;

class UserInfoDataRequest extends StringDataRequest {

	public UserInfoDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/userInfo";
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
		if (statusCode == 0 && !TextUtils.isEmpty(content)) {
			List<LoginInfo> infos = (List<LoginInfo>) outputData[0];
			List<VipInfo> vipInfos = (List<VipInfo>) outputData[1];
			JSONObject jsonObj = new JSONObject(content);

			JSONArray listJson = jsonObj.getJSONArray(LIST);
			JSONArray vipDataList = jsonObj.optJSONArray("vipData");
			LoginInfo loginInfo = new LoginInfo();
			JSONObject obj = listJson.getJSONObject(0);

			loginInfo.setUserId(obj.optString("userId"));
			loginInfo.setNickName(obj.optString("nickName"));
			loginInfo.setUserIcon(obj.optString("userIcon"));
			loginInfo.setBirthday(obj.optLong("birthday"));
			loginInfo.setGender(obj.optInt("gender"));
			loginInfo.setToken(obj.optString("token"));
			loginInfo.setPhoneNumber(obj.optString("phoneNumber"));
			loginInfo.setState(obj.optInt("state"));
			loginInfo.setIsVip(obj.optInt("isVip",1));
			if (vipDataList != null) {
				VipInfo vipInfo;
				for (int i = 0; i < vipDataList.length(); i++) {
					vipInfo = new VipInfo();
					JSONObject vipData = (JSONObject) vipDataList.get(i);
					vipInfo.setEndTime(vipData.optString("endTime"));
					vipInfo.setLevel(vipData.optInt("level"));
					vipInfo.setId(vipData.optString("id"));
					vipInfo.setLevelName(vipData.optString("levelName"));
					vipInfos.add(vipInfo);
				}
			}
			infos.add(loginInfo);
		}
		return statusCode;

	}

}
