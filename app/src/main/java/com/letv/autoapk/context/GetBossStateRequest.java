package com.letv.autoapk.context;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.common.utils.Logger;

public class GetBossStateRequest extends StringDataRequest {

	public GetBossStateRequest(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getUrl() {
		// TODO Auto-generated method stub
		return "/getTenantBossStatus";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {
		// TODO Auto-generated method stub

	}
    private int bossstate = 0;
    public int getBossState(){
    	return bossstate;
    }
	@Override
	protected int onParseResponse(int statusCode, String alertMessage,
			String content, Object[] outputData) throws JSONException {
		try {
			if(statusCode==0){
				JSONObject jsonObj = new JSONObject(content);
				bossstate = jsonObj.getInt("statusCode");
			}
		} catch (Exception e) {
			Logger.log(e);
		}
		return statusCode;
	}

}
