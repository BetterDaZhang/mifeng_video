package com.letv.autoapk.boss;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.android.volley.Request;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.activity.PlayVideoActivity;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.net.StringDataRequest.DataRequestCallback;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.ui.login.LoginAPI;
import com.letv.autoapk.ui.player.CollectionInfo;
import com.letv.autoapk.ui.player.CollectionStatusDataRequest;
import com.letv.autoapk.ui.player.DanmakuDialog;
import com.letv.autoapk.ui.player.DanmakuSendRequest;
import com.letv.autoapk.utils.MD5Utls;
import com.letv.lepaysdk.ELePayCountry;
import com.letv.lepaysdk.ELePayState;
import com.letv.lepaysdk.LePayApi;
import com.letv.lepaysdk.LePayConfig;
import com.letv.lepaysdk.LePay.ILePayCallback;
import com.letv.lepaysdk.utils.NetworkUtils;
import com.letv.lepaysdk.wxpay.WXPay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class LepayManager {
	private static Context context;
	private static Fragment fragment;
	private String ip;
	private String timestamp;
	private String sign;
	private String tradeInfo;
	private LepayInfo lepayInfo;

	public final static String PAY_FOR_WHAT = "payForWhat";// 支付完成后的intent
	public static final int PAY_FOR_PLAY = 31;// 支付成功，播放
	public static final int PAY_FOR_FINISH = 33;// 支付成功，返回播放页不播放
	public static final int PAY_NORMAL = 32;// 支付成功，回到上一级页面
	public static final int PAY_AUTH_FINISH = 34;// 支付成功后鉴权
	public static final int PAY_CONSUMERECORD = 35;// 消费记录

	public static final String PACKAGE_ID = "packageId";
	public static final String PLATFORM = "platform";
	public static final String PRICE = "price";
	public static final String LEPAY_INFO = "lepayInfo";
	public static final String LEPAY_VERSION = "2.0";

	private LePaySuccessListener paySuccessListener;
	private static LepayManager lepayManager;
	private boolean isVipPaySuccess = false;

	private LepayManager(Context ctx, Fragment frag) {
		this.context = ctx;
		this.fragment = frag;
		ip = NetworkUtils.getLocalIpAddress(context);
		WXPay.getInstance(context).isWXAppInstalled();
	}

	public static LepayManager getInstance(Context ctx, Fragment frag) {
		if (lepayManager == null) {
			lepayManager = new LepayManager(ctx, frag);
		}
		context = ctx;
		fragment = frag;
		return lepayManager;
	}

	private void initLepay() {
		lepayInfo = new LepayInfo();
	}

	// 进入收银台按钮点击事件
	public void enterCashier() throws Exception {
		if (isParamsOk() && isPriceOK()) {
			setTimestamp();
			setSign();
			// 生成请求链接的串
			tradeInfo = getTradeInfo();

			/**
			 * 不是必须项
			 */
			LePayConfig lePayConfig = new LePayConfig();
			lePayConfig.hasShowTimer = false;
			lePayConfig.eLePayCountry = ELePayCountry.CN;
			lePayConfig.mWatingTime = 20;// 设置订单轮询时长
			// lePayConfig.hasShowPaySuccess = false;
			LePayApi.initConfig(context, lePayConfig);
			LePayApi.doPay(context, tradeInfo, new ILePayCallback() {
				@Override
				public void payResult(ELePayState status, String message) {

					if (fragment instanceof BossLoginFragment) {
						fragment.getActivity().finish();
					}
					int flag = MyApplication.getInstance().getInt(LepayManager.PAY_FOR_WHAT);
					boolean isFinshAfterPay = (flag == LepayManager.PAY_FOR_PLAY
							|| flag == LepayManager.PAY_FOR_FINISH);
					if (fragment instanceof MemberMobileFragment && isFinshAfterPay) {
						fragment.getActivity().finish();
					}

					// Toast.makeText(context, "result:" + status + "|" +
					// message, Toast.LENGTH_SHORT).show();
					// Toast.makeText(context, message,
					// Toast.LENGTH_SHORT).show();
					if (ELePayState.CANCEL == status) { // 支付取消
						Toast.makeText(context, status + " " + message, Toast.LENGTH_SHORT).show();
					} else if (ELePayState.FAILT == status) { // 支付失败
						Toast.makeText(context, status + " " + message, Toast.LENGTH_SHORT).show();
					} else if (ELePayState.OK == status) { // 支付成功
						paySuccess(flag);
						if (isVipPaySuccess) {
							LoginInfoUtil.setIsVip(0, context);
						}
					} else if (ELePayState.WAITTING == status) { // 支付中
					} else if (ELePayState.NONETWORK == status) { // 支付中
					}
				}

			});
		}
	}

	private boolean isParamsOk() {
		if (TextUtils.isEmpty(lepayInfo.mVersion.trim()) || TextUtils.isEmpty(lepayInfo.mUserId.trim())
				|| TextUtils.isEmpty(lepayInfo.mLetvUserId.trim()) || TextUtils.isEmpty(lepayInfo.mNotifyUrl.trim())
				|| TextUtils.isEmpty(lepayInfo.mPrice.trim()) || TextUtils.isEmpty(lepayInfo.mPayExpire.trim())
				|| TextUtils.isEmpty(lepayInfo.mDeptid.trim()) || TextUtils.isEmpty(lepayInfo.mPid.trim())
				|| TextUtils.isEmpty(lepayInfo.mProductid.trim()) || TextUtils.isEmpty(lepayInfo.mProductName.trim())
				|| TextUtils.isEmpty(lepayInfo.mProductDesc.trim()) || TextUtils.isEmpty(lepayInfo.mProductUrls.trim())
				|| TextUtils.isEmpty(lepayInfo.mKeyIndex.trim()) || TextUtils.isEmpty(lepayInfo.mSignType.trim())) {
			Toast.makeText(context, context.getString(R.string.paramserror), Toast.LENGTH_LONG).show();
			return false;
		} else {
			return true;
		}
	}

	// 金额不能超过100万
	private boolean isPriceOK() {
		String price = lepayInfo.mPrice.trim();
		double money = Double.parseDouble(price);
		if (money < 0 || money > 999999.99) {
			Toast.makeText(context, context.getString(R.string.pricerange), Toast.LENGTH_LONG).show();
			return false;
		} else {
			return true;
		}
	}

	// 获取时间戳
	private void setTimestamp() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String dateStr = df.format(new Date());
		Log.i("lepaytest", "timestamp=" + dateStr);
		timestamp = dateStr;
	}

	private void setSign() throws Exception {
		// signKey=mSignKey.getText().toString().trim();
		String sourceUrlStr = getAscParamsUrlStr(); // 按参数字母升序排列参数原串
		Log.i("lepaytest", "签名原串:" + sourceUrlStr + "&key=" + "753d2fd4c888806dde77e0ba66b29f2d");
		String signStr = MD5Utls.MD5(sourceUrlStr + "&key=" + "753d2fd4c888806dde77e0ba66b29f2d");
		Log.i("lepaytest", "signStr:" + signStr);
		sign = signStr;
	}

	// 获取签名数据
	private String getAscParamsUrlStr() throws UnsupportedEncodingException {
		List<NameValuePair> paramslist = new LinkedList<NameValuePair>();
		paramslist.add(new BasicNameValuePair("app_id", lepayInfo.app_id));
		paramslist.add(new BasicNameValuePair("currency", lepayInfo.mCurrency.trim()));
		paramslist.add(new BasicNameValuePair("input_charset", lepayInfo.mInputCharset.trim()));
		paramslist.add(new BasicNameValuePair("ip", ip));
		paramslist.add(new BasicNameValuePair("key_index", lepayInfo.mKeyIndex.trim()));
		paramslist.add(new BasicNameValuePair("merchant_business_id", lepayInfo.marchantBusinessId));
		paramslist.add(new BasicNameValuePair("merchant_no", lepayInfo.mMerchantNo.trim()));
		paramslist.add(new BasicNameValuePair("notify_url", lepayInfo.mNotifyUrl.trim()));
		paramslist.add(new BasicNameValuePair("out_trade_no", lepayInfo.mOutTradeNo.trim()));
		paramslist.add(new BasicNameValuePair("pay_expire", lepayInfo.mPayExpire.trim()));
		paramslist.add(new BasicNameValuePair("price", lepayInfo.mPrice.trim()));
		paramslist.add(new BasicNameValuePair("product_id", lepayInfo.mProductid.trim()));
		paramslist.add(new BasicNameValuePair("product_name", lepayInfo.mProductName.trim()));
		paramslist.add(new BasicNameValuePair("product_desc", lepayInfo.mProductDesc.trim()));
		paramslist.add(new BasicNameValuePair("service", "lepay.app.api.show.cashier"));
		paramslist.add(new BasicNameValuePair("timestamp", lepayInfo.timestamp.trim()));
		paramslist.add(new BasicNameValuePair("user_id", lepayInfo.mUserId.trim()));
		paramslist.add(new BasicNameValuePair("user_name", lepayInfo.mUserName.trim()));
		paramslist.add(new BasicNameValuePair("version", lepayInfo.mVersion.trim()));

		paramslist.add(new BasicNameValuePair("country_code", lepayInfo.mCountryCode.trim()));
		paramslist.add(new BasicNameValuePair("saas_boss_shareSecret", lepayInfo.appleSign.trim()));

		// paramslist.add(new BasicNameValuePair("call_back_url",
		// mNotifyUrl.trim()));
		// paramslist.add(new BasicNameValuePair("product_urls",
		// mProductUrls.trim()));
		paramslist.add(new BasicNameValuePair("sign_type", lepayInfo.mSignType.trim()));

		String urlStr = getUrlParams(paramslist);
		Log.i("lepaytest", "urlString:" + urlStr);
		return urlStr;
	}

	private String getUrlParams(List<NameValuePair> paramslist) {
		if (paramslist == null || paramslist.size() == 0) {
			return "";
		}
		URLEncodedUtils.format(paramslist, "UTF-8");
		Collections.sort(paramslist, DICCOMPARATOR);
		StringBuffer sb = new StringBuffer();
		final int count = paramslist.size();
		for (int i = 0; i < count; i++) {
			if (i != 0) {
				sb.append("&");
			}
			sb.append(paramslist.get(i).getName());
			sb.append("=");
			sb.append(paramslist.get(i).getValue());
		}
		return sb.toString();
	}

	private final Comparator<NameValuePair> DICCOMPARATOR = new Comparator<NameValuePair>() {
		@Override
		public int compare(NameValuePair lhs, NameValuePair rhs) {
			if (lhs.getName().equals(rhs.getName())) {
				return 0;
			}
			return lhs.getName().compareToIgnoreCase(rhs.getName());
		}
	};

	// 20个参数，1个sign，2个ios需要参数
	private String getTradeInfo() throws UnsupportedEncodingException {
		StringBuffer params = new StringBuffer("");
		params.append("app_id").append("=").append(lepayInfo.app_id);
		params.append("&").append("currency").append("=").append(lepayInfo.mCurrency.trim());
		params.append("&").append("input_charset").append("=").append(lepayInfo.mInputCharset.trim());
		params.append("&").append("ip").append("=").append(ip);
		params.append("&").append("key_index").append("=").append(lepayInfo.mKeyIndex.trim());
		params.append("&").append("merchant_business_id").append("=").append(lepayInfo.marchantBusinessId.trim());
		params.append("&").append("merchant_no").append("=").append(lepayInfo.mMerchantNo.trim());
		params.append("&").append("notify_url").append("=").append(lepayInfo.mNotifyUrl.trim());
		params.append("&").append("out_trade_no").append("=").append(lepayInfo.mOutTradeNo.trim());
		params.append("&").append("pay_expire").append("=").append(lepayInfo.mPayExpire.trim());
		params.append("&").append("price").append("=").append(lepayInfo.mPrice.trim());
		params.append("&").append("product_desc").append("=").append(lepayInfo.mProductDesc.trim());
		params.append("&").append("product_id").append("=").append(lepayInfo.mProductid.trim());
		params.append("&").append("product_name").append("=").append(lepayInfo.mProductName.trim());
		params.append("&").append("service").append("=").append("lepay.app.api.show.cashier");
		params.append("&").append("timestamp").append("=").append(lepayInfo.timestamp);
		params.append("&").append("user_id").append("=").append(lepayInfo.mUserId.trim());
		params.append("&").append("user_name").append("=").append(lepayInfo.mUserName.trim());
		params.append("&").append("version").append("=").append(lepayInfo.mVersion.trim());
		// params.append("&").append("letv_user_id").append("=").append(lepayInfo.mLetvUserId.trim());
		// params.append("&").append("call_back_url").append("=").append(mNotifyUrl.trim());
		// params.append("&").append("product_urls").append("=").append(lepayInfo.mProductUrls.trim());
		/**
		 * sign 为安全保证，需商户服务器进行签名，签名秘钥不暴露给客户端；
		 */
		params.append("&").append("sign").append("=").append(lepayInfo.signKey.trim());
		// params.append("&").append("sign").append("=").append(sign);
		Log.i("lepaytest", "lepayInfo.signKey" + lepayInfo.signKey.trim());
		params.append("&").append("sign_type").append("=").append(lepayInfo.mSignType.trim());
		/**
		 * ios需要的参数
		 */
		params.append("&").append("country_code").append("=").append(lepayInfo.mCountryCode);
		params.append("&").append("saas_boss_shareSecret").append("=").append(lepayInfo.appleSign);
		return params.toString();
	}

	public void vipOrder(final String packageId, final String platform) {
		initLepay();
		isVipPaySuccess = true;
		new UiAsyncTask<Integer>(fragment) {
			@Override
			protected void post(Integer result) {
				if (result == 0) {
					// initLepayData();
					try {
						if (TextUtils.isEmpty(lepayInfo.marchantBusinessId) || TextUtils.isEmpty(lepayInfo.signKey)) {
							Toast.makeText(context, context.getResources().getString(R.string.no_pay),
									Toast.LENGTH_SHORT).show();
						} else {
							enterCashier();
						}
					} catch (Exception e) {
						Logger.log(e);
					}
				}
			}

			@Override
			protected Integer doBackground() throws Throwable {
				// 会员下单
				LepayOrderCreateDataRequest request = new LepayOrderCreateDataRequest(context);
				Map<String, String> mInputParam = new HashMap<String, String>();
				mInputParam.put("packageId", packageId);
				mInputParam.put("platform", platform);
				mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(context));
				mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
				mInputParam.put("version", LepayManager.LEPAY_VERSION);
				return request.setInputParam(mInputParam).setOutputData(lepayInfo).request(Request.Method.GET);

			}

		}.showDialog().execute();

	}

	public void albumOrder(LepayInfo lepayInfo) {
		this.lepayInfo = lepayInfo;
		isVipPaySuccess = false;
		// initLepayData();
		try {
			if (TextUtils.isEmpty(lepayInfo.marchantBusinessId) || TextUtils.isEmpty(lepayInfo.signKey)) {
				Toast.makeText(context, context.getResources().getString(R.string.no_pay), Toast.LENGTH_SHORT).show();
			} else {
				enterCashier();
			}
		} catch (Exception e) {
			Logger.log(e);
		}
	}

	private void paySuccess(int flag) {
		switch (flag) {
		case PAY_FOR_PLAY:
			Intent intent = new Intent(context, PlayVideoActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(intent);
			if (paySuccessListener != null) {
				paySuccessListener.paymentSuccess();
			}
			break;
		case PAY_NORMAL:
			finish();
			if (MyApplication.getInstance().getLepaySuccessListener() != null) {
				MyApplication.getInstance().getLepaySuccessListener().paymentSuccess();
			}
			break;
		case PAY_FOR_FINISH:
			finish();
			break;
		case PAY_AUTH_FINISH:
			finish();
			break;
		case PAY_CONSUMERECORD:
			if (MyApplication.getInstance().getLepaySuccessListener() != null) {
				MyApplication.getInstance().getLepaySuccessListener().paymentSuccess();
			}
			break;
		default:
			break;
		}
	}

	public void setLePaySuccessListener(LePaySuccessListener lePaySuccessListener) {
		this.paySuccessListener = lePaySuccessListener;
	}

	private void finish() {
		if (context instanceof BaseActivity) {
			((BaseActivity) context).finish();
		}
	}

}
