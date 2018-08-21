package com.letv.autoapk.ui.mobilelive;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.android.volley.Request;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.constant.StatusCode;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.lecloud.sdk.videoview.mobile.MobileLiveVideoView;
import com.letv.autoapk.R;
import com.letv.autoapk.base.dialog.BaseDialog;
import com.letv.autoapk.base.fragment.BaseFragment;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.MyProgressDialog;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.common.net.LruImageCache;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.open.OpenShareActivity;
import com.letv.autoapk.open.OpenShareDialog;
import com.letv.autoapk.open.OpenShareDialog.OnShareListener;
import com.letv.autoapk.ui.player.PlayerAPI;
import com.letv.autoapk.utils.SystemUtls;
import com.letv.autoapk.widgets.CircleImageView;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 直播——移动直播页面(用户)
 * 
 * @author wangzhen5 <br>
 *         如果该页面的入口在首页发现页等页面，与点击移动直播列表直接获取主播信息不同，<br>
 *         主播头像主播昵称等信息需要单独请求一个接口来获取，
 * 
 */
public class MobileLiveVideoFragment extends BaseFragment implements OnClickListener {

	// 移动直播播放器
	private MobileLiveVideoView videoView;
	public final static String DATA = "data";
	private String mPlayUrl = "";
	private String mCorverUrl = "";
	private String mShareUrl = "";
	private RelativeLayout videoContainer;
	private View view;
	private String packageName = "";
	/**
	 * 以下两参数用来计算是否一直在缓冲
	 */
	private boolean isBufferring;
	private int couter;

	private void initActionLiveVideoView() {

		View notWatch = view.findViewById(R.id.control_close);// 不再观看
		View shareLive = view.findViewById(R.id.control_share);// 直播分享
		CircleImageView anchorHeadImg = (CircleImageView) view.findViewById(R.id.anchor_headimg);//
		TextView anchorName = (TextView) view.findViewById(R.id.anchor_name);//

		notWatch.setOnClickListener(this);
		shareLive.setOnClickListener(this);
		anchorHeadImg.setImageUrl(this.anchorHeadImg, LruImageCache.getImageLoader(mActivity));
		anchorName.setText(this.anchorName);

		videoView = new MobileLiveVideoView(getActivity(),MyApplication.getInstance().getTenantId());
		videoView.setCacheWatermark(800, 200);
		videoView.setMaxDelayTime(1000);
		videoView.setCachePreSize(500);
		videoView.setCacheMaxSize(10000);
		videoView.setVideoViewListener(mVideoViewListener);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		videoContainer.addView((View) videoView, params);
		placeImg = new View(mActivity);
		placeImg.setBackgroundResource(R.drawable.letv_record_default_img_discover);
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		videoContainer.addView(placeImg, params);

		if (!TextUtils.isEmpty(mPlayUrl)) {
			videoView.setDataSource(mPlayUrl);
		}

		showProgressDialog();
	}

	private void initData() {
		Intent intent = getActivity().getIntent();
		if (intent != null) {
			Bundle mBundle = intent.getExtras();
			final String anchorId;
			if (mBundle == null) {
				Toast.makeText(mActivity, "no data", Toast.LENGTH_LONG).show();
				return;
			} else {
				mShareUrl = mBundle.getString("shareUrl");
				mCorverUrl = mBundle.getString("coverUrl");
				mPlayUrl = mBundle.getString("liveUrl");
				anchorHeadImg = mBundle.getString("anchorHeadImg");
				anchorName = mBundle.getString("anchorName");
				anchorId = mBundle.getString("anchorId");
			}
			if (!TextUtils.isEmpty(anchorId)) {// 非移动直播列表页开启的观看直播
				final MobileLiveStreamInfo info = new MobileLiveStreamInfo();
				new UiAsyncTask<Integer>(this) {

					@Override
					protected Integer doBackground() throws Throwable {
						String[] anchorInfos = anchorId.split("_");
						GetMobileLiveStreamInfoDataRequest request = new GetMobileLiveStreamInfoDataRequest(mActivity);
						Map<String, String> mInputParam = new HashMap<String, String>();
						mInputParam.put(StringDataRequest.USER_ID, anchorInfos[1]);
						mInputParam.put(StringDataRequest.TENANT_ID, anchorInfos[0]);
						return request.setInputParam(mInputParam).setOutputData(info).request(Request.Method.GET);
					}

					protected void post(Integer result) {
						if (result == 0) {
							mShareUrl = info.shareUrl;
							mCorverUrl = info.coverPic;
							mPlayUrl = info.liveUrl;
							anchorHeadImg = info.headPic;
							anchorName = info.userName;
							initActionLiveVideoView();
						}
					};
				}.execute();
			} else {
				initActionLiveVideoView();
			}

		}
	}

