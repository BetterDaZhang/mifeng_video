package com.letv.autoapk.ui.search;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.net.DisplayVideoInfo;

/** 比如展示集数 */
class SearchResultGridAdapter extends BaseAdapter {
	private List<DisplayVideoInfo> displayVideoInfos;
	private Context context;

	public SearchResultGridAdapter(List<DisplayVideoInfo> displayVideoInfos, Context ctx) {
		this.displayVideoInfos = displayVideoInfos;
		this.context = ctx;
	}

	@Override
	public int getCount() {
		return displayVideoInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return displayVideoInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(R.layout.search_result_tv_griditem, null);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.textView.setText(String.valueOf(position));
		holder.textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stu启动播放页面
				displayVideoInfos.get(position);

			}
		});

		return convertView;
	}

	class Holder {
		TextView textView;

		public Holder(View view) {
			this.textView = (TextView) view.findViewById(R.id.search_history_tv);
		}
	}

}
