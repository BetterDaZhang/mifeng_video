package com.letv.autoapk.ui.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.CollectionRecordDao;
import com.letv.autoapk.dao.CollectionRecordInfo;
import com.letv.autoapk.dao.CollectionRecordInfo.Flag;
import com.letv.autoapk.ui.collection.CollectionAdapter.Holder;
import com.letv.autoapk.ui.player.PlayerAPI;
import com.letv.autoapk.widgets.LoadingLayout;

public class CollectionFragment extends BaseTitleFragment implements OnClickListener, OnItemClickListener {
	private LayoutInflater inflater;
	private PullToRefreshListView recordPullListView;
	private CollectionAdapter<CollectionRecordInfo> adapter;
	private TextView allSelect;
	private TextView delect;
	private TextView titleRight;
	private volatile boolean state = true;// 当前的状态。如果是浏览状态就是true 。如果是删除状态就是false
	private HashSet<CollectionRecordInfo> recordDelects;
	private List<CollectionRecordInfo> records = new LinkedList<CollectionRecordInfo>();
	private View delectPanel;
	private final static int COUNT_PAGE_NUM = 20;
	private String isSubjectPage = "";
	CollectionRecordDao dao;
	private final int PULLFROMSTART = 1;
	private final int PULLFROMEND = 2;
	private PageInfo mPageInfo;
	private boolean isFirst = true;
	private View root;

	@Override
	protected void onHandleMessage(Message msg) {
		if (msg.what == 0x10001) {
			adapter.setRecords(records);
			adapter.notifyDataSetChanged();
			recordPullListView.postInvalidate();
		}
	}

	@Override
	protected boolean hasContentData() {
		return records != null && !records.isEmpty();
	}

