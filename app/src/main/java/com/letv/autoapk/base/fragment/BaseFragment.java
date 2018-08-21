package com.letv.autoapk.base.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.common.utils.Logger;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseFragment extends Fragment {
	private final String TAG = "BaseFragment";
	protected View mRoot;
	protected BaseActivity mActivity;
	private Handler mDefaultHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			onHandleMessage(msg);
		}

	};
	protected abstract void onHandleMessage(Message msg);
	public Handler getDefaultHandler() {
		return mDefaultHandler;
	}
	public String getResString(int resId){
		if(getActivity()==null)
			return "";
		return super.getString(resId);
	}
	public String getResString(int resId,Object... objects){
		if(getActivity()==null)
			return "";
		return super.getString(resId, objects);
	}
	protected abstract View setupDataView();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		try {
			mRoot = setupDataView();
		} catch (Exception e) {
			Logger.log(e);
		}
		return mRoot;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (BaseActivity) getActivity();
		setHasOptionsMenu(true);
	}
	/**
	 * 友盟统计，统计页面
	 */
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(this.getClass().getName()); // 统计页面
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(this.getClass().getName());
	}
}
