package com.letv.autoapk.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.ui.main.MainAPI;
import com.umeng.analytics.MobclickAgent;



public class CrashExceptionHandler implements UncaughtExceptionHandler {
	private final String TAG = "CrashExceptionHandler";
	private static CrashExceptionHandler sInstance;
	private Context mContext;
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	private CrashExceptionHandler(Context context) {
		mContext = context;
	}

	public static CrashExceptionHandler getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new CrashExceptionHandler(context);
		}
		return sInstance;
	}

	public void init() {
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				Intent intent = MainAPI.getMainIntent(mContext);
				mContext.startActivity(intent);
				
			}
		});
		if (ex == null) {
			return;
		}
		ex.printStackTrace();
		Log.e(TAG, ex.getMessage());
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		StringBuffer sb = new StringBuffer();

		sb.append(result);
		
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = "crash-" + time + "-" + timestamp + ".log";
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				String path = "/sdcard/autoapk_crash/";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
			
		} catch (Exception e) {
			Logger.log(e);
		}
//		MobclickAgent.reportError(mContext,ex);//umeng
//		MobclickAgent.onKillProcess(mContext);
		System.exit(0);
		//杀死进程调用此方法来保存友盟统计数据
		MobclickAgent.onKillProcess(mContext);
		//友盟错误信息日志上报
		MobclickAgent.reportError(mContext, sb.toString());
		//android.os.Process.killProcess(android.os.Process.myPid());

	}

}
