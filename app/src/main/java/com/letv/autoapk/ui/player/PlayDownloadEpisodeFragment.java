package com.letv.autoapk.ui.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.lecloud.sdk.download.info.LeDownloadInfo;
import com.lecloud.sdk.download.observer.LeDownloadObserver;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.fragment.BaseFragment;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.boss.AuthDataRequest;
import com.letv.autoapk.boss.AuthInfo;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.download.DownloadSaasCenter;

public class PlayDownloadEpisodeFragment extends BaseFragment implements OnClickListener {
	private GridView downlaodEpisodeGridview;
	private TextView downlaodEpisodeTitle;
	private TextView downloadEpisodeClose;
	private TextView downloadCheck;
	private TextView downlaodCount;
	private RelativeLayout downloadEpisodeloadLay;
	private PlayDownlaodEpisodeAdapter downlaodEpisodeAdapter;
	private DownloadSaasCenter downloadSaaSCenter;

	private String totalCount;
	private String updateEpisode;
	private boolean isCheckAll = false;
	private List<PlayVideoInfo> downlaodEpisdeoInfos;
	List<String> videoIdList = new ArrayList<String>();
	private int hasDownCount = 0;
	private int hasPermissionCount = 0;
	private String videoId;
	private String albumId;

	public final static int UPFATE_DOWNLOAD_TEXT_FLAG = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		downloadSaaSCenter = DownloadSaasCenter.getInstances(mActivity.getApplicationContext());
		downloadSaaSCenter.allowShowMsg(false);
		Bundle bundle = getArguments();
		downlaodEpisdeoInfos = (List<PlayVideoInfo>) bundle.get("episodes");
		videoId = bundle.getString("videoId");
		albumId = bundle.getString("albumId");
	};

	@Override
	protected void onHandleMessage(Message msg) {
		switch (msg.what) {
		case UPFATE_DOWNLOAD_TEXT_FLAG:
			if (downlaodEpisodeAdapter.getDownloadCount() > 0) {
				downlaodCount.setBackgroundColor(getResources().getColor(R.color.code02));
				downlaodCount.setText(getString(R.string.play_downloadsize, downlaodEpisodeAdapter.getDownloadCount()));
			} else {
				downlaodCount.setBackgroundColor(getResources().getColor(R.color.code03));
				downlaodCount.setText(getString(R.string.play_downloadsize, downlaodEpisodeAdapter.getDownloadCount()));
			}
			// TODO 默认同专辑下权限一样，若不一样则后期修改
			int allowDownlaodSize = downlaodEpisdeoInfos.size() - hasDownCount - hasPermissionCount;
			if (downlaodEpisodeAdapter.getDownloadCount() == allowDownlaodSize && allowDownlaodSize != 0) {
				isCheckAll = true;
			} else {
				isCheckAll = false;
			}
			break;
		default:
			break;
		}

	}

	@Override
	protected View setupDataView() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		downloadEpisodeloadLay = (RelativeLayout) inflater.inflate(R.layout.play_download_episode_gridview, null);
		downlaodEpisodeTitle = (TextView) downloadEpisodeloadLay.findViewById(R.id.play_episode_tiltle);
		downloadEpisodeClose = (TextView) downloadEpisodeloadLay.findViewById(R.id.play_episode_colse);

		downloadCheck = (TextView) downloadEpisodeloadLay.findViewById(R.id.play_download_checkall);
		downlaodCount = (TextView) downloadEpisodeloadLay.findViewById(R.id.play_download_count);

		downlaodEpisodeGridview = (GridView) downloadEpisodeloadLay.findViewById(R.id.play_episode_grid);
		if (downlaodEpisdeoInfos == null) {
			downlaodEpisdeoInfos = new ArrayList<PlayVideoInfo>();
		}
		downlaodEpisodeAdapter = new PlayDownlaodEpisodeAdapter(mActivity, getDefaultHandler(), downlaodEpisdeoInfos,
				videoId, videoIdList);
		downlaodEpisodeGridview.setAdapter(downlaodEpisodeAdapter);

		// 设置码率
		downlaodEpisodeTitle.setText("");
		downloadCheck.setText(getResources().getString(R.string.play_downlaod_check));
		downlaodCount.setText(getString(R.string.play_downloadsize, "0"));
		downloadEpisodeClose.setOnClickListener(this);
		downloadCheck.setOnClickListener(this);
		downlaodCount.setOnClickListener(this);

		List<LeDownloadInfo> downloadInfoList = downloadSaaSCenter.getDownloadInfoList();
		if (downloadInfoList != null) {
			for (LeDownloadInfo leDownloadInfo : downloadInfoList) {
				videoIdList.add(leDownloadInfo.getVu());
			}
		}
		hasDownCount = 0;
		hasPermissionCount = 0;
		for (int i = 0; i < downlaodEpisdeoInfos.size(); i++) {
			if (videoIdList.contains(downlaodEpisdeoInfos.get(i).getVideoId())) {
				hasDownCount = hasDownCount + 1;
			}
			if (!(downlaodEpisdeoInfos.get(i).getDownloadPlatform() != null
					&& downlaodEpisdeoInfos.get(i).getDownloadPlatform().contains("104002"))) {
				hasPermissionCount = hasPermissionCount + 1;
			}
		}
		return downloadEpisodeloadLay;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play_episode_colse:
			getFragmentManager().popBackStack();
			break;
		case R.id.play_download_checkall:
			if (!isCheckAll) {
				isCheckAll = true;
				downlaodEpisodeAdapter.addAllDownlaodInfos();
				downlaodEpisodeAdapter.notifyDataSetChanged();
			} else {
				isCheckAll = false;
				downlaodEpisodeAdapter.removeAllDownloadInfos();
				downlaodEpisodeAdapter.notifyDataSetChanged();
			}
			getDefaultHandler().sendEmptyMessage(PlayDownloadEpisodeFragment.UPFATE_DOWNLOAD_TEXT_FLAG);
			break;
		case R.id.play_download_count:
			// Iterator<PlayVideoInfo> iterator =
			// downlaodEpisodeAdapter.getAddDownlaodInfos(new
			// HashSet<PlayVideoInfo>()).iterator();
			if (downlaodEpisodeAdapter.getAddDownlaodInfos().size() <= 0) {
				return;
			}
			boolean isdownloadFailture = PlayerAPI.addDownlaodLimit(mActivity);
			if (isdownloadFailture) {
				return;
			}
			batchDownlaodVideo();
			// 关闭下载页面
			getFragmentManager().popBackStack();
			break;
		}
	}

	private void batchDownlaodVideo() {
		// 将选中视频添加到下载队列
		Iterator<PlayVideoInfo> iterator = downlaodEpisodeAdapter.getAddDownlaodInfos().iterator();
		while (iterator.hasNext()) {
			PlayVideoInfo playVideoInfo = (PlayVideoInfo) iterator.next();
			if (playVideoInfo.getVideoId() != null) {
				PlayerAPI.addSDKdownlaod(mActivity, playVideoInfo, downloadSaaSCenter, false);
			}
		}
		if (downlaodEpisodeAdapter.getAddDownlaodInfos().size() > 0) {
			Toast.makeText(mActivity, getResources().getString(R.string.play_downlaod), 0).show();
		}
	};

	@Override
	public void onPause() {
		downloadSaaSCenter.unregisterDownloadObserver(leDownloadObserver);
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * 下载监听
	 */
	LeDownloadObserver leDownloadObserver = new LeDownloadObserver() {

		@Override
		public void onDownloadSuccess(LeDownloadInfo info) {
			// info.setDownloadState(LeDownloadObserver.DOWLOAD_STATE_SUCCESS);
		}

		@Override
		public void onDownloadStop(LeDownloadInfo info) {
			Log.e("gsgs", "onDownloadStop ");
			// info.setDownloadState(LeDownloadObserver.DOWLOAD_STATE_STOP);
		}

		@Override
		public void onDownloadStart(LeDownloadInfo info) {
			Log.e("gsgs", "onDownloadStart ");
		}

		@Override
		public void onDownloadProgress(LeDownloadInfo info) {
			// info.setDownloadState(LeDownloadObserver.DOWLOAD_STATE_DOWNLOADING);
		}

		@Override
		public void onDownloadCancel(LeDownloadInfo info) {
		}

		@Override
		public void onDownloadFailed(LeDownloadInfo arg0, String arg1) {
			// TODO Auto-generated method stub
			mActivity.showToastSafe(arg1, 0);
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
}
