package com.letv.autoapk.download;


public interface AddDownLoadListener {
	public void addSuccess(LeOfflineInfo info);
	public void addFailed(String errorMsg);

}
