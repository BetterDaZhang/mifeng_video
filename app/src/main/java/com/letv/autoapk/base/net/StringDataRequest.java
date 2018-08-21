package com.letv.autoapk.base.net;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.common.net.HttpEngine;
import com.letv.autoapk.common.net.HttpEngine.HttpEngineCallback;
import com.letv.autoapk.common.net.RequestParam;
import com.letv.autoapk.common.net.ResponseData;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.utils.NetworkUtils;
import com.letv.autoapk.utils.SerializeableUtil;
import com.letv.autoapk.utils.SystemUtls;

public abstract class StringDataRequest {

	private final String TAG = "StringDataRequest";

	private static final String HEDAER_DID = "did";
	private static final String HEDAER_NETWORK = "network";
	private static final String HEDAER_MOBILE = "mobile";
	private static final String HEDAER_VERSION = "version";
	private static final String HEDAER_SV = "sv";
	public static final String PACKAGENAME = "packagname";
	public static final String LANGUAGE = "language";
	public static final String CHANNEL = "channel";
	public static final String AUTO_TOKEN = "authtoken";
	public static final String IP = "ip";

	public static final int PAGE_SIZE_COUNT = 20;
	public static final int PAGE_SIZEB_COUNT = 8;
	public static final int PAGE_SIZEC_COUNT = 18;

	/**
	 * http请求错误
	 */
	public static final int CODE_HTTP_ERROR = 1;
	/**
	 * 客户端参数错误
	 */
	public static final int CODE_PARAM_ERROR = 2;
	/**
	 * response返回数据解析错误
	 */
	public static final int CODE_RESPONSE_PARSE_ERROR = 3;

	/**
	 * HTTP请求成功
	 */
	public static final int CODE_HTTP_OK = 200;
	/**
	 * 服务端返回状态码-成功
	 */
	public static final int STATUS_SUCESS_CODE = 0;
	/**
	 * 服务端地址
	 */
	// public static final String MAIN_URL = "https://10.11.144.234:8443";
	// public static final String MAIN_URL = "http://121.40.113.108";
	// public static final String MAIN_URL = "http://10.11.144.234:8080";
//	public static final String MAIN_URL = "https://saasapi.lecloud.com";
	public static final String MAIN_URL = "http://47.99.50.57/interface";
	// public static final String MAIN_URL

	// ="http://10.58.136.129/mobileappproxy";
	/**
	 * json通用字段
	 */
	public static final String STATE = "state";
	public static final String MESSAGE = "message";
	public static final String ALERT_MESSAGE = "alertMessage";
	public static final String CONTENT = "content";
	public static final String LIST = "list";
	public static final String TOTAL_PAGE = "totalpage";
	public static final String CURPAGE = "curpage";

	public static final String PAGE = "page";
	public final static String PAGE_SIZE = "pageSize";
	public final static String PAGE_SIZEB = "pageSizeB";
	public final static String PAGE_SIZEC = "pageSizeC";
	public final static String PAGE_ID = "pageId";
	public static final String TOKEN = "authtoken";

	public static final String TENANT_ID = "tenantId";
	public static final String TEMPLATE_ID = "gtpId";
	public static final String USER_ID = "userId";
	public static final String CID = "cid";
	protected Context mContext;

	protected String alertMsg;

	/**
	 * 请求上行输入参数
	 */
	protected Map<String, String> mInputParam;
	/**
	 * 请求上行输入参数,第二个参数为流
	 */
	protected Map<String, OutputStream> mOutputStreamInputParam;
	/**
	 * 请求下行数据解析后返回的数据集
	 */
	protected Object[] mOutputData;

	/**
	 * 数据请求状态回调
	 */
	protected DataRequestCallback mCallback;
	protected CacheListener mCacheListener;
	protected boolean isPreLoadCache = false;
	protected RetryPolicy retryPolicy;

	public StringDataRequest(Context context) {
		mContext = context;
	}

	public void setCallback(DataRequestCallback cb) {
		mCallback = cb;
	}

	public void setCacheListener(CacheListener cacheListener) {
		mCacheListener = cacheListener;
	}

	public Map<String, String> getInputParam() {
		return mInputParam;
	}

	// 设置上传参数类型为String
	public StringDataRequest setInputParam(Map<String, String> mInputParam) {
		this.mInputParam = mInputParam;
		return this;
	}

	// 设置上传参数为流
	public StringDataRequest setOutputStreamInputParam(Map<String, OutputStream> mOutputStreamInputParam) {
		this.mOutputStreamInputParam = mOutputStreamInputParam;
		return this;
	}

	public Object[] getOutputData() {
		return mOutputData;
	}

	public void setPreLoadCache(boolean isLoad) {
		isPreLoadCache = isLoad;
	}

