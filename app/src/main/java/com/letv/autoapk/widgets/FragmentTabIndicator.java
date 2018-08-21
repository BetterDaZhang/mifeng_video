package com.letv.autoapk.widgets;

import android.content.Context;

import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letv.autoapk.R;

/**
 * Tab页图标
 * 
 *
 */
public class FragmentTabIndicator extends LinearLayout implements OnClickListener {

	private int mDefaultTabIndex = 0;

	private static int mCurTabIndex;

	private static View[] mTabs;

	private Context mContext;
	private OnTabSelectedListener mOnTabSelectedListener;

	private static final String TAG_ICON_0 = "icon_tag_0";
	private static final String TAG_ICON_1 = "icon_tag_1";
	private static final String TAG_ICON_2 = "icon_tag_2";
	private static final String TAG_ICON_3 = "icon_tag_3";
	private static final String TAG_ICON_4 = "icon_tag_4";

	private static final String TAG_TEXT_0 = "text_tag_0";
	private static final String TAG_TEXT_1 = "text_tag_1";
	private static final String TAG_TEXT_2 = "text_tag_2";
	private static final String TAG_TEXT_3 = "text_tag_3";
	private static final String TAG_TEXT_4 = "text_tag_4";

	// private static final int COLOR_UNSELECT = R.color.tab_unselect;
	// private static final int COLOR_SELECT = R.color.tab_select;

	private FragmentTabIndicator(Context context) {
		super(context);
		mContext = context;
	}

	public FragmentTabIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mCurTabIndex = mDefaultTabIndex;
		setOrientation(LinearLayout.HORIZONTAL);
		init();
	}

	/**
	 * 创建Tab页图标
	 * 
	 * @param iconResID
	 *            图标资源id
	 * @param stringResID
	 *            图标名称
	 * @param stringColor
	 *            字体颜色
	 * @param iconTag
	 *            图标Tag
	 * @param textTag
	 *            文本Tag
	 * @return
	 */
	private View createIndicator(int iconResID, int stringResID, int stringColor, int linearResID, String iconTag, String textTag) {
		LinearLayout view = new LinearLayout(getContext());
		view.setOrientation(LinearLayout.VERTICAL);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
		view.setGravity(Gravity.CENTER_HORIZONTAL);
		view.setBackgroundColor(mContext.getResources().getColor(linearResID));
		view.setPadding(0, dip2px(mContext, 7), 0, 0);
		ImageView iconView = new ImageView(getContext());
		iconView.setTag(iconTag);
		int side = dip2px(mContext, 23);
		iconView.setLayoutParams(new LayoutParams(side, side, 1));
		iconView.setImageResource(iconResID);

		TextView textView = new TextView(getContext());
		textView.setTag(textTag);
		textView.setPadding(0, dip2px(mContext, 2), 0, 0);
		textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
		textView.setTextColor(mContext.getResources().getColor(stringColor));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
		textView.setText(stringResID);

		view.addView(iconView);
		view.addView(textView);

		return view;

	}

	/**
	 * Tab页图标初始化
	 */
