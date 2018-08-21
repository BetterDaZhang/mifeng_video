package com.letv.autoapk.ui.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.download.control.LeDownloadManager;
import com.lecloud.sdk.download.info.LeDownloadInfo;
import com.lecloud.sdk.download.observer.LeDownloadObserver;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.base.activity.PlayVideoActivity;
import com.letv.autoapk.base.activity.WebviewActivity;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.boss.AuthDataRequest;
import com.letv.autoapk.boss.AuthInfo;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.download.DownloadSaasCenter;
import com.letv.autoapk.download.LeOfflineInfo;
import com.letv.autoapk.player.SaasRequest;
import com.letv.autoapk.ui.mobilelive.MobileLiveAPI;
import com.letv.autoapk.ui.recommend.RecommendAPI;
import com.letv.autoapk.utils.NetworkUtils;
import com.letv.autoapk.utils.SerializeableUtil;
import com.letv.autoapk.widgets.NetImageView;

public class PlayerAPI {

	public static void initNetImgView(Context context, DisplayVideoInfo displayVideoInfo, View... views) {
		for (int i = 0; i < views.length; i++) {
			if (i == 0) {
				((NetImageView) views[0]).setCoverUrl(displayVideoInfo.getImageUrl(), context);
				String superScriptName = displayVideoInfo.getSuperScripName();
				if (superScriptName != null && !superScriptName.isEmpty()) {
					((NetImageView) views[0]).setSuperscriptType(displayVideoInfo.getSuperScripType());
					// ((NetImageView)
					// views[0]).setSuperscriptColor(displayVideoInfo.getSuperScripColor());
					((NetImageView) views[0]).setSuperscriptName(displayVideoInfo.getSuperScripName());
				} else {
					((NetImageView) views[0]).setSuperscriptGone();
				}
				String subScriptName = displayVideoInfo.getSubscriptName();
				if (subScriptName != null && !subScriptName.isEmpty()) {
					((NetImageView) views[0]).setSubscriptType(displayVideoInfo.getSubscriptType());
					((NetImageView) views[0]).setSubscriptName(subScriptName);
				} else {
					((NetImageView) views[0]).setSubscriptGone();
				}
				((NetImageView) views[0]).setVisibility(View.VISIBLE);
			} else if (i == 1 && displayVideoInfo.getVideoTitle() != null) {
				((TextView) views[1]).setText(displayVideoInfo.getVideoTitle());
				((TextView) views[1]).setVisibility(View.VISIBLE);
			} else if (i == 2 && displayVideoInfo.getVideoDesc() != null) {
				((TextView) views[2]).setText(displayVideoInfo.getVideoDesc());
				((TextView) views[2]).setVisibility(View.VISIBLE);
			}
		}
	}

	public static void startPlayActivity(Context context, DisplayVideoInfo displayVideoInfo) {
		Intent intent = new Intent(context, PlayVideoActivity.class);
		switch (displayVideoInfo.getDetailType()) {
		case 11:// 点播
			Bundle vodBundle = new Bundle();
			intent.putExtra(ContainerActivity.FRAGMENTNAME, PlayVideoFragment.class.getName());
			vodBundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_VOD);
			vodBundle.putString(PlayerParams.KEY_PLAY_UUID, "400717");
//			vodBundle.putString(PlayerParams.KEY_PLAY_VUID, "200538314");
			vodBundle.putString(PlayerParams.KEY_PLAY_VUID, displayVideoInfo.getVideoId());
			vodBundle.putString(SaasRequest.SAAS_USERID, LoginInfoUtil.getUserId(context));
			vodBundle.putString(SaasRequest.SAAS_UTOKEN, LoginInfoUtil.getToken(context));
			vodBundle.putString("albumId",
					TextUtils.isEmpty(displayVideoInfo.getAlbumId()) ? "" : displayVideoInfo.getAlbumId());
			vodBundle.putString("albumName", displayVideoInfo.getmAlbumName());
			vodBundle.putString("shareUrl", displayVideoInfo.getShareUrl());
			vodBundle.putString("imageUrl", displayVideoInfo.getImageUrl());
			vodBundle.putString("videoDesc", displayVideoInfo.getVideoDesc());
			vodBundle.putString("videoTitle", displayVideoInfo.getVideoTitle());
			vodBundle.putString("nextLinkUrl", displayVideoInfo.getNextLinkUrl());
			intent.putExtras(vodBundle);
			context.startActivity(intent);
			break;
		case 20000:// 本地
			Bundle localBundle = new Bundle();
			intent.putExtra(ContainerActivity.FRAGMENTNAME, PlayLocalVideoFragment.class.getName());
			localBundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_VOD);
			localBundle.putString(PlayerParams.KEY_PLAY_UUID, MyApplication.getInstance().getTenantId());
			localBundle.putString(PlayerParams.KEY_PLAY_VUID, displayVideoInfo.getVideoId());
			// localBundle.putString("albumId", displayVideoInfo.getAblumId());
			// localBundle.putString("albumname",
			// displayVideoInfo.getmAlbumName());
			// localBundle.putBoolean(PlayProxy.PLAY_USESAAS, true);
			// localBundle.putString("shareurl",
			// displayVideoInfo.getShareUrl());
			// localBundle.putString("imageurl",
			// displayVideoInfo.getImageUrl());
			// localBundle.putString("videodesc",
			// displayVideoInfo.getVideoDesc());
			localBundle.putBoolean("local_pano", false);
			localBundle.putString("videoTitle", displayVideoInfo.getVideoTitle());
			localBundle.putString("path", displayVideoInfo.getVideoId());
			intent.putExtras(localBundle);
			context.startActivity(intent);
			break;

