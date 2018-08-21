package com.letv.autoapk.ui.login;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.letv.autoapk.base.net.LoginInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.boss.VipInfo;
import com.letv.autoapk.context.MyApplication;

/**
 * 三方登录接口
 * 
 * @author wangzhen5
 * 
 */
public class UserTPLoginDataRequest extends StringDataRequest {

	public UserTPLoginDataRequest(Context context) {
		super(context);
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> inputParam) {

	}

	@Override
	protected String getUrl() {
		return "/thirdPlatLogin";
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
			VipInfo vipInfo;
			if(vipDataList!=null){
				for (int i = 0; i < vipDataList.length(); i++) {
					vipInfo = new VipInfo();
					JSONObject vipData = (JSONObject) vipDataList.get(i);
					vipInfo.setEndTime(vipData.optString("endTime"));
					vipInfo.setLevel(vipData.optInt("level"));
					vipInfo.setId(vipData.optString("id"));
					vipInfos.add(vipInfo);
				}
				
			}
			MyApplication.getInstance().putString(MyApplication.REFRESH_TOKEN, obj.optString("refreshToken"));
			infos.add(loginInfo);
		}
		return statusCode;
	}

}
