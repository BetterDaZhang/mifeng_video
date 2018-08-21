package com.letv.autoapk.ui.login;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.utils.DESUtils;
import com.letv.autoapk.utils.SystemUtls;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class NewPasswordFragment extends BaseTitleFragment implements OnClickListener {
	protected static final String TAG = "NewPasswordFragment";
	private EditText et_password;
	private String phoneNumber;
	private String checkCode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		phoneNumber = getArguments().getString("phoneNumber");
		checkCode = getArguments().getString("checkCode");
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
		View view = inflater.inflate(R.layout.mine_login_newpwd, null);
		setTitleLeftResource(R.drawable.mine_login_back, mActivity.dip2px(16));
		setLeftClickListener(new TitleLeftClickListener() {

			@Override
			public void onLeftClickListener() {
				getFragmentManager().popBackStack();
			}
		});
		setTitle(getString(R.string.newpassword_title), getResources().getColor(R.color.code6));
		et_password = (EditText) view.findViewById(R.id.et_password);
		view.findViewById(R.id.resetpwd_complete).setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.resetpwd_complete) {
			final String password = et_password.getText().toString().trim();
			if (TextUtils.isEmpty(password) || !LoginInfoUtil.isPasswordValid(password)) {
				mActivity.showToastSafe(getResources().getString(R.string.mine_password_error), 0);
				return;
			}
			SystemUtls.hideInputMethod(mActivity, v);
			setNewPassword(password);
		}

	}

	private void setNewPassword(final String password) {
		// 设置完成密码
		new UiAsyncTask<Integer>(this) {

			@Override
			protected void post(Integer result) {
				if (result == 0) {// 表示请求成功，跳转到登录界面
					mActivity.showToastSafe(getResources().getString(R.string.mine_retrieve_pwd_success),
							Toast.LENGTH_SHORT);
					if (getActivity() != null) {
						getActivity().finish();
					}
				}
			}

			@Override
			protected Integer doBackground() {
				ResetPwdDataRequest request = new ResetPwdDataRequest(mActivity);
				Map<String, String> mInputParam = new HashMap<String, String>();
				mInputParam.put("mobile", phoneNumber);
				mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
				mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
				mInputParam.put("checkcode", checkCode);
				mInputParam.put("userPassword", DESUtils.encryptBasedDes(password));
				mInputParam.put("s", "1");
				int code = request.setInputParam(mInputParam).request(Request.Method.GET);
				return code;
			}

		}.showDialog().execute();

	}

}
