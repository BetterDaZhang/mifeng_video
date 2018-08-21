package com.letv.autoapk.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

import com.letv.autoapk.context.MyApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * Log utility class
 */
public class Logger {

	/**
	 * Global application tag
	 */
	private static final String TAG = "AutoApk";

	/**
	 * Log level
	 */
	private static final int VERBOSE_LEVEL = 6;
	private static final int DEBUG_LEVEL = 5;
	private static final int INFO_LEVEL = 4;
	private static final int WARN_LEVEL = 3;
	private static final int ERROR_LEVEL = 2;

	/**
	 * For controlling log print according to the level.
	 */
	private static int mPrintCtrl = Logger.WARN_LEVEL;

	/**
	 * Whether the log write to the SD card.
	 */
	private static boolean mWriteSDCardCtrl = false;

	private static boolean IS_DEBUG = true;

	/**
	 * Priority constant for the print method;
	 */
	private static final int VERBOSE = 2;
	private static final int DEBUG = 3;
	private static final int INFO = 4;
	private static final int WARN = 5;
	private static final int ERROR = 6;

	private Logger() {

	}

	public static final void setLogLevel(int level) {
		Logger.mPrintCtrl = level;
	}

	public static final void v(String location, String msg) {
		if (Logger.mPrintCtrl >= Logger.VERBOSE_LEVEL) {
			Logger.print(Logger.VERBOSE, Logger.TAG, Logger.buildLog(location, msg, null));
		}
	}

	public static final void v(String location, String msg, Throwable tr) {
		if (Logger.mPrintCtrl >= Logger.VERBOSE_LEVEL) {
			Logger.print(Logger.VERBOSE, Logger.TAG, Logger.buildLog(location, msg, tr));
		}
	}

	public static final void d(String location, String msg) {
		if (!IS_DEBUG) {
			return;
		}
		if (Logger.mPrintCtrl >= Logger.DEBUG_LEVEL) {
			Logger.print(Logger.DEBUG, Logger.TAG, Logger.buildLog(location, msg, null));
		}
	}

	public static final void d(String location, String msg, Throwable tr) {
		if (!IS_DEBUG) {
			return;
		}
		if (Logger.mPrintCtrl >= Logger.DEBUG_LEVEL) {
			Logger.print(Logger.DEBUG, Logger.TAG, Logger.buildLog(location, msg, tr));
		}
	}

	public static final void i(String location, String msg) {
		if (Logger.mPrintCtrl >= Logger.INFO_LEVEL) {
			Logger.print(Logger.INFO, Logger.TAG, Logger.buildLog(location, msg, null));
		}
	}

	public static final void i(String location, String msg, Throwable tr) {
		if (Logger.mPrintCtrl >= Logger.INFO_LEVEL) {
			Logger.print(Logger.INFO, Logger.TAG, Logger.buildLog(location, msg, tr));
		}
	}

	public static final void w(String location, String msg) {
		if (Logger.mPrintCtrl >= Logger.WARN_LEVEL) {
			Logger.print(Logger.WARN, Logger.TAG, Logger.buildLog(location, msg, null));
		}
	}

	public static final void w(String location, String msg, Throwable tr) {
		if (Logger.mPrintCtrl >= Logger.WARN_LEVEL) {
			Logger.print(Logger.WARN, Logger.TAG, Logger.buildLog(location, msg, tr));
		}
	}

	public static final void e(String location, String msg) {
		if (Logger.mPrintCtrl >= Logger.ERROR_LEVEL) {
			Logger.print(Logger.ERROR, Logger.TAG, Logger.buildLog(location, msg, null));
		}
	}

	public static final void e(String location, String msg, Throwable tr) {
		if (Logger.mPrintCtrl >= Logger.ERROR_LEVEL) {
			Logger.print(Logger.ERROR, Logger.TAG, Logger.buildLog(location, msg, tr));
		}
	}

	private static final void print(int priority, String tag, String msg) {
		android.util.Log.println(priority, tag, msg);
		if (Logger.mWriteSDCardCtrl) {
			Logger.writeToSdcard();
		}
	}

	private static final String buildLog(String location, String msg, Throwable tr) {
		if (tr != null) {
			msg = msg + '\n' + Logger.getStackTraceString(tr);
		}

		return new StringBuffer().append("[").append(location).append("]").append(msg).toString();
	}

	private static final void writeToSdcard() {
		// do something
	}

	public static void log(Throwable e) {
		e.printStackTrace();
		// 友盟错误信息日志上报
		MobclickAgent.reportError(MyApplication.getInstance(), e);
	}

	/**
	 * Handy function to get a loggable stack trace from a Throwable
	 * 
	 * @param tr
	 *            An exception to log
	 */
	public static String getStackTraceString(Throwable tr) {
		if (tr == null) {
			return "";
		}

		Throwable t = tr;
		while (t != null) {
			if (t instanceof UnknownHostException) {
				return "";
			}
			t = t.getCause();
		}

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, false);
		tr.printStackTrace(pw);
		pw.flush();

		return sw.toString();
	}
}
