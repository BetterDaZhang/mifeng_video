package com.letv.autoapk.ui.mobilelive;

/**
 * 移动直播地址
 * 
 * @author wangzhen5
 * 
 */
public class MobileLivePathInfo {
    // 推流地址
    public String path;
    // 签名密钥
    public String signedKey;
    //分享地址
    public String shareUrl;
    // 应用状态：1:正常 0:异常
    public String status;
    public String cause;
}
