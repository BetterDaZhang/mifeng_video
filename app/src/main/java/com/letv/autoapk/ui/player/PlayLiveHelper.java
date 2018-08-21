package com.letv.autoapk.ui.player;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;

import com.lecloud.sdk.player.IPlayer;
import com.letv.autoapk.R;

public class PlayLiveHelper extends PlayHelper {

	PlayLiveHelper(Fragment fragment, UiPlayContext uicontext, IPlayer player,
			Handler playHandler) {
		super(fragment, uicontext, player, playHandler);
		// TODO Auto-generated constructor stub
	}
	public boolean playOrpause(View v) {
		if (player.isPlaying()) {
			player.stop();
			player.reset();
			player.release();
			if (uicontext != null) {
				// 记录用户按下暂停
				uicontext.setClickPauseByUser(true);
			}
			v.setBackgroundResource(R.drawable.play_play);
			return false;
		}
		else if (player.getStatus() == IPlayer.PLAYER_STATUS_EOS) {
			player.retry();
			if (playHandler != null)
				this.playHandler.sendEmptyMessage(PlayConst.STARTTIME);
			v.setBackgroundResource(R.drawable.play_pause);
		} 
		else {
			if (uicontext != null) {
				// 用户按下播放按钮
				uicontext.setClickPauseByUser(false);
			}
			if (playHandler != null)
				this.playHandler.sendEmptyMessage(PlayConst.STARTTIME);
			player.retry();
			uicontext.setCurrentTimeShirtProgress(0);
			v.setBackgroundResource(R.drawable.play_pause);
			
		}
		return true;
	}
}
