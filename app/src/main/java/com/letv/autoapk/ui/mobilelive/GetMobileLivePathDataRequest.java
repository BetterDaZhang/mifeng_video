package com.letv.autoapk.ui.mobilelive;

/**
 * 根据租户ID和用户id查询推流地址
 */
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.letv.autoapk.base.net.StringDataRequest;

class GetMobileLivePathDataRequest extends StringDataRequest {

	public GetMobileLivePathDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/getMobileBroadcastPath";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {
	}

	@Override
	protected int onParseResponse(int statusCode, final String alertMessage, String content, Object[] outputData)
			throws JSONException {
		if (statusCode == 0 && !TextUtils.isEmpty(content)) {
			MobileLivePathInfo info = (MobileLivePathInfo) outputData[0];
			JSONObject jsonObj = new JSONObject(content);

			JSONObject data = jsonObj.optJSONObject("data");

			info.path = data.optString("path");
			info.signedKey = data.optString("signedKey");
			info.status = data.optString("status");
			info.cause = data.optString("cause");
			info.shareUrl = data.optString("shareUrl");
		}
		return statusCode;
	}

}
