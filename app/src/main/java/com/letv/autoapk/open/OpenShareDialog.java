package com.letv.autoapk.open;

import java.util.ArrayList;
import java.util.List;

import org.xutils.common.util.DensityUtil;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.adapter.BaseAdapter;
import com.letv.autoapk.base.dialog.BaseDialog;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;

public class OpenShareDialog extends BaseDialog {

	@Override
	public int layoutId() {
		return R.layout.open_sharemenu;
	}

	private OnShareListener listener;

	public void setOnShareListener(OnShareListener listener) {
		this.listener = listener;
	}

	@Override
	protected void setupUI(View view, Bundle bundle) throws Exception {
		try {
			Window dialogWindow = getDialog().getWindow();
			dialogWindow.setGravity(Gravity.BOTTOM);
			view.setMinimumWidth(getResources().getDisplayMetrics().widthPixels);
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			lp.x = 0;
			lp.y = 0;
			dialogWindow.setAttributes(lp);
		} catch (Exception e) {
			Logger.log(e);
		}
		OpenSdk openSdk = MyApplication.getInstance().getOpenSdk();
		GridView gridView = (GridView) view(R.id.rl_share_imgs);
		List<Integer> list = new ArrayList<Integer>();

		if (openSdk.hasBLOG()) {
			list.add(OpenSdk.TYPE_BLOG);
		}
		if (openSdk.hasMM()) {
			list.add(OpenSdk.TYPE_MM);
			list.add(OpenSdk.TYPE_MM_TIMELINE);
		}
		if (openSdk.hasQQ()) {
			list.add(OpenSdk.TYPE_QQ);
			list.add(OpenSdk.TYPE_ZONE);
		}
		ShareAdapter adapter = new ShareAdapter(list);
		gridView.setAdapter(adapter);
	}

	@Override
	public int getStyle() {
		// TODO Auto-generated method stub
		return R.style.DateDialogStyle;
	}

	class ShareAdapter extends BaseAdapter implements OnClickListener {

		List<Integer> sharelist;

		ShareAdapter(List<Integer> list) {
			sharelist = list;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int opentype = (Integer) v.getTag();
			if (opentype != -1 && listener != null) {
				listener.doShare(opentype);
			}
			dismiss();
		}

		private View getImageView(int res) {
			FrameLayout frameLayout = new FrameLayout(context);
			ImageView imageView = new ImageView(context);
			imageView.setImageResource(res);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(DensityUtil.dip2px(50), DensityUtil.dip2px(50));
			params.gravity = Gravity.CENTER;
			frameLayout.addView(imageView, params);
			frameLayout.setOnClickListener(this);
			return frameLayout;
		}

		private int getIcon(int type) {
			switch (type) {
			case OpenSdk.TYPE_QQ:

				return R.drawable.mine_share_qq;
			case OpenSdk.TYPE_ZONE:

				return R.drawable.mine_share_qqzone;
			case OpenSdk.TYPE_MM_TIMELINE:

				return R.drawable.mine_share_feedline;
			case OpenSdk.TYPE_MM:

				return R.drawable.mine_share_mm;
			case OpenSdk.TYPE_BLOG:

				return R.drawable.mine_share_blog;
			default:
				break;
			}
			return R.drawable.mine_share_blog;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return sharelist.size();
		}

		@Override
		public Integer getItem(int position) {
			// TODO Auto-generated method stub
			return sharelist.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		protected View getMyView(int position, View convertView,
				ViewGroup parent) {
			convertView = getImageView(getIcon(getItem(position)));
			convertView.setTag(getItem(position));
			return convertView;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		dismiss();
	}

	public interface OnShareListener {
		void doShare(int openType);
	}
}
