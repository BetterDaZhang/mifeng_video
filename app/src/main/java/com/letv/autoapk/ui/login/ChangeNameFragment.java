package com.letv.autoapk.ui.login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.LoginInfo;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.utils.CharUtil;
import com.letv.autoapk.utils.SerializeableUtil;
import com.letv.autoapk.utils.SystemUtls;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ChangeNameFragment extends BaseTitleFragment implements OnClickListener {
	protected static final String TAG = "NewPasswordFragment";
	private String selectSexInt;
	private Long birth;
	private EditText et_nickName;
	private String newName;
	private String lastName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		selectSexInt = getActivity().getIntent().getStringExtra("sex");
		birth = getActivity().getIntent().getLongExtra("birthday", 0l);
		lastName = getActivity().getIntent().getStringExtra("lastName");
	}

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	protected boolean loadingData() {
		return true;
	}

	@Override
	protected View createContentView() {
		// TODO Auto-generated method stub
		return setupDataView();
	}

	@Override
	protected View setupDataView() {
		setStatusBarColor(getResources().getColor(R.color.code04));
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		View view = inflater.inflate(R.layout.mine_login_newname, null);
		setTitleLeftResource(R.drawable.base_head_back, mActivity.dip2px(16));
		setTitle(getString(R.string.modify_name), getResources().getColor(R.color.code6));
		et_nickName = (EditText) view.findViewById(R.id.et_nickname);
		et_nickName.setText(lastName);
		et_nickName.requestFocus();
		view.findViewById(R.id.resetpwd_complete).setOnClickListener(this);
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
		SystemUtls.hideInputMethod(mActivity, v);
		newName = et_nickName.getText().toString().trim();
		if (TextUtils.isEmpty(newName)) {
//			getActivity().finish();
			mActivity.showToastSafe(getString(R.string.namecannotempty), 0);
			return;
		}
		if (new CharUtil().isValid(newName)==0 || new CharUtil().isValid(newName)==1) {
			mActivity.showToastSafe(getString(R.string.mine_nickname_illegal_toast), 0);
			return;
		} 
		hideSoftKeybord();
		upLoadUserInfo();
	}

	/** 上传个人资料 */
	private void upLoadUserInfo() {
		final String newName = et_nickName.getText().toString().trim();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			JSONObject contentObject = new JSONObject();
			contentObject.put("editType", 0);
			contentObject.put("editValue", selectSexInt);
			JSONObject contentObject2 = new JSONObject();
			contentObject2.put("editType", 1);
			contentObject2.put("editValue", newName);
			JSONObject contentObject3 = new JSONObject();
			contentObject3.put("editType", 2);
			contentObject3.put("editValue", birth);
			jsonArray.put(contentObject);
			jsonArray.put(contentObject2);
			jsonArray.put(contentObject3);

			jsonObject.put("editInfoList", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestParams params = new RequestParams(StringDataRequest.MAIN_URL + "/editUserInfo");
		LoginInfo loginInfo = LoginInfoUtil.getLoginInfo(mActivity);
		if (null != loginInfo) {
			params.addHeader("authtoken", loginInfo.getToken());
		}
		params.addBodyParameter(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
		params.addBodyParameter(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		params.addBodyParameter("editInfoList", jsonObject.toString());
		x.http().post(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				LoginInfo loginInfo = LoginInfoUtil.getLoginInfo(mActivity);
				loginInfo.setGender(Integer.valueOf(selectSexInt));
				loginInfo.setNickName(newName);
				loginInfo.setBirthday(birth);
				SerializeableUtil.saveObject(mActivity, MyApplication.USER_INFO, loginInfo);
				if (getActivity() != null) {
					getActivity().finish();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				mActivity.showToastSafe(getString(R.string.modifyname_failed), 0);

			}

			@Override
			public void onCancelled(CancelledException cex) {
				mActivity.showToastSafe(getString(R.string.modifyname_failed), 0);
			}

			@Override
			public void onFinished() {

			}
		});
	}
	
	public void hideSoftKeybord() {
		InputMethodManager inputMethodManager = (InputMethodManager) mActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean bool = inputMethodManager.showSoftInput(et_nickName, InputMethodManager.SHOW_FORCED);
		inputMethodManager.hideSoftInputFromWindow(et_nickName.getApplicationWindowToken(), 0);
	}

}
