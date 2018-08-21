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
import android.widget.TextView;
import android.widget.Toast;

import com.letv.autoapk.R;
import com.letv.autoapk.widgets.NetImageView;

/**
 * 频道列表Adapter
 *
 */
public class ArtsRecyclerAdapter extends RecyclerView.Adapter<ArtsRecyclerAdapter.ViewHolder> {

	private Context context;
	private int itemLayout;
	private List<PlayVideoInfo> artsInfos;
	private Handler handler;
	private String videoId;
	private String clickVideoId;

	public ArtsRecyclerAdapter(Context context, List<PlayVideoInfo> items, int itemLayout, Handler handler,
			String videoId) {
		this.context = context;
		this.itemLayout = itemLayout;
		this.artsInfos = items;
		this.handler = handler;
		this.videoId = videoId;
		this.clickVideoId = videoId;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
		ViewHolder viewHolder = new ViewHolder(v);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		PlayVideoInfo playVideoInfo = artsInfos.get(position);
		if (videoId.equals(artsInfos.get(position).getVideoId())) {
			// holder.convertView.setBackgroundColor(context.getResources().getColor(R.color.code08));
			holder.convertView.setBackgroundResource(R.drawable.item_selected);
		} else {
			holder.convertView.setBackgroundColor(context.getResources().getColor(R.color.code01));
		}
		View[] views = new View[] { holder.netImageView, holder.recommendTitle };
		holder.netImageView.setDefaultImageResId(R.drawable.default_img_16_10);
		holder.netImageView.setErrorImageResId(R.drawable.default_img_16_10);
		PlayerAPI.initNetImgView(context, playVideoInfo, views);
		holder.netImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				videoId = artsInfos.get(position).getVideoId();
				if (clickVideoId.endsWith(videoId)) {
					Toast.makeText(context, context.getResources().getString(R.string.play_now), Toast.LENGTH_SHORT)
							.show();
				} else {
					clickVideoId = videoId;
					notifyDataSetChanged();
					// 播放下一集
					Message msg = new Message();
					msg.what = PlayConst.UPDATE_PALY_DETAIL;
					msg.obj = artsInfos.get(position);
					handler.sendMessage(msg);
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return artsInfos.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private NetImageView netImageView;
		private TextView recommendTitle;
		private View convertView;

		public ViewHolder(View itemView) {
			super(itemView);
			this.convertView = itemView;
			netImageView = (NetImageView) itemView.findViewById(R.id.play_detail_arts_img);
			recommendTitle = (TextView) itemView.findViewById(R.id.play_detail_arts_title);
		}
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public void setClickVideoId(String clickId) {
		this.clickVideoId = clickId;
	}
}
