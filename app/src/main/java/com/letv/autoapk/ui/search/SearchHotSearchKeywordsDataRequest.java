package com.letv.autoapk.ui.search;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.letv.autoapk.base.net.StringDataRequest;

class SearchHotSearchKeywordsDataRequest extends StringDataRequest {

	public SearchHotSearchKeywordsDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/hotSearchKeywords";
	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData) throws JSONException {
		if (statusCode == 0) {
			List<String> hotSearchKeywords = (List<String>) outputData[0];
			JSONObject jsonObj = new JSONObject(content);
			JSONArray array = jsonObj.getJSONArray(LIST);
			String hotSearchKeyword;
			for (int i = 0; i < array.length(); i++) {
				hotSearchKeyword = array.getString(i);
				hotSearchKeywords.add(hotSearchKeyword);
			}
		}
		return statusCode;
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

}
