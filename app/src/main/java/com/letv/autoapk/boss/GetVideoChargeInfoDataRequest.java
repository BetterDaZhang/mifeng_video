package com.letv.autoapk.boss;

/**
 * 获取影片信息的接口,sdk鉴权失败，需要向移动代理接口请求视频详细信息
 */
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.common.net.RequestParam;

public class GetVideoChargeInfoDataRequest extends StringDataRequest {

	public GetVideoChargeInfoDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/videoChargeInfo1";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	@Override
	protected void addHeaders(RequestParam params) {
		super.addHeaders(params);
		String authtoken = LoginInfoUtil.getToken(mContext);
		params.addHeader("authtoken", authtoken);
	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData) throws JSONException {
		JSONObject jsonObj = new JSONObject(content);
		JSONObject data = jsonObj.optJSONObject("data");

		VideoChargeInfo videoChargeInfo = (VideoChargeInfo) outputData[0];

		videoChargeInfo.setAlbumName(data.optString("albumName"));
		videoChargeInfo.setChargeId(data.optString("chargeId"));
		videoChargeInfo.setAlbumId(data.optString("albumId"));
		videoChargeInfo.setCreateTime(data.optString("createTime"));
		videoChargeInfo.setStatus(data.optInt("status"));
		videoChargeInfo.setUpdateTime(data.optString("updateTime"));
		videoChargeInfo.setIsCharge(data.optInt("isCharge"));
		videoChargeInfo.setChargePlatform(data.optString("chargePlatform"));
		videoChargeInfo.setPic(data.optString("pic"));
		videoChargeInfo.setTryLookTime(data.optInt("tryLookTime"));
		videoChargeInfo.setTenantId(data.optString("tenantId"));
		videoChargeInfo.setPrice(data.optString("price"));
		videoChargeInfo.setVideoId(data.optString("videoId"));
		videoChargeInfo.setCatgory(data.optString("catgory"));
		videoChargeInfo.setCustomCatgoryId(data.optString("customCatgoryId"));
		videoChargeInfo.setFixedTime(data.optString("fixedTime"));
		videoChargeInfo.setValidTime(data.optInt("validTime"));
		videoChargeInfo.setChargeType(data.optInt("chargeType"));
		return statusCode;
	}

}
