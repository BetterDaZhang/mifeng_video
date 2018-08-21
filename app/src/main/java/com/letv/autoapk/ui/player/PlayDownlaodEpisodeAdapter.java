package com.letv.autoapk.ui.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.context.MyApplication;

public class PlayDownlaodEpisodeAdapter extends BaseAdapter {
	private BaseActivity context;
	private Handler handler;
	private List<PlayVideoInfo> episodeInfos;
	private String videoId;
	private Set<PlayVideoInfo> addDownlaodInfos;
	/** 一下在视频的Vu集合 */
	private List<String> videoIdList;

	public PlayDownlaodEpisodeAdapter(BaseActivity ctx, Handler handler, List<PlayVideoInfo> infos, String videoId,
			List<String> videoIdList) {
		addDownlaodInfos = new HashSet<PlayVideoInfo>();
		this.context = ctx;
		this.handler = handler;
		this.episodeInfos = infos;
		this.videoId = videoId;
		this.videoIdList = videoIdList;
	}

	@Override
	public int getCount() {
		return episodeInfos.size();
	}

	@Override
	public PlayVideoInfo getItem(int position) {
		return episodeInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	protected View getMyView(final int position, View convertView, ViewGroup parent) {
		Holder holder;
		PlayVideoInfo playVideoInfo;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.play_downlaod_episode_item, null);
			holder = new Holder();
			holder.episodeTitle = (Button) convertView.findViewById(R.id.play_downlaod_episode_item_title);
			holder.cornorImg = (TextView) convertView.findViewById(R.id.play_downlaod_episode_item_cornor);
			holder.downloadCheck = (ImageView) convertView.findViewById(R.id.play_download_episode_check);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		playVideoInfo = episodeInfos.get(position);
		holder.episodeTitle.setText(playVideoInfo.getEpisode());
		if (context.getString(R.string.foreshow).equals(episodeInfos.get(position).getVideoTypeCode())) {
			holder.cornorImg.setVisibility(View.VISIBLE);
			holder.cornorImg.setText(episodeInfos.get(position).getVideoType());
		} else {
			holder.cornorImg.setVisibility(View.GONE);
		}
		if (MyApplication.getInstance().isNeedBoss() == 1) {
			if (episodeInfos.get(position).getVip()) {
				holder.cornorImg.setVisibility(View.VISIBLE);
				holder.cornorImg.setText("VIP");
			}
		}
		// if (videoId.equals(episodeInfos.get(position).getVideoId())) {
		// addDownlaodInfos.add(playVideoInfo);
		// }
		
		if (videoIdList != null && videoIdList.contains(playVideoInfo.getVideoId())) {
			holder.downloadCheck.setVisibility(View.VISIBLE);
			holder.downloadCheck.setImageResource(R.drawable.play_down_uncheck);
			holder.episodeTitle.setTextColor(context.getResources().getColor(R.color.code2));
		}else if (addDownlaodInfos.contains(playVideoInfo)) {
			holder.downloadCheck.setVisibility(View.VISIBLE);
			holder.downloadCheck.setImageResource(R.drawable.play_down_check);
			holder.episodeTitle.setTextColor(context.getResources().getColor(R.color.code3));
		} else {
			holder.downloadCheck.setVisibility(View.GONE);
		}
		holder.episodeTitle.setOnClickListener(new DownlaodClickListener(holder, playVideoInfo));
		return convertView;
	}

	class DownlaodClickListener implements OnClickListener {
		Holder viewHolder;
		PlayVideoInfo playVideoInfo;

		public DownlaodClickListener(Holder holder, PlayVideoInfo playVideoInfo) {
			this.viewHolder = holder;
			this.playVideoInfo = playVideoInfo;
		}

		@Override
		public void onClick(View v) {
			if (videoIdList != null && videoIdList.contains(playVideoInfo.getVideoId())) {
				context.showToastSafe(context.getResources().getString(R.string.play_download_hasdowload), Toast.LENGTH_SHORT);
				return;
			}
			if (!(playVideoInfo.getDownloadPlatform() != null
					&& playVideoInfo.getDownloadPlatform().contains("104002"))) {
				context.showToastSafe(context.getResources().getString(R.string.play_download_nodowload), Toast.LENGTH_SHORT);
				return;
			}
			if (addDownlaodInfos.contains(playVideoInfo)) {
				addDownlaodInfos.remove(playVideoInfo);
				viewHolder.downloadCheck.setVisibility(View.GONE);
			} else {
				addDownlaodInfos.add(playVideoInfo);
				viewHolder.downloadCheck.setVisibility(View.VISIBLE);
				viewHolder.downloadCheck.setImageResource(R.drawable.play_down_check);
			}
			handler.sendEmptyMessage(PlayDownloadEpisodeFragment.UPFATE_DOWNLOAD_TEXT_FLAG);
		}
	}

	class Holder {
		private Button episodeTitle;
		private TextView cornorImg;
		private ImageView downloadCheck;

		public Holder() {

		}
	}

	int getDownloadCount() {
		return addDownlaodInfos.size();
	}

	void addAllDownlaodInfos() {
		boolean hasDownload = false;
		boolean hasAuthority = true;
		addDownlaodInfos.clear();
		List<PlayVideoInfo> downloadEpisodeInfos = new ArrayList<PlayVideoInfo>();
		for (PlayVideoInfo info : episodeInfos) {
			if (videoIdList != null && videoIdList.contains(info.getVideoId())) {
				hasDownload = true;
			} else if (info.getDownloadPlatform() != null && !info.getDownloadPlatform().contains("104002")) {
				hasAuthority = false;
			} else {
				downloadEpisodeInfos.add(info);
			}
		}
		addDownlaodInfos.addAll(downloadEpisodeInfos);
		// if (hasDownload ||!hasAuthority) {
		// context.showToastSafe(context.getResources().getString(R.string.play_downlaod_all_hasdowload),
		// 1);
		// }
		if (hasDownload || !hasAuthority) {
			context.showToastSafe(context.getResources().getString(R.string.play_downlaod_all_nodowload),Toast.LENGTH_SHORT);
		}
	}

	void removeAllDownloadInfos() {
		addDownlaodInfos.clear();
	}

	public Set<PlayVideoInfo> getAddDownlaodInfos() {
		return addDownlaodInfos;
	}
}
