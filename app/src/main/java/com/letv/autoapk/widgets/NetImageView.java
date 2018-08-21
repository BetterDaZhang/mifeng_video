package com.letv.autoapk.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.letv.autoapk.R;
import com.letv.autoapk.common.net.GifLoader;
import com.letv.autoapk.common.net.LruGifCache;
import com.letv.autoapk.common.net.LruImageCache;

public class NetImageView extends FrameLayout {

	private Context mContext;
	private AttributeSet mAttr;
	protected MyImageView mNetworkImageView;
	private CornerView mCornerView;
	private LayoutParams topParams;
	private LayoutParams bottomParams;
	private TextView mTopView;
	private TextView mBottomView;

	public NetImageView(Context context) {
		super(context);
		mContext = context;
		initView();
	}

	public NetImageView(Context context, AttributeSet attr) {
		super(context, attr);
		mContext = context;
		mAttr = attr;
		initView();
	}

	private void initView() {
		mNetworkImageView = new MyImageView(mContext, mAttr);
		topParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mNetworkImageView.setScaleType(ScaleType.CENTER_CROP);
		mNetworkImageView.setLayoutParams(topParams);
		addView(mNetworkImageView);

		mCornerView = new CornerView(mContext);
		mCornerView.setTextColor(Color.WHITE);
		mCornerView.setGravity(Gravity.CENTER);
		mCornerView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				getResources().getDimension(R.dimen.recommend_top_cornorview));

