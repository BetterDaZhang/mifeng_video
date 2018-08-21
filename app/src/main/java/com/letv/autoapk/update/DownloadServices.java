package com.letv.autoapk.update;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.letv.autoapk.R;
import com.letv.autoapk.ui.main.MainActivity;
import com.letv.autoapk.update.DownloadUtil.IOnDownloadListener;

public class DownloadServices extends Service
{
	private Context mContext = DownloadServices.this;
	/** 正在下载 */
	private final int DOWN_LOADING = 0;
	/** 下载完成 */
	private final int DOWN_COMPLETE = 1;
	/** 下载失败 */
	private final int DOWN_ERR = 2;
	/** Timer 执行时间间隔 */
	private final int TIMER_PERIOD = 1500;

	protected Timer mTimer;
	protected NotificationManager mNotificationManager;
	private NotificationCompat.Builder mBuilder;
	/** 下载任务管理 */
	protected Map<String, DownloadTask> map_downloadtask;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public void onCreate()
	{
		// TODO Auto-generated method stub
		super.onCreate();
		mTimer = new Timer();
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(this);
		map_downloadtask = new HashMap<String, DownloadTask>();
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		String mUrl = intent.getExtras().getString("url");
		int mNotifyId = intent.getExtras().getInt("notifyId");
		Intent pintent = new Intent(mContext, MainActivity.class); // 点击安装APK 未实现
		pintent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, pintent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentTitle(getString(R.string.app_name))
        .setWhen(System.currentTimeMillis())
        .setContentText(getString(R.string.downloading))
        .setTicker(getString(R.string.downloadupadte))
        .setSmallIcon(R.drawable.icon)
        .setContentIntent(pIntent)
        .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon))
		.setProgress(100, 0, false).setContentInfo(0 + "%");
		if(map_downloadtask.containsKey(mUrl)){
			Toast.makeText(mContext, getString(R.string.alreadyinorder), Toast.LENGTH_SHORT).show();
		}
		else{
			DownloadTask mDownloadTask = new DownloadTask();
			mDownloadTask.setUrl(mUrl);
			mDownloadTask.setNotifyID(mNotifyId);
			Notification notification = mBuilder.build();
			mDownloadTask.setNotification(notification);
			map_downloadtask.put(mUrl, mDownloadTask);
			mNotificationManager.notify(mNotifyId, notification);
			Runnable mRunnable = new MyRunnable(mDownloadTask);
			new Thread(mRunnable).start();
		}

		return super.onStartCommand(intent, flags, startId);
	}


	
	class MyRunnable implements Runnable
	{
		private DownloadUtil mDownUtil = new DownloadUtil();
		private DownloadTask mDownTask;
		private Handler mHandler;
		private TimerTask mTimerTask;

		public MyRunnable(DownloadTask downTask)
        {
	        super();
	        this.mDownTask = downTask;
	        this.mHandler = new MyHandler(mDownUtil);
	        this.mTimerTask = new MyTimerTask(mDownUtil, mHandler, mDownTask);
        }

		@Override
		public void run()
		{
			mTimer.schedule(mTimerTask, 0, TIMER_PERIOD);
			mDownUtil.downloadFile(mDownTask.getUrl());
		}

	}

	
	class MyTimerTask extends TimerTask
	{
		private Handler mHandler;
		private DownloadUtil mDownUtil;
		private DownloadTask mDownTask;
		private IOnDownloadListener mListener;
		
		public MyTimerTask(DownloadUtil downUtil, Handler handler, DownloadTask downTask)
        {
	        super();
	        this.mHandler = handler;
	        this.mDownUtil = downUtil;
	        this.mDownTask = downTask;
	        this.mListener = new IOnDownloadListener()
			{
				@Override
				public void updateNotification(int progress, int totalSize, File downFile)
				{
					// TODO Auto-generated method stub
					int rate = 0;
					// 计算百分比
					if (totalSize > 0)
					{
						rate = progress * 100 / totalSize;
						mHandler.obtainMessage(DOWN_LOADING, rate, mDownTask.getNotifyID(), mDownTask.getNotification()).sendToTarget();
					}
					else if (totalSize == 0)
					{
						mHandler.obtainMessage(DOWN_LOADING, 0, mDownTask.getNotifyID(), mDownTask.getNotification()).sendToTarget();
					}
					else {
						cancel();
						mHandler.obtainMessage(DOWN_ERR, 0,mDownTask.getNotifyID(),mDownTask).sendToTarget();
					}
					// 是否下载结束
					if (totalSize > 0 && null != downFile && totalSize == (int) downFile.length())
					{
						cancel();
						mHandler.obtainMessage(DOWN_COMPLETE,0,mDownTask.getNotifyID(), downFile).sendToTarget();
						map_downloadtask.remove(mDownTask.getUrl());// 移除已完成任务
						System.out.println("DOWN_COMPLETE ==> totalSize ==> " + totalSize);
					}
				}

			};
        }

		@Override
		public void run()
		{
			mDownUtil.setOnDownloadListener(mListener);
		}
	}
	

	class MyHandler extends Handler
	{
		private DownloadUtil mDownUtil;

		public MyHandler(DownloadUtil downUtil)
        {
	        super();
	        this.mDownUtil = downUtil;
        }

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case DOWN_LOADING:
				mBuilder.setProgress(100, msg.arg1, false).setContentInfo(msg.arg1+ "% ");
				mNotificationManager.notify(msg.arg2, mBuilder.build());
				break;
			case DOWN_COMPLETE:
				mNotificationManager.cancel(msg.arg2);
				removeMessages(DOWN_LOADING);
				Toast.makeText(mContext, getString(R.string.downloadok), Toast.LENGTH_SHORT).show();
				mDownUtil.installApk(mContext, (File)msg.obj);
				stopService();
				break;
			case DOWN_ERR:
				removeMessages(DOWN_LOADING);
				mNotificationManager.cancel(msg.arg2);
				map_downloadtask.remove(((DownloadTask)msg.obj).getUrl());
				Toast.makeText(mContext, getString(R.string.downlaodfailed), Toast.LENGTH_SHORT).show();
				stopService();
				break;
			default:
				break;
			}
		}
		
		/**
		 * 如果无下载任务，关闭服务
		 */
		private void stopService(){
			if(map_downloadtask.isEmpty()){
				stopSelf(-1);
			}
		}

	}

}
