package com.letv.autoapk.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.open.OpenLoginActivity;
import com.letv.autoapk.open.OpenSdk;
import com.letv.autoapk.utils.SystemUtls;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

	private IWXAPI api;


	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		api = (IWXAPI) MyApplication.getInstance().getOpenSdk()
				.getOpenObject(OpenSdk.TYPE_MM);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		// TODO Auto-generated method stub
		if (resp == null) {
			finish();
			return;
		}
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			if (resp instanceof SendAuth.Resp) {
				SendAuth.Resp temp = (SendAuth.Resp) resp;
				final String code = temp.code;
				final String state = temp.state;
				if (state.endsWith(SystemUtls.getIMEI(this))) {
					Intent intent = new Intent(getApplicationContext(), OpenLoginActivity.class);
					Bundle bundle = new Bundle();
					if (code == null) {
						bundle.putInt("result", Activity.RESULT_CANCELED);
					} else {
						bundle.putInt("result", Activity.RESULT_OK);
						bundle.putString("data", code);
					}
					intent.putExtra("wxresp", bundle);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					finish();
					startActivity(intent);
				}
			}
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			if (resp instanceof SendAuth.Resp) {
				Toast.makeText(this, R.string.usercancel, Toast.LENGTH_LONG).show();
					Intent intent = new Intent(getApplicationContext(), OpenLoginActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("result", Activity.RESULT_CANCELED);
					intent.putExtra("wxresp", bundle);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					finish();
					startActivity(intent);
			}
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			if (resp instanceof SendAuth.Resp) {
				Toast.makeText(this, R.string.userrefuse, Toast.LENGTH_LONG).show();
					Intent intent = new Intent(getApplicationContext(), OpenLoginActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("result", Activity.RESULT_CANCELED);
					intent.putExtra("wxresp", bundle);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					finish();
					startActivity(intent);
			}
			break;
		default:
			break;
		}
		finish();
	}

	@Override
	protected void onHandleMessage(Message msg) {
		// TODO Auto-generated method stub
		
	}

}
