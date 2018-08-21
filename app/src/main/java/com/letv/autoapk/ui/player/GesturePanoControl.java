package com.letv.autoapk.ui.player;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.lecloud.sdk.surfaceview.ISurfaceView;
import com.letv.autoapk.player.BasePanoSurfaceView;
import com.letv.pano.OnPanoViewTapUpListener;

/**
 * 播放控手势控制
 * 
 * @author dengjiaping
 */
class GesturePanoControl implements OnTouchListener, OnPanoViewTapUpListener {
	private static final String TAG = "GesturePanoControl";

	/**
	 * 播放控制层
	 * **/
	private BasePanoSurfaceView panoVideoView;

	/**
	 * 是否允许触摸
	 */

	private Handler mHandler;

	/**
	 * @param mContext
	 * @param mPlayControllerView
	 *            播放控制层
	 */
	public GesturePanoControl(Handler handler, ISurfaceView panoVideoView) {
		mHandler = handler;
		this.panoVideoView = (BasePanoSurfaceView)panoVideoView;
		if (this.panoVideoView != null )
			this.panoVideoView.setTapUpListener(this);
	}

	@Override
	public void onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(PlayConst.SHOWORHIDECONTROL);
		mHandler.removeMessages(PlayConst.HIDECONTROL);
		mHandler.sendEmptyMessageDelayed(PlayConst.HIDECONTROL, 4000);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if (panoVideoView == null)
			return false;

		return panoVideoView.onPanoTouch(panoVideoView, event);
	}

}