package com.letv.autoapk.ui.me;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.LoginInfo;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.utils.DESUtils;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ChangePasswordFragment extends BaseTitleFragment implements OnClickListener {
	private EditText et_oldpassword;
	private EditText et_newpassword;
	private EditText et_confirm;
	private LoginInfo loginInfo;

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	protected boolean loadingData() {
		return true;
	}

	@Override
	protected View createContentView() {
		return setupDataView();
	}

	@Override
	protected View setupDataView() {
		setStatusBarColor(getResources().getColor(R.color.code04));
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		loginInfo = LoginInfoUtil.getLoginInfo(mActivity);
		View view = inflater.inflate(R.layout.mine_login_changepwd, null);
		setTitleLeftResource(R.drawable.mine_login_back, mActivity.dip2px(16));
		setTitle(getString(R.string.mine_change_password), getResources().getColor(R.color.code6));
		et_oldpassword = (EditText) view.findViewById(R.id.et_oldpassword);
		et_newpassword = (EditText) view.findViewById(R.id.et_newpassword);
		et_confirm = (EditText) view.findViewById(R.id.et_confirm);
		view.findViewById(R.id.iv_next).setOnClickListener(this);
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
		if (v.getId() == R.id.iv_next) {
			final String confirm = et_confirm.getText().toString().trim();
			final String newPassword = et_newpassword.getText().toString().trim();
			final String oldPassword = et_oldpassword.getText().toString().trim();
			if (TextUtils.isEmpty(confirm) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(oldPassword)) {
				mActivity.showToastSafe(getString(R.string.password_notempty), 1);
				return;
			}
			if (newPassword.equals(confirm) == false) {
				mActivity.showToastSafe(getString(R.string.confirm_error), 1);
				return;
			}
			hideSoftKeybord();
			new UiAsyncTask<Integer>(this) {

				@Override
				protected void post(Integer result) {
					if (result == 0) {
						mActivity.showToastSafe(getString(R.string.resetpass_ok), 0);
						getActivity().finish();
					} else if (result != 1) {
						mActivity.showToastSafe(getString(R.string.resetpass_failed), 0);
					}
				}

				@Override
				protected Integer doBackground() {
					ChangePasswordDataRequest request = new ChangePasswordDataRequest(mActivity);
					Map<String, String> mInputParam = new HashMap<String, String>();
					mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
					mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
					mInputParam.put("oldPassword", DESUtils.encryptBasedDes(oldPassword));
					mInputParam.put("newPassword", DESUtils.encryptBasedDes(newPassword));
					mInputParam.put("s", "1");
					mInputParam.put("app_key", "app_key");
					if (loginInfo != null) {
						mInputParam.put("username", loginInfo.getNickName());
					}
					int code = request.setInputParam(mInputParam).request(Request.Method.GET);
					return code;
				}
			}.showDialog().execute();
		}

	}

	public void hideSoftKeybord() {
		InputMethodManager inputMethodManager = (InputMethodManager) mActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean bool = inputMethodManager.showSoftInput(et_oldpassword, InputMethodManager.SHOW_FORCED);
		inputMethodManager.hideSoftInputFromWindow(et_oldpassword.getApplicationWindowToken(), 0);
	}

}
