package com.letv.autoapk.ui.player.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.letv.autoapk.R;

public class PlayControlContainer extends FrameLayout implements AnimationListener{
	Animation mShowAnim, mHideAnim;
	Context context;
	boolean locktouch;
	public PlayControlContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup(attrs);
	}

	public PlayControlContainer(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		setup(attrs);
	}

	public PlayControlContainer(Context context) {
		super(context);
		setup(null);
	}
	private void setup(AttributeSet attrs) {
		context = getContext();
		locktouch = true;
		if(attrs != null){
			TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PlayControlContainer);
			int hide = typedArray.getResourceId(R.styleable.PlayControlContainer_hideanime, 0);
			int show = typedArray.getResourceId(R.styleable.PlayControlContainer_showanime, 0);
			typedArray.recycle();
			if(hide!=0&&show!=0){
				mShowAnim = AnimationUtils.loadAnimation(context, show);
				mShowAnim.setAnimationListener(this);

				mHideAnim = AnimationUtils.loadAnimation(context, hide);
				mHideAnim.setAnimationListener(this);
			}
		}
		
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		boolean istouch = super.onTouchEvent(event);
		return istouch||lockTouch();
	}

	protected boolean lockTouch(){
		return locktouch;
	}
	public void setLockTouch(boolean lock){
		locktouch = lock;
	}
	public void showOrhide(){
		if(isShown()){
			hide();
		}else{
			show();
		}
	}
	public void hide() {
		if(isShown())
		  startAnimation(mHideAnim);
	}

	public void show() {
		if(isShown()==false||lockTouch()==false)
			startAnimation(mShowAnim);
	}
	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		setVisibility(View.VISIBLE);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if (animation == mHideAnim) {
			setVisibility(View.INVISIBLE);
		} else if (animation == mShowAnim) {
			setVisibility(View.VISIBLE);
			requestLayout();
		}
		
		clearAnimation();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}
}