//	private void init() {
//		mTabs = new View[5];
//		mTabs[0] = createIndicator(R.drawable.tab_recommend_selected, R.string.tab1, R.color.code1, R.color.code06, TAG_ICON_0,
//				TAG_TEXT_0);
//		mTabs[0].setTag(Integer.valueOf(0));
//		mTabs[0].setOnClickListener(this);
//		addView(mTabs[0]);
//		mTabs[1] = createIndicator(R.drawable.tab_channcel, R.string.tab2, R.color.code5,R.color.code06, TAG_ICON_1, TAG_TEXT_1);
//		mTabs[1].setTag(Integer.valueOf(1));
//		mTabs[1].setOnClickListener(this);
//		addView(mTabs[1]);
//		mTabs[2] = createIndicator(R.drawable.tab_finder, R.string.tab3, R.color.code5,R.color.code06, TAG_ICON_2, TAG_TEXT_2);
//		mTabs[2].setTag(Integer.valueOf(2));
//		mTabs[2].setOnClickListener(this);
//		addView(mTabs[2]);
//
//		mTabs[3] = createIndicator(R.drawable.tab_live, R.string.tab4, R.color.code5,R.color.code06, TAG_ICON_3, TAG_TEXT_3);
//		mTabs[3].setTag(Integer.valueOf(3));
//		mTabs[3].setOnClickListener(this);
//		addView(mTabs[3]);
//		mTabs[4] = createIndicator(R.drawable.tab_mine, R.string.tab5, R.color.code5,R.color.code06, TAG_ICON_4, TAG_TEXT_4);
//		mTabs[4].setTag(Integer.valueOf(4));
//		mTabs[4].setOnClickListener(this);
//		addView(mTabs[4]);
//	}

	private void init() {
		mTabs = new View[3];
		mTabs[0] = createIndicator(R.drawable.tab_recommend_selected, R.string.tab1, R.color.code1, R.color.code06, TAG_ICON_0,
				TAG_TEXT_0);
		mTabs[0].setTag(Integer.valueOf(0));
		mTabs[0].setOnClickListener(this);
		addView(mTabs[0]);
		mTabs[1] = createIndicator(R.drawable.tab_channcel, R.string.tab2, R.color.code5,R.color.code06, TAG_ICON_1, TAG_TEXT_1);
		mTabs[1].setTag(Integer.valueOf(1));
		mTabs[1].setOnClickListener(this);
		addView(mTabs[1]);
		mTabs[2] = createIndicator(R.drawable.tab_mine, R.string.tab5, R.color.code5,R.color.code06, TAG_ICON_4, TAG_TEXT_4);
		mTabs[2].setTag(Integer.valueOf(2));
		mTabs[2].setOnClickListener(this);
		addView(mTabs[2]);
	}

