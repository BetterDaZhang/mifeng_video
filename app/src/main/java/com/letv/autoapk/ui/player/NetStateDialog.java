package com.letv.autoapk.ui.player;

import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;

import com.letv.autoapk.R;
import com.letv.autoapk.base.dialog.BaseDialog;

public class NetStateDialog extends BaseDialog {

	@Override
	public int layoutId() {
		return R.layout.play_netstate_dialog;
	}
	private OnClickListener ok;
	private OnClickListener exit;
	public void setOnClickListener(OnClickListener ok,OnClickListener exit){
		this.ok = ok;
		this.exit = exit;
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
	}

}
