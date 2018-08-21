package com.letv.autoapk.common.net;

import pl.droidsonroids.gif.GifDrawable;
import android.content.Context;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.letv.autoapk.common.net.GifLoader.GifCache;

public class LruGifCache implements GifCache{
	private static LruCache<String, GifDrawable> mMemoryCache;
	private static LruGifCache lruGifCache;
	private static GifLoader gifLoader;


	private LruGifCache() {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, GifDrawable>(cacheSize) {
			@Override
			protected int sizeOf(String key, GifDrawable drawable) {
				
				return (int)drawable.getAllocationByteCount();
			}
		};
	}

	public static LruGifCache instance(Context context) {
		if (lruGifCache == null) {
			lruGifCache = new LruGifCache();
		}
		
		return lruGifCache;
	}
	public static GifLoader getGifLoader(Context context){
		if(null == gifLoader ){
			RequestQueue queue = Volley.newRequestQueue(context,40*1024*1024);
			gifLoader = new GifLoader(queue,instance(context));
		}
		return gifLoader;
	}
	@Override
	public GifDrawable getGifDrawable(String url) {
		// TODO Auto-generated method stub
		return mMemoryCache.get(url);
	}

	@Override
	public void putGifDrawable(String url, GifDrawable drawable) {
		// TODO Auto-generated method stub
		if (getGifDrawable(url) == null) {
			mMemoryCache.put(url, drawable);
		}
	}

}
