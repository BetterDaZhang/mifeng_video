package com.letv.recorder.ui.filter;

public class MagicFilterType {	
  
	public static final int NONE = 0x00;  //原图
	public static final int BEAUTYSKIN = NONE + 1;//美肤
	public static final int WARM = BEAUTYSKIN + 1;//温暖
	public static final int CALM = WARM + 1;//平静
	public static final int ROMANCE = CALM + 1;//浪漫
	public static final int FILTER_COUNT = ROMANCE - NONE;
}
