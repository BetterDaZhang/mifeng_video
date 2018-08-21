package com.letv.autoapk.boss;

import java.io.Serializable;

public class LepayInfo implements Serializable {
	public String mVersion = "2.0";// 支付版本号
	public String mUserId = ""; // 不能为空，用户Id
	public String mUserName = "";// 用户名
	public String mLetvUserId = ""; // 不能为空,乐视集团商户
	public String mNotifyUrl = ""; // 不能为空，通知地址
	public String mMerchantNo = "";// 订单号
	public String mOutTradeNo = "";// 交易流水号
	public String mPrice = ""; // 不能为空，价格
	public String mCurrency = "";// 币种 "CNY"
	public String mPayExpire = ""; // 不能为空,交易自动关闭时间 "1000"
	public String mDeptid = ""; // 不能为空, "0"
	public String mPid = ""; // 不能为空 "0"
	public String mProductid = ""; // 不能为空，商品Id
	public String mProductName = ""; // 不能为空，商品名称
	public String mProductDesc = ""; // 不能为空，商品描述
	public String mProductUrls = ""; // 不能为空，商品地址
	public String mKeyIndex = ""; // 不能为空,密钥索引 1
	public String mInputCharset = "";// 字符编码 UTF-8
	public String mSignType = ""; // 不能为空，签名方式 MD5
	public String signKey; // 不能为空，密钥
	public String marchantBusinessId;// 不能为空，业务线Id
	public String app_id;// 租户id
	public String timestamp;// 时间戳
	public String appleSign = "3b8c974a10134a588605573537713c60";//苹果公钥
	public String mCountryCode;

}
