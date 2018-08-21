package com.letv.autoapk.ui.recommend;

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

class RecommendVideoBlockListDataRequest extends StringDataRequest {

	public RecommendVideoBlockListDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/videoBlockList2";
	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData)
			throws JSONException {
		if (statusCode == 0) {
			List<DisplayBlockInfo> blockInfos = (List<DisplayBlockInfo>) outputData[0];
			DisplayBlockTitleInfo blockTitleInfo = (DisplayBlockTitleInfo) outputData[1];
			PageInfo pageInfo = (PageInfo) outputData[2];
			blockInfos.clear();
			JSONObject jsonObj = new JSONObject(content);
			JSONArray array = jsonObj.optJSONArray(LIST);
			if (array == null) {
				return statusCode;
			}
			for (int i = 0; i < array.length(); i++) {
				JSONObject json = array.getJSONObject(i);
				DisplayBlockInfo block = new DisplayBlockInfo();
				block.setBlockName(json.optString("blockName"));
				block.setBlockDisplayType(json.optInt("blockDisplayType"));
				block.setBlockMoreName(json.optString("blockMoreName"));
				block.setBlockDetailId(json.optString("blockDetailId"));
				JSONArray videoArray = json.optJSONArray("list");
				if (videoArray == null) {
					continue;
				}
				for (int j = 0; j < videoArray.length(); j++) {
					JSONObject videoJson = videoArray.getJSONObject(j);
					DisplayVideoInfo vedioInfo = new DisplayVideoInfo();
					vedioInfo.setVideoId(videoJson.optString("target"));
					vedioInfo.setShareUrl(videoJson.optString("shareUrl"));
					vedioInfo.setId(videoJson.optString("id"));
					vedioInfo.setVideoTitle(StringEscapeUtils.unescapeHtml4(videoJson.optString("title")));
					vedioInfo.setVideoDesc(StringEscapeUtils.unescapeHtml4(videoJson.optString("brief")));
					vedioInfo.setImageUrl(videoJson.optString("image"));
					vedioInfo.setAlbumId(videoJson.optString("ablumId"));
					vedioInfo.setDetailType(videoJson.optInt("detailType"));
					vedioInfo.setSuperScripType(videoJson.optInt("superscriptType"));
//					vedioInfo.setSuperScripColor(videoJson.optInt("superscriptColor"));
					vedioInfo.setSuperScripName(StringEscapeUtils.unescapeHtml4(videoJson.optString("superscriptName")));
					vedioInfo.setSubscriptType(videoJson.optInt("subscriptType"));
					vedioInfo.setSubscriptName(StringEscapeUtils.unescapeHtml4(videoJson.optString("subscriptName")));
					vedioInfo.setChanncelId(videoJson.optString("channelId"));
					block.addVideoListInfo(vedioInfo);
				}
				blockInfos.add(block);
			}
			JSONObject obj = jsonObj.getJSONObject("page");
			pageInfo.setTotalPage(obj.optInt("totalPage"));
			pageInfo.setPageIndex(obj.optInt("currentPage"));
			pageInfo.setTotalCount(obj.optInt("totalCount"));
			blockTitleInfo.setBlockTitle(jsonObj.optString("title"));
		}
		return statusCode;
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

}
