package com.letv.autoapk.ui.live;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.utils.StringEscapeUtils;

class LiveVideoListRequest extends StringDataRequest{
	private Context context;

	public LiveVideoListRequest(Context ctx) {
		super(ctx);
		this.context = ctx;
	}

	@Override
	protected String getUrl() {
		return "/liveBlockList2";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {
		
	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData) throws JSONException {
		if (statusCode == 0) {
			List<DisplayVideoInfo> videoInfos = (List<DisplayVideoInfo>) outputData[0];
			videoInfos.clear();
			PageInfo pageInfo = (PageInfo) outputData[1];
			JSONObject jsonObj = new JSONObject(content);
			JSONArray videoArray = jsonObj.getJSONArray(LIST);
				for (int j = 0; j < videoArray.length(); j++) {
					JSONObject videoJson = videoArray.getJSONObject(j).getJSONObject("video");
					DisplayVideoInfo videoInfo = new DisplayVideoInfo();
					videoInfo.setVideoId(videoJson.optString("videoId"));
					videoInfo.setShareUrl(videoJson.optString("videoShareUrl"));
					videoInfo.setVideoTitle(StringEscapeUtils.unescapeHtml4(videoJson.optString("videoTitle")));
					videoInfo.setVideoDesc(StringEscapeUtils.unescapeHtml4(videoJson.optString("videoDesc")));
					videoInfo.setImageUrl(videoJson.optString("videoImage"));
					videoInfo.setAlbumId(videoJson.optString("ablumId"));
					videoInfo.setDetailType(videoJson.optInt("detailType"));
					videoInfo.setSuperScripType(videoJson.optInt("superscriptType"));
//					videoInfo.setSuperScripColor(videoJson.optInt("superscriptColor"));
					videoInfo.setSuperScripName(videoJson.optString("superscriptName"));
					videoInfo.setSubscriptType(videoJson.optInt("subscriptType"));
					videoInfo.setSubscriptName(videoJson.optString("subscriptName"));
					videoInfo.setChanncelId(videoJson.optString("channelId"));
					videoInfo.setId(videoJson.optString("id"));
					videoInfos.add(videoInfo);
				}
				JSONObject page = jsonObj.optJSONObject("page");
				if (page != null) {
					pageInfo.setPageIndex(page.optInt("currentPage"));
					pageInfo.setTotalCount(page.optInt("totalCount"));
					pageInfo.setTotalPage(page.optInt("totalPage"));
				}
				
			}

		return statusCode;
	}

}
