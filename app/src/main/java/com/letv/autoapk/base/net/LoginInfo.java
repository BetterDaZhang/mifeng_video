package com.letv.autoapk.base.net;

import java.io.Serializable;

public class LoginInfo implements Serializable {
	/** 用户id */
	private String userId;
	/** 昵称 */
	private String nickName;
	/** 登录token */
	private String token;
	/** 用户头像 */
	private String userIcon;
	/** 生日，距离19700101的时间戳，单位秒 */
	private Long birthday;

	private String phoneNumber;
	/** 状态 0成功 1失败 2token过期 3token失效 */
	private int state;

	private int isVip;//0是会员，1不是会员

	public int getIsVip() {
		return isVip;
	}

	public void setIsVip(int isVip) {
		this.isVip = isVip;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getUserId() {
		return userId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserIcon() {
		return userIcon;
	}

	public void setUserIcon(String userIcon) {
		this.userIcon = userIcon;
	}

	public Long getBirthday() {
		return birthday;
	}

	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	/** 性别 0男；1女；2保密 */
	private int gender;

}
