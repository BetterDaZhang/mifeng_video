package com.letv.autoapk.ui.channel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.ContainerBackActivity;
import com.letv.autoapk.base.activity.DetailActivity;
import com.letv.autoapk.base.fragment.BaseCacheTitleFragment;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.CacheListener;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.ChannelEditDao;
import com.letv.autoapk.dao.CollectionRecordDao;
import com.letv.autoapk.ui.mobilelive.MobileLiveAPI;
import com.letv.autoapk.widgets.CustomTabPageIndicator;
import com.letv.autoapk.widgets.LoadingLayout;
import com.viewpagerindicator.TabPageIndicator;

public class ChannelFragment extends BaseTitleFragment implements OnClickListener {
	private List<ChannelVideoInfo> channelTempInfos;
	private List<ChannelVideoInfo> channelInfos = new ArrayList<ChannelVideoInfo>();
	private LinearLayout channelLay;
	private ImageView tabIcon;
	CustomTabPageIndicator indicator;
	final static int CHANNEL_EDIT_RESULT = 0x001;
	final static int CHANNEL_EIDIT_PRESS_RESULT = 0x002;
	private ChannelEditDao channelEditDao;
	private FragmentPagerAdapter adapter;
	private ViewPager pager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		channelEditDao = MyApplication.getInstance().getDaoByKey(ChannelEditDao.class.getName());
	}

	@Override
	protected boolean hasTitleBar() {
		return false;
	}

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	protected boolean loadingData() {
		channelTempInfos = new ArrayList<ChannelVideoInfo>();
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputParam.put(StringDataRequest.TEMPLATE_ID, MyApplication.getInstance().getTemplate());
		ChannelDataRequest request = new ChannelDataRequest(mActivity);
		int status = request.setInputParam(mInputParam).setOutputData(channelTempInfos).request(Request.Method.GET,
				true);
		if (channelTempInfos.size() > 0) {
			channelInfos.clear();
			channelInfos.addAll(channelTempInfos);
			return true;
		}
		return false;
	}

	protected boolean hasContentData() {
		if (channelInfos.size() > 0) {
			return true;
		}
		return false;
	};

	private LayoutInflater localInflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		try {
			final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.ChannelPageIndicator);
			// clone the inflater using the ContextThemeWrapper
			localInflater = inflater.cloneInContext(contextThemeWrapper);
			return super.onCreateView(localInflater, container, savedInstanceState);
		} catch (Exception e1) {
			Logger.log(e1);
		}
		return mRoot;
	}

	protected View createContentView() {
		loadingLayout = new LoadingLayout(mActivity) {

			@Override
			public void loadData(View loadingView) {
				startLoading();

			}

			@Override
			public View createLoadedView() {
				return setupDataView();
			}

			@Override
			public boolean hasContent() {
				return hasContentData();
			}

			@Override
			public void onSuccess(Boolean result) {
				super.onSuccess(result);
				// 对比网络数据和本地数据，以本地数据顺序为主
				findSavedDataFromDao();
				if (result) {
					adapter = new ChannelSubpageAdapter(getFragmentManager());
					pager = (ViewPager) channelLay.findViewById(R.id.channel_pager);
					pager.setAdapter(adapter);

					indicator = (CustomTabPageIndicator) channelLay.findViewById(R.id.channel_indicator);
					indicator.setViewPager(pager);
				}
			}
		};
		loadingLayout.show();
		return loadingLayout;
	}

	@Override
	protected View setupDataView() {
		initChannelView();
		return channelLay;
	}

	/**
	 * 初始化方法
	 */
	private void initChannelView() {
		channelLay = (LinearLayout) localInflater.inflate(R.layout.channel_view, null);

		tabIcon = (ImageView) channelLay.findViewById(R.id.channel_tab_icon);
		tabIcon.setOnClickListener(this);

	}

	class ChannelSubpageAdapter extends FragmentPagerAdapter {
		public ChannelSubpageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Bundle bundle = new Bundle();
			bundle.putString("pageId", channelInfos.get(position).getmPageId());
			if (channelInfos.get(position).getChannelDetailType() == 33) {
				return Fragment.instantiate(getActivity(), MobileLiveAPI.getLiveFragmentName(), bundle);
			}
			return ChannelVideoFragment.instantiate(getActivity(), ChannelVideoFragment.class.getName(), bundle);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return channelInfos.get(position % channelInfos.size()).getChannelName();
		}

		@Override
		public int getCount() {
			return channelInfos.size();
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Fragment fragment = (Fragment) super.instantiateItem(container, position);
			String pageId = channelInfos.get(position).getmPageId();
			if (fragment instanceof ChannelVideoFragment) {
				((ChannelVideoFragment) fragment).setPageId(pageId);
			}
			return fragment;
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.channel_tab_icon:
			Intent intent = new Intent(mActivity, ContainerBackActivity.class);
			Bundle channelEditBundle = new Bundle();
			channelEditBundle.putSerializable("channelInfos", (Serializable) channelInfos);
			channelEditBundle.putString(DetailActivity.FRAGMENTNAME, ChannelTabEditFragment.class.getName());
			intent.putExtras(channelEditBundle);
			startActivityForResult(intent, Activity.RESULT_FIRST_USER);
			break;

		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			Bundle bundle = data.getExtras();
			channelInfos.clear();
			channelInfos.addAll((List<ChannelVideoInfo>) bundle.getSerializable("channelInfos"));
			indicator.notifyDataSetChanged();
			adapter.notifyDataSetChanged();
			if (resultCode == CHANNEL_EIDIT_PRESS_RESULT) {
				int channelPosition = bundle.getInt("channel_position");
				indicator.setCurrentItem(channelPosition);
			}
			// 保存数据库
			channelEditDao.delete(channelInfos);
			channelEditDao.save(channelInfos);
			channelInfos.clear();
			channelInfos.addAll(channelEditDao.findAll());
		}
	}

	public List<ChannelVideoInfo> findSavedDataFromDao() {
		List<ChannelVideoInfo> infos = channelEditDao.findAll();
		if (infos == null || infos.size() == 0) {
			return channelInfos;
		}
		for (int i = 0; i < infos.size(); i++) {
			if (!channelInfos.contains(infos.get(i))) {
				channelEditDao.delete(infos.get(i));
			}
		}
		for (int i = 0; i < channelInfos.size(); i++) {
			ChannelVideoInfo info = channelEditDao.findById("mChannelId", channelInfos.get(i).getChannelId());
			if (info != null) {
				channelEditDao.update(channelInfos.get(i));
			} else {
				channelEditDao.save(channelInfos.get(i));
			}
		}
		channelInfos.clear();
		channelInfos.addAll(channelEditDao.findAll());
		return channelInfos;
	}

}
