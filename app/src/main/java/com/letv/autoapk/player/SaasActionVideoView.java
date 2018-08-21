package com.letv.autoapk.player;

import android.content.Context;

import com.lecloud.sdk.player.IAdPlayer;
import com.lecloud.sdk.videoview.live.ActionLiveVideoView;
import com.letv.android.client.cp.sdk.player.live.CPActionLivePlayer;

public class SaasActionVideoView extends ActionLiveVideoView {

	public static final int NO_LIVE_STREAM = -10086;
	private boolean adsound = true;
	public SaasActionVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}


	@Override
	protected void initPlayer() {
		player = new CPActionLivePlayer(context);
	}

	public CPActionLivePlayer getPlayer() {
		return (CPActionLivePlayer) player;
	}

	protected void processLiveStatus(int status) {
		listener.onStateResult(NO_LIVE_STREAM, null);
	}
	@Override
	public void onResume() {
		super.onResume();
		if (player instanceof IAdPlayer && adsound == false) {
			setVolume(0, 0);
		}
		//dismissAdPic();
	}
	public boolean isAdsound() {
		return adsound;
	}


	public void setAdsound(boolean adsound) {
		this.adsound = adsound;
		int volume = adsound ? 1 : 0;
		setVolume(volume, volume);
	}
}
