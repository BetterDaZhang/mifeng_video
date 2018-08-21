package com.letv.autoapk.ui.mobilelive;

/**
 * 获取移动直播流状态/获取主播信息
 */
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.letv.autoapk.base.net.StringDataRequest;

public class GetMobileLiveStreamInfoDataRequest extends StringDataRequest {

	public GetMobileLiveStreamInfoDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/getLiveStreamInfo";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {
	}

	@Override
	protected int onParseResponse(int statusCode, final String alertMessage, String content, Object[] outputData)
			throws JSONException {
		if (statusCode == 0 && !TextUtils.isEmpty(content)) {
			JSONObject contentObject = new JSONObject(content);
			JSONObject data = contentObject.optJSONObject("data");
			MobileLiveStreamInfo info = (MobileLiveStreamInfo) outputData[0];
			info.liveUrl = data.optString("liveUrl");
			info.cause = data.optString("cause");
			info.status = data.optString("status");
			info.coverPic = data.optString("coverPic");
			info.detailType = data.optString("detailType");
			info.headPic = data.optString("headPic");
			info.isOnlie = data.optString("isOnlie");
			info.liveTitle = data.optString("liveTitle");
			info.userName = data.optString("userName");
			info.shareUrl = data.optString("shareUrl");
		}
		return statusCode;

	}

}
