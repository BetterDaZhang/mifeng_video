package com.letv.autoapk.base.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.letv.autoapk.context.MyApplication;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

public abstract class BaseActivity extends FragmentActivity {
	/** UI线程ID */
	private int mUIThreadId;
	// public SharedPreferences sp;
	// /** 是否已经登陆 */
	// public String ISLOGIN = "isLogin";
	// /** 通知信息 */
	// public String NOTICE_LIST = "noticeList";
	// /** 是否是第一次使用 */
	// public String IS_FIRST_USE = "is_first_use";
	// /** 该三方账号是否是第一次登录 */
	// public String IS_FIRST_LOGIN = "is_first_login";
	// /** Token */
	// public String ACCESS_TOKEN = "accessToken";
	private Toast mToast;
	// Animation shake;
	/** 默认handler */
	private Handler mDefaultHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			onHandleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		// 解决切换到后台，activity被回收，再次进入应用时getActivity
		if (arg0 != null) {
			String FRAGMENTS_TAG = "android:support:fragments";
			// remove掉保存的Fragment
			arg0.remove(FRAGMENTS_TAG);
		}
		getWindow().setFormat(PixelFormat.TRANSLUCENT);// 解决surfaceView黑屏问题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
		MyApplication.getInstance().initScreenParams(this.getWindowManager().getDefaultDisplay());
		//友盟推送
		PushAgent.getInstance(this).onAppStart();
	}

	/**
	 * 友盟session统计,onresum，onpause统计应用时长
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		getDefaultHandler().removeCallbacksAndMessages(null);
	}

	/**
	 * 获取UI ID
	 * 
	 */
	public int getUIThreadId() {
		return mUIThreadId;
	}

	/**
	 * 获取handler
	 * 
	 */
	public Handler getDefaultHandler() {
		return mDefaultHandler;
	}

	public void setDefaultHandler(Handler handler) {
		mDefaultHandler = handler;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		mUIThreadId = Process.myTid();
		super.onNewIntent(intent);
	}

	public boolean post(Runnable r) {
		return mDefaultHandler.post(r);
	}

	public boolean postDelayed(Runnable r, long delayMillis) {
		return mDefaultHandler.postDelayed(r, delayMillis);
	}

	public void sendMessage(int what) {
		mDefaultHandler.sendEmptyMessage(what);
	}

	public void sendMessage(int what, long delay) {
		mDefaultHandler.sendEmptyMessageDelayed(what, delay);
	}

	public void sendMessage(int what, Object obj) {
		mDefaultHandler.obtainMessage(what, obj).sendToTarget();
	}

	public void sendMessage(int what, int arg1, int arg2) {
		mDefaultHandler.obtainMessage(what, arg1, arg2).sendToTarget();
	}

	public void sendMessage(int what, int arg1, int arg2, Object obj) {
		mDefaultHandler.obtainMessage(what, arg1, arg2, obj).sendToTarget();
	}

	public void removeCallback(Runnable r) {
		mDefaultHandler.removeCallbacks(r);
	}

	public void showToastSafe(final int resId, final int duration) {

		if (Process.myTid() == mUIThreadId) {
			if (mToast == null) {
				mToast = Toast.makeText(getBaseContext(), resId, duration);
			} else {
				mToast.setText(resId);
			}
			// UI线程
			mToast.show();
		} else {
			// 非UI线程
			post(new Runnable() {
				@Override
				public void run() {
					if (mToast == null) {
						mToast = Toast.makeText(getBaseContext(), resId, duration);
					} else {
						mToast.setText(resId);
					}
					mToast.show();
				}
			});
		}
	}

	public void showToastSafe(final CharSequence text, final int duration) {

		if (Process.myTid() == mUIThreadId) {
			if (mToast == null) {
				mToast = Toast.makeText(getBaseContext(), text, duration);
			} else {
				mToast.setText(text);
			}
			// UI线程
			mToast.show();
		} else {
			// 非UI线程
			post(new Runnable() {
				@Override
				public void run() {
					if (mToast == null) {
						mToast = Toast.makeText(getBaseContext(), text, duration);
					} else {
						mToast.setText(text);
					}
					mToast.show();
				}
			});
		}
	}

	public void refreshAdapterViewSafe(final BaseAdapter adapter) {
		if (null == adapter) {
			return;
		}
		if (Process.myTid() == mUIThreadId) {
			// UI线程
			adapter.notifyDataSetChanged();
		} else {
			// 非UI线程
			post(new Runnable() {
				@Override
				public void run() {
					adapter.notifyDataSetChanged();
				}
			});
		}
	}

	public int dip2px(float dipValue) {
		return dip2px(this, dipValue);
	}

	public int px2dip(float pxValue) {
		return px2dip(this, pxValue);
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public int getDimensionPixel(int resId) {
		return getResources().getDimensionPixelSize(resId);
	}

	public void cancelToast() {
		if (mToast != null) {
			mToast.cancel();
		}
	}

	public void onBackPressed() {
		cancelToast();
		super.onBackPressed();
	}

	protected abstract void onHandleMessage(Message msg);
}
