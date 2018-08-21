package com.letv.autoapk.base.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.letv.autoapk.R;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.widgets.HorizontalProgressBarWithNumber;

public class WebviewActivity extends BaseActivity {
	private WebView webview;
	private HorizontalProgressBarWithNumber pb_progressBar;
	private String webviewUrl = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.base_webview);

		webviewUrl =  getIntent().getStringExtra("webUrl");
		if (webviewUrl == null || webviewUrl.isEmpty()) {
			webviewUrl = "error";
		}

		webview = (WebView) findViewById(R.id.wv);
		pb_progressBar = (HorizontalProgressBarWithNumber) findViewById(R.id.pb_webview);

		webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				pb_progressBar.setVisibility(View.VISIBLE);
				pb_progressBar.setProgress(newProgress);
				if (newProgress == 100) {
					pb_progressBar.setVisibility(View.GONE);
				}
				super.onProgressChanged(view, newProgress);
			}
		});

		webview.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(WebviewActivity.this);
				String message = "网页证书不安全，是否继续访问？";
				builder.setTitle("证书验证提示");
				builder.setMessage(message);
				builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.proceed();
					}
				});
				builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.cancel();
					}
				});
				final AlertDialog dialog = builder.create();
				dialog.show();
			}
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("http:") || url.startsWith("https:")) {
					return false;
				}
				try {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(intent);
				} catch (Exception e) {
					Logger.log(e);
					return false;
				}
				// 下面这一行保留的时候，原网页仍报错，新网页正常.所以注释掉后，也就没问题了
				// view.loadUrl(url);//重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				return true;
			}
		});

		// LinearLayout.LayoutParams mWebViewLP = new LinearLayout.LayoutParams(
		// LinearLayout.LayoutParams.FILL_PARENT,
		// LinearLayout.LayoutParams.FILL_PARENT);
		// webview.setLayoutParams(mWebViewLP);
		// webview.setInitialScale(25);
		WebSettings settings = webview.getSettings();
		// 适应屏幕
		settings.setUseWideViewPort(true);// 任意比例缩放
		settings.setSupportZoom(true);
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		settings.setLoadWithOverviewMode(true);
		settings.setBuiltInZoomControls(true);
		settings.setJavaScriptEnabled(true);
		settings.setDomStorageEnabled(true);
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		// sendDidMd5Request();
		webview.loadUrl(webviewUrl);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
			// 返回键退回
			webview.goBack();
			return true;
		} else {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * public void sendDidMd5Request(){ final ShakePrizeDataRequest request =
	 * new ShakePrizeDataRequest(this); Map<String, String> mInputParam = new
	 * TreeMap<String, String>(); mInputParam.put("did",
	 * SystemUtls.getIMEI(this));
	 * request.setInputParam(mInputParam).setOutputData
	 * (null).requestTask(Request.Method.GET, new DataRequestCallback() {
	 * 
	 * @Override public void onDataRequestFailed(int errorCode, String msg) { }
	 * 
	 * @Override public void onDataRequestSuccess(Object[] mOutputData) {
	 * RequestParam params = new RequestParam(); request.addHeaders(params);
	 * webview.loadUrl(webviewUrl,params.getHeaders()); }
	 * 
	 * });
	 * 
	 * }
	 */

	@Override
	protected void onHandleMessage(Message msg) {
		// TODO Auto-generated method stub

	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			if(webview!=null){
				webview.onResume();
			}
		} catch (Exception e) {
			Logger.log(e);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		try {
			if(webview!=null){
				webview.onPause();
			}
		} catch (Exception e) {
			Logger.log(e);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			// webView.clearFocus();
			webview.setVisibility(View.GONE);
			webview.clearCache(true);
			webview.clearHistory();
			long timeout = ViewConfiguration.getZoomControlsTimeout();
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					
					webview.destroy();
				}
			}, timeout);
			
			System.gc();
		} catch (Exception e) {
			Logger.log(e);
		}
	}
}
