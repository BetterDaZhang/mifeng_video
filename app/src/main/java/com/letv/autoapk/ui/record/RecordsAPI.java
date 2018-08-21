package com.letv.autoapk.ui.record;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.base.fragment.BaseFragment;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.PlayRecordInfo;
import com.letv.autoapk.dao.PlayedRecordDao;
import com.letv.autoapk.utils.NetworkUtils;

import android.content.Context;
import android.content.Intent;

/**
 * 添加观看记录和删除记录<br>
 * 播放记录存储的时候肯定存数据库，所以展示的时候只需要拿本地数据库的数据
 */
public class RecordsAPI {

	private JSONObject obj;
	BaseFragment fragment;
	static List<PlayRecordInfo> records = new LinkedList<PlayRecordInfo>();
	PageInfo pageInfo = new PageInfo();

	public RecordsAPI(BaseFragment fragment) {
		this.fragment = fragment;
	}

	public static void startPlayRecord(Context context) {
		Intent intent = new Intent(context, ContainerActivity.class);
		intent.putExtra(ContainerActivity.FRAGMENTNAME, PlayRecordFragment.class.getName());
		context.startActivity(intent);
	}

	/**
	 * 子线程！！！！
	 * 
	 * @param context
	 * @return
	 */
	public List<PlayRecordInfo> getRecords(final Context context) {
		pageInfo = new PageInfo();
		PlayedRecordDao dao = MyApplication.getInstance().getDaoByKey(PlayedRecordDao.class.getName());
		if (records == null) {
			records = new LinkedList<PlayRecordInfo>();
		}
		records.clear();
		if (MyApplication.getInstance().isLogin()) {
			GetPlayRecordsDataRequest request = new GetPlayRecordsDataRequest(context);
			Map<String, String> mInputParam = new HashMap<String, String>();
			mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(context));
			mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
			mInputParam.put(StringDataRequest.PAGE, String.valueOf(1));
			mInputParam.put(StringDataRequest.PAGE_SIZE, String.valueOf(StringDataRequest.PAGE_SIZE_COUNT));
			int code = request.setInputParam(mInputParam).setOutputData(records, pageInfo).request(Request.Method.GET);
			if (code == 0 && records != null && !records.isEmpty()) {
				for (int i = records.size() - 1; i >= 0; i--) {
					dao.save(records.get(i));
				}
			}
		} else {
			records = dao.findAll();
		}
		return records;
	}

	public void addRecords(final Context context, final String videoId, final long lastPlayTime, final long lastOpenTime, final String nextLinkUrl) {
		if (MyApplication.getInstance().isLogin() && NetworkUtils.isNetAvailable(context)) {
			new UiAsyncTask<Boolean>(fragment) {
				@Override
				protected Boolean doBackground() throws Throwable {
					AddPlayRecordsDataRequest request = new AddPlayRecordsDataRequest(context);
					Map<String, String> mInputParam = new HashMap<String, String>();
					mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(context));
					mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
					JSONArray jsonArray = new JSONArray();
					JSONObject object = new JSONObject();
					try {
						obj = new JSONObject();
						obj.put("videoId", videoId);
						obj.put("playTime", lastPlayTime);
						obj.put("openTime", lastOpenTime / 1000);
						obj.put("nextLinkUrl", nextLinkUrl);
						jsonArray.put(obj);
						object.put("playRecordList", jsonArray);
					} catch (JSONException e) {
						Logger.log(e);
					}
					mInputParam.put("playRecordList", object.toString());
					int code = request.setInputParam(mInputParam).request(Request.Method.POST);
					if (code == 0) {
						return true;
					} else {
						return false;
					}
				}
			}.execute();
		} else {
		}
	}

	/**
	 * 本地播放记录与云端同步
	 * 
	 * @param context
	 */
	public void addAllRecords(final Context context) {
		PlayedRecordDao dao = MyApplication.getInstance().getDaoByKey(PlayedRecordDao.class.getName());
		final List<PlayRecordInfo> infos = dao.findAll();
		if (infos == null || infos.isEmpty()) {
			return;
		}
		AddPlayRecordsDataRequest request = new AddPlayRecordsDataRequest(context);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(context));
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		JSONArray jsonArray = new JSONArray();
		JSONObject object = new JSONObject();
		try {
			for (PlayRecordInfo playRecordInfo : infos) {
				obj = new JSONObject();
				obj.put("videoId", playRecordInfo.getVideoId());
				obj.put("playTime", playRecordInfo.getLastPlayTime());
				obj.put("openTime", playRecordInfo.getLastOpenTime() / 1000);
				obj.put("nextLinkUrl", playRecordInfo.getNextLinkUrl());
				jsonArray.put(obj);
			}
			object.put("playRecordList", jsonArray);
		} catch (JSONException e) {
			Logger.log(e);
		}
		mInputParam.put("playRecordList", object.toString());
		int code = request.setInputParam(mInputParam).request(Request.Method.POST);
	}

	/** 批量删除播放记录，需上传播放记录的id，播放记录id需要记录 */
	public boolean removePlayRecords(Context context, String videoId, long playTime, String nextLinkUrl) {
		AddPlayRecordsDataRequest request = new AddPlayRecordsDataRequest(context);
		Map<String, String> mInputParam = new HashMap<String, String>();
		mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(context));
		mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		JSONArray jsonArray = new JSONArray();
		JSONObject object = new JSONObject();
		try {
			obj = new JSONObject();
			obj.put("videoId", videoId);
			obj.put("playTime", playTime);
			obj.put("nextLinkUrl", nextLinkUrl);
			jsonArray.put(obj);
			object.put("playRecordList", jsonArray);
		} catch (JSONException e) {
			Logger.log(e);
		}
		mInputParam.put("playRecordList", object.toString());
		int code = request.setInputParam(mInputParam).request(Request.Method.POST);
		if (code == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static PlayRecordInfo getLastRecord() {
		PlayedRecordDao dao = MyApplication.getInstance().getDaoByKey(PlayedRecordDao.class.getName());
		records = dao.findAll();
		if (records != null && records.size() > 0) {
			return records.get(0);
		}
		return null;
	}
}
