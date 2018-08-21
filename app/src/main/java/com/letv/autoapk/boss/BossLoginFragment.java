package com.letv.autoapk.boss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.DetailActivity;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.LoginInfo;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.PlayRecordInfo;
import com.letv.autoapk.dao.PlayedRecordDao;
import com.letv.autoapk.open.OpenLoginActivity;
import com.letv.autoapk.open.OpenSdk;
import com.letv.autoapk.open.Platform;
import com.letv.autoapk.ui.collection.CollectionAPI;
import com.letv.autoapk.ui.login.FillDataFragment;
import com.letv.autoapk.ui.login.ForgetPasswordFragment;
import com.letv.autoapk.ui.login.LoginDataRequest;
import com.letv.autoapk.ui.login.RegisterFragment;
import com.letv.autoapk.ui.login.UserTPLoginDataRequest;
import com.letv.autoapk.ui.record.RecordsAPI;
import com.letv.autoapk.utils.Base64Util;
import com.letv.autoapk.utils.DESUtils;
import com.letv.autoapk.utils.SerializeableUtil;
import com.letv.autoapk.utils.SystemUtls;

public class BossLoginFragment extends BaseTitleFragment implements OnClickListener {

	private static final int LOGINREQ = 1;
	private List<LoginInfo> infos = new ArrayList<LoginInfo>();// 登陆成功后返回的信息

	private OpenSdk openSdk;
	private EditText et_username;
	private EditText et_password;
	private int loginFlag;
	boolean needBossAuth;

	@Override
	protected boolean loadingData() {
		return true;
	}

	// private Animation shake;
	@Override
	protected View setupDataView() {
		Bundle bundle = mActivity.getIntent().getExtras();
		loginFlag = bundle.getInt(BossLoginAPI.LOGINAIM);
		needBossAuth = bundle.getBoolean(BossManager.NEED_AUTH);
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		View view = inflater.inflate(R.layout.mine_login, null);

		setTitleLeftResource(R.drawable.mine_login_back, mActivity.dip2px(16));
		setLeftClickListener(new TitleLeftClickListener() {

			@Override
			public void onLeftClickListener() {
				getActivity().finish();
			}
		});
		setTitle(getActivity().getString(R.string.login), getResources().getColor(R.color.code6));
		openSdk = MyApplication.getInstance().getOpenSdk();
		if (openSdk.hasOpenId()) {
			if (openSdk.hasBLOG()) {
				View blog = view.findViewById(R.id.rl_login_sina);
				blog.setVisibility(View.VISIBLE);
				view.findViewById(R.id.iv_login_sina).setOnClickListener(this);
			}
			if (openSdk.hasMM()) {
				View mm = view.findViewById(R.id.rl_login_weixin);
				mm.setVisibility(View.VISIBLE);
				view.findViewById(R.id.iv_login_weixin).setOnClickListener(this);
			}
			if (openSdk.hasQQ()) {
				View qq = view.findViewById(R.id.rl_login_qq);
				qq.setVisibility(View.VISIBLE);
				view.findViewById(R.id.iv_login_qq).setOnClickListener(this);
			}
		} else {
			view.findViewById(R.id.rl_login_imgs).setVisibility(View.GONE);
		}
		view.findViewById(R.id.fl_denglu).setOnClickListener(this);
		view.findViewById(R.id.fl_zhuce).setOnClickListener(this);
		view.findViewById(R.id.tv_forget_pwd).setOnClickListener(this);
		et_username = (EditText) view.findViewById(R.id.et_username);
		et_password = (EditText) view.findViewById(R.id.et_password);
		// shake = AnimationUtils.loadAnimation(mActivity, R.anim.shake);
		setLeftClickListener(new TitleLeftClickListener() {

			@Override
			public void onLeftClickListener() {
				hideSoftKeybord();
				getActivity().finish();
			}
		});
		return view;
	}

