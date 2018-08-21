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
import com.letv.autoapk.utils.SystemUtls;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ForgetPasswordFragment extends BaseTitleFragment implements
		OnClickListener {
	protected static final String TAG = "ForgetPasswordFragment";
	private int time = 60;
	private ImageView iv_check_code;
	private EditText et_phone_number;
	private EditText et_check_code;
	private TextView tv_left_time;
	private String phoneNumber;
	private static final int SEND = 0;

	@Override
	protected void onHandleMessage(Message msg) {
		if (msg.what == SEND) {
			--time;
			if (time >= 0) {
				iv_check_code.setImageResource(R.drawable.mine_codecheck_gray);
				iv_check_code.setClickable(false);
				tv_left_time.setVisibility(View.VISIBLE);
				tv_left_time.setTextColor(getResources()
						.getColor(R.color.code4));
				tv_left_time.setText(time
						+ getResources().getString(R.string.second));
				getDefaultHandler().sendEmptyMessageDelayed(SEND, 1000);
				return;
			}
			iv_check_code.setImageResource(R.drawable.mine_btn_yzm);
			iv_check_code.setClickable(true);
			tv_left_time.setTextColor(getResources().getColor(R.color.code6));
			tv_left_time.setText(getResources().getString(
					R.string.register_checkcode));
			time = 60;
		}

	}

	@Override
	protected boolean loadingData() {
		return true;
	}

	@Override
	public void onDestroyView() {
		getDefaultHandler().removeMessages(SEND);
		super.onDestroyView();
	}

	@Override
	protected View setupDataView() {
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		View view = inflater.inflate(R.layout.mine_login_forgetpwd, null);
		setTitleLeftResource(R.drawable.mine_login_back, mActivity.dip2px(16));
		setLeftClickListener(new TitleLeftClickListener() {

			@Override
			public void onLeftClickListener() {
				getActivity().finish();
			}
		});
		setTitle(getResources().getString(R.string.mine_retrieve_password),
				getResources().getColor(R.color.code6));
		et_phone_number = (EditText) view.findViewById(R.id.et_phone_number);
		et_check_code = (EditText) view.findViewById(R.id.et_check_code);
		iv_check_code = (ImageView) view.findViewById(R.id.iv_check_code);
		tv_left_time = (TextView) view.findViewById(R.id.tv_left_time);
		view.findViewById(R.id.iv_next).setOnClickListener(this);
		iv_check_code.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_check_code:
			phoneNumber = et_phone_number.getText().toString().trim();
			if (TextUtils.isEmpty(phoneNumber)
					|| !phoneNumber
							.matches("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$")) {
				// et_phone_number.startAnimation(shake);
				mActivity.showToastSafe(getString(R.string.phonenum_error), 0);
				return;
			}
			// 获取验证码
			SystemUtls.hideInputMethod(mActivity, v);
			getCheckCode();
			break;
		case R.id.iv_next:
			SystemUtls.hideInputMethod(mActivity, v);
			phoneNumber = et_phone_number.getText().toString().trim();
			final String checkCode = et_check_code.getText().toString().trim();
			if (TextUtils.isEmpty(phoneNumber)
					|| !phoneNumber
							.matches("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$")) {
				mActivity.showToastSafe(getString(R.string.phonenum_error), 0);
				return;
			} else if (TextUtils.isEmpty(checkCode)) {
				mActivity.showToastSafe(getString(R.string.codecannotempty), 0);
				return;
			} else {
				new UiAsyncTask<Integer>(this) {

					@Override
					protected void post(Integer result) {
						if (result == 0) {
							// 跳转到下一步
							FragmentTransaction transation = getActivity()
									.getSupportFragmentManager()
									.beginTransaction();
							Fragment newPasswordFragment = Fragment
									.instantiate(getActivity(),
											NewPasswordFragment.class.getName());
							Bundle bundle = new Bundle();
							bundle.putString("phoneNumber", phoneNumber);
							bundle.putString("checkCode", checkCode);
							newPasswordFragment.setArguments(bundle);
							transation.replace(R.id.container,
									newPasswordFragment, "newPasswordFragment");
							transation.addToBackStack(null);
							transation.commit();
						}
					}

					@Override
					protected Integer doBackground() {
						CheckCodeDataRequest request = new CheckCodeDataRequest(
								mActivity);
						Map<String, String> mInputParam = new HashMap<String, String>();
						mInputParam.put("telephone", phoneNumber);
						mInputParam.put(StringDataRequest.TENANT_ID,
								MyApplication.getInstance().getTenantId());
						mInputParam.put(StringDataRequest.USER_ID,
								LoginInfoUtil.getUserId(mActivity));
						mInputParam.put("countryCode",
								SystemUtls.getContryCode(getActivity()));
						mInputParam.put("msgType", "2");
						mInputParam.put("vcode", checkCode);
						int code = request.setInputParam(mInputParam).request(
								Request.Method.GET);
						return code;
					}

				}.showDialog().execute();
			}

			break;
		default:
			break;
		}

	}

	private void getCheckCode() {
		final String phoneNumber = et_phone_number.getText().toString().trim();
		new UiAsyncTask<Integer>(this) {

			@Override
			protected void post(Integer result) {
				if (result == 0) {// 表示请求成功，那么图片应该变成灰色
					getDefaultHandler().sendEmptyMessage(SEND);
				}
			}

			@Override
			protected Integer doBackground() {
				CheckCodeResetPwdDataRequest request = new CheckCodeResetPwdDataRequest(
						mActivity);
				Map<String, String> mInputParam = new HashMap<String, String>();
				mInputParam.put("phoneNumber", phoneNumber);
				mInputParam.put(StringDataRequest.TENANT_ID, MyApplication
						.getInstance().getTenantId());
				mInputParam.put(StringDataRequest.USER_ID,
						LoginInfoUtil.getUserId(mActivity));
				int code = request.setInputParam(mInputParam).request(
						Request.Method.GET);
				return code;
			}

		}.showDialog().execute();
	}
}
