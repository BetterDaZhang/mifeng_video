package com.letv.autoapk.ui.player;

import java.util.ArrayList;
import java.util.List;

public class PlayCommentInfo {
	private String commentId;
	private String commentContent;
	private String commentTime;
	private CommentUser User;
	private int supportCount;
	private boolean hasSupport;
	private List<PlayCommentInfo> replyCommentInfos;
	private int commentCount;
	private int commentType;
	private String replayNickName;

	public String getReplayNickName() {
		return replayNickName;
	}

	public void setReplayNickName(String replayNickName) {
		this.replayNickName = replayNickName;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	public String getCommentTime() {
		return commentTime;
	}

	public void setCommentTime(String commentTime) {
		this.commentTime = commentTime;
	}

	public CommentUser getUser() {
		return User;
	}

	public void setUser(CommentUser user) {
		User = user;
	}

	public int getSupportCount() {
		return supportCount;
	}

	public void setSupportCount(int supportCount) {
		this.supportCount = supportCount;
	}

	public boolean isHasSupport() {
		return hasSupport;
	}

	public void setHasSupport(boolean hasSupport) {
		this.hasSupport = hasSupport;
	}

	public List<PlayCommentInfo> getReplyCommentInfos() {
		if (replyCommentInfos == null) {
			replyCommentInfos = new ArrayList<PlayCommentInfo>();
		}
		return replyCommentInfos;
	}

	public void setReplyCommentInfos(List<PlayCommentInfo> playCommentInfos) {
		this.replyCommentInfos = playCommentInfos;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public int getCommentType() {
		return commentType;
	}

	public void setCommentType(int commentType) {
		this.commentType = commentType;
	}

}
