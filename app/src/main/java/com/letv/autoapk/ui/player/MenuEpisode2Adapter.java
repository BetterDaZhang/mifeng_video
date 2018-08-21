package com.letv.autoapk.ui.player;

import java.util.List;

import org.xutils.common.util.DensityUtil;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.base.fragment.BaseFragment;

class MenuEpisode2Adapter extends BaseAdapter implements OnClickListener {

	private List<PlayVideoInfo> playlist;
	private Context context;
	private String videoId;
	private String clickVideoId;

	MenuEpisode2Adapter(Context context, List<PlayVideoInfo> playlist, String videoId) {
		this.playlist = playlist;
		this.context = context;
		this.videoId = videoId;
		this.clickVideoId = videoId;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return playlist.size();
	}

	@Override
	public PlayVideoInfo getItem(int position) {
		// TODO Auto-generated method stub
		return playlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	protected View getMyView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = convertView;
		if (view == null) {
			TextView textView = new TextView(context);
			textView.setLayoutParams(
					new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			textView.setTextColor(context.getResources().getColor(R.color.code7));
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			textView.setPadding(DensityUtil.dip2px(15), DensityUtil.dip2px(15), 0, DensityUtil.dip2px(15));
			textView.setGravity(Gravity.LEFT);
			view = textView;
		}
		PlayVideoInfo info = getItem(position);
		view.setTag(info);
		view.setOnClickListener(this);
		((TextView) view).setText(info.getVideoTitle());
		if (videoId.equals(info.getVideoId())) {
			((TextView) view).setTextColor(context.getResources().getColor(R.color.code1));
		} else {
			((TextView) view).setTextColor(context.getResources().getColor(R.color.code7));
		}
		return view;

	}

	@Override
	public void onClick(View v) {
		PlayVideoInfo playVideoInfo = (PlayVideoInfo) v.getTag();
		if (clickVideoId.endsWith(playVideoInfo.getVideoId())) {
			Toast.makeText(context, context.getResources().getString(R.string.play_now), Toast.LENGTH_SHORT).show();
		} else {
			clickVideoId = playVideoInfo.getVideoId();
			if (context instanceof BaseActivity) {
				FragmentManager fragmentManager = ((BaseActivity) context).getSupportFragmentManager();
				Fragment vodFragment = fragmentManager.findFragmentByTag(PlayConst.VODFRAGMENTTAG);
				if (vodFragment != null && vodFragment instanceof BaseFragment) {
					Handler vodHandler = ((BaseFragment) vodFragment).getDefaultHandler();
					Message msg = vodHandler.obtainMessage();
					msg.what = PlayConst.UPDATE_PALY_DETAIL;
					msg.obj = playVideoInfo;
					msg.sendToTarget();
				}
			}
			videoId = playVideoInfo.getVideoId();
			notifyDataSetChanged();
		}

	}

}
