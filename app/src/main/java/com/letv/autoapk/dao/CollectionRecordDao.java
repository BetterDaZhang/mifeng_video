package com.letv.autoapk.dao;

import java.util.List;

import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import android.content.Context;

import com.letv.autoapk.base.db.BaseDao;
import com.letv.autoapk.common.utils.Logger;

public class CollectionRecordDao extends BaseDao<CollectionRecordInfo> {

	public CollectionRecordDao(Context context) {
		super(context);
	}

	public CollectionRecordInfo findBySpecialColum(String columnName, String specValue) {
		try {
			CollectionRecordInfo info = db.getDb().findBySpecialColum(CollectionRecordInfo.class, columnName,
					specValue);
			return info;
		} catch (DbException e) {
			Logger.log(e);
		}
		return null;

	}

	@Override
	public boolean save(CollectionRecordInfo t) {
		try {
			db.getDb().save(t);
			changeNotify(this);
			return true;
		} catch (DbException e) {
			Logger.log(e);
			return false;
		}
	}
	
	@Override
	public void save(List<CollectionRecordInfo> lists) {
		try {
			db.getDb().save(lists);
			changeNotify(this);
		} catch (DbException e) {
			Logger.log(e);
		}
	}

	@Override
	public List<CollectionRecordInfo> findAll() {
		try {
			return db.getDb().selector(CollectionRecordInfo.class).findAll();
		} catch (DbException e) {
			Logger.log(e);
			return null;
		}
	}

	/**
	 * 获取用户已经删除的。但是还没有从数据库中完全删除的数据
	 * 
	 * @return
	 */
	public List<CollectionRecordInfo> findAllDelete() {
		try {
			return db.getDb().findAll(CollectionRecordInfo.class);
		} catch (DbException e) {
			Logger.log(e);
			return null;
		}
	}

	@Override
	public CollectionRecordInfo update(CollectionRecordInfo t) {
		try {
			db.getDb().update(t, WhereBuilder.b("videoId", "=", t.getVideoId()), "iflag");
			changeNotify(this);
			return t;
		} catch (DbException e) {
			Logger.log(e);
			return null;
		}
	}

	public void updateList(List<CollectionRecordInfo> list) {
		try {
			for (CollectionRecordInfo collectRecord : list) {
				db.getDb().update(collectRecord, WhereBuilder.b("videoId", "=", collectRecord.getVideoId()), "iflag");
			}
			changeNotify(this);
		} catch (DbException e) {
			Logger.log(e);
		}
	}

	public long count() {
		try {
			return db.getDb().selector(CollectionRecordInfo.class).count();
		} catch (DbException e) {
			Logger.log(e);
			return 0;
		}
	}

	@Override
	public CollectionRecordInfo delete(CollectionRecordInfo t) {
		try {
			db.getDb().delete(t);
			return t;
		} catch (DbException e) {
			Logger.log(e);
			return null;
		}
	}

	@Override
	public void delete(List<CollectionRecordInfo> list) {
		try {
			for (CollectionRecordInfo collectRecord : list) {
				db.getDb().delete(collectRecord);
			}
		} catch (DbException e) {
			Logger.log(e);
		}
	}
}