		case 21:// 直播
			Bundle liveBundle = new Bundle();
			liveBundle.putString(ContainerActivity.FRAGMENTNAME, PlayLiveVideoFragment.class.getName());
			liveBundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_ACTION_LIVE);
			liveBundle.putString(PlayerParams.KEY_PLAY_UUID, MyApplication.getInstance().getTenantId());
			liveBundle.putString(SaasRequest.SAAS_USERID, LoginInfoUtil.getUserId(context));
			liveBundle.putString(SaasRequest.SAAS_UTOKEN, LoginInfoUtil.getToken(context));
			liveBundle.putString(PlayerParams.KEY_PLAY_ACTIONID, displayVideoInfo.getVideoId());
			liveBundle.putBoolean(PlayerParams.KEY_PLAY_USEHLS, false);
			liveBundle.putBoolean(PlayerParams.KEY_PLAY_ISLETV, false);
			liveBundle.putBoolean("local_pano", false);
			liveBundle.putString("shareUrl", displayVideoInfo.getShareUrl());
			liveBundle.putString("imageUrl", displayVideoInfo.getImageUrl());
			liveBundle.putString("videoDesc", displayVideoInfo.getVideoDesc());
			liveBundle.putString("videoTitle", displayVideoInfo.getVideoTitle());
			intent.putExtras(liveBundle);
			context.startActivity(intent);
			break;
		case 22:// 音频
			Bundle tvLiveBundle = new Bundle();
			tvLiveBundle.putString(ContainerActivity.FRAGMENTNAME, PlayAudioLiveVideoFragment.class.getName());
			tvLiveBundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_ACTION_LIVE);
			tvLiveBundle.putString(PlayerParams.KEY_PLAY_UUID, MyApplication.getInstance().getTenantId());
			tvLiveBundle.putString(SaasRequest.SAAS_USERID, LoginInfoUtil.getUserId(context));
			tvLiveBundle.putString(SaasRequest.SAAS_UTOKEN, LoginInfoUtil.getToken(context));
			// "A2016053100000je"
			tvLiveBundle.putString(PlayerParams.KEY_PLAY_ACTIONID, displayVideoInfo.getVideoId());
			tvLiveBundle.putBoolean(PlayerParams.KEY_PLAY_USEHLS, false);
			tvLiveBundle.putBoolean(PlayerParams.KEY_PLAY_ISLETV, false);
			tvLiveBundle.putBoolean("local_pano", false);
			tvLiveBundle.putString("shareUrl", displayVideoInfo.getShareUrl());
			tvLiveBundle.putString("imageUrl", displayVideoInfo.getImageUrl());
			tvLiveBundle.putString("videoDesc", displayVideoInfo.getVideoDesc());
			tvLiveBundle.putString("videoTitle", displayVideoInfo.getVideoTitle());
			intent.putExtras(tvLiveBundle);
			context.startActivity(intent);
			break;
		case 23:// 卫视
			Bundle audioLiveBundle = new Bundle();
			audioLiveBundle.putString(ContainerActivity.FRAGMENTNAME, PlayTVLiveVideoFragment.class.getName());
			audioLiveBundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_ACTION_LIVE);
			audioLiveBundle.putString(PlayerParams.KEY_PLAY_UUID, MyApplication.getInstance().getTenantId());
			audioLiveBundle.putString(SaasRequest.SAAS_USERID, LoginInfoUtil.getUserId(context));
			audioLiveBundle.putString(SaasRequest.SAAS_UTOKEN, LoginInfoUtil.getToken(context));
			audioLiveBundle.putString(PlayerParams.KEY_PLAY_ACTIONID, displayVideoInfo.getVideoId());
			audioLiveBundle.putBoolean(PlayerParams.KEY_PLAY_USEHLS, false);
			audioLiveBundle.putBoolean(PlayerParams.KEY_PLAY_ISLETV, false);
			audioLiveBundle.putBoolean("local_pano", false);
			audioLiveBundle.putString("shareUrl", displayVideoInfo.getShareUrl());
			audioLiveBundle.putString("imageUrl", displayVideoInfo.getImageUrl());
			audioLiveBundle.putString("videoDesc", displayVideoInfo.getVideoDesc());
			audioLiveBundle.putString("videoTitle", displayVideoInfo.getVideoTitle());
			intent.putExtras(audioLiveBundle);
			context.startActivity(intent);
			break;
		case 24:// 移动直播
			MobileLiveAPI.startMobileLiveFragment(context, displayVideoInfo.getVideoId());
			break;
		case 31:// 图文
			RecommendAPI.startSubject(context, displayVideoInfo.getVideoId());
			break;
		case 32:// web 页
			intent = new Intent(context, WebviewActivity.class);
			intent.putExtra("webUrl", displayVideoInfo.getVideoId());
			context.startActivity(intent);
			break;
		case 33:// 移动直播 页
			MobileLiveAPI.startMobileLiveListFragment(context, true);
			break;
		default:
			Toast.makeText(context, context.getString(R.string.play_cannotview), Toast.LENGTH_SHORT).show();
			break;
		}
	}

	public static void addSignleDownloadInfo(Fragment fragment, final Context context,
			final DisplayVideoInfo playVideoInfo, final DownloadSaasCenter downloadSaaSCenter, final boolean isToast) {
		// 无网络和禁止非wifi下缓存
		if (addDownlaodLimit(context)) {
			return;
		}
		// 无权限视频提示
		if (!(playVideoInfo.getDownloadPlatform() != null && playVideoInfo.getDownloadPlatform().contains("104002"))) {
			((BaseActivity) context).showToastSafe(context.getResources().getString(R.string.play_download_nodowload),
					Toast.LENGTH_SHORT);
			return;
		}
		// 已下载视频提示
		List<String> videoIdList = new ArrayList<String>();
		List<LeDownloadInfo> downloadInfoList = downloadSaaSCenter.getDownloadInfoList();
		if (downloadInfoList != null) {
			for (LeDownloadInfo leDownloadInfo : downloadInfoList) {
				videoIdList.add(leDownloadInfo.getVu());
			}
		}
		if (videoIdList != null && videoIdList.contains(playVideoInfo.getVideoId())) {
			((BaseActivity) context).showToastSafe(context.getResources().getString(R.string.play_download_hasdowload),
					Toast.LENGTH_SHORT);
			return;
		}
		if (MyApplication.getInstance().isNeedBoss() == 0) {
			addSDKdownlaod(context, playVideoInfo, downloadSaaSCenter, isToast);
		} else {
			// 单视频无需鉴权直接下载
			if (TextUtils.isEmpty(playVideoInfo.getAlbumId()) || "0".equals(playVideoInfo.getAlbumId())) {
				addSDKdownlaod(context, playVideoInfo, downloadSaaSCenter, isToast);
			} else {
				// 视频免费/付费鉴权
				new UiAsyncTask<Integer>(fragment) {

					@Override
					protected Integer doBackground() throws Throwable {
						AuthInfo authInfo = new AuthInfo();
						AuthDataRequest request = new AuthDataRequest(context);
						Map<String, String> mInputParam = new HashMap<String, String>();
						mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(context));
						mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
						mInputParam.put("platform", "104002");
						mInputParam.put("albumId", playVideoInfo != null ? playVideoInfo.getAlbumId() : "");
						mInputParam.put("storepath", "anyanyanything");
						return request.setInputParam(mInputParam).setOutputData(authInfo).request(Request.Method.GET);
					}

					protected void post(Integer result) {
						if (result == 0) {
							addSDKdownlaod(context, playVideoInfo, downloadSaaSCenter, isToast);
						} else {
							((BaseActivity) context).showToastSafe(R.string.play_downlaodauthfailed,
									Toast.LENGTH_SHORT);
						}
					};
				}.showDialog().execute();
			}
		}

	}

	public static void addSDKdownlaod(final Context context, final DisplayVideoInfo playVideoInfo,
			final DownloadSaasCenter downloadSaaSCenter, final boolean isToast) {
		LeDownloadManager.getInstance(context).registerDownloadObserver(new LeDownloadObserver() {

			@Override
			public void onGetVideoInfoRate(LeDownloadInfo arg0, List<String> list) {
				String rateText = "";
				if (list != null && list.size() > 2) {
					rateText = list.get(2);
				} else if (list != null && list.size() > 1) {
					rateText = list.get(1);
				} else if (list != null && list.size() > 0) {
					rateText = list.get(0);
				}
				downloadSaaSCenter.setDownloadRateText(rateText);

			}

			@Override
			public void onDownloadWait(LeDownloadInfo arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDownloadSuccess(LeDownloadInfo arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDownloadStop(LeDownloadInfo arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDownloadStart(LeDownloadInfo arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDownloadProgress(LeDownloadInfo arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDownloadInit(LeDownloadInfo arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDownloadFailed(LeDownloadInfo arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDownloadCancel(LeDownloadInfo arg0) {
				// TODO Auto-generated method stub

			}
		});

		addRateDownlaodInfo(context, playVideoInfo, downloadSaaSCenter, isToast);
	}

	private static void addRateDownlaodInfo(Context context, DisplayVideoInfo playVideoInfo,
			final DownloadSaasCenter downloadSaaSCenter, boolean isToast) {
		// arg0 userKey arg1 uu arg2 vu arg3 码率
		LeOfflineInfo leOfflineInfo = new LeOfflineInfo();
		String imgUrl = playVideoInfo.getImageUrl();
		leOfflineInfo.setImgUrl(imgUrl);
		leOfflineInfo.setAlbumId(playVideoInfo.getAlbumId());
		leOfflineInfo.setVideoTitle(playVideoInfo.getVideoTitle());
		leOfflineInfo.setAlbumName(playVideoInfo.getmAlbumName());
		if (playVideoInfo instanceof PlayVideoInfo) {
			leOfflineInfo.setAlbumPicUrl(((PlayVideoInfo) playVideoInfo).getAlbumPicUrl());
			leOfflineInfo.setEpisode(((PlayVideoInfo) playVideoInfo).getEpisode());
			leOfflineInfo.setDisplayType(((PlayVideoInfo) playVideoInfo).getDisplayType());
		}
		String offlineStringInfo = SerializeableUtil.getHexStringFromObject(context, leOfflineInfo);
		// downloadSaaSCenter.downloadVideo(null,
		// MyApplication.getInstance().getTenantId(),
		// playVideoInfo.getVideoId(), null, offlineStringInfo);
		LeDownloadInfo info = new LeDownloadInfo();
		info.setUserKey("");
		info.setUu(MyApplication.getInstance().getTenantId());
		info.setVu(playVideoInfo.getVideoId());
		info.setP("120");
		// info.setUserId(LoginInfoUtil.getUserId(context));
		// info.setUtoken(LoginInfoUtil.getToken(context));
		info.setString1(offlineStringInfo);
		downloadSaaSCenter.downloadVideo(info);
		if (isToast) {
			Toast.makeText(context, context.getResources().getString(R.string.play_downlaod), Toast.LENGTH_SHORT)
					.show();
		}
	}

	public static boolean addDownlaodLimit(Context context) {
		if (!NetworkUtils.isNetAvailable(context)) {
			Toast.makeText(context, context.getResources().getString(R.string.play_downlaod_noNet), Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		if (!MyApplication.getInstance().getBoolean("iswificache") && !NetworkUtils.isWifiConnected(context)) {
			Toast.makeText(context, context.getResources().getString(R.string.play_downlaod_nowifi), Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		return false;
	}

	public static int fixedPostion(List<PlayVideoInfo> playVideoInfos, String videoId) {
		for (int i = 0; i < playVideoInfos.size(); i++) {
			PlayVideoInfo playVideoInfo = playVideoInfos.get(i);
			if (videoId.equals(playVideoInfo.getVideoId())) {
				return i;
			}
		}
		return -1;
	}

	public static boolean addNoNetworkLimit(Context context) {
		if (!NetworkUtils.isNetAvailable(context)) {
			Toast.makeText(context, context.getResources().getString(R.string.play_toast_noNet), Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		return false;
	}

	public static String formatCount(int x) {
		String result = null;
		if (x < 10000) {
			result = String.valueOf(x);
		} else if (x < 100000000) {
			result = show_short(x / 10000, x % 10000, MyApplication.getInstance().getString(R.string.tenthousand));
		} else {
			result = show_short(x / 100000000, x % 100000000 / 10000,
					MyApplication.getInstance().getString(R.string.hundredmillion));
		}
		return result;
	}

	public static String show_short(int i, int s, String unit) {
		StringBuilder resultBuilder = new StringBuilder();
		String result = String.valueOf(i);
		if (result.length() > 2) {
			// 3 位或 4 位整数, 无需小数
			if (s >= 5000) {
				resultBuilder.append(i + 1);
			} else {
				resultBuilder.append(i);
			}

		} else if (result.length() == 2) {
			resultBuilder.append(result).append(".").append((s + 500) / 1000);
		} else {
			resultBuilder.append(result).append(".").append(String.format("%02d", (s + 50) / 100));
		}
		resultBuilder.append(" ").append(unit);
		return resultBuilder.toString();
	}

}
