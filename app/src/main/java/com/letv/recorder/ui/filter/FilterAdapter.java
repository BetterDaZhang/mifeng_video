package com.letv.recorder.ui.filter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.letv.recorder.util.LeLog;
import com.letv.recorder.util.ReUtils;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder>{
	
	private LayoutInflater mInflater;
	private int lastSelected = 0;
	private Context context;
	private List<FilterInfo> filterInfos;
	
	public FilterAdapter(Context context){
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getItemCount() {
		return filterInfos.size();
	}
	
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return filterInfos.get(position).getFilterType();
	}
	
	@SuppressLint("NewApi") @Override
	public void onBindViewHolder(FilterHolder arg0, final int arg1) {
		if(filterInfos.get(arg1).getFilterType() != -1){
			arg0.thumbImage.setImageResource(FilterTypeHelper.FilterType2Thumb(context, filterInfos.get(arg1).getFilterType()));
			if(filterInfos.get(arg1).isSelected()){
				arg0.thumbSelected.setVisibility(View.VISIBLE);
			}else{
				arg0.thumbSelected.setVisibility(View.GONE);
			}
			arg0.filterRoot.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(onFilterChangeListener!= null && filterInfos.get(arg1).getFilterType() != -1 && arg1!= lastSelected&& !filterInfos.get(arg1).isSelected()){
						
						filterInfos.get(lastSelected).setSelected(false);
						filterInfos.get(arg1).setSelected(true);
						notifyItemChanged(lastSelected);
						notifyItemChanged(arg1);
						lastSelected = arg1;		
						onFilterChangeListener.onFilterChanged(filterInfos.get(arg1).getFilterType(), arg1);
					}
				}
			});
		}
	}

	@Override
	public FilterHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		if(arg1 != -1){
			View view = mInflater.inflate(ReUtils.getLayoutId(context, "letv_recorder_filter_item"),
					arg0, false);  
			FilterHolder viewHolder = new FilterHolder(view);
			viewHolder.thumbImage = (ImageView) view
	                .findViewById(ReUtils.getId(context, "filter_thumb_image"));
			viewHolder.filterRoot = (FrameLayout)view
					.findViewById(ReUtils.getId(context, "filter_root"));
			viewHolder.thumbSelected = (FrameLayout) view  
	                .findViewById(ReUtils.getId(context, "filter_thumb_selected"));
			return viewHolder;
		}else{
			View view = mInflater.inflate(ReUtils.getLayoutId(context, "filter_division_layout"),
					arg0, false);
			FilterHolder viewHolder = new FilterHolder(view);
			return viewHolder;
		}
	}
	
	
	public void reSet(){
		filterInfos.get(lastSelected).setSelected(false);
		setLastSelected(0);
		filterInfos.get(0).setSelected(true);
	}
	public void setLastSelected(int arg){
		lastSelected = arg;
	}
	
	public int getLastSelected(){
		return lastSelected;
	}
	
	public void setFilterInfos(List<FilterInfo> filterInfos){
		this.filterInfos = filterInfos;		
		notifyDataSetChanged();
	}
	
	class FilterHolder extends ViewHolder{		
		ImageView thumbImage;
		FrameLayout thumbSelected;
		FrameLayout filterRoot;
		
		public FilterHolder(View itemView) {
			super(itemView);
		}
	}
	
	public interface onFilterChangeListener{
		void onFilterChanged(int filterType, int position);
	}
	
	private onFilterChangeListener onFilterChangeListener;
	
	public void setOnFilterChangeListener(onFilterChangeListener onFilterChangeListener){
		this.onFilterChangeListener = onFilterChangeListener;
	}
}
