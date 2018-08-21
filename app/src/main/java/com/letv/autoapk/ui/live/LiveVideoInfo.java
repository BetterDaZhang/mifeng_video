package com.letv.autoapk.ui.live;

import java.io.Serializable;

public class LiveVideoInfo implements Serializable{
	/**
	 * 序列化Id
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 直播模块ID
	 */
	protected String mLiveVideoId;
	/**
	 * 对应下行页的PageId
	 */
	protected String mLiveVideoDetailId;
	/**
	 * 直播模块名称
	 */
	protected String mLiveVideoName;
	/**
	 * 直播模块图标
	 */
	protected String mLiveVideoIcon;
	
	protected int mLiveDetailType;
	
	public int getmLiveDetailType() {
		return mLiveDetailType;
	}
	public void setmLiveDetailType(int mLiveDetailType) {
		this.mLiveDetailType = mLiveDetailType;
	}
	public String getmLiveVideoId() {
		return mLiveVideoId;
	}
	public void setmLiveVideoId(String mLiveVideoId) {
		this.mLiveVideoId = mLiveVideoId;
	}
	public String getmLiveVideoDetailId() {
		return mLiveVideoDetailId;
	}
	public void setmLiveVideoDetailId(String mLiveVideoDetailId) {
		this.mLiveVideoDetailId = mLiveVideoDetailId;
	}
	public String getmLiveVideoName() {
		return mLiveVideoName;
	}
	public void setmLiveVideoName(String mLiveVideoName) {
		this.mLiveVideoName = mLiveVideoName;
	}
	public String getLiveVideoIcon() {
		return mLiveVideoIcon;
	}
	public void setmLiveVideoIcon(String mLiveVideoIcon) {
		this.mLiveVideoIcon = mLiveVideoIcon;
	}
	
}
