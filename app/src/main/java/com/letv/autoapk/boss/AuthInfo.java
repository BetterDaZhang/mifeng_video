package com.letv.autoapk.boss;

/**
 * 鉴权信息
 * 
 * @author wangzhen5
 * 
 */
public class AuthInfo {
	private int status;// 0,0：鉴权失败，1：鉴权成功
	private int tryLookTime;// 360,试看时间
	private String tokenUserId;// 123123,鉴权用户id，当鉴权成功是有次参数返回
	private String token;// 1231af22"//防盗链,当鉴权成功是有次参数返回

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getTryLookTime() {
		return tryLookTime;
	}

	public void setTryLookTime(int tryLookTime) {
		this.tryLookTime = tryLookTime;
	}

	public String getTokenUserId() {
		return tokenUserId;
	}

	public void setTokenUserId(String tokenUserId) {
		this.tokenUserId = tokenUserId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
