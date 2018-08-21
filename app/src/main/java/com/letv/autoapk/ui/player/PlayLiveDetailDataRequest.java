package com.letv.autoapk.ui.player;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.letv.autoapk.base.net.StringDataRequest;

public class PlayLiveDetailDataRequest extends StringDataRequest {

	public PlayLiveDetailDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/getLiveDetail";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData)
			throws JSONException {
		if (statusCode == 0) {
			PlayDetailInfo playDetailInfo = (PlayDetailInfo) outputData[0];
			JSONObject jsonObj = new JSONObject(content);
			JSONObject describeObj = jsonObj.getJSONObject("videoInfo");
			PlayVideoInfo describeInfo = new PlayVideoInfo();
			describeInfo.setVideoId(describeObj.optString("videoId"));
			describeInfo.setShareUrl(describeObj.optString("videoShareUrl"));
			describeInfo.setVideoTitle(describeObj.optString("videoTitle"));
			describeInfo.setImageUrl(describeObj.optString("videoImage"));
			describeInfo.setVideoBrief(describeObj.optString("videoBrief"));
			describeInfo.setPlayTimes(describeObj.optLong("playTimes"));
			describeInfo.setVideoActor(describeObj.optString("mainActorsDesc"));
			describeInfo.setArea(describeObj.optString("area"));
			describeInfo.setSubCategory(describeObj.optString("subCategory"));
			describeInfo.setVideoType(describeObj.optString("videoType"));
			describeInfo.setVideoTypeCode(describeObj.optString("videoTypeCode"));
			describeInfo.setPublishYear(describeObj.optString("publishYear"));
			describeInfo.setVideoDirector(describeObj.optString("director"));
			describeInfo.setMusician(describeObj.optString("musician"));
			describeInfo.setTvChannelName(describeObj.optString("tvChannelName"));
			describeInfo.setGuest(describeObj.optString("guest"));
			describeInfo.setIsSubscript(describeObj.optString("isSubscript"));
			describeInfo.setEpisode(describeObj.optString("episode"));
			describeInfo.setSubscriptType(describeObj.optInt("subscriptType"));
			describeInfo.setSubscriptName(describeObj.optString("subscriptName"));
			describeInfo.setStartTime(describeObj.optString("startTime"));
			describeInfo.setEndTime(describeObj.optString("endTime"));
			playDetailInfo.setDescribeInfo(describeInfo);

			JSONArray recomendArray = jsonObj.optJSONArray("recommendList");
			if (recomendArray == null) {
				return statusCode;
			}
			for (int i = 0; i < recomendArray.length(); i++) {
				JSONObject videoJson = recomendArray.getJSONObject(i);
				PlayVideoInfo episodeInfo = new PlayVideoInfo();
				episodeInfo.setVideoId(describeObj.optString("videoId"));
				episodeInfo.setShareUrl(describeObj.optString("videoShareUrl"));
				episodeInfo.setVideoTitle(describeObj.optString("videoTitle"));
				episodeInfo.setImageUrl(describeObj.optString("videoImage"));
				episodeInfo.setVideoBrief(describeObj.optString("videoBrief"));
				episodeInfo.setPlayTimes(videoJson.optLong("playTimes"));
				episodeInfo.setVideoActor(videoJson.optString("mainActorsDesc"));
				episodeInfo.setArea(videoJson.optString("area"));
				episodeInfo.setSubCategory(videoJson.optString("subCategory"));
				episodeInfo.setVideoType(videoJson.optString("videoType"));
				episodeInfo.setVideoTypeCode(videoJson.optString("videoTypeCode"));
				episodeInfo.setPublishYear(videoJson.optString("publishYear"));
				episodeInfo.setVideoDirector(videoJson.optString("director"));
				episodeInfo.setMusician(videoJson.optString("musician"));
				episodeInfo.setTvChannelName(videoJson.optString("tvChannelName"));
				episodeInfo.setGuest(videoJson.optString("guest"));
				episodeInfo.setIsSubscript(videoJson.optString("isSubscript"));
				episodeInfo.setEpisode(videoJson.optString("episode"));
				episodeInfo.setSubscriptType(videoJson.optInt("subscriptType"));
				episodeInfo.setSubscriptName(videoJson.optString("subscriptName"));
				playDetailInfo.getRecommendInfos().add(episodeInfo);
			}
		}
		return statusCode;
	}

}
