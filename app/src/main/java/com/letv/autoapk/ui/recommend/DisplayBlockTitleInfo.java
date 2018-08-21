package com.letv.autoapk.ui.recommend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.letv.autoapk.base.net.ChannelInfo;
import com.letv.autoapk.base.net.DisplayVideoInfo;

class DisplayBlockTitleInfo {
	private String blockTitle;
	private ArrayList<DisplayBlockInfo> displayBlockTitleInfos;

	public String getBlockTitle() {
		return blockTitle;
	}

	public void setBlockTitle(String blockTitle) {
		this.blockTitle = blockTitle;
	}

	public ArrayList<DisplayBlockInfo> getDisplayBlockTitleInfos() {
		if (displayBlockTitleInfos == null) {
			displayBlockTitleInfos = new ArrayList<DisplayBlockInfo>();
		}
		return displayBlockTitleInfos;
	}

}
