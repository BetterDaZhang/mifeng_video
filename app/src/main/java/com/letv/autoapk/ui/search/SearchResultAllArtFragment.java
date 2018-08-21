package com.letv.autoapk.ui.search;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.ui.player.PlayerAPI;
import com.letv.autoapk.widgets.NetImageView;

/** 搜索结果-综艺全部 */
public class SearchResultAllArtFragment extends BaseTitleFragment implements OnItemClickListener {

	public static final String ALL_ART = "all_art";

	private List<DisplayVideoInfo> infos = new ArrayList<DisplayVideoInfo>();

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	protected boolean loadingData() {
		// 全部综艺model，如果为空，显示无数据，不会这样，因为如果为空是不会进入到这个页面
		// 不为空返回true
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			Logger.log(e);
		}
		Bundle arguments = getArguments();
		infos = (List<DisplayVideoInfo>) arguments.get(ALL_ART);
		return true;
	}

	@Override
	protected View setupDataView() {
		View root = View.inflate(mActivity, R.layout.search_result_arts_all, null);
		setTitleLeftResource(R.drawable.mine_login_back, mActivity.dip2px(16));
		setLeftClickListener(new TitleLeftClickListener() {

			@Override
			public void onLeftClickListener() {
				getFragmentManager().popBackStack();
			}
		});
		setTitle(getString(R.string.search_nper), getResources().getColor(R.color.code6));
		ListView allArtListView = (ListView) root.findViewById(R.id.all_art_listview);
		View footView = mActivity.getLayoutInflater().inflate(R.layout.search_result_foot_view, null);
		allArtListView.addFooterView(footView);
		SearchResultAllArtAdapter adapter = new SearchResultAllArtAdapter(mActivity, infos);
		allArtListView.setAdapter(adapter);
		allArtListView.setOnItemClickListener(this);
		return root;
	}

	class SearchResultAllArtAdapter extends BaseAdapter {
		private Context context;
		private List<DisplayVideoInfo> infos;

		public SearchResultAllArtAdapter(Context context, List<DisplayVideoInfo> infos) {
			this.context = context;
			this.infos = infos;
		}

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		protected View getMyView(int position, View convertView, ViewGroup parent) {
			SearchResultAllArtHolder holder;
			if (convertView == null) {
				holder = new SearchResultAllArtHolder();
				convertView = View.inflate(context, R.layout.search_result_arts_all_item, null);
				holder.itemImg = (NetImageView) convertView.findViewById(R.id.search_result__item_img);
				holder.searchResultTitle = (TextView) convertView.findViewById(R.id.search_result__title);
				holder.searchResultPlaycount = (TextView) convertView.findViewById(R.id.search_result_playcount);
				convertView.setTag(holder);
			} else {
				holder = (SearchResultAllArtHolder) convertView.getTag();
			}
			holder.itemImg.setCoverUrl(infos.get(position).getImageUrl(), context);
			holder.searchResultTitle.setText(infos.get(position).getVideoTitle());
			holder.searchResultPlaycount.setText(infos.get(position).getPlayTimes() + "");

			return convertView;
		}
	}

	class SearchResultAllArtHolder {
		NetImageView itemImg;
		TextView searchResultTitle;
		TextView searchResultPlaycount;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position < infos.size()) {
			PlayerAPI.startPlayActivity(mActivity, infos.get(position));
		}
	}

}
