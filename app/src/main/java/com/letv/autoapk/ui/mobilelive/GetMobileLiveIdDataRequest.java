package com.letv.autoapk.ui.mobilelive;

/**
 * 建立直播流后回填直播ID数据（post）
 */
import java.util.Map;

import org.json.JSONException;

import android.content.Context;

import com.letv.autoapk.base.net.StringDataRequest;

class GetMobileLiveIdDataRequest extends StringDataRequest {

    public GetMobileLiveIdDataRequest(Context context) {
        super(context);
    }

    @Override
    protected String getUrl() {
        return "/getMobileBroadcastLiveId";
    }

    @Override
    protected void onMakeRequestParam(Map<String, String> InputParam) {
    }

    @Override
    protected int onParseResponse(int statusCode, final String alertMessage, String content, Object[] outputData) throws JSONException {

        return statusCode;

    }

}
