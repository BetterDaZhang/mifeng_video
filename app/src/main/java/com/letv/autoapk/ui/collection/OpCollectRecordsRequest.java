package com.letv.autoapk.ui.collection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.CollectionRecordInfo;

import android.content.Context;

/**
 * 收藏和取消收藏的接口。在这个接口中没有返回的数据
 * 
 * 
 */
class OpCollectRecordsRequest extends StringDataRequest {


	public OpCollectRecordsRequest(Context context) {
		super(context);
	}

	@Override
	protected String getUrl() {
		return "/doVideoCollect";
	}

	@Override
	protected void onMakeRequestParam(Map<String, String> InputParam) {

	}

	@Override
	protected String getToken() {
		return LoginInfoUtil.getToken(mContext);
	}

	@Override
	protected int onParseResponse(int statusCode, String alertMessage, String content, Object[] outputData) throws JSONException {
		return statusCode;
	}

	/**
	 * 取消视频的收藏
	 * 
	 * @param context
	 * @param infos
	 *            需要取消收藏的数据
	 * @return 返回非0表示取消收藏失败
	 */
	public static int unCollectRecords(Context context, List<CollectionRecordInfo> infos) {
		OpCollectRecordsRequest request = new OpCollectRecordsRequest(context);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put("doCollect", "0");
		mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(context));
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputParam.put("clientId", MyApplication.getInstance().getClientId());
		try {
			JSONArray array = new JSONArray();
			JSONObject obj;
			JSONObject obj2 = new JSONObject();
			for (CollectionRecordInfo info : infos) {
				obj = new JSONObject();
				obj.put("videoId", info.getVideoId());
				obj.put("ablumId", info.getAlbumId());
				obj.put("favoriteId", info.getFavoriteId());
				array.put(obj);
			}
			obj2.put("refIdList", array);
			mInputParam.put("refIdList", obj2.toString());
		} catch (JSONException e) {
			Logger.log(e);
		}
		return request.setInputParam(mInputParam).setOutputData(new String()).request(Method.POST);
	}

	/**
	 * 数据收藏的借口
	 * 
	 * @param context
	 * @param infos
	 *            需要收藏的数据
	 * @return 返回非0表示收藏失败
	 */
	public static int collectRecords(Context context, List<CollectionRecordInfo> infos) {
		OpCollectRecordsRequest request = new OpCollectRecordsRequest(context);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put("doCollect", "1");
		mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(context));
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputParam.put("clientId", MyApplication.getInstance().getClientId());
		try {
			JSONArray array = new JSONArray();
			JSONObject obj;
			JSONObject obj2 = new JSONObject();
			for (CollectionRecordInfo info : infos) {
				obj = new JSONObject();
				obj.put("videoId", info.getVideoId());
				obj.put("ablumId", info.getAlbumId());
				array.put(obj);
			}
			obj2.put("refIdList", array);
			mInputParam.put("refIdList", obj2.toString());
		} catch (JSONException e) {
			Logger.log(e);
		}
		return request.setInputParam(mInputParam).setOutputData(new String()).request(Method.POST);
	}

}
