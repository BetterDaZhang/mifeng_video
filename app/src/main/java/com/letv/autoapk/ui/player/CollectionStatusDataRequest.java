package com.letv.autoapk.ui.player;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;

import android.content.Context;

/**
 * 获取收藏状态接口
 */
public class CollectionStatusDataRequest extends StringDataRequest {

	public CollectionStatusDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/getCollectStatus";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	@Override
	protected String getToken() {
		return LoginInfoUtil.getToken(mContext);
	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData) throws JSONException {
		int ret = statusCode;
		if (ret == 0) {
			ArrayList<CollectionInfo> items = (ArrayList<CollectionInfo>) outputData[0];
			ret = parseData(content, items);
		}
		return statusCode;
	}

	private int parseData(String content, ArrayList<CollectionInfo> items) throws JSONException {
		// TODO 收藏接口请求返回json格式不对
		JSONObject rootJson = new JSONObject(content);
		JSONArray scJson = rootJson.optJSONArray("list");
		for (int i = 0; i < scJson.length(); i++) {
			JSONObject json = scJson.optJSONObject(i);
			CollectionInfo item = new CollectionInfo();
			item.hasCollect = json.optInt("hasCollect");
			item.videoId = json.optString("videoId");
			items.add(item);
		}
		return 0;
	}

}
