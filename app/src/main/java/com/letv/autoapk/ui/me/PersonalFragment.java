package com.letv.autoapk.ui.me;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import android.R.integer;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.DetailActivity;
import com.letv.autoapk.base.dialog.BaseDialog;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.LoginInfo;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.boss.BossManager;
import com.letv.autoapk.boss.ConsumeRecordsFragment;
import com.letv.autoapk.boss.LePaySuccessListener;
import com.letv.autoapk.boss.LepayManager;
import com.letv.autoapk.boss.VipInfo;
import com.letv.autoapk.common.net.LruImageCache;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.ui.login.LoginAPI;
import com.letv.autoapk.ui.me.DateDialog.OnSaveListener;
import com.letv.autoapk.utils.ImageUtils;
import com.letv.autoapk.utils.SerializeableUtil;
import com.letv.autoapk.utils.SystemUtls;
import com.summerxia.dateselector.widget.DateSelectorWheelView;
import com.summerxia.dateselector.widget.DateTimeSelectorDialogBuilder;

public class PersonalFragment extends BaseTitleFragment
		implements OnClickListener, OnSaveListener, LePaySuccessListener {
	/** 拍照 */
	private static final int PHOTOHRAPH = 3;
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private static final int NONE = 0;
	private static final int PHOTORESULT = 2;
	/** 从图库选择,KITKAT以上 */
	private static final int SELECT_PIC_KITKAT = 4;
	private static final int SELECT_PIC = 5;
	private TextView tv_name;// 显示姓名的tv
	private TextView tv_select_sex;// 显示性别的tv
	private TextView tv_select_birthday;
	private ImageView civ_headpic;// 圆形头像
	private Dialog dialog;
	private LoginInfo loginInfo;// 从application中获取到的用户数据
	private List<LoginInfo> infos;
	private List<VipInfo> vipInfos;
	private SimpleDateFormat df;
	private String birthday;
	private Uri imgUri;// 图片uri
	private Uri tempUri;
	private Long birth;// 生日的时间戳
	private File uploadFile;// 要上传的图片文件
	private String lastName;// 修改昵称，返回数据的保存
	private String selectSex;// 修改性别，返回数据的保存
	private String selectBirthday;// 修改生日，返回数据的保存
	private String selectSexInt;// 性别，数字表示
	private TextView dialog_cancel;
	private TextView btn_male;
	// private TextView btn_none;
	private TextView btn_female;
	private TextView dialog_ensure;
	private View root;
	private boolean sameNickname = true;
	private LinearLayout ll_vip_desc;
	private TextView tv_vip_endtime;
	private TextView tv_renew;
	private TextView tv_get_vip;
	private View rl_vip_desc;

	@Override
	protected boolean loadingData() {
		infos = new ArrayList<LoginInfo>();
		vipInfos = new ArrayList<VipInfo>();
		UserInfoDataRequest request = new UserInfoDataRequest(mActivity);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputParam.put("isaddboss", MyApplication.getInstance().isNeedBoss() + "");
		int code = request.setInputParam(mInputParam).setOutputData(infos, vipInfos).request(Request.Method.GET);// infos.get(0)就是用户登录信息
		if (code == 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void initCustomerView() {
		setTitle(getActivity().getString(R.string.mine_modify_date), getResources().getColor(R.color.code6));
		setTitleLeftResource(R.drawable.mine_login_back, mActivity.dip2px(16));
		setStatusBarColor(getResources().getColor(R.color.code04));
		setLeftClickListener(new TitleLeftClickListener() {
			@Override
			public void onLeftClickListener() {
				if (loginInfo == null || tv_name == null) {
					getActivity().finish();
					return;
				}
				sameNickname = loginInfo.getNickName().equals(tv_name.getText().toString().trim());
				if (!sameNickname) {
					loginInfo.setNickName(tv_name.getText().toString().trim());
					upLoadUserInfo();
				} else {
					getActivity().finish();
				}
			}
		});
	}

	@Override
	protected View setupDataView() {
		df = new SimpleDateFormat(getActivity().getString(R.string.mine_date_form));
		root = View.inflate(mActivity, R.layout.mine_personal, null);
		// 寻找控件
		tv_name = (TextView) root.findViewById(R.id.tv_name);
		tv_select_sex = (TextView) root.findViewById(R.id.tv_sex);
		tv_select_birthday = (TextView) root.findViewById(R.id.tv_birthday);
		civ_headpic = (ImageView) root.findViewById(R.id.civ_headpic);
		ll_vip_desc = (LinearLayout) root.findViewById(R.id.ll_vip_desc);
		tv_get_vip = (TextView) root.findViewById(R.id.tv_get_vip);

		root.findViewById(R.id.ll_name).setOnClickListener(this);
		root.findViewById(R.id.ll_sex).setOnClickListener(this);
		root.findViewById(R.id.ll_birthday).setOnClickListener(this);
		root.findViewById(R.id.ll_changepassword).setOnClickListener(this);
		root.findViewById(R.id.ll_pay_record).setOnClickListener(this);
		root.findViewById(R.id.ctv_headpic).setOnClickListener(this);
		root.findViewById(R.id.ll_headpic).setOnClickListener(this);
		root.findViewById(R.id.iv_mine_message).setOnClickListener(this);

		civ_headpic.setOnClickListener(this);
		tv_name.setOnClickListener(this);
		tv_get_vip.setOnClickListener(this);
		if (infos.isEmpty()) {
			mActivity.showToastSafe(R.string.mine_info_abnormal, 0);
			return root;
		}
		loginInfo = infos.get(0);
		saveUserInfo();
		boolean isLoginNormal = MyApplication.getInstance().getBoolean(MyApplication.IS_LOGIN_NORMAL);
		if (isLoginNormal) {
			root.findViewById(R.id.ll_changepassword).setVisibility(View.VISIBLE);
		} else {
			root.findViewById(R.id.ll_changepassword).setVisibility(View.GONE);
		}
		bossUiSetting();
		loadUserInfo();
		setVipPaySuccessListener();
		return root;
	}
	
	private void setVipPaySuccessListener() {
		MyApplication.getInstance().setLePaySuccessListener(this);
	}

	private void bossUiSetting() {
		if (MyApplication.getInstance().isNeedBoss() == 0) {
			root.findViewById(R.id.ll_pay_record).setVisibility(View.GONE);
		}
	}

	private void loadUserInfo() {
		if (null != loginInfo) {
			String headImg = LoginInfoUtil.getUserInfoPhoto(mActivity);
			if (!TextUtils.isEmpty(headImg)) {
				ImageListener listener = ImageLoader.getImageListener(civ_headpic, R.drawable.mine_default_head_img,
						R.drawable.mine_default_head_img);
				LruImageCache.getImageLoader(mActivity.getApplicationContext()).get(headImg, listener);
			}
			tv_name.setText(
					loginInfo.getNickName() == null ? getString(R.string.mine_nickname) : loginInfo.getNickName());
			int gender = loginInfo.getGender();
			String male = getString(R.string.mine_male);
			String female = getString(R.string.mine_female);
			String secretSex = getString(R.string.mine_secret_sex);
			tv_select_sex.setText(gender == 0 ? male : (gender == 1 ? female : secretSex));

			if (loginInfo.getBirthday() != 0) {
				birthday = df.format(new Date(loginInfo.getBirthday() * 1000));
			} else {
				birthday = "";// 如果服务器返回数据为1970
			}
			tv_select_birthday.setText(birthday);
			if (vipInfos.size() > 0) {
				VipInfo vipInfo;
				for (int i = 0; i < vipInfos.size(); i++) {
					vipInfo = vipInfos.get(i);
					rl_vip_desc = View.inflate(mActivity, R.layout.mine_personal_vip_desc_layout, null);
					tv_vip_endtime = (TextView) rl_vip_desc.findViewById(R.id.tv_vip_endtime);
					tv_renew = (TextView) rl_vip_desc.findViewById(R.id.tv_renew);
					tv_renew.setOnClickListener(this);
					tv_vip_endtime.setText(vipInfo.getLevelName() + getString(R.string.mine_allscreen_vip_endtime_text)
							+ vipInfo.getEndTime().substring(0, 11) + ")");
					ll_vip_desc.addView(rl_vip_desc, LayoutParams.MATCH_PARENT, mActivity.dip2px(45));
					if (i < vipInfos.size() - 1) {
						View view = new View(mActivity);
						view.setBackgroundColor(getResources().getColor(R.color.code03));
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
						ll_vip_desc.addView(view, params);
					}
				}
			}
			// 不需要boss功能
			if (MyApplication.getInstance().isNeedBoss() == 0) {
				ll_vip_desc.setVisibility(View.GONE);
				tv_get_vip.setVisibility(View.GONE);
			} else {
				if (LoginInfoUtil.isVip(mActivity)) {
					ll_vip_desc.setVisibility(View.VISIBLE);
					tv_get_vip.setVisibility(View.GONE);
				} else {
					ll_vip_desc.setVisibility(View.GONE);
					tv_get_vip.setVisibility(View.VISIBLE);
				}
			}

		}
	}

	private void saveUserInfo() {
		SerializeableUtil.saveObject(mActivity, MyApplication.USER_INFO, loginInfo);// 新刷新的信息，所以要重新保存
		MyApplication.getInstance().putInfo(MyApplication.VIPINFO, vipInfos);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_changepassword:
			Intent cintent = new Intent(getActivity(), DetailActivity.class);
			cintent.putExtra(DetailActivity.FRAGMENTNAME, ChangePasswordFragment.class.getName());
			startActivity(cintent);
			break;
		case R.id.ll_pay_record:
			cintent = new Intent(getActivity(), DetailActivity.class);
			cintent.putExtra(DetailActivity.FRAGMENTNAME, ConsumeRecordsFragment.class.getName());
			startActivity(cintent);
			break;
		case R.id.tv_renew:
			MyApplication.getInstance().putInt(LepayManager.PAY_FOR_WHAT, LepayManager.PAY_NORMAL);
			Intent membersIntent = MyApplication.getInstance().getBossManager().getMembersIntent(mActivity);
			mActivity.startActivity(membersIntent);
			break;
		case R.id.tv_name:
			if (TextUtils.isEmpty(selectSex)) {
				selectSex = tv_select_sex.getText().toString().trim();
			}
			lastName = tv_name.getText().toString().trim();
			if (null == selectBirthday) {
				selectBirthday = tv_select_birthday.getText().toString().trim();
			}
			LoginAPI.stratChangeName(getActivity(), selectSex, lastName, selectBirthday);
			break;
		case R.id.ll_sex:
			showSexDialog();
			break;
		case R.id.ll_birthday:
			showSelectBirthdayDialog();
			break;
		case R.id.ctv_headpic:
		case R.id.iv_mine_message:
		case R.id.civ_headpic:
			showSelectPicDialog();
			break;
		// case R.id.btn_none:
		// selectSex = getString(R.string.mine_secret);
		// notifySexDialog();
		// break;
		case R.id.btn_male:
			selectSex = getString(R.string.mine_male);
			notifySexDialog();
			break;
		case R.id.btn_female:
			selectSex = getString(R.string.mine_famale);
			notifySexDialog();
			break;
		case R.id.dialog_ensure:
			upLoadUserInfo();// 上传个人信息
			tv_select_sex.setText(selectSex);
			dialog.dismiss();
			break;
		case R.id.gallary:
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);// ACTION_OPEN_DOCUMENT
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("image/*");
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
				startActivityForResult(intent, SELECT_PIC_KITKAT);
			} else {
				startActivityForResult(intent, SELECT_PIC);
			}
			break;
		case R.id.dialog_cancel:
			dialog.dismiss();
			break;
		case R.id.camera:
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// Wysie_Soh: Create path for temp file
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				imgUri = Uri.fromFile(new File(mActivity.getExternalCacheDir(),
						"tmp_contact_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
			} else {
				imgUri = Uri.fromFile(new File(mActivity.getCacheDir(),
						"tmp_contact_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
			}

			intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
			intent.putExtra("return-data", false);
			startActivityForResult(intent, PHOTOHRAPH);
			break;
		case R.id.tv_get_vip:
			MyApplication.getInstance().putInt(LepayManager.PAY_FOR_WHAT, LepayManager.PAY_NORMAL);
			Intent it = MyApplication.getInstance().getBossManager().getMembersIntent(mActivity);
			startActivity(it);
			break;

		default:
			break;
		}

	}

	private void showSelectBirthdayDialog() {
		if (birthday == null) {
			birthday = "";
		}
		DateDialog dialog = new DateDialog(birthday);
		// 此处取出正确的生日，转换后传递参数
		dialog.setOnSaveListener(this);
		BaseDialog.show(getFragmentManager(), dialog);
	}

	/**
	 * 将长时间格式字符串转换为时间 yyyy-MM-dd
	 */
	public static String getStringDate(Long date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(date);

		return dateString;
	}

	/**
	 * 生日的回显
	 * 
	 * @param dialogBuilder
	 * @param birthday
	 */
	private void echoBirthday(DateTimeSelectorDialogBuilder dialogBuilder, String birthday) {
		if (!TextUtils.isEmpty(birthday)) {
			DateSelectorWheelView dateWheelView = dialogBuilder.getDateWheelView();
			dateWheelView.setCurrentYear(birthday.substring(0, 4));
			dateWheelView.setCurrentMonth(birthday.substring(5, 7));
			dateWheelView.setCurrentDay(birthday.substring(8, 10));
		}
	}

	private void showSelectPicDialog() {
		View view = mActivity.getLayoutInflater().inflate(R.layout.mine_login_photodialog, null);
		dialog = new Dialog(mActivity, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		// dialog中的三个按钮，从图库选择，拍照选择，取消
		dialog.findViewById(R.id.dialog_cancel).setOnClickListener(this);
		dialog.findViewById(R.id.gallary).setOnClickListener(this);
		dialog.findViewById(R.id.camera).setOnClickListener(this);
		Window window = dialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		wl.y = mActivity.getWindowManager().getDefaultDisplay().getHeight();
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = LayoutParams.MATCH_PARENT;
		wl.height = LayoutParams.WRAP_CONTENT;

		// 设置显示位置
		dialog.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	private void showSexDialog() {
		final View view = mActivity.getLayoutInflater().inflate(R.layout.mine_login_sexdialog, null);
		dialog = new Dialog(mActivity, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		dialog_cancel = (TextView) dialog.findViewById(R.id.dialog_cancel);
		btn_male = (TextView) dialog.findViewById(R.id.btn_male);
		// btn_none = (TextView) dialog.findViewById(R.id.btn_none);
		btn_female = (TextView) dialog.findViewById(R.id.btn_female);
		dialog_ensure = (TextView) dialog.findViewById(R.id.dialog_ensure);

		dialog_cancel.setOnClickListener(this);
		btn_male.setOnClickListener(this);
		btn_female.setOnClickListener(this);
		// btn_none.setOnClickListener(this);
		dialog_ensure.setOnClickListener(this);

		Window window = dialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		wl.y = mActivity.getWindowManager().getDefaultDisplay().getHeight();
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = LayoutParams.MATCH_PARENT;
		wl.height = LayoutParams.WRAP_CONTENT;

		// 设置显示位置
		dialog.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == NONE)
			return;
		// 拍照
		if (requestCode == PHOTOHRAPH) {
			cropImage(imgUri, mActivity.dip2px(59), mActivity.dip2px(59), PHOTORESULT);
		}
		if (data == null)
			return;
		// 读取相册缩放图片
		if (requestCode == SELECT_PIC || requestCode == SELECT_PIC_KITKAT) {
			imgUri = data.getData();
			cropImage(data.getData(), mActivity.dip2px(59), mActivity.dip2px(59), PHOTORESULT);
		}
		// 处理结果
		if (requestCode == PHOTORESULT) {
			if (tempUri != null) {
				Bitmap photo = decodeUriAsBitmap(tempUri);
				saveBitmapFile(photo);
				uploadHeadpic();
				dialog.dismiss();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void notifySexDialog() {
		if (getString(R.string.mine_male).equals(selectSex)) {
			// btn_male.setBackgroundColor(getResources().getColor(R.color.code08));
			btn_male.setTextColor(getResources().getColor(R.color.code1));
		} else {
			btn_male.setTextColor(getResources().getColor(R.color.code3));
		}
		if (getString(R.string.mine_famale).equals(selectSex)) {
			btn_female.setTextColor(getResources().getColor(R.color.code1));
		} else {
			btn_female.setTextColor(getResources().getColor(R.color.code3));
		}
		// if (getString(R.string.mine_secret).equals(selectSex)) {
		// btn_none.setBackgroundColor(getResources().getColor(R.color.code08));
		// } else {
		// btn_none.setBackgroundColor(getResources().getColor(R.color.code06));
		// }
	}

	/** 上传图片的逻辑 */
	private void uploadHeadpic() {
		new UiAsyncTask<Boolean>(this) {

			private String userId;
			private String token;
			private String userIcon;
			private String userIconAlertMessage;

			@Override
			protected Boolean doBackground() {
				try {
					if (loginInfo != null) {
						userId = loginInfo.getUserId();
						token = loginInfo.getToken();
					}
					RequestParams params = new RequestParams(StringDataRequest.MAIN_URL + "/updateUserImage");
					params.addBodyParameter("headerImage", uploadFile, null);
					params.addBodyParameter(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
					params.addBodyParameter(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
					params.addHeader("authtoken", token);
					String responseStr;
					responseStr = x.http().postSync(params, String.class);
					JSONObject responseJSON;
					try {
						responseJSON = new JSONObject(responseStr);
						int statusCode = responseJSON.optInt("state");
						userIconAlertMessage = responseJSON.optString("alertMessage");
						if (statusCode == 0) {
							String content = responseJSON.optString("content");
							JSONObject jsonObj = new JSONObject(content);
							// JSONArray listJson =
							// jsonObj.getJSONArray("list");
							String list = jsonObj.optString("list");
							JSONObject userIconJson = new JSONObject(list);
							userIcon = userIconJson.optString("userIcon");
							MyApplication.getInstance().putString("iconUrl" + userId, userIcon);//
						} else {
							return false;
						}
					} catch (JSONException e) {
						Logger.log(e);
						return false;
					}
				} catch (Throwable e1) {
					Logger.log(e1);
					return false;
				}
				return true;
			}

			protected void post(Boolean result) {
				if (result) {
					ImageListener listener = ImageLoader.getImageListener(civ_headpic, R.drawable.mine_default_head_img,
							R.drawable.mine_default_head_img);
					LruImageCache.getImageLoader(mActivity.getApplicationContext()).get(userIcon, listener);
				} else {
					mActivity.showToastSafe(userIconAlertMessage != null ? userIconAlertMessage
							: getString(R.string.mine_upload_headimg_failed), 1);
				}
			};
		}.showDialog().execute();

	}

	/**
	 * 截取图片
	 * 
	 * @param uri
	 *            图片对应的uri
	 * @param outputX
	 *            输出宽度
	 * @param outputY
	 *            输出长度
	 * @param requestCode
	 *            裁剪请求码
	 */
	public void cropImage(Uri uri, int outputX, int outputY, int requestCode) {
		if (uri == null) {
			System.out.println("this uri is null");
			return;
		}
		Intent intent = new Intent("com.android.camera.action.CROP");
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			String url = ImageUtils.getPath(mActivity.getApplicationContext(), uri);
			intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
		} else {
			intent.setDataAndType(uri, "image/*");
		}
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			tempUri = Uri.fromFile(new File(mActivity.getExternalCacheDir(),
					"tmp_contact_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
		} else {
			tempUri = Uri.fromFile(new File(mActivity.getCacheDir(),
					"tmp_contact_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
		}
		intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
		startActivityForResult(intent, requestCode);
	}

	/**
	 * 保存生日的回调
	 */
	@Override
	public void onSaveSelectedDate(String arg0) {
		if (tv_select_birthday != null) {
			tv_select_birthday.setText(arg0);
			selectBirthday = arg0;
			birthday = arg0;
			upLoadUserInfo();// 上传个人信息
		}
	}

	/** 上传个人资料 */
	private void upLoadUserInfo() {
		if (TextUtils.isEmpty(selectSex)) {
			selectSex = tv_select_sex.getText().toString().trim();
		}
		selectSexInt = (getActivity().getString(R.string.mine_male).equals(selectSex) ? "0"
				: (getActivity().getString(R.string.mine_female).equals(selectSex) ? "1" : "2"));
		lastName = tv_name.getText().toString().trim();
		if (null == selectBirthday) {
			selectBirthday = tv_select_birthday.getText().toString().trim();//
		}
		if (selectBirthday == "") {// 如果从生日框中得到的数据为空
			birth = System.currentTimeMillis() / 1000 - 630720000;
		} else {
			try {
				birth = SystemUtls.Strdate2Long(selectBirthday, getActivity().getString(R.string.mine_date_form));
			} catch (ParseException e1) {
				Logger.log(e1);
				birth = 0l;
			}
		}
		String editInfo = "{\"editInfoList\":[{\"editType\":0," + "\"editValue\": \"" + selectSexInt
				+ "\"},{\"editType\": 1," + "\"editValue\": \"" + lastName + "\"},{\"editType\": 2,"
				+ "\"editValue\":\"" + birth + "\"}]}";
		RequestParams params = new RequestParams(StringDataRequest.MAIN_URL + "/editUserInfo");
		LoginInfo loginInfo = LoginInfoUtil.getLoginInfo(mActivity);
		if (null != loginInfo) {
			params.addHeader("authtoken", loginInfo.getToken());
		}
		params.addBodyParameter(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
		params.addBodyParameter(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		params.addBodyParameter("editInfoList", editInfo);
		x.http().post(params, new CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				tv_name.setText(lastName);
				tv_select_sex.setText(selectSex);
				tv_select_birthday.setText(selectBirthday);
				// 保存对象
				LoginInfo loginInfo = LoginInfoUtil.getLoginInfo(mActivity);
				loginInfo.setGender(Integer.valueOf(selectSexInt));
				loginInfo.setNickName(lastName);
				loginInfo.setBirthday(birth);
				SerializeableUtil.saveObject(mActivity, MyApplication.USER_INFO, loginInfo);
				if (!sameNickname && getActivity() != null) {
					getActivity().finish();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				mActivity.showToastSafe(R.string.mine_upload_abnormal, 0);
			}

			@Override
			public void onCancelled(CancelledException cex) {

			}

			@Override
			public void onFinished() {

			}
		});

	}

	/**
	 * 解析uri，得到Bitmap对象
	 * 
	 * @param uri
	 * @return
	 */
	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(mActivity.getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			Logger.log(e);
			return null;
		}
		return bitmap;
	}

	/**
	 * Bitmap对象保存味图片文件
	 * 
	 * @param bitmap
	 */
	public void saveBitmapFile(Bitmap bitmap) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			uploadFile = new File(mActivity.getExternalCacheDir(),
					"tmp_contact_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
		} else {
			uploadFile = new File(mActivity.getCacheDir(),
					"tmp_contact_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
		}
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(uploadFile));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			Logger.log(e);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		loginInfo = LoginInfoUtil.getLoginInfo(mActivity);
		if (tv_name != null) {
			tv_name.setText(loginInfo.getNickName());
		}
		super.onResume();
	}

	@Override
	protected void onHandleMessage(Message msg) {

	}
	
	@Override
	public void paymentSuccess() {
		if (getActivity() == null) {
			return;
		}
		new UiAsyncTask<Integer>(this) {

			@Override
			protected Integer doBackground() throws Throwable {
				int code = updateVipInfo();
				return code;
			}

			protected void post(Integer result) {
				if (result == 0) {
					MyApplication.getInstance().putInfo(MyApplication.VIPINFO, vipInfos);
					SerializeableUtil.saveObject(mActivity, MyApplication.USER_INFO, infos.get(0));// 新刷新的信息，所以要重新保存

					List<VipInfo> vipUpdateInfos = (List<VipInfo>) MyApplication.getInstance()
							.getInfo(MyApplication.VIPINFO);
					// if (vipInfos != null && vipInfos.size() > 0) {
					// for (int i = 0; i < vipInfos.size(); i++) {
					// String showVipInfo = vipInfos.get(i).getLevelName()
					// + getString(R.string.mine_allscreen_vip_endtime_text)
					// + vipInfos.get(i).getEndTime().substring(0, 11) + ")";
					// if (ll_vip_desc != null &&
					// ll_vip_desc.findViewWithTag("showVipInfo" + i) != null) {
					// TextView tv_vip_endtime = (TextView)
					// ll_vip_desc.findViewWithTag("showVipInfo" + i)
					// .findViewById(R.id.tv_vip_endtime);
					// if (tv_vip_endtime != null) {
					// tv_vip_endtime.setText(showVipInfo);
					// }
					// }
					// }
					VipInfo vipInfo;
					ll_vip_desc.removeAllViews();
					for (int i = 0; i < vipUpdateInfos.size(); i++) {
						vipInfo = vipUpdateInfos.get(i);
						rl_vip_desc = View.inflate(mActivity, R.layout.mine_personal_vip_desc_layout, null);
						rl_vip_desc.setTag(String.valueOf("showVipInfo" + i));
						tv_vip_endtime = (TextView) rl_vip_desc.findViewById(R.id.tv_vip_endtime);
						tv_renew = (TextView) rl_vip_desc.findViewById(R.id.tv_renew);
						tv_renew.setOnClickListener(PersonalFragment.this);
						tv_vip_endtime
								.setText(vipInfo.getLevelName() + getString(R.string.mine_allscreen_vip_endtime_text)
										+ vipInfo.getEndTime().substring(0, 11) + ")");
						ll_vip_desc.addView(rl_vip_desc, LayoutParams.MATCH_PARENT, mActivity.dip2px(45));
						if (i < vipUpdateInfos.size() - 1) {
							View view = new View(mActivity);
							view.setBackgroundColor(getResources().getColor(R.color.code03));
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
									1);
							ll_vip_desc.addView(view, params);
						}
					}

					if (LoginInfoUtil.isVip(mActivity)) {
						ll_vip_desc.setVisibility(View.VISIBLE);
						tv_get_vip.setVisibility(View.GONE);
					} else {
						ll_vip_desc.setVisibility(View.GONE);
						tv_get_vip.setVisibility(View.VISIBLE);
					}
				}
			}

		}.showDialog().execute();
	}
	
	public int updateVipInfo() {
		infos.clear();
		vipInfos.clear();
		UserInfoDataRequest request = new UserInfoDataRequest(mActivity);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputParam.put("isaddboss", MyApplication.getInstance().isNeedBoss() + "");
		return request.setInputParam(mInputParam).setOutputData(infos, vipInfos).request(Request.Method.GET);
	}
}
