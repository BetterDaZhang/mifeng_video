package com.letv.autoapk.ui.me;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.fragment.BaseTitleFragment.TitleLeftClickListener;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.base.net.LoginInfo;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.boss.LepayManager;
import com.letv.autoapk.boss.VipInfo;
import com.letv.autoapk.common.net.LruImageCache;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.PlayRecordInfo;
import com.letv.autoapk.dao.PlayedRecordDao;
import com.letv.autoapk.ui.collection.CollectionAPI;
import com.letv.autoapk.ui.login.LoginAPI;
import com.letv.autoapk.ui.offline.OfflineAPI;
import com.letv.autoapk.ui.player.PlayerAPI;
import com.letv.autoapk.ui.record.RecordsAPI;
import com.letv.autoapk.ui.search.SearchAPI;
import com.letv.autoapk.utils.ScreenUtils;
import com.letv.autoapk.utils.SerializeableUtil;
import com.letv.autoapk.widgets.NetImageView;

public class MineFragment extends BaseTitleFragment implements OnClickListener, OnItemClickListener {
	private ImageView userIcon;// 用户头像
	private boolean isLogin;
	// private ImageView iv_new_msg_tip;// 新消息tip
	// private View rl_msg;
	private TextView loginText;
	private LoginInfo loginInfo;
	private View root;
	// private int height;
	// private int height_42;
	// private List<PlayRecordInfo> records;
	private ArrayList<LoginInfo> infos;
	private ImageView ivVip;
	private TextView getVip;
	private List<VipInfo> vipInfos;
	private RelativeLayout loginRel;

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	protected boolean loadingData() {
		infos = new ArrayList<LoginInfo>();
		vipInfos = new ArrayList<VipInfo>();
		UserInfoDataRequest request = new UserInfoDataRequest(mActivity);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputParam.put("isaddboss", MyApplication.getInstance().isNeedBoss() + "");
		int code = request.setInputParam(mInputParam).setOutputData(infos, vipInfos).request(Request.Method.GET);// infos.get(0)就是用户登录信息
		loginInfo = infos.get(0);
		if (code == 0 && infos != null) {
			SerializeableUtil.saveObject(mActivity, MyApplication.USER_INFO, loginInfo);
			MyApplication.getInstance().putInfo(MyApplication.VIPINFO, vipInfos);
		}
		return true;
	}

	@Override
	protected void initCustomerView() {
		// new UiAsyncTask<Boolean>(this) {
		// private PlayedRecordDao dao;
		//
		// @Override
		// protected Boolean doBackground() throws Throwable {
		// // dao =
		// MyApplication.getInstance().getDaoByKey(PlayedRecordDao.class.getName());
		// // records = new
		// RecordsUtils(MineFragment.this).getRecords(mActivity);
		// return null;
		// }
		//
		// protected void post(Boolean result) {
		// dao.save(records);
		// };
		// };
		setLeftClickListener(new TitleLeftClickListener() {

			@Override
			public void onLeftClickListener() {
				// 跳转到搜索
				SearchAPI.startSearch(mActivity);

			}
		});
		setTitleLeftResource(R.drawable.recommend_search_head, mActivity.dip2px(16));

	}

	@Override
	protected View createContentView() {
		return setupDataView();
	}

	@Override
	protected View setupDataView() {
		isLogin = MyApplication.getInstance().isLogin();
		setTitle(getResString(R.string.mine_title), getResources().getColor(R.color.code6));
		root = View.inflate(mActivity, R.layout.mine_fragement, null);
		loginText = (TextView) root.findViewById(R.id.tv_not_login);
		userIcon = (ImageView) root.findViewById(R.id.user_icon);
		ivVip = (ImageView) root.findViewById(R.id.iv_vip);
		getVip = (TextView) root.findViewById(R.id.tv_get_vip);
		loginRel = (RelativeLayout) root.findViewById(R.id.login_rl);
		root.findViewById(R.id.ll_collect).setOnClickListener(this);
		root.findViewById(R.id.ll_offline).setOnClickListener(this);
		root.findViewById(R.id.ll_setting).setOnClickListener(this);
		root.findViewById(R.id.tv_vip_center).setOnClickListener(this);
		loginRel.setOnClickListener(this);
		userIcon.setOnClickListener(this);
		loginText.setOnClickListener(this);
		getVip.setOnClickListener(this);
		bossUiSetting();
		loadUserInfo();
		// initPlayRecords(root);
		View history = root.findViewById(R.id.ll_history);
		history.setOnClickListener(this);
		return root;
	}

	/**
	 * boss功能设置，对相关页面进行隐藏显示
	 */
	private void bossUiSetting() {
		if (MyApplication.getInstance().isNeedBoss() == 0) {
			root.findViewById(R.id.tv_vip_center).setVisibility(View.GONE);
		}
	}

