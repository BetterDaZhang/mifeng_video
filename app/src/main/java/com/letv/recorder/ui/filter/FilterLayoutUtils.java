package com.letv.recorder.ui.filter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.letv.autoapk.R;
import com.letv.recorder.controller.Publisher;
import com.letv.recorder.controller.VideoRecordDevice;
import com.letv.recorder.ui.SurfaceFrameLayout;
import com.letv.recorder.util.LeLog;
import com.letv.recorder.util.ReUtils;

import java.util.ArrayList;
import java.util.List;

public class FilterLayoutUtils{
	private Context mContext;
	private VideoRecordDevice videoRecorderDevice;
	private FilterAdapter mAdapter;

	private int position;
	private List<FilterInfo> filterInfos;
	private List<FilterInfo> favouriteFilterInfos;
	
	private int mFilterType = MagicFilterType.NONE;
	private FilterShowListener listener;
	
	public FilterLayoutUtils(Context context,VideoRecordDevice filterSwticher,FilterShowListener listener) {
		this.listener = listener;
		mContext = context;	
		videoRecorderDevice = filterSwticher;
	}

	public void init(RelativeLayout mFilterLayou,SurfaceFrameLayout frameLayout){
		mFilterLayou.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		frameLayout.setListener(new SurfaceFrameLayout.OnListener() {
			
			@Override
			public void onTouch() {
				if(!listener.isShowFilter())return;
				listener.hideFilter();
			}
		});
		
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);  
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		RecyclerView mFilterListView =  (RecyclerView)(mFilterLayou.findViewById(R.id.filter_listView));
		mFilterListView.setLayoutManager(linearLayoutManager);

		ImageView btnCancel =  (ImageView)(mFilterLayou.findViewById(R.id.btn_filter_cancle));
		btnCancel.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(videoRecorderDevice != null)
					videoRecorderDevice.setFilterModel(MagicFilterType.NONE);
				mAdapter.reSet();
				mAdapter.notifyDataSetChanged();
				listener.hideFilter();
			}
		});
		ImageView btnSave =  (ImageView)(mFilterLayou.findViewById(R.id.btn_filter_save));
		btnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.hideFilter();
			}
		});

        mAdapter = new FilterAdapter(mContext);
		mFilterListView.setAdapter(mAdapter);
        initFilterInfos();
        mAdapter.setFilterInfos(filterInfos);
        mAdapter.setOnFilterChangeListener(onFilterChangeListener);          
	}
	
	public void init(View view){
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);  
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView mFilterListView =  (RecyclerView)((Activity) mContext).findViewById(ReUtils.getId(mContext, "filter_listView"));
        mFilterListView.setLayoutManager(linearLayoutManager);       
        
        mAdapter = new FilterAdapter(mContext);
        mFilterListView.setAdapter(mAdapter);
        initFilterInfos();
        mAdapter.setFilterInfos(filterInfos);
        mAdapter.setOnFilterChangeListener(onFilterChangeListener);

        view.findViewById(ReUtils.getId(mContext, "btn_camera_closefilter")).setVisibility(View.GONE);
	}
	
	
	private FilterAdapter.onFilterChangeListener onFilterChangeListener = new FilterAdapter.onFilterChangeListener(){

		@Override
		public void onFilterChanged(int filterType, int position) {
			// TODO Auto-generated method stub
			int Type = filterInfos.get(position).getFilterType();//锟斤拷取锟斤拷锟斤拷
			FilterLayoutUtils.this.position = position;
			if(videoRecorderDevice==null){
				LeLog.d("切换滤镜，获取VideoRecorderDevice对象");
				videoRecorderDevice=Publisher.getInstance().getVideoRecordDevice();
			}
//			mFilterSwticher.switchFilter(getFilterType(filterType), 100);
			if(videoRecorderDevice != null) {
				videoRecorderDevice.setFilterModel(filterType);
			}
			mFilterType = filterType;
			for(int i = 1; i < filterInfos.size(); i++){
				if(filterInfos.get(i).getFilterType() == Type){
					filterInfos.get(i).setSelected(true);
					mAdapter.notifyItemChanged(i);
				}else if(filterInfos.get(i).isSelected()){
					filterInfos.get(i).setSelected(false);
					mAdapter.notifyItemChanged(i);
				}
			}
		}
		
	};
	
	private void initFilterInfos(){
		filterInfos = new ArrayList<FilterInfo>();
		//add original
		FilterInfo filterInfo = new FilterInfo();
		filterInfo.setFilterType(MagicFilterType.NONE);
		filterInfo.setSelected(true);
		filterInfos.add(filterInfo);
		
		//addAll
		for(int i = 1;i < MagicFilterType.FILTER_COUNT+1; i++){
			filterInfo = new FilterInfo();
			filterInfo.setFilterType(MagicFilterType.NONE + i);
			filterInfos.add(filterInfo);
		}
	}
}
