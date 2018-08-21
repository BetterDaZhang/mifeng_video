package com.letv.autoapk.ui.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xutils.x;
import org.xutils.common.Callback.GroupCallback;
import org.xutils.common.task.AbsTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lecloud.sdk.constant.PlayerParams;
import com.letv.autoapk.R;
import com.letv.autoapk.base.dialog.BaseDialog;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.boss.BossLoginAPI;
import com.letv.autoapk.boss.BossManager;
import com.letv.autoapk.common.net.LruImageCache;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.CollectionRecordDao;
import com.letv.autoapk.download.DownloadSaasCenter;
import com.letv.autoapk.open.OpenShareActivity;
import com.letv.autoapk.open.OpenShareDialog;
import com.letv.autoapk.open.OpenShareDialog.OnShareListener;
import com.letv.autoapk.ui.login.LoginAPI;
import com.letv.autoapk.widgets.CircleImageView;
import com.letv.autoapk.widgets.KeyboardShownLayout;
import com.support.v7.recyclerview.BaseLayoutManager;
import com.support.v7.recyclerview.FFocusCustomRecyclerView;
import com.support.v7.recyclerview.GridLayoutManager;

public class PlayDetailFragment extends BaseTitleFragment implements OnClickListener {
	public static final int SEND_COMMENT_CONTENT = 0x00011;
	public static final int SEND_COMMENT_PRISE = 0x00012;
	KeyboardShownLayout listviewRay;
	PullToRefreshListView mPullRefreshListView;
	LinearLayout headView;
	View headBarView;
	View describeHeaderView;
	View desrcibeRl;
	View describeVip;
	View episodeHeaderView;
	View recommendHeaderView;

	boolean detailSuccess;
	boolean commentSuccess;
	boolean isAllFinished;

	TextView prise;
	TextView criticize;
	ImageView shareImg;
	ImageView downloadImg;
	ImageView collectImg;
	TextView descriptTitle;
	ImageView descriptMore;
	LinearLayout describeAll;
	boolean isSpread = false;
	TextView descriptPlaycount;
	TextView descriptBarrageCount;
	TextView descriptType;
	TextView descriptArea;
	TextView descriptShow;
	TextView descriptBrief;
	TextView episodeTitle;
	TextView episodeUpdateTitle;
	FFocusCustomRecyclerView episodeRecyclerView;
	EpisodeRecyclerAdapter episodeRecyclerAdapter;
	ArtsRecyclerAdapter artsRecyclerAdapter;
	TextView recommendTitle;
	ImageView recommendMore;
	RecommendRecyclerAdapter recommendRecyclerAdapter;
	FFocusCustomRecyclerView recommendRecyclerView;
	PlayVideoInfo describeInfo;
	List<PlayVideoInfo> episodeInfos = new ArrayList<PlayVideoInfo>();
	List<PlayVideoInfo> recommendInfos = new ArrayList<PlayVideoInfo>();
	PlayDetailInfo playDetailInfo;
	PlayDetailResponseInfo playDetailResponseInfo;

	PlayCommentAdapter commentAdapter;
	View commentHeadView;
	TextView commentCount;
	TextView commentUserName;
	EditText sendComment;
	View commentNocontent;
	CircleImageView userCommentIcon;
	RelativeLayout inputCommentLay;
	CircleImageView inputUserCommentIcon;
	EditText inputSendComment;
	TextView inputSendIcon;
	PageInfo mPageInfo;
	List<PlayCommentInfo> commentInfos = new ArrayList<PlayCommentInfo>();
	List<PlayCommentInfo> formatCommentInfos = new ArrayList<PlayCommentInfo>();

