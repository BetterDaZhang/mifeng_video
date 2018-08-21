package com.letv.autoapk.ui.recommend;

import java.util.List;

import com.letv.autoapk.R;

import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

class HeadLinePageChangeListener implements OnPageChangeListener {

	private List<View> pageViews;
	private List<ImageView> circleImageViews;
	private int checkPosition;

	public HeadLinePageChangeListener(List<View> pageViews, List<ImageView> circleImageViews) {
		this.pageViews = pageViews;
		this.circleImageViews = circleImageViews;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		if (position == pageViews.size() - 1) {
			position = 0;
		} else if (position == 0) {
			position = pageViews.size() - 3;
		} else {
			position -= 1;
		}
		checkPosition = position;
		for (int i = 0; i < circleImageViews.size(); i++) {
			circleImageViews.get(position).setImageResource(R.drawable.recommend_pageindicator_focused);
			if (position != i) {
				circleImageViews.get(i).setImageResource(R.drawable.recommend_pageindicator);
			}
		}
	}
	
	public int getCheckPosition(){
		return checkPosition;
	}

}
