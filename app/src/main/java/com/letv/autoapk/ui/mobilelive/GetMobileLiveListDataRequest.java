package com.letv.autoapk.ui.mobilelive;

/**
 * 根据租户ID查询移动直播列表
 */
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;

class GetMobileLiveListDataRequest extends StringDataRequest {

	public GetMobileLiveListDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/getMobileBroadcastList";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {
	}

	@Override
	protected int onParseResponse(int statusCode, final String alertMessage, String content, Object[] outputData)
			throws JSONException {
		if (statusCode == 0 && !TextUtils.isEmpty(content)) {
			JSONObject contentObject = new JSONObject(content);
			JSONArray listArray = contentObject.optJSONArray("list");
			List<MobileLiveInfo> infos = (List<MobileLiveInfo>) outputData[0];
			infos.clear();
			PageInfo pageInfo = (PageInfo) outputData[1];
			MobileLiveInfo info;
			JSONObject obj;
			for (int i = 0; i < listArray.length(); i++) {
				info = new MobileLiveInfo();
				obj = listArray.getJSONObject(i);
				info.coverPic = obj.optString("coverPic");
				info.liveTitle = obj.optString("liveTitle");
				info.liveUrl = obj.optString("liveUrl");
				info.isOnlie = obj.optString("isOnlie");
				info.userName = obj.optString("userName");
				info.headPic = obj.optString("headPic");
				info.beginTime = obj.optString("beginTime");
				info.shareUrl = obj.optString("shareUrl");
				infos.add(info);
			}
			JSONObject pageObject = contentObject.optJSONObject("page");
			pageInfo.setPageIndex(pageObject.optInt("currentPage"));
			pageInfo.setTotalCount(pageObject.optInt("totalCount"));
			pageInfo.setTotalPage(pageObject.optInt("totalPage"));
		}

		return statusCode;

	}

}
