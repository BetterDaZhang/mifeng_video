package com.letv.autoapk.ui.player;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;

public class PlayVodCommentRequest extends StringDataRequest {

	public PlayVodCommentRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/getCommentList";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData)
			throws JSONException {
		if (statusCode == 0) {
			List<PlayCommentInfo> playCommentInfos = (List<PlayCommentInfo>) outputData[0];
			PageInfo pageInfo = (PageInfo) outputData[1];
			JSONObject json = new JSONObject(content);
			JSONArray array = json.getJSONArray(LIST);
			if (array == null) {
				return -1;
			}
			for (int i = 0; i < array.length(); i++) {
				PlayCommentInfo playCommentInfo = new PlayCommentInfo();
				JSONObject jsonObj = array.getJSONObject(i);
				playCommentInfo.setCommentId(jsonObj.optString("commentId"));
				playCommentInfo.setCommentContent(jsonObj.getString("commentContent"));
				playCommentInfo.setCommentTime(jsonObj.optString("commentTime"));
				playCommentInfo.setHasSupport((jsonObj.optInt("hasSupport") == 0 ? false : true));
				playCommentInfo.setSupportCount(jsonObj.optInt("supportCount"));
				playCommentInfo.setCommentCount(jsonObj.optInt("commentCount"));
				playCommentInfo.setReplayNickName(jsonObj.optString("replyNickName"));

				JSONObject userObj = jsonObj.getJSONObject("user");
				CommentUser commentUser = new CommentUser();
				commentUser.setUserId(userObj.optString("userId"));
				commentUser.setUserIcon(userObj.optString("userIcon"));
				commentUser.setNickName(userObj.optString("nickName"));
				playCommentInfo.setUser(commentUser);

				JSONArray commentArray = jsonObj.optJSONArray("reply");
				for (int j = 0; j < commentArray.length(); j++) {
					JSONObject secCommntObj = commentArray.getJSONObject(j);
					PlayCommentInfo secondCommnetInfo = new PlayCommentInfo();
					secondCommnetInfo.setCommentId(secCommntObj.optString("commentId"));
					secondCommnetInfo.setCommentContent(secCommntObj.getString("commentContent"));
					secondCommnetInfo.setCommentTime(secCommntObj.optString("commentTime"));
					secondCommnetInfo.setHasSupport((secCommntObj.optInt("hasSupport") == 0 ? false : true));
					secondCommnetInfo.setSupportCount(secCommntObj.optInt("supportCount"));
					secondCommnetInfo.setCommentCount(secCommntObj.optInt("commentCount"));
					secondCommnetInfo.setReplayNickName(secCommntObj.optString("replyNickName"));
					JSONObject secUserObj = secCommntObj.getJSONObject("user");
					CommentUser secCommentUser = new CommentUser();
					secCommentUser.setUserId(secUserObj.optString("userId"));
					secCommentUser.setUserIcon(secUserObj.optString("userIcon"));
					secCommentUser.setNickName(secUserObj.optString("nickName"));
					secondCommnetInfo.setUser(secCommentUser);
					playCommentInfo.getReplyCommentInfos().add(secondCommnetInfo);
				}
				playCommentInfos.add(playCommentInfo);
			}
			JSONObject obj = json.getJSONObject("page");
			pageInfo.setTotalPage(obj.optInt("totalPage"));
			pageInfo.setPageIndex(obj.optInt("currentPage"));
			pageInfo.setTotalCount(obj.optInt("totalCount"));
			pageInfo.setmTotalCountShow(obj.optInt("totalCountForShow"));
		}
		return statusCode;
	}
}
