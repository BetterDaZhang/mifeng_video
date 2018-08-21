package com.summerxia.dateselector.widget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.summerxia.dateselector.utils.DateUtils;
import com.summerxia.dateselector.wheelview.OnWheelChangedListener;
import com.summerxia.dateselector.wheelview.StrericWheelAdapter;
import com.summerxia.dateselector.wheelview.WheelView;

/**
 * 项目名称: DateSelector
 */
public class DateSelectorWheelView extends RelativeLayout implements OnWheelChangedListener {
	private final String flag = "PfpsDateWheelView";
	// 存放时间的容器
	private String selectYear;
	private String selectMonth;
	private String selectDay;

	private LinearLayout llWheelViews;
	private TextView tvSubTitle;
	private WheelView wvYear;
	private WheelView wvMonth;
	private WheelView wvDay;
	private String[] years;
	private String[] months = new String[12];
	private String[] tinyDays = new String[28];
	private String[] smallDays = new String[29];
	private String[] normalDays = new String[30];
	private String[] bigDays = new String[31];
	// 不同的轮子适配器
	private StrericWheelAdapter yearsAdapter;
	private StrericWheelAdapter monthsAdapter;
	private StrericWheelAdapter tinyDaysAdapter;
	private StrericWheelAdapter smallDaysAdapter;
	private StrericWheelAdapter bigDaysAdapter;
	private StrericWheelAdapter normalDaysAdapter;

	
	public DateSelectorWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initLayout(context);
	}

	public DateSelectorWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initLayout(context);
	}

	public DateSelectorWheelView(Context context) {
		super(context);
		initLayout(context);
	}

	/** 初始化布局 */
	private void initLayout(Context context) {
		LayoutInflater.from(context).inflate(R.layout.dete_time_layout, this, true);
		llWheelViews = (LinearLayout) findViewById(R.id.ll_wheel_views);
		wvYear = (WheelView) findViewById(R.id.wv_date_of_year);
		wvMonth = (WheelView) findViewById(R.id.wv_date_of_month);
		wvDay = (WheelView) findViewById(R.id.wv_date_of_day);
		wvYear.addChangingListener(this);
		wvMonth.addChangingListener(this);
		wvDay.addChangingListener(this);
		setData();
	}

	private void setData() {

		Calendar calendar = Calendar.getInstance();
		int thisYear = calendar.get(Calendar.YEAR);// 今年
		years = new String[101];
		// 表示年的数组
		for (int i = 0; i < years.length; i++) {
			years[i] = thisYear - 100 + i + " 年";
		}
		// 表示月份的数组，
		for (int i = 0; i < months.length; i++) {
			if (i < 9) {
				months[i] = "0" + (1 + i) + " 月";
			} else {
				months[i] = (1 + i) + " 月";
			}
		}
		// 28天
		for (int i = 0; i < tinyDays.length; i++) {
			if (i < 9) {
				tinyDays[i] = "0" + (1 + i) + " 日";
			} else {
				tinyDays[i] = (1 + i) + " 日";
			}
		}
		// 29day
		for (int i = 0; i < smallDays.length; i++) {
			if (i < 9) {
				smallDays[i] = "0" + (1 + i) + " 日";
			} else {
				smallDays[i] = (1 + i) + " 日";
			}
		}
		// 30day
		for (int i = 0; i < normalDays.length; i++) {
			if (i < 9) {
				normalDays[i] = "0" + (1 + i) + " 日";
			} else {
				normalDays[i] = (1 + i) + " 日";
			}
		}
		// 31day
		for (int i = 0; i < bigDays.length; i++) {
			if (i < 9) {
				bigDays[i] = "0" + (1 + i) + " 日";
			} else {
				bigDays[i] = (1 + i) + " 日";
			}
		}
		yearsAdapter = new StrericWheelAdapter(years);
		monthsAdapter = new StrericWheelAdapter(months);
		tinyDaysAdapter = new StrericWheelAdapter(tinyDays);
		smallDaysAdapter = new StrericWheelAdapter(smallDays);
		normalDaysAdapter = new StrericWheelAdapter(normalDays);
		bigDaysAdapter = new StrericWheelAdapter(bigDays);
		wvYear.setAdapter(yearsAdapter);
		wvYear.setCurrentItem(getTodayYear());
		wvYear.setCyclic(false);// 设置年份不轮滚
		wvMonth.setAdapter(monthsAdapter);
		flashMonths();

		wvMonth.setCurrentItem(getTodayMonth());
		wvMonth.setCyclic(false);// 设置月份不轮滚

		if (isBigMonth(getTodayMonth() + 1)) {// 大月
			wvDay.setAdapter(bigDaysAdapter);
		} else if (getTodayMonth() == 1// 二月且是闰年
				&& isLeapYear(wvYear.getCurrentItemValue().subSequence(0, 4).toString().trim())) {
			wvDay.setAdapter(smallDaysAdapter);
		} else if (getTodayMonth() == 1) {// 二月非闰年
			wvDay.setAdapter(tinyDaysAdapter);
		} else {// 正常月
			wvDay.setAdapter(normalDaysAdapter);
		}
		flashDays();
		wvDay.setCurrentItem(getTodayDay());
		wvDay.setCyclic(false);
	}

	/**
	 * 获取当前日期的天数的位置
	 * 
	 * @return
	 */
	private int getTodayDay() {
		// 2015年12月01日
		int position = 0;
		String today = getToday();
		String day = today.substring(8, 10);
		selectDay = day;
		day = day + " 日";
		for (int i = 0; i < bigDays.length; i++) {
			if (day.equals(bigDays[i])) {
				position = i;
				break;
			}
		}
		System.out.println("today day " + day);
		return position;
	}

	/**
	 * 获取当前日期的月数的位置
	 * 
	 * @return
	 */
	private int getTodayMonth() {
		// 2015年12月01日
		int position = 0;
		String today = getToday();
		String month = today.substring(5, 7);
		selectMonth = month;
		month = month + " 月";
		for (int i = 0; i < months.length; i++) {
			if (month.equals(months[i])) {
				position = i;
				break;
			}
		}
		return position;
	}

	/**
	 * 获取当天的年份的位置
	 * 
	 * @return
	 */
	private int getTodayYear() {
		int position = 0;
		String today = getToday();
		String year = today.substring(0, 4);
		selectYear = year;
		year = year + " 年";
		for (int i = 0; i < years.length; i++) {
			if (year.equals(years[i])) {
				position = i;
				break;
			}
		}
		return position;
	}

	/**
	 * 设置当前显示的年份
	 * 
	 * @param year
	 */
	public void setCurrentYear(String year) {
		boolean overYear = true;
		year = year + " 年";
		for (int i = 0; i < years.length; i++) {
			if (year.equals(years[i])) {
				wvYear.setCurrentItem(i);
				overYear = false;
				break;
			}
		}
		if (overYear) {
			Log.e(flag, "设置的年份超出了数组的范围");
		}
	}

	/**
	 * 设置当前显示的月份
	 * 
	 * @param month
	 */
	public void setCurrentMonth(String month) {
		month = month + " 月";
		for (int i = 0; i < months.length; i++) {
			if (month.equals(months[i])) {
				wvMonth.setCurrentItem(i);
				break;
			}
		}
	}

	/**
	 * 设置当前显示的日期号
	 * 
	 * @param day
	 *            14
	 */
	public void setCurrentDay(String day) {
		day = day + " 日";
		for (int i = 0; i < smallDays.length; i++) {
			if (day.equals(smallDays[i])) {
				wvDay.setCurrentItem(i);
				break;
			}
		}
	}

	/**
	 * 获取选择的日期的值
	 * 
	 * @return
	 */
	public String getSelectedDate() {
		return selectYear.concat("年").concat(selectMonth).concat("月").concat(selectDay).concat("日");
	}

	/**
	 * 设置日期选择器的日期转轮是否可见
	 * 
	 * @param visibility
	 */
	public void setDateSelectorVisiblility(int visibility) {
		llWheelViews.setVisibility(visibility);
	}

	public int getDateSelectorVisibility() {
		return llWheelViews.getVisibility();
	}

	/**
	 * 判断是否是闰年
	 * 
	 * @param year
	 * @return
	 */
	private boolean isLeapYear(String year) {
		int temp = Integer.parseInt(year);
		return temp % 4 == 0 && (temp % 100 != 0 || temp % 400 == 0) ? true : false;
	}

	/**
	 * 判断是否是大月
	 * 
	 * @param month
	 * @return
	 */
	private boolean isBigMonth(int month) {
		boolean isBigMonth = false;
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			isBigMonth = true;
			break;

		default:
			isBigMonth = false;
			break;
		}
		return isBigMonth;
	}

	int currentMonth = 1;
	private List<String> monthsList;
	private ArrayList<String> daysList;

	/**
	 * 日期改变的回调
	 */
	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		int id = wheel.getId();
		if (id == R.id.wv_date_of_year) {// 年轮
			// 将当前选中的年份分割出来并放在year中
			selectYear = DateUtils.splitDateString(wvYear.getCurrentItemValue()).trim();
			// 刷新月份的集合
			flashMonths();
			selectMonth = DateUtils.splitDateString(wvMonth.getCurrentItemValue()).trim();

			boolean flashDays = flashDays();
			if (!flashDays) {
				if (isLeapYear(selectYear)) {
					if (currentMonth == 2) {// 闰年2月
						wvDay.setAdapter(smallDaysAdapter);
					} else if (isBigMonth(currentMonth)) {// 闰年大月
						wvDay.setAdapter(bigDaysAdapter);
					} else {// 闰年小月
						wvDay.setAdapter(normalDaysAdapter);
					}
				} else if (currentMonth == 2) {// 非闰年2月
					wvDay.setAdapter(tinyDaysAdapter);
				} else if (isBigMonth(currentMonth)) {// 非闰年大月
					wvDay.setAdapter(bigDaysAdapter);
				} else {// 非闰年小月
					wvDay.setAdapter(smallDaysAdapter);
				}
			}
		} else if (id == R.id.wv_date_of_month) {// 月轮

			selectMonth = DateUtils.splitDateString(wvMonth.getCurrentItemValue()).trim();

			currentMonth = Integer.parseInt(selectMonth);
			switch (currentMonth) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:

				wvDay.setAdapter(bigDaysAdapter);
				break;
			case 2:
				String yearString = DateUtils.splitDateString(wvYear.getCurrentItemValue()).trim();
				if (isLeapYear(yearString)) {
					wvDay.setAdapter(smallDaysAdapter);
				} else {
					wvDay.setAdapter(tinyDaysAdapter);
				}
				break;
			default:
				wvDay.setAdapter(normalDaysAdapter);
				break;
			}
			// 刷新日子的集合
			flashDays();
		} else if (id == R.id.wv_date_of_day) {
			System.out.println(wvDay.getCurrentItemValue());
			selectDay = DateUtils.splitDateString(wvDay.getCurrentItemValue()).trim();//
		}
	}

	/**
	 * 获取今天的日期
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	private String getToday() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}

	/**
	 * 根据是否是当前年份设置月份数组 onChange中调用，传入选中的年份，如果是今年 ，就将月份的数组的最大值置为当前月份。
	 */
	private void setMonths(String year, List<String> months) {
		if (year.equals(getToday().substring(0, 4))) {// 如果选中的年份是今年
			// 表示月份的数组，
			for (int i = 0; i <= getTodayMonth(); i++) {
				if (i < 9) {
					months.add("0" + (1 + i) + " 月");
				} else {
					months.add((1 + i) + " 月");
				}
			}
			// 设置显示1月
			wvMonth.setCurrentItem(getTodayMonth());
		} else {
			for (int i = 0; i <= 11; i++) {
				if (i < 9) {
					months.add("0" + (1 + i) + " 月");
				} else {
					months.add((1 + i) + " 月");
				}
			}
		}
	}

	/**
	 * 根据是否是当前月份设置日子数组 onChange中调用，传入选中的月份，如果是当月 ，就将日子的数组的最大值置为当天。
	 */
	private void setDays(String month, List<String> days) {
		// 表示月份的数组，
		for (int i = 0; i <= getTodayDay(); i++) {
			if (i < 9) {
				days.add("0" + (1 + i) + " 日");
			} else {
				days.add((1 + i) + " 日");
			}
		}
		// 设置显示1日
		wvDay.setCurrentItem(0);
	}

	/**
	 * 刷新月份的集合,设置到适配器
	 */
	private void flashMonths() {
		monthsList = new ArrayList<String>();
		if (selectYear != null) {
			setMonths(selectYear, monthsList);// --------------------------->>>>>
			wvMonth.setAdapter(new StrericWheelAdapter((String[]) monthsList.toArray(new String[monthsList.size()])));
			// wvMonth.invalidate();
		}
	}

	/**
	 * 刷新日子的集合,设置到适配器
	 */
	private boolean flashDays() {
		daysList = new ArrayList<String>();
		// 如果选中的月份是当月)
		if (selectMonth != null && selectMonth.equals(getToday().substring(5, 7)) && selectYear.equals(getToday().subSequence(0, 4))) {
			setDays(selectMonth, daysList);// --------------------------->>>>>
			wvDay.setAdapter(new StrericWheelAdapter(daysList.toArray(new String[daysList.size()])));
			System.out.println(selectMonth + "---" + daysList.size());
			// wvDay.invalidate();
			return true;
		}
		return false;
	}

}
