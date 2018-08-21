package com.letv.autoapk.base.fragment;

import org.xutils.x;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.task.AbsTask;

import android.R.integer;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.widgets.LoadingLayout;

public abstract class BaseTitleFragment extends BaseFragment {

	private final String TAG = "BaseTitleFragment";
	protected RelativeLayout mCustomeTitleBar;
	protected FrameLayout mTitleLeftLay;
	protected LinearLayout mTitleRightLay;
	protected RelativeLayout mTitleContent;
	protected ImageView mLeftImage, mRightImage;
	protected View mContentView;
	private TitleLeftClickListener mLeftClickListener;
	private TitleRightClickListener mRightClickListener;
	protected LoadingLayout loadingLayout;
	private LoadingTask loadingTask;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Logger.d(TAG, "onCreateView");
		if (hasTitleBar()) {
			initViewHasTitle(inflater);
		} else {
			initView();
		}
		initCustomerView();
		return mRoot;
	}

	private void initView() {
		mRoot = createContentView();
	}

	public int getStatusBarHeight() {
		int result = mActivity.dip2px(25);
		int resourceId = mActivity.getResources().getIdentifier(
				"status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = mActivity.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	protected void setStatusBarColor(int color) {
		if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
			Window window = mActivity.getWindow();
			// 取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			// 设置状态栏颜色
			window.setStatusBarColor(color);
			return;
		}
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			Window window = mActivity.getWindow();
			ViewGroup mContentView = (ViewGroup) mActivity
					.findViewById(Window.ID_ANDROID_CONTENT);

			// First translucent status bar.
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			int statusBarHeight = getStatusBarHeight();

			View mChildView = mContentView.getChildAt(0);
			if (mChildView != null) {
				FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mChildView
						.getLayoutParams();
				// 如果已经为 ChildView 设置过了 marginTop, 再次调用时直接跳过
				if (lp != null && lp.topMargin < statusBarHeight
						&& lp.height != statusBarHeight) {
					// 不预留系统空间
					ViewCompat.setFitsSystemWindows(mChildView, false);
					lp.topMargin += statusBarHeight;
					mChildView.setLayoutParams(lp);
				}
			}

			View statusBarView = mContentView.getChildAt(0);
			if (statusBarView != null
					&& statusBarView.getLayoutParams() != null
					&& statusBarView.getLayoutParams().height == statusBarHeight) {
				// 避免重复调用时多次添加 View
				statusBarView.setBackgroundColor(color);
				return;
			}
			statusBarView = new View(mActivity);
			LayoutParams lp = new LayoutParams(
					LayoutParams.MATCH_PARENT, statusBarHeight);
			statusBarView.setBackgroundColor(color);
			// 向 ContentView 中添加假 View
			mContentView.addView(statusBarView, 0, lp);
		}
	}

	private void initViewHasTitle(LayoutInflater inflater) {
		mRoot = new RelativeLayout(getActivity());
		mCustomeTitleBar = (RelativeLayout) inflater.inflate(
				R.layout.base_custom_titlebar, null);
		mTitleLeftLay = (FrameLayout) mCustomeTitleBar
				.findViewById(R.id.leftLay);
		mTitleRightLay = (LinearLayout) mCustomeTitleBar
				.findViewById(R.id.rightLay);
		mTitleContent = (RelativeLayout) mCustomeTitleBar
				.findViewById(R.id.contentLay);
		mLeftImage = (ImageView) mCustomeTitleBar.findViewById(R.id.leftImage);
		mRightImage = (ImageView) mCustomeTitleBar
				.findViewById(R.id.rightImage);
		mTitleLeftLay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mLeftClickListener != null) {
					mLeftClickListener.onLeftClickListener();
				}
			}
		});

		mTitleRightLay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mRightClickListener != null) {
					mRightClickListener.onRightClickListener();
				}
			}
		});
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, mActivity.dip2px(38));
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		((RelativeLayout) mRoot).addView(mCustomeTitleBar, params);

		mContentView = createContentView();
		params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.BELOW, mCustomeTitleBar.getId());
		((RelativeLayout) mRoot).addView(mContentView, params);

	}

	protected void initCustomerView() {

	}

	protected void setTitle(String txt) {
		if (hasTitleBar()) {
			TextView tv = new TextView(mActivity);
			tv.setText(txt);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			tv.setTextColor(getActivity().getResources()
					.getColor(R.color.code1));
			setTitleContentView(tv);
		}

	}

	protected void setTitle(String txt, int color) {
		if (hasTitleBar()) {
			TextView tv = new TextView(mActivity);
			tv.setText(txt);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			tv.setTextColor(color);
			setTitleContentView(tv);
		}

	}

	protected void setTitleContentView(View v) {
		if (hasTitleBar()) {
			LayoutParams params = v.getLayoutParams();
			RelativeLayout.LayoutParams param;
			if (params == null) {
				param = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			} else {
				param = new RelativeLayout.LayoutParams(params.width,
						params.height);
			}
			param.addRule(RelativeLayout.CENTER_IN_PARENT);
			mTitleContent.addView(v, param);
		}

	}
	

	protected void setTitleLeftResource(int resId) {
		if (mLeftImage != null) {
			mLeftImage.setImageResource(resId);
		}
	}

	// 设置标题栏左部对齐
	protected void setTitleLeftResource(int resId, int padding) {
		if (mLeftImage != null) {
			mLeftImage.setImageResource(resId);
			mLeftImage.setPadding(padding, 0, 0, 0);
		}
	}

	protected void setTitleRightResource(int resId) {
		if (mRightImage != null) {
			mRightImage.setImageResource(resId);
		}
	}

	// 设置标题栏右边布局
	protected void setTitleRightResource(int resId, int padding) {
		if (mRightImage != null) {
			mRightImage.setImageResource(resId);
			mRightImage.setPadding(0, 0, padding, 0);
		}
	}

	public void setLeftClickListener(TitleLeftClickListener leftClickListener) {
		this.mLeftClickListener = leftClickListener;
	}

	public void setRightClickListener(TitleRightClickListener rightClickListener) {
		this.mRightClickListener = rightClickListener;
	}

	protected void startLoading() {
		if (loadingTask != null) {
			loadingTask.cancel();
			loadingTask = null;
		}
		loadingTask = new LoadingTask();
		x.task().start(loadingTask);
	}

	protected View createContentView() {
		loadingLayout = new LoadingLayout(mActivity) {

			@Override
			public void loadData(View loadingView) {
				// TODO Auto-generated method stub
				startLoading();

			}

			@Override
			public View createLoadedView() {
				return setupDataView();
			}

			@Override
			public boolean hasContent() {
				return hasContentData();
			}

		};
		loadingLayout.show();
		return loadingLayout;
	}

	protected abstract boolean loadingData();

	protected boolean hasTitleBar() {
		return true;
	}

	protected boolean hasContentData() {
		return true;
	}

	public interface TitleLeftClickListener {
		public void onLeftClickListener();
	}

	public interface TitleRightClickListener {
		public void onRightClickListener();
	}

	protected class LoadingTask extends AbsTask<Boolean> {

		@Override
		protected Boolean doBackground() {
			return loadingData();
		}

		@Override
		protected void onSuccess(Boolean result) {
			loadingLayout.onSuccess(result);

		}

		@Override
		protected void onError(Throwable ex, boolean isCallbackError) {
			Logger.log(ex);
			if (!isCallbackError) {
				loadingLayout.onError();
			}
		}

		@Override
		protected void onCancelled(CancelledException cex) {

		}

		@Override
		protected void onFinished() {

		}

	}
}