	/*
	 * private void initPlayRecords(View root) { height =
	 * ScreenUtils.getTwoColsHight16_10(mActivity, 10); height_42 =
	 * BaseActivity.dip2px(getActivity(), 42); if (root == null) { return; }
	 * View history = root.findViewById(R.id.ll_history); GridView gvMineHistory
	 * = (GridView) root.findViewById(R.id.gv_mine_history);
	 * history.setOnClickListener(this); if (records == null || records.size()
	 * == 0) { gvMineHistory.setVisibility(View.GONE); return; }
	 * LinearLayout.LayoutParams params = new
	 * LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0); if
	 * (records.size() <= 2 && records.size() > 0) { params = new
	 * LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height
	 * + height_42); } else if (records.size() > 2) { params = new
	 * LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 2 *
	 * height + height_42); } gvMineHistory.setVisibility(View.VISIBLE);
	 * gvMineHistory.setLayoutParams(params); RecordsAdapter recordsAdapter =
	 * new RecordsAdapter(mActivity); recordsAdapter.setRecords(records);
	 * gvMineHistory.setAdapter(recordsAdapter);
	 * gvMineHistory.setOnItemClickListener(this);
	 * 
	 * }
	 */

	/**
	 * 加载用户信息
	 */
	private void loadUserInfo() {
		if (loginInfo != null) {
			String imageUrl = MyApplication.getInstance().getString("iconUrl" + LoginInfoUtil.getUserId(mActivity));
			if (imageUrl != null) {
				ImageListener listener = ImageLoader.getImageListener(userIcon, R.drawable.mine_default_head_img,
						R.drawable.mine_default_head_img);
				LruImageCache.getImageLoader(mActivity.getApplicationContext()).get(imageUrl, listener);
			} else {
				ImageListener listener = ImageLoader.getImageListener(userIcon, R.drawable.mine_default_head_img,
						R.drawable.mine_default_head_img);
				LruImageCache.getImageLoader(mActivity.getApplicationContext()).get("", listener);
			}
			loginText.setText(
					loginInfo.getNickName() == null ? getResString(R.string.mine_namehint) : loginInfo.getNickName());
			if (MyApplication.getInstance().isNeedBoss() == 0) {// 是否需要boss功能
				ivVip.setVisibility(View.GONE);
				getVip.setVisibility(View.GONE);
			} else {
				getVip.setVisibility(View.VISIBLE);
				if (LoginInfoUtil.isVip(mActivity)) {
					ivVip.setVisibility(View.VISIBLE);
					getVip.setBackgroundResource(R.drawable.mine_vip_renew);
					getVip.setText(getResString(R.string.mine_vip_renew));
					getVip.setTextColor(getResources().getColor(android.R.color.white));
				} else {
					ivVip.setVisibility(View.GONE);
					getVip.setBackgroundResource(R.drawable.mine_getvip);
					getVip.setText(getResString(R.string.mine_getvip));
					getVip.setTextColor(getResources().getColor(R.color.vip_color));
				}
			}
		} else {
			ivVip.setVisibility(View.GONE);
			getVip.setVisibility(View.GONE);
			loginText.setText(getResString(R.string.login_mine));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// if (records != null) {
		// records.clear();
		// }
		// initRecordsBlock();
		// initUserInfoBlock();

		isLogin = MyApplication.getInstance().isLogin();
		loginInfo = LoginInfoUtil.getLoginInfo(mActivity);
		if (loginText == null) {
			return;
		}
		if (isLogin) {
			if (userIcon != null) {
				loadUserInfo();
			}
		} else {
			loginText.setText(R.string.login_mine);
			ivVip.setVisibility(View.GONE);
			getVip.setVisibility(View.GONE);
			if (userIcon != null) {
				userIcon.setImageResource(R.drawable.mine_default_head_img);
			}
		}

	}

	/**
	 * 初始化播放记录部分
	 */
	/*
	 * private void initRecordsBlock() { new UiAsyncTask<Void>(this) {
	 * 
	 * @Override protected Void doBackground() throws Throwable {
	 * PlayedRecordDao dao =
	 * MyApplication.getInstance().getDaoByKey(PlayedRecordDao.class.getName());
	 * records = dao.findAll(); return null; }
	 * 
	 * protected void post(Void result) { initPlayRecords(root); }; }.execute();
	 * }
	 */

	/**
	 * 初始化用户信息部分
	 */
	private void initUserInfoBlock() {
		new UiAsyncTask<Void>(this) {
			@Override
			protected Void doBackground() throws Throwable {
				infos = new ArrayList<LoginInfo>();
				vipInfos = new ArrayList<VipInfo>();
				UserInfoDataRequest request = new UserInfoDataRequest(mActivity);
				Map<String, String> mInputParam = new HashMap<String, String>();
				mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
				mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
				mInputParam.put("isaddboss", MyApplication.getInstance().isNeedBoss() + "");
				int code = request.setInputParam(mInputParam).setOutputData(infos, vipInfos)
						.request(Request.Method.GET);// infos.get(0)就是用户登录信息
				loginInfo = infos.get(0);
				if (code == 0 && infos != null) {
					SerializeableUtil.saveObject(mActivity, MyApplication.USER_INFO, loginInfo);
					MyApplication.getInstance().putInfo(MyApplication.VIPINFO, vipInfos);
				}
				return null;
			}

			protected void post(Void result) {
			};
		}.execute();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.user_icon:// 这个是头像图标
		case R.id.tv_not_login: // 这个是名称
			if (isLogin) {// 登录
				intent = new Intent(getActivity(), ContainerActivity.class);
				intent.putExtra(ContainerActivity.FRAGMENTNAME, PersonalFragment.class.getName());
				startActivity(intent);
			} else {// 未登录
				LoginAPI.stratLogin(getActivity());
			}
			break;
		case R.id.login_rl:// 这个是头像整个区域
			if (isLogin) {// 登录
				intent = new Intent(getActivity(), ContainerActivity.class);
				intent.putExtra(ContainerActivity.FRAGMENTNAME, PersonalFragment.class.getName());
				startActivity(intent);
			} else {// 未登录
				LoginAPI.stratLogin(getActivity());
			}
			break;
		case R.id.ll_history:// 这个是查看历史记录
			RecordsAPI.startPlayRecord(getActivity());
			break;
		case R.id.ll_collect:// 这个是收藏记录
			CollectionAPI.startCollection(getActivity());
			break;
		case R.id.ll_setting:// 设置
			intent = new Intent(getActivity(), ContainerActivity.class);
			intent.putExtra(ContainerActivity.FRAGMENTNAME, SettingFragment.class.getName());
			startActivity(intent);
			break;
		case R.id.ll_offline:// 设置
//			OfflineAPI.startOfflineFragment(getActivity());
			Toast.makeText(getActivity(), "暂未实现...", Toast.LENGTH_SHORT).show();
			break;
		case R.id.tv_vip_center:
			MyApplication.getInstance().putInt(LepayManager.PAY_FOR_WHAT, LepayManager.PAY_NORMAL);
			Intent it = MyApplication.getInstance().getBossManager().getMembersIntent(mActivity);
			startActivity(it);
			break;
		case R.id.tv_get_vip:
			MyApplication.getInstance().putInt(LepayManager.PAY_FOR_WHAT, LepayManager.PAY_NORMAL);
			it = MyApplication.getInstance().getBossManager().getMembersIntent(mActivity);
			startActivity(it);
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		PlayRecordInfo record = (PlayRecordInfo) ((Holder) view.getTag()).re;
		if (null != record) {
			DisplayVideoInfo info = record.getDisplayVedioInfo();
			// info.setLastPositon(info.getLastPositon());
			PlayerAPI.startPlayActivity(mActivity, info);
		}
	}

	class RecordsAdapter extends BaseAdapter {
		private List<PlayRecordInfo> records = new LinkedList<PlayRecordInfo>();
		private Context context;
		private LayoutInflater inflater;
		private Holder holder;
		private boolean selectAll = false;
		private PlayRecordInfo currentObj;
		AbsListView.LayoutParams params;
		private SimpleDateFormat format = new SimpleDateFormat("mm:ss");
		private SimpleDateFormat dataFormat = new SimpleDateFormat("hh:mm:ss");
		private SimpleDateFormat date = new SimpleDateFormat("MM-dd");
		private PlayRecordInfo playRecordInfo;

		public RecordsAdapter(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return records.size() > 4 ? 4 : records.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			playRecordInfo = records.get(position);
			if (null == convertView) {
				convertView = inflater.inflate(R.layout.mine_fragment_play_record_item, null);
				holder = new Holder();
				holder.videoIcon = (NetImageView) convertView.findViewById(R.id.iv_played_records_video_img);
				holder.videoName = (TextView) convertView.findViewById(R.id.tv_video_title);
				holder.videoTime = (TextView) convertView.findViewById(R.id.tv_video_playing_time);
				holder.videoIcon.setDefaultImageResId(R.drawable.default_img_16_10);
				holder.videoIcon.setErrorImageResId(R.drawable.default_img_16_10);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			// 这个判断是对播放记录的节点的判断。如果要显示时间借点。那么相应的要对一部分View进行隐藏
			holder.videoIcon.setCoverUrl(records.get(position).getVideoImage(), context);
			holder.videoName.setText(records.get(position).getVideoTitle());
			int lastPlayTime = (int) playRecordInfo.getLastPlayTime();
			Calendar calendar = Calendar.getInstance();
			calendar.set(0, 0, 0, lastPlayTime / 3600, lastPlayTime % 3600 / 60, lastPlayTime % 3600 % 60);
			if (lastPlayTime > 60 * 60) {
				holder.videoTime.setText(getResString(R.string.mine_lasttime, dataFormat.format(calendar.getTime())));
			} else {
				holder.videoTime.setText(getResString(R.string.mine_lasttime, format.format(calendar.getTime())));
			}
			holder.re = records.get(position);
			return convertView;
		}

		/**
		 * 向adapte中添加需要的所有的数据。
		 * 
		 * @param records
		 *            这个list 中，如果有一个元素为空，那么就代表分为一层
		 */
		public void setRecords(List<PlayRecordInfo> records) {
			if (null == records) {
				return;
			}
			this.records = records;
			this.notifyDataSetChanged();
		}
	}

	class Holder {
		NetImageView videoIcon;
		TextView videoName;
		TextView videoTime;
		PlayRecordInfo re;
	}
}
