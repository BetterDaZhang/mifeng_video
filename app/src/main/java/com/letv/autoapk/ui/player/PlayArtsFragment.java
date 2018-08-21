package com.letv.autoapk.ui.player;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseFragment;

public class PlayArtsFragment extends BaseFragment implements OnClickListener {
	private ListView artsListview;
	private TextView artsTitle;
	private ImageView artsExit;
	private LinearLayout artLay;
	private PlayArtsAdapter artsAdapter;

	private List<PlayVideoInfo> episdeoInfos;
	private String videoId;
	private Handler handler;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		episdeoInfos = (List<PlayVideoInfo>) bundle.get("episodes");
		videoId = bundle.getString("videoId");
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	protected View setupDataView() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		artLay = (LinearLayout) inflater.inflate(R.layout.play_arts_listview, null);
		artsTitle = (TextView) artLay.findViewById(R.id.play_arts_tiltle);
		artsExit = (ImageView) artLay.findViewById(R.id.play_arts_exit);

		artsListview = (ListView) artLay.findViewById(R.id.play_arts_listView);
		if (episdeoInfos == null) {
			episdeoInfos = new ArrayList<PlayVideoInfo>();
		}
		artsAdapter = new PlayArtsAdapter(mActivity, handler, episdeoInfos, videoId);
		artsListview.setAdapter(artsAdapter);
		artsListview.postDelayed(new Runnable() {
			public void run() {
				artsListview.postDelayed(new Runnable() {
					public void run() {
						int position = PlayerAPI.fixedPostion(episdeoInfos, videoId);
						artsListview.setSelection(position);
					}
				}, 50);
			}
		}, 50);

		artsTitle.setText(mActivity.getResources().getString(R.string.play_detail_arts_title));
		artsExit.setOnClickListener(this);
		return artLay;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.play_arts_exit:
			getFragmentManager().popBackStack();
			break;
		}
	}
	
	public void notifyDataChanged(String id) {
		if (artsAdapter != null) {
			artsAdapter.setVideoId(id);
			artsAdapter.notifyDataSetChanged();
		}
	}

}
