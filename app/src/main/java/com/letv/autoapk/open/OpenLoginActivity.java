package com.letv.autoapk.open;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.task.AbsTask;
import org.xutils.common.task.Priority;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

public class OpenLoginActivity extends BaseActivity {
	private final static int TIMEOUT = 30 * 1000;
	private OpenSdk openSdk;
	private LoginUtil loginUtil;
	public static final int MSG_AUTH_CANCEL = 2;
	public static final int MSG_AUTH_ERROR = 3;
	public static final int MSG_AUTH_COMPLETE = 4;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.opensdk_login);
		setScreenBrightness();
		openSdk = MyApplication.getInstance().getOpenSdk();
		loginUtil = new LoginUtil(this, new PlatformActionListener() {

			@Override
			public void onCancel(Platform arg0, int arg1) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = MSG_AUTH_CANCEL;
				getDefaultHandler().sendMessage(msg);
			}

			@Override
			public void onComplete(Platform info, int type) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = MSG_AUTH_COMPLETE;
				msg.obj = new Object[] { info, type };
				getDefaultHandler().sendMessage(msg);
			}

			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = MSG_AUTH_ERROR;
				msg.obj = new Object[] { arg0.platform, arg2 };
				getDefaultHandler().sendMessage(msg);
			}
		});
		if (getIntent() != null) {
			final int type = getIntent().getExtras().getInt("type");
			Runnable loginRunnable = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					loginUtil.authorize(type);
				}
			};
			if(type==OpenSdk.TYPE_MM){
				x.task().post(loginRunnable);
			}else{
				x.task().run(loginRunnable);
			}
			
		}
	}

	private void setScreenBrightness() {
		Window window = getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		/**
		 * 此处设置亮度值。dimAmount代表黑暗数量，也就是昏暗的多少，设置为0则代表完全明亮。 范围是0.0到1.0
		 */
		lp.dimAmount = (float) 0.1;
		window.setAttributes(lp);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_API) {
			if (resultCode == Constants.RESULT_LOGIN) {
				Tencent.handleResultData(data, loginUtil.loginListener);
			}
		} else if (loginUtil.mSsoHandler != null) {
			loginUtil.mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onHandleMessage(Message msg) {
		switch (msg.what) {
		case MSG_AUTH_ERROR:
			Object[] objects = (Object[]) msg.obj;
			Intent errorintent = new Intent();
			errorintent.putExtra("login", false);
			errorintent.putExtra("data", objects[1].toString());
			setResult(Activity.RESULT_OK, errorintent);
			break;
		case MSG_AUTH_COMPLETE:
			Object[] objectss = (Object[]) msg.obj;
			Platform platform = (Platform) objectss[0];
			Gson gson = new Gson();
			String data = gson.toJson(platform);
			Intent okintent = new Intent();
			okintent.putExtra("login", true);
			okintent.putExtra("type", (Integer) objectss[1]);
			okintent.putExtra("data", data);
			setResult(Activity.RESULT_OK, okintent);
			break;
		case MSG_AUTH_CANCEL:
			setResult(Activity.RESULT_CANCELED);
			break;
		default:
			break;
		}
		finish();

	}

	@Override
	protected void onNewIntent(Intent newintent) {
		// TODO Auto-generated method stub
		super.onNewIntent(newintent);
		Intent intent = newintent;
		if (intent != null && intent.hasExtra("wxresp")) {
			Bundle bundle = intent.getBundleExtra("wxresp");
			int result = bundle.getInt("result");
			if (result == RESULT_OK) {
				String code = bundle.getString("data");
				getWXCodeTask task = new getWXCodeTask(code);
				x.task().start(task);
			} else {
//				Platform platform = new Platform();
//				platform.platform = "mm";
				Message msg = new Message();
				msg.what = MSG_AUTH_CANCEL;
//				msg.obj = new Object[] { platform.platform, new IOException() };
				getDefaultHandler().sendMessage(msg);
			}
			intent.removeExtra("wxresp");
		}
	}

	class getWXCodeTask extends AbsTask<JSONObject> {

		private String code;

		public getWXCodeTask(String code) {
			// TODO Auto-generated constructor stub
			this.code = code;
		}
		public Priority getPriority() {
	        return Priority.UI_NORMAL;
	    }
		@Override
		protected JSONObject doBackground() throws Throwable {
			String url = String.format(OpenSdk.WXGETTOKEN, openSdk.WXID, openSdk.WXKEY, code);
			HttpURLConnection conn = null;
			JSONObject result = null;
			try {
				conn = (HttpURLConnection) new URL(url).openConnection();
				conn.setConnectTimeout(TIMEOUT);
				conn.setReadTimeout(TIMEOUT);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				conn.setRequestMethod("GET");
				conn.connect();
				int rescode = conn.getResponseCode();
				if (rescode == 200) {
					InputStream inputStream = conn.getInputStream();
					if (inputStream == null) {
						return null;
					}
					try {

						final BufferedReader ins = new BufferedReader(new InputStreamReader(inputStream), 8192);
						final StringBuilder response = new StringBuilder();
						String line;
						while ((line = ins.readLine()) != null) {
							response.append(line);
						}
						String res = response.toString();
						result = new JSONObject(res);
					} catch (Exception e) {
					} finally {
						try {
							inputStream.close();
							inputStream = null;
							conn.disconnect();
							conn = null;
						} catch (Exception e) {
							Logger.log(e);
						}
					}
				}
			} catch (MalformedURLException e) {
			} catch (Exception e) {
				Logger.log(e);	
			}
			return result;
		}

		@Override
		protected void onSuccess(JSONObject result) {
			Platform platform = new Platform();
			try {
				platform.platform = "mm";
				platform.tokenExpir = result.getLong("expires_in");
				platform.openid = result.getString("openid");
				platform.accessToken = result.getString("access_token");
				platform.refreshToken = result.getString("refresh_token");
				Message msg = new Message();
				msg.what = MSG_AUTH_COMPLETE;
				msg.obj = new Object[] { platform, OpenSdk.TYPE_MM };
				getDefaultHandler().sendMessage(msg);
			} catch (Exception e) {
				Message msg = new Message();
				msg.what = MSG_AUTH_ERROR;
				msg.obj = new Object[] { platform.platform, e };
				getDefaultHandler().sendMessage(msg);
			}
			
		}

		@Override
		protected void onError(Throwable ex, boolean isCallbackError) {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.what = MSG_AUTH_ERROR;
			msg.obj = new Object[] { "mm", ex };
			getDefaultHandler().sendMessage(msg);
		}

	}
}
