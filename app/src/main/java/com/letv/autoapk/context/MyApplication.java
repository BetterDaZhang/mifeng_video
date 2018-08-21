package com.letv.autoapk.context;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.xutils.x;

import com.lecloud.sdk.api.stats.IAppStats;
import com.lecloud.sdk.api.stats.ICdeSetting;
import com.lecloud.sdk.config.LeCloudPlayerConfig;
import com.lecloud.sdk.listener.OnInitCmfListener;
import com.letv.autoapk.R;
import com.letv.autoapk.base.db.BaseDao;
import com.letv.autoapk.base.db.DbWrapper;
import com.letv.autoapk.base.db.Model;
import com.letv.autoapk.boss.BossManager;
import com.letv.autoapk.boss.LePaySuccessListener;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.open.OpenSdk;
import com.letv.autoapk.utils.CrashExceptionHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.support.v4.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

public class MyApplication extends Application {
	private static MyApplication instance;
	private int width;
	private int height;
	private int densityDpi;
	private float density;
	public final static String ISLOGIN = "isLogin";// 是否登陆
	public final static String REFRESH_TOKEN = "refreshToken";// 是否登陆
	public final static String IS_LOGIN_NORMAL = "is_login_normal";// 登录标记，普通登录还是三方登录
	public final static String IS_NEED_UPLOAD_RECORDS = "is_need_upload_records";// 登录标记，普通登录还是三方登录
	public final static String TOKEN = "token";
	private SharedPreferences sp;
	private DbWrapper database;
	private OpenSdk openSdk;
	private AtomicReference<BossManager> bossManager;
	public final static String USER_INFO = "userinfo";
	public final static String NOT_ONLYWIFI = "notonlywifi";
	private ArrayMap<String, BaseDao<Model>> daomap;
	public final static String VIPINFO = "vipinfo";// 存储用户会员信息
	public final static String CURRENT_VIDEO_CHARGE_INFO = "currentVideoChargeInfo";// 当前影片信息，会员or单片or单片或会员（生成订单详情时会使用）
	public final static String CURRENT_VOD_VIDEO_INFO = "currentVodVideoInfo";// 当前影片信息，会员or单片or单片或会员（生成订单详情时会使用）
	public final static String CURRENT_PACKAGE_INFO = "currentVideoPackgeInfo";// 当前会员信息
	private boolean isAuthSuccess;// 是否鉴权成功
	public static final String BOSS_STATE = "bossstate";// 是否需要boss功能,1有0无

	/**
	 * 会员信息、影片信息、会员信息的容器
	 */
	private HashMap<String, Object> infoMap;

	/**
	 * 判断是否登陆
	 * 
	 * @return
	 */
	public boolean isLogin() {
		return sp.getBoolean(ISLOGIN, false);
	}

	/** 获取boolean */
	public boolean getBoolean(String key) {
		return sp.getBoolean(key, false);
	}

	/** 保存boolean */
	public void putBoolean(String key, boolean value) {
		sp.edit().putBoolean(key, value).commit();
	}

	public int isNeedBoss() {
		int needboss = getInt(BOSS_STATE);
		return needboss == 1 ? 1 : 0;
	}

	public void setIsNeedBoss(int isNeedBoss) {
		putInt(BOSS_STATE, isNeedBoss);
	}

	public boolean isAuthSuccess() {
		return isAuthSuccess;
	}

	public void setAuthSuccess(boolean isAuthSuccess) {
		this.isAuthSuccess = isAuthSuccess;
	}

	/** 获取string */
	public String getString(String key) {
		return sp.getString(key, null);
	}

	/** 保存string */
	public void putString(String key, String value) {
		sp.edit().putString(key, value).commit();
	}

	/** 保存int */
	public void putInt(String key, int value) {
		sp.edit().putInt(key, value).commit();
	}

	/** 保存int */
	public int getInt(String key) {
		return sp.getInt(key, -1);
	}

	public void putInfo(String key, Object object) {
		infoMap.put(key, object);
	}

	public Object getInfo(String key) {
		return infoMap.get(key);

	}

	public String getAppid() {
		return getResources().getString(R.string.app_id);
	}

	public String getTenantId() {
		return getResources().getString(R.string.tenant_id);
	}

