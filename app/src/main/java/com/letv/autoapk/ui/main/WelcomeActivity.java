package com.letv.autoapk.ui.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.volley.Request;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.base.net.StringDataRequest.DataRequestCallback;
import com.letv.autoapk.boss.BossLoginFragment;
import com.letv.autoapk.common.net.HttpEngine;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.context.SyncService;
import com.letv.autoapk.ui.mobilelive.MobileLiveAPI;
import com.letv.autoapk.ui.mobilelive.MobileLiveListFragment;
import com.letv.autoapk.utils.SystemUtls;
import com.umeng.message.PushAgent;

public class WelcomeActivity extends BaseActivity implements OnClickListener {
	private String TAG = "WelcomeActivity";
	private FrameLayout root;
//	ImageView iv_start;
	private String adUrl;
	public ArrayList<String> click;
	public ArrayList<String> impression;
	private int adShowTime;
	private String clickUrl;
	private View baiduAdlayout;
	private ImageView skipImg;
	private ImageView baiduImgAd;
	private boolean isBaiduChannel = false;

	@Override
	protected void onHandleMessage(Message msg) {
		if(msg.what==1){
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//			Intent intent = new Intent(getApplicationContext(), ContainerActivity.class);
//			intent.putExtra(ContainerActivity.FRAGMENTNAME, MobileLiveAPI.getLiveFragmentName());
			startActivity(intent);
			finish();
		}
	}

	@Override
	protected void onCreate(Bundle arg0) {
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(arg0);
//		MobclickAgent.openActivityDurationTrack(false);
//		MobclickAgent.setDebugMode(false);
//		AnalyticsConfig.enableEncrypt(true);
		//初始化缓存数据
		HttpEngine httpEngine = HttpEngine.getInstance(getApplicationContext());
		// 判断网络环境
		if (!SystemUtls.isNetworkConnected(this)) {
			showToastSafe(getString(R.string.base_networkerror), 0);
		}else if(MyApplication.getInstance().isLogin()){
			refreshToken();
		}
		SyncService.actionBoss(getApplicationContext());
		// 根布局
//		root = new FrameLayout(this);
//		root.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		setContentView(root);
		// 欢迎页
		View view = new View(this);
		view.setBackgroundResource(R.drawable.welcome_normal);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		root.addView(view);// 添加欢迎页
		setContentView(view);
		sendMessage(1, 2000);
	}

	private void refreshToken() {
		RefreshTokenDataRequest request = new RefreshTokenDataRequest(this);
		Map<String,String> inputParam= new HashMap<String,String>();
		inputParam.put("refreshToken", MyApplication.getInstance().getString(MyApplication.REFRESH_TOKEN));
		request.setInputParam(inputParam).requestTask(Request.Method.GET, new DataRequestCallback() {
			
			@Override
			public void onDataRequestSuccess(Object[] mOutputData) {
				
			}
			@Override
			public void onDataRequestFailed(int errorCode, String msg) {
				
			}
		});
	}

	@Override
	public void onClick(View v) {
//		startMainActivity();
//		sp.edit().putBoolean(IS_FIRST_USE, false).commit();
	}

	/** 开启主界面activity */
	/*private void startMainActivity() {
		Intent intent = new Intent(WelcomeActivity.this, AdActivity.class);
		intent.putExtra("AD_URL", adUrl);
		intent.putExtra("AD_SHOW_TIME", adShowTime);
		intent.putExtra("CLICK_URL", clickUrl);
		intent.putExtra("CLICK_ENENT", click);
		intent.putExtra("IMPRESSION_ENENT", impression);
		startActivity(intent);
		overridePendingTransition(R.anim.fade, R.anim.hold);// 切换Activity的过渡动画
		finish();
	}*/

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 欢迎页viewPager适配器
	 * 
	 * @author wangzhen5
	 */
	/*class WelcomeViewpagerAdapter extends PagerAdapter {

		private List<Integer> list;
		private Context context;
		List<View> views = new ArrayList<View>();

		public WelcomeViewpagerAdapter(List<Integer> list, Context context) {
			this.list = list;
			this.context = context;
			RelativeLayout.LayoutParams mParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT);
			// 初始化引导图片列表

			for (int i = 0; i < list.size(); i++) {
				ImageView iv = new ImageView(context);
				iv.setLayoutParams(mParams);
				iv.setBackgroundResource(list.get(i));

				if (i == 3) {
					RelativeLayout rl = new RelativeLayout(context);
					rl.addView(iv);
					iv_start.setImageResource(R.drawable.guiding_skip_btn);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.addRule(RelativeLayout.CENTER_HORIZONTAL);
					params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					params.setMargins(0, 0, 0, dip2px(24));
					iv_start.setLayoutParams(params);
					iv_start.setVisibility(View.VISIBLE);
					rl.addView(iv_start);
					views.add(rl);
				} else {
					views.add(iv);
				}

			}
		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(views.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(views.get(position));
			return views.get(position);
		}
	}*/

}
