package com.letv.autoapk.ui.mobilelive;

/**
 * 主播停止直播通知接口
 */
import java.util.Map;

import org.json.JSONException;

import android.content.Context;

import com.letv.autoapk.base.net.StringDataRequest;
public class QuitLiveNotifyDataRequest extends StringDataRequest {

    public QuitLiveNotifyDataRequest(Context context) {
        super(context);
    }

    @Override
    protected String getUrl() {
        return "/quitLiveNotify";
    }

    @Override
    protected void onMakeRequestParam(Map<String, String> InputParam) {
    }

    @Override
    protected int onParseResponse(int statusCode, final String alertMessage, String content, Object[] outputData) throws JSONException {

        return statusCode;

    }

}
