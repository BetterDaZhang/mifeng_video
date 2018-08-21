package com.letv.autoapk.ui.mobilelive;

/**
 * 是否提交过身份认证及验证是否通过
 */
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.letv.autoapk.base.net.StringDataRequest;

class IsAnchorAuthenticationDataRequest extends StringDataRequest {

    public IsAnchorAuthenticationDataRequest(Context context) {
        super(context);
    }

    @Override
    protected String getUrl() {
        return "/isPassAuthentication";
    }

    @Override
    protected void onMakeRequestParam(Map<String, String> InputParam) {
    }

    @Override
    protected int onParseResponse(int statusCode, final String alertMessage, String content, Object[] outputData) throws JSONException {

        JSONObject contentObject = new JSONObject(content);
        JSONObject data = contentObject.optJSONObject("data");
        AnchorInfo info = (AnchorInfo) outputData[0];
        info.isComplete = data.optString("isComplete");
        info.cause = data.optString("cause");
        info.isPassAuthentication = data.optString("isPassAuthentication");
        return statusCode;

    }

}
