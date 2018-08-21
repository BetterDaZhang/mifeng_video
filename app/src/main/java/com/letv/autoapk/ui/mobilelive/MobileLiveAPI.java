package com.letv.autoapk.ui.mobilelive;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.base.activity.DetailActivity;
import com.letv.autoapk.base.activity.PlayVideoActivity;
import com.letv.autoapk.open.OpenShareActivity;

public class MobileLiveAPI {

	public static String getLiveFragmentName() {
		return MobileLiveListFragment.class.getName();
	}

	/**
	 * 开启播放页
	 * 
	 * @param context
	 * @param url
	 */
	public static void startMobileLiveFragment(Context context, String url) {
		Intent intent = new Intent(context, PlayVideoActivity.class);
		intent.putExtra(PlayVideoActivity.FRAGMENTNAME, MobileLiveVideoFragment.class.getName());
		Bundle liveBundle2 = new Bundle();
		liveBundle2.putString("anchorId", url);
		// liveBundle2.putString("anchorHeadImg",anchorHeadImg);
		// liveBundle2.putString("anchorName",anchorName);
		intent.putExtras(liveBundle2);
		context.startActivity(intent);
	}

	/**
	 * 开启移动直播列表页
	 * 
	 * @param context
	 * @param url
	 */
	public static void startMobileLiveListFragment(Context context, boolean hasTitleBar) {
		Intent intent;
		if (context instanceof DetailActivity) {
			intent = new Intent(context, ContainerActivity.class);
			intent.putExtra(ContainerActivity.FRAGMENTNAME, MobileLiveListFragment.class.getName());
		} else {
			intent = new Intent(context, DetailActivity.class);
			intent.putExtra(DetailActivity.FRAGMENTNAME, MobileLiveListFragment.class.getName());
		}
		Bundle bundle = new Bundle();
		bundle.putBoolean("hasTitleBar", hasTitleBar);
		//// liveBundle2.putString("anchorHeadImg",anchorHeadImg);
		//// liveBundle2.putString("anchorName",anchorName);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}
}
