package com.letv.autoapk.boss;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.base.activity.DetailActivity;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.net.StringDataRequest.DataRequestCallback;
import com.letv.autoapk.context.MyApplication;

/**
 * orderInfo和payInfo通过myApplication进行传递
 */
public class BossManager {
	private static Context context;

	public static final int FLAG_ENTER_ORDER = 3;// 进入订单详情页
	public static final int FLAG_ENTER_VIPCENTER = 1;// 进入会员中心
	public static final int FLAG_ENTER_BOSS_PAY = 2;// 支付页
	public static final int LOGIN_TO_CDKEY = 4;// 进入兑换码页面
	public static final int LOGIN_TO_IDVIP = 5;// 进行会员判定
	public static final int FLAG_ENTER_VIPCENTER_AUTH = 6;//会员支付成功后再进行鉴权；单片购买未登录页面的开通会员
	// 进入会员中心,选择套餐
	public static final int SDK_END_TRYANDSEE = 43;// 未经过其他流程，直接
	private VipInfo vipInfo;
	public static final String KEY_MEMBERID = "memberid";
	public static final String KEY_ORDERID = "orderid";
	public static final String NEED_AUTH = "need_auth";
	public static final String ORDER_INFO = "order_info";
	public static final String PAY_INFO = "pay_info";
	public static final String BOSS_AUTH_SUCCESS = "boss_auth_success";

	public BossManager(Context context) {
		this.context = context;
	}

	public void init(VipInfo vipInfo) {
		this.vipInfo = vipInfo;
	}

	/**
	 * Boss鉴权
	 */
	public synchronized int auth() {
		AuthInfo authInfo = new AuthInfo();
		AuthDataRequest request = new AuthDataRequest(context);
		DisplayVideoInfo info = (DisplayVideoInfo) MyApplication.getInstance()
				.getInfo(MyApplication.CURRENT_VOD_VIDEO_INFO);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(context));
		// mInputParam.put(StringDataRequest.USER_ID, "49781");
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputParam.put("platform", "104002");
		// mInputParam.put("albumId", "456");
		mInputParam.put("albumId", info != null ? info.getAlbumId() : "");
		mInputParam.put("videoId", info != null ? info.getVideoId() : "");
		mInputParam.put("storepath", "anyanyanything");
		return request.setInputParam(mInputParam).setOutputData(authInfo).request(Request.Method.GET);
	}

	public void generateOrder() {
		VideoChargeInfo info = (VideoChargeInfo) MyApplication.getInstance()
				.getInfo(MyApplication.CURRENT_VIDEO_CHARGE_INFO);
		Intent intent = null;
		Bundle bundle = new Bundle();
		bundle.putString("orderExpire", info.getValidTime() + "");
		bundle.putString("orderPrice", info.getPrice() + "");
		bundle.putString("albumId", info.getAlbumId());
		bundle.putString("title", info.getAlbumName());
		if (context instanceof DetailActivity) {
			intent = new Intent(context, ContainerActivity.class);
			intent.putExtra(ContainerActivity.FRAGMENTNAME, OrderInformationFragment.class.getName());
		} else {
			intent = new Intent(context, DetailActivity.class);
			intent.putExtra(DetailActivity.FRAGMENTNAME, OrderInformationFragment.class.getName());
		}
		intent.putExtras(bundle);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);

	}

	public void updateVipInfo(VipInfo info) {
		this.vipInfo = info;
	}

	public Intent getMembersIntent(Context context) {
		Intent intent = null;
		if (context instanceof DetailActivity) {
			intent = new Intent(context, ContainerActivity.class);
			intent.putExtra(ContainerActivity.FRAGMENTNAME, MemberCenterFragment.class.getName());
		} else {
			intent = new Intent(context, DetailActivity.class);
			intent.putExtra(DetailActivity.FRAGMENTNAME, MemberCenterFragment.class.getName());
		}
		return intent;
	}

	public Intent getCdkeyIntent(Context mActivity) {
		Intent intent = null;
		if (mActivity instanceof DetailActivity) {
			intent = new Intent(mActivity, ContainerActivity.class);
			intent.putExtra(ContainerActivity.FRAGMENTNAME, CdkeyFragment.class.getName());
		} else {
			intent = new Intent(mActivity, DetailActivity.class);
			intent.putExtra(DetailActivity.FRAGMENTNAME, CdkeyFragment.class.getName());
		}
		return intent;
	}

	public Intent getConsumeRecordsIntent(Context context) {
		Intent intent = null;
		if (context instanceof DetailActivity) {
			intent = new Intent(context, ContainerActivity.class);
			intent.putExtra(ContainerActivity.FRAGMENTNAME, ConsumeRecordsFragment.class.getName());
		} else {
			intent = new Intent(context, DetailActivity.class);
			intent.putExtra(DetailActivity.FRAGMENTNAME, ConsumeRecordsFragment.class.getName());
		}
		return intent;
	}

	/**
	 * 试看后点击不同按钮，会有不同的目标导向
	 * 
	 * @param destFlag
	 *            目的flag
	 * @param fromFlag
	 *            该目的flag的来源
	 */
	public void switchAim(int destFlag, boolean isPlayAfterPay) {
		switch (destFlag) {
		case BossManager.FLAG_ENTER_ORDER:
			// 进入订单详情（购买流程）
			MyApplication.getInstance().putInt(LepayManager.PAY_FOR_WHAT, LepayManager.PAY_FOR_PLAY);
			generateOrder();
			break;
		case BossManager.FLAG_ENTER_VIPCENTER:
			// 单片非会员,购买会员结束，返回播放页，展示“单片购买-已登录-会员”的界面
			// 进入会员中心页，完成购买，还要回来，展示其他页面
			if (isPlayAfterPay) {
				MyApplication.getInstance().putInt(LepayManager.PAY_FOR_WHAT, LepayManager.PAY_FOR_PLAY);
			} else {
				MyApplication.getInstance().putInt(LepayManager.PAY_FOR_WHAT, LepayManager.PAY_FOR_FINISH);
			}
			Intent membersIntent = getMembersIntent(context);
			membersIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(membersIntent);
			break;
		case BossManager.FLAG_ENTER_VIPCENTER_AUTH:
			MyApplication.getInstance().putInt(LepayManager.PAY_FOR_WHAT, LepayManager.PAY_AUTH_FINISH);
			Intent intent = getMembersIntent(context);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			break;
		default:
			break;
		}
	}
}
