package com.letv.autoapk.ui.channel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.letv.autoapk.R;
import com.letv.autoapk.common.net.LruImageCache;
import com.letv.autoapk.widgets.CircleImageView;
import com.letv.autoapk.widgets.NetImageView;
import com.letv.autoapk.widgets.drag.DragGridBaseAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChannelTagEditAdapter extends BaseAdapter implements DragGridBaseAdapter {
	private List<ChannelVideoInfo> channelInfos;
	private LayoutInflater mInflater;
	private int mHidePosition = -1;
	private Context context;

	public ChannelTagEditAdapter(Context ctx, List<ChannelVideoInfo> infos) {
		this.context = ctx;
		this.channelInfos = infos;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return channelInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return channelInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 由于复用convertView导致某些item消失了，所以这里不复用item，
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(R.layout.channel_edit_grid_item, null);
		CircleImageView channelIcon = (CircleImageView) convertView.findViewById(R.id.channel_item_circle);
		TextView channelTitle = (TextView) convertView.findViewById(R.id.channel_item_title);

		ChannelVideoInfo channelInfo = channelInfos.get(position);
		channelIcon.setDefaultImageResId(R.drawable.default_circle_img_small);
		channelIcon.setErrorImageResId(R.drawable.default_circle_img_small);
		channelIcon.setImageUrl(channelInfo.getChannelImageUrl(), LruImageCache.getImageLoader(context));
		channelTitle.setText(channelInfo.getChannelName());
		if (position == mHidePosition) {
			convertView.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	class Holder {
		CircleImageView channelIcon;
		TextView channelTitle;
	}

	void setDefaultIconData(Holder holder) {
		holder.channelIcon.setDefaultImageResId(R.drawable.default_circle_img_small);
		holder.channelIcon.setErrorImageResId(R.drawable.default_circle_img_small);
	}

	@Override
	public void reorderItems(int oldPosition, int newPosition) {
		ChannelVideoInfo temp = channelInfos.get(oldPosition);
		if (oldPosition < newPosition) {
			for (int i = oldPosition; i < newPosition; i++) {
				Collections.swap(channelInfos, i, i + 1);
			}
		} else if (oldPosition > newPosition) {
			for (int i = oldPosition; i > newPosition; i--) {
				Collections.swap(channelInfos, i, i - 1);
			}
		}
		channelInfos.set(newPosition, temp);
	}

	@Override
	public void setHideItem(int hidePosition) {
		this.mHidePosition = hidePosition;
		notifyDataSetChanged();
	}
	

}
