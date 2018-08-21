package com.letv.autoapk.ui.offline;

import com.letv.autoapk.base.activity.ContainerActivity;

import android.content.Context;
import android.content.Intent;

public class OfflineAPI {
	public static void startOfflineFragment(Context context){
		Intent intent = new Intent(context, ContainerActivity.class);
		intent.putExtra(ContainerActivity.FRAGMENTNAME, OfflineFragment.class.getName());
		context.startActivity(intent);
	}
}
