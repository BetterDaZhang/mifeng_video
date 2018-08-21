package com.letv.autoapk.common.net;
@Deprecated
public interface HttpRequestCallback {
	
	public void onSuccess(Object[] mOutputData);
	
	public void onFailed(int errorCode, String msg);
	
	 
}
