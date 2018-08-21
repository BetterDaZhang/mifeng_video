package com.letv.autoapk.ui.player;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.letv.autoapk.base.net.StringDataRequest;

public class PriseStatusDateRequest extends StringDataRequest {

	public PriseStatusDateRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/getSupportStatus";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData)
			throws JSONException {
		if (statusCode == 0) {
			List<PlayPriseInfo> playPriseInfos = (List<PlayPriseInfo>) outputData[0];
			JSONObject json = new JSONObject(content);
			JSONArray jsonArray = json.getJSONArray(StringDataRequest.LIST);
			if (jsonArray == null) {
				return statusCode;
			}

			for (int i = 0; i < jsonArray.length(); i++) {
				PlayPriseInfo playPriseInfo = new PlayPriseInfo();
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				playPriseInfo.setVideoId(jsonObject.getString("videoId"));
				if ("0".equals(jsonObject.getString("hasSupport"))) {
					playPriseInfo.setHasSupport(false);
				}else{
					playPriseInfo.setHasSupport(true);
				}
				if ("0".equals(jsonObject.getString("hasCai"))) {
					playPriseInfo.setHasCai(false);
				}else{
					playPriseInfo.setHasCai(true);
				}
				playPriseInfo.setSupportDingNumeber(jsonObject.getString("supportDingNumber"));
				playPriseInfo.setSupportCaiNumber(jsonObject.getString("supportCaiNumber"));
				playPriseInfo.setAblumId(jsonObject.getString("albumId"));
				playPriseInfo.setLiveId(jsonObject.getString("liveId"));
				playPriseInfos.add(playPriseInfo);
			}
		}
		return statusCode;
	}

}
