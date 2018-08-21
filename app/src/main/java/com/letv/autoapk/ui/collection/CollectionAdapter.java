package com.letv.autoapk.ui.collection;

import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.dao.CollectionRecordInfo;
import com.letv.autoapk.dao.CollectionRecordInfo.Flag;
import com.letv.autoapk.widgets.NetImageView;

/**
 * 
 */
class CollectionAdapter<T> extends BaseAdapter {
	private List<CollectionRecordInfo> records = new LinkedList<CollectionRecordInfo>();
	private Context context;
	private LayoutInflater inflater;
	private Holder holder;
	private CollectionRecordInfo.Flag status = Flag.NORMAL;
	private boolean selectAll = false;
	private CollectionRecordInfo currentObj;
	LayoutParams params;
	/**
	 * 删除列表。保存用户点击每一个Item后的选项。
	 */
	private HashSet<CollectionRecordInfo> delectList;

	public CollectionAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return records.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.mine_collection_item, null);
			holder = new Holder();
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
			holder.recordsLayout = (RelativeLayout) convertView.findViewById(R.id.rl_played_record);
			holder.videoIcon = (NetImageView) convertView.findViewById(R.id.iv_played_records_video_icon);
			holder.videoName = (TextView) convertView.findViewById(R.id.tv_played_records_video_name);
			holder.videoTime = (TextView) convertView.findViewById(R.id.tv_played_records_time);
			holder.videoIcon.setDefaultImageResId(R.drawable.default_img_16_10);
			holder.videoIcon.setErrorImageResId(R.drawable.default_img_16_10);
			// holder.videoIcon.setLayoutParams(imageParams);
			convertView.setTag(holder);
		}
		holder = (Holder) convertView.getTag();
		holder.videoTime.setVisibility(View.GONE);
		// 这个是Item 模式的判断。判断当前是删除模式还是正常模式。对于不同的模式。显示不同的界面
		if (status.equals(Flag.DELETE)) {
			holder.checkBox.setVisibility(View.VISIBLE);
			// 一种BUG的修复。在全选和之前选中的时候。当加载ITem的时候，自动变为选中状态
			if (selectAll || (delectList != null && delectList.contains(records.get(position)))) {
				holder.checkBox.setChecked(true);
			} else {
				holder.checkBox.setChecked(false);
			}
		} else {
			holder.checkBox.setVisibility(View.GONE);
		}
//		params = new LayoutParams(LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.played_records_item_height));
		holder.recordsLayout.setVisibility(View.VISIBLE);
		holder.videoIcon.setCoverUrl((records.get(position)).getVideoImage(), context);
		// holder.videoIcon.setCornerText(records.get(position).getUnitSubscriptName());
		holder.videoName.setText((records.get(position)).getVideoTitle());
		holder.videoTime.setText(context.getResources().getString(R.string.mine_play_with_space) + (records.get(position)).getNumber()
				+ context.getResources().getString(R.string.mine_times));

		holder.re = records.get(position);
//		convertView.setLayoutParams(params);
		return convertView;
	}

	/**
	 * 计算时间差值
	 * 
	 * @param d1
	 *            // 过去的时间点
	 * @param d2
	 *            // 最新的时间点 @return// 返回当前时间差。按天返回
	 */
	public int getDaysBetween(Calendar d1, Calendar d2) {
		int days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
		int y2 = d2.get(Calendar.YEAR);
		if (d1.get(Calendar.YEAR) != y2) {
			d1 = (Calendar) d1.clone();
			do {
				days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);// 得到当年的实际天数
				d1.add(Calendar.YEAR, 1);
			} while (d1.get(Calendar.YEAR) != y2);
		}
		return days;
	}

	public class Holder {
		public CheckBox checkBox;
		RelativeLayout recordsLayout;
		NetImageView videoIcon;
		TextView videoName;
		TextView videoTime;
		public CollectionRecordInfo re;
	}

	/**
	 * 向adapte中添加需要的所有的数据。
	 * 
	 * @param records
	 *            这个list 中，如果有一个元素为空，那么就代表分为一层
	 */
	public void setRecords(List<CollectionRecordInfo> records) {
		if (null == records) {
			return;
		}
		this.records = (List<CollectionRecordInfo>) ((LinkedList<CollectionRecordInfo>) records).clone();
		if (records.size() > 0) {
			currentObj = new CollectionRecordInfo();
		}
		this.notifyDataSetChanged();
	}

	/**
	 * 设置选中状态。是不是全部选中
	 * 
	 * @param status
	 */
	public void setSelectAll(boolean status) {
		selectAll = status;
		this.notifyDataSetChanged();
	}

	public boolean getSelectAll() {
		return selectAll;
	}

	public void setFlag(Flag status) {
		this.status = status;
		this.notifyDataSetChanged();
	}

	public void setDelectList(HashSet<CollectionRecordInfo> delectList) {
		this.delectList = delectList;
	}
}
