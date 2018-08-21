package com.letv.autoapk.dao;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import com.letv.autoapk.base.db.Model;

@Table(name = "SearchHistoryInfo")
public class SearchHistoryInfo extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4952535532356075859L;
	@Column(name = "sarchTitle")
	private String sarchTitle;

	public SearchHistoryInfo() {
	}

	public String getSarchTitle() {
		return sarchTitle;
	}

	public void setSarchTitle(String sarchTitle) {
		this.sarchTitle = sarchTitle;
	}

}
