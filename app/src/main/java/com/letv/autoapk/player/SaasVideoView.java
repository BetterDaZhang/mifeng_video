package com.letv.autoapk.player;

import android.content.Context;
import android.os.Bundle;
import android.view.Surface;

import com.lecloud.sdk.player.IAdPlayer;
import com.lecloud.sdk.surfaceview.ISurfaceView;
import com.lecloud.sdk.surfaceview.impl.BaseSurfaceView;
import com.lecloud.sdk.videoview.vod.VodVideoView;
import com.letv.pano.ISurfaceListener;

public class SaasVideoView extends VodVideoView {

	private boolean adsound = true;
	private boolean ispano = false;
	BasePanoSurfaceView.PanoControllMode panoControllMode;
    public static final int MODE_TOUCH = 0;
    public static final int MODE_MOVE = 1;
	public SaasVideoView(Context context) {
		super(context);
	}

	@Override
	protected void initPlayer() {
		player = new SaasPlayer(context);
	}

	public SaasPlayer getPlayer() {
		return (SaasPlayer) player;
	}

	@Override
	protected void onInterceptVodMediaDataSuccess(int event, Bundle bundle) {
		// VideoHolder videoHolder =
		// bundle.getParcelable(PlayerParams.KEY_RESULT_DATA);
		// ((IMediaDataPlayer)
		// player).setDataSourceByRate(onInterceptSelectDefiniton(videoHolder.getVtypes(),
		// videoHolder.getDefaultVtype()));
//		if (listener != null)
//			listener.onStateResult(event, bundle);
	}
	
    @Override
	protected void prepareVideoSurface() {
		if(ispano){
			preparePanoSurface();
			return;
		}
			
		super.prepareVideoSurface();
	}

	private void preparePanoSurface(){
    	 baseSurfaceView = new BasePanoSurfaceView(context);
         if (panoControllMode != null) {
             ((BasePanoSurfaceView) baseSurfaceView).switchControllMode(panoControllMode);
         }
         panoControllMode = ((BasePanoSurfaceView) baseSurfaceView).getPanoMode();
         setVideoView(baseSurfaceView);
         ((BasePanoSurfaceView) baseSurfaceView).registerSurfacelistener(new ISurfaceListener() {
             @Override
             public void setSurface(Surface surface) {
                 player.setDisplay(surface);
             }
         });
//         ((BasePanoSurfaceView) baseSurfaceView).setTapUpListener(new OnPanoViewTapUpListener() {
//             @Override
//             public void onSingleTapUp(MotionEvent e) {
//             }
//         });

//         setOnTouchListener(new OnTouchListener() {
//             @Override
//             public boolean onTouch(View v, MotionEvent event) {
//                 ((BasePanoSurfaceView) baseSurfaceView).onPanoTouch(v, event);
//                 return true;
//             }
//         });
    }
	protected ISurfaceView baseSurfaceView;

	@Override
	protected void setVideoView(ISurfaceView surfaceView) {
		// TODO Auto-generated method stub
		super.setVideoView(surfaceView);
		baseSurfaceView =  surfaceView;
		if(baseSurfaceView instanceof BaseSurfaceView)
			((BaseSurfaceView)baseSurfaceView).setDisplayMode(BaseSurfaceView.DISPLAY_MODE_SCALE_ZOOM);  
	}

	public ISurfaceView getSurfaceView() {
		return baseSurfaceView;
	}

	public boolean isAdsound() {
		return adsound;
	}

	public boolean isIspano() {
		return ispano;
	}

	public void setIspano(boolean ispano) {
		this.ispano = ispano;
	}

	public void setAdsound(boolean adsound) {
		this.adsound = adsound;
		int volume = adsound ? 1 : 0;
		setVolume(volume, volume);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (player instanceof IAdPlayer && adsound == false) {
			setVolume(0, 0);
		}
	}

    public void switchPanoVideoMode(int mode) {
        if (mode == MODE_MOVE) {
            panoControllMode = BasePanoSurfaceView.PanoControllMode.GESTURE_AND_GYRO;
        } else {
            panoControllMode = BasePanoSurfaceView.PanoControllMode.GESTURE;
        }
        ((BasePanoSurfaceView) baseSurfaceView).switchControllMode(panoControllMode);
    }
}