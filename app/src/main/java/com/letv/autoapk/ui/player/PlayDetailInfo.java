package com.letv.autoapk.ui.player;

import java.util.ArrayList;
import java.util.List;

public class PlayDetailInfo {
	private PlayVideoInfo describeInfo;
	private ArrayList<PlayVideoInfo> episodeInfos;
	private ArrayList<PlayVideoInfo> recommendInfos;
	private int displayType;
	private String totalEpisodes;
	private String updateEpisode;

	public PlayVideoInfo getDescribeInfo() {
		return describeInfo;
	}

	public void setDescribeInfo(PlayVideoInfo describeInfo) {
		this.describeInfo = describeInfo;
	}

	public ArrayList<PlayVideoInfo> getEpisodeInfos() {
		if (episodeInfos == null) {
			episodeInfos = new ArrayList<PlayVideoInfo>();
		}
		return episodeInfos;
	}

	public void setEpisodeInfos(ArrayList<PlayVideoInfo> episodeInfos) {
		
		this.episodeInfos = episodeInfos;
	}

	public List<PlayVideoInfo> getRecommendInfos() {
		if (recommendInfos == null) {
			recommendInfos = new ArrayList<PlayVideoInfo>();
		}
		return recommendInfos;
	}

	public void setRecommendInfos(ArrayList<PlayVideoInfo> recommendInfos) {
		this.recommendInfos = recommendInfos;
	}

	public int getDisplayType() {
		return displayType;
	}

	public void setDisplayType(int displayType) {
		this.displayType = displayType;
	}

	public String getTotalEpisodes() {
		return totalEpisodes;
	}

	public void setTotalEpisodes(String totalEpisodes) {
		this.totalEpisodes = totalEpisodes;
	}

	public String getUpdateEpisode() {
		return updateEpisode;
	}

	public void setUpdateEpisode(String updateEpisode) {
		this.updateEpisode = updateEpisode;
	}
	
	

}
