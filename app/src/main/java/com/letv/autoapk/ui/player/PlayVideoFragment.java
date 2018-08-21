package com.letv.autoapk.ui.player;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.xutils.common.util.DensityUtil;

import com.android.volley.Request;
import com.lecloud.sdk.api.ad.constant.ADPlayerParams;
import com.lecloud.sdk.api.md.entity.vod.VideoHolder;
import com.lecloud.sdk.config.LeCloudPlayerConfig;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.constant.StatusCode;
import com.lecloud.sdk.player.IAdPlayer;
import com.lecloud.sdk.player.IVodPlayer;
import com.lecloud.sdk.surfaceview.ISurfaceView;
import com.lecloud.sdk.surfaceview.impl.BaseSurfaceView;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.lecloud.sdk.videoview.base.BaseVideoView;
import com.letv.ads.bean.AdElementMime;
import com.letv.ads.constant.AdInfoConstant;
import com.letv.android.client.cp.sdk.player.vod.CPVodPlayer;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.PlayVideoActivity;
import com.letv.autoapk.base.activity.PlayVideoActivity.onBackPressedListener;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.base.dialog.BaseDialog;
import com.letv.autoapk.base.fragment.BaseFragment;
import com.letv.autoapk.base.net.LoginInfo;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.boss.BossLoginAPI;
import com.letv.autoapk.boss.BossManager;
import com.letv.autoapk.boss.GetVideoChargeInfoDataRequest;
import com.letv.autoapk.boss.LePaySuccessListener;
import com.letv.autoapk.boss.LepayManager;
import com.letv.autoapk.boss.VideoChargeInfo;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.PlayRecordInfo;
import com.letv.autoapk.dao.PlayedRecordDao;
import com.letv.autoapk.player.ISplayerController;
import com.letv.autoapk.player.SaasRequest;
import com.letv.autoapk.player.SaasVideo;
import com.letv.autoapk.player.SaasVideoView;
import com.letv.autoapk.player.ShowAdPicUtils;
import com.letv.autoapk.ui.login.LoginAPI;
import com.letv.autoapk.ui.player.view.PlayControlContainer;
import com.letv.autoapk.ui.player.view.PlaySeekBar;
import com.letv.autoapk.ui.player.view.PlayerContainerLayout;
import com.letv.autoapk.ui.record.RecordsAPI;
import com.letv.autoapk.utils.NetworkUtils;
import com.letv.autoapk.utils.SystemUtls;
import com.letv.autoapk.utils.TimerUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig;
import master.flame.danmaku.danmaku.parser.DanmakuFactory;
import master.flame.danmaku.danmaku.parser.android.JSONSource;

public class PlayVideoFragment extends BaseFragment implements LePaySuccessListener {
	private static final String TAG = PlayVideoFragment.class.getSimpleName();
	private DanmakuDialog ddialog;
	/**
	 * 是否正在试看
	 */
	private boolean showTryandsee;
	private boolean showEndTryandsee;
	private long trylooktime;
	private int needbuy;
	private View adskipView;
	VideoViewListener videoViewListener = new VideoViewListener() {
		private void setRateType(SaasVideo video) {
			Map<String, String> map = video.getVtypes();
			uicontext.setRateTypeItems(map);
			String currenttype = MyApplication.getInstance().getString(PlayConst.DEFINATIONTYPE);
			if (map != null) {
				if (map.containsKey(currenttype)) {
					uicontext.setCurrentRateType(currenttype);
					player.setDataSourceByRate(currenttype);
				} else if (uicontext.getRateTypeItems() != null && uicontext.getRateTypeItems().size() > 0) {
					uicontext.setCurrentRateType(video.getDefaultVtype());
					player.setDataSourceByRate(video.getDefaultVtype());
				} else {
					// playContext.setCurrentDefinationType(-1);
				}
				if (ratetype != null && uicontext.getRateTypeItemById(uicontext.getCurrentRateType()) != null) {
					ratetype.setText(uicontext.getRateTypeItemById(uicontext.getCurrentRateType()).getName());
				}
			}
		}

		private void onMediaback(SaasVideo video) {
			needbuy = 0;
			trylooktime = 360000;
			int ashowmin = 0;
			if (video != null) {
				needbuy = video.getNeedbuy();
				trylooktime = video.getTrylooktime();
				if (MyApplication.getInstance().isNeedBoss() == 0) {
					needbuy = 0;
				}
				if (needbuy == 0) {// 如果是免费视频，那么试看时间是尽可能长的
					trylooktime = Long.MAX_VALUE;
				}
				ashowmin = video.getAshowmin();
			}
			if (needbuy == 1) {
				getVideoChargeInfo(false);
			}
			if (video != null) {
				uicontext.setVideoTitle(video.getTitle());
				uicontext.setDownloadable(video.isDownload());
			}
			if (titleView != null) {
				titleView.setText(uicontext.getVideoTitle());
			}
		}

		@Override
		public void onStateResult(int state, Bundle bundle) {
			if (player == null || getActivity() == null) {
				return;
			}
			switch (state) {
			case PlayerEvent.PLAY_VIDEOSIZE_CHANGED:
				int vwidth = bundle.getInt(PlayerParams.KEY_WIDTH);
				int vheight = bundle.getInt(PlayerParams.KEY_HEIGHT);
				ISurfaceView ssurfaceView = surfaceView.getSurfaceView();
				if (ssurfaceView instanceof BaseSurfaceView) {
					((BaseSurfaceView) ssurfaceView).onVideoSizeChanged(vwidth, vheight);
				}

				break;
			case PlayerEvent.PLAY_PREPARED:
				hideMsg();
				player.start();
				// player.seekTo(lastPosition);
				if (!controllayout.isShown() && !menulayout.isShown())
					showControllayout();
				getDefaultHandler().sendEmptyMessage(PlayConst.ONSTART);
				if (MyApplication.getInstance().getBoolean(PlayConst.DANMAKUENABLE) && mDanmakuView != null
						&& !mDanmakuView.isPrepared()) {
					View danview = controllayout.findViewById(R.id.control_barrage);
					if (danview != null) {
						danview.setBackgroundResource(R.drawable.play_barrage_p);
					}

					uicontext.setShowDanmaku(true);
					getDefaultHandler().sendEmptyMessage(PlayConst.START_DANMAKU);
					if (sendDM != null)
						sendDM.setVisibility(View.VISIBLE);
				}
				break;
			case PlayerEvent.MEDIADATA_VOD:
				if (SystemUtls.isNetworkConnected(mActivity.getApplicationContext())) {
					int statusCode = bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE, 0);
					if (statusCode == StatusCode.MEDIADATA_SUCCESS) {
						VideoHolder videoHolder = bundle.getParcelable(PlayerParams.KEY_RESULT_DATA);
						SaasVideo video = (SaasVideo) videoHolder;
						surfaceView.setIspano(video.isPano());
						setRateType(video);
						onMediaback(video);
						if (uicontext.isPlayingAd())
							return;
						if (surfaceView.isIspano()) {
							GesturePanoControl gestureControl = new GesturePanoControl(getDefaultHandler(),
									surfaceView.getSurfaceView());
							controllayout.setOnTouchListener(gestureControl);
						} else {
							GestureControl gestureControl = new GestureControl(mActivity, controllayout,
									getDefaultHandler());
							gestureControl.setSeekable(true);
							controllayout.setOnTouchListener(gestureControl);
						}
					} else {
						String errormsg = bundle.getString(PlayerParams.KEY_RESULT_ERROR_MSG);
						String errorCode = bundle.getString(PlayerParams.KEY_RESULT_ERROR_CODE);
						if (errormsg != null && errormsg.contains("gpc")) {
							showErrorMsg(getResString(R.string.player_proxy_error) + errorCode);
						} else {
							showErrorMsg(getResString(R.string.player_proxy_error) + errorCode + "\n" + errormsg);
						}
					}
				} else {
					showNetErrorMsg();
				}
				break;

			case PlayerEvent.PLAY_ERROR:
				if (!SystemUtls.isNetworkConnected(mActivity.getApplicationContext())) {
					showNetErrorMsg();
					return;
				}
				int errorCode = bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE, 0);
				if (errorCode == StatusCode.PLAY_ERROR_DECODE)
					showErrorMsg(getResString(R.string.media_decoding_error));
				if (errorCode == StatusCode.PLAY_ERROR_UNKNOWN) {
					showErrorMsg(getResString(R.string.media_not_exist));
				}
				break;
			case PlayerEvent.PLAY_LOADINGSTART:
				if (!SystemUtls.isNetworkConnected(mActivity.getApplicationContext())) {
					showNetErrorMsg();
					if (uicontext.isPlayingAd()) {
						uicontext.setIsPlayingAd(false);
						getDefaultHandler().sendEmptyMessage(PlayConst.AD);
					}
					return;
				}
				if (lastPosition > 0) {
					surfaceView.seekTo(lastPosition);
				}
				showBufferMsg(getResString(R.string.loading_pls_wait));
				// if (uicontext.isClickPauseByUser()) {
				// player.setVolume(0, 0);
				// }
				break;
			case PlayerEvent.PLAY_BUFFERING:
				if (!uicontext.isPlayingAd()) {
					int percentage = bundle.getInt(PlayerParams.KEY_PLAY_BUFFERPERCENT);
					int second = seekBar.getMax() * percentage / 100;
					seekBar.setSecondaryProgress(second);
				}
				break;
			case PlayerEvent.PLAY_INFO:
				if (!SystemUtls.isNetworkConnected(mActivity.getApplicationContext())) {
					if (player != null) {
						player.stop();
					}
					showNetErrorMsg();
					return;
				}
				int statusCode = bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE, 0);
				if (statusCode == StatusCode.PLAY_INFO_VIDEO_RENDERING_START) {
					surfaceTransparent();
					uicontext.setRenderingStart(true);
					if (uicontext.isClickPauseByUser()) {
						surfaceView.onPause();
						player.setVolume(1.0f, 1.0f);
						controllayout.findViewById(R.id.control_play).setBackgroundResource(R.drawable.play_play);
					} else {
						playTimer.sendEmptyMessage(PlayConst.STARTTIME);
					}
				}
				if (statusCode == StatusCode.PLAY_INFO_BUFFERING_START) {
					showBufferMsg(getResString(R.string.buffering));
					if (mDanmakuView.isPrepared() && !mDanmakuView.isPaused()) {
						mDanmakuView.pause();
					}
					getDefaultHandler().sendEmptyMessageDelayed(PlayConst.NETWORK_TIMEOUT, 30000);
				}

