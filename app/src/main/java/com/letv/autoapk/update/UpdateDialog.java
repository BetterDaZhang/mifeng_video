package com.letv.autoapk.update;

import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.dialog.BaseDialog;

public class UpdateDialog extends BaseDialog {

	@Override
	public int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.update_dialog;
	}
	
	private String version;
	private OnClickListener ok;
	private OnClickListener exit;
	public void setOnClickListener(OnClickListener ok,OnClickListener exit){
		this.ok = ok;
		this.exit = exit;
	}
	UpdateDialog(String version){
		this.version = version;
	}
	@Override
	protected void setupUI(View view, Bundle bundle) throws Exception {
		// TODO Auto-generated method stub
		view(R.id.dialog_ok).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				if(ok!=null)
					ok.onClick(getDialog(), 0);
			}
		});
		view(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				if(exit!=null)
					exit.onClick(getDialog(), 0);
			}
		});
		TextView msg = view(R.id.newversion);
		msg.setText(getString(R.string.gotnewversion, version));
	}

}
