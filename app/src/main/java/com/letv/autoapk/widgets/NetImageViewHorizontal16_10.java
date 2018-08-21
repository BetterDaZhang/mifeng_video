package com.letv.autoapk.widgets;

import android.content.Context;
import android.util.AttributeSet;

public class NetImageViewHorizontal16_10 extends NetImageView {

	public NetImageViewHorizontal16_10(Context context, AttributeSet attr) {
		super(context, attr);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode == MeasureSpec.EXACTLY && widthMode != MeasureSpec.EXACTLY) {
			int height = MeasureSpec.getSize(heightMeasureSpec);
			int width = height * 16 / 10;
			if (widthMode == MeasureSpec.AT_MOST) {
				width = Math.min(width, MeasureSpec.getSize(widthMeasureSpec));
			}
			setMeasuredDimension(width, height);
			measureChildren(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), heightMeasureSpec);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}
