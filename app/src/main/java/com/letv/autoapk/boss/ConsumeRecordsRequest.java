package com.letv.autoapk.boss;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;

public class ConsumeRecordsRequest extends StringDataRequest {

	public ConsumeRecordsRequest(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getUrl() {
		// TODO Auto-generated method stub
		return "/orderPageList";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {
		// TODO Auto-generated method stub

	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData)
			throws JSONException {
		if (statusCode == 0 && !TextUtils.isEmpty(content)) {
			List<ConsumeInfo> infos = (List<ConsumeInfo>) outputData[0];
			PageInfo pageInfo = (PageInfo) outputData[1];
			JSONObject jsonObj = new JSONObject(content).getJSONObject("data");
			JSONArray listJson = jsonObj.getJSONArray("rows");
			for (int i = 0; i < listJson.length(); i++) {
				JSONObject object = listJson.getJSONObject(i);
				ConsumeInfo info = new ConsumeInfo();
				info.date = object.getString("endTime");
				info.datetime = object.getString("createTime");
				info.desc = object.getString("price");
				info.name = object.getString("name");
				info.number = object.getString("id");
				info.status = object.getInt("status");
				info.type = object.getInt("payType");
				JSONObject payObject = object.getJSONObject("payInfo");
				LepayInfo lepayInfo = new LepayInfo();
				lepayInfo.marchantBusinessId = payObject.optString("merchant_business_id");
				lepayInfo.signKey = payObject.optString("sign");
				lepayInfo.mLetvUserId = payObject.optString("letv_user_id");
				lepayInfo.mNotifyUrl = payObject.optString("notify_url");
				lepayInfo.mMerchantNo = payObject.optString("merchant_no");
				lepayInfo.mOutTradeNo = payObject.optString("out_trade_no");
				lepayInfo.mCurrency = payObject.optString("currency");
				lepayInfo.mPayExpire = payObject.optString("pay_expire");
				lepayInfo.mDeptid = payObject.optString("mdeptid");
				lepayInfo.mProductName = payObject.optString("product_name");
				lepayInfo.mProductDesc = payObject.optString("product_desc");
				lepayInfo.mPid = payObject.optString("mpid");
				lepayInfo.mProductid = payObject.optString("product_id");
				lepayInfo.mProductUrls = payObject.optString("mProductUrls");
				lepayInfo.mKeyIndex = payObject.optString("key_index");
				lepayInfo.mInputCharset = payObject.optString("input_charset");
				lepayInfo.mSignType = payObject.optString("sign_type");
				lepayInfo.timestamp = payObject.optString("timestamp");
				lepayInfo.app_id = payObject.optString("app_id");
				lepayInfo.mCountryCode  = payObject.optString("country_code");
				lepayInfo.mUserId = payObject.optString("user_id");
				lepayInfo.mUserName = payObject.optString("user_name");
				lepayInfo.mVersion = payObject.optString("version");
				lepayInfo.mPrice = payObject.optString("price");
				info.lepayInfo = lepayInfo;
				infos.add(info);
			}
			JSONObject page = jsonObj.optJSONObject("page");
			if (page != null) {
				pageInfo.setPageIndex(page.optInt("currentPage"));
				pageInfo.setTotalCount(page.optInt("totalCount"));
				pageInfo.setTotalPage(page.optInt("totalPage"));
			}
		} else {
			Toast.makeText(mContext, alertMessage, Toast.LENGTH_SHORT).show();
		}
		return statusCode;
	}

}
