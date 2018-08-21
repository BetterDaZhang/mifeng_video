package com.letv.autoapk.boss;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.Request;
import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.context.MyApplication;

public class OrderInformationFragment extends BaseTitleFragment implements OnClickListener {
	private String orderExpire;
//	private String orderPrice;
	private String albumId;
	private String title = "";
	private Context context;
	private LepayInfo lepayInfo;
	private TextView orderTitleTxt;
	private TextView orderTitileDescribe;
	private TextView orderNumTxt;
	private TextView orderExpireTxt;
	private TextView orderPriceTxt;
	private TextView orderPay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		Bundle bundle = getActivity().getIntent().getExtras();
		orderExpire = bundle.getString("orderExpire");
//		orderPrice = bundle.getString("orderPrice");
		albumId = bundle.getString("albumId");
		title = bundle.getString("title");
	}

	@Override
	protected boolean loadingData() {
		lepayInfo = new LepayInfo();
		LepayAlbumOrderCreateDataRequest request = new LepayAlbumOrderCreateDataRequest(context);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put("albumId", albumId);
		mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(context));
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputParam.put("version", LepayManager.LEPAY_VERSION);	
		int code = request.setInputParam(mInputParam).setOutputData(lepayInfo).request(Request.Method.GET);
		if (code == 0) {
			return true;
		}
		return false;

	}

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	protected void initCustomerView() {
		setTitle(getString(R.string.order_information), getResources().getColor(R.color.code1));
		setTitleLeftResource(R.drawable.base_head_back, mActivity.dip2px(3));

		setLeftClickListener(new TitleLeftClickListener() {
			@Override
			public void onLeftClickListener() {
				getActivity().finish();
			}
		});
	}

	@Override
	protected View setupDataView() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View root = inflater.inflate(R.layout.boss_orderinfo, null);
		orderTitleTxt = (TextView) root.findViewById(R.id.boss_order_title);
		orderTitileDescribe = (TextView) root.findViewById(R.id.boss_order_describe);
		orderNumTxt = (TextView) root.findViewById(R.id.boss_order_num);
		orderExpireTxt = (TextView) root.findViewById(R.id.boss_order_expire);
		orderPriceTxt = (TextView) root.findViewById(R.id.boss_order_price);
		orderPay = (TextView) root.findViewById(R.id.boss_order_pay);
		orderPay.setOnClickListener(this);

		orderTitleTxt.setText(title);
		if (TextUtils.isEmpty(lepayInfo.mProductDesc.trim())) {
			orderTitileDescribe.setVisibility(View.GONE);
		} else {
			orderTitileDescribe.setVisibility(View.VISIBLE);
			orderTitileDescribe.setText(lepayInfo.mProductDesc);
		}
		orderNumTxt.setText(getResources().getString(R.string.order_num) + lepayInfo.mMerchantNo);
		orderExpireTxt.setText(getResources().getString(R.string.order_expire) + orderExpire + " "
				+ getResources().getString(R.string.order_expire_day));
		orderPriceTxt.setText(lepayInfo.mPrice + " " +getResources().getString(R.string.order_price_rmb));
		return root;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.boss_order_pay:
			LepayManager lepayManager = LepayManager.getInstance(mActivity, OrderInformationFragment.this);
			lepayManager.albumOrder(lepayInfo);
			break;

		default:
			break;
		}

	}

}
