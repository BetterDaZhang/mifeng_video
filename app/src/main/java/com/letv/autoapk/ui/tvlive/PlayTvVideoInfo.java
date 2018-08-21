package com.letv.autoapk.ui.tvlive;

import java.util.ArrayList;
import java.util.List;

public class PlayTvVideoInfo {
	String cid = "";
	String tvTitle = "";
	String tvDesc = "";
	String tvShareUrl = "";
	String tvImgUrl = "";
	String beginTime = "";
	String endTime = "";
	long serverTime;
	List<PlayTvDateInfo> tvDateInfos = new ArrayList<PlayTvDateInfo>();
	
	public long getServerTime() {
		return serverTime;
	}

	public void setServerTime(long serverTime) {
		this.serverTime = serverTime;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getTvTitle() {
		return tvTitle;
	}

	public void setTvTitle(String tvTitle) {
		this.tvTitle = tvTitle;
	}

	public String getTvDesc() {
		return tvDesc;
	}

	public void setTvDesc(String tvDesc) {
		this.tvDesc = tvDesc;
	}

	public String getTvShareUrl() {
		return tvShareUrl;
	}

	public void setTvShareUrl(String tvShareUrl) {
		this.tvShareUrl = tvShareUrl;
	}

	public String getTvImgUrl() {
		return tvImgUrl;
	}

	public void setTvImgUrl(String tvImgUrl) {
		this.tvImgUrl = tvImgUrl;
	}

	public List<PlayTvDateInfo> getTvDateInfos() {
		return tvDateInfos;
	}

	public void setTvDateInfos(List<PlayTvDateInfo> tvDateInfos) {
		this.tvDateInfos = tvDateInfos;
	}

}
