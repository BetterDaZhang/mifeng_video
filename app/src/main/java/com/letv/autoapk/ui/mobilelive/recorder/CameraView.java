package com.letv.autoapk.ui.mobilelive.recorder;

import java.util.Formatter;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.autoapk.R;
import com.letv.recorder.bean.AudioParams;
import com.letv.recorder.bean.CameraParams;
import com.letv.recorder.callback.ISurfaceCreatedListener;
import com.letv.recorder.callback.PublishListener;
import com.letv.recorder.controller.CameraSurfaceView;
import com.letv.recorder.controller.Publisher;

public class CameraView extends CameraSurfaceView implements ISurfaceCreatedListener {
	private Context mContext;
	private Publisher publisher;
	private CameraParams cameraParams;
	private AudioParams audioParams;
	private final static String TAG = "CameraView";
	private boolean isBack = false;// 后台标志,在进入后台之前正在推流设置为true。判断是否在后台回来时继续推流
	private String url;
	private TextView timeView;// 推流时间显示
	private int time = 0;
	private Handler mHandler;
	public static final int PUSHSTREM_URL_NULL = 0;

	public CameraView(Context context) {
		super(context);
	}

	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void init(Activity context, Handler handler, boolean isLandscape) {
		this.mContext = context;
		this.mHandler = handler;
		publisher = Publisher.getInstance();
		publisher.initPublisher((Activity) mContext);
		publisher.getRecorderContext().setUseLanscape(isLandscape);// 告诉推流器使用横屏推流还是竖屏推流
		cameraParams = publisher.getCameraParams();
		audioParams = publisher.getAudioParams();
		publisher.setPublishListener(listener);// 设置推流状态监听器
		// 绑定Camera显示View,要求必须是CameraSurfaceView
		publisher.getVideoRecordDevice().bindingGLView(this);
		// 设置CameraSurfaceView 监听器,当CameraSurfaceView
		// 创建成功的时候回回调onGLSurfaceCreatedListener,这个时候才能开启摄像头
		publisher.getVideoRecordDevice().setSurfaceCreatedListener(this);
		////////////////// 以下设置必须在在推流之前设置,也可以不设置////////////////////////////////////////
		if (isLandscape) {// 设置流分辨率。要求宽度必须是16的整倍数,高度没有要求
			cameraParams.setWidth(640);
			cameraParams.setHeight(368);
		} else {
			cameraParams.setWidth(360);
			cameraParams.setHeight(640);
		}
		cameraParams.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT); // 开启默认前置摄像头
		cameraParams.setVideoBitrate(1000 * 1000); // 设置码率
		audioParams.setEnableVolumeGain(true);// 开启音量调节,注意,这一点会影响性能,如果没有必要,设置为false
		cameraParams.setFocusOnTouch(true);// 关闭对焦功能
		cameraParams.setFocusOnAnimation(true);// 关闭对焦动画
		cameraParams.setOpenGestureZoom(false);
//		ImageView iv= new ImageView(context);
//		iv.setBackgroundResource(R.drawable.letv_recorder_focus_auto);
//		publisher.getVideoRecordDevice().setFocusView(new View(getContext()));// 设置对焦图片。如果需要对焦功能和对焦动画,请打开上边两个设置,并且在这里传入一个合适的View
	}
	
	public void setFocusView(View view){
	    publisher.getVideoRecordDevice().setFocusView(view);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (publisher.isRecording()) { // 正在推流
			isBack = true;
			publisher.stopPublish();// 停止推流
		}
		// 关闭摄像头
		publisher.getVideoRecordDevice().stop();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		publisher.release();// 销毁推流器
		timerHandler.removeCallbacksAndMessages(null);
		publisher = null;
	}

	@Override
	public void onGLSurfaceCreatedListener() {
		publisher.getVideoRecordDevice().start();// 打开摄像头
		if (isBack) {
			isBack = false;
			publish(url, time);
		}
	}

	/**
	 * 开始推流
	 * 
	 * @param url
	 *            推流地址
	 */
	public void publish(String url, int time) {
		this.time = time;
		this.url = url;
		if (!publisher.isRecording() && url != null) {
			publisher.setUrl(url);// 设置推流地址
			publisher.publish();// 在摄像头打开以后才能开始推流
		} else {
			stopPublish();
		}
	}

	public void stopPublish() {
		if (publisher.isRecording()) {
			publisher.stopPublish();
		} else if (url == null) {
			mHandler.sendEmptyMessage(PUSHSTREM_URL_NULL);
			Toast.makeText(mContext, getResources().getString(R.string.letv_recorder_url_error), Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * 切换摄像头,需要注意,切换摄像头不能太频繁,如果太频繁会导致应用程序崩溃。建议最快10秒一次
	 */
	boolean isSwitch = false;

	public int switchCamera() {
		int cameraID;
		if (isSwitch) {
			Toast.makeText(getContext(), mContext.getResources().getString(R.string.letv_recorder_camera),
					Toast.LENGTH_SHORT).show();
			return 2;
		}
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				isSwitch = false;
			}
		}, 10 * 1000);
		isSwitch = true;
		if (cameraParams.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;// 0
		} else {
			cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;// 1
		}
		publisher.getVideoRecordDevice().switchCamera(cameraID);// 切换摄像头
		return cameraID;
	}

	public int getCameraId() {
		return cameraParams.getCameraId();
	}

	/**
	 * 切换闪光灯。注意,当使用前置摄像头时不能打开闪光灯
	 */
	boolean flag = false;

	public boolean changeFlash() {
		if (cameraParams.getCameraId() != Camera.CameraInfo.CAMERA_FACING_FRONT) {
			flag = !flag;
			publisher.getVideoRecordDevice().setFlashFlag(flag);// 切换闪关灯
		}
		return flag;
	}

	public boolean closeFlash() {
		if (flag) {
			flag = !flag;// 切换前置摄像头会自动关闭闪光灯
			publisher.getVideoRecordDevice().setFlashFlag(flag);
		}
		return flag;
	}

	public boolean getFlash() {
		return flag;
	}

	/**
	 * 切换滤镜,设置为0为关闭滤镜
	 */
	int model = CameraParams.FILTER_VIDEO_NONE;// 无效果

	public void switchFilter() {
		if (model == CameraParams.FILTER_VIDEO_NONE) {
			model = CameraParams.FILTER_VIDEO_DEFAULT; // 默认的美颜效果
		} else {
			model = CameraParams.FILTER_VIDEO_NONE;// 无效果
		}
		publisher.getVideoRecordDevice().setFilterModel(model);// 切换滤镜
	}

	/**
	 * 设置声音大小,必须对setEnableVolumeGain设置为true
	 * 
	 * @param volume
	 *            0-1为缩小音量,1为正常音量,大于1为放大音量
	 */
	int volume = 1;

	public int setVolume() {
		if (volume == 1) {
			volume = 0;
		} else {
			volume = 1;
		}
		publisher.setVolumeGain(volume);// 设置声音大小
		return volume;
	}

	private PublishListener listener = new PublishListener() {
		@Override
		public void onPublish(int code, String msg, Object... obj) {
			Message message = mHandler.obtainMessage(code);
			message.obj = msg;
			mHandler.sendMessage(message);
		}
	};

	private Handler timerHandler = new Handler();
	private Runnable timerRunnable = new Runnable() {
		@Override
		public void run() {
			if (publisher!=null && publisher.isRecording()) {
				time++;
				if (timeView != null) {
					timeView.setText(
							mContext.getString(R.string.letv_record_live_time_already) + "  " + stringForTime(time));
				}
				timerHandler.postDelayed(timerRunnable, 1000);
			}
		}
	};

	void setTime(TextView time) {
		this.timeView = time;
	}

	/**
	 * 开始计时
	 */
	public void calculateTime() {
		timerHandler.postDelayed(timerRunnable, 1000);
	}

	/**
	 * 格式化时间
	 * 
	 * @param timeMs
	 * @return
	 */
	private StringBuilder mFormatBuilder = new StringBuilder();
	private Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

	private String stringForTime(int timeMs) {
		int totalSeconds = timeMs;
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;
		mFormatBuilder.setLength(0);
		return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
	}

	@Override
	public void zoomOnTouch(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
