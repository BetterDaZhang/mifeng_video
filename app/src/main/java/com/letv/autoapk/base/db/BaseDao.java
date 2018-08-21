package com.letv.autoapk.base.db;

import java.util.List;

import android.content.Context;

import com.letv.autoapk.context.MyApplication;

/**
 * @param <T>
 */
public abstract class BaseDao<T extends Model> {
	protected DbWrapper db;
	private OnDataChangeListener<T> listener;

	public BaseDao(Context context) {
		db = MyApplication.getInstance().getDataBase();
	}

	/**
	 * 保存一条数据。
	 * 
	 * @param t
	 * @return 如果保存成功返回True,失败返回false
	 */
	public abstract boolean save(T t);

	/**
	 * 查询所有的数据记录
	 * 
	 * @return 返回所有的数据记录
	 */
	public abstract List<T> findAll();

	/**
	 * 更新一条数据。
	 * 
	 * @param t
	 *            需要更新的数据
	 * @return 返回更新成功后的完整数据
	 */
	public abstract T update(T t);

	/**
	 * 删除一条数据
	 * 
	 * @param t
	 *            需要删除的数据
	 * @return 返回已经删除的完整数据
	 */
	public abstract T delete(T t);

	public abstract void save(List<T> list);

	public abstract void delete(List<T> list);

	public void changeNotify(BaseDao<T> dao) {
		if (null != listener) {
			listener.dataChange(dao);
		}
	}

	/**
	 * 注册一个数据变化的监听器
	 * 
	 * @param litener
	 */
	public void setOnDataChangeListener(OnDataChangeListener<T> litener) {
		this.listener = litener;
	}

	public interface OnDataChangeListener<T extends Model> {
		public void dataChange(BaseDao<T> dao);
	}
}
