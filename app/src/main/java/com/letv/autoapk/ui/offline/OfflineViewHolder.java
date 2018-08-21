package com.letv.autoapk.ui.offline;

import java.util.List;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.lecloud.sdk.download.observer.LeDownloadObserver;
import com.letv.autoapk.R;
import com.letv.autoapk.download.DownloadSaasCenter;
import com.letv.autoapk.download.LeOfflineInfo;
import com.letv.autoapk.utils.SystemUtls;
import com.letv.autoapk.widgets.HorizontalProgressBarWithNumber;
import com.letv.autoapk.widgets.NetImageView16_10;

class OfflineViewHolder {
	public CheckBox checkBox;
	public NetImageView16_10 img;
	public TextView title;
	public ImageView mineOfflineItemStateImage;
	public TextView mineOfflineItemStateText;
	public TextView downloadRate;
	public TextView downloadSize;
	public Context context;
	private long lastTotalRxBytes = 0;
	private long lastTimeStamp = 0;
	private DownloadSaasCenter downloadCenter;
	private HorizontalProgressBarWithNumber downloadProgress;
	public int itemViewType;

	public OfflineViewHolder(View view, Context ctx) {
		this.context = ctx;
		this.checkBox = (CheckBox) view.findViewById(R.id.offline_checkbox);
		this.img = (NetImageView16_10) view.findViewById(R.id.iv_offline_item_img);
		this.title = (TextView) view.findViewById(R.id.offline_item_title);
		this.downloadRate = (TextView) view.findViewById(R.id.offline_item_rate);
		this.downloadSize = (TextView) view.findViewById(R.id.offline_item_downloadsize);
		this.mineOfflineItemStateImage = (ImageView) view.findViewById(R.id.mine_offline_item_state_image);
		this.mineOfflineItemStateText = (TextView) view.findViewById(R.id.mine_offline_item_state_text);
		this.downloadProgress = (HorizontalProgressBarWithNumber) view.findViewById(R.id.download_progress);
		downloadCenter = DownloadSaasCenter.getInstances(ctx.getApplicationContext());
		downloadCenter.allowShowMsg(false);
	}

	public LeOfflineInfo cloneLeDownloadInfo(OfflineAlbumAdapter offlineLineAdatpter, LeOfflineInfo info, int position) {
		offlineLineAdatpter.getOfflineInfos().get(position).setDownloadState(info.getDownloadState());
		offlineLineAdatpter.getOfflineInfos().get(position).setFileLength(info.getFileLength());
		offlineLineAdatpter.getOfflineInfos().get(position).setProgress(info.getProgress());
		offlineLineAdatpter.getOfflineInfos().get(position).setVideoTitle(info.getVideoTitle());
		offlineLineAdatpter.getOfflineInfos().get(position).setImgUrl(info.getImgUrl());
		offlineLineAdatpter.getOfflineInfos().get(position).setFileSavePath(info.getFileSavePath());
		offlineLineAdatpter.getOfflineInfos().get(position).setVedioId(info.getVedioId());
		offlineLineAdatpter.getOfflineInfos().get(position).setDownloadUrl(info.getDownloadUrl());
		offlineLineAdatpter.getOfflineInfos().get(position).setDownloadUrlGroup(info.getDownloadUrlGroup());
		offlineLineAdatpter.getOfflineInfos().get(position).setLeDownloadInfo(info.getLeDownloadInfo());
		return offlineLineAdatpter.getOfflineInfos().get(position);
	}

	public LeOfflineInfo cloneLeDownloadInfo(OfflineAdapter offlineAdapter, LeOfflineInfo info, int position) {
		offlineAdapter.getOfflineInfos().get(position).setDownloadState(info.getDownloadState());
		offlineAdapter.getOfflineInfos().get(position).setFileLength(info.getFileLength());
		offlineAdapter.getOfflineInfos().get(position).setProgress(info.getProgress());
		offlineAdapter.getOfflineInfos().get(position).setVideoTitle(info.getVideoTitle());
		offlineAdapter.getOfflineInfos().get(position).setImgUrl(info.getImgUrl());
		offlineAdapter.getOfflineInfos().get(position).setFileSavePath(info.getFileSavePath());
		offlineAdapter.getOfflineInfos().get(position).setVedioId(info.getVedioId());
		offlineAdapter.getOfflineInfos().get(position).setDownloadUrl(info.getDownloadUrl());
		offlineAdapter.getOfflineInfos().get(position).setDownloadUrlGroup(info.getDownloadUrlGroup());
		offlineAdapter.getOfflineInfos().get(position).setLeDownloadInfo(info.getLeDownloadInfo());
		return offlineAdapter.getOfflineInfos().get(position);
	}

	public void updateItem(LeOfflineInfo info) {
		img.setVisibility(View.VISIBLE);
		img.setDefaultImageResId(R.drawable.default_img_16_10);
		img.setErrorImageResId(R.drawable.default_img_16_10);
		if (info.isInAlbumList()) {
			img.setCoverUrl(info.getAlbumPicUrl(), context);
			title.setText(info.getAlbumName());
		} else {
			img.setCoverUrl(info.getImgUrl(), context);
			title.setText(info.getVideoTitle());
		}
	}

