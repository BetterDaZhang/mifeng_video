package com.letv.autoapk.ui.offline;

/**
 * 离线视频中专辑内容页面
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lecloud.sdk.download.info.LeDownloadInfo;
import com.lecloud.sdk.download.observer.LeDownloadObserver;
import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.download.DownloadSaasCenter;
import com.letv.autoapk.download.LeOfflineInfo;
import com.letv.autoapk.utils.SerializeableUtil;
import com.letv.autoapk.widgets.LoadingLayout;

public class OfflineAlbumFragment extends BaseTitleFragment implements OnClickListener {
	private final String TAG = "OfflineAlbumFragment";
	private PullToRefreshListView mPullRefreshListView;
	private List<LeDownloadInfo> downloadInfos;
	private List<LeOfflineInfo> offlineInfos = new ArrayList<LeOfflineInfo>();
	private OfflineAlbumAdapter offlineAdatpter;
	private View offline_checkall_rl;
	private TextView allSelect;
	private TextView delete;
	private TextView titleRight;
	private boolean isCheckAll = false;

	@Override
	protected void onHandleMessage(Message msg) {
		// TODO Auto-generated method stub

	}

	protected void initCustomerView() {
		setTitle(offlineInfos.get(0).getAlbumName(), getResources().getColor(R.color.code6));
		setTitleLeftResource(R.drawable.mine_login_back, mActivity.dip2px(16));
		setStatusBarColor(getResources().getColor(R.color.code04));
		setLeftClickListener(new TitleLeftClickListener() {

			@Override
			public void onLeftClickListener() {
				getActivity().finish();
			}
		});
	}

	public static final int UPFATE_DELETE_TEXT_FLAG = 111;
	public static final int UPDATE_CHECKALL_FLAG = 112;
	public static final String ALBUM_LIST = "album_list";
	private Handler handler = new Handler() {

		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case UPFATE_DELETE_TEXT_FLAG:
				updateDeleteCount();
				break;
			case UPDATE_CHECKALL_FLAG:
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected boolean loadingData() {
		return true;
	}

	@Override
	protected View setupDataView() {
		titleRight = new TextView(mActivity);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
		titleRight.setGravity(Gravity.CENTER);
		titleRight.setLayoutParams(params);
		titleRight.setText(R.string.mine_edit);
		titleRight.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
		titleRight.setTextColor(getResources().getColor(R.color.code7));
		titleRight.setPadding(0, 0, mActivity.dip2px(15), 0);
		titleRight.setId(android.R.id.edit);
		setRightClickListener(new TitleRightClickListener() {

			@Override
			public void onRightClickListener() {
				if (offlineAdatpter.getCount() == 0) {
					return;
				}
				if (!offlineAdatpter.isDeleteState()) {
					titleRight.setText(R.string.cancel);
					offlineAdatpter.getDeleteInfos().clear();
					offlineAdatpter.setDeleteState(true);
					// setTitleRightResource(R.drawable.offline_clear_focused);
					offline_checkall_rl.setVisibility(View.VISIBLE);
					if (offlineAdatpter.getDeleteCount() > 0) {
						delete.setBackgroundColor(getResources().getColor(R.color.code04));
					} else {
						delete.setBackgroundColor(getResources().getColor(R.color.code04));
					}
					delete.setText(
							getResources().getString(R.string.offline_delete) + "(" + offlineAdatpter.getDeleteCount() + ")");
					isCheckAll = false;
				} else {
					titleRight.setText(R.string.mine_edit);
					offlineAdatpter.setDeleteState(false);
					// setTitleRightResource(R.drawable.offline_clear);
					offline_checkall_rl.setVisibility(View.GONE);
					isCheckAll = false;

				}
				offlineAdatpter.notifyDataSetChanged();
			}
		});
		mTitleRightLay.addView(titleRight);
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View root = inflater.inflate(R.layout.mine_offline, null);
		mPullRefreshListView = (PullToRefreshListView) root.findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setMode(Mode.DISABLED);
		// temp data
		offlineAdatpter = new OfflineAlbumAdapter(getActivity(), offlineInfos, handler);
		mPullRefreshListView.setAdapter(offlineAdatpter);
		// updateListView();
		offline_checkall_rl = root.findViewById(R.id.offline_checkall_rl);
		allSelect = (TextView) root.findViewById(R.id.tv_offline_all);
		delete = (TextView) root.findViewById(R.id.tv_delect);

		allSelect.setOnClickListener(this);
		delete.setOnClickListener(this);
		mDownloadSaasCenter.registerDownloadObserver(leDownloadObserver);
		return root;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_delect:
			if (offlineAdatpter.getDeleteInfos().size() == 0) {
				return;
			}
			for (LeOfflineInfo leOfflineInfo : offlineAdatpter.getDeleteInfos()) {
				mDownloadSaasCenter.cancelDownload(leOfflineInfo.getLeDownloadInfo(), true);
				offlineAdatpter.getOfflineInfos().remove(leOfflineInfo);
			}
			offlineAdatpter.getDeleteInfos().clear();
			updateDeleteCount();
			if (offlineAdatpter.getOfflineInfos().size() == 0) {
				getActivity().finish();
				// 换成灰色
				// setTitleRightResource(R.drawable.offline_clear_gray);
				offline_checkall_rl.setVisibility(View.GONE);
				mPullRefreshListView.setVisibility(View.GONE);
			} else {
				// setTitleRightResource(R.drawable.offline_clear);
				offline_checkall_rl.setVisibility(View.GONE);
				mPullRefreshListView.setVisibility(View.VISIBLE);
			}
			offlineAdatpter.setDeleteState(false);
			offlineAdatpter.notifyDataSetChanged();
			break;
		case R.id.tv_offline_all:
			if (!isCheckAll) {
				isCheckAll = true;
				offlineAdatpter.checkedAll();
			} else {
				isCheckAll = false;
				offlineAdatpter.removeAll();
			}
			break;

		default:
			break;
		}

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
				ViewGroup mView = (ViewGroup) mInflater.inflate(R.layout.mine_nooffline, null);
				// ImageView nodataimage = (ImageView)
				// mView.findViewById(R.id.iv_mine_nodata);
				// TextView noDataText = (TextView)
				// mView.findViewById(R.id.tv_mine_nodata);
				return mView;
			}
		};
		loadingLayout.onSuccess(true);
		return loadingLayout;
	}

	@Override
	protected boolean hasContentData() {
		return offlineInfos != null && !offlineInfos.isEmpty();
	}

	protected void updateDeleteCount() {
		if (offlineAdatpter.getDeleteCount() > 0) {
			delete.setBackgroundColor(getResources().getColor(R.color.code04));
		} else {
			delete.setBackgroundColor(getResources().getColor(R.color.code04));
		}
		delete.setText(getResources().getString(R.string.offline_delete) + "(" + offlineAdatpter.getDeleteCount() + ")");
		int allowDownlaodSize = offlineAdatpter.getDeleteCount();
		int totalDownlaodSize = offlineAdatpter.getOfflineInfos().size();
		if (totalDownlaodSize == allowDownlaodSize && allowDownlaodSize != 0) {
			isCheckAll = true;
		} else {
			isCheckAll = false;
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mDownloadSaasCenter = DownloadSaasCenter.getInstances(mActivity.getApplicationContext());
		mDownloadSaasCenter.allowShowMsg(false);
		Bundle arguments = getArguments();
		offlineInfos = (List<LeOfflineInfo>) MyApplication.getInstance().getInfo(OfflineAlbumFragment.ALBUM_LIST);
		Collections.sort(offlineInfos);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		if (offlineAdatpter != null) {
			mDownloadSaasCenter.registerDownloadObserver(leDownloadObserver);
		}
		super.onResume();
		Logger.d(TAG, "onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		mDownloadSaasCenter.unregisterDownloadObserver(leDownloadObserver);
	}

	public void bindListener() {

	}

	private void updateItemView(LeOfflineInfo info) {
		int aPositon = getInfoPosition(info);
		if (NOT_VISIBLE_IN_CURRENT_SCREEN != aPositon) {
			View converview = mPullRefreshListView.getRefreshableView().getChildAt(aPositon - firstVisibleIndex);
			if (null != converview) {
				OfflineAlbumViewHolder viewHolder = (OfflineAlbumViewHolder) converview.getTag();
				if (null != viewHolder) {
					LeOfflineInfo cloneInfo = viewHolder.cloneLeDownloadInfo(offlineAdatpter, info, aPositon - 1);
					viewHolder.updateItemState(cloneInfo, null);
				}
			}
		}
	}

	/**
	 * if info in current screen is visible return position otherwise return
	 * NOT_VISIBLE_IN_CURRENT_SCREEN
	 */
	protected int getInfoPosition(LeOfflineInfo info) {
		firstVisibleIndex = mPullRefreshListView.getRefreshableView().getFirstVisiblePosition();
		lastVisibleIndex = mPullRefreshListView.getRefreshableView().getLastVisiblePosition();
		int position = offlineAdatpter.getItemPosition(info);
		int aPosition = position + 1;
		if ((aPosition) >= firstVisibleIndex && (aPosition) <= lastVisibleIndex) {
			return aPosition;
		}
		return NOT_VISIBLE_IN_CURRENT_SCREEN;
	}

	private static final int NOT_VISIBLE_IN_CURRENT_SCREEN = -1;

	/**
	 * 下载监听
	 */
	LeDownloadObserver leDownloadObserver = new LeDownloadObserver() {

		@Override
		public void onDownloadSuccess(LeDownloadInfo info) {
//			Log.i(TAG, "success ");
			updateItemView(getSpecialOfflineInfoByLeDownloadInfo(info));
		}

		@Override
		public void onDownloadStop(LeDownloadInfo info) {
//			Log.e("gsgs", "onDownloadStop ");
			updateItemView(getSpecialOfflineInfoByLeDownloadInfo(info));
		}

		@Override
		public void onDownloadStart(LeDownloadInfo info) {
//			Log.e("gsgs", "onDownloadStart ");
			updateItemView(getSpecialOfflineInfoByLeDownloadInfo(info));
		}

		@Override
		public void onDownloadProgress(LeDownloadInfo info) {
			updateItemView(getSpecialOfflineInfoByLeDownloadInfo(info));
		}

		@Override
		public void onDownloadCancel(LeDownloadInfo info) {
			Log.i(TAG, "cancel ");
		}


		@Override
		public void onDownloadFailed(LeDownloadInfo info, String arg1) {
			// TODO Auto-generated method stub
//			mActivity.showToastSafe(arg1, 0);
			updateItemView(getSpecialOfflineInfoByLeDownloadInfo(info));
		}

		@Override
		public void onDownloadInit(LeDownloadInfo arg0, String arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDownloadWait(LeDownloadInfo arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetVideoInfoRate(LeDownloadInfo arg0, List<String> arg1) {
			// TODO Auto-generated method stub
			
		}
	};

	/**
	 * 将单个leDownloadInfo转换成了leOfflineInfo
	 * 
	 * @param info
	 * @return
	 */
	public LeOfflineInfo getSpecialOfflineInfoByLeDownloadInfo(LeDownloadInfo info) {
		if(info==null){
			return null;
		}
		LeOfflineInfo offlineInfo = null;
		for (int i = 0; i < offlineInfos.size(); i++) {
			if (info.getVu().equals(offlineInfos.get(i).getVedioId())) {
				offlineInfo = offlineInfos.get(i);
				break;
			}
		}
		if (offlineInfo == null) {
			return null;
		}
		String extendInfo = info.getString1();
		LeOfflineInfo leOfflineInfo = (LeOfflineInfo) SerializeableUtil.readObjectFromHexString(mActivity, extendInfo);
		offlineInfo.setProgress(info.getProgress());
		offlineInfo.setFileName(info.getFileName());
		offlineInfo.setFileSavePath(info.getFileSavePath());
		offlineInfo.setFileLength(info.getFileLength());
		offlineInfo.setDownloadState(info.getDownloadState());
		offlineInfo.setDownloadUrl(info.getDownloadUrl());
		offlineInfo.setLeDownloadInfo(info);
		offlineInfo.setVedioId(info.getVu());
		offlineInfo.setAlbumId(leOfflineInfo.getAlbumId());
		offlineInfo.setAlbumName(leOfflineInfo.getAlbumName());
		offlineInfo.setAlbumPicUrl(leOfflineInfo.getAlbumPicUrl());
		offlineInfo.setImgUrl(leOfflineInfo.getImgUrl());
		offlineInfo.setVideoTitle(leOfflineInfo.getVideoTitle());
		return offlineInfo;
	}

	private int lastVisibleIndex;
	private int firstVisibleIndex;
	private DownloadSaasCenter mDownloadSaasCenter;

}
