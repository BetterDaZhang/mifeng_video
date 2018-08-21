package com.letv.autoapk.ui.recommend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.PlayRecordInfo;
import com.letv.autoapk.ui.mobilelive.recorder.RecorderActivity;
import com.letv.autoapk.ui.player.PlayerAPI;
import com.letv.autoapk.ui.record.RecordsAPI;
import com.letv.autoapk.ui.search.SearchAPI;
import com.letv.autoapk.widgets.LoadingLayout;

public class RecommendFragment extends BaseCacheTitleFragment {
	// headline
	private RelativeLayout listviewLay;
	private LinearLayout recordLay;
	private TextView playRecord;
	private ImageView playRecordClose;
	private PullToRefreshListView mPullRefreshListView;
	// private View footView;
	private TextView searchEditText;
	// 首页数据
	private List<DisplayBlockInfo> mReommendDatas = new ArrayList<DisplayBlockInfo>();
	private List<DisplayBlockInfo> mTempReommendDatas = new ArrayList<DisplayBlockInfo>();
	// content list
	private RecommendAdapter mAdapter;
	private ArrayList<DisplayBlockInfo> recommendBlockList;
	private RecordTask recordTask;
	static final int PAGERSCROLL = 10;
	private boolean isCache = false;
	private PlayRecordInfo playRecordInfo;
	private PageInfo mPageInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void initCustomerView() {
		// TODO 设置logo图标
		setStatusBarColor(getResources().getColor(R.color.code04));
		setTitleLeftResource(R.drawable.recommend_search_head, mActivity.dip2px(16));
		setLeftClickListener(new TitleLeftClickListener() {
			@Override
			public void onLeftClickListener() {
				SearchAPI.startSearch(mActivity);
			}
		});
		setTitleRightResource(R.drawable.recommend_record, mActivity.dip2px(16));
		setRightClickListener(new TitleRightClickListener() {

			@Override
			public void onRightClickListener() {
				RecordsAPI.startPlayRecord(mActivity);
			}

		});
		// TextView tv = new TextView(mActivity);
		// tv.setText(R.string.app_name);
		// tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
		// tv.setCompoundDrawablePadding(mActivity.dip2px(3));
		// tv.setTextColor(getResources().getColor(R.color.code6));
		// Drawable logo = getResources().getDrawable(R.drawable.icon);
		// logo.setBounds(0, 0, mActivity.dip2px(20), mActivity.dip2px(20));
		// tv.setCompoundDrawables(logo, null, null, null);
		// setTitleContentView(tv);
		ImageView img = new ImageView(mActivity);
		img.setImageResource(R.drawable.logo);
		setTitleContentView(img);
	}

	@Override
	protected boolean loadingCacheData(boolean isCache) {
		playRecordInfo = RecordsAPI.getLastRecord();
		mTempReommendDatas.clear();
		mPageInfo = new PageInfo();
		RecommendListDataRequest request = new RecommendListDataRequest(mActivity);
		request.setCacheListener(new CustomCacheListener(getDefaultHandler()));
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputParam.put(StringDataRequest.TEMPLATE_ID, MyApplication.getInstance().getTemplate());
		mInputParam.put(StringDataRequest.PAGE, "1");
		mInputParam.put(StringDataRequest.PAGE_SIZEB, String.valueOf(StringDataRequest.PAGE_SIZEB_COUNT));
		mInputParam.put(StringDataRequest.PAGE_SIZEC, String.valueOf(StringDataRequest.PAGE_SIZEC_COUNT));
		int code = request.setInputParam(mInputParam).setOutputData(mTempReommendDatas, mPageInfo)
				.request(Request.Method.GET, isCache);
		if (code == 0) {
			mReommendDatas.clear();
			mReommendDatas.addAll(mTempReommendDatas);
			return true;
		}
		return false;
	}

	@Override
	protected boolean getIsCache() {
		return isCache;
	}

