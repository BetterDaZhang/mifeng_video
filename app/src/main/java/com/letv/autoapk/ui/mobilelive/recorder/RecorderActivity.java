package com.letv.autoapk.ui.mobilelive.recorder;

import java.util.HashMap;
import java.util.Map;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.dialog.BaseDialog;
import com.letv.autoapk.base.dialog.DialogResultListener;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.MyProgressDialog;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.open.OpenSdk;
import com.letv.autoapk.open.OpenShareActivity;
import com.letv.autoapk.open.OpenShareDialog;
import com.letv.autoapk.open.OpenShareDialog.OnShareListener;
import com.letv.autoapk.ui.mobilelive.CreateMobileLiveDialog;
import com.letv.autoapk.ui.mobilelive.GetMobileLiveStreamInfoDataRequest;
import com.letv.autoapk.ui.mobilelive.MobileLiveDialog;
import com.letv.autoapk.ui.mobilelive.MobileLiveStreamInfo;
import com.letv.autoapk.ui.mobilelive.QuitLiveNotifyDataRequest;
import com.letv.autoapk.ui.player.PlayerAPI;
import com.letv.autoapk.utils.SystemUtls;
import com.letv.recorder.controller.Publisher;
import com.letv.recorder.ui.Rotate3dAnimation;
import com.letv.recorder.ui.SurfaceFrameLayout;
import com.letv.recorder.ui.filter.FilterLayoutUtils;
import com.letv.recorder.ui.filter.FilterShowListener;
import com.letv.recorder.ui.logic.RecorderConstance;
import com.letv.recorder.util.NetworkUtils;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;

