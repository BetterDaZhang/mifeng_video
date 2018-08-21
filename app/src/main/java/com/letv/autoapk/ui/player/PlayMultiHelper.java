package com.letv.autoapk.ui.player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.lecloud.sdk.api.md.entity.action.LiveInfo;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.constant.StatusCode;
import com.lecloud.sdk.player.IPlayer;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.letv.autoapk.R;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.player.ISplayerController;
import com.letv.autoapk.player.SaasLiveVideoView;
import com.letv.autoapk.ui.player.view.PlayControlContainer;

public class PlayMultiHelper implements OnClickListener {
	public LinearLayout mMultLivelayout;
	public PlayControlContainer mMultParentLayout;
	public Button mMultLiveBtn;

	public boolean isShowSubLiveView = false;
	public boolean isFirstShowSubLive = true;

	/**
	 * 需要参数
	 */
	private int mCurrentIndex;
	public static String[] numArray;

	static {
		numArray = MyApplication.getInstance().getResources()
				.getStringArray(R.array.play_placement);
	}
	/**
	 * 这个是保存每次弹出的小视屏
	 */
	public List<MultLivePlayHolder> actionPlays;
	UiPlayContext uiPlayContext;

	public PlayMultiHelper(UiPlayContext uiPlayContext,
			PlayControlContainer multParentLayout) {
		this.uiPlayContext = uiPlayContext;
		mMultParentLayout = multParentLayout;
		mMultLivelayout = (LinearLayout) mMultParentLayout
				.findViewById(R.id.multi_content);
		mMultLiveBtn = (Button) mMultParentLayout
				.findViewById(R.id.multi_control);
		actionPlays = new ArrayList<MultLivePlayHolder>();
	}

	public void addLiveView() {

		if (uiPlayContext.getActionInfo() == null) {
			return;
		}
		List<LiveInfo> liveInfos = uiPlayContext.getActionInfo().getLiveInfos();
		if (liveInfos.size() <= 1) {
			return;
		}
		actionPlays.clear();
		mMultLivelayout.removeAllViews();

		for (int i = 0; i < liveInfos.size(); i++) {
			final MultLivePlayHolder holder = new MultLivePlayHolder();
			final String url = liveInfos.get(i).getPreviewStreamPlayUrl();

			final View layout = View.inflate(mMultLivelayout.getContext(),
					R.layout.play_multiitem, null);
			TextView title = (TextView) layout.findViewById(R.id.playindex);
			final FrameLayout framelayout = (FrameLayout) layout
					.findViewById(R.id.playcontainer);
			holder.loading = (ProgressBar) framelayout
					.findViewById(R.id.multiloading);
			holder.location = i;
			holder.url = url;
			holder.no_video_layout = framelayout.findViewById(R.id.multimsg);
			holder.framelayout = framelayout;
			title.setText(MyApplication.getInstance().getString(
					R.string.placement, numArray[i]));
			holder.layout = layout;
			holder.layout.setOnClickListener(this);
			holder.layout.setTag(holder);

			mMultLivelayout.addView(layout);
			holder.textview = title;
			if (liveInfos.get(i).getLiveId()
					.equals(uiPlayContext.getMultCurrentLiveId())) {
				mCurrentIndex = i;
				actionSelected(holder, true);
				curholder = holder;
			}
			actionPlays.add(holder);
			initVideoView(holder);
			// mVideoViews.get(i).getHolder().setFormat(PixelFormat.TRANSPARENT);
			// mVideoViews.get(i).setZOrderOnTop(true);
		}
	}

	class MultLivePlayHolder {
		FrameLayout framelayout;
		TextView textview;
		ProgressBar loading;
		View no_video_layout;
		View layout;
		/**
		 * 这个表示视屏是不是正常。如果正常就为true。否则为false
		 */
		boolean videoState = true;
		int location;
		String url;
		SaasLiveVideoView videoView;
	}

