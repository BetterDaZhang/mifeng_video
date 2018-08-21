package com.letv.autoapk.ui.recommend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.common.net.LruImageCache;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.ui.search.SearchAPI;
import com.letv.autoapk.widgets.NetImageView;

public class SubjectFrament extends BaseTitleFragment {
	private LinearLayout listviewLay;
	private PullToRefreshListView mPullRefreshListView;
	// 首页数据
	private List<DisplayBlockInfo> mSubjectDatas = new ArrayList<DisplayBlockInfo>();
	private List<DisplayBlockInfo> mSubjectTempDatas = new ArrayList<DisplayBlockInfo>();
	private ArrayList<DisplayBlockInfo> recommendBlockList;
	private SubjectInfo subjectInfo;
	private String pageId;

	private RecommendAdapter mAdapter;
	private View headerView;
	private View footView;
	private NetImageView headImg;
	private TextView headTitle;
	private TextView headBrief;
	private EditText searchEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		pageId = bundle.getString("pageId");
	}


	@Override
	protected void onHandleMessage(Message msg) {

	}
	
	protected void initCustomerView() {
		setTitle(mActivity.getResources().getString(R.string.subject_title), mActivity.getResources().getColor(R.color.code1));
	}

	@Override
	protected boolean loadingData() {
		subjectInfo = new SubjectInfo();
		SubjectDataRequest request = new SubjectDataRequest(mActivity);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputParam.put("pageId", pageId);
		int code = request.setInputParam(mInputParam).setOutputData(subjectInfo).request(Request.Method.GET);
		if (code == 0) {
			mSubjectDatas.clear();
			mSubjectDatas.addAll(subjectInfo.getSubjectBlocks());
			return true;
		}
		return false;
	}

	@Override
	protected View setupDataView() {
		initListview();
		initSubectHeadView();
		recommendBlockList = new ArrayList<DisplayBlockInfo>();
		for (DisplayBlockInfo blockInfo : mSubjectDatas) {
			if (blockInfo.getVideoList() != null && blockInfo.getBlockDisplayType() > 0
					&& blockInfo.getBlockDisplayType() < 4) {
				formatBlockList(recommendBlockList, blockInfo);
			}
		}

		ListView actualListView = mPullRefreshListView.getRefreshableView();

		// Need to use the Actual ListView when registering for Context Menu
		registerForContextMenu(actualListView);
		renderHeaderView();
		mAdapter = new RecommendAdapter(mActivity, recommendBlockList,getDefaultHandler());
		// You can also just use setListAdapter(mAdapter) or
		// mPullRefreshListView.setAdapter(mAdapter)
		actualListView.setAdapter(mAdapter);
		if (recommendBlockList != null && recommendBlockList.size() > 0) {
			initFootView();
			bindListener();
		}
		return listviewLay;
	}

	public void formatBlockList(ArrayList<DisplayBlockInfo> formatRecommendBlockList, DisplayBlockInfo blockInfo) {
		if (blockInfo.getBlockDisplayType() == 1) {// 两列多行
			blockInfo.setBlockDisplayType(RecommendAdapter.TYPE_BLOCK_HEAD_TYPE);
			formatRecommendBlockList.add(blockInfo);
			DisplayBlockInfo formatBlockInfo = new DisplayBlockInfo();
			for (int i = 0; i < blockInfo.getVideoList().size(); i++) {
				if (i % 2 == 0) {
					formatBlockInfo = new DisplayBlockInfo();
					formatBlockInfo.setBlockDisplayType(RecommendAdapter.TYPE_TWO_LANDSCAPE_TYPE);
					formatBlockInfo.addVideoListInfo(blockInfo.getVideoList().get(i));
				} else {
					formatBlockInfo.addVideoListInfo(blockInfo.getVideoList().get(i));
					formatRecommendBlockList.add(formatBlockInfo);
				}
			}
		} else if (blockInfo.getBlockDisplayType() == 2) {// 一行大，多行列
			blockInfo.setBlockDisplayType(RecommendAdapter.TYPE_BLOCK_HEAD_TYPE);
			formatRecommendBlockList.add(blockInfo);
			DisplayBlockInfo formatBlockInfo = new DisplayBlockInfo();
			formatBlockInfo.setBlockDisplayType(RecommendAdapter.TYPE_ONE_LANDSCAPE_TYPE);
			formatBlockInfo.addVideoListInfo(blockInfo.getVideoList().get(0));
			formatRecommendBlockList.add(formatBlockInfo);
			for (int i = 1; i < blockInfo.getVideoList().size(); i++) {
				if (i % 2 == 1) {
					formatBlockInfo = new DisplayBlockInfo();
					formatBlockInfo.setBlockDisplayType(RecommendAdapter.TYPE_TWO_LANDSCAPE_TYPE);
					formatBlockInfo.addVideoListInfo(blockInfo.getVideoList().get(i));
				} else {
					formatBlockInfo.addVideoListInfo(blockInfo.getVideoList().get(i));
					formatRecommendBlockList.add(formatBlockInfo);
				}
			}

		} else if (blockInfo.getBlockDisplayType() == 3) {// 三列多行
			blockInfo.setBlockDisplayType(RecommendAdapter.TYPE_BLOCK_HEAD_TYPE);
			formatRecommendBlockList.add(blockInfo);
			DisplayBlockInfo formatBlockInfo = new DisplayBlockInfo();
			for (int i = 0; i < blockInfo.getVideoList().size(); i++) {
				if (i % 3 == 0) {
					formatBlockInfo = new DisplayBlockInfo();
					formatBlockInfo.setBlockDisplayType(RecommendAdapter.TYPE_THREE_VERTICAL_TYPE);
					formatBlockInfo.addVideoListInfo(blockInfo.getVideoList().get(i));
				} else if (i % 3 == 1) {
					formatBlockInfo.addVideoListInfo(blockInfo.getVideoList().get(i));
				} else {
					formatBlockInfo.addVideoListInfo(blockInfo.getVideoList().get(i));
					formatRecommendBlockList.add(formatBlockInfo);
				}
			}
		}

	}

	private void initListview() {
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		listviewLay = (LinearLayout) inflater.inflate(R.layout.recommend_pulltolistview, null, false);
		mPullRefreshListView = (PullToRefreshListView) listviewLay.findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setMode(Mode.PULL_FROM_START);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				new UiAsyncTask<Boolean>(SubjectFrament.this) {

					@Override
					protected Boolean doBackground() {
						return loadingData();
					}

					@Override
					protected void post(Boolean result) {
						super.post(result);
						if (result) {
							notifyDataView();
						}
						mPullRefreshListView.onRefreshComplete();
					}
				}.execute();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

			}
		});

		// Add an end-of-list listener
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {

			}
		});
	}

	/**
	 * 初始化头部轮播图
	 */
	private void initSubectHeadView() {
		if (headerView == null) {
			headerView = mActivity.getLayoutInflater().inflate(R.layout.subject_headline, null);
		}
		headImg = (NetImageView) headerView.findViewById(R.id.subject_head_img);
		headTitle = (TextView) headerView.findViewById(R.id.subject_head_title);
		headBrief = (TextView) headerView.findViewById(R.id.subject_head_brief);
		mPullRefreshListView.getRefreshableView().addHeaderView(headerView);
	}
	
	/**
	 * 初始化foot
	 * 
	 * @param scrollList
	 */
	private void initFootView() {
		footView = mActivity.getLayoutInflater().inflate(R.layout.recommend_footview, null);
		searchEditText = (EditText) footView.findViewById(R.id.recommend_search_editText);
		mPullRefreshListView.getRefreshableView().addFooterView(footView);
	}

	private void renderHeaderView() {
		try {
			headImg.setDefaultImageResId(R.drawable.default_img_22_10);
			headImg.setErrorImageResId(R.drawable.default_img_22_10);
			headImg.setCoverUrl(subjectInfo.getSubjectImg(),mActivity);
			headTitle.setText(subjectInfo.getSubjectName());
			headBrief.setText(subjectInfo.getSubjectBrief());
		} catch (Exception e) {
			Logger.log(e);
		}

	}
	
	/**
	 * bindlistener
	 * 
	 * @param
	 */
	private void bindListener() {
		searchEditText.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					InputMethodManager imm = (InputMethodManager) v.getContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE);

					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
					}
					String searchContent = searchEditText.getText().toString().trim();
					if (searchContent.isEmpty()) {
						searchEditText.setText("");
						Toast.makeText(getActivity(), R.string.channel_noinput, Toast.LENGTH_SHORT).show();
						return true;
					}
					// 去掉搜索中的所有空格
					String searchTvString = searchEditText.getText().toString().replaceAll(" ", "");
					// 清空textvew中的字符中的空格
					searchEditText.setText(searchTvString);
					SearchAPI searchUtil = new SearchAPI();
					searchUtil.startSearchResultActivity(searchTvString, mActivity);
					return true;
				}

				return false;
			}
		});
	}

	private void notifyDataView() {
		recommendBlockList.clear();
		for (DisplayBlockInfo blockInfo : mSubjectDatas) {
			if (blockInfo.getVideoList() != null && blockInfo.getBlockDisplayType() > 0
					&& blockInfo.getBlockDisplayType() < 4) {
				formatBlockList(recommendBlockList, blockInfo);
			}
		}
		renderHeaderView();
		mAdapter.notifyDataSetChanged();
	}

}
