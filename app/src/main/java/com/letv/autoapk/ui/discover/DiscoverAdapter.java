package com.letv.autoapk.ui.discover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.base.dialog.BaseDialog;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.CollectionRecordDao;
import com.letv.autoapk.dao.CollectionRecordInfo;
import com.letv.autoapk.download.DownloadSaasCenter;
import com.letv.autoapk.open.OpenShareActivity;
import com.letv.autoapk.open.OpenShareDialog;
import com.letv.autoapk.open.OpenShareDialog.OnShareListener;
import com.letv.autoapk.ui.player.PlayerAPI;
import com.letv.autoapk.widgets.NetImageView;

class DiscoverAdapter extends BaseAdapter implements OnClickListener {
	private static final String DiscoverHotInfo = null;
	private ArrayList<DiscoverHotInfo> finderHotList;
	private BaseActivity activity;
	private LayoutInflater inflater;
	private BaseTitleFragment fragment;
	DownloadSaasCenter downloadSaaSCenter;
	private CollectionRecordDao dao;
	private HashMap<String, List<Integer>> positionMap = new HashMap<String, List<Integer>>();

	DiscoverAdapter(BaseActivity activity, BaseTitleFragment frag, ArrayList<DiscoverHotInfo> finderHotList) {
		downloadSaaSCenter = DownloadSaasCenter.getInstances(activity);
		downloadSaaSCenter.allowShowMsg(false);
		this.activity = activity;
		this.finderHotList = finderHotList;
		inflater = LayoutInflater.from(activity);
		dao = MyApplication.getInstance().getDaoByKey(CollectionRecordDao.class.getName());
		getPositionMap();
		this.fragment = frag;
	}

	private void getPositionMap() {
		for (int i = 0; i < finderHotList.size(); i++) {
			String albumId = finderHotList.get(i).getDisplayVideoInfo().getAlbumId();
			List<Integer> albumList = positionMap.get(albumId);
			if (albumList == null) {
				albumList = new ArrayList<Integer>();
			}
			albumList.add(i);
			if (!"0".endsWith(albumId)) {
				positionMap.put(albumId, albumList);
			}
		}
	}

	@Override
	public int getCount() {
		return finderHotList.size();
	}

	public void setVideoList(ArrayList<DiscoverHotInfo> finderHotList) {
		this.finderHotList = finderHotList;
		notifyDataSetChanged();
	}

	@Override
	public DiscoverHotInfo getItem(int position) {
		return finderHotList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	protected View getMyView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(activity).inflate(R.layout.discover_bottom_item, null);
			holder = new Holder();
			holder.share = (TextView) convertView.findViewById(R.id.share);
			holder.prise = (TextView) convertView.findViewById(R.id.prise);
			holder.comment = (TextView) convertView.findViewById(R.id.comment);
			holder.collection = (TextView) convertView.findViewById(R.id.collection);
			holder.image = (NetImageView) convertView.findViewById(R.id.icon);
			holder.title = (TextView) convertView.findViewById(R.id.tv_title);
			holder.content = (TextView) convertView.findViewById(R.id.tv_content);
			holder.functionList = (LinearLayout) convertView.findViewById(R.id.function_list);
			holder.image.setErrorImageResId(R.drawable.default_img_16_10);
			holder.image.setDefaultImageResId(R.drawable.default_img_16_10);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		DiscoverHotInfo info = finderHotList.get(position);
		DisplayVideoInfo displayVideoInfo = info.getDisplayVideoInfo();
		if (21 == displayVideoInfo.getDetailType()) {
			holder.collection.setVisibility(View.GONE);
		} else {
			holder.collection.setVisibility(View.VISIBLE);
		}
		if (33 == displayVideoInfo.getDetailType() || 24 == displayVideoInfo.getDetailType()
				|| 31 == displayVideoInfo.getDetailType() || 32 == displayVideoInfo.getDetailType()) {
			holder.functionList.setVisibility(View.GONE);
		} else if (11 == displayVideoInfo.getDetailType()) {
			holder.functionList.setVisibility(View.VISIBLE);
			holder.collection.setVisibility(View.VISIBLE);
			holder.prise.setVisibility(View.VISIBLE);
			holder.comment.setVisibility(View.VISIBLE);
		} else if (21 == displayVideoInfo.getDetailType()) {// 21直播
			holder.functionList.setVisibility(View.VISIBLE);
			holder.collection.setVisibility(View.GONE);
			holder.prise.setVisibility(View.VISIBLE);
			holder.comment.setVisibility(View.VISIBLE);
		} else {// 22音频,23卫视直播
			holder.functionList.setVisibility(View.VISIBLE);
			holder.collection.setVisibility(View.GONE);
			holder.prise.setVisibility(View.GONE);
			holder.comment.setVisibility(View.GONE);
		}
		holder.image.setTag(displayVideoInfo);
		setDefaultData(holder.image);
		holder.image.setCoverUrl(displayVideoInfo.getImageUrl(), activity);
		holder.title.setText(displayVideoInfo.getVideoTitle());
		if (TextUtils.isEmpty(displayVideoInfo.getVideoDesc())) {
			holder.content.setVisibility(View.GONE);
		} else {
			holder.content.setVisibility(View.VISIBLE);
			holder.content.setText(displayVideoInfo.getVideoDesc());
		}

		holder.share.setTag(displayVideoInfo);
		holder.comment.setTag(displayVideoInfo.getVideoId());
		holder.comment.setText(PlayerAPI.formatCount(info.commentCount));
		holder.collection.setTag(info);
		String albumId = info.getDisplayVideoInfo().getAlbumId();
		String videoId = info.getDisplayVideoInfo().getVideoId();
		if (info.hasCollect == 1) {
			holder.collection.setBackgroundResource(R.drawable.discover_collection_p);
		} else {
			holder.collection.setBackgroundResource(R.drawable.discover_collection);
		}
		Drawable drawable = activity.getResources().getDrawable(R.drawable.discover_zan_gray);
		if (info.isHasSupport()) {
			drawable = activity.getResources().getDrawable(R.drawable.discover_zan_oriange);
		}
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		holder.prise.setCompoundDrawables(drawable, null, null, null);
		holder.prise.setText(PlayerAPI.formatCount(info.getSupportCount()));
		holder.prise.setTag(info);
		holder.collection.setOnClickListener(this);
		holder.image.setOnClickListener(this);
		holder.share.setOnClickListener(this);
		holder.comment.setOnClickListener(this);
		holder.prise.setOnClickListener(this);
		return convertView;
	}

