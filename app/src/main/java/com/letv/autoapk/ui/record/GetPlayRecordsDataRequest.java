package com.letv.autoapk.ui.record;

/**
 *获取播放记录接口
 */
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.common.net.RequestParam;
import com.letv.autoapk.dao.PlayRecordInfo;

import android.content.Context;

class GetPlayRecordsDataRequest extends StringDataRequest {

	public GetPlayRecordsDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/getPlayRecords";
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
		JSONObject contentObject = new JSONObject(content);
		JSONArray listArray = contentObject.optJSONArray("list");
		List<PlayRecordInfo> infos = (List<PlayRecordInfo>) outputData[0];
		PageInfo pageInfo = (PageInfo) outputData[1];
		PlayRecordInfo info;
		for (int i = 0; i < listArray.length(); i++) {
			info = new PlayRecordInfo();
			JSONObject obj = listArray.getJSONObject(i);
			info.setPlayRecordId(obj.optString("playRecordId"));
			info.setVideoId(obj.optString("videoId"));
			info.setAlbumId(obj.optString("ablumId"));
			info.setVideoTitle(obj.optString("videoTitle"));
			info.setVideoImage(obj.optString("videoImage"));
			info.setLastOpenTime(obj.optLong("lastOpenTime"));
			info.setLastPlayTime(obj.optLong("lastPlayTime"));
			info.setNextLinkUrl(obj.optString("nextLinkUrl"));
			infos.add(info);
		}
		JSONObject pageObject = contentObject.optJSONObject("page");
		pageInfo.setPageIndex(pageObject.optInt("currentPage"));
		pageInfo.setTotalCount(pageObject.optInt("totalCount"));
		pageInfo.setTotalPage(pageObject.optInt("totalPage"));
		return statusCode;
	}

}
