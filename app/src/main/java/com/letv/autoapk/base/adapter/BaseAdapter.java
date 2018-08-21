package com.letv.autoapk.base.adapter;

import com.letv.autoapk.common.utils.Logger;

import android.view.View;
import android.view.ViewGroup;


public abstract class BaseAdapter extends android.widget.BaseAdapter {


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		try {
			view = getMyView(position, convertView, parent);
		} catch (Exception e) {
			Logger.log(e);
		}catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
		}
		return view;
	}

	protected abstract View getMyView(int position, View convertView, ViewGroup parent);

}
