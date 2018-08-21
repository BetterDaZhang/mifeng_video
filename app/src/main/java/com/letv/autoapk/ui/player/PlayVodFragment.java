package com.letv.autoapk.ui.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.net.StringDataRequest.DataRequestCallback;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.boss.AuthDataRequest;
import com.letv.autoapk.boss.AuthInfo;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.CollectionRecordInfo;
import com.letv.autoapk.utils.StringEscapeUtils;
import com.support.v7.recyclerview.BaseLayoutManager;
import com.support.v7.recyclerview.FFocusCustomRecyclerView;
import com.support.v7.recyclerview.GridLayoutManager;

public class PlayVodFragment extends PlayDetailFragment {
	private ImageView episodeImg;
	// 收藏
	final int MIN_CLICK_DELAY_TIME = 1000;
	long lastCollecionClickTime = 0;
	private CollectionInfo collectionInfo;

	/**
	 * 点播详情顶部栏
	 */
	void initTopView() {
		super.initTopView();
		headBarView = (LinearLayout) LayoutInflater.from(mActivity).inflate(R.layout.play_detail_vod_headview, null);
		prise = (TextView) headBarView.findViewById(R.id.play_detail_priseTv);
		criticize = (TextView) headBarView.findViewById(R.id.play_detail_criticizeTv);
		commentCount = (TextView) headBarView.findViewById(R.id.play_detail_comment_count);
		shareImg = (ImageView) headBarView.findViewById(R.id.play_detail_share);
		downloadImg = (ImageView) headBarView.findViewById(R.id.play_detail_download);
		collectImg = (ImageView) headBarView.findViewById(R.id.play_detail_collect);
		prise.setOnClickListener(this);
		criticize.setOnClickListener(this);
		shareImg.setOnClickListener(this);
		shareImg.setTag(describeInfo);
		downloadImg.setOnClickListener(this);
		collectImg.setOnClickListener(this);
		// mPullRefreshListView.getRefreshableView().addHeaderView(headBarView);

	}

	void updateTopView() {
		if (describeInfo != null) {
			headView.removeView(headBarView);
			headView.addView(headBarView);
			// 获取收藏状态
			getCollectState();
			// 获取点赞状态
			getPriseState("1");
		} else {
			headView.removeView(headBarView);
		}

	}

	/**
	 * 初始化点播介绍
	 */
	void initDescriptView() {
		super.initDescriptView();
		// 一期暂时去掉观看人数
		descriptPlaycount.setVisibility(View.GONE);
		desrcibeRl.setOnClickListener(this);
	}

	void updateDescriptView() {
		super.updateDescriptView();
		try {
			descriptMore.setBackgroundResource(R.drawable.play_tv_unspread);
			describeAll.setVisibility(View.GONE);
			descriptTitle.setText(StringEscapeUtils.unescapeHtml4(describeInfo.getVideoTitle()));
			// if (!TextUtils.isEmpty(describeInfo.getPlaytTimes()+"")) {
			// descriptPlaycount.setText(getResString(R.string.play_detail_playcount)
			// +describeInfo.getPlaytTimes() + +
			// getResString(R.string.play_detail_ci));
			// descriptPlaycount.setVisibility(View.VISIBLE);
			// }else{
			// descriptPlaycount.setVisibility(View.GONE);
			// }
			// 设置弹幕
			// descriptBarrageCount.setText(text);
			if (!TextUtils.isEmpty(describeInfo.getSubCategory())) {
				descriptType.setText(getResString(R.string.play_category, describeInfo.getSubCategory()));
				descriptType.setVisibility(View.VISIBLE);
			} else {
				descriptType.setVisibility(View.GONE);
			}
			if (!TextUtils.isEmpty(describeInfo.getSubCategory())) {
				descriptBrief.setText(getResString(R.string.play_abstract, describeInfo.getVideoBrief()));
				descriptBrief.setVisibility(View.VISIBLE);
			} else {
				descriptBrief.setVisibility(View.GONE);
			}
			if (!TextUtils.isEmpty(describeInfo.getArea())) {
				descriptArea.setText(getResString(R.string.play_area, describeInfo.getArea()));
				descriptArea.setVisibility(View.VISIBLE);
			} else {
				descriptArea.setVisibility(View.GONE);
			}
			if (!TextUtils.isEmpty(describeInfo.getPublishTime())) {
				descriptShow.setText(getResString(R.string.play_showtime) + describeInfo.getPublishTime());
				descriptShow.setVisibility(View.VISIBLE);
			} else {
				descriptShow.setVisibility(View.GONE);
			}
			mPullRefreshListView.getRefreshableView().removeHeaderView(describeHeaderView);
			mPullRefreshListView.getRefreshableView().addHeaderView(describeHeaderView);
		} catch (NullPointerException e) {
			mPullRefreshListView.getRefreshableView().removeHeaderView(describeHeaderView);
			e.printStackTrace();
		}
	}

