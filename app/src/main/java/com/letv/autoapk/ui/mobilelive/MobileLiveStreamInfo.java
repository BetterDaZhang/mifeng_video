package com.letv.autoapk.ui.mobilelive;

/**
 * 移动直播流状态
 * 
 * @author wangzhen5
 * 
 */
public class MobileLiveStreamInfo {
    // 直播地址
    public String liveUrl;
    // 1:正常 0:异常
    public String status;
    // 直播名称
    public String userName;
    // 异常原因
    public String cause;
    // 1:在线 0:不在线
    public String isOnlie;
    // 直播标题
    public String liveTitle;
    // 封面图
    public String coverPic;
    // 移动直播下行页类型 
    public String detailType;
    // 直播头像
    public String headPic;
    //分享地址
    public String shareUrl;
}
