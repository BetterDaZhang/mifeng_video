package com.letv.autoapk.player;

import android.content.Context;

import com.letv.android.client.cp.sdk.player.vod.CPVodPlayer;

public class SaasPlayer extends CPVodPlayer {

	public SaasPlayer(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initMediaData() {
		// TODO Auto-generated method stub
		mediaData = new SaasMediaData(context);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4523468131631419951L;

}
