package com.letv.autoapk.open;

import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;

import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.utils.SystemUtls;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

class LoginUtil {

	private BaseActivity context;

	private PlatformActionListener listener;
	private int logintype;
	SsoHandler mSsoHandler;
	public static final String TAG = LoginUtil.class.getSimpleName();

	public LoginUtil(BaseActivity context, PlatformActionListener listener) {
		this.context = context;
		this.listener = listener;
	}

	// 执行授权,获取用户信息
	// 文档：http://wiki.mob.com/Android_%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E8%B5%84%E6%96%99
	public void authorize(int type) {
		logintype = type;
		switch (type) {
		case OpenSdk.TYPE_BLOG:
			doLoginBLog();
			break;
		case OpenSdk.TYPE_QQ:
			doLoginQQ();
			break;
		case OpenSdk.TYPE_MM:
			doLoginMM();
			break;
		default:
			break;
		}

	}

	private void doLoginMM() {
		IWXAPI api = (IWXAPI) MyApplication.getInstance().getOpenSdk().getOpenObject(OpenSdk.TYPE_MM);
		if (api.isWXAppInstalled() && api.isWXAppSupportAPI()) {
			SendAuth.Req req = new SendAuth.Req();
			req.scope = "snsapi_userinfo";
			req.state = TAG + SystemUtls.getIMEI(context);
			api.sendReq(req);
		} else {
			Platform info = new Platform();
			info.platform = "mm";
			listener.onError(info, logintype,new Exception(context.getString(R.string.noweichat)));
		}
	}

	private void doLoginQQ() {
		Tencent mTencent = (Tencent) MyApplication.getInstance().getOpenSdk().getOpenObject(OpenSdk.TYPE_QQ);
//		if (!mTencent.isSessionValid()) {
		mTencent.login(context, "all", loginListener);
//		} else {
//			Platform info = new Platform();
//			info.platform = "qq";
//			info.tokenExpir = System.currentTimeMillis() + mTencent.getExpiresIn() * 1000;
//			info.openid = mTencent.getOpenId();
//			info.accessToken = mTencent.getAccessToken();
//			listener.onComplete(info, logintype);
//		}

	}

	private void doLoginBLog() {
		AuthInfo authInfo = (AuthInfo) MyApplication.getInstance().getOpenSdk().getOpenObject(OpenSdk.TYPE_BLOG);
		mSsoHandler = new SsoHandler(context, authInfo);
		mSsoHandler.authorize(new AuthListener());
	}

	IUiListener loginListener = new IUiListener() {

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			Platform info = new Platform();
			info.platform = "qq";
			listener.onCancel(info, logintype);
		}

		@Override
		public void onComplete(Object response) {
			if (null == response) {
				return;
			}
			JSONObject jsonResponse = (JSONObject) response;
			if (null != jsonResponse && jsonResponse.length() == 0) {
				return;
			}
			Platform info = new Platform();
			info.platform = "qq";
			try {
				String token = jsonResponse.getString(Constants.PARAM_ACCESS_TOKEN);
				String expires = jsonResponse.getString(Constants.PARAM_EXPIRES_IN);
				String openId = jsonResponse.getString(Constants.PARAM_OPEN_ID);
				if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
					Tencent mTencent = (Tencent) MyApplication.getInstance().getOpenSdk().getOpenObject(OpenSdk.TYPE_QQ);
					mTencent.setAccessToken(token, expires);
					mTencent.setOpenId(openId);
					info = new Platform();
					info.platform = "qq";
					info.tokenExpir = System.currentTimeMillis() + Long.parseLong(expires) * 1000;
					info.openid = openId;
					info.accessToken = token;
					listener.onComplete(info, logintype);

				}

			} catch (Exception e) {
				listener.onError(info, logintype, e);
			}
		}

		@Override
		public void onError(UiError error) {
			// TODO Auto-generated method stub
			Platform info = new Platform();
			info.platform = "qq";
			listener.onError(info, logintype, new Exception(error.errorMessage));
		}
	};

	class AuthListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle jsonResponse) {
			// 从 Bundle 中解析 Token
			Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(jsonResponse);

			if (mAccessToken.isSessionValid()) {
				Platform info = new Platform();
				info.tokenExpir = mAccessToken.getExpiresTime();
				info.openid = mAccessToken.getUid();
				info.accessToken = mAccessToken.getToken();
				info.refreshToken = mAccessToken.getRefreshToken();
				info.platform = "weibo";
				listener.onComplete(info, logintype);
			} else {
				// 以下几种情况，您会收到 Code：
				// 1. 当您未在平台上注册的应用程序的包名与签名时；
				// 2. 当您注册的应用程序包名与签名不正确时；
				// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
				String code = jsonResponse.getString("code");
				String message = "";
				if (!TextUtils.isEmpty(code)) {
					message = "\nObtained the code: " + code;
				}
				Platform info = new Platform();
				info.platform = "weibo";
				listener.onError(info, logintype, new Exception(message));
			}
		}

		@Override
		public void onCancel() {
			Platform info = new Platform();
			info.platform = "weibo";
			listener.onCancel(info, logintype);
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Platform info = new Platform();
			info.platform = "weibo";
			listener.onError(info, logintype, e);
		}
	}

}
