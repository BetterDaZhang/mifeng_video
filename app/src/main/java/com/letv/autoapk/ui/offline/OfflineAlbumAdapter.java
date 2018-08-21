package com.letv.autoapk.ui.offline;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lecloud.sdk.download.control.DownloadCenter;
import com.lecloud.sdk.download.observer.LeDownloadObserver;
import com.letv.autoapk.R;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.download.LeOfflineInfo;
import com.letv.autoapk.ui.player.PlayerAPI;
import com.letv.autoapk.utils.NetworkUtils;

/**
 * 
 */
class OfflineAlbumAdapter extends BaseAdapter {
	/** list中每个info都是非专辑视频 */
	private List<LeOfflineInfo> offlineInfos;
	/** 要删除的视频集合，LeDownloadInfo */
	private Set<LeOfflineInfo> deleteInfos;
	private Context context;
	private boolean isDeleteState = false;
	private Handler handler;
	private DownloadCenter downloadCenter;

	public OfflineAlbumAdapter(Context ctx, List<LeOfflineInfo> offlineInfos, Handler handler) {
		this.context = ctx;
		this.offlineInfos = offlineInfos;
		deleteInfos = new HashSet<LeOfflineInfo>();
		this.handler = handler;
		downloadCenter = DownloadCenter.getInstances(ctx.getApplicationContext());
	}

	@Override
	public int getCount() {
		return offlineInfos.size();
	}

	/**
	 * 返回值不可用
	 */
	@Override
	public Object getItem(int index) {
		return offlineInfos.get(index);
	}

	@Override
	public long getItemId(int itemId) {
		return itemId;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup viewGroup) {
		OfflineAlbumViewHolder viewHolder;
		LeOfflineInfo info = offlineInfos.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.mine_offline_item, viewGroup, false);
			viewHolder = new OfflineAlbumViewHolder(convertView, context);
			convertView.setTag(viewHolder);
		}
		viewHolder = (OfflineAlbumViewHolder) convertView.getTag();

		if (isDeleteState) {
			viewHolder.checkBox.setVisibility(View.VISIBLE);
			DeleteStateClickListener listener = new DeleteStateClickListener(viewHolder, info);
			convertView.setOnClickListener(listener);
			viewHolder.checkBox.setOnClickListener(listener);
			if (deleteInfos.contains(info)) {
				viewHolder.checkBox.setChecked(true);
			} else {
				viewHolder.checkBox.setChecked(false);
			}
		} else {
			viewHolder.checkBox.setVisibility(View.GONE);
			convertView.setOnClickListener(new UndeleteStateClickListener(viewHolder, info));
		}
		viewHolder.updateItem(info);
		viewHolder.updateItemState(info, null);

		return convertView;
	}

	public boolean isDeleteState() {
		return isDeleteState;
	}

	public void setDeleteState(boolean isDeleteState) {
		this.isDeleteState = isDeleteState;
	}

	/** 删除模式 <b>非专辑视频</b> 点击事件 */
	class DeleteStateClickListener implements OnClickListener {
		OfflineAlbumViewHolder viewHolder;
		LeOfflineInfo offlineInfo;

		public DeleteStateClickListener(OfflineAlbumViewHolder holder, LeOfflineInfo offlineInfo) {
			this.viewHolder = holder;
			this.offlineInfo = offlineInfo;
		}

		@Override
		public void onClick(View v) {
			if (deleteInfos.contains(offlineInfo)) {
				viewHolder.checkBox.setChecked(false);
				deleteInfos.remove(offlineInfo);
			} else {
				deleteInfos.add(offlineInfo);
				viewHolder.checkBox.setChecked(true);
			}
			handler.sendEmptyMessage(OfflineFragment.UPFATE_DELETE_TEXT_FLAG);
			handler.sendEmptyMessage(OfflineFragment.UPDATE_CHECKALL_FLAG);

		}
	}

	/** 非删除模式 <b>普通视频</b> 点击事件 */
	class UndeleteStateClickListener implements OnClickListener {
		OfflineAlbumViewHolder viewHolder;
		LeOfflineInfo offlineInfo;

		public UndeleteStateClickListener(OfflineAlbumViewHolder holder, LeOfflineInfo offlineInfo) {
			this.viewHolder = holder;
			this.offlineInfo = offlineInfo;
		}

		@Override
		public void onClick(View v) {
			if (offlineInfo.getLeDownloadInfo().getDownloadState() == LeDownloadObserver.DOWLOAD_STATE_DOWNLOADING) {
				downloadCenter.stopDownload(offlineInfo.getLeDownloadInfo());
			} else if (offlineInfo.getDownloadState() == LeDownloadObserver.DOWLOAD_STATE_STOP) {
				String netType = NetworkUtils.getNetType(context);
				if (!NetworkUtils.Type_WIFI.equals(netType)) {
					// showPromptDialog(offlineInfo,viewHolder);
				} else {
					downloadCenter.resumeDownload(offlineInfo.getLeDownloadInfo());
				}
			} else if (offlineInfo.getLeDownloadInfo().getDownloadState() == LeDownloadObserver.DOWLOAD_STATE_FAILED) {
				String netType = NetworkUtils.getNetType(context);
				if (!NetworkUtils.Type_WIFI.equals(netType)) {
					// showPromptDialog(offlineInfo,viewHolder);
				} else {
					downloadCenter.retryDownload(offlineInfo.getLeDownloadInfo());
				}
			} else if (offlineInfo.getLeDownloadInfo().getDownloadState() == LeDownloadObserver.DOWLOAD_STATE_SUCCESS) {
				// Log.e("gsgs", "undelete state DOWLOAD_STATE_SUCCESS");
				DisplayVideoInfo displayVideoInfo = offlineInfo.getDisplayVideoInfo();
				displayVideoInfo.setDetailType(20000);
				displayVideoInfo.setVideoId(offlineInfo.getLeDownloadInfo().getFileSavePath());
				displayVideoInfo.setEpisode(offlineInfo.getEpisode());
				displayVideoInfo.setDisplayType(offlineInfo.getDisplayType());
				displayVideoInfo.setVideoId(offlineInfo.getLeDownloadInfo().getFileSavePath());
				MyApplication.getInstance().putInfo(OfflineAlbumFragment.ALBUM_LIST, offlineInfos);
				PlayerAPI.startPlayActivity(context, displayVideoInfo);
			}else if (offlineInfo.getLeDownloadInfo().getDownloadState() == LeDownloadObserver.DOWLOAD_STATE_WAITING) {
				downloadCenter.resumeDownload(offlineInfo.getLeDownloadInfo());
			}
		}
	}

	public Set<LeOfflineInfo> getDeleteInfos() {
		return deleteInfos;
	}

	/** 获取要删除的视频的总数 */
	public int getDeleteCount() {
		return getDeleteInfos().size();
	}

	public void checkedAll() {
		deleteInfos.addAll(offlineInfos);
		notifyDataSetChanged();
		handler.sendEmptyMessage(OfflineFragment.UPFATE_DELETE_TEXT_FLAG);
	}

	public void removeAll() {
		deleteInfos.clear();
		notifyDataSetChanged();
		handler.sendEmptyMessage(OfflineFragment.UPFATE_DELETE_TEXT_FLAG);
	}

	public int getItemPosition(LeOfflineInfo info) {
		return offlineInfos.indexOf(info);
	}

	public void setOfflineInfos(List<LeOfflineInfo> offlineInfos) {
		this.offlineInfos = offlineInfos;
	}

	public List<LeOfflineInfo> getOfflineInfos() {
		return offlineInfos;
	}

}
