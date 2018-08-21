package com.letv.autoapk.ui.discover;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.CollectionRecordDao;
import com.letv.autoapk.dao.CollectionRecordInfo;
import com.letv.autoapk.utils.StringEscapeUtils;

class DiscoverRequest extends StringDataRequest {

	public DiscoverRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/getFinderList2";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage,
			String content, Object[] outputData) throws JSONException {
		if (statusCode == 0 && !TextUtils.isEmpty(content)) {
			List<DiscoverHotInfo> infos = (List<DiscoverHotInfo>) outputData[0];
			infos.clear();
			PageInfo pageInfo = (PageInfo) outputData[1];
			JSONObject jsonObj = new JSONObject(content);
			JSONArray listJson = jsonObj.getJSONArray(LIST);
			for (int i = 0; i < listJson.length(); i++) {
				DiscoverHotInfo info = new DiscoverHotInfo();
				DisplayVideoInfo displayVideoInfo = new DisplayVideoInfo();
				JSONObject obj = listJson.getJSONObject(i);
				JSONObject video = obj.optJSONObject("video");
				displayVideoInfo.setVideoTitle(StringEscapeUtils.unescapeHtml4(video.optString("videoTitle")));
				String videoId = video.optString("videoId");
				displayVideoInfo.setVideoId(video.optString("videoId"));
				String albumId = video.optString("ablumId");
				displayVideoInfo.setAlbumId(albumId);
				displayVideoInfo.setImageUrl(video.optString("videoImage"));
				displayVideoInfo.setChanncelId(video.optString("videoChannelId"));
				displayVideoInfo.setShareUrl(video.optString("videoShareUrl"));
				displayVideoInfo.setVideoDesc(StringEscapeUtils.unescapeHtml4(video.optString("videoDesc")));
				displayVideoInfo.setDetailType(video.optInt("detailType"));
				displayVideoInfo.setDownloadPlatform(video.optString("downloadPlatform"));
				info.setDisplayVideoInfo(displayVideoInfo);
				info.setHasSupport(obj.optInt("hasSupport") == 1 ? true : false);
				info.setSupportCount(obj.optInt("supportCount"));
				info.setEditorName(StringEscapeUtils.unescapeHtml4(obj.optString("editorName")));
				info.setEditorComment(StringEscapeUtils.unescapeHtml4(obj.optString("editorComment")));
				info.commentCount = obj.optInt("commentCount");
				try {
					info.hasCollect = obj.getInt("hasCollect");
				} catch (Exception e) {
					CollectionRecordDao dao = MyApplication.getInstance().getDaoByKey(CollectionRecordDao.class.getName());
					CollectionRecordInfo collectionRecordInfo;
					if (albumId != null && !albumId.isEmpty() && !"0".equals(albumId)) {
						// 专辑显示
						collectionRecordInfo = dao.findBySpecialColum("albumId", albumId);
					} else {
						collectionRecordInfo = dao.findBySpecialColum("videoId", videoId);
					}
					if (collectionRecordInfo != null){
						info.hasCollect = 1;
					}
				}
				infos.add(info);
			}
			JSONObject page = jsonObj.optJSONObject("page");
			if (page != null) {
				pageInfo.setPageIndex(page.optInt("currentPage"));
				pageInfo.setTotalCount(page.optInt("totalCount"));
				pageInfo.setTotalPage(page.optInt("totalPage"));
			}
		}else if(mContext instanceof BaseActivity){
			((BaseActivity)mContext).showToastSafe(alertMessage, Toast.LENGTH_SHORT);
		}
		return statusCode;
	}
}
