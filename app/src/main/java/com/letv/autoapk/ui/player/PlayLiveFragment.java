package com.letv.autoapk.ui.player;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.letv.autoapk.R;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.context.MyApplication;

public class PlayLiveFragment extends PlayDetailFragment {

	/**
	 * 直播详情顶部栏
	 */
	void initTopView() {
		super.initTopView();
		headBarView = (LinearLayout) LayoutInflater.from(mActivity).inflate(R.layout.play_detail_live_headview, null);
		commentCount = (TextView) headBarView.findViewById(R.id.play_detail_comment_count);
		prise = (TextView) headBarView.findViewById(R.id.play_detail_priseTv);
		prise.setOnClickListener(this);
		shareImg = (ImageView) headBarView.findViewById(R.id.play_detail_share);
		shareImg.setTag(describeInfo);
		shareImg.setOnClickListener(this);
	}

	void updateTopView() {
		if (describeInfo != null) {
			headView.removeView(headBarView);
			headView.addView(headBarView);
			// 获取点赞状态
			getPriseState("4");
		} else {
			headView.removeView(headBarView);
		}

	}

	/**
	 * 初始化点播介绍
	 */
	void initDescriptView() {
		super.initDescriptView();
		// 一期暂时去掉观看人数
		descriptPlaycount.setVisibility(View.GONE);
		desrcibeRl.setOnClickListener(this);
	}

	void updateDescriptView() {
		super.updateDescriptView();
		try {
			descriptMore.setBackgroundResource(R.drawable.play_tv_unspread);
			describeAll.setVisibility(View.GONE);
			descriptTitle.setText(describeInfo.getVideoTitle());
			// if (!TextUtils.isEmpty(describeInfo.getPlaytTimes()+"")) {
			// descriptPlaycount.setText(getResString(R.string.play_detail_playcount)
			// +describeInfo.getPlaytTimes() + +
			// getResString(R.string.play_detail_ci));
			// descriptPlaycount.setVisibility(View.VISIBLE);
			// }else{
			// descriptPlaycount.setVisibility(View.GONE);
			// }
			// 设置弹幕
			// descriptBarrageCount.setText(text);
			if (!TextUtils.isEmpty(describeInfo.getSubCategory())) {
				descriptType.setText(getResString(R.string.play_category) + describeInfo.getSubCategory());
				descriptType.setVisibility(View.VISIBLE);
			} else {
				descriptType.setVisibility(View.GONE);
			}
			if (!TextUtils.isEmpty(describeInfo.getSubCategory())) {
				descriptBrief.setText(getResString(R.string.play_abstract) + describeInfo.getVideoBrief());
				descriptBrief.setVisibility(View.VISIBLE);
			} else {
				descriptBrief.setVisibility(View.GONE);
			}
			if (!TextUtils.isEmpty(describeInfo.getArea())) {
				descriptArea.setText(getResString(R.string.play_area) + describeInfo.getArea());
				descriptArea.setVisibility(View.VISIBLE);
			} else {
				descriptArea.setVisibility(View.GONE);
			}
			if (!TextUtils.isEmpty(describeInfo.getPublishTime())) {
				descriptShow.setText(getResString(R.string.play_showtime) + describeInfo.getPublishTime());
				descriptShow.setVisibility(View.VISIBLE);
			} else {
				descriptShow.setVisibility(View.GONE);
			}
			mPullRefreshListView.getRefreshableView().removeHeaderView(describeHeaderView);
			mPullRefreshListView.getRefreshableView().addHeaderView(describeHeaderView);
		} catch (NullPointerException e) {
			mPullRefreshListView.getRefreshableView().removeHeaderView(describeHeaderView);
			e.printStackTrace();
		}
	}

	void initEpisodeView() {
		super.initEpisodeView();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.play_detail_priseTv:
			sendPlayPriseRequest("4");
			break;
		case R.id.play_detail_describeRl:
			if (isSpread) {
				isSpread = false;
				describeAll.setVisibility(View.GONE);
				descriptMore.setBackgroundResource(R.drawable.play_tv_unspread);
			} else {
				isSpread = true;
				describeAll.setVisibility(View.VISIBLE);
				descriptMore.setBackgroundResource(R.drawable.play_tv_spread);
			}
			break;
		default:
			break;
		}
	}

	String getCommentType() {
		return "live";
	}

	PlayDetailResponseInfo loadingDetailData() {
		playDetailInfo = new PlayDetailInfo();
		playDetailResponseInfo = new PlayDetailResponseInfo();
		PlayLiveDetailDataRequest playDetailDataRequest = new PlayLiveDetailDataRequest(mActivity);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputParam.put("liveVideoId", videoId);
		// playDetailInfo 下拉刷新的时候是重新new的，如果下拉时解析时出现错误也将之前的数据清空。正确是当code ==
		// 0时，才重新添加数据，待修改
		int code = playDetailDataRequest.setInputParam(mInputParam).setOutputData(playDetailInfo)
				.request(Request.Method.GET);
		if (code == 0) {
			playDetailResponseInfo.responseSuccess = true;
			playDetailResponseInfo.state = code;
			return playDetailResponseInfo;
		}
		playDetailResponseInfo.responseSuccess = false;
		playDetailResponseInfo.state = code;
		return playDetailResponseInfo;
	}
}
