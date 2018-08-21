package com.letv.autoapk.ui.collection;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.dao.CollectionRecordInfo;

import android.content.Context;

/**
 * 获取收藏的视屏接口
 * 
 * 
 */
class GetCollectRecordsRequest extends StringDataRequest {

	public GetCollectRecordsRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/getMyCollectList";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	@Override
	protected String getToken() {
		return LoginInfoUtil.getToken(mContext);
	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData) throws JSONException {

		List<CollectionRecordInfo> record = (List<CollectionRecordInfo>) outputData[0];
		PageInfo pageInfo = (PageInfo) outputData[1];
		if (statusCode == 0) {
			JSONObject jsonObj = new JSONObject(content);
			JSONArray array = jsonObj.getJSONArray(LIST);
			CollectionRecordInfo cr;
			for (int i = 0; i < array.length(); i++) {
				cr = new CollectionRecordInfo();
				JSONObject obj = array.getJSONObject(i);

				cr.setVideoId(obj.optString("videoId"));
				cr.setVideoTitle(obj.optString("videoTitle"));
				cr.setVideoImage(obj.optString("videoImage"));
				cr.setFavoriteId(obj.optString("favoriteId"));
				cr.setVideoShareUrl(obj.optString("videoShareUrl"));
				cr.setPlayTimes(obj.optLong("playTimes"));
				cr.setMainActorsDesc(obj.optString("mainActorsDesc"));
				cr.setArea(obj.optString("area"));
				cr.setVideoTypesDesc(obj.optString("videoTypesDesc"));
				cr.setVideoBrief(obj.optString("videoBrief"));
				cr.setPublishYear(obj.optString("publishYear"));
				cr.setDirector(obj.optString("director"));
				cr.setMusician(obj.optString("musician"));
				cr.setTvChannelName(obj.optString("tvChannelName"));
				cr.setGuest(obj.optString("guest"));
				cr.setAlbumId(obj.optString("albumId"));
				record.add(cr);
			}
			JSONObject pageObj = jsonObj.getJSONObject("page");
			pageInfo.setTotalPage(pageObj.optInt("totalPage"));
			pageInfo.setPageIndex(pageObj.optInt("currentPage"));
			pageInfo.setTotalCount(pageObj.optInt("totalCount"));
		}
		return statusCode;
	}

}