	public StringDataRequest setOutputData(Object... mOutputData) {
		this.mOutputData = mOutputData;
		return this;
	}

	public void requestTask(int method, final DataRequestCallback cb) {
		mCallback = cb;
		RequestParam reqParams = makeRequest(method);

		if (reqParams == null) {
			Logger.e(TAG, "request params is null.");
		}
		// Log.i(TAG, getRequestUrl() + reqParams.getQueryStringParameter());
		HttpEngine httpEngine = HttpEngine.getInstance(mContext.getApplicationContext());
		httpEngine.sendAsynStringRequest(method, getRequestUrl(), reqParams, new HttpEngineCallback<String>() {

			@Override
			public void onSuccess(ResponseData<String> data) {

				String content = data.data;
				int statusCode = parseResponse(content);

				if (statusCode == 0) {
					if (cb != null) {
						cb.onDataRequestSuccess(mOutputData);
					}
				} else {
					if (cb != null) {
						cb.onDataRequestFailed(data.httpCode, alertMsg);
					}
				}
			}

			@Override
			public void onFailed(ResponseData<String> data) {
				if (cb != null) {
					cb.onDataRequestFailed(data.httpCode, data.volleyError.getMessage());
				}
				Logger.e(TAG, "requestTask ERROR:" + data.httpCode);
			}
		});
	}

	public int request(int method) {
		return request(method, false);
	}

	/**
	 * 同步数据请求
	 * 
	 * @param method
	 * @return
	 */
	public int request(int method, boolean isLoadCache) {

		int code = -1;
		isPreLoadCache = isLoadCache;
		RequestParam param = makeRequest(method);
		if (param == null) {
			Logger.e(TAG, "request params is null.");
		}
		if (isLoadCache) {
			loadCache(method, param);
		}
		ResponseData<String> response = executeHttpRequest(method, param);
		code = response.httpCode;
		if (code != 200) {
			Logger.e(TAG, "http error , status code is " + code);
			 showErrorMsg(200, "网络请求失败");
			if (mCallback != null) {
				mCallback.onDataRequestFailed(code, mContext.getString(R.string.base_neterror));
			}
			return code;
		}
		try {
			String responseStr = response.data;
			// ------------------------------>>>>>>>>>>>>>>>>>>>>>>>
			code = parseResponse(responseStr);
		} catch (Exception e) {
			Logger.log(e);
			return CODE_RESPONSE_PARSE_ERROR;
		}
		return code;
	}

	/**
	 * 解析下行数据-通用解析
	 * 
	 * @param responseStr
	 * @return
	 */
	public int parseResponse(String responseStr) {
		if (responseStr == null) {
			Logger.e(TAG, "response str is null");
			return CODE_RESPONSE_PARSE_ERROR;
		}
		Logger.d(TAG, "response data:" + responseStr);
		try {
			// //////////////////////////////////////////////////////// Begin

			JSONObject responseJSON = new JSONObject(responseStr);
			int statusCode = -1;
			try {
				statusCode = responseJSON.getInt(STATE);
			} catch (Exception e) {
				Logger.log(e);
			}

			String message = responseJSON.optString(MESSAGE);

			alertMsg = responseJSON.optString(ALERT_MESSAGE);

			String content = responseJSON.optString(CONTENT);

			statusCode = onParseResponse(statusCode, alertMsg, content, mOutputData);

			Logger.e(TAG, "statusCode:" + statusCode);
			Logger.e(TAG, "message:" + message);

			if (statusCode == 3) {// token失效
				SerializeableUtil.saveObject(mContext, MyApplication.USER_INFO, null);
				MyApplication.getInstance().putBoolean(MyApplication.ISLOGIN, false);// 将用户登录状态改为未登录
				if (mContext instanceof BaseActivity) {
					((BaseActivity) mContext).showToastSafe(mContext.getString(R.string.base_authtimeout),
							Toast.LENGTH_SHORT);
				}
			} else {
				showErrorMsg(statusCode, alertMsg);
			}
			// //////////////////////////////////////////////////// Failed中间包异常
			return statusCode;
		} catch (JSONException e) {
			Logger.log(e);
		}

		return CODE_RESPONSE_PARSE_ERROR;
	}

	protected void showErrorMsg(int statusCode, String alertMsg) {
		if (mContext instanceof BaseActivity) {
			if (statusCode != 0 && !TextUtils.isEmpty(alertMsg)) {
				((BaseActivity) mContext).showToastSafe(alertMsg, Toast.LENGTH_SHORT);
			}
		}
	}

	protected ResponseData<String> executeHttpRequest(int method, RequestParam param) {
		HttpEngine httpEngine = HttpEngine.getInstance(mContext.getApplicationContext());
		// loadCache(method, param, httpEngine);
		ResponseData<String> response = httpEngine.sendStringRequest(method, getRequestUrl(), param, getRetryPolicy());
		return response;
	}

