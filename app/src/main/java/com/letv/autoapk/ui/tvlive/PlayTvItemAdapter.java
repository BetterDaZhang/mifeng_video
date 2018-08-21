package com.letv.autoapk.ui.tvlive;

import java.nio.charset.CodingErrorAction;
import java.util.Date;
import java.util.List;

import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.utils.DateUtils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PlayTvItemAdapter extends BaseAdapter {
	public static final int TYPE_DATE = 101;
	public static final int TYPE_PROGREM = 102;
	public static final int TYPE_DATE_TYPE = 0;
	public static final int TYPE_PROGREM_TYPE = 1;
	public static final int TYPE_MAX_COUNT = TYPE_PROGREM_TYPE + 1;
	private Context context;
	private List<PlayTvItemInfo> list;
	private BaseActivity mActivity;
	private long positionTime;

	public PlayTvItemAdapter(Context ctx, List<PlayTvItemInfo> list, BaseActivity activity) {
		this.context = ctx;
		this.list = list;
		this.mActivity = activity;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public int getViewTypeCount() {

		return TYPE_MAX_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		if (list.get(position).getType() == TYPE_DATE) {
			return TYPE_DATE_TYPE;
		} else if (list.get(position).getType() == TYPE_PROGREM) {
			return TYPE_PROGREM_TYPE;
		}
		return TYPE_DATE;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	protected View getMyView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			if (getItemViewType(position) == TYPE_DATE_TYPE) {
				convertView = LayoutInflater.from(context).inflate(R.layout.play_detail_tv_item_date, parent, false);
				DateHolder dateHolder = new DateHolder(convertView);
				convertView.setTag(dateHolder);
			} else if (getItemViewType(position) == TYPE_PROGREM_TYPE) {
				convertView = LayoutInflater.from(context).inflate(R.layout.play_detail_tv_item_pro, parent, false);
				ProgramHolder programHolder = new ProgramHolder(convertView);
				convertView.setTag(programHolder);
			}
		}
		Object tag = convertView.getTag();
		if (getItemViewType(position) == TYPE_DATE_TYPE) {
			if (tag instanceof DateHolder) {
				DateHolder dateHolder = (DateHolder) tag;
				dateHolder.setDate(list.get(position));
			} else {
				Toast.makeText(context, "tv date convertiew failed", Toast.LENGTH_LONG).show();
			}
		} else if (getItemViewType(position) == TYPE_PROGREM_TYPE) {
			if (tag instanceof ProgramHolder) {
				ProgramHolder programHolder = (ProgramHolder) tag;
				programHolder.loadingData(list.get(position));
			} else {
				Toast.makeText(context, "tv program convertiew failed", Toast.LENGTH_LONG).show();
			}
		}
		return convertView;
	}

	class DateHolder {
		private TextView date;

		public DateHolder(View view) {
			date = (TextView) view.findViewById(R.id.date_tx);
		}

		public void setDate(PlayTvItemInfo info) {
			date.setText(info.getDateTime());
		}
	}

	class ProgramHolder {
		private TextView dateTx;
		private TextView titleTx;
		private TextView statusTx;
		private ImageView liveFlagImg;
		private View view;

		public ProgramHolder(View view) {
			this.view = view;
			dateTx = (TextView) view.findViewById(R.id.date_tx);
			titleTx = (TextView) view.findViewById(R.id.title_tx);
			statusTx = (TextView) view.findViewById(R.id.status_tx);
			liveFlagImg = (ImageView) view.findViewById(R.id.play_tv_tag);
		}

		void loadingData(final PlayTvItemInfo info) {
			Date date = DateUtils.getDateFromGreenwichSec(info.getBeginTime());
			dateTx.setText(DateUtils.formatTime(date));
			titleTx.setText(info.getTvItemTitle());
			// 修改播放状态
			Drawable drawable;
			if (info.getPlayState() == 0) {// 未开始
				statusTx.setCompoundDrawables(null, null, null, null);
				statusTx.setText(context.getResources().getString(R.string.play_tvlive_no));
				statusTx.setTextColor(context.getResources().getColor(R.color.code4));
				dateTx.setTextColor(context.getResources().getColor(R.color.code4));
				titleTx.setTextColor(context.getResources().getColor(R.color.code4));
				liveFlagImg.setVisibility(View.GONE);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.ALIGN_PARENT_START);
				params.addRule(RelativeLayout.CENTER_VERTICAL);
				dateTx.setLayoutParams(params);
			} else if (info.getPlayState() == 1) {// 正在直播
				drawable = context.getResources().getDrawable(R.drawable.play_tvlive_live);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				statusTx.setCompoundDrawables(null, drawable, null, null);
				statusTx.setText(context.getResources().getString(R.string.play_tvlive_live));
				statusTx.setTextColor(context.getResources().getColor(R.color.code1));
				dateTx.setTextColor(context.getResources().getColor(R.color.code1));
				titleTx.setTextColor(context.getResources().getColor(R.color.code1));
				liveFlagImg.setVisibility(View.VISIBLE);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				params.setMarginStart(mActivity.dip2px(10));
				params.addRule(RelativeLayout.RIGHT_OF, liveFlagImg.getId());
				params.addRule(RelativeLayout.CENTER_VERTICAL);
				dateTx.setLayoutParams(params);
			} else if (info.getPlayState() == 2) {// 回看
				drawable = context.getResources().getDrawable(R.drawable.play_tv_back);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				statusTx.setCompoundDrawables(null, drawable, null, null);
				statusTx.setText(context.getResources().getString(R.string.play_tvlive_back));
				statusTx.setTextColor(context.getResources().getColor(R.color.code4));
				dateTx.setTextColor(context.getResources().getColor(R.color.code4));
				titleTx.setTextColor(context.getResources().getColor(R.color.code4));
				liveFlagImg.setVisibility(View.GONE);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.ALIGN_PARENT_START);
				params.addRule(RelativeLayout.CENTER_VERTICAL);
				dateTx.setLayoutParams(params);
			}

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (positionTime >= info.getBeginTime() && positionTime <= info.getEndTime()) {
						return;
					}
					if (info.getPlayState() == 2 || info.getPlayState() == 1) {
						Message msg = mActivity.getDefaultHandler().obtainMessage();
						msg.what = PlayTVConst.TIMESHIFT;
						msg.obj = info;
						Bundle bundle = new Bundle();
						msg.setData(bundle);
						msg.sendToTarget();
					}
				}
			});
		}
	}

	public void setPositionTime(long time) {
		positionTime = time;
	}

}
