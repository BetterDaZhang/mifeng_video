package com.letv.autoapk.ui.player.view;

import com.lecloud.sdk.api.ad.iml.AdStatusManager;
import com.letv.ads.bean.AdElementMime;
import com.letv.autoapk.player.ShowAdPicUtils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

public class PlayerContainerLayout extends FrameLayout {

	public static final int DEFAULT_W = 16;
	public static final int DEFAULT_H = 9;
	private int portraitWidth;
	private int portraitHeight;
	private int landscapeWidth;
	private int landscapeHeight;

	private ShowAdPicUtils mInstance;
	private AdStatusManager mAdStatusManager;
	FrameLayout mAdPauseView;
	private int mPortraitWidth;
	private int mPortraitHeight;
	private int mLandscapeWidth;
	private int mLandscapeHeight;
	public PlayerContainerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(true);
		initLayoutParams();
	}
	public void showAdPic(AdElementMime adElementMime,ShowAdPicUtils.VideoPauseListener mVideoPauseListener) {
		mInstance = new ShowAdPicUtils(getContext().getApplicationContext());
		if (adElementMime != null && !TextUtils.isEmpty(adElementMime.mediaFileUrl)) {
			mAdStatusManager = new AdStatusManager(getContext().getApplicationContext(), adElementMime);
			mInstance.setmAdPicStatusListener(adPicStatusListener);
			mInstance.setmVideoPauseListener(mVideoPauseListener);
			mAdPauseView = mInstance.showAdPic(adElementMime.mediaFileUrl);
			if (!mAdPauseView.isShown()) {
				removeView(mAdPauseView);
				addView(mAdPauseView);
				bringChildToFront(mAdPauseView);
			}
		}
	}
//	ShowAdPicUtils.VideoPauseListener mVideoPauseListener = new ShowAdPicUtils.VideoPauseListener() {
//		@Override
//		public boolean isVideoPause() {
//			return videoPause();
//		}
//	};
//
//	public boolean videoPause() {
//		return !(player != null && player.isPlaying());
//	}

	ShowAdPicUtils.AdPicStatusListener adPicStatusListener = new ShowAdPicUtils.AdPicStatusListener() {
		@Override
		public void onAdPicClicked() {
			if (mAdStatusManager != null) {
				mAdStatusManager.onAdClicked();
			}
		}

		@Override
		public void onAdPicClosed() {
			if (mAdStatusManager != null) {
				mAdStatusManager.onAdClosedClicked();
			}
		}

		@Override
		public void onAdPicStarted() {
			if (mAdStatusManager != null) {
				mAdStatusManager.onAdPlayStarted();
			}
		}
	};
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
        if (mAdPauseView != null && mAdPauseView.isShown()) {
            changeAdViewSize();
            bringChildToFront(mAdPauseView);
        }
    }
	public void dismissAdPic() {
		if (mAdPauseView != null && mAdPauseView.isShown()) {
			removeView(mAdPauseView);
			mAdPauseView = null;
		}
		if (mInstance != null) {
			mInstance.closeAdPic();
		}
	}

	public void changeAdViewSize() {
		if (mInstance != null) {
			mInstance.changeAdPicSize();
		}
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			mPortraitWidth = h;
			mPortraitHeight = h * 9 / 16;
			mLandscapeWidth = w;
			mLandscapeHeight = h;
		}
	}
	public void initLayoutParams() {
		// DisplayMetrics dm = getResources().getDisplayMetrics();
		DisplayMetrics dm = new DisplayMetrics();
		if (!isInEditMode()) {
			// 造成错误的代码段
			((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
		}
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		if (screenHeight > screenWidth) {// 初始横屏状态
			screenWidth = screenHeight;
			screenHeight = dm.widthPixels;
		}
		// 记录竖屏宽高
		portraitWidth = screenHeight;
		portraitHeight = screenHeight * DEFAULT_H / DEFAULT_W;
		// 记录横屏宽高
		landscapeWidth = -1;
		landscapeHeight = -1;
	}

	public void changeLayoutParams() {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			this.getLayoutParams().width = landscapeWidth;
			this.getLayoutParams().height = landscapeHeight;
		} else {
			this.getLayoutParams().width = portraitWidth;
			this.getLayoutParams().height = portraitHeight;
		}
		requestLayout();
	}

	/*
	 * @Override protected void onMeasure(int widthMeasureSpec, int
	 * heightMeasureSpec) { if (getResources().getConfiguration().orientation ==
	 * Configuration.ORIENTATION_LANDSCAPE) { super.onMeasure(widthMeasureSpec,
	 * heightMeasureSpec); } else { int widthMode =
	 * MeasureSpec.getMode(widthMeasureSpec); int heightMode =
	 * MeasureSpec.getMode(heightMeasureSpec); int width =
	 * MeasureSpec.getSize(widthMeasureSpec); int height = width * DEFAULT_H /
	 * DEFAULT_W; if (heightMode == MeasureSpec.AT_MOST) { height =
	 * Math.min(height, MeasureSpec.getSize(heightMeasureSpec)); }
	 * setMeasuredDimension(width, height); measureChildren(widthMeasureSpec,
	 * MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)); }
	 * 
	 * }
	 */

}
