package com.letv.autoapk.boss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.toolbox.ImageLoader;
import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.common.net.LruImageCache;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.widgets.NetImageView;
import com.letv.lepaysdk.ELePayState;
import com.viewpagerindicator.TabPageIndicator;

public class MemberCenterFragment extends BaseTitleFragment {
	/**
	 * lepay
	 */
    private List<List<PackageInfo>> list;
    private SparseArray<String> vipPic;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerLePayCallback();
	}

	@Override
	protected boolean loadingData() {
		list = new ArrayList<List<PackageInfo>>();
		VipPackageRequest2 request = new VipPackageRequest2(mActivity);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		int code = request.setInputParam(mInputParam).setOutputData(list).request(Request.Method.GET);
		Logger.d("GetPackageListTask", "code:" + code);
		if (code == 0) {
			vipPic = request.getVipPic();
			return true;
		} 
		return false;
	}

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	protected void initCustomerView() {
		setTitle(getString(R.string.title_member), getResources().getColor(R.color.code6));
		setTitleLeftResource(R.drawable.base_head_back, mActivity.dip2px(3));

		setLeftClickListener(new TitleLeftClickListener() {
			@Override
			public void onLeftClickListener() {
				getActivity().finish();
			}
		});
	}

	/*@Override
	protected View createContentView() {
		return setupDataView();
	}*/

	private LayoutInflater localInflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		try {
			final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.StyledIndicators);
			// clone the inflater using the ContextThemeWrapper
			localInflater = inflater.cloneInContext(contextThemeWrapper);
			return super.onCreateView(localInflater, container, savedInstanceState);
		} catch (Exception e1) {
			Logger.log(e1);
		}
		return mRoot;
	}
	
	@Override
	protected boolean hasContentData() {
		return list != null && !list.isEmpty();
	}

	private TabPageIndicator indicator;
	private ViewPager pager;
    private NetImageView imageView;
    private FragmentPagerAdapter adapter;
	@Override
	protected View setupDataView() {
		View view = localInflater.inflate(R.layout.boss_members, null);
		FragmentManager fragmentManager = getChildFragmentManager();
		pager = (ViewPager) view.findViewById(R.id.pager);
		SparseArray<String> names = new SparseArray<String>(list.size());
		String[] titles = new String[list.size()];
		for(int i = 0;i<list.size();i++){
			names.append(i, MemberMobileFragment.class.getName());
			titles[i] = list.get(i).get(0).name;
		}
		adapter = new BossPagerAdapter(fragmentManager, names, mActivity, titles,list);
		pager.setAdapter(adapter);

		indicator = (TabPageIndicator) view.findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		indicator.setBackgroundColor(getResources().getColor(R.color.code01));
		indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int index) {
				if (!TextUtils.isEmpty(vipPic.get(index)))
					imageView.setImageUrl(vipPic.get(index), LruImageCache.getImageLoader(mActivity));
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		if(list.size()<=1){
			indicator.setVisibility(View.GONE);
		}
		imageView = (NetImageView) view.findViewById(R.id.top_image);
		imageView.setDefaultImageResId(R.drawable.default_img_22_10);
		imageView.setErrorImageResId(R.drawable.default_img_22_10);
		imageView.setCoverUrl(vipPic.get(0), mActivity);
		return view;
	}

	void registerLePayCallback() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ELePayState.FAILT.toString());
		intentFilter.addAction(ELePayState.OK.toString());
		intentFilter.addAction(ELePayState.WAITTING.toString());
		intentFilter.addAction(ELePayState.NONE.toString());
		getActivity().registerReceiver(broadcastReceiver, intentFilter);
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(broadcastReceiver);
	};
}
