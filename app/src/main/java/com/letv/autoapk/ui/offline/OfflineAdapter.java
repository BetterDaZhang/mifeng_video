package com.letv.autoapk.ui.offline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lecloud.sdk.download.observer.LeDownloadObserver;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.DetailActivity;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.download.DownloadSaasCenter;
import com.letv.autoapk.download.LeOfflineInfo;
import com.letv.autoapk.ui.player.PlayerAPI;
import com.letv.autoapk.utils.NetworkUtils;

/**
 * 对下载的每个视频的专辑Id进行判断，如果专辑Id相同，那么归为一个专辑， <br>
 * 每次显示的时候，adapter都进行判断，相同Id只展示一个，然后点击这个<br>
 * 专辑封面， 进入专辑详情页，其中的视频通过数据库检索AlbumId得到。
 * 
 * 2015-12-10 17:25 pm 核心是sdk4.0的下载，就是展示问题。 <br>
 * 离线页面数据的展示，分为两类，album和非album， <br>
 * 针对两种数据类型，不同的ui展示方式，有两种不同的事件点击处理，<br>
 * 删除逻辑已经修改完成，还剩下具体两种ui怎么展示，比如专辑封面，专辑标题<br>
 * 还有专辑也点击进入新页面的ui。
 */
class OfflineAdapter extends BaseAdapter {
	/** list中每个info都是非专辑视频 */
	private List<LeOfflineInfo> offlineInfos;
	/** map中的每个list都是一个专辑 */
	private HashMap<String, List<LeOfflineInfo>> albumMap;
	/** 要删除的视频集合，LeDownloadInfo */
	private Set<LeOfflineInfo> deleteInfos;
	/** 要删除的专辑集合，LeDownloadInfo */
	private HashMap<String, List<LeOfflineInfo>> deleteList = new HashMap<String, List<LeOfflineInfo>>();
	private Context context;
	private boolean isDeleteState = false;
	private Handler handler;
	private DownloadSaasCenter downloadCenter;
	/** 离线类型，专辑 */
	private int OFFLINE_TYPE_ALBUM = 0;
	/** 离线类型，单个视频，非专辑 */
	private int OFFLINE_TYPE_SINGLE_VIDEO = 1;
	private List<List<LeOfflineInfo>> mapValuesList;

	public OfflineAdapter(Context ctx, List<LeOfflineInfo> offlineInfos, HashMap<String, List<LeOfflineInfo>> albumMap, Handler handler) {
		this.context = ctx;
		this.offlineInfos = offlineInfos;
		deleteInfos = new HashSet<LeOfflineInfo>();
		this.handler = handler;
		this.albumMap = albumMap;
		downloadCenter = DownloadSaasCenter.getInstances(ctx.getApplicationContext());
		downloadCenter.allowShowMsg(false);
		mapValuesList = new ArrayList<List<LeOfflineInfo>>(albumMap.values());
	}

	@Override
	public int getCount() {
		return offlineInfos.size() + albumMap.size();
	}

	/**
	 * 返回值不可用
	 */
	@Override
	public Object getItem(int index) {
		return null;
	}

	@Override
	public long getItemId(int itemId) {
		return itemId;
	}

