package com.letv.recorder.ui.filter;


import android.content.Context;

import com.letv.recorder.util.ReUtils;

public class FilterTypeHelper {
	
	public static int FilterType2Color(Context context,int filterType){
		switch (filterType) {
			case MagicFilterType.NONE:
				return ReUtils.getColorId(context, "filter_color_grey_light");
			case MagicFilterType.BEAUTYSKIN:
			case MagicFilterType.ROMANCE:
				return ReUtils.getColorId(context, "filter_color_brown_light");
			case MagicFilterType.WARM:
				return ReUtils.getColorId(context, "filter_color_blue_dark");
			case MagicFilterType.CALM:
				return ReUtils.getColorId(context, "filter_color_blue");
			default:
				return ReUtils.getColorId(context, "filter_color_grey_light");
		}
	}
	
	public static int FilterType2Thumb(Context context,int filterType){
		switch (filterType) {
		case MagicFilterType.NONE:
			return ReUtils.getDrawableId(context, "letv_recorder_filter_thumb_original");
		case MagicFilterType.BEAUTYSKIN:
			return ReUtils.getDrawableId(context, "letv_recorder_filter_thumb_beautyskin");
		case MagicFilterType.ROMANCE:
			return ReUtils.getDrawableId(context, "letv_filter_filter_thumb_romance");
		case MagicFilterType.WARM:
			return ReUtils.getDrawableId(context, "letv_filter_filter_thumb_warm");
		case MagicFilterType.CALM:
			return ReUtils.getDrawableId(context, "letv_filter_filter_thumb_calm");
		default:
			return ReUtils.getDrawableId(context, "letv_recorder_filter_thumb_original");
		}
	}
	
	public static int FilterType2Name(Context context,int filterType){
		switch (filterType) {
		case MagicFilterType.NONE:
			return ReUtils.getStringId(context, "filter_none");
		case MagicFilterType.BEAUTYSKIN:
			return ReUtils.getStringId(context, "filter_beauty_skin");
		case MagicFilterType.ROMANCE:
			return ReUtils.getStringId(context, "filter_romance");
		case MagicFilterType.WARM:
			return ReUtils.getStringId(context, "filter_warm");
		case MagicFilterType.CALM:
			return ReUtils.getStringId(context, "filter_calm");
		default:
			return ReUtils.getStringId(context, "filter_none");
		}
	}
	
	public static int FilterGetShadow(Context context){
		return ReUtils.getDrawableId(context, "img_shadow");
	}
}
