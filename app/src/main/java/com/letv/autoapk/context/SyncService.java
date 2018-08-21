package com.letv.autoapk.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xutils.x;
import org.xutils.common.task.AbsTask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.letv.autoapk.base.net.StringDataRequest;

public class SyncService extends Service {

	private static final String ACTION_BOSS = "com.lecloud.app.boss";
	private static final String ACTION_REPEAT = "com.lecloud.app.repeat";

	private static final long KEEP_DELAY_INTERVAL = 1000 * 60;

	public static void actionBoss(Context ctx) {
		Intent i = new Intent(ctx, SyncService.class);
		i.setAction(ACTION_BOSS);
		ctx.startService(i);
	}

	private ArrayList<String> actions;

	@Override
	public void onCreate() {
		super.onCreate();
		actions = new ArrayList<String>();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null || intent.getAction() == null) {
			return super.onStartCommand(intent, flags, startId);
		}
		String action = intent.getAction();
		if (!action.equals(ACTION_REPEAT) && !actions.contains(action)) {
			actions.add(action);
		}
		processAction();
		return START_STICKY;
	}

	private GetBossStateTask getBossStateTask;

	private void processAction() {
		synchronized (actions) {
			for (String action : actions) {
				if (action.equals(ACTION_BOSS)) {
					if (getBossStateTask != null) {
						getBossStateTask.cancel();
						getBossStateTask = null;
					}
					getBossStateTask = new GetBossStateTask();
					x.task().start(getBossStateTask);
				}
			}
		}
	}

	private void keepSync() {
		Intent i = new Intent();
		i.setClass(this, SyncService.class);
		i.setAction(ACTION_REPEAT);
		PendingIntent pi = PendingIntent.getService(this, 10, i, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		if (actions.size() < 1) {
			alarmMgr.cancel(pi);
			stopSelf();
		} else {
			alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + KEEP_DELAY_INTERVAL, pi);
		}

	}

	class GetBossStateTask extends AbsTask<Integer> {
        private GetBossStateRequest request;
		@Override
		protected Integer doBackground() throws Throwable {
			request = new GetBossStateRequest(getApplicationContext());
			Map<String, String> mInputParam = new HashMap<String, String>();
			mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
			int code = request.setInputParam(mInputParam).request(Request.Method.GET);
			return code;
		}

		@Override
		protected void onSuccess(Integer result) {
			if (result != null && result == 0) {
				actions.remove(ACTION_BOSS);
				MyApplication.getInstance().setIsNeedBoss(request==null?0:request.getBossState());
			}
			keepSync();
		}

		@Override
		protected void onError(Throwable ex, boolean isCallbackError) {
			keepSync();
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
