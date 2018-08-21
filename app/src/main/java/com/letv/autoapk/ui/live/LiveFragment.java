package com.letv.autoapk.ui.live;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseCacheTitleFragment;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.CacheListener;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.ui.mobilelive.MobileLiveAPI;
import com.letv.autoapk.widgets.CustomTabPageIndicator;
import com.letv.autoapk.widgets.LoadingLayout;

public class LiveFragment extends BaseTitleFragment {
	private List<LiveVideoInfo> liveTempInfo;
	private List<LiveVideoInfo> liveInfos = new ArrayList<LiveVideoInfo>();
	private ViewGroup liveVideoLay;
	CustomTabPageIndicator indicator;
	private FragmentPagerAdapter adapter;

	@Override
	protected boolean hasTitleBar() {
		return false;
	}

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	protected boolean loadingData() {
		liveTempInfo = new ArrayList<LiveVideoInfo>();
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputParam.put(StringDataRequest.TEMPLATE_ID, MyApplication.getInstance().getTemplate());
		LiveDataRequest request = new LiveDataRequest(mActivity);
		int status = request.setInputParam(mInputParam).setOutputData(liveTempInfo).request(Request.Method.GET, true);
		if (liveTempInfo.size() > 0) {
			liveInfos.clear();
			liveInfos.addAll(liveTempInfo);
			return true;
		}
		return false;
	}



	protected boolean hasContentData() {
		if (liveInfos.size() > 0) {
			return true;
		}
		return false;
	}

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
				// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
				super.onSuccess(result);
				if (result) {
					adapter = new LiveSubpageAdapter(getChildFragmentManager());

					ViewPager pager = (ViewPager) liveVideoLay.findViewById(R.id.live_pager);
					pager.setAdapter(adapter);

					indicator = (CustomTabPageIndicator) liveVideoLay.findViewById(R.id.live_indicator);
					indicator.setViewPager(pager);
				}

			}

		};
		loadingLayout.show();
		return loadingLayout;
	}

	@Override
	protected View setupDataView() {
		liveVideoLay = (ViewGroup) localInflater.inflate(R.layout.live_view, null);
		// FragmentPagerAdapter adapter = new
		// LiveSubpageAdapter(getChildFragmentManager());
		// ViewPager pager = (ViewPager)
		// liveVideoLay.findViewById(R.id.live_pager);
		// pager.setAdapter(adapter);
		// indicator = (CustomTabPageIndicator)
		// liveVideoLay.findViewById(R.id.live_indicator);
		// indicator.setViewPager(pager);
		return liveVideoLay;
	}

	class LiveSubpageAdapter extends FragmentPagerAdapter {
		public LiveSubpageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Bundle bundle = new Bundle();
			bundle.putString("pageId", liveInfos.get(position).getmLiveVideoDetailId());
			if (liveInfos.get(position).getmLiveDetailType() == 33) {
				return Fragment.instantiate(getActivity(), MobileLiveAPI.getLiveFragmentName(), bundle);
			}
			return LiveVideoLandsFragment.instantiate(getActivity(), LiveVideoLandsFragment.class.getName(), bundle);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (liveInfos.get(position).getmLiveVideoName() != null) {
				return liveInfos.get(position % liveInfos.size()).getmLiveVideoName();
			}
			return "";
		}

		@Override
		public int getCount() {
			return liveInfos.size();
		}
	}

}