	CollectionRecordDao dao;
	DownloadSaasCenter downloadSaaSCenter;
	Bundle mBundle;
	String videoId;
	String albumId;
	String videoTitle;
	boolean firstSetUpView = true;
	int tempCommentCount;
	int commentCountLimit = 200;
	// 点赞
	PlayPriseInfo playPriseInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mBundle = getArguments();
		if (mBundle == null) {
			Toast.makeText(getActivity(), "no data", Toast.LENGTH_LONG).show();
			return;
		}
		dao = MyApplication.getInstance().getDaoByKey(CollectionRecordDao.class.getName());
		downloadSaaSCenter = DownloadSaasCenter.getInstances(mActivity.getApplicationContext());
		downloadSaaSCenter.allowShowMsg(false);
		videoId = mBundle.getString("videoId");
		albumId = mBundle.getString("albumId");
		videoTitle = mBundle.getString("videoTitle");
	}

	@Override
	public void onResume() {
		super.onResume();
		if (userCommentIcon != null) {
			userCommentIcon.setDefaultImageResId(R.drawable.default_img_16_10);
			userCommentIcon.setErrorImageResId(R.drawable.default_img_16_10);
			String userIncon = LoginInfoUtil.getUserInfoPhoto(mActivity);
			userCommentIcon.setImageUrl(userIncon, LruImageCache.getImageLoader(mActivity));
		}
		if (describeInfo != null && describeVip != null) {
			if (MyApplication.getInstance().isNeedBoss() == 1) {
				showDescribeVip();
			} else {
				describeVip.setVisibility(View.GONE);
			}
		}
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

	/**
	 * 初始化顶部栏
	 */
	void initTopView() {
		headView = (LinearLayout) listviewRay.findViewById(R.id.play_detail_head);
	};

	void updateTopView() {

	}

	void getCollectState() {
	}

	/**
	 * 获取点赞、踩的状态接口
	 */
	void getPriseState(final String priseType) {
		final List<PlayPriseInfo> playPriseInfos = new ArrayList<PlayPriseInfo>();
		new UiAsyncTask<Boolean>(PlayDetailFragment.this) {

			@Override
			protected Boolean doBackground() throws Throwable {
				PriseStatusDateRequest priseStatusDateRequest = new PriseStatusDateRequest(mActivity);
				Map<String, String> mInputParam = new HashMap<String, String>();
				mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
				mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
				// if (albumId == null || "0".equals(albumId)) {
				mInputParam.put("type", priseType);
				mInputParam.put("videoIdList", videoId);
				// } else {
				// mInputParam.put("type", "2");
				// mInputParam.put("videoIdList", albumId);
				// }
				int code = priseStatusDateRequest.setInputParam(mInputParam).setOutputData(playPriseInfos)
						.request(Method.GET);
				if (code == 0) {
					return true;
				}
				return false;
			}

			@Override
			protected void post(Boolean result) {
				super.post(result);
				playPriseInfo = playPriseInfos.get(0);
				Drawable priseDrawable = mActivity.getResources().getDrawable(R.drawable.discover_zan_oriange);
				priseDrawable.setBounds(0, 0, priseDrawable.getMinimumWidth(), priseDrawable.getMinimumHeight());
				Drawable priseGrayDrawable = mActivity.getResources().getDrawable(R.drawable.discover_zan_gray);
				priseGrayDrawable.setBounds(0, 0, priseGrayDrawable.getMinimumWidth(),
						priseGrayDrawable.getMinimumHeight());
				Drawable criticizeDrawable = mActivity.getResources().getDrawable(R.drawable.cai);
				criticizeDrawable.setBounds(0, 0, criticizeDrawable.getMinimumWidth(),
						criticizeDrawable.getMinimumHeight());
				Drawable criticizeGrayDrawable = mActivity.getResources().getDrawable(R.drawable.cai_gray);
				criticizeGrayDrawable.setBounds(0, 0, criticizeDrawable.getMinimumWidth(),
						criticizeGrayDrawable.getMinimumHeight());
				if (playPriseInfo.isHasSupport()) {
					prise.setCompoundDrawables(priseDrawable, null, null, null);
				} else {
					prise.setCompoundDrawables(priseGrayDrawable, null, null, null);
				}
				int supportNumber = Integer.parseInt(playPriseInfo.getSupportDingNumeber());
				prise.setText(PlayerAPI.formatCount(supportNumber));
				if (criticize == null) {
					return;
				}
				if (playPriseInfo.isHasCai()) {
					criticize.setCompoundDrawables(criticizeDrawable, null, null, null);
				} else {
					criticize.setCompoundDrawables(criticizeGrayDrawable, null, null, null);
				}
				int caiNumber = Integer.parseInt(playPriseInfo.getSupportCaiNumber());
				criticize.setText(PlayerAPI.formatCount(caiNumber));
			}
		}.execute();
	}

	@Override
	protected View setupDataView() {
		if (playDetailResponseInfo != null && playDetailResponseInfo.state == 5) {
			PlayVideoLeftDialog playVideoLeftDialog = new PlayVideoLeftDialog();
			playVideoLeftDialog.setOnClickListener(new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (getActivity() != null) {
						getActivity().finish();
					}
				}
			});
			BaseDialog.show(getFragmentManager(), playVideoLeftDialog);
			return LayoutInflater.from(mActivity).inflate(R.layout.play_detail_left, null, false);
		}
		if (mBundle.getBoolean("willPlayNext")) {
			UiPlayContext uiPlayContext = new UiPlayContext();
			PlayVideoInfo playVideoInfo = uiPlayContext.getNext(mBundle.getString(PlayerParams.KEY_PLAY_VUID),
					playDetailInfo.getEpisodeInfos());
			mBundle.putString("shareUrl", playVideoInfo.getShareUrl());
			mBundle.putString("imageUrl", playVideoInfo.getImageUrl());
			mBundle.putString("videoDesc", playVideoInfo.getVideoDesc());
			mBundle.putString("videoTitle", playVideoInfo.getVideoTitle());
			mBundle.putString("nextLinkUrl", playVideoInfo.getNextLinkUrl());
		}
		formatDetailData();
		if (firstSetUpView) {
			LayoutInflater inflater = LayoutInflater.from(mActivity);
			listviewRay = (KeyboardShownLayout) inflater.inflate(R.layout.play_detail_pulltolistview, null, false);
			initListview();
			initTopView();
			initDescriptView();
			initEpisodeView();
			initRecommendView();
			if (detailSuccess) {
				// 更新专辑id，videoId
				if (playDetailInfo.getDescribeInfo() != null) {
					videoId = playDetailInfo.getDescribeInfo().getVideoId();
					albumId = playDetailInfo.getDescribeInfo().getAlbumId();
					videoTitle = playDetailInfo.getDescribeInfo().getVideoTitle();
				}
				updateTopView();
				updateDescriptView();
				updateFirstEpisodeView();
				updateFirtRecommendView();
			} else {
				// detailsuccess false则不提示
				// Toast.makeText(mActivity,
				// getResString(R.string.play_detail_detail_failed),
				// Toast.LENGTH_SHORT).show();
			}

			initCommendHeadView();
			formatCommentInfos.clear();
			if (commentSuccess) {
				// 初始化评论
				formatCommentData();
				updateCommendHeadView();
			} else {
				// commentSuccess暂不提示
				// Toast.makeText(mActivity,
				// getResString(R.string.play_detail_comment_failed),
				// Toast.LENGTH_SHORT).show();
			}
			commentAdapter = new PlayCommentAdapter(mActivity, formatCommentInfos, getDefaultHandler(), this);
			mPullRefreshListView.setAdapter(commentAdapter);
			return listviewRay;
		} else {
			if (commentSuccess && commentInfos.size() > 0) {
				mPullRefreshListView.setMode(Mode.BOTH);
			} else {
				mPullRefreshListView.setMode(Mode.PULL_FROM_START);
			}
			if (!detailSuccess) {
				// detailSuccess暂不提示
				// Toast.makeText(mActivity,
				// getResString(R.string.play_detail_detail_failed),
				// Toast.LENGTH_SHORT).show();
			}
			if (playDetailInfo.getDescribeInfo() != null) {
				videoId = playDetailInfo.getDescribeInfo().getVideoId();
				albumId = playDetailInfo.getDescribeInfo().getAlbumId();
				videoTitle = playDetailInfo.getDescribeInfo().getVideoTitle();
			}
			updateTopView();
			updateDescriptView();
			updateRefreshEpisodeView();
			updateRefreshRecommendView();

			formatCommentInfos.clear();
			if (commentSuccess) {
				// 初始化评论
				formatCommentData();
				updateCommendHeadView();
			} else {
				mPullRefreshListView.getRefreshableView().removeHeaderView(commentHeadView);
				mPullRefreshListView.getRefreshableView().removeHeaderView(commentNocontent);
				// Toast.makeText(mActivity,
				// getResString(R.string.play_detail_comment_failed),
				// Toast.LENGTH_SHORT).show();
			}
			commentAdapter.notifyDataSetChanged();
			return listviewRay;
		}
		// 格式话详情数据
	}

	void formatDetailData() {
		episodeInfos.clear();
		recommendInfos.clear();
		describeInfo = null;
		episodeInfos.addAll(playDetailInfo.getEpisodeInfos());
		recommendInfos.addAll(playDetailInfo.getRecommendInfos());
		describeInfo = playDetailInfo.getDescribeInfo();
		MyApplication.getInstance().putInfo("currentPlayVideo", describeInfo);
	}

	void formatCommentData() {
		for (int i = 0; i < commentInfos.size(); i++) {
			PlayCommentInfo playCommentInfo = commentInfos.get(i);
			playCommentInfo.setCommentType(0);
			formatCommentInfos.add(playCommentInfo);
			for (int j = 0; j < playCommentInfo.getReplyCommentInfos().size(); j++) {
				PlayCommentInfo replyCommentInfo = playCommentInfo.getReplyCommentInfos().get(j);
				replyCommentInfo.setCommentType(1);
				formatCommentInfos.add(replyCommentInfo);
			}
		}
	}

	void initListview() {
		mPullRefreshListView = (PullToRefreshListView) listviewRay.findViewById(R.id.play_detail_listview);
		// 有评论数据才支持上拉加载
		if (commentSuccess && commentInfos.size() > 0) {
			mPullRefreshListView.setMode(Mode.BOTH);
		} else {
			mPullRefreshListView.setMode(Mode.PULL_FROM_START);
		}
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				x.task().startTasks(new GroupCallback<AbsTask>() {

					@Override
					public void onSuccess(AbsTask item) {
						if (item instanceof DetailLoadingTask) {
							playDetailResponseInfo = (PlayDetailResponseInfo) item.getResult();
							detailSuccess = playDetailResponseInfo.responseSuccess;
							// 剧集数
							Message msg = mActivity.getDefaultHandler().obtainMessage();
							Bundle bundle = new Bundle();
							bundle.putString("totolEpisodes", playDetailInfo.getTotalEpisodes());
							bundle.putString("updateEpisode", playDetailInfo.getUpdateEpisode());
							bundle.putSerializable("episodes", playDetailInfo.getEpisodeInfos());
							bundle.putInt("displayType", playDetailInfo.getDisplayType());
							msg.setData(bundle);
							msg.what = PlayConst.EPISODE_LIST;
							msg.sendToTarget();
							// 保存鉴权信息
							if (playDetailInfo != null && playDetailInfo.getDescribeInfo() != null) {
								MyApplication.getInstance().putInfo(MyApplication.CURRENT_VOD_VIDEO_INFO,
										playDetailInfo.getDescribeInfo());
							}
						} else if (item instanceof CommentLoadingTask) {
							commentSuccess = (Boolean) item.getResult();
						}
					}

					@Override
					public void onError(AbsTask item, Throwable ex, boolean isOnCallback) {
						if (item instanceof DetailLoadingTask) {
							playDetailResponseInfo = (PlayDetailResponseInfo) item.getResult();
							detailSuccess = playDetailResponseInfo.responseSuccess;
						} else if (item instanceof CommentLoadingTask) {
							commentSuccess = (Boolean) item.getResult();
						}
					}

					@Override
					public void onCancelled(AbsTask item, CancelledException cex) {

					}

					@Override
					public void onFinished(AbsTask item) {

					}

					@Override
					public void onAllFinished() {
						mPullRefreshListView.onRefreshComplete();
						if (playDetailResponseInfo != null && playDetailResponseInfo.state == 5) {
							PlayVideoLeftDialog playVideoLeftDialog = new PlayVideoLeftDialog();
							playVideoLeftDialog.setOnClickListener(new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (getActivity() != null) {
										getActivity().finish();
									}
								}
							});
							BaseDialog.show(getFragmentManager(), playVideoLeftDialog);
							return;
						}
						if (detailSuccess || commentSuccess) {
							notifyDeatailView();
						}

					}
				}, new DetailLoadingTask(), new CommentLoadingTask());
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				new UiAsyncTask<Boolean>(PlayDetailFragment.this) {

					@Override
					protected Boolean doBackground() {
						if (loadingCommentMoreDate() == 0) {
							return true;
						}
						return false;
					}

					@Override
					protected void post(Boolean result) {
						super.post(result);
						mPullRefreshListView.onRefreshComplete();
						if (result && null != commentAdapter) {
							formatCommentInfos.clear();
							notifyCommentView();
						} else {
							if (mPageInfo.getPageIndex() + 1 > mPageInfo.getTotalPage()) {
								mPullRefreshListView.setMode(Mode.PULL_FROM_START);
							}
						}
					}
				}.execute();
			}
		});
	}

	/**
	 * 初始化视频介绍
	 */
	void initDescriptView() {
		if (describeHeaderView == null) {
			describeHeaderView = mActivity.getLayoutInflater().inflate(R.layout.play_detail_describe, null);
		}
		desrcibeRl = describeHeaderView.findViewById(R.id.play_detail_describeRl);
		descriptTitle = (TextView) describeHeaderView.findViewById(R.id.play_detail_describe_title);
		describeVip = describeHeaderView.findViewById(R.id.play_detail_describe_openVip);
		descriptMore = (ImageView) describeHeaderView.findViewById(R.id.play_detail_describe_next);

		// 展开后
		describeAll = (LinearLayout) describeHeaderView.findViewById(R.id.play_detail_all);
		descriptPlaycount = (TextView) describeHeaderView.findViewById(R.id.play_detail_describe_playcount);
		descriptBarrageCount = (TextView) describeHeaderView.findViewById(R.id.play_describe_danmucount);
		descriptType = (TextView) describeHeaderView.findViewById(R.id.play_detail_describe_type);
		descriptArea = (TextView) describeHeaderView.findViewById(R.id.play_detail_describe_area);
		descriptShow = (TextView) describeHeaderView.findViewById(R.id.play_detail_describe_show);
		descriptBrief = (TextView) describeHeaderView.findViewById(R.id.play_detail_describe_brief);

	}

	void updateDescriptView() {
		try {
			if (MyApplication.getInstance().isNeedBoss() == 1) {
				showDescribeVip();
			} else {
				describeVip.setVisibility(View.GONE);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private void showDescribeVip() {
		if (!LoginInfoUtil.isVip(mActivity) && describeInfo.getVip()) {
			describeVip.setVisibility(View.VISIBLE);
			describeVip.setOnClickListener(this);
		} else {
			describeVip.setVisibility(View.GONE);
		}
	}

	/**
	 * 初始化剧集
	 */
	void initEpisodeView() {

	}

	void updateFirstEpisodeView() {
	}

	void updateRefreshEpisodeView() {
	}

	/**
	 * 初始化推荐
	 */
	void initRecommendView() {
		if (recommendHeaderView == null) {
			recommendHeaderView = mActivity.getLayoutInflater().inflate(R.layout.play_detail_recommend, null);
		}
		recommendTitle = (TextView) recommendHeaderView.findViewById(R.id.play_detail_recommend_tiltle);
		recommendMore = (ImageView) recommendHeaderView.findViewById(R.id.play_detail_recommend_next);
		recommendRecyclerView = (FFocusCustomRecyclerView) recommendHeaderView
				.findViewById(R.id.play_recommend_recyclerview);
		recommendRecyclerView.setHasFixedSize(true);
		recommendRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, BaseLayoutManager.HORIZONTAL));
		recommendRecyclerView.setItemAnimator(new DefaultItemAnimator());
		recommendRecyclerAdapter = new RecommendRecyclerAdapter(mActivity, recommendInfos,
				R.layout.play_detail_recommend_item);

	}

	void updateFirtRecommendView() {
		if (recommendInfos.size() == 0) {
			return;
		}
		recommendTitle.setText(getResString(R.string.play_detail_recommend_title));
		// recommendRecyclerAdapter.setVideoId(videoId);
		recommendRecyclerView.setAdapter(recommendRecyclerAdapter);
		mPullRefreshListView.getRefreshableView().addHeaderView(recommendHeaderView);
	}

	void updateRefreshRecommendView() {
		if (recommendInfos.size() == 0) {
			mPullRefreshListView.getRefreshableView().removeHeaderView(recommendHeaderView);
			return;
		}
		mPullRefreshListView.getRefreshableView().removeHeaderView(recommendHeaderView);
		mPullRefreshListView.getRefreshableView().addHeaderView(recommendHeaderView);
		recommendTitle.setText(getResString(R.string.play_detail_recommend_title));
		// recommendRecyclerAdapter.setVideoId(videoId);
		recommendRecyclerAdapter.notifyDataSetChanged();
	}

	/**
	 * 初始化评论头部
	 */

	void initCommendHeadView() {
		commentHeadView = mActivity.getLayoutInflater().inflate(R.layout.play_detail_commend_head, null);
		commentUserName = (TextView) commentHeadView.findViewById(R.id.play_detail_comment_username);
		userCommentIcon = (CircleImageView) commentHeadView.findViewById(R.id.play_detail_comment_icon);
		sendComment = (EditText) commentHeadView.findViewById(R.id.play_detail_comment_edit);

		// 底部输入框
		inputCommentLay = (RelativeLayout) listviewRay.findViewById(R.id.play_detail_input_lay);
		inputUserCommentIcon = (CircleImageView) listviewRay.findViewById(R.id.play_detail__inputcomment_icon);
		inputSendComment = (EditText) listviewRay.findViewById(R.id.play_detail_input_comment_edit);
		inputSendComment.setPadding(mActivity.dip2px(7), 0, 0, 0);
		inputSendIcon = (TextView) listviewRay.findViewById(R.id.play_detail_input_comment_send);
		bindCommentListener();

		commentNocontent = mActivity.getLayoutInflater().inflate(R.layout.play_detail_commend_nocontent, null);
	}

	void updateCommendHeadView() {
		mPullRefreshListView.getRefreshableView().removeHeaderView(commentHeadView);
		mPullRefreshListView.getRefreshableView().addHeaderView(commentHeadView);
		String commentFormatCount = PlayerAPI.formatCount(mPageInfo.getmTotalCountShow());
		commentCount.setText(commentFormatCount);
		tempCommentCount = mPageInfo.getmTotalCountShow();

		userCommentIcon.setDefaultImageResId(R.drawable.play_comment_user);
		userCommentIcon.setErrorImageResId(R.drawable.play_comment_user);
		String userIncon = LoginInfoUtil.getUserInfoPhoto(mActivity);
		userCommentIcon.setImageUrl(userIncon, LruImageCache.getImageLoader(mActivity));
		commentUserName.setText(LoginInfoUtil.getUserName(mActivity));

		// 底部输入框
		inputUserCommentIcon.setDefaultImageResId(R.drawable.play_comment_user);
		inputUserCommentIcon.setErrorImageResId(R.drawable.play_comment_user);
		inputUserCommentIcon.setImageUrl(userIncon, LruImageCache.getImageLoader(mActivity));

		if (formatCommentInfos.size() == 0) {
			mPullRefreshListView.getRefreshableView().removeHeaderView(commentNocontent);
			mPullRefreshListView.getRefreshableView().addHeaderView(commentNocontent);
		} else {
			mPullRefreshListView.getRefreshableView().removeHeaderView(commentNocontent);
		}
	}

	/**
	 * bindlistener
	 * 
	 * @param
	 */
	private void bindCommentListener() {
		sendComment.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					showSoftInputEdit(0, 0, "", "", 0);
				}
				return false;
			}
		});
	}

	@Override
	protected void startLoading() {
		x.task().startTasks(new GroupCallback<AbsTask>() {

			@Override
			public void onSuccess(AbsTask item) {
				if (item instanceof DetailLoadingTask) {
					playDetailResponseInfo = (PlayDetailResponseInfo) item.getResult();
					detailSuccess = playDetailResponseInfo.responseSuccess;
					// 剧集数
					Message msg = mActivity.getDefaultHandler().obtainMessage();
					Bundle bundle = new Bundle();
					bundle.putString("totolEpisodes", playDetailInfo.getTotalEpisodes());
					bundle.putString("updateEpisode", playDetailInfo.getUpdateEpisode());
					bundle.putSerializable("episodes", playDetailInfo.getEpisodeInfos());
					bundle.putInt("displayType", playDetailInfo.getDisplayType());
					msg.setData(bundle);
					msg.what = PlayConst.EPISODE_LIST;
					msg.sendToTarget();
					// 保存鉴权信息
					if (playDetailInfo != null && playDetailInfo.getDescribeInfo() != null) {
						MyApplication.getInstance().putInfo(MyApplication.CURRENT_VOD_VIDEO_INFO,
								playDetailInfo.getDescribeInfo());
					}
				} else if (item instanceof CommentLoadingTask) {
					commentSuccess = (Boolean) item.getResult();
				}
			}

			@Override
			public void onError(AbsTask item, Throwable ex, boolean isOnCallback) {
				if (item instanceof DetailLoadingTask) {
					playDetailResponseInfo = (PlayDetailResponseInfo) item.getResult();
					detailSuccess = playDetailResponseInfo.responseSuccess;
				} else if (item instanceof CommentLoadingTask) {
					commentSuccess = (Boolean) item.getResult();
				}
			}

			@Override
			public void onCancelled(AbsTask item, CancelledException cex) {

			}

			@Override
			public void onFinished(AbsTask item) {

			}

			@Override
			public void onAllFinished() {
				if (getActivity() == null)
					return;
				isAllFinished = true;
				if (!detailSuccess && !commentSuccess) {
					loadingLayout.onError();
					return;
				}
				if (detailSuccess || commentSuccess) {
					loadingLayout.onSuccess(true);
				} else {
					loadingLayout.onSuccess(false);
				}

			}
		}, new DetailLoadingTask(), new CommentLoadingTask());
	}

	protected class DetailLoadingTask extends AbsTask<PlayDetailResponseInfo> {

		@Override
		protected PlayDetailResponseInfo doBackground() {
			return loadingDetailData();
		}

		@Override
		protected void onSuccess(PlayDetailResponseInfo result) {

		}

		@Override
		protected void onError(Throwable ex, boolean isCallbackError) {

		}

	}

	protected class CommentLoadingTask extends AbsTask<Boolean> {

		@Override
		protected Boolean doBackground() {
			return loadingCommentData();
		}

		@Override
		protected void onSuccess(Boolean result) {

		}

		@Override
		protected void onError(Throwable ex, boolean isCallbackError) {

		}
	}

	Boolean loadingCommentData() {
		commentInfos.clear();
		mPageInfo = new PageInfo();
		PlayVodCommentRequest playCommentDataRequest = new PlayVodCommentRequest(mActivity);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put("videoId", videoId);
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
		mInputParam.put("type", getCommentType());
		mInputParam.put(StringDataRequest.PAGE, "1");
		mInputParam.put(StringDataRequest.PAGE_SIZE, "" + StringDataRequest.PAGE_SIZE_COUNT);
		int code = playCommentDataRequest.setInputParam(mInputParam).setOutputData(commentInfos, mPageInfo)
				.request(Method.GET);
		if (code == 0) {
			return true;
		}
		return false;
	}

	// 必须重写commentType表明评论是点播还是直播
	String getCommentType() {
		return "";
	}

	PlayDetailResponseInfo loadingDetailData() {
		return null;
	}

	@Override
	protected boolean loadingData() {
		// 无需重写的类
		return false;
	}

	@Override
	protected void onHandleMessage(Message msg) {
		switch (msg.what) {
		case SEND_COMMENT_CONTENT:
			Bundle bundle = msg.getData();
			final int listPositon = bundle.getInt("listPositon");
			final String replayId = bundle.getString("replayId");
			final String replyName = bundle.getString("replayName");
			final int replyType = bundle.getInt("replyType");
			showSoftInputEdit(listPositon, 1, replayId, replyName, replyType);
			break;
		case PlayConst.UPDATE_PALY_DETAIL:
			DisplayVideoInfo displayVideoInfo = (DisplayVideoInfo) msg.obj;
			videoId = displayVideoInfo.getVideoId();
			albumId = displayVideoInfo.getAlbumId();
			videoTitle = displayVideoInfo.getVideoTitle();
			// refreshDetailListView();
			firstSetUpView = false;
			startLoading();
			Message msgDetail = mActivity.getDefaultHandler().obtainMessage();
			msgDetail.what = PlayConst.VIDEO_SELECTED;
			Bundle vodBundle = new Bundle();
			vodBundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_VOD);
			vodBundle.putString(PlayerParams.KEY_PLAY_UUID, MyApplication.getInstance().getTenantId());
			vodBundle.putString(PlayerParams.KEY_PLAY_VUID, displayVideoInfo.getVideoId());
			vodBundle.putString("albumId", displayVideoInfo.getAlbumId());
			vodBundle.putString("albumName", displayVideoInfo.getmAlbumName());
			vodBundle.putString("shareUrl", displayVideoInfo.getShareUrl());
			vodBundle.putString("imageUrl", displayVideoInfo.getImageUrl());
			vodBundle.putString("videoDesc", displayVideoInfo.getVideoDesc());
			vodBundle.putString("videoTitle", displayVideoInfo.getVideoTitle());
			msgDetail.setData(vodBundle);
			msgDetail.sendToTarget();

			// 若剧集分页显示在栈顶，则处理
			Fragment fragment = getFragmentManager().findFragmentByTag("describeFragment");
			if (fragment != null && fragment instanceof PlayEpisodeFragment
					&& msg.arg1 != PlayConst.IFEPISODEFRAGMENTSHOE) {
				int position = PlayerAPI.fixedPostion(episodeInfos, videoId);
				int episodePosition = position / PlayEpisodeFragment.MOD_COUNT;
				((PlayEpisodeFragment) fragment).notifyDataChanged(videoId, episodePosition);
			}
			if (fragment != null && fragment instanceof PlayArtsFragment
					&& msg.arg1 != PlayConst.IFEPISODEFRAGMENTSHOE) {
				((PlayArtsFragment) fragment).notifyDataChanged(videoId);
			}
			break;
		case PlayConst.VIDEO_COllECTION_CHANGE:
			getCollectState();
			break;
		default:
			break;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play_detail_share:
			// 分享
			boolean isNoNetwork = PlayerAPI.addNoNetworkLimit(mActivity);
			if (isNoNetwork) {
				return;
			}
			if (mActivity instanceof OpenShareActivity) {
				final DisplayVideoInfo info = (DisplayVideoInfo) v.getTag();
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
						shareActivity.doshare(openType, info.getShareUrl(), info.getVideoTitle(), info.getVideoDesc(),
								info.getImageUrl());
					}
				});
				BaseDialog.show(shareActivity.getSupportFragmentManager(), dialog);

			}
			break;
		case R.id.play_detail_describe_openVip:
			//  开通vip会员
			if (!MyApplication.getInstance().isLogin()) {// 登录
				BossLoginAPI.startLogin(mActivity, BossManager.FLAG_ENTER_VIPCENTER, true);
			} else {
				MyApplication.getInstance().getBossManager().switchAim(BossManager.FLAG_ENTER_VIPCENTER, true);
			}
			break;
		default:
			break;
		}

	}

	void sendPlayPriseRequest(final String type) {
		boolean isNoNetwork = PlayerAPI.addNoNetworkLimit(mActivity);
		if (isNoNetwork) {
			return;
		}
		if (playPriseInfo == null) {
			return;
		}
		if (playPriseInfo.isHasCai()) {
			mActivity.showToastSafe(R.string.play_prise_toast, Toast.LENGTH_SHORT);
			return;
		}
		if (playPriseInfo.isHasSupport()) {
			mActivity.showToastSafe(R.string.play_prise_repeat_toast, Toast.LENGTH_SHORT);
			return;
		}
		new UiAsyncTask<Boolean>(PlayDetailFragment.this) {

			@Override
			protected Boolean doBackground() {
				String target = videoId;
				// if (albumId != null && "0".equals(albumId)) {
				// // 专辑点赞
				// type = "2";
				// target = albumId;
				// }
				PraiseUtils praiseUtils = new PraiseUtils();
				boolean doSupport = praiseUtils.sendPraiseRequest(type, "1", target, LoginInfoUtil.getUserId(mActivity),
						MyApplication.getInstance().getTenantId(), mActivity);

				return doSupport;
			}

			protected void post(Boolean result) {

			};
		}.execute();
		try {
			if (!playPriseInfo.isHasSupport()) {
				Drawable priseDrawable = mActivity.getResources().getDrawable(R.drawable.discover_zan_oriange);
				priseDrawable.setBounds(0, 0, priseDrawable.getMinimumWidth(), priseDrawable.getMinimumHeight());
				prise.setText(PlayerAPI.formatCount(Integer.parseInt(playPriseInfo.getSupportDingNumeber()) + 1));
				prise.setCompoundDrawables(priseDrawable, null, null, null);
				playPriseInfo.setHasSupport(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendComment(int position, int commentType, final String replyId, final String replayName,
			final int replyTpe) {
		if (!MyApplication.getInstance().isLogin()) {
			Toast.makeText(getActivity(), R.string.plaly_login_comment, Toast.LENGTH_SHORT).show();
			return;
		}
		final String comment = inputSendComment.getText().toString().trim();
		if (comment.isEmpty()) {
			Toast.makeText(getActivity(), getResString(R.string.play_nocomment), Toast.LENGTH_SHORT).show();
			return;
		}
		if (comment.length() > commentCountLimit) {
			mActivity.showToastSafe(getResString(R.string.play_solong, (comment.length() - commentCountLimit)),
					Toast.LENGTH_SHORT);
			return;
		}
		hideSoftKeybord();
		hideSoftInputEdit();
		inputSendComment.setText("");
		// 本地添加评论内容
		mPullRefreshListView.getRefreshableView().removeHeaderView(commentNocontent);
		PlayCommentInfo playCommentInfo = new PlayCommentInfo();
		playCommentInfo.setCommentContent(comment);
		CommentUser commentUser = new CommentUser();
		commentUser.setNickName(LoginInfoUtil.getLoginInfo(mActivity).getNickName());
		commentUser.setUserIcon(LoginInfoUtil.getUserInfoPhoto(mActivity));
		commentUser.setUserId(LoginInfoUtil.getUserId(mActivity));
		playCommentInfo.setUser(commentUser);
		// playCommentInfo.setCommentTime("" + System.currentTimeMillis());
		playCommentInfo.setCommentTime(getResString(R.string.justnow));
		if (replayName != null && !replayName.isEmpty() && replyTpe == 1) {
			playCommentInfo.setReplayNickName("@" + replayName);
		}
		playCommentInfo.setSupportCount(0);
		playCommentInfo.setCommentType(commentType);
		formatCommentInfos.add(position, playCommentInfo);
		commentAdapter.notifyDataSetChanged();
		commentCount.setText((PlayerAPI.formatCount(++tempCommentCount)));

		new UiAsyncTask<Boolean>(PlayDetailFragment.this) {

			@Override
			protected Boolean doBackground() throws Throwable {
				PlaySendCommentDataRequest playSendCommentDataRequest = new PlaySendCommentDataRequest(mActivity);
				Map<String, String> mInputParam = new HashMap<String, String>();
				mInputParam.put("videoId", videoId);
				mInputParam.put("albumId", albumId);
				mInputParam.put("replyCommentId", replyId);
				mInputParam.put("content", comment);
				mInputParam.put("token", LoginInfoUtil.getToken(mActivity));
				mInputParam.put("title", videoTitle);
				mInputParam.put("type", getCommentType());
				mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
				mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
				int code = playSendCommentDataRequest.setInputParam(mInputParam).setOutputData(commentInfos)
						.request(Method.POST);
				if (code == 0) {
					return true;
				}
				return false;
			}

			@Override
			protected void post(Boolean result) {
				super.post(result);
				if (sendComment != null) {
					sendComment.setText("");
				}
			}
		}.execute();
	}

	public void showSoftInputEdit(final int listPositon, final int type, final String replyId, final String replyName,
			final int replyType) {
		if (!MyApplication.getInstance().isLogin()) {// 登录
			Toast.makeText(mActivity, R.string.plaly_login_comment, Toast.LENGTH_SHORT).show();
			LoginAPI.stratLogin(mActivity);
			return;
		}
		if (inputCommentLay != null) {
			getDefaultHandler().postDelayed(new Runnable() {
				@Override
				public void run() {
					inputSendComment.setFocusableInTouchMode(true);
					inputSendComment.requestFocus();
					InputMethodManager inputMethodManager = (InputMethodManager) mActivity
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					boolean bool = inputMethodManager.showSoftInput(inputSendComment, InputMethodManager.SHOW_FORCED);
					if (bool) {
						inputCommentLay.setVisibility(View.VISIBLE);
						String userIncon = LoginInfoUtil.getUserInfoPhoto(mActivity);
						;
						inputUserCommentIcon.setImageUrl(userIncon, LruImageCache.getImageLoader(mActivity));
						inputSendComment.setTextColor(getActivity().getResources().getColor(R.color.code4));
						inputSendComment.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
						inputSendComment.setOnKeyListener(new OnKeyListener() {

							@Override
							public boolean onKey(View v, int keyCode, KeyEvent event) {
								if (keyCode == KeyEvent.KEYCODE_ENTER) {
									sendComment(listPositon, type, replyId, replyName, replyType);
									return true;
								} else if (keyCode == KeyEvent.KEYCODE_BACK) {
									hideSoftInputEdit();
									return false;
								}
								return false;
							}
						});
						inputSendIcon.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								sendComment(listPositon, type, replyId, replyName, replyType);
							}
						});
					}
				}
			}, 300);
			listviewRay.setOnkbdStateListener(new KeyboardShownLayout.onKybdsChangeListener() {

				@Override
				public void onKeyBoardStateChange(int state) {
					if (state == KeyboardShownLayout.KEYBOARD_STATE_HIDE) {
						hideSoftInputEdit();
					}
				}
			});
		}
	}

	public void hideSoftInputEdit() {
		if (inputCommentLay != null) {
			inputCommentLay.setVisibility(View.GONE);
			getDefaultHandler().postDelayed(new Runnable() {
				@Override
				public void run() {
					inputSendComment.setFocusableInTouchMode(false);
					inputSendComment.clearFocus();
				}
			}, 200);

		}
	}

	public void showSoftKeybord() {
		InputMethodManager manager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.showSoftInput(listviewRay, InputMethodManager.SHOW_FORCED);
	}

	public void hideSoftKeybord() {
		InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(listviewRay.getApplicationWindowToken(), 0);
	}

	private int loadingCommentMoreDate() {
		// 过滤结果接口
		PlayVodCommentRequest playCommentDataRequest = new PlayVodCommentRequest(mActivity);
		Map<String, String> mInputstreamParams = new HashMap<String, String>();
		if (mPageInfo.getPageIndex() + 1 <= mPageInfo.getTotalPage()) {
			mInputstreamParams.put("videoId", videoId);
			mInputstreamParams.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
			mInputstreamParams.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
			mInputstreamParams.put("type", getCommentType());
			mInputstreamParams.put(StringDataRequest.PAGE, String.valueOf(mPageInfo.getPageIndex() + 1));
			mInputstreamParams.put(StringDataRequest.PAGE_SIZE, String.valueOf(StringDataRequest.PAGE_SIZE_COUNT));
			int code = playCommentDataRequest.setInputParam(mInputstreamParams).setOutputData(commentInfos, mPageInfo)
					.request(Method.GET);
			return code;
		} else {
			return -1;
		}
	}

	protected void notifyCommentView() {
		// 初始化评论
		formatCommentData();
		commentAdapter.notifyDataSetChanged();
	}

	protected void notifyDeatailView() {
		if (getActivity() == null)
			return;
		if (detailSuccess) {
			// 更新专辑id，videoId
			if (playDetailInfo.getDescribeInfo() != null) {
				videoId = playDetailInfo.getDescribeInfo().getVideoId();
				albumId = playDetailInfo.getDescribeInfo().getAlbumId();
				videoTitle = playDetailInfo.getDescribeInfo().getVideoTitle();
			}
			formatDetailData();
			updateTopView();
			updateDescriptView();
			updateRefreshEpisodeView();
			updateRefreshRecommendView();
		}
		if (commentSuccess) {
			// 初始化评论
			formatCommentInfos.clear();
			formatCommentData();
			updateCommendHeadView();
			commentAdapter.notifyDataSetChanged();
			if (formatCommentInfos.size() > 0) {
				mPullRefreshListView.setMode(Mode.BOTH);
			} else {
				mPullRefreshListView.setMode(Mode.PULL_FROM_START);
			}
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
			if (info != null && isAllFinished && !detailSuccess && !commentSuccess) {
				firstSetUpView = true;
				startLoading();
			}
		}
	};

}
