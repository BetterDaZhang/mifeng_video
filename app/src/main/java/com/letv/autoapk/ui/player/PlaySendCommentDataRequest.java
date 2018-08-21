package com.letv.autoapk.ui.player;

import java.util.Map;

import org.json.JSONException;

import android.content.Context;

import com.letv.autoapk.base.net.StringDataRequest;

public class PlaySendCommentDataRequest extends StringDataRequest {

	public PlaySendCommentDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/sendComment";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData)
			throws JSONException {
		return statusCode;
	}
}
