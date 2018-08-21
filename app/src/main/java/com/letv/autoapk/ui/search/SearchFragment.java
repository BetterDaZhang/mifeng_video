package com.letv.autoapk.ui.search;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.autoapk.R;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.base.db.BaseDao;
import com.letv.autoapk.base.db.BaseDao.OnDataChangeListener;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.SearchHistoryDao;
import com.letv.autoapk.dao.SearchHistoryInfo;
import com.letv.autoapk.utils.SystemUtls;
import com.letv.autoapk.widgets.SearchClearDialog;

/**
 * 搜索页面 当输入框中有数据的时候，就进入搜索联想关键词界面 输入框数据为空，进入搜索历史和搜索热门搜索界面
 * 
 * @author wangzhen5
 * 
 */
public class SearchFragment extends BaseTitleFragment implements
		OnClickListener, OnDataChangeListener<SearchHistoryInfo> {

	private View root;
	/** 搜索框 */
	private EditText et_search;
	private List<String> matchWords = new ArrayList<String>();
	// private List<String> hotSearchKeywords = new ArrayList<String>();
	private ListView lv_search_match_words;
	private MatchWordsAdapter matchWordsAdapter;
	private View searchHistoryAndHotLayout;
	private View searchMatchwordsLayout;
	// private GridView searchHotGridView;
	// private HotSearchAdapter hotSearchAdapter;
	private GridView searchHistoryGridView;
	private List<SearchHistoryInfo> findSearchHistoryInfo;// 搜索历史数据
	private SearchHistoryAdapter searchHistoryAdapter;
	private TextView tv_cancel;
	private ImageView history_clear;
	private SearchHistoryDao searchHistoryDao;
	private RelativeLayout historyTitleRl;// 搜索历史页面
	/** 热搜词索引图片 */
	private List<Integer> hotWordsIndexImages = new ArrayList<Integer>(
			Arrays.asList(R.drawable.search_hot_one, R.drawable.search_hot_two,
					R.drawable.search_hot_three, R.drawable.search_hot_four,
					R.drawable.search_hot_four, R.drawable.search_hot_four,
					R.drawable.search_hot_four, R.drawable.search_hot_four,
					R.drawable.search_hot_four, R.drawable.search_hot_four));

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		searchHistoryDao = MyApplication.getInstance().getDaoByKey(
				SearchHistoryDao.class.getName());
		searchHistoryDao.setOnDataChangeListener(this);
		setStatusBarColor(getResources().getColor(R.color.code04));
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	public void onResume() {
		if (searchHistoryDao != null) {
			findSearchHistoryInfo = findSearchHistoryInfo();
			if (searchHistoryAdapter == null && searchHistoryGridView != null
					&& findSearchHistoryInfo != null) {
				searchHistoryAdapter = new SearchHistoryAdapter(mActivity,
						findSearchHistoryInfo);
				searchHistoryGridView.setAdapter(searchHistoryAdapter);
			}
		}
		super.onResume();
	}

	@Override
	protected boolean loadingData() {
		// SearchHotSearchKeywordsDataRequest request = new
		// SearchHotSearchKeywordsDataRequest(getActivity());// 热门搜索词接口
		// Map<String, String> mInputParam = new HashMap<String, String>();
		// // keyword热门搜索词接口为什么要上传关键词？传什么？
		// mInputParam.put("keyword", "");
		// mInputParam.put(StringDataRequest.TENANT_ID, "1");
		// int code =
		// request.setInputParam(mInputParam).setOutputData(hotSearchKeywords).request(Request.Method.GET);
		// findSearchHistoryInfo = findSearchHistoryInfo();
		// if (code == 0) {
		// return true;
		// }
		// return false;
		return true;
	}

	@Override
	protected View setupDataView() {
		root = View.inflate(mActivity, R.layout.search_layout, null);
		mCustomeTitleBar.setVisibility(View.GONE);// 去掉baseFragment的顶部标题栏，用自己的
		initSearchHeader();

		initCoverView();

		searchHistoryAndHotLayout = root
				.findViewById(R.id.search_history_and_hot);
		searchContentView = root.findViewById(R.id.search_content);
		searchMatchwordsLayout = root.findViewById(R.id.search_matchwords);
		historyTitleRl = (RelativeLayout) root
				.findViewById(R.id.history_title_rl);
		history_clear = (ImageView) root
				.findViewById(R.id.search_history_clear);
		history_clear.setOnClickListener(this);
		lv_search_match_words = (ListView) root
				.findViewById(R.id.lv_search_match_words);
		lv_search_match_words.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				hideSoftInputFromWindow(view);
				SearchHistoryInfo info = new SearchHistoryInfo();
				info.setSarchTitle(matchWords.get(position).trim());
				saveSearchHistoryInfo(info);
				// TODO 调用搜索接口
				search(info.getSarchTitle());
			}
		});

		// searchHotGridView = (GridView)
		// root.findViewById(R.id.search_hot_gridview);
		searchHistoryGridView = (GridView) root
				.findViewById(R.id.search_history_gd);
		searchHistoryGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				hideSoftInputFromWindow(view);
				SearchHistoryInfo info = (SearchHistoryInfo) parent
						.getItemAtPosition(position);
				saveSearchHistoryInfo(info);
				// TODO 调用搜索接口
				search(info.getSarchTitle().replace("　", ""));
			}
		});
		if (findSearchHistoryInfo != null && findSearchHistoryInfo.size() > 0) {
			historyTitleRl.setVisibility(View.VISIBLE);
			searchHistoryAdapter = new SearchHistoryAdapter(getActivity(),
					findSearchHistoryInfo);
			searchHistoryGridView.setAdapter(searchHistoryAdapter);
		} else {
			historyTitleRl.setVisibility(View.GONE);
		}

		return root;
	}

	private void initCoverView() {
		mTransparentView = root.findViewById(R.id.cover_view);
		mTransparentView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTransparentView.setVisibility(View.GONE);
				hideSoftInputFromWindow(v);
				searchContentView.setFocusable(true);
				searchContentView.setFocusableInTouchMode(true);
				boolean requestFocus = searchContentView.requestFocus();
				searchContentView.requestFocusFromTouch();
			}
		});
	}

	private void initSearchHeader() {
		// 初始化上方的搜索框
		et_search = (EditText) root.findViewById(R.id.et_search);
		et_search.addTextChangedListener(textWatcher);
		et_search.setOnFocusChangeListener(onFocusChangeListener);
		et_search.setOnKeyListener(onKeyListener);
		tv_cancel = (TextView) root.findViewById(R.id.tv_cancel);
		tv_cancel.setOnClickListener(this);

		searchClearImg = (ImageView) root.findViewById(R.id.iv_search_clear);
		searchClearImg.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_cancel:
			SystemUtls.hideInputMethod(mActivity, v);
			getActivity().finish();
			break;
		case R.id.search_history_clear:
			showClearDialog();
			break;
		case R.id.iv_search_clear:
			et_search.setText("");
			break;

		default:
			break;
		}
	}

	public void showClearDialog() {
		SearchClearDialog.Builder builder = new SearchClearDialog.Builder(
				mActivity);
		builder.setTitle(mActivity.getResources().getString(
				R.string.search_clear_dialg_title));
		builder.setMessage(null).setPositiveButton("",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						clearSearchHistoryInfo();
						findSearchHistoryInfo.clear();
						historyTitleRl.setVisibility(View.GONE);
						searchHistoryAdapter.notifyDataSetChanged();
						dialog.dismiss();
					}
				});
		builder.setNegativeButton("", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		SearchClearDialog alert = builder.create(R.layout.search_dialog);
		alert.setCancelListenser(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.cancel();
			}
		});
		alert.show();
	}

	OnKeyListener onKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_ENTER
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				hideSoftInputFromWindow(v);
				String searchContent = et_search.getText().toString().trim();
				if (searchContent.isEmpty()) {
					et_search.setText("");
					Toast.makeText(getActivity(), R.string.mine_input_nothing,
							Toast.LENGTH_SHORT).show();
					return true;
				}
				SearchHistoryInfo info = new SearchHistoryInfo();
				// 去掉搜索中的所有空格
				String et_searchString = et_search.getText().toString();
				info.setSarchTitle(et_searchString.trim() + "　");
				saveSearchHistoryInfo(info);
				// 清空textvew中的字符中的空格
				et_search.setText(et_searchString);
				search(et_searchString);
				return true;
			}
			return false;
		}

	};

	private void hideSoftInputFromWindow(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
		}
	}

	OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				mTransparentView.setVisibility(View.VISIBLE);
			} else {
				mTransparentView.setVisibility(View.GONE);
			}
		}
	};

	TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			String keyWord = et_search.getText().toString();
			if (TextUtils.isEmpty(keyWord)) {
				searchClearImg.setVisibility(View.GONE);
				// searchMatchwordsLayout.setVisibility(View.GONE);
				// searchHistoryAndHotLayout.setVisibility(View.VISIBLE);
				return;
			} else {
				searchClearImg.setVisibility(View.VISIBLE);
				// searchMatchwordsLayout.setVisibility(View.VISIBLE);
				// searchHistoryAndHotLayout.setVisibility(View.GONE);
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
			// if (matchWordsAdapter == null) {
			// matchWordsAdapter = new MatchWordsAdapter(getActivity(),
			// matchWords);
			// lv_search_match_words.setAdapter(matchWordsAdapter);
			// }
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
	private View mTransparentView;
	private View searchContentView;

	/** 开始搜索 */
	public void search(String keyWord) {
		FragmentTransaction ft = mActivity.getSupportFragmentManager()
				.beginTransaction();
		SearchResultFragment searchResultFragment = new SearchResultFragment();
		Bundle bundle = new Bundle();
		bundle.putString("keyword", keyWord);
		searchResultFragment.setArguments(bundle);
		ft.replace(R.id.container, searchResultFragment).addToBackStack(null)
				.commit();
	}

	public void clearSearchHistoryInfo() {
		searchHistoryDao.deleteAll();
	}

	public void saveSearchHistoryInfo(SearchHistoryInfo info) {
		searchHistoryDao.save(info);
		// if (findSearchHistoryInfo != null && searchHistoryAdapter != null) {
		// findSearchHistoryInfo = searchHistoryDao.findAll();
		// if (findSearchHistoryInfo.size() == 9) {
		// findSearchHistoryInfo.remove(8);
		// }
		// for (int i = 0; i < findSearchHistoryInfo.size(); i++) {
		// if
		// (findSearchHistoryInfo.get(i).getSarchTitle().equals(info.getSarchTitle()))
		// {
		// findSearchHistoryInfo.remove(i);
		// i--;
		// }
		// }
		// findSearchHistoryInfo.add(0, info);
		// searchHistoryAdapter = new SearchHistoryAdapter(mActivity,
		// findSearchHistoryInfo);
		// searchHistoryGridView.setAdapter(searchHistoryAdapter);
		// }
	}

	public List<SearchHistoryInfo> findSearchHistoryInfo() {
		List<SearchHistoryInfo> findHistoryInfos = searchHistoryDao.findAll();
		if (findHistoryInfos == null) {
			return null;
		}
		Collections.reverse(findHistoryInfos);// 反转，保证刚查询的关键词在列表上方,只能对findAll后的数据反转
		if (findHistoryInfos.size() > 8) {
			return findHistoryInfos.subList(0, 8);
		}
		return findHistoryInfos;
	}

	/** 热门搜索词adapter */
	public class HotSearchAdapter extends BaseAdapter {

		Context context;
		List<String> hotSearchKeywords;

		public HotSearchAdapter(Context context, List<String> hotSearchKeywords) {
			this.context = context;
			this.hotSearchKeywords = hotSearchKeywords;
		}

		@Override
		public int getCount() {
			return hotSearchKeywords.size();
		}

		@Override
		public Object getItem(int position) {
			return hotSearchKeywords.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		protected View getMyView(int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(context,
						R.layout.search_hot_keywords_item, null);
				holder.tv_hot_keyword = (TextView) convertView
						.findViewById(R.id.tv_hot_keyword);
				holder.hotWordIndex = (TextView) convertView
						.findViewById(R.id.tv_hot_words_index);
				holder.hotWordIndexImg = (ImageView) convertView
						.findViewById(R.id.iv_hot_words_index);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (position % 2 == 0) {
				position = position / 2;
			} else {
				position = (position + 1) / 2 + 4;
			}
			holder.tv_hot_keyword.setText(hotSearchKeywords.get(position));
			holder.hotWordIndex.setText(position + 1 + "");
			holder.hotWordIndexImg.setBackgroundResource(hotWordsIndexImages
					.get(position));
			return convertView;
		}

	}

	/** 联想词adapter */
	public class MatchWordsAdapter extends BaseAdapter {

		Context context;
		List<String> matchWords;

		public MatchWordsAdapter(Context context, List<String> matchWords) {
			this.context = context;
			this.matchWords = matchWords;
		}

		@Override
		public int getCount() {
			return matchWords.size();
		}

		@Override
		public Object getItem(int position) {
			return matchWords.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		protected View getMyView(int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(context,
						R.layout.search_matchword_item, null);
				holder.tvSearchHot = (TextView) convertView
						.findViewById(R.id.search_layout_item_hottitle);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tvSearchHot.setText(matchWords.get(position));
			return convertView;
		}
	}

	@Override
	public void dataChange(BaseDao<SearchHistoryInfo> dao) {
		List<SearchHistoryInfo> infos = dao.findAll();
		if (infos == null || infos.size() == 0) {
			historyTitleRl.setVisibility(View.GONE);
		} else {
			historyTitleRl.setVisibility(View.VISIBLE);
		}
	}

}
