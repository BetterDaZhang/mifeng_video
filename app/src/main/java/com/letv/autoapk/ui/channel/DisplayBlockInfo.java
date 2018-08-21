package com.letv.autoapk.ui.channel;

import java.util.ArrayList;
import java.util.List;

import com.letv.autoapk.base.net.ChannelInfo;
import com.letv.autoapk.base.net.DisplayVideoInfo;



class DisplayBlockInfo extends ChannelInfo {

	private static final long serialVersionUID = -5418061768902495483L;

	private String blockName;
	/**
	 * list列表样式
	 */
	private int blockDisplayType;
	
	private String blockMoreName;
	
	private String blockDetailId;
	
	private List<DisplayVideoInfo> mVideoList;

	public String getBlockName() {
		return blockName;
	}

	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}

	public int getBlockDisplayType() {
		return blockDisplayType;
	}

	public void setBlockDisplayType(int blockDisplayType) {
		this.blockDisplayType = blockDisplayType;
	}

	public String getBlockMoreName() {
		return blockMoreName;
	}

	public void setBlockMoreName(String blockMoreName) {
		this.blockMoreName = blockMoreName;
	}

	public String getBlockDetailId() {
		return blockDetailId;
	}

	public void setBlockDetailId(String blockDetailId) {
		this.blockDetailId = blockDetailId;
	}


	public List<DisplayVideoInfo> getVideoList() {
		if (mVideoList == null) {
			mVideoList = new ArrayList<DisplayVideoInfo>();
		}
		return mVideoList;
	}

	public void setVideoList(List<DisplayVideoInfo> mVedioList) {
		this.mVideoList = mVedioList;
	}

	public void addVideoListInfo(DisplayVideoInfo info){
		if(mVideoList == null){
			mVideoList = new ArrayList<DisplayVideoInfo>();
		}
		mVideoList.add(info);
	}
}
