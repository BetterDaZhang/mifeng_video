package com.letv.autoapk.ui.channel;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.common.net.LruImageCache;
import com.letv.autoapk.ui.player.PlayerAPI;
import com.letv.autoapk.widgets.NetImageView;

class ChannelVideoAdapter extends BaseAdapter {
	private ArrayList<DisplayBlockInfo> channelBlockInfos;
	private Handler handler;
	private BaseActivity mactivity;
	private Fragment fragment;
	public static final int TYPE_SUBJECT = 101;
	public static final int TYPE_CIRCULATE = 201;
	public static final int TYPE_BLOCK_HEAD = 301;
	public static final int TYPE_ONE_LANDSCAPE = 503;
	public static final int TYPE_TWO_LANDSCAPE = 501;
	public static final int TYPE_THREE_VERTICAL = 502;
	public static final int TYPE_BLOCK_HEAD_MORE = 302;
	public static final int TYPE_SUBJECT_TYPE = 0; // 101;
	public static final int TYPE_CIRCULATE_TYPE = 1;// 201;
	public static final int TYPE_BLOCK_HEAD_TYPE = 2; // 301;
	public static final int TYPE_ONE_LANDSCAPE_TYPE = 3;// 503;
	public static final int TYPE_TWO_LANDSCAPE_TYPE = 4;// 501;
	public static final int TYPE_THREE_VERTICAL_TYPE = 5; // 502;
	public static final int TYPE_BLOCK_HEAD_MORE_TYPE = 6; // 504;
	public static final int TYPE_BLOCK_DEFAULT_TYPE = 7; // 504;
	public static final int TYPE_MAX_COUNT = TYPE_BLOCK_DEFAULT_TYPE + 1;

	public ChannelVideoAdapter(BaseActivity ctx, Fragment fragment, ArrayList<DisplayBlockInfo> infos,
			Handler handler) {
		this.mactivity = ctx;
		this.fragment = fragment;
		this.channelBlockInfos = infos;
		this.handler = handler;
	}

	@Override
	public int getItemViewType(int position) {
		if (channelBlockInfos.get(position).getBlockDisplayType() == TYPE_SUBJECT) {
			return TYPE_SUBJECT_TYPE;
		} else if (channelBlockInfos.get(position).getBlockDisplayType() == TYPE_CIRCULATE) {
			return TYPE_CIRCULATE_TYPE;
		} else if (channelBlockInfos.get(position).getBlockDisplayType() == TYPE_BLOCK_HEAD) {
			return TYPE_BLOCK_HEAD_TYPE;
		} else if (channelBlockInfos.get(position).getBlockDisplayType() == TYPE_ONE_LANDSCAPE) {
			return TYPE_ONE_LANDSCAPE_TYPE;
		} else if (channelBlockInfos.get(position).getBlockDisplayType() == TYPE_TWO_LANDSCAPE) {
			return TYPE_TWO_LANDSCAPE_TYPE;
		} else if (channelBlockInfos.get(position).getBlockDisplayType() == TYPE_THREE_VERTICAL) {
			return TYPE_THREE_VERTICAL_TYPE;
		} else if (channelBlockInfos.get(position).getBlockDisplayType() == TYPE_BLOCK_HEAD_MORE) {
			return TYPE_BLOCK_HEAD_MORE_TYPE;
		}
		return TYPE_BLOCK_DEFAULT_TYPE;
	}

	@Override
	public int getViewTypeCount() {

		return TYPE_MAX_COUNT;
	}

