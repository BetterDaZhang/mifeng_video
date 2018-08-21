package com.letv.autoapk.ui.recommend;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.utils.StringEscapeUtils;

class SubjectDataRequest extends StringDataRequest {

	public SubjectDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/getSubjectList";
	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData)
			throws JSONException {
		if (statusCode == 0) {
			SubjectInfo subjectInfo = (SubjectInfo) outputData[0];
			JSONObject jsonObj = new JSONObject(content);
			
			subjectInfo.setSubjectId(jsonObj.optString("subjectId"));
			subjectInfo.setSubjectName(jsonObj.optString("subjectName"));
			subjectInfo.setSubjectBrief(jsonObj.optString("subjectBrief"));
			subjectInfo.setSubjectImg(jsonObj.optString("subjectImage"));
			
			JSONArray array = jsonObj.getJSONArray(LIST);
			for (int i = 0; i < array.length(); i++) {
				JSONObject json = array.getJSONObject(i);
				DisplayBlockInfo block = new DisplayBlockInfo();
				block.setBlockName(json.optString("blockName"));
				block.setChannelId(json.optString("blockChannelId"));
				block.setBlockDisplayType(json.optInt("blockDisplayType"));
//				block.setBlockDetailType(json.optInt("blockDetailType"));
				block.setBlockMoreName(json.optString("blockMoreName"));
//				block.setBlockId(json.optString("blockId"));
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
					vedioInfo.setSuperScripColor(videoJson.optInt("superscriptColor"));
					vedioInfo.setSuperScripName(StringEscapeUtils.unescapeHtml4(videoJson.optString("superscriptName")));
					vedioInfo.setSubscriptType(videoJson.optInt("subscriptType"));
					vedioInfo.setSubscriptName(StringEscapeUtils.unescapeHtml4(videoJson.optString("subscriptName")));
					vedioInfo.setChanncelId(videoJson.optString("channelId"));
					block.addVideoListInfo(vedioInfo);
				}
				subjectInfo.getSubjectBlocks().add(block);
			}

		}
		return statusCode;
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

}
