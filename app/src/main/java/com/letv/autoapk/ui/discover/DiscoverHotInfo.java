package com.letv.autoapk.ui.discover;

import com.letv.autoapk.base.net.DisplayVideoInfo;

class DiscoverHotInfo {
	private boolean hasSupport;
	private int supportCount;
	public int commentCount;
	public int hasCollect;
	private String editorName;
	private String editorComment;
	private DisplayVideoInfo displayVideoInfos;

	public boolean isHasSupport() {
		return hasSupport;
	}

	public void setHasSupport(boolean hasSupport) {
		this.hasSupport = hasSupport;
	}

	public int getSupportCount() {
		return supportCount;
	}

	public void setSupportCount(int supportCount) {
		this.supportCount = supportCount;
	}

	public String getEditorName() {
		return editorName;
	}

	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}

	public String getEditorComment() {
		return editorComment;
	}

	public void setEditorComment(String editorComment) {
		this.editorComment = editorComment;
	}

	public DisplayVideoInfo getDisplayVideoInfo() {
		return displayVideoInfos;
	}

	public void setDisplayVideoInfo(DisplayVideoInfo displayVideoInfos) {
		this.displayVideoInfos = displayVideoInfos;
	}

}
