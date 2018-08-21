package com.letv.autoapk.base.task;

import java.lang.ref.WeakReference;

import org.xutils.x;
import org.xutils.common.task.AbsTask;
import org.xutils.common.task.Priority;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.support.v4.app.Fragment;

import com.letv.autoapk.common.utils.Logger;

public abstract class UiAsyncTask<Result> extends AbsTask<Result> implements
		OnCancelListener {
	private WeakReference<Fragment> fReference;
	private ProgressDialog dialog;
	private int dialogstringid = -1;
	private boolean dialogcancel = false;

	public UiAsyncTask(Fragment fragment) {
		fReference = new WeakReference<Fragment>(fragment);
	}
	public void execute(){
		x.task().start(this);
	}
	public Priority getPriority() {
        return Priority.UI_NORMAL;
    }
	public UiAsyncTask<Result> showDialog() {
		dialogstringid = 0;
		return this;
	}

	public UiAsyncTask<Result> setDialogMsg(int dialogMsg) {
		dialogstringid = dialogMsg;
		return this;
	}

	public UiAsyncTask<Result> setDialogCancelable(boolean dialogcancel) {
		this.dialogcancel = dialogcancel;
		return this;
	}

	final protected void onWaiting() {
		try {
			final Fragment fragment = fReference.get();
			if (fragment == null)
				return;
			Activity activity = fragment.getActivity();
			if (dialogstringid != -1 && activity != null) {
				dialog = new MyProgressDialog(activity);
				dialog.setCancelable(dialogcancel);
				dialog.setIndeterminate(true);
				dialog.setOnCancelListener(this);
				if (dialogstringid > 0)
					dialog.setMessage(fragment.getText(dialogstringid));
				showProgress(true);
			}
			pre();
		} catch (Exception e) {
			Logger.log(e);
		}
	}

	private boolean isActive() {
		final Fragment fragment = fReference.get();
		if (fragment == null || fragment.getActivity() == null) {
			return false;
		}

		return true;
	}

	protected void showProgress(boolean show) {
		if (dialog != null) {
			if (show) {
				dialog.show();
			} else {
				if (dialog.isShowing())
					dialog.dismiss();
			}
		}
	}

	final protected void onFinished() {
		try {
			showProgress(false);
		} catch (Exception e) {
			Logger.log(e);
		}
	}

	protected void pre() {

	}

	protected void post(Result result) {

	}

	@Override
	final protected void onSuccess(Result result) {
		try {
			if (!isCancelled() && isActive()) {
				post(result);
			}
		} catch (Exception e) {
			Logger.log(e);
		}

	}

	@Override
	protected void onError(Throwable ex, boolean isCallbackError) {
		// TODO Auto-generated method stub
		Logger.log(ex);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		cancel();
	}

}
