package com.letv.autoapk.ui.login;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import com.letv.autoapk.R;
import com.letv.autoapk.base.dialog.BaseDialog;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.LoginInfo;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.ui.login.DateDialog.OnSaveListener;
import com.letv.autoapk.utils.CharUtil;
import com.letv.autoapk.utils.ImageUtils;
import com.letv.autoapk.utils.SerializeableUtil;
import com.letv.autoapk.utils.SystemUtls;
import com.summerxia.dateselector.widget.DateSelectorWheelView;
import com.summerxia.dateselector.widget.DateTimeSelectorDialogBuilder;
import com.tencent.open.utils.HttpUtils;

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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 完善个人资料
 */
public class FillDataFragment extends BaseTitleFragment implements
		OnClickListener, OnSaveListener {
	private static final int NEWNAME = 1;
	/** 拍照 */
	private static final int PHOTOHRAPH = 3;
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private static final int NONE = 0;
	private static final int PHOTORESULT = 2;
	/** 从图库选择,KITKAT以上 */
	private static final int SELECT_PIC_KITKAT = 4;
	private static final int SELECT_PIC = 5;
	// 姓名，性别，生日的tv
	private RelativeLayout rl_name;
	private RelativeLayout rl_sex;
	private RelativeLayout rl_birthday;
	private TextView tv_select_sex;// 显示性别的tv
	private TextView tv_select_birthday;
	private ImageView civ_headpic;// 圆形头像
	private Dialog dialog;

	private String birthday;
	private Uri imgUri;// 图片uri
	private Uri tempUri;
	private Long birth;// 生日的时间戳
	private File uploadFile;// 要上传的图片文件
	private String newName;// 修改昵称，返回数据的保存
	private String selectSex;// 修改性别，返回数据的保存
	private String selectBirthday;// 修改生日，返回数据的保存
	private HttpUtils http;// HttpUtils
	private String selectSexInt;// 性别，数字表示
	private FrameLayout fl_next;
	private TextView dialog_cancel;
	private TextView btn_male;
//	private TextView btn_none;
	private TextView btn_female;
	private TextView dialog_ensure;
	private LoginInfo loginInfo;
	private EditText et_name;

	@Override
	protected void onHandleMessage(Message msg) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean loadingData() {
		return true;
	}

	@Override
	protected View setupDataView() {
		setTitle(getActivity().getString(R.string.mine_fill_userinfo),
				getResources().getColor(R.color.code6));
		setTitleLeftResource(R.drawable.mine_login_back, mActivity.dip2px(16));
		View root = View.inflate(mActivity, R.layout.mine_login_filldata, null);

		rl_name = (RelativeLayout) root.findViewById(R.id.rl_name);
		rl_sex = (RelativeLayout) root.findViewById(R.id.rl_sex);
		rl_birthday = (RelativeLayout) root.findViewById(R.id.rl_birthday);

		fl_next = (FrameLayout) root.findViewById(R.id.fl_next);

		et_name = (EditText) root.findViewById(R.id.et_name);
		tv_select_sex = (TextView) root.findViewById(R.id.tv_select_sex);
		tv_select_birthday = (TextView) root
				.findViewById(R.id.tv_select_birthday);
		civ_headpic = (ImageView) root.findViewById(R.id.civ_headpic);

		rl_birthday.setOnClickListener(this);
		rl_sex.setOnClickListener(this);
		fl_next.setOnClickListener(this);
		civ_headpic.setOnClickListener(this);
		loginInfo = LoginInfoUtil.getLoginInfo(mActivity);
		;
		return root;
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.rl_sex:
			showSexDialog();
			break;
		case R.id.fl_next:
			upLoadUserInfo();
			break;
		case R.id.rl_birthday:
			showSelectBirthdayDialog();
			break;
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
		case R.id.gallary:
			intent = new Intent(Intent.ACTION_GET_CONTENT);// ACTION_OPEN_DOCUMENT
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
		case R.id.dialog_ensure:
			tv_select_sex.setText(selectSex);
			dialog.dismiss();
			break;
		case R.id.camera:
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// Wysie_Soh: Create path for temp file
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				imgUri = Uri.fromFile(new File(mActivity.getExternalCacheDir(),
						"tmp_contact_"
								+ String.valueOf(System.currentTimeMillis())
								+ ".jpg"));
			} else {
				imgUri = Uri.fromFile(new File(mActivity.getCacheDir(),
						"tmp_contact_"
								+ String.valueOf(System.currentTimeMillis())
								+ ".jpg"));
			}
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
			intent.putExtra("return-data", false);
			startActivityForResult(intent, PHOTOHRAPH);
			break;

		}

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
			String url = ImageUtils.getPath(mActivity, uri);
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
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			tempUri = Uri.fromFile(new File(mActivity.getExternalCacheDir(),
					"tmp_contact_" + String.valueOf(System.currentTimeMillis())
							+ ".jpg"));
		} else {
			tempUri = Uri.fromFile(new File(mActivity.getCacheDir(),
					"tmp_contact_" + String.valueOf(System.currentTimeMillis())
							+ ".jpg"));
		}
		intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
		startActivityForResult(intent, requestCode);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == NONE)
			return;
		// 拍照
		if (requestCode == PHOTOHRAPH) {
			cropImage(imgUri, mActivity.dip2px(59), mActivity.dip2px(59),
					PHOTORESULT);
		}
		if (data == null)
			return;
		// 读取相册缩放图片
		if (requestCode == SELECT_PIC || requestCode == SELECT_PIC_KITKAT) {
			imgUri = data.getData();
			cropImage(data.getData(), mActivity.dip2px(59),
					mActivity.dip2px(59), PHOTORESULT);
		}
		// 处理结果
		if (requestCode == PHOTORESULT) {
			if (tempUri != null) {
				Bitmap photo = decodeUriAsBitmap(tempUri);
				// ///////////////
				saveBitmapFile(photo);
				civ_headpic.setImageBitmap(photo);
				uploadHeadpic();
				dialog.dismiss();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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
			bitmap = BitmapFactory.decodeStream(mActivity.getContentResolver()
					.openInputStream(uri));
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
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			uploadFile = new File(mActivity.getExternalCacheDir(),
					"tmp_contact_" + String.valueOf(System.currentTimeMillis())
							+ ".jpg");
		} else {
			uploadFile = new File(mActivity.getCacheDir(), "tmp_contact_"
					+ String.valueOf(System.currentTimeMillis()) + ".jpg");
		}
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(uploadFile));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			Logger.log(e);
		}
	}

	/** 上传个人资料 */
	private void upLoadUserInfo() {
		if (TextUtils.isEmpty(selectSex)) {
			selectSex = tv_select_sex.getText().toString().trim();
		}
		selectSexInt = (getString(R.string.male).equals(selectSex) ? "0"
				: (getString(R.string.female).equals(selectSex) ? "1" : "2"));
		newName = et_name.getText().toString().trim();
		if (TextUtils.isEmpty(newName)) {
			mActivity.showToastSafe(getString(R.string.namecannotempty), 0);
			return;
		}
		if (new CharUtil().isValid(newName) == 0
				|| new CharUtil().isValid(newName) == 1) {
			mActivity.showToastSafe(
					getString(R.string.mine_nickname_illegal_toast), 0);
			return;
		}
		if (null == selectBirthday) {
			selectBirthday = tv_select_birthday.getText().toString().trim();//
		}
		if (selectBirthday == "") {// 如果从生日框中得到的数据为空
			birth = System.currentTimeMillis() / 1000 - 630720000;
		} else {
			try {
				birth = SystemUtls.Strdate2Long(selectBirthday,
						getString(R.string.dateformat));
			} catch (ParseException e1) {
				Logger.log(e1);
				birth = 0l;
			}
		}
		String editInfo = "{\"editInfoList\":[{\"editType\":0,"
				+ "\"editValue\": \"" + selectSexInt + "\"},{\"editType\": 1,"
				+ "\"editValue\": \"" + newName + "\"},{\"editType\": 2,"
				+ "\"editValue\":\"" + birth + "\"}]}";
		RequestParams params = new RequestParams(StringDataRequest.MAIN_URL
				+ "/editUserInfo");
		LoginInfo loginInfo = LoginInfoUtil.getLoginInfo(mActivity);
		if (null != loginInfo) {
			params.addHeader("authtoken", loginInfo.getToken());
		}
		params.addBodyParameter(StringDataRequest.USER_ID,
				LoginInfoUtil.getUserId(mActivity));
		params.addBodyParameter(StringDataRequest.TENANT_ID, MyApplication
				.getInstance().getTenantId());
		params.addBodyParameter("editInfoList", editInfo);
		x.http().post(params, new CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				et_name.setText(newName);
				tv_select_sex.setText(selectSex);
				tv_select_birthday.setText(selectBirthday);

				// 保存对象
				LoginInfo loginInfo = LoginInfoUtil.getLoginInfo(mActivity);
				if (loginInfo == null) {
					loginInfo = new LoginInfo();
				}
				loginInfo.setGender(Integer.valueOf(selectSexInt));
				loginInfo.setNickName(newName);
				loginInfo.setBirthday(birth);
				SerializeableUtil.saveObject(mActivity,
						MyApplication.USER_INFO, loginInfo);
				getActivity().finish();
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				// TODO Auto-generated method stub
				mActivity.showToastSafe(getString(R.string.upload_error), 0);
			}

			@Override
			public void onCancelled(CancelledException cex) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub

			}
		});

	}

	public void notifySexDialog() {
		if (getString(R.string.mine_male).equals(selectSex)) {
//			btn_male.setBackgroundColor(getResources().getColor(R.color.code08));
			btn_male.setTextColor(getResources().getColor(R.color.code1));
		} else {
			btn_male.setTextColor(getResources().getColor(R.color.code3));
		}
		if (getString(R.string.mine_famale).equals(selectSex)) {
			btn_female.setTextColor(getResources().getColor(R.color.code1));
		} else {
			btn_female.setTextColor(getResources().getColor(R.color.code3));
		}
//		if (getString(R.string.mine_secret).equals(selectSex)) {
//			btn_none.setBackgroundColor(getResources().getColor(R.color.code08));
//		} else {
//			btn_none.setBackgroundColor(getResources().getColor(R.color.code06));
//		}
	}

	/** 上传图片的逻辑 */
	private void uploadHeadpic() {
		new UiAsyncTask<Void>(this) {

			private String userId;
			private String token;

			@Override
			protected Void doBackground() {
				//
				if (loginInfo != null) {
					userId = loginInfo.getUserId();
					token = loginInfo.getToken();
				}
				// params
				RequestParams params = new RequestParams(
						StringDataRequest.MAIN_URL + "/updateUserImage");
				params.addBodyParameter("headerImage", uploadFile, null);
				params.addBodyParameter(StringDataRequest.USER_ID,
						LoginInfoUtil.getUserId(mActivity));
				params.addBodyParameter(StringDataRequest.TENANT_ID,
						MyApplication.getInstance().getTenantId());
				// params.addHeader(StringDataRequest.USER_ID,
				// MyApplication.getInstance().getUserId());
				params.addHeader("authtoken", token);
				// params.addHeader(StringDataRequest.TENANT_ID,
				// MyApplication.getInstance().getTenantid());
				try {
					String responseStr = x.http()
							.postSync(params, String.class);
					JSONObject responseJSON;

					responseJSON = new JSONObject(responseStr);
					String content = responseJSON.optString("content");
					JSONObject jsonObj = new JSONObject(content);
					String list = jsonObj.optString("list");
					JSONObject userIconJson = new JSONObject(list);
					String userIcon = userIconJson.optString("userIcon");// 用户头像地址
					MyApplication.getInstance().putString("iconUrl" + userId,
							userIcon);//
				} catch (JSONException e) {
					Logger.log(e);
				} catch (Throwable e) {
					Logger.log(e);
				}
				return null;
			}
		}.showDialog().execute();

	}

	private void showSelectBirthdayDialog() {
		if (birthday == null) {
			birthday = "";
		}
		DateDialog dialog = new DateDialog(birthday);
		dialog.setOnSaveListener(this);
		BaseDialog.show(getFragmentManager(), dialog);
	}

	private void echoBirthday(DateTimeSelectorDialogBuilder dialogBuilder,
			String birthday) {
		// 生日的回显
		if (!TextUtils.isEmpty(birthday)) {
			DateSelectorWheelView dateWheelView = dialogBuilder
					.getDateWheelView();
			dateWheelView.setCurrentYear(birthday.substring(0, 4));
			dateWheelView.setCurrentMonth(birthday.substring(5, 7));
			dateWheelView.setCurrentDay(birthday.substring(8, 10));
		}
	}

	private void showSelectPicDialog() {
		View view = mActivity.getLayoutInflater().inflate(
				R.layout.mine_login_photodialog, null);
		dialog = new Dialog(mActivity, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
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
		final View view = mActivity.getLayoutInflater().inflate(
				R.layout.mine_login_sexdialog, null);
		dialog = new Dialog(mActivity, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		dialog_cancel = (TextView) dialog.findViewById(R.id.dialog_cancel);
		btn_male = (TextView) dialog.findViewById(R.id.btn_male);
		// btn_none = (TextView) dialog.findViewById(R.id.btn_none);
		btn_female = (TextView) dialog.findViewById(R.id.btn_female);
		dialog_ensure = (TextView) dialog.findViewById(R.id.dialog_ensure);

		dialog_cancel.setOnClickListener(this);
		btn_male.setOnClickListener(this);
		btn_female.setOnClickListener(this);
//		btn_none.setOnClickListener(this);
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

	/**
	 * 保存生日的回调
	 */
	@Override
	public void onSaveSelectedDate(String arg0) {
		if (tv_select_birthday != null) {
			tv_select_birthday.setText(arg0);
			selectBirthday = arg0;
			birthday = arg0;
			// upLoadUserInfo();// 上传个人信息
		}
	}
}
