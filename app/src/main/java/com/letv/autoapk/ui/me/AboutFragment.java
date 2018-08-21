package com.letv.autoapk.ui.me;

import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseTitleFragment;

public class AboutFragment extends BaseTitleFragment {

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	protected boolean loadingData() {
		return true;
	}

	@Override
	protected View createContentView() {
		return setupDataView();
	}

	@Override
	protected View setupDataView() {
		setStatusBarColor(getResources().getColor(R.color.code04));
		setTitle(getString(R.string.mine_about_us), getResources().getColor(R.color.code6));
		setTitleLeftResource(R.drawable.mine_login_back, mActivity.dip2px(16));

		setLeftClickListener(new TitleLeftClickListener() {
			@Override
			public void onLeftClickListener() {
				getActivity().finish();
			}
		});
		View root = View.inflate(mActivity, R.layout.mine_about, null);

		// TextView version = (TextView)root.findViewById(R.id.tv_version);
		TextView about = (TextView) root.findViewById(R.id.tv_about);
		// ImageView logo = (ImageView)root.findViewById(R.id.iv_logo);
		return root;
	}

}
