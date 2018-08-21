package com.letv.autoapk.ui.player.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlaySeekBar extends SeekBar {

	public PlaySeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}


	public PlaySeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public PlaySeekBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
    private boolean measured; 
	private Drawable progressDrawable;
    private TextView positionView;
    public void setPositionView(TextView positionView){
    	this.positionView = positionView;
    }
	public void setMyProgressDrawable(Drawable d) {
		progressDrawable = d;
	}
    public Drawable getMyProgressDrawable(){
    	return progressDrawable;
    }
	public synchronized void doRefresh(int progress) {
		final Drawable d = progressDrawable;
		if (d != null) {
			float scale = getMax() > 0 ? (float) progress / (float) getMax()
					: 0;
			d.setBounds(0, 0, (int) (getMeasuredWidth() * scale), getMeasuredHeight());
			d.invalidateSelf();
		} else {
			invalidate();
		}
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(!measured){
			doRefresh(getProgress());
			if(positionView!=null){
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) positionView.getLayoutParams();
				params.leftMargin = (getMeasuredWidth() - positionView.getMeasuredWidth()) * getProgress() / getMax();
			}
			measured = true;
		}
	};
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		final Drawable d = progressDrawable;
		if (d != null) {
			final int saveCount = canvas.save();
			canvas.translate(getPaddingLeft(), getPaddingTop());
			d.draw(canvas);
			canvas.restoreToCount(saveCount);
		}
	}

}
