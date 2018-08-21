package com.letv.autoapk.ui.discover;

import java.util.Map;
import java.util.TreeMap;

import android.content.Context;

import com.android.volley.Request;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.utils.SystemUtls;

/**
 * 发现页点赞
 */
class PraiseUtils {
	/**
	 * 
	 * @param doSupport
	 *            1 顶 2 踩
	 * @param userId
	 *            用户id，没有用户ID，则用设备ID
	 * @param tenantId
	 *            租户Id
	 */
	public boolean sendPraiseRequest(String praiseType, String doSupport, String targetId, String userId,
			String tenantId, Context context) {
		SendPriseRequest sendPriseRequest = new SendPriseRequest(context);
		Map<String, String> mInputParam = new TreeMap<String, String>();
		mInputParam.put("praiseType", praiseType);
		mInputParam.put("targetId", targetId);
		mInputParam.put("clientId", SystemUtls.getIMEI(context));
		mInputParam.put("userId", userId);
		mInputParam.put(StringDataRequest.TENANT_ID, tenantId);
		mInputParam.put("optType", doSupport);
		// }
		int code = sendPriseRequest.setInputParam(mInputParam).setOutputData(new Object()).request(Request.Method.GET);
		if (code == 0) {
			return true;
		} else {
			return false;
		}
	}

}
