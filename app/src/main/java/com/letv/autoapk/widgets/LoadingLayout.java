package com.letv.autoapk.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.utils.SystemUtls;

public abstract class LoadingLayout extends RelativeLayout {

	private final String TAG = "LoadingLayout";
	/**
	 * 状态-未加载
	 */
	private static final int STATE_UNLOADED = 0;
	/**
	 * 状态-加载中
	 */
	private static final int STATE_LOADING = 1;
	/**
	 * 状态-已 加载
	 */
	private static final int STATE_LOADED = 2;

	/**
	 * 视图-未加载
	 */
	private View mUnloadedView;

	/**
	 * 视图-加载中
	 */
	private View mLoadingView;

	/**
	 * 视图-已加载
	 */
	private View mLoadedView;

	/**
	 * 无内容视图
	 */
	private View mNoContentView;

	/**
	 * 错误提示视图
	 */
	private View mErrorMsgView;

	/**
	 * 无网络提示视图
	 */
	private View mNoConnectView;

	/**
	 * 当前加载状态
	 */
	private int mState;

	private BaseActivity mActivity;

	public LoadingLayout(BaseActivity context) {
		super(context);
		mActivity = context;
		init();
	}

	public LoadingLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LoadingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void setLoadingState(int state) {
		this.mState = state;
	}

