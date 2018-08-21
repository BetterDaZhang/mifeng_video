package com.letv.autoapk.base.net;

import java.io.Serializable;

public class ChannelInfo implements Serializable {

	/**
	 * 序列化Id
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 增设一个字段设置判断是否是频道筛选页
	 */
	/**
	 * 频道ID
	 */
	protected String mChannelId;
	/**
	 * 对应下行也的PageId
	 */
	protected String mChannelDetailId;
	/**
	 * 频道名称
	 */
	protected String mChannelName;
	/**
	 * 频道简介
	 */
	protected String mChannelDetailName;
	/**
	 * 频道图标
	 */
	protected String mChannelIcon;
	/**
	 * 频道缩略图
	 */
	protected String mChannelImageUrl;
	/**
	 * 频道详情
	 */
	 protected int mChannelDetailType;

	public String getChannelId() {
		return mChannelId;
	}

	public void setChannelId(String mChannellId) {
		this.mChannelId = mChannellId;
	}

	public String getChannelDetailId() {
		return mChannelDetailId;
	}

	public void setChannelDetailId(String mChannelDetailId) {
		this.mChannelDetailId = mChannelDetailId;
	}

	public String getChannelName() {
		return mChannelName;
	}

	public void setChannelName(String mChannellName) {
		this.mChannelName = mChannellName;
	}

	public String getChannelDetailName() {
		return mChannelDetailName;
	}

	public void setChannelDetailName(String mChannelDetailName) {
		this.mChannelDetailName = mChannelDetailName;
	}

	public String getChannelIcon() {
		return mChannelIcon;
	}

	public void setChannelIcon(String mChannelIcon) {
		this.mChannelIcon = mChannelIcon;
	}

	public String getChannelImageUrl() {
		return mChannelImageUrl;
	}

	public void setChannelImageUrl(String mChannelImageUrl) {
		this.mChannelImageUrl = mChannelImageUrl;
	}

	public int getChannelDetailType() {
		return mChannelDetailType;
	}

	public void setChannelDetailType(int mChannelDetailType) {
		this.mChannelDetailType = mChannelDetailType;
	}
	
	

}
