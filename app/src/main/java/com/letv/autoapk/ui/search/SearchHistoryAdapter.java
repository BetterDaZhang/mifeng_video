package com.letv.autoapk.ui.search;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.dao.SearchHistoryInfo;

/** 联想词adapter */
class SearchHistoryAdapter extends BaseAdapter {

	Context context;
	List<SearchHistoryInfo> searchHistoryInfos;

	public SearchHistoryAdapter(Context context, List<SearchHistoryInfo> searchHistoryInfos) {
		this.context = context;
		this.searchHistoryInfos = searchHistoryInfos;
	}

	@Override
	public int getCount() {
		return searchHistoryInfos.size()>8?8:searchHistoryInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return searchHistoryInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	protected View getMyView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.search_history_item, null);
			holder.search_history_tv = (TextView) convertView.findViewById(R.id.search_history_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.search_history_tv.setText(searchHistoryInfos.get(position).getSarchTitle());
		return convertView;
	}
}
