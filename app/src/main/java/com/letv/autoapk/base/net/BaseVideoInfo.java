package com.letv.autoapk.base.net;

import java.io.Serializable;

public class BaseVideoInfo implements Serializable {

	private static final long serialVersionUID = 295476600051989569L;
	/*
	 * 视频ID
	 */
	protected String mVideoId;
	/*
	 * 视频标题
	 */
	protected String mVideoTitle;
	/*
	 * 视频简介
	 */
	protected String mVideoDesc;
	/*
	 * 视频播放URL
	 */
	protected String mPlayUrl;
	/*
	 * 视频码率
	 */
	protected String mRateType;
	/*
	 * 视频上次播放时间点
	 */
	protected long mLastPositon;
	/*
	 * 视频总时长
	 */
	protected long mTotalTime;
	/*
	 * 视频图版URL
	 */
	protected String mImageUrl;

	/*
	 * 专辑id
	 */
	protected String mAlbumId;
	/*
	 * 专辑名称
	 */
	protected String mAlbumName;
	
	private String albumPicUrl;
	public String getAlbumPicUrl() {
		return albumPicUrl;
	}

	public void setAlbumPicUrl(String albumPicUrl) {
		this.albumPicUrl = albumPicUrl;
	}
	public String getmAlbumName() {
		return mAlbumName;
	}

	public void setmAlbumName(String mAlbumName) {
		this.mAlbumName = mAlbumName;
	}

	/*
	 * 该视频所属频道
	 */
	protected String mChanncelId;

	public String getVideoId() {
		return mVideoId;
	}

	public void setVideoId(String mVideoId) {
		this.mVideoId = mVideoId;
	}

	public String getVideoTitle() {
		return mVideoTitle;
	}

	public void setVideoTitle(String mVideoTitle) {
		this.mVideoTitle = mVideoTitle;
	}

	public String getVideoDesc() {
		return mVideoDesc;
	}

	public void setVideoDesc(String mVideoBrief) {
		this.mVideoDesc = mVideoBrief;
	}

	public String getPlayUrl() {
		return mPlayUrl;
	}

	public void setPlayUrl(String mPlayUrl) {
		this.mPlayUrl = mPlayUrl;
	}

	public String getRateType() {
		return mRateType;
	}

	public void setRateType(String mRateType) {
		this.mRateType = mRateType;
	}

	public long getLastPositon() {
		return mLastPositon;
	}

	public void setLastPositon(long mLastPositon) {
		this.mLastPositon = mLastPositon;
	}

	public long getTotalTime() {
		return mTotalTime;
	}

	public void setTotalTime(long mTotalTime) {
		this.mTotalTime = mTotalTime;
	}

	public String getImageUrl() {
		return mImageUrl;
	}

	public void setImageUrl(String mImageUrl) {
		this.mImageUrl = mImageUrl;
	}

	public String getAlbumId() {
		return mAlbumId;
	}

	public void setAlbumId(String mAblumId) {
		this.mAlbumId = mAblumId;
	}

	public String getChanncelId() {
		return mChanncelId;
	}

	public void setChanncelId(String mChanncelId) {
		this.mChanncelId = mChanncelId;
	}
}
