package com.letv.autoapk.boss;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.android.volley.Request;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.PlayVideoActivity;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.ui.login.LoginAPI;

/**
 * 兑换码
 * 
 * @author wangzhen5
 * 
 */
public class CdkeyFragment extends BaseTitleFragment implements OnClickListener {
	protected static final String TAG = "CdkeyFragment";
	private EditText et_cdkey;
	private String cdkey;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	protected boolean loadingData() {
		return true;
	}

	@Override
	protected View setupDataView() {
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		View view = inflater.inflate(R.layout.mine_cdkey, null);
		setTitleLeftResource(R.drawable.base_head_back, mActivity.dip2px(15));
		setTitle(getString(R.string.title_cdkey), getResources().getColor(R.color.code6));
		et_cdkey = (EditText) view.findViewById(R.id.et_cdkey);
		et_cdkey.requestFocus();
		view.findViewById(R.id.cdkey_active).setOnClickListener(this);
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
	public void onClick(View v) {
		cdkey = et_cdkey.getText().toString().trim();
		if (TextUtils.isEmpty(cdkey)) {
			mActivity.showToastSafe(getString(R.string.cdkey_notnull), 0);
			return;
		}
		hideSoftKeybord();
		activeCdkey(cdkey);
	}

	/**
	 * cdkey激活
	 */
	private void activeCdkey(final String cdkey) {
		new UiAsyncTask<Integer>(this) {
			@Override
			protected Integer doBackground() throws Throwable {

				CdkeyDataRequest request = new CdkeyDataRequest(mActivity);
				Map<String, String> mInputParam = new HashMap<String, String>();
				mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
				mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
				mInputParam.put("cardId", cdkey);
				int code = request.setInputParam(mInputParam).request(Request.Method.GET);
				if (code == 0) {
					mActivity.showToastSafe(getString(R.string.cdkey_ok), 1);
					LoginInfoUtil.setIsVip(0, mActivity);
					// 若兑换码激活成功后，入口是播放器页面，则鉴权成功，否则不能改鉴权
					int flag = MyApplication.getInstance().getInt(LepayManager.PAY_FOR_WHAT);
					switch (flag) {
					case LepayManager.PAY_FOR_PLAY:
						MyApplication.getInstance().setAuthSuccess(true);
						break;
					case LepayManager.PAY_NORMAL:
					case LepayManager.PAY_FOR_FINISH:
						MyApplication.getInstance().setAuthSuccess(false);
						break;
					case LepayManager.PAY_AUTH_FINISH:
						int auth = MyApplication.getInstance().getBossManager().auth();
						if (auth != 0) {
							MyApplication.getInstance().setAuthSuccess(false);
						} else {
							MyApplication.getInstance().setAuthSuccess(true);
						}
						break;
					}
					if (MyApplication.getInstance().getLepaySuccessListener() != null) {
						MyApplication.getInstance().getLepaySuccessListener().paymentSuccess();
					}
					mActivity.finish();
				} else {
					mActivity.showToastSafe(getString(R.string.cdkey_faild), 1);
				}
				return null;
			}

		}.execute();
	}

	public void hideSoftKeybord() {
		InputMethodManager inputMethodManager = (InputMethodManager) mActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean bool = inputMethodManager.showSoftInput(et_cdkey, InputMethodManager.SHOW_FORCED);
		inputMethodManager.hideSoftInputFromWindow(et_cdkey.getApplicationWindowToken(), 0);
	}

}
