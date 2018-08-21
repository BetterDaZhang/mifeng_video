package com.letv.autoapk.player;

import com.lecloud.sdk.surfaceview.ISurfaceView;
import com.letv.pano.PanoVideoControllerView;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by heyuekuai on 16/5/26.
 */
public class BasePanoSurfaceView extends PanoVideoControllerView implements ISurfaceView {
    public BasePanoSurfaceView(Context context) {
        super(context);
    }

    public BasePanoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PanoControllMode getPanoMode() {
        return panoMode;
    }


}