	/**
	 * 初始化剧集
	 */
	@Override
	void initEpisodeView() {
		if (playDetailInfo.getDisplayType() == 1) {
			if (episodeHeaderView == null) {
				episodeHeaderView = mActivity.getLayoutInflater().inflate(R.layout.play_detail_episode, null);
			}
			episodeTitle = (TextView) episodeHeaderView.findViewById(R.id.play_detail_episode_tiltle);
			episodeUpdateTitle = (TextView) episodeHeaderView.findViewById(R.id.play_detail_episode_brief);
			episodeRecyclerView = (FFocusCustomRecyclerView) episodeHeaderView
					.findViewById(R.id.play_episode_RecyclerView);
			episodeRecyclerView.setHasFixedSize(true);
			episodeRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, BaseLayoutManager.HORIZONTAL));
			episodeRecyclerView.setItemAnimator(new DefaultItemAnimator());
			episodeRecyclerAdapter = new EpisodeRecyclerAdapter(mActivity, episodeInfos,
					R.layout.play_detail_episode_item, getDefaultHandler(), videoId);
			episodeUpdateTitle.setOnClickListener(this);
		} else if (playDetailInfo.getDisplayType() == 2) {
			if (episodeHeaderView == null) {
				episodeHeaderView = mActivity.getLayoutInflater().inflate(R.layout.play_detail_arts, null);
			}
			episodeTitle = (TextView) episodeHeaderView.findViewById(R.id.play_detail_arts_tiltle);
			episodeImg = (ImageView) episodeHeaderView.findViewById(R.id.play_detail_arts_next);
			episodeRecyclerView = (FFocusCustomRecyclerView) episodeHeaderView
					.findViewById(R.id.play_detail_arts_recyclerview);
			episodeRecyclerView.setHasFixedSize(true);
			episodeRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, BaseLayoutManager.HORIZONTAL));
			episodeRecyclerView.setItemAnimator(new DefaultItemAnimator());
			artsRecyclerAdapter = new ArtsRecyclerAdapter(mActivity, episodeInfos, R.layout.play_detail_arts_item,
					getDefaultHandler(), videoId);
			episodeImg.setOnClickListener(this);
		} else if (playDetailInfo.getDisplayType() == 0) {
			super.initEpisodeView();
		}
	}

	@Override
	void updateFirstEpisodeView() {
		if (playDetailInfo.getDisplayType() == 1) {
			if (episodeInfos.size() == 0) {
				return;
			}
			episodeTitle.setText(getResString(R.string.play_detail_episode_title));
			episodeUpdateTitle.setText(getResString(R.string.play_episodeprogress, playDetailInfo.getTotalEpisodes(),
					playDetailInfo.getUpdateEpisode()));
			episodeRecyclerView.setAdapter(episodeRecyclerAdapter);
			episodeRecyclerView.postDelayed(new Runnable() {
				@Override
				public void run() {
					int position = PlayerAPI.fixedPostion(episodeInfos, videoId);
					episodeRecyclerView.scrollToPosition(position);
				}
			}, 50);
			mPullRefreshListView.getRefreshableView().addHeaderView(episodeHeaderView);
		} else if (playDetailInfo.getDisplayType() == 2) {
			if (episodeInfos.size() == 0) {
				return;
			}
			episodeTitle.setText(getResString(R.string.play_detail_arts_title));
			episodeRecyclerView.setAdapter(artsRecyclerAdapter);
			episodeRecyclerView.postDelayed(new Runnable() {
				@Override
				public void run() {
					int position = PlayerAPI.fixedPostion(episodeInfos, videoId);
					episodeRecyclerView.scrollToPosition(position);
				}
			}, 50);
			mPullRefreshListView.getRefreshableView().addHeaderView(episodeHeaderView);
		} else if (playDetailInfo.getDisplayType() == 0) {
			super.initEpisodeView();
		}
	}

	@Override
	void updateRefreshEpisodeView() {
		if (playDetailInfo.getDisplayType() == 1) {
			if (episodeInfos.size() == 0) {
				mPullRefreshListView.getRefreshableView().removeHeaderView(episodeHeaderView);
				return;
			}
			mPullRefreshListView.getRefreshableView().removeHeaderView(episodeHeaderView);
			mPullRefreshListView.getRefreshableView().addHeaderView(episodeHeaderView);
			episodeTitle.setText(getResString(R.string.play_detail_episode_title));
			episodeUpdateTitle.setText(getResString(R.string.play_episodeprogress, playDetailInfo.getTotalEpisodes(),
					playDetailInfo.getUpdateEpisode()));
			episodeRecyclerAdapter.setVideoId(videoId);
			episodeRecyclerAdapter.setClickVideoId(videoId);
			episodeRecyclerAdapter.notifyDataSetChanged();
			episodeRecyclerView.postDelayed(new Runnable() {
				@Override
				public void run() {
					int position = PlayerAPI.fixedPostion(episodeInfos, videoId);
					episodeRecyclerView.scrollToPosition(position);
				}
			}, 50);
		} else if (playDetailInfo.getDisplayType() == 2) {
			if (episodeInfos.size() == 0) {
				mPullRefreshListView.getRefreshableView().removeHeaderView(episodeHeaderView);
				return;
			}
			mPullRefreshListView.getRefreshableView().removeHeaderView(episodeHeaderView);
			mPullRefreshListView.getRefreshableView().addHeaderView(episodeHeaderView);
			episodeTitle.setText(getResString(R.string.play_detail_arts_title));
			artsRecyclerAdapter.setVideoId(videoId);
			artsRecyclerAdapter.setClickVideoId(videoId);
			artsRecyclerAdapter.notifyDataSetChanged();
			episodeRecyclerView.postDelayed(new Runnable() {
				@Override
				public void run() {
					int position = PlayerAPI.fixedPostion(episodeInfos, videoId);
					episodeRecyclerView.scrollToPosition(position);
				}
			}, 50);
		} else if (playDetailInfo.getDisplayType() == 0) {
			super.initEpisodeView();
			mPullRefreshListView.getRefreshableView().removeHeaderView(episodeHeaderView);
		}
	}

	@Override
	public void onClick(View v) {
		final FragmentTransaction transaction = getFragmentManager().beginTransaction();
		switch (v.getId()) {
		case R.id.play_detail_priseTv:
			sendPlayPriseRequest("1");
			break;
		case R.id.play_detail_criticizeTv:
			sendPlayCaiRequest();
			break;
		case R.id.play_detail_download:
			if (episodeInfos.size() > 0) {
				isShowAlbumPage(transaction);
			} else {
				PlayerAPI.addSignleDownloadInfo(this, mActivity, playDetailInfo.getDescribeInfo(), downloadSaaSCenter,
						true);
			}
			break;
		case R.id.play_detail_collect:
			// 收藏
			long clickTime = System.currentTimeMillis();
			if (clickTime - lastCollecionClickTime > MIN_CLICK_DELAY_TIME) {
				lastCollecionClickTime = clickTime;
				collectiionClick();
			} else {
				mActivity.showToastSafe(getResString(R.string.play_clicktoomuch), 0);
			}
			break;
		case R.id.play_detail_describeRl:
			if (isSpread) {
				isSpread = false;
				describeAll.setVisibility(View.GONE);
				descriptMore.setBackgroundResource(R.drawable.play_tv_unspread);
			} else {
				isSpread = true;
				describeAll.setVisibility(View.VISIBLE);
				descriptMore.setBackgroundResource(R.drawable.play_tv_spread);
			}

			break;
		case R.id.play_detail_episode_brief:
			// 剧集
			Fragment episodeFragment = Fragment.instantiate(mActivity, PlayEpisodeFragment.class.getName());
			((PlayEpisodeFragment) episodeFragment).setHandler(getDefaultHandler());
			Bundle bundle = new Bundle();
			bundle.putSerializable("episodes", (Serializable) episodeInfos);
			bundle.putString("episodeTotalCount", playDetailInfo.getTotalEpisodes());
			bundle.putString("episodeUpdate", playDetailInfo.getUpdateEpisode());
			bundle.putString("videoId", videoId);
			int position = PlayerAPI.fixedPostion(episodeInfos, videoId);
			int episodePosition = position / PlayEpisodeFragment.MOD_COUNT;
			bundle.putInt("subPagePosition", episodePosition);
			episodeFragment.setArguments(bundle);
			transaction.hide(PlayVodFragment.this);
			transaction.add(R.id.detailbody, episodeFragment, "describeFragment");
			transaction.addToBackStack(null);
			transaction.commit();
			break;
		case R.id.play_detail_arts_next:
			// 剧集
			Fragment artsFragment = Fragment.instantiate(mActivity, PlayArtsFragment.class.getName());
			((PlayArtsFragment) artsFragment).setHandler(getDefaultHandler());
			Bundle artsbundle = new Bundle();
			artsbundle.putSerializable("episodes", (Serializable) episodeInfos);
			artsbundle.putString("videoId", videoId);
			artsFragment.setArguments(artsbundle);
			transaction.hide(PlayVodFragment.this);
			transaction.add(R.id.detailbody, artsFragment, "describeFragment");
			transaction.addToBackStack(null);
			transaction.commit();
			break;
		default:
			super.onClick(v);
			break;
		}
	}

	private void showPlayDownloadFragment(final FragmentTransaction transaction) {
		if (playDetailInfo.getDisplayType() == 1) {
			Fragment downEpisodeFragment = Fragment.instantiate(mActivity, PlayDownloadEpisodeFragment.class.getName());
			Bundle downBundle = new Bundle();
			downBundle.putSerializable("episodes", (Serializable) episodeInfos);
			downBundle.putString("videoId", videoId);
			downBundle.putString("albumId", albumId);
			downEpisodeFragment.setArguments(downBundle);
			transaction.hide(PlayVodFragment.this);
			transaction.add(R.id.detailbody, downEpisodeFragment, "downEpisodeFragment");
			transaction.addToBackStack(null);
			transaction.commit();
		} else if (playDetailInfo.getDisplayType() == 2) {
			Fragment downArtsFragment = Fragment.instantiate(mActivity, PlayDownloadArtsFragment.class.getName());
			Bundle downBundle = new Bundle();
			downBundle.putSerializable("episodes", (Serializable) episodeInfos);
			downBundle.putString("videoId", videoId);
			downBundle.putString("albumId", albumId);
			downArtsFragment.setArguments(downBundle);
			transaction.hide(PlayVodFragment.this);
			transaction.add(R.id.detailbody, downArtsFragment, "downEpisodeFragment");
			transaction.addToBackStack(null);
			transaction.commit();
		} else {
			PlayerAPI.addSignleDownloadInfo(this, mActivity, playDetailInfo.getDescribeInfo(), downloadSaaSCenter,
					true);
		}
	}

	void isShowAlbumPage(final FragmentTransaction transaction) {
		// 无网络和禁止非wifi下缓存
		if (PlayerAPI.addDownlaodLimit(mActivity)) {
			return;
		}
		if (MyApplication.getInstance().isNeedBoss() == 0) {
			showPlayDownloadFragment(transaction);
		} else {
			// 视频免费/付费鉴权
			new UiAsyncTask<Integer>(this) {

				@Override
				protected Integer doBackground() throws Throwable {
					AuthInfo authInfo = new AuthInfo();
					AuthDataRequest request = new AuthDataRequest(mActivity);
					Map<String, String> mInputParam = new HashMap<String, String>();
					mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
					mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
					mInputParam.put("platform", "104002");
					mInputParam.put("albumId", describeInfo != null ? describeInfo.getAlbumId() : "");
					mInputParam.put("storepath", "anyanyanything");
					return request.setInputParam(mInputParam).setOutputData(authInfo).request(Request.Method.GET);
				}

				protected void post(Integer result) {
					if (result == 0) {
						showPlayDownloadFragment(transaction);
					} else {
						((BaseActivity) mActivity).showToastSafe(R.string.play_downlaodauthfailed, Toast.LENGTH_SHORT);
					}
				};
			}.showDialog().execute();
		}
	}

	private void sendPlayCaiRequest() {
		boolean isNoNetwork = PlayerAPI.addNoNetworkLimit(mActivity);
		if (isNoNetwork) {
			return;
		}
		if (playPriseInfo == null) {
			return;
		}
		if (playPriseInfo.isHasSupport()) {
			mActivity.showToastSafe(R.string.play_cai_toast, Toast.LENGTH_SHORT);
			return;
		}
		if (playPriseInfo.isHasCai()) {
			mActivity.showToastSafe(R.string.play_cai_repeat_toast, Toast.LENGTH_SHORT);
			return;
		}
		new UiAsyncTask<Boolean>(PlayVodFragment.this) {

			@Override
			protected Boolean doBackground() {
				String type = "1";
				String target = videoId;
				// if (albumId != null && "0".equals(albumId)) {
				// // 专辑点赞
				// type = "2";
				// target = albumId;
				// }
				PraiseUtils praiseUtils = new PraiseUtils();
				boolean doSupport = praiseUtils.sendPraiseRequest(type, "0", target, LoginInfoUtil.getUserId(mActivity),
						MyApplication.getInstance().getTenantId(), mActivity);

				return doSupport;
			}

			protected void post(Boolean result) {

			};
		}.execute();
		try {
			if (!playPriseInfo.isHasCai()) {
				Drawable caiDrawable = mActivity.getResources().getDrawable(R.drawable.cai);
				caiDrawable.setBounds(0, 0, caiDrawable.getMinimumWidth(), caiDrawable.getMinimumHeight());
				criticize.setText(PlayerAPI.formatCount(Integer.parseInt(playPriseInfo.getSupportCaiNumber()) + 1));
				criticize.setCompoundDrawables(caiDrawable, null, null, null);
				playPriseInfo.setHasCai(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void getCollectState() {
		getCollectionRequest(videoId, albumId);
	}

	void collectiionClick() {
		final List<CollectionRecordInfo> collectionRecordInfos = new ArrayList<CollectionRecordInfo>();
		final CollectionRecordInfo collectionRecordInfo = new CollectionRecordInfo();
		collectionRecordInfo.setVideoId(videoId);
		collectionRecordInfo.setAlbumId(albumId);
		collectionRecordInfo.setVideoImage(describeInfo.getImageUrl());
		collectionRecordInfo.setVideoTitle(describeInfo.getVideoTitle());
		collectionRecordInfo.setPlayTimes(describeInfo.getPlayTimes());
		describeInfo.setAlbumId(albumId);
		collectionRecordInfos.add(collectionRecordInfo);
		if (!MyApplication.getInstance().isLogin()) {// 未登录
			CollectionRecordInfo info;
			if (albumId != null && !albumId.isEmpty() && !"0".equals(albumId)) {
				// 按专辑
				info = dao.findBySpecialColum("albumId", albumId);
			} else {
				info = dao.findBySpecialColum("videoId", videoId);
			}
			if (info != null) {
				CollectionRecordInfo daoInfo = dao.delete(info);
				if (daoInfo == null) {
					mActivity.showToastSafe(getResString(R.string.delete_failed), 1);
					collectImg.setImageResource(R.drawable.play_collected);
				} else {
					mActivity.showToastSafe(getResString(R.string.delete_ok), 1);
					collectImg.setImageResource(R.drawable.play_collect);
				}
			} else {
				boolean collectSuccess = false;
				if (!TextUtils.isEmpty(albumId) && !"0".equals(albumId)) {
					// 按专辑
					collectionRecordInfo.setVideoImage(describeInfo.getAlbumPicUrl());
					collectionRecordInfo.setVideoTitle(describeInfo.getmAlbumName());
					collectSuccess = dao.save(collectionRecordInfo);
				} else {
					collectSuccess = dao.save(collectionRecordInfo);
				}
				if (collectSuccess) {
					mActivity.showToastSafe(getResString(R.string.collect_ok), 1);
					collectImg.setImageResource(R.drawable.play_collected);
				} else {
					mActivity.showToastSafe(getResString(R.string.collect_failed), 1);
					collectImg.setImageResource(R.drawable.play_collect);
				}
			}
			return;
		}
		boolean isNoNetwork = PlayerAPI.addNoNetworkLimit(mActivity);
		if (isNoNetwork) {
			return;
		}
		if (collectionInfo != null) {
			final OpCollectRecordsRequest request = new OpCollectRecordsRequest(mActivity);
			if (collectionInfo.hasCollect == 0) {
				new UiAsyncTask<Integer>(PlayVodFragment.this) {
					@Override
					protected Integer doBackground() throws Throwable {
						return request.collectRecords(mActivity, collectionRecordInfos);
					}

					@Override
					protected void post(Integer result) {
						if (result == 0) {
							mActivity.showToastSafe(getResString(R.string.collect_ok), 1);
							// getCollectionRequest(describeInfo.getVideoId(),
							// describeInfo.getAblumId());
							collectionInfo.hasCollect = 1;
							collectImg.setImageResource(R.drawable.play_collected);
						} else {
							collectImg.setImageResource(R.drawable.play_collect);
							mActivity.showToastSafe(getResString(R.string.collect_failed), 1);
						}
						super.post(result);
					}
				}.execute();
			} else {
				new UiAsyncTask<Integer>(PlayVodFragment.this) {

					@Override
					protected Integer doBackground() throws Throwable {
						return request.unCollectRecords(mActivity, collectionRecordInfos);
					}

					@Override
					protected void post(Integer result) {
						if (result == 0) {
							mActivity.showToastSafe(getResString(R.string.delete_ok), 1);
							collectImg.setImageResource(R.drawable.play_collect);
							collectionInfo.hasCollect = 0;
							// getCollectionRequest(describeInfo.getVideoId(),
							// describeInfo.getAblumId());
						} else {
							collectImg.setImageResource(R.drawable.play_collected);
							mActivity.showToastSafe(getResString(R.string.delete_failed), 1);
						}
						super.post(result);
					}

				}.execute();
			}
		}
	}

	/**
	 * 获取收藏状态接口
	 */
	private void getCollectionRequest(String videoId, String albumId) {
		if (collectImg == null) {
			return;
		}
		if (!MyApplication.getInstance().isLogin()) {
			CollectionRecordInfo info;
			if (albumId != null && !albumId.isEmpty() && !"0".equals(albumId)) {
				// 专辑显示
				info = dao.findBySpecialColum("albumId", albumId);
			} else {
				info = dao.findBySpecialColum("videoId", videoId);
			}
			if (info != null) {
				collectImg.setImageResource(R.drawable.play_collected);
			} else {
				collectImg.setImageResource(R.drawable.play_collect);
			}
			return;
		}
		CollectionStatusDataRequest request = new CollectionStatusDataRequest(mActivity);
		final ArrayList<CollectionInfo> data = new ArrayList<CollectionInfo>();
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put("videoId", videoId);
		mInputParam.put("albumId", albumId);
		mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		request.setInputParam(mInputParam).setOutputData(data).requestTask(Request.Method.GET,
				new DataRequestCallback() {
					@Override
					public void onDataRequestSuccess(Object[] mOutputData) {
						mActivity.runOnUiThread(new Runnable() {

							public void run() {
								if (data.size() > 0) {
									collectionInfo = data.get(0);
									if (collectionInfo != null) {
										if (1 == collectionInfo.hasCollect) {
											collectImg.setImageResource(R.drawable.play_collected);
										} else {
											collectImg.setImageResource(R.drawable.play_collect);
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

	String getCommentType() {
		return "video";
	}

	PlayDetailResponseInfo loadingDetailData() {
		playDetailInfo = new PlayDetailInfo();
		playDetailResponseInfo = new PlayDetailResponseInfo();
		PlayVodDetailDataRequest playDetailDataRequest = new PlayVodDetailDataRequest(mActivity);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputParam.put("videoId", videoId);
		// playDetailInfo 下拉刷新的时候是重新new的，如果下拉时解析时出现错误也将之前的数据清空。正确是当code ==
		// 0时，才重新添加数据，待修改
		int code = playDetailDataRequest.setInputParam(mInputParam).setOutputData(playDetailInfo)
				.request(Request.Method.GET);
		if (code == 0) {
			playDetailResponseInfo.responseSuccess = true;
			playDetailResponseInfo.state = code;
			return playDetailResponseInfo;
		}
		playDetailResponseInfo.responseSuccess = false;
		playDetailResponseInfo.state = code;
		return playDetailResponseInfo;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (describeInfo != null) {
			// 获取收藏状态
			getCollectState();
		}
	}

}
