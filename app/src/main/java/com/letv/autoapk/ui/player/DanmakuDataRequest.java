package com.letv.autoapk.ui.player;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.letv.autoapk.base.net.StringDataRequest;

import android.content.Context;
import master.flame.danmaku.danmaku.parser.android.JSONSource;

/**
 * 获取收藏状态接口
 */
public class DanmakuDataRequest extends StringDataRequest {

	public DanmakuDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/getVodBarrage";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	JSONSource getResult(){
		return result;
	}
	
	private JSONSource result;
	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData) throws JSONException {
		if (statusCode == 0) {
			JSONObject jsonObj = new JSONObject(content);
			result = new JSONSource(jsonObj.getJSONArray(LIST));
		}
		return statusCode;
	}


}
