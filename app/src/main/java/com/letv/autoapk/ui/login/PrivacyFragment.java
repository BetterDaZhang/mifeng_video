package com.letv.autoapk.ui.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.WebviewActivity;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.widgets.HorizontalProgressBarWithNumber;
import com.letv.autoapk.widgets.LoadingLayout;

public class PrivacyFragment extends BaseTitleFragment{
	private WebView wv;
	private HorizontalProgressBarWithNumber pb_progressBar;
	private String URL = "file:///android_asset/privacyTerms.htm";
	@Override
	protected void onHandleMessage(Message msg) {
		
	}
	
	protected View createContentView() {
		Bundle intent = getArguments();
		if(intent != null){
			URL = intent.getString("web_url");
		}
		View view = View.inflate(mActivity, R.layout.base_webview, null);

		setTitle(getString(R.string.privacy_title),getResources().getColor(R.color.code6));
		setTitleLeftResource(R.drawable.base_head_back, mActivity.dip2px(3));

		wv = (WebView) view.findViewById(R.id.wv);
		pb_progressBar = (HorizontalProgressBarWithNumber) view.findViewById(R.id.pb_webview);

		wv.setWebChromeClient(new WebChromeClient() {
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

		wv.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}
			@Override
			public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
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
		});
		wv.loadUrl(URL);
		setLeftClickListener(new TitleLeftClickListener() {
			@Override
			public void onLeftClickListener() {
				getActivity().finish();
			}
		});
		return view;
	}

	@Override
	protected boolean loadingData() {
		return false;
	}

	@Override
	protected View setupDataView() {
		return null;
	}

}
