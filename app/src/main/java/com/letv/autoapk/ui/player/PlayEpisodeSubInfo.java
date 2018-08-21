package com.letv.autoapk.ui.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.letv.autoapk.R.string;

public class PlayEpisodeSubInfo implements Serializable{
	private String subEpisodeTitle;
	private List<PlayVideoInfo> subEpisodes;
	
	public String getSubEpisodeTitle() {
		return subEpisodeTitle;
	}
	
	public void setSubEpisodeTitle(String subEpisodeTitle) {
		this.subEpisodeTitle = subEpisodeTitle;
	}

	public List<PlayVideoInfo> getSubEpisodes() {
		if (subEpisodes == null) {
			subEpisodes = new ArrayList<PlayVideoInfo>();
		}
		return subEpisodes;
	}
}
