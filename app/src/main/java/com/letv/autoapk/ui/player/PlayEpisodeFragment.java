package com.letv.autoapk.ui.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.gesture.GesturePoint;
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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseFragment;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.widgets.CustomDetailTabPageIndicator;
import com.letv.autoapk.widgets.CustomTabPageIndicator;

public class PlayEpisodeFragment extends BaseFragment implements OnClickListener {
	private TextView episodeTitle;
	private TextView episodeBrief;
	private LinearLayout episodelay;
	private String totalCount;
	private String updateEpisode;

	private List<PlayVideoInfo> episodeInfos;
	private List<PlayEpisodeSubInfo> episodeSubPageInfos = new ArrayList<PlayEpisodeSubInfo>();
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private String videoId;
	static final int MOD_COUNT = 20;
	FragmentStatePagerAdapter adapter;

	private Handler handler;

	CustomDetailTabPageIndicator indicator;
	private LayoutInflater localInflater;
	private int subPagePosition;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		episodeInfos = (List<PlayVideoInfo>) bundle.get("episodes");
		totalCount = (String) bundle.get("episodeTotalCount");
		updateEpisode = (String) bundle.get("episodeUpdate");
		videoId = bundle.getString("videoId");
		subPagePosition = bundle.getInt("subPagePosition");
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		try {
			final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.episodePageIndicator);
			// clone the inflater using the ContextThemeWrapper
			localInflater = inflater.cloneInContext(contextThemeWrapper);
			return super.onCreateView(localInflater, container, savedInstanceState);
		} catch (Exception e1) {
			Logger.log(e1);
		}
		return mRoot;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	protected View setupDataView() {
		episodelay = (LinearLayout) localInflater.inflate(R.layout.play_episode_indicator, null);
		episodeTitle = (TextView) episodelay.findViewById(R.id.play_episode_tiltle);
		episodeBrief = (TextView) episodelay.findViewById(R.id.play_episode_brief);

		if (episodeInfos == null) {
			episodeInfos = new ArrayList<PlayVideoInfo>();
		}
		processEpisodeSubPage();
		episodeTitle.setText(mActivity.getResources().getString(R.string.play_detail_episode_title));
		episodeBrief.setText(getString(R.string.play_episodeprogress, totalCount, updateEpisode));
		episodeBrief.setOnClickListener(this);

		adapter = new PlayEpisodeAdapter(getFragmentManager());
		fragments.clear();
		for (int i = 0; i < episodeSubPageInfos.size(); i++) {
			Bundle bundle = new Bundle();
			bundle.putString("videoId", videoId);
			bundle.putSerializable("episodes", (Serializable) episodeSubPageInfos.get(i).getSubEpisodes());
			Fragment playEpisodeSubFragment = PlayEpisodeSubPageFragment.instantiate(getActivity(),
					PlayEpisodeSubPageFragment.class.getName(), bundle);
			((PlayEpisodeSubPageFragment) playEpisodeSubFragment).setSubHandler(handler);
			fragments.add(playEpisodeSubFragment);
		}

		ViewPager pager = (ViewPager) episodelay.findViewById(R.id.play_detail_episode_pager);
		pager.setAdapter(adapter);

		indicator = (CustomDetailTabPageIndicator) episodelay.findViewById(R.id.play_detail_episode_indicator);
		indicator.setViewPager(pager);
		indicator.setCurrentItem(subPagePosition);
		return episodelay;
	}

	private void processEpisodeSubPage() {
		int size = episodeInfos.size() / MOD_COUNT;
		for (int i = 0; i < (size + 1); i++) {
			String title = buildTitle(i);
			int fromOriginalsIndex = i * MOD_COUNT;
			int toIndex = ((i + 1) * MOD_COUNT);
			int originalModelsSize = episodeInfos.size();
			toIndex = toIndex > originalModelsSize ? originalModelsSize : toIndex;
			if (fromOriginalsIndex < toIndex) {
				PlayEpisodeSubInfo pllayEpisodeInfo = new PlayEpisodeSubInfo();
				for (int j = fromOriginalsIndex; j < toIndex; j++) {
					pllayEpisodeInfo.getSubEpisodes().add(episodeInfos.get(j));
				}
				pllayEpisodeInfo.setSubEpisodeTitle(title);
				episodeSubPageInfos.add(pllayEpisodeInfo);
			}
		}
	}

	private String buildTitle(int index) {
		int originalModelsSize = episodeInfos.size();
		int targetFromIndex = index * MOD_COUNT + 1;
		int targetToIndex = (index + 1) * MOD_COUNT;
		if (targetToIndex > originalModelsSize) {
			targetToIndex = originalModelsSize;
		}
		return "" + targetFromIndex + "-" + targetToIndex;
	}

	class PlayEpisodeAdapter extends FragmentStatePagerAdapter {
		public PlayEpisodeAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = fragments.get(position);
			return fragment;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return episodeSubPageInfos.get(position % episodeSubPageInfos.size()).getSubEpisodeTitle();
		}

		@Override
		public int getCount() {
			return episodeSubPageInfos.size();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play_episode_brief:
			getFragmentManager().popBackStack();
			break;
		}

	}

	public void notifyDataChanged(String id, int subPagePosition) {
		try {
			this.videoId = id;
			this.subPagePosition = subPagePosition;
			if (indicator != null && subPagePosition != -1) {
				indicator.setCurrentItem(subPagePosition);
			}
			for (int i = 0; i < fragments.size(); i++) {
				 ((PlayEpisodeSubPageFragment)fragments.get(i)).notifyDataChanged(id);
			}
		} catch (Exception e) {
			Logger.log(e);
		}
	}

}
