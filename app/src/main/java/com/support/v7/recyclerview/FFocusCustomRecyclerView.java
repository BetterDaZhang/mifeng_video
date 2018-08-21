package com.support.v7.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
/**
 * 横向滚动RecyclerView
 *
 */
public class FFocusCustomRecyclerView extends RecyclerView {

	private boolean fScroll = true;
	public FFocusCustomRecyclerView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public FFocusCustomRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public FFocusCustomRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void setfScroll(boolean fScroll) {
		this.fScroll = fScroll;
	}

	@Override
	public void smoothScrollBy(int dx, int dy) {
		if(fScroll){
			super.smoothScrollBy(dx, dy);// jjj125 0
			fScroll = false;
		}else{
			if (dx > 0) {
				super.scrollBy(dx + 100, dy);// jjj125 0
			} else if (dx < 0) {
				super.scrollBy(dx - 100, dy);// jjj125 0
			} else {
				super.scrollBy(dx, dy);// jjj125 0
			}
		}
	}
}