	VideoViewListener mVideoViewListener = new VideoViewListener() {

		@Override
		public void onStateResult(int event, Bundle bundle) {
			handleVideoInfoEvent(event, bundle);// 处理视频信息事件
			handlePlayerEvent(event, bundle);// 处理播放器事件
			handleLiveEvent(event, bundle);// 处理直播类事件,如果是点播，则这些事件不会回调

		}

		@Override
		public String onGetVideoRateList(LinkedHashMap<String, String> arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		// @Override
		// public String onGetVideoRateList(LinkedHashMap<String, String> map) {
		// for(Map.Entry<String,String> rates:map.entrySet()){
		// if(rates.getValue().equals("高清")){
		// return rates.getKey();
		// }
		// }
		// return "";
		// }
	};
	private String anchorHeadImg;
	private String anchorName;
	private View placeImg;
	private MyProgressDialog progressDialog;

	/**
	 * 处理播放器本身事件，具体事件可以参见IPlayer类
	 */
	private void handlePlayerEvent(int state, Bundle bundle) {
		switch (state) {
		case PlayerEvent.PLAY_VIDEOSIZE_CHANGED:
			/**
			 * 获取到视频的宽高的时候，此时可以通过视频的宽高计算出比例，进而设置视频view的显示大小。
			 * 如果不按照视频的比例进行显示的话，(以surfaceView为例子)内容会填充整个surfaceView。
			 * 意味着你的surfaceView显示的内容有可能是拉伸的
			 */
			break;

		case PlayerEvent.PLAY_PREPARED:
			// 播放器准备完成，此刻调用start()就可以进行播放了
			if (videoView != null) {
				videoView.onStart();
			}
			break;
		default:
			break;
		}
	}

	private void showProgressDialog() {
		progressDialog = new MyProgressDialog(mActivity);
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

	/**
	 * 处理直播类事件
	 */
	private void handleLiveEvent(int state, Bundle bundle) {
		Message bufferMsg = new Message();
		bufferMsg.what = 12345;
		Log.e("wz", state + "");
		switch (state) {
		case PlayerEvent.PLAY_COMPLETION:
			showProgress(false);
			showMobileliveFinishDialog();
			break;
		case PlayerEvent.PLAY_PREPARED:
			placeImg.setVisibility(View.GONE);
			break;
		case PlayerEvent.PLAY_BUFFERING:
			// // 主播主动断流，会回调206,201
			// // 5s 后断流
			break;
		case PlayerEvent.PLAY_INFO:
			// 开始播的时候，会回调500006，渲染第一针，但是关闭直播的时候，播放器会回调开始缓冲
			int code = bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE);
			if ((code == 0 || code == 500004) && !getDefaultHandler().hasMessages(12345)) {
				getDefaultHandler().sendMessageDelayed(bufferMsg, 10000);
			} else {
				getDefaultHandler().removeMessages(12345);
			}
			if (code == StatusCode.PLAY_INFO_BUFFERING_START) {
			}
			if (code == StatusCode.PLAY_INFO_BUFFERING_END) {
			}
			if (code == StatusCode.PLAY_INFO_VIDEO_RENDERING_START) {
				showProgress(false);
			}

			break;
		case PlayerEvent.PLAY_ERROR:
			placeImg.setVisibility(View.VISIBLE);
			showProgress(false);
			showMobileliveFinishDialog();
			break;

		default:
			break;
		}
	}

	/**
	 * 处理视频信息类事件
	 */
	private void handleVideoInfoEvent(int state, Bundle bundle) {

	}

	@Override
	protected void onHandleMessage(Message msg) {
		if (msg.what == 12345) {
			showMobileliveFinishDialog();
		}

	}

	@Override
	protected View setupDataView() {
		getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		view = View.inflate(mActivity, R.layout.letv_record_play_controller, null);
		videoContainer = (RelativeLayout) view.findViewById(R.id.video_container);
		initMobileLiveController();
		initData();
		return view;
	}

	private void initMobileLiveController() {

	}

	@Override
	public void onResume() {
		super.onResume();
		if (videoView != null) {
			videoView.onResume();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (getDefaultHandler().hasMessages(12345)) {
			getDefaultHandler().removeMessages(12345);
		}
		if (videoView != null) {
			videoView.onPause();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (videoView != null) {
			videoView.onDestroy();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// mActivity.showToastSafe("zhuanping", 0);
		super.onConfigurationChanged(newConfig);
		if (videoView != null) {
			videoView.onConfigurationChanged(newConfig);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.control_close:
			getActivity().finish();
			break;
		case R.id.control_share:
			// title：APP的名称；正文：“XXX正在直播，快来一起看”；链接地址：H5页面的URL地址；
			boolean isNoNetwork = PlayerAPI.addNoNetworkLimit(mActivity);
			if (isNoNetwork) {
				return;
			}
			if (mActivity instanceof OpenShareActivity) {
				if (!MyApplication.getInstance().getOpenSdk().hasOpenId()) {
					mActivity.showToastSafe(getResString(R.string.pleasewait), Toast.LENGTH_SHORT);
					return;
				}
				packageName = SystemUtls.getApplicationName(getActivity());
				final OpenShareActivity shareActivity = (OpenShareActivity) mActivity;
				OpenShareDialog dialog = new OpenShareDialog();
				dialog.setOnShareListener(new OnShareListener() {

					@Override
					public void doShare(int openType) {
						shareActivity.doshare(openType, mShareUrl, packageName,
								anchorName + getResString(R.string.letv_mobilelive_share_desc), mCorverUrl);
					}
				});
				BaseDialog.show(shareActivity.getSupportFragmentManager(), dialog);

			}
			break;

		default:
			break;
		}

	}

	/**
	 * zhibo finish
	 */
	public void showMobileliveFinishDialog() {
		final Dialog dialog = new Dialog(mActivity, R.style.Dialog);
		dialog.setContentView(R.layout.letv_record_mobilelive_finish);
		View cancel = dialog.findViewById(R.id.cancel);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		// TextView causeText = (TextView)
		// dialog.findViewById(R.id.clear_dialog_title);
		// causeText.setText(cause);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				getActivity().finish();
			}
		});
		if (!mActivity.isFinishing()) {
			dialog.show();
		}
	}

}
