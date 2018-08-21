package com.letv.autoapk.ui.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
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

public class PlayDownloadArtsFragment extends BaseFragment implements OnClickListener {
	private ListView downloadArtsListview;
	private TextView downlaodArtsTitle;
	private ImageView downloadArtsExit;
	private RelativeLayout downlaodArtLay;
	private PlayDownlaodArtsAdapter downlaodArtsAdapter;
	private TextView downloadCheck;
	private TextView downlaodCount;

	private List<PlayVideoInfo> episdeoInfos;
	List<String> videoIdList = new ArrayList<String>();
	private String videoId;
	private String albumId;
	private Handler handler;
	private boolean isCheckAll = false;
	public final static int UPFATE_DOWNLOAD_TEXT_FLAG = 0;
	private int hasDownCount = 0;
	private int hasPermissionCount = 0;
	private DownloadSaasCenter downloadSaaSCenter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		downloadSaaSCenter = DownloadSaasCenter.getInstances(mActivity.getApplicationContext());
		downloadSaaSCenter.allowShowMsg(false);
		Bundle bundle = getArguments();
		episdeoInfos = (List<PlayVideoInfo>) bundle.get("episodes");
		videoId = bundle.getString("videoId");
		albumId = bundle.getString("albumId");
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected void onHandleMessage(Message msg) {
		switch (msg.what) {
		case UPFATE_DOWNLOAD_TEXT_FLAG:
			if (downlaodArtsAdapter.getDownloadCount() > 0) {
				downlaodCount.setBackgroundColor(getResources().getColor(R.color.code02));
				downlaodCount.setText(getString(R.string.play_downloadsize, downlaodArtsAdapter.getDownloadCount()));
			} else {
				downlaodCount.setBackgroundColor(getResources().getColor(R.color.code03));
				downlaodCount.setText(getString(R.string.play_downloadsize, downlaodArtsAdapter.getDownloadCount()));
			}
			// TODO 默认同专辑下权限一样，若不一样则后期修改
			int allowDownlaodSize = episdeoInfos.size() - hasDownCount - hasPermissionCount;
			if (downlaodArtsAdapter.getDownloadCount() == allowDownlaodSize && allowDownlaodSize != 0) {
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
		downlaodArtLay = (RelativeLayout) inflater.inflate(R.layout.play_downlaod_arts_listview, null);
		downlaodArtsTitle = (TextView) downlaodArtLay.findViewById(R.id.play_arts_tiltle);
		downloadArtsExit = (ImageView) downlaodArtLay.findViewById(R.id.play_arts_exit);

		downloadCheck = (TextView) downlaodArtLay.findViewById(R.id.play_download_checkall);
		downlaodCount = (TextView) downlaodArtLay.findViewById(R.id.play_download_count);

		downloadArtsListview = (ListView) downlaodArtLay.findViewById(R.id.play_arts_listView);
		if (episdeoInfos == null) {
			episdeoInfos = new ArrayList<PlayVideoInfo>();
		}
		downlaodArtsAdapter = new PlayDownlaodArtsAdapter(mActivity, getDefaultHandler(), episdeoInfos, videoId,
				videoIdList);
		downloadArtsListview.setAdapter(downlaodArtsAdapter);

		downlaodArtsTitle.setText(mActivity.getResources().getString(R.string.play_detail_arts_title));
		// 设置码率
		downlaodArtsTitle.setText("");
		downloadCheck.setText(getResources().getString(R.string.play_downlaod_check));
		downlaodCount.setText(getString(R.string.play_downloadsize, "0"));
		downloadArtsExit.setOnClickListener(this);
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
		for (int i = 0; i < episdeoInfos.size(); i++) {
			if (videoIdList.contains(episdeoInfos.get(i).getVideoId())) {
				hasDownCount = hasDownCount + 1;
			}
			if (!(episdeoInfos.get(i).getDownloadPlatform() != null
					&& episdeoInfos.get(i).getDownloadPlatform().contains("104002"))) {
				hasPermissionCount = hasPermissionCount + 1;
			}
		}
		return downlaodArtLay;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play_arts_exit:
			getFragmentManager().popBackStack();
			break;
		case R.id.play_download_checkall:
			if (!isCheckAll) {
				isCheckAll = true;
				downlaodArtsAdapter.addAllDownlaodInfos();
				downlaodArtsAdapter.notifyDataSetChanged();
			} else {
				isCheckAll = false;
				downlaodArtsAdapter.removeAllDownloadInfos();
				downlaodArtsAdapter.notifyDataSetChanged();
			}
			getDefaultHandler().sendEmptyMessage(PlayDownloadEpisodeFragment.UPFATE_DOWNLOAD_TEXT_FLAG);
			break;
		case R.id.play_download_count:
			// HashSet<PlayVideoInfo> set = (HashSet<PlayVideoInfo>)
			// downlaodArtsAdapter
			// .getAddDownlaodInfos(new HashSet<PlayVideoInfo>());
			if (downlaodArtsAdapter.getAddDownlaodInfos().size() <= 0) {
				return;
			}
			boolean isdownloadFailture = PlayerAPI.addDownlaodLimit(mActivity);
			if (isdownloadFailture) {
				return;
			}
			batchDownloadVideo();
			// 关闭下载页面
			getFragmentManager().popBackStack();
			break;
		}
	}

	private void batchDownloadVideo() {
		// 将选中视频添加到下载队列
		HashSet<PlayVideoInfo> set = (HashSet<PlayVideoInfo>) downlaodArtsAdapter.getAddDownlaodInfos();
		Iterator<PlayVideoInfo> iterator = set.iterator();
		while (iterator.hasNext()) {
			PlayVideoInfo playVideoInfo = (PlayVideoInfo) iterator.next();
			if (playVideoInfo.getVideoId() != null) {
				PlayerAPI.addSDKdownlaod(mActivity, playVideoInfo, downloadSaaSCenter, false);
			}
		}
		if (downlaodArtsAdapter.getAddDownlaodInfos().size() > 0) {
			Toast.makeText(mActivity, getResources().getString(R.string.play_downlaod), 0).show();
		}
	}

	@Override
	public void onPause() {
		downloadSaaSCenter.unregisterDownloadObserver(leDownloadObserver);
		super.onPause();
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
