package com.letv.autoapk.ui.tvlive;

public class PlayTvItemInfo {
	long beginTime;
	long endTime;
	int playState;//0表示未开始 1表示正在直播 2表示回看
	String tvItemTitle;
	String dateTime;
	int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public long getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getPlayState() {
		return playState;
	}

	public void setPlayState(int playState) {
		this.playState = playState;
	}

	public String getTvItemTitle() {
		return tvItemTitle;
	}

	public void setTvItemTitle(String tvItemTitle) {
		this.tvItemTitle = tvItemTitle;
	}

}
