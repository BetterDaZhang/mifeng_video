package com.letv.autoapk.ui.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.ui.player.PlayDownlaodEpisodeAdapter.Holder;
import com.letv.autoapk.utils.NetworkUtils;
import com.letv.autoapk.utils.SystemUtls;
import com.letv.autoapk.widgets.NetImageView;

public class PlayDownlaodArtsAdapter extends BaseAdapter {
	private BaseActivity context;
	private Handler handler;
	private List<PlayVideoInfo> artsInfos;
	private String videoId;
	private Set<PlayVideoInfo> addDownlaodInfos;
	private List<String> videoIdList;

	public PlayDownlaodArtsAdapter(BaseActivity ctx, Handler handler, List<PlayVideoInfo> infos, String videoId,
			List<String> videoIdList) {
		addDownlaodInfos = new HashSet<PlayVideoInfo>();
		this.context = ctx;
		this.handler = handler;
		this.artsInfos = infos;
		this.videoId = videoId;
		this.videoIdList = videoIdList;
	}

	@Override
	public int getCount() {
		return artsInfos.size();
	}

	@Override
	public PlayVideoInfo getItem(int position) {
		return artsInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	protected View getMyView(final int position, View convertView, ViewGroup parent) {
		PlayVideoInfo playVideoInfo;
		Holder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.play_downlaod_arts_item, null);
			holder = new Holder();
			holder.netIamgevew = (NetImageView) convertView.findViewById(R.id.play_arts_img);
			holder.artsTitle = (TextView) convertView.findViewById(R.id.play_arts_item_title);
			holder.artsDirector = (TextView) convertView.findViewById(R.id.play_arts_item_director);
			holder.artsActor = (TextView) convertView.findViewById(R.id.play_arts_item_actor);
			holder.playCounts = (TextView) convertView.findViewById(R.id.play_arts_item_playTimes);
			holder.downloadCheck = (ImageView) convertView.findViewById(R.id.play_download_arts_check);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		playVideoInfo = artsInfos.get(position);
		holder.artsTitle.setText(playVideoInfo.getVideoType());
		holder.artsDirector.setText(playVideoInfo.getVideoDirector());
		holder.artsActor.setText(playVideoInfo.getVideoActor());
		// holder.playCounts.setText();
		holder.playCounts.setVisibility(View.GONE);
		View[] views = new View[] { holder.netIamgevew, holder.artsTitle };
		holder.netIamgevew.setDefaultImageResId(R.drawable.default_img_16_10);
		holder.netIamgevew.setErrorImageResId(R.drawable.default_img_16_10);
		PlayerAPI.initNetImgView(context, artsInfos.get(position), views);
		// if (videoId.equals(artsInfos.get(position).getVideoId())) {
		// convertView.setBackgroundColor(context.getResources().getColor(R.color.code08));
		// }
		
		if (videoIdList != null && videoIdList.contains(playVideoInfo.getVideoId())) {
			holder.downloadCheck.setVisibility(View.VISIBLE);
			holder.downloadCheck.setImageResource(R.drawable.play_down_uncheck);
			holder.artsTitle.setTextColor(context.getResources().getColor(R.color.code4));
		}else if (addDownlaodInfos.contains(playVideoInfo)) {
			holder.downloadCheck.setVisibility(View.VISIBLE);
			holder.downloadCheck.setImageResource(R.drawable.play_down_check);
			holder.artsTitle.setTextColor(context.getResources().getColor(R.color.code3));
		}else {
			holder.downloadCheck.setVisibility(View.GONE);
		}
		convertView.setOnClickListener(new DownlaodClickListener(holder, playVideoInfo));
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
			//是否是已经下载的视频
			if (videoIdList != null && videoIdList.contains(playVideoInfo.getVideoId())) {
				context.showToastSafe(context.getResources().getString(R.string.play_download_hasdowload),
						Toast.LENGTH_SHORT);
				return;
			}
			//是否在该平台有权限
			if (!(playVideoInfo.getDownloadPlatform() != null
					&& playVideoInfo.getDownloadPlatform().contains("104002"))) {
				context.showToastSafe(context.getResources().getString(R.string.play_download_nodowload),
						Toast.LENGTH_SHORT);
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
		private NetImageView netIamgevew;
		private TextView artsTitle;
		private TextView artsDirector;
		private TextView artsActor;
		private TextView playCounts;
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
		List<PlayVideoInfo> downloadArtInfos = new ArrayList<PlayVideoInfo>();
		for (PlayVideoInfo info : artsInfos) {
			if (videoIdList != null && videoIdList.contains(info.getVideoId())) {
				hasDownload = true;
			} else if (!(info.getDownloadPlatform() != null && info.getDownloadPlatform().contains("104002"))) {
				hasAuthority = false;
			} else {
				downloadArtInfos.add(info);
			}
		}
		addDownlaodInfos.addAll(downloadArtInfos);
		// if (hasDownload) {
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

	public Set<PlayVideoInfo> getAddDownlaodInfos(HashSet<PlayVideoInfo> infos) {
		infos.addAll(addDownlaodInfos);
		return infos;
	}

	public Set<PlayVideoInfo> getAddDownlaodInfos() {
		return addDownlaodInfos;
	}

}
