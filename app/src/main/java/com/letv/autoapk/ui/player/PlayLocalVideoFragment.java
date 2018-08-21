package com.letv.autoapk.ui.player;

import java.io.File;
import java.util.Date;
import java.util.LinkedHashMap;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.constant.StatusCode;
import com.lecloud.sdk.surfaceview.ISurfaceView;
import com.lecloud.sdk.surfaceview.impl.BaseSurfaceView;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.letv.android.client.cp.sdk.player.vod.CPVodPlayer;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.PlayVideoActivity;
import com.letv.autoapk.base.activity.PlayVideoActivity.onBackPressedListener;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.base.fragment.BaseFragment;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.player.ISplayerController;
import com.letv.autoapk.player.SaasVideoView;
import com.letv.autoapk.ui.player.view.PlayControlContainer;
import com.letv.autoapk.ui.player.view.PlaySeekBar;
import com.letv.autoapk.ui.player.view.PlayerContainerLayout;
import com.letv.autoapk.utils.TimerUtils;

public class PlayLocalVideoFragment extends BaseFragment {

	private static final String TAG = PlayLocalVideoFragment.class
			.getSimpleName();
	VideoViewListener videoViewListener = new VideoViewListener() {

		@Override
		public void onStateResult(int state, Bundle bundle) {
			if (player == null || getActivity() == null) {
				return;
			}
			switch (state) {
			case PlayerEvent.PLAY_VIDEOSIZE_CHANGED:
				int vwidth = bundle.getInt(PlayerParams.KEY_WIDTH);
				int vheight = bundle.getInt(PlayerParams.KEY_HEIGHT);
				ISurfaceView ssurfaceView = surfaceView.getSurfaceView();
				if (ssurfaceView instanceof BaseSurfaceView) {
					((BaseSurfaceView) ssurfaceView).onVideoSizeChanged(vwidth, vheight);
				}
				break;
			case PlayerEvent.PLAY_PREPARED:
				hideMsg();
				if (lastPosition > 0) {
					player.seekTo(lastPosition);
				}
				player.start();
				lastOpenTime = new Date().getTime();
				getDefaultHandler().sendEmptyMessage(PlayConst.ONSTART);
				playTimer.sendEmptyMessage(PlayConst.STARTTIME);
				getDefaultHandler().sendEmptyMessage(PlayConst.ONSTART);
				break;

			case PlayerEvent.PLAY_ERROR:

				int errorCode = bundle.getInt(
						PlayerParams.KEY_RESULT_ERROR_CODE, 0);
				if (errorCode == StatusCode.PLAY_ERROR_DECODE)
					showErrorMsg(getResString(R.string.media_decoding_error));
				if (errorCode == StatusCode.PLAY_ERROR_UNKNOWN) {
					showErrorMsg(getResString(R.string.media_not_exist));
				}
				break;
			case PlayerEvent.PLAY_INFO:
				int statusCode = bundle.getInt(
						PlayerParams.KEY_RESULT_STATUS_CODE, 0);
				if (statusCode == StatusCode.PLAY_INFO_VIDEO_RENDERING_START) {
					if (uicontext.isClickPauseByUser()) {
						surfaceView.onPause();
					}
				}
				break;
			case PlayerEvent.PLAY_SEEK_COMPLETE:
				playTimer.removeMessages(PlayConst.STARTTIME);
				playTimer.sendEmptyMessage(PlayConst.STARTTIME);
				if (helper != null && uicontext.isClickPauseByUser()) {
					helper.playOrpause(controllayout
							.findViewById(R.id.control_play));
				}
				break;

			case PlayerEvent.PLAY_COMPLETION:// 播放完成
				playTimer.sendEmptyMessage(PlayConst.STOPTIME);
				player.retry();
				break;
			}

		}

		@Override
		public String onGetVideoRateList(LinkedHashMap<String, String> arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	class PlayListener implements OnClickListener, OnSeekBarChangeListener,
			OnItemClickListener, OnTouchListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.control_lock:
				if (uicontext.isLockFlag()) {
					uicontext.setLockFlag(false);
					GestureControl gestureControl = new GestureControl(
							mActivity, controllayout, getDefaultHandler());
					gestureControl.setSeekable(true);
					controllayout.setOnTouchListener(gestureControl);
					getDefaultHandler().sendEmptyMessage(PlayConst.SHOWCONTROL);
					((ImageView) v).setImageResource(R.drawable.play_lock_off);
					mActivity.showToastSafe(getResString(R.string.unlock),
							Toast.LENGTH_SHORT);
				} else {
					uicontext.setLockFlag(true);
					controllayout.setOnTouchListener(listener);
					((ImageView) v).setImageResource(R.drawable.play_lock);
					getDefaultHandler().sendEmptyMessage(PlayConst.HIDECONTROL);
					mActivity.showToastSafe(getResString(R.string.locked),
							Toast.LENGTH_SHORT);
				}

				break;
			case R.id.control_back:
				getActivity().finish();
				break;
			case R.id.control_play:
				if (helper != null)
					helper.playOrpause(v);
				break;

			case R.id.control_choose:
				if (helper != null) {
					BaseAdapter adapter = helper.initmenu(PlayHelper.CHOOSE,
							mActivity, mBundle);
					if (adapter == null)
						return;
					menuListView.setAdapter(adapter);
					menuListView.setOnItemClickListener(this);
					menuListView.setTag(id);
					getDefaultHandler().sendEmptyMessage(PlayConst.SHOWMENU);
				}

				break;
			default:
				break;
			}

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) positionView
					.getLayoutParams();
			params.leftMargin = (seekBar.getWidth() - positionView
					.getWidth()) * progress / 1000;
			positionView.requestLayout();

