package com.letv.autoapk.ui.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.utils.StringEscapeUtils;

class SearchResultDataRequest extends StringDataRequest {
	public static final String VIDEOTYPE = "videoType";
	public static final String KEYWORD = "keyword";
	private Context context;

	public SearchResultDataRequest(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected String getUrl() {
		return "/search";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData) throws JSONException {
		//
		if (statusCode == 0 && !TextUtils.isEmpty(content)) {
			List<SearchResultInfo> infos = (List<SearchResultInfo>) outputData[0];
			PageInfo pageInfo = (PageInfo) outputData[1];
			Map<String, String> categories = (Map<String, String>) outputData[2];
			Boolean isReInitHeadView = (Boolean) outputData[3];
			JSONObject jsonObj = new JSONObject(content);
			JSONArray categorySetList = jsonObj.optJSONArray("categorySet");
			for (int i = 0; i < categorySetList.length(); i++) {
				if (isReInitHeadView) {
					JSONObject obj = categorySetList.optJSONObject(i);
					categories.put(obj.optString("name"), obj.optString("value"));
				}
			}
			JSONArray listJson = jsonObj.optJSONArray(LIST);
			for (int i = 0; i < listJson.length(); i++) {
				SearchResultInfo searchResultInfo = new SearchResultInfo();// 1
				DisplayVideoInfo displayVideoInfo = new DisplayVideoInfo();// 当前展示的视频
				JSONObject obj = listJson.optJSONObject(i);
				searchResultInfo.setVideoSearchBlockDisplayType(obj.optInt("videoSearchBlockDisplayType"));
				// 对当前结果需要展示的首要视频解析
				displayVideoInfo.setVideoId(obj.optString("target"));
				displayVideoInfo.setDetailType(obj.optInt("detailType"));
				displayVideoInfo.setShareUrl(obj.optString("shareUrl"));
				displayVideoInfo.setId(obj.optString("id"));
				displayVideoInfo.setVideoTitle(StringEscapeUtils.unescapeHtml4(obj.optString("title")));
				displayVideoInfo.setVideoDesc(StringEscapeUtils.unescapeHtml4(obj.optString("brief")));
				displayVideoInfo.setImageUrl(obj.optString("image"));
				displayVideoInfo.setAlbumId(obj.optString("ablumId"));
				displayVideoInfo.setSubscriptType(obj.optInt("subscriptType"));
				// displayVideoInfo.setNeedLogin(unitVideo.optInt("needLogin"));
				displayVideoInfo.setSubscriptName(obj.optString("subscriptName"));
				displayVideoInfo.setChanncelId(obj.optString("channelId"));
				displayVideoInfo.setPlayTimes(obj.optLong("playTimes"));
				displayVideoInfo.setPublishTime(obj.optString("publishTime"));
				displayVideoInfo.setVideoDirector(obj.optString("videoDirector"));
				displayVideoInfo.setVideoActor(obj.optString("videoActor"));

				searchResultInfo.setDispalyVideoInfoUnit(displayVideoInfo);
				// 相关结果列表，单个视频该字段为空
				JSONArray blockVideos = obj.optJSONArray("videoSearchBlockRefList");
				if (null != blockVideos) {
					List<DisplayVideoInfo> displayBlockVideos = new ArrayList<DisplayVideoInfo>();
					for (int j = 0; j < blockVideos.length(); j++) {
						JSONObject blockVideo = blockVideos.optJSONObject(j);
						DisplayVideoInfo displayBlockVideo = new DisplayVideoInfo();
						displayBlockVideo.setVideoId(blockVideo.optString("target"));
						displayBlockVideo.setDetailType(blockVideo.optInt("detailType"));
						displayBlockVideo.setShareUrl(blockVideo.optString("shareUrl"));
						displayBlockVideo.setId(blockVideo.optString("id"));
						displayBlockVideo.setVideoTitle(StringEscapeUtils.unescapeHtml4(blockVideo.optString("title")));
						displayBlockVideo.setVideoDesc(StringEscapeUtils.unescapeHtml4(blockVideo.optString("brief")));
						displayBlockVideo.setImageUrl(blockVideo.optString("image"));
						displayBlockVideo.setAlbumId(blockVideo.optString("ablumId"));
						displayBlockVideo.setSubscriptType(blockVideo.optInt("subscriptType"));
						// displayBlockVideo.setNeedLogin(blockVideo.optInt("needLogin"));
						displayBlockVideo.setSubscriptName(blockVideo.optString("subscriptName"));
						displayBlockVideo.setChanncelId(blockVideo.optString("channelId"));
						displayBlockVideo.setPlayTimes(blockVideo.optLong("playTimes"));
						displayBlockVideo.setEpisode(blockVideo.optString("episode"));
						displayBlockVideo.setPublishTime(blockVideo.optString("publishTime"));
						displayBlockVideo.setVideoDirector(blockVideo.optString("videoDirector"));
						displayBlockVideo.setVideoActor(blockVideo.optString("videoActor"));
						displayBlockVideos.add(displayBlockVideo);
					}
					searchResultInfo.setDisplayVideoInfos(displayBlockVideos);
				}
				infos.add(searchResultInfo);
			}
			// TODO 分页信息暂时去掉
			JSONObject page = jsonObj.optJSONObject("page");
			if (null != page) {
				pageInfo.setPageIndex(page.optInt("currentPage"));
				pageInfo.setTotalCount(page.optInt("totalCount"));
				pageInfo.setTotalPage(page.optInt("totalPage"));
			}
		} else {
			// Toast.makeText(context, alertMessage, Toast.LENGTH_SHORT).show();
		}
		return statusCode;
	}

}