	@Override
	protected boolean loadingData() {
		records.clear();
		mPageInfo = new PageInfo();
		GetCollectRecordsRequest request = new GetCollectRecordsRequest(mActivity);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.PAGE, String.valueOf(1));
		mInputParam.put(StringDataRequest.PAGE_SIZE, String.valueOf(StringDataRequest.PAGE_SIZE_COUNT));
		mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		int status = request.setInputParam(mInputParam).setOutputData(records, mPageInfo).request(Method.GET);
		if (status == 0)
			return true;
		return false;
	}

	private boolean loadingNextCollectionData() {
		GetCollectRecordsRequest request = new GetCollectRecordsRequest(mActivity);
		Map<String, String> mInputParam = new HashMap<String, String>();
		if (mPageInfo.getPageIndex() + 1 <= mPageInfo.getTotalPage()) {
			mInputParam.put(StringDataRequest.PAGE, String.valueOf(mPageInfo.getPageIndex() + 1));
			mInputParam.put(StringDataRequest.PAGE_SIZE, String.valueOf(StringDataRequest.PAGE_SIZE_COUNT));
			mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
			mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
			int status = request.setInputParam(mInputParam).setOutputData(records).request(Method.GET);
			if (status == 0)
				return true;
			return false;
		} else {
			return false;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isFirst) {
			return;
		}
		new UiAsyncTask<Boolean>(this) {
			@Override
			protected Boolean doBackground() throws Throwable {
				if (MyApplication.getInstance().isLogin()) {
					return loadingData();
				} else {
					records.clear();
					dao = MyApplication.getInstance().getDaoByKey(CollectionRecordDao.class.getName());
					records = dao.findAll();
					return true;
				}
			}

			@Override
			protected void post(Boolean result) {
				super.post(result);
				loadingLayout.onSuccess(result);
				if (records.isEmpty()) {
					titleRight.setVisibility(View.GONE);
				}
			}
		}.showDialog().execute();
	}

	@Override
	public void onPause() {
		super.onPause();
		isFirst = false;
	};

	@Override
	protected void initCustomerView() {
		setTitle(getResources().getString(R.string.collect_text), getResources().getColor(R.color.code6));
		setTitleLeftResource(R.drawable.mine_login_back, mActivity.dip2px(16));
		setStatusBarColor(getResources().getColor(R.color.code04));
		setLeftClickListener(new TitleLeftClickListener() {

			@Override
			public void onLeftClickListener() {
				getActivity().finish();
			}
		});
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

			@Override
			protected View createNoContentView() {
				LayoutInflater mInflater = LayoutInflater.from(mActivity);
				View mView = (ViewGroup) mInflater.inflate(R.layout.mine_nocollection, null);
				ImageView nodataimage = (ImageView) mView.findViewById(R.id.iv_mine_nodata);
				TextView noDataText = (TextView) mView.findViewById(R.id.tv_mine_nodata);
				return mView;
			}
		};
		if (MyApplication.getInstance().isLogin()) {
			loadingLayout.show();
		} else {
			dao = MyApplication.getInstance().getDaoByKey(CollectionRecordDao.class.getName());
			records = dao.findAll();
			loadingLayout.onSuccess(true);
		}
		return loadingLayout;
	}

	@Override
	protected View setupDataView() {
		if (root != null && recordPullListView != null && adapter != null) {
			adapter.setRecords(records);
			adapter.notifyDataSetChanged();
		} else {
			titleRight = new TextView(mActivity);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.MATCH_PARENT);
			titleRight.setGravity(Gravity.CENTER);
			titleRight.setLayoutParams(params);
			titleRight.setText(R.string.mine_edit);
			titleRight.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
			titleRight.setTextColor(getResources().getColor(R.color.code6));
			titleRight.setPadding(0, 0, mActivity.dip2px(15), 0);
			titleRight.setId(android.R.id.edit);
			setRightClickListener(new TitleRightClickListener() {

				@Override
				public void onRightClickListener() {
					if (state) {// 浏览状态点击
						state = !state;
						titleRight.setText(getResources().getString(R.string.cancel));
						adapter.setFlag(Flag.DELETE);
						delectPanel.setVisibility(View.VISIBLE);
						recordDelects = new HashSet<CollectionRecordInfo>();
						adapter.setDelectList(null);
						adapter.setSelectAll(false);
						delect.setBackgroundColor(getResources().getColor(R.color.code04));
						delect.setText(getResources().getString(R.string.mine_delete_with_bracket) + recordDelects.size() + ")");
						if (recordPullListView != null) {
							recordPullListView.setMode(Mode.DISABLED);
						}
					} else {// 删除状态点击
						state = !state;
						titleRight.setText(R.string.mine_edit);
						adapter.setFlag(Flag.NORMAL);
						delectPanel.setVisibility(View.GONE);
						recordDelects = null;
						delect.setText(getResources().getString(R.string.delete_num));
						if (recordPullListView != null) {
							recordPullListView.setMode(Mode.BOTH);
						}
					}
				}
			});
			mTitleRightLay.addView(titleRight);
			inflater = LayoutInflater.from(mActivity);
			root = inflater.inflate(R.layout.mine_collection, null);
			delectPanel = root.findViewById(R.id.ll_delect_select);
			recordPullListView = (PullToRefreshListView) root.findViewById(R.id.lv_played_records_list);
			recordPullListView.setMode(Mode.BOTH);
			recordPullListView.setOnRefreshListener(new RecordsListOnRefreshListener());
			adapter = new CollectionAdapter<CollectionRecordInfo>(mActivity);
			recordPullListView.setOnItemClickListener(this);
			allSelect = (TextView) root.findViewById(R.id.tv_records_all);
			delect = (TextView) root.findViewById(R.id.tv_delect);

			allSelect.setOnClickListener(this);
			delect.setOnClickListener(this);

			adapter.setRecords(records);
			recordPullListView.setAdapter(adapter);
		}

		return root;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CollectionRecordInfo record = (CollectionRecordInfo) ((Holder) view.getTag()).re;
		if (null != record && !state) {
			if (!recordDelects.contains(record)) {
				recordDelects.add(record);
				((Holder) view.getTag()).checkBox.setChecked(true);
			} else {
				recordDelects.remove(record);
				((Holder) view.getTag()).checkBox.setChecked(false);
			}
			adapter.setDelectList(recordDelects);
			if (recordDelects.size() > 0) {
				delect.setBackgroundColor(getResources().getColor(R.color.code04));
			} else {
				delect.setBackgroundColor(getResources().getColor(R.color.code04));
			}
			delect.setText(getResources().getString(R.string.mine_delete_with_bracket) + recordDelects.size() + ")");
			if (records.size() == recordDelects.size()) {
				adapter.setSelectAll(true);
			} else {
				adapter.setSelectAll(false);
			}
		} else if (null != record && state) {
			PlayerAPI.startPlayActivity(mActivity, record.getDisplayVedioInfo());
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_records_all:
			if (records == null || records.size() <= 0 || recordDelects == null)
				break;
			if (adapter.getSelectAll()) {
				recordDelects = new HashSet<CollectionRecordInfo>();
				delect.setText(getResources().getString(R.string.mine_delete_with_bracket) + recordDelects.size() + ")");
				delect.setBackgroundColor(getResources().getColor(R.color.code04));
				adapter.setDelectList(null);
				adapter.setSelectAll(false);
				recordPullListView.invalidate();
			} else {
				recordDelects.clear();
				recordDelects.addAll(records);
				delect.setText(getResources().getString(R.string.mine_delete_with_bracket) + recordDelects.size() + ")");
				delect.setBackgroundColor(getResources().getColor(R.color.code04));
				adapter.setSelectAll(true);
				recordPullListView.invalidate();
			}
			break;
		case R.id.tv_delect:
			if (recordDelects == null || recordDelects.size() <= 0)
				break;
			if (MyApplication.getInstance().isLogin()) {
				new DelectTask(recordDelects, this).showDialog().execute();
			} else {
				if (recordDelects != null && recordDelects.size() > 0) {
					Iterator<CollectionRecordInfo> iterator = recordDelects.iterator();
					while (iterator.hasNext()) {
						CollectionRecordInfo collectionRecordInfo = iterator.next();
						dao.delete(collectionRecordInfo);
					}
					somethingTodoWhenDeleteOk();
				}
			}

			break;

		}

	}

	class DelectTask extends UiAsyncTask<Integer> {
		HashSet<CollectionRecordInfo> delects;

		DelectTask(HashSet<CollectionRecordInfo> delects, Fragment fragment) {
			super(fragment);
			this.delects = delects;
		}

		@Override
		protected void post(Integer result) {
			if (result == null)
				return;
			if (result == 0) {
				somethingTodoWhenDeleteOk();
			} else {
				mActivity.showToastSafe(R.string.mine_delete_failed, Toast.LENGTH_SHORT);
			}
		}

		@Override
		protected Integer doBackground() {
			if (delect == null) {
				return null;
			}
			if (delects != null && dao != null) {
				ArrayList<CollectionRecordInfo> list = new ArrayList<CollectionRecordInfo>();
				list.addAll(delects);
				dao.delete(list);
				records.removeAll(list);
			}
			List<CollectionRecordInfo> list = new LinkedList<CollectionRecordInfo>();
			list.addAll(delects);
			return OpCollectRecordsRequest.unCollectRecords(mActivity, list);
		}
	}

	private void somethingTodoWhenDeleteOk() {
		adapter.setFlag(Flag.NORMAL);
		delectPanel.setVisibility(View.GONE);
		records.removeAll(recordDelects);
		adapter.setRecords(records);
		recordPullListView.postInvalidate();
		delect.setText(R.string.delete_num);
		titleRight.setText(R.string.mine_edit);
		mActivity.showToastSafe(R.string.mine_delete_success, Toast.LENGTH_SHORT);
		recordDelects = new HashSet<CollectionRecordInfo>();
		state = true;
		if (records == null || records.isEmpty()) {
			titleRight.setVisibility(View.GONE);
			if (mContentView instanceof ViewGroup) {
				LayoutInflater mInflater = LayoutInflater.from(mActivity);
				ViewGroup mView = (ViewGroup) mInflater.inflate(R.layout.mine_nocollection, null);
				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
				((ViewGroup) mContentView).addView(mView, params);
			}
		}
	}

	class RecordsListOnRefreshListener implements OnRefreshListener2<ListView> {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			new UpdateCollectRecordsTask(CollectionFragment.this, PULLFROMSTART).execute();
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			new UpdateCollectRecordsTask(CollectionFragment.this, PULLFROMEND).execute();
		}
	}

	class UpdateCollectRecordsTask extends UiAsyncTask<Boolean> {
		private int defalutPull;

		public UpdateCollectRecordsTask(Fragment fragment, int d) {
			super(fragment);
			this.defalutPull = d;
		}

		@Override
		protected void post(Boolean result) {
			if (recordPullListView == null) {
				return;
			}
			recordPullListView.onRefreshComplete();
			if (!MyApplication.getInstance().isLogin()) {
				recordPullListView.setMode(Mode.PULL_FROM_START);
				return;
			}
			if (result && null != adapter) {
				adapter.setRecords(records);
				adapter.notifyDataSetChanged();
			} else {
				if (defalutPull == PULLFROMEND && mPageInfo.getPageIndex() + 1 > mPageInfo.getTotalPage()) {
					recordPullListView.setMode(Mode.PULL_FROM_START);
				}
			}
		}

		@Override
		protected Boolean doBackground() {
			if (defalutPull == PULLFROMSTART) {
				if (MyApplication.getInstance().isLogin()) {
					return loadingData();
				}
			} else {
				if (MyApplication.getInstance().isLogin()) {
					return loadingNextCollectionData();
				}
			}
			return false;
		}
	}

}
