package com.letv.autoapk.ui.player;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.base.fragment.BaseFragment;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;

class MenuEpisodeAdapter extends BaseAdapter implements OnClickListener {

	private List<PlayVideoInfo> playlist;
	private Context context;
	private String videoId;
	private String clickVideoId;

	MenuEpisodeAdapter(Context context, List<PlayVideoInfo> playlist, String videoId) {
		this.playlist = playlist;
		this.context = context;
		this.videoId = videoId;
		this.clickVideoId = videoId;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 1;
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
		if (convertView == null) {
			View scrollView = LayoutInflater.from(context).inflate(R.layout.play_menuepisode, null);
			GridLayout view = (GridLayout) scrollView.findViewById(R.id.gridlayout);
			for (PlayVideoInfo playVideoInfo : playlist) {
				try {
					View child = LayoutInflater.from(context).inflate(R.layout.play_menuepisodeitem, null);
					ChooseViewHolder holder = new ChooseViewHolder(child);
					holder.episodeTitle.setText(playVideoInfo.getEpisode());
					if (context.getString(R.string.foreshow).equals(playVideoInfo.getVideoTypeCode())) {
						holder.cornorImg.setVisibility(View.VISIBLE);
						holder.cornorImg.setText(playVideoInfo.getVideoTypeCode());
						holder.cornorImg.setBackgroundColor(context.getResources().getColor(R.color.code02));
					} else {
						holder.cornorImg.setVisibility(View.GONE);
					}
					if (MyApplication.getInstance().isNeedBoss() == 1) {
						if (playVideoInfo.getVip()) {
							holder.cornorImg.setVisibility(View.VISIBLE);
							holder.cornorImg.setText("VIP");
							holder.cornorImg.setBackgroundColor(context.getResources().getColor(R.color.vip_color));
						}
					}
					holder.videoid = playVideoInfo.getVideoId();
					if (videoId.equals(holder.videoid)) {
						holder.episodeTitle.setSelected(true);
						holder.episodeTitle.setTextColor(context.getResources().getColor(R.color.code1));
					} else {
						holder.episodeTitle.setSelected(false);
						holder.episodeTitle.setTextColor(Color.WHITE);
					}

					holder.episodeTitle.setTag(playVideoInfo);
					holder.episodeTitle.setOnClickListener(this);
					child.setTag(holder);
					view.addView(child);
				} catch (Exception e) {
					Logger.log(e);
				}
			}
			convertView = scrollView;
		} else {
			GridLayout view = (GridLayout) convertView.findViewById(R.id.gridlayout);
			for (int i = 0; i < view.getChildCount(); i++) {
				ChooseViewHolder holder = (ChooseViewHolder) view.getChildAt(i).getTag();
				if (videoId.equals(holder.videoid)) {
					holder.episodeTitle.setSelected(true);
				} else {
					holder.episodeTitle.setSelected(false);
				}
			}
		}

		return convertView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		PlayVideoInfo playVideoInfo = (PlayVideoInfo) v.getTag();
		if (v.getId() == R.id.play_detail_episode_item_title) {
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
		// 播放下一集

	}

	public static class ChooseViewHolder {
		private Button episodeTitle;
		private TextView cornorImg;
		private String videoid;

		public ChooseViewHolder(View itemView) {
			episodeTitle = (Button) itemView.findViewById(R.id.play_detail_episode_item_title);
			cornorImg = (TextView) itemView.findViewById(R.id.play_detail_episode_item_cornor);
		}
	}

}
