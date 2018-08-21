package com.letv.autoapk.ui.main;

import android.content.Context;
import android.content.Intent;

public class MainAPI {

	public static Intent getMainIntent(Context context){
		return new Intent(context, MainActivity.class);
	}
}
