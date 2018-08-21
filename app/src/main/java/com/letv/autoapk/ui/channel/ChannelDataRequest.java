package com.letv.autoapk.ui.channel;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.letv.autoapk.base.net.StringDataRequest;

class ChannelDataRequest extends StringDataRequest {
	private Context context;

	public ChannelDataRequest(Context ctx) {
		super(ctx);
		this.context = ctx;
	}

	@Override
	protected String getUrl() {
//		return "/getChannelList2";
		return "/channel/androidChannelIndex";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData) throws JSONException {
		List<ChannelVideoInfo> channels = (List<ChannelVideoInfo>) outputData[0];
		ChannelVideoInfo channel = null;
		if (statusCode == 0) {
			channels.clear();
			JSONObject json = new JSONObject(content);
			JSONArray jsonArray = (JSONArray) json.get("list");
			JSONObject obj = null;
			for (int i = 0; i < jsonArray.length(); ++i) {
				channel = new ChannelVideoInfo();
				obj = (JSONObject) jsonArray.get(i);
				channel.setChannelId(obj.optString("channelId"));
				channel.setChannelName(obj.optString("channelName"));
				channel.setChannelDetailName(obj.optString("channelDatailName"));
				channel.setmPageId(obj.optString("channelDetailId"));
				channel.setChannelIcon(obj.optString("channelIcon"));
				channel.setChannelImageUrl(obj.optString("channelImageUrl"));
				channel.setChannelDetailType(obj.optInt("channelDetailType"));
				channels.add(channel);
			}
		}
		return statusCode;
	}

}
