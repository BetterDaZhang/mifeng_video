package com.letv.autoapk.ui.player;

import java.util.Map;

import org.json.JSONException;

import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;

import android.content.Context;

/**
 * 获取收藏状态接口
 */
public class DanmakuSendRequest extends StringDataRequest {

	public DanmakuSendRequest(Context context) {
		super(context);
	}
	
	@Override
	protected String getUrl() {
		return "/sendVodBarrage";
	}
	@Override
	protected String getToken() {
		return LoginInfoUtil.getToken(mContext);
	}
	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData) throws JSONException {
		return statusCode;
	}


}
