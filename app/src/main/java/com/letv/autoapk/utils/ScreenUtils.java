package com.letv.autoapk.utils;

import android.app.Activity;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

import com.letv.autoapk.base.activity.BaseActivity;

public class ScreenUtils {
    public static int getWight(Context mContext) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }

    public static int getHeight(Context mContext) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenHeight = dm.heightPixels;
        return screenHeight;
    }

    public static boolean isInRight(Context mContext, int xWeight) {
        return (xWeight > getWight(mContext) * 1 / 2);
    }

    public static boolean isInLeft(Context mContext, int xWeight) {
        return (xWeight < getWight(mContext) * 1 / 2);
    }
    
    
    /**
     * 是否横屏
     * @param mContext
     * @return
     */
    public static boolean screenIsLanscape(Context mContext){
        boolean ret = false;
        switch (mContext.getResources().getConfiguration().orientation) {
        case Configuration.ORIENTATION_PORTRAIT:
            ret=false;
            break;
        case Configuration.ORIENTATION_LANDSCAPE:
            ret = true;
            break;
        default:
            break;
        }
        return ret;
    }
    
    /**
     * 获取当前屏幕状态
     * @param mContext
     * @return
     */
    public static int getOrientation(Context mContext){
        return mContext.getResources().getConfiguration().orientation;
    }
    /**
     * 获取16：10图片的宽度。
     * @param mContext 两列
     * @return
     */
    public static int getTwoColsWidht16_10(Context mContext,int paddingValue){
    	return (getWight(mContext)-paddingValue)/2;
    } 
    
    /**
     * 获取16：10图片的宽度。
     * @param mContext 三列
     * @return
     */
    public static int getThreeColsWidht10_16(Context mContext,int paddingValue){
    	return (getWight(mContext)-paddingValue)/3;
    } 
    
    /**
     * 获取16：10图片的宽度。
     * @param mContext 一列
     * @return
     */
    public static int getOneColsWidht16_10(Context mContext,int paddingValue){
    	return (getWight(mContext)-paddingValue);
    } 
    
    /**
     * 获取16：10图片的高度。两列
     * @param mContext
     * @return
     */
    public static int getTwoColsHight16_10(Context mContext,int paddingValue){
    	return (int) (getTwoColsWidht16_10(mContext,paddingValue)/1.6);
    }
    
    /**
     * 获取16：10图片的高度,
     * @param mContext 三列
     * @return
     */
    public static int getThreeColsHight10_16(Context mContext,int paddingValue){
    	return (int) (getThreeColsWidht10_16(mContext,paddingValue) * 1.6);
    }
    
    /**
     * 获取16：10图片的高度。
     * @param mContext 
     * @return
     */
    public static int getOneColsHeight16_10(Context mContext,int paddingValue){
    	return (int)(getOneColsWidht16_10(mContext,paddingValue)/1.6);
    } 
    
    /**
     * 获取22：10图片的gao度。
     * @param mContext 
     * @return
     */
    public static int getOneColsHeight22_10(Context mContext,int paddingValue){
    	return (int)((getWight(mContext)-paddingValue)/2.2);
    } 
    
    /**
     * 根据屏幕宽度获取16-10的屏幕高度
     */
    public static int getImageWidth16_10(int heightPx){
    	return  (int) (heightPx * 1.6); 
    }
    
    public static int getImageHeight16_10(int widthPx){
    	return (int)(widthPx/1.6);
    }
    
    public static int getImageHeight16_9(int widthPx){
    	return (int)((widthPx *9)/16);
    }
    
    public static int getImageHeight7_2(int widthPx){
    	return (int)((widthPx *2)/7);
    }
    
//    public int dip2px(float dipValue) {
//		return dip2px(this, dipValue);
//	}
//
//	public int px2dip(float pxValue) {
//		return px2dip(this, pxValue);
//	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

}
