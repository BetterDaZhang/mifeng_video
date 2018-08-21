package com.letv.autoapk.ui.channel;

import java.io.Serializable;
import java.util.List;
import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BackHandledFragment;
import com.letv.autoapk.widgets.drag.DragGridView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ChannelTabEditFragment extends BackHandledFragment {
	private View channelEditView;
	private List<ChannelVideoInfo> channelInfos;
	private DragGridView channelDragGridview;
	private ChannelTagEditAdapter channelDragAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = mActivity.getIntent().getExtras();
		channelInfos = (List<ChannelVideoInfo>) bundle.get("channelInfos");
	}

	protected void initCustomerView() {
		setStatusBarColor(getResources().getColor(R.color.code04));
		setTitleLeftResource(R.drawable.channel_back, mActivity.dip2px(16));
		setLeftClickListener(new TitleLeftClickListener() {
			@Override
			public void onLeftClickListener() {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("channelInfos", (Serializable) channelInfos);
				intent.putExtras(bundle);
				getActivity().setResult(ChannelFragment.CHANNEL_EDIT_RESULT, intent);
				getActivity().finish();
			}
		});
		setTitle(getResString(R.string.channel_title), getResources().getColor(R.color.code6));

	}

	@Override
	protected void onHandleMessage(Message msg) {

	}

	protected View createContentView() {
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		channelEditView = inflater.inflate(R.layout.channel_edit_layout, null);
		channelDragGridview = (DragGridView) channelEditView.findViewById(R.id.channel_dragGridView);
		channelDragAdapter = new ChannelTagEditAdapter(mActivity, channelInfos);
		channelDragGridview.setAdapter(channelDragAdapter);
		channelDragGridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("channelInfos", (Serializable) channelInfos);
				bundle.putInt("channel_position", position);
				intent.putExtras(bundle);
				getActivity().setResult(ChannelFragment.CHANNEL_EIDIT_PRESS_RESULT, intent);
				getActivity().finish();
			}
		});
		return channelEditView;
	}

	@Override
	protected boolean loadingData() {
		return false;
	}

	@Override
	protected View setupDataView() {
		return null;
	}

	@Override
	public boolean onBackPressed() {
		return true;
	}

	@Override
	public void onBackProgerss() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("channelInfos", (Serializable) channelInfos);
		intent.putExtras(bundle);
		getActivity().setResult(ChannelFragment.CHANNEL_EDIT_RESULT, intent);
		getActivity().finish();
	}
}
