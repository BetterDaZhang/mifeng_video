package com.letv.autoapk.base.fragment;

import org.xutils.x;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.task.AbsTask;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.autoapk.R;
import com.letv.autoapk.base.fragment.BaseTitleFragment.LoadingTask;
import com.letv.autoapk.common.utils.Logger;
import com.letv.autoapk.widgets.LoadingLayout;

public abstract class BaseCacheTitleFragment extends BaseTitleFragment {
	private LoadingCacheTask loadingCahceTask;

	protected void startLoading() {
		if (loadingCahceTask != null) {
			loadingCahceTask.cancel();
			loadingCahceTask = null;
		}
		loadingCahceTask = new LoadingCacheTask();
		x.task().start(loadingCahceTask);
	}

	protected class LoadingCacheTask extends AbsTask<Boolean> {

		@Override
		protected Boolean doBackground() {
			return loadingCacheData(true);
		}

		@Override
		protected void onSuccess(Boolean result) {
			boolean isCache = getIsCache();
			if (!isCache) {
				loadingLayout.onSuccess(result);
			} else if (result) {
				updateCacheView();
			}
		}

		@Override
		protected void onError(Throwable ex, boolean isCallbackError) {
			boolean isCache = getIsCache();
			if (!isCache) {
				loadingLayout.onError();
			}

		}

		@Override
		protected void onCancelled(CancelledException cex) {

		}

		@Override
		protected void onFinished() {

		}

	}

	protected abstract void updateCacheView();

	protected abstract boolean loadingCacheData(boolean isCache);

	protected abstract boolean getIsCache();

}
