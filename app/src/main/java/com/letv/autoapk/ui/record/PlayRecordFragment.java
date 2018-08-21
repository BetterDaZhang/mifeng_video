package com.letv.autoapk.ui.record;

/**
 * 观看记录
 */
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.PlayRecordInfo;
import com.letv.autoapk.dao.PlayedRecordDao;
import com.letv.autoapk.ui.player.PlayerAPI;
import com.letv.autoapk.ui.record.PlayRecordAdapter.Holder;
import com.letv.autoapk.widgets.LoadingLayout;

import android.app.Dialog;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PlayRecordFragment extends BaseTitleFragment implements
		OnClickListener, OnItemClickListener {
	private LayoutInflater inflater;
	private PullToRefreshListView recordList;
	private List<PlayRecordInfo> records = new LinkedList<PlayRecordInfo>();
	private PlayedRecordDao dao;
	private String isSubjectPage = "";
	private PlayRecordAdapter adapter;
	private PageInfo pageInfo;
	private Dialog dialog;
	private final int PULLFROMSTART = 1;
	private final int PULLFROMEND = 2;

	@Override
	protected void onHandleMessage(Message msg) {
		adapter.notifyDataSetChanged();
		recordList.postInvalidate();
	}

	@Override
	protected boolean hasContentData() {
		if (records == null) {
			return false;
		}
		return !records.isEmpty();
	}

	@Override
	public void onResume() {
		if (adapter != null && dao != null) {
			records = dao.findAll();
			adapter.setRecords(records);
			adapter.notifyDataSetChanged();
		}
		super.onResume();
	}

	@Override
	protected boolean loadingData() {
		dao = MyApplication.getInstance().getDaoByKey(
				PlayedRecordDao.class.getName());
		pageInfo = new PageInfo();
		GetPlayRecordsDataRequest request = new GetPlayRecordsDataRequest(
				mActivity);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.USER_ID,
				LoginInfoUtil.getUserId(mActivity));
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication
				.getInstance().getTenantId());
		mInputParam.put(StringDataRequest.PAGE, String.valueOf(1));
		mInputParam.put(StringDataRequest.PAGE_SIZE,
				String.valueOf(StringDataRequest.PAGE_SIZE_COUNT));
		records = new LinkedList<PlayRecordInfo>();
		int code = request.setInputParam(mInputParam)
				.setOutputData(records, pageInfo).request(Method.GET);
		if (code == 0 && records != null) {
			dao.deleleAll();
			for (int i = records.size() - 1; i >= 0; i--) {
				dao.save(records.get(i));
			}
		}
		if (code == 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void initCustomerView() {
		setTitle(getResources().getString(R.string.mine_play_record),
				getResources().getColor(R.color.code6));
		setTitleLeftResource(R.drawable.mine_login_back, mActivity.dip2px(16));
		setStatusBarColor(getResources().getColor(R.color.code04));
		setLeftClickListener(new TitleLeftClickListener() {
			@Override
			public void onLeftClickListener() {
				getActivity().finish();
			}
		});
		setTitleRightResource(R.drawable.mine_record_clear,
				mActivity.dip2px(15));
		setRightClickListener(new TitleRightClickListener() {

			@Override
			public void onRightClickListener() {
				if (records == null || records.isEmpty()) {
					return;
				}
				dialog = new Dialog(mActivity,
						R.style.transparentFrameWindowStyle);
				dialog.setContentView(R.layout.mine_record_clear_dialog);
				dialog.findViewById(R.id.dialog_cancel).setOnClickListener(
						PlayRecordFragment.this);
				dialog.findViewById(R.id.dialog_ensure).setOnClickListener(
						PlayRecordFragment.this);
				dialog.show();
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
				ViewGroup mView = (ViewGroup) mInflater.inflate(
						R.layout.mine_nodata, null);
				ImageView nodataimage = (ImageView) mView
						.findViewById(R.id.iv_mine_nodata);
				TextView noDataText = (TextView) mView
						.findViewById(R.id.tv_mine_nodata);
				return mView;
			}
		};
		if (MyApplication.getInstance().isLogin()) {
			loadingLayout.show();
		} else {
			dao = MyApplication.getInstance().getDaoByKey(
					PlayedRecordDao.class.getName());
			records = dao.findAll();
			loadingLayout.onSuccess(true);
		}
		return loadingLayout;
	}

	@Override
	protected View setupDataView() {
		inflater = LayoutInflater.from(mActivity);
		View root = inflater.inflate(R.layout.mine_records, null);
		recordList = (PullToRefreshListView) root
				.findViewById(R.id.lv_played_records_list);
		recordList.setMode(Mode.BOTH);
		recordList.setOnRefreshListener(new RecordsListOnRefreshListener());
		adapter = new PlayRecordAdapter(mActivity);
		recordList.setOnItemClickListener(this);
		// dao.setOnDataChangeListener(new
		// OnDataChangeListener<PlayRecordInfo>() {
		//
		// @Override
		// public void dataChange(BaseDao dao) {
		// records = dao.findAll();
		// adapter.setRecords(records);
		// recordList.setAdapter(adapter);
		// }
		// });
		adapter.setRecords(records);
		recordList.setAdapter(adapter);
		return root;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		PlayRecordInfo record = (PlayRecordInfo) ((Holder) view.getTag()).re;
		if (null != record) {
			DisplayVideoInfo info = record.getDisplayVedioInfo();
			// info.setLastPositon(info.getLastPositon());
			PlayerAPI.startPlayActivity(mActivity, info);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_cancel:
			dialog.dismiss();
			break;
		case R.id.dialog_ensure:
			if (MyApplication.getInstance().isLogin()) {
				// TODO 批量删除播放记录接口
				removeRecordsRequest();
			}
			dao.deleleAll();
			records.clear();
			adapter.setRecords(records);
			adapter.notifyDataSetChanged();
			loadingLayout.onSuccess(true);
			dialog.dismiss();

			break;
		default:
			break;
		}
	}

	/**
	 * 批量删除播放记录的请求
	 */
	public void removeRecordsRequest() {
		new UiAsyncTask<Integer>(this) {
			@Override
			protected void post(Integer result) {
				if (result == 0) {
					records.clear();
					adapter.setRecords(records);
					adapter.notifyDataSetChanged();
					if (mContentView instanceof ViewGroup) {
						LayoutInflater mInflater = LayoutInflater
								.from(mActivity);
						ViewGroup mView = (ViewGroup) mInflater.inflate(
								R.layout.mine_nodata, null);
						ImageView nodataimage = (ImageView) mView
								.findViewById(R.id.iv_mine_nodata);
						TextView noDataText = (TextView) mView
								.findViewById(R.id.tv_mine_nodata);
						LayoutParams params = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						((ViewGroup) mContentView).addView(mView, params);
					}
					dialog.dismiss();
				}
			}

			@Override
			protected Integer doBackground() throws Throwable {
				String playRecordId = "";
				String playRecordList = "";
				RemoveRecordsDataRequest request = new RemoveRecordsDataRequest(
						mActivity);
				Map<String, String> mInputParam = new HashMap<String, String>();
				mInputParam.put(StringDataRequest.USER_ID,
						LoginInfoUtil.getUserId(mActivity));
				mInputParam.put(StringDataRequest.TENANT_ID, MyApplication
						.getInstance().getTenantId());
				if (records != null && !records.isEmpty()) {
					for (int i = 0; i < records.size(); i++) {
						playRecordId = records.get(i).getPlayRecordId();
						if (i == records.size() - 1) {
							playRecordList += playRecordId;
						} else {
							playRecordList += playRecordId + ",";
						}
					}
				}
				dao.deleleAll();
				records.clear();
				mInputParam.put("playRecordList", playRecordList);
				int code = request.setInputParam(mInputParam).request(
						Method.GET);
				return code;
			}
		}.showDialog().execute();
	}

	class RecordsListOnRefreshListener implements OnRefreshListener2<ListView> {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			new UpdateCollectRecordsTask(PlayRecordFragment.this, PULLFROMSTART)
					.execute();
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			new UpdateCollectRecordsTask(PlayRecordFragment.this, PULLFROMEND)
					.execute();
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
			if (recordList == null) {
				return;
			}
			recordList.onRefreshComplete();
			if (!MyApplication.getInstance().isLogin()) {
				recordList.setMode(Mode.PULL_FROM_START);
				return;
			}
			if (result && null != adapter) {
				adapter.setRecords(records);
				adapter.notifyDataSetChanged();
			} else {
				if (defalutPull == PULLFROMEND
						&& pageInfo.getPageIndex() + 1 > pageInfo
								.getTotalPage()) {
					recordList.setMode(Mode.PULL_FROM_START);
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

	private boolean loadingNextCollectionData() {
		GetPlayRecordsDataRequest request = new GetPlayRecordsDataRequest(
				mActivity);
		Map<String, String> mInputParam = new HashMap<String, String>();
		if (pageInfo.getPageIndex() + 1 <= pageInfo.getTotalPage()) {
			mInputParam.put(StringDataRequest.PAGE,
					String.valueOf(pageInfo.getPageIndex() + 1));
			mInputParam.put(StringDataRequest.PAGE_SIZE,
					String.valueOf(StringDataRequest.PAGE_SIZE_COUNT));
			mInputParam.put(StringDataRequest.USER_ID,
					LoginInfoUtil.getUserId(mActivity));
			mInputParam.put(StringDataRequest.TENANT_ID, MyApplication
					.getInstance().getTenantId());
			int status = request.setInputParam(mInputParam)
					.setOutputData(records).request(Method.GET);
			if (status == 0)
				return true;
			return false;
		} else {
			return false;
		}
	}
}
