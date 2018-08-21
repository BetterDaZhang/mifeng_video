package com.letv.autoapk.ui.mobilelive;

import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.dialog.BaseDialog;

public class MobileLiveDialog extends BaseDialog {
	public static final String MOBILE_LIVE_DIALOG_TITLE = "dialogTitle";
	public static final String MOBILE_LIVE_DIALOG_OK = "dialogOk";
	public static final String MOBILE_LIVE_DIALOG_CANCEL = "dialogCancel";

	@Override
	public int layoutId() {
		return R.layout.mobile_live_dialog;
	}
	public MobileLiveDialog(){
		
	}

	private OnClickListener ok;
	private OnClickListener exit;

	public void setOnClickListener(OnClickListener ok, OnClickListener exit) {
		this.ok = ok;
		this.exit = exit;
	}
	private TextView dialogOk;
	private TextView dialogCancle;
	private TextView dialogTitle;

	@Override
	protected void setupUI(View view, Bundle bundle) throws Exception {
		dialogOk = (TextView) view(R.id.dialog_ok);
		dialogOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				if (ok != null)
					ok.onClick(getDialog(), 0);
			}
		});
		dialogCancle = (TextView) view(R.id.dialog_cancel);
		dialogCancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				if (exit != null)
					exit.onClick(getDialog(), 0);
			}
		});
		dialogTitle = (TextView) view(R.id.mobile_live_title);
		Bundle dataBundle = getArguments();
		String title = dataBundle.getString(MOBILE_LIVE_DIALOG_TITLE);
		String ok = dataBundle.getString(MOBILE_LIVE_DIALOG_OK);
		String cancel = dataBundle.getString(MOBILE_LIVE_DIALOG_CANCEL);
		if (title != null ) {
			dialogTitle.setText(title);
		}
		setDialogOk(ok);
		setDialogCancel(cancel);
	}
	
	private void setDialogOk(String text){
		if (text != null) {
			dialogOk.setText(text);
			dialogOk.setVisibility(View.VISIBLE);
		}else{
			dialogOk.setVisibility(View.GONE);
		}
		
	}
	
	private void setDialogCancel(String text){
		if (text != null) {
			dialogCancle.setText(text);
			dialogCancle.setVisibility(View.VISIBLE);
		}else{
			dialogCancle.setVisibility(View.GONE);
		}
	}

}
