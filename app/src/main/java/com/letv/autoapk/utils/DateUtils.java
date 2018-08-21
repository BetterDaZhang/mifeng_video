package com.letv.autoapk.utils;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	public static final String FORMAT_DATE = "yyyy.MM.dd";
	public static final String FORMAT_MONTH_DATE = "MM月dd日";
	public static final String FORMAT_TIME = "HH:mm";

	public static String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
		return sdf.format(date);
	}
	
	public static String formatTime(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_TIME);
		return sdf.format(date);
	}

	public static Date getDateFromGreenwichSec(long sec) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(sec);
		return cal.getTime();
	}

	public static String formatWeekDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_MONTH_DATE);
		if (isYesterday(date)) {
			return "昨天 " + getWeekDay(date);
		} else if (isDodayday(date)) {
			return "今天 " + getWeekDay(date);
		} else if (isTomorrowday(date)) {
			return "明天 " + getWeekDay(date);
		} else {
			return sdf.format(date) + " " + getWeekDay(date);
		}
	}

	public static boolean isYesterday(Date a) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, c.get(Calendar.DATE) - 1);
		Date today = c.getTime();
		SimpleDateFormat format = new SimpleDateFormat(FORMAT_MONTH_DATE);
		return format.format(today).equals(format.format(a));

	}

	public static boolean isDodayday(Date a) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, c.get(Calendar.DATE));
		Date today = c.getTime();
		SimpleDateFormat format = new SimpleDateFormat(FORMAT_MONTH_DATE);
		return format.format(today).equals(format.format(a));

	}

	public static boolean isTomorrowday(Date a) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, c.get(Calendar.DATE) + 1);
		Date tomorrow = c.getTime();
		SimpleDateFormat format = new SimpleDateFormat(FORMAT_MONTH_DATE);
		return format.format(tomorrow).equals(format.format(a));

	}

	public static String getWeekDay(Date date) {
		// 返回结果
		String ret = "";
		// 星期
		int day = 0;
		// 中文星期
		String dayOfWeek = null;
		// 日历类
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		day = cal.get(Calendar.DAY_OF_WEEK);
		// 判断为周几
		switch (day) {
		case 1:
			dayOfWeek = "日";
			break;
		case 2:
			dayOfWeek = "一";
			break;
		case 3:
			dayOfWeek = "二";
			break;
		case 4:
			dayOfWeek = "三";
			break;
		case 5:
			dayOfWeek = "四";
			break;
		case 6:
			dayOfWeek = "五";
			break;
		case 7:
			dayOfWeek = "六";
			break;
		default:
			dayOfWeek = "日";

		}
		ret = "周" + dayOfWeek;
		return ret;
	}

}