	@Override
	public int getCount() {
		return channelBlockInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return channelBlockInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	protected View getMyView(int position, View convertView, ViewGroup viewGroup) {
		if (convertView == null) {
			if (getItemViewType(position) == TYPE_SUBJECT_TYPE) {
				convertView = LayoutInflater.from(mactivity).inflate(R.layout.subject_headline, viewGroup, false);
				SubjectHolder subjectHolder = new SubjectHolder(convertView);
				convertView.setTag(subjectHolder);
			} else if (getItemViewType(position) == TYPE_CIRCULATE_TYPE) {
				convertView = LayoutInflater.from(mactivity).inflate(R.layout.channel_headline_vplayout, viewGroup,
						false);
				CirculateHolder blockHeadHolder = new CirculateHolder(convertView);
				convertView.setTag(blockHeadHolder);
			} else if (getItemViewType(position) == TYPE_BLOCK_HEAD_TYPE) {
				convertView = LayoutInflater.from(mactivity).inflate(R.layout.channel_block_head_item, viewGroup,
						false);
				BlockHeadHolder blockHeadHolder = new BlockHeadHolder(convertView);
				convertView.setTag(blockHeadHolder);
			} else if (getItemViewType(position) == TYPE_TWO_LANDSCAPE_TYPE) {
				convertView = LayoutInflater.from(mactivity).inflate(R.layout.channel_two_lands_item, viewGroup, false);
				TwoLandsViewHolder twoTwoRViewHoler = new TwoLandsViewHolder(convertView);
				convertView.setTag(twoTwoRViewHoler);
			} else if (getItemViewType(position) == TYPE_ONE_LANDSCAPE_TYPE) {
				convertView = LayoutInflater.from(mactivity).inflate(R.layout.channel_one_lands_item, viewGroup, false);
				OneLandsViewHolder oneLandsViewHolder = new OneLandsViewHolder(convertView);
				convertView.setTag(oneLandsViewHolder);
			} else if (getItemViewType(position) == TYPE_THREE_VERTICAL_TYPE) {
				convertView = LayoutInflater.from(mactivity).inflate(R.layout.channel_three_ver_item, viewGroup, false);
				ThreeVerViewHolder oneTwoRViewHoler = new ThreeVerViewHolder(convertView);
				convertView.setTag(oneTwoRViewHoler);
			} else if (getItemViewType(position) == TYPE_BLOCK_HEAD_MORE_TYPE) {
				convertView = LayoutInflater.from(mactivity).inflate(R.layout.channel_block_head_item_more, viewGroup,
						false);
				BlockHeadMoreHolder blockHeadMoreHolder = new BlockHeadMoreHolder(convertView);
				convertView.setTag(blockHeadMoreHolder);
			} else if (getItemViewType(position) == TYPE_BLOCK_DEFAULT_TYPE) {
				convertView = LayoutInflater.from(mactivity).inflate(R.layout.channel_default_item, viewGroup, false);
				DefaultHolder defaultViewHoler = new DefaultHolder(convertView);
				convertView.setTag(defaultViewHoler);
			}
			// convertView.setBackgroundColor(mactivity.getResources().getColor(R.color.code01));
		}
		Object tag = convertView.getTag();
		if (getItemViewType(position) == TYPE_SUBJECT_TYPE) {
			if (tag instanceof SubjectHolder) {
				SubjectHolder subjectHolder = (SubjectHolder) tag;
				subjectHolder.loadingData(channelBlockInfos.get(position).getVideoList().get(0));
			} else {
				Toast.makeText(mactivity, "subject convertiew failed", Toast.LENGTH_LONG).show();
			}
		} else if (getItemViewType(position) == TYPE_CIRCULATE_TYPE) {
			if (tag instanceof CirculateHolder) {
				CirculateHolder circulateHolder = (CirculateHolder) tag;
				circulateHolder.loadingData(channelBlockInfos.get(position).getVideoList());
			} else {
				Toast.makeText(mactivity, "circulate convertiew failed", Toast.LENGTH_LONG).show();
			}
		} else if (getItemViewType(position) == TYPE_BLOCK_HEAD_TYPE) {
			if (tag instanceof BlockHeadHolder) {
				BlockHeadHolder blockHeadHolder = (BlockHeadHolder) tag;
				blockHeadHolder.loadingData(channelBlockInfos.get(position));
			} else {
				Toast.makeText(mactivity, "two lands convertiew failed", Toast.LENGTH_LONG).show();
			}
		} else if (getItemViewType(position) == TYPE_TWO_LANDSCAPE_TYPE) {
			if (tag instanceof TwoLandsViewHolder) {
				TwoLandsViewHolder twoLandsViewHolder = (TwoLandsViewHolder) tag;
				twoLandsViewHolder.lodingData(channelBlockInfos.get(position));
			} else {
				Toast.makeText(mactivity, "two lands convertiew failed", Toast.LENGTH_LONG).show();
			}
		} else if (getItemViewType(position) == TYPE_ONE_LANDSCAPE_TYPE) {
			if (tag instanceof OneLandsViewHolder) {
				OneLandsViewHolder oneLandsViewHolder = (OneLandsViewHolder) tag;
				oneLandsViewHolder.lodingData(channelBlockInfos.get(position));
			} else {
				Toast.makeText(mactivity, "one two lands convertiew failed", Toast.LENGTH_LONG).show();
			}
		} else if (getItemViewType(position) == TYPE_THREE_VERTICAL_TYPE) {
			if (tag instanceof ThreeVerViewHolder) {
				ThreeVerViewHolder threeVerViewHolder = (ThreeVerViewHolder) tag;
				threeVerViewHolder.lodingData(channelBlockInfos.get(position));
			} else {
				Toast.makeText(mactivity, "three vertical convertiew failed", Toast.LENGTH_LONG).show();
			}
		} else if (getItemViewType(position) == TYPE_BLOCK_HEAD_MORE_TYPE) {
			if (tag instanceof BlockHeadMoreHolder) {
				BlockHeadMoreHolder blockHeadHolder = (BlockHeadMoreHolder) tag;
				blockHeadHolder.loadingData(channelBlockInfos.get(position));
			} else {
				Toast.makeText(mactivity, "head more convertiew failed", Toast.LENGTH_LONG).show();
			}
		} else if (getItemViewType(position) == TYPE_BLOCK_DEFAULT_TYPE) {
			if (tag instanceof DefaultHolder) {
				DefaultHolder defaultHolder = (DefaultHolder) tag;
			}
		}
		return convertView;

	}

	class BlockHeadMoreHolder {
		private DisplayBlockInfo blockInfo;
		private TextView blockTitle;
		private View moreTitle;

		protected BlockHeadMoreHolder(View view) {
			blockTitle = (TextView) view.findViewById(R.id.block_title);
			moreTitle = view.findViewById(R.id.more_title);
		}

		public void loadingData(DisplayBlockInfo info) {
			this.blockInfo = info;
			blockTitle.setText(blockInfo.getBlockName());
			moreTitle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (blockInfo.getBlockDetailId() != null && !blockInfo.getBlockDetailId().isEmpty()
							&& !"null".equals(blockInfo.getBlockDetailId())) {
						Intent intent = new Intent(mactivity, ContainerActivity.class);
						intent.putExtra(ContainerActivity.FRAGMENTNAME, ChannelVideoBlockFragment.class.getName());
						intent.putExtra("pageId", blockInfo.getBlockDetailId());
						intent.putExtra("pageName", blockInfo.getBlockMoreName());
						mactivity.startActivity(intent);
					}
				}
			});
		}
	}

	class BlockHeadHolder {
		private DisplayBlockInfo blockInfo;
		private LinearLayout headHotLay;
		private TextView blockTitle;

		protected BlockHeadHolder(View view) {
			headHotLay = (LinearLayout) view.findViewById(R.id.channel_head_hot_rl);
			blockTitle = (TextView) view.findViewById(R.id.block_title);
		}

		public void loadingData(DisplayBlockInfo info) {
			this.blockInfo = info;
			blockTitle.setText(blockInfo.getBlockName());
			// 增加热点标题
			headHotLay.removeAllViews();
			if (null != info && null != info.getVideoList() && !info.getVideoList().isEmpty()) {
				for (int i = 0; i < info.getVideoList().size(); i++) {
					TextView hotTitle = new TextView(mactivity);
					LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					params.gravity = Gravity.CENTER_VERTICAL;
					params.leftMargin = (int) mactivity.getResources().getDimension(R.dimen.recommend_hottitle_margin);
					hotTitle.setText(info.getVideoList().get(i).getVideoTitle());
					hotTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
							mactivity.getResources().getDimension(R.dimen.recommend_item_hottitle));
					hotTitle.setTextColor(mactivity.getResources().getColor(R.color.code4));
					// hotTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
					// 加粗
					// headHotLay.addView(hotTitle, params);
					headHotLay.addView(hotTitle, 0, params);
					hotTitle.setSingleLine();
					hotTitle.setOnClickListener(new hotClickListener(info.getVideoList().get(i)));
				}
			}

			blockTitle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (blockInfo.getBlockDetailId() != null && !blockInfo.getBlockDetailId().isEmpty()
							&& !"null".equals(blockInfo.getBlockDetailId())) {
						Intent intent = new Intent(mactivity, ContainerActivity.class);
						intent.putExtra(ContainerActivity.FRAGMENTNAME, ChannelVideoBlockFragment.class.getName());
						intent.putExtra("pageId", blockInfo.getBlockDetailId());
						intent.putExtra("pageName", blockInfo.getBlockMoreName());
						mactivity.startActivity(intent);
					}
				}
			});
		}
	}

	class TwoLandsViewHolder {
		private NetImageView leftOneNetImg;
		private TextView leftOneTitle;
		private TextView leftOneBrief;
		private NetImageView rightOneNetImg;
		private TextView rightOneTitle;
		private TextView rightOneBrief;

		protected TwoLandsViewHolder(View view) {
			leftOneNetImg = (NetImageView) view.findViewById(R.id.left_one_img);
			leftOneTitle = (TextView) view.findViewById(R.id.left_one_title);
			leftOneBrief = (TextView) view.findViewById(R.id.left_one_brief);

			rightOneNetImg = (NetImageView) view.findViewById(R.id.right_one_img);
			rightOneTitle = (TextView) view.findViewById(R.id.right_one_title);
			rightOneBrief = (TextView) view.findViewById(R.id.right_one_brief);
		}

		public void lodingData(DisplayBlockInfo info) {
			View[][] views = new View[][] { { leftOneNetImg, leftOneTitle, leftOneBrief },
					{ rightOneNetImg, rightOneTitle, rightOneBrief } };
			setTwoLandsDefaultImage(leftOneNetImg, rightOneNetImg);
			setData(info.getVideoList(), views);
		}

		/**
		 * 统一设置默认的小图16：10
		 */
		private void setTwoLandsDefaultImage(View... views) {
			for (View view : views) {
				((NetImageView) view).setDefaultImageResId(R.drawable.default_img_16_10);
				((NetImageView) view).setErrorImageResId(R.drawable.default_img_16_10);
			}
		}
	}

	class hotClickListener implements OnClickListener {
		DisplayVideoInfo displayVideoInfo;

		hotClickListener(DisplayVideoInfo info) {
			this.displayVideoInfo = info;
		}

		@Override
		public void onClick(View v) {
			PlayerAPI.startPlayActivity(mactivity, displayVideoInfo);
		}

	}

	class OneLandsViewHolder {
		private NetImageView leftOneNetImg;
		private TextView leftOneTitle;
		private TextView leftOneBrief;

		protected OneLandsViewHolder(View view) {
			leftOneNetImg = (NetImageView) view.findViewById(R.id.left_one_img);
			leftOneTitle = (TextView) view.findViewById(R.id.left_one_title);
			leftOneBrief = (TextView) view.findViewById(R.id.left_one_brief);
		}

		public void lodingData(DisplayBlockInfo info) {
			View[][] views = new View[][] { { leftOneNetImg, leftOneTitle, leftOneBrief } };
			// 设置电影高度
			setOneDefaultImage(leftOneNetImg);
			setData(info.getVideoList(), views);
		}

		/**
		 * 统一设置默认的小图16：10
		 */
		private void setOneDefaultImage(View... views) {
			for (View view : views) {
				((NetImageView) view).setDefaultImageResId(R.drawable.default_img_22_10);
				((NetImageView) view).setErrorImageResId(R.drawable.default_img_22_10);
			}
		}

	}

	class ThreeVerViewHolder {
		private NetImageView leftOneNetImg;
		private TextView leftOneTitle;
		private NetImageView middleOneNetImg;
		private TextView middleOneTitle;
		private NetImageView rightOneNetImg;
		private TextView rightOneTitle;

		protected ThreeVerViewHolder(View view) {
			leftOneNetImg = (NetImageView) view.findViewById(R.id.left_one_img);
			leftOneTitle = (TextView) view.findViewById(R.id.left_one_title);

			middleOneNetImg = (NetImageView) view.findViewById(R.id.middle_one_img);
			middleOneTitle = (TextView) view.findViewById(R.id.middle_one_title);

			rightOneNetImg = (NetImageView) view.findViewById(R.id.right_one_img);
			rightOneTitle = (TextView) view.findViewById(R.id.right_one_title);
		}

		public void lodingData(DisplayBlockInfo info) {
			View[][] views = new View[][] { { leftOneNetImg, leftOneTitle }, { middleOneNetImg, middleOneTitle },
					{ rightOneNetImg, rightOneTitle } };
			setThreeLandsDefaultImage(leftOneNetImg, middleOneNetImg, rightOneNetImg);
			setData(info.getVideoList(), views);
		}

		/**
		 * 统一设置默认的小图16：10
		 */
		private void setThreeLandsDefaultImage(View... views) {
			for (View view : views) {
				((NetImageView) view).setDefaultImageResId(R.drawable.default_img_16_10);
				((NetImageView) view).setErrorImageResId(R.drawable.default_img_16_10);
			}
		}

	}

	/**
	 * 填充数据
	 */
	private void setData(List<DisplayVideoInfo> infos, View[][] views) {
		if (infos == null)
			return;
		int count;

		if (infos.size() >= views.length) {
			count = views.length;
		} else {
			count = infos.size();
			for (int i = count; i < views.length; i++) {
				for (int j = 0; j < views[i].length; j++) {
					views[i][j].setVisibility(View.INVISIBLE);
				}
			}
		}
		for (int i = 0; i < count; i++) {
			PlayerAPI.initNetImgView(mactivity, infos.get(i), views[i]);
			((NetImageView) views[i][0]).setOnClickListener(new SkipVideoClickListener(infos.get(i)));
		}
	}

	class SkipVideoClickListener implements OnClickListener {
		DisplayVideoInfo videoInfo;

		protected SkipVideoClickListener(DisplayVideoInfo info) {
			this.videoInfo = info;
		}

		@Override
		public void onClick(View v) {
			PlayerAPI.startPlayActivity(mactivity, videoInfo);
		}
	}

	class CirculateHolder {
		private ViewGroup viewPoints;
		private CycleViewPager bannerViewPager;
		// viewPager中view数组
		List<View> pageViews = new ArrayList<View>();
		// 创建imageviews数组，大小是要显示的图片的数量
		List<ImageView> circleImageViews = new ArrayList<ImageView>();
		List<DisplayVideoInfo> scrollList = new ArrayList<DisplayVideoInfo>();

		HeadLineViewPagerAdapter headLineViewPagerAdapter;
		HeadLinePageChangeListener headLinePageChangeListener;

		protected CirculateHolder(View view) {
			viewPoints = (ViewGroup) view.findViewById(R.id.viewPoints);
			bannerViewPager = (CycleViewPager) view.findViewById(R.id.headlineViewpager);

			// 设置viewpager的适配器和监听事件
			headLineViewPagerAdapter = new HeadLineViewPagerAdapter(mactivity, pageViews, scrollList, true);
			bannerViewPager.setAdapter(headLineViewPagerAdapter);
			headLinePageChangeListener = new HeadLinePageChangeListener(pageViews, circleImageViews);
			bannerViewPager.setOnPageChangeListener(headLinePageChangeListener);
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					bannerViewPager.setCurrentItem(1);
				}
			}, 200);
		}

		private void loadingData(List<DisplayVideoInfo> scrollList) {
			this.scrollList = scrollList;
			if (scrollList == null || scrollList.size() <= 0)
				return;
			// viewPager中view数组
			pageViews.clear();
			for (int i = 0; i < scrollList.size() + 2; i++) {
				View rowView = LayoutInflater.from(mactivity).inflate(R.layout.channel_headline_vpitem, null);
				pageViews.add(rowView);
			}

			viewPoints.removeAllViews();
			circleImageViews.clear();
			// 添加小圆点的图片
			int circlePadding = BaseActivity.px2dip(mactivity,
					mactivity.getResources().getDimension(R.dimen.recommend_circle_padding));
			for (int i = 0; i < scrollList.size(); i++) {
				ImageView circleImageview = new ImageView(mactivity);
				LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutParams.setMargins(circlePadding, 0, circlePadding, 0);
				circleImageview.setLayoutParams(layoutParams);
				circleImageViews.add(circleImageview);
				// 默认选中的是当前选中的
				if (i == headLinePageChangeListener.getCheckPosition()) {
					circleImageViews.get(i).setImageResource(R.drawable.recommend_pageindicator_focused);
				} else {
					circleImageViews.get(i).setImageResource(R.drawable.recommend_pageindicator);
				}
				// 将imageviews添加到小圆点视图
				viewPoints.addView(circleImageViews.get(i));
			}
			headLineViewPagerAdapter.setScrollList(scrollList);
			headLineViewPagerAdapter.setPageViews(pageViews);
			bannerViewPager.notifyDataSetChanged();
		}
	}

	class SubjectHolder {
		private NetImageView headImg;
		private TextView headTitle;
		private TextView headBrief;

		public SubjectHolder(View view) {
			headImg = (NetImageView) view.findViewById(R.id.subject_head_img);
			headTitle = (TextView) view.findViewById(R.id.subject_head_title);
			headBrief = (TextView) view.findViewById(R.id.subject_head_brief);
		}

		public void loadingData(DisplayVideoInfo videoInfo) {
			headImg.setDefaultImageResId(R.drawable.default_img_22_10);
			headImg.setErrorImageResId(R.drawable.default_img_22_10);
			headImg.setCoverUrl(videoInfo.getImageUrl(), mactivity);
			headTitle.setText(videoInfo.getVideoTitle());
			headBrief.setText(videoInfo.getVideoDesc());
		}
	}

	class DefaultHolder {

		public DefaultHolder(View view) {
		}
	}

}
