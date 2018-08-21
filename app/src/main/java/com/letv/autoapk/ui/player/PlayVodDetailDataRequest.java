package com.letv.autoapk.ui.player;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.letv.autoapk.base.net.StringDataRequest;

public class PlayVodDetailDataRequest extends StringDataRequest {

	public PlayVodDetailDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/video/androidVideoDetail";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData) throws JSONException {
		if (statusCode == 0) {
			PlayDetailInfo playDetailInfo = (PlayDetailInfo) outputData[0];
			JSONObject jsonObj = new JSONObject(content);
			int display = jsonObj.getInt("displayType");
			JSONArray episodeArray = jsonObj.optJSONArray("albumList");
			if (episodeArray != null) {
				for (int i = 0; i < episodeArray.length(); i++) {
					JSONObject videoJson = episodeArray.getJSONObject(i);
					PlayVideoInfo episodeInfo = new PlayVideoInfo();
					episodeInfo.setVideoId(videoJson.optString("videoId"));
					episodeInfo.setShareUrl(videoJson.optString("videoShareUrl"));
					episodeInfo.setVideoTitle(videoJson.optString("videoTitle"));
					episodeInfo.setImageUrl(videoJson.optString("videoImage"));
					episodeInfo.setVideoBrief(videoJson.optString("videoBrief"));
					episodeInfo.setmAlbumName(videoJson.optString("albumName"));
					episodeInfo.setAlbumPicUrl(videoJson.optString("albumPicUrl"));
					episodeInfo.setAlbumId(videoJson.optString("ablumId"));
					episodeInfo.setPlayTimes(videoJson.optLong("playTimes"));
					episodeInfo.setVideoActor(videoJson.optString("mainActorsDesc"));
					episodeInfo.setArea(videoJson.optString("area"));
					episodeInfo.setSubCategory(videoJson.optString("subCategory"));
					episodeInfo.setVideoType(videoJson.optString("videoType"));
					episodeInfo.setVideoTypeCode(videoJson.optString("videoTypeCode"));
					episodeInfo.setPublishYear(videoJson.optString("publishYear"));
					episodeInfo.setIsVip(videoJson.optInt("isVip"));
					episodeInfo.setVideoDirector(videoJson.optString("director"));
					episodeInfo.setMusician(videoJson.optString("musician"));
					episodeInfo.setTvChannelName(videoJson.optString("tvChannelName"));
					episodeInfo.setGuest(videoJson.optString("guest"));
					episodeInfo.setIsSubscript(videoJson.optString("isSubscript"));
					episodeInfo.setEpisode(videoJson.optString("episode"));
					episodeInfo.setSubscriptType(videoJson.optInt("subscriptType"));
					episodeInfo.setSubscriptName(videoJson.optString("subscriptName"));
					episodeInfo.setDownloadPlatform(videoJson.optString("downloadPlatform"));
					episodeInfo.setPayPlatform(videoJson.optString("payPlatform"));
					playDetailInfo.getEpisodeInfos().add(episodeInfo);
				}
			}

			JSONObject describeObj = jsonObj.optJSONObject("videoInfo");
			if (describeObj != null) {
				PlayVideoInfo describeInfo = new PlayVideoInfo();
				describeInfo.setVideoId(describeObj.optString("videoId"));
				describeInfo.setShareUrl(describeObj.optString("videoShareUrl"));
				describeInfo.setVideoTitle(describeObj.optString("videoTitle"));
				describeInfo.setImageUrl(describeObj.optString("videoImage"));
				describeInfo.setVideoBrief(describeObj.optString("videoBrief"));
				describeInfo.setmAlbumName(describeObj.optString("albumName"));
				describeInfo.setAlbumPicUrl(describeObj.optString("albumPicUrl"));
				describeInfo.setAlbumId(describeObj.optString("albumId"));
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
				describeInfo.setDownloadPlatform(describeObj.optString("downloadPlatform"));
				describeInfo.setIsVip(describeObj.optInt("isVip"));
				describeInfo.setPayPlatform(describeObj.optString("payPlatform"));
				describeInfo.setDisplayType(display);
				playDetailInfo.setDescribeInfo(describeInfo);
			}

			playDetailInfo.setDisplayType(display);
			playDetailInfo.setTotalEpisodes(jsonObj.optString("totalEpisodes"));
			playDetailInfo.setUpdateEpisode(jsonObj.optString("updateEpisode"));

			JSONArray recomendArray = jsonObj.optJSONArray("recommendList");
			if (recomendArray == null) {
				return statusCode;
			}
			for (int i = 0; i < recomendArray.length(); i++) {
				JSONObject videoJson = recomendArray.getJSONObject(i);
				PlayVideoInfo episodeInfo = new PlayVideoInfo();
				episodeInfo.setVideoId(videoJson.optString("videoId"));
				episodeInfo.setShareUrl(videoJson.optString("videoShareUrl"));
				episodeInfo.setVideoTitle(videoJson.optString("videoTitle"));
				episodeInfo.setImageUrl(videoJson.optString("videoImage"));
				episodeInfo.setVideoBrief(videoJson.optString("videoBrief"));
				// episodeInfo.setAblumId(videoJson.optString("ablumId"));
				episodeInfo.setmAlbumName(describeObj.optString("albumName"));
				episodeInfo.setAlbumPicUrl(describeObj.optString("albumPicUrl"));
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
