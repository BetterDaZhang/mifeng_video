package com.letv.autoapk.boss;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public class BossPagerAdapter extends FragmentPagerAdapter {
	protected SparseArray<String> names;
	protected Context context;
	protected String[] titles;
	private List<List<PackageInfo>> list;

	public BossPagerAdapter(FragmentManager fm, SparseArray<String> names,
			Context context, String[] titles, List<List<PackageInfo>> list) {
		super(fm);
		this.names = names;
		this.context = context;
		this.titles = titles;
		this.list = list;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = Fragment.instantiate(context, names.get(position));
		if (fragment instanceof MemberMobileFragment) {
			((MemberMobileFragment) fragment).updateData(list.get(position));
		}
		return fragment;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return names.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		return titles[position];
	}

}
