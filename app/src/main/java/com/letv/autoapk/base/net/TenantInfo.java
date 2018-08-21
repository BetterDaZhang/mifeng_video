package com.letv.autoapk.base.net;

public class TenantInfo {
	/**
	 * 租户Id
	 */
	private String tenantId;
	/**
	 * 用户Id
	 */
	private String userId;
	/**
	 * pageID
	 */
	private String pageId;

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

}
