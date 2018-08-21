package com.letv.autoapk.ui.search;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.base.activity.DetailActivity;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.dao.SearchHistoryDao;
import com.letv.autoapk.dao.SearchHistoryInfo;

public class SearchAPI {
	private SearchHistoryDao searchHistoryDao;
	public static void startSearch(Context context){
		//搜索页面暂时去掉，代码勿删除 
		Intent intent;
		if (context instanceof DetailActivity) {
			intent = new Intent(context, ContainerActivity.class);
			intent.putExtra(ContainerActivity.FRAGMENTNAME, SearchFragment.class.getName());
		}else {
			intent = new Intent(context, DetailActivity.class);
			intent.putExtra(DetailActivity.FRAGMENTNAME, SearchFragment.class.getName());
		}
		context.startActivity(intent);
//		Toast.makeText(context, "敬请期待", Toast.LENGTH_SHORT).show();
	}
	public void startSearchResultActivity(String searchTvString,Context context) {
		searchHistoryDao = MyApplication.getInstance().getDaoByKey(SearchHistoryDao.class.getName());
		
		SearchHistoryInfo info = new SearchHistoryInfo();
		info.setSarchTitle(searchTvString);
		// 保存搜索记录
		saveSearchHistoryInfo(info);
		// 跳转搜索结果页
		Intent intent = new Intent(context.getApplicationContext(), DetailActivity.class);
		intent.putExtra(ContainerActivity.FRAGMENTNAME, SearchResultFragment.class.getName());
		intent.putExtra("keyword", searchTvString);
		context.startActivity(intent);
	}
	
	public void saveSearchHistoryInfo(SearchHistoryInfo info) {
		SearchHistoryInfo delete = searchHistoryDao.delete(info);// 为了保证关键词顺序，先删除，后添加
		searchHistoryDao.save(info);
	}

}
