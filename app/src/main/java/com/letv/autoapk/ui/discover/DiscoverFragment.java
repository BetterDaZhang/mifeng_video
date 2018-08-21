package com.letv.autoapk.ui.discover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Request;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseCacheTitleFragment;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.CacheListener;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.ui.search.SearchAPI;

public class DiscoverFragment extends BaseCacheTitleFragment implements OnRefreshListener2<ListView> {
	private static final String TAG = "DiscoverFragment";
	private ArrayList<DiscoverHotInfo> finderHotList = new ArrayList<DiscoverHotInfo>();
	private ArrayList<DiscoverHotInfo> tempFinderHotList = new ArrayList<DiscoverHotInfo>();
	private PageInfo mPageInfo;
	private static final int PAGE_SIZE_COUNT = 5;
	private boolean isCache = false;

	@Override
	protected void onHandleMessage(Message msg) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean loadingCacheData(boolean isCache) {
		mPageInfo = new PageInfo();
		tempFinderHotList.clear();
		DiscoverRequest request = new DiscoverRequest(getActivity());
		request.setCacheListener(new CustomCacheListener(getDefaultHandler()));
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.PAGE, String.valueOf(1));
		mInputParam.put(StringDataRequest.PAGE_SIZE, String.valueOf(StringDataRequest.PAGE_SIZE_COUNT));
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputParam.put(StringDataRequest.TEMPLATE_ID, MyApplication.getInstance().getTemplate());
		if (MyApplication.getInstance().isLogin())
			mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
		int code = request.setInputParam(mInputParam).setOutputData(tempFinderHotList, mPageInfo)
				.request(Request.Method.GET, isCache);

		Logger.d(TAG, "code:" + code);
		if (code == 0) {
			finderHotList.clear();
			finderHotList.addAll(tempFinderHotList);
			return true;
		}
		return false;
	}

	@Override
	protected boolean hasContentData() {
		return !finderHotList.isEmpty();
	}

	@Override
	public void updateCacheView() {
		if (isCache) {
			adapter.notifyDataSetChanged();
		}
	}

	class CustomCacheListener extends CacheListener {

		public CustomCacheListener(Handler h) {
			super(h);
		}

		@Override
		public void onRefreshCache(Object[] mOutputData) {
			if (tempFinderHotList.size() > 0) {
				isCache = true;
				finderHotList.clear();
				finderHotList.addAll(tempFinderHotList);
				loadingLayout.onSuccess(true);
			}
		}

	}

	@Override
	protected boolean getIsCache() {
		return isCache;
	}

	private PullToRefreshListView listView;
	private DiscoverAdapter adapter;

	@Override
	protected void initCustomerView() {
		setTitle(getResString(R.string.discover_title), getResources().getColor(R.color.code6));
		setLeftClickListener(new TitleLeftClickListener() {

			@Override
			public void onLeftClickListener() {
				// 跳转到搜索
				SearchAPI.startSearch(mActivity);

			}
		});
		setTitleLeftResource(R.drawable.recommend_search_head, mActivity.dip2px(16));
	}

	@Override
	protected View setupDataView() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View root = inflater.inflate(R.layout.discover_bottom, null);

		listView = (PullToRefreshListView) root.findViewById(R.id.pull_refresh_list);
		listView.setOnRefreshListener(this);
		listView.setMode(Mode.BOTH);
		adapter = new DiscoverAdapter(mActivity, this, finderHotList);
		listView.setAdapter(adapter);
		return root;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		new PullDownLoadingTask(this, 1).execute();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		new PullDownLoadingTask(this, 2).execute();
	}

	class PullDownLoadingTask extends UiAsyncTask<Boolean> {

		private boolean isCanceled = false;
		private int pullFromStart = 1;
		private int PullFromEnd = 2;
		private int defalutPull;

		PullDownLoadingTask(Fragment fragment, int d) {
			super(fragment);
			this.defalutPull = d;
		}

		private boolean loadingNextData() {
			DiscoverRequest request = new DiscoverRequest(getActivity());
			Map<String, String> mInputParam = new HashMap<String, String>();
			if (mPageInfo.getPageIndex() + 1 <= mPageInfo.getTotalPage()) {
				mInputParam.put(StringDataRequest.PAGE, String.valueOf(mPageInfo.getPageIndex() + 1));
				mInputParam.put(StringDataRequest.PAGE_SIZE, String.valueOf(StringDataRequest.PAGE_SIZE_COUNT));
				mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());//
				mInputParam.put(StringDataRequest.TEMPLATE_ID, MyApplication.getInstance().getTemplate());
				if (MyApplication.getInstance().isLogin())
					mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
				int code = request.setInputParam(mInputParam).setOutputData(tempFinderHotList, mPageInfo)
						.request(Request.Method.GET);
				if (code == 0) {
					finderHotList.addAll(tempFinderHotList);
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		@Override
		protected void post(Boolean result) {
			super.post(result);
			if (result) {
				listView.setMode(Mode.BOTH);
				adapter.notifyDataSetChanged();
				listView.onRefreshComplete();
			} else if (defalutPull == PullFromEnd && mPageInfo.getPageIndex() + 1 > mPageInfo.getTotalPage()) {
				listView.onRefreshComplete();
				listView.setMode(Mode.PULL_FROM_START);
			}
		}

		@Override
		protected Boolean doBackground() {
			if (isCanceled) {
				return false;
			}
			if (defalutPull == PullFromEnd) {
				return loadingNextData();
			} else {
				return loadingCacheData(false);
			}
		}

	}


	@Override
	protected boolean loadingData() {
		return false;
	}
}
