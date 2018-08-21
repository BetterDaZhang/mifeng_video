package com.letv.autoapk.ui.tvlive;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;

import android.content.Context;

public class PlayTVLiveDataRequest extends StringDataRequest {

	public PlayTVLiveDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/getAudioList";
	}


	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData)
			throws JSONException {
		if (statusCode == 0) {
			PlayTvVideoInfo playTvVideoInfo = (PlayTvVideoInfo) outputData[0];
			PageInfo pageInfo = (PageInfo) outputData[1];
			JSONObject json = new JSONObject(content);
			JSONObject object = (JSONObject) json.opt("liveInfo");
			JSONObject videoObj = (JSONObject) object.opt("videoInfo");
			playTvVideoInfo.setCid(videoObj.optString("videoId"));
			playTvVideoInfo.setTvDesc(videoObj.optString("videoBrief"));
			playTvVideoInfo.setTvImgUrl(videoObj.optString("videoImage"));
			playTvVideoInfo.setTvShareUrl(videoObj.optString("videoShareUrl"));
			playTvVideoInfo.setTvTitle(videoObj.optString("videoTitle"));
			playTvVideoInfo.setBeginTime(videoObj.optString("startTime"));
			playTvVideoInfo.setEndTime(videoObj.optString("endTime"));

			JSONArray array = json.getJSONArray(LIST);
			if (array == null) {
				return statusCode;
			}
			for (int i = 0; i < array.length(); i++) {
				PlayTvDateInfo playTvDateInfo = new PlayTvDateInfo();
				JSONObject jsonObj = array.getJSONObject(i);
				playTvDateInfo.setDateTime(jsonObj.optLong("date"));
				JSONArray subArray = jsonObj.optJSONArray("content");
				if (subArray == null) {
					continue;
				}
				for (int j = 0; j < subArray.length(); j++) {
					JSONObject jsonSubObj = subArray.getJSONObject(j);
					PlayTvItemInfo playTvItemInfo = new PlayTvItemInfo();
					playTvItemInfo.setBeginTime(jsonSubObj.optLong("beginTime"));
					playTvItemInfo.setEndTime(jsonSubObj.optLong("endTime"));
					playTvItemInfo.setTvItemTitle(jsonSubObj.optString("title"));
					playTvDateInfo.getTvItemInfos().add(playTvItemInfo);
				}
				playTvVideoInfo.getTvDateInfos().add(playTvDateInfo);
			}
			JSONObject obj = json.getJSONObject("page");
			pageInfo.setTotalPage(obj.optInt("totalPage"));
			pageInfo.setPageIndex(obj.optInt("currentPage"));
			pageInfo.setTotalCount(obj.optInt("totalCount"));
			pageInfo.setmTotalCountShow(obj.optInt("totalCountForShow"));
			playTvVideoInfo.setServerTime(json.optLong("servertime"));
		}
		return statusCode;
	}

}
