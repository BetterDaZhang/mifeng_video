package com.letv.autoapk.ui.offline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lecloud.sdk.download.info.LeDownloadInfo;
import com.lecloud.sdk.download.observer.LeDownloadObserver;
import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.download.DownloadSaasCenter;
import com.letv.autoapk.download.LeOfflineInfo;
import com.letv.autoapk.utils.SerializeableUtil;
import com.letv.autoapk.widgets.LoadingLayout;

public class OfflineFragment extends BaseTitleFragment implements
		OnClickListener {
	private final String TAG = "OfflineFragment";
	private PullToRefreshListView mPullRefreshListView;
	private List<LeDownloadInfo> downloadInfos;
	private List<LeOfflineInfo> offlineInfos = new ArrayList<LeOfflineInfo>();
	private OfflineAdapter offlineAdatpter;
	private View offline_checkall_rl;
	private TextView allSelect;
	private TextView delete;
	private TextView titleRight;
	private boolean isCheckAll = false;
	/** 存储专辑，key为专辑的Id，值为专辑的视频集合 */
	HashMap<String, List<LeOfflineInfo>> albumMap = new HashMap<String, List<LeOfflineInfo>>();

	@Override
	protected void onHandleMessage(Message msg) {

	}

	protected void initCustomerView() {
		setTitle(getResources().getString(R.string.mine_offline_video),
				getResources().getColor(R.color.code6));
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
	private static final String LeOfflineInfo = null;
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
		try {
			Thread.sleep(50);
			downloadInfos = mDownloadSaasCenter.getDownloadInfoList();
			if (downloadInfos == null || downloadInfos.size() == 0) {
				return true;
			}
			getOfflineInfos();
		} catch (Exception e) {
			Logger.log(e);
			return true;
		}
		return true;
	}

	/**
	 * 将数据库中下载的视频信息获取到，并转换成我们需要的离线信息model
	 */
	private List<LeOfflineInfo> getOfflineInfos() {
		offlineInfos.clear();
		for (LeDownloadInfo info : downloadInfos) {
			String extendInfo = info.getString1();
			LeOfflineInfo leOfflineInfo = (LeOfflineInfo) SerializeableUtil
					.readObjectFromHexString(mActivity, extendInfo);
			leOfflineInfo.setProgress(info.getProgress());
			leOfflineInfo.setVedioId(info.getVu());
			leOfflineInfo.setFileName(info.getFileName());
			leOfflineInfo.setFileSavePath(info.getFileSavePath());
			leOfflineInfo.setFileLength(info.getFileLength());
			leOfflineInfo.setDownloadState(info.getDownloadState());
			leOfflineInfo.setDownloadUrl(info.getDownloadUrl());
			leOfflineInfo.setLeDownloadInfo(info);
			offlineInfos.add(leOfflineInfo);
		}
		return offlineInfos;
	}

	/**
	 * 将单个leDownloadInfo转换成了leOfflineInfo
	 * 
	 * @param info
	 * @return
	 */
	public LeOfflineInfo getSpecialOfflineInfoByLeDownloadInfo(
			LeDownloadInfo info) {
		if (info == null) {
			return null;
		}
		LeOfflineInfo offlineInfo = null;
		String extendInfo = info.getString1();
		LeOfflineInfo leOfflineInfo = (LeOfflineInfo) SerializeableUtil
				.readObjectFromHexString(mActivity, extendInfo);
		for (int i = 0; i < offlineInfos.size(); i++) {
			if (info.getVu().equals(offlineInfos.get(i).getVedioId())) {
				offlineInfo = offlineInfos.get(i);
				break;
			}
		}
		List<LeOfflineInfo> list = albumMap.get(leOfflineInfo.getAlbumId());
		if (list != null) {
			for (LeOfflineInfo leInfo : list) {
				if (info.getVu().equals(leInfo.getVedioId())) {
					offlineInfo = leInfo;
					break;
				}
			}
		}
		if (offlineInfo == null) {
			return null;
		}
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

	@Override
	protected View setupDataView() {
		titleRight = new TextView(mActivity);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,
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
				if (offlineAdatpter.getCount() == 0) {
					return;
				}
				if (!offlineAdatpter.isDeleteState()) {
					titleRight.setText(R.string.cancel);
					offlineAdatpter.getDeleteInfos().clear();
					offlineAdatpter.getDeleteAlbums().clear();
					offlineAdatpter.setDeleteState(true);
					// setTitleRightResource(R.drawable.offline_clear_focused);
					offline_checkall_rl.setVisibility(View.VISIBLE);
					if (offlineAdatpter.getDeleteCount() > 0) {
						delete.setBackgroundColor(getResources().getColor(
								R.color.code04));
					} else {
						delete.setBackgroundColor(getResources().getColor(
								R.color.code04));
					}
					delete.setText(getResources().getString(
							R.string.offline_delete)
							+ "(" + offlineAdatpter.getDeleteCount() + ")");
					isCheckAll = false;
				} else {
					titleRight.setText(R.string.mine_edit);
					offlineAdatpter.setDeleteState(false);
					offlineAdatpter.removeAll();
					isCheckAll = false;
					// setTitleRightResource(R.drawable.offline_clear);
					offline_checkall_rl.setVisibility(View.GONE);
					isCheckAll = false;

				}
				offlineAdatpter.notifyDataSetChanged();
			}
		});
		mTitleRightLay.addView(titleRight);
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View root = inflater.inflate(R.layout.mine_offline, null);
		mPullRefreshListView = (PullToRefreshListView) root
				.findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setMode(Mode.DISABLED);
		handleOfflineInfo();
		// temp data
		offlineAdatpter = new OfflineAdapter(getActivity(), offlineInfos,
				albumMap, handler);
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
			deleteOfflineVideos();
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

	private void deleteOfflineVideos() {
		if (offlineAdatpter.getDeleteCount() == 0) {
			return;
		}
		for (LeOfflineInfo leOfflineInfo : offlineAdatpter.getDeleteInfos()) {
			mDownloadSaasCenter.cancelDownload(
					leOfflineInfo.getLeDownloadInfo(), true);
			offlineAdatpter.getOfflineInfos().remove(leOfflineInfo);
		}
		HashMap<String, List<LeOfflineInfo>> deleteAlbums = offlineAdatpter
				.getDeleteAlbums();
		Set<String> keySet = deleteAlbums.keySet();
		for (String key : keySet) {
			List<LeOfflineInfo> list = deleteAlbums.get(key);
			for (LeOfflineInfo leOfflineInfo : list) {
				mDownloadSaasCenter.cancelDownload(
						leOfflineInfo.getLeDownloadInfo(), true);
			}
			offlineAdatpter.getAlbumMap().remove(list.get(0).getAlbumId());
		}
		offlineAdatpter.getDeleteInfos().clear();
		offlineAdatpter.getDeleteAlbums().clear();
		updateDeleteCount();
		if (offlineAdatpter.getOfflineInfos().size() == 0
				&& offlineAdatpter.getAlbumMap().size() == 0) {
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

		titleRight.setText(R.string.mine_edit);
		mActivity.showToastSafe(R.string.mine_delete_success,
				Toast.LENGTH_SHORT);
		if (offlineAdatpter.getOfflineInfos().isEmpty()
				&& offlineAdatpter.getAlbumMap().isEmpty()) {
			titleRight.setVisibility(View.GONE);
			if (mContentView instanceof ViewGroup) {
				LayoutInflater mInflater = LayoutInflater.from(mActivity);
				ViewGroup mView = (ViewGroup) mInflater.inflate(
						R.layout.mine_nooffline, null);
				LayoutParams params = new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				((ViewGroup) mContentView).addView(mView, params);
			}
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
				ViewGroup mView = (ViewGroup) mInflater.inflate(
						R.layout.mine_nooffline, null);
				return mView;
			}
		};
		downloadInfos = mDownloadSaasCenter.getDownloadInfoList();
		if (downloadInfos == null || downloadInfos.size() == 0) {
		}
		getOfflineInfos();
		loadingLayout.onSuccess(true);
		return loadingLayout;
	}

	@Override
	protected boolean hasContentData() {
		return downloadInfos != null && !downloadInfos.isEmpty();
	}

	protected void updateDeleteCount() {
		if (offlineAdatpter.getDeleteCount() > 0) {
			delete.setBackgroundColor(getResources().getColor(R.color.code04));
		} else {
			delete.setBackgroundColor(getResources().getColor(R.color.code04));
		}
		delete.setText(getResources().getString(R.string.offline_delete) + "("
				+ offlineAdatpter.getDeleteCount() + ")");
		int allowDownlaodSize = offlineAdatpter.getDeleteCount();
		int totalDownlaodSize = offlineAdatpter.getOfflineCount();
		if (totalDownlaodSize == allowDownlaodSize && allowDownlaodSize != 0) {
			isCheckAll = true;
		} else {
			isCheckAll = false;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mDownloadSaasCenter = DownloadSaasCenter.getInstances(mActivity.getApplicationContext());
		mDownloadSaasCenter.allowShowMsg(false);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (offlineAdatpter != null) {
			downloadInfos.clear();
			downloadInfos = mDownloadSaasCenter.getDownloadInfoList();
			if (downloadInfos == null || downloadInfos.isEmpty()) {
				if (mContentView instanceof ViewGroup) {
					LayoutInflater mInflater = LayoutInflater.from(mActivity);
					ViewGroup mView = (ViewGroup) mInflater.inflate(
							R.layout.mine_nooffline, null);
					LayoutParams params = new LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT);
					((ViewGroup) mContentView).addView(mView, params);
					titleRight.setVisibility(View.GONE);
				}
				return;
			}
			getOfflineInfos();
			handleOfflineInfo();
			offlineAdatpter.setOfflineInfos(offlineInfos);
			offlineAdatpter.setAlbumMap(albumMap);
			offlineAdatpter.notifyDataSetChanged();
			mDownloadSaasCenter.registerDownloadObserver(leDownloadObserver);
		}
		Logger.d(TAG, "onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		mDownloadSaasCenter.unregisterDownloadObserver(leDownloadObserver);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	public void bindListener() {

	}

	private void updateItemView(LeOfflineInfo info) {
		int position = getInfoPosition(info);
		if (NOT_VISIBLE_IN_CURRENT_SCREEN != position) {
			View converview = mPullRefreshListView.getRefreshableView()
					.getChildAt(position + 1 - firstVisibleIndex);
			if (null != converview) {
				OfflineViewHolder viewHolder = (OfflineViewHolder) converview
						.getTag();
				if (null != viewHolder) {
					viewHolder.updateItemState(info,
							albumMap.get(info.getAlbumId()));
				}
			}
		}
	}

	/**
	 * if info in current screen is visible return position otherwise return
	 * NOT_VISIBLE_IN_CURRENT_SCREEN
	 */
	protected int getInfoPosition(LeOfflineInfo info) {
		if (info == null) {
			return NOT_VISIBLE_IN_CURRENT_SCREEN;
		}
		firstVisibleIndex = mPullRefreshListView.getRefreshableView()
				.getFirstVisiblePosition();
		lastVisibleIndex = mPullRefreshListView.getRefreshableView()
				.getLastVisiblePosition();

		for (int i = 0; i < offlineInfos.size(); i++) {
			if (info.getVedioId().equals(offlineInfos.get(i).getVedioId())) {
				return i;
			}
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
			// info.setDownloadState(LeDownloadObserver.DOWLOAD_STATE_SUCCESS);
			// Log.e(TAG, "onDownloadSuccess 中的状态是：" + info.getDownloadState());
			updateItemView(getSpecialOfflineInfoByLeDownloadInfo(info));
		}

		@Override
		public void onDownloadStop(LeDownloadInfo info) {
			Log.e("gsgs", "onDownloadStop ");
			// info.setDownloadState(LeDownloadObserver.DOWLOAD_STATE_STOP);
			// Log.e(TAG, "onDownloadStop 中的状态是：" + info.getDownloadState());
			updateItemView(getSpecialOfflineInfoByLeDownloadInfo(info));
		}

		@Override
		public void onDownloadStart(LeDownloadInfo info) {
			Log.e("gsgs", "onDownloadStart ");
			// Log.e(TAG, "onDownloadStart 中的状态是：" + info.getDownloadState());
			updateItemView(getSpecialOfflineInfoByLeDownloadInfo(info));
		}

		@Override
		public void onDownloadProgress(LeDownloadInfo info) {
			// info.setDownloadState(LeDownloadObserver.DOWLOAD_STATE_DOWNLOADING);
			// Log.e(TAG, "onDownloadProgress 中的状态是：" +
			// info.getDownloadState());
			updateItemView(getSpecialOfflineInfoByLeDownloadInfo(info));
		}

		@Override
		public void onDownloadCancel(LeDownloadInfo info) {
			Log.i(TAG, "cancel ");
			// Log.e(TAG, "onDownloadCancel 中的状态是：" + info.getDownloadState());
		}

		@Override
		public void onDownloadFailed(LeDownloadInfo info, String arg1) {
//			mActivity.showToastSafe(arg1, 0);
			// Log.e(TAG, "onDownloadFailed 中的状态是：" + info.getDownloadState(),
			// arg1);
			updateItemView(getSpecialOfflineInfoByLeDownloadInfo(info));

		}

		@Override
		public void onDownloadInit(LeDownloadInfo info, String arg1) {

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
	private int lastVisibleIndex;
	private int firstVisibleIndex;
	private DownloadSaasCenter mDownloadSaasCenter;

	/**
	 * 处理离线数据，以适应专辑展示使用<br>
	 * 走完for循环，albumMap中就是所有的专辑集合， <br>
	 * offlineInfos中就是所有的非专辑视频 在setAdapter <br>
	 * 的时候，将offlineInfos和albumMap同时传递
	 */
	public void handleOfflineInfo() {
		albumMap.clear();
		/** 专辑集合 */
		List<LeOfflineInfo> albumList;
		boolean isAlbum = false;

		if (offlineInfos == null || offlineInfos.isEmpty()) {
			return;
		}

		for (int i = 0; i < offlineInfos.size(); i++) {
			albumList = new ArrayList<LeOfflineInfo>();
			isAlbum = false;
			for (int j = i + 1; j < offlineInfos.size(); j++) {
				if (!"0".equals(offlineInfos.get(i).getAlbumId())
						&& offlineInfos.get(i).getAlbumId()
								.equals(offlineInfos.get(j).getAlbumId())) {
					albumList.add(offlineInfos.remove(j));
					j--;
					isAlbum = true;
				}
			}
			if (isAlbum) {
				albumMap.put(offlineInfos.get(i).getAlbumId(), albumList);
				albumMap.get(offlineInfos.get(i).getAlbumId()).add(
						offlineInfos.remove(i));
				i--;
			}
		}
	}
}