		mTopView = new TextView(mContext);
		mTopView.setTextColor(Color.WHITE);
		mTopView.setGravity(Gravity.CENTER);
		mTopView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.recommend_top_textview));

		topParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		topParams.gravity = Gravity.TOP | Gravity.LEFT;
		topParams.setMargins(0, 0, 0, 0);
		addView(mCornerView, topParams);
		mCornerView.setVisibility(View.GONE);

		topParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		topParams.gravity = Gravity.TOP | Gravity.LEFT;
		topParams.setMargins(dip2px(mContext, 5), dip2px(mContext, 5), 0, 0);
		addView(mTopView, topParams);
		mTopView.setVisibility(View.GONE);

		mBottomView = new TextView(mContext);
		mBottomView.setTextColor(Color.WHITE);
		mBottomView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				getResources().getDimension(R.dimen.recommend_bottom_textview));
		mBottomView.setGravity(Gravity.CENTER);

		mBottomView.setBackgroundResource(R.drawable.recommend_corner_bottom);
		bottomParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		bottomParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		bottomParams.setMargins(0, 0, 0, 0);

		addView(mBottomView, bottomParams);
		mBottomView.setPadding(dip2px(mContext, 5), 0, dip2px(mContext, 5), 0);
		mBottomView.setVisibility(View.GONE);
	}

	/**
	 * 根据服务器传递的类型显示不同的角标
	 * 
	 * @param unitSuperscriptType
	 */
	public void setSuperscriptType(int unitSuperscriptType) {
		switch (unitSuperscriptType) {
		case 21:// 样式一 斜角 橙色
			if (mTopView != null) {
				mTopView.setVisibility(View.GONE);
			}
			if (mCornerView != null) {
				mCornerView.setVisibility(View.VISIBLE);
				mCornerView.setBackgroundResource(R.drawable.recommend_cornor_red);
			}
		case 22:// 样式一 斜角 粉色
			if (mTopView != null) {
				mTopView.setVisibility(View.GONE);
			}
			if (mCornerView != null) {
				mCornerView.setVisibility(View.VISIBLE);
				mCornerView.setBackgroundResource(R.drawable.recommend_cornor_pink);
			}
			break;
		case 23:// 样式一 斜角 蓝色
			if (mTopView != null) {
				mTopView.setVisibility(View.GONE);
			}
			if (mCornerView != null) {
				mCornerView.setVisibility(View.VISIBLE);
				mCornerView.setBackgroundResource(R.drawable.recommend_cornor_blue);
			}
			break;
		case 24:// 样式一 斜角 绿色
			if (mTopView != null) {
				mTopView.setVisibility(View.GONE);
			}
			if (mCornerView != null) {
				mCornerView.setVisibility(View.VISIBLE);
				mCornerView.setBackgroundResource(R.drawable.recommend_cornor_green);
			}
			break;
		case 11:// 样式二 椭圆橙色
			if (mCornerView != null) {
				mCornerView.setVisibility(View.GONE);
			}
			if (mTopView != null) {
				mTopView.setVisibility(View.VISIBLE);
				mTopView.setBackgroundResource(R.drawable.recommend_style_red);
			}
			break;
		case 12:// 样式二 椭圆粉色
			if (mCornerView != null) {
				mCornerView.setVisibility(View.GONE);
			}
			if (mTopView != null) {
				mTopView.setVisibility(View.VISIBLE);
				mTopView.setBackgroundResource(R.drawable.recommend_style_pink);
			}
			break;
		case 13:// 样式二 椭圆蓝色
			if (mCornerView != null) {
				mCornerView.setVisibility(View.GONE);
			}
			if (mTopView != null) {
				mTopView.setVisibility(View.VISIBLE);
				mTopView.setBackgroundResource(R.drawable.recommend_style_blue);
			}
			break;
		case 14:// 样式二 椭圆绿色
			if (mCornerView != null) {
				mCornerView.setVisibility(View.GONE);
			}
			if (mTopView != null) {
				mTopView.setVisibility(View.VISIBLE);
				mTopView.setBackgroundResource(R.drawable.recommend_style_green);
			}
			break;
		default:
			if (mCornerView != null && mTopView != null) {
				mCornerView.setVisibility(View.GONE);
				mTopView.setVisibility(View.GONE);
			}
			break;
		}
	}

	/**
	 * 根据服务器传递的类型显示不同的颜色
	 * 
	 * @param unitSuperscriptColor
	 */
	public void setSuperscriptColor(int unitSuperscriptColor) {
		if (mTopView != null) {
			switch (unitSuperscriptColor) {
			case 1:// 红色
				mTopView.setBackgroundResource(R.drawable.recommend_style_red);
				break;
			case 2:// 粉色
				mTopView.setBackgroundResource(R.drawable.recommend_style_pink);
				break;
			default: // 默认显示红色
				mTopView.setVisibility(View.GONE);
				;
				break;
			}
		}
	}

	/**
	 * 根据服务器传递的类型显示不同的下角标
	 * 
	 * @param unitSuperscriptType
	 */
	public void setSubscriptType(int unitSubscriptType) {
		if (mBottomView != null) {
			switch (unitSubscriptType) {
			case 1:
				mBottomView.setVisibility(View.VISIBLE);
				mBottomView.setTextColor(getResources().getColor(android.R.color.white));
				break;
			case 3:
				mBottomView.setVisibility(View.VISIBLE);
				mBottomView.setTextColor(getResources().getColor(R.color.code1));
				break;
			default:
				mBottomView.setVisibility(View.GONE);
				break;
			}

		}

	}

	public void setSuperscriptName(String text) {
		if (mTopView != null) {
			mTopView.setText(text);
		}

		if (mCornerView != null) {
			mCornerView.setText(text);
		}
	}

	public void setSubscriptName(String text) {
		if (mBottomView != null && mBottomView.getVisibility() == View.VISIBLE) {
			mBottomView.setText(text);
		}
	}

	public void setDefaultImageResId(int resId) {
		if (mNetworkImageView != null) {
			mNetworkImageView.setDefaultImageResId(resId);
		}
	}

	public void setErrorImageResId(int resId) {
		if (mNetworkImageView != null) {
			mNetworkImageView.setErrorImageResId(resId);
		}
	}

	public void setImageUrl(String imageUrl, ImageLoader imageLoader) {
		if (mNetworkImageView != null) {
			mNetworkImageView.setImageUrl(imageUrl, imageLoader);
		}
	}

	public void setGifUrl(String imageUrl, GifLoader gifLoader) {
		if (mNetworkImageView != null) {
			mNetworkImageView.setImageUrl(imageUrl, gifLoader);
		}
	}

	public void setCoverUrl(String url, Context context) {
		if (url != null && url.toLowerCase().endsWith(".gif")) {
			setGifUrl(url, LruGifCache.getGifLoader(context));
		} else {
			setImageUrl(url, LruImageCache.getImageLoader(context));
		}
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public class CornerView extends TextView {

		public CornerView(Context context) {
			super(context);
			setGravity(Gravity.LEFT);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// 倾斜度-45,上下左右居中
			canvas.rotate(-47, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
			int offset = dip2px(mContext, 8);
			canvas.translate(0, -offset);
			super.onDraw(canvas);
		}
	}

	public void setSuperscriptGone() {
		if (mTopView != null && mCornerView != null) {
			mTopView.setVisibility(View.GONE);
			mCornerView.setVisibility(View.GONE);
		}
	}

	public void setSubscriptGone() {
		if (mBottomView != null) {
			mBottomView.setVisibility(View.GONE);
		}
	}

}
