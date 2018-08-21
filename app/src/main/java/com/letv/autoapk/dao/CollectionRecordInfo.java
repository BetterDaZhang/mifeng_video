package com.letv.autoapk.dao;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import com.letv.autoapk.base.db.Model;
import com.letv.autoapk.base.net.DisplayVideoInfo;

/**
 * 
 * 
 */
@Table(name = "CollectRecord")
public class CollectionRecordInfo extends Model {

	private static final long serialVersionUID = 1L;
	/**
	 * 收藏Id
	 */
	@Column(name = "favouriteId", property = "UNIQUE")
	private String favouriteId;

	/**
	 * 视频ID
	 */
	@Column(name = "videoId", property = "UNIQUE")
	// @NotNull
	protected String videoId;
	/**
	 * 专辑ID
	 */
	@Column(name = "albumId")
	// @NotNull
	protected String albumId;

	/**
	 * 视频标题
	 */
	// @NotNull
	@Column(name = "videoTitle")
	protected String videoTitle;
	/**
	 * 视频图片
	 */
	// @NotNull
	@Column(name = "videoImage")
	protected String videoImage;
	/**
	 * 播放次数
	 */
	@Column(name = "playTimes")
	protected long playTimes;
	/**
	 * 主演列表
	 */
	// @NotNull
	@Column(name = "mainActorsDesc")
	protected String mainActorsDesc;
	/**
	 * 地区
	 */
	@Column(name = "area")
	protected String area;
	/**
	 * 类型列表
	 */
	@Column(name = "videoTypesDesc")
	protected String videoTypesDesc;
	/**
	 * 视频简介
	 */
	@Column(name = "videoBrief")
	protected String videoBrief;
	/**
	 * 年份
	 */
	@Column(name = "publishYear")
	protected String publishYear;
	/**
	 * 导演
	 */
	@Column(name = "director")
	protected String director;
	/**
	 * 音乐人
	 */
	@Column(name = "musician")
	protected String musician;
	/**
	 * 电视台名称
	 */
	@Column(name = "tvChannelName")
	// @NotNull
	protected String tvChannelName;
	/**
	 * 当前记录状态，暂时有删除和正常两种状态
	 */
	// @Column
	protected int iFlag;
	/*
	 * 嘉宾
	 */
	@Column(name = "guest")
	protected String guest;
	/**
	 * 分享的Url 地址
	 */
	@Column(name = "videoShareUrl")
	protected String videoShareUrl;

	public int getiFlag() {
		return iFlag;
	}

	public void setiFlag(int iFlag) {
		this.iFlag = iFlag;
		// flag = Flag.getFlag(iFlag);
	}

	public enum Flag {
		DELETE(0), NORMAL(1),UNKNOW(-1);
		public int status;

		Flag(int status) {
			this.status = status;
		}

		public static Flag getFlag(int status) {
			switch (status) {
			case 0:
				return DELETE;
			case 1:
				return NORMAL;
			default:
				return UNKNOW;
			}
		}
	}

	/**
	 * 当前视屏被观看的次数
	 */
	@Column(name = "number")
	// @NotNull
	private long number;
	/**
	 * 收藏ＩＤ
	 */
	@Column(name = "favoriteId")
	private String favoriteId;

	public String getFavoriteId() {
		return favoriteId;
	}

	public void setFavoriteId(String favoriteId) {
		this.favoriteId = favoriteId;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	/**
	 * 把播放历史记录Model转换为正常的BaseVedioInfo 。其中有些数据可能为空。
	 * 
	 * @error 这个暂时这样写。对于这个Model，我还需要一个当前视屏被观看的次数。
	 * @return
	 */
	public DisplayVideoInfo getDisplayVedioInfo() {
		DisplayVideoInfo vedioInfo = new DisplayVideoInfo();
		vedioInfo.setImageUrl(getVideoImage());
		vedioInfo.setVideoTitle(getVideoTitle());
		vedioInfo.setDetailType(11);// 只有点播有收藏功能
		vedioInfo.setVideoId(getVideoId());
		vedioInfo.setAlbumId(getAlbumId());
		vedioInfo.setShareUrl(getVideoShareUrl());
		return vedioInfo;
	}

	public String getFavouriteId() {
		return favouriteId;
	}

	public void setFavouriteId(String favouriteId) {
		this.favouriteId = favouriteId;
	}

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

	public long getPlayTimes() {
		return playTimes;
	}

	public void setPlayTimes(long playTimes) {
		this.playTimes = playTimes;
	}

	public String getMainActorsDesc() {
		return mainActorsDesc;
	}

	public void setMainActorsDesc(String mainActorsDesc) {
		this.mainActorsDesc = mainActorsDesc;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getVideoTypesDesc() {
		return videoTypesDesc;
	}

	public void setVideoTypesDesc(String videoTypesDesc) {
		this.videoTypesDesc = videoTypesDesc;
	}

	public String getVideoBrief() {
		return videoBrief;
	}

	public void setVideoBrief(String videoBrief) {
		this.videoBrief = videoBrief;
	}

	public String getPublishYear() {
		return publishYear;
	}

	public void setPublishYear(String publishYear) {
		this.publishYear = publishYear;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getMusician() {
		return musician;
	}

	public void setMusician(String musician) {
		this.musician = musician;
	}

	public String getTvChannelName() {
		return tvChannelName;
	}

	public void setTvChannelName(String tvChannelName) {
		this.tvChannelName = tvChannelName;
	}

	public String getGuest() {
		return guest;
	}

	public void setGuest(String guest) {
		this.guest = guest;
	}

	public String getVideoShareUrl() {
		return videoShareUrl;
	}

	public void setVideoShareUrl(String videoShareUrl) {
		this.videoShareUrl = videoShareUrl;
	}

	/**
	 * 根据已有的BaseVedioInfo。把对应的数据生成一个特定的播放记录Model。这种转换会丢失部分数据
	 * 
	 * @param vedioInfo
	 * @error 这个暂时这样写。对于这个Model，我还需要一个当前视屏被观看的次数。
	 * @return
	 */
	public CollectionRecordInfo getCollectRecord(DisplayVideoInfo vedioInfo) {
		this.videoImage = vedioInfo.getImageUrl();
		this.videoId = vedioInfo.getVideoId();
		this.videoTitle = vedioInfo.getVideoTitle();
		this.videoShareUrl = vedioInfo.getShareUrl();
		return this;
	}

	public CollectionRecordInfo() {

	}

}
