package com.letv.recorder.ui;

import com.letv.recorder.util.LeLog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class SurfaceFrameLayout extends FrameLayout{
	private OnListener listener;

	public SurfaceFrameLayout(Context context) {
		super(context);
	}
    public SurfaceFrameLayout(Context context, AttributeSet attrs) {
    	super(context, attrs);
    }

    public SurfaceFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    	super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	  if (ev.getAction() == MotionEvent.ACTION_DOWN) {
    		  if(listener != null){
    			  listener.onTouch();
    		  }
          }
    	  
    	return super.onInterceptTouchEvent(ev);
    }

    public void setListener(OnListener listener){
    	this.listener = listener;
    }
    public interface OnListener{
    	public void onTouch();
    }
}
