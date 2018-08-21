package com.letv.autoapk.dao;

import java.util.ArrayList;
import java.util.List;

import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import android.content.Context;
import android.text.TextUtils;

import com.letv.autoapk.base.db.BaseDao;
import com.letv.autoapk.base.net.DisplayVideoInfo;
import com.letv.autoapk.common.utils.Logger;

/**
 * 播放历史记录 的Dao层
 * 
 * @author wangqiangqiang
 * 
 */
public class PlayedRecordDao extends BaseDao<PlayRecordInfo> {

	public PlayedRecordDao(Context context) {
		super(context);
	}

	@Override
	public boolean save(PlayRecordInfo t) {
		try {
			db.getDb().delete(PlayRecordInfo.class, WhereBuilder.b("videoId", "=", t.getVideoId()));
			if(!"0".equals(t.getAlbumId())&&!TextUtils.isEmpty(t.getAlbumId())){
				db.getDb().delete(PlayRecordInfo.class, WhereBuilder.b("albumId", "=", t.getAlbumId()));
			}
			db.getDb().save(t);
			changeNotify(this);
			return true;
		} catch (DbException e) {
			Logger.log(e);
			return false;
		}
	}

	/**
	 * 查询出来的数据会根据添加的时间顺序去进行排序
	 */
	@Override
	public List<PlayRecordInfo> findAll() {
		try {
			List<PlayRecordInfo> list = db.getDb().selector(PlayRecordInfo.class).orderBy("lastOpenTime", true).findAll();
			return list;
		} catch (DbException e) {
			Logger.log(e);
			return null;
		}
	}

	/**
	 * 根据videoId查询时间
	 */
	public PlayRecordInfo findPlayRecord(String videoId) {
		try {
			List<PlayRecordInfo> playrecords = db.getDb().selector(PlayRecordInfo.class).where(WhereBuilder.b("videoId", "=", videoId)).findAll();
			if (playrecords.size() > 0) {
				PlayRecordInfo playrecord = playrecords.get(0);
				return playrecord;
			} else {
				return null;
			}
		} catch (Exception e) {
			Logger.log(e);
			return null;
		}
	}

	/**
	 * 查询最新的几条数据
	 * 
	 * @param items
	 *            需要查询的数据条数
	 * @return 返回对应的几条数据。返回的数据《= items
	 */
	public List<PlayRecordInfo> findNumItem(int items) {
		try {
			return db.getDb().selector(PlayRecordInfo.class).orderBy("lastOpenTime", true).limit(items).findAll();
		} catch (DbException e) {
			return new ArrayList<PlayRecordInfo>();
		}
	}

	@Override
	public PlayRecordInfo update(PlayRecordInfo t) {
		return null;
	}

	@Override
	public PlayRecordInfo delete(PlayRecordInfo t) {
		try {
			db.getDb().delete(t);
			changeNotify(this);
			return t;
		} catch (DbException e) {
			Logger.log(e);
			return null;
		}
	}

	public void deleleAll() {
		try {
			db.getDb().delete(findAll());
		} catch (Exception e) {
			Logger.log(e);
		}
	}

	@Override
	public void save(List<PlayRecordInfo> lists) {
		if (lists != null && lists.size() > 0) {
			try {
				db.getDb().saveOrUpdate(lists);
				changeNotify(this);
			} catch (DbException e) {
				Logger.log(e);
			}
		}

	}

	@Override
	public void delete(List<PlayRecordInfo> list) {
		try {
			db.getDb().delete(PlayRecordInfo.class);
			changeNotify(this);
		} catch (DbException e) {
			Logger.log(e);
		}
	}

	/**
	 * 保存一个播放记录。这个播放记录会根据BaseVedioInfo 中的数据去填充，但是对数据有所丢失
	 * 
	 * @param info
	 */
	public void save(DisplayVideoInfo info) {
		this.save(new PlayRecordInfo().getPlayRecordInfo(info));
	}

	public long count() {
		try {
			return db.getDb().selector(PlayRecordInfo.class).count();
		} catch (DbException e) {
			Logger.log(e);
			return 0;
		}
	}
}
