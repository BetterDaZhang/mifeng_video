package com.letv.autoapk.ui.login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.base.activity.DetailActivity;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.LoginInfo;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.boss.BossManager;
import com.letv.autoapk.boss.CdkeyFragment;
import com.letv.autoapk.boss.LepayManager;
import com.letv.autoapk.boss.MemberMobileFragment;
import com.letv.autoapk.boss.VipInfo;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.PlayRecordInfo;
import com.letv.autoapk.dao.PlayedRecordDao;
import com.letv.autoapk.open.OpenLoginActivity;
import com.letv.autoapk.open.OpenSdk;
import com.letv.autoapk.open.Platform;
import com.letv.autoapk.ui.collection.CollectionAPI;
import com.letv.autoapk.ui.main.MainAPI;
import com.letv.autoapk.ui.record.RecordsAPI;
import com.letv.autoapk.utils.Base64Util;
import com.letv.autoapk.utils.DESUtils;
import com.letv.autoapk.utils.SerializeableUtil;
import com.letv.autoapk.utils.SystemUtls;

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
import android.widget.EditText;

public class LoginFragment extends BaseTitleFragment implements OnClickListener {

	private static final int LOGINREQ = 1;
	private List<LoginInfo> infos = new ArrayList<LoginInfo>();// 登陆成功后返回的信息

	private OpenSdk openSdk;
	private EditText et_username;
	private EditText et_password;

	@Override
	protected boolean loadingData() {
		return true;
	}

	// private Animation shake;
	@Override
	protected View setupDataView() {
		setStatusBarColor(getResources().getColor(R.color.code04));
		Bundle bundle = mActivity.getIntent().getExtras();
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		View view = inflater.inflate(R.layout.mine_login, null);

		setTitleLeftResource(R.drawable.mine_login_back, mActivity.dip2px(16));
		setLeftClickListener(new TitleLeftClickListener() {

			@Override
			public void onLeftClickListener() {
				getActivity().finish();
			}
		});
		setTitle(getString(R.string.login), getResources().getColor(R.color.code6));
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
		return view;
	}

	@Override
	protected View createContentView() {
		return setupDataView();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
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
			SystemUtls.hideInputMethod(mActivity, v);
			String username = et_username.getText().toString().trim();
			String password = et_password.getText().toString().trim();
			if (TextUtils.isEmpty(username)
					|| !username.matches("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$")) {
				// et_username.startAnimation(shake);
				mActivity.showToastSafe(getString(R.string.pls_input_correct_username), 0);
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
								mActivity.showToastSafe(R.string.login_success, 1);
								mActivity.finish();
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
									new RecordsAPI(LoginFragment.this).addAllRecords(mActivity);
								} else {
									dao.deleleAll();
								}
								// 登陆成功
								MyApplication.getInstance().putBoolean(MyApplication.ISLOGIN, true);

								LoginInfo loginInfo = infos.get(0);
								LoginFragment.this.loginInfo = loginInfo;
								// 此处只是将登录信息存起来，并不表示已经登录成功
								saveThirdPartyParam();
								MyApplication.getInstance().putBoolean(MyApplication.IS_LOGIN_NORMAL, false);
								MyApplication.getInstance().putString("iconUrl" + loginInfo.getUserId(),
										loginInfo.getUserIcon());

								// 登录成功后，将云端播放记录存储在本地
								List<PlayRecordInfo> records = new RecordsAPI(LoginFragment.this).getRecords(mActivity);
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
					mActivity.showToastSafe(R.string.login_success, 1);
					mActivity.finish();
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
						new RecordsAPI(LoginFragment.this).addAllRecords(mActivity);
					} else {
						dao.deleleAll();
					}
					// 登录成功后，将云端播放记录存储在本地
					records = new RecordsAPI(LoginFragment.this).getRecords(mActivity);
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
				LoginFragment.this.accessToken = jsonObj.getString("access_token");
				LoginFragment.this.refreshToken = jsonObj.getString("refresh_token");
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
		mInputParam.put("accessToken", LoginFragment.this.accessToken);
		mInputParam.put("openId", LoginFragment.this.openId);
		mInputParam.put("tokenExpir", LoginFragment.this.tokenExpir);
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

}
