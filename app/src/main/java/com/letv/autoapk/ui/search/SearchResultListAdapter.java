package com.letv.autoapk.ui.search;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.download.DownloadSaasCenter;
import com.letv.autoapk.ui.player.PlayerAPI;
import com.letv.autoapk.utils.ScreenUtils;
import com.letv.autoapk.widgets.FlowLayout;
import com.letv.autoapk.widgets.NetImageView;

class SearchResultListAdapter extends BaseAdapter {
	private List<SearchResultInfo> searchResultList;
	private Context context;
	private static final int TYPE_FILM = 0;
	private static final int TYPE_TV = 1;
	private static final int TYPE_ARTS = 2;
	private static final int TYPE_EMPTY = 3;
	private static final int TYPE_MAX_COUNT = TYPE_ARTS + 1 + 1;
	private String isSubjectPage = "";
	private DownloadSaasCenter downloadSaasCenter;
	private Fragment fragment;

	public SearchResultListAdapter(List<SearchResultInfo> list, Context ctx,
			Fragment fragment) {
		this.searchResultList = list;
		this.context = ctx;
		this.fragment = fragment;
		downloadSaasCenter = DownloadSaasCenter.getInstances(ctx.getApplicationContext());
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return TYPE_EMPTY;
		}
		return searchResultList.get(position - 1)
				.getVideoSearchBlockDisplayType();
	}

	@Override
	public int getViewTypeCount() {

		return TYPE_MAX_COUNT;
	}

	@Override
	public int getCount() {
		return searchResultList.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		return searchResultList.get(position - 1);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SearchResultInfo searchResultInfo = null;
		if (position != 0) {
			searchResultInfo = searchResultList.get(position - 1);
		}
		if (convertView == null) {
			if (position == 0) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.search_result_empty_item, parent, false);
			} else if (getItemViewType(position) == TYPE_FILM) {
				// 如果是电影的话，那么只会有一条或者多条相似的。
				convertView = LayoutInflater.from(context).inflate(
						R.layout.search_result_film_item, parent, false);
				FilmViewHolder fileViewHolder = new FilmViewHolder(convertView,
						searchResultInfo);
				convertView.setTag(fileViewHolder);
			} else if (getItemViewType(position) == TYPE_TV) {
				// 如果是电视剧，那么底部是一个flowLayout以放置剧集列表
				convertView = LayoutInflater.from(context).inflate(
						R.layout.search_result_tv_item, parent, false);
				TVViewHolder tvViewHolder = new TVViewHolder(convertView,
						searchResultInfo);
				convertView.setTag(tvViewHolder);
			} else if (getItemViewType(position) == TYPE_ARTS) {
				// 如果是综艺，那么底部是个LinearLayout，放置其他期数
				convertView = LayoutInflater.from(context).inflate(
						R.layout.search_result_film_item, parent, false);
				ARTSViewHolder artsViewHolder = new ARTSViewHolder(convertView,
						searchResultInfo);
				convertView.setTag(artsViewHolder);
			}
		}
		Object tag = convertView.getTag();
		if (getItemViewType(position) == TYPE_FILM) {
			if (tag instanceof FilmViewHolder) {
				FilmViewHolder filmViewHolder = (FilmViewHolder) tag;
				filmViewHolder.setSearchResultInfo(searchResultInfo);
				// 填充数据
				filmViewHolder.updateUI(position);
				// filmViewHolder.bindListener();

			}
		} else if (getItemViewType(position) == TYPE_TV) {
			if (tag instanceof TVViewHolder) {
				TVViewHolder tvViewHolder = (TVViewHolder) tag;
				tvViewHolder.setSearchResultInfo(searchResultInfo);
				// 填充数据
				tvViewHolder.updateUI(position);
				tvViewHolder.dynamicChangeTvSets();
				// tvViewHolder.bindListener();
			}
		} else if (getItemViewType(position) == TYPE_ARTS) {
			if (tag instanceof ARTSViewHolder) {
				ARTSViewHolder artsViewHolder = (ARTSViewHolder) tag;
				artsViewHolder.setSearchResultInfo(searchResultInfo);
				// 填充数据
				artsViewHolder.updateUI(position);
			}
		}

		return convertView;
	}

	class FilmViewHolder {
		private View convertView;
		public NetImageView img;
		public TextView title;
		public TextView publishTime;
		public TextView actor;
		public TextView director;
		// public TextView play;
		// public TextView download;
		private RelativeLayout tvImgRl;
		private SearchResultInfo searchResultInfo;

		public FilmViewHolder(View view, SearchResultInfo searchResultInfo) {
			this.convertView = view;
			this.img = (NetImageView) view
					.findViewById(R.id.search_result__item_img);
			this.title = (TextView) view
					.findViewById(R.id.search_result__title);
			this.publishTime = (TextView) view
					.findViewById(R.id.search_result_publishtime);
			this.actor = (TextView) view.findViewById(R.id.search_result_actor);
			this.director = (TextView) view
					.findViewById(R.id.search_result_director);
			// this.play = (TextView) view.findViewById(R.id.tv_play);
			// this.download = (TextView) view.findViewById(R.id.download);
			this.tvImgRl = (RelativeLayout) view.findViewById(R.id.film_img_rl);
			this.searchResultInfo = searchResultInfo;
		}

		public void updateUI(int position) {
			final DisplayVideoInfo mainInfo = searchResultInfo
					.getDispalyVideoInfoUnit();// 首要展示的视频信息
			((NetImageView) img)
					.setDefaultImageResId(R.drawable.default_img_16_10);
			((NetImageView) img)
					.setErrorImageResId(R.drawable.default_img_16_10);
			((NetImageView) img).setCoverUrl(mainInfo.getImageUrl(), context);
			// 设置角标

			title.setText(mainInfo.getVideoTitle());
			setText(publishTime, context.getString(R.string.search_showtime),
					mainInfo.getPublishTime());
			setText(actor, context.getString(R.string.search_starring),
					mainInfo.getVideoActor());
			setText(director, context.getString(R.string.search_director),
					mainInfo.getVideoDirector());
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					PlayerAPI.startPlayActivity(context, mainInfo);
				}
			});
			// download.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// PlayerUtils.addDownloadInfo(context, mainInfo,
			// downloadSaasCenter, false);
			// }
			// });
		}

		public void setSearchResultInfo(SearchResultInfo searchResultInfo) {
			this.searchResultInfo = searchResultInfo;
		}

		public void bindListener() {
			tvImgRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// StartPlayActivity.start((Activity) context,
					// searchResultInfo.getDispalyVideoInfoUnit(),isSubjectPage);
					// Log.i("SearchResultListAdapter", "film 进入播放详情页 ");
				}
			});
		}
	}

	class TVViewHolder {
		private View convertView;
		private SearchResultInfo searchResultInfo;
		public NetImageView img;
		public TextView title;
		public TextView publishTime;
		public TextView actor;
		public TextView director;
		// public TextView play;
		// public TextView download;
		// public GridView gridView;
		// public SearchResultGridAdapter searchResultGridAdapter;
		public FlowLayout searchResultFlowLayout;
		private RelativeLayout tvImgRl;

		public TVViewHolder(View view, SearchResultInfo searchResultInfo) {
			this.convertView = view;
			this.searchResultInfo = searchResultInfo;
			this.img = (NetImageView) view
					.findViewById(R.id.search_result__item_img);
			this.title = (TextView) view
					.findViewById(R.id.search_result__title);
			this.publishTime = (TextView) view
					.findViewById(R.id.search_result_publishtime);
			this.actor = (TextView) view.findViewById(R.id.search_result_actor);
			this.director = (TextView) view
					.findViewById(R.id.search_result_director);
			// this.play = (TextView) view.findViewById(R.id.tv_play);
			// this.download = (TextView) view.findViewById(R.id.download);
			// this.gridView = (GridView)
			// view.findViewById(R.id.search_result_gd);
			this.searchResultFlowLayout = (FlowLayout) view
					.findViewById(R.id.search_result_flowlayout);
			tvImgRl = (RelativeLayout) view.findViewById(R.id.tv_img_rl);
		}

		public void setSearchResultInfo(SearchResultInfo searchResultInfo) {
			this.searchResultInfo = searchResultInfo;
		}

		public void updateUI(int position) {
			final DisplayVideoInfo mainInfo = searchResultInfo
					.getDispalyVideoInfoUnit();
			((NetImageView) img)
					.setDefaultImageResId(R.drawable.default_img_16_10);
			((NetImageView) img)
					.setErrorImageResId(R.drawable.default_img_16_10);
			((NetImageView) img).setCoverUrl(mainInfo.getImageUrl(), context);
			title.setText(mainInfo.getVideoTitle());
			setText(publishTime, context.getString(R.string.search_showtime),
					mainInfo.getPublishTime());
			setText(actor, context.getString(R.string.search_starring),
					mainInfo.getVideoActor());
			setText(director, context.getString(R.string.search_director),
					mainInfo.getVideoDirector());
			Log.e("search", "before click");
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.e("search", "after click");
					PlayerAPI.startPlayActivity(context, mainInfo);
				}
			});
			// download.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// PlayerUtils.addDownloadInfo(context, mainInfo,
			// downloadSaasCenter, false);
			// }
			// });
		}

		public void dynamicChangeTvSets() {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					ScreenUtils.dip2px(context, 57.6f), ScreenUtils.dip2px(
							context, 28.8f));
			searchResultFlowLayout.removeAllViews();
			final List<DisplayVideoInfo> episodes = searchResultInfo
					.getDisplayVideoInfos();
			if (episodes == null) {
				return;
			}
			for (int i = 1; i <= (episodes.size() > 20 ? 20 : episodes.size()); i++) {
				final TextView channelTv = new TextView(context);
				DisplayVideoInfo info = episodes.get(i - 1);
				channelTv.setText(info.getEpisode());
				if (episodes.size() > 20) {
					if (i == 19) {
						channelTv.setText("···");
					}
					if (i == 20) {
						channelTv.setText(String.valueOf(info.getEpisode()));
					}
				}
				channelTv
						.setBackgroundResource(R.drawable.search_result_episode);
				channelTv.setTextColor(context.getResources().getColor(
						R.color.code4));
				channelTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
				channelTv.setGravity(Gravity.CENTER);
				channelTv.setLayoutParams(params);
				searchResultFlowLayout.addView(channelTv);
				channelTv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if ("···".equals(channelTv.getText().toString())) {
							loadMoreTvSets(episodes);
						} else {
							int xuanji = Integer.valueOf((channelTv.getText()
									.toString())) - 1;
							PlayerAPI.startPlayActivity(context,
									episodes.get(xuanji));
						}
					}
				});
			}
		}

		public void loadMoreTvSets(final List<DisplayVideoInfo> episodes) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					ScreenUtils.dip2px(context, 61.6f), ScreenUtils.dip2px(
							context, 29f));
			searchResultFlowLayout.removeAllViews();
			for (int i = 1; i <= searchResultInfo.getDisplayVideoInfos().size(); i++) {
				final TextView channelTv = new TextView(context);
				channelTv
						.setBackgroundResource(R.drawable.search_result_episode);
				channelTv.setTextColor(context.getResources().getColor(
						R.color.code4));
				channelTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
				channelTv.setGravity(Gravity.CENTER);
				channelTv.setText(String.valueOf(i));
				channelTv.setLayoutParams(params);
				searchResultFlowLayout.addView(channelTv);
				channelTv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int xuanji = Integer.valueOf((channelTv.getText()
								.toString())) - 1;
						PlayerAPI.startPlayActivity(context,
								episodes.get(xuanji));
					}
				});
			}
		}

		public void bindListener() {
			tvImgRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// StartPlayActivity.start((Activity) context,
					// searchResultInfo.getDispalyVideoInfoUnit(),isSubjectPage);
				}
			});
		}
	}

	class ARTSViewHolder {
		private View convertView;
		public NetImageView img;
		public TextView title;
		public TextView publishTime;
		public TextView actor;
		public TextView director;
		// public TextView play;
		// public TextView download;
		private LinearLayout artsSortLay;
		// private TextView time;
		private TextView artsTitle;
		private View artDivider;
		private SearchResultInfo searchResultInfo;

		public ARTSViewHolder(View view, SearchResultInfo searchResultInfo) {
			this.convertView = view;
			this.img = (NetImageView) view
					.findViewById(R.id.search_result__item_img);
			this.title = (TextView) view
					.findViewById(R.id.search_result__title);
			this.publishTime = (TextView) view
					.findViewById(R.id.search_result_publishtime);
			this.actor = (TextView) view.findViewById(R.id.search_result_actor);
			this.director = (TextView) view
					.findViewById(R.id.search_result_director);
			// this.play = (TextView) view.findViewById(R.id.tv_play);
			// this.download = (TextView) view.findViewById(R.id.download);
			this.artsSortLay = (LinearLayout) view
					.findViewById(R.id.arts_item_sort);
			this.searchResultInfo = searchResultInfo;
		}

		public void updateUI(int position) {
			final DisplayVideoInfo mainInfo = searchResultInfo
					.getDispalyVideoInfoUnit();
			((NetImageView) img)
					.setDefaultImageResId(R.drawable.default_img_16_10);
			((NetImageView) img)
					.setErrorImageResId(R.drawable.default_img_16_10);
			((NetImageView) img).setCoverUrl(mainInfo.getImageUrl(), context);
			// 设置角标

			title.setText(mainInfo.getVideoTitle());
			setText(publishTime, context.getString(R.string.search_showtime),
					mainInfo.getPublishTime());
			setText(actor, context.getString(R.string.search_starring),
					mainInfo.getVideoActor());
			setText(director, context.getString(R.string.search_director),
					mainInfo.getVideoDirector());
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					PlayerAPI.startPlayActivity(context, mainInfo);
				}
			});
			// download.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// PlayerUtils.addDownloadInfo(context, mainInfo,
			// downloadSaasCenter, false);
			// }
			// });
			// TODO 扩展数据
			artsSortLay.removeAllViews();
			List<DisplayVideoInfo> displayVideoInfos = searchResultInfo
					.getDisplayVideoInfos();
			int artsItemCount = displayVideoInfos.size() > 3 ? 3 + 1
					: displayVideoInfos.size();
			for (int i = 0; i < artsItemCount; i++) {
				if (i == 3) {
					RelativeLayout artsItemSortRl = (RelativeLayout) LayoutInflater
							.from(context).inflate(
									R.layout.search_result_arts_menu_all, null);
					artsSortLay.addView(artsItemSortRl);
					artsItemSortRl.setOnClickListener(new AllArtsListener(
							searchResultInfo.getDisplayVideoInfos()));
					return;
				}
				RelativeLayout artsItemSortRl = (RelativeLayout) LayoutInflater
						.from(context).inflate(
								R.layout.search_result_arts_item_sort, null);
				// time = (TextView)
				// artsItemSortRl.findViewById(R.id.search_result__time);
				artsTitle = (TextView) artsItemSortRl
						.findViewById(R.id.search_result_artstitle);
				artDivider = artsItemSortRl
						.findViewById(R.id.result_item_divider_two);
				if (artsItemCount == 1 || i == artsItemCount - 1 || i == 2) {
					artDivider.setVisibility(View.GONE);
				}
				// Date publishDate =
				// DateUitls.getDateFromGreenwichSec(searchResultInfo.getDisplayVideoInfos().get(i).getPublishTime());
				// String publishTime = DateUitls.formatDate(publishDate);
				// time.setText(searchResultInfo.getDisplayVideoInfos().get(i).getPublishTime());
				// artsTitle.setText(searchResultInfo.getDisplayVideoInfos().get(i).getVideoTitle());
				artsTitle.setText(searchResultInfo.getDisplayVideoInfos()
						.get(i).getVideoTitle());
				// LayoutParams params = new
				// LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				// ScreenUtils.dip2px(context, 38.7f));
				artsSortLay.addView(artsItemSortRl);
				artsItemSortRl.setOnClickListener(new ClickListener(i,
						searchResultInfo.getDisplayVideoInfos().get(i)));
			}
		}

		public void setSearchResultInfo(SearchResultInfo searchResultInfo) {
			this.searchResultInfo = searchResultInfo;
		}
	}

	class ClickListener implements OnClickListener {
		private int position = 0;
		private DisplayVideoInfo displaVideoInfo;

		public ClickListener(int pos, DisplayVideoInfo info) {
			this.position = pos;
			this.displaVideoInfo = info;
		}

		@Override
		public void onClick(View v) {
			PlayerAPI.startPlayActivity(context, displaVideoInfo);
		}

	}

	class AllArtsListener implements OnClickListener {
		List<DisplayVideoInfo> infos;

		public AllArtsListener(List<DisplayVideoInfo> infos) {
			this.infos = infos;
		}

		@Override
		public void onClick(View v) {
			FragmentTransaction transation = ((BaseActivity) context)
					.getSupportFragmentManager().beginTransaction();
			Fragment searchResultFragment = new SearchResultAllArtFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable(SearchResultAllArtFragment.ALL_ART,
					(Serializable) infos);
			searchResultFragment.setArguments(bundle);
			transation.hide(fragment);
			transation.add(R.id.container, searchResultFragment)
					.addToBackStack(null).commit();
		}
	}

	private void setText(TextView tv, String contentType, String content) {
		if (TextUtils.isEmpty(content) || "null".equals(content)) {
			tv.setVisibility(View.INVISIBLE);
		} else {
			tv.setText(contentType + ":" + content);
		}
	}

}
