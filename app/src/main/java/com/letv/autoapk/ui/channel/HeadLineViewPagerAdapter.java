package com.letv.autoapk.ui.channel;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.letv.autoapk.R;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.common.net.LruGifCache;
import com.letv.autoapk.common.net.LruImageCache;
import com.letv.autoapk.ui.player.PlayerAPI;
import com.letv.autoapk.widgets.MyImageView;
import com.letv.autoapk.widgets.NetImageView;

class HeadLineViewPagerAdapter extends PagerAdapter implements OnClickListener {

	private List<View> pageViews;
	private Context context;
	private List<DisplayVideoInfo> scrollList;
	private TextView headlineTitle;
	private TextView headlineBrief;
	private TextView headlineTitleBottom;
	private NetImageView vpImageView;
	private int mPosition;
	private boolean isShowBottomTitle;

	public HeadLineViewPagerAdapter(Context context, List<View> pageViews, List<DisplayVideoInfo> scrollList,
			boolean isShow) {
		this.pageViews = pageViews;
		this.context = context;
		this.scrollList = scrollList;
		this.isShowBottomTitle = isShow;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object obj) {
		container.removeView((View) obj);
	}

	@Override
	public void finishUpdate(View arg0) {

	}

	@Override
	public int getCount() {
		return scrollList.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		if (position == 0) {
			mPosition = getCount() - 1;
		} else if (position == getCount() + 1) {
			mPosition = 0;
		} else {
			mPosition = position - 1;
		}
		if (scrollList.size() == 0) {
			return super.instantiateItem(container, position);
		}

		View view = pageViews.get(position);
		headlineTitle = (TextView) view.findViewById(R.id.headlineTitle);
		headlineTitle.setText(scrollList.get(mPosition).getVideoTitle());
		headlineTitle.setVisibility(View.GONE);
		headlineBrief = (TextView) view.findViewById(R.id.headlineBrief);
		headlineBrief.setText(scrollList.get(mPosition).getVideoDesc());
		headlineBrief.setVisibility(view.GONE);
		headlineTitleBottom = (TextView) view.findViewById(R.id.circulate_title);
		if (isShowBottomTitle) {
			headlineTitleBottom.setVisibility(View.VISIBLE);
		} else {
			headlineTitleBottom.setVisibility(View.GONE);
		}
		headlineTitleBottom.setText(scrollList.get(mPosition).getVideoTitle());
		vpImageView = (NetImageView) view.findViewById(R.id.headline_vpimageview);
		vpImageView.setDefaultImageResId(R.drawable.default_img_recomend_22_10);
		vpImageView.setErrorImageResId(R.drawable.default_img_recomend_22_10);
		String url = scrollList.get(mPosition).getImageUrl();
		vpImageView.setCoverUrl(url, context);
		// vpImageView.setImageUrl(scrollList.get(mPosition).getImageUrl(),
		// LruImageCache.getImageLoader(context));
		view.setTag(scrollList.get(mPosition));
		view.setTag(R.id.cycle_tag_pos, mPosition);
		view.setOnClickListener(this);
		container.addView(view);
		return view;
	}

	// 判断是否由对象生成界面
	@Override
	public boolean isViewFromObject(View v, Object arg1) {
		return v == arg1;
	}

	@Override
	public void startUpdate(View arg0) {

	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void onClick(View v) {
		// 缩略图点击
		DisplayVideoInfo info = (DisplayVideoInfo) v.getTag();
		PlayerAPI.startPlayActivity(context, info);
	}

	public void setPageViews(List<View> pageViews) {
		this.pageViews = pageViews;
	}

	public void setScrollList(List<DisplayVideoInfo> scrollList) {
		this.scrollList = scrollList;
	}

	public List<DisplayVideoInfo> getScrollList() {
		return scrollList;
	}
}
