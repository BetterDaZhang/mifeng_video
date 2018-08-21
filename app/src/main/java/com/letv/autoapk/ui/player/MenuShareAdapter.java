package com.letv.autoapk.ui.player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.letv.autoapk.R;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.open.OpenSdk;
import com.letv.autoapk.open.OpenShareActivity;

public class MenuShareAdapter extends BaseAdapter implements OnClickListener {
	private Context context;
    private DisplayVideoInfo info;
    private OpenSdk openSdk;
	MenuShareAdapter(Context context,DisplayVideoInfo info) {
		this.context = context;
		this.info = info;
		openSdk = MyApplication.getInstance().getOpenSdk();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	protected View getMyView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.play_menushare, null);
			GridLayout gridLayout = (GridLayout)convertView.findViewById(R.id.rl_share_imgs);
			if (openSdk.hasMM()) {
				gridLayout.findViewById(R.id.menu_mm).setOnClickListener(this);
				gridLayout.findViewById(R.id.menu_mm).setVisibility(View.VISIBLE);
				gridLayout.findViewById(R.id.menu_mmtimeline).setOnClickListener(this);
				gridLayout.findViewById(R.id.menu_mmtimeline).setVisibility(View.VISIBLE);
			}else{
				gridLayout.removeView(gridLayout.findViewById(R.id.menu_mm));
				gridLayout.removeView(gridLayout.findViewById(R.id.menu_mmtimeline));
			}
			if (openSdk.hasBLOG()) {
				gridLayout.findViewById(R.id.menu_blog).setOnClickListener(this);
				gridLayout.findViewById(R.id.menu_blog).setVisibility(View.VISIBLE);
			}else{
				gridLayout.removeView(gridLayout.findViewById(R.id.menu_blog));
			}
			if (openSdk.hasQQ()) {
				gridLayout.findViewById(R.id.menu_qq).setOnClickListener(this);
				gridLayout.findViewById(R.id.menu_qq).setVisibility(View.VISIBLE);
				gridLayout.findViewById(R.id.menu_qzone).setOnClickListener(this);
				gridLayout.findViewById(R.id.menu_qzone).setVisibility(View.VISIBLE);
			}else{
				gridLayout.removeView(gridLayout.findViewById(R.id.menu_qq));
				gridLayout.removeView(gridLayout.findViewById(R.id.menu_qzone));
			}
		}
		return convertView;
	}

	@Override
	public void onClick(View v) {
		boolean isNoNetwork = PlayerAPI.addNoNetworkLimit(context);
		if (isNoNetwork) {
			return ;
		}
		OpenShareActivity openShareActivity = null;
		if(context instanceof OpenShareActivity && info!=null){
			openShareActivity = (OpenShareActivity)context;
			int id = v.getId();
			switch (id) {
			case R.id.menu_mm:
				openShareActivity.doshare(OpenSdk.TYPE_MM, info.getShareUrl(), info.getVideoTitle(), info.getVideoDesc(), info.getImageUrl());
				break;
			case R.id.menu_blog:
				openShareActivity.doshare(OpenSdk.TYPE_BLOG, info.getShareUrl(), info.getVideoTitle(), info.getVideoDesc(), info.getImageUrl());
				break;
			case R.id.menu_qq:
				openShareActivity.doshare(OpenSdk.TYPE_QQ, info.getShareUrl(), info.getVideoTitle(), info.getVideoDesc(), info.getImageUrl());
				break;
			case R.id.menu_qzone:
				openShareActivity.doshare(OpenSdk.TYPE_ZONE, info.getShareUrl(), info.getVideoTitle(), info.getVideoDesc(), info.getImageUrl());
				break;
			case R.id.menu_mmtimeline:
				openShareActivity.doshare(OpenSdk.TYPE_MM_TIMELINE, info.getShareUrl(), info.getVideoTitle(), info.getVideoDesc(), info.getImageUrl());
				break;
			default:
				break;
			}
		}
		
	}

}
