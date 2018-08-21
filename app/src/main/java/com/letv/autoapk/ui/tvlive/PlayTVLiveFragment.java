package com.letv.autoapk.ui.tvlive;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.Request;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.letv.autoapk.R;
import com.letv.autoapk.base.dialog.BaseDialog;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.open.OpenShareActivity;
import com.letv.autoapk.open.OpenShareDialog;
import com.letv.autoapk.open.OpenShareDialog.OnShareListener;
import com.letv.autoapk.ui.player.PlayDetailFragment;
import com.letv.autoapk.ui.player.PlayerAPI;
import com.letv.autoapk.ui.recommend.RecommendFragment;
import com.letv.autoapk.utils.DateUtils;
import com.letv.autoapk.widgets.LoadingLayout;

public class PlayTVLiveFragment extends BaseTitleFragment implements OnClickListener {
	private LinearLayout detailLayout;
	private TextView detailTitle;
	private ImageView detailShare;
	private TextView detailNocon;
	private PullToRefreshListView listView;

	private PlayTvVideoInfo playTvVideoInfo;
	private PageInfo mPageInfo;
	private String cid;
	private List<PlayTvItemInfo> playTvItemInfos = new ArrayList<PlayTvItemInfo>();
	private PlayTvItemAdapter adapter;
	private long serverTime;
	private long positionTime;
	private int currentPostion = 1;// 当前正在直播的

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		cid = bundle.getString("cid");
	}

	@Override
	public void onResume() {
		super.onResume();
		mActivity.registerReceiver(netStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}

	@Override
	public void onPause() {
		super.onPause();
		mActivity.unregisterReceiver(netStateReceiver);
	}

	@Override
	protected boolean hasTitleBar() {
		return false;
	}

	@Override
	protected boolean loadingData() {
		playTvVideoInfo = new PlayTvVideoInfo();
		mPageInfo = new PageInfo();
		PlayTVLiveDataRequest playTvLiveDataRequest = new PlayTVLiveDataRequest(mActivity);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputParam.put("cid", cid);
		mInputParam.put(StringDataRequest.PAGE, "1");
		int code = playTvLiveDataRequest.setInputParam(mInputParam).setOutputData(playTvVideoInfo, mPageInfo)
				.request(Request.Method.GET);
		if (playTvVideoInfo.getTvDateInfos() != null && playTvVideoInfo.getTvDateInfos().size() > 0) {
			formatTVItemList(playTvVideoInfo.getTvDateInfos());
		}
		// 传送info的图片地址
		if (playTvVideoInfo.getTvImgUrl() != null) {
			Message msg = mActivity.getDefaultHandler().obtainMessage();
			msg.what = PlayTVConst.AUDIOIMG;
			msg.obj = playTvVideoInfo.getTvImgUrl();
			msg.sendToTarget();
		}
		if (code == 0) {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					detailTitle.setText(playTvVideoInfo.getTvTitle());
					detailShare.setTag(playTvVideoInfo);
					detailShare.setOnClickListener(PlayTVLiveFragment.this);
				}
			});
			return true;
		}
		return false;
	}

	private void formatTVItemList(List<PlayTvDateInfo> list) {
		playTvItemInfos.clear();
		for (int i = 0; i < list.size(); i++) {
			PlayTvDateInfo playTvDateInfo = list.get(i);
			Date date = DateUtils.getDateFromGreenwichSec(playTvDateInfo.getDateTime());
			String showDate = DateUtils.formatWeekDate(date);

			PlayTvItemInfo playTvItemInfo = new PlayTvItemInfo();
			playTvItemInfo.setType(PlayTvItemAdapter.TYPE_DATE);
			playTvItemInfo.setDateTime(showDate);
			playTvItemInfos.add(playTvItemInfo);
			for (int j = 0; j < playTvDateInfo.getTvItemInfos().size(); j++) {
				playTvItemInfo = playTvDateInfo.getTvItemInfos().get(j);
				playTvItemInfo.setType(PlayTvItemAdapter.TYPE_PROGREM);
				playTvItemInfo.setDateTime(showDate);
				playTvItemInfos.add(playTvItemInfo);
			}
		}
	}

	@Override
	protected boolean hasContentData() {
		return playTvItemInfos.size() == 0 ? false : true;
	};

	@Override
	protected void onHandleMessage(Message msg) {
		switch (msg.what) {
		case PlayTVConst.UPDATETVITEM:
			break;
		default:
			break;
		}

	}

	private int ergodicTvItem() {
		for (int i = 0; i < playTvItemInfos.size(); i++) {
			if (playTvItemInfos.get(i).getType() == PlayTvItemAdapter.TYPE_DATE) {
				continue;
			}
			if (serverTime >= playTvItemInfos.get(i).beginTime && serverTime <= playTvItemInfos.get(i).endTime) {
				playTvItemInfos.get(i).setPlayState(1);
				return i;
			} else if (serverTime < playTvItemInfos.get(i).beginTime) {
				playTvItemInfos.get(i).setPlayState(0);
			} else if (serverTime > playTvItemInfos.get(i).endTime) {
				playTvItemInfos.get(i).setPlayState(2);
			} else {
				playTvItemInfos.get(i).setPlayState(0);
			}
		}
		return 1;
	}

	@Override
	protected View createContentView() {
		detailLayout = (LinearLayout) initView();
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
				detailNocon.setVisibility(View.VISIBLE);
				return detailLayout;
			}
		};
		loadingLayout.show();
		return loadingLayout;
	}

	@Override
	protected View setupDataView() {
		serverTime = playTvVideoInfo.getServerTime();
		detailNocon.setVisibility(View.GONE);
		if (serverTime > 0) {
			currentPostion = ergodicTvItem();
			if (currentPostion == 1) {// 若数据是第一条的话，显示在最头上
				currentPostion = 2;
			}
		}
		adapter = new PlayTvItemAdapter(mActivity, playTvItemInfos, mActivity);
		listView.setAdapter(adapter);
		listView.setSelection(currentPostion);
		return detailLayout;
	}

	private View initView() {
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		detailLayout = (LinearLayout) inflater.inflate(R.layout.play_detail_tvlive_main, null, false);
		detailTitle = (TextView) detailLayout.findViewById(R.id.play_detail_tvlive_title);
		detailNocon = (TextView) detailLayout.findViewById(R.id.play_detail_tv_nocon);
		detailShare = (ImageView) detailLayout.findViewById(R.id.play_detail_tvlive_share);
		listView = (PullToRefreshListView) detailLayout.findViewById(R.id.play_detail_listview);
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				new UiAsyncTask<Boolean>(PlayTVLiveFragment.this) {

					@Override
					protected Boolean doBackground() {
						return loadingData();
					}

					@Override
					protected void post(Boolean result) {
						super.post(result);
						if (result && playTvItemInfos.size() > 0) {
							detailNocon.setVisibility(View.GONE);
							notifyDataView();
						} else if (result && !hasContentData()) {
							detailNocon.setVisibility(View.VISIBLE);
							loadingLayout.onSuccess(true);
						}
						listView.onRefreshComplete();
					}
				}.execute();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				new UiAsyncTask<Boolean>(PlayTVLiveFragment.this) {

					@Override
					protected Boolean doBackground() {
						if (loadingTvItemMoreDate() == 0) {
							return true;
						}
						return false;
					}

					@Override
					protected void post(Boolean result) {
						super.post(result);
						listView.onRefreshComplete();
						if (result && null != adapter) {
							serverTime = playTvVideoInfo.getServerTime();
							currentPostion = ergodicTvItem();
							adapter.notifyDataSetChanged();
						} else {
							if (mPageInfo.getPageIndex() + 1 > mPageInfo.getTotalPage()) {
								listView.setMode(Mode.PULL_FROM_START);
							}
						}
					}
				}.execute();
			}
		});
		return detailLayout;
	}

	private void notifyDataView() {
		serverTime = playTvVideoInfo.getServerTime();
		if (serverTime > 0) {
			currentPostion = ergodicTvItem();
			if (currentPostion == 1) {// 若数据是第一条的话，显示在最头上
				currentPostion = 2;
			}
		}
		if (adapter == null) {
			adapter = new PlayTvItemAdapter(mActivity, playTvItemInfos, mActivity);
			listView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
		listView.setSelection(currentPostion);
	}

	private int loadingTvItemMoreDate() {
		if (mPageInfo.getPageIndex() + 1 <= mPageInfo.getTotalPage()) {
			PlayTVLiveDataRequest playTvLiveDataRequest = new PlayTVLiveDataRequest(mActivity);
			Map<String, String> mInputParam = new HashMap<String, String>();
			mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
			mInputParam.put("cid", cid);
			mInputParam.put(StringDataRequest.PAGE, String.valueOf(mPageInfo.getPageIndex() + 1));
			int code = playTvLiveDataRequest.setInputParam(mInputParam).setOutputData(playTvVideoInfo, mPageInfo)
					.request(Request.Method.GET);
			if (playTvVideoInfo.getTvDateInfos() != null && playTvVideoInfo.getTvDateInfos().size() > 0) {
				formatTVItemList(playTvVideoInfo.getTvDateInfos());
			}
			return code;
		} else {
			return -1;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play_detail_tvlive_share:
			// 分享
			boolean isNoNetwork = PlayerAPI.addNoNetworkLimit(mActivity);
			if (isNoNetwork) {
				return;
			}
			if (mActivity instanceof OpenShareActivity) {
				final PlayTvVideoInfo info = (PlayTvVideoInfo) v.getTag();
				if (info == null)
					return;
				if (!MyApplication.getInstance().getOpenSdk().hasOpenId()) {
					mActivity.showToastSafe(getResString(R.string.pleasewait), Toast.LENGTH_SHORT);
					return;
				}
				final OpenShareActivity shareActivity = (OpenShareActivity) mActivity;
				OpenShareDialog dialog = new OpenShareDialog();
				dialog.setOnShareListener(new OnShareListener() {

					@Override
					public void doShare(int openType) {
						shareActivity.doshare(openType, info.getTvShareUrl(), info.getTvTitle(), info.getTvDesc(),
								info.getTvImgUrl());
					}
				});
				BaseDialog.show(shareActivity.getSupportFragmentManager(), dialog);
			}
			break;

		default:
			break;
		}

	}

	private int getLiveItem() {
		return 0;
	}

	public void setServiceTime(long time) {
		serverTime = time;
		if (serverTime > 0 && currentPostion < playTvItemInfos.size()
				&& serverTime > playTvItemInfos.get(currentPostion).getEndTime()) {
			if (currentPostion + 1 < playTvItemInfos.size() && adapter != null) {
				playTvItemInfos.get(currentPostion).setPlayState(2);
				currentPostion = currentPostion + 1;
				playTvItemInfos.get(currentPostion).setPlayState(1);
				adapter.notifyDataSetChanged();
			} else if (currentPostion == playTvItemInfos.size() - 1 && adapter != null) {
				playTvItemInfos.get(currentPostion).setPlayState(2);
				adapter.notifyDataSetChanged();
			}
		}
	}

	public void setPositionTime(long time) {
		if (adapter != null) {
			adapter.setPositionTime(time);
		}
	}

	private BroadcastReceiver netStateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (!ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()))
				return;
			boolean nonetwork = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			// 断网后若有网络则重新播放
			if (info != null && adapter == null) {
				loadingLayout.show();
			}
		}
	};

}
