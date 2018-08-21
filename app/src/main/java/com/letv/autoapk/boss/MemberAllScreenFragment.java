package com.letv.autoapk.boss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import android.content.Intent;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.base.activity.DetailActivity;
import com.letv.autoapk.base.fragment.BaseFragment;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.ui.login.LoginAPI;
import com.sina.weibo.sdk.constant.WBConstants.Msg;

public class MemberAllScreenFragment extends BaseFragment implements OnClickListener{

	public static final String LEVEL = "7";
	private List<PackageInfo> list;
	private LayoutInflater inflater;
	private LinearLayout menuLayout;
	private String vipPic;
	@Override
	protected void onHandleMessage(Message msg) {
		// TODO Auto-generated method stub

	}

	@Override
	protected View setupDataView() {
		inflater = LayoutInflater.from(mActivity);
		View view = inflater.inflate(R.layout.boss_mobilemember, null);
		view.findViewById(R.id.tv_cdkey).setOnClickListener(this);
		menuLayout = (LinearLayout)view.findViewById(R.id.menulist);
		GetPackageListTask task = new GetPackageListTask(this);
		x.task().start(task);
		return view;
	}
	private void initMenu(){
		PackageInfo info = null;
		for(int i=0;i<list.size();i++){
			info = list.get(i);
			View view = inflater.inflate(R.layout.boss_menuitem, null);
			view.findViewById(R.id.open).setOnClickListener(this);
			view.findViewById(R.id.open).setTag(info);
			TextView name = (TextView)view.findViewById(R.id.during);
			name.setText(info.duration.concat(":"));
			TextView cost = (TextView)view.findViewById(R.id.cost);
			cost.setText("￥".concat(info.price));
			menuLayout.addView(view);
		}
		if(info!=null){
			JSONObject obj = new JSONObject();
			try {
				obj.put("name", info.name)
				.put("icon", vipPic);
			} catch (JSONException e) {
				Logger.log(e);
			}
			Message msg = mActivity.getDefaultHandler().obtainMessage(0, 1, 0, obj.toString());
			msg.sendToTarget();
		}
	}
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.tv_cdkey){
			if(!MyApplication.getInstance().isLogin()){
				BossLoginAPI.startLogin(mActivity, BossManager.LOGIN_TO_CDKEY,false);
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
		}else if (v.getId() == R.id.open) {
			PackageInfo info = (PackageInfo) v.getTag();
			// 开通vip会员
			if (!MyApplication.getInstance().isLogin()) {// 登录
				MyApplication.getInstance().putInfo(MyApplication.CURRENT_PACKAGE_INFO, info);
				BossLoginAPI.startLogin(mActivity, BossManager.FLAG_ENTER_BOSS_PAY,false);
			}else{
				LepayManager lepayManager = LepayManager.getInstance(mActivity, MemberAllScreenFragment.this);
				lepayManager.vipOrder(info.pid, "104002");
			}
		}
		
	}
    class GetPackageListTask extends UiAsyncTask<Boolean>{

		public GetPackageListTask(Fragment fragment) {
			super(fragment);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void post(Boolean result) {
			if(result){
				initMenu();
			}
		}

		@Override
		protected Boolean doBackground() throws Throwable {
			list = new ArrayList<PackageInfo>();
			VipPackageRequest request = new VipPackageRequest(mActivity);
			Map<String, String> mInputParam = new HashMap<String, String>();
			mInputParam.put("level", LEVEL);
			mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());//
			int code = request.setInputParam(mInputParam).setOutputData(list).request(Request.Method.GET);
			Logger.d("GetPackageListTask", "code:" + code);
			if (code == 0) {
				vipPic = request.getVipPic();
				return true;
			} 
			return false;
		}
    	
    }
	
}
