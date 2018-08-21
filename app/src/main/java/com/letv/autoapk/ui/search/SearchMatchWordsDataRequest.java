package com.letv.autoapk.ui.search;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.letv.autoapk.base.net.StringDataRequest;

/**
 * 搜索联想词接口
 * 
 * @author wangzhen5
 * 
 */
class SearchMatchWordsDataRequest extends StringDataRequest {

	public SearchMatchWordsDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/getMatchWords";
	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData) throws JSONException {
		if (statusCode == 0) {
			List<String> matchWords = (List<String>) outputData[0];
			JSONObject jsonObj = new JSONObject(content);
			String matchWord;
			String matchs = jsonObj.optString(LIST);
			String[] arrays = matchs.split(",");
			matchWords.clear();
			for (int i = 0; i < arrays.length; i++) {
				matchWord = arrays[i];
				matchWords.add(matchWord);
			}
			matchWords.clear();
		}
		return statusCode;
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

}
