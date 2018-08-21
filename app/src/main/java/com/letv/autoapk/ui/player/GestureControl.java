package com.letv.autoapk.ui.player;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.ui.player.view.VerticalProgressBar;
import com.letv.autoapk.utils.ScreenBrightnessManager;
import com.letv.autoapk.utils.ScreenUtils;

/**
 * 播放控手势控制
 * 
 * @author dengjiaping
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
class GestureControl implements OnTouchListener {
	private static final String TAG = "GestureControl";
	public static final int GESTURE_CONTROL_DOWN = 5010;
	public static final int GESTURE_CONTROL_SEEK = 5011;
	public static final int GESTURE_CONTROL_UP = 5012;
	public static final String GESTURE_CONTROL_SEEK_GAP = "gesture_control_seek_gap";

	public Context mContext;
	/**
	 * 播放控制层
	 * **/
	public ViewGroup mPlayControllerView;

	/**
	 * 声音
	 */
	private ViewGroup mVolumelayout;
	/**
	 * 亮度
	 */
	private ViewGroup mBrightnesslayout;

	/**
	 * 滑动
	 */
	private ImageView mIconSeekTo;
	private ViewGroup mSeeklayout;
	
    private SeekBar seekBar;
	private GestureDetector mGestureDetector;
	private AudioManager mAudioManager;

	/**
	 * 是否允许触摸
	 */
	public boolean touchable = true;

	/**
	 * 是否允许滑动快进
	 */
	private boolean seekable = false;
	/**
	 * 是否允许滑动控制
	 */
	private boolean scrollable = true;
	// private ISplayer mISplayer;

	private int seekGap = -1;

	private boolean isSeeking = false;
	private boolean isChangeVolume = false;
	private boolean isChangeBrightness = false;

	private Handler mHandler;
	private VerticalProgressBar voice;
	private VerticalProgressBar bright;
	private long duration;
	private static final int MINDISTANCE = 10;

	/**
	 * @param mContext
	 * @param mPlayControllerView
	 *            播放控制层
	 */
	public GestureControl(Context mContext, ViewGroup mPlayControllerView,
			Handler handler) {
		this.mContext = mContext;
		this.mPlayControllerView = mPlayControllerView;
		mHandler = handler;
		init();
	}

	private void init() {
		this.mAudioManager = (AudioManager) this.mContext
				.getSystemService(Service.AUDIO_SERVICE);
		this.mGestureDetector = new GestureDetector(mContext,
				mOnGestureListener);
		this.mVolumelayout = (ViewGroup) mPlayControllerView
				.findViewById(R.id.control_leftlayout);
		mBrightnesslayout = (ViewGroup) mPlayControllerView
				.findViewById(R.id.control_rightlayout);
		this.mSeeklayout = (ViewGroup)mPlayControllerView.findViewById(R.id.control_centerlayout);
		mIconSeekTo = (ImageView) mPlayControllerView.findViewById(R.id.iv_seekto);
		
		seekBar = (SeekBar)mPlayControllerView.findViewById(R.id.control_seek);
		voice = (VerticalProgressBar) mPlayControllerView
				.findViewById(R.id.control_voice);
		bright = (VerticalProgressBar) mPlayControllerView
				.findViewById(R.id.control_bright);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v == mPlayControllerView) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if(duration==0){
					TextView duration = (TextView)mPlayControllerView.findViewById(R.id.control_duration);
					if(duration.getTag()!=null){
						this.duration = (Long)duration.getTag()/1000;
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				if (isSeeking) {
					Message msg = mHandler.obtainMessage();
					msg.what = PlayConst.SEEKEND;
					mHandler.sendMessage(msg);
					seekGap = -1;
				}
				if(Math.abs(seekGap) < MINDISTANCE){
					mHandler.sendEmptyMessage(PlayConst.SHOWORHIDECONTROL);
					mHandler.removeMessages(PlayConst.HIDECONTROL);
					mHandler.sendEmptyMessageDelayed(PlayConst.HIDECONTROL, 4000);
				}
			case MotionEvent.ACTION_CANCEL:
				isChangeBrightness = false;
				isChangeVolume = false;
				isSeeking = false;
				mVolumelayout.setVisibility(View.INVISIBLE);
				mBrightnesslayout.setVisibility(View.INVISIBLE);
				mSeeklayout.setVisibility(View.INVISIBLE);
				break;

			default:
				break;
			}
		}
		if (!touchable) {
			return false;
		}
		return mGestureDetector.onTouchEvent(event);
	}
	
	private final OnGestureListener mOnGestureListener = new OnGestureListener() {
		private int level = 0;// 记录popupwindow每次显示时候的初始值
		private float mYDown;
		private float mXDown;
		private float mYMove;
        private int mprogress;
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// 处理点击事件
			mPlayControllerView.performClick();
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			/**
			 * 关闭手势
			 * */
			if (!scrollable || e1 == null || e2 == null) {
				return false;
			}
			if (Math.abs(distanceX) > Math.abs(distanceY)&& isSeekable()) {
				if (isChangeVolume || isChangeBrightness) {
					return true;
				}
				isSeeking = true;
				if (!mSeeklayout.isShown()) {
					mSeeklayout.setVisibility(View.VISIBLE);
					mHandler.sendEmptyMessage(PlayConst.SEEKSTART);
					mprogress =seekBar.getProgress();
				}
				if(mHandler.hasMessages(PlayConst.HIDECONTROL)){
					mHandler.removeMessages(PlayConst.HIDECONTROL);
				}
				seekGap = (int) ((e2.getX() - e1.getX()) * 1000 / mPlayControllerView
						.getWidth());
				if (seekGap > 0) {
					mIconSeekTo.setImageResource(R.drawable.play_ff);
				} else {
					mIconSeekTo.setImageResource(R.drawable.play_bf);
				}
				mHandler.obtainMessage(PlayConst.SEEKTO, (int)seekGap, 0).sendToTarget();
				return false;
			}

			if (isSeeking) {
				return true;
			}

			this.mYMove = e2.getY();
			int addtion = (int) (this.mYDown - this.mYMove) * 100
					/ ScreenUtils.getHeight(mContext);
			if (ScreenUtils.isInRight(mContext, (int) e1.getX())) {
				isChangeVolume = true;
				if (!mVolumelayout.isShown()) {
					mVolumelayout.setVisibility(View.VISIBLE);
					this.level = getVolume();
				}
				int vloume = this.level + addtion;
				vloume = (vloume > 100 ? 100 : (vloume < 0 ? 0 : vloume));
				setVolume(vloume);
				voice.setProgress(vloume);
				return true;
			} else if (ScreenUtils.isInLeft(mContext, (int) e1.getX())) {
				isChangeBrightness = true;
				if (!mBrightnesslayout.isShown()) {
					mBrightnesslayout.setVisibility(View.VISIBLE);
					this.level = (getScreenBrightness((Activity) mContext) * 100) / 255;
					bright.setProgress(this.level);
					setScreenBrightness((Activity) mContext, this.level);
				}
				int brightness = this.level + addtion;
				brightness = (brightness > 100 ? 100 : (brightness < 0 ? 0
						: brightness));
				setScreenBrightness((Activity) mContext, brightness * 255 / 100);
				bright.setProgress(brightness);
			}
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			this.mXDown = e.getX();
			this.mYDown = e.getY();
			return true;
		}

	};

	// ------------------------音量控制----------------------
	public void setVolume(int percentage) {
		if (null == this.mAudioManager) {
			return;
		}

		if (percentage < 0 || percentage > 100) {
			return;
		}

		int maxValue = this.mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		this.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				percentage * maxValue / 100, 0);
	}

	/**
	 * 获取当前音量百分比(0-100)
	 */
	public int getVolume() {
		if (null == this.mAudioManager) {
			return 0;
		}
		int volume = this.mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		int maxValue = this.mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		return volume * 100 / maxValue;
	}

	// ------------------------音量控制--end--------------------

	// ------------------------亮度控制----------------------

	private int mCurrentBrightness = -1;

	/**
	 * 设置亮度
	 * 
	 * @param paramInt
	 *            取值0-255
	 */
	public void setScreenBrightness(Activity activity, int paramInt) {
		this.mCurrentBrightness = paramInt;
		ScreenBrightnessManager.setScreenBrightness(activity, paramInt);

	}

	/**
	 * 获取当前亮度(取值0-255)
	 */
	public int getScreenBrightness(Activity activity) {
		if (this.mCurrentBrightness != -1) {
			return this.mCurrentBrightness;
		}
		return ScreenBrightnessManager.getScreenBrightness(activity);
	}

	public void cancelTouchable(boolean cancel) {
		this.touchable = !cancel;
	}

	public boolean isSeekable() {
		return seekable;
	}

	public void setSeekable(boolean seekable) {
		this.seekable = seekable;
	}

}
