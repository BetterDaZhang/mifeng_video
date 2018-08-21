package com.letv.autoapk.boss;

import java.util.ArrayList;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.base.fragment.BaseFragment;
import com.letv.autoapk.context.MyApplication;

public class ConsumeRecordsAdapter extends BaseAdapter implements
		OnClickListener {
	private ArrayList<ConsumeInfo> list;
	private BaseActivity activity;
	private LayoutInflater inflater;
    private BaseFragment currentFragment;
	public ConsumeRecordsAdapter(BaseActivity mActivity,
			ArrayList<ConsumeInfo> list,BaseFragment fragment) {
		this.activity = mActivity;
		inflater = LayoutInflater.from(mActivity);
		this.list = list;
		currentFragment = fragment;

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public ConsumeInfo getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	protected View getMyView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		ConsumeInfo info = getItem(position);
		if (convertView == null) {
			holder = new Holder();
			if(info.status==2){
				convertView = inflater.inflate(
						R.layout.boss_recorditem, null);
				holder.date = (TextView) convertView.findViewById(R.id.date);
			}else{
				convertView = inflater.inflate(
						R.layout.boss_recorditem_failed, null);
			}
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.desc = (TextView) convertView.findViewById(R.id.desc);
			holder.number = (TextView) convertView.findViewById(R.id.number);
			holder.datetime = (TextView) convertView
					.findViewById(R.id.datetime);
			holder.status = (TextView) convertView.findViewById(R.id.status);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		Object object = holder.name.getTag();
		if (object == null || !object.equals(info.number)) {
			holder.name.setTag(info.number);
			holder.name.setText(info.name);
			if(info.status == 2){
				holder.number.setText(activity.getString(R.string.orderid,info.number));
				holder.date.setText(activity.getString(R.string.end_date, info.date));
				holder.desc.setText(activity.getString(R.string.paynum,info.desc));
				holder.datetime.setText(activity.getString(R.string.acttime,info.datetime));
				holder.status.setText(getStatusString(info.status));
			}else{
				holder.number.setText(activity.getString(R.string.paynum,info.desc));
				holder.desc.setText(R.string.continuepay);
				holder.desc.setTag(info);
				holder.desc.setOnClickListener(this);
				holder.datetime.setText(activity.getString(R.string.orderid,info.number));
				holder.status.setText(getStatusString(info.status));
			}
		}
		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		ConsumeInfo info = getItem(position);
		if(info.status==2){
			return 0;
		}
		return 1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	private String getStatusString(int status) {
		switch (status) {
		case 1:

			return activity.getString(R.string.paystatus,activity.getString(R.string.pay_faild));
		case 2:

			return activity.getString(R.string.paystatus,activity.getString(R.string.pay_ok));
		case 3:

			return activity.getString(R.string.paystatus,activity.getString(R.string.pay_faild));
		default:
			break;
		}
		return activity.getString(R.string.paystatus,activity.getString(R.string.pay_all));
		
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.desc){
			ConsumeInfo info = (ConsumeInfo)v.getTag();
			MyApplication.getInstance().putInt(LepayManager.PAY_FOR_WHAT, LepayManager.PAY_CONSUMERECORD);
			LepayManager lepayManager = LepayManager.getInstance(activity, currentFragment);
			lepayManager.albumOrder(info.lepayInfo);
		}

	}

}

class Holder {
	TextView name;
	TextView date;
	TextView desc;
	TextView number;
	TextView datetime;
	TextView status;
}