	@Override
	public void onClick(final View v) {
		boolean isNoNetwork = PlayerAPI.addNoNetworkLimit(activity);
		switch (v.getId()) {
		case R.id.icon:
			PlayerAPI.startPlayActivity(activity, (DisplayVideoInfo) v.getTag());
			break;
		case R.id.share:
			if (isNoNetwork) {
				return;
			}
			if (activity instanceof OpenShareActivity) {
				final DisplayVideoInfo info = (DisplayVideoInfo) v.getTag();
				if (info == null)
					return;
				if (!MyApplication.getInstance().getOpenSdk().hasOpenId()) {
					activity.showToastSafe(activity.getString(R.string.pleasewait), Toast.LENGTH_SHORT);
					return;
				}
				final OpenShareActivity shareActivity = (OpenShareActivity) activity;
				OpenShareDialog dialog = new OpenShareDialog();
				dialog.setOnShareListener(new OnShareListener() {

					@Override
					public void doShare(int openType) {
						// TODO Auto-generated method stub
						shareActivity.doshare(openType, info.getShareUrl(), info.getVideoTitle(), info.getVideoDesc(),
								info.getImageUrl());
					}
				});
				BaseDialog.show(shareActivity.getSupportFragmentManager(), dialog);

			}
			break;
		case R.id.comment:
			break;
		case R.id.collection:
			final DiscoverHotInfo discoverHotInfo = (DiscoverHotInfo) v.getTag();
			final DisplayVideoInfo displayVideoInfo = discoverHotInfo.getDisplayVideoInfo();

			final List<CollectionRecordInfo> collectionRecordInfos = new ArrayList<CollectionRecordInfo>();
			final CollectionRecordInfo collectionRecordInfo = new CollectionRecordInfo();
			final String albumId = displayVideoInfo.getAlbumId();
			String videoId = displayVideoInfo.getVideoId();
			collectionRecordInfo.setVideoId(displayVideoInfo.getVideoId());
			collectionRecordInfo.setAlbumId(albumId);
			collectionRecordInfo.setVideoImage(displayVideoInfo.getImageUrl());
			collectionRecordInfo.setVideoTitle(displayVideoInfo.getVideoTitle());
			collectionRecordInfo.setPlayTimes(displayVideoInfo.getPlayTimes());
			collectionRecordInfos.add(collectionRecordInfo);
			if (!MyApplication.getInstance().isLogin()) {// 未登录
				CollectionRecordInfo info;
				if (albumId != null && !albumId.isEmpty() && !"0".equals(albumId)) {
					// 按专辑
					info = dao.findBySpecialColum("albumId", albumId);
				} else {
					info = dao.findBySpecialColum("videoId", videoId);
				}
				if (info != null) {// 取关
					CollectionRecordInfo daoInfo = dao.delete(info);
					if (daoInfo == null) {
						activity.showToastSafe(activity.getString(R.string.delete_failed), 1);
						v.setBackgroundResource(R.drawable.play_collected);
					} else {
						discoverHotInfo.hasCollect = 0;
						notifyCollectStatus(albumId, 0);
						activity.showToastSafe(activity.getString(R.string.delete_ok), 1);
						v.setBackgroundResource(R.drawable.play_collect);
					}
				} else {// 收藏
					boolean collectSuccess = false;
					if (!TextUtils.isEmpty(albumId) && !"0".equals(albumId)) {// 按专辑
						collectionRecordInfo.setVideoImage(displayVideoInfo.getAlbumPicUrl());
						collectionRecordInfo.setVideoTitle(displayVideoInfo.getmAlbumName());
						collectSuccess = dao.save(collectionRecordInfo);
					} else {// 按单片
						collectSuccess = dao.save(collectionRecordInfo);
					}
					if (collectSuccess) {
						discoverHotInfo.hasCollect = 1;
						notifyCollectStatus(albumId, 1);
						activity.showToastSafe(activity.getString(R.string.collect_ok), 1);
						v.setBackgroundResource(R.drawable.play_collected);
					} else {
						activity.showToastSafe(activity.getString(R.string.collect_failed), 1);
						v.setBackgroundResource(R.drawable.play_collect);
					}
				}
				return;
			}
			if (isNoNetwork) {
				return;
			}
			if (discoverHotInfo != null) {
				final OpCollectRecordsRequest request = new OpCollectRecordsRequest(activity);
				if (discoverHotInfo.hasCollect == 0) {
					new UiAsyncTask<Integer>(fragment) {
						@Override
						protected Integer doBackground() throws Throwable {
							return request.collectRecords(activity, collectionRecordInfos);
						}

						@Override
						protected void post(Integer result) {
							if (result == 0) {
								activity.showToastSafe(activity.getString(R.string.collect_ok), 1);
								// getCollectionRequest(describeInfo.getVideoId(),
								// describeInfo.getAblumId());
								discoverHotInfo.hasCollect = 1;
								notifyCollectStatus(albumId, 1);
								v.setBackgroundResource(R.drawable.play_collected);
							} else {
								v.setBackgroundResource(R.drawable.play_collect);
								activity.showToastSafe(activity.getString(R.string.collect_failed), 1);
							}
							super.post(result);
						}
					}.execute();
				} else {
					new UiAsyncTask<Integer>(fragment) {

						@Override
						protected Integer doBackground() throws Throwable {
							return request.unCollectRecords(activity, collectionRecordInfos);
						}

						@Override
						protected void post(Integer result) {
							if (result == 0) {
								activity.showToastSafe(activity.getString(R.string.delete_ok), 1);
								v.setBackgroundResource(R.drawable.play_collect);
								discoverHotInfo.hasCollect = 0;
								notifyCollectStatus(albumId, 0);
								// getCollectionRequest(describeInfo.getVideoId(),
								// describeInfo.getAblumId());
							} else {
								v.setBackgroundResource(R.drawable.play_collected);
								activity.showToastSafe(activity.getString(R.string.delete_failed), 1);
							}
							super.post(result);
						}

					}.execute();
				}
			}

			break;
		case R.id.prise:
			// 点赞
			if (isNoNetwork) {
				return;
			}
			final DiscoverHotInfo discoverHotInfo2 = (DiscoverHotInfo) v.getTag();
			if (discoverHotInfo2.isHasSupport()) {
				activity.showToastSafe(R.string.play_prise_repeat_toast, Toast.LENGTH_SHORT);
				return;
			}
			new UiAsyncTask<Boolean>(fragment) {

				@Override
				protected Boolean doBackground() {
					// 专辑点赞
					PraiseUtils praiseUtils = new PraiseUtils();
					String target = discoverHotInfo2.getDisplayVideoInfo().getVideoId();
					String type = "1";
					// if (albumId != null && "0".equals(albumId)) {
					// // 专辑点赞
					// type = "2";
					// String albumId =
					// discoverHotInfo.getDisplayVideoInfo().getAblumId();
					// target = albumId;
					// }
					boolean doSupport = praiseUtils.sendPraiseRequest(type, "1", target,
							LoginInfoUtil.getUserId(activity), MyApplication.getInstance().getTenantId(), activity);

					return doSupport;
				}

				protected void post(Boolean result) {

				};
			}.execute();
			if (!discoverHotInfo2.isHasSupport()) {
				Drawable drawable = activity.getResources().getDrawable(R.drawable.discover_zan_oriange);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				((TextView) v).setCompoundDrawables(drawable, null, null, null);
				((TextView) v).setText(PlayerAPI.formatCount(discoverHotInfo2.getSupportCount() + 1));
				discoverHotInfo2.setHasSupport(true);
			}
			break;
		default:
			break;
		}
	}

	private void notifyCollectStatus(String albumId, int status) {
		List<Integer> positionList = positionMap.get(albumId);
		if (positionList == null) {
			return;
		}
		for (int position : positionList) {
			finderHotList.get(position).hasCollect = status;
		}
		notifyDataSetChanged();
	}

	void setDefaultData(NetImageView img) {
		img.setDefaultImageResId(R.drawable.default_img_22_10);
		img.setErrorImageResId(R.drawable.default_img_22_10);
	}

}

class Holder {
	TextView share;
	TextView prise;
	TextView comment;
	TextView collection;
	NetImageView image;
	TextView title;
	TextView content;
	LinearLayout functionList;

}
