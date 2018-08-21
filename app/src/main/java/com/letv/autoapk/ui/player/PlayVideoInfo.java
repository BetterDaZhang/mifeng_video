package com.letv.autoapk.ui.player;

import com.letv.autoapk.base.net.DisplayVideoInfo;

public class PlayVideoInfo extends DisplayVideoInfo {
	private String area;
	private String subCategory;
	private String videoType;
	private String videoTypeCode;
	private String videoBrief;
	private String publishYear;
	private String musician;
	private String tvChannelName;
	private String guest;

	private String subscriptName;
	private int subscriptType;
	private String isSubscript;

	private String startTime;
	private String endTime;

	private int isVip;// 1表示影片付费，0表示影片本身不付费
	private String payPlatform = "";// 付费平台

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
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

	public String getIsSubscript() {
		return isSubscript;
	}

	public void setIsSubscript(String isSubscript) {
		this.isSubscript = isSubscript;
	}

	public String getVideoType() {
		return videoType;
	}

	public void setVideoType(String videoType) {
		this.videoType = videoType;
	}

	public String getSubscriptName() {
		return subscriptName;
	}

	public void setSubscriptName(String subscriptName) {
		this.subscriptName = subscriptName;
	}

	public int getSubscriptType() {
		return subscriptType;
	}

	public void setSubscriptType(int subscriptType) {
		this.subscriptType = subscriptType;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getIsVip() {
		return isVip;
	}

	public void setIsVip(int isVip) {
		this.isVip = isVip;
	}

	// 移动平台是否是付费 mobile后台是否勾选，勾选则有“104002”
	public boolean getVip() {
		return (isVip == 1 && payPlatform.contains("104002"));
	}

	public String getPayPlatform() {
		return payPlatform;
	}

	public void setPayPlatform(String patPlatform) {
		this.payPlatform = patPlatform;
	}

	public String getVideoTypeCode() {
		return videoTypeCode;
	}

	public void setVideoTypeCode(String videoTypeCode) {
		this.videoTypeCode = videoTypeCode;
	}
}