	private void initVideoView(final MultLivePlayHolder mMultLivePlayHolder) {
		final SaasLiveVideoView videoView = new SaasLiveVideoView(
				mMultParentLayout.getContext());
		videoView.setLayoutParams(new ViewGroup.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mMultLivePlayHolder.framelayout.addView(videoView, 0);
		mMultLivePlayHolder.videoView = videoView;
		videoView.setDataSource(mMultLivePlayHolder.url);
		videoView.setVideoViewListener(new VideoViewListener() {

			@Override
			public void onStateResult(int event, Bundle bundle) {
				switch (event) {
				case PlayerEvent.PLAY_PREPARED:
					// 播放器准备完成，此刻调用start()就可以进行播放了
					if (videoView != null) {
						videoView.onStart();
					}
					break;

				case PlayerEvent.PLAY_ERROR:
					mMultLivePlayHolder.videoState = false;
					mMultLivePlayHolder.loading.setVisibility(View.GONE);
					mMultLivePlayHolder.no_video_layout
							.setVisibility(View.VISIBLE);
					break;
				case PlayerEvent.PLAY_VIDEOSIZE_CHANGED:
					// 设置视频比例
					// videoView.setVideoLayout(VideoViewSizeHelper.VIDEO_LAYOUT_STRETCH,
					// 0);
					break;
				case PlayerEvent.PLAY_INFO:
					int code = bundle
							.getInt(PlayerParams.KEY_RESULT_STATUS_CODE);
					if (code == StatusCode.PLAY_INFO_VIDEO_RENDERING_START) {
						mMultLivePlayHolder.videoState = true;
					}

					break;

				default:
					break;
				}

			}

			@Override
			public String onGetVideoRateList(LinkedHashMap<String, String> arg0) {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}

	/**
	 * 下面的小视屏选中和取消选中
	 */
	private void actionSelected(MultLivePlayHolder holder, boolean selected) {
		if (selected) {
			holder.framelayout.setBackgroundColor(holder.framelayout
					.getResources().getColor(R.color.code02));
			holder.textview.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.play_multiicon_p, 0, 0, 0);
			holder.textview.setTextColor(holder.textview.getResources()
					.getColor(R.color.code1));
		} else {
			holder.textview.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.play_multiicon, 0, 0, 0);
			holder.textview.setTextColor(0xffffffff);
			holder.framelayout.setBackgroundColor(Color.TRANSPARENT);
		}
	}

	/**
	 * 切换直播窗口
	 */
	public interface SwitchMultLiveCallback {
		public void switchMultLive(String liveId);
	}

	private SwitchMultLiveCallback mSwitchMultLiveCallback;

	public void setSwitchMultLiveCallbackk(
			SwitchMultLiveCallback mSwitchMultLiveCallback) {
		this.mSwitchMultLiveCallback = mSwitchMultLiveCallback;
	}

	public void onChangeConfig() {
		mMultLivelayout.setVisibility(View.GONE);
		mMultLiveBtn.setBackgroundResource(R.drawable.play_openmulti);
		mMultParentLayout.setBackgroundColor(Color.TRANSPARENT);
		if (uiPlayContext.getScreenResolution() == ISplayerController.SCREEN_ORIENTATION_PORTRAIT) {
			mMultParentLayout.setVisibility(View.INVISIBLE);
			hide();
		} else {
			mMultParentLayout.setVisibility(View.VISIBLE);
			mMultParentLayout.bringToFront();
		}

	}

	public void setLock(boolean islock) {
		if (islock && isShown())
			hide();
		mMultParentLayout.setVisibility(islock ? View.INVISIBLE : View.VISIBLE);
	}

	public boolean isShown() {
		// TODO Auto-generated method stub
		return mMultLivelayout.isShown();
	}

	public void selectIndex(int index) {
		if (actionPlays != null && index >= actionPlays.size())
			return;
		onClick(actionPlays.get(index).layout);
	}

	private boolean onclicking;
	private MultLivePlayHolder curholder;

	@Override
	public void onClick(View v) {
		if (onclicking)
			return;
		MultLivePlayHolder holder = (MultLivePlayHolder) v.getTag();
		if (holder == null)
			return;
		onclicking = true;
		
		if (holder.videoState && mCurrentIndex != holder.location) {
			if (curholder != null)
				actionSelected(curholder, false);
			curholder = holder;
			actionSelected(holder, true);
			mCurrentIndex = holder.location;
			if (mSwitchMultLiveCallback != null) {
				mSwitchMultLiveCallback
						.switchMultLive(uiPlayContext.getActionInfo()
								.getLiveInfo(mCurrentIndex).getLiveId());
			}
			hide();
		} else if (!holder.loading.isShown() && !holder.videoState
				&& holder.videoView != null) {
			if (holder.videoView.isPlaying())
				return;
			holder.videoView.setDataSource(holder.url);
			holder.no_video_layout.setVisibility(View.INVISIBLE);
			holder.loading.setVisibility(View.VISIBLE);

		}
		onclicking = false;
	}

	public void show() {
		// TODO Auto-generated method stub
		ishide = false;
		mMultLiveBtn.setBackgroundResource(R.drawable.play_close_multi);
		mMultParentLayout.show();
		if (curholder == null)
			addLiveView();
		for (MultLivePlayHolder holder : actionPlays) {
			if (holder.videoView != null) {
				holder.videoView.onResume();
			}
		}
	}
	boolean ishide;
	public void hide() {
		// TODO Auto-generated method stub
		if(ishide)
			return;
		ishide = true;
		mMultLiveBtn.setBackgroundResource(R.drawable.play_openmulti);
		for (MultLivePlayHolder holder : actionPlays) {
			if (holder.videoView != null) {
				holder.videoView.hideSurface();
			}
		}
		mMultParentLayout.hide();
	}

	public void onDestroy() {
		if (actionPlays == null)
			return;
		for (MultLivePlayHolder holder : actionPlays) {
			if (holder.videoView != null) {
				holder.videoView.onDestroy();
			}
		}
	}

}
