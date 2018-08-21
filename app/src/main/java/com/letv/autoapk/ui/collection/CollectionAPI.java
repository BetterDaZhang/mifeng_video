package com.letv.autoapk.ui.collection;

import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.letv.autoapk.base.activity.BaseActivity;
import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.CollectionRecordDao;
import com.letv.autoapk.dao.CollectionRecordInfo;

public class CollectionAPI {

	CollectionRecordDao dao;
	BaseActivity context;

	public CollectionAPI(BaseActivity context) {
		this.context = context;
		dao = MyApplication.getInstance().getDaoByKey(CollectionRecordDao.class.getName());
	}

	public static void startCollection(Context context) {
		Intent intent = new Intent(context, ContainerActivity.class);
		intent.putExtra(ContainerActivity.FRAGMENTNAME, CollectionFragment.class.getName());
		context.startActivity(intent);
	}

	/**
	 * warning:子线程中！！
	 */
	public void addAllCollections() {
		List<CollectionRecordInfo> infos = dao.findAll();
		if (infos == null) {
			return;
		}
		OpCollectRecordsRequest recordsRequest = new OpCollectRecordsRequest(context);
		int code = recordsRequest.collectRecords(context, infos);
		
		//收藏记录上传成功则删除本地
		if (code == 0) {
			deleteNativeColletions();
		}
	}

	public void deleteNativeColletions() {
		if (dao == null) {
			dao = MyApplication.getInstance().getDaoByKey(CollectionRecordDao.class.getName());
		} 
		dao.delete(dao.findAll());
	}
}
