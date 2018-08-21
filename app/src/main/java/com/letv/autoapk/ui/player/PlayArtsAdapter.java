package com.letv.autoapk.ui.player;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.autoapk.R;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.widgets.NetImageView;

public class PlayArtsAdapter extends BaseAdapter {
	private Context context;
	private Handler handler;
	private List<PlayVideoInfo> artsInfos;
	private String videoId;
	private String clickVideoId;

	public PlayArtsAdapter(Context ctx, Handler handler, List<PlayVideoInfo> infos, String videoId) {
		this.context = ctx;
		this.handler = handler;
		this.artsInfos = infos;
		this.videoId = videoId;
		this.clickVideoId = videoId;
	}

	@Override
	public int getCount() {
		return artsInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return artsInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	protected View getMyView(final int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.play_arts_item, null);
			holder = new Holder();
			holder.netIamgevew = (NetImageView) convertView.findViewById(R.id.play_arts_img);
			holder.artsTitle = (TextView) convertView.findViewById(R.id.play_arts_item_title);
			holder.artsDirector = (TextView) convertView.findViewById(R.id.play_arts_item_director);
			holder.artsActor = (TextView) convertView.findViewById(R.id.play_arts_item_actor);
			holder.playCounts = (TextView) convertView.findViewById(R.id.play_arts_item_playTimes);
			holder.background = convertView.findViewById(R.id.arts_back);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.artsTitle.setText(artsInfos.get(position).getVideoType());
		holder.artsDirector.setText(artsInfos.get(position).getVideoDirector());
		holder.artsActor.setText(artsInfos.get(position).getVideoActor());
		// holder.playCounts.setText();
		holder.playCounts.setVisibility(View.INVISIBLE);
		View[] views = new View[] { holder.netIamgevew, holder.artsTitle };
		holder.netIamgevew.setDefaultImageResId(R.drawable.default_img_16_10);
		holder.netIamgevew.setErrorImageResId(R.drawable.default_img_16_10);
		PlayerAPI.initNetImgView(context, artsInfos.get(position), views);
		if (videoId.equals(artsInfos.get(position).getVideoId())) {
			// convertView.setBackgroundColor(context.getResources().getColor(R.color.code08));
			holder.background.setBackgroundResource(R.drawable.item_selected);
		} else {
			holder.background.setBackgroundColor(context.getResources().getColor(R.color.code01));
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (position < artsInfos.size()) {
					videoId = artsInfos.get(position).getVideoId();
					if (clickVideoId.endsWith(videoId)) {
						Toast.makeText(context, context.getResources().getString(R.string.play_now), Toast.LENGTH_SHORT)
								.show();
					} else {
						clickVideoId = videoId;
						notifyDataSetChanged();
						// 更新剧集
						Message msg = new Message();
						msg.what = PlayConst.UPDATE_PALY_DETAIL;
						msg.obj = artsInfos.get(position);
						msg.arg1 = PlayConst.IFEPISODEFRAGMENTSHOE;
						handler.sendMessage(msg);
					}
				}
			}
		});
		return convertView;
	}

	class Holder {
		private NetImageView netIamgevew;
		private TextView artsTitle;
		private TextView artsDirector;
		private TextView artsActor;
		private TextView playCounts;
		private View background;

		public Holder() {

		}
	}
	
	void setVideoId(String id) {
		this.videoId = id;
		clickVideoId = videoId;
	}

}
