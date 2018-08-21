package com.letv.autoapk.ui.search;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.CacheListener;
import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.net.StringDataRequest.DataRequestCallback;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.SearchHistoryDao;
import com.letv.autoapk.dao.SearchHistoryInfo;
import com.letv.autoapk.ui.search.SearchFragment.MatchWordsAdapter;
import com.letv.autoapk.utils.SystemUtls;
import com.letv.autoapk.widgets.LoadingLayout;

public class SearchResultFragment extends BaseTitleFragment {
	private String TAG = "SearchResultFragment";
	private LinearLayout headLayout;
	private List<String> headList;
	private List<SearchResultInfo> searchResultInfos;
	private Map<String, String> categories;
	private PageInfo mPageInfo;
	private PullToRefreshListView listView;
	private Context context;
	/** 搜索结果页adapter */
	private SearchResultListAdapter listAdapter;
	// private SearchResultGridAdapter gridAdapter;
	private RelativeLayout searchHeaderLayout;
	private EditText et_search;
	private View mView = null;
	private View headView = null;
	private TextView headtitleAll;
	private RelativeLayout mRoot;
	private View mContentView;
	private String videoType = "0";
	private String keyWord = "";
	private SearchHistoryDao searchHistoryDao;
	/** 搜索联想词页面 */
	private RelativeLayout matchWordsLayout;
	List<String> matchWords = new ArrayList<String>();
	private ListView lv_search_match_words;
	private MatchWordsAdapter matchWordsAdapter;
	private String oldSearchContent;
	private boolean isReInitHeadView = true;
	/** 当前频道名称，为了区分相同频道ID但不同频道名称的情况 */
	private String currentHeadTitle = "全部";

	private final int PULLFROMSTART = 1;
	private final int PULLFROMEND = 2;

