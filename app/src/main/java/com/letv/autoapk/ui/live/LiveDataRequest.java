package com.letv.autoapk.ui.live;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.letv.autoapk.base.net.StringDataRequest;

class LiveDataRequest extends StringDataRequest {
	private Context context;

	public LiveDataRequest(Context ctx) {
		super(ctx);
		this.context = ctx;
	}

	@Override
	protected String getUrl() {
		return "/getLiveList2";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData) throws JSONException {
		List<LiveVideoInfo> liveVideos = (List<LiveVideoInfo>) outputData[0];
		LiveVideoInfo liveVideo = null;
		if (statusCode == 0) {
			liveVideos.clear();
			JSONObject json = new JSONObject(content);
			JSONArray jsonArray = (JSONArray) json.get("list");
			JSONObject obj = null;
			for (int i = 0; i < jsonArray.length(); ++i) {
				liveVideo = new LiveVideoInfo();
				obj = (JSONObject) jsonArray.get(i);
				liveVideo.setmLiveVideoId(obj.optString("channelId"));
				liveVideo.setmLiveVideoName(obj.optString("channelName"));
				liveVideo.setmLiveVideoDetailId(obj.optString("channelDetailId"));
				liveVideo.setmLiveVideoIcon(obj.optString("channelIcon"));
				liveVideo.setmLiveDetailType(obj.optInt("channelDetailType"));
				liveVideos.add(liveVideo);
			}
		}
		return statusCode;
	}

}
