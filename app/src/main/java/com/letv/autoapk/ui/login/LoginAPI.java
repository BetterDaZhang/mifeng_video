package com.letv.autoapk.ui.login;

import java.text.ParseException;

import android.content.Context;
import android.content.Intent;

import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.base.activity.DetailActivity;
import com.letv.autoapk.boss.BossLoginAPI;
import com.letv.autoapk.boss.BossLoginFragment;
import com.letv.autoapk.boss.BossManager;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.utils.SystemUtls;

public class LoginAPI {

	/**
	 * 
	 * @param context
	 * @param loginAim
	 *            登录目的
	 */
	public static void stratLogin(Context context) {
		Intent intent;
		if (context instanceof DetailActivity) {
			intent = new Intent(context, ContainerActivity.class);
			intent.putExtra(ContainerActivity.FRAGMENTNAME, LoginFragment.class.getName());
		} else {
			intent = new Intent(context, DetailActivity.class);
			intent.putExtra(DetailActivity.FRAGMENTNAME, LoginFragment.class.getName());
		}
		context.startActivity(intent);
	}

	public static void stratChangeName(Context context, String selectSex, String lastName, String selectBirthday) {
		Intent newNameIntent = new Intent(context, DetailActivity.class);
		newNameIntent.putExtra(DetailActivity.FRAGMENTNAME, ChangeNameFragment.class.getName());
		String male = context.getString(R.string.mine_male);
		String female = context.getString(R.string.mine_female);
		String selectSexInt = (male.equals(selectSex) ? "0" : (female.equals(selectSex) ? "1" : "2"));
		long birth = 01;
		if (selectBirthday == "") {// 如果从生日框中得到的数据为空
			birth = System.currentTimeMillis() / 1000 - 630720000;
		} else {
			try {
				birth = SystemUtls.Strdate2Long(selectBirthday, context.getString(R.string.yyyy_mm_dd_));
			} catch (ParseException e1) {
				Logger.log(e1);
				birth = 0l;
			}
		}
		newNameIntent.putExtra("sex", selectSexInt);
		newNameIntent.putExtra("birthday", birth);
		newNameIntent.putExtra("lastName", lastName);
		context.startActivity(newNameIntent);
	}
}
