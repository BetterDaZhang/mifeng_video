package com.letv.autoapk.ui.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.letv.autoapk.R;
import com.letv.autoapk.base.dialog.BaseDialog;
import com.letv.autoapk.common.utils.Logger;
import com.summerxia.dateselector.widget.DateSelectorWheelView;

class DateDialog extends BaseDialog implements OnClickListener {
	private OnSaveListener saveListener;
	public DateSelectorWheelView dateWheelView;
	public String echoBirthday;
	
	public DateDialog(String birthday) {
		this.echoBirthday = birthday;
	}

	@Override
	public int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.mine_login_datedialog;
	}


	@Override
	protected void setupUI(View view, Bundle bundle) throws Exception {
		try {
			Window dialogWindow = getDialog().getWindow();
			dialogWindow.setGravity(Gravity.BOTTOM);
			view.setMinimumWidth(getResources().getDisplayMetrics().widthPixels);
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			lp.x = 0;
			lp.y = 0;
			dialogWindow.setAttributes(lp);
		} catch (Exception e) {
			Logger.log(e);
		}
		dateWheelView = view(R.id.date_time_wheelView);
		echoBirthday(dateWheelView, echoBirthday);
		view(R.id.dialog_cancel).setOnClickListener(this);
		view(R.id.dialog_ok).setOnClickListener(this);
	}

	public interface OnSaveListener {
		abstract void onSaveSelectedDate(String selectedDate);
	}

	@Override
	public int getStyle() {
		// TODO Auto-generated method stub
		return R.style.DateDialogStyle;
	}

	public void setOnSaveListener(OnSaveListener saveListener) {
		this.saveListener = saveListener;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.dialog_cancel) {
			dismiss();
		} else if (id == R.id.dialog_ok) {
			if (null != saveListener) {
				saveListener.onSaveSelectedDate(dateWheelView.getSelectedDate());
			}
			dismiss();
		}
	}

	// 生日的回显
	public void echoBirthday(DateSelectorWheelView dateWheelView, String birthday) {
		if (!TextUtils.isEmpty(birthday)) {
			dateWheelView.setCurrentYear(birthday.substring(0, 4));
			dateWheelView.setCurrentMonth(birthday.substring(5, 7));
			dateWheelView.setCurrentDay(birthday.substring(8, 10));
		}
	}

}
