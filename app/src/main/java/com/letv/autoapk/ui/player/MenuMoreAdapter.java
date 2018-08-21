package com.letv.autoapk.ui.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xutils.x;
import org.xutils.common.task.AbsTask;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.android.volley.Request;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.base.fragment.BaseFragment;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.net.StringDataRequest.DataRequestCallback;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.CollectionRecordDao;
import com.letv.autoapk.dao.CollectionRecordInfo;
import com.letv.autoapk.download.DownloadSaasCenter;
import com.letv.autoapk.open.OpenSdk;
import com.letv.autoapk.open.OpenShareActivity;

class MenuMoreAdapter extends BaseAdapter implements OnClickListener {

	private BaseActivity context;
	private Fragment fragment;
	private PlayVideoInfo info;
	private OpenSdk openSdk;
	private DownloadSaasCenter downloadSaaSCenter;
	private boolean candownload;
	private ImageView collectImg;
	protected CollectionRecordDao dao;
	private CollectionInfo collectionInfo;
	private final int MIN_CLICK_DELAY_TIME = 1500;
	private long lastCollecionClickTime = 0;

	MenuMoreAdapter(Fragment frag, BaseActivity context, PlayVideoInfo info, boolean candownload) {
		this.fragment = frag;
		this.context = context;
		this.info = info;
		openSdk = MyApplication.getInstance().getOpenSdk();
		downloadSaaSCenter = DownloadSaasCenter.getInstances(context.getApplicationContext());
		dao = MyApplication.getInstance().getDaoByKey(CollectionRecordDao.class.getName());
		this.candownload = candownload;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	protected View getMyView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.play_menumore, null);
			if (candownload) {
				info.setDownloadPlatform("104002");
			} else {
				ImageView download = (ImageView) convertView.findViewById(R.id.menu_download);
				download.setImageResource(R.drawable.play_download_gray);
			}
			convertView.findViewById(R.id.menu_download).setOnClickListener(this);
			collectImg = (ImageView) convertView.findViewById(R.id.menu_collect);
			collectImg.setOnClickListener(this);
			getCollectionRequest(info.getVideoId(), info.getAlbumId());
			GridLayout gridLayout = (GridLayout) convertView.findViewById(R.id.rl_share_imgs);
			if (openSdk.hasMM()) {
				gridLayout.findViewById(R.id.menu_mm).setOnClickListener(this);
				gridLayout.findViewById(R.id.menu_mm).setVisibility(View.VISIBLE);
				gridLayout.findViewById(R.id.menu_mmtimeline).setOnClickListener(this);
				gridLayout.findViewById(R.id.menu_mmtimeline).setVisibility(View.VISIBLE);
			} else {
				gridLayout.removeView(gridLayout.findViewById(R.id.menu_mm));
				gridLayout.removeView(gridLayout.findViewById(R.id.menu_mmtimeline));
			}
			if (openSdk.hasBLOG()) {
				gridLayout.findViewById(R.id.menu_blog).setOnClickListener(this);
				gridLayout.findViewById(R.id.menu_blog).setVisibility(View.VISIBLE);
			} else {
				gridLayout.removeView(gridLayout.findViewById(R.id.menu_blog));
			}
			if (openSdk.hasQQ()) {
				gridLayout.findViewById(R.id.menu_qq).setOnClickListener(this);
				gridLayout.findViewById(R.id.menu_qq).setVisibility(View.VISIBLE);
				gridLayout.findViewById(R.id.menu_qzone).setOnClickListener(this);
				gridLayout.findViewById(R.id.menu_qzone).setVisibility(View.VISIBLE);
			} else {
				gridLayout.removeView(gridLayout.findViewById(R.id.menu_qq));
				gridLayout.removeView(gridLayout.findViewById(R.id.menu_qzone));
			}

		}
		return convertView;
	}

	@Override
	public void onClick(View v) {
		OpenShareActivity openShareActivity = null;
		if (context instanceof OpenShareActivity && info != null) {
			openShareActivity = (OpenShareActivity) context;
			int id = v.getId();
			switch (id) {
			case R.id.menu_mm:
				openShareActivity.doshare(OpenSdk.TYPE_MM, info.getShareUrl(), info.getVideoTitle(),
						info.getVideoDesc(), info.getImageUrl());
				break;
			case R.id.menu_blog:
				openShareActivity.doshare(OpenSdk.TYPE_BLOG, info.getShareUrl(), info.getVideoTitle(),
						info.getVideoDesc(), info.getImageUrl());
				break;
			case R.id.menu_qq:
				openShareActivity.doshare(OpenSdk.TYPE_QQ, info.getShareUrl(), info.getVideoTitle(),
						info.getVideoDesc(), info.getImageUrl());
				break;
			case R.id.menu_qzone:
				openShareActivity.doshare(OpenSdk.TYPE_ZONE, info.getShareUrl(), info.getVideoTitle(),
						info.getVideoDesc(), info.getImageUrl());
				break;
			case R.id.menu_mmtimeline:
				openShareActivity.doshare(OpenSdk.TYPE_MM_TIMELINE, info.getShareUrl(), info.getVideoTitle(),
						info.getVideoDesc(), info.getImageUrl());
				break;
			case R.id.menu_collect:
				final CollectionRecordInfo collectionRecordInfo = new CollectionRecordInfo();
				collectionRecordInfo.setVideoId(info.getVideoId());
				collectionRecordInfo.setAlbumId(info.getAlbumId());
				collectionRecordInfo.setVideoImage(info.getImageUrl());
				collectionRecordInfo.setVideoTitle(info.getVideoTitle());
				collectionRecordInfo.setPlayTimes(info.getPlayTimes());
				if (!MyApplication.getInstance().isLogin()) {// 登录
					CollectionRecordInfo collectiionInfo;
					if (info.getAlbumId() != null && !info.getAlbumId().isEmpty() && !"0".equals(info.getAlbumId())) {
						// 按专辑
						collectiionInfo = dao.findBySpecialColum("albumId", info.getAlbumId());
					} else {
						collectiionInfo = dao.findBySpecialColum("videoId", info.getVideoId());
					}
					if (collectiionInfo != null) {
						CollectionRecordInfo daoInfo = dao.delete(collectiionInfo);
						if (daoInfo == null) {
							context.showToastSafe(context.getString(R.string.delete_failed), 1);
							collectImg.setImageResource(R.drawable.play_more_collection_p);
						} else {
							context.showToastSafe(context.getString(R.string.delete_ok), 1);
							collectImg.setImageResource(R.drawable.play_more_collection);
						}
					} else {
						boolean collectSuccess = false;
						if (info.getAlbumId() != null && !info.getAlbumId().isEmpty()
								&& !"0".equals(info.getAlbumId())) {
							// 按专辑
							collectionRecordInfo.setVideoImage(info.getAlbumPicUrl());
							collectionRecordInfo.setVideoTitle(info.getmAlbumName());
							collectSuccess = dao.save(collectionRecordInfo);
						} else {
							collectSuccess = dao.save(collectionRecordInfo);
						}
						if (collectSuccess) {
							context.showToastSafe(context.getString(R.string.collect_ok), 1);
							collectImg.setImageResource(R.drawable.play_more_collection_p);
						} else {
							context.showToastSafe(context.getString(R.string.collect_failed), 1);
							collectImg.setImageResource(R.drawable.play_more_collection);
						}
					}
					collectionStateChange();
					return;
				}
				boolean isNoNetwork = PlayerAPI.addNoNetworkLimit(context);
				if (isNoNetwork) {
					return;
				}
				final View collect = v;
				final List<CollectionRecordInfo> infos = new ArrayList<CollectionRecordInfo>();
				infos.add(collectionRecordInfo);
				if (collectionInfo != null) {
					final OpCollectRecordsRequest request = new OpCollectRecordsRequest(context);
					if (collectionInfo.hasCollect == 0) {
						AbsTask<Integer> task = new AbsTask<Integer>() {
							@Override
							protected Integer doBackground() throws Throwable {
								return request.collectRecords(context, infos);
							}

							@Override
							protected void onSuccess(Integer result) {
								// TODO Auto-generated method stub
								if (result != null && result == 0) {
									context.showToastSafe(context.getString(R.string.collect_ok), 1);
									// getCollectionRequest(describeInfo.getVideoId(),
									// describeInfo.getAblumId());
									collectionInfo.hasCollect = 1;
									collectImg.setImageResource(R.drawable.play_more_collection_p);
									collectionStateChange();
								} else {
									collectionInfo.hasCollect = 0;
									collectImg.setImageResource(R.drawable.play_more_collection);
									context.showToastSafe(context.getString(R.string.collect_failed), 1);
								}
							}

							@Override
							protected void onError(Throwable ex, boolean isCallbackError) {
								// TODO Auto-generated method stub
								collectImg.setImageResource(R.drawable.play_more_collection);
								context.showToastSafe(context.getString(R.string.collect_failed), 1);
							}
						};

						x.task().start(task);
					} else {
						AbsTask<Integer> task = new AbsTask<Integer>() {

							@Override
							protected Integer doBackground() throws Throwable {
								return request.unCollectRecords(context, infos);
							}

							@Override
							protected void onSuccess(Integer result) {
								// TODO Auto-generated method stub
								if (request != null && result == 0) {
									context.showToastSafe(context.getString(R.string.delete_ok), 1);
									collectImg.setImageResource(R.drawable.play_more_collection);
									collectionInfo.hasCollect = 0;
									// getCollectionRequest(describeInfo.getVideoId(),
									// describeInfo.getAblumId());
									collectionStateChange();
								} else {
									collectionInfo.hasCollect = 1;
									collectImg.setImageResource(R.drawable.play_more_collection_p);
									context.showToastSafe(context.getString(R.string.delete_failed), 1);
								}
							}

							@Override
							protected void onError(Throwable ex, boolean isCallbackError) {
								// TODO Auto-generated method stub
								collectImg.setImageResource(R.drawable.play_more_collection_p);
								context.showToastSafe(context.getString(R.string.delete_failed), 1);
							}

						};
						x.task().start(task);
					}
				}
				break;
			case R.id.menu_download:
				long clickTime = System.currentTimeMillis();
				if (clickTime - lastCollecionClickTime > MIN_CLICK_DELAY_TIME) {
					lastCollecionClickTime = clickTime;
					PlayerAPI.addSignleDownloadInfo(fragment,context, info, downloadSaaSCenter, true);
				}
				// v.setEnabled(false);
				break;
			default:
				break;
			}
		}

	}

	public void collectionStateChange() {
		FragmentManager fragmentManager = context.getSupportFragmentManager();
		Fragment vodFragment = fragmentManager.findFragmentByTag(PlayConst.VODFRAGMENTTAG);
		if (vodFragment != null && vodFragment instanceof BaseFragment) {
			Handler vodHandler = ((BaseFragment) vodFragment).getDefaultHandler();
			Message msg = vodHandler.obtainMessage();
			msg.what = PlayConst.VIDEO_COllECTION_CHANGE;
			msg.sendToTarget();
		}
	}

	/**
	 * 获取收藏状态接口
	 */
	private void getCollectionRequest(String videoId, String albumId) {
		if (!MyApplication.getInstance().isLogin()) {
			CollectionRecordInfo info;
			if (albumId != null && !albumId.isEmpty() && !"0".equals(albumId)) {
				// 专辑显示
				info = dao.findBySpecialColum("albumId", albumId);
			} else {
				info = dao.findBySpecialColum("videoId", videoId);
			}
			if (info != null) {
				collectImg.setImageResource(R.drawable.play_more_collection_p);
			} else {
				collectImg.setImageResource(R.drawable.play_more_collection);
			}
			return;
		}
		CollectionStatusDataRequest request = new CollectionStatusDataRequest(context);
		final ArrayList<CollectionInfo> data = new ArrayList<CollectionInfo>();
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put("videoId", videoId);
		mInputParam.put("albumId", albumId);
		mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(context));
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		request.setInputParam(mInputParam).setOutputData(data).requestTask(Request.Method.GET,
				new DataRequestCallback() {
					@Override
					public void onDataRequestSuccess(Object[] mOutputData) {
						context.runOnUiThread(new Runnable() {

							public void run() {
								if (data.size() > 0) {
									collectionInfo = data.get(0);
									if (collectionInfo != null) {
										if (1 == collectionInfo.hasCollect) {
											collectImg.setImageResource(R.drawable.play_more_collection_p);
										} else {
											collectImg.setImageResource(R.drawable.play_more_collection);
										}
									}
								}
							}
						});
					}

					@Override
					public void onDataRequestFailed(int errorCode, String msg) {

					}
				});
	}

}
