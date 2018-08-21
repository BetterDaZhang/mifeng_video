package com.letv.autoapk.ui.player;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.autoapk.R;
import com.letv.autoapk.context.MyApplication;

/**
 * 频道列表Adapter
 *
 */
public class EpisodeRecyclerAdapter extends RecyclerView.Adapter<EpisodeRecyclerAdapter.ViewHolder> {

	private Context context;
	private int itemLayout;
	private List<PlayVideoInfo> episodeInfos;
	private String videoId;
	private Handler handler;
	private String clickVideoId;

	public EpisodeRecyclerAdapter(Context context, List<PlayVideoInfo> items, int itemLayout, Handler handler,
			String videoId) {
		this.context = context;
		this.itemLayout = itemLayout;
		this.episodeInfos = items;
		this.videoId = videoId;
		this.clickVideoId = videoId;
		this.handler = handler;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
		ViewHolder viewHolder = new ViewHolder(v);
		v.setTag(viewHolder);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		PlayVideoInfo playVideoInfo = episodeInfos.get(position);
		holder.episodeTitle.setText(playVideoInfo.getEpisode());
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
				videoId = episodeInfos.get(position).getVideoId();
				if (clickVideoId.endsWith(videoId)) {
					Toast.makeText(context,context.getResources().getString(R.string.play_now),Toast.LENGTH_SHORT).show();
				}else {
					clickVideoId = videoId;
					notifyDataSetChanged();
					// 播放下一集
					Message msg = new Message();
					msg.what = PlayConst.UPDATE_PALY_DETAIL;
					msg.obj = episodeInfos.get(position);
					handler.sendMessage(msg);
				}
				
			}
		});
	}

	@Override
	public int getItemCount() {
		return episodeInfos.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private Button episodeTitle;
		private TextView cornorImg;

		public ViewHolder(View itemView) {
			super(itemView);
			episodeTitle = (Button) itemView.findViewById(R.id.play_detail_episode_item_title);
			cornorImg = (TextView) itemView.findViewById(R.id.play_detail_episode_item_cornor);
		}
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	
	public void setClickVideoId(String clickId){
		this.clickVideoId = clickId;
	}
	
}
