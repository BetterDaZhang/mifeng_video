package com.letv.autoapk.boss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
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
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.ui.me.PersonalFragment;
import com.letv.autoapk.utils.SerializeableUtil;

public class ConsumeRecordsFragment extends BaseTitleFragment
		implements OnRefreshListener2<ListView>, LePaySuccessListener {
	private static final String TAG = "ConsumeRecordsFragment";
	private ArrayList<ConsumeInfo> list = new ArrayList<ConsumeInfo>();
	private ArrayList<ConsumeInfo> templist = new ArrayList<ConsumeInfo>();
	private PageInfo mPageInfo;
	private PullToRefreshListView listView;
	private ConsumeRecordsAdapter adapter;

	@Override
	protected boolean loadingData() {
		mPageInfo = new PageInfo();
		templist.clear();
		ConsumeRecordsRequest request = new ConsumeRecordsRequest(mActivity);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.PAGE, String.valueOf(1));
		mInputParam.put(StringDataRequest.PAGE_SIZE, String.valueOf(StringDataRequest.PAGE_SIZE_COUNT));
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
		mInputParam.put("version", LepayManager.LEPAY_VERSION);
		int code = request.setInputParam(mInputParam).setOutputData(templist, mPageInfo).request(Request.Method.GET);

		Logger.d(TAG, "code:" + code);
		if (code == 0) {
			list.clear();
			list.addAll(templist);
			return true;
		}
		return false;
	}

	@Override
	protected void onHandleMessage(Message msg) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initCustomerView() {
		setTitle(getString(R.string.title_consume), getResources().getColor(R.color.code6));
		setTitleLeftResource(R.drawable.base_head_back, mActivity.dip2px(3));

		setLeftClickListener(new TitleLeftClickListener() {
			@Override
			public void onLeftClickListener() {
				getActivity().finish();
			}
		});
	}

	@Override
	protected View setupDataView() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View root = inflater.inflate(R.layout.boss_recordlist, null);

		listView = (PullToRefreshListView) root.findViewById(R.id.pull_refresh_list);
		listView.setOnRefreshListener(this);
		listView.setMode(Mode.BOTH);
		adapter = new ConsumeRecordsAdapter(mActivity, list, this);
		listView.setAdapter(adapter);
		setPaySuccessListener();
		return root;
	}

	protected boolean hasContentData() {
		return list != null && !list.isEmpty();
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
			ConsumeRecordsRequest request = new ConsumeRecordsRequest(getActivity());
			Map<String, String> mInputParam = new HashMap<String, String>();
			if (mPageInfo.getPageIndex() + 1 <= mPageInfo.getTotalPage()) {
				mInputParam.put(StringDataRequest.PAGE, String.valueOf(mPageInfo.getPageIndex() + 1));
				mInputParam.put(StringDataRequest.PAGE_SIZE, String.valueOf(StringDataRequest.PAGE_SIZE_COUNT));
				mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());//
				mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
				mInputParam.put("version", LepayManager.LEPAY_VERSION);
				int code = request.setInputParam(mInputParam).setOutputData(list, mPageInfo)
						.request(Request.Method.GET);
				if (code == 0) {
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
			listView.onRefreshComplete();
			if (result) {
				adapter.notifyDataSetChanged();
			} else if (defalutPull == PullFromEnd && mPageInfo.getPageIndex() + 1 > mPageInfo.getTotalPage()) {
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
				return loadingData();
			}
		}
	}

	@Override
	public void paymentSuccess() {
		new UiAsyncTask<Integer>(this) {

			@Override
			protected Integer doBackground() throws Throwable {
				boolean code = loadingData();
				if (code) {
					return 0;
				} else {
					return -1;
				}

			}

			protected void post(Integer result) {
				if (adapter != null) {
					adapter.notifyDataSetChanged();
				}
			}

		}.showDialog().execute();
	}

	private void setPaySuccessListener() {
		MyApplication.getInstance().setLePaySuccessListener(this);
	}
}
