package com.letv.autoapk.player;

import java.util.List;

import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.constant.StatusCode;
import com.lecloud.sdk.http.request.HttpRequest;
import com.lecloud.sdk.http.request.HttpRequest.OnResultListener;
import com.letv.android.client.cp.sdk.api.md.impl.CPVodMediaData;
import com.letv.android.client.cp.sdk.entity.Medialist;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by heyuekuai on 16/5/3.
 */
public class SaasMediaData extends CPVodMediaData {

	private SaasVideo video;

	public SaasMediaData(Context context) {
		super(context);
	}

	@Override
	public String findUrlByRate(String vType) {
		List<Medialist> medialists = video.getData().getVideoInfo()
				.getMedialist();
		for (Medialist medialist : medialists) {
			if (medialist.getVtype().equals(vType)) {
				String url = medialist.getUrllist().get(0).getUrl();
				return url;
			}
		}
		return null;
	}

	@Override
	public String getUserId() {
		if (video == null || video.getData() == null
				|| video.getData().getUserInfo() == null) {
			return null;
		}
		return video.getData().getUserInfo().getUserId();
	}

	@Override
	public String getVideoId() {
		if (video == null || video.getData() == null
				|| video.getData().getVideoInfo() == null) {
			return null;
		}
		return video.getData().getVideoInfo().getVid();
	}

	@Override
	public void requestVod() {
		SaasRequest request = new SaasRequest();
		request.setMediaDataParams(mediaDataParams);
		request.setServerTimestemp(serverTimestemp);
		request.setContext(context);
		mzStart();
		request.setOnResultListener(new OnResultListener() {

			@Override
			public void OnRequestResult(HttpRequest request, Object data) {
				if (isCancel()) {
					return;
				}
				mzDone();
				setIsActive(false);
				Bundle bundle = new Bundle();
				bundle.putString(PlayerParams.KEY_HTTP_CODE,
						request.getStatusCode());
				if (data != null) {
					video = (SaasVideo) data;
					video.setUu(mediaDataParams
							.getString(PlayerParams.KEY_PLAY_UUID));
					video.setVu(mediaDataParams
							.getString(PlayerParams.KEY_PLAY_VUID));

					if (TIMESTEMP_ERROR == video.getCode() && count < 2) {
						serverTimestemp = String.valueOf(video.getTimestamp());
						count++;
						requestVod();
						return;
					}
					if (video.isError()) {
						bundle.putInt(PlayerParams.KEY_RESULT_STATUS_CODE,
								StatusCode.MEDIADATA_SERVER_ERROR);
						bundle.putInt(PlayerParams.KEY_STATS_CODE,
								StatusCode.MEDIADATA_GPC_RETURN_DATA_ILLEGAL);
						bundle.putString(PlayerParams.KEY_RESULT_ERROR_CODE,
								video.getCode() + "");
						bundle.putString(PlayerParams.KEY_RESULT_ERROR_MSG,
								video.getMessage());
					} else {
						bundle.putInt(PlayerParams.KEY_RESULT_STATUS_CODE,
								StatusCode.MEDIADATA_SUCCESS);
						bundle.putParcelable(PlayerParams.KEY_RESULT_DATA,
								video);
					}
				} else {
					bundle.putInt(PlayerParams.KEY_RESULT_STATUS_CODE,
							StatusCode.MEDIADATA_NETWORK_ERROR);
				}
				if (listener != null) {
					listener.onMediaDataEvent(PlayerEvent.MEDIADATA_VOD, bundle);
				}
			}
		});
		request.executeOnPoolExecutor();
	}

}