	public String getTemplate() {
		return getResources().getString(R.string.template);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		x.Ext.init(this);
		x.Ext.setDebug(false);
		sp = this.getSharedPreferences("autoapp", Context.MODE_PRIVATE);
		bossManager = new AtomicReference<BossManager>(new BossManager(getApplicationContext()));
		// 抓取crash log
		CrashExceptionHandler.getInstance(this).init();
		openSdk = new OpenSdk(getApplicationContext());
		daomap = new ArrayMap<String, BaseDao<Model>>();
		infoMap = new HashMap<String, Object>();

		// sdk 4.5
		try {
			String processName = getProcessName(this, android.os.Process.myPid());
			if (getApplicationInfo().packageName.equals(processName)) {
				PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				final LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
				parameters.put(ICdeSetting.HOST_TYPE, LeCloudPlayerConfig.HOST_DEFAULT + "");
				parameters.put(ICdeSetting.LOG_OUTPUT_TYPE, LeCloudPlayerConfig.LOG_LOGCAT + "");
				parameters.put(ICdeSetting.USE_CDE_PORT, false + "");
				parameters.put(IAppStats.APP_NAME, "Phone-Android");
				parameters.put(IAppStats.APP_PACKAGE_NAME, getPackageName());
				parameters.put(IAppStats.APP_VERSION_NAME, packageInfo.versionName);
				parameters.put(IAppStats.APP_VERSION_CODE, packageInfo.versionCode + "");
				parameters.put(IAppStats.APP_CHANNEL, "lecloud");
				parameters.put(IAppStats.APP_CATEGORY, "IAB1-5");
				parameters.put(IAppStats.AD_IS_TEST, "0");
				LeCloudPlayerConfig.setmInitCmfListener(new OnInitCmfListener() {
					@Override
					public void onCdeStartSuccess() {
						Log.d("hua", " onCdeStartSuccess ");
					}

					@Override
					public void onCdeStartFail() {
						Log.d("hua", " onCdeStartFail() ");
					}

					@Override
					public void onCmfCoreInitSuccess() {

					}

					@Override
					public void onCmfCoreInitFail() {

					}

					@Override
					public void onCmfDisconnected() {
						try {
							Log.d("Mapplication", "onCmfDisconnected: ");
							LeCloudPlayerConfig.init(getApplicationContext(), parameters);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				LeCloudPlayerConfig.init(getApplicationContext(), parameters);
			}
		} catch (Exception e) {
			// assets目录中必须放三个文件，否则报错
			e.printStackTrace();
		}
		// 友盟场景类型设置接口
		MobclickAgent.setScenarioType(this, EScenarioType.E_UM_NORMAL);
		// 友盟集成测试，是否上报
		// MobclickAgent.setDebugMode( true );
		/**
		 * 友盟推送
		 */
		PushAgent mPushAgent = PushAgent.getInstance(this);
		// 注册推送服务，每次调用register方法都会回调该接口
		mPushAgent.register(new IUmengRegisterCallback() {

			@Override
			public void onSuccess(String deviceToken) {
				// 注册成功会返回device token
			}

			@Override
			public void onFailure(String s, String s1) {

			}
		});
		// 关闭日志输出
		mPushAgent.setDebugMode(false);
	}

	public BossManager getBossManager() {
		return bossManager.get();
	}

	@SuppressWarnings("unchecked")
	public <E extends BaseDao> E getDaoByKey(String key) {
		if (!daomap.containsKey(key)) {
			synchronized (daomap) {
				if (!daomap.containsKey(key)) {
					try {
						Constructor<E> constructor = (Constructor<E>) Class.forName(key).getConstructor(Context.class);
						putDao(key, constructor.newInstance(getApplicationContext()));
					} catch (Exception e) {
						Logger.log(e);
						return null;
					}
				}
			}
		}
		return (E) daomap.get(key);
	}

	public void putDao(String key, BaseDao<Model> dao) {
		if (daomap.containsKey(key) == false) {
			daomap.put(key, dao);
		}
	}

	public DbWrapper getDataBase() {
		if (database == null) {
			synchronized (this) {
				if (database == null) {
					database = new DbWrapper();
				}
			}
		}
		return database;
	}

	public OpenSdk getOpenSdk() {
		return openSdk;
	}

	public static MyApplication getInstance() {
		return instance;
	}

	public static boolean isInitScreenParam = false;

	public void initScreenParams(Display display) {
		if (!isInitScreenParam) {
			isInitScreenParam = true;
			DisplayMetrics metric = new DisplayMetrics();
			display.getMetrics(metric);
			this.width = metric.widthPixels; // 屏幕宽度（像素）
			this.height = metric.heightPixels; // 屏幕高度（像素） metric.heightPixels
			this.densityDpi = metric.densityDpi;// 屏幕密度dpi
			this.density = metric.density;// 屏幕密度
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public static String getClientId() {
		return android.provider.Settings.Secure.getString(instance.getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		try {
			daomap.clear();
			if (database != null)
				database.closeDb();
		} catch (Exception e) {
			Logger.log(e);
		}
	}

	public static String getProcessName(Context cxt, int pid) {
		ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
		if (runningApps != null) {
			for (RunningAppProcessInfo procInfo : runningApps) {
				if (procInfo.pid == pid) {
					return procInfo.processName;
				}
			}
		}
		return null;
	}

	private LePaySuccessListener vipPaySuccessListener;

	public void setLePaySuccessListener(LePaySuccessListener lePaySuccessListener) {
		this.vipPaySuccessListener = lePaySuccessListener;
	}

	public LePaySuccessListener getLepaySuccessListener() {
		return vipPaySuccessListener;
	}

}
