package com.letv.autoapk.ui.channel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
class DisplayBlockTitleInfo {
	private String blockTitle;
	private List<DisplayBlockInfo> displayBlockTitleInfos;

	public String getBlockTitle() {
		return blockTitle;
	}

	public void setBlockTitle(String blockTitle) {
		this.blockTitle = blockTitle;
	}

	public List<DisplayBlockInfo> getDisplayBlockTitleInfos() {
		if (displayBlockTitleInfos == null) {
			displayBlockTitleInfos = new ArrayList<DisplayBlockInfo>();
		}
		return displayBlockTitleInfos;
	}
}
