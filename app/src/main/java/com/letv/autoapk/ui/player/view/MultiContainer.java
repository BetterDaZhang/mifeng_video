package com.letv.autoapk.ui.player.view;

import com.letv.autoapk.R;
import com.letv.autoapk.common.utils.Logger;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

public class MultiContainer extends PlayControlContainer {
	public MultiContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MultiContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MultiContainer(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private View contentView;

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		contentView = findViewById(R.id.multi_content);
//		mShowAnim = AnimationUtils.loadAnimation(getContext(),
//				R.anim.slide_in_from_end);
//		mShowAnim.setAnimationListener(this);
	}
	protected boolean lockTouch(){
		return false;
	}


	public void hide() {
		if (contentView.isShown()) {
			if (mHideAnim == null) {
				contentView.setEnabled(false);
				contentView.setVisibility(View.GONE);
				setBackgroundColor(Color.TRANSPARENT);
				requestLayout();
			} else {
				startAnimation(mHideAnim);
			}

		}
	}

	public void show() {
		if (contentView.isShown() == false) {
			if (mShowAnim != null) {
				startAnimation(mShowAnim);
			} else {
				contentView.setVisibility(View.VISIBLE);
				contentView.setEnabled(true);
				setBackgroundColor(getResources().getColor(
						R.color.player_shadow));
				requestLayout();
			}

		}

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		contentView.setVisibility(View.VISIBLE);
		if (animation == mShowAnim) {
			setBackgroundColor(getResources().getColor(R.color.player_shadow));
		} else if (animation == mHideAnim) {
			setBackgroundColor(Color.TRANSPARENT);
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if (animation == mShowAnim) {
			contentView.setVisibility(View.VISIBLE);
			setBackgroundColor(getResources().getColor(R.color.player_shadow));
		} else if (animation == mHideAnim) {
			contentView.setVisibility(View.GONE);
		}
		requestLayout();
		clearAnimation();
	}
}
