package com.letv.autoapk.ui.live;

import java.util.List;

import com.letv.autoapk.R;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.ui.player.PlayerAPI;
import com.letv.autoapk.widgets.NetImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

class LiveVdeoLandsAdapter extends BaseAdapter {
	private Context context;
	private List<DisplayVideoInfo> liveVideoInfos;

	public LiveVdeoLandsAdapter(Context ctx, List<DisplayVideoInfo> infos) {
		this.context = ctx;
		this.liveVideoInfos = infos;
	}

	@Override
	public int getCount() {
		return liveVideoInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return liveVideoInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	protected View getMyView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.live_video_lands_item, null);
			holder = new Holder();
			holder.img = (NetImageView) convertView.findViewById(R.id.live_video_img);
			holder.title = (TextView) convertView.findViewById(R.id.live_video_title);
			holder.brief = (TextView) convertView.findViewById(R.id.live_video_brief);
			convertView.setTag(holder);
			setDefaultData(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		View[] views = new View[]{holder.img,holder.title,holder.brief};
		final DisplayVideoInfo info = liveVideoInfos.get(position);
		PlayerAPI.initNetImgView(context,info,views);
		holder.img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PlayerAPI.startPlayActivity(context, info); 
			}
		});
		return convertView;
	}

	void setDefaultData(Holder holder) {
		holder.img.setDefaultImageResId(R.drawable.default_img_16_10);
		holder.img.setErrorImageResId(R.drawable.default_img_16_10);
	}

	class Holder {
		NetImageView img;
		TextView title;
		TextView brief;
	}
}
