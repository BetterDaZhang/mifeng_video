package com.letv.autoapk.widgets;

import android.content.Context;

import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class UnderlineTextView extends TextView {

	public UnderlineTextView(Context context) {
		super(context);
		getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); 
	}

	public UnderlineTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); 
	}
	

}
