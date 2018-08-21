package com.letv.autoapk.dao;

import java.util.List;

import org.xutils.db.Selector;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import android.content.Context;

import com.letv.autoapk.base.db.BaseDao;
import com.letv.autoapk.common.utils.Logger;

public class SearchHistoryDao extends BaseDao<SearchHistoryInfo> {

	public SearchHistoryDao(Context context) {
		super(context);
	}

	@Override
	public boolean save(SearchHistoryInfo t) {
		try {
			db.getDb().delete(SearchHistoryInfo.class, WhereBuilder.b("sarchTitle", "=", t.getSarchTitle()));
			db.getDb().save(t);
			changeNotify(this);
			return true;
		} catch (DbException e) {
			Logger.log(e);
			return false;
		}
	}

	@Override
	public List<SearchHistoryInfo> findAll() {
		try {
			List<SearchHistoryInfo> list = db.getDb().selector(SearchHistoryInfo.class).findAll();
			return list;
		} catch (DbException e) {
			Logger.log(e);
			return null;
		}
	}

	@Override
	public SearchHistoryInfo update(SearchHistoryInfo t) {
		return null;
	}

	@Override
	public SearchHistoryInfo delete(SearchHistoryInfo t) {
		try {
			db.getDb().delete(t);
			changeNotify(this);
			return t;
		} catch (DbException e) {
			Logger.log(e);
			return null;
		}
	}

	@Override
	public void save(List<SearchHistoryInfo> list) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(List<SearchHistoryInfo> list) {
		try {
			db.getDb().delete(list);
			changeNotify(this);
		} catch (DbException e) {
			Logger.log(e);
		}

	}

	public void deleteAll() {
		try {
			db.getDb().delete(SearchHistoryInfo.class);
		} catch (Exception e) {
			Logger.log(e);
		}
	}

}
