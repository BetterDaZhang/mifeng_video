package com.letv.autoapk.ui.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.Request;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.base.activity.DetailActivity;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.LoginInfo;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.boss.VipInfo;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.utils.DESUtils;
import com.letv.autoapk.utils.SerializeableUtil;
import com.letv.autoapk.utils.SystemUtls;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RegisterFragment extends BaseTitleFragment implements OnClickListener {
    protected static final String TAG = "RegFragment";
    private int time = 60;
    private ImageView iv_check_code;
    private EditText et_phone_number;
    private EditText et_password;
    private EditText et_check_code;
    private TextView tv_additional;
    private TextView tv_left_time;
    private String phoneNumber;
    private static final int SEND = 0;
    public static final int FLAG_REGISTER = 0;
    private CheckBox cbReg;
    private List<VipInfo> vipInfos = new ArrayList<VipInfo>();

    @Override
    protected void onHandleMessage(Message msg) {
        // TODO Auto-generated method stub
        if (msg.what == SEND) {
            --time;
            if (time >= 0) {
                iv_check_code.setImageResource(R.drawable.mine_codecheck_gray);
                iv_check_code.setClickable(false);
                tv_left_time.setVisibility(View.VISIBLE);
                tv_left_time.setTextColor(getResources().getColor(R.color.code4));
                tv_left_time.setText(time + getResources().getString(R.string.second));
                getDefaultHandler().sendEmptyMessageDelayed(SEND, 1000);
                return;
            }
            iv_check_code.setImageResource(R.drawable.mine_btn_yzm);
            iv_check_code.setClickable(true);
            tv_left_time.setTextColor(getResources().getColor(R.color.code6));
            tv_left_time.setText(getResources().getString(R.string.register_checkcode));
            time = 60;
        }
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

    protected void initCustomerView() {
        setStatusBarColor(getResources().getColor(R.color.code04));
    }

    @Override
    protected View setupDataView() {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view = inflater.inflate(R.layout.mine_register, null);
        setTitleLeftResource(R.drawable.mine_login_back, mActivity.dip2px(16));
        setLeftClickListener(new TitleLeftClickListener() {

            @Override
            public void onLeftClickListener() {
                int count = getActivity().getSupportFragmentManager().getBackStackEntryCount();
                if (count > 0) {
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    getActivity().finish();
                }
            }
        });
        setTitle(getResources().getString(R.string.register), getResources().getColor(R.color.code6));
        et_password = (EditText) view.findViewById(R.id.et_password);
        et_phone_number = (EditText) view.findViewById(R.id.et_phone_number);
        et_check_code = (EditText) view.findViewById(R.id.et_check_code);
        cbReg = (CheckBox) view.findViewById(R.id.reg_checkbox);
        iv_check_code = (ImageView) view.findViewById(R.id.iv_check_code);
        tv_left_time = (TextView) view.findViewById(R.id.tv_left_time);
        tv_additional = (TextView) view.findViewById(R.id.tv_additional);
        tv_additional.setOnClickListener(this);
        iv_check_code.setOnClickListener(this);
        view.findViewById(R.id.iv_zhuce).setOnClickListener(this);
        cbReg.setChecked(true);// 默认已阅读协议状态
        return view;
    }

    @Override
    public void onDestroyView() {
        getDefaultHandler().removeMessages(SEND);
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.iv_check_code:
            phoneNumber = et_phone_number.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber) || !phoneNumber.matches("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$")) {
                mActivity.showToastSafe(getString(R.string.phonenum_error), 0);
                return;
            }
            // 获取验证码
            SystemUtls.hideInputMethod(mActivity, v);
            getCheckCode();
            break;
        case R.id.reg_checkbox:
            // 默认为未勾选
            boolean isRead = cbReg.isChecked();
            if (isRead) {
                cbReg.setChecked(false);
            } else {
                cbReg.setChecked(true);
            }
            break;
        case R.id.tv_additional:
            // 开启服务协议页面
            Intent intent;
            if (mActivity instanceof DetailActivity) {
                intent = new Intent(mActivity, ContainerActivity.class);
                intent.putExtra(ContainerActivity.FRAGMENTNAME, PrivacyFragment.class.getName());
            } else {
                intent = new Intent(mActivity, DetailActivity.class);
                intent.putExtra(DetailActivity.FRAGMENTNAME, PrivacyFragment.class.getName());
            }
            intent.putExtra("web_url", "file:///android_asset/privacyTerms.htm");
            startActivity(intent);
            break;
        case R.id.iv_zhuce:
            SystemUtls.hideInputMethod(mActivity, v);
            phoneNumber = et_phone_number.getText().toString().trim();
            final String password = et_password.getText().toString().trim();
            final String checkCode = et_check_code.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber) || !phoneNumber.matches("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$")) {
                mActivity.showToastSafe(getString(R.string.phonenum_error), 0);
                return;
            } else if (TextUtils.isEmpty(password) || !LoginInfoUtil.isPasswordValid(password)) {
                mActivity.showToastSafe(getResources().getString(R.string.mine_password_error), 0);
                return;
            } else if (TextUtils.isEmpty(checkCode)) {
                mActivity.showToastSafe(getString(R.string.codecannotempty), 0);
                return;
            } else if (!cbReg.isChecked()) {
                Builder builder = new Builder(mActivity).setTitle(getString(R.string.dialog_title)).setMessage(getString(R.string.argeeprivacy));
                builder.setNegativeButton(getString(R.string.login_cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                builder.setPositiveButton(getString(R.string.login_ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cbReg.setChecked(true);
                        zhuce(phoneNumber, password, checkCode);
                    }
                });
                builder.show();
            } else {
                zhuce(phoneNumber, password, checkCode);
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
                // 失败stringdatarequest统一提示
                if (result == 0) {// 表示请求成功，那么图片应该变成灰色
                    getDefaultHandler().sendEmptyMessage(SEND);
                    mActivity.showToastSafe(getString(R.string.requestcodeok), 0);
                }
            }

            @Override
            protected Integer doBackground() {
                CheckCodeRegisterDataRequest request = new CheckCodeRegisterDataRequest(mActivity);
                Map<String, String> mInputParam = new HashMap<String, String>();
                mInputParam.put("phoneNumber", phoneNumber);
                mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
                mInputParam.put("country_code", SystemUtls.getContryCode(getActivity()));
                // mInputParam.put("captcha","1");
                // mInputParam.put("app_key","1");
                int code = request.setInputParam(mInputParam).request(Request.Method.GET);
                return code;
            }
        }.showDialog().execute();
    }

    public void zhuce(final String phoneNumber, final String password, final String checkCode) {
        if (!SystemUtls.isNetworkConnected(mActivity)) {
            mActivity.showToastSafe(getString(R.string.base_networkerror), 1);
            return;
        }
        new UiAsyncTask<Integer>(this) {
            List<LoginInfo> infos = new ArrayList<LoginInfo>();

            @Override
            protected void post(Integer result) {
                if (result != null && result == 0) {
                    userLogin(phoneNumber, password, mActivity, infos, 0);
                    MyApplication.getInstance().putBoolean(MyApplication.ISLOGIN, true);
                    // 表示登录成功,将用户登录信息序列化
                    if (infos.size() > 0) {
                        saveLoginInfo(mActivity, infos.get(0));
                    }
                }
            }

            @Override
            protected Integer doBackground() {

                RegisterDataRequest request = new RegisterDataRequest(mActivity);
                Map<String, String> mInputParam = new HashMap<String, String>();
                mInputParam.put("mobile", phoneNumber);// 手机号
                mInputParam.put("password", DESUtils.encryptBasedDes(password));
                mInputParam.put("s", "1");
                mInputParam.put("checkCode", checkCode);
                mInputParam.put("countryCode", SystemUtls.getContryCode(getActivity()));
                mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
                mInputParam.put("version", "2");
                mInputParam.put("username", phoneNumber);
                // request.setCacheListener(cacheListener);
                int code = request.setInputParam(mInputParam).setOutputData(infos).request(Request.Method.GET);
                return code;
            }
        }.showDialog().execute();

    }

    /**
     * 用户登录
     * 
     * @param username
     * @param password
     * @param login_loading
     *            登录时显示的进度条
     * @param context
     *            ActionBarBaseActivity对象,否则,进度条将不能正确隐藏
     * @param infos
     *            请求数据成功后返回的登录信息数据
     */
    public void userLogin(final String username, final String password, final BaseActivity context, final List<LoginInfo> infos, final int flag) {
        if (!SystemUtls.isNetworkConnected(context)) {
            MyApplication.getInstance().putBoolean(MyApplication.ISLOGIN, false);
            return;
        }
        new UiAsyncTask<Integer>(this) {

            @Override
            protected void post(Integer result) {
                if (result == 0) {
                    MyApplication.getInstance().putBoolean(MyApplication.ISLOGIN, true);
                    // 表示登录成功,将用户登录信息序列化
                    saveLoginInfo(context, infos.get(0));
                    Intent intent = new Intent(mActivity.getApplicationContext(), DetailActivity.class);
                    intent.putExtra(DetailActivity.FRAGMENTNAME, FillDataFragment.class.getName());
                    MyApplication.getInstance().putBoolean(MyApplication.IS_LOGIN_NORMAL, true);
                    getActivity().finish();
                    startActivity(intent);
                } else {
                }
            }

            @Override
            protected Integer doBackground() {
                LoginDataRequest request = new LoginDataRequest(context);
                Map<String, String> mInputParam = new HashMap<String, String>();
                mInputParam.put("username", username);// 手机号
                mInputParam.put("userPassword", DESUtils.encryptBasedDes(password));
                mInputParam.put("name_type", "2");
                mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
                mInputParam.put("version", "2");
                mInputParam.put("s", "1");
                mInputParam.put("isaddboss", MyApplication.getInstance().isNeedBoss()+"");
                int code = request.setInputParam(mInputParam).setOutputData(infos, vipInfos).request(Request.Method.POST);
                return code;
            }

        }.showDialog().execute();
    }

    public void saveLoginInfo(Context context, LoginInfo loginInfo) {
        SerializeableUtil.saveObject(context, MyApplication.USER_INFO, loginInfo);
        MyApplication.getInstance().putInfo(MyApplication.VIPINFO, vipInfos);
    }

}