			if (seekBar instanceof PlaySeekBar) {
				((PlaySeekBar) seekBar).doRefresh(progress);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			helper.startTrackingTouch(seekBar);
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			helper.stopTrackingTouch(seekBar);
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			int menuid = (Integer) parent.getTag();
			if (menuid == R.id.control_ratetype) {
			}
			if (menuid == R.id.control_choose) {
			}
			if (menuid == R.id.control_more) {
			}
			getDefaultHandler().sendEmptyMessage(PlayConst.HIDEMENU);
			menuListView.setAdapter(null);
			menuListView.setOnItemClickListener(null);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			boolean onclick = v.onTouchEvent(event);
			if (v == menulayout && onclick == false) {
				getDefaultHandler().sendEmptyMessage(PlayConst.HIDEMENU);
			}
			if (v == controllayout) {
				View childView = controllayout.findViewById(R.id.control_lock);
				childView.setVisibility(childView.isShown() ? View.INVISIBLE
						: View.VISIBLE);
				childView.requestLayout();
			}
			return onclick;
		}

	}

	void hideMsg() {
		msglayout.setVisibility(View.INVISIBLE);
	}

	void showBufferMsg(String msg) {
		msglayout.findViewById(R.id.progress_wheel).setVisibility(View.VISIBLE);
		msgView.setText(msg);
		msglayout.setVisibility(View.VISIBLE);
	}

	void showErrorMsg(String msg) {
		msglayout.findViewById(R.id.progress_wheel).setVisibility(View.GONE);
		msgView.setText(msg);
		msglayout.setVisibility(View.VISIBLE);
	}

	void showMenulayout() {
		menulayout.show();
	}

	void hideMenulayout() {
		menulayout.hide();
	}

	void showControllayout() {
		GestureControl gestureControl = new GestureControl(mActivity,
				controllayout, getDefaultHandler());
		gestureControl.setSeekable(true);
		controllayout.setVisibility(View.VISIBLE);
		controllayout.setOnTouchListener(gestureControl);
	}

	void hideControllayout() {
		controllayout.setVisibility(View.GONE);
		controllayout.setOnTouchListener(null);
	}

	Handler playTimer = new Handler() {
		private long setProgress() {
			long position = getPosition();
			long duration = getDuration();
			if (duration > 0) {
				/**
				 * 缓冲进度
				 */
//				long percentage = player.getBufferPercentage();
				if (player.isPlaying()) {
					if (seekBar.isHovered() == false) {
						long pos = 1000L * position / duration;
						seekBar.setProgress((int) pos);
					}

					positionView.setText(TimerUtils
							.stringForTime((int) position / 1000));
					durationView.setText(TimerUtils
							.stringForTime((int) duration / 1000));
					durationView.setTag(duration);
				}
			}

			return position;
		}

		private long getDuration() {
			if (player == null) {
				return 0;
			}
			return player.getDuration();
		}

		private long getPosition() {
			if (player == null) {
				return 0;
			}
			return player.getCurrentPosition();
		}

		void reset() {
			try {
				removeMessages(PlayConst.STARTTIME);
			} catch (Exception e) {
				Logger.log(e);
			}
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case PlayConst.STOPTIME:
				reset();
				controllayout.findViewById(R.id.control_play)
						.setBackgroundResource(R.drawable.play_pause);
				break;
			case PlayConst.STARTTIME:
				long position = setProgress();
				if (player != null && player.isPlaying()) {
					sendEmptyMessageDelayed(PlayConst.STARTTIME,
							1000 - (position % 1000));
				}
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onHandleMessage(Message msg) {
		// TODO Auto-generated method stub
		int what = msg.what;
		switch (what) {
		case PlayConst.HIDEMENU:
			hideMenulayout();
			showControllayout();
			getDefaultHandler().sendEmptyMessage(PlayConst.SHOWCONTROL);
			break;
		case PlayConst.SHOWMENU:
			hideControllayout();
			showMenulayout();
			break;
		case PlayConst.SHOWORHIDECONTROL:
			int scount = controllayout.getChildCount();
			boolean isshow = false;
			for (int i = 0; i < scount; i++) {
				View child = controllayout.getChildAt(i);
				if (child instanceof PlayControlContainer) {
					isshow = child.isShown();
					((PlayControlContainer) child).showOrhide();
				} else if (child.getId() == R.id.control_lock) {
					child.setVisibility(isshow ? View.INVISIBLE : View.VISIBLE);
				}
			}
			break;
		case PlayConst.HIDECONTROL:
			int count = controllayout.getChildCount();
			for (int i = 0; i < count; i++) {
				View child = controllayout.getChildAt(i);
				if (child instanceof PlayControlContainer) {
					((PlayControlContainer) child).hide();
				} else if (child.getId() == R.id.control_lock) {
					child.setVisibility(View.INVISIBLE);
				}
			}
			break;
		case PlayConst.SHOWCONTROL:
			int count2 = controllayout.getChildCount();
			for (int i = 0; i < count2; i++) {
				View child = controllayout.getChildAt(i);
				if (child instanceof PlayControlContainer) {
					((PlayControlContainer) child).show();
				} else if (child.getId() == R.id.control_lock) {
					child.setVisibility(View.VISIBLE);
				}
			}
			getDefaultHandler().sendEmptyMessageDelayed(PlayConst.HIDECONTROL,
					4000);
			break;
		case PlayConst.CONTROL:
			if (uicontext.isPlayingAd() || menulayout.isShown()) {
				hideControllayout();
			} else {
				showControllayout();
				getDefaultHandler().sendEmptyMessageDelayed(
						PlayConst.HIDECONTROL, 4000);
			}

			break;
		case PlayConst.ONSTART:
			seekBar.setVisibility(View.VISIBLE);
			if (player.isPlaying()) {
				controllayout.findViewById(R.id.control_play)
						.setBackgroundResource(R.drawable.play_pause);
			} else {
				controllayout.findViewById(R.id.control_play)
						.setBackgroundResource(R.drawable.play_play);
			}

			break;
		case PlayConst.SEEKEND:
			if (helper != null)
				helper.stopTrackingTouch(seekBar);
			getDefaultHandler().sendEmptyMessageDelayed(PlayConst.HIDECONTROL,
					4000);
			break;
		case PlayConst.SEEKSTART:
			if (helper != null) {
				helper.startTrackingTouch(seekBar);
				getDefaultHandler().sendEmptyMessage(PlayConst.SHOWCONTROL);
			}

			break;
		case PlayConst.SEEKTO:
			if (seekBar != null) {
				seekBar.setProgress(helper.progress + msg.arg1);
			}
			mTextSeekTo
					.setText(getPlayerProgress() + "/" + getPlayerDuration());
		default:
			break;
		}

	}

	public CharSequence getPlayerProgress() {
		String progress = TimerUtils.stringForTime((int) (seekBar.getProgress()
				* player.getDuration() / 1000 / 1000));
		return progress;
	}

	public CharSequence getPlayerDuration() {
		String duration = TimerUtils
				.stringForTime((int) player.getDuration() / 1000);
		return duration;
	}

	private PlayListener listener = new PlayListener();
	private PlayHelper helper;
	private Bundle mBundle;
	private long lastPosition = 0;
	private boolean isPanoVideo;
	private UiPlayContext uicontext;
	private CPVodPlayer player;

	private SeekBar seekBar;
	private ViewGroup videolayout;
	private View msglayout;
	private RelativeLayout controllayout;
	private PlayControlContainer menulayout;
	private SaasVideoView surfaceView;
	private TextView positionView;
	private TextView durationView;
	private TextView titleView;
	private TextView msgView;
	private TextView mTextSeekTo;
	private FrameLayout detailContainer;
	/** 上次打开时间 */
	private long lastOpenTime;
	private String path;
	private ListView menuListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mActivity
				.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		mActivity.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		try {
			FrameLayout frameLayout = new FrameLayout(mActivity);
			frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			frameLayout.addView(setupDataView());
			mRoot = frameLayout;
			PlayerContainerLayout viewGroup = (PlayerContainerLayout) mRoot
					.findViewById(R.id.playcontainer);
			viewGroup.changeLayoutParams();
		} catch (Exception e) {
			Logger.log(e);
		}
		return mRoot;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (surfaceView != null) {
			surfaceView.onDestroy();
			surfaceView.stopAndRelease();
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (surfaceView != null && !uicontext.isClickPauseByUser()
				&& !surfaceView.isPlaying()) {
			surfaceView.onResume();
			playTimer.removeMessages(PlayConst.STARTTIME);
			playTimer.sendEmptyMessage(PlayConst.STARTTIME);
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		playTimer.removeMessages(PlayConst.STARTTIME);
		getDefaultHandler().removeMessages(PlayConst.HIDECONTROL);
		if (player != null && surfaceView != null) {
			lastPosition = player.getCurrentPosition();
			surfaceView.onPause();
		}

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Bundle mBundle = getArguments();
		if (mBundle == null) {
			Toast.makeText(getActivity(), "no data", Toast.LENGTH_LONG).show();
			return;
		}
		init(mBundle);

		if (mActivity instanceof PlayVideoActivity) {
			((PlayVideoActivity) mActivity)
					.setOnBackPressedListener(new onBackPressedListener() {

						@Override
						public boolean onBackPressed() {
							if (menulayout != null && menulayout.isShown()) {
								getDefaultHandler().sendEmptyMessage(
										PlayConst.HIDEMENU);
								return true;
							}
							return false;
						}
					});

		}
	}

	@Override
	protected View setupDataView() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(mActivity).inflate(
				R.layout.play_localmain, null);
		videolayout = (ViewGroup) view.findViewById(R.id.play_layout);
		msglayout = view.findViewById(R.id.msg_layout);
		msgView = (TextView) msglayout.findViewById(R.id.play_msg);
		controllayout = (RelativeLayout) view.findViewById(R.id.control_layout);
		menulayout = (PlayControlContainer) view.findViewById(R.id.menu_layout);
		menulayout.setLockTouch(false);
		menuListView = (ListView) menulayout.findViewById(R.id.menulist);
		menulayout.setOnTouchListener(listener);
		initControllayout();
		return view;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (mRoot != null) {
			PlayerContainerLayout viewGroup = (PlayerContainerLayout) mRoot
					.findViewById(R.id.playcontainer);
			if (viewGroup == null)
				return;
			viewGroup.removeView(controllayout);
			controllayout = (RelativeLayout) LayoutInflater.from(mActivity)
					.inflate(R.layout.play_controller, null);
			viewGroup.addView(controllayout);
			initControllayout();
			viewGroup.changeLayoutParams();
			if (player != null && player.getDuration() > 0) {
				getDefaultHandler().sendEmptyMessage(PlayConst.ONSTART);
			}
		}

	}

	private void initControllayout() {
		controllayout.findViewById(R.id.control_play).setOnClickListener(
				listener);
		controllayout.findViewById(R.id.control_back).setOnClickListener(
				listener);
		int progress = seekBar == null ? 0 : seekBar.getProgress();
		seekBar = (SeekBar) controllayout.findViewById(R.id.control_seek);
		if (seekBar instanceof PlaySeekBar) {
			Drawable drawable = getResources().getDrawable(
					R.drawable.play_progressdrawable);
			((PlaySeekBar) seekBar).setMyProgressDrawable(drawable);
			((PlaySeekBar) seekBar).setPositionView(positionView);
		}
		seekBar.setProgress(progress);
		seekBar.setOnSeekBarChangeListener(listener);
		positionView = (TextView) controllayout.findViewById(R.id.control_time);
		durationView = (TextView) controllayout
				.findViewById(R.id.control_duration);

		titleView = (TextView) controllayout.findViewById(R.id.control_title);
		controllayout.findViewById(R.id.control_switch)
				.setVisibility(View.GONE);
		mTextSeekTo = (TextView) controllayout.findViewById(R.id.tv_seekto);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (titleView != null) {
				if (!TextUtils.isEmpty(uicontext.getVideoTitle())) {
					titleView.setText(uicontext.getVideoTitle());
				} else {
					titleView.setText(mBundle.getString("videoTitle",
							uicontext.getVideoTitle()));
				}

			}
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) durationView
					.getLayoutParams();
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			controllayout.findViewById(R.id.control_barrage).setVisibility(
					View.GONE);
			controllayout.findViewById(R.id.control_choose).setVisibility(
					View.GONE);
			// .setOnClickListener(listener);

			controllayout.findViewById(R.id.control_ratetype).setVisibility(
					View.GONE);

			controllayout.findViewById(R.id.control_more).setVisibility(
					View.GONE);
			controllayout.findViewById(R.id.control_lock).setOnClickListener(
					listener);
		}
		getDefaultHandler().sendEmptyMessage(PlayConst.CONTROL);
	}

	public void init(Bundle mBundle) {
		this.mBundle = mBundle;
		path = mBundle.getString("path", "");

		isPanoVideo = false;// mBundle.getBoolean("isPanoVideo", false);

		uicontext = new UiPlayContext();
		uicontext
				.setScreenResolution(ISplayerController.SCREEN_ORIENTATION_LANDSCAPE);
		createOnePlayer();
	}

	/**
	 * 创建一个新的播放器
	 * 
	 * @param holder
	 */
	void createOnePlayer() {
		File file = new File(path);
		if (file.exists() == false)
			return;
		if (surfaceView == null) {
			surfaceView = new SaasVideoView(mActivity);
			surfaceView.setKeepScreenOn(true);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
			videolayout.addView(surfaceView, params);
			player = surfaceView.getPlayer();
			surfaceView.setVideoViewListener(videoViewListener);
			surfaceView.setDataSource(path);
			helper = new PlayHelper(this, uicontext, player, playTimer);
			lastOpenTime = new Date().getTime();
		} else {
			player = surfaceView.getPlayer();
			surfaceView.setDataSource(path);
			helper = new PlayHelper(this, uicontext, player, playTimer);
			lastOpenTime = new Date().getTime();
		}
	}

}