	@Override
	protected View createContentView() {
		return setupDataView();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		hideSoftKeybord();
		int id = v.getId();
		switch (id) {
		case R.id.iv_login_qq:
			intent = new Intent(mActivity.getApplicationContext(), OpenLoginActivity.class);
			intent.putExtra("type", OpenSdk.TYPE_QQ);
			startActivityForResult(intent, LOGINREQ);
			break;
		case R.id.iv_login_weixin:
			intent = new Intent(mActivity.getApplicationContext(), OpenLoginActivity.class);
			intent.putExtra("type", OpenSdk.TYPE_MM);
			startActivityForResult(intent, LOGINREQ);
			break;
		case R.id.iv_login_sina:
			intent = new Intent(mActivity.getApplicationContext(), OpenLoginActivity.class);
			intent.putExtra("type", OpenSdk.TYPE_BLOG);
			startActivityForResult(intent, LOGINREQ);
			break;
		case R.id.fl_zhuce:
			FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
			Fragment registerFragment = Fragment.instantiate(getActivity(), RegisterFragment.class.getName());
			ft.replace(R.id.container, registerFragment).addToBackStack(null).commit();
			break;
		case R.id.tv_forget_pwd:
			intent = new Intent(mActivity.getApplicationContext(), DetailActivity.class);
			intent.putExtra(DetailActivity.FRAGMENTNAME, ForgetPasswordFragment.class.getName());
			startActivity(intent);
			break;
		case R.id.fl_denglu:
			String username = et_username.getText().toString().trim();
			String password = et_password.getText().toString().trim();
			if (TextUtils.isEmpty(username)
					|| !username.matches("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$")) {
				// et_username.startAnimation(shake);
				mActivity.showToastSafe(R.string.pls_input_correct_username, 0);
				return;
			} else if (TextUtils.isEmpty(password) || !LoginInfoUtil.isPasswordValid(password)) {
				// et_password.startAnimation(shake);
				mActivity.showToastSafe(getResources().getString(R.string.mine_password_error), 0);
				return;
			} else {
				// 登录
				userLogin(username, password, infos);
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LOGINREQ) {
			if (resultCode == Activity.RESULT_OK) {
				boolean loginok = data.getBooleanExtra("login", false);
				if (loginok) {
					final int type = data.getIntExtra("type", 0);
					String jsonString = data.getStringExtra("data");
					final Platform platform = new Gson().fromJson(jsonString, Platform.class);
					new UiAsyncTask<Integer>(this) {

						@Override
						protected void post(Integer result) {
							if (result == 0) {
								if (TextUtils.isEmpty(loginInfo.getNickName())) {
									Intent intent = new Intent(mActivity.getApplicationContext(), DetailActivity.class);
									intent.putExtra(DetailActivity.FRAGMENTNAME, FillDataFragment.class.getName());
									mActivity.startActivity(intent);
									return;
								}
								intentAfterLogin();
							} else if (result != 1) {
								// 登陆失败
								mActivity.showToastSafe(R.string.login_failed, 1);
								MyApplication.getInstance().putBoolean(MyApplication.ISLOGIN, false);
							}
						}

						@Override
						protected Integer doBackground() {
							int code = thirdPartyLogin(platform.accessToken, platform.openid,
									Long.toString(platform.tokenExpir), platform.platform, infos, type,
									platform.refreshToken);
							if (code == 0) {
								String lastUserId = LoginInfoUtil.getUserId(mActivity);
								PlayedRecordDao dao = MyApplication.getInstance()
										.getDaoByKey(PlayedRecordDao.class.getName());
								if (lastUserId != null && lastUserId.equals(infos.get(0).getUserId())) {
									new RecordsAPI(BossLoginFragment.this).addAllRecords(mActivity);
								} else {
									dao.deleleAll();
								}
								// 登陆成功
								MyApplication.getInstance().putBoolean(MyApplication.ISLOGIN, true);
								LoginInfo loginInfo = infos.get(0);
								BossLoginFragment.this.loginInfo = loginInfo;
								// 此处只是将登录信息存起来，并不表示已经登录成功
								saveThirdPartyParam();
								MyApplication.getInstance().putBoolean(MyApplication.IS_LOGIN_NORMAL, false);
								MyApplication.getInstance().putString("iconUrl" + loginInfo.getUserId(),
										loginInfo.getUserIcon());

								// 登录成功后，将云端播放记录存储在本地
								List<PlayRecordInfo> records = new RecordsAPI(BossLoginFragment.this)
										.getRecords(mActivity);
								if (records != null) {
									for (PlayRecordInfo playRecordInfo : records) {
										dao.save(playRecordInfo);
									}
								}
								new CollectionAPI(mActivity).addAllCollections();
							}
							return code;
						}
					}.showDialog().execute();
				} else {
					String errormsg = data.getStringExtra("data");
					if (errormsg != null)
						mActivity.showToastSafe(errormsg, 1);
				}
			}
		}
	}

	private List<VipInfo> vipInfos;

	/**
	 * 用户登录
	 * 
	 * @param username
	 * @param password
	 * @param context
	 *            ActionBarBaseActivity对象,否则,进度条将不能正确隐藏
	 * @param infos
	 *            请求数据成功后返回的登录信息数据
	 */
	public void userLogin(final String username, final String password, final List<LoginInfo> infos) {
		vipInfos = new ArrayList<VipInfo>();
		if (!SystemUtls.isNetworkConnected(mActivity)) {
			MyApplication.getInstance().putBoolean(MyApplication.ISLOGIN, false);
			return;
		}
		new UiAsyncTask<Integer>(this) {
			private PlayedRecordDao dao;
			private List<PlayRecordInfo> records;

			@Override
			protected void post(Integer result) {
				if (result == 0) {
					intentAfterLogin();
				}
			}

			@Override
			protected Integer doBackground() {
				LoginDataRequest request = new LoginDataRequest(mActivity);
				Map<String, String> mInputParam = new HashMap<String, String>();
				mInputParam.put("username", username);// 手机号
				mInputParam.put("userPassword", DESUtils.encryptBasedDes(password));
				mInputParam.put("name_type", "2");
				mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
				mInputParam.put("version", "2");
				mInputParam.put("s", "1");
				mInputParam.put("isaddboss", MyApplication.getInstance().isNeedBoss() + "");
				int code = request.setInputParam(mInputParam).setOutputData(infos, vipInfos)
						.request(Request.Method.POST);
				if (code == 0) {
					String lastUserId = LoginInfoUtil.getUserId(mActivity);
					MyApplication.getInstance().putBoolean(MyApplication.ISLOGIN, true);
					// 表示登录成功,将用户登录信息序列化
					LoginInfo info = infos.get(0);
					saveLoginInfo(mActivity, info);
					dao = MyApplication.getInstance().getDaoByKey(PlayedRecordDao.class.getName());
					if (lastUserId != null && lastUserId.equals(info.getUserId())) {
						new RecordsAPI(BossLoginFragment.this).addAllRecords(mActivity);
					} else {
						dao.deleleAll();
					}
					// 登录成功后，将云端播放记录存储在本地
					records = new RecordsAPI(BossLoginFragment.this).getRecords(mActivity);
					MyApplication.getInstance().putString("iconUrl" + info.getUserId(), info.getUserIcon());
					MyApplication.getInstance().putBoolean(MyApplication.IS_LOGIN_NORMAL, true);
					// 上传收藏记录
					new CollectionAPI(mActivity).addAllCollections();
				}
				return code;
			}

		}.showDialog().execute();
	}

	/**
	 * boss相关的登录成功后统一进行boss鉴权(为了流程简便)，但是有的是不需要鉴权的，所有有参数needAuth
	 * 
	 * @param flag
	 * @param needAuth
	 */
	private void bossAuth(final int flag) {
		new UiAsyncTask<Integer>(this) {

			@Override
			protected Integer doBackground() throws Throwable {
				return MyApplication.getInstance().getBossManager().auth();
			}

			protected void post(Integer result) {
				Bundle data = new Bundle();
				if (result != 0) {// 1：鉴权失败
					if (flag == BossManager.LOGIN_TO_IDVIP) {
						MyApplication.getInstance().setAuthSuccess(false);
						mActivity.finish();
					} else {
						MyApplication.getInstance().setAuthSuccess(false);
						MyApplication.getInstance().getBossManager().switchAim(flag, true);
					}
				} else {// 0：鉴权成功
					MyApplication.getInstance().setAuthSuccess(true);
					mActivity.finish();
				}
			};
		}.showDialog().execute();
	}

	private void bossSingleFilmAuth(final int flag) {
		new UiAsyncTask<Integer>(this) {

			@Override
			protected Integer doBackground() throws Throwable {
				return MyApplication.getInstance().getBossManager().auth();
			}

			protected void post(Integer result) {
				Bundle data = new Bundle();
				if (result != 0) {// 1：鉴权失败
					MyApplication.getInstance().setAuthSuccess(false);
				} else {// 0：鉴权成功
					MyApplication.getInstance().setAuthSuccess(true);
				}
				MyApplication.getInstance().getBossManager().switchAim(BossManager.FLAG_ENTER_VIPCENTER_AUTH, true);
			};
		}.showDialog().execute();
	}

	/**
	 * 保存登录信息
	 * 
	 * @param loginInfo
	 */
	public void saveLoginInfo(Context context, LoginInfo loginInfo) {
		SerializeableUtil.saveObject(context, MyApplication.USER_INFO, loginInfo);
		MyApplication.getInstance().putInfo(MyApplication.VIPINFO, vipInfos);
	}

	/**
	 * 三方登录
	 * 
	 * @param accessToken
	 *            token
	 * @param openId
	 * @param tokenExpir
	 *            token有效期
	 * @param platform
	 *            三方平台
	 * @param context
	 * @param infos
	 *            接收返回的登录信息
	 * @param flag
	 *            登录来源，是登录页还是欢迎页
	 * @param refreshToken
	 */
	private String accessToken;
	private String openId;
	private String tokenExpir;
	private String platform;
	private String refreshToken;
	private LoginInfo loginInfo;

	public int thirdPartyLogin(final String accessToken, final String openId, final String tokenExpir,
			final String platform, final List<LoginInfo> infos, final int flag, String refreshToken) {

		this.accessToken = accessToken;
		this.openId = openId;
		this.tokenExpir = tokenExpir;
		this.platform = platform;
		this.refreshToken = refreshToken;
		vipInfos = new ArrayList<VipInfo>();
		if (!TextUtils.isEmpty(refreshToken) && flag == OpenSdk.TYPE_MM) {// 说明是微信登录
			String refreshurl = String.format(OpenSdk.refreshTokenUrl, openSdk.WXID, refreshToken);
			try {
				String result = x.http().getSync(new RequestParams(refreshurl), String.class);
				JSONObject jsonObj = new JSONObject(result);
				BossLoginFragment.this.accessToken = jsonObj.getString("access_token");
				BossLoginFragment.this.refreshToken = jsonObj.getString("refresh_token");
			} catch (HttpException e1) {
				Logger.log(e1);
			} catch (IOException e) {
				Logger.log(e);
			} catch (JSONException e) {
				Logger.log(e);
			} catch (Throwable e) {
				Logger.log(e);
			}
		}
		UserTPLoginDataRequest request = new UserTPLoginDataRequest(mActivity);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put("accessToken", BossLoginFragment.this.accessToken);
		mInputParam.put("openId", BossLoginFragment.this.openId);
		mInputParam.put("tokenExpir", BossLoginFragment.this.tokenExpir);
		mInputParam.put("loginType", String.valueOf(flag));
		mInputParam.put("tenantId", MyApplication.getInstance().getTenantId());
		mInputParam.put("isaddboss", MyApplication.getInstance().isNeedBoss() + "");
		if (flag == OpenSdk.TYPE_QQ) {
			mInputParam.put("platAppId", openSdk.QQID);
		}
		int code = request.setInputParam(mInputParam).setOutputData(infos, vipInfos).request(Request.Method.GET);
		return code;
	}

	public void saveThirdPartyParam() {
		if (TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(openId) || TextUtils.isEmpty(tokenExpir)
				|| TextUtils.isEmpty(platform) || mActivity == null || loginInfo == null) {
			return;
		}
		// 将三方登录参数存放
		MyApplication.getInstance().putString("accessToken", Base64Util.encode(accessToken));
		MyApplication.getInstance().putString("openId", Base64Util.encode(openId));
		MyApplication.getInstance().putString("tokenExpir", Base64Util.encode(tokenExpir));
		MyApplication.getInstance().putString("loginType", Base64Util.encode(platform));
		// 表示登录成功,将用户登录信息存放到application中
		saveLoginInfo(mActivity, loginInfo);
	}

	@Override
	protected void onHandleMessage(Message msg) {

	}

	/**
	 * 登录完成后的意图
	 */
	private void intentAfterLogin() {
		/**
		 * 登录完成之后有5种情况，1.进入兑换码页面的2、直接支付的（会员中心） 3、进行鉴权进入订单详情4、进入鉴权进入会员中心
		 * 5、无须鉴权，需要判断是否是会员进入会员中心
		 */
		switch (loginFlag) {
		case BossManager.LOGIN_TO_CDKEY:
			mActivity.startActivity(MyApplication.getInstance().getBossManager().getCdkeyIntent(mActivity));
			mActivity.finish();
			break;
		case BossManager.FLAG_ENTER_BOSS_PAY:
			PackageInfo info = (PackageInfo) MyApplication.getInstance().getInfo(MyApplication.CURRENT_PACKAGE_INFO);
			LepayManager lepayManager = LepayManager.getInstance(mActivity, BossLoginFragment.this);
			lepayManager.vipOrder(info.pid, "104002");
			break;
		case BossManager.LOGIN_TO_IDVIP:
			// 登录判断是否是会员，是会员则关闭登录页面展示是会员的情况，非会员则跳转到会员中心，单片开通会员的时候
			if (LoginInfoUtil.isVip(mActivity)) {// 鉴权
				bossAuth(loginFlag);
			} else {// 非会员，开通会员，并且开通后鉴权
				bossSingleFilmAuth(loginFlag);
			}
			break;
		default:
			if (needBossAuth) {
				bossAuth(loginFlag);
			} else if (LoginInfoUtil.isVip(mActivity) && loginFlag == BossManager.FLAG_ENTER_VIPCENTER) {
				// 会员去广告
				MyApplication.getInstance().setAuthSuccess(true);
				mActivity.finish();
			} else {
				MyApplication.getInstance().setAuthSuccess(false);
				MyApplication.getInstance().getBossManager().switchAim(loginFlag, true);
			}
			break;
		}
	}

	public void hideSoftKeybord() {
		InputMethodManager inputMethodManager = (InputMethodManager) mActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean bool = inputMethodManager.showSoftInput(et_username, InputMethodManager.SHOW_FORCED);
		inputMethodManager.hideSoftInputFromWindow(et_username.getApplicationWindowToken(), 0);
	}
}
