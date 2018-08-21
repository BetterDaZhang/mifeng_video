package com.letv.autoapk.base.net;

import android.os.Handler;

public abstract class CacheListener {
	public Handler handler;

	public CacheListener(Handler h) {
		handler = h;
	}

	public abstract void onRefreshCache(Object[] mOutputData);
}