	public void updateItemState(LeOfflineInfo info, List<LeOfflineInfo> albumList) {
		downloadRate.setVisibility(View.VISIBLE);
//		downloadRate.setBackgroundColor(context.getResources().getColor(R.color.code06));
		downloadRate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
		downloadRate.setPadding(0, 0, 0, 0);
		if (info.isInAlbumList() && albumList != null) {
			long albumSize = 0;
			for (LeOfflineInfo leOfflineInfo : albumList) {
				albumSize += leOfflineInfo.getFileLength();
			}
			downloadRate.setText(albumList.size() + context.getResources().getString(R.string.mine_downloading_video_num)
					+ SystemUtls.getPrintSize(albumSize));
			return;
		}
//		downloadSize.setBackgroundColor(context.getResources().getColor(R.color.code06));
		downloadSize.setCompoundDrawables(null, null, null, null);
		downloadSize.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
		downloadSize.setVisibility(View.VISIBLE);
		String downloadedLength = SystemUtls.getPrintSize(info.getProgress());
		String totalLenth = SystemUtls.getPrintSize(info.getFileLength());
		downloadSize.setText(downloadedLength + "/" + totalLenth);
		downloadProgress.setVisibility(View.VISIBLE);
		mineOfflineItemStateImage.setVisibility(View.VISIBLE);
		mineOfflineItemStateText.setVisibility(View.VISIBLE);
		if (info.getFileLength() != 0) {
			downloadProgress.setProgress((int) (info.getProgress() * 100 / info.getFileLength()));
		} else {
			downloadProgress.setProgress((int) (0));
		}
		switch (info.getDownloadState()) {
		case LeDownloadObserver.DOWLOAD_STATE_WAITING:
			updateDownloadWaitingUI(info);
			break;
		case LeDownloadObserver.DOWLOAD_STATE_DOWNLOADING:
			if (info.getFileLength() != 0) {
				updateDownloadingUI(info);
			} else {
				updateDownloadWaitingUI(info);
			}
			break;
		case LeDownloadObserver.DOWLOAD_STATE_STOP:
			updateDownloadStopUI(info);
			break;
		case LeDownloadObserver.DOWLOAD_STATE_SUCCESS:
			updateDownloadSuccessUI(info);
			break;
		case LeDownloadObserver.DOWLOAD_STATE_FAILED:
			updateDownloadFailed(info);
			break;
		default:
			updateDownloadFailed(info);
			break;
		}
	}

	private void updateDownloadWaitingUI(LeOfflineInfo info) {
		downloadRate.setVisibility(View.INVISIBLE);
		downloadSize.setText("0M/0M");
		mineOfflineItemStateImage.setBackgroundResource(R.drawable.mine_offline_wait);
		mineOfflineItemStateText.setText(R.string.mine_waiting);
	}

	private void updateDownloadingUI(LeOfflineInfo info) {
		downloadRate.setVisibility(View.VISIBLE);
		downloadSize.setVisibility(View.VISIBLE);
		String rate = getDownloadRate(info);
		downloadRate.setText(rate + "/s");
		mineOfflineItemStateImage.setBackgroundResource(R.drawable.mine_downloading);
		mineOfflineItemStateText.setText(R.string.mine_downloading);
	}

	private void updateDownloadFailed(LeOfflineInfo info) {
		downloadRate.setVisibility(View.INVISIBLE);
		downloadSize.setVisibility(View.VISIBLE);
		downloadSize.setText(context.getResources().getString(R.string.downlaod_failed));
		mineOfflineItemStateImage.setVisibility(View.GONE);
		mineOfflineItemStateText.setVisibility(View.GONE);
	}

	private void updateDownloadStopUI(LeOfflineInfo info) {
		downloadRate.setVisibility(View.INVISIBLE);
		mineOfflineItemStateImage.setBackgroundResource(R.drawable.mine_offline_pause);
		mineOfflineItemStateText.setText(R.string.mine_stopped);

		if (info.getFileLength() == 0) {
			downloadSize.setVisibility(View.INVISIBLE);
		} else {
			downloadSize.setVisibility(View.VISIBLE);
		}
	}

	private void updateDownloadSuccessUI(LeOfflineInfo info) {
		downloadRate.setVisibility(View.INVISIBLE);
		downloadSize.setText(SystemUtls.getPrintSize(info.getFileLength()));
		downloadProgress.setVisibility(View.GONE);
		mineOfflineItemStateImage.setVisibility(View.GONE);
		mineOfflineItemStateText.setVisibility(View.GONE);
	}

	public String getDownloadRate(LeOfflineInfo info) {
		String formatSpeed;
		long speed = 0;
		long nowTotalRxBytes = info.getProgress();
		long nowTimeStamp = System.currentTimeMillis();
		if ((nowTimeStamp - lastTimeStamp) != 0) {
			// Log.i("OfflineViewHolder", nowTotalRxBytes - lastTotalRxBytes +
			// "<>" + (nowTimeStamp - lastTimeStamp));
			speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));// 毫秒转换
		} else {
			speed = nowTotalRxBytes - lastTotalRxBytes;
		}
		formatSpeed = SystemUtls.getPrintSize(speed < 0 ? 0 : speed);
		lastTimeStamp = nowTimeStamp;
		lastTotalRxBytes = nowTotalRxBytes;
		// Log.i("OfflineViewHolder", "速率 " + formatSpeed);
		return formatSpeed;
	}

}
