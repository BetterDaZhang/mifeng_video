package com.letv.autoapk.base.task;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.letv.autoapk.R;

public class MyProgressDialog extends ProgressDialog {

	public MyProgressDialog(Context context) {
		super(context);
	}

	private CharSequence message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_progressbar);
		TextView textView = (TextView) findViewById(R.id.text);
		textView.setText(message);
		setScreenBrightness();
		setCanceledOnTouchOutside(false);
	}

	@Override
	public void setMessage(CharSequence message) {
		this.message = message;

	}

	private void setScreenBrightness() {
		Window window = getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		/**
		 * 此处设置亮度值。dimAmount代表黑暗数量，也就是昏暗的多少，设置为0则代表完全明亮。 范围是0.0到1.0
		 */
		lp.dimAmount = (float) 0.1;
		window.setAttributes(lp);
	}
}
