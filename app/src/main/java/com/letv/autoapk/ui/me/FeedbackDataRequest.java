package com.letv.autoapk.ui.me;

/**
 * 用户反馈的接口
 */
import java.util.Map;

import org.json.JSONException;

import com.letv.autoapk.base.net.StringDataRequest;

import android.content.Context;

class FeedbackDataRequest extends StringDataRequest {

	public FeedbackDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/reportSuggestion";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {
	}

	@Override
	protected int onParseResponse(int statusCode, final String alertMessage,
			String content, Object[] outputData) throws JSONException {
		
		return statusCode;
	}

}
