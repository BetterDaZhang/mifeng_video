package com.letv.autoapk.dao;

import java.util.List;

import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import android.content.Context;
import android.util.Log;

import com.letv.autoapk.base.db.BaseDao;
import com.letv.autoapk.base.net.ChannelInfo;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.ui.channel.ChannelVideoInfo;

public class ChannelEditDao extends BaseDao<ChannelVideoInfo> {

	public ChannelEditDao(Context context) {
		super(context);
	}

	public ChannelVideoInfo findById(String channelId, String videoId) {
		try {
			ChannelVideoInfo info = db.getDb().findBySpecialColum(ChannelVideoInfo.class, channelId, videoId);
			return info;
		} catch (DbException e) {
			Logger.log(e);
		}
		return null;

	}

	@Override
	public ChannelVideoInfo update(ChannelVideoInfo channelVideoinfo) {
		try {
			db.getDb().update(channelVideoinfo, WhereBuilder.b("mChannelId", "=", channelVideoinfo.getChannelId()));
			changeNotify(this);
			return channelVideoinfo;
		} catch (DbException e) {
			Logger.log(e);
			return null;
		}
	}

	@Override
	public boolean save(ChannelVideoInfo info) {
		try {
			db.getDb().save(info);
			changeNotify(this);
			return true;
		} catch (DbException e) {
			Logger.log(e);
			return false;
		}
	}

	@Override
	public List<ChannelVideoInfo> findAll() {
		try {
			return db.getDb().findAll(ChannelVideoInfo.class);
		} catch (DbException e) {
			Logger.log(e);
			return null;
		}
	}

	public long count() {
		try {
			return db.getDb().selector(ChannelVideoInfo.class).count();
		} catch (DbException e) {
			Logger.log(e);
			return 0;
		}
	}

	@Override
	public ChannelVideoInfo delete(ChannelVideoInfo info) {
		try {
			db.getDb().delete(info);
			return info;
		} catch (DbException e) {
			Logger.log(e);
			return null;
		}
	}

	@Override
	public void save(List<ChannelVideoInfo> lists) {
		try {
			db.getDb().save(lists);
			changeNotify(this);
		} catch (DbException e) {
			Logger.log(e);
		}
	}

	@Override
	public void delete(List<ChannelVideoInfo> list) {
		try {
				db.getDb().delete(list);
		} catch (DbException e) {
			Logger.log(e);
		}
	}
}
