package com.letv.autoapk.ui.player;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.DanmakuFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.DensityUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lecloud.sdk.api.md.entity.action.ActionInfo;
import com.lecloud.sdk.api.md.entity.live.LiveInfo;
import com.lecloud.sdk.api.md.entity.live.Stream;
import com.lecloud.sdk.api.timeshift.ItimeShiftListener;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.constant.StatusCode;
import com.lecloud.sdk.player.IAdPlayer;
import com.lecloud.sdk.player.IMediaDataPlayer;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.letv.android.client.cp.sdk.player.live.CPActionLivePlayer;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.activity.PlayVideoActivity;
import com.letv.autoapk.base.activity.PlayVideoActivity.onBackPressedListener;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.base.dialog.BaseDialog;
import com.letv.autoapk.base.fragment.BaseFragment;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.player.ISplayerController;
import com.letv.autoapk.player.SaasActionVideoView;
import com.letv.autoapk.ui.login.LoginAPI;
import com.letv.autoapk.ui.player.DanmakuLiveClient;
import com.letv.autoapk.ui.player.DanmakuLiveDialog;
import com.letv.autoapk.ui.player.MyParser;
import com.letv.autoapk.ui.player.NetStateDialog;
import com.letv.autoapk.ui.player.PlayConst;
import com.letv.autoapk.ui.player.PlayHelper;
import com.letv.autoapk.ui.player.PlayMultiHelper;
import com.letv.autoapk.ui.player.RateTypeItem;
import com.letv.autoapk.ui.player.UiPlayContext;
import com.letv.autoapk.ui.player.DanmakuLiveClient.ConnectionListener;
import com.letv.autoapk.ui.player.PlayMultiHelper.SwitchMultLiveCallback;
import com.letv.autoapk.ui.player.view.PlayControlContainer;
import com.letv.autoapk.ui.player.view.PlaySeekBar;
import com.letv.autoapk.ui.player.view.PlayerContainerLayout;
import com.letv.autoapk.ui.tvlive.PlayTVConst;
import com.letv.autoapk.ui.tvlive.PlayTVLiveFragment;
import com.letv.autoapk.ui.tvlive.PlayTvItemInfo;
import com.letv.autoapk.utils.NetworkUtils;
import com.letv.autoapk.utils.SystemUtls;
import com.letv.autoapk.utils.TimerUtils;

