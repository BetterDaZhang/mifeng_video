package com.letv.autoapk.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.open.OpenSdk;
import com.letv.lepaysdk.utils.LOG;
import com.letv.lepaysdk.wxpay.WXPay;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * LePaySDK回调类
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	private IWXAPI api;
	private WXPay wxPay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wxPay = WXPay.getInstance(this.getApplicationContext());
		// if (openSdk.hasMM()) {
		// api = (IWXAPI) openSdk.getOpenObject(OpenSdk.TYPE_MM);
		// }else{
		api = WXAPIFactory.createWXAPI(this, null);
		// }
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		if (api != null) {
			api.handleIntent(intent, this);
		}

	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		wxPay.setResp(resp);
		LOG.logE("resp:" + resp.errCode);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}