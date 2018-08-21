package com.letv.autoapk.update;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.context.MyApplication;

public class GetUpgradeRequest extends StringDataRequest {

	public GetUpgradeRequest(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getUrl() {
		// TODO Auto-generated method stub
		return "/getUpgrade";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {
		// TODO Auto-generated method stub
		mInputParam = new HashMap<String, String>();
		mInputParam.put("type", "android");
		mInputParam.put("tenantId",  MyApplication.getInstance().getTenantId());
	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage,
			String content, Object[] outputData) throws JSONException {
		if (statusCode == 0 && !TextUtils.isEmpty(content)) {
			JSONObject result = new JSONObject(content);
			JSONArray list = result.getJSONArray("list");
			if(list==null || list.length()<=0)
				return statusCode;
			JSONObject object = list.getJSONObject(0);
			VersionInfo info = new VersionInfo();
			info.flag = object.getInt("flag");
			info.version = object.getString("latestVersion");
			info.url = object.getString("latestUrl");
			outputData[0] = info;
		}
		return statusCode;
	}

}
