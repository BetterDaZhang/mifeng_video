package com.letv.autoapk.ui.player;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.autoapk.R;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.context.MyApplication;

public class PlayEpisodeSubAdapter extends BaseAdapter {
	private Context context;
	private Handler handler;
	private List<PlayVideoInfo> episodeInfos;
	private String videoId;
	private String clickVideoId;

	public PlayEpisodeSubAdapter(Context ctx, Handler handler, List<PlayVideoInfo> infos, String videoId) {
		this.context = ctx;
		this.handler = handler;
		this.episodeInfos = infos;
		this.videoId = videoId;
		this.clickVideoId = videoId;
	}

	@Override
	public int getCount() {
		return episodeInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return episodeInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	protected View getMyView(final int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.play_detail_episode_item, null);
			holder = new Holder();
			holder.episodeTitle = (Button) convertView.findViewById(R.id.play_detail_episode_item_title);
			holder.cornorImg = (TextView) convertView.findViewById(R.id.play_detail_episode_item_cornor);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.episodeTitle.setText(episodeInfos.get(position).getEpisode());
		if (context.getString(R.string.foreshow).equals(episodeInfos.get(position).getVideoTypeCode())) {
			holder.cornorImg.setVisibility(View.VISIBLE);
			holder.cornorImg.setText(episodeInfos.get(position).getVideoType());
			holder.cornorImg.setBackgroundColor(context.getResources().getColor(R.color.code02));
		} else {
			holder.cornorImg.setVisibility(View.GONE);
		}
		if (MyApplication.getInstance().isNeedBoss() == 1) {
			if (episodeInfos.get(position).getVip()) {
				holder.cornorImg.setVisibility(View.VISIBLE);
				holder.cornorImg.setText("VIP");
				holder.cornorImg.setBackgroundColor(context.getResources().getColor(R.color.vip_color));
			}
		}
		if (videoId.equals(episodeInfos.get(position).getVideoId())) {
			holder.episodeTitle.setSelected(true);
		} else {
			holder.episodeTitle.setSelected(false);
		}
		holder.episodeTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (position < episodeInfos.size()) {
					videoId = episodeInfos.get(position).getVideoId();
					if (clickVideoId.endsWith(videoId)) {
						Toast.makeText(context, context.getResources().getString(R.string.play_now), Toast.LENGTH_SHORT)
								.show();
					} else {
						clickVideoId = videoId;
						notifyDataSetChanged();
						// 更新剧集
						Message msg = new Message();
						msg.what = PlayConst.UPDATE_PALY_DETAIL;
						msg.obj = episodeInfos.get(position);
						msg.arg1 = PlayConst.IFEPISODEFRAGMENTSHOE;
						handler.sendMessage(msg);
					}
				}
			}
		});
		return convertView;
	}

	class Holder {
		private Button episodeTitle;
		private TextView cornorImg;

		public Holder() {

		}
	}

	void setVideoId(String id) {
		this.videoId = id;
		clickVideoId = videoId;
	}

}
