package com.letv.autoapk.dao;

import java.util.Date;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import android.graphics.Shader;

import com.letv.autoapk.base.db.Model;
import com.letv.autoapk.base.net.BaseVideoInfo;
import com.letv.autoapk.base.net.DisplayVideoInfo;

@Table(name = "PlayRecordInfo")
public class PlayRecordInfo extends Model {

	private static final long serialVersionUID = 307395213010732817L;
	/** 播放记录id */
	@Column(name = "playRecordId", property = "UNIQUE")
	private String playRecordId;

	@Column(name = "videoId", property = "UNIQUE")
	private String videoId;

	@Column(name = "albumId")
	private String albumId;

	@Column(name = "videoTitle")
	private String videoTitle;
	
	private boolean todayFlag;
	private boolean earlierFlag;

	@Column(name = "videoImage")
	private String videoImage;

	/** 上次打开时间，时间戳 */
	@Column(name = "lastOpenTime")
	private long lastOpenTime;

	/** 上次播放时间，播放位置，单位为秒 */
	@Column(name = "lastPlayTime")
	private long lastPlayTime;

	/** 下一集VideoId */
	@Column(name = "nextLinkUrl")
	private String nextLinkUrl;

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getAlbumId() {
		return albumId;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}

	public String getVideoTitle() {
		return videoTitle;
	}

	public void setVideoTitle(String videoTitle) {
		this.videoTitle = videoTitle;
	}

	public String getVideoImage() {
		return videoImage;
	}

	public void setVideoImage(String videoImage) {
		this.videoImage = videoImage;
	}

	public long getLastOpenTime() {
		return lastOpenTime;
	}

	public void setLastOpenTime(long lastOpenTime) {
		this.lastOpenTime = lastOpenTime;
	}

	public long getLastPlayTime() {
		return lastPlayTime;
	}

	public void setLastPlayTime(long lastPlayTime) {
		this.lastPlayTime = lastPlayTime;
	}

	public String getNextLinkUrl() {
		return nextLinkUrl;
	}

	public void setNextLinkUrl(String nextLinkUrl) {
		this.nextLinkUrl = nextLinkUrl;
	}
	
	public String getPlayRecordId() {
		return playRecordId;
	}

	public void setPlayRecordId(String playRecordId) {
		this.playRecordId = playRecordId;
	}
	
	public boolean isTodayFlag() {
		return todayFlag;
	}

	public void setTodayFlag(boolean todayFlag) {
		this.todayFlag = todayFlag;
	}

	public boolean isEarlierFlag() {
		return earlierFlag;
	}

	public void setEarlierFlag(boolean earlierFlag) {
		this.earlierFlag = earlierFlag;
	}


	/**
	 * <b>下一集地址怎么获取? ? ?</b> <br>
	 * 将视频展示信息转换为播放记录信息
	 * */
	public PlayRecordInfo getPlayRecordInfo(DisplayVideoInfo vedioInfo) {
		this.albumId = vedioInfo.getAlbumId();
		this.videoId = vedioInfo.getVideoId();
		this.videoImage = vedioInfo.getImageUrl();
		this.lastPlayTime = vedioInfo.getLastPositon();
		// this.mPlayUrl = vedioInfo.getPlayUrl();
		this.videoTitle = vedioInfo.getVideoTitle();
		this.lastOpenTime = new Date().getTime();
		return this;
	}

	public DisplayVideoInfo getDisplayVedioInfo() {
		DisplayVideoInfo info = new DisplayVideoInfo();
		info.setAlbumId(this.albumId);
		info.setVideoId(this.videoId);
		info.setImageUrl(this.videoImage);
		info.setLastPositon(this.lastPlayTime);
		info.setDetailType(11);// 只有点播有播放记录功能
		info.setVideoTitle(this.videoTitle);
		return info;
	}

}