	/**
	 * 初始化
	 */
	private void init() {
		mState = STATE_UNLOADED;
		mUnloadedView = createUnloadedView();
		if (null != mUnloadedView) {
			addView(mUnloadedView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		}

		mLoadingView = createLoadingView();
		if (null != mLoadingView) {
			mLoadingView.setVisibility(View.GONE);
			LayoutParams params = (LayoutParams) mLoadingView.getLayoutParams();
			if (params == null) {
				params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			}
			addView(mLoadingView, params);
		}

		mNoConnectView = createNoConnectView();
		if (null != mNoConnectView) {
			mNoConnectView.setVisibility(View.GONE);
			addView(mNoConnectView,
					new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		}

		mNoContentView = createNoContentView();
		if (null != mNoContentView) {
			mNoContentView.setVisibility(View.GONE);
			addView(mNoContentView,
					new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		}

		mErrorMsgView = createErrorMsgView();
		if (null != mErrorMsgView) {
			mErrorMsgView.setVisibility(View.GONE);
			addView(mErrorMsgView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		}
	}

	/**
	 * 启动内容区域UI异步加载
	 */
	public void show() {
		synchronized (this) {
			switch (mState) {
			case STATE_UNLOADED:
				mState = STATE_LOADING;
				if (null != mLoadedView) {
					removeView(mLoadedView);
					mLoadedView = null;
				}

				if (null != mLoadingView) {
					mLoadingView.setVisibility(View.VISIBLE);
				}

				if (null != mErrorMsgView) {
					mErrorMsgView.setVisibility(View.GONE);
				}

				if (null != mNoContentView) {
					mNoContentView.setVisibility(View.GONE);
				}
				if (null != mNoConnectView) {
					mNoConnectView.setVisibility(View.GONE);
				}
				loadData(mLoadingView);
				break;
			case STATE_LOADING:
				break;
			case STATE_LOADED:
				break;
			}
		}
	}

	public void onSuccess(Boolean result) {
		if (!result) {
			mState = STATE_UNLOADED;
			if (mLoadedView != null) {
				removeView(mLoadedView);
			}
			if (null != mLoadingView) {
				mLoadingView.setVisibility(View.GONE);
				if (!SystemUtls.isNetworkConnected(mActivity)) {
					mNoConnectView.setVisibility(View.VISIBLE);
					mErrorMsgView.setVisibility(View.GONE);
				} else {
					mErrorMsgView.setVisibility(View.VISIBLE);
					mNoConnectView.setVisibility(View.GONE);
				}
			}
			return;
		}
		removeView(mLoadedView);
		boolean hasContent = hasContent();
		if (hasContent) {
			mLoadedView = createLoadedView();
			if (mNoContentView != null) {
				mNoContentView.setVisibility(View.GONE);
			}
			if (mErrorMsgView != null) {
				mErrorMsgView.setVisibility(View.GONE);
			}
			if (mNoConnectView != null) {
				mNoConnectView.setVisibility(View.GONE);
			}
		} else {
			if (mNoContentView != null) {
				mNoContentView.setVisibility(View.VISIBLE);
			}
			mLoadedView = mNoContentView;
		}
		if (null != mLoadedView) {
			ViewParent vp = mLoadedView.getParent();
			if (vp instanceof ViewGroup) {
				((ViewGroup) vp).removeView(mLoadedView);
			}
			ViewGroup.LayoutParams lp = mLoadedView.getLayoutParams();
			if (lp == null) {
				mLoadedView.setLayoutParams(
						new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			} else if (!(lp instanceof LayoutParams)) {
				mLoadedView.setLayoutParams(new LayoutParams(lp.width, lp.height));
			}
			mLoadedView.setVisibility(View.VISIBLE);

			measureAndLayoutLoadedView(mLoadedView);

			addView(mLoadedView);
		}
		if (null != mLoadingView) {
			mLoadingView.setVisibility(View.GONE);
		}
		if (hasContent) {
			mState = STATE_LOADED;
		} else {
			mState = STATE_UNLOADED;
		}
	}

	public void onError() {
		mState = STATE_UNLOADED;
		if (mLoadedView != null) {
			removeView(mLoadedView);
		}
		if (null != mLoadingView) {
			mLoadingView.setVisibility(View.GONE);
			if (!SystemUtls.isNetworkConnected(mActivity)) {
				mNoConnectView.setVisibility(View.VISIBLE);
				mErrorMsgView.setVisibility(View.GONE);
			} else {
				mErrorMsgView.setVisibility(View.VISIBLE);
				mNoConnectView.setVisibility(View.GONE);
			}
		}
	}

	protected View createErrorMsgView() {
		// return null;
		return createDataStatusView(R.drawable.base_ic_error, getResources().getString(R.string.base_dataerror),getResources().getString(R.string.base_dataerror2), new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Logger.e(TAG, "createErrorMsgView");
				show();
			}
		});
	}

	/**
	 * 网络连接失败，请检查设置网络
	 * 
	 * @return
	 */
	protected View createNoConnectView() {
		return createDataStatusView(R.drawable.base_wifi_no, getResources().getString(R.string.base_no_network), getResources().getString(R.string.base_networkerror2),new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				show();
			}
		});
	}

	protected View createNoContentView() {

		// return null;
		return createDataStatusView(R.drawable.base_ic_empty, getResources().getString(R.string.base_nodata),getResources().getString(R.string.base_nodata2), new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				show();
			}

		});
	}

	private View createDataStatusView(int imgResId, String messageTxt,String statusDesc, OnClickListener listener) {
		LayoutInflater mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup mView = (ViewGroup) mInflater.inflate(R.layout.base_listview_status, null);
		Button btn = (Button) mView.findViewById(R.id.refresh_btn);
		TextView Viewmesssage = (TextView) mView.findViewById(R.id.textViewMessage);
		TextView tvStatusDesc = (TextView) mView.findViewById(R.id.tv_status_desc);
		Drawable img = getResources().getDrawable(imgResId);
		img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
		Viewmesssage.setCompoundDrawables(null, img, null, null);

		Viewmesssage.setText(messageTxt);
		tvStatusDesc.setText(statusDesc);
		
		btn.setOnClickListener(listener);
		return mView;
	}

	/** 创建加载中视图 */
	protected View createLoadingView() {
		RelativeLayout layout = new RelativeLayout(mActivity);
		LayoutParams params1 = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		ProgressBar loading = new ProgressBar(mActivity);
		int side = mActivity.dip2px(50);
		LayoutParams params = new LayoutParams(side, side);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		Drawable d = getResources().getDrawable(R.drawable.base_progressbar);
		loading.setIndeterminateDrawable(d);
		loading.setLayoutParams(params);
		layout.setLayoutParams(params1);
		layout.addView(loading);
		layout.setBackgroundColor(getResources().getColor(R.color.code01));
//		layout.setBackgroundResource(R.drawable.base_background);
		return layout;
	}

	/** 创建未加载视图 */
	private View createUnloadedView() {
		return null;
	}

	public boolean hasContent() {
		return true;
	}

	private void measureAndLayoutLoadedView(View loadedView) {
		try {
			int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST);
			int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST);
			loadedView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
			loadedView.layout(getPaddingLeft(), getPaddingTop(),
					getRight() - getLeft() - getPaddingLeft() - getPaddingRight(),
					getBottom() - getTop() - getPaddingTop() - getPaddingBottom());
		} catch (Exception e) {
			Logger.log(e);
		}
	}

	public abstract View createLoadedView();

	public abstract void loadData(View loadingView);
}
