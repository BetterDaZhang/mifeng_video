package com.letv.autoapk.ui.main;

import java.util.List;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.lecloud.sdk.download.info.LeDownloadInfo;
import com.lecloud.sdk.download.observer.LeDownloadObserver;
import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.download.DownloadSaasCenter;
import com.letv.autoapk.open.OpenShareActivity;
import com.letv.autoapk.ui.channel.ChannelAPI;
import com.letv.autoapk.ui.discover.DiscoverAPI;
import com.letv.autoapk.ui.live.LiveAPI;
import com.letv.autoapk.ui.me.MeAPI;
import com.letv.autoapk.ui.recommend.RecommendAPI;
import com.letv.autoapk.update.UpdateApi;
import com.letv.autoapk.update.UpdateApi.GetUpgradeListener;
import com.letv.autoapk.widgets.FragmentTabIndicator;
import com.letv.autoapk.widgets.FragmentTabIndicator.OnTabSelectedListener;

public class MainActivity extends OpenShareActivity implements OnClickListener, OnTabSelectedListener {
	private Fragment[] mFragments;
	private FragmentManager mFragmentManager;
	public FragmentTabIndicator mTabIndicator;
	private int mCurrentTabIndex;
	private long exitTime;
	private DownloadSaasCenter downloadCenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		initFragments();
		mFragmentManager = getSupportFragmentManager();
		mTabIndicator = (FragmentTabIndicator) findViewById(R.id.indicator);
		mTabIndicator.setOnIndicateListener(this);
		downloadCenter = DownloadSaasCenter.getInstances(this.getApplicationContext());
		downloadCenter.allowShowMsg(false);
		// resumeDownload();
		setDefaultFragment();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		String lasttime = MyApplication.getInstance().getString(UpdateApi.UPDATE);
		if (lasttime != null && DateUtils.isToday(Long.parseLong(lasttime))) {
			return;
		}
		final UpdateApi updateApi = new UpdateApi();
		updateApi.getUpdateGrage(getApplicationContext(), new GetUpgradeListener() {

			@Override
			public void onNothing() {
				// MyApplication.getInstance().putString(UpdateApi.UPDATE,
				// String.valueOf(System.currentTimeMillis()));
			}

			@Override
			public void onNewVersion(String version, String url) {

					try {
						MyApplication.getInstance().putString(UpdateApi.UPDATE, String.valueOf(System.currentTimeMillis()));
						updateApi.showNewversionDialog(version, MainActivity.this, url);
					} catch (Exception e) {
						Logger.log(e);
					}
					

			}
		});
	}

	private void resumeDownload() {
		List<LeDownloadInfo> downloadInfoList = downloadCenter.getDownloadInfoList();
		if (downloadInfoList != null && !downloadInfoList.isEmpty()) {
			for (LeDownloadInfo leDownloadInfo : downloadInfoList) {
				if (leDownloadInfo.getDownloadState() == LeDownloadObserver.DOWLOAD_STATE_STOP
						|| leDownloadInfo.getDownloadState() == LeDownloadObserver.DOWLOAD_STATE_FAILED) {
					downloadCenter.resumeDownload(leDownloadInfo);
				}
			}
		}

	}

	@Override
	protected void init() {

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initFragments() {
		mCurrentTabIndex = 0;
		mFragments = new BaseTitleFragment[3];
		mFragments[0] = Fragment.instantiate(this, RecommendAPI.getRecommendFragmentName());
		mFragments[1] = Fragment.instantiate(this, ChannelAPI.getChannelFragmentName());
		mFragments[2] = Fragment.instantiate(this, MeAPI.getMeFragmentName());

	}

	private void setDefaultFragment() {
		mCurrentTabIndex = 0;
		FragmentTransaction mFragTransaction = mFragmentManager.beginTransaction();
		if (!mFragments[0].isAdded()) {
			mFragTransaction.add(R.id.content, mFragments[0]);
			mFragTransaction.commit();
		}
		
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onTabSelceted(View v, int which) {
		selectionTab(which);

	}

	private void selectionTab(int index) {
		if (mCurrentTabIndex != index) {
			FragmentTransaction mFragTransaction = mFragmentManager.beginTransaction();
			if (!mFragments[index].isAdded()) {
				mFragTransaction.hide(mFragments[mCurrentTabIndex]).add(R.id.content, mFragments[index]);
			} else {
				mFragTransaction.hide(mFragments[mCurrentTabIndex]).show(mFragments[index]);
			}
			mFragTransaction.commit();
			mCurrentTabIndex = index;
		}

	}

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

			if ((System.currentTimeMillis() - exitTime) > 2000) // System.currentTimeMillis()无论何时调用，肯定大于2000
			{
				Toast.makeText(getApplicationContext(), getString(R.string.exittip), Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				downloadCenter.stopAllDownload();
				downloadCenter.backupDownloadInfoList();
				finish();
				// System.exit(0);
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
