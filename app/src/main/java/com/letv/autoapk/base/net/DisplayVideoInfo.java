package com.letv.autoapk.base.net;

public class DisplayVideoInfo extends BaseVideoInfo {

	/*
	 * @Override public String toString() { return "{\"id\":\"" + id +
	 * "\",\"detailType\":\"" + detailType + "\",\"shareUrl\":\"" + shareUrl +
	 * "\",\"needLogin\":\"" + needLogin + "\",\"playTimes\":\"" + playTimes +
	 * "\",\"publishTime\":\"" + publishTime + "\",\"videoDirector\":\"" +
	 * videoDirector + "\",\"videoActor\":\"" + videoActor +
	 * "\",\"superScripType\":\"" + superScripType + "\",\"superScripColor\":\""
	 * + superScripColor + "\",\"superScripName\":\"" + superScripName +
	 * "\",\"subscriptType\":\"" + subscriptType + "\",\"subscriptName\":\"" +
	 * subscriptName + "\",\"isPush\":\"" + isPush + "\",\"activeChannel\":\"" +
	 * activeChannel + "\",\"iconType\":\"" + iconType + "\"}"; }
	 */

	/**
	 * 序列化Id
	 */
	private static final long serialVersionUID = 7353795576352529774L;
	/**
	 * id
	 */
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 下行页类型 0直播1点播2专题3web页
	 */
	private int detailType;

	/**
	 * 分享的Url 地址
	 */
	private String shareUrl;
	/**
	 * 是否需要登录 0,不需要，1,需要
	 */
	private int needLogin;
	/**
	 * 视频播放次数
	 */
	private long playTimes;
	/**
	 * 视频发布时间(时间戳)
	 */
	private String publishTime;

	private String videoDirector;

	private String videoActor;

	/**
	 * 视频左角标样式 0: 无 1: 样式 活动 2: 样式2 直播 3: 样式3 会员
	 */
	private int superScripType;
	/**
	 * 视频左角标颜色
	 */
	private int superScripColor;
	/**
	 * 视频左角标文字
	 */
	private String superScripName;
	/**
	 * 右下角标类型<br>
	 * 0: 无 1: 电影显示评分 2: 直播活动不显示 3: 点播显示时间
	 */
	private int subscriptType;
	/**
	 * 右下角标文字
	 */
	private String subscriptName;
	/**
	 * 是否有权限下载
	 */
	private String downloadPlatform;

	private boolean isPush = false;

	/**
	 * 下一集
	 */
	private String nextLinkUrl;

	private String episode;
	private int displayType;

	public String getEpisode() {
		return episode;
	}

	public void setEpisode(String episode) {
		this.episode = episode;
	}

	public int getDisplayType() {
		return displayType;
	}

	public void setDisplayType(int displayType) {
		this.displayType = displayType;
	}

	public String getNextLinkUrl() {
		return nextLinkUrl;
	}

	public void setNextLinkUrl(String nextLinkUrl) {
		this.nextLinkUrl = nextLinkUrl;
	}

	public int getDetailType() {
		return detailType;
	}

	public void setDetailType(int detailType) {
		this.detailType = detailType;
	}

	public String getShareUrl() {
		return shareUrl;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}

	public int getSuperScripType() {
		return superScripType;
	}

	public void setSuperScripType(int superScripType) {
		this.superScripType = superScripType;
	}

	public int getSuperScripColor() {
		return superScripColor;
	}

	public void setSuperScripColor(int superScripColor) {
		this.superScripColor = superScripColor;
	}

	public String getSuperScripName() {
		return superScripName;
	}

	public void setSuperScripName(String superScripName) {
		this.superScripName = superScripName;
	}

	public int getNeedLogin() {
		return needLogin;
	}

	public void setNeedLogin(int needLogin) {
		this.needLogin = needLogin;
	}

	public int getSubscriptType() {
		return subscriptType;
	}

	public void setSubscriptType(int subscriptType) {
		this.subscriptType = subscriptType;
	}

	public String getSubscriptName() {
		return subscriptName;
	}

	public void setSubscriptName(String subscriptName) {
		this.subscriptName = subscriptName;
	}

	public long getPlayTimes() {
		return playTimes;
	}

	public void setPlayTimes(long playTimes) {
		this.playTimes = playTimes;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public String getVideoDirector() {
		return videoDirector;
	}

	public void setVideoDirector(String videoDirector) {
		this.videoDirector = videoDirector;
	}

	public String getVideoActor() {
		return videoActor;
	}

	public void setVideoActor(String videoActor) {
		this.videoActor = videoActor;
	}

	public boolean isPush() {
		return isPush;
	}

	public void setPush(boolean isPush) {
		this.isPush = isPush;
	}

	public String getDownloadPlatform() {
		return downloadPlatform;
	}

	public void setDownloadPlatform(String downloadPlatform) {
		this.downloadPlatform = downloadPlatform;
	}

}
