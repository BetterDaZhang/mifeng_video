package com.letv.autoapk.ui.search;

import java.util.ArrayList;
import java.util.List;

import com.letv.autoapk.base.net.DisplayVideoInfo;

class SearchResultInfo {
	private int videoSearchBlockDisplayType;
	private DisplayVideoInfo dispalyVideoInfoUnit;
	private List<DisplayVideoInfo> displayVideoInfos;

	public int getVideoSearchBlockDisplayType() {
		return videoSearchBlockDisplayType;
	}

	public void setVideoSearchBlockDisplayType(
			int videoSearchBlockDisplayType) {
		this.videoSearchBlockDisplayType = videoSearchBlockDisplayType;
	}

	public DisplayVideoInfo getDispalyVideoInfoUnit() {
		return dispalyVideoInfoUnit;
	}

	public void setDispalyVideoInfoUnit(DisplayVideoInfo dispalyVideoInfoUnit) {
		this.dispalyVideoInfoUnit = dispalyVideoInfoUnit;
	}

	public List<DisplayVideoInfo> getDisplayVideoInfos() {
		return displayVideoInfos;
	}

	public void setDisplayVideoInfos(List<DisplayVideoInfo> displayVideoInfos) {
		this.displayVideoInfos = displayVideoInfos;
	}
	
	public void addDiaplayVideoInfos(DisplayVideoInfo info){
		if (null  == displayVideoInfos) {
			displayVideoInfos = new ArrayList<DisplayVideoInfo>();
		}
		displayVideoInfos.add(info);
	}

}
