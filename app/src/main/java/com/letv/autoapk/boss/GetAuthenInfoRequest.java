package com.letv.autoapk.boss;

import java.util.Map;

import org.json.JSONException;

import android.content.Context;

import com.letv.autoapk.base.net.StringDataRequest;

public class GetAuthenInfoRequest extends StringDataRequest {

	public GetAuthenInfoRequest(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getUrl() {
		// TODO Auto-generated method stub
		return "/getAuthenInfo";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {
		// TODO Auto-generated method stub

	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage,
			String content, Object[] outputData) throws JSONException {
		// TODO Auto-generated method stub
		return 0;
	}

}