	public SearchResultFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		headList = new ArrayList<String>();
		searchResultInfos = new ArrayList<SearchResultInfo>();
		categories = new HashMap<String, String>();
		keyWord = getArguments().getString("keyword");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		searchHistoryDao = MyApplication.getInstance().getDaoByKey(
				SearchHistoryDao.class.getName());
		initCustomView(inflater);
		initView();
		return mRoot;
	}

	private View initView() {
		mRoot = new RelativeLayout(getActivity());
		mCustomeTitleBar = (RelativeLayout) View.inflate(mActivity,
				R.layout.base_custom_titlebar, null);
		searchHeaderLayout = (RelativeLayout) View.inflate(mActivity,
				R.layout.search_header_layout, null);
		mTitleContent = (RelativeLayout) mCustomeTitleBar
				.findViewById(R.id.contentLay);
		mTitleContent.addView(searchHeaderLayout);
		initSearchHeader();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, mActivity.dip2px(45));
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		mRoot.addView(mCustomeTitleBar, params);

		mContentView = createContentView();
		RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp3.addRule(RelativeLayout.BELOW, mCustomeTitleBar.getId());
		mRoot.addView(mContentView, lp3);
		mContentView.setVisibility(View.VISIBLE);

		RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp4.addRule(RelativeLayout.BELOW, mCustomeTitleBar.getId());
		matchWordsLayout = (RelativeLayout) View.inflate(mActivity,
				R.layout.search_matchwords, null);
		mRoot.addView(matchWordsLayout, lp4);
		lv_search_match_words = (ListView) matchWordsLayout
				.findViewById(R.id.lv_search_match_words);
		matchWordsLayout.setVisibility(View.GONE);

		return mRoot;
	}

	private void initCustomView(LayoutInflater inflater) {

		mView = inflater.inflate(R.layout.search_result_layout, null);// 搜索结果页
		headView = inflater.inflate(R.layout.search_result_head_layout, null);// 搜索结果头部搜索框

		headLayout = (LinearLayout) headView
				.findViewById(R.id.search_result_head);// 搜索结果头部分类布局
		listView = (PullToRefreshListView) mView
				.findViewById(R.id.search_result_listview);
		listView.setMode(Mode.PULL_FROM_END);
		View footView = mActivity.getLayoutInflater().inflate(
				R.layout.search_result_foot_view, null);
		listView.getRefreshableView().addFooterView(footView);
	}

	OnRefreshListener2<ListView> searchResultRefreshListener = new OnRefreshListener2<ListView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			// 分页加载
			PullDownLoadingTask pullDownLoadingTask = new PullDownLoadingTask(
					PULLFROMSTART);
			pullDownLoadingTask.execute(null, null);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			// 分页加载
			PullDownLoadingTask pullDownLoadingTask = new PullDownLoadingTask(
					PULLFROMEND);
			pullDownLoadingTask.execute(null, null);
		};
	};

	/** 初始化搜索框 */
	private void initSearchHeader() {

		et_search = (EditText) searchHeaderLayout.findViewById(R.id.et_search);
		et_search.addTextChangedListener(textWatcher);
		et_search.setOnKeyListener(onKeyListener);
		et_search.setText(keyWord);
		TextView tv_cancel = (TextView) searchHeaderLayout
				.findViewById(R.id.tv_cancel);
		tv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SystemUtls.hideInputMethod(mActivity, v);
				getFragmentManager().popBackStack();
			}
		});

		searchClearImg = (ImageView) searchHeaderLayout
				.findViewById(R.id.iv_search_clear);
		searchClearImg.setVisibility(View.VISIBLE);
		searchClearImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et_search.setText("");
			}
		});

	}

	@Override
	protected boolean hasContentData() {
		if (null != searchResultInfos && searchResultInfos.size() > 0) {
			return true;
		}
		if (isReInitHeadView) {
			headView.setVisibility(View.GONE);
			headLayout.removeAllViews();
		} else {
			initHeadView();
		}
		return false;
	}

	public TextView getHeadChannel(String channelTitle) {
		TextView channelTv = new TextView(context);
		channelTv.setBackgroundResource(android.R.color.transparent);
		channelTv.setText(channelTitle);
		channelTv.setTextColor(context.getResources().getColor(R.color.code3));
		channelTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
		channelTv.setGravity(Gravity.CENTER_VERTICAL);
		channelTv.setTag(TextUtils.isEmpty(categories.get(channelTitle)) ? "0"
				: categories.get(channelTitle));
		// Drawable drawable =
		// context.getResources().getDrawable(R.drawable.divider_vertical);
		// drawable.setBounds(0, 0, drawable.getMinimumWidth(),
		// drawable.getMinimumHeight());
		// channelTv.setCompoundDrawables(drawable, null, null, null);
		// channelTv.setCompoundDrawablePadding(22);
		// channelTv.setPadding(0, 0, 22, 0);
		// channelTv.setBackgroundResource(R.drawable.search_result_head_focused);
		return channelTv;
	}

	public ImageView getHeadChannelDivider() {
		ImageView img = new ImageView(context);
		img.setImageResource(R.drawable.search_divider_vertical);
		return img;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean loadingData() {
		searchResultInfos.clear();
		if (isReInitHeadView) {
			categories.clear();
		}
		mPageInfo = new PageInfo();
		SearchResultDataRequest request = new SearchResultDataRequest(
				getActivity());
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication
				.getInstance().getTenantId()); //
		mInputParam.put(SearchResultDataRequest.KEYWORD, keyWord); //
		mInputParam.put(SearchResultDataRequest.VIDEOTYPE, videoType + ""); //
		mInputParam.put(StringDataRequest.PAGE, String.valueOf(1));
		mInputParam.put(StringDataRequest.PAGE_SIZE,
				String.valueOf(StringDataRequest.PAGE_SIZE_COUNT));
		request.setCacheListener(cacheListener);
		int code = request
				.setInputParam(mInputParam)
				.setOutputData(searchResultInfos, mPageInfo, categories,
						isReInitHeadView).request(Request.Method.GET);
		Logger.d(TAG, "code:" + code);
		if (code == 0) {
			return true;
		} else if (code != 1) {
			mActivity.showToastSafe(getString(R.string.search_failed), 0);
		}
		return false;
	}

	class PullDownLoadingTask extends AsyncTask<Void, Void, Boolean> {

		private boolean isCanceled = false;
		private int defalutPull;

		PullDownLoadingTask(int d) {
			this.defalutPull = d;
		}

		public void cancel() {
			isCanceled = true;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (isCanceled) {
				return false;
			}
			if (defalutPull == PULLFROMEND) {
				return loadingNextData();
			} else {
				return loadingData();
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			listView.onRefreshComplete();
			if (result) {
				notifyDataView();
			} else if (mPageInfo.getPageIndex() + 1 > mPageInfo.getTotalPage()) {
				// listView.setMode(Mode.PULL_FROM_START);
			}
		}
	}

	private boolean loadingNextData() {
		isReInitHeadView = false;
		SearchResultDataRequest request = new SearchResultDataRequest(
				getActivity());
		Map<String, String> mInputParam = new HashMap<String, String>();
		if (mPageInfo.getPageIndex() + 1 <= mPageInfo.getTotalPage()) {
			mInputParam.put(SearchResultDataRequest.VIDEOTYPE, videoType + "");
			mInputParam.put(SearchResultDataRequest.KEYWORD, keyWord); //
			mInputParam.put(SearchResultDataRequest.TENANT_ID, MyApplication
					.getInstance().getTenantId()); //
			mInputParam.put(StringDataRequest.PAGE,
					String.valueOf(mPageInfo.getPageIndex() + 1));
			mInputParam.put(StringDataRequest.PAGE_SIZE,
					String.valueOf(StringDataRequest.PAGE_SIZE_COUNT));
			request.setCacheListener(cacheListener);
			int code = request
					.setInputParam(mInputParam)
					.setOutputData(searchResultInfos, mPageInfo, categories,
							isReInitHeadView).request(Request.Method.GET);
			Logger.d(TAG, "code:" + code);
			if (code == 0) {
				return true;
			} else if (code != 1) {
				mActivity.showToastSafe(
						getString(R.string.search_contentfailed), 0);
			}
			return false;
		} else {
			mActivity.showToastSafe(getString(R.string.search_nomore), 0);
			return false;
		}
	}

	private void notifyDataView() {
		listView.onRefreshComplete();
		listAdapter.notifyDataSetChanged();
	}

	Handler handler = new Handler();
	CacheListener cacheListener = new CacheListener(handler) {

		@Override
		public void onRefreshCache(Object[] mOutputData) {

		}
	};

	@Override
	public View setupDataView() {
		initHeadView();
		listAdapter = new SearchResultListAdapter(searchResultInfos, context,
				SearchResultFragment.this);
		listView.setOnRefreshListener(searchResultRefreshListener);
		ListView actualListView = listView.getRefreshableView();
		actualListView.setAdapter(listAdapter);
		return mView;
	}

	@Override
	protected View createContentView() {
		loadingLayout = new LoadingLayout(mActivity) {

			@Override
			public void loadData(View loadingView) {
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

			// @Override
			// protected View createNoContentView() {
			// LayoutInflater mInflater = LayoutInflater.from(mActivity);
			// View mView = (ViewGroup)
			// mInflater.inflate(R.layout.mine_nosearchresult, null);
			// return mView;
			// }
		};
		loadingLayout.show();
		return loadingLayout;
	}

	public void initHeadTitle(Map<String, String> categories) {
		headList.clear();
		headList.add(getResources().getString(R.string.search_channel_all));
		Set<String> keySet = categories.keySet();
		for (String str : keySet) {
			headList.add(str);
		}
	}

	/**
	 * 
	 * @param isInit是否重新init
	 */
	private void initHeadView() {
		if (isReInitHeadView) {
			initHeadTitle(categories);
		}
		if (headLayout.getChildCount() > 0) {
			headLayout.removeAllViews();
		}
		for (int i = 0; i < headList.size(); i++) {
			mRoot.removeView(headView);
			final String headTitle = headList.get(i);
			final TextView channelTv = getHeadChannel(headTitle);
			// final ImageView dividerImg = getHeadChannelDivider();
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 0, 20, 0);
			params.gravity = Gravity.CENTER_VERTICAL;
			// headLayout.addView(dividerImg, params);
			headLayout.addView(channelTv, params);
			Object channelType = channelTv.getTag();
			if (channelType != null && videoType.equals(channelType)
					&& headTitle.equals(currentHeadTitle)) {
				// channelTv.setBackgroundResource(R.drawable.search_result_head_focused);
				channelTv.setTextColor(context.getResources().getColor(
						R.color.code1));
			}
			channelTv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (categories.isEmpty()) {
						return;
					}
					int count = headLayout.getChildCount();
					for (int i = 0; i < count; i++) {
						View view = headLayout.getChildAt(i);
						if (view instanceof TextView) {
							view.setBackgroundResource(android.R.color.transparent);
							((TextView) view).setTextColor(context
									.getResources().getColor(R.color.code3));
						}
					}
					if (getResources().getString(R.string.search_channel_all)
							.equals(headTitle)) {
						currentHeadTitle = headTitle;
						videoType = "0";
					} else {
						currentHeadTitle = headTitle;
						videoType = TextUtils.isEmpty(categories.get(headTitle)) ? "0"
								: categories.get(headTitle);
					}
					retryOtherChannelData();
				}
			});
		}
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp2.addRule(RelativeLayout.BELOW, mCustomeTitleBar.getId());
		mRoot.addView(headView, lp2);
		headView.setVisibility(View.VISIBLE);
	}

	private void retryOtherChannelData() {
		// 重新请求
		isReInitHeadView = false;
		mRoot.removeView(mContentView);
		mContentView = createContentView();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.BELOW, mCustomeTitleBar.getId());
		mRoot.addView(mContentView, params);
		headView.setVisibility(View.VISIBLE);
		headView.bringToFront();
	}

	public void clearSearchHistoryInfo() {
		searchHistoryDao.deleteAll();
	}

	public void saveSearchHistoryInfo(SearchHistoryInfo info) {
		searchHistoryDao.save(info);
	}

	@Override
	protected void onHandleMessage(Message msg) {
		// TODO Auto-generated method stub
	}

	OnKeyListener onKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_ENTER
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				hideSoftInputFromWindow(v);
				oldSearchContent = et_search.getText().toString();
				if (oldSearchContent.isEmpty()) {
					Toast.makeText(getActivity(), R.string.search_noinput,
							Toast.LENGTH_SHORT).show();
					return true;
				}
				SearchHistoryInfo info = new SearchHistoryInfo();
				// 去掉搜索中的所有空格
				String et_searchString = oldSearchContent;
				info.setSarchTitle(et_searchString.trim().endsWith("　") ? et_searchString
						.trim() : et_searchString.trim() + "　");
				saveSearchHistoryInfo(info);
				et_search.setText(et_searchString);
				// TODO videoType怎么处理
				search(et_searchString);
				return true;
			}
			return false;
		}

	};

	TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			oldSearchContent = s.toString();
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			String keyWord = et_search.getText().toString().trim();
			currentHeadTitle = getString(R.string.search_allover);
			videoType = "0";
			if (keyWord.equals(oldSearchContent)) {
				return;
			}
			if (mContentView == null || headView == null
					|| matchWordsLayout == null) {
				return;
			}
			if (TextUtils.isEmpty(keyWord)) {
				searchClearImg.setVisibility(View.GONE);
				// matchWordsLayout.setVisibility(View.GONE);
				// mContentView.setVisibility(View.VISIBLE);
				return;
			} else {
				searchClearImg.setVisibility(View.VISIBLE);
				// matchWordsLayout.setVisibility(View.VISIBLE);
				// mContentView.setVisibility(View.GONE);
				// matchWordsLayout.bringToFront();
			}
			SearchMatchWordsDataRequest request = new SearchMatchWordsDataRequest(
					getActivity());// 联想词接口
			Map<String, String> mInputParam = new HashMap<String, String>();
			mInputParam.put("name", URLEncoder.encode(keyWord));// key word
			mInputParam.put(StringDataRequest.TENANT_ID, MyApplication
					.getInstance().getTenantId());
			// request.setInputParam(mInputParam).setOutputData(matchWords).requestTask(Request.Method.POST,
			// new DataRequestCallback() {
			//
			// @Override
			// public void onDataRequestSuccess(Object[] mOutputData) {
			// // listview设置数据
			// matchWordsAdapter = new SearchFragment().new
			// MatchWordsAdapter(mActivity, matchWords);
			// lv_search_match_words.setAdapter(matchWordsAdapter);
			// matchWordsAdapter.notifyDataSetChanged();
			// }
			//
			// @Override
			// public void onDataRequestFailed(int errorCode, String msg) {
			// }
			// });
		}
	};
	private ImageView searchClearImg;

	private void hideSoftInputFromWindow(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
		}
	}

	/** 刷新结果页数据 */
	private void search(String et_searchString) {
		isReInitHeadView = true;
		keyWord = et_searchString;
		// 重新请求
		mRoot.removeView(mContentView);
		mContentView = createContentView();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.BELOW, mCustomeTitleBar.getId());
		mRoot.addView(mContentView, params);
		headView.setVisibility(View.VISIBLE);
		headView.bringToFront();
	}

}