public class RecorderActivity extends OpenShareActivity implements FilterShowListener, DialogResultListener {
	private String TAG = "pushStream";
	private CameraView cameraView;
	private String path = "rtmp://216.mpush.live.lecloud.com/live/camerView";
	// 播放地址:rtmp://216.mpull.live.lecloud.com/live/camerView
	private String packageName = "";
	private ImageView flashImg;
	private ImageView cameraImg;
	private ImageView voiceImg;
	private TextView pushTime;
	private RelativeLayout mFilterLayout;
	private FrameLayout surfaceContainer;
	private static boolean isShowFilter = false;
	private Context context;
	private CreateMobileLiveDialog mobileLivedialog;
	private FrameLayout topFloatView;
	private FrameLayout bottomFloatView;
	private String pushSteamUrl;
	private ProgressDialog progressDialog;
	private String shareUrl = "";
	private String mCorverUrl = "";
	private String anchorName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		context = RecorderActivity.this;
		setContentView(R.layout.letv_recorder_noskin_main);
		cameraView = (CameraView) findViewById(R.id.camera_view);
		topFloatView = (FrameLayout) findViewById(R.id.letv_recorder_top_container);
		bottomFloatView = (FrameLayout) findViewById(R.id.letv_recorder_bottom_container);
		// 底部事件
		flashImg = (ImageView) findViewById(R.id.imgV_flashlight);
		flashImg.setOnClickListener(listener);// 闪光灯点击事件
		findViewById(R.id.imgV_postposition_filter).setOnClickListener(listener);// 滤镜点击事件
		cameraImg = (ImageView) findViewById(R.id.imgV_postposition_camera);
		cameraImg.setOnClickListener(listener);// 切换摄像头点击事件
		voiceImg = (ImageView) findViewById(R.id.imgV_voice);
		voiceImg.setOnClickListener(listener);// 声音点击事件
		// 滤镜
		surfaceContainer = (FrameLayout) findViewById(R.id.letv_recorder_surface_container);
		mFilterLayout = (RelativeLayout) findViewById(R.id.layout_filter);
		FilterLayoutUtils mFilterLayoutUtils = new FilterLayoutUtils(context,
				Publisher.getInstance().getVideoRecordDevice(), this);
		mFilterLayoutUtils.init(mFilterLayout, (SurfaceFrameLayout) surfaceContainer);
		// 顶部事件
		findViewById(R.id.imgB_back).setOnClickListener(listener);
		findViewById(R.id.control_share).setOnClickListener(listener);
		pushTime = (TextView) findViewById(R.id.tv_time);
		cameraView.setTime(pushTime);
		// 开始直播
		cameraView.init(this, getDefaultHandler(), false);
		View view = findViewById(R.id.focusView);
		cameraView.setFocusView(view);
		if (cameraView.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			flashImg.setVisibility(View.INVISIBLE);
		} else {
			flashImg.setVisibility(View.VISIBLE);
		}
		mobileLivedialog = new CreateMobileLiveDialog();
		hideBottomSkin();// 隐藏播放器皮肤
		BaseDialog.show(RecorderActivity.this.getSupportFragmentManager(), mobileLivedialog);
		hideTopSkin();// 隐藏播放器皮肤
		pushSteamUrl = getIntent().getStringExtra("recordUrl");
		shareUrl = getIntent().getStringExtra("shareUrl");
		anchorName = LoginInfoUtil.getUserName(getApplicationContext());
	}

	protected void init() {
		try {
			Intent intent = getIntent();
			if (mWeiboShareAPI == null) {
				OpenSdk openSdk = MyApplication.getInstance().getOpenSdk();
				if (openSdk.hasBLOG()) {
					IWeiboShareAPI mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, openSdk.BLOGID);
					mWeiboShareAPI.handleWeiboResponse(intent, this);

				}
			}
		} catch (Exception e) {
			Logger.log(e);
		}
	}

	private void showProgressDialog() {
		progressDialog = new MyProgressDialog(RecorderActivity.this);
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		showProgress(true);
	}

	protected void showProgress(boolean show) {
		if (progressDialog != null) {
			if (show) {
				progressDialog.show();
			} else {
				if (progressDialog.isShowing())
					progressDialog.dismiss();
			}
		}
	}

	@Override
	public void onDialogResult(int requestCode, int resultCode, Bundle arguments) {
		if (resultCode == Activity.RESULT_CANCELED) {
			finish();
		}
		if (resultCode == Activity.RESULT_OK) {
			mCorverUrl = arguments.getString("coverUrl");
			if (mobileLivedialog != null && mobileLivedialog.isVisible()) {
				mobileLivedialog.dismiss();
			}
			showTopSkin();
			showBottomSkin();
			showProgressDialog();
			getDefaultHandler().postDelayed(new Runnable() {

				@Override
				public void run() {
					cameraView.publish(pushSteamUrl, 0);
				}
			}, 500);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		cameraView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		cameraView.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cameraView.onDestroy();
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.imgV_flashlight:// 切换闪光灯
				boolean flashFlag = cameraView.changeFlash();
				if (!flashFlag) {
					flashImg.setImageResource(R.drawable.letv_recorder_flash_light_close);
					flashImg.setVisibility(View.VISIBLE);
				} else {
					flashImg.setImageResource(R.drawable.letv_recorder_flash_light_open);
					flashImg.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.imgV_postposition_filter: // 切换滤镜效果
				showFilter();
				break;
			case R.id.imgV_postposition_camera:// 切换前后置摄像头
				int cameraId = cameraView.switchCamera();
				if (cameraId != 2) {
					Rotate3dAnimation animation = new Rotate3dAnimation(0, 180, cameraImg.getWidth() / 2f,
							cameraImg.getHeight() / 2f, 0f, true);
					animation.setDuration(500);
					animation.setFillAfter(true);
					cameraImg.startAnimation(animation);
					if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
						boolean flash = cameraView.closeFlash();
						cameraImg.setImageResource(R.drawable.letv_recorder_postposition_camera);
						if (!flash) {
							flashImg.setImageResource(R.drawable.letv_recorder_flash_light_close);
							flashImg.setVisibility(View.INVISIBLE);
						} else {
							flashImg.setImageResource(R.drawable.letv_recorder_flash_light_open);
							flashImg.setVisibility(View.VISIBLE);
						}
					} else if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
						cameraImg.setImageResource(R.drawable.letv_recorder_postposition_camera);
						if (!cameraView.getFlash()) {
							flashImg.setImageResource(R.drawable.letv_recorder_flash_light_close);
						} else {
							flashImg.setImageResource(R.drawable.letv_recorder_flash_light_open);
						}
						flashImg.setVisibility(View.VISIBLE);
					}
				}
				break;
			case R.id.imgV_voice:// 切换声音
				int voice = cameraView.setVolume();
				if (voice == 1) {
					voiceImg.setImageResource(R.drawable.letv_recorder_voise_open);
				} else {
					voiceImg.setImageResource(R.drawable.letv_recorder_voise_close);
				}
				break;
			case R.id.imgB_back:
				showExitMobileLiveDialog();
				break;
			case R.id.control_share:
				Share();
				break;
			}
		}
	};

	public void Share() {
		boolean isNoNetwork = PlayerAPI.addNoNetworkLimit(context);
		if (isNoNetwork) {
			return;
		}
		if (context instanceof OpenShareActivity) {
			if (!MyApplication.getInstance().getOpenSdk().hasOpenId()) {
				((BaseActivity) context).showToastSafe(getResources().getString(R.string.pleasewait),
						Toast.LENGTH_SHORT);
				return;
			}
			packageName = SystemUtls.getApplicationName(context);
			final OpenShareActivity shareActivity = (OpenShareActivity) context;
			OpenShareDialog dialog = new OpenShareDialog();
			dialog.setOnShareListener(new OnShareListener() {

				@Override
				public void doShare(int openType) {
					shareActivity.doshare(openType, shareUrl, packageName,
							anchorName + getResources().getString(R.string.letv_mobilelive_share_desc), mCorverUrl);
				}
			});
			BaseDialog.show(shareActivity.getSupportFragmentManager(), dialog);

		}
	}

	public void showFilter() {
		isShowFilter = true;
		ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", mFilterLayout.getHeight(), 0);
		animator.setDuration(200);
		animator.addListener(new Animator.AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				mFilterLayout.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
		animator.start();
	}

	public void hideFilter() {
		isShowFilter = false;
		ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", 0, mFilterLayout.getHeight());
		animator.setDuration(200);
		animator.addListener(new Animator.AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@SuppressLint("NewApi")
			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				mFilterLayout.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				mFilterLayout.setVisibility(View.INVISIBLE);
			}
		});
		animator.start();
	}

	public boolean isShowFilter() {
		return isShowFilter;
	}

	private void hideTopSkin() {
		if (topFloatView != null) {
			topFloatView.setVisibility(View.INVISIBLE);
		}
	}

	private void hideBottomSkin() {
		if (bottomFloatView != null) {
			bottomFloatView.setVisibility(View.GONE);
		}
	}

	public void showBottomSkin() {
		if (bottomFloatView != null) {
			bottomFloatView.setVisibility(View.VISIBLE);
		}
	}

	public void showTopSkin() {
		if (topFloatView != null) {
			topFloatView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 退出直播dialog
	 */
	public void showExitMobileLiveDialog() {
		final MobileLiveDialog mobileExitDialog = new MobileLiveDialog();
		Bundle bundle = new Bundle();
		bundle.putString(MobileLiveDialog.MOBILE_LIVE_DIALOG_TITLE,
				getResources().getString(R.string.letv_record_is_exit_mobilelive));
		bundle.putString(MobileLiveDialog.MOBILE_LIVE_DIALOG_OK,
				getResources().getString(R.string.letv_record_continue_mobilelive));
		bundle.putString(MobileLiveDialog.MOBILE_LIVE_DIALOG_CANCEL,
				getResources().getString(R.string.letv_record_exit_mobilelive));
		mobileExitDialog.setArguments(bundle);
		mobileExitDialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ok
				mobileExitDialog.dismiss();
			}
		}, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// cancle
				quitMobilelive();
				finish();
			}
		});
		BaseDialog.show(RecorderActivity.this.getSupportFragmentManager(), mobileExitDialog);

	}

	public void quitMobilelive() {
		new AsyncTask<Void, Void, Integer>() {

			@Override
			protected Integer doInBackground(Void... params) {
				QuitLiveNotifyDataRequest request = new QuitLiveNotifyDataRequest(RecorderActivity.this);
				Map<String, String> mInputParam = new HashMap<String, String>();
				mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(context));
				mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
				mInputParam.put("state", "0");
				return request.setInputParam(mInputParam).request(Request.Method.GET);
			}

			protected void onPostExecute(Integer result) {

			};
		}.execute();

	}

	@Override
	protected void onHandleMessage(Message msg) {
		switch (msg.what) {
		case CameraView.PUSHSTREM_URL_NULL:
			Log.i(TAG, "推流地址为空");
			getDefaultHandler().post(new Runnable() {
				@Override
				public void run() {
					showProgress(false);
				}
			});
			break;
		case RecorderConstance.RECORDER_OPEN_URL_SUCESS:
			Log.i(TAG, "推流连接成功:只有当连接成功以后才能开始推流");
			break;
		case RecorderConstance.RECORDER_OPEN_URL_FAILED:
			getDefaultHandler().post(new Runnable() {
				@Override
				public void run() {
					showProgress(false);
				}
			});
			getBreakStreamReason();
			Log.i(TAG, "推流连接失败:如果失败,大多是推流地址不可用或者网络问题");
			break;
		case RecorderConstance.RECORDER_PUSH_FIRST_SIZE:
			Log.i(TAG, "第一针画面推流成功,代表成功的开始推流了:推流成功的标志回调");
			getDefaultHandler().post(new Runnable() {
				@Override
				public void run() {
					showProgress(false);
				}
			});
			cameraView.calculateTime();
			break;
		case RecorderConstance.RECORDER_PUSH_AUDIO_PACKET_LOSS_RATE:
			Log.i(TAG, "音频出现丢帧现象。如果一分钟丢帧次数大于5次,导致声音跳动:可以对网络进行判定");
			break;
		case RecorderConstance.RECORDER_PUSH_VIDEO_PACKET_LOSS_RATE:
			Log.d(TAG, "视频出现丢帧现象,如果一分钟丢帧次数大于5次,导致画面跳动:可以对网络进行判定");
			break;
		case RecorderConstance.RECORDER_PUSH_ERROR:
			Log.i(TAG, "推流失败,原因:网络较差,编码出错,推流崩溃,第一针数据发送失败...等等各种原因导致");
			getBreakStreamReason();
			break;
		case RecorderConstance.RECORDER_PUSH_STOP_SUCCESS:
			Log.d(TAG, "成功的关闭了底层推流,可以进行下次推流了:保证推流成功关闭");
			getDefaultHandler().post(new Runnable() {
				@Override
				public void run() {
					showProgress(false);
				}
			});
			break;
		}
	}

	// 获取断流原因
	private void getBreakStreamReason() {
		final MobileLiveStreamInfo info = new MobileLiveStreamInfo();
		new AsyncTask<Void, Void, Integer>() {

			@Override
			protected Integer doInBackground(Void... params) {
				GetMobileLiveStreamInfoDataRequest request = new GetMobileLiveStreamInfoDataRequest(context);
				Map<String, String> mInputParam = new HashMap<String, String>();
				mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(context));
				mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
				return request.setInputParam(mInputParam).setOutputData(info).request(Request.Method.GET);
			}

			protected void onPostExecute(Integer result) {
				String alertContent = null;
				if (result == 0) {
					alertContent = info.cause;
				} else {
					alertContent = ((NetworkUtils.getNetType(context) == null) ? "网络异常," : "") + "无法连接推流服务器";
				}

				final Dialog dialog = new Dialog(RecorderActivity.this, R.style.Dialog);
				dialog.setContentView(R.layout.mobile_live_dialog);
				TextView cancel = (TextView) dialog.findViewById(R.id.dialog_cancel);
				dialog.findViewById(R.id.dialog_ok).setVisibility(View.GONE);
				cancel.setText(R.string.letv_record_exit_mobilelive);
				TextView causeText = (TextView) dialog.findViewById(R.id.mobile_live_title);
				causeText.setText(alertContent);
				cancel.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// cancle
						dialog.dismiss();
						finish();
					}
				});
				dialog.setCanceledOnTouchOutside(false);
				dialog.setCancelable(false);
				dialog.show();
				// final MobileLiveDialog mobileErrorDialog = new
				// MobileLiveDialog();
				// Bundle bundle = new Bundle();
				// bundle.putString(MobileLiveDialog.MOBILE_LIVE_DIALOG_TITLE,
				// alertContent);
				// bundle.putString(MobileLiveDialog.MOBILE_LIVE_DIALOG_CANCEL,
				// getResources().getString(R.string.letv_record_exit_mobilelive));
				// mobileErrorDialog.setArguments(bundle);
				// mobileErrorDialog.setOnClickListener(new OnClickListener() {
				//
				// @Override
				// public void onClick(DialogInterface dialog, int which) {
				// // ok
				// }
				// }, new OnClickListener() {
				//
				// @Override
				// public void onClick(DialogInterface dialog, int which) {
				// // cancle
				// mobileErrorDialog.dismiss();
				// finish();
				// }
				// });
				// BaseDialog.show(RecorderActivity.this.getSupportFragmentManager(),
				// mobileErrorDialog);
				// // dialog.setDialogTitle(alertContent);
				// // dialog.setDialogCancel("我知道了");
				// // dialog.setDialogOk(null);
			};
		}.execute();
	}

	@Override
	public void onBackPressed() {
		showExitMobileLiveDialog();
	}

}
