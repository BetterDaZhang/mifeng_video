package com.letv.autoapk.player;

import org.json.JSONObject;

import com.letv.android.client.cp.sdk.api.md.entity.vod.cp.CPVideo;
import com.letv.android.client.cp.sdk.entity.Data;

import android.os.Parcel;

public class SaasVideo extends CPVideo {
	private int ashowmin;
	private int needbuy;
	private long trylooktime;

	public static final Creator<SaasVideo> CREATOR = new Creator<SaasVideo>() {
		@Override
		public SaasVideo createFromParcel(Parcel source) {
			return new SaasVideo(source);
		}

		@Override
		public SaasVideo[] newArray(int size) {
			return new SaasVideo[size];
		}
	};

	public SaasVideo() {
	}

	protected SaasVideo(Parcel in) {
		super(in);
		this.ashowmin = in.readInt();
		this.needbuy = in.readInt();
		trylooktime = in.readLong();
	}

	public static SaasVideo fromJson(JSONObject json) {
		SaasVideo mVideo = new SaasVideo();
		mVideo.code = json.optInt("code");
		mVideo.message = json.optString("message");
		if (json.optJSONObject("data") != null) {
			mVideo.data = Data.fromJson(json.optJSONObject("data"));
			JSONObject videoInfo = json.optJSONObject("data").optJSONObject("videoinfo");
			if (videoInfo != null) {
				mVideo.ashowmin = videoInfo.optInt("ashowmin", 60);
				mVideo.needbuy = videoInfo.optInt("needbuy", 1);
				mVideo.trylooktime = videoInfo.optLong("tryLookTime");
			}
		}
		mVideo.timestamp = json.optInt("timestamp");
		parseCover(mVideo);
		return mVideo;
	}

	@Override
	public SaasVideo getSaasVideo() {
		return this;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(this.ashowmin);
		dest.writeInt(this.needbuy);
		dest.writeLong(trylooktime);
	}

	public int getAshowmin() {
		return ashowmin;
	}

	public void setAshowmin(int ashowmin) {
		this.ashowmin = ashowmin;
	}

	public int getNeedbuy() {
		return needbuy;
	}

	public void setNeedbuy(int needbuy) {
		this.needbuy = needbuy;
	}

	public long getTrylooktime() {
		return trylooktime;
	}

	public void setTrylooktime(long trylooktime) {
		this.trylooktime = trylooktime;
	}

}
