package com.letv.autoapk.ui.recommend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseCacheTitleFragment;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.CacheListener;
import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.ui.search.SearchAPI;

public class RecommendVideoBlockFragment extends BaseCacheTitleFragment {
	// headline
	private LinearLayout listviewLay;
	private PullToRefreshListView mPullRefreshListView;
	private View footView;
	private TextView searchEditText;
	// 首页数据
	private DisplayBlockTitleInfo blockTitleInfo;
	private List<DisplayBlockInfo> mReommendBlockDatas = new ArrayList<DisplayBlockInfo>();
	// content list
	private RecommendVideoBlockAdapter mAdapter;
	private ArrayList<DisplayBlockInfo> recommendBlockList;

	private String pageId;
	private boolean isCache = false;
	// private String pageName;
	private PageInfo mPageInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		pageId = bundle.getString("pageId");
		// pageName = bundle.getString("pageName");
	}

	protected void initCustomerView() {
		setStatusBarColor(getResources().getColor(R.color.code04));
		setTitleLeftResource(R.drawable.base_head_back, mActivity.dip2px(16));
		setLeftClickListener(new TitleLeftClickListener() {
			@Override
			public void onLeftClickListener() {
				if (getFragmentManager().getBackStackEntryCount() > 0) {
					getFragmentManager().popBackStack();
				} else {
					if (getActivity() != null) {
						getActivity().finish();
					}
				}
			}
		});
		setRightClickListener(new TitleRightClickListener() {

			@Override
			public void onRightClickListener() {
				// 跳转到搜索
				SearchAPI.startSearch(mActivity);
			}
		});
		setTitleRightResource(R.drawable.recommend_search_head, mActivity.dip2px(16));
	}

	@Override
	protected boolean loadingCacheData(boolean isCache) {
		mPageInfo = new PageInfo();
		blockTitleInfo = new DisplayBlockTitleInfo();
		RecommendVideoBlockListDataRequest request = new RecommendVideoBlockListDataRequest(mActivity);
		request.setCacheListener(new CustomCacheListener(getDefaultHandler()));
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputParam.put("pageId", pageId);
		mInputParam.put(StringDataRequest.PAGE, "1");
		mInputParam.put(StringDataRequest.PAGE_SIZEB, String.valueOf(StringDataRequest.PAGE_SIZEB_COUNT));
		mInputParam.put(StringDataRequest.PAGE_SIZEC, String.valueOf(StringDataRequest.PAGE_SIZEC_COUNT));
		int code = request.setInputParam(mInputParam)
				.setOutputData(blockTitleInfo.getDisplayBlockTitleInfos(), blockTitleInfo, mPageInfo)
				.request(Request.Method.GET);

		if (code == 0) {
			mReommendBlockDatas.clear();
			mReommendBlockDatas.addAll(blockTitleInfo.getDisplayBlockTitleInfos());
			return true;
		}
		return false;
	}

	@Override
	public void updateCacheView() {
		if (isCache) {
			recommendBlockList.clear();
			for (DisplayBlockInfo blockInfo : mReommendBlockDatas) {
				if (blockInfo.getBlockDisplayType() == RecommendAdapter.TYPE_BLOCK_HEAD
						|| blockInfo.getBlockDisplayType() == RecommendAdapter.TYPE_BLOCK_HEAD_MORE) {
					formatBlockList(recommendBlockList, blockInfo);
				} else {
					if (blockInfo.getVideoList() != null) {
						formatBlockList(recommendBlockList, blockInfo);
					}
				}
			}
			mAdapter.notifyDataSetChanged();
		}
	}

	class CustomCacheListener extends CacheListener {

		public CustomCacheListener(Handler h) {
			super(h);
		}

		@Override
		public void onRefreshCache(Object[] mOutputData) {
			if (blockTitleInfo.getDisplayBlockTitleInfos().size() > 0) {
				isCache = true;
				mReommendBlockDatas.clear();
				mReommendBlockDatas.addAll(blockTitleInfo.getDisplayBlockTitleInfos());
				loadingLayout.onSuccess(true);
			}
		}

	}

	@Override
	protected boolean getIsCache() {
		return isCache;
	}

	@Override
	protected boolean hasContentData() {
		if (mReommendBlockDatas != null && mReommendBlockDatas.size() > 0) {
			return true;
		}
		return false;
	};

	@Override
	protected View setupDataView() {
		if (blockTitleInfo.getBlockTitle() != null) {
			setTitle(blockTitleInfo.getBlockTitle(), mActivity.getResources().getColor(R.color.code6));
		}
		initListview();
		recommendBlockList = new ArrayList<DisplayBlockInfo>();
		for (DisplayBlockInfo blockInfo : mReommendBlockDatas) {
			if (blockInfo.getBlockDisplayType() == RecommendAdapter.TYPE_BLOCK_HEAD
					|| blockInfo.getBlockDisplayType() == RecommendAdapter.TYPE_BLOCK_HEAD_MORE) {
				formatBlockList(recommendBlockList, blockInfo);
			} else {
				if (blockInfo.getVideoList() != null) {
					formatBlockList(recommendBlockList, blockInfo);
				}
			}
		}
		ListView actualListView = mPullRefreshListView.getRefreshableView();
		// Need to use the Actual ListView when registering for Context Menu
		registerForContextMenu(actualListView);
		mAdapter = new RecommendVideoBlockAdapter(mActivity, this, recommendBlockList, getDefaultHandler());
		// You can also just use setListAdapter(mAdapter) or
		// mPullRefreshListView.setAdapter(mAdapter)
		actualListView.setAdapter(mAdapter);
		// listview有数据则加载footview，否则不加载
		// if (recommendBlockList != null && recommendBlockList.size() > 0) {
		// initFootView();
		// bindListener();
		// }
		return listviewLay;
	}

	public void formatBlockList(ArrayList<DisplayBlockInfo> formatRecommendBlockList, DisplayBlockInfo blockInfo) {
		if (blockInfo.getBlockDisplayType() == RecommendAdapter.TYPE_CIRCULATE) {// 轮播图
			formatRecommendBlockList.add(blockInfo);
		} else if (blockInfo.getBlockDisplayType() == RecommendAdapter.TYPE_BLOCK_HEAD) {// 导航图
			formatRecommendBlockList.add(blockInfo);
		} else if (blockInfo.getBlockDisplayType() == RecommendAdapter.TYPE_SUBJECT) {// 导语图
			for (int i = 0; i < blockInfo.getVideoList().size(); i++) {
				DisplayBlockInfo formatBlockInfo = new DisplayBlockInfo();
				formatBlockInfo.setBlockName(blockInfo.getBlockName());
				formatBlockInfo.setBlockMoreName(blockInfo.getBlockMoreName());
				formatBlockInfo.setBlockDisplayType(blockInfo.getBlockDisplayType());
				formatBlockInfo.setBlockDetailId(blockInfo.getBlockDetailId());
				formatBlockInfo.addVideoListInfo(blockInfo.getVideoList().get(i));
				formatRecommendBlockList.add(formatBlockInfo);
			}
		} else if (blockInfo.getBlockDisplayType() == RecommendAdapter.TYPE_ONE_LANDSCAPE) {// 横图
			for (int i = 0; i < blockInfo.getVideoList().size(); i++) {
				DisplayBlockInfo formatBlockInfo = new DisplayBlockInfo();
				formatBlockInfo.setBlockName(blockInfo.getBlockName());
				formatBlockInfo.setBlockMoreName(blockInfo.getBlockMoreName());
				formatBlockInfo.setBlockDisplayType(blockInfo.getBlockDisplayType());
				formatBlockInfo.setBlockDetailId(blockInfo.getBlockDetailId());
				formatBlockInfo.addVideoListInfo(blockInfo.getVideoList().get(i));
				formatRecommendBlockList.add(formatBlockInfo);
			}
		} else if (blockInfo.getBlockDisplayType() == RecommendAdapter.TYPE_TWO_LANDSCAPE) {// 两列多行
			DisplayBlockInfo formatBlockInfo = new DisplayBlockInfo();
			for (int i = 0; i < blockInfo.getVideoList().size(); i++) {
				if (i % 2 == 0) {
					formatBlockInfo = new DisplayBlockInfo();
					formatBlockInfo.setBlockName(blockInfo.getBlockName());
					formatBlockInfo.setBlockMoreName(blockInfo.getBlockMoreName());
					formatBlockInfo.setBlockDisplayType(blockInfo.getBlockDisplayType());
					formatBlockInfo.setBlockDetailId(blockInfo.getBlockDetailId());
					formatBlockInfo.addVideoListInfo(blockInfo.getVideoList().get(i));
					if (i == blockInfo.getVideoList().size() - 1) {
						formatRecommendBlockList.add(formatBlockInfo);
					}
				} else {
					formatBlockInfo.addVideoListInfo(blockInfo.getVideoList().get(i));
					formatRecommendBlockList.add(formatBlockInfo);
				}
			}
		} else if (blockInfo.getBlockDisplayType() == RecommendAdapter.TYPE_THREE_VERTICAL) {// 三列多行
			DisplayBlockInfo formatBlockInfo = new DisplayBlockInfo();
			for (int i = 0; i < blockInfo.getVideoList().size(); i++) {
				if (i % 3 == 0) {
					formatBlockInfo = new DisplayBlockInfo();
					formatBlockInfo.setBlockName(blockInfo.getBlockName());
					formatBlockInfo.setBlockMoreName(blockInfo.getBlockMoreName());
					formatBlockInfo.setBlockDisplayType(blockInfo.getBlockDisplayType());
					formatBlockInfo.setBlockDetailId(blockInfo.getBlockDetailId());
					formatBlockInfo.addVideoListInfo(blockInfo.getVideoList().get(i));
					if (i == blockInfo.getVideoList().size() - 1) {
						formatRecommendBlockList.add(formatBlockInfo);
					}
				} else if (i % 3 == 1) {
					formatBlockInfo.addVideoListInfo(blockInfo.getVideoList().get(i));
					if (i == blockInfo.getVideoList().size() - 1) {
						formatRecommendBlockList.add(formatBlockInfo);
					}
				} else {
					formatBlockInfo.addVideoListInfo(blockInfo.getVideoList().get(i));
					formatRecommendBlockList.add(formatBlockInfo);
				}
			}
		} else if (blockInfo.getBlockDisplayType() == RecommendAdapter.TYPE_BLOCK_HEAD_MORE) {// 更多导航图
			formatRecommendBlockList.add(blockInfo);
		}

	}

	private void initListview() {
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		listviewLay = (LinearLayout) inflater.inflate(R.layout.recommend_pulltolistview, null, false);
		mPullRefreshListView = (PullToRefreshListView) listviewLay.findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setMode(Mode.BOTH);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				new UiAsyncTask<Boolean>(RecommendVideoBlockFragment.this) {

					@Override
					protected Boolean doBackground() {
						return loadingCacheData(false);
					}

					@Override
					protected void post(Boolean result) {
						super.post(result);
						if (result) {
							mPullRefreshListView.setMode(Mode.BOTH);
							notifyDataView();
						}
						mPullRefreshListView.onRefreshComplete();
					}
				}.execute();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				new UiAsyncTask<Boolean>(RecommendVideoBlockFragment.this) {

					@Override
					protected Boolean doBackground() {
						return loadMoreDataRequest();
					}

					@Override
					protected void post(Boolean result) {
						super.post(result);
						if (result) {
							notifyDataView();
							mPullRefreshListView.onRefreshComplete();
						} else if (mPageInfo.getPageIndex() + 1 > mPageInfo.getTotalPage()) {
							mPullRefreshListView.onRefreshComplete();
							mPullRefreshListView.setMode(Mode.PULL_FROM_START);
						}
					}
				}.execute();
			}
		});

		// Add an end-of-list listener
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {

			}
		});
	}

	private void notifyDataView() {
		recommendBlockList.clear();
		for (DisplayBlockInfo blockInfo : mReommendBlockDatas) {
			if (blockInfo.getBlockDisplayType() == RecommendAdapter.TYPE_BLOCK_HEAD
					|| blockInfo.getBlockDisplayType() == RecommendAdapter.TYPE_BLOCK_HEAD_MORE) {
				formatBlockList(recommendBlockList, blockInfo);
			} else {
				if (blockInfo.getVideoList() != null) {
					formatBlockList(recommendBlockList, blockInfo);
				}
			}
		}
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 初始化foot
	 * 
	 * @param scrollList
	 */
	private void initFootView() {
		footView = mActivity.getLayoutInflater().inflate(R.layout.recommend_footview, null);
		searchEditText = (TextView) footView.findViewById(R.id.recommend_search_editText);
		mPullRefreshListView.getRefreshableView().addFooterView(footView);
	}

	/**
	 * bindlistener
	 * 
	 * @param
	 */
	private void bindListener() {
		// searchEditText.setOnKeyListener(new OnKeyListener() {
		//
		// @Override
		// public boolean onKey(View v, int keyCode, KeyEvent event) {
		// if (keyCode == KeyEvent.KEYCODE_ENTER) {
		// InputMethodManager imm = (InputMethodManager) v.getContext()
		// .getSystemService(Context.INPUT_METHOD_SERVICE);
		//
		// if (imm.isActive()) {
		// imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
		// }
		// String searchContent = searchEditText.getText().toString().trim();
		// if (searchContent.isEmpty()) {
		// searchEditText.setText("");
		// Toast.makeText(getActivity(), "没有输入任何内容", Toast.LENGTH_SHORT).show();
		// return true;
		// }
		// // 去掉搜索中的所有空格
		// String searchTvString =
		// searchEditText.getText().toString().replaceAll(" ", "");
		// // 清空textvew中的字符中的空格
		// searchEditText.setText(searchTvString);
		// SearchUtils searchUtil = new SearchUtils();
		// searchUtil.startSearchResultActivity(searchTvString, mActivity);
		// return true;
		// }
		// return false;
		// }
		// });
		searchEditText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SearchAPI.startSearch(mActivity);
			}
		});
		searchEditText.setInputType(InputType.TYPE_NULL);
	}

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	protected boolean loadingData() {
		// 非缓存页面不需要重写
		return false;
	}

	private boolean loadMoreDataRequest() {
		blockTitleInfo.getDisplayBlockTitleInfos().clear();
		RecommendVideoBlockListDataRequest request = new RecommendVideoBlockListDataRequest(mActivity);
		Map<String, String> mInputParam = new HashMap<String, String>();
		if (mPageInfo.getPageIndex() + 1 <= mPageInfo.getTotalPage()) {
			mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
			mInputParam.put("pageId", pageId);
			mInputParam.put(StringDataRequest.PAGE, String.valueOf(mPageInfo.getPageIndex() + 1));
			mInputParam.put(StringDataRequest.PAGE_SIZEB, String.valueOf(StringDataRequest.PAGE_SIZEB_COUNT));
			mInputParam.put(StringDataRequest.PAGE_SIZEC, String.valueOf(StringDataRequest.PAGE_SIZEC_COUNT));
			int code = request.setInputParam(mInputParam)
					.setOutputData(blockTitleInfo.getDisplayBlockTitleInfos(), blockTitleInfo, mPageInfo)
					.request(Request.Method.GET);
			if (code == 0) {
				mReommendBlockDatas.addAll(blockTitleInfo.getDisplayBlockTitleInfos());
				return true;
			}
			return false;
		} else {
			return false;
		}
	}

}
