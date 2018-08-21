package com.letv.autoapk.ui.live;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.letv.autoapk.R;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.base.fragment.BaseCacheTitleFragment;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.CacheListener;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.widgets.HeaderGridView;

public class LiveVideoLandsFragment extends BaseCacheTitleFragment implements OnItemClickListener {
	private String livePageId;
	private List<DisplayVideoInfo> liveVideoInfos;
	private List<DisplayVideoInfo> tempLiveVideoInfos;
	private LinearLayout liveVideoLandsLay;
	private PullToRefreshGridView liveVideoGridview;
	private BaseAdapter liveVideoListAdapter;
	private PageInfo mPageInfo;
	private int PULLFROMSTART = 1;
	private int PULLFROMEND = 2;
	private boolean isCache = false;
	private boolean isChange = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		livePageId = (String) getArguments().get("pageId");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		isCache = false;
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected boolean hasTitleBar() {
		return false;
	};

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	protected boolean hasContentData() {
		if (liveVideoInfos.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean loadingCacheData(boolean isCache) {
		if (!isChange) {
			return true;
		}
		liveVideoInfos = new ArrayList<DisplayVideoInfo>();
		tempLiveVideoInfos = new ArrayList<DisplayVideoInfo>();
		int code = loadDataRequest(isCache);
		if (code == 0) {
			return true;
		}
		return false;
	}

	private int loadDataRequest(boolean isCache) {
		mPageInfo = new PageInfo();
		tempLiveVideoInfos.clear();
		LiveVideoListRequest request = new LiveVideoListRequest(mActivity);
		request.setCacheListener(new CustomCacheListener(getDefaultHandler()));
		Map<String, String> mInputstreamParams = new HashMap<String, String>();
		mInputstreamParams.put(StringDataRequest.PAGE_ID, livePageId);
		mInputstreamParams.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputstreamParams.put(StringDataRequest.PAGE, "1");
		mInputstreamParams.put(StringDataRequest.PAGE_SIZE, String.valueOf(StringDataRequest.PAGE_SIZE_COUNT));
		int code = request.setInputParam(mInputstreamParams).setOutputData(tempLiveVideoInfos, mPageInfo)
				.request(Method.GET, isCache);
		if (code == 0) {
			liveVideoInfos.clear();
			liveVideoInfos.addAll(tempLiveVideoInfos);
		}
		return code;
	}

	@Override
	public void updateCacheView() {
		if (isCache) {
			liveVideoListAdapter.notifyDataSetChanged();
		}
	}

	class CustomCacheListener extends CacheListener {

		public CustomCacheListener(Handler h) {
			super(h);
		}

		@Override
		public void onRefreshCache(Object[] mOutputData) {
			if (tempLiveVideoInfos.size() > 0) {
				isCache = true;
				liveVideoInfos.clear();
				liveVideoInfos.addAll(tempLiveVideoInfos);
				loadingLayout.onSuccess(true);
			}
		}

	}

	@Override
	protected boolean getIsCache() {
		return isCache;
	}

	private int loadMoreDataRequest() {
		LiveVideoListRequest request = new LiveVideoListRequest(mActivity);
		Map<String, String> mInputstreamParams = new HashMap<String, String>();
		if (mPageInfo.getPageIndex() + 1 <= mPageInfo.getTotalPage()) {
			mInputstreamParams.put(StringDataRequest.PAGE_ID, livePageId);
			mInputstreamParams.put(StringDataRequest.PAGE, String.valueOf(mPageInfo.getPageIndex() + 1));
			mInputstreamParams.put(StringDataRequest.PAGE_SIZE, String.valueOf(StringDataRequest.PAGE_SIZE_COUNT));
			mInputstreamParams.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
			int code = request.setInputParam(mInputstreamParams).setOutputData(tempLiveVideoInfos,mPageInfo).request(Method.GET);
			if (code == 0) {
				liveVideoInfos.addAll(tempLiveVideoInfos);
			}
			return code;
		} else {
			return -1;
		}

	}

	@Override
	protected View setupDataView() {
		isChange = false;
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		liveVideoLandsLay = (LinearLayout) inflater.inflate(R.layout.live_video_lands_gridview, null);
		liveVideoGridview = (PullToRefreshGridView) liveVideoLandsLay.findViewById(R.id.live_video_lands_grid);
		liveVideoListAdapter = new LiveVdeoLandsAdapter(mActivity, liveVideoInfos);
		// addHeaderView 放在adapter之前
		HeaderGridView headerGridView = (HeaderGridView) liveVideoGridview.getRefreshableView();
		headerGridView.setAdapter(liveVideoListAdapter);
		headerGridView.setOnItemClickListener(this);

		// 设置gridview 下拉刷新 上拉加载
		liveVideoGridview.setMode(Mode.BOTH);
		liveVideoGridview.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				// 下拉刷新
				new ChannelAsyncTask(LiveVideoLandsFragment.this, PULLFROMSTART).execute();

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
				// TODO 上拉加载
				new ChannelAsyncTask(LiveVideoLandsFragment.this, PULLFROMEND).execute();
			}
		});

		return liveVideoLandsLay;
	}

	class ChannelAsyncTask extends UiAsyncTask<Boolean> {
		private int defalutPull;

		public ChannelAsyncTask(Fragment fragment, int d) {
			super(fragment);
			this.defalutPull = d;
		}

		@Override
		protected Boolean doBackground() {
			int code = -1;
			if (defalutPull == PULLFROMSTART) {
				isChange = true;
				code = loadDataRequest(false);
			} else {
				code = loadMoreDataRequest();
			}

			if (code == 0) {
				return true;
			} else {
				return false;
			}

		}

		@Override
		protected void post(Boolean result) {
			super.post(result);
			if (liveVideoGridview == null) {
				return;
			}
			if (result && null != liveVideoListAdapter) {
				liveVideoGridview.setMode(Mode.BOTH);
				liveVideoListAdapter.notifyDataSetChanged();
				liveVideoGridview.onRefreshComplete();
			} else {
				liveVideoGridview.onRefreshComplete();
				if (defalutPull == PULLFROMEND && mPageInfo.getPageIndex() + 1 > mPageInfo.getTotalPage()) {
					liveVideoGridview.setMode(Mode.PULL_FROM_START);
				}
			}
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}

	@Override
	protected boolean loadingData() {
		return false;
	}

}