public class PlayTVLiveVideoFragment extends BaseFragment {
	private static final String TAG = PlayTVLiveVideoFragment.class.getSimpleName();
	private DanmakuLiveDialog ddialog;
	private boolean istouch;
	VideoViewListener videoViewListener = new VideoViewListener() {

		@Override
		public void onStateResult(int state, Bundle bundle) {
			if (player == null) {
				return;
			}
			switch (state) {
			case PlayerEvent.PLAY_LOADINGSTART:
				showBufferMsg(getResString(R.string.loading_pls_wait));
				break;
			case PlayerEvent.PLAY_PREPARED:
				// 获取当前播放的码率
				hideMsg();
				player.start();
				lastOpenTime = new Date().getTime();
				getDefaultHandler().sendEmptyMessage(PlayConst.ONSTART);
				if (MyApplication.getInstance().getBoolean(PlayConst.DANMAKUENABLE) && !mDanmakuView.isPrepared()) {
					View danview = controllayout.findViewById(R.id.control_barrage);
					if (danview != null) {
						danview.setBackgroundResource(R.drawable.play_barrage_p);
						((Button) danview).setText(getResString(R.string.play_barrage));
						((Button) danview).setTextColor(getResources().getColor(R.color.code01));
					}

					uicontext.setShowDanmaku(true);
					getDefaultHandler().sendEmptyMessage(PlayConst.START_DANMAKU);
					if (sendDM != null)
						sendDM.setVisibility(View.VISIBLE);
				}
				break;
			case PlayerEvent.MEDIADATA_LIVE:// 所有机位信息回调回来
				int livestatusCode = bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE, 0);
				if (livestatusCode == StatusCode.MEDIADATA_SUCCESS) {
					Map<String, String> ratetypes = new LinkedHashMap<String, String>();
					LiveInfo liveInfo = bundle.getParcelable(PlayerParams.KEY_RESULT_DATA);
					if (uicontext != null) {
						uicontext.setMultCurrentLiveId(liveInfo.getLiveId());
						uicontext.setEnableTimeShift(liveInfo.enableTimeShift());
						if (uicontext.isEnableTimeShift()) {
							seekBar.setVisibility(View.VISIBLE);
							durationView.setVisibility(View.VISIBLE);
							seekBarbg.setVisibility(View.VISIBLE);
						} else {
							seekBar.setVisibility(View.INVISIBLE);
							durationView.setVisibility(View.INVISIBLE);
							seekBarbg.setVisibility(View.INVISIBLE);
						}
					}
					List<Stream> mStreams = liveInfo.getStreams();
					for (Stream stream : mStreams) {
						ratetypes.put(stream.getRateType(), stream.getRateName());
					}
					uicontext.setRateTypeItems(ratetypes);
					String lastRate = ((IMediaDataPlayer) player).getLastRate();
					uicontext.setCurrentRateType(lastRate);
					if (ratetype != null && uicontext.getRateTypeItemById(uicontext.getCurrentRateType()) != null) {
						ratetype.setText(uicontext.getRateTypeItemById(uicontext.getCurrentRateType()).getName());
					}
				} else {
					String errormsg = bundle.getString(PlayerParams.KEY_RESULT_ERROR_MSG);
					int errorCode = bundle.getInt(PlayerParams.KEY_RESULT_ERROR_CODE);
					if (errormsg != null && errormsg.contains("gpc")) {
						showErrorMsg(getResString(R.string.player_proxy_error) + errorCode);
					} else {
						showErrorMsg(getResString(R.string.player_proxy_error) + errorCode + "\n" + errormsg);
					}
				}
				break;
			case SaasActionVideoView.NO_LIVE_STREAM:
				showErrorMsg(getResString(R.string.media_not_exist));
				break;
			case PlayerEvent.MEDIADATA_ACTION:// 创建活动的信息回调
				if (SystemUtls.isNetworkConnected(mActivity.getApplicationContext())) {
					int statusCode = bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE, 0);
					if (statusCode == StatusCode.MEDIADATA_SUCCESS) {
						ActionInfo actionInfo = bundle.getParcelable(PlayerParams.KEY_RESULT_DATA);
						if (actionInfo != null && actionInfo.getLiveInfos() != null
								&& actionInfo.getLiveInfos().size() > 1) {
							uicontext.setActionInfo(actionInfo);
							multiHelper.setSwitchMultLiveCallbackk(new SwitchMultLiveCallback() {

								@Override
								public void switchMultLive(String liveId) {
									player.setDataSourceByLiveId(liveId);

								}
							});
						} else {
							multilayout.findViewById(R.id.multi_control).setVisibility(View.GONE);
						}
					} else {
						String errormsg = bundle.getString(PlayerParams.KEY_RESULT_ERROR_MSG);
						int errorCode = bundle.getInt(PlayerParams.KEY_RESULT_ERROR_CODE);
						if (errormsg != null && errormsg.contains("gpc")) {
							showErrorMsg(getResString(R.string.player_proxy_error) + errorCode);
						} else {
							showErrorMsg(getResString(R.string.player_proxy_error) + errorCode + "\n" + errormsg);
						}
						multilayout.findViewById(R.id.multi_control).setVisibility(View.GONE);
					}
					return;
				}
				showErrorMsg(getResString(R.string.network_connected_failed));
				multilayout.findViewById(R.id.multi_control).setVisibility(View.GONE);
				break;
			case PlayerEvent.PLAY_ERROR:
				if (!SystemUtls.isNetworkConnected(mActivity.getApplicationContext())) {
					showErrorMsg(getResString(R.string.network_connected_failed));
					return;
				}
				int errorCode = bundle.getInt(PlayerParams.KEY_RESULT_ERROR_CODE, 0);
				if (errorCode == StatusCode.PLAY_ERROR_DECODE)
					showErrorMsg(getResString(R.string.media_decoding_error));
				if (errorCode == StatusCode.PLAY_ERROR_UNKNOWN) {
					showErrorMsg(getResString(R.string.media_not_exist));
				}
				break;
			case PlayerEvent.PLAY_INFO:
				int statusCode = bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE, 0);
				if (statusCode == StatusCode.PLAY_INFO_VIDEO_RENDERING_START) {
					if (uicontext.isClickPauseByUser()) {
						surfaceView.onPause();
					}
				}
				if (statusCode == StatusCode.PLAY_INFO_BUFFERING_START) {
					showBufferMsg(getResString(R.string.buffering));
				}
				if (statusCode == StatusCode.PLAY_INFO_BUFFERING_END) {
					hideMsg();
				}
				break;
			case PlayerEvent.PLAY_COMPLETION:
				if (!uicontext.isLockFlag())
					getDefaultHandler().sendEmptyMessage(PlayConst.SHOWCONTROL);
				showErrorMsg(getResString(R.string.play_liveend));
				break;
			case PlayerEvent.AD_START:
				uicontext.setIsPlayingAd(true);// 广告播放时间
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
				if (adTime != null && time > 0)
					adTime.setText(String.valueOf(time).concat(getResString(R.string.play_seconds_text)));
				break;
			case PlayerEvent.MEDIADATA_GET_PLAYURL:
				int code = bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE, 0);
				if (code == StatusCode.MEDIADATA_SERVER_ERROR) {
					showErrorMsg(getResString(R.string.media_playurl_server_error)
							+ bundle.getInt(PlayerParams.KEY_HTTP_CODE, 0));
				} else if (code == StatusCode.MEDIADATA_NETWORK_ERROR) {
					showErrorMsg(getResString(R.string.media_playurl_network_error)
							+ bundle.getInt(PlayerParams.KEY_HTTP_CODE, 0));
				}
				break;
			}

		}

		@Override
		public String onGetVideoRateList(LinkedHashMap<String, String> arg0) {
			return null;
		}

	};

	class PlayListener implements OnClickListener, OnSeekBarChangeListener, OnItemClickListener, OnTouchListener,
			ConnectionListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.control_danmaku:
				if (MyApplication.getInstance().isLogin() == false) {
					mActivity.showToastSafe(getResString(R.string.not_login), Toast.LENGTH_SHORT);
					LoginAPI.stratLogin(mActivity);
					return;
				}
				if (player == null || !uicontext.isShowDanmaku())
					return;
				if (ddialog == null)
					ddialog = new DanmakuLiveDialog();
				ddialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						String textString = ddialog.getSendText();
						if (textString != null && danmakuLiveClient.isOpen()) {
							JSONObject jsonObject = new JSONObject();
							try {
								long htime = 10000;
								if (beginTime != 0) {
									htime = System.currentTimeMillis() - beginTime;
								}
								jsonObject.put("videoId", mBundle.getString(PlayerParams.KEY_PLAY_ACTIONID))
										.put("albumId", "").put("content", textString)
										.put("title", mBundle.getString("videoTitle")).put("clientType", 2)
										.put("htime", String.valueOf(htime))
										.put("tenantid", MyApplication.getInstance().getTenantId())
										.put("token", LoginInfoUtil.getToken(mActivity)).put("type", "live");
							} catch (JSONException e) {
								Logger.log(e);
							}
							danmakuLiveClient.send(jsonObject.toString());
						}
					}
				});
				BaseDialog.show(getFragmentManager(), ddialog);
				break;
			case R.id.control_backtolive:
				hideMsg();
				player.retry();

				// seekBar.setProgress(seekBar.getMax());
				uicontext.setCurrentTimeShirtProgress(0);
				v.setVisibility(View.GONE);
				positionView.setVisibility(View.GONE);
				break;
			case R.id.control_lock:
				if (uicontext.isLockFlag()) {
					uicontext.setLockFlag(false);
					GestureControl gestureControl = new GestureControl(mActivity, controllayout, getDefaultHandler());
					controllayout.setOnTouchListener(gestureControl);
					multiHelper.setLock(false);
					getDefaultHandler().sendEmptyMessage(PlayConst.SHOWCONTROL);
					((ImageView) v).setImageResource(R.drawable.play_lock_off);
					mActivity.showToastSafe(getResString(R.string.unlock), Toast.LENGTH_SHORT);
				} else {
					uicontext.setLockFlag(true);
					multiHelper.setLock(true);
					controllayout.setOnTouchListener(listener);
					((ImageView) v).setImageResource(R.drawable.play_lock);
					getDefaultHandler().sendEmptyMessage(PlayConst.HIDECONTROL);
					mActivity.showToastSafe(R.string.locked, Toast.LENGTH_SHORT);
				}

				break;
			case R.id.multi_control:
				if (multiHelper == null)
					return;
				if (multiHelper.isShown()) {
					multiHelper.hide();
				} else {
					multiHelper.show();
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
					helper.playOrpause(v);
				} else if (!NetworkUtils.isWifiConnected(mActivity.getApplicationContext())
						&& NetworkUtils.isNetAvailable(mActivity)) {
					boolean notonly = MyApplication.getInstance().getBoolean(MyApplication.NOT_ONLYWIFI);
					if (notonly) {
						mActivity.showToastSafe(R.string.wifi_network, Toast.LENGTH_LONG);
					} else {
						if (surfaceView != null) {
							showNetStateDialog();
						}
						return;
					}

				}
				if (backtolive != null && backtolive.isShown())
					backtolive.setVisibility(View.GONE);
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
				break;
			case R.id.ad_skip:
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
					BaseAdapter adapter = helper.initmenu(PlayHelper.SHARE, mActivity, mBundle);
					if (adapter == null)
						return;
					menuListView.setAdapter(adapter);
					getDefaultHandler().sendEmptyMessage(PlayConst.SHOWMENU);
				}

				break;
			case R.id.control_barrage:
				// if (MyApplication.getInstance().isLogin() == false) {
				// mActivity.showToastSafe("您还没有登录", Toast.LENGTH_SHORT);
				// LoginAPI.stratLogin(mActivity);
				// return;
				// }
				if (uicontext != null) {
					if (mDanmakuView.isPrepared() == false && uicontext.isShowDanmaku() == false) {
						v.setBackgroundResource(R.drawable.play_barrage_p);
						uicontext.setShowDanmaku(true);
						getDefaultHandler().sendEmptyMessage(PlayConst.START_DANMAKU);
						sendDM.setVisibility(View.VISIBLE);
					} else if (uicontext.isShowDanmaku()) {
						v.setBackgroundResource(R.drawable.play_barrage);
						uicontext.setShowDanmaku(false);
						mDanmakuView.hide();
						sendDM.setVisibility(View.INVISIBLE);
					} else {
						v.setBackgroundResource(R.drawable.play_barrage_p);
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
		public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) positionView.getLayoutParams();
			params.leftMargin = (seekBar.getWidth() - positionView.getWidth()) * progress / seekbar.getMax();
			positionView.requestLayout();
			if (seekBar instanceof PlaySeekBar) {
				((PlaySeekBar) seekBar).doRefresh(progress);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			if (istouch == true)
				return;
			istouch = true;
			mprogress = seekBar.getProgress();
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (istouch == false)
				return;
			istouch = false;
			if (player != null) {// 只有在播放的时候才允许seek
				int progress = seekBar.getProgress();
				long seekTime = 0;
				if (positionTime == 0 || serverTime == 0) {
					return;
				}
				long gapTime = (long) ((positionTime - serverTime) * 0.001);
				int gapProgress = progress - mprogress;

				seekTime = gapTime + progress - mprogress;
				if (seekTime >= 0) {
					seekBar.setProgress(seekBar.getMax());
					uicontext.setCurrentTimeShirtProgress(0);
					seekTime = MIN_SEEKTIME_BUFFER;
					player.seekTimeShift(serverTime);
					Toast.makeText(getContext(), R.string.alreadynow, Toast.LENGTH_SHORT).show();
					return;
				}
				if (seekTime > -60) {
					seekBar.setProgress(MAX);
				} else if (seekTime >= -600) {// 回到直播取临界点
					seekBar.setProgress(MAX + (int) seekTime);
				} else if (gapProgress < 0 && progress - MAX < -6600) {// 往前时移
					seekBar.setProgress((int) (seekBar.getMax() * 0.5));
				} else if (gapProgress > 0 && progress > 6600) {
					seekBar.setProgress((int) (seekBar.getMax() * 0.5));
				} else {
					setLiveSeekBarProgress(seekBar, progress);
				}
				uicontext.setCurrentTimeShirtProgress(seekBar.getProgress());// 进入时移模式

				Log.i(TAG, "[seekTime] seekTime:" + seekTime + ",gapTime:" + gapTime);
				player.seekTimeShift(serverTime + seekTime * 1000);
				if (!player.isPlaying()) {
					player.start();
				}
			} else {
				seekBar.setProgress(mprogress);
			}
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			int menuid = (Integer) parent.getTag();

			if (menuid == R.id.control_ratetype) {
				String ratetypeid = (String) view.getTag();
				if (ratetypeid != null) {
					long current = player.getCurrentPosition();
					player.setDataSourceByRate(ratetypeid);
					player.seekTo(current);
					ratetype.setText(uicontext.getRateTypeItems().get(position).getName());
					uicontext.setCurrentRateType(ratetypeid);
					MyApplication.getInstance().putString(PlayConst.DEFINATIONTYPE, ratetypeid);
				}
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

		@Override
		public void onTimeOut() {
			getDefaultHandler().sendEmptyMessageDelayed(PlayConst.OPEN_DANMAKUCLIENT, 3000);
		}

	}

	void hideMsg() {
		msglayout.setVisibility(View.INVISIBLE);
	}

	private void setRateType(RateTypeItem item) {
		if (ratetype == null)
			return;
		String name = item.getName();
		if (TextUtils.isEmpty(name)) {
			ratetype.setVisibility(View.GONE);
		} else {
			ratetype.setText(name);
			ratetype.setVisibility(View.VISIBLE);
		}

	}

	void showBufferMsg(String msg) {
		View wheel = msglayout.findViewById(R.id.progress_wheel);
		if (!wheel.isShown()) {
			wheel.setVisibility(View.VISIBLE);
			msgView.setText(msg);
			msglayout.setVisibility(View.VISIBLE);
		}

	}

	void showErrorMsg(String msg) {
		msglayout.findViewById(R.id.progress_wheel).setVisibility(View.GONE);
		msgView.setText(msg);
		msglayout.setVisibility(View.VISIBLE);
	}

	private void initOldAdtime() {
		adTime = new TextView(mActivity);
		adTime.setId(R.id.ad_skip);
		adTime.setBackgroundColor(Color.parseColor("#89000000"));
		adTime.setTextColor(getResources().getColor(R.color.code1));
		adTime.setTextSize(13);
		adTime.setGravity(Gravity.CENTER_VERTICAL);
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
			adlayout.findViewById(R.id.ad_skiplayout).setOnClickListener(listener);
		} else {
			View view = adlayout.findViewById(R.id.ad_skiplayout);
			if (view != null) {
				((FrameLayout) adlayout).removeView(view);
				initOldAdtime();
			}

		}
		// adTime.setOnClickListener(listener);
		adlayout.findViewById(R.id.ad_zoom).setOnClickListener(listener);
	}

	void hideAdlayout() {
		adlayout.setVisibility(View.GONE);
	}

	void showMenulayout() {
		menulayout.show();
		menulayout.bringToFront();
	}

	void hideMenulayout() {
		menulayout.hide();
	}

	void showControllayout() {
		GestureControl gestureControl = new GestureControl(mActivity, controllayout, getDefaultHandler());
		gestureControl.setSeekable(true);
		controllayout.setVisibility(View.VISIBLE);
		controllayout.setOnTouchListener(gestureControl);
	}

	void hideControllayout() {
		controllayout.setVisibility(View.GONE);
		controllayout.setOnTouchListener(null);
	}

	private void openDanmakuClient() {
		String uri = DanmakuLiveClient.WBHOST + "?dm_session_id=" + mBundle.getString(PlayerParams.KEY_PLAY_ACTIONID);
		URI serverURI = URI.create(uri);
		danmakuLiveClient = new DanmakuLiveClient(serverURI, getDefaultHandler());
		danmakuLiveClient.connect();
		danmakuLiveClient.setConnectionListener(listener);
	}

	@Override
	protected void onHandleMessage(Message msg) {
		int what = msg.what;
		switch (what) {
		case PlayConst.OPEN_DANMAKUCLIENT:
			openDanmakuClient();
			break;
		case PlayConst.GOT_DANMAKU:
			String json = (String) msg.obj;
			try {
				JSONObject jsonObject = new JSONObject(json);
				String content = jsonObject.getString("content");
				String error = jsonObject.optString("errorInfo", "");
				boolean isself = jsonObject.optBoolean("selfSend", false);
				BaseDanmaku danmaku = DanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
				if (danmaku == null || mDanmakuView == null || !TextUtils.isEmpty(error)) {
					return;
				}
				danmaku.text = content;
				danmaku.padding = 0;
				danmaku.priority = 0; // 可能会被各种过滤器过滤并隐藏显示
				danmaku.isLive = true;
				danmaku.time = mDanmakuView.getCurrentTime() + 1000;
				danmaku.textSize = 28f * (DensityUtil.getDensity() - 0.6f);
				if (isself) {
					danmaku.textColor = Color.WHITE;
				} else {
					danmaku.textColor = MyParser.getRandomColor();
				}
				danmaku.textShadowColor = Color.BLACK;
				mDanmakuView.addDanmaku(danmaku);
			} catch (Exception e) {
				Logger.log(e);
			}
			break;
		case PlayConst.START_DANMAKU:
			if (mDanmakuView.isPrepared())
				break;
			BaseDanmakuParser parser = new BaseDanmakuParser() {

				@Override
				protected IDanmakus parse() {
					return new Danmakus();
				}
			};
			mDanmakuView.setCallback(new DrawHandler.Callback() {
				@Override
				public void updateTimer(DanmakuTimer timer) {

				}

				@Override
				public void prepared() {
					mDanmakuView.start();
					if (uicontext.getScreenResolution() == ISplayerController.SCREEN_ORIENTATION_PORTRAIT)
						mDanmakuView.hide();
				}
			});
			mDanmakuView.prepare(parser);
			mDanmakuView.showFPS(false);
			mDanmakuView.enableDanmakuDrawingCache(false);
			openDanmakuClient();

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
				} else if (child.getId() == R.id.control_lock) {
					child.setVisibility(isshow ? View.INVISIBLE : View.VISIBLE);
				} else if (child.getId() == R.id.control_danmaku) {
					if (uicontext != null && uicontext.isShowDanmaku())
						child.setVisibility(isshow ? View.INVISIBLE : View.VISIBLE);
				}
			}
			break;
		case PlayConst.HIDECONTROL:
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
			if (uicontext.isPlayingAd() || menulayout.isShown()) {
				hideControllayout();
			} else {
				showControllayout();
				getDefaultHandler().sendEmptyMessageDelayed(PlayConst.HIDECONTROL, 4000);
			}

			break;
		case PlayConst.AD:
			if (uicontext.isPlayingAd()) {
				showAdlayout();
			} else {
				hideAdlayout();
			}
			break;
		case PlayConst.ONSTART:
			if (uicontext.isEnableTimeShift())
				seekBar.setVisibility(View.VISIBLE);
			else
				seekBar.setVisibility(View.INVISIBLE);
			if (player.isPlaying() && !uicontext.isClickPauseByUser()) {
				controllayout.findViewById(R.id.control_play).setBackgroundResource(R.drawable.play_pause);
			} else {
				controllayout.findViewById(R.id.control_play).setBackgroundResource(R.drawable.play_play);
			}

			break;
		case PlayConst.SEEKEND:
			listener.onStopTrackingTouch(seekBar);
			getDefaultHandler().sendEmptyMessageDelayed(PlayConst.HIDECONTROL, 4000);
			break;
		case PlayConst.SEEKSTART:
			listener.onStartTrackingTouch(seekBar);
			getDefaultHandler().sendEmptyMessage(PlayConst.SHOWCONTROL);

			break;
		case PlayConst.SEEKTO:
			if (seekBar != null) {
				seekBar.setProgress(this.mprogress + msg.arg1 * seekBar.getMax() / 1000);
			}
			mTextSeekTo.setText(getSeekToTime());
			break;
		default:
			break;
		}

	}

	public String getSeekToTime() {
		if (serverTime == 0 || seekBar == null) {
			return "";
		}
		long currentPosition = positionTime + (seekBar.getProgress() - mprogress) * 1000;
		String time = TimerUtils.timeToDate(currentPosition) + "";
		return time;
	}

	private long positionTime;
	private long beginTime;
	private long serverTime;
	private static final int MIN_SEEKTIME_BUFFER = -5;
	private static final int MAX = 3600 * 2;
	private static final int MAX_2 = 7200;
	private static final int HOURS_2_SECOND = 2 * 60 * 60 * 1000;
	private long betweenTime;
	private int mprogress = 0;
	private PlayListener listener = new PlayListener();
	private PlayHelper helper;
	private Bundle mBundle;
	private boolean isPanoVideo;
	// private PanoRenderer panoRenderer;
	private UiPlayContext uicontext;
	private CPActionLivePlayer player;
	private PlayMultiHelper multiHelper;

	private SeekBar seekBar;
	private ViewGroup videolayout;
	private View msglayout;
	private View adlayout;
	private RelativeLayout controllayout;
	private PlayControlContainer menulayout;
	private SaasActionVideoView surfaceView;
	private TextView positionView;
	private TextView durationView;
	private TextView titleView;
	private TextView mTextSeekTo;
	private TextView msgView;
	private FrameLayout detailContainer;
	private PlayControlContainer multilayout;
	private View backtolive;
	private View seekBarbg;
	// private LinearLayout multicontent;
	// private Button multicontrol;
	private IDanmakuView mDanmakuView;
	private DanmakuLiveClient danmakuLiveClient;
	private View sendDM;
	/** 上次打开时间 */
	private long lastOpenTime;
	private String vuid;
	private TextView ratetype;
	private ListView menuListView;
	private TextView adTime;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		try {
			FrameLayout frameLayout = new FrameLayout(mActivity);
			frameLayout.setLayoutParams(
					new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			frameLayout.addView(setupDataView());
			mRoot = frameLayout;
			PlayerContainerLayout viewGroup = (PlayerContainerLayout) mRoot.findViewById(R.id.playcontainer);
			viewGroup.changeLayoutParams();
		} catch (Exception e) {
			Logger.log(e);
		}
		return mRoot;
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
				mActivity.showToastSafe(getResString(R.string.wifi_network), Toast.LENGTH_LONG);
			} else {
				showNetStateDialog();
			}

		}

	};

	@Override
	public void onResume() {
		super.onResume();
		olistener.enable();
		mActivity.registerReceiver(netStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		if (!NetworkUtils.isWifiConnected(mActivity.getApplicationContext())
				&& NetworkUtils.isNetAvailable(mActivity)) {
			boolean notonly = MyApplication.getInstance().getBoolean(MyApplication.NOT_ONLYWIFI);
			if (notonly) {
				mActivity.showToastSafe(getResString(R.string.wifi_network), Toast.LENGTH_LONG);
			} else {
				showNetStateDialog();
				return;
			}

		}
		if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
			mDanmakuView.resume();
			openDanmakuClient();
		}
		if (surfaceView != null && !player.isPlaying()) {
			surfaceView.onResume();
			if (uicontext.isPlayingAd() && adTime != null)
				adTime.setText("");
			if (uicontext.isClickPauseByUser()) {
				controllayout.findViewById(R.id.control_play).setBackgroundResource(R.drawable.play_play);
			} else {
				controllayout.findViewById(R.id.control_play).setBackgroundResource(R.drawable.play_pause);
			}

		}

	}

	@Override
	public void onPause() {
		super.onPause();
		olistener.disable();
		if (mDanmakuView != null && mDanmakuView.isPrepared()) {
			mDanmakuView.pause();
			danmakuLiveClient.close();
		}
		getDefaultHandler().removeMessages(PlayConst.OPEN_DANMAKUCLIENT);
		getDefaultHandler().removeMessages(PlayConst.HIDECONTROL);
		if (surfaceView != null) {
			surfaceView.onPause();
		}
		mActivity.unregisterReceiver(netStateReceiver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mDanmakuView != null) {
			// dont forget release!
			mDanmakuView.release();
			mDanmakuView = null;
		}
		if (danmakuLiveClient != null && !danmakuLiveClient.isClosed()) {
			danmakuLiveClient.close();
			danmakuLiveClient = null;
		}
		if (multiHelper != null) {
			multiHelper.onDestroy();
		}
		if (surfaceView != null) {
			surfaceView.onDestroy();
		}
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
		init(mBundle);
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
				if (uicontext.isEnableAutoChangeScreen())
					helper.changeScreen(mActivity, screenorientation);
				else {
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
		}
		mActivity.setDefaultHandler(bindHandler);
		initDetailPage();
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

	@Override
	protected View setupDataView() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(mActivity).inflate(R.layout.play_livemain, null);
		videolayout = (ViewGroup) view.findViewById(R.id.play_layout);
		msglayout = view.findViewById(R.id.msg_layout);
		msgView = (TextView) msglayout.findViewById(R.id.play_msg);
		controllayout = (RelativeLayout) view.findViewById(R.id.control_layout);
		menulayout = (PlayControlContainer) view.findViewById(R.id.menu_layout);
		menulayout.setLockTouch(false);
		menulayout.setOnTouchListener(listener);
		adlayout = view.findViewById(R.id.ad_layout);
		menuListView = (ListView) menulayout.findViewById(R.id.menulist);
		multilayout = (PlayControlContainer) view.findViewById(R.id.multi_layout);
		multilayout.findViewById(R.id.multi_control).setOnClickListener(listener);
		initControllayout();
		detailContainer = (FrameLayout) view.findViewById(R.id.detailbody);
		initDanmaku(view);
		return view;
	}

	private void initControllayout() {
		controllayout.findViewById(R.id.control_play).setOnClickListener(listener);
		if (player != null && player.isPlaying() && uicontext != null && !uicontext.isClickPauseByUser()) {
			controllayout.findViewById(R.id.control_play).setBackgroundResource(R.drawable.play_pause);
		}
		controllayout.findViewById(R.id.control_back).setOnClickListener(listener);
		controllayout.findViewById(R.id.control_switch).setOnClickListener(listener);
		seekBar = (SeekBar) controllayout.findViewById(R.id.control_seek);
		seekBar.setMax(MAX);
		int pv = positionView == null ? View.GONE : positionView.getVisibility();
		CharSequence position = positionView == null ? "00:00" : positionView.getText();
		CharSequence duration = durationView == null ? "00:00" : durationView.getText();
		positionView = (TextView) controllayout.findViewById(R.id.control_time);
		durationView = (TextView) controllayout.findViewById(R.id.control_duration);
		seekBarbg = controllayout.findViewById(R.id.control_seek_bg);
		if (uicontext == null || uicontext.isEnableTimeShift()) {
			positionView.setText(position);
			durationView.setText(duration);
			durationView.setVisibility(View.VISIBLE);
			positionView.setVisibility(pv);
			if (uicontext != null && uicontext.getCurrentTimeShirtProgress() > 0) {
				seekBar.setProgress(uicontext.getCurrentTimeShirtProgress());
			} else {
				seekBar.setProgress(MAX);
			}
			if (seekBar instanceof PlaySeekBar) {
				Drawable drawable = getResources().getDrawable(R.drawable.play_progressdrawable);
				((PlaySeekBar) seekBar).setMyProgressDrawable(drawable);
				((PlaySeekBar) seekBar).setPositionView(positionView);
			}
			seekBar.setOnSeekBarChangeListener(listener);
		} else {
			positionView.setVisibility(View.GONE);
			seekBar.setVisibility(View.INVISIBLE);
			seekBarbg.setVisibility(View.INVISIBLE);
			durationView.setVisibility(View.INVISIBLE);
		}

		int v = backtolive == null ? View.GONE : backtolive.getVisibility();
		backtolive = controllayout.findViewById(R.id.control_backtolive);
		backtolive.setVisibility(v);
		backtolive.setOnClickListener(listener);
		titleView = (TextView) controllayout.findViewById(R.id.control_title);
		mTextSeekTo = (TextView) controllayout.findViewById(R.id.tv_seekto);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (titleView != null) {
				titleView.setText(TextUtils.isEmpty(uicontext.getVideoTitle()) ? mBundle.getString("videoTitle")
						: uicontext.getVideoTitle());
			}
			controllayout.findViewById(R.id.control_barrage).setOnClickListener(listener);
			controllayout.findViewById(R.id.control_choose).setVisibility(View.GONE);
			controllayout.findViewById(R.id.control_lock).setOnClickListener(listener);
			sendDM = controllayout.findViewById(R.id.control_danmaku);
			sendDM.setOnClickListener(listener);
			if (uicontext.isShowDanmaku()) {
				Button controlBrrage = (Button) controllayout.findViewById(R.id.control_barrage);
				controlBrrage.setBackgroundResource(R.drawable.play_barrage_p);
				// controlBrrage.setText(getResString(R.string.play_barrage));
				// controlBrrage.setTextColor(getResources().getColor(R.color.code1));
				sendDM.setVisibility(View.VISIBLE);
			}
			ratetype = (TextView) controllayout.findViewById(R.id.control_ratetype);
			if (uicontext.getRateTypeItems() != null) {
				RateTypeItem item = uicontext.getRateTypeItemById(uicontext.getCurrentRateType());
				if (item != null)
					setRateType(item);
			}

			ratetype.setOnClickListener(listener);
			if (MyApplication.getInstance().getOpenSdk().hasOpenId()) {
				controllayout.findViewById(R.id.control_more).setOnClickListener(listener);
			} else {
				controllayout.findViewById(R.id.control_more).setVisibility(View.GONE);
			}
			Button shareButton = (Button) controllayout.findViewById(R.id.control_more);
			shareButton.setBackgroundResource(R.drawable.play_barrage);
			shareButton.setText(getResString(R.string.play_share));
		}
		getDefaultHandler().sendEmptyMessage(PlayConst.CONTROL);
	}

	public void init(Bundle mBundle) {
		this.mBundle = mBundle;
		final String uuid = mBundle.getString(PlayerParams.KEY_PLAY_UUID);
		vuid = mBundle.getString(PlayerParams.KEY_PLAY_VUID);
		this.mBundle.putString(PlayerParams.KEY_PLAY_BUSINESSLINE, "100");
		this.mBundle.putString(PlayerParams.KEY_PLAY_CUSTOMERID, uuid);
		isPanoVideo = false;// mBundle.getBoolean("isPanoVideo", false);
		uicontext = new UiPlayContext();
		uicontext.setScreenResolution(ISplayerController.SCREEN_ORIENTATION_PORTRAIT);
		createOnePlayer();
	}

	private NetStateDialog netdialog;

	private void showNetStateDialog() {
		if (netdialog != null && netdialog.isAdded())
			return;
		netdialog = new NetStateDialog();
		netdialog.setOnClickListener(new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				MyApplication.getInstance().putBoolean(MyApplication.NOT_ONLYWIFI, true);
				if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
					mDanmakuView.resume();
					openDanmakuClient();
				}
				if (player != null && !uicontext.isClickPauseByUser() && !player.isPlaying()) {
					player.start();
				} else {
					createOnePlayer();
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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (player != null && uicontext != null && !uicontext.isLockFlag()) {
			// uicontext.setEnableAutoChangeScreen(false);
			if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
					&& uicontext.getScreenResolution() == ISplayerController.SCREEN_ORIENTATION_LANDSCAPE) {
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

		PlayerContainerLayout viewGroup = (PlayerContainerLayout) mRoot.findViewById(R.id.playcontainer);
		viewGroup.removeView(controllayout);
		controllayout = (RelativeLayout) LayoutInflater.from(mActivity).inflate(R.layout.play_controller, null);
		viewGroup.addView(controllayout);
		initControllayout();
		viewGroup.changeLayoutParams();
		if (surfaceView != null)
			surfaceView.onConfigurationChanged(newConfig);
		multiHelper.onChangeConfig();
		if (menulayout.isShown()) {
			getDefaultHandler().sendEmptyMessage(PlayConst.HIDEMENU);
		}
		if (player.isPlaying()) {
			getDefaultHandler().sendEmptyMessage(PlayConst.ONSTART);
		}
		// if (isPanoVideo && player != null && !playContext.isPlayingAd()) {
		// stopAndRelease();
		// createOnePlayer(null);
		// }

	}

	/**
	 * 创建一个新的播放器
	 * 
	 * @param holder
	 */
	void createOnePlayer() {
		if (!NetworkUtils.isWifiConnected(mActivity.getApplicationContext())
				&& NetworkUtils.isNetAvailable(mActivity)) {
			boolean notonly = MyApplication.getInstance().getBoolean(MyApplication.NOT_ONLYWIFI);
			if (notonly) {
				mActivity.showToastSafe(getResString(R.string.wifi_network), Toast.LENGTH_LONG);
			} else {
				showNetStateDialog();
				return;
			}

		}
		if (surfaceView == null) {
			surfaceView = new SaasActionVideoView(mActivity);
			surfaceView.setKeepScreenOn(true);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
			videolayout.addView(surfaceView, params);
			surfaceView.setVideoViewListener(videoViewListener);
			surfaceView.setDataSource(mBundle);
			player = surfaceView.getPlayer();
			helper = new PlayLiveHelper(this, uicontext, player, null);
			multiHelper = new PlayMultiHelper(uicontext, multilayout);
			lastOpenTime = new Date().getTime();
			player.setTimeShiftListener(timeShiftListener);
		} else {
			surfaceView.setDataSource(mBundle);
			helper = new PlayLiveHelper(this, uicontext, player, null);
			player = surfaceView.getPlayer();
			multiHelper = new PlayMultiHelper(uicontext, multilayout);
			lastOpenTime = new Date().getTime();
		}

	}

	private void setLiveSeekBarProgress(SeekBar seekBar, int progress) {
		if (progress > 600 && progress < 6600) {
			seekBar.setProgress(progress);
		} else {
			seekBar.setProgress((int) (seekBar.getMax() * 0.5));
		}
	}

	private ItimeShiftListener timeShiftListener = new ItimeShiftListener() {

		@Override
		public void onChange(long serverTime1, long positionTime1, long beginTime1) {
			if (uicontext.isEnableTimeShift() == false)
				return;
			if (uicontext.isClickPauseByUser())
				return;
			if (!seekBar.isShown()) {
				seekBar.setVisibility(View.VISIBLE);
			}
			serverTime = serverTime1;
			positionTime = positionTime1;
			beginTime = beginTime1;
			if (positionView.isShown()) {
				positionView.setText(DateFormat.format("HH:mm:ss", positionTime));
			}
			durationView.setText(DateFormat.format("HH:mm:ss", serverTime));
			if (serverTime1 > 0 && positionTime > 0) {
				betweenTime = serverTime1 - positionTime1;
				Log.d(TAG, "[seekbar] betweenTime:" + betweenTime + ",HOURS_2_SECOND:" + HOURS_2_SECOND + ",--:"
						+ (betweenTime - HOURS_2_SECOND));

				if (betweenTime < 600 * 1000) {
					seekBar.setMax(MAX);
					seekBar.setProgress(MAX - (int) betweenTime / 1000);
				} else {
					seekBar.setMax(MAX);
					if (uicontext.getCurrentTimeShirtProgress() > 0) {
						setLiveSeekBarProgress(seekBar, uicontext.getCurrentTimeShirtProgress());
					} else {
						seekBar.setProgress(MAX);
					}
				}
			}
			if (listener != null && !istouch && player.isPlaying()) {
				listener.onProgressChanged(seekBar, seekBar.getProgress(), false);
			}
			if (serverTime1 - positionTime1 > 60 * 1000) {
				backtolive.setVisibility(View.VISIBLE);
				positionView.setVisibility(View.VISIBLE);
			} else {
				backtolive.setVisibility(View.GONE);
				positionView.setVisibility(View.GONE);
			}
			FragmentManager fragmentManager = ((BaseActivity) mActivity).getSupportFragmentManager();
			Fragment tvFragment = fragmentManager.findFragmentByTag("PlayTVLiveFragment");
			if (tvFragment != null && tvFragment instanceof PlayTVLiveFragment) {
				((PlayTVLiveFragment) tvFragment).setServiceTime(serverTime);
				((PlayTVLiveFragment) tvFragment).setPositionTime(positionTime);
			}
		}

	};

	protected void initDetailPage() {
		int playMode = mBundle.getInt(PlayerParams.KEY_PLAY_MODE, -1);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		mBundle.putString("cid", mBundle.getString(PlayerParams.KEY_PLAY_ACTIONID));
		Fragment liveFragment = Fragment.instantiate(mActivity, PlayTVLiveFragment.class.getName());
		liveFragment.setArguments(mBundle);
		transaction.replace(R.id.detailbody, liveFragment, "PlayTVLiveFragment");
		transaction.commit();
	}

	private Handler bindHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PlayTVConst.TIMESHIFT:
				// 回看的时候
				if (positionTime > 0) {
					PlayTvItemInfo info = (PlayTvItemInfo) msg.obj;
					int MaxProgress = (int) ((info.getEndTime() - info.getBeginTime()) * 0.001);
					player.seekTimeShift(info.getBeginTime());

					long seekTime = (long) ((info.getBeginTime() - positionTime) * 0.001);
					int leftCriticalPoint = 60 - seekBar.getProgress();// 负数
					int rightCriticalPoint = seekBar.getProgress() - 60;// 正数
					positionView.setVisibility(View.VISIBLE);
					if (seekTime < 0 && seekTime > leftCriticalPoint) {
						seekBar.setProgress((int) (seekBar.getProgress() + seekTime));
						uicontext.setCurrentTimeShirtProgress(seekBar.getProgress());
					} else if (seekTime > 0 && seekTime < rightCriticalPoint) {
						seekBar.setProgress((int) (seekBar.getProgress() + seekTime));
						uicontext.setCurrentTimeShirtProgress(seekBar.getProgress());
					} else if (seekTime > 0 && (serverTime - info.getBeginTime()) <= 60) {
						seekBar.setProgress(MAX);
						uicontext.setCurrentTimeShirtProgress(0);
					} else {
						seekBar.setProgress((int) (seekBar.getMax() * 0.5));
						uicontext.setCurrentTimeShirtProgress(seekBar.getProgress());
					}
				} else {
					Toast.makeText(getActivity(), getResources().getString(R.string.loading_pls_wait),
							Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};
}
