package com.letv.autoapk.ui.tvlive;

import java.util.ArrayList;
import java.util.List;

public class PlayTvDateInfo {
	private Long dateTime;
	private List<PlayTvItemInfo> tvItemInfos = new ArrayList<PlayTvItemInfo>();

	public Long getDateTime() {
		return dateTime;
	}

	public void setDateTime(Long dateTime) {
		this.dateTime = dateTime;
	}

	public List<PlayTvItemInfo> getTvItemInfos() {
		return tvItemInfos;
	}

	public void setTvItemInfos(List<PlayTvItemInfo> tvItemInfos) {
		this.tvItemInfos = tvItemInfos;
	}
}