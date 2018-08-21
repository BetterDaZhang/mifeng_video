package com.letv.autoapk.base.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BackHandledFragment;
import com.letv.autoapk.common.utils.Logger;

/**
 * fragment容器
 * 
 * @author xuan
 * 
 */
public class ContainerBackActivity extends BaseActivity implements BackHandledInterface {
	public static final String TAG = ContainerBackActivity.class.getSimpleName();
	public static final String FRAGMENTNAME = "fragmentname";
	private BackHandledFragment mBackHandedFragment;

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		init();
	}

	protected void init() {
		try {
			Intent intent = getIntent();
			if (intent != null && intent.hasExtra(FRAGMENTNAME)) {
				Bundle bundle = intent.getExtras();
				String name = bundle.getString(FRAGMENTNAME);
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				Fragment current = Fragment.instantiate(this, name, bundle);
				ft.replace(R.id.container, current).commit();
			} else {
				finish();
			}
		} catch (Exception e) {
			Logger.log(e);
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.base_container);
		init();
	}

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void setSelectedFragment(BackHandledFragment selectedFragment) {
		this.mBackHandedFragment = selectedFragment;
	}

	@Override
	public void onBackPressed() {
		if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
			if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
				finish();
			} else {
				getSupportFragmentManager().popBackStack();
			}
		} else {
			mBackHandedFragment.onBackProgerss();
		}
	}

}
