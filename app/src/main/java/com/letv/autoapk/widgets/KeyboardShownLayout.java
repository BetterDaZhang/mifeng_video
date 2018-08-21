package com.letv.autoapk.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class KeyboardShownLayout extends RelativeLayout {
	private onKybdsChangeListener mListener;
	public static final byte KEYBOARD_STATE_SHOW = -3;
	public static final byte KEYBOARD_STATE_HIDE = -2;
	public static final byte KEYBOARD_STATE_INIT = -1;
	private boolean mHasInit;
	private boolean mHasKeybord;
	private int mHeight;

	public KeyboardShownLayout(Context context) {
		super(context);
	}

	public KeyboardShownLayout(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	public KeyboardShownLayout(Context context, AttributeSet attributeSet, int i) {
		super(context, attributeSet, i);
	}

	public void setOnkbdStateListener(onKybdsChangeListener listener) {
		mListener = listener;
	}

	public interface onKybdsChangeListener {
		public void onKeyBoardStateChange(int state);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (!mHasInit) {
			mHasInit = true;
			mHeight = b;
			if (mListener != null) {
				mListener.onKeyBoardStateChange(KEYBOARD_STATE_INIT);
			}
		} else {
			mHeight = mHeight < b ? b : mHeight;
		}
		if (mHasInit && mHeight - b > 128) {
			mHasKeybord = true;
			if (mListener != null) {
				mListener.onKeyBoardStateChange(KEYBOARD_STATE_SHOW);
			}
		}
		if (mHasInit && mHasKeybord && mHeight == b) {
			mHasKeybord = false;
			if (mListener != null) {
				mListener.onKeyBoardStateChange(KEYBOARD_STATE_HIDE);
			}
		}
	}

}
