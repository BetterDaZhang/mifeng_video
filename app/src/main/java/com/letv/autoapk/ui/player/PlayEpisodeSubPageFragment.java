package com.letv.autoapk.ui.player;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseFragment;

public class PlayEpisodeSubPageFragment extends BaseFragment implements OnClickListener {
	private GridView episodeGridview;
	private LinearLayout episodelay;
	private PlayEpisodeSubAdapter episodeAdapter;
	private List<PlayVideoInfo> episdeoInfos;
	private String videoId;

	private Handler handler;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		episdeoInfos = (List<PlayVideoInfo>) bundle.get("episodes");
		videoId = bundle.getString("videoId");
	};

	public void setSubHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	protected View setupDataView() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		episodelay = (LinearLayout) inflater.inflate(R.layout.play_episode_gridview, null);

		episodeGridview = (GridView) episodelay.findViewById(R.id.play_episode_grid);
		if (episdeoInfos == null) {
			episdeoInfos = new ArrayList<PlayVideoInfo>();
		}
		episodeAdapter = new PlayEpisodeSubAdapter(mActivity, handler, episdeoInfos, videoId);
		episodeGridview.setAdapter(episodeAdapter);
		final int position = PlayerAPI.fixedPostion(episdeoInfos, videoId);
		if (position != -1) {
			episodeGridview.postDelayed(new Runnable() {
				public void run() {
					episodeGridview.setSelection(position);
				}
			}, 50);
		}
		
		return episodelay;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play_episode_brief:
			getFragmentManager().popBackStack();
			break;
		}
	}

	public GridView getEpisodeGridview() {
		return episodeGridview;
	}
	
	public void notifyDataChanged(String id) {
		if (episodeAdapter != null) {
			episodeAdapter.setVideoId(id);
			episodeAdapter.notifyDataSetChanged();
		}
	}
	

}