				if (statusCode == StatusCode.PLAY_INFO_BUFFERING_END) {
					getDefaultHandler().removeMessages(PlayConst.NETWORK_TIMEOUT);
					if (mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
						mDanmakuView.resume();
					}
					hideMsg();
				}
				break;
			case PlayerEvent.PLAY_SEEK_COMPLETE:
				playTimer.removeMessages(PlayConst.STARTTIME);
				playTimer.sendEmptyMessage(PlayConst.STARTTIME);
				if (mDanmakuView.isPrepared() && player != null) {
					mDanmakuView.seekTo(player.getCurrentPosition());
				}
				if (helper != null && uicontext.isClickPauseByUser()) {
					helper.playOrpause(controllayout.findViewById(R.id.control_play));
				}
				break;

			case PlayerEvent.PLAY_COMPLETION:// 播放完成
				if (uicontext.isPlayingAd()) {
					return;
				}
				playTimer.sendEmptyMessage(PlayConst.STOPTIME);

				if (uicontext.getPlayList() == null || uicontext.getPlayList().isEmpty()) {
					player.retry();
				} else {
					PlayVideoInfo playVideoInfo = uicontext.getNext(mBundle.getString(PlayerParams.KEY_PLAY_VUID),
							uicontext.getPlayList());
					if (playVideoInfo == null || (ddialog != null && ddialog.isResumed()))
						break;
					FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
					Fragment vodFragment = fragmentManager.findFragmentByTag(PlayConst.VODFRAGMENTTAG);
					if (vodFragment != null && vodFragment instanceof BaseFragment) {
						Handler vodHandler = ((BaseFragment) vodFragment).getDefaultHandler();
						Message msg = vodHandler.obtainMessage();
						msg.what = PlayConst.UPDATE_PALY_DETAIL;
						msg.obj = playVideoInfo;
						msg.sendToTarget();
					}
				}
				break;
			case PlayerEvent.AD_START:
				if (adTime != null) {
					adTime.setText("");
				}
				uicontext.setIsPlayingAd(true);// 广告播放时间
				hideMsg();
				hideMenulayout();
				hideTryandseeLayout();
				getDefaultHandler().sendEmptyMessage(PlayConst.CONTROL);
				getDefaultHandler().sendEmptyMessage(PlayConst.AD);
				break;
			case PlayerEvent.AD_COMPLETE:
				uicontext.setIsPlayingAd(false);
				adlayout.findViewById(R.id.ad_silence).setBackgroundResource(R.drawable.play_voice_on);
				getDefaultHandler().sendEmptyMessage(PlayConst.AD);
				getDefaultHandler().sendEmptyMessage(PlayConst.CONTROL);
				player.setVolume(1, 1);
				break;
			case PlayerEvent.AD_PROGRESS:
				int time = bundle.getInt(IAdPlayer.AD_TIME);
				if(adskipView!=null){
					adskipView.setVisibility(View.VISIBLE);
				}
				if (adTime != null && time > 0){
					adTime.setVisibility(View.VISIBLE);
					adTime.setText(String.valueOf(time).concat(getResources().getString(R.string.second_short)));
				}
				break;
			case PlayerEvent.AD_ELEMENT_RESULT:
				if (bundle != null && !(player != null && player.isPlaying())) {
					AdElementMime mAdElementMime = (AdElementMime) bundle
							.getSerializable(ADPlayerParams.KEY_AD_ELEMENTS);
					containerlayout.showAdPic(mAdElementMime, mVideoPauseListener);
				}
				break;
			}

		}

		ShowAdPicUtils.VideoPauseListener mVideoPauseListener = new ShowAdPicUtils.VideoPauseListener() {
			@Override
			public boolean isVideoPause() {
				return !(player != null && player.isPlaying());
			}
		};

		@Override
		public String onGetVideoRateList(LinkedHashMap<String, String> arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	class PlayListener implements OnClickListener, OnSeekBarChangeListener, OnItemClickListener, OnTouchListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.play_retry:
				if (!NetworkUtils.isNetworkConnected(mActivity.getApplicationContext())) {
					mActivity.showToastSafe(getResString(R.string.base_networkerror), Toast.LENGTH_SHORT);
				}
				retryplay();
				break;
			case R.id.control_danmaku:
				if (MyApplication.getInstance().isLogin() == false) {
					mActivity.showToastSafe(R.string.not_login, Toast.LENGTH_SHORT);
					LoginAPI.stratLogin(mActivity);
					return;
				}
				containerlayout.dismissAdPic();
				if (player == null || !uicontext.isShowDanmaku())
					return;
				if (ddialog == null)
					ddialog = new DanmakuDialog();
				ddialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						if (player != null) {
							// player.resume();
							// if(mDanmakuView.isPaused())
							// mDanmakuView.resume();
							// playTimer.removeMessages(PlayConst.STARTTIME);
							// playTimer.sendEmptyMessage(PlayConst.STARTTIME);
							String textString = ddialog.getSendText();
							if (textString != null) {
								BaseDanmaku danmaku = DanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
								if (danmaku == null || mDanmakuView == null) {
									return;
								}
								danmaku.text = textString;
								danmaku.padding = 0;
								danmaku.priority = 0; // 可能会被各种过滤器过滤并隐藏显示
								danmaku.isLive = false;
								danmaku.time = mDanmakuView.getCurrentTime() + 1000;
								danmaku.textSize = 28f * (DensityUtil.getDensity() - 0.6f);
								danmaku.textColor = Color.WHITE;
								danmaku.textShadowColor = Color.BLACK;
								mDanmakuView.addDanmaku(danmaku);
							}
						}
					}
				});
				String htime = String.valueOf(player.getCurrentPosition() + 500);
				// player.pause();
				// if(!mDanmakuView.isPaused())
				// mDanmakuView.pause();
				Map<String, String> mInputParam = new HashMap<String, String>();
				// videoId=dddo&albumId=“1&title=“”&content=““&type=”“&clientType=”“&htime=”“&token=”&tenantId=“”&”
				mInputParam.put("videoId", mBundle.getString(PlayerParams.KEY_PLAY_VUID));
				mInputParam.put("albumId", mBundle.getString("albumId"));
				mInputParam.put("title", mBundle.getString("videoTitle"));
				mInputParam.put("type", "record");
				mInputParam.put("clientType", "1");
				mInputParam.put("htime", htime);
				mInputParam.put("token", LoginInfoUtil.getToken(mActivity));
				mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
				ddialog.setParam(mInputParam);
				BaseDialog.show(getFragmentManager(), ddialog);
				break;
			case R.id.control_lock:
				if (uicontext.isLockFlag()) {
					uicontext.setLockFlag(false);
					if (surfaceView != null && surfaceView.isIspano()) {
						GesturePanoControl gestureControl = new GesturePanoControl(getDefaultHandler(),
								surfaceView.getSurfaceView());
						controllayout.setOnTouchListener(gestureControl);
					} else {
						GestureControl gestureControl = new GestureControl(mActivity, controllayout,
								getDefaultHandler());
						gestureControl.setSeekable(true);
						controllayout.setOnTouchListener(gestureControl);
					}

					getDefaultHandler().sendEmptyMessage(PlayConst.SHOWCONTROL);
					((ImageView) v).setImageResource(R.drawable.play_lock_off);
					mActivity.showToastSafe(R.string.unlock, Toast.LENGTH_SHORT);
				} else {
					uicontext.setLockFlag(true);
					containerlayout.dismissAdPic();
					controllayout.setOnTouchListener(listener);
					((ImageView) v).setImageResource(R.drawable.play_lock);
					getDefaultHandler().sendEmptyMessage(PlayConst.HIDECONTROL);
					mActivity.showToastSafe(R.string.locked, Toast.LENGTH_SHORT);
				}

				break;
			case R.id.control_back:
			case R.id.ad_back:
				if (helper == null)
					mActivity.finish();
				else if (!helper.doBack(mActivity)) {
					mActivity.finish();
				}
				break;
			case R.id.control_play:
				if (helper != null) {
					if (helper.playOrpause(v)) {
						containerlayout.dismissAdPic();
					}
					;
				} else if (!NetworkUtils.isWifiConnected(mActivity.getApplicationContext())
						&& NetworkUtils.isNetAvailable(mActivity)) {
					boolean notonly = MyApplication.getInstance().getBoolean(MyApplication.NOT_ONLYWIFI);
					if (notonly) {
						mActivity.showToastSafe(R.string.wifi_network, Toast.LENGTH_LONG);
					} else {
						showNetStateDialog();
						return;
					}

				}
				if (uicontext != null && mDanmakuView.isPrepared()) {
					if (uicontext.isClickPauseByUser()) {
						mDanmakuView.pause();
					} else {
						mDanmakuView.resume();
					}
				}
				break;
			case R.id.control_switch:
			case R.id.ad_zoom:
				if (helper != null) {
					int orientation = uicontext.getScreenResolution() == ISplayerController.SCREEN_ORIENTATION_PORTRAIT
							? ISplayerController.SCREEN_ORIENTATION_LANDSCAPE
							: ISplayerController.SCREEN_ORIENTATION_PORTRAIT;
					helper.changeScreen(mActivity, orientation);
				}

				break;
			case R.id.ad_silence:
				int backres = surfaceView.isAdsound() ? R.drawable.play_voice_off : R.drawable.play_voice_on;
				v.setBackgroundResource(backres);
				surfaceView.setAdsound(!surfaceView.isAdsound());
				break;
			case R.id.ad_skiplayout:
				if (!MyApplication.getInstance().isLogin()) {
					BossLoginAPI.startLogin(mActivity, BossManager.FLAG_ENTER_VIPCENTER, false);
				} else {
					MyApplication.getInstance().getBossManager().switchAim(BossManager.FLAG_ENTER_VIPCENTER, true);
				}
				break;
			case R.id.control_ratetype:
				if (helper != null) {
					BaseAdapter adapter = helper.initmenu(PlayHelper.RATETYPE, mActivity, mBundle);
					if (adapter == null)
						return;
					menuListView.setAdapter(adapter);
					menuListView.setOnItemClickListener(this);
					menuListView.setTag(id);
					getDefaultHandler().sendEmptyMessage(PlayConst.SHOWMENU);
				}
				break;
			case R.id.control_more:
				if (helper != null) {
					BaseAdapter adapter = helper.initmenu(PlayHelper.MORE, mActivity, mBundle);
					if (adapter == null)
						return;
					menuListView.setAdapter(adapter);
					getDefaultHandler().sendEmptyMessage(PlayConst.SHOWMENU);
				}

				break;
			case R.id.control_choose:
				if (helper != null) {
					BaseAdapter adapter = helper.initmenu(PlayHelper.CHOOSE, mActivity, mBundle);
					if (adapter == null)
						return;
					menuListView.setAdapter(adapter);
					getDefaultHandler().sendEmptyMessage(PlayConst.SHOWMENU);
				}
				break;
			case R.id.control_barrage:
				if (uicontext != null) {
					if (mDanmakuView.isPrepared() == false && uicontext.isShowDanmaku() == false) {
						v.setBackgroundResource(R.drawable.play_barrage_p);
						// ((Button) v)
						// .setText(getResString(R.string.play_barrage));
						// ((Button) v).setTextColor(getResources().getColor(
						// R.color.code1));
						uicontext.setShowDanmaku(true);
						getDefaultHandler().sendEmptyMessage(PlayConst.START_DANMAKU);
						sendDM.setVisibility(View.VISIBLE);
					} else if (uicontext.isShowDanmaku()) {
						v.setBackgroundResource(R.drawable.play_barrage);
						// ((Button) v)
						// .setText(getResString(R.string.play_barrage));
						// ((Button) v).setTextColor(getResources().getColor(
						// R.color.white));
						uicontext.setShowDanmaku(false);
						mDanmakuView.hide();
						sendDM.setVisibility(View.INVISIBLE);
					} else {
						v.setBackgroundResource(R.drawable.play_barrage_p);
						// ((Button) v)
						// .setText(getResString(R.string.play_barrage));
						// ((Button) v).setTextColor(getResources().getColor(
						// R.color.code1));
						uicontext.setShowDanmaku(true);
						mDanmakuView.show();
						sendDM.setVisibility(View.VISIBLE);
					}
					MyApplication.getInstance().putBoolean(PlayConst.DANMAKUENABLE, uicontext.isShowDanmaku());
				}
				break;
			default:
				break;
			}

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (msglayout.isShown()) {
				hideMsg();
			}
			// if (needbuy == 1 && progress / 1000 >= trylooktime) {
			// showEndTryAndSeeLayout();
			// }
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) positionView.getLayoutParams();
			params.leftMargin = (seekBar.getWidth() - positionView.getWidth()) * progress / 1000;
			positionView.requestLayout();

			if (seekBar instanceof PlaySeekBar) {
				((PlaySeekBar) seekBar).doRefresh(progress);
			}

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			helper.startTrackingTouch(seekBar);
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			containerlayout.dismissAdPic();
			helper.stopTrackingTouch(seekBar);
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			int menuid = (Integer) parent.getTag();
			if (menuid == R.id.control_ratetype) {
				String ratetypeid = (String) view.getTag();

				if (ratetypeid != null) {
					long current = player.getCurrentPosition();
					containerlayout.dismissAdPic();
					player.setDataSourceByRate(ratetypeid);
					player.seekTo(current);
					ratetype.setText(uicontext.getRateTypeItems().get(position).getName());
					uicontext.setCurrentRateType(ratetypeid);
					uicontext.setClickPauseByUser(false);
					MyApplication.getInstance().putString(PlayConst.DEFINATIONTYPE, ratetypeid);
				}
			}
			if (menuid == R.id.control_choose) {
			}
			if (menuid == R.id.control_more) {
			}
			getDefaultHandler().sendEmptyMessage(PlayConst.HIDEMENU);
			menuListView.setAdapter(null);
			menuListView.setOnItemClickListener(null);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			boolean onclick = v.onTouchEvent(event);
			if (v == menulayout && onclick == false) {
				getDefaultHandler().sendEmptyMessage(PlayConst.HIDEMENU);
			}
			if (v == controllayout) {
				View childView = controllayout.findViewById(R.id.control_lock);
				childView.setVisibility(childView.isShown() ? View.INVISIBLE : View.VISIBLE);
				childView.requestLayout();
			}
			return onclick;
		}

	}

	private void retryplay() {
		hideMsg();
		showControllayout();
		if (player == null)
			createOnePlayer();
		else {
			if (player.getCurrentPosition() > 0) {
				lastPosition = player.getCurrentPosition();
			}
			player.retry();
			player.seekTo(lastPosition);
		}
	}

	private void showEndTryAndSeeLayout() {
		if (getActivity() == null)
			return;
		showEndTryandsee = true;
		player.stop();
		player.reset();
		hideTryandseeLayout();
		surfaceBlack();
		if (needbuy == 1 && videoChargeInfo == null) {
			showRetryVideoChargeInfoLayout(true);
			return;
		}
		switchEndTryandseeLayout(videoChargeInfo);
	}

	/**
	 * Only when the video is paid, the method will be invoked
	 * 
	 * @param hasGotVideoChargeInfo
	 */
	private void getVideoChargeInfo(final boolean hasGotVideoChargeInfo) {
		new UiAsyncTask<Integer>(this) {

			@Override
			protected Integer doBackground() throws Throwable {
				GetVideoChargeInfoDataRequest request = new GetVideoChargeInfoDataRequest(mActivity);
				Map<String, String> mInputParam = new HashMap<String, String>();
				mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
				mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
				mInputParam.put("platform", "104002");
				mInputParam.put("albumId", mBundle.getString("albumId"));
				int code = request.setInputParam(mInputParam).setOutputData(videoChargeInfo)
						.request(Request.Method.GET);
				MyApplication.getInstance().putInfo(MyApplication.CURRENT_VIDEO_CHARGE_INFO, videoChargeInfo);
				return code;
			}

			@Override
			protected void post(Integer result) {
				// bindHandler.sendEmptyMessageDelayed(PLAY_SHOW_TRYANDSEE,
				// 1000);
				if (result != 0) {
					videoChargeInfo = null;
					if (hasGotVideoChargeInfo) {
						hideTryandseeLayout();
						showRetryVideoChargeInfoLayout(true);
					}
				} else {
					if (hasGotVideoChargeInfo) {
						hideTryandseeLayout();
						hideRetryVideoChargeInfoLayout();
						showEndTryAndSeeLayout();
					}
				}
				super.post(result);
			}
		}.execute();
	}

	void hideMsg() {
		msglayout.setVisibility(View.INVISIBLE);
		msglayout.findViewById(R.id.play_retry).setVisibility(View.GONE);
	}

	/**
	 * vip页面
	 */
	void showVip() {
		vip.setVisibility(View.VISIBLE);
		vip.findViewById(R.id.control_back)
				.setOnClickListener(new BossOnClickListener(false, BossManager.FLAG_ENTER_VIPCENTER, true));
		View ll_login = vip.findViewById(R.id.ll_login);
		View tv_login = vip.findViewById(R.id.tv_login);
		View tv_get_vip = vip.findViewById(R.id.tv_get_vip);

		if (MyApplication.getInstance().isLogin()) {
			ll_login.setVisibility(View.GONE);
			tv_get_vip.setOnClickListener(new BossOnClickListener(false, BossManager.FLAG_ENTER_VIPCENTER, true));
		} else {
			ll_login.setVisibility(View.VISIBLE);
			tv_login.setOnClickListener(new BossOnClickListener(true, BossManager.FLAG_ENTER_VIPCENTER, true));
			tv_get_vip.setOnClickListener(new BossOnClickListener(true, BossManager.FLAG_ENTER_VIPCENTER, true));
		}
	}

	/**
	 * 单片
	 */
	void showSingle() {
		singleFilm.setVisibility(View.VISIBLE);
		singleFilm.findViewById(R.id.control_back)
				.setOnClickListener(new BossOnClickListener(false, BossManager.FLAG_ENTER_VIPCENTER, true));
		View ll_login = singleFilm.findViewById(R.id.ll_login);
		View tv_login = singleFilm.findViewById(R.id.tv_login);
		View tv_pay = singleFilm.findViewById(R.id.tv_pay);
		View vipHalf = singleFilm.findViewById(R.id.ll_vip_half);
		View tv_get_vip = singleFilm.findViewById(R.id.tv_get_vip);
		TextView tv_price = (TextView) singleFilm.findViewById(R.id.tv_price);
		TextView tv_price_to_pay = (TextView) singleFilm.findViewById(R.id.tv_price_to_pay);
		tv_price.setText(videoChargeInfo.getPrice());
		if (MyApplication.getInstance().isLogin()) {
			ll_login.setVisibility(View.GONE);
			if (loginInfo.getIsVip() == 0) {
				tv_price_to_pay.setText(R.string.play_how_much__to_pay_with_half);
				vipHalf.setVisibility(View.GONE);
				tv_pay.setOnClickListener(new BossOnClickListener(false, BossManager.FLAG_ENTER_ORDER, true));
			} else {
				tv_price_to_pay.setText(R.string.play_how_much_to_pay);
				vipHalf.setVisibility(View.VISIBLE);
				tv_pay.setOnClickListener(new BossOnClickListener(false, BossManager.FLAG_ENTER_ORDER, true));
				tv_get_vip.setOnClickListener(new BossOnClickListener(false, BossManager.FLAG_ENTER_VIPCENTER, false));// 会员中心购买
			}
		} else {
			tv_price_to_pay.setText(R.string.play_how_much_to_pay);
			tv_get_vip.setOnClickListener(new BossOnClickListener(true, BossManager.LOGIN_TO_IDVIP, false));
			tv_pay.setOnClickListener(new BossOnClickListener(true, BossManager.FLAG_ENTER_ORDER, true));
			tv_login.setOnClickListener(new BossOnClickListener(true, BossManager.FLAG_ENTER_ORDER, true));
			ll_login.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * vip或者单片
	 */
	void showVipOrSingle() {
		vipOrSingle.setVisibility(View.VISIBLE);
		vipOrSingle.findViewById(R.id.control_back)
				.setOnClickListener(new BossOnClickListener(false, BossManager.FLAG_ENTER_VIPCENTER, true));
		View ll_vip_login = vipOrSingle.findViewById(R.id.ll_vip_login);
		View tv_login = vipOrSingle.findViewById(R.id.tv_login);
		TextView tv_pay = (TextView) vipOrSingle.findViewById(R.id.tv_pay);
		View tv_get_vip = vipOrSingle.findViewById(R.id.tv_get_vip);
		tv_pay.setText(videoChargeInfo.getPrice() + getResString(R.string.play_how_much_to_pay));
		if (MyApplication.getInstance().isLogin()) {
			ll_vip_login.setVisibility(View.GONE);
			tv_get_vip.setOnClickListener(new BossOnClickListener(false, BossManager.FLAG_ENTER_VIPCENTER, true));
			tv_pay.setOnClickListener(new BossOnClickListener(false, BossManager.FLAG_ENTER_ORDER, true));
		} else {
			tv_get_vip.setOnClickListener(new BossOnClickListener(true, BossManager.FLAG_ENTER_VIPCENTER, true));
			tv_login.setOnClickListener(new BossOnClickListener(true, BossManager.FLAG_ENTER_VIPCENTER, true));
			tv_pay.setOnClickListener(new BossOnClickListener(true, BossManager.FLAG_ENTER_ORDER, true));
			ll_vip_login.setVisibility(View.VISIBLE);
		}
	}

	void showBufferMsg(String msg) {
		msglayout.findViewById(R.id.progress_wheel).setVisibility(View.VISIBLE);
		msgView.setText(msg);
		msglayout.setVisibility(View.VISIBLE);
	}

	void showErrorMsg(String msg) {
		msglayout.findViewById(R.id.progress_wheel).setVisibility(View.GONE);
		msgView.setText(msg);
		msglayout.setVisibility(View.VISIBLE);
	}

	void showNetErrorMsg() {
		hideControllayout();
		if (menulayout.isShown()) {
			hideMenulayout();
		}
		msglayout.findViewById(R.id.progress_wheel).setVisibility(View.GONE);
		msglayout.findViewById(R.id.play_retry).setVisibility(View.VISIBLE);
		msgView.setText(R.string.network_connected_failed);
		msglayout.setVisibility(View.VISIBLE);
	}

	private void initOldAdtime() {
		adTime = new TextView(mActivity);
		adTime.setId(R.id.ad_skip);
		adTime.setBackgroundColor(Color.parseColor("#89000000"));
		adTime.setTextColor(getResources().getColor(R.color.code1));
		adTime.setTextSize(13);
		adTime.setGravity(Gravity.CENTER_VERTICAL);
		adTime.setVisibility(View.INVISIBLE);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, mActivity.dip2px(28),
				Gravity.RIGHT);
		params.topMargin = mActivity.dip2px(10);
		params.rightMargin = mActivity.dip2px(15);
		adTime.setPadding(mActivity.dip2px(10), 0, mActivity.dip2px(10), 0);
		((FrameLayout) adlayout).addView(adTime, params);
	}

	void showAdlayout() {
		adlayout.setVisibility(View.VISIBLE);
		adlayout.findViewById(R.id.ad_back).setOnClickListener(listener);
		// adlayout.findViewById(R.id.ad_details).setOnClickListener(listener);
		adlayout.findViewById(R.id.ad_silence).setOnClickListener(listener);
		if (MyApplication.getInstance().isNeedBoss() == 1) {
			adTime = (TextView) adlayout.findViewById(R.id.ad_skip);
			adskipView = adlayout.findViewById(R.id.ad_skiplayout);
			adskipView.setOnClickListener(listener);
			adskipView.setVisibility(View.INVISIBLE);
		} else {
			View view = adlayout.findViewById(R.id.ad_skiplayout);
			if (view != null) {
				((FrameLayout) adlayout).removeView(view);
				initOldAdtime();
			}

		}

		adlayout.findViewById(R.id.ad_zoom).setOnClickListener(listener);
	}

	void hideAdlayout() {
		adlayout.setVisibility(View.GONE);
	}

	void showMenulayout() {
		containerlayout.dismissAdPic();
		menulayout.show();
	}

	void hideMenulayout() {
		menulayout.hide();
	}

	void showTryandseeLayout() {
		if (getActivity() == null)
			return;
		if (uicontext.isPlayingAd() || needbuy == 0) {
			return;
		}
		if (videoChargeInfo == null) {// 请求影片信息接口失败，byebye
			return;
		}
		showTryandsee = true;
		tryandseeLayout.setVisibility(View.VISIBLE);
		((View) tryandseeLayout.getParent()).bringToFront();
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(1000);
		tryandseeLayout.startAnimation(alphaAnimation);
		// fill view
		TextView player_tryandsee_desc = (TextView) tryandseeLayout.findViewById(R.id.player_tryandsee_desc);
		String beforeTime = getResString(R.string.play_text_before_tryandsee_time);
		String afterTime = getResString(R.string.play_text_after_tryandsee_time);
		String showTime;
		if (trylooktime < 60) {
			afterTime = getResString(R.string.play_text_after_tryandsee_time_second);
			showTime = trylooktime + "";
		} else {
			showTime = trylooktime / 60 + "";
		}
		player_tryandsee_desc.setText(beforeTime + showTime + afterTime);

		boolean login = MyApplication.getInstance().isLogin();
		if (login) {
			llPayedToLogin.setVisibility(View.GONE);
		} else {
			llPayedToLogin.setVisibility(View.VISIBLE);
		}
		switch (videoChargeInfo.getChargeType()) {
		case 3:// vip
			tvToSeeTotal.setText(R.string.mine_get_vip);
			if (login) {
				tvToSeeTotal.setOnClickListener(new BossOnClickListener(false, BossManager.FLAG_ENTER_VIPCENTER, true));
			} else {
				tvToSeeTotal.setOnClickListener(new BossOnClickListener(true, BossManager.FLAG_ENTER_VIPCENTER, true));
				tvLogin.setOnClickListener(new BossOnClickListener(true, BossManager.FLAG_ENTER_VIPCENTER, true));
				tvLoginOrVip.setText(R.string.play_is_vip);
				tvLogin.setText(R.string.play_login_rightnow);
			}
			break;
		case 1:// single
			tvToSeeTotal.setText(R.string.play_pay_rightnow);
			if (login) {
				if (LoginInfoUtil.isVip(mActivity)) {
					tvToSeeTotal.setOnClickListener(new BossOnClickListener(false, BossManager.FLAG_ENTER_ORDER, true));
				} else {
					tvToSeeTotal.setOnClickListener(new BossOnClickListener(false, BossManager.FLAG_ENTER_ORDER, true));
				}
			} else {
				tvToSeeTotal.setOnClickListener(new BossOnClickListener(true, BossManager.FLAG_ENTER_ORDER, true));
				tvLogin.setOnClickListener(new BossOnClickListener(true, BossManager.FLAG_ENTER_ORDER, true));
				tvLoginOrVip.setText(R.string.play_payed2);
				tvLogin.setText(R.string.play_login_rightnow);
			}

			break;
		case 2:// single or vip
			tvToSeeTotal.setText(R.string.mine_get_vip);
			if (login) {
				tvToSeeTotal.setOnClickListener(new BossOnClickListener(false, BossManager.FLAG_ENTER_VIPCENTER, true));
			} else {
				tvToSeeTotal.setOnClickListener(new BossOnClickListener(true, BossManager.FLAG_ENTER_VIPCENTER, true));
				tvLoginOrVip.setOnClickListener(new BossOnClickListener(true, BossManager.FLAG_ENTER_VIPCENTER, true));
				tvLogin.setOnClickListener(new BossOnClickListener(true, BossManager.FLAG_ENTER_VIPCENTER, true));
				tvLoginOrVip.setText(R.string.play_is_vip);
				tvLogin.setText(R.string.play_login_rightnow);
			}
			break;

		default:
			break;
		}
	}

	void showRetryVideoChargeInfoLayout(final boolean hasGotVideoChargeInfo) {
		playRetryVideoChargeInfoLayout.findViewById(R.id.control_back)
				.setOnClickListener(new BossOnClickListener(false, 0, true));
		playRetryVideoChargeInfoLayout.setVisibility(View.VISIBLE);
		View retry = playRetryVideoChargeInfoLayout.findViewById(R.id.play_retry);
		retry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getVideoChargeInfo(hasGotVideoChargeInfo);
			}
		});
	}

	void hideRetryVideoChargeInfoLayout() {
		playRetryVideoChargeInfoLayout.setVisibility(View.GONE);
	}

	void hideTryandseeLayout() {
		showTryandsee = false;
		tryandseeLayout.setVisibility(View.GONE);
	}

	private void hideEndTryandsee() {
		vip.setVisibility(View.GONE);
		vipOrSingle.setVisibility(View.GONE);
		singleFilm.setVisibility(View.GONE);
	}

	void showControllayout() {
		if (showEndTryandsee) {
			return;
		}
		controllayout.setVisibility(View.VISIBLE);
		// hideTryandseeLayout();
		if (surfaceView == null)
			return;
		if (uicontext != null && uicontext.isPlayingAd())
			return;
		if (surfaceView.isIspano()) {
			GesturePanoControl gestureControl = new GesturePanoControl(getDefaultHandler(),
					surfaceView.getSurfaceView());
			controllayout.setOnTouchListener(gestureControl);
		} else {
			GestureControl gestureControl = new GestureControl(mActivity, controllayout, getDefaultHandler());
			gestureControl.setSeekable(true);
			controllayout.setOnTouchListener(gestureControl);
		}

	}

	void hideControllayout() {
		controllayout.setVisibility(View.GONE);
		controllayout.setOnTouchListener(null);
		// if (showTryandsee) {
		// tryandseeLayout.setVisibility(View.VISIBLE);
		// }
	}

	Handler playTimer = new Handler() {
		private long setProgress() {
			long position = getPosition();
			long duration = getDuration();
			// Logger.i("playvideo", position + " " + duration);
			if (duration > 0) {
				/**
				 * 缓冲进度
				 */
				if (player.isPlaying()) {
					if (seekBar.isHovered() == false) {
						long pos = 1000L * position / duration;
						seekBar.setProgress((int) pos);
					}

					positionView.setText(TimerUtils.stringForTime((int) position / 1000));
					durationView.setText(TimerUtils.stringForTime((int) duration / 1000));
					durationView.setTag(duration);
				}
			}

			return position;
		}

		private long getDuration() {
			if (player == null) {
				return 0;
			}
			return player.getDuration();
		}

		private long getPosition() {
			if (player == null) {
				return 0;
			}
			return player.getCurrentPosition();
		}

		void reset() {
			try {
				removeMessages(PlayConst.STARTTIME);
			} catch (Exception e) {
				Logger.log(e);
			}
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case PlayConst.STOPTIME:
				reset();
				controllayout.findViewById(R.id.control_play).setBackgroundResource(R.drawable.play_pause);
				break;
			case PlayConst.STARTTIME:
				long position = setProgress();
				if (needbuy == 1 && position / 1000 >= trylooktime && !isAuthSuccess) {
					showEndTryAndSeeLayout();
				}
				if (player != null && player.isPlaying()) {
					sendEmptyMessageDelayed(PlayConst.STARTTIME, 1000 - (position % 1000));
				}
				break;
			case PlayConst.REQUESTADPIC:
				if (LeCloudPlayerConfig.getInstance().getAdType().equals("ssp")) {
					((IVodPlayer) player).requestAdData(String.valueOf(AdInfoConstant.AdZoneType.PAUSE));
				}
				break;
			default:
				break;
			}
		}
	};

	private UiAsyncTask<Integer> danmakutask;

	@Override
	protected void onHandleMessage(Message msg) {
		int what = msg.what;
		final int times = msg.arg1;
		switch (what) {
		case PlayConst.NETWORK_TIMEOUT:
			if (surfaceView != null)
				surfaceView.onPause();
			showNetErrorMsg();
			break;
		case PlayConst.START_DANMAKU:
			if (danmakutask != null) {
				danmakutask.cancel();
				danmakutask = null;
			}
			danmakutask = new UiAsyncTask<Integer>(PlayVideoFragment.this) {
				DanmakuDataRequest request;

				@Override
				protected void post(Integer result) {
					if (result == 0) {
						JSONSource source = request.getResult();
						MyParser parser = new MyParser();
						parser.load(source);
						mDanmakuView.setCallback(new DrawHandler.Callback() {
							@Override
							public void updateTimer(DanmakuTimer timer) {

							}

							@Override
							public void prepared() {
								mDanmakuView.start(player.getCurrentPosition());
								if (uicontext.getScreenResolution() == ISplayerController.SCREEN_ORIENTATION_PORTRAIT)
									mDanmakuView.hide();
							}
						});
						mDanmakuView.prepare(parser);
						mDanmakuView.showFPS(false);
						mDanmakuView.enableDanmakuDrawingCache(true);
					} else if (times < 3) {
						Message danMessage = getDefaultHandler().obtainMessage(PlayConst.START_DANMAKU);
						danMessage.arg1 = times + 1;
						getDefaultHandler().sendMessageDelayed(danMessage, 3000);
					}
				}

				@Override
				protected Integer doBackground() throws Throwable {
					// ?videoId=dddo&tenantId=“”&startTimestamp=“1”&endTimestamp=“1"
					request = new DanmakuDataRequest(mActivity);
					Map<String, String> mInputParam = new HashMap<String, String>();
					mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
					mInputParam.put("videoId", mBundle.getString(PlayerParams.KEY_PLAY_VUID));
					mInputParam.put("startTimestamp", "0");
					mInputParam.put("endTimestamp", String.valueOf(player.getDuration()));
					int code = request.setInputParam(mInputParam).request(Request.Method.GET);
					return code;
				}
			};
			danmakutask.execute();
			break;
		case PlayConst.HIDEMENU:
			hideMenulayout();
			showControllayout();
			getDefaultHandler().sendEmptyMessage(PlayConst.SHOWCONTROL);
			break;
		case PlayConst.SHOWMENU:
			hideControllayout();
			showMenulayout();
			break;
		case PlayConst.SHOWORHIDECONTROL:
			int scount = controllayout.getChildCount();
			boolean isshow = false;
			for (int i = 0; i < scount; i++) {
				View child = controllayout.getChildAt(i);
				if (child instanceof PlayControlContainer) {
					isshow = child.isShown();
					((PlayControlContainer) child).showOrhide();
					if (child.isShown()) {
						showTryandseeLayout();
					} else {
						hideTryandseeLayout();
					}
				} else if (child.getId() == R.id.control_lock) {
					child.setVisibility(isshow ? View.INVISIBLE : View.VISIBLE);
				} else if (child.getId() == R.id.control_danmaku) {
					if (uicontext != null && uicontext.isShowDanmaku())
						child.setVisibility(isshow ? View.INVISIBLE : View.VISIBLE);
				}
			}
			break;
		case PlayConst.HIDECONTROL:
			showTryandseeLayout();

			int count = controllayout.getChildCount();
			for (int i = 0; i < count; i++) {
				View child = controllayout.getChildAt(i);
				if (child instanceof PlayControlContainer) {
					((PlayControlContainer) child).hide();

				} else if (child.getId() == R.id.control_lock) {
					child.setVisibility(View.INVISIBLE);
				} else if (child.getId() == R.id.control_danmaku) {
					child.setVisibility(View.INVISIBLE);
				}
			}
			break;
		case PlayConst.SHOWCONTROL:
			hideTryandseeLayout();
			int count2 = controllayout.getChildCount();
			for (int i = 0; i < count2; i++) {
				View child = controllayout.getChildAt(i);
				if (child instanceof PlayControlContainer) {
					((PlayControlContainer) child).show();
				} else if (child.getId() == R.id.control_lock) {
					child.setVisibility(View.VISIBLE);
				} else if (child.getId() == R.id.control_danmaku) {
					if (uicontext != null && uicontext.isShowDanmaku())
						child.setVisibility(View.VISIBLE);
				}
			}
			getDefaultHandler().sendEmptyMessageDelayed(PlayConst.HIDECONTROL, 4000);
			break;
		case PlayConst.CONTROL:
			if (uicontext.isPlayingAd() || menulayout.isShown() || msglayout.findViewById(R.id.play_retry).isShown()) {
				hideControllayout();
			} else {
				showControllayout();
				getDefaultHandler().sendEmptyMessageDelayed(PlayConst.HIDECONTROL, 4000);
			}

			break;
		case PlayConst.AD:
			if (uicontext.isPlayingAd()) {// ad_start
				showAdlayout();
			} else {// ad_end
				hideAdlayout();
				getDefaultHandler().sendEmptyMessage(PlayConst.CONTROL);
			}
			break;
		case PlayConst.ONSTART:
			seekBar.setVisibility(View.VISIBLE);
			if (player.isPlaying()) {
				controllayout.findViewById(R.id.control_play).setBackgroundResource(R.drawable.play_pause);
			} else {
				controllayout.findViewById(R.id.control_play).setBackgroundResource(R.drawable.play_play);
			}

			break;
		case PlayConst.SEEKEND:
			if (helper != null) {
				containerlayout.dismissAdPic();
				helper.stopTrackingTouch(seekBar);
				long position = player.getCurrentPosition();
				if (needbuy == 1 && position / 1000 >= trylooktime) {
					showEndTryAndSeeLayout();
				}
			}
			getDefaultHandler().sendEmptyMessageDelayed(PlayConst.HIDECONTROL, 4000);
			break;
		case PlayConst.SEEKSTART:
			if (helper != null) {
				helper.startTrackingTouch(seekBar);
				getDefaultHandler().sendEmptyMessage(PlayConst.SHOWCONTROL);
			}

			break;
		case PlayConst.SEEKTO:
			if (seekBar != null) {
				seekBar.setProgress(helper.progress + msg.arg1);
			}
			mTextSeekTo.setText(getPlayerProgress() + "/" + getPlayerDuration());
			break;
		default:
			break;
		}

	}

	public CharSequence getPlayerProgress() {
		String progress = TimerUtils.stringForTime((int) (seekBar.getProgress() * player.getDuration() / 1000 / 1000));
		return progress;
	}

	public CharSequence getPlayerDuration() {
		String duration = TimerUtils.stringForTime((int) player.getDuration() / 1000);
		return duration;
	}

	private PlayListener listener = new PlayListener();
	private PlayHelper helper;
	private Bundle mBundle;
	private long lastPosition = 0;
	// private boolean isPanoVideo;
	// private PanoRenderer panoRenderer;
	// private Config mConfig;
	private UiPlayContext uicontext;
	private CPVodPlayer player;
	private String nextVideoId;

	private SeekBar seekBar;
	private ViewGroup videolayout;
	private View msglayout;
	private View adlayout;
	private RelativeLayout controllayout;
	private PlayControlContainer menulayout;
	private SaasVideoView surfaceView;
	private TextView positionView;
	private TextView durationView;
	private TextView titleView;
	private TextView mTextSeekTo;
	private TextView msgView;
	private FrameLayout detailContainer;
	private IDanmakuView mDanmakuView;
	private View sendDM;
	/** 上次打开时间 */
	private long lastOpenTime;
	// private String vuid;
	private TextView ratetype;
	private ListView menuListView;
	private TextView adTime;
	private PlayerContainerLayout containerlayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		try {
			FrameLayout frameLayout = new FrameLayout(mActivity);
			frameLayout.setLayoutParams(
					new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			frameLayout.addView(setupDataView());
			mRoot = frameLayout;
			containerlayout = (PlayerContainerLayout) mRoot.findViewById(R.id.playcontainer);
			containerlayout.changeLayoutParams();
		} catch (Exception e) {
			Logger.log(e);
		}
		return mRoot;
	}

	@Override
	public void onResume() {
		super.onResume();
		isAuthSuccess = MyApplication.getInstance().isAuthSuccess();
		if (MyApplication.getInstance().isNeedBoss() == 1 && needbuy == 1) {
			getVideoChargeInfo(false);
		}
		if (isAuthSuccess) {
			paymentSuccess();
		} else {// 鉴权成功是不需要展示试看的
			if (showTryandsee) {
				showTryandseeLayout();
			}
		}
		loginInfo = LoginInfoUtil.getLoginInfo(mActivity);
		if (showEndTryandsee && videoChargeInfo != null) {
			switchEndTryandseeLayout(videoChargeInfo);
		}
		setLePaySuccessListener();
		olistener.enable();
		mActivity.registerReceiver(netStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		if (!NetworkUtils.isWifiConnected(mActivity.getApplicationContext())
				&& NetworkUtils.isNetAvailable(mActivity)) {
			boolean notonly = MyApplication.getInstance().getBoolean(MyApplication.NOT_ONLYWIFI);
			if (notonly) {
				mActivity.showToastSafe(R.string.wifi_network, Toast.LENGTH_LONG);
			} else {
				showNetStateDialog();
				return;
			}
		}
		if (surfaceView != null && !player.isPlaying()) {
			containerlayout.dismissAdPic();
			try {
				Field field = BaseVideoView.class.getDeclaredField("isSreenLock");
				field.setAccessible(true);
				field.setBoolean(surfaceView, false);
			} catch (NoSuchFieldException e) {
				Logger.log(e);
			} catch (IllegalAccessException e) {
				Logger.log(e);
			} catch (Exception e) {
				Logger.log(e);
			}
			if(uicontext.isIslogin()==MyApplication.getInstance().isLogin()){
				surfaceView.onResume();
			}else{
				uicontext.setIslogin(MyApplication.getInstance().isLogin());
				mBundle.putString(SaasRequest.SAAS_USERID, LoginInfoUtil.getUserId(mActivity));
				mBundle.putString(SaasRequest.SAAS_UTOKEN, LoginInfoUtil.getToken(mActivity));
				if(uicontext.isPlayingAd()){
					surfaceView.stopAndRelease();
					videolayout.removeAllViews();
					surfaceView = null;
					hideAdlayout();
					uicontext.setIsPlayingAd(false);
					getDefaultHandler().sendEmptyMessage(PlayConst.CONTROL);
					createOnePlayer();
				}else{
					surfaceView.setDataSource(mBundle);
				}
			}
			if (lastPosition > 0) {
				surfaceView.seekTo(lastPosition);
			}
		} else {
			createOnePlayer();
		}

		if (!uicontext.isClickPauseByUser() && !showEndTryandsee) {// 如果展示了试看结束页面，那么不应该resume，resume会导致播放动作

			playTimer.removeMessages(PlayConst.STARTTIME);
			playTimer.sendEmptyMessage(PlayConst.STARTTIME);
		}
		if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()
				&& !uicontext.isClickPauseByUser()) {
			mDanmakuView.resume();
		}
		// 暂停其他播放器播放
		pauseOtherMusic();
	}

	private void switchEndTryandseeLayout(VideoChargeInfo videoChargeInfo) {
		switch (videoChargeInfo.getChargeType()) {
		case 3:// vip
			showVip();
			break;
		case 1:// singleFilm
			showSingle();
			break;
		case 2:// vip or singleFilm
			if (loginInfo != null && loginInfo.getIsVip() == 0) {
				// 直接播放
			} else {
				showVipOrSingle();
			}
			break;

		default:
			break;
		}
	}

	private void setLePaySuccessListener() {
		LepayManager lepayManager = LepayManager.getInstance(mActivity, PlayVideoFragment.this);
		lepayManager.setLePaySuccessListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		MyApplication.getInstance().setAuthSuccess(false);
		if (mDanmakuView != null) {
			// dont forget release!
			mDanmakuView.release();
			mDanmakuView = null;
		}
		if (surfaceView != null) {
			containerlayout.dismissAdPic();
			surfaceView.onDestroy();
			surfaceView.stopAndRelease();
		}
	}

	private BroadcastReceiver netStateReceiver = new BroadcastReceiver() {
		int type = ConnectivityManager.TYPE_WIFI;

		@Override
		public void onReceive(Context context, Intent intent) {
			if (!ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()))
				return;
			boolean nonetwork = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			// 断网后若有网络则重新播放
			if (info != null && msglayout != null
					&& msglayout.findViewById(R.id.play_retry).getVisibility() == View.VISIBLE) {
				retryplay();
			}
			int type = info == null ? ConnectivityManager.TYPE_WIFI : info.getType();

			if (nonetwork || this.type == type)
				return;
			this.type = type;

			if (type == ConnectivityManager.TYPE_WIFI)
				return;
			if (info.getState() != NetworkInfo.State.CONNECTED)
				return;

			if (mDanmakuView != null && mDanmakuView.isPrepared() && !mDanmakuView.isPaused()) {
				mDanmakuView.pause();
			}
			if (surfaceView != null && surfaceView.isPlaying()) {
				surfaceView.onPause();
			}
			boolean notonly = MyApplication.getInstance().getBoolean(MyApplication.NOT_ONLYWIFI);
			if (notonly) {
				mActivity.showToastSafe(R.string.wifi_network, Toast.LENGTH_LONG);
			} else {
				showNetStateDialog();
			}

		}

	};

	@Override
	public void onPause() {
		super.onPause();
		olistener.disable();
		playTimer.removeMessages(PlayConst.STARTTIME);
		getDefaultHandler().removeMessages(PlayConst.NETWORK_TIMEOUT);
		getDefaultHandler().removeMessages(PlayConst.START_DANMAKU);
		getDefaultHandler().removeMessages(PlayConst.HIDECONTROL);
		if (mDanmakuView != null && mDanmakuView.isPrepared() && !mDanmakuView.isPaused()) {
			mDanmakuView.pause();
		}
		MyApplication.getInstance().putInfo("currentPlayVideo", null);// 当前播放视频木有了
		mActivity.unregisterReceiver(netStateReceiver);
		if (surfaceView != null && NetworkUtils.isNetAvailable(mActivity)) {
			lastPosition = showEndTryandsee ? trylooktime * 1000 : player.getCurrentPosition();// 试看结束的话，上次播放位置就是试看时长的位置
			RecordsAPI playRecordsUtil = new RecordsAPI(this);
			String vuid = mBundle.getString(PlayerParams.KEY_PLAY_VUID);
			String nextLinkUrl = null;
			String imageUrl = null;
			String videoTitle = null;
			if (uicontext != null) {
				List<PlayVideoInfo> playList = uicontext.getPlayList();
				nextLinkUrl = getNextLinkUrl(vuid, playList);
				PlayVideoInfo info = (PlayVideoInfo) MyApplication.getInstance().getInfo("currentPlayVideo");
				if (info != null) {
					imageUrl = info.getImageUrl();
					videoTitle = info.getVideoTitle();
				} else {
					imageUrl = mBundle.getString("imageUrl");
					videoTitle = mBundle.getString("videoTitle");
				}
			} else {
				imageUrl = mBundle.getString("imageUrl");
				videoTitle = mBundle.getString("videoTitle");
			}
			PlayedRecordDao dao = MyApplication.getInstance().getDaoByKey(PlayedRecordDao.class.getName());
			PlayRecordInfo t = new PlayRecordInfo();
			t.setVideoId(vuid);
			t.setLastPlayTime(lastPosition / 1000);
			t.setLastOpenTime(lastOpenTime);
			t.setVideoImage(imageUrl);
			t.setAlbumId(mBundle.getString("albumId"));
			t.setVideoTitle(videoTitle);
			t.setNextLinkUrl(nextLinkUrl);
			dao.save(t);
			playRecordsUtil.addRecords(mActivity, vuid, lastPosition / 1000, lastOpenTime, nextLinkUrl);
			surfaceView.onPause();
		}
		// 恢复系统播放
		resumeOtherMusic();
	}

	private String getNextLinkUrl(String vuid, List<PlayVideoInfo> playList) {
		if (!TextUtils.isEmpty(vuid) && playList != null && !playList.isEmpty()) {
			for (int i = 0; i < playList.size() - 1; i++) {
				if (vuid.equals(playList.get(i).getVideoId())) {
					return playList.get(i + 1).getVideoId();
				}
			}
		}
		return null;
	}

	private OrientationEventListener olistener;

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle mBundle = getArguments();
		if (mBundle == null) {
			Toast.makeText(getActivity(), "no data", Toast.LENGTH_LONG).show();
			return;
		}
		olistener = new OrientationEventListener(mActivity) {
			int lastconfig = 0;

			private void change(int orientation) {
				int screenorientation = -1;
				if ((orientation < 290 && orientation > 250) && (lastconfig >= 290 || lastconfig <= 250)) {
					screenorientation = ISplayerController.SCREEN_ORIENTATION_LANDSCAPE;
				} else if ((orientation < 20 || orientation > 340) && (lastconfig >= 20 && lastconfig <= 340)) {
					screenorientation = ISplayerController.SCREEN_ORIENTATION_PORTRAIT;
				} else if ((orientation < 200 && orientation > 160) && (lastconfig <= 160 || lastconfig >= 200)) {
					screenorientation = ISplayerController.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
				} else if ((orientation < 110 && orientation > 70) && (lastconfig >= 110 || lastconfig <= 70)) {
					screenorientation = ISplayerController.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
				}
				lastconfig = orientation;
				if (screenorientation == -1)
					return;
				if (uicontext.isEnableAutoChangeScreen()) {
					helper.changeScreen(mActivity, screenorientation);
				} else {
					uicontext.setEnableAutoChangeScreen(true);
				}
			}

			@Override
			public void onOrientationChanged(int orientation) {
				if (uicontext == null || uicontext.isLockFlag())
					return;

				if (helper == null || orientation == OrientationEventListener.ORIENTATION_UNKNOWN)
					return;
				if (ddialog != null && ddialog.isResumed())
					return;
				synchronized (olistener) {
					change(orientation);
				}

			}
		};

		if (mActivity instanceof PlayVideoActivity) {
			((PlayVideoActivity) mActivity).setOnBackPressedListener(new onBackPressedListener() {

				@Override
				public boolean onBackPressed() {
					if (menulayout != null && menulayout.isShown() && menulayout.getAnimation() == null) {
						getDefaultHandler().sendEmptyMessage(PlayConst.HIDEMENU);
						return true;
					}
					if (helper != null && helper.doBack(mActivity)) {
						return true;
					}
					return false;
				}
			});

			mActivity.setDefaultHandler(bindHandler);
		}
		init(mBundle);
		initDetailPage();
	}

	private Handler bindHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PlayConst.VIDEO_SELECTED:
				showEndTryandsee = false;
				hideTryandseeLayout();
				hideEndTryandsee();
				Bundle bundle = msg.getData();
				onSelectedVideo(bundle);
				break;
			// case PLAY_SHOW_TRYANDSEE:
			// showTryandseeLayout();
			// break;
			case PlayConst.EPISODE_LIST:
				Bundle infoBundle = msg.getData();
				List<PlayVideoInfo> list = (List<PlayVideoInfo>) infoBundle.getSerializable("episodes");

				if (!TextUtils.isEmpty(nextVideoId)) {
					PlayVideoInfo playVideoInfo = uicontext.getNext(mBundle.getString(PlayerParams.KEY_PLAY_VUID),
							list);
					if (playVideoInfo != null) {
						Bundle vodBundle = new Bundle();
						vodBundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_VOD);
						vodBundle.putString(PlayerParams.KEY_PLAY_UUID, MyApplication.getInstance().getTenantId());
						vodBundle.putString(PlayerParams.KEY_PLAY_VUID, playVideoInfo.getVideoId());
						vodBundle.putString("albumId", playVideoInfo.getAlbumId());
						vodBundle.putString("albumName", playVideoInfo.getmAlbumName());
						vodBundle.putString("shareUrl", playVideoInfo.getShareUrl());
						vodBundle.putString("imageUrl", playVideoInfo.getImageUrl());
						vodBundle.putString("videoDesc", playVideoInfo.getVideoDesc());
						vodBundle.putString("videoTitle", playVideoInfo.getVideoTitle());
						onSelectedVideo(vodBundle);
					}
					nextVideoId = null;
				}

				int displaytype = infoBundle.getInt("displayType", 1);
				uicontext.setDisplayType(displaytype);
				String total = infoBundle.getString("totolEpisodes");
				String update = infoBundle.getString("updateEpisode");
				uicontext.setPlayList(list);
				break;
			default:
				break;
			}

		}

	};

	private void onSelectedVideo(Bundle newBundle) {
		if (playRetryVideoChargeInfoLayout.getVisibility() == View.VISIBLE) {
			playRetryVideoChargeInfoLayout.setVisibility(View.GONE);
		}
		ReentrantLock lock = new ReentrantLock();
		try {
			if (lock.tryLock(1, TimeUnit.SECONDS)) {
				if (mDanmakuView.isPrepared()) {
					mDanmakuView.release();
					View danview = controllayout.findViewById(R.id.control_barrage);
					if (danview != null)
						danview.setBackgroundResource(R.drawable.play_barrage);
				}
				playTimer.removeMessages(PlayConst.STARTTIME);
				if (surfaceView != null) {
					surfaceView.stopAndRelease();
				}
				videolayout.removeAllViews();
				surfaceView = null;

				if (uicontext.isPlayingAd()) {
					hideAdlayout();
					uicontext.setIsPlayingAd(false);
					getDefaultHandler().sendEmptyMessage(PlayConst.CONTROL);
				}
				lastPosition = 0;
				this.mBundle = newBundle;
				mBundle.putString(SaasRequest.SAAS_USERID, LoginInfoUtil.getUserId(mActivity));
				mBundle.putString(SaasRequest.SAAS_UTOKEN, LoginInfoUtil.getToken(mActivity));
				mBundle.putString(PlayerParams.KEY_PLAY_BUSINESSLINE, "120");
				mBundle.putString(PlayerParams.KEY_PLAY_PU, "0");
				createOnePlayer();
			}
		} catch (InterruptedException e) {
			Logger.log(e);
		} finally {
			lock.unlock();
		}

	}

	@Override
	protected View setupDataView() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(mActivity).inflate(R.layout.play_main, null);
		videolayout = (ViewGroup) view.findViewById(R.id.play_layout);
		msglayout = view.findViewById(R.id.msg_layout);
		msgView = (TextView) msglayout.findViewById(R.id.play_msg);
		msglayout.findViewById(R.id.play_retry).setOnClickListener(listener);
		controllayout = (RelativeLayout) view.findViewById(R.id.control_layout);
		menulayout = (PlayControlContainer) view.findViewById(R.id.menu_layout);
		tryandseeLayout = view.findViewById(R.id.play_vip_tryandsee);
		playRetryVideoChargeInfoLayout = view.findViewById(R.id.play_retry_videochargeinfo);

		vipOrSingle = view.findViewById(R.id.play_vip_or_single_film);
		vip = view.findViewById(R.id.play_needvip);
		singleFilm = view.findViewById(R.id.play_single_film_not_vip);
		tryandseeTip = view.findViewById(R.id.menu_layout);

		menulayout.setLockTouch(false);

		adlayout = view.findViewById(R.id.ad_layout);
		menuListView = (ListView) menulayout.findViewById(R.id.menulist);
		menulayout.setOnTouchListener(listener);
		initControllayout();
		initTryandseeLayout();
		detailContainer = (FrameLayout) view.findViewById(R.id.detailbody);
		initDanmaku(view);
		return view;
	}

	private void initDanmaku(View view) {
		mDanmakuView = (IDanmakuView) view.findViewById(R.id.play_danmaku);
		// 设置最大显示行数
		HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
		maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示3行
		// 设置是否禁止重叠
		HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
		overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
		overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
		DanmakuGlobalConfig.DEFAULT.setDanmakuStyle(DanmakuGlobalConfig.DANMAKU_STYLE_STROKEN, 3)
				.setDuplicateMergingEnabled(false).setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f)
				// .setCacheStuffer(new SpannedCacheStuffer()) //
				// 图文混排使用SpannedCacheStuffer
				// .setCacheStuffer(new BackgroundCacheStuffer()) //
				// 绘制背景使用BackgroundCacheStuffer
				.setMaximumLines(maxLinesPair).preventOverlapping(overlappingEnablePair);
	}

	private void initControllayout() {
		controllayout.findViewById(R.id.control_play).setOnClickListener(listener);
		if (uicontext != null && uicontext.isClickPauseByUser()) {
			controllayout.findViewById(R.id.control_play).setBackgroundResource(R.drawable.play_play);
		}
		controllayout.findViewById(R.id.control_back).setOnClickListener(listener);
		controllayout.findViewById(R.id.control_switch).setOnClickListener(listener);

		CharSequence position = positionView == null ? "00:00" : positionView.getText();
		CharSequence duration = durationView == null ? "00:00" : durationView.getText();
		positionView = (TextView) controllayout.findViewById(R.id.control_time);
		positionView.setText(position);
		durationView = (TextView) controllayout.findViewById(R.id.control_duration);
		durationView.setText(duration);

		int progress = seekBar == null ? 0 : seekBar.getProgress();
		int secondprogress = seekBar == null ? 0 : seekBar.getSecondaryProgress();
		seekBar = (SeekBar) controllayout.findViewById(R.id.control_seek);
		if (seekBar instanceof PlaySeekBar) {
			Drawable drawable = getResources().getDrawable(R.drawable.play_progressdrawable);
			((PlaySeekBar) seekBar).setMyProgressDrawable(drawable);
			((PlaySeekBar) seekBar).setPositionView(positionView);
		}
		seekBar.setProgress(progress);
		seekBar.setSecondaryProgress(secondprogress);
		seekBar.setOnSeekBarChangeListener(listener);

		titleView = (TextView) controllayout.findViewById(R.id.control_title);
		mTextSeekTo = (TextView) controllayout.findViewById(R.id.tv_seekto);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (titleView != null) {
				titleView.setText(uicontext.getVideoTitle());
			}
			controllayout.findViewById(R.id.control_barrage).setOnClickListener(listener);

			controllayout.findViewById(R.id.control_choose).setOnClickListener(listener);
			controllayout.findViewById(R.id.control_lock).setOnClickListener(listener);
			sendDM = controllayout.findViewById(R.id.control_danmaku);
			sendDM.setOnClickListener(listener);
			if (uicontext.isShowDanmaku()) {
				Button controlBrrage = (Button) controllayout.findViewById(R.id.control_barrage);
				controlBrrage.setBackgroundResource(R.drawable.play_barrage_p);
				// controlBrrage.setText(getResString(R.string.play_barrage));
				// controlBrrage.setTextColor(getResources().getColor(
				// R.color.code1));
				sendDM.setVisibility(View.VISIBLE);
			}
			ratetype = (TextView) controllayout.findViewById(R.id.control_ratetype);
			if (uicontext.getRateTypeItems() != null
					&& uicontext.getRateTypeItemById(uicontext.getCurrentRateType()) != null) {
				ratetype.setText(uicontext.getRateTypeItemById(uicontext.getCurrentRateType()).getName());
			}
			if (uicontext.getPlayList() == null || uicontext.getPlayList().isEmpty()) {
				controllayout.findViewById(R.id.control_choose).setVisibility(View.GONE);
			}
			ratetype.setOnClickListener(listener);
			controllayout.findViewById(R.id.control_more).setOnClickListener(listener);
		}
		if (msglayout.findViewById(R.id.play_retry).getVisibility() == View.VISIBLE) {
			hideControllayout();
		} else {
			getDefaultHandler().sendEmptyMessage(PlayConst.CONTROL);
		}

	}

	void initTryandseeLayout() {
		llPayedToLogin = (LinearLayout) tryandseeLayout.findViewById(R.id.ll_payed_to_login);
		tvToSeeTotal = (TextView) tryandseeLayout.findViewById(R.id.tv_to_see_total);
		tvLoginOrVip = (TextView) tryandseeLayout.findViewById(R.id.tv_was_login_or_vip);
		tvLogin = (TextView) tryandseeLayout.findViewById(R.id.tv_login);
	}

	public void init(Bundle mBundle) {
		nextVideoId = mBundle.getString("nextLinkUrl", null);
		mBundle.remove("nextLinkUrl");
		this.mBundle = mBundle;
		this.mBundle.putString(PlayerParams.KEY_PLAY_BUSINESSLINE, "120");
		this.mBundle.putString(PlayerParams.KEY_PLAY_PU, "0");
		final String uuid = mBundle.getString(PlayerParams.KEY_PLAY_UUID);
		String vuid = mBundle.getString(PlayerParams.KEY_PLAY_VUID);
		PlayedRecordDao dao = MyApplication.getInstance().getDaoByKey(PlayedRecordDao.class.getName());
		PlayRecordInfo info = dao.findPlayRecord(vuid);
		if (info != null) {
			lastPosition = info.getLastPlayTime() * 1000;
		}
		uicontext = new UiPlayContext();
		uicontext.setIslogin(MyApplication.getInstance().isLogin());
		uicontext.setScreenResolution(ISplayerController.SCREEN_ORIENTATION_PORTRAIT);
		if (!NetworkUtils.isWifiConnected(mActivity.getApplicationContext())
				&& NetworkUtils.isNetAvailable(mActivity)) {
			boolean notonly = MyApplication.getInstance().getBoolean(MyApplication.NOT_ONLYWIFI);
			if (!notonly)
				return;
		}
		if (TextUtils.isEmpty(nextVideoId))
			createOnePlayer();
	}

	private NetStateDialog netdialog;

	private void showNetStateDialog() {
		if (netdialog != null && netdialog.isAdded())
			return;
		netdialog = new NetStateDialog();
		controllayout.findViewById(R.id.control_play).setBackgroundResource(R.drawable.play_play);
		netdialog.setOnClickListener(new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				MyApplication.getInstance().putBoolean(MyApplication.NOT_ONLYWIFI, true);
				if (player != null && !uicontext.isClickPauseByUser() && !player.isPlaying()) {
					player.start();
					playTimer.removeMessages(PlayConst.STARTTIME);
					playTimer.sendEmptyMessage(PlayConst.STARTTIME);
				} else {
					createOnePlayer();
				}
				if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()
						&& !uicontext.isClickPauseByUser()) {
					mDanmakuView.resume();
				}

			}
		}, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				MyApplication.getInstance().putBoolean(MyApplication.NOT_ONLYWIFI, false);
			}
		});
		BaseDialog.show(getFragmentManager(), netdialog);
	}

	private View vipOrSingle;
	private View vip;
	private View tryandseeTip;
	private View singleFilm;
	private LoginInfo loginInfo;
	private View tryandseeLayout;
	private TextView tvLogin;
	private TextView tvLoginOrVip;
	private TextView tvToSeeTotal;
	private LinearLayout llPayedToLogin;
	private VideoChargeInfo videoChargeInfo = new VideoChargeInfo();
	private View playRetryVideoChargeInfoLayout;
	private boolean isAuthSuccess;// 鉴权是否成功

	private void surfaceBlack() {
		if (getActivity() != null && surfaceView != null)
			surfaceView.setBackgroundColor(Color.BLACK);
	}

	private void surfaceTransparent() {
		if (getActivity() != null && surfaceView != null) {
			surfaceView.setBackgroundColor(Color.TRANSPARENT);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (uicontext != null && !uicontext.isLockFlag()) {
			// uicontext.setEnableAutoChangeScreen(false);
			if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
					&& uicontext.getScreenResolution() == ISplayerController.SCREEN_ORIENTATION_LANDSCAPE) {
				// if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
				// View decor = mActivity.getWindow().getDecorView();
				// decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				// | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				// | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				// | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav
				// | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
				// | View.SYSTEM_UI_FLAG_IMMERSIVE);
				// }
				WindowManager.LayoutParams attrs = mActivity.getWindow().getAttributes();
				attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
				mActivity.getWindow().setAttributes(attrs);
				mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

				if (mDanmakuView.isPrepared() && uicontext.isShowDanmaku()) {
					mDanmakuView.show();
				}
				changeConfig(newConfig);
			} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT
					&& uicontext.getScreenResolution() == ISplayerController.SCREEN_ORIENTATION_PORTRAIT) {
				// if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
				// View decor = mActivity.getWindow().getDecorView();
				// decor.setSystemUiVisibility(0);
				// }
				WindowManager.LayoutParams attrs = mActivity.getWindow().getAttributes();
				attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
				mActivity.getWindow().setAttributes(attrs);
				mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

				if (mDanmakuView.isPrepared() && mDanmakuView.isShown()) {
					mDanmakuView.hide();
				}
				changeConfig(newConfig);
			}

		}

	}

	private void changeConfig(Configuration newConfig) {
		Logger.i("playvideofragment", "changeConfig");
		containerlayout.removeView(controllayout);
		controllayout = (RelativeLayout) LayoutInflater.from(mActivity).inflate(R.layout.play_controller, null);
		containerlayout.addView(controllayout);
		// if (uicontext.isPlayingAd()) {
		hideTryandseeLayout();
		// }

		if (showEndTryandsee && controllayout != null) {
			hideControllayout();
		}
		initControllayout();
		containerlayout.changeLayoutParams();
		if (surfaceView != null)
			surfaceView.onConfigurationChanged(newConfig);
		if (menulayout.isShown()) {
			getDefaultHandler().sendEmptyMessage(PlayConst.HIDEMENU);
		}
		if (player != null && player.getDuration() > 0) {
			getDefaultHandler().sendEmptyMessage(PlayConst.ONSTART);
		}

	}
	String TEST_URL = "https://aliuwmp3.changba.com/userdata/video/EA33E207EF0659039C33DC5901307461.mp4";
	/**
	 * 创建一个新的播放器
	 * 
	 * @param holder
	 */
	void createOnePlayer() {
		if (showEndTryandsee) {// 展示试看页面的时候，不用播放
			return;
		}
		if (!NetworkUtils.isWifiConnected(mActivity.getApplicationContext())
				&& NetworkUtils.isNetAvailable(mActivity)) {
			boolean notonly = MyApplication.getInstance().getBoolean(MyApplication.NOT_ONLYWIFI);
			if (notonly) {
				mActivity.showToastSafe(R.string.wifi_network, Toast.LENGTH_LONG);
			} else {
				showNetStateDialog();
				return;
			}

		}
		if (surfaceView == null) {
			surfaceView = new SaasVideoView(mActivity);
			surfaceView.setKeepScreenOn(true);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
			videolayout.addView(surfaceView, params);
			surfaceView.setVideoViewListener(videoViewListener);
//			surfaceView.setDataSource(mBundle);
			surfaceView.setDataSource(TEST_URL);
			player = surfaceView.getPlayer();
			helper = new PlayHelper(this, uicontext, player, playTimer);
			lastOpenTime = new Date().getTime();
		} else {

//			surfaceView.setDataSource(mBundle);
			surfaceView.setDataSource(TEST_URL);
			if (lastPosition > 0) {
				surfaceView.seekTo(lastPosition);
			}
			player = surfaceView.getPlayer();
			helper = new PlayHelper(this, uicontext, player, playTimer);
			lastOpenTime = new Date().getTime();
		}
		surfaceBlack();
	}

	protected void initDetailPage() {
		int playMode = mBundle.getInt(PlayerParams.KEY_PLAY_MODE, -1);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		switch (playMode) {
		case PlayerParams.VALUE_PLAYER_VOD:
			if (TextUtils.isEmpty(nextVideoId)) {// meiyouxiayiji
				mBundle.putString("videoId", mBundle.getString(PlayerParams.KEY_PLAY_VUID));
			} else {
				mBundle.putString("videoId", nextVideoId);
			}
			Fragment vodFragment = Fragment.instantiate(mActivity, PlayVodFragment.class.getName());
			vodFragment.setArguments(mBundle);
			transaction.replace(R.id.detailbody, vodFragment, PlayConst.VODFRAGMENTTAG);
			transaction.commit();
			break;
		case PlayerParams.VALUE_PLAYER_ACTION_LIVE:
			mBundle.putString("videoId", mBundle.getString(PlayerParams.KEY_PLAY_ACTIONID));
			Fragment liveFragment = Fragment.instantiate(mActivity, PlayLiveFragment.class.getName());
			liveFragment.setArguments(mBundle);
			transaction.replace(R.id.detailbody, liveFragment, "PlayLiveFragment");
			transaction.commit();
			break;
		}
	}

	private void pauseOtherMusic() {
		if (Build.VERSION.SDK_INT < 8) {
			return;
		}
		AudioManager mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
		if (mAudioManager != null) {
			try {
				mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC,
						AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
			} catch (Exception ex) {
			}
		}
	}

	private void resumeOtherMusic() {
		if (Build.VERSION.SDK_INT < 8) {
			return;
		}
		AudioManager mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
		if (mAudioManager != null) {
			try {
				mAudioManager.abandonAudioFocus(null);
			} catch (Exception ex) {
			}
		}
	}

	class BossOnClickListener implements OnClickListener {
		/**
		 * if需要登录，表示登录后的意图 <br>
		 * if已登录，表示当前点击事件的意图 <br>
		 * 对于有的view来说，flag可能无用
		 */
		int flag;
		boolean loginFirst;// 是否需要先登录
		boolean isPlay;// 会员付费后是否播放

		public BossOnClickListener(boolean loginFirst, int flag, boolean isplay) {
			this.loginFirst = loginFirst;
			this.flag = flag;
			this.isPlay = isplay;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_login:
				BossLoginAPI.startLogin(mActivity, flag, true);
				break;
			case R.id.tv_get_vip:
				if (loginFirst) {
					BossLoginAPI.startLogin(mActivity, flag, true);
				} else {
					MyApplication.getInstance().getBossManager().switchAim(flag, isPlay);
				}
				break;
			case R.id.tv_pay:
				if (loginFirst) {
					BossLoginAPI.startLogin(mActivity, flag, true);
				} else {
					MyApplication.getInstance().getBossManager().switchAim(flag, isPlay);
				}
				break;
			case R.id.tv_to_see_total:
				if (loginFirst) {
					BossLoginAPI.startLogin(mActivity, flag, true);
				} else {
					MyApplication.getInstance().getBossManager().switchAim(flag, isPlay);
				}
				break;
			case R.id.control_back:
				if (helper == null)
					mActivity.finish();
				else if (!helper.doBack(mActivity)) {
					mActivity.finish();
				}
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 支付或者鉴权成功
	 */
	@Override
	public void paymentSuccess() {
		showEndTryandsee = false;
		hideTryandseeLayout();
		vip.setVisibility(View.GONE);
		vipOrSingle.setVisibility(View.GONE);
		singleFilm.setVisibility(View.GONE);
		mBundle.putString(SaasRequest.SAAS_USERID, LoginInfoUtil.getUserId(mActivity));
		mBundle.putString(SaasRequest.SAAS_UTOKEN, LoginInfoUtil.getToken(mActivity));
		videolayout.removeAllViews();
		surfaceView = null;
		if (uicontext.isPlayingAd()) {
			hideAdlayout();
			uicontext.setIsPlayingAd(false);
			getDefaultHandler().sendEmptyMessage(PlayConst.CONTROL);
		}
		createOnePlayer();

	}
}