	@Override
	public void updateCacheView() {
		if (isCache) {
			recommendBlockList.clear();
			for (DisplayBlockInfo blockInfo : mReommendDatas) {
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

	@Override
	protected View createContentView() {
		loadingLayout = new LoadingLayout(mActivity) {

			@Override
			public void loadData(View loadingView) {
				// TODO Auto-generated method stub
				startLoading();

			}

			@Override
			public View createLoadedView() {
				return setupDataView();
			}

			@Override
			public boolean hasContent() {
				return hasContentData();
			}

			@Override
			public void onSuccess(Boolean result) {
				super.onSuccess(result);
				if (result) {
					initAdapter();
				}
			}
		};
		loadingLayout.show();
		return loadingLayout;
	}

	private void initAdapter() {
		mAdapter = new RecommendAdapter(mActivity, recommendBlockList, getDefaultHandler());
		ListView actualListView = mPullRefreshListView.getRefreshableView();
		registerForContextMenu(actualListView);
		actualListView.setAdapter(mAdapter);
	}

	class CustomCacheListener extends CacheListener {

		public CustomCacheListener(Handler h) {
			super(h);
		}

		@Override
		public void onRefreshCache(Object[] mOutputData) {
			if (mTempReommendDatas.size() > 0) {
				isCache = true;
				mReommendDatas.clear();
				mReommendDatas.addAll(mTempReommendDatas);
				loadingLayout.onSuccess(true);
				initAdapter();
			}
		}

	}

	@Override
	protected boolean hasContentData() {
		if (mReommendDatas != null && mReommendDatas.size() > 0) {
			return true;
		}
		return false;
	};

	@Override
	protected View setupDataView() {
		initListview();
		initRecommendRecord(listviewLay);
		recommendBlockList = new ArrayList<DisplayBlockInfo>();
		for (DisplayBlockInfo blockInfo : mReommendDatas) {
			if (blockInfo.getBlockDisplayType() == RecommendAdapter.TYPE_BLOCK_HEAD
					|| blockInfo.getBlockDisplayType() == RecommendAdapter.TYPE_BLOCK_HEAD_MORE) {
				formatBlockList(recommendBlockList, blockInfo);
			} else {
				if (blockInfo.getVideoList() != null) {
					formatBlockList(recommendBlockList, blockInfo);
				}
			}
		}
		// ListView actualListView = mPullRefreshListView.getRefreshableView();
		// // Need to use the Actual ListView when registering for Context Menu
		// registerForContextMenu(actualListView);
		// mAdapter = new RecommendAdapter(mActivity, recommendBlockList,
		// getDefaultHandler());
		// // You can also just use setListAdapter(mAdapter) or
		// // mPullRefreshListView.setAdapter(mAdapter)
		// actualListView.setAdapter(mAdapter);
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
		listviewLay = (RelativeLayout) inflater.inflate(R.layout.recommend_record_pulltolistview, null, false);
		mPullRefreshListView = (PullToRefreshListView) listviewLay.findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setMode(Mode.BOTH);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				new UiAsyncTask<Boolean>(RecommendFragment.this) {

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
				new UiAsyncTask<Boolean>(RecommendFragment.this) {

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
						}else {
							mPullRefreshListView.onRefreshComplete();
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

	private boolean loadMoreDataRequest() {
		mTempReommendDatas.clear();
		RecommendListDataRequest request = new RecommendListDataRequest(mActivity);
		Map<String, String> mInputParam = new HashMap<String, String>();
		if (mPageInfo.getPageIndex() + 1 <= mPageInfo.getTotalPage()) {
			mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
			mInputParam.put(StringDataRequest.TEMPLATE_ID, MyApplication.getInstance().getTemplate());
			mInputParam.put(StringDataRequest.PAGE, String.valueOf(mPageInfo.getPageIndex() + 1));
			mInputParam.put(StringDataRequest.PAGE_SIZEB, String.valueOf(StringDataRequest.PAGE_SIZEB_COUNT));
			mInputParam.put(StringDataRequest.PAGE_SIZEC, String.valueOf(StringDataRequest.PAGE_SIZEC_COUNT));
			int code = request.setInputParam(mInputParam).setOutputData(mTempReommendDatas, mPageInfo)
					.request(Request.Method.GET);
			if (code == 0) {
				mReommendDatas.addAll(mTempReommendDatas);
				return true;
			}
			return false;
		} else {
			return false;
		}

	}

	private void initRecommendRecord(View listviewLay) {
		recordLay = (LinearLayout) listviewLay.findViewById(R.id.recommend_play_record);
		Drawable mDrawable = new ColorDrawable(getActivity().getResources().getColor(R.color.code02)).mutate();
		mDrawable.setAlpha(205);
		recordLay.setBackgroundDrawable(mDrawable);
		playRecord = (TextView) listviewLay.findViewById(R.id.play_record_title);
		playRecordClose = (ImageView) listviewLay.findViewById(R.id.play_record_close);
		if (playRecordInfo == null) {
			recordLay.setVisibility(View.GONE);
			return;
		}
		recordLay.setVisibility(View.VISIBLE);
		String recordTitle = playRecordInfo.getVideoTitle();
		if (recordTitle != null) {
			playRecord.setText(recordTitle);
		}
		playRecordClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				recordLay.setVisibility(View.GONE);
			}
		});
		playRecord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PlayerAPI.startPlayActivity(mActivity, playRecordInfo.getDisplayVedioInfo());
			}
		});

		if (recordTask != null) {
			recordTask.cancel();
		}
		recordTask = new RecordTask();
		Timer timer = new Timer();
		timer.schedule(recordTask, 5000);
	}

