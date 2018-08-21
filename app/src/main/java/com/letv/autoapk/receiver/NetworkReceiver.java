package com.letv.autoapk.receiver;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.lecloud.sdk.download.info.LeDownloadInfo;
import com.lecloud.sdk.download.observer.LeDownloadObserver;
import com.letv.autoapk.R;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.download.DownloadSaasCenter;
import com.letv.autoapk.utils.NetworkUtils;

public class NetworkReceiver extends BroadcastReceiver {
	private boolean DownloadingFlag;
	public List<NetWorkChange> changes = new ArrayList<NetWorkChange>();
	public DownloadSaasCenter downloadCenter;
	@Override
	public void onReceive(Context context, Intent intent) {
		downloadCenter = DownloadSaasCenter.getInstances(context.getApplicationContext());
		downloadCenter.allowShowMsg(false);
		boolean wifiState = false;
		DownloadingFlag =false;
		String netType = NetworkUtils.getNetType(context);
		if (!NetworkUtils.Type_WIFI.equals(netType)) {
			List<LeDownloadInfo> leDownloadInfos = downloadCenter.getDownloadInfoList();
			if (leDownloadInfos != null) {
				for (LeDownloadInfo info :leDownloadInfos) {
					if (info.getDownloadState() == LeDownloadObserver.DOWLOAD_STATE_DOWNLOADING 
							|| info.getDownloadState() == LeDownloadObserver.DOWLOAD_STATE_WAITING){
							DownloadingFlag = true;
						}
					downloadCenter.stopAllDownload();
					downloadCenter.backupDownloadInfoList();
				}
			}
			
			if (DownloadingFlag) {
				Toast.makeText(context, R.string.download_netchange,Toast.LENGTH_SHORT).show();
			}
			wifiState = false;
		} else if (NetworkUtils.Type_WIFI.equals(netType)) {
			MyApplication.getInstance().putBoolean(MyApplication.NOT_ONLYWIFI, false);
			wifiState = true;
		}
		
		for (NetWorkChange change : changes) {
			try{
				change.netWorkChange(wifiState);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public interface NetWorkChange {
		public void netWorkChange(boolean wifi);
	}
}
