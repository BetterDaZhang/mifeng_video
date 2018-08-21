package com.letv.autoapk.ui.mobilelive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xutils.common.util.DensityUtil;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.letv.autoapk.R;
import com.letv.autoapk.base.activity.ContainerActivity;
import com.letv.autoapk.base.fragment.BaseCacheTitleFragment;
import com.letv.autoapk.base.fragment.BaseTitleFragment;
import com.letv.autoapk.base.net.CacheListener;
import com.letv.autoapk.base.net.LoginInfoUtil;
import com.letv.autoapk.base.net.PageInfo;
import com.letv.autoapk.base.net.StringDataRequest;
import com.letv.autoapk.base.task.UiAsyncTask;
import com.letv.autoapk.context.MyApplication;
import com.letv.autoapk.ui.login.LoginAPI;
import com.letv.autoapk.ui.mobilelive.identity.LiveIdentityFragment;
import com.letv.autoapk.ui.mobilelive.recorder.RecorderActivity;
import com.letv.autoapk.widgets.LoadingLayout;
import com.letv.autoapk.widgets.SearchClearDialog;

public class MobileLiveListFragment extends BaseCacheTitleFragment implements OnClickListener {

	private String livePageId;
	private List<MobileLiveInfo> liveVideoInfos;
	private List<MobileLiveInfo> tempLiveVideoInfos;
	private FrameLayout liveVideoLandsLay;
	private PullToRefreshListView liveVideoGridview;
	private MobileLiveAdapter liveVideoListAdapter;
	private PageInfo mPageInfo;
	private int PULLFROMSTART = 1;
	private int PULLFROMEND = 2;
	private Button pushButton;
	private boolean isCache = false;
	AnchorInfo info = new AnchorInfo();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		liveVideoInfos = new ArrayList<MobileLiveInfo>();
		tempLiveVideoInfos = new ArrayList<MobileLiveInfo>();
	}

	@Override
	protected boolean hasTitleBar() {
		Bundle extras = getActivity().getIntent().getExtras();
		if (extras == null) {
			return false;
		}
		return extras.getBoolean("hasTitleBar");
	};

	@Override
	protected void initCustomerView() {
		setTitle(getString(R.string.letv_recorder_mobilelive), getResources().getColor(R.color.code6));
		setTitleLeftResource(R.drawable.base_head_back, mActivity.dip2px(15));
		setLeftClickListener(new TitleLeftClickListener() {

			@Override
			public void onLeftClickListener() {
				getActivity().finish();
			}
		});
	}

	@Override
	protected void onHandleMessage(Message msg) {

	}

	@Override
	protected boolean hasContentData() {
		if (liveVideoInfos.size() > 0) {
			return true;
		}
		return false;
		// return true;
	}

	@Override
	protected boolean loadingCacheData(boolean isCache) {
		liveVideoInfos = new ArrayList<MobileLiveInfo>();
		tempLiveVideoInfos = new ArrayList<MobileLiveInfo>();
		int code = loadDataRequest(false);
		if (code == 0) {
			return true;
		}
		return false;
	}

	@Override
	public void updateCacheView() {
		if (isCache) {
			liveVideoListAdapter.notifyDataSetChanged();
		}
	}

	class CustomCacheListener extends CacheListener {

		public CustomCacheListener(Handler h) {
			super(h);
		}

		@Override
		public void onRefreshCache(Object[] mOutputData) {
			if (tempLiveVideoInfos.size() > 0) {
				isCache = true;
				liveVideoInfos.clear();
				liveVideoInfos.addAll(tempLiveVideoInfos);
				loadingLayout.onSuccess(true);
			}
		}

	}

	@Override
	protected boolean getIsCache() {
		return isCache;
	}

	@Override
	protected View createContentView() {
		loadingLayout = new LoadingLayout(mActivity) {

			@Override
			public void loadData(View loadingView) {
				startLoading();
			}

			@Override
			public View createLoadedView() {
				return setupDataView();
			}

			@Override
			public boolean hasContent() {
				return hasContentData();
			}

			@Override
			protected View createNoContentView() {
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				View empty = inflater.inflate(R.layout.live_mobile_list_no_content, null);
				liveVideoLandsLay = (FrameLayout) inflater.inflate(R.layout.live_mobile_list, null);
				liveVideoGridview = (PullToRefreshListView) liveVideoLandsLay.findViewById(R.id.mobile_list);
				liveVideoListAdapter = new MobileLiveAdapter(mActivity, liveVideoInfos);
				liveVideoGridview.setAdapter(liveVideoListAdapter);
				liveVideoGridview.setEmptyView(empty);
				liveVideoGridview.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
						// 下拉刷新
						new ChannelAsyncTask(MobileLiveListFragment.this, PULLFROMSTART).execute();
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
						// TODO 上拉加载
						new ChannelAsyncTask(MobileLiveListFragment.this, PULLFROMEND).execute();
					}
				});
				liveVideoGridview.setMode(Mode.PULL_FROM_START);
				Button pushButton = (Button) liveVideoLandsLay.findViewById(R.id.pushlive);
				pushButton.setOnClickListener(MobileLiveListFragment.this);
				return liveVideoLandsLay;
			}

		};
		loadingLayout.show();
		return loadingLayout;
	}

	private int loadDataRequest(boolean isCache) {
		mPageInfo = new PageInfo();
		tempLiveVideoInfos.clear();
		GetMobileLiveListDataRequest request = new GetMobileLiveListDataRequest(mActivity);
		request.setCacheListener(new CustomCacheListener(getDefaultHandler()));
		Map<String, String> mInputstreamParams = new HashMap<String, String>();
		// mInputstreamParams.put(StringDataRequest.PAGE_ID, livePageId);
		mInputstreamParams.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
		mInputstreamParams.put(StringDataRequest.PAGE, "1");
		mInputstreamParams.put(StringDataRequest.PAGE_SIZE, "10");
		int code = request.setInputParam(mInputstreamParams).setOutputData(tempLiveVideoInfos, mPageInfo)
				.request(Method.GET, isCache);
		if (code == 0) {
			liveVideoInfos.clear();
			liveVideoInfos.addAll(tempLiveVideoInfos);
		}
		return code;
	}

	private int loadMoreDataRequest() {
		tempLiveVideoInfos.clear();
		GetMobileLiveListDataRequest request = new GetMobileLiveListDataRequest(mActivity);
		Map<String, String> mInputstreamParams = new HashMap<String, String>();
		if (mPageInfo.getPageIndex() + 1 <= mPageInfo.getTotalPage()) {
			// mInputstreamParams.put(StringDataRequest.PAGE_ID, livePageId);
			mInputstreamParams.put(StringDataRequest.PAGE, String.valueOf(mPageInfo.getPageIndex() + 1));
			mInputstreamParams.put(StringDataRequest.PAGE_SIZE, "10");
			mInputstreamParams.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
			int code = request.setInputParam(mInputstreamParams).setOutputData(tempLiveVideoInfos, mPageInfo)
					.request(Method.GET);
			if (code == 0) {
				liveVideoInfos.addAll(tempLiveVideoInfos);
			}
			return code;
		} else {
			return -1;
		}

	}

	private float mLastY = 0, mLastDeltaY;
	private ObjectAnimator animator;

	@Override
	protected View setupDataView() {
		setTitleLeftResource(R.drawable.mine_login_back, mActivity.dip2px(16));
		setLeftClickListener(new TitleLeftClickListener() {

			@Override
			public void onLeftClickListener() {
				getActivity().finish();
			}
		});
		setTitle(getString(R.string.letv_recorder_mobilelive), getResources().getColor(R.color.code6));

		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View empty = inflater.inflate(R.layout.live_mobile_list_no_content, null);
		liveVideoLandsLay = (FrameLayout) inflater.inflate(R.layout.live_mobile_list, null);
		liveVideoGridview = (PullToRefreshListView) liveVideoLandsLay.findViewById(R.id.mobile_list);
		liveVideoListAdapter = new MobileLiveAdapter(mActivity, liveVideoInfos);
		pushButton = (Button) liveVideoLandsLay.findViewById(R.id.pushlive);
		pushButton.setOnClickListener(this);
		// 设置gridview 下拉刷新 上拉加载
		liveVideoGridview.setMode(Mode.BOTH);
		liveVideoGridview.setAdapter(liveVideoListAdapter);
		liveVideoGridview.setEmptyView(empty);
		liveVideoGridview.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// 下拉刷新
				new ChannelAsyncTask(MobileLiveListFragment.this, PULLFROMSTART).execute();

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO 上拉加载
				new ChannelAsyncTask(MobileLiveListFragment.this, PULLFROMEND).execute();
			}
		});
		final int margin = DensityUtil.dip2px(85);
		liveVideoGridview.getRefreshableView().setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (liveVideoListAdapter.getCount() < 2)
					return false;
				final float y = event.getY();
				float translationY = pushButton.getTranslationY();
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					mLastY = y;

					break;
				case MotionEvent.ACTION_MOVE:
					if (mLastY == 0) {
						mLastY = y;
					}
					float mDeltaY = y - mLastY;

					float newTansY = translationY - mDeltaY;
					if ((newTansY >= 0 && newTansY <= margin)
							&& ((mDeltaY > 0 && translationY > 0) || (mDeltaY <= 0 && translationY <= margin))) {

						pushButton.setTranslationY(newTansY);
					}
					mLastY = y;
					mLastDeltaY = mDeltaY;
					float scale = 1.0f;
					if (mLastDeltaY >= 0) {
						scale = pushButton.getScaleX() >= 1.0f ? 1.0f : pushButton.getScaleX() + 0.01f;

					} else {
						scale = pushButton.getScaleX() <= 0.8f ? 0.8f : pushButton.getScaleX() - 0.01f;
					}
					pushButton.setScaleX(scale);
					pushButton.setScaleY(scale);
					// Log.v(TAG, "Move");
					break;
				case MotionEvent.ACTION_UP:
					pushButton.setTranslationY(translationY);
					if (mLastDeltaY == 0) {
						mLastDeltaY = translationY <= (margin / 2) ? 1 : -1;
					}

					if (mLastDeltaY > 0) {
						pushButton.setScaleX(1.0f);
						pushButton.setScaleY(1.0f);

						if (translationY > 0) {
							startAnimator(translationY, 0);
						}

					}
					if (mLastDeltaY < 0) {
						pushButton.setScaleX(0.8f);
						pushButton.setScaleY(0.8f);
						if (translationY < margin) {
							startAnimator(translationY, margin);
						}
					}
					mLastDeltaY = 0;
					mLastY = 0;
					break;
				}
				return false;
			}

		});
		return liveVideoLandsLay;
	}

	private void startAnimator(float translationY, final float toY) {

		animator = ObjectAnimator.ofFloat(pushButton, "translationY", translationY, toY);
		animator.setDuration(100);
		animator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				pushButton.setTranslationY(toY);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				pushButton.setTranslationY(toY);
			}
		});
		animator.start();
	}

	class ChannelAsyncTask extends UiAsyncTask<Boolean> {
		private int defalutPull;

		public ChannelAsyncTask(Fragment fragment, int d) {
			super(fragment);
			this.defalutPull = d;
		}

		@Override
		protected Boolean doBackground() {
			int code = -1;
			if (defalutPull == PULLFROMSTART) {
				code = loadDataRequest(false);
			} else {
				code = loadMoreDataRequest();
			}

			if (code == 0) {
				return true;
			} else {
				return false;
			}

		}

		@Override
		protected void post(Boolean result) {
			super.post(result);
			if (liveVideoGridview == null) {
				return;
			}
			if (result && null != liveVideoListAdapter) {
				liveVideoGridview.setMode(Mode.BOTH);
				liveVideoListAdapter.setLiveVideoInfos(liveVideoInfos);
				liveVideoListAdapter.notifyDataSetChanged();
				liveVideoGridview.onRefreshComplete();
			} else {
				liveVideoGridview.onRefreshComplete();
				if (defalutPull == PULLFROMEND && mPageInfo.getPageIndex() + 1 > mPageInfo.getTotalPage()) {
					liveVideoGridview.setMode(Mode.PULL_FROM_START);
				}
			}
		}
	};

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.pushlive) {
			if (MyApplication.getInstance().isLogin() == false) {
				mActivity.showToastSafe(getResString(R.string.not_login), Toast.LENGTH_SHORT);
				LoginAPI.stratLogin(mActivity);
				return;
			}
			new UiAsyncTask<AnchorInfo>(this) {

				@Override
				protected AnchorInfo doBackground() throws Throwable {
					IsAnchorAuthenticationDataRequest request = new IsAnchorAuthenticationDataRequest(mActivity);
					Map<String, String> mInputParam = new HashMap<String, String>();
					mInputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
					mInputParam.put(StringDataRequest.TOKEN, LoginInfoUtil.getToken(mActivity));
					mInputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
					int code = request.setInputParam(mInputParam).setOutputData(info).request(Method.GET);
					if (code == 0) {
						return info;
					}
					return null;
				}

				@Override
				protected void post(AnchorInfo result) {
					if (result == null) {
						return;
					}
					if ("1".equals(result.isComplete)) {
						if ("1".equals(result.isPassAuthentication)) {
							checkAnchorIsOnline();
						}
						if ("0".equals(result.isPassAuthentication)) {
							showFailedIdentityDialog(result.cause);
						}
						if ("2".equals(result.isPassAuthentication)) {
							showWaitIdentityDialog(R.layout.letv_record_wait_identity, result.cause,
									getResString(R.string.ensure));
						}
					} else if ("2".equals(result.isComplete)) {// 冻结
						showWaitIdentityDialog(R.layout.letv_record_wait_identity, result.cause,
								getResString(R.string.cancel));
					} else if ("0".equals(result.isComplete)) {
						Intent intent = new Intent(mActivity, ContainerActivity.class);
						intent.putExtra(ContainerActivity.FRAGMENTNAME, LiveIdentityFragment.class.getName());
						mActivity.startActivity(intent);
					}
				}

			}.showDialog().execute();
		}
	}

	public void checkAnchorIsOnline() {

		new AsyncTask<Void, Void, Integer>() {
			MobileLivePathInfo mobileLivePathInfo = new MobileLivePathInfo();

			@Override
			protected Integer doInBackground(Void... params) {
				GetMobileLivePathDataRequest request = new GetMobileLivePathDataRequest(mActivity);
				Map<String, String> inputParam = new HashMap<String, String>();
				inputParam.put(StringDataRequest.USER_ID, LoginInfoUtil.getUserId(mActivity));
				inputParam.put(StringDataRequest.TENANT_ID, MyApplication.getInstance().getTenantId());
				int code = request.setInputParam(inputParam).setOutputData(mobileLivePathInfo)
						.request(Method.GET);
				return code;
			}

			@Override
			protected void onPostExecute(Integer result) {
				if (result == 0) {
					if ("2".equals(mobileLivePathInfo.status)) {// 正常,不在线
						Intent intent = new Intent(mActivity, RecorderActivity.class);
						intent.putExtra("recordUrl", mobileLivePathInfo.path);
						intent.putExtra("shareUrl", mobileLivePathInfo.shareUrl);
						mActivity.startActivity(intent);
					}
					if ("1".equals(mobileLivePathInfo.status)) {// 已在线
						showWaitIdentityDialog(R.layout.letv_record_is_online_yet, "", getResString(R.string.cancel));
						return;
					}
					if ("9".equals(mobileLivePathInfo.status)) {// 异常
						showWaitIdentityDialog(R.layout.letv_record_is_online_yet, mobileLivePathInfo.cause,
								getResString(R.string.cancel));
						return;
					}
				}
			}

		}.execute();
	}

	// private void checkMobileLiveInfo(final String url) {
	// new UiAsyncTask<Integer>(this) {
	// MobileLiveStreamInfo mobileLiveStreamInfo = new MobileLiveStreamInfo();
	//
	// @Override
	// protected Integer doBackground() throws Throwable {
	// GetMobileLiveStreamInfoDataRequest request = new
	// GetMobileLiveStreamInfoDataRequest(mActivity);
	// Map<String, String> mInputParam = new HashMap<String, String>();
	// mInputParam.put(StringDataRequest.USER_ID,
	// LoginInfoUtil.getUserId(mActivity));
	// mInputParam.put(StringDataRequest.TENANT_ID,
	// MyApplication.getInstance().getTenantId());
	// return
	// request.setInputParam(mInputParam).setOutputData(mobileLiveStreamInfo).request(Request.Method.GET);
	// }
	//
	// @Override
	// protected void post(Integer result) {
	// if (result == 0) {
	// if ("1".equals(mobileLiveStreamInfo.isOnlie)) {// 已在线
	// showWaitIdentityDialog(R.layout.letv_record_is_online_yet, "");
	// return;
	// }
	//
	// }
	// }
	// }.execute();
	// }

	/**
	 * 主播身份审核失败
	 */
	public void showFailedIdentityDialog(String cause) {
		SearchClearDialog alert;
		SearchClearDialog.Builder builder = new SearchClearDialog.Builder(mActivity);
		builder.setMessage(null).setPositiveButton(R.string.letv_record_recommit_identity,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(mActivity, ContainerActivity.class);
						intent.putExtra(ContainerActivity.FRAGMENTNAME, LiveIdentityFragment.class.getName());
						mActivity.startActivity(intent);
						dialog.dismiss();
					}
				});
		builder.setNegativeButton(R.string.letv_record_cancel_identity, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert = builder.create(R.layout.letv_record_failed_identity);
		TextView title = (TextView) alert.findViewById(R.id.clear_dialog_title);
		title.setText(cause);
		alert.show();
	}

	/**
	 * 
	 */
	public void showWaitIdentityDialog(int layoutId, String cause, String cancelStr) {
		final Dialog dialog = new Dialog(mActivity, R.style.Dialog);
		dialog.setContentView(layoutId);
		TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
		cancel.setText(cancelStr);
		TextView causeText = (TextView) dialog.findViewById(R.id.clear_dialog_title);
		if (!TextUtils.isEmpty(cause)) {
			causeText.setText(cause);
		}
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	@Override
	protected boolean loadingData() {
		return false;
	}
}