	@Override
	public int getItemViewType(int position) {
		if (position < offlineInfos.size()) {
			return OFFLINE_TYPE_SINGLE_VIDEO;
		}
		return OFFLINE_TYPE_ALBUM;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup viewGroup) {
		OfflineViewHolder viewHolder = null;
		LeOfflineInfo info = null;
		if (position < offlineInfos.size()) {
			info = offlineInfos.get(position);
		} else {
			mapValuesList = new ArrayList<List<LeOfflineInfo>>(albumMap.values());
			List<LeOfflineInfo> albumList = mapValuesList.get(position - offlineInfos.size());
			info = albumList.get(0);
			info.setInAlbumList(true);
			Set<String> keySet = albumMap.keySet();
		}
		if (convertView != null) {
			viewHolder = (OfflineViewHolder) convertView.getTag();
			if (viewHolder.itemViewType != getItemViewType(position)) {
				convertView = null;
			}
		}
		if (convertView == null) {
			if (getItemViewType(position) == OFFLINE_TYPE_ALBUM) {
				convertView = LayoutInflater.from(context).inflate(R.layout.mine_offline_album_item, viewGroup, false);
				viewHolder = new OfflineViewHolder(convertView, context);
				viewHolder.itemViewType = OFFLINE_TYPE_ALBUM;
				convertView.setTag(viewHolder);
			} else {
				convertView = LayoutInflater.from(context).inflate(R.layout.mine_offline_item, viewGroup, false);
				viewHolder = new OfflineViewHolder(convertView, context);
				viewHolder.itemViewType = OFFLINE_TYPE_SINGLE_VIDEO;
				convertView.setTag(viewHolder);
			}
		}
		if (isDeleteState) {
			viewHolder.checkBox.setVisibility(View.VISIBLE);
			DeleteStateClickListener listener = new DeleteStateClickListener(viewHolder, info);
			if (getItemViewType(position) == OFFLINE_TYPE_ALBUM) {
				convertView.setOnClickListener(new DeleteStateAlbumClickListener(viewHolder, albumMap.get(info.getAlbumId())));
			} else {
				convertView.setOnClickListener(listener);
			}
			if (info.isInAlbumList()) {
				if (deleteList.containsKey(info.getAlbumId())) {
					viewHolder.checkBox.setChecked(true);
				} else {
					viewHolder.checkBox.setChecked(false);
				}
			} else {
				if (deleteInfos.contains(info)) {
					viewHolder.checkBox.setChecked(true);
				} else {
					viewHolder.checkBox.setChecked(false);
				}
			}
		} else {
			viewHolder.checkBox.setVisibility(View.GONE);
			if (getItemViewType(position) == OFFLINE_TYPE_ALBUM) {
				convertView.setOnClickListener(new UndeleteStateAlbumClickListener(albumMap.get(info.getAlbumId())));
			} else {
				convertView.setOnClickListener(new UndeleteStateClickListener(viewHolder, info));
			}
		}
		viewHolder.updateItem(info);
		viewHolder.updateItemState(info, albumMap.get(info.getAlbumId()));

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
		OfflineViewHolder viewHolder;
		LeOfflineInfo offlineInfo;

		public DeleteStateClickListener(OfflineViewHolder holder, LeOfflineInfo offlineInfo) {
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

	/** 删除模式 <b>专辑</b> 点击事件 */
	class DeleteStateAlbumClickListener implements OnClickListener {
		OfflineViewHolder viewHolder;
		List<LeOfflineInfo> infos;

		public DeleteStateAlbumClickListener(OfflineViewHolder holder, List<LeOfflineInfo> infos) {
			this.viewHolder = holder;
			this.infos = infos;
		}

		@Override
		public void onClick(View v) {
			String albumId = infos.get(0).getAlbumId();
			if (deleteList.containsKey(albumId)) {
				viewHolder.checkBox.setChecked(false);
				deleteList.remove(albumId);
			} else {
				deleteList.put(albumId, infos);
				viewHolder.checkBox.setChecked(true);
			}
			handler.sendEmptyMessage(OfflineFragment.UPFATE_DELETE_TEXT_FLAG);
			handler.sendEmptyMessage(OfflineFragment.UPDATE_CHECKALL_FLAG);
		}
	}

	/** 非删除模式 <b>普通视频</b> 点击事件 */
	class UndeleteStateClickListener implements OnClickListener {
		OfflineViewHolder viewHolder;
		LeOfflineInfo offlineInfo;

		public UndeleteStateClickListener(OfflineViewHolder holder, LeOfflineInfo offlineInfo) {
			this.viewHolder = holder;
			this.offlineInfo = offlineInfo;
		}

		@Override
		public void onClick(View v) {
			if (offlineInfo.getLeDownloadInfo().getDownloadState() == LeDownloadObserver.DOWLOAD_STATE_DOWNLOADING) {
				// Log.e("gsgs", "undelete state DOWLOAD_STATE_DOWNLOADING");
				downloadCenter.stopDownload(offlineInfo.getLeDownloadInfo());
			} else if (offlineInfo.getLeDownloadInfo().getDownloadState() == LeDownloadObserver.DOWLOAD_STATE_STOP) {
				// Log.e("gsgs", "undelete state DOWLOAD_STATE_STOP");
				String netType = NetworkUtils.getNetType(context);
				if (!NetworkUtils.Type_WIFI.equals(netType)) {
					// showPromptDialog(offlineInfo,viewHolder);
				} else {
					downloadCenter.resumeDownload(offlineInfo.getLeDownloadInfo());
					viewHolder.updateItemState(offlineInfo, albumMap.get(offlineInfo.getAlbumId()));
				}
			} else if (offlineInfo.getLeDownloadInfo().getDownloadState() == LeDownloadObserver.DOWLOAD_STATE_FAILED) {
				String netType = NetworkUtils.getNetType(context);
				if (!NetworkUtils.Type_WIFI.equals(netType)) {
					// showPromptDialog(offlineInfo,viewHolder);
				} else {
					downloadCenter.retryDownload(offlineInfo.getLeDownloadInfo());
//					viewHolder.updateItemState(offlineInfo, albumMap.get(offlineInfo.getAlbumId()));
				}
			} else if (offlineInfo.getLeDownloadInfo().getDownloadState() == LeDownloadObserver.DOWLOAD_STATE_SUCCESS) {
				// Log.e("gsgs", "undelete state DOWLOAD_STATE_SUCCESS");
				DisplayVideoInfo displayVideoInfo = offlineInfo.getDisplayVideoInfo();
				displayVideoInfo.setDetailType(20000);
				displayVideoInfo.setVideoId(offlineInfo.getLeDownloadInfo().getFileSavePath());
				PlayerAPI.startPlayActivity(context, displayVideoInfo);
			} else if (offlineInfo.getLeDownloadInfo().getDownloadState() == LeDownloadObserver.DOWLOAD_STATE_WAITING) {
				downloadCenter.resumeDownload(offlineInfo.getLeDownloadInfo());
			} else if (offlineInfo.getLeDownloadInfo().getDownloadState() == LeDownloadObserver.DOWLOAD_STATE_WAITING) {
				downloadCenter.resumeDownload(offlineInfo.getLeDownloadInfo());
			}
		}
	}

	/** 非删除模式 <b>专辑</b> 点击事件 */
	class UndeleteStateAlbumClickListener implements OnClickListener {
		List<LeOfflineInfo> albumList;

		public UndeleteStateAlbumClickListener(List<LeOfflineInfo> albumList) {
			this.albumList = albumList;
		}

		@Override
		public void onClick(View v) {
			// FragmentTransaction transation = ((BaseActivity)
			// context).getSupportFragmentManager().beginTransaction();
			// Fragment fragment = new OfflineAlbumFragment();
			// Bundle bundle = new Bundle();
			// bundle.putSerializable(OfflineAlbumFragment.ALBUM_LIST,
			// (Serializable) albumList);
			// fragment.setArguments(bundle);
			// transation.replace(R.id.container, fragment);
			// transation.commit();
			Intent intent = new Intent(context, DetailActivity.class);
			intent.putExtra(DetailActivity.FRAGMENTNAME, OfflineAlbumFragment.class.getName());
			MyApplication.getInstance().putInfo(OfflineAlbumFragment.ALBUM_LIST, albumList);
			context.startActivity(intent);
		}
	}

	public Set<LeOfflineInfo> getDeleteInfos() {
		return deleteInfos;
	}

	/** 获取要删除的视频的总数 */
	public int getDeleteCount() {
		HashMap<String, List<LeOfflineInfo>> deleteAlbums = getDeleteAlbums();
		Set<String> keySet = deleteAlbums.keySet();
		int videoCountInAlbums = 0;
		for (String key : keySet) {
			videoCountInAlbums += deleteAlbums.get(key).size();
		}
		return getDeleteInfos().size() + videoCountInAlbums;
	}

	public HashMap<String, List<LeOfflineInfo>> getDeleteAlbums() {
		return deleteList;
	}

	public void checkedAll() {
		deleteInfos.addAll(offlineInfos);
		deleteList.putAll(albumMap);
		notifyDataSetChanged();
		handler.sendEmptyMessage(OfflineFragment.UPFATE_DELETE_TEXT_FLAG);
	}

	public void removeAll() {
		deleteInfos.clear();
		deleteList.clear();
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

	public void setAlbumMap(HashMap<String, List<LeOfflineInfo>> albumMap) {
		this.albumMap = albumMap;
	}

	public HashMap<String, List<LeOfflineInfo>> getAlbumMap() {
		return albumMap;
	}
	
	/** 获取所有的视频的总数 */
	public int getOfflineCount() {
		HashMap<String, List<LeOfflineInfo>> offlineAlbums = getAlbumMap();
		Set<String> keySet = offlineAlbums.keySet();
		int videoCountInAlbums = 0;
		for (String key : keySet) {
			videoCountInAlbums += offlineAlbums.get(key).size();
		}
		return getOfflineInfos().size() + videoCountInAlbums;
	}

}
