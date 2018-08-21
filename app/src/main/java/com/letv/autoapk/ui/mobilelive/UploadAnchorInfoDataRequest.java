package com.letv.autoapk.ui.mobilelive;

/**
 * 上传用户身份证号、头像、姓名、身份证图片（post）
 */
import java.util.Map;

import org.json.JSONException;

import android.content.Context;

import com.letv.autoapk.base.net.StringDataRequest;
class UploadAnchorInfoDataRequest extends StringDataRequest {

    public UploadAnchorInfoDataRequest(Context context) {
        super(context);
    }

    @Override
    protected String getUrl() {
        return "/upLoadMobileBroadcastInfo";
    }

    @Override
    protected void onMakeRequestParam(Map<String, String> InputParam) {
    }

    @Override
    protected int onParseResponse(int statusCode, final String alertMessage, String content, Object[] outputData) throws JSONException {

        return statusCode;

    }

}
