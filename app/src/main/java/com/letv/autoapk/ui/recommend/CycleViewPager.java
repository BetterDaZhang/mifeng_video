package com.letv.autoapk.ui.recommend;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.letv.autoapk.R;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.common.utils.Logger;

public class CycleViewPager extends ViewPager {

	private InnerPagerAdapter mAdapter;

	public CycleViewPager(Context context) {
		super(context);
		setOnPageChangeListener(null);
	}

	public CycleViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnPageChangeListener(null);
	}

	@Override
	public void setAdapter(PagerAdapter arg0) {
		mAdapter = new InnerPagerAdapter(arg0);
		super.setAdapter(mAdapter);
//		setCurrentItem(1);
	}

	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		super.setOnPageChangeListener(new InnerOnPageChangeListener(listener));
	}

	private class InnerOnPageChangeListener implements OnPageChangeListener {

		private OnPageChangeListener listener;
		private int position;

		public InnerOnPageChangeListener(OnPageChangeListener listener) {
			this.listener = listener;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			if (null != listener) {
				listener.onPageScrollStateChanged(arg0);
			}
			if (arg0 == ViewPager.SCROLL_STATE_IDLE) {
				if (position == mAdapter.getCount() - 1) {
					setCurrentItem(1, false);
				} else if (position == 0) {
					setCurrentItem(mAdapter.getCount() - 2, false);
				}
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			if (null != listener) {
				listener.onPageScrolled(arg0, arg1, arg2);
			}
		}

		@Override
		public void onPageSelected(int arg0) {
			position = arg0;
			if (null != listener) {
				listener.onPageSelected(arg0);
			}
		}
	}

	private class InnerPagerAdapter extends PagerAdapter {

		private PagerAdapter adapter;

		public InnerPagerAdapter(PagerAdapter adapter) {
			this.adapter = adapter;
			adapter.registerDataSetObserver(new DataSetObserver() {

				@Override
				public void onChanged() {
					notifyDataSetChanged();
				}

				@Override
				public void onInvalidated() {
					notifyDataSetChanged();
				}

			});
		}

		@Override
		public int getCount() {
			if (adapter.getCount() == 0) {
				return adapter.getCount();
			}
			return adapter.getCount() + 2;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return adapter.isViewFromObject(arg0, arg1);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			return adapter.instantiateItem(container, position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			adapter.destroyItem(container, position, object);
		}

		@Override
		public int getItemPosition(Object object) {
			try {
				View view = (View) object;
				DisplayVideoInfo info = (DisplayVideoInfo) view.getTag();
				int pos = (Integer) view.getTag(R.id.cycle_tag_pos);
				List<DisplayVideoInfo> list = ((HeadLineViewPagerAdapter) adapter).getScrollList();
				DisplayVideoInfo updateInfo = list.get(pos);
				if (updateInfo.getImageUrl() != null && updateInfo.getImageUrl().equals(info.getImageUrl())) {
					return POSITION_UNCHANGED;
				}
			} catch (Exception e) {
				Logger.log(e);
			}
			return POSITION_NONE;
		}

	}

	public void notifyDataSetChanged() {
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}
}