//	public void hideTabView(int which) {
//		mTabs[0].setVisibility(View.VISIBLE);
//		mTabs[1].setVisibility(View.VISIBLE);
//		mTabs[2].setVisibility(View.VISIBLE);
//		mTabs[4].setVisibility(View.VISIBLE);
//		if (mTabs[which] != null) {
//			mTabs[which].setVisibility(View.INVISIBLE);
//		}
//	}

	public void hideTabView(int which) {
		mTabs[0].setVisibility(View.VISIBLE);
		mTabs[1].setVisibility(View.VISIBLE);
		mTabs[2].setVisibility(View.VISIBLE);
		if (mTabs[which] != null) {
			mTabs[which].setVisibility(View.INVISIBLE);
		}
	}

	public void showTabView(int which) {
		if (mTabs[which] != null) {
			mTabs[which].setVisibility(View.VISIBLE);
		}
	}

	public void setIndicator(int which) {
		// clear previous status.
		// mTabs[mCurTabIndex].setBackgroundColor(Color.alpha(0));
		ImageView prevIcon;
		TextView prevText;
		switch (mCurTabIndex) {
		case 0:
			prevIcon = (ImageView) mTabs[mCurTabIndex].findViewWithTag(TAG_ICON_0);
			prevIcon.setImageResource(R.drawable.tab_recommend);
			prevText = (TextView) mTabs[mCurTabIndex].findViewWithTag(TAG_TEXT_0);
			prevText.setTextColor(mContext.getResources().getColor(R.color.code5));
			mTabs[mCurTabIndex].setBackgroundColor(mContext.getResources().getColor(R.color.code06));
			break;
		case 1:
			prevIcon = (ImageView) mTabs[mCurTabIndex].findViewWithTag(TAG_ICON_1);
			prevIcon.setImageResource(R.drawable.tab_channcel);
			prevText = (TextView) mTabs[mCurTabIndex].findViewWithTag(TAG_TEXT_1);
			prevText.setTextColor(mContext.getResources().getColor(R.color.code5));
			mTabs[mCurTabIndex].setBackgroundColor(mContext.getResources().getColor(R.color.code06));
			break;
		case 2:
			prevIcon = (ImageView) mTabs[mCurTabIndex].findViewWithTag(TAG_ICON_4);
			prevIcon.setImageResource(R.drawable.tab_mine);
			prevText = (TextView) mTabs[mCurTabIndex].findViewWithTag(TAG_TEXT_4);
			prevText.setTextColor(mContext.getResources().getColor(R.color.code5));
			mTabs[mCurTabIndex].setBackgroundColor(mContext.getResources().getColor(R.color.code06));
			break;
		case 3:
			prevIcon = (ImageView) mTabs[mCurTabIndex].findViewWithTag(TAG_ICON_3);
			prevIcon.setImageResource(R.drawable.tab_live);
			prevText = (TextView) mTabs[mCurTabIndex].findViewWithTag(TAG_TEXT_3);
			prevText.setTextColor(mContext.getResources().getColor(R.color.code5));
			mTabs[mCurTabIndex].setBackgroundColor(mContext.getResources().getColor(R.color.code06));
			break;
		case 4:
			prevIcon = (ImageView) mTabs[mCurTabIndex].findViewWithTag(TAG_ICON_4);
			prevIcon.setImageResource(R.drawable.tab_mine);
			prevText = (TextView) mTabs[mCurTabIndex].findViewWithTag(TAG_TEXT_4);
			prevText.setTextColor(mContext.getResources().getColor(R.color.code5));
			mTabs[mCurTabIndex].setBackgroundColor(mContext.getResources().getColor(R.color.code06));
			break;
		}

		// update current status.
		// mTabs[which].setBackgroundResource(R.drawable.blue);
		ImageView currIcon;
		TextView currText;
		switch (which) {
		case 0:
			currIcon = (ImageView) mTabs[which].findViewWithTag(TAG_ICON_0);
			currIcon.setImageResource(R.drawable.tab_recommend_selected);
			currText = (TextView) mTabs[which].findViewWithTag(TAG_TEXT_0);
			currText.setTextColor(mContext.getResources().getColor(R.color.code1));
			mTabs[which].setBackgroundColor(mContext.getResources().getColor(R.color.code06));
			break;
		case 1:
			currIcon = (ImageView) mTabs[which].findViewWithTag(TAG_ICON_1);
			currIcon.setImageResource(R.drawable.tab_channcel_selected);
			currText = (TextView) mTabs[which].findViewWithTag(TAG_TEXT_1);
			currText.setTextColor(mContext.getResources().getColor(R.color.code1));
			mTabs[which].setBackgroundColor(mContext.getResources().getColor(R.color.code06));
			break;
		case 2:
			currIcon = (ImageView) mTabs[which].findViewWithTag(TAG_ICON_4);
			currIcon.setImageResource(R.drawable.tab_mine_selected);
			currText = (TextView) mTabs[which].findViewWithTag(TAG_TEXT_4);
			currText.setTextColor(mContext.getResources().getColor(R.color.code1));
			mTabs[which].setBackgroundColor(mContext.getResources().getColor(R.color.code06));
			break;
		case 3:
			currIcon = (ImageView) mTabs[which].findViewWithTag(TAG_ICON_3);
			currIcon.setImageResource(R.drawable.tab_live_selected);
			currText = (TextView) mTabs[which].findViewWithTag(TAG_TEXT_3);
			currText.setTextColor(mContext.getResources().getColor(R.color.code1));
			mTabs[which].setBackgroundColor(mContext.getResources().getColor(R.color.code06));
			break;
		case 4:
			currIcon = (ImageView) mTabs[which].findViewWithTag(TAG_ICON_4);
			currIcon.setImageResource(R.drawable.tab_mine_selected);
			currText = (TextView) mTabs[which].findViewWithTag(TAG_TEXT_4);
			currText.setTextColor(mContext.getResources().getColor(R.color.code1));
			mTabs[which].setBackgroundColor(mContext.getResources().getColor(R.color.code06));
			break;
		}

		mCurTabIndex = which;
	}

	public interface OnTabSelectedListener {
		public void onTabSelceted(View v, int which);
	}

	public void setOnIndicateListener(OnTabSelectedListener listener) {
		mOnTabSelectedListener = listener;
	}

	@Override
	public void onClick(View v) {
		if (mOnTabSelectedListener != null) {
			int tag = (Integer) v.getTag();
			switch (tag) {
			case 0:
				if (mCurTabIndex != 0) {
					mOnTabSelectedListener.onTabSelceted(v, 0);
					setIndicator(0);
				}
				break;
			case 1:
				if (mCurTabIndex != 1) {
					mOnTabSelectedListener.onTabSelceted(v, 1);
					setIndicator(1);
				}
				break;
			case 2:
				if (mCurTabIndex != 2) {
					mOnTabSelectedListener.onTabSelceted(v, 2);
					setIndicator(2);
				}
				break;
			case 3:
				if (mCurTabIndex != 3) {
					mOnTabSelectedListener.onTabSelceted(v, 3);
					setIndicator(3);
				}
				break;
			case 4:
				if (mCurTabIndex != 4) {
					mOnTabSelectedListener.onTabSelceted(v, 4);
					setIndicator(4);
				}
				break;
			default:
				break;
			}

		}
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
}