package com.letv.autoapk.update;

import org.xutils.x;
import org.xutils.common.task.AbsTask;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

import com.android.volley.Request.Method;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.dialog.BaseDialog;
import com.letv.autoapk.context.MyApplication;

public class UpdateApi {
	public static final String UPDATE = "updateversion";
    public static final String PASSV = "passv";
	public void showNewversionDialog(final String version,
			final BaseActivity mContext, final String url) {
		UpdateDialog d = new UpdateDialog(version);
		d.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent i = new Intent(mContext, DownloadServices.class);
				i.putExtra("url", url);
				i.putExtra("notifyId", 44);
				mContext.startService(i);
			}
		}, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				MyApplication.getInstance().putString(PASSV, version);
			}
		});

		BaseDialog.show(mContext.getSupportFragmentManager(), d);
	}

	public void getUpdateGrage(Context context,
			GetUpgradeListener listener) {
		final GetUpgradeListener upgradeListener = listener;
		final Context mContext = context;
		AbsTask<Integer> task = new AbsTask<Integer>() {
			Object[] result;

			@Override
			protected Integer doBackground() throws Throwable {
				// TODO Auto-generated method stub
				GetUpgradeRequest request = new GetUpgradeRequest(mContext);
				result = new Object[1];
				return request.setOutputData(result).request(Method.GET);
			}

			@Override
			protected void onSuccess(Integer code) {
				// TODO Auto-generated method stub
				if (upgradeListener == null)
					return;
				if (result != null && result.length > 0 && code == 0) {
					VersionInfo info = (VersionInfo) result[0];
					String current = mContext.getString(R.string.app_version);
                    String passv = MyApplication.getInstance().getString(PASSV);
					if (info.flag == 1) {
						if (info.version.equals(current)||info.version.equals(passv))
							upgradeListener.onNothing();
						else
							upgradeListener
									.onNewVersion(info.version, info.url);
					}
				}
			}

			@Override
			protected void onError(Throwable ex, boolean isCallbackError) {
				// TODO Auto-generated method stub

			}

		};
		x.task().start(task);
	}

	public interface GetUpgradeListener {
		void onNewVersion(String version, String url);

		void onNothing();
	}
}
