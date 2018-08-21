package com.letv.autoapk.base.activity;

import android.content.Intent;

import com.letv.autoapk.open.OpenShareActivity;

/**
 * fragment容器
 * 
 */
public class PlayVideoActivity extends OpenShareActivity {
	public interface onBackPressedListener {
		boolean onBackPressed();
	}

	private onBackPressedListener listener;

	public void setOnBackPressedListener(
			onBackPressedListener onBackPressedListener) {
		listener = onBackPressedListener;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (listener != null && listener.onBackPressed())
			return;
		super.onBackPressed();
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(intent.hasExtra(PlayVideoActivity.FRAGMENTNAME)){
		    Intent newIntent = (Intent)intent.clone();
		    finish();
		    startActivity(newIntent);
		}
	}
}
