package com.letv.autoapk.ui.me;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Environment;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.DetailActivity;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.LoginInfo;
import com.letv.autoapk.base.task.MyProgressDialog;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.PlayedRecordDao;
import com.letv.autoapk.open.OpenSdk;
import com.letv.autoapk.utils.SerializeableUtil;
import com.letv.autoapk.widgets.DataCleanManager;
import com.letv.autoapk.widgets.SearchClearDialog;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.tauth.Tencent;

public class SettingFragment extends BaseTitleFragment implements OnClickListener, OnCheckedChangeListener {
	private TextView tv_cachesize;
	private View ll_clearcache;
	private View fl_tuichu;
	private Switch iv_iswifi_cache;
	private Switch iv_ispush;
	private Switch iv_isskip;
	private static final String IS_WIFI_CACHE = "iswificache";
	private static final String IS_PUSH = "ispush";
	private static final String IS_SKIP = "isskip";
	private ProgressDialog dialog;

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	protected View createContentView() {
		return setupDataView();
	}

	@Override
	protected boolean loadingData() {
		return true;
	}

	@Override
	protected View setupDataView() {
		setStatusBarColor(getResources().getColor(
				R.color.code04));
		setTitle(getResources().getString(R.string.mine_setting), getResources().getColor(R.color.code6));
		setTitleLeftResource(R.drawable.mine_login_back, mActivity.dip2px(16));

		setLeftClickListener(new TitleLeftClickListener() {
			@Override
			public void onLeftClickListener() {
				getActivity().finish();
			}
		});
		initDialog();
		// 寻找控件
		View root = View.inflate(mActivity, R.layout.mine_setting, null);
		tv_cachesize = (TextView) root.findViewById(R.id.tv_cachesize);
		iv_iswifi_cache = (Switch) root.findViewById(R.id.sw_wificache);
		iv_ispush = (Switch) root.findViewById(R.id.sw_push);
		iv_isskip = (Switch) root.findViewById(R.id.sw_skip);
		ll_clearcache = root.findViewById(R.id.ll_clearcache);
		fl_tuichu = root.findViewById(R.id.tv_logout);
		root.findViewById(R.id.tv_about).setOnClickListener(this);
		root.findViewById(R.id.tv_feedback).setOnClickListener(this);
		tv_cachesize.setText(getCacheSize());
		// 设置监听
		ll_clearcache.setOnClickListener(this);
		fl_tuichu.setOnClickListener(this);
		// 设置回显
		boolean is_wifi_cache = MyApplication.getInstance().getBoolean(IS_WIFI_CACHE);
		boolean is_push = MyApplication.getInstance().getBoolean(IS_PUSH);
		boolean is_skip = MyApplication.getInstance().getBoolean(IS_SKIP);
		iv_iswifi_cache.setChecked(is_wifi_cache);
		iv_ispush.setChecked(is_push);
		iv_isskip.setChecked(is_skip);

		iv_iswifi_cache.setOnCheckedChangeListener(this);
		iv_ispush.setOnCheckedChangeListener(this);
		iv_isskip.setOnCheckedChangeListener(this);
		// 没有登录不显示退出登录
		boolean login = MyApplication.getInstance().isLogin();
		if (!login) {
			fl_tuichu.setVisibility(View.INVISIBLE);
		} else {
			fl_tuichu.setVisibility(View.VISIBLE);
		}
		return root;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_feedback:
			Intent fintent = new Intent(getActivity(), DetailActivity.class);
			fintent.putExtra(DetailActivity.FRAGMENTNAME, FeedBackFragment.class.getName());
			startActivity(fintent);

			break;
		case R.id.tv_about:
			Intent intent = new Intent(getActivity(), DetailActivity.class);
			intent.putExtra(DetailActivity.FRAGMENTNAME, AboutFragment.class.getName());
			startActivity(intent);
			break;
		case R.id.ll_clearcache:
			// SearchClearDialog.Builder builder = new
			// SearchClearDialog.Builder(mActivity);
			// builder.setTitle("清理缓存会删除已缓存的视频，\n确认清理？");
			// builder.setMessage(null).setPositiveButton("", new
			// DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// LeDownloadManager.getInstance(SettingActivity.this).deleteAll();
			// DaoManager.getInstance(SettingActivity.this).getSearchHistoryDao().deleleAll();
			// // 删除缓存的视频
			// DataCleanManager.cleanCustomCache(SystemUtls.getDownloadPath());
			// // 删除播放记录
			// PlayedRecordDao playedRecordDao =
			// DaoManager.getInstance(SettingActivity.this).getPlayedRecordDao();
			// playedRecordDao.deleleAll();
			// 删除内外缓存
			// DataCleanManager.deleteFolderFile(getCacheDir().getAbsolutePath(),
			// false);
			// if
			// (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			// {
			// DataCleanManager.deleteFolderFile(getExternalCacheDir().getAbsolutePath(),
			// false);
			// }
			// mActivity.showToastSafe("缓存清理完成", 1);
			// tv_cachesize.setText("0B");
			// alert.dismiss();
			// }
			// });
			// builder.setNegativeButton("", new
			// DialogInterface.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// alert.dismiss();
			// }
			// });
			// alert = builder.create();
			// alert.show();
			showProgress(true);
			boolean cleanCache = cleanCache();
			if (!cleanCache) {
				mActivity.showToastSafe(R.string.mine_clear_cache_failed, 1);
			} else {
				tv_cachesize.setText(getCacheSize());
			}
			break;
		case R.id.tv_logout:
			// 退出登录，点击回退到我的.
			saveLoginInfo(mActivity, null);
			showClearDialog();
			MyApplication.getInstance().putBoolean(MyApplication.ISLOGIN, false);// 将用户登录状态改为未登录
			MyApplication.getInstance().putString(MyApplication.TOKEN, "");// 将用户登录状态改为未登录
			MyApplication.getInstance().putString(MyApplication.REFRESH_TOKEN, "");

			// 三方登录的置为空
			// 将三方登录参数存放
			String logintype = MyApplication.getInstance().getString("loginType");
			MyApplication.getInstance().getOpenSdk().logout(logintype);
			MyApplication.getInstance().putString("accessToken", "");
			MyApplication.getInstance().putString("openId", "");
			MyApplication.getInstance().putString("tokenExpir", "");
			MyApplication.getInstance().putString("loginType", "");
			break;

		default:
			break;
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case R.id.sw_wificache:
			if (buttonView.isChecked()) {
				MyApplication.getInstance().putBoolean(IS_WIFI_CACHE, true);
			} else {
				MyApplication.getInstance().putBoolean(IS_WIFI_CACHE, false);
			}
			break;
		case R.id.sw_push:

			break;
		case R.id.sw_skip:

			break;

		default:
			break;
		}
	}

	private String getCacheSize() {
		long totalCache = 0;
		long internalCache;
		long internalFiles;
		long externalFiles;
		try {
			internalCache = DataCleanManager.getFolderSize(mActivity.getCacheDir());
			// internalFiles =
			// DataCleanManager.getFolderSize(mActivity.getFilesDir());
			externalFiles = DataCleanManager.getFolderSize(mActivity.getExternalCacheDir());
			// totalCache = internalCache + internalFiles + externalFiles;
			totalCache = internalCache + externalFiles;
		} catch (Exception e) {
			Logger.log(e);
		}
		return DataCleanManager.getFormatSize(totalCache);
	}

	private boolean cleanCache() {
		try {
			DataCleanManager.deleteFolderFile(mActivity.getCacheDir().getAbsolutePath(), true);
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				DataCleanManager.deleteFolderFile(mActivity.getExternalCacheDir().getAbsolutePath(), true);
			}
			// DataCleanManager.deleteFolderFile(mActivity.getFilesDir().getAbsolutePath(),
			// true);
		} catch (Exception e) {
			Logger.log(e);
			showProgress(false);
			return false;
		}
		showProgress(false);
		return true;
	}

	private void initDialog() {
		dialog = new MyProgressDialog(mActivity);
		dialog.setCancelable(true);
		dialog.setIndeterminate(true);
		dialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
	}

	protected void showProgress(final boolean show) {
		if (dialog != null) {
			if (show) {
				dialog.show();
			} else {
				if (dialog.isShowing())
					dialog.dismiss();
			}
		}
	}

	public void showClearDialog() {
		SearchClearDialog.Builder builder = new SearchClearDialog.Builder(mActivity);
		builder.setTitle(mActivity.getResources().getString(R.string.logout_isclearrecord));
		builder.setMessage(null).setPositiveButton(R.string.mine_not_save, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PlayedRecordDao dao = MyApplication.getInstance().getDaoByKey(PlayedRecordDao.class.getName());
				dao.deleleAll();
				dialog.dismiss();
				getActivity().finish();
			}
		});
		builder.setNegativeButton(R.string.mine_save, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				getActivity().finish();
			}
		});
		SearchClearDialog alert = builder.create(R.layout.mine_logout_dialog);
		alert.setCancelListenser(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.cancel();
				getActivity().finish();
			}
		});
		alert.show();
	}

	/**
	 * 保存登录信息
	 * 
	 * @param loginInfo
	 */
	public void saveLoginInfo(Context context, LoginInfo loginInfo) {
		SerializeableUtil.saveObject(context, MyApplication.USER_INFO, loginInfo);
		MyApplication.getInstance().putInfo(MyApplication.VIPINFO, null);
	}

}
