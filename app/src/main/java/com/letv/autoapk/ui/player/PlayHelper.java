package com.letv.autoapk.ui.player;

import java.util.List;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;

import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.player.IPlayer;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.player.ISplayerController;
import com.letv.autoapk.utils.StringEscapeUtils;

class PlayHelper {

	static final int RATETYPE = 1;
	static final int MORE = 2;
	static final int CHOOSE = 3;
	static final int SHARE = 4;
	private Fragment fragment;
	protected UiPlayContext uicontext;
	protected IPlayer player;
	int progress;
	protected Handler playHandler;
	private boolean istouch;

	PlayHelper(Fragment fragment, UiPlayContext uicontext, IPlayer player, Handler playHandler) {
		this.fragment = fragment;
		this.uicontext = uicontext;
		this.player = player;
		this.playHandler = playHandler;
	}

	public void startTrackingTouch(SeekBar seekBar) {
		if (istouch)
			return;
		istouch = true;
		progress = seekBar.getProgress();
	}

	public void stopTrackingTouch(SeekBar seekBar) {
		if (istouch == false)
			return;
		istouch = false;
		if (player != null && (player.isPlaying() || uicontext.isClickPauseByUser())) {
			long duration = 0;
			duration = player.getDuration();
			if (player != null) {
				long seek = seekBar.getProgress() * duration / seekBar.getMax();
				player.seekTo(seek);
				if (playHandler != null)
					playHandler.removeMessages(PlayConst.STARTTIME);
			}
		} else {
			seekBar.setProgress(this.progress);
		}
	}

	public boolean playOrpause(View v) {
		if (player.isPlaying()) {
			player.pause();
			if (playHandler != null)
				this.playHandler.sendEmptyMessage(PlayConst.REQUESTADPIC);
			if (uicontext != null) {
				// 记录用户按下暂停
				uicontext.setClickPauseByUser(true);
			}
			v.setBackgroundResource(R.drawable.play_play);
			return false;
		} else if (player.getStatus() == IPlayer.PLAYER_STATUS_EOS) {
			player.retry();
			if (playHandler != null)
				this.playHandler.sendEmptyMessage(PlayConst.STARTTIME);
			v.setBackgroundResource(R.drawable.play_pause);
		} else {
			if (uicontext != null) {
				// 用户按下播放按钮
				uicontext.setClickPauseByUser(false);
			}
			if (playHandler != null)
				this.playHandler.sendEmptyMessage(PlayConst.STARTTIME);
			player.start();
			uicontext.setCurrentTimeShirtProgress(0);
			v.setBackgroundResource(R.drawable.play_pause);

		}
		return true;
	}

	public void changeScreen(Activity activity, int state) {
		// TODO Auto-generated method stub
		if (player != null && uicontext != null && !uicontext.isLockFlag()) {
			int activityorentation = -1;
			switch (state) {
			case ISplayerController.SCREEN_ORIENTATION_USER_PORTRAIT:
				uicontext.setEnableAutoChangeScreen(false);
			case ISplayerController.SCREEN_ORIENTATION_PORTRAIT:
				activityorentation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
				break;
			case ISplayerController.SCREEN_ORIENTATION_USER_LANDSCAPE:
				uicontext.setEnableAutoChangeScreen(false);
			case ISplayerController.SCREEN_ORIENTATION_LANDSCAPE:
				activityorentation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
				break;
			case ISplayerController.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
				activityorentation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
				break;
			case ISplayerController.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
				activityorentation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
				break;
			default:
				activityorentation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
				break;
			}
			if (activityorentation != activity.getRequestedOrientation()) {
				activity.setRequestedOrientation(activityorentation);
			}
			uicontext.setScreenResolution(state);
		}
	}

	public boolean doBack(BaseActivity mActivity) {
		// TODO Auto-generated method stub
		if (uicontext.getScreenResolution() == ISplayerController.SCREEN_ORIENTATION_LANDSCAPE) {
			changeScreen(mActivity, ISplayerController.SCREEN_ORIENTATION_PORTRAIT);
			return true;
		}
		return false;
	}

	public BaseAdapter initmenu(int type, BaseActivity context, Bundle bundle) {
		BaseAdapter adapter = null;
		if (type == RATETYPE) {
			if (uicontext.getRateTypeItems() != null)
				adapter = new MenuRateAdapter(uicontext.getRateTypeItems(), context, uicontext.getCurrentRateType());
		}
		if (type == MORE) {
			List<PlayVideoInfo> playlist = uicontext.getPlayList();
			PlayVideoInfo info = new PlayVideoInfo();
			if (playlist != null && !playlist.isEmpty()) {
				for (PlayVideoInfo playVideoInfo : playlist) {
					if (playVideoInfo.getVideoId() != null
							&& playVideoInfo.getVideoId().equals(bundle.getString(PlayerParams.KEY_PLAY_VUID))) {
						info = playVideoInfo;
						break;
					}
				}
				if (TextUtils.isEmpty(info.getVideoId()) || TextUtils.isEmpty(info.getAlbumId())) {
					info.setImageUrl(bundle.getString("imageUrl"));
					info.setVideoDesc(StringEscapeUtils.unescapeHtml4(bundle.getString("videoDesc")));
					info.setVideoTitle(StringEscapeUtils.unescapeHtml4(bundle.getString("videoTitle")));
					info.setShareUrl(bundle.getString("shareUrl"));
					info.setVideoId(bundle.getString(PlayerParams.KEY_PLAY_VUID));
					info.setAlbumId(bundle.getString("albumId"));
					info.setmAlbumName(bundle.getString("albumName"));
				}
			} else {
				info.setImageUrl(bundle.getString("imageUrl"));
				info.setVideoDesc(StringEscapeUtils.unescapeHtml4(bundle.getString("videoDesc")));
				info.setVideoTitle(StringEscapeUtils.unescapeHtml4(bundle.getString("videoTitle")));
				info.setShareUrl(bundle.getString("shareUrl"));
				info.setVideoId(bundle.getString(PlayerParams.KEY_PLAY_VUID));
				info.setAlbumId(bundle.getString("albumId"));
				info.setmAlbumName(bundle.getString("albumName"));
			}
			if (uicontext.isDownloadable()) {
				info.setDownloadPlatform("104002");
			}
			adapter = new MenuMoreAdapter(fragment, context, info, uicontext.isDownloadable());
		}
		if (type == SHARE) {
			if (MyApplication.getInstance().getOpenSdk().hasOpenId()) {
				DisplayVideoInfo info = new DisplayVideoInfo();
				info.setImageUrl(bundle.getString("imageUrl"));
				info.setVideoDesc(StringEscapeUtils.unescapeHtml4(bundle.getString("videoDesc")));
				info.setVideoTitle(StringEscapeUtils.unescapeHtml4(bundle.getString("videoTitle")));
				info.setShareUrl(bundle.getString("shareUrl"));
				adapter = new MenuShareAdapter(context, info);
			}

		}
		if (type == CHOOSE) {
			List<PlayVideoInfo> playlist = uicontext.getPlayList();
			if (playlist != null && playlist.size() > 0) {
				if (uicontext.getDisplayType() == 1) {
					adapter = new MenuEpisodeAdapter(context, playlist, bundle.getString(PlayerParams.KEY_PLAY_VUID));
				} else {
					adapter = new MenuEpisode2Adapter(context, playlist, bundle.getString(PlayerParams.KEY_PLAY_VUID));
				}
			}
		}
		return adapter;
	}

}
