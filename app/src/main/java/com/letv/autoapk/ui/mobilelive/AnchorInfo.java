package com.letv.autoapk.ui.mobilelive;

/**
 * 
 * 主播身份验证信息
 * 
 * @author wangzhen5
 * 
 */
public class AnchorInfo {

    // //未上传信息，未通过验证，等待审核原因
    public String cause;
    // 1:已经上传认证信息 0:未上传认证信息
    public String isComplete;
    // 1:已经通过认证 0:未通过认证 2.等待审核
    public String isPassAuthentication;
}
