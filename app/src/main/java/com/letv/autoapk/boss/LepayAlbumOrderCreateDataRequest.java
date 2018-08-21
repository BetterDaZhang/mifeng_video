package com.letv.autoapk.boss;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import com.letv.autoapk.base.net.StringDataRequest;

import android.content.Context;

public class LepayAlbumOrderCreateDataRequest extends StringDataRequest {

	public LepayAlbumOrderCreateDataRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/albumOrderCreate";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData)
			throws JSONException {
		if (statusCode == 0) {
			LepayInfo lepayInfo = (LepayInfo) outputData[0];
			JSONObject jsonDataObj = new JSONObject(content);
			JSONObject jsonObj = jsonDataObj.optJSONObject("data");
			lepayInfo.marchantBusinessId = jsonObj.optString("merchant_business_id");
			lepayInfo.signKey = jsonObj.optString("sign");
			lepayInfo.mLetvUserId = jsonObj.optString("letv_user_id");
			lepayInfo.mNotifyUrl = jsonObj.optString("notify_url");
			lepayInfo.mMerchantNo = jsonObj.optString("merchant_no");
			lepayInfo.mOutTradeNo = jsonObj.optString("out_trade_no");
			lepayInfo.mCurrency = jsonObj.optString("currency");
			lepayInfo.mPayExpire = jsonObj.optString("pay_expire");
			lepayInfo.mDeptid = jsonObj.optString("mdeptid");
			lepayInfo.mProductName = jsonObj.optString("product_name");
			lepayInfo.mProductDesc = jsonObj.optString("product_desc");
			lepayInfo.mPid = jsonObj.optString("mpid");
			lepayInfo.mProductid = jsonObj.optString("product_id");
			lepayInfo.mProductUrls = jsonObj.optString("mProductUrls");
			lepayInfo.mKeyIndex = jsonObj.optString("key_index");
			lepayInfo.mInputCharset = jsonObj.optString("input_charset");
			lepayInfo.mSignType = jsonObj.optString("sign_type");
			lepayInfo.timestamp = jsonObj.optString("timestamp");
			lepayInfo.app_id = jsonObj.optString("app_id");
			lepayInfo.mCountryCode  = jsonObj.optString("country_code");
			lepayInfo.mUserId = jsonObj.optString("user_id");
			lepayInfo.mUserName = jsonObj.optString("user_name");
			lepayInfo.mVersion = jsonObj.optString("version");
			lepayInfo.mPrice = jsonObj.optString("price");
		}
		return statusCode;
	}

}