	class RecordTask extends TimerTask {

		@Override
		public void run() {
			getDefaultHandler().post(new Runnable() {
				@Override
				public void run() {
					recordLay.setVisibility(View.GONE);
				}
			});
		}
	}

	private void notifyDataView() {
		recommendBlockList.clear();
		for (DisplayBlockInfo blockInfo : mReommendDatas) {
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
	/*
	 * private void initFootView() { footView =
	 * mActivity.getLayoutInflater().inflate(R.layout.recommend_footview, null);
	 * searchEditText = (TextView)
	 * footView.findViewById(R.id.recommend_search_editText);
	 * mPullRefreshListView.getRefreshableView().addFooterView(footView); }
	 */

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
	}

	/*
	 * private void renderHeaderView(List<DisplayVideoInfo> scrollList) { if
	 * (scrollList == null || scrollList.size() <= 0) return; //
	 * viewPager中view数组 List<View> pageViews = new ArrayList<View>(); for (int i
	 * = 0; i < scrollList.size() + 2; i++) { View rowView =
	 * mActivity.getLayoutInflater().inflate(R.layout.recommend_headline_vpitem,
	 * null); rowView.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { } }); pageViews.add(rowView); }
	 * 
	 * // 创建imageviews数组，大小是要显示的图片的数量 ImageView[] circleImageViews = new
	 * ImageView[scrollList.size()]; viewPoints.removeAllViews(); // 添加小圆点的图片
	 * int circlePadding = BaseActivity.px2dip(mActivity,
	 * mActivity.getResources().getDimension(R.dimen.recommend_circle_padding));
	 * for (int i = 0; i < scrollList.size(); i++) { ImageView circleImageview =
	 * new ImageView(mActivity); LayoutParams layoutParams = new
	 * LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	 * layoutParams.setMargins(circlePadding, 0, circlePadding, 0);
	 * circleImageview.setLayoutParams(layoutParams); circleImageViews[i] =
	 * circleImageview; // 默认选中的是第一张图片，此时第一个小圆点是选中状态，其他不是 if (i == 0) {
	 * circleImageViews[i].setImageResource(R.drawable.
	 * recommend_pageindicator_focused); } else {
	 * circleImageViews[i].setImageResource(R.drawable.recommend_pageindicator);
	 * } // 将imageviews添加到小圆点视图 viewPoints.addView(circleImageViews[i]); }
	 * 
	 * // 设置viewpager的适配器和监听事件 bannerViewPager.setAdapter(new
	 * HeadLineViewPagerAdapter(mActivity, pageViews, scrollList));
	 * bannerViewPager.setOnPageChangeListener(new
	 * HeadLinePageChangeListener(pageViews, circleImageViews)); ((BaseActivity)
	 * getActivity()).getDefaultHandler().postDelayed(new Runnable() {
	 * 
	 * @Override public void run() { bannerViewPager.setCurrentItem(1); } },
	 * 200); }
	 */
	@Override
	public void onPause() {
		super.onPause();
		getDefaultHandler().removeMessages(PAGERSCROLL);
	}

	public void onResume() {
		super.onResume();
		if (mAdapter != null && mAdapter.hasPager())
			getDefaultHandler().sendEmptyMessageDelayed(PAGERSCROLL, 4000);
		;
	}

	@Override
	protected void onHandleMessage(Message msg) {
		if (msg.what == PAGERSCROLL) {
			if (mAdapter != null) {
				mAdapter.notifyPagerscroll();
				if (mAdapter.hasPager())
					getDefaultHandler().sendEmptyMessageDelayed(PAGERSCROLL, 4000);
			}
		}
	}

	@Override
	protected boolean loadingData() {
		// 非缓存页面不需要重写
		return false;
	}

}
