package com.letv.autoapk.download;

import java.util.List;

import com.lecloud.sdk.download.info.LeDownloadInfo;
import com.letv.autoapk.base.db.Model;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.common.utils.Logger;

/**
 * 
 */
public class LeOfflineInfo extends Model implements Cloneable,
		Comparable<LeOfflineInfo> {
	/*
	 * public Map<VideoRateType, String> getmVedioRateUrlMap() { return
	 * mVedioRateUrlMap; }
	 * 
	 * public void setmVedioRateUrlMap(Map<VideoRateType, String>
	 * mVedioRateUrlMap) { this.mVedioRateUrlMap = mVedioRateUrlMap; }
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setDisplayVideoInfo(DisplayVideoInfo displayVideoInfo) {
		this.displayVideoInfo = displayVideoInfo;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8795803714655202937L;
	private String videoTitle;
	private String vedioId;
	private String albumId;
	private String albumPicUrl;
	private int downloadState;
	private String downloadUrl;
	private String fileName;
	private String fileSavePath;
	private long progress;
	private long fileLength;
	private boolean autoResume;
	private boolean autoRename;
	private int retryCount = 0;
	private long lastTotalRxBytes = 0;
	private long lastTimeStamp = 0;
	private String imgUrl;
	private String albumName;
	private List<String> downloadUrls;
	private String downloadUrlGroup;
	private LeDownloadInfo leDownloadInfo;
	private boolean isInAlbumList;
	private DisplayVideoInfo displayVideoInfo;
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

	public DisplayVideoInfo getDisplayVideoInfo() {
		displayVideoInfo = new DisplayVideoInfo();
		displayVideoInfo.setVideoTitle(getVideoTitle());
		displayVideoInfo.setAlbumId(getAlbumId());
		return displayVideoInfo;

	}

	public LeDownloadInfo getLeDownloadInfo() {
		return leDownloadInfo;
	}

	public void setLeDownloadInfo(LeDownloadInfo leDownloadInfo) {
		this.leDownloadInfo = leDownloadInfo;
	}

	public String getAlbumId() {
		return albumId;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}

	public boolean isInAlbumList() {
		return isInAlbumList;
	}

	public void setInAlbumList(boolean isInAlbumList) {
		this.isInAlbumList = isInAlbumList;
	}

	public String getAlbumPicUrl() {
		return albumPicUrl;
	}

	public void setAlbumPicUrl(String albumPicUrl) {
		this.albumPicUrl = albumPicUrl;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	// @Transient
	// private HttpHandler<File> handler;
	/**
	 * 不同码率对应mail_url映射
	 */
	/*
	 * private Map<VideoRateType, String> mVedioRateUrlMap = new
	 * HashMap<VideoRateType, String>();
	 * 
	 * public void putVedioRateUrl(VideoRateType key, String value) { if
	 * (mVedioRateUrlMap == null) { mVedioRateUrlMap = new
	 * HashMap<VideoRateType, String>(); } mVedioRateUrlMap.put(key, value); }
	 * 
	 * public String getVedioRateUrl(VideoRateType key) { if (mVedioRateUrlMap
	 * != null && mVedioRateUrlMap.size() > 0) { return
	 * mVedioRateUrlMap.get(key); } return null; }
	 */

	// public HttpHandler<File> getHandler() {
	// return handler;
	// }
	//
	// public void setHandler(HttpHandler<File> handler) {
	// this.handler = handler;
	// }

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSavePath() {
		return fileSavePath;
	}

	public void setFileSavePath(String fileSavePath) {
		this.fileSavePath = fileSavePath;
	}

	public long getProgress() {
		return progress;
	}

	public void setProgress(long progress) {
		this.progress = progress;
	}

	public long getFileLength() {
		return fileLength;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public boolean isAutoResume() {
		return autoResume;
	}

	public void setAutoResume(boolean autoResume) {
		this.autoResume = autoResume;
	}

	public boolean isAutoRename() {
		return autoRename;
	}

	public void setAutoRename(boolean autoRename) {
		this.autoRename = autoRename;
	}

	public int getDownloadState() {
		return downloadState;
	}

	public void setDownloadState(int downloadState) {
		this.downloadState = downloadState;
	}

	public String getVideoTitle() {
		return videoTitle;
	}

	public void setVideoTitle(String videoTitle) {
		this.videoTitle = videoTitle;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public long getLastTotalRxBytes() {
		return lastTotalRxBytes;
	}

	public void setLastTotalRxBytes(long lastTotalRxBytes) {
		this.lastTotalRxBytes = lastTotalRxBytes;
	}

	public long getLastTimeStamp() {
		return lastTimeStamp;
	}

	public void setLastTimeStamp(long lastTimeStamp) {
		this.lastTimeStamp = lastTimeStamp;
	}

	public List<String> getDownloadUrls() {
		return downloadUrls;
	}

	public void setDownloadUrls(List<String> downloadUrls) {
		this.downloadUrls = downloadUrls;
	}

	public String getDownloadUrlGroup() {
		return downloadUrlGroup;
	}

	public void setDownloadUrlGroup(String downloadUrlGroup) {
		this.downloadUrlGroup = downloadUrlGroup;
	}

	//
	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj)
	// return true;
	// if (obj == null)
	// return false;
	// if (getClass() != obj.getClass())
	// return false;
	// LeOfflineInfo other = (LeOfflineInfo) obj;
	// if (vedioId == null) {
	// if (other.vedioId != null)
	// return false;
	// } else if (!vedioId.equals(other.vedioId))
	// return false;
	// return true;
	// }

	public String getVedioId() {
		return vedioId;
	}

	public void setVedioId(String vedioId) {
		this.vedioId = vedioId;
	}

	@Override
	public Object clone() {
		Object o = null;
		try {
			o = (Object) super.clone();
		} catch (CloneNotSupportedException ex) {
			Logger.log(ex);
		}

		return o;
	}

	/*
	 * @Override public String toString() { return "{\"videoTitle\":\"" +
	 * videoTitle + "\",\"vedioId\":\"" + vedioId + "\",\"albumId\":\"" +
	 * albumId + "\",\"albumPicUrl\":\"" + albumPicUrl +
	 * "\",\"downloadState\":\"" + downloadState + "\",\"downloadUrl\":\"" +
	 * downloadUrl + "\",\"fileName\":\"" + fileName + "\",\"fileSavePath\":\""
	 * + fileSavePath + "\",\"progress\":\"" + progress + "\",\"fileLength\":\""
	 * + fileLength + "\",\"autoResume\":\"" + autoResume +
	 * "\",\"autoRename\":\"" + autoRename + "\",\"retryCount\":\"" + retryCount
	 * + "\",\"lastTotalRxBytes\":\"" + lastTotalRxBytes +
	 * "\",\"lastTimeStamp\":\"" + lastTimeStamp + "\",\"imgUrl\":\"" + imgUrl +
	 * "\",\"albumName\":\"" + albumName + "\",\"downloadUrls\":\"" +
	 * downloadUrls + "\",\"downloadUrlGroup\":\"" + downloadUrlGroup +
	 * "\",\"leDownloadInfo\":\"" + leDownloadInfo + "\",\"isInAlbumList\":\"" +
	 * isInAlbumList + "\",\"displayVideoInfo\":\"" + displayVideoInfo +
	 * "\",\"episode\":\"" + episode + "\",\"displayType\":\"" + displayType +
	 * "\",\"mVedioRateUrlMap\":\"" + mVedioRateUrlMap + "\"}"; }
	 */
	@Override
	public int compareTo(LeOfflineInfo another) {
		return this.getVideoTitle().compareTo(another.getVideoTitle());
	}

}
