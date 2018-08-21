package com.letv.autoapk.ui.channel;

import java.io.Serializable;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import com.letv.autoapk.base.db.Model;

@Table(name = "ChannelVideoInfo")
public class ChannelVideoInfo extends Model implements Serializable {

	/**
	 * 频道ID
	 */
	@Column(name = "mChannelId", property = "UNIQUE")
	protected String mChannelId;
	/**
	 * 频道名称
	 */
	@Column(name = "mChannelName")
	protected String mChannelName;
	/**
	 * 频道简介
	 */
	@Column(name = "mChannelDetailName")
	protected String mChannelDetailName;
	/**
	 * 频道图标
	 */
	@Column(name = "mChannelIcon")
	protected String mChannelIcon;
	/**
	 * 频道缩略图
	 */
	@Column(name = "mChannelImageUrl")
	protected String mChannelImageUrl;

	// 下行页Id
	@Column(name = "mPageId")
	protected String mPageId;
	//移动直播添加字段
	@Column(name = "mChannelDetailType")
	protected int mChannelDetailType;
	
	
	public int getChannelDetailType() {
		return mChannelDetailType;
	}

	public void setChannelDetailType(int channelDetailType) {
		this.mChannelDetailType = channelDetailType;
	}

	public String getChannelId() {
		return mChannelId;
	}

	public void setChannelId(String mChannellId) {
		this.mChannelId = mChannellId;
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

	public String getmPageId() {
		return mPageId;
	}

	public void setmPageId(String mPageId) {
		this.mPageId = mPageId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (null == obj) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ChannelVideoInfo info = (ChannelVideoInfo) obj;
		if (!this.getChannelId().equals(info.getChannelId())) {
			return false;
		}
		return true;
	}

}
