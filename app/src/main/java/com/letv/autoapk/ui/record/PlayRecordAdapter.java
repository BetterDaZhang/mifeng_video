package com.letv.autoapk.ui.record;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.dao.PlayRecordInfo;
import com.letv.autoapk.ui.player.PlayerAPI;
import com.letv.autoapk.widgets.NetImageView;

/**
 * 
 * @author wangqiangqiang 这个是我的收藏记录和我的播放记录公用的一个adapter 。 这个adapter
 *         可能会影响性能。我暂时记下这个问题。做完别的事情在处理
 * 
 */
class PlayRecordAdapter extends BaseAdapter {
	/**
	 * records 这个list 中，如果有一个元素为空，那么就代表分为一层
	 */
	private List<PlayRecordInfo> records = new LinkedList<PlayRecordInfo>();
	private Context context;
	private LayoutInflater inflater;
	private Holder holder;
	private boolean selectAll = false;
	private PlayRecordInfo currentObj;
	private SimpleDateFormat format = new SimpleDateFormat("mm:ss");
	private SimpleDateFormat dataFormat = new SimpleDateFormat("hh:mm:ss");
	private SimpleDateFormat date = new SimpleDateFormat("MM-dd");
	// private long currentTime;
	private Calendar currentTime;
	/**
	 * 删除列表。保存用户点击每一个Item后的选项。
	 */
	private HashSet<PlayRecordInfo> deleteList;
	private PlayRecordInfo playRecordInfo;

	public PlayRecordAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		float height = context.getResources().getDimension(R.dimen.played_records_item_height) - 2
				* context.getResources().getDimension(R.dimen.played_records_item_rlayout_margin_top_buttom);
		currentTime = Calendar.getInstance();
		currentTime.setTimeInMillis(System.currentTimeMillis());
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
		playRecordInfo = records.get(position);
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.mine_record_item, null);
			holder = new Holder();
			holder.recordsLayout = (RelativeLayout) convertView.findViewById(R.id.rl_played_record);
			holder.videoIcon = (NetImageView) convertView.findViewById(R.id.iv_played_records_video_icon);
			holder.videoName = (TextView) convertView.findViewById(R.id.tv_played_records_video_name);
			holder.videoTime = (TextView) convertView.findViewById(R.id.tv_played_records_time);
			holder.type = (TextView) convertView.findViewById(R.id.tv_date_type);
			holder.type2 =  convertView.findViewById(R.id.ll_date_type);
			holder.nextTv = (RelativeLayout) convertView.findViewById(R.id.rl_next);
			holder.videoIcon.setDefaultImageResId(R.drawable.default_img_16_10);
			holder.videoIcon.setErrorImageResId(R.drawable.default_img_16_10);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.nextTv.setOnClickListener(new NextClickListener(playRecordInfo));
		// 这个判断是对播放记录的节点的判断。如果要显示时间借点。那么相应的要对一部分View进行隐藏
		if (playRecordInfo.isTodayFlag() == true) {
			holder.type.setText(context.getString(R.string.play_today));
			holder.type2.setVisibility(View.VISIBLE);
		}
		if (playRecordInfo.isEarlierFlag() == true) {
			holder.type.setText(context.getString(R.string.play_earlier));
			holder.type2.setVisibility(View.VISIBLE);
		}
		if (!playRecordInfo.isTodayFlag() && !playRecordInfo.isEarlierFlag()) {
			holder.type2.setVisibility(View.GONE);
		}
		// 对于分隔符不能添加点击效果。所以把它的背景变为白色就行了
		// convertView.setBackgroundColor(0xffeeeeee);
		holder.recordsLayout.setVisibility(View.VISIBLE);
		holder.videoIcon.setCoverUrl(records.get(position).getVideoImage(),context);
		// holder.videoIcon.setCornerText(records.get(position).getUnitSubscriptName());
		holder.videoName.setText(records.get(position).getVideoTitle());
		if (!TextUtils.isEmpty(playRecordInfo.getNextLinkUrl())) {
			holder.nextTv.setVisibility(View.VISIBLE);
		} else {
			holder.nextTv.setVisibility(View.GONE);
		}
		int lastPlayTime = (int) playRecordInfo.getLastPlayTime();
		Calendar calendar = Calendar.getInstance();
		calendar.set(0, 0, 0, lastPlayTime / 3600, lastPlayTime % 3600 / 60, lastPlayTime % 3600 % 60);
		if (lastPlayTime > 60 * 60) {
			holder.videoTime.setText(context.getString(R.string.play_lastposition,dataFormat.format(calendar.getTime())));
		} else {
			holder.videoTime.setText(context.getString(R.string.play_lastposition,format.format(calendar.getTime())));
		}
		holder.re = records.get(position);
		return convertView;
	}

	/**
	 * 计算时间差值
	 * 
	 * @param d1
	 *            // 过去的时间点
	 * @param d2
	 *            // 最新的时间点
	 * @return// 返回当前时间差。按天返回
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
		TextView type;
		View type2;
		RelativeLayout recordsLayout;
		NetImageView videoIcon;
		TextView videoName;
		TextView videoTime;
		RelativeLayout nextTv;
		PlayRecordInfo re;
	}

	/**
	 * 向adapte中添加需要的所有的数据。
	 * 
	 * @param records
	 *            这个list 中，如果有一个元素为空，那么就代表分为一层
	 */
	// public void setRecords(List<PlayRecordInfo> records, HashMap<String,
	// List<PlayRecordInfo>> albumMap) {
	public void setRecords(List<PlayRecordInfo> records) {
		if (null == records) {
			return;
		}
		/** 显示今天的标志位 */
		boolean todayFlag = false;
		/** 显示更早的标志位 */
		boolean earlierFlag = false;
		for (int i = 0; i < records.size(); i++) {
			Calendar timer = Calendar.getInstance();
			timer.setTimeInMillis(records.get(i).getLastOpenTime());
			switch (getDaysBetween(timer, currentTime)) {
			case 0:
				if (todayFlag == false) {
					records.get(i).setTodayFlag(true);
					todayFlag = true;
				}
				break;
			default:
				if (earlierFlag == false) {
					records.get(i).setEarlierFlag(true);
					earlierFlag = true;
				}
				break;
			}
		}
		this.records = records;
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

	public void setDelectList(HashSet<PlayRecordInfo> delectList) {
		this.deleteList = delectList;
	}

	class NextClickListener implements OnClickListener {
		PlayRecordInfo playRecordInfo;

		public NextClickListener(PlayRecordInfo playRecordInfo) {
			this.playRecordInfo = playRecordInfo;
		}

		@Override
		public void onClick(View v) {
			DisplayVideoInfo displayVedioInfo = playRecordInfo.getDisplayVedioInfo();
			displayVedioInfo.setNextLinkUrl(playRecordInfo.getNextLinkUrl());
			PlayerAPI.startPlayActivity(context, displayVedioInfo);
		}
	}
}
