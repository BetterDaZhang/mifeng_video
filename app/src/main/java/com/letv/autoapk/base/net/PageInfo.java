package com.letv.autoapk.base.net;

/**
 * 列表分页信息实体
 * 
 * @author zhangzhiyong
 * 
 */
public class PageInfo {

	/**
	 * 总数量
	 */
	private int mTotalCount = 20;
	
	/**
	 * 评论总数量
	 */
	private int mTotalCountShow ;
	
	/*
	 * 当前页数
	 */
	private int mPageIndex;
	/**
	 * 总页数
	 */
	private int mTotalPage;
	

	public int getTotalCount() {
		return mTotalCount;
	}

	public void setTotalCount(int TotalCount) {
		this.mTotalCount = TotalCount;
	}

	public int getPageIndex() {
		return mPageIndex;
	}

	public void setPageIndex(int mPageIndex) {
		this.mPageIndex = mPageIndex;
	}

	public int getTotalPage() {
		return mTotalPage;
	}

	public void setTotalPage(int mTotalPage) {
		this.mTotalPage = mTotalPage;
	}

	public int getmTotalCountShow() {
		return mTotalCountShow;
	}

	public void setmTotalCountShow(int mTotalCountShow) {
		this.mTotalCountShow = mTotalCountShow;
	}
	
}
