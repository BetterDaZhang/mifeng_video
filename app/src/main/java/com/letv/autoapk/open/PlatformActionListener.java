package com.letv.autoapk.open;



interface PlatformActionListener {
	public void onCancel(Platform info, int type);
	public void onComplete(Platform info, int type);
	public void onError(Platform info, int type, Throwable error);
}
