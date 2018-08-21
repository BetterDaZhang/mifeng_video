package com.letv.autoapk.ui.mobilelive.view;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class MobileLiveLayout extends FrameLayout {
	public MobileLiveLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MobileLiveLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MobileLiveLayout(Context context) {
		super(context);
	}

	public static final int DEFAULT_W = 4;
	public static final int DEFAULT_H = 3;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		} else {
			int widthMode = MeasureSpec.getMode(widthMeasureSpec);
			int heightMode = MeasureSpec.getMode(heightMeasureSpec);
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = width * DEFAULT_H / DEFAULT_W;
			if (heightMode == MeasureSpec.AT_MOST) {
				height = Math.min(height,
						MeasureSpec.getSize(heightMeasureSpec));
			}
			setMeasuredDimension(width, height);
			measureChildren(widthMeasureSpec,
					MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
		}

	}
}
