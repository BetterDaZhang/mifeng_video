package com.letv.autoapk.boss;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.base.activity.DetailActivity;
import com.letv.autoapk.base.fragment.BaseFragment;
import com.letv.autoapk.context.MyApplication;

public class MemberMobileFragment extends BaseFragment implements OnClickListener {
	// public static final String LEVEL = "1";
	private List<PackageInfo> list;
	private LayoutInflater inflater;
	private LinearLayout menuLayout;

	// private String vipPic;
	@Override
	protected void onHandleMessage(Message msg) {
		// TODO Auto-generated method stub

	}

	@Override
	protected View setupDataView() {
		// TODO Auto-generated method stub
		inflater = LayoutInflater.from(mActivity);
		View view = inflater.inflate(R.layout.boss_mobilemember, null);
		view.findViewById(R.id.tv_cdkey).setOnClickListener(this);
		menuLayout = (LinearLayout) view.findViewById(R.id.menulist);
		if (this.list != null)
			initMenu(mActivity);
		return view;
	}

	public void updateData(List<PackageInfo> list) {
		if (this.list == null) {
			this.list = list;
		}
	}

	private void initMenu(Context context) {
		PackageInfo info = null;
		for (int i = 0; i < list.size(); i++) {
			info = list.get(i);
			View view = LayoutInflater.from(context).inflate(R.layout.boss_menuitem, null);
			view.findViewById(R.id.open).setOnClickListener(this);
			view.findViewById(R.id.open).setTag(info);
			TextView name = (TextView) view.findViewById(R.id.during);
			name.setText(info.duration.concat(":"));
			TextView cost = (TextView) view.findViewById(R.id.cost);
			cost.setText("￥".concat(info.price));
			menuLayout.addView(view);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.tv_cdkey) {
			if (!MyApplication.getInstance().isLogin()) {
				BossLoginAPI.startLogin(mActivity, BossManager.LOGIN_TO_CDKEY, false);
				return;
			}
			Intent intent = null;
			if (mActivity instanceof DetailActivity) {
				intent = new Intent(mActivity, ContainerActivity.class);
				intent.putExtra(ContainerActivity.FRAGMENTNAME, CdkeyFragment.class.getName());
			} else {
				intent = new Intent(mActivity, DetailActivity.class);
				intent.putExtra(DetailActivity.FRAGMENTNAME, CdkeyFragment.class.getName());
			}
			startActivity(intent);
			int flag = MyApplication.getInstance().getInt(LepayManager.PAY_FOR_WHAT);
			if (flag == LepayManager.PAY_FOR_PLAY || flag == LepayManager.PAY_FOR_FINISH
					|| flag == LepayManager.PAY_AUTH_FINISH) {
				mActivity.finish();
			}
		} else if (v.getId() == R.id.open) {
			PackageInfo info = (PackageInfo) v.getTag();
			//  开通vip会员
			if (!MyApplication.getInstance().isLogin()) {// 登录
				MyApplication.getInstance().putInfo(MyApplication.CURRENT_PACKAGE_INFO, info);
				BossLoginAPI.startLogin(mActivity, BossManager.FLAG_ENTER_BOSS_PAY, false);
			} else {
				LepayManager lepayManager = LepayManager.getInstance(mActivity, MemberMobileFragment.this);
				lepayManager.vipOrder(info.pid, "104002");
			}
		}

	}

	/*
	 * class GetPackageListTask extends UiAsyncTask<Boolean> {
	 * 
	 * public GetPackageListTask(Fragment fragment) { super(fragment); // TODO
	 * Auto-generated constructor stub }
	 * 
	 * @Override protected void post(Boolean result) { if (result) { initMenu();
	 * } }
	 * 
	 * @Override protected Boolean doBackground() throws Throwable { list = new
	 * ArrayList<PackageInfo>(); VipPackageRequest request = new
	 * VipPackageRequest(mActivity); Map<String, String> mInputParam = new
	 * HashMap<String, String>(); mInputParam.put("level", LEVEL);
	 * mInputParam.put(StringDataRequest.TENANT_ID,
	 * MyApplication.getInstance().getTenantId()); int code =
	 * request.setInputParam(mInputParam).setOutputData(list).request(Request.
	 * Method.GET); Logger.d("GetPackageListTask", "code:" + code); if (code ==
	 * 0) { vipPic = request.getVipPic(); return true; } return false; }
	 * 
	 * }
	 */
}
