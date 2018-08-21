package com.letv.autoapk.boss;

public class VipInfo {
	private String id;
	/** 会员到期时间,yyyy-MM-dd HH:mm:ss */
	private String endTime;
	/** 会员等级 */
	private int level;
	/** 会员名称 */
	private String levelName;

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
