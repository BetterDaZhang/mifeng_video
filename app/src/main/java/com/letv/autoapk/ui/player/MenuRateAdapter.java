package com.letv.autoapk.ui.player;

import java.util.List;

import org.xutils.common.util.DensityUtil;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.adapter.BaseAdapter;

class MenuRateAdapter extends BaseAdapter {

	private List<RateTypeItem> list;
	private Context context;
	private String current;

	MenuRateAdapter(List<RateTypeItem> list, Context context, String type) {
		this.list = list;
		this.context = context;
		current = type;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list==null?0:list.size();
	}

	@Override
	public RateTypeItem getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	protected View getMyView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			TextView textView = new TextView(context);
			textView.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			textView.setTextColor(context.getResources()
					.getColor(R.color.code7));
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			textView.setPadding(0, DensityUtil.dip2px(15), 0, DensityUtil.dip2px(15));
			textView.setGravity(Gravity.CENTER);
			view = textView;
		} 
		
		RateTypeItem rateTypeItem = getItem(position);
		view.setTag(rateTypeItem.getTypeId());
		((TextView)view).setText(rateTypeItem.getName());
		if(current.equals(rateTypeItem.getTypeId()) ){
			((TextView)view).setTextColor(context.getResources().getColor(R.color.code1));
		}else{
			((TextView)view).setTextColor(context.getResources().getColor(R.color.code7));
		}
		return view;
	}

}
