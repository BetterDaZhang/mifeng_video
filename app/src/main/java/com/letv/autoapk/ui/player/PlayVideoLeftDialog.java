package com.letv.autoapk.ui.player;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import com.letv.autoapk.R;
import com.letv.autoapk.base.dialog.BaseDialog;

public class PlayVideoLeftDialog extends BaseDialog {

	@Override
	public int layoutId() {
		return R.layout.play_videoleft_dialog;
	}
	private OnClickListener ok;
	public void setOnClickListener(OnClickListener ok){
		this.ok = ok;
	}
	@Override
	protected void setupUI(View view, Bundle bundle) throws Exception {
		view(R.id.dialog_ok).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				ok.onClick(getDialog(), 0);
			}
		});
		this.setCancelable(false);
	}
}
