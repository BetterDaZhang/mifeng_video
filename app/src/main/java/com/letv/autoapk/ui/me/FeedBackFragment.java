package com.letv.autoapk.ui.me;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.fragment.BaseTitleFragment.TitleLeftClickListener;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.context.MyApplication;

import android.content.Context;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class FeedBackFragment extends BaseTitleFragment {

	@Override
	protected void onHandleMessage(Message msg) {
		// TODO Auto-generated method stub

	}

	@Override
	protected View createContentView() {
		return setupDataView();
	}

	@Override
	protected boolean loadingData() {
		// TODO Auto-generated method stub
		return true;
	}

	private EditText et_feedback;
	private TextView tv_num;

	@Override
	protected View setupDataView() {
		setStatusBarColor(getResources().getColor(R.color.code04));
		setTitle(getString(R.string.mine_feedback), getResources().getColor(R.color.code6));
		setTitleLeftResource(R.drawable.mine_login_back, mActivity.dip2px(16));

		View root = View.inflate(mActivity, R.layout.mine_feedback, null);
		et_feedback = (EditText) root.findViewById(R.id.et_feedback);
		tv_num = (TextView) root.findViewById(R.id.tv_num);
		root.findViewById(R.id.tv_submit).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String feedback = et_feedback.getText().toString().trim();
				if (TextUtils.isEmpty(feedback)) {
					mActivity.showToastSafe(getString(R.string.input_content), 1);
					return;
				}
				hideSoftKeybord();
				dosubmit(feedback);
			}
		});
		et_feedback.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				tv_num.setText(String.valueOf(s.length()) + "/300");
			}
		});
		setLeftClickListener(new TitleLeftClickListener() {
			@Override
			public void onLeftClickListener() {
				hideSoftKeybord();
				getActivity().finish();
			}
		});
		return root;
	}

	protected void dosubmit(final String feedback) {
		new UiAsyncTask<Integer>(this) {

			@Override
			protected void post(Integer result) {
				if (0 == result) {
					et_feedback.setText("");
					mActivity.showToastSafe(getString(R.string.qqq), 0);
					getActivity().finish();
				} else if (result != 1) {
					mActivity.showToastSafe(getString(R.string.comit_failed), 0);
				}
			}

			@Override
			protected Integer doBackground() {
				FeedbackDataRequest request = new FeedbackDataRequest(mActivity);
				Map<String, String> mInputParam = new HashMap<String, String>();
				mInputParam.put("content", feedback);
				mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
				mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
				mInputParam.put("contactInfo", "");// 手机号或者qq号或者E-mail怎么获取
				int code = request.setInputParam(mInputParam).request(Request.Method.POST);
				return code;
			}
		}.showDialog().execute();
	}

	public void hideSoftKeybord() {
		InputMethodManager inputMethodManager = (InputMethodManager) mActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean bool = inputMethodManager.showSoftInput(et_feedback, InputMethodManager.SHOW_FORCED);
		inputMethodManager.hideSoftInputFromWindow(et_feedback.getApplicationWindowToken(), 0);
	}

}