	public RetryPolicy getRetryPolicy() {
		if (retryPolicy == null) {
			return new DefaultRetryPolicy(4000, 1, 1f);
		}
		return retryPolicy;
	}

	public void setRetryPolicy(RetryPolicy retryPolicy) {
		this.retryPolicy = retryPolicy;
	}

	private void loadCache(int method, RequestParam param) {
		HttpEngine httpEngine = HttpEngine.getInstance(mContext.getApplicationContext());
		String url = getRequestUrl();
		if (method == Request.Method.GET) {
			url = url + param.getQueryStringParameter();
		}
		String cacheJsonData = httpEngine.getCacheData(method + ":" + url);
		if (!TextUtils.isEmpty(cacheJsonData)) {
			int code = parseResponse(cacheJsonData);
			if (code == 0) {
				if (mCacheListener != null) {
					mCacheListener.handler.post(new Runnable() {
						@Override
						public void run() {
							mCacheListener.onRefreshCache(mOutputData);
						}
					});
				}
			}
		}
	}

	private RequestParam makeRequest(int method) {
		RequestParam params = new RequestParam();
		addHeaders(params);

		onMakeRequestParam(mInputParam);

		if (mInputParam == null) {
			Logger.w(TAG, "input params is null.");
			return params;
		}
		if (method == Request.Method.GET) {
			for (String key : mInputParam.keySet()) {
				try {
					String param = mInputParam.get(key);
					if (!TextUtils.isEmpty(param) && param != null) {
						params.addQueryStringParameter(key, URLEncoder.encode(param, "UTF-8"));
					}

				} catch (UnsupportedEncodingException e) {
					Logger.log(e);
				}
			}
		} else if (method == Request.Method.POST) {
			for (String key : mInputParam.keySet()) {
				try {
					String param = mInputParam.get(key);
					if (!TextUtils.isEmpty(param)) {
						// params.addBodyParameter(key, URLEncoder.encode(param,
						// "UTF-8"));
						params.addBodyParameter(key, param);
					}
				} catch (Exception e) {
					Logger.log(e);
				}
			}
		}
		return params;
	}

	protected void addHeaders(RequestParam params) {
		if (!TextUtils.isEmpty(getToken())) {
			params.addHeader(TOKEN, getToken());
		}
		// params.addHeader("Content-Type", "application/x-www-form-urlencoded;
		// charset=utf-8");
		params.addHeader(HEDAER_DID, SystemUtls.getIMEI(mContext));
		params.addHeader(HEDAER_MOBILE, android.os.Build.MODEL);
		params.addHeader(HEDAER_NETWORK, NetworkUtils.getNetType(mContext));
		params.addHeader(HEDAER_VERSION, String.valueOf(SystemUtls.getAppVersionCode(mContext)));
		params.addHeader(HEDAER_SV, SystemUtls.getOsVersion());
		params.addHeader(PACKAGENAME, MyApplication.getInstance().getPackageName());
		params.addHeader(CHANNEL, String.valueOf(SystemUtls.getChannelName(mContext)));
		params.addHeader(LANGUAGE, String.valueOf(SystemUtls.getLanguage(mContext)));
		params.addHeader(AUTO_TOKEN, String.valueOf(LoginInfoUtil.getToken(mContext)));
		params.addHeader(IP, com.letv.lepaysdk.utils.NetworkUtils.getLocalIpAddress(mContext));
	}

	/**
	 * 生成手机TOKEN 信息 需要传入TOKEN时，子类重写此方法
	 * 
	 * @return
	 */
	protected String getToken() {
		return "";
	}

	/**
	 * 获取请求的完整URL
	 * 
	 * @return
	 */
	protected String getRequestUrl() {
		// Logger.e(TAG, "request url : "+MAIN_URL + getUrl());
		return MAIN_URL + getUrl();
	}

	// protected boolean isPreLoadJsonCache() {
	// return false;
	// }

	/**
	 * 获取请求的相对URL-子类必须重写
	 * 
	 * @return
	 */
	protected abstract String getUrl();

	protected abstract void onMakeRequestParam(Map<String, String> InputParam);

	/**
	 * 解析JSON CONTENT-子类必须重写
	 * 
	 * @param statusCode
	 * @param alertMessage
	 * @param content
	 * @param outputData
	 * @return
	 * @throws JSONException
	 */
	protected abstract int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData)
			throws JSONException;

	public interface DataRequestCallback {
		public void onDataRequestSuccess(Object[] mOutputData);

		public void onDataRequestFailed(int errorCode, String msg);
	}

}
