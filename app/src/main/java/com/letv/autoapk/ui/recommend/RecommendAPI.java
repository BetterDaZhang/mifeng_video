package com.letv.autoapk.ui.recommend;

import android.content.Context;
import android.content.Intent;

import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.base.activity.DetailActivity;

public class RecommendAPI {

	public static void startSubject(Context context, String id) {
		if (id == null || id.isEmpty() || "null".equals(id)) {
			return;
		}
		Intent intent;
		if (context instanceof DetailActivity) {
			intent = new Intent(context, ContainerActivity.class);
			intent.putExtra(ContainerActivity.FRAGMENTNAME, RecommendVideoBlockFragment.class.getName());
		} else {
			intent = new Intent(context, DetailActivity.class);
			intent.putExtra(DetailActivity.FRAGMENTNAME, RecommendVideoBlockFragment.class.getName());
		}
		intent.putExtra("pageId", id);
		context.startActivity(intent);

	}

	public static String getRecommendFragmentName() {
		return RecommendFragment.class.getName();
	}
}
