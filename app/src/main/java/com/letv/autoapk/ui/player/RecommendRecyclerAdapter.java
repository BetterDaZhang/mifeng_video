package com.letv.autoapk.ui.player;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.autoapk.R;
import com.letv.autoapk.widgets.NetImageView;

/**
 * 频道列表Adapter
 *
 */
public class RecommendRecyclerAdapter extends RecyclerView.Adapter<RecommendRecyclerAdapter.ViewHolder>
		implements View.OnClickListener {

	private Context context;
	private int itemLayout;
	private List<PlayVideoInfo> recommendInfos;

	public RecommendRecyclerAdapter(Context context, List<PlayVideoInfo> items, int itemLayout) {
		this.context = context;
		this.itemLayout = itemLayout;
		this.recommendInfos = items;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
		ViewHolder viewHolder = new ViewHolder(v);
		v.setOnClickListener(this);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		PlayVideoInfo playVideoInfo = recommendInfos.get(position);
		View[] views = new View[] { holder.netImageView, holder.recommendTitle };
		holder.netImageView.setDefaultImageResId(R.drawable.default_img_16_10);
		holder.netImageView.setErrorImageResId(R.drawable.default_img_16_10);
		PlayerAPI.initNetImgView(context, playVideoInfo, views);
	}

	@Override
	public int getItemCount() {
		return recommendInfos.size();
	}

	@Override
	public void onClick(View view) {
		// 播放下一集
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private NetImageView netImageView;
		private TextView recommendTitle;

		public ViewHolder(View itemView) {
			super(itemView);
			netImageView = (NetImageView) itemView.findViewById(R.id.play_detail_recommend_img);
			recommendTitle = (TextView) itemView.findViewById(R.id.play_detail_recommend_title);
		}
	}